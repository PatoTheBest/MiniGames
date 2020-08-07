package me.patothebest.thetowers.placeholder.placeholders.player;

import com.google.inject.Inject;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.placeholder.PlaceHolder;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.thetowers.arena.Arena;
import org.bukkit.entity.Player;

public class GamePointsPlaceholder implements PlaceHolder {

    private final PlayerManager playerManager;

    @Inject public GamePointsPlaceholder(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public String getPlaceholderName() {
        return "game_scored_points";
    }

    @Override
    public String replace(Player player, String args) {
        AbstractArena currentArena = playerManager.getPlayer(player).getCurrentArena();
        if (currentArena == null) {
            return "not in arena";
        }
        return Math.max(((Arena)currentArena).getScoredPoints().getPoints(player.getName()), 0) + "";
    }

    @Override
    public String replace(AbstractArena arena) {
        throw new IllegalArgumentException("Not supported");
    }
}
