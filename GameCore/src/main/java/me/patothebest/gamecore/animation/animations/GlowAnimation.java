package me.patothebest.gamecore.animation.animations;

import me.patothebest.gamecore.scoreboard.ScoreboardEntry;
import me.patothebest.gamecore.animation.Animation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GlowAnimation extends Animation {

    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //

    public GlowAnimation() {
        super("glow");
    }

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    public List<String> convert(final String s, final Map<String, String> map, ScoreboardEntry scoreboardEntry) {
        final ArrayList<String> list = new ArrayList<>();

        String normalColor = map.getOrDefault("normalcolor", "&f");
        String startGlowColor = map.getOrDefault("startglowcolor", "&e");
        String endGlowColor = map.getOrDefault("endglowcolor", "&e");
        String glowcolor = map.getOrDefault("glowcolor", "&6");

        list.add(normalColor + s);
        char[] string = s.toCharArray();
        int lastIndex = s.length() - 1;
        for (int end = 0; end < s.length() + 2; end++) {
            StringBuilder currentLine = new StringBuilder();
            int start = end - 2;
            int middle = end - 1;

            if (start > 0) {
                currentLine.append(normalColor).append(string, 0, start);
            }

            if (start >= 0 && start <= lastIndex) {
                currentLine.append(startGlowColor).append(string[start]);
            }

            if (middle >= 0 && middle <= lastIndex) {
                currentLine.append(glowcolor).append(string[middle]);
            }

            if (end <= lastIndex) {
                currentLine.append(endGlowColor).append(string[end]);
            }

            if (end < lastIndex) {
                currentLine.append(normalColor).append(string, end + 1, lastIndex - end);
            }
            list.add(currentLine.toString());
        }

        return list;
    }

}
