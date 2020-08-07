package me.patothebest.gamecore.api;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BungeeStateUpdate extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final String newMOTD;

    public BungeeStateUpdate(String newMOTD) {
        this.newMOTD = newMOTD;
    }

    /**
     * Gets the new MOTD
     *
     * @return the new MOTD
     */
    public String getNewMOTD() {
        return newMOTD;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
