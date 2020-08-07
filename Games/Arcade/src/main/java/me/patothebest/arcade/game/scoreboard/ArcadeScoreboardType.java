package me.patothebest.arcade.game.scoreboard;

import me.patothebest.gamecore.scoreboard.ScoreboardType;

public enum ArcadeScoreboardType implements ScoreboardType {

    STAR_SUMMARY("star-summary"),
    LAST_ALIVE("last-alive-game"),
    POINTS_GOAL("points-game"),

    ;

    private boolean enabled;
    private final String configName;

    ArcadeScoreboardType(String configName) {
        this.configName = configName;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String getConfigName() {
        return configName;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
