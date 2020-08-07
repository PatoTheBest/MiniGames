package me.patothebest.gamecore.event.player;

import me.patothebest.gamecore.player.CorePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerLoginPrepareEvent extends Event {

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    private static final HandlerList handlers = new HandlerList();
    private final CorePlayer player;

    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //

    public PlayerLoginPrepareEvent(CorePlayer player) {
        this.player = player;
    }

    // -------------------------------------------- //
    // GETTERS
    // -------------------------------------------- //

    /**
     * Get's the player that has been loaded
     *
     * @return the player object
     */
    public CorePlayer getPlayer() {
        return player;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
