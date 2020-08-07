package me.patothebest.gamecore.event.arena;

import me.patothebest.gamecore.arena.AbstractArena;
import org.bukkit.event.HandlerList;

public class ArenaDisableEvent extends ArenaEvent {

    private static final HandlerList handlers = new HandlerList();

    public ArenaDisableEvent(AbstractArena arena) {
        super(arena);
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
