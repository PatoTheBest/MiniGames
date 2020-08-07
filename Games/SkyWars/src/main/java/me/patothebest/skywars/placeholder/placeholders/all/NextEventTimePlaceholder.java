package me.patothebest.skywars.placeholder.placeholders.all;

import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.feature.features.other.LimitedTimePhaseFeature;
import me.patothebest.gamecore.placeholder.PlaceHolder;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public class NextEventTimePlaceholder implements PlaceHolder {

    private final PlayerManager playerManager;

    @Inject public NextEventTimePlaceholder(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public String getPlaceholderName() {
        return "next_event_countdown";
    }

    @Override
    public String replace(Player player, String args) {
        return replace(playerManager.getPlayer(player).getCurrentArena());
    }

    @Override
    public String replace(AbstractArena arena) {
        return arena == null || arena.getFeature(LimitedTimePhaseFeature.class) == null ? "None" : Utils.secondsToString(Math.max(0, arena.getFeature(LimitedTimePhaseFeature.class).getRemainingTime()));
    }
}
