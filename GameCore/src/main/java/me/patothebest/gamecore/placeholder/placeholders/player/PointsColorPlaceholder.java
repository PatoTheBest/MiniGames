package me.patothebest.gamecore.placeholder.placeholders.player;

import com.google.inject.Inject;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.placeholder.PlaceHolder;
import me.patothebest.gamecore.player.PlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PointsColorPlaceholder implements PlaceHolder {

    private final PlayerManager playerManager;

    @Inject public PointsColorPlaceholder(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public String getPlaceholderName() {
        return "points_color";
    }

    @Override
    public String replace(Player player, String args) {
        return (playerManager.getPlayer(player).getPoints() < 0 ? ChatColor.RED : ChatColor.GREEN).toString();
    }

    @Override
    public String replace(AbstractArena arena) {
        throw new IllegalArgumentException("Not supported");
    }
}
