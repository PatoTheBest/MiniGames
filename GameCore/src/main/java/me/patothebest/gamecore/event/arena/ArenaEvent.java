package me.patothebest.gamecore.event.arena;

import me.patothebest.gamecore.arena.AbstractArena;
import org.bukkit.event.Event;

public abstract class ArenaEvent extends Event {

    private final AbstractArena arena;

    public ArenaEvent(AbstractArena arena) {
        this.arena = arena;
    }

    public AbstractArena getArena() {
        return arena;
    }
}