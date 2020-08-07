package me.patothebest.gamecore.placeholder.placeholders.player;

import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.placeholder.PlaceHolder;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.entity.Player;

public class NamePlaceholder implements PlaceHolder {

    @Override
    public String getPlaceholderName() {
        return "player_name";
    }

    @Override
    public String replace(Player player, String args) {
        if (args != null) {
            if (!Utils.isNumber(args)) {
                return "USAGE e.g: player_name:18";
            }
            return String.format("%-" + Integer.parseInt(args) + "s", player.getName());
        }

        return player.getName();
    }

    @Override
    public String replace(AbstractArena arena) {
        throw new IllegalArgumentException("Not supported");
    }
}
