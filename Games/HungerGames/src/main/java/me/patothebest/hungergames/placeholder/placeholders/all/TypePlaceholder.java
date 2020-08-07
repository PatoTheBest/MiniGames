package me.patothebest.hungergames.placeholder.placeholders.all;

import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.placeholder.PlaceHolder;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.hungergames.arena.Arena;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public class TypePlaceholder implements PlaceHolder {

    private final PlayerManager playerManager;

    @Inject public TypePlaceholder(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public String getPlaceholderName() {
        return "type";
    }

    @Override
    public String replace(Player player, String args) {
        return replace(playerManager.getPlayer(player).getCurrentArena());
    }

    @Override
    public String replace(AbstractArena arena) {
        return arena == null ? "None" : ((Arena)arena).getArenaType().getName();
    }
}
