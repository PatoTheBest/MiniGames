package me.patothebest.gamecore.title;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public interface Title {
    
    void setTitle(String title);

    String getTitle();

    void setSubtitle(String subtitle);

    String getSubtitle();

    void setTitleColor(ChatColor color);

    void setSubtitleColor(ChatColor color);

    void setFadeInTime(int time);

    void setFadeOutTime(int time);

    void setStayTime(int time);

    void setTimingsToTicks();

    void setTimingsToSeconds();

    /**
     * Send the title to a player
     *
     * @param player Player
     */
    void send(Player player);

    void broadcast();

    /**
     * Clear the title
     *
     * @param player Player
     */
    void clearTitle(Player player);

    /**
     * Reset the title settings
     *
     * @param player Player
     */
    void resetTitle(Player player);
}
