package me.patothebest.gamecore.animation;

import me.patothebest.gamecore.scoreboard.ScoreboardEntry;

import java.util.List;
import java.util.Map;

public abstract class Animation {

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    private final String name;
    protected boolean condition;

    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //

    public Animation(String name) {
        this.name = name;
    }

    // -------------------------------------------- //
    // ABSTRACT METHODS
    // -------------------------------------------- //

    public abstract List<String> convert(String animation, Map<String, String> arguments, ScoreboardEntry scoreboardEntry);

    // -------------------------------------------- //
    // GETTERS
    // -------------------------------------------- //

    public String getName() {
        return name;
    }

    boolean isCondition() {
        return condition;
    }
}
