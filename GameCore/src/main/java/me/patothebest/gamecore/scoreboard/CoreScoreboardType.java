package me.patothebest.gamecore.scoreboard;

public enum CoreScoreboardType implements ScoreboardType {

    LOBBY("lobby-scoreboard"),
    WAITING("waiting-scoreboard"),
    GAME("game-scoreboard"),
    NONE("none")

    ;

    private boolean enabled;
    private final String configName;

    CoreScoreboardType(String configName) {
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
