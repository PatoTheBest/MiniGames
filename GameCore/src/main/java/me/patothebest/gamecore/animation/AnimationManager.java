package me.patothebest.gamecore.animation;

import me.patothebest.gamecore.scoreboard.ScoreboardEntry;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.modules.Module;
import me.patothebest.gamecore.util.ThrowOnce;
import org.bukkit.ChatColor;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnimationManager implements Module {

    // -------------------------------------------- //
    // CONSTANTS
    // -------------------------------------------- //

    private final static Pattern animationPattern = Pattern.compile("<(\\w+)( +.+)*>((.*))</\\1>");
    private final static Pattern argsPattern = Pattern.compile(" (.*?)=\"(.*?)\"");

    private static Set<Animation> animations;

    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //

    @Inject public AnimationManager(Set<Animation> animations) {
        AnimationManager.animations = animations;
    }

    // -------------------------------------------- //
    // METHODS
    // -------------------------------------------- //

    public static List<String> parseAnimation(ScoreboardEntry scoreboardEntry, String animationString) {
        List<String> finalList = new ArrayList<>();

        boolean customScore = false;
        int score = 0;

        if(animationString.contains("|")) {
            String[] split = animationString.split("\\|");

            if(Utils.isNumber(split[0])) {
                score = Integer.parseInt(split[0]);
                customScore = true;
            }

            animationString = split[1];
        }

        int finalScore = score;
        boolean finalCustomScore = customScore;

        Matcher matcher = animationPattern.matcher(animationString);
        boolean matched = false;
        while(matcher.find()) {
            String animationName = matcher.group(1);
            String string = matcher.group(matcher.groupCount() - 1);
            Map<String, String> arguments = new HashMap<>();
            for(int i = 2; i < matcher.groupCount() - 1; i++) {
                if(matcher.group(i) == null) {
                    continue;
                }

                String argumentWhole = matcher.group(i).trim();
                if(!argumentWhole.contains("=")) {
                    continue;
                }

                if(argumentWhole.contains("\"")) {
                    Matcher argsMatcher = argsPattern.matcher(" " + argumentWhole);
                    while (argsMatcher.find()) {
                        arguments.put(argsMatcher.group(1), argsMatcher.group(2));
                    }
                } else {
                    for(String argument1 : argumentWhole.split(" ")) {
                        arguments.put(argument1.split("=")[0], argument1.split("=")[1]);
                    }
                }
            }

            Animation animation = animations.stream().filter(animation1 -> animation1.getName().equalsIgnoreCase(animationName)).findFirst().orElse(null);
            if(animation == null) {
                continue;
            }

            if(animation.isCondition()) {
                scoreboardEntry.setCondition(true);
            }

            try {
                animation.convert(string, arguments, scoreboardEntry).forEach(s -> finalList.add((finalCustomScore ? finalScore + "|" : "") + s));
            } catch (Exception e) {
                finalList.add(ChatColor.RED + "ERROR");
                System.err.println("Error while parsing animation " + animationName);
                ThrowOnce.printOnce(e);
            }

            matched = true;
        }

        if(!matched) {
            finalList.add((finalCustomScore ? finalScore + "|" : "") + animationString);
        }

        return finalList;
    }
}
