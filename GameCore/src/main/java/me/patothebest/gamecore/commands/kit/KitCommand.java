package me.patothebest.gamecore.commands.kit;

import me.patothebest.gamecore.guis.kit.KitUIFactory;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.permission.Permission;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.chat.CommandPagination;
import me.patothebest.gamecore.command.BaseCommand;
import me.patothebest.gamecore.command.ChildOf;
import me.patothebest.gamecore.command.Command;
import me.patothebest.gamecore.command.CommandContext;
import me.patothebest.gamecore.command.CommandException;
import me.patothebest.gamecore.command.CommandPermissions;
import me.patothebest.gamecore.command.CommandsManager;
import me.patothebest.gamecore.command.LangDescription;
import me.patothebest.gamecore.command.NestedCommand;
import me.patothebest.gamecore.kit.Kit;
import me.patothebest.gamecore.kit.KitManager;
import me.patothebest.gamecore.modules.Module;
import me.patothebest.gamecore.modules.RegisteredCommandModule;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.util.CommandUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.List;

public class KitCommand implements Module {

    private final KitManager kitManager;
    private final KitUIFactory kitUIFactory;
    private final PlayerManager playerManager;

    @Inject private KitCommand(KitManager kitManager, KitUIFactory kitUIFactory, PlayerManager playerManager) {
        this.kitManager = kitManager;
        this.kitUIFactory = kitUIFactory;
        this.playerManager = playerManager;
    }

    @ChildOf(BaseCommand.class)
    public static class Parent implements RegisteredCommandModule {

        private final CommandsManager<CommandSender> commandsManager;

        @Inject
        private Parent(CommandsManager<CommandSender> commandsManager) {
            this.commandsManager = commandsManager;
        }

        @Command(
                aliases = "kits",
                langDescription = @LangDescription(
                    element = "KIT_COMMAND_DESCRIPTION",
                    langClass = CoreLang.class
            )
        )
        @NestedCommand(value = KitCommand.class)
        public void signs(CommandContext args, CommandSender sender) throws CommandException {
            new CommandPagination(commandsManager, args).display(sender);
        }
    }

    @Command(
            aliases = {"create", "new"},
            usage = "<name>",
            min = 1,
            max = 1,
            langDescription = @LangDescription(
                    element = "KIT_CREATE",
                    langClass = CoreLang.class
            )
    )
    @CommandPermissions(permission = Permission.SETUP)
    public void create(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);

        // requirements
        String kitName = args.getString(0);
        if(kitManager.kitExists(kitName)) {
            player.sendMessage(CoreLang.KIT_ALREADY_EXISTS.getMessage(player));
            return;
        }

        // create the kit
        kitManager.createKit(kitName, player.getInventory());
        player.sendMessage(CoreLang.KIT_CREATED.getMessage(player));
    }

    @Command(
            aliases = {"gui", "menu"},
            max = 0,
            langDescription = @LangDescription(
                    element = "KIT_GUI",
                    langClass = CoreLang.class
            )
    )
    @CommandPermissions(permission = Permission.SETUP)
    public void openGUI(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);
        kitUIFactory.createChooseKitToEditGUI(player);
    }

    @SuppressWarnings("Duplicates")
    @Command(
            aliases = {"give", "add"},
            usage = "<player> <item> [amount] [-p(ermanent)]",
            flags = "p",
            min = 2,
            max = 3,
            langDescription = @LangDescription(
                    element = "SHOP_ITEMS_GIVE_COMMAND",
                    langClass = CoreLang.class
            )
    )
    @CommandPermissions(permission = Permission.ADMIN)
    public List<String> giveShopItems(CommandContext args, CommandSender sender) throws CommandException {
        if (args.getSuggestionContext() != null) {
            switch (args.getSuggestionContext().getIndex()) {
                case 0:
                    return CommandUtils.completePlayers(args.getString(0));
                case 1:
                    return CommandUtils.complete(args.getString(1), Utils.toList(kitManager.getEnabledKits()));
                default:
                    return null;
            }
        }

        IPlayer player = CommandUtils.getPlayer(args, playerManager, 0);
        Kit kit = kitManager.getKits().get(args.getString(1));
        CommandUtils.validateNotNull(kit, CoreLang.SHOP_ITEM_NOT_FOUND);
        if (!kit.isOneTimeKit()) {
            player.buyPermanentKit(kit);
            CoreLang.SHOP_ITEMS_GIVEN_PERMANENT.replaceAndSend(sender, player.getName(), kit.getName(), "kit");
        } else {
            int amount = args.getInteger(2, -1);
            CommandUtils.validateTrue(amount > 0, CoreLang.SHOP_ITEMS_GIVE_MUST_BE_POSITIVE);
            player.addKitUses(kit, amount);
            CoreLang.SHOP_ITEMS_GIVEN.replaceAndSend(sender, player.getName(), amount, kit.getName(), "kit");
        }
        return null;
    }

    @SuppressWarnings("Duplicates")
    @Command(
            aliases = {"giveall", "addall"},
            usage = "<player> [amount] [-p(ermanent]",
            flags = "p",
            min = 1,
            max = 2,
            langDescription = @LangDescription(
                    element = "SHOP_ITEMS_GIVE_ALL_COMMAND",
                    langClass = CoreLang.class
            )
    )
    @CommandPermissions(permission = Permission.ADMIN)
    public List<String> giveAll(CommandContext args, CommandSender sender) throws CommandException {
        if (args.getSuggestionContext() != null) {
            if (args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.completePlayers(args.getString(0));
            }
            return null;
        }

        IPlayer player = CommandUtils.getPlayer(args, playerManager, 0);

        int amount = -1;
        if (args.isInBounds(1)) {
            amount = args.getInteger(1, -1);
            CommandUtils.validateTrue(amount > 0, CoreLang.SHOP_ITEMS_GIVE_MUST_BE_POSITIVE);
        }

        for (Kit kit : kitManager.getEnabledKits()) {
            if (args.hasFlag('p')) {
                player.buyPermanentKit(kit);
                CoreLang.SHOP_ITEMS_GIVEN_PERMANENT.replaceAndSend(sender, player.getName(), kit.getName(), "kit");
            } else if (amount != -1 && kit.isOneTimeKit()) {
                player.addKitUses(kit, amount);
                CoreLang.SHOP_ITEMS_GIVEN.replaceAndSend(sender, player.getName(), amount, kit.getName(), "kit");
            }
        }

        return null;
    }

    @SuppressWarnings("Duplicates")
    @Command(
            aliases = {"set"},
            usage = "<player> <item> <amount>",
            min = 3,
            max = 3,
            langDescription = @LangDescription(
                    element = "SHOP_ITEMS_SET_COMMAND",
                    langClass = CoreLang.class
            )
    )
    @CommandPermissions(permission = Permission.ADMIN)
    public List<String> setShopItems(CommandContext args, CommandSender sender) throws CommandException {
        if (args.getSuggestionContext() != null) {
            switch (args.getSuggestionContext().getIndex()) {
                case 0:
                    return CommandUtils.completePlayers(args.getString(0));
                case 1:
                    return CommandUtils.complete(args.getString(1), Utils.toList(kitManager.getEnabledKits()));
                default:
                    return null;
            }
        }
        IPlayer player = CommandUtils.getPlayer(args, playerManager, 0);
        Kit kit = kitManager.getKits().get(args.getString(1));
        CommandUtils.validateNotNull(kit, CoreLang.SHOP_ITEM_NOT_FOUND);
        CommandUtils.validateTrue(kit.isOneTimeKit(), CoreLang.SHOP_ITEM_IS_PERMANENT);

        int amount = args.getInteger(2);
        CommandUtils.validateTrue(amount > 0, CoreLang.SHOP_ITEMS_GIVE_MUST_BE_POSITIVE);
        player.setKitUses(kit, amount);
        CoreLang.SHOP_ITEMS_GIVEN.replaceAndSend(sender, player.getName(), amount, kit.getName(), "kit");
        return null;
    }

    @SuppressWarnings("Duplicates")
    @Command(
            aliases = {"remove", "take", "reset"},
            usage = "<player> <item> [amount]",
            min = 2,
            max = 3,
            langDescription = @LangDescription(
                    element = "SHOP_ITEMS_REMOVE_COMMAND",
                    langClass = CoreLang.class
            )
    )
    @CommandPermissions(permission = Permission.ADMIN)
    public List<String> removeShopItems(CommandContext args, CommandSender sender) throws CommandException {
        if (args.getSuggestionContext() != null) {
            switch (args.getSuggestionContext().getIndex()) {
                case 0:
                    return CommandUtils.completePlayers(args.getString(0));
                case 1:
                    return CommandUtils.complete(args.getString(1), Utils.toList(kitManager.getEnabledKits()));
                default:
                    return null;
            }
        }

        IPlayer player = CommandUtils.getPlayer(args, playerManager, 0);
        Kit kit = kitManager.getKits().get(args.getString(1));
        CommandUtils.validateNotNull(kit, CoreLang.SHOP_ITEM_NOT_FOUND);
        CommandUtils.validateTrue(kit.isOneTimeKit(), CoreLang.SHOP_ITEM_IS_PERMANENT);
        if (!kit.isOneTimeKit()) {
            player.removeKit(kit);
            CoreLang.SHOP_ITEMS_REMOVED_PERMANENT.replaceAndSend(sender, player.getName(), kit.getName(), "kit");
        } else {
            if (args.isInBounds(2)) {
                int amount = args.getInteger(2, -1);
                CommandUtils.validateTrue(amount > 0, CoreLang.SHOP_ITEMS_TAKE_MUST_BE_POSITIVE);
                player.removeKit(kit, args.getInteger(2));
                CoreLang.SHOP_ITEMS_REMOVED.replaceAndSend(sender, player.getName(), amount, kit.getName(), "kit");
            } else {
                player.removeKit(kit);
                CoreLang.SHOP_ITEMS_REMOVED_PERMANENT.replaceAndSend(sender, player.getName(), kit.getName(), "kit");
            }
        }
        return null;
    }

    @SuppressWarnings("Duplicates")
    @Command(
            aliases = {"removeall", "takeall", "resetall"},
            usage = "<player> [amount]",
            min = 1,
            max = 2,
            langDescription = @LangDescription(
                    element = "SHOP_ITEMS_REMOVE_ALL_COMMAND",
                    langClass = CoreLang.class
            )
    )
    @CommandPermissions(permission = Permission.ADMIN)
    public List<String> removeAll(CommandContext args, CommandSender sender) throws CommandException {
        if (args.getSuggestionContext() != null) {
            if (args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.completePlayers(args.getString(0));
            }
            return null;
        }

        IPlayer player = CommandUtils.getPlayer(args, playerManager, 0);

        int amount = -1;
        if (args.isInBounds(1)) {
            amount = args.getInteger(1, -1);
            CommandUtils.validateTrue(amount > 0, CoreLang.SHOP_ITEMS_TAKE_MUST_BE_POSITIVE);
        }

        for (Kit kit : kitManager.getEnabledKits()) {
            if (amount == -1) {
                player.removeKit(kit);
                CoreLang.SHOP_ITEMS_REMOVED_PERMANENT.replaceAndSend(sender, player.getName(), kit.getName(), "kit");
            } else {
                player.removeKit(kit, amount);
                CoreLang.SHOP_ITEMS_REMOVED.replaceAndSend(sender, player.getName(), amount, kit.getName(), "kit");
            }
        }

        return null;
    }
}