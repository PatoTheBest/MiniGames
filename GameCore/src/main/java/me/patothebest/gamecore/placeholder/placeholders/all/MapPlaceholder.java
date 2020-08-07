package me.patothebest.gamecore.placeholder.placeholders.all;

import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.placeholder.PlaceHolder;
import me.patothebest.gamecore.player.PlayerManager;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public class MapPlaceholder implements PlaceHolder {

    private final PlayerManager playerManager;

    @Inject public MapPlaceholder(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public String getPlaceholderName() {
        return "map";
    }

    @Override
    public String replace(Player player, String args) {
        return replace(playerManager.getPlayer(player).getCurrentArena());
    }

    @Override
    public String replace(AbstractArena arena) {
        return arena == null ? "None" : arena.getName();
    }
}
