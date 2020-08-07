package me.patothebest.gamecore.event.other;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LeaderboardUpdateEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public LeaderboardUpdateEvent() {
        super(true);
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
