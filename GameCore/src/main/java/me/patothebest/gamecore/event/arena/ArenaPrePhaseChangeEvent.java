package me.patothebest.gamecore.event.arena;

import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.phase.Phase;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class ArenaPrePhaseChangeEvent extends ArenaEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Phase<?> oldPhase;
    private final Phase<?> newPhase;
    private boolean cancelled;

    public ArenaPrePhaseChangeEvent(AbstractArena arena, Phase<?> oldPhase, Phase<?> newPhase) {
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

    /**
     * Gets if the event is cancelled
     *
     * @return true if cancelled
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets if the phase change is cancelled
     */
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
