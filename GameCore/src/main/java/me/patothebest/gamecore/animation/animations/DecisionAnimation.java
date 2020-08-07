package me.patothebest.gamecore.animation.animations;

import me.patothebest.gamecore.scoreboard.ScoreboardEntry;
import me.patothebest.gamecore.animation.Animation;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DecisionAnimation extends Animation {

    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //

    public DecisionAnimation() {
        super("decision");
        this.condition = true;
    }

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    public List<String> convert(String animation, Map<String, String> arguments, ScoreboardEntry scoreboardEntry) {
        String trueValue = arguments.getOrDefault("whentrue", "true");
        String falseValue = arguments.getOrDefault("whenfalse", "false");
        String comparison = arguments.getOrDefault("equals", "true");
        return Collections.singletonList(animation.equalsIgnoreCase(comparison) ? trueValue : falseValue);
    }
}
