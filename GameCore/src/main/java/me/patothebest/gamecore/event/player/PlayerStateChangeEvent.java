package me.patothebest.gamecore.event.player;

import me.patothebest.gamecore.arena.AbstractArena;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class PlayerStateChangeEvent extends ArenaPlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    private final PlayerState playerState;

    public PlayerStateChangeEvent(Player player, AbstractArena arena, PlayerState playerState) {
        super(player, arena);
        this.playerState = playerState;
    }

    public PlayerState getPlayerState() {
        return playerState;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public enum PlayerState {
        SPECTATOR,
        PLAYER,
        NONE
    }
}
