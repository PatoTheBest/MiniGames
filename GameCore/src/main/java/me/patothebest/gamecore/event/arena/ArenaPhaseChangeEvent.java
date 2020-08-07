package me.patothebest.gamecore.event.arena;

import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.phase.Phase;
import org.bukkit.event.HandlerList;

public class ArenaPhaseChangeEvent extends ArenaEvent {

    private static final HandlerList handlers = new HandlerList();
    private final Phase<?> oldPhase;
    private final Phase<?> newPhase;

    public ArenaPhaseChangeEvent(AbstractArena arena, Phase<?> oldPhase, Phase<?> newPhase) {
        super(arena);
        this.oldPhase = oldPhase;
        this.newPhase = newPhase;
    }

    /**
     * Gets the old phase
     *
     * @return the old phase
     */
    public Phase<?> getOldPhase() {
        return oldPhase;
    }

    /**
     * Gets the new phase
     *
     * @return the new phase
     */
    public Phase<?> getNewPhase() {
        return newPhase;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
