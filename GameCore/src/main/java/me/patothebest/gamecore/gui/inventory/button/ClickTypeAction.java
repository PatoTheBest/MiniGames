package me.patothebest.gamecore.gui.inventory.button;

import org.bukkit.event.inventory.ClickType;

@FunctionalInterface
public interface ClickTypeAction {

    void onClick(ClickType clickType);

}
