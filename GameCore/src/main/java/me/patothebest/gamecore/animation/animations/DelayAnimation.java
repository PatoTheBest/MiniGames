package me.patothebest.gamecore.animation.animations;

import me.patothebest.gamecore.scoreboard.ScoreboardEntry;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.animation.Animation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DelayAnimation extends Animation {

    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //

    public DelayAnimation() {
        super("delay");
    }

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    public List<String> convert(String animation, Map<String, String> args, ScoreboardEntry scoreboardEntry) {
        final List<String> converted = new ArrayList<>();
        int times = 1;

        if (args.containsKey("times") && Utils.isNumber(args.get("times"))) {
            times = Integer.parseInt(args.get("times"));
        }

        for (int i = 0; i < times; ++i) {
            converted.add(animation);
        }

        return converted;
    }
}
