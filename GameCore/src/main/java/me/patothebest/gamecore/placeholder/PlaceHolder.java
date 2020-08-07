package me.patothebest.gamecore.placeholder;

import me.patothebest.gamecore.arena.AbstractArena;
import org.bukkit.entity.Player;

public interface PlaceHolder {

    String getPlaceholderName();

    String replace(Player player, String args);

    String replace(AbstractArena arena);

}
