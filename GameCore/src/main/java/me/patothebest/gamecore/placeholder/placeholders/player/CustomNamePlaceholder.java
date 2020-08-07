package me.patothebest.gamecore.placeholder.placeholders.player;

import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.placeholder.PlaceHolder;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.entity.Player;

public class CustomNamePlaceholder implements PlaceHolder {

    @Override
    public String getPlaceholderName() {
        return "player_custom_name";
    }

    @Override
    public String replace(Player player, String args) {
        if (args != null) {
            if (!Utils.isNumber(args)) {
                return "USAGE e.g: player_custom_name:18";
            }
            return String.format("%-" + Integer.parseInt(args) + "s", player.getCustomName());
        }

        return player.getCustomName();
    }

    @Override
    public String replace(AbstractArena arena) {
        throw new IllegalArgumentException("Not supported");
    }
}
