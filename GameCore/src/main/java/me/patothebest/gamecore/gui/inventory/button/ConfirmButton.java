package me.patothebest.gamecore.gui.inventory.button;

import me.patothebest.gamecore.gui.inventory.GUIPage;
import me.patothebest.gamecore.gui.inventory.page.ConfirmationPage;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ConfirmButton extends SimpleButton {

    private final ItemStack item1;
    private final ItemStack item2;

    public ConfirmButton(ItemStack item, ItemStack item1, ItemStack item2) {
        super(item);
        this.item1 = item1;
        this.item2 = item2;
    }

    public ConfirmButton(ItemStack item, ItemStack item1, ItemStack item2, ButtonAction buttonAction) {
        super(item, buttonAction);
        this.item1 = item1;
        this.item2 = item2;
    }

    @Override
    public void click(ClickType clickType, GUIPage page) {
        new ConfirmationPage(page.getPlugin(), page.getPlayer(), item1, item2) {
            @Override
            public void onConfirm() {
                if(action == null) {
                    return;
                }

                action.onClick();
            }

            @Override
            public void onCancel() {
                getPlayer().closeInventory();
            }
        };
    }
}