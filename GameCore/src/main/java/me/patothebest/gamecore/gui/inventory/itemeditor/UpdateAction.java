package me.patothebest.gamecore.gui.inventory.itemeditor;

import me.patothebest.gamecore.gui.inventory.page.GenericGUI;
import org.bukkit.inventory.ItemStack;

public interface UpdateAction extends GenericGUI {

    void onUpdate(ItemStack itemStack);

}
