package me.patothebest.gamecore.guis.kit;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.guis.grouppermissible.PermissionGroupUIFactory;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.itemstack.PotionBuilder;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.gui.anvil.AnvilSlot;
import me.patothebest.gamecore.gui.inventory.GUIPage;
import me.patothebest.gamecore.gui.inventory.button.AnvilButton;
import me.patothebest.gamecore.gui.inventory.button.AnvilButtonAction;
import me.patothebest.gamecore.gui.inventory.button.BackButton;
import me.patothebest.gamecore.gui.inventory.descriptioneditor.DescriptionEdition;
import me.patothebest.gamecore.gui.inventory.descriptioneditor.DescriptionEditorMainPage;
import me.patothebest.gamecore.gui.inventory.itemeditor.ItemMainPage;
import me.patothebest.gamecore.gui.inventory.itemeditor.UpdateAction;
import me.patothebest.gamecore.kit.Kit;
import me.patothebest.gamecore.kit.KitManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionType;

import java.util.List;

public class EditKitUI extends GUIPage {

    private final Kit kit;
    private final KitUIFactory kitUIFactory;
    private final KitManager kitManager;
    private final PermissionGroupUIFactory permissionGroupUIFactory;

    @Inject private EditKitUI(Plugin plugin, KitUIFactory kitUIFactory, PermissionGroupUIFactory permissionGroupUIFactory, KitManager kitManager, @Assisted Player player, @Assisted Kit kit) {
        super(plugin, player, CoreLang.GUI_EDIT_KIT_TITLE.replace(player, kit.getKitName()), 36);
        this.permissionGroupUIFactory = permissionGroupUIFactory;
        this.kit = kit;
        this.kitUIFactory = kitUIFactory;
        this.kitManager = kitManager;
        build();
    }

    @Override
    protected void buildPage() {
        ItemStackBuilder renameItem = new ItemStackBuilder().material(Material.NAME_TAG).name(getPlayer(), CoreLang.GUI_EDIT_KIT_RENAME);
        ItemStackBuilder enabledSlimeball = new ItemStackBuilder().createTogglableItem(getPlayer(), kit.isOneTimeKit()).lore(CoreLang.GUI_EDIT_KIT_ONE_TIME.getMessage(getPlayer()));
        ItemStackBuilder preview = new ItemStackBuilder().material(Material.PAINTING).name(getPlayer(), CoreLang.GUI_EDIT_KIT_PREVIEW);
        ItemStackBuilder priceItem = new ItemStackBuilder().material(Material.EMERALD).name(getPlayer(), CoreLang.GUI_EDIT_KIT_PRICE);
        ItemStackBuilder permission = new ItemStackBuilder().material(Material.BLAZE_POWDER).name(getPlayer(), CoreLang.GUI_EDIT_KIT_SET_PERMISSION);
        ItemStackBuilder changeItems = new ItemStackBuilder().material(Material.DIAMOND_CHESTPLATE).name(getPlayer(), CoreLang.GUI_EDIT_KIT_CHANGE_ITEMS);
        ItemStackBuilder enabled = new ItemStackBuilder().createTogglableItem(getPlayer(), kit.isEnabled());
        ItemStackBuilder setPotionEffects = new ItemStackBuilder(new PotionBuilder().effect(PotionType.WATER).toItemStack(1)).name(getPlayer(), CoreLang.GUI_EDIT_KIT_POTION_EFFECTS);
        ItemStackBuilder descriptionItem = new ItemStackBuilder().material(Material.WRITABLE_BOOK).name(getPlayer(), CoreLang.GUI_EDIT_KIT_DESCRIPTION);
        ItemStackBuilder itemFrame = new ItemStackBuilder().material(Material.ITEM_FRAME).name(getPlayer(), CoreLang.GUI_EDIT_KIT_DISPLAY_ITEM);
        ItemStackBuilder barrier = new ItemStackBuilder().material(Material.BARRIER).name(getPlayer(), CoreLang.GUI_EDIT_KIT_DELETE);

        addButton(new SimpleButton(enabledSlimeball).action(() -> {
            kit.setOneTimeKit(!kit.isOneTimeKit());
            kit.save();
            refresh();
        }), 2);
        addButton(new AnvilButton(renameItem).action(new AnvilButtonAction() {
            @Override
            public void onConfirm(String output) {
                kit.setKitName(output);
                kit.save();
                player.sendMessage(CoreLang.GUI_EDIT_KIT_KIT_RENAMED.getMessage(player));
                new EditKitUI(plugin, kitUIFactory, permissionGroupUIFactory, kitManager, player, kit);
            }

            @Override
            public void onCancel() {
                new EditKitUI(plugin, kitUIFactory, permissionGroupUIFactory, kitManager, player, kit);
            }
        }).slot(AnvilSlot.INPUT_LEFT, new ItemStackBuilder().material(Material.NAME_TAG).name(kit.getKitName())), 4);
        addButton(new SimpleButton(preview).action(() -> kitUIFactory.createKitPreview(player, kit)), 6);

        addButton(new SimpleButton(priceItem).action(() -> kitUIFactory.createEditPriceUI(player, kit)), 12);
        addButton(new SimpleButton(permission).action(() -> permissionGroupUIFactory.create(player, kit, () -> new EditKitUI(plugin, kitUIFactory, permissionGroupUIFactory, kitManager, player, kit))), 14);

        addButton(new SimpleButton(changeItems).action(() -> {
            kit.setArmorItems(player.getInventory().getArmorContents());
            kit.setInventoryItems(player.getInventory().getContents());
            kit.save();
            player.sendMessage(CoreLang.GUI_EDIT_KIT_ITEMS_UPDATED.getMessage(player));
        }), 20);

        addButton(new SimpleButton(enabled).action(() -> {
            kit.setEnabled(!kit.isEnabled());
            kit.save();
            refresh();
        }), 22);

        addButton(new SimpleButton(setPotionEffects).action(() -> kitUIFactory.createChoosePotionEffectUI(player, kit)), 24);
        addButton(new BackButton(getPlayer()).action(() -> kitUIFactory.createChooseKitToEditGUI(player)), 27);
        addButton(new SimpleButton(itemFrame, () -> new ItemMainPage(plugin, player, (kit.getDisplayItem() == null ? Material.REDSTONE_BLOCK.parseItem() : kit.getDisplayItem()), new UpdateAction() {
            @Override
            public void onUpdate(ItemStack itemStack) {
                kit.setDisplayItem(itemStack);
                kit.save();
            }

            @Override
            public void onBack() {
                new EditKitUI(plugin, kitUIFactory, permissionGroupUIFactory, kitManager, player, kit);
            }
        })), 30);

        addButton(new SimpleButton(descriptionItem).action(() -> new DescriptionEditorMainPage(plugin, player, new DescriptionEdition() {
            @Override
            public void onBack() {
                new EditKitUI(plugin, kitUIFactory, permissionGroupUIFactory, kitManager, player, kit);
            }

            @Override
            public void onUpdate() {
                kit.save();
            }

            @Override
            public List<String> getDescription() {
                return kit.getDescription();
            }
        })), 32);

        addButton(new SimpleButton(barrier, () -> {
                kitManager.getKits().remove(kit.getKitName());
                kit.delete();
                player.sendMessage(CoreLang.GUI_EDIT_KIT_KIT_DELETED.getMessage(player));
                player.closeInventory();
        }), 35);
    }

}