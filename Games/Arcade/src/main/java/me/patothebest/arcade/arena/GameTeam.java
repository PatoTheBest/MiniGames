package me.patothebest.arcade.arena;

import me.patothebest.gamecore.arena.AbstractGameTeam;
import org.bukkit.DyeColor;

import java.util.Map;

public class GameTeam extends AbstractGameTeam {

    public GameTeam(Arena arena, String name, DyeColor color) {
        super(arena, name, color);
    }

    public GameTeam(Arena arena, Map<String, Object> data) {
        super(arena, data);
    }

    @Override
    public Map<String, Object> serialize() {
        return super.serialize();
    }

    @Override
    public String toString() {
        return "GameTeam{" +
                "arena=" + arena.getName() +
                ", players=" + players +
                ", name='" + name + '\'' +
                ", color=" + color +
                ", teamChatPrefix=" + teamChatPrefix +
                ", spawn=" + spawn +
                '}';
    }
}
