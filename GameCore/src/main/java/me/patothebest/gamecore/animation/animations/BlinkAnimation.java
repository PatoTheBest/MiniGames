package me.patothebest.gamecore.animation.animations;

import me.patothebest.gamecore.scoreboard.ScoreboardEntry;
import me.patothebest.gamecore.animation.Animation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BlinkAnimation extends Animation {

    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //

    public BlinkAnimation() {
        super("blink");
    }

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    public List<String> convert(String animation, Map<String, String> arguments, ScoreboardEntry scoreboardEntry) {
        List<String> converted = new ArrayList<>();
        converted.add(animation);
        converted.add("");
        return converted;
    }
}
