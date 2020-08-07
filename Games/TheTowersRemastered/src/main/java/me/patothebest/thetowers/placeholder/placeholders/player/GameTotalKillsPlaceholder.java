package me.patothebest.thetowers.placeholder.placeholders.player;

import com.google.inject.Inject;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.placeholder.PlaceHolder;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.thetowers.arena.Arena;
import org.bukkit.entity.Player;

public class GameTotalKillsPlaceholder implements PlaceHolder {

    private final PlayerManager playerManager;

    @Inject public GameTotalKillsPlaceholder(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public String getPlaceholderName() {
        return "game_total_kills";
    }

    @Override
    public String replace(Player player, String args) {
        AbstractArena currentArena = playerManager.getPlayer(player).getCurrentArena();
        if (currentArena == null) {
            return "not in arena";
        }
        return Math.max(0, ((Arena)currentArena).getKills().getPoints(player.getName())) + "";
    }

    @Override
    public String replace(AbstractArena arena) {
        throw new IllegalArgumentException("Not supported");
    }
}
