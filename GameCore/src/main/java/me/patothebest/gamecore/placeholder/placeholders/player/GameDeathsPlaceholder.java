package me.patothebest.gamecore.placeholder.placeholders.player;

import com.google.inject.Inject;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.placeholder.PlaceHolder;
import me.patothebest.gamecore.player.PlayerManager;
import org.bukkit.entity.Player;

public class GameDeathsPlaceholder implements PlaceHolder {

    private final PlayerManager playerManager;

    @Inject public GameDeathsPlaceholder(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public String getPlaceholderName() {
        return "game_deaths";
    }

    @Override
    public String replace(Player player, String args) {
        return playerManager.getPlayer(player).getGameDeaths() + "";
    }

    @Override
    public String replace(AbstractArena arena) {
        throw new IllegalArgumentException("Not supported");
    }
}
