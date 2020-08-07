package me.patothebest.gamecore.scoreboard;

public interface ScoreboardType {
    boolean isEnabled();

    String getConfigName();

    void setEnabled(boolean enabled);
}
