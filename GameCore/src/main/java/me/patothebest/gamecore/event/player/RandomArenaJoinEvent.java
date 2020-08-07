package me.patothebest.gamecore.event.player;

import me.patothebest.gamecore.arena.AbstractArena;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class RandomArenaJoinEvent extends ArenaPlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    public RandomArenaJoinEvent(Player player, AbstractArena arena) {
        super(player, arena);
    }

    /**
     * Gets the cancelled state
     *
     * @return true if event is cancelled
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets the cancelled state
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
