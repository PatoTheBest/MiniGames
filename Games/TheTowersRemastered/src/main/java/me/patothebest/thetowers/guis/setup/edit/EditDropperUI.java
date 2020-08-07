package me.patothebest.thetowers.guis.setup.edit;

import me.patothebest.thetowers.arena.Arena;
import me.patothebest.thetowers.arena.ItemDropper;
import me.patothebest.gamecore.gui.inventory.GUIPage;
import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.gui.inventory.itemeditor.ItemMainPage;
import me.patothebest.gamecore.gui.inventory.itemeditor.UpdateAction;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.thetowers.language.Lang;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

class EditDropperUI extends GUIPage {

    private final Arena arena;
    private final ItemDropper itemDropper;

    EditDropperUI(Plugin plugin, Player player, Arena arena, ItemDropper itemDropper) {
        super(plugin, player, Lang.GUI_EDIT_DROPPER_TITLE.getMessage(player), 9);
        this.arena = arena;
        this.itemDropper = itemDropper;
        build();
    }

    @Override
    public void buildPage() {
        ItemStackBuilder changeInterval = new ItemStackBuilder().material(Material.EXPERIENCE_BOTTLE).name(Lang.GUI_EDIT_DROPPER_CHANGE_INTERVAL.getMessage(getPlayer()));
        ItemStackBuilder changeDroppedItem = new ItemStackBuilder(itemDropper.getItemStack()).name(Lang.GUI_EDIT_DROPPER_CHANGE_ITEM_NAME.getMessage(getPlayer()).replace("%name%", itemDropper.getName())).lore(Lang.GUI_EDIT_DROPPER_CHANGE_ITEM_LORE.getMessage(getPlayer()).replace("%interval%", itemDropper.getInterval() + "").split("\n"));

        addButton(new SimpleButton(new ItemStackBuilder().createBackItem(getPlayer()), () -> new ChooseDropperUI(plugin, player, arena)), 0);
        addButton(new SimpleButton(changeDroppedItem, () -> new ItemMainPage(plugin, player, itemDropper.getItemStack(), new UpdateAction() {
            @Override
            public void onUpdate(ItemStack itemStack) {
                itemDropper.setItemStack(new ItemStackBuilder(itemStack, null));
                arena.save();
            }

            @Override
            public void onBack() {
                new EditDropperUI(plugin, player, arena, itemDropper);
            }
        })), 3);

        addButton(new SimpleButton(changeInterval, () -> new EditDropperIntervalUI(plugin, player, arena, itemDropper)), 5);

        addButton(new SimpleButton(new ItemStackBuilder().material(Material.BARRIER).name(Lang.GUI_EDIT_DROPPER_DELETE.getMessage(getPlayer())), () -> {
            arena.getDroppers().remove(itemDropper.getName());
            arena.save();
            new ChooseDropperUI(plugin, player, arena);
        }), 8);
    }

}