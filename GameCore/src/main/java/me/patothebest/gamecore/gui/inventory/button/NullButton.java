package me.patothebest.gamecore.gui.inventory.button;

import me.patothebest.gamecore.gui.inventory.GUIButton;
import me.patothebest.gamecore.gui.inventory.GUIPage;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class NullButton implements GUIButton {

    @Override
    public void click(ClickType clickType, GUIPage page) {

    }

    @Override
    public void destroy() {

    }

    @Override
    public ItemStack getItem() {
        return null;
    }
}
