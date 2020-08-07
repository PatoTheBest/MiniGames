package me.patothebest.gamecore.treasure;

import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.permission.Permission;
import me.patothebest.gamecore.treasure.type.TreasureType;
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
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.util.CommandUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.List;

public class TreasureCommand implements Module {

    private final TreasureManager treasureManager;
    private final PlayerManager playerManager;

    @Inject private TreasureCommand(TreasureManager treasureManager, PlayerManager playerManager) {
        this.treasureManager = treasureManager;
        this.playerManager = playerManager;
    }

    @ChildOf(BaseCommand.class)
    public static class Parent implements RegisteredCommandModule {

        private final CommandsManager<CommandSender> commandsManager;

        @Inject private Parent(CommandsManager<CommandSender> commandsManager) {
            this.commandsManager = commandsManager;
        }

        @Command(
                aliases = {"treasure", "treasurechests", "chests"},
                langDescription = @LangDescription(
                    langClass = CoreLang.class,
                    element = "TREASURE_CHESTS_COMMAND_DESCRIPTION"
                )
        )
        @CommandPermissions(permission = Permission.SETUP)
        @NestedCommand(value = TreasureCommand.class)
        public void signs(CommandContext args, CommandSender sender) throws CommandException {
            new CommandPagination(commandsManager, args).display(sender);
        }
    }

    @SuppressWarnings("Duplicates")
    @Command(
            aliases = {"give", "add"},
            usage = "<player> <type> <amount>",
            min = 3,
            max = 3,
            langDescription = @LangDescription(
                    element = "GIVE_TREASURE_CHESTS_COMMAND",
                    langClass = CoreLang.class
            )
    )
    public List<String> giveTreasureChest(CommandContext args, CommandSender sender) throws CommandException {
        if (args.getSuggestionContext() != null) {
            switch (args.getSuggestionContext().getIndex()) {
                case 0:
                    return CommandUtils.completePlayers(args.getString(0));
                case 1:
                    return CommandUtils.complete(args.getString(1), TreasureType.values());
                default:
                    return null;
            }
        }

        IPlayer player = CommandUtils.getPlayer(args, playerManager, 0);
        TreasureType treasureType = CommandUtils.getEnumValueFromString(TreasureType.class, args.getString(1), CoreLang.INVALID_TREASURE_CHEST);

        player.setKeys(treasureType, player.getKeys(treasureType) + args.getInteger(2));

        CoreLang.TREASURE_CHESTS_GIVEN.replaceAndSend(sender, player.getName(), player.getKeys(treasureType), treasureType.getName());
        return null;
    }

    @Command(
            aliases = {"giveall", "addall"},
            usage = "<player> <amount>",
            min = 2,
            max = 2,
            langDescription = @LangDescription(
                    element = "GIVE_ALL_TREASURE_CHESTS_COMMAND",
                    langClass = CoreLang.class
            )
    )
    public List<String> giveAllTreasureChests(CommandContext args, CommandSender sender) throws CommandException {
        if (args.getSuggestionContext() != null) {
            if (args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.completePlayers(args.getString(0));
            }

            return null;
        }

        IPlayer player = CommandUtils.getPlayer(args, playerManager, 0);

        for (TreasureType type : TreasureType.values()) {
            player.setKeys(type, player.getKeys(type) + args.getInteger(1));
            CoreLang.TREASURE_CHESTS_GIVEN.replaceAndSend(sender, player.getName(), player.getKeys(type), type.getName());
        }

        return null;
    }

    @SuppressWarnings("Duplicates")
    @Command(
            aliases = {"set"},
            usage = "<player> <type> <amount>",
            min = 3,
            max = 3,
            langDescription = @LangDescription(
                    element = "SET_TREASURE_CHEST_COMMAND",
                    langClass = CoreLang.class
            )
    )
    public List<String> setTreasureChest(CommandContext args, CommandSender sender) throws CommandException {
        if (args.getSuggestionContext() != null) {
            switch (args.getSuggestionContext().getIndex()) {
                case 0:
                    return CommandUtils.completePlayers(args.getString(0));
                case 1:
                    return CommandUtils.complete(args.getString(1), TreasureType.values());
                default:
                    return null;
            }
        }

        IPlayer player = CommandUtils.getPlayer(args, playerManager, 0);
        TreasureType treasureType = CommandUtils.getEnumValueFromString(TreasureType.class, args.getString(1), CoreLang.INVALID_TREASURE_CHEST);

        player.setKeys(treasureType, args.getInteger(2));

        CoreLang.TREASURE_CHESTS_GIVEN.replaceAndSend(sender, player.getName(), player.getKeys(treasureType), treasureType.getName());
        return null;
    }

    @Command(
            aliases = {"setall"},
            usage = "<player> <amount>",
            min = 2,
            max = 2,
            langDescription = @LangDescription(
                    element = "SET_ALL_TREASURE_CHEST_COMMAND",
                    langClass = CoreLang.class
            )
    )
    public List<String> giveAllCages(CommandContext args, CommandSender sender) throws CommandException {
        if (args.getSuggestionContext() != null) {
            if (args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.completePlayers(args.getString(0));
            }

            return null;
        }

        IPlayer player = CommandUtils.getPlayer(args, playerManager, 0);

        for (TreasureType type : TreasureType.values()) {
            player.setKeys(type, args.getInteger(1));
            CoreLang.TREASURE_CHESTS_GIVEN.replaceAndSend(sender, player.getName(), player.getKeys(type), type.getName());
        }

        return null;
    }

    @Command(
            aliases = {"addlocation"},
            max = 0,
            langDescription = @LangDescription(
                    element = "TREASURE_ADD_DESC",
                    langClass = CoreLang.class
            )
    )
    public void addChest(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);
        treasureManager.getPlayerCache().add(player);
        CoreLang.TREASURE_ADD.sendMessage(player);
    }
}