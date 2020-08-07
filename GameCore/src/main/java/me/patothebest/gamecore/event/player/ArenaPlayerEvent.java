package me.patothebest.gamecore.event.player;

import me.patothebest.gamecore.arena.AbstractArena;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public abstract class ArenaPlayerEvent extends Event {

    private final AbstractArena arena;
    private final Player player;

    public ArenaPlayerEvent(Player player, AbstractArena arena) {
        this.player = player;
        this.arena = arena;
    }

    public Player getPlayer() {
        return player;
    }

    public AbstractArena getArena() {
        return arena;
    }
}
