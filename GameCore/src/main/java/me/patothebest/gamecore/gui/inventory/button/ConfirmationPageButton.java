package me.patothebest.gamecore.gui.inventory.button;

import me.patothebest.gamecore.gui.inventory.page.ConfirmationPage;
import me.patothebest.gamecore.gui.inventory.GUIButton;
import me.patothebest.gamecore.gui.inventory.GUIPage;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ConfirmationPageButton implements GUIButton {

    private ItemStack item;
    private final boolean isConfirm;

    public ConfirmationPageButton(boolean isConfirm, ItemStack item) {
        this.item = item;
        this.isConfirm = isConfirm;
    }

    public ItemStack getItem() {
        return item;
    }

    public void click(ClickType clickType, GUIPage page) {
        ConfirmationPage confirmationPage = (ConfirmationPage) page;
        if (isConfirm) {
            confirmationPage.onConfirm();
        } else {
            confirmationPage.onCancel();
        }
    }

    public void destroy() {
        this.item = null;
    }

}
