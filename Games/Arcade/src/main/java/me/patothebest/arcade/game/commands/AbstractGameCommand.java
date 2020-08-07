package me.patothebest.arcade.game.commands;

import me.patothebest.arcade.game.GameType;
import me.patothebest.arcade.arena.Arena;
import me.patothebest.arcade.game.Game;
import me.patothebest.gamecore.arena.ArenaManager;
import me.patothebest.gamecore.command.CommandContext;
import me.patothebest.gamecore.command.CommandException;
import me.patothebest.gamecore.util.CommandUtils;

import java.util.List;

public abstract class AbstractGameCommand {

    protected final ArenaManager arenaManager;

    protected AbstractGameCommand(ArenaManager arenaManager) {
        this.arenaManager = arenaManager;
    }

    protected List<String> completeArena(CommandContext args) {
        assert args.getSuggestionContext() != null;
        if(args.getSuggestionContext().getIndex() == 0) {
            return CommandUtils.complete(args.getString(0), arenaManager);
        }

        return null;
    }

    protected Game getGame(CommandContext args) throws CommandException {
        Arena arena = CommandUtils.getArena(args, 0, arenaManager);
        return getGame(args, arena);
    }

    protected Game getGame(CommandContext args, Arena arena) throws CommandException {
        String gameName = args.getWholeArgs()[args.getLevel() - 1];
        GameType gameType = GameType.getGameTypeFromCommand(gameName);
        CommandUtils.validateNotNull(gameType, "Game type does not exist");
        CommandUtils.validateTrue(arena.getGames().containsKey(gameType), "That game is not enabled for this arena! Type /arcade games " + gameName + " enable");
        return arena.getGames().get(gameType);
    }
}
