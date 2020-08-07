package me.patothebest.gamecore.selection;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public interface SelectionManager extends Listener {

    Selection getSelection(Player player);

}