package me.patothebest.arcade.game;

import me.patothebest.arcade.game.games.oitc.OneInTheChamber;
import me.patothebest.arcade.game.games.snake.Snake;
import me.patothebest.arcade.game.games.splegg.Splegg;
import me.patothebest.arcade.game.games.tntrun.TNTRun;
import me.patothebest.arcade.game.scoreboard.ArcadeScoreboardType;
import me.patothebest.gamecore.modules.ParentCommandModule;

public enum GameType {

    SPLEGG("splegg", "splegg", ArcadeScoreboardType.LAST_ALIVE, Splegg.class, Splegg.Command.class),
    TNTRUN("tntrun", "tntrun", ArcadeScoreboardType.LAST_ALIVE, TNTRun.class, TNTRun.Command.class),
    OITC("oitc", "oitc", ArcadeScoreboardType.POINTS_GOAL, OneInTheChamber.class, OneInTheChamber.Command.class),
    SNAKE("snake", "snake", ArcadeScoreboardType.LAST_ALIVE, Snake.class, Snake.Command.class)
    ;

    private final String configName;
    private final String commandName;
    private final ArcadeScoreboardType scoreboardType;
    private final Class<? extends Game> gameClass;
    private final Class<? extends ParentCommandModule> commandClass;

    GameType(String configName, String commandName, ArcadeScoreboardType scoreboardType, Class<? extends Game> gameClass, Class<? extends ParentCommandModule> commandClass) {
        this.configName = configName;
        this.commandName = commandName;
        this.scoreboardType = scoreboardType;
        this.gameClass = gameClass;
        this.commandClass = commandClass;
    }

    public static GameType getGameType(String name) {
        for (GameType itr : GameType.values()) {
            if(itr.getConfigName().equalsIgnoreCase(name)) {
                return itr;
            }
        }

        return null;
    }

    public static GameType getGameTypeFromCommand(String name) {
        for (GameType itr : GameType.values()) {
            if(itr.getCommandName().equalsIgnoreCase(name)) {
                return itr;
            }
        }

        return null;
    }


    public static GameType getFromClass(Class<? extends Game> gameClass) {
        for (GameType value : GameType.values()) {
            if (value.getGameClass() == gameClass) {
                return value;
            }
        }

        throw new IllegalArgumentException("Game " + gameClass + " is not registered!");
    }

    public String getConfigName() {
        return configName;
    }

    public Class<? extends Game> getGameClass() {
        return gameClass;
    }

    public Class<? extends ParentCommandModule> getCommandClass() {
        return commandClass;
    }

    public String getCommandName() {
        return commandName;
    }

    public ArcadeScoreboardType getScoreboardType() {
        return scoreboardType;
    }
}
