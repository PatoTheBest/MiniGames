package me.patothebest.arcade.placeholder.placeholders.player.stars;

import com.google.inject.Inject;
import me.patothebest.arcade.arena.Arena;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.placeholder.PlaceHolder;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import org.bukkit.entity.Player;

public class StarsPlaceholder implements PlaceHolder {

    private final PlayerManager playerManager;

    @Inject private StarsPlaceholder(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public String getPlaceholderName() {
        return "stars";
    }

    @Override
    public final String replace(Player player, String args) {
        IPlayer arcadePlayer = playerManager.getPlayer(player);
        Arena arena = (Arena) arcadePlayer.getCurrentArena();
        if (arena == null) {
            return "NOT IN ARENA";
        }

        return arena.getStarCount().getPoints(player) + "";
    }

    @Override
    public final String replace(AbstractArena arena) {
        throw new IllegalArgumentException("Not supported");
    }
}
