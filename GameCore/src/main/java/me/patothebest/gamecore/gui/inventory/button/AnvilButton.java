package me.patothebest.gamecore.gui.inventory.button;

import me.patothebest.gamecore.gui.anvil.AnvilGUI;
import me.patothebest.gamecore.gui.anvil.AnvilSlot;
import me.patothebest.gamecore.gui.inventory.GUIButton;
import me.patothebest.gamecore.gui.inventory.GUIPage;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class AnvilButton implements GUIButton {

    private final ItemStack item;
    private final Map<AnvilSlot, ItemStack> items;
    private AnvilButtonAction action;

    public AnvilButton(ItemStack item) {
        this.item = item;
        this.items = new HashMap<>();
    }

    public AnvilButton(ItemStack item, AnvilButtonAction action) {
        this.item = item;
        this.action = action;
        this.items = new HashMap<>();
    }

    @Override
    public void click(ClickType clickType, GUIPage page) {
        AnvilGUI gui = new AnvilGUI(page.getPlugin(), page.getPlayer(), event -> {
            if(event.getSlot() == AnvilSlot.OUTPUT){
                action.onConfirm(event.getName());
            } else {
                action.onCancel();
            }
        });
        page.destroy();
        page.destroyInternal();
        gui.getItems().putAll(items);
        items.clear();
        gui.open();
    }

    public AnvilButton action(AnvilButtonAction action) {
        this.action = action;
        return this;
    }

    @Override
    public ItemStack getItem() {
        return item;
    }

    public AnvilButton slot(final AnvilSlot slot, final ItemStack item) {
        this.items.put(slot, item);
        return this;
    }

    @Override
    public void destroy() {

    }
}
