package me.patothebest.arcade.game.commands;

import com.google.inject.Inject;
import me.patothebest.arcade.game.GameType;
import me.patothebest.arcade.arena.Arena;
import me.patothebest.arcade.game.Game;
import me.patothebest.arcade.lang.Lang;
import me.patothebest.gamecore.arena.ArenaManager;
import me.patothebest.gamecore.command.Command;
import me.patothebest.gamecore.command.CommandContext;
import me.patothebest.gamecore.command.CommandException;
import me.patothebest.gamecore.command.LangDescription;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.selection.Selection;
import me.patothebest.gamecore.selection.SelectionManager;
import me.patothebest.gamecore.util.CommandUtils;
import me.patothebest.gamecore.vector.ArenaLocation;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Provider;
import java.util.List;

public class BasicGameSetupCommands extends AbstractGameCommand {

    private final Provider<SelectionManager> selectionManagerProvider;

    @Inject private BasicGameSetupCommands(ArenaManager arenaManager, Provider<SelectionManager> selectionManagerProvider) {
        super(arenaManager);
        this.selectionManagerProvider = selectionManagerProvider;
    }

    @Command(
            aliases = {"enable"},
            usage = "<arena>",
            min = 1,
            max = 1,
            langDescription = @LangDescription(
                    element = "ENABLE_GAME",
                    langClass = Lang.class
            )
    )
    public List<String> enable(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);
        if (args.getSuggestionContext() != null) {
            return completeArena(args);
        }

        Arena arena = CommandUtils.getArena(args, 0, arenaManager);
        String gameName = args.getWholeArgs()[args.getLevel() - 1];
        GameType gameType = GameType.getGameTypeFromCommand(gameName);
        Game game;
        if (arena.getGames().containsKey(gameType)) {
            game = arena.getGames().get(gameType);
        } else {
            game = arena.createGame(gameType);
        }

        game.setEnabled(true);
        player.sendMessage(Lang.ENABLED_GAME.replace(player, game.getName(), arena.getName()));
        return null;
    }

    @Command(
            aliases = {"disable"},
            usage = "<arena>",
            min = 1,
            max = 1,
            langDescription = @LangDescription(
                    element = "DISABLE_GAME",
                    langClass = Lang.class
            )
    )
    public List<String> disable(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);
        if (args.getSuggestionContext() != null) {
            return completeArena(args);
        }

        Arena arena = CommandUtils.getArena(args, 0, arenaManager);
        Game game = getGame(args, arena);
        game.setEnabled(false);
        player.sendMessage(Lang.DISABLED_GAME.replace(player, game.getName(), arena.getName()));

        return null;
    }

    @Command(
            aliases = {"setspec", "setspectator"},
            usage = "<arena>",
            min = 1,
            max = 1,
            langDescription = @LangDescription(
                    element = "SET_SPECTATOR_LOCATION",
                    langClass = CoreLang.class
            )
    )
    public List<String> setSpectatorLocation(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);

        if(args.getSuggestionContext() != null) {
            if(args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.complete(args.getString(0), arenaManager);
            }

            return null;
        }

        Arena arena = CommandUtils.getDisabledArena(args, 0, arenaManager);
        Game game = getGame(args, arena);

        // set the lobby location of the arena
        game.setSpectatorLocation(new ArenaLocation(arena, player.getLocation()));
        player.sendMessage(CoreLang.SPECTATOR_LOCATION_SET.getMessage(player));
        arena.save();
        return null;
    }

    @Command(
            aliases = {"teleport", "tp"},
            usage = "<arena>",
            min = 1,
            max = 1,
            langDescription = @LangDescription(
                    element = "TELEPORT_TO_GAME",
                    langClass = Lang.class
            )
    )
    public List<String> teleport(CommandContext args, CommandSender sender) throws CommandException {
        if(args.getSuggestionContext() != null) {
            if(args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.complete(args.getString(0), arenaManager);
            }

            return null;
        }

        Player player = CommandUtils.getPlayer(sender);
        Arena arena = CommandUtils.getArena(args, 0, arenaManager);
        Game game = getGame(args, arena);
        CommandUtils.validateTrue(!game.getSpawns().isEmpty(), Lang.NO_SPAWNS);
        player.teleport(game.getSpawns().get(0));
        player.sendMessage(Lang.TELEPORTED_TO_GAME.getMessage(player));
        return null;
    }


    @Command(
            aliases = {"setarea", "setgamearea"},
            usage = "<arena>",
            min = 1,
            max = 1,
            langDescription = @LangDescription(
                    element = "SET_GAME_AREA",
                    langClass = Lang.class
            )
    )
    public List<String> setArenaArea(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandUtils.getPlayer(sender);

        if(args.getSuggestionContext() != null) {
            if(args.getSuggestionContext().getIndex() == 0) {
                return CommandUtils.complete(args.getString(0), arenaManager);
            }

            return null;
        }

        Arena arena = CommandUtils.getDisabledArena(args, 0, arenaManager);
        Game game = getGame(args, arena);

        Selection selection = selectionManagerProvider.get().getSelection(player);
        CommandUtils.validateTrue(selection != null && selection.arePointsSet(), CoreLang.SELECT_AN_AREA);

        // set the arena area
        game.setArea(selection.toCubiod(arena.getName(), arena));
        player.sendMessage(Lang.GAME_AREA_SET.getMessage(player));
        arena.save();
        return null;
    }
}
