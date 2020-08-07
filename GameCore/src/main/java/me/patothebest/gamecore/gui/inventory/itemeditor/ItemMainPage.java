package me.patothebest.gamecore.gui.inventory.itemeditor;

import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.gui.inventory.GUIPage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class ItemMainPage extends GUIPage {

    private final ItemStackBuilder originalItem;
    private final UpdateAction updateAction;

    public ItemMainPage(Plugin plugin, Player player, ItemStack originalItem, UpdateAction updateAction) {
        super(plugin, player, CoreLang.GUI_EDIT_ITEM_TITLE.getMessage(player), 9);
        this.originalItem = new ItemStackBuilder(originalItem);
        this.updateAction = updateAction;
        build();
    }

    @Override
    public void buildPage() {
        ItemStack changeDisplayItem = new ItemStackBuilder().material(Material.ITEM_FRAME).name(CoreLang.GUI_EDIT_ITEM_CHANGE_ITEM.getMessage(getPlayer()));
        ItemStack changeStackAmount = new ItemStackBuilder().material(Material.WHITE_WOOL).name(CoreLang.GUI_EDIT_ITEM_CHANGE_AMOUNT.getMessage(getPlayer())).amount(64);

        addButton(new SimpleButton(new ItemStackBuilder().createBackItem(getPlayer()), updateAction::onBack), 0);
        addButton(new SimpleButton(originalItem), 3);
        addButton(new SimpleButton(changeDisplayItem, () -> new ItemPage(plugin, player, originalItem, updateAction)), 6);
        addButton(new SimpleButton(changeStackAmount, () -> new ChangeItemAmount(plugin, player, originalItem, updateAction)), 8);
    }

}
