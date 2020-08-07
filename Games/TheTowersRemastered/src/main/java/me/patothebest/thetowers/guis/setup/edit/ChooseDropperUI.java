package me.patothebest.thetowers.guis.setup.edit;

import me.patothebest.thetowers.arena.Arena;
import me.patothebest.thetowers.arena.ItemDropper;
import me.patothebest.gamecore.gui.anvil.AnvilSlot;
import me.patothebest.gamecore.gui.inventory.button.AnvilButton;
import me.patothebest.gamecore.gui.inventory.button.AnvilButtonAction;
import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.gui.inventory.page.GUIMultiPage;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.thetowers.language.Lang;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

class ChooseDropperUI extends GUIMultiPage {

    private final Arena arena;

    ChooseDropperUI(Plugin plugin, Player player, Arena arena) {
        super(plugin, player, Lang.GUI_CHOOSE_DROPPER_TITLE.getMessage(player), 54);
        this.arena = arena;
        build();
    }

    @Override
    protected void buildContent() {
        final int[] i = {0};
        arena.getDroppers().entrySet().stream().skip(pageSize*currentPage).limit(pageSize).forEach(itemDropper -> {
            addButton(new SimpleButton(new ItemStackBuilder(itemDropper.getValue().getItemStack()).name(ChatColor.GREEN + itemDropper.getKey()), () -> new EditDropperUI(plugin, player, arena, itemDropper.getValue())), i[0]);
            i[0]++;
        });

        addButton(new SimpleButton(new ItemStackBuilder().createBackItem(getPlayer()), () -> new EditArenaUI(plugin, player, arena)), 47);
        addButton(new AnvilButton(new ItemStackBuilder().material(Material.DRAGON_EGG).name(getPlayer(), Lang.GUI_CHOOSE_DROPPER_CREATE), new AnvilButtonAction() {
            @Override
            public void onConfirm(String output) {
                ItemDropper dropper = new ItemDropper(arena, output, player.getLocation(), new ItemStackBuilder(Material.STONE), 1);
                arena.getDroppers().put(output, dropper);
                arena.save();
                new EditDropperUI(plugin, player, arena, dropper);
            }

            @Override
            public void onCancel() {
                new ChooseDropperUI(plugin, player, arena);
            }
        }).slot(AnvilSlot.INPUT_LEFT, new ItemStackBuilder().material(Material.DROPPER).name("Dropper")), 51);
    }

    @Override
    protected int getListCount() {
        return arena.getDroppers().size();
    }

}