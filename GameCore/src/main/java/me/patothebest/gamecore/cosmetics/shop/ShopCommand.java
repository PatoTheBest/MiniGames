package me.patothebest.gamecore.cosmetics.shop;

import me.patothebest.gamecore.lang.CoreLang;
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
import me.patothebest.gamecore.modules.Module;
import me.patothebest.gamecore.modules.RegisteredCommandModule;
import me.patothebest.gamecore.permission.Permission;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.util.CommandUtils;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.List;

public class ShopCommand implements Module {

    private final ShopRegistry shopRegistry;
    private final PlayerManager playerManager;
    private final ShopFactory shopFactory;

    @Inject private ShopCommand(ShopRegistry shopRegistry, PlayerManager playerManager, ShopFactory shopFactory) {
        this.shopRegistry = shopRegistry;
        this.playerManager = playerManager;
        this.shopFactory = shopFactory;
    }

    @ChildOf(BaseCommand.class)
    public static class Parent implements RegisteredCommandModule {

        private final CommandsManager<CommandSender> commandsManager;

        @Inject private Parent(CommandsManager<CommandSender> commandsManager) {
            this.commandsManager = commandsManager;
        }

        @Command(
                aliases = {"shop"},
                langDescription = @LangDescription(
                        element = "SHOP_COMMANDS_DESC",
                        langClass = CoreLang.class
                )
        )
        @NestedCommand(value = ShopCommand.class)
        public void shop(CommandContext args, CommandSender sender) throws CommandException {
            new CommandPagination(commandsManager, args).display(sender);
        }
    }

    @Command(
            aliases = {"openshop", "openmenu", "open"},
            usage = "<shop>",
            min = 1,
            max = 1,
            langDescription = @LangDescription(
                    element = "",
                    langClass = CoreLang.class
            )
    )
    public List<String> openShop(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);
        if(args.getSuggestionContext() != null) {
            return CommandUtils.complete(args.getString(0), shopRegistry.getShopManagersNamesMap().keySet());
        }

        //CommandUtils.validateTrue(!playerManager.getPlayer(player).isInArena(), CoreLang.YOU_CANNOT_EXECUTE_COMMANDS);
        ShopManager<?> shopManager = shopRegistry.getShopManagersNamesMap().get(args.getString(0));
        CommandUtils.validateNotNull(shopManager, CoreLang.SHOP_NOT_FOUND);
        shopFactory.createShopMenu(player, shopManager);
        return null;
    }

    @SuppressWarnings("Duplicates")
    @Command(
            aliases = {"give", "add"},
            usage = "<player> <shop> <item> [amount] [-p(ermanent)]",
            flags = "p",
            min = 3,
            max = 4,
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
                    return CommandUtils.complete(args.getString(1), shopRegistry.getShopManagersNamesMap().keySet());
                case 2:
                    ShopManager<?> shopManager = shopRegistry.getShopManagersNamesMap().get(args.getString(1));
                    CommandUtils.validateNotNull(shopManager, CoreLang.SHOP_NOT_FOUND);
                    return CommandUtils.complete(args.getString(2), Utils.toList(shopManager.getShopItems()));
                default:
                    return null;
            }
        }

        IPlayer player = CommandUtils.getPlayer(args, playerManager, 0);
        ShopManager<?> shopManager = shopRegistry.getShopManagersNamesMap().get(args.getString(1));
        CommandUtils.validateNotNull(shopManager, CoreLang.SHOP_NOT_FOUND);
        ShopItem shopItem = shopManager.getShopItemsMap().get(args.getString(2));
        CommandUtils.validateNotNull(shopItem, CoreLang.SHOP_ITEM_NOT_FOUND);
        if (shopItem.isPermanent() || args.hasFlag('p')) {
            player.buyItemPermanently(shopItem);
            CoreLang.SHOP_ITEMS_GIVEN_PERMANENT.replaceAndSend(sender, player.getName(), shopItem.getName(), shopManager.getName());
        } else {
            int amount = args.getInteger(3, -1);
            CommandUtils.validateTrue(amount > 0, CoreLang.SHOP_ITEMS_GIVE_MUST_BE_POSITIVE);
            player.buyItemUses(shopItem, args.getInteger(3));
            CoreLang.SHOP_ITEMS_GIVEN.replaceAndSend(sender, player.getName(), amount, shopItem.getName(), shopManager.getName());
        }
        return null;
    }

    @SuppressWarnings("Duplicates")
    @Command(
            aliases = {"giveall", "addall"},
            usage = "<player> <shop> [amount] [-p(ermanent]",
            flags = "p",
            min = 2,
            max = 3,
            langDescription = @LangDescription(
                    element = "SHOP_ITEMS_GIVE_ALL_COMMAND",
                    langClass = CoreLang.class
            )
    )
    @CommandPermissions(permission = Permission.ADMIN)
    public List<String> giveAll(CommandContext args, CommandSender sender) throws CommandException {
        if (args.getSuggestionContext() != null) {
            switch (args.getSuggestionContext().getIndex()) {
                case 0:
                    return CommandUtils.completePlayers(args.getString(0));
                case 1:
                    return CommandUtils.complete(args.getString(1), shopRegistry.getShopManagersNamesMap().keySet());
                default:
                    return null;
            }
        }

        IPlayer player = CommandUtils.getPlayer(args, playerManager, 0);
        ShopManager<?> shopManager = shopRegistry.getShopManagersNamesMap().get(args.getString(1));
        CommandUtils.validateNotNull(shopManager, CoreLang.SHOP_NOT_FOUND);

        int amount = -1;
        if (args.isInBounds(2)) {
            amount = args.getInteger(2, -1);
            CommandUtils.validateTrue(amount > 0, CoreLang.SHOP_ITEMS_GIVE_MUST_BE_POSITIVE);
        }

        for (ShopItem shopItem : shopManager.getShopItems()) {
            if (args.hasFlag('p')) {
                player.buyItemPermanently(shopItem);
                CoreLang.SHOP_ITEMS_GIVEN_PERMANENT.replaceAndSend(sender, player.getName(), shopItem.getName(), shopManager.getName());
            } else if (amount != -1 && !shopItem.isPermanent()) {
                player.buyItemUses(shopItem, amount);
                CoreLang.SHOP_ITEMS_GIVEN.replaceAndSend(sender, player.getName(), amount, shopItem.getName(), shopManager.getName());
            }
        }

        return null;
    }

    @SuppressWarnings("Duplicates")
    @Command(
            aliases = {"set"},
            usage = "<player> <shop> <item> <amount>",
            min = 4,
            max = 4,
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
                    return CommandUtils.complete(args.getString(1), shopRegistry.getShopManagersNamesMap().keySet());
                case 2:
                    ShopManager<?> shopManager = shopRegistry.getShopManagersNamesMap().get(args.getString(1));
                    CommandUtils.validateNotNull(shopManager, CoreLang.SHOP_NOT_FOUND);
                    return CommandUtils.complete(args.getString(2), Utils.toList(shopManager.getShopItems()));
                default:
                    return null;
            }
        }
        IPlayer player = CommandUtils.getPlayer(args, playerManager, 0);
        ShopManager<?> shopManager = shopRegistry.getShopManagersNamesMap().get(args.getString(1));
        CommandUtils.validateNotNull(shopManager, CoreLang.SHOP_NOT_FOUND);
        ShopItem shopItem = shopManager.getShopItemsMap().get(args.getString(2));
        CommandUtils.validateNotNull(shopItem, CoreLang.SHOP_ITEM_NOT_FOUND);
        CommandUtils.validateTrue(!shopItem.isPermanent(), CoreLang.SHOP_ITEM_IS_PERMANENT);

        int amount = args.getInteger(3);
        CommandUtils.validateTrue(amount > 0, CoreLang.SHOP_ITEMS_GIVE_MUST_BE_POSITIVE);
        player.setItemAmount(shopItem, args.getInteger(3));
        CoreLang.SHOP_ITEMS_GIVEN.replaceAndSend(sender, player.getName(), amount, shopItem.getName(), shopManager.getName());
        return null;
    }

    @SuppressWarnings("Duplicates")
    @Command(
            aliases = {"remove", "take", "reset"},
            usage = "<player> <shop> <item> [amount]",
            min = 3,
            max = 4,
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
                    return CommandUtils.complete(args.getString(1), shopRegistry.getShopManagersNamesMap().keySet());
                case 2:
                    ShopManager<?> shopManager = shopRegistry.getShopManagersNamesMap().get(args.getString(1));
                    CommandUtils.validateNotNull(shopManager, CoreLang.SHOP_NOT_FOUND);
                    return CommandUtils.complete(args.getString(2), Utils.toList(shopManager.getShopItems()));
                default:
                    return null;
            }
        }

        IPlayer player = CommandUtils.getPlayer(args, playerManager, 0);
        ShopManager<?> shopManager = shopRegistry.getShopManagersNamesMap().get(args.getString(1));
        CommandUtils.validateNotNull(shopManager, CoreLang.SHOP_NOT_FOUND);
        ShopItem shopItem = shopManager.getShopItemsMap().get(args.getString(2));
        CommandUtils.validateNotNull(shopItem, CoreLang.SHOP_ITEM_NOT_FOUND);
        if (shopItem.isPermanent()) {
            player.removeItem(shopItem);
            CoreLang.SHOP_ITEMS_REMOVED_PERMANENT.replaceAndSend(sender, player.getName(), shopItem.getName(), shopManager.getName());
        } else {
            if (args.isInBounds(3)) {
                int amount = args.getInteger(3, -1);
                CommandUtils.validateTrue(amount > 0, CoreLang.SHOP_ITEMS_TAKE_MUST_BE_POSITIVE);
                player.removeItem(shopItem, args.getInteger(3));
                CoreLang.SHOP_ITEMS_REMOVED.replaceAndSend(sender, player.getName(), amount, shopItem.getName(), shopManager.getName());
            } else {
                player.removeItem(shopItem);
                CoreLang.SHOP_ITEMS_REMOVED_PERMANENT.replaceAndSend(sender, player.getName(), shopItem.getName(), shopManager.getName());
            }
        }
        return null;
    }

    @SuppressWarnings("Duplicates")
    @Command(
            aliases = {"removeall", "takeall", "resetall"},
            usage = "<player> <shop> [amount]",
            min = 2,
            max = 3,
            langDescription = @LangDescription(
                    element = "SHOP_ITEMS_REMOVE_ALL_COMMAND",
                    langClass = CoreLang.class
            )
    )
    @CommandPermissions(permission = Permission.ADMIN)
    public List<String> removeAll(CommandContext args, CommandSender sender) throws CommandException {
        if (args.getSuggestionContext() != null) {
            switch (args.getSuggestionContext().getIndex()) {
                case 0:
                    return CommandUtils.completePlayers(args.getString(0));
                case 1:
                    return CommandUtils.complete(args.getString(1), shopRegistry.getShopManagersNamesMap().keySet());
                default:
                    return null;
            }
        }

        IPlayer player = CommandUtils.getPlayer(args, playerManager, 0);
        ShopManager<?> shopManager = shopRegistry.getShopManagersNamesMap().get(args.getString(1));
        CommandUtils.validateNotNull(shopManager, CoreLang.SHOP_NOT_FOUND);

        int amount = -1;
        if (args.isInBounds(2)) {
            amount = args.getInteger(2, -1);
            CommandUtils.validateTrue(amount > 0, CoreLang.SHOP_ITEMS_TAKE_MUST_BE_POSITIVE);
        }

        for (ShopItem shopItem : shopManager.getShopItems()) {
            if (amount == -1) {
                player.removeItem(shopItem);
                CoreLang.SHOP_ITEMS_REMOVED_PERMANENT.replaceAndSend(sender, player.getName(), shopItem.getName(), shopManager.getName());
            } else {
                player.removeItem(shopItem, amount);
                CoreLang.SHOP_ITEMS_REMOVED.replaceAndSend(sender, player.getName(), amount, shopItem.getName(), shopManager.getName());
            }
        }

        return null;
    }

}
