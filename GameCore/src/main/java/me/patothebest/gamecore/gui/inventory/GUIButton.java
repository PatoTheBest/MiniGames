package me.patothebest.gamecore.gui.inventory;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public interface GUIButton {

    void click(ClickType click, GUIPage page);
    void destroy();
    ItemStack getItem();
}
