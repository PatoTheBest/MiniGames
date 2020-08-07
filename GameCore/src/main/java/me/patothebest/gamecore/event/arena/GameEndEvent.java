package me.patothebest.gamecore.event.arena;

import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.util.PlayerList;
import org.bukkit.event.HandlerList;

public class GameEndEvent extends ArenaEvent {

    private static final HandlerList handlers = new HandlerList();
    private final PlayerList winners;
    private final PlayerList losers;

    public GameEndEvent(AbstractArena arena, PlayerList winners, PlayerList losers) {
        super(arena);
        this.winners = winners;
        this.losers = losers;
    }

    public PlayerList getWinners() {
        return winners;
    }

    public PlayerList getLosers() {
        return losers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
