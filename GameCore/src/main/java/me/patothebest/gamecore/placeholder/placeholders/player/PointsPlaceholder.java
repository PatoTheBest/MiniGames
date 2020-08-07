package me.patothebest.gamecore.placeholder.placeholders.player;

import com.google.inject.Inject;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.placeholder.PlaceHolder;
import me.patothebest.gamecore.player.PlayerManager;
import org.bukkit.entity.Player;

public class PointsPlaceholder implements PlaceHolder {

    private final PlayerManager playerManager;

    @Inject public PointsPlaceholder(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public String getPlaceholderName() {
        return "points";
    }

    @Override
    public String replace(Player player, String args) {
        if (!playerManager.getPlayer(player).isAllDataLoaded()) {
            return CoreLang.LOADING.getMessage(player);
        }

        return playerManager.getPlayer(player).getPoints() + "";
    }

    @Override
    public String replace(AbstractArena arena) {
        throw new IllegalArgumentException("Not supported");
    }
}
