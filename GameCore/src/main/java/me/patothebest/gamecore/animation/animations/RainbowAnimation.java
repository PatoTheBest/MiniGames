package me.patothebest.gamecore.animation.animations;

import me.patothebest.gamecore.scoreboard.ScoreboardEntry;
import me.patothebest.gamecore.animation.Animation;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RainbowAnimation extends Animation {

    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //

    public RainbowAnimation() {
        super("rainbow");
    }

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    public List<String> convert(final String animation, final Map<String, String> args, ScoreboardEntry scoreboardEntry) {
        // ITS A DOUBLE RAINBOW ALL THE WAY ACROSS
        final List<String> converted = new ArrayList<>();
        converted.add(ChatColor.DARK_RED + animation);
        converted.add(ChatColor.RED + animation);
        converted.add(ChatColor.GOLD + animation);
        converted.add(ChatColor.YELLOW + animation);
        converted.add(ChatColor.DARK_GREEN + animation);
        converted.add(ChatColor.GREEN + animation);
        converted.add(ChatColor.AQUA + animation);
        converted.add(ChatColor.DARK_AQUA + animation);
        converted.add(ChatColor.DARK_BLUE + animation);
        converted.add(ChatColor.BLUE + animation);
        converted.add(ChatColor.LIGHT_PURPLE + animation);
        converted.add(ChatColor.DARK_PURPLE + animation);
        converted.add(ChatColor.WHITE + animation);
        converted.add(ChatColor.GRAY + animation);
        converted.add(ChatColor.DARK_GRAY + animation);
        return converted;
    }
}
