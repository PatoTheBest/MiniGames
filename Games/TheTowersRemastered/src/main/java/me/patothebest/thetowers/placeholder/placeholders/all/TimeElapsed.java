package me.patothebest.thetowers.placeholder.placeholders.all;

import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.placeholder.PlaceHolder;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public class TimeElapsed implements PlaceHolder {

    private final PlayerManager playerManager;

    @Inject public TimeElapsed(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public String getPlaceholderName() {
        return "time_elapsed";
    }

    @Override
    public String replace(Player player, String args) {
        return replace(playerManager.getPlayer(player).getCurrentArena());
    }

    @Override
    public String replace(AbstractArena arena) {
        return arena == null ? "not in arena" : Utils.secondsToString(arena.getTimePhaseHasBeenRunning() / 1000L);
    }
}
