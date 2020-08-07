package me.patothebest.gamecore.event.player;

import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.arena.AbstractGameTeam;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import javax.annotation.Nullable;

public class ArenaLeaveMidGameEvent extends ArenaPlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    private final AbstractGameTeam lastGameTeam;

    public ArenaLeaveMidGameEvent(Player player, AbstractArena arena, AbstractGameTeam lastGameTeam) {
        super(player, arena);
        this.lastGameTeam = lastGameTeam;
    }

    @Nullable
    public AbstractGameTeam getLastGameTeam() {
        return lastGameTeam;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
