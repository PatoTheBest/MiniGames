package me.patothebest.gamecore.gui.inventory.button;

import me.patothebest.gamecore.gui.inventory.GUIButton;
import me.patothebest.gamecore.gui.inventory.GUIPage;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class CloseButton implements GUIButton {

    private ItemStack item;

    public CloseButton(ItemStack item) {
        this.item = item;
    }

    public ItemStack getItem() {
        return item;
    }

    public void click(ClickType clickType, GUIPage page) {
        page.getPlayer().closeInventory();
    }

    public void destroy() {
        this.item = null;
    }

}
