package me.patothebest.arcade.game.commands;

import me.patothebest.arcade.arena.Arena;
import me.patothebest.arcade.game.Game;
import me.patothebest.arcade.lang.Lang;
import me.patothebest.gamecore.arena.ArenaManager;
import me.patothebest.gamecore.chat.Pagination;
import me.patothebest.gamecore.command.ChatColor;
import me.patothebest.gamecore.command.Command;
import me.patothebest.gamecore.command.CommandContext;
import me.patothebest.gamecore.command.CommandException;
import me.patothebest.gamecore.command.LangDescription;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.util.CommandUtils;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.vector.ArenaLocation;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class SpawnsCommand extends AbstractGameCommand {

    @Inject
    private SpawnsCommand(ArenaManager arenaManager) {
        super(arenaManager);
    }

//    public static class Parent implements Module {
//
//        private final CommandsManager<CommandSender> commandsManager;
//
//        @Inject
//        private Parent(CommandsManager<CommandSender> commandsManager) {
//            this.commandsManager = commandsManager;
//        }
//
//        @Command(
//                aliases = {"spawns"},
//                langDescription = @LangDescription(
//                        element = "SPAWNS_DESC",
//                        langClass = Lang.class
//                )
//        )
//        @CommandPermissions(permission = Permission.SETUP)
//        @NestedCommand(value = SpawnsCommand.class)
//        public void spawns(CommandContext args, CommandSender sender) throws CommandException {
//            new CommandPagination(commandsManager, "spawns", "spawns", args).display(sender);
//        }
//    }


    @Command(
            aliases = {"add", "addspawn"},
            usage = "<arena>",
            min = 1,
            max = 1,
            langDescription = @LangDescription(
                    element = "ADD_SPAWN",
                    langClass = Lang.class
            )
    )
    public List<String> addSpawn(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);
        if (args.getSuggestionContext() != null) {
            return completeArena(args);
        }

        Arena arena = CommandUtils.getArena(args, 0, arenaManager);
        Game game = getGame(args, arena);
        game.getSpawns().add(new ArenaLocation(arena, player.getLocation()));
        player.sendMessage(Lang.SPAWN_ADDED.replace(player, game.getSpawns().size()));
        return null;
    }

    @Command(
            aliases = {"remove", "delete"},
            usage = "<arena> <id>",
            min = 2,
            max = 2,
            langDescription = @LangDescription(
                    element = "REMOVE_SPAWN",
                    langClass = Lang.class
            )
    )
    public List<String> removeSpawn(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);
        if (args.getSuggestionContext() != null) {
            return completeArena(args);
        }

        Game game = getGame(args);

        int index = args.getInteger(1);
        CommandUtils.validateTrue(index <= game.getSpawns().size() && index > 0, CoreLang.OUT_OF_BOUNDS);
        game.getSpawns().remove(index-1);
        Lang.SPAWN_REMOVED.replaceAndSend(player, index);
        return null;
    }

    @Command(
            aliases = {"list", "ls"},
            usage = "<arena> [page]",
            min = 1,
            max = 2,
            langDescription = @LangDescription(
                    element = "LIST_SPAWN",
                    langClass = Lang.class
            )
    )
    public List<String> listSpawns(CommandContext args, CommandSender sender) throws CommandException {
        CommandUtils.getPlayer(sender);
        if (args.getSuggestionContext() != null) {
            return completeArena(args);
        }

        Game game = getGame(args);
        int page = args.getInteger(1, 1);

        new Pagination<ArenaLocation>() {
            @Override
            protected String title() {
                return "Spawns for " + game.getName();
            }

            @Override
            protected String entry(ArenaLocation entry, int index, CommandSender commandSender) {
                return ChatColor.GRAY + "* " + ChatColor.GOLD.toString() + (index+1) + ". " + ChatColor.BLUE + "Spawn "
                        + ChatColor.YELLOW + " Location: " + Utils.locationToCoords(entry, sender);
            }
        }.display(sender, game.getSpawns(), page);
        return null;
    }

    @Command(
            aliases = {"clear", "cl"},
            usage = "<arena>",
            min = 1,
            langDescription = @LangDescription(
                    element = "CLEAR_SPAWNS",
                    langClass = Lang.class
            )
    )
    public List<String> clearSpawns(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);
        if (args.getSuggestionContext() != null) {
            return completeArena(args);
        }

        Game game = getGame(args);
        game.getSpawns().clear();
        Lang.CLEARED_SPAWNS.sendMessage(player);
        return null;
    }
}
