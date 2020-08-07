package me.patothebest.gamecore.privatearenas.ui;

import me.patothebest.gamecore.privatearenas.PrivateArena;
import org.bukkit.entity.Player;

public interface PrivateArenaUIFactory {

    PrivateArenaUI createPrivateArenaMenu(Player player, PrivateArena privateArena);

}
