package me.patothebest.gamecore.gui.inventory.button;

import me.patothebest.gamecore.gui.inventory.GUIButton;
import me.patothebest.gamecore.gui.inventory.GUIPage;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class SimpleButton implements GUIButton {

    private final ItemStack item;
    protected ButtonAction action;

    public SimpleButton(ItemStack item) {
        this.item = item;
    }

    public SimpleButton(ItemStack item, ButtonAction buttonAction) {
        this.item = item;
        this.action = buttonAction;
    }

    public SimpleButton action(ButtonAction action) {
        this.action = action;
        return this;
    }

    public ItemStack getItem() {
        return item;
    }

    public void click(ClickType clickType, GUIPage page) {
        if(action == null) {
            return;
        }

        action.onClick();
    }

    public void destroy() { }

    @Override
    public String toString() {
        return "SimpleButton{" +
                "item=" + item +
                ", action=" + action +
                '}';
    }
}
