package me.patothebest.gamecore.placeholder.placeholders.all;

import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.placeholder.PlaceHolder;
import org.bukkit.entity.Player;

public class DatePlaceholder implements PlaceHolder {

    @Override
    public String getPlaceholderName() {
        return "date";
    }

    @Override
    public String replace(Player player, String args) {
        return replace(null);
    }

    @Override
    public String replace(AbstractArena arena) {
        return Utils.getCurrentTimeStamp("dd/MM/yyyy");
    }
}
