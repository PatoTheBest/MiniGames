package me.patothebest.arcade.placeholder.placeholders.player.stars;

import com.google.inject.Inject;
import me.patothebest.arcade.arena.Arena;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.placeholder.PlaceHolder;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.entity.Player;

public class GameStarsNamePlaceholder implements PlaceHolder {

    private final PlayerManager playerManager;

    @Inject private GameStarsNamePlaceholder(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public String getPlaceholderName() {
        return "game_stars_name";
    }

    @Override
    public final String replace(Player player, String args) {
        if (args == null) {
            return "USAGE eg. {game_stars_name:1}";
        }

        String placeString;
        String padding = null;

        if (args.contains("_")) {
            placeString = args.substring(0, args.indexOf("_"));
            padding = args.substring(args.indexOf("_") + 1);
        } else {
            placeString = args;
        }

        if (!Utils.isNumber(placeString)) {
            return "USAGE eg. {game_stars_name:1}";
        }

        int place = Integer.parseInt(placeString) - 1;
        IPlayer arcadePlayer = playerManager.getPlayer(player);
        Arena arena = (Arena) arcadePlayer.getCurrentArena();
        if (arena == null) {
            return "NOT IN ARENA";
        }

        if (arena.getStarCount().size() <= place) {
            return "#N/A";
        }

        String playerName = arena.getStarCount().get(place).getPlayer().getName();
        if (padding != null) {
            if (!Utils.isNumber(padding)) {
                return "USAGE e.g: game_stars_name:1_16";
            }
            return String.format("%-" + Integer.parseInt(padding) + "s", playerName);
        }

        return playerName;
    }

    @Override
    public final String replace(AbstractArena arena) {
        throw new IllegalArgumentException("Not supported");
    }
}
