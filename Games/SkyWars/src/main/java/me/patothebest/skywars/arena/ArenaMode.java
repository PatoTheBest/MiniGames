package me.patothebest.skywars.arena;

import org.bukkit.ChatColor;

public enum ArenaMode {

    NORMAL(ChatColor.GRAY + "Normal"),
    INSANE(ChatColor.RED + "Insane")

    ;

    private final String name;

    ArenaMode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
