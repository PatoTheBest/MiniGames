package me.patothebest.arcade.placeholder.placeholders.player.stars;

import com.google.inject.Inject;
import me.patothebest.arcade.arena.Arena;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.placeholder.PlaceHolder;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.entity.Player;

public class IsTopStarsPlaceholder implements PlaceHolder {

    private final PlayerManager playerManager;

    @Inject private IsTopStarsPlaceholder(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public String getPlaceholderName() {
        return "is_top_stars";
    }

    @Override
    public final String replace(Player player, String args) {
        if (args == null || !Utils.isNumber(args)) {
            return "Usage e.g: {is_top_stars:5}";
        }

        IPlayer arcadePlayer = playerManager.getPlayer(player);
        Arena arena = (Arena) arcadePlayer.getCurrentArena();
        if (arena == null) {
            return "NOT IN ARENA";
        }

        int compareTo = Integer.parseInt(args);
        return arena.getStarCount().getPosition(player) + 1 <= compareTo ? "true" : "false";
    }

    @Override
    public final String replace(AbstractArena arena) {
        throw new IllegalArgumentException("Not supported");
    }
}
