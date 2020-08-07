package me.patothebest.gamecore.placeholder.placeholders.all;

import com.google.inject.Inject;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.feature.features.other.CountdownFeature;
import me.patothebest.gamecore.placeholder.PlaceHolder;
import me.patothebest.gamecore.player.PlayerManager;
import org.bukkit.entity.Player;

public class IsCountingPlaceholder implements PlaceHolder {

    private final PlayerManager playerManager;

    @Inject public IsCountingPlaceholder(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public String getPlaceholderName() {
        return "is_counting";
    }

    @Override
    public String replace(Player player, String args) {
        return replace(playerManager.getPlayer(player).getCurrentArena());
    }

    @Override
    public String replace(AbstractArena arena) {
        return arena == null ? "ERROR" : arena.getFeature(CountdownFeature.class).isRunning() + "";
    }
}
