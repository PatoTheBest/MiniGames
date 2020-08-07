package me.patothebest.gamecore.arena;

import me.patothebest.gamecore.command.ChatColor;
import org.bukkit.DyeColor;

public enum ArenaState {

    WAITING("Waiting", ChatColor.GREEN, DyeColor.LIME),
    STARTING("Starting", ChatColor.GREEN, DyeColor.LIME),
    IN_GAME("In-Game", ChatColor.GOLD, DyeColor.ORANGE),
    ENDING("Ending", ChatColor.RED, DyeColor.RED),
    RESTARTING("Restarting", ChatColor.RED, DyeColor.RED),
    OTHER("Other", ChatColor.RED, DyeColor.RED),
    ERROR("Error", ChatColor.RED, DyeColor.RED)

    ;

    private final String name;
    private ChatColor color;
    private DyeColor data;

    ArenaState(String name, ChatColor color, DyeColor data) {
        this.name = name;
        this.color = color;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public ChatColor getColor() {
        return color;
    }

    public DyeColor getData() {
        return data;
    }

    public static void configureInGameJoinable() {
        ArenaState.IN_GAME.color = ChatColor.GREEN;
        ArenaState.IN_GAME.data = DyeColor.LIME;
    }
}
