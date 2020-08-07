package me.patothebest.arcade.placeholder.placeholders.player.stars;

import com.google.inject.Inject;
import me.patothebest.arcade.arena.Arena;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.placeholder.PlaceHolder;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.entity.Player;

public class GameStarsCountPlaceholder implements PlaceHolder {

    private final PlayerManager playerManager;

    @Inject private GameStarsCountPlaceholder(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public String getPlaceholderName() {
        return "game_stars_count";
    }

    @Override
    public final String replace(Player player, String args) {
        if (args == null) {
            return "USAGE eg. {game_stars_count:1}";
        }

        if (!Utils.isNumber(args)) {
            return "USAGE eg. {game_stars_name:1}";
        }

        int place = Integer.parseInt(args) - 1;
        IPlayer arcadePlayer = playerManager.getPlayer(player);
        Arena arena = (Arena) arcadePlayer.getCurrentArena();
        if (arena == null) {
            return "NOT IN ARENA";
        }

        if (arena.getStarCount().size() <= place) {
            return "#N/A";
        }

        return arena.getStarCount().get(place).getPoints() + "";
    }

    @Override
    public final String replace(AbstractArena arena) {
        throw new IllegalArgumentException("Not supported");
    }
}
