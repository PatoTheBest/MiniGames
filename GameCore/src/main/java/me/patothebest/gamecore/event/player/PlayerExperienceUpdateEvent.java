package me.patothebest.gamecore.event.player;

import me.patothebest.gamecore.player.IPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerExperienceUpdateEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final IPlayer player;
    private final UpdateType updateType;
    private final long oldExperience;
    private final long newExperience;

    public PlayerExperienceUpdateEvent(IPlayer player, UpdateType updateType, long oldExperience, long newExperience) {
        this.player = player;
        this.updateType = updateType;
        this.oldExperience = oldExperience;
        this.newExperience = newExperience;
    }

    public IPlayer getPlayer() {
        return player;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public long getOldExperience() {
        return oldExperience;
    }

    public long getNewExperience() {
        return newExperience;
    }

    public UpdateType getUpdateType() {
        return updateType;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public enum UpdateType {

        ADD,
        SUBTRACT,
        SET

    }
}
