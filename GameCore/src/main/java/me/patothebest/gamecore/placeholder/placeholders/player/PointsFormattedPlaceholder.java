package me.patothebest.gamecore.placeholder.placeholders.player;

import com.google.inject.Inject;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.placeholder.PlaceHolder;
import me.patothebest.gamecore.player.PlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PointsFormattedPlaceholder implements PlaceHolder {

    private final PlayerManager playerManager;

    @Inject public PointsFormattedPlaceholder(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public String getPlaceholderName() {
        return "points_formatted";
    }

    @Override
    public String replace(Player player, String args) {
        return (playerManager.getPlayer(player).getPoints() < 0 ? ChatColor.RED.toString() : ChatColor.GREEN + "+") + playerManager.getPlayer(player).getPoints();
    }

    @Override
    public String replace(AbstractArena arena) {
        throw new IllegalArgumentException("Not supported");
    }
}
