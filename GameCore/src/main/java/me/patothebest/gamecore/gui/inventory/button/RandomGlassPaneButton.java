package me.patothebest.gamecore.gui.inventory.button;

import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.StainedGlassPane;
import me.patothebest.gamecore.gui.inventory.GUIButton;
import me.patothebest.gamecore.gui.inventory.GUIPage;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class RandomGlassPaneButton implements GUIButton {


    public RandomGlassPaneButton(final GUIPage page, int slot) {
    }

    @Override
    public void click(ClickType clickType, GUIPage page) {

    }

    @Override
    public ItemStack getItem() {
        return new ItemStackBuilder(StainedGlassPane.getRandom()).name(" ");
    }

    @Override
    public void destroy() {

    }
}
