package me.patothebest.hungergames.placeholder.placeholders.all;

import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.placeholder.PlaceHolder;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.hungergames.phase.HungerGamesPhase;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public class NextEventPlaceholder implements PlaceHolder {

    private final PlayerManager playerManager;

    @Inject public NextEventPlaceholder(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public String getPlaceholderName() {
        return "next_event";
    }

    @Override
    public String replace(Player player, String args) {
        return replace(playerManager.getPlayer(player).getCurrentArena());
    }

    @Override
    public String replace(AbstractArena arena) {
        return arena == null || arena.getPhase().getNextPhase() == null || !(arena.getPhase().getNextPhase() instanceof HungerGamesPhase) ? "None" : ((HungerGamesPhase) arena.getPhase().getNextPhase()).getPhaseType().getConfigName();
    }
}
