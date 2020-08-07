package me.patothebest.gamecore.gui.inventory.button;

import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.gui.inventory.GUIButton;
import me.patothebest.gamecore.gui.inventory.GUIPage;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class IncrementingButton implements GUIButton {

    private final ItemStack item;
    private int amount;
    private IncrementingButtonAction action;

    public IncrementingButton(ItemStackBuilder item, int amount) {
        this.item = item.amount(Math.abs(amount));
    }

    public IncrementingButton(ItemStackBuilder item, int amount, IncrementingButtonAction buttonAction) {
        this.item = item.amount(Math.abs(amount));
        this.action = buttonAction;
    }

    public IncrementingButton(int amount, IncrementingButtonAction buttonAction) {
        this.item = new ItemStackBuilder().material(amount < 0 ? Material.RED_STAINED_GLASS_PANE : Material.GREEN_STAINED_GLASS_PANE).amount(Math.abs(amount)).name((amount < 0 ? ChatColor.RED : ChatColor.GREEN + "+").toString() + amount);
        this.amount = amount;
        this.action = buttonAction;
    }

    public IncrementingButton setAction(IncrementingButtonAction action) {
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

        action.onClick(amount);
    }

    public void destroy() {
    }
}
