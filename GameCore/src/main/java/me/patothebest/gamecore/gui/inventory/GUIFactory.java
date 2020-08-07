package me.patothebest.gamecore.gui.inventory;

import org.bukkit.entity.Player;

public interface GUIFactory<Menu extends GUIPage> {

    Menu create(Player player);

}
