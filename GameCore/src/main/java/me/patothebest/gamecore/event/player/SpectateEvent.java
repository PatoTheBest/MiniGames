package me.patothebest.gamecore.event.player;

import me.patothebest.gamecore.arena.AbstractArena;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class SpectateEvent extends ArenaPlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    public SpectateEvent(Player player, AbstractArena arena) {
        super(player, arena);
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
