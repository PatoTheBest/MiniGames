package me.patothebest.gamecore.stats;

import me.patothebest.gamecore.player.IPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class StatsUpdateEvent extends Event {

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    private static final HandlerList handlers = new HandlerList();
    private final IPlayer player;
    private final Statistic statistic;
    private final int changed;

    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //

    public StatsUpdateEvent(IPlayer player, Statistic statistic, int changed) {
        this.player = player;
        this.statistic = statistic;
        this.changed = changed;
    }

    // -------------------------------------------- //
    // GETTERS
    // -------------------------------------------- //

    /**
     * Get's the player that has been loaded
     *
     * @return the player object
     */
    public IPlayer getPlayer() {
        return player;
    }

    /**
     * Gets the statistic
     *
     * @return the statistic
     */
    public Statistic getStatistic() {
        return statistic;
    }

    /**
     * Gets the amount changed
     *
     * @return the amount changed
     */
    public int getChanged() {
        return changed;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
