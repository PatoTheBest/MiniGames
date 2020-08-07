package me.patothebest.thetowers.guis.setup.edit;

import me.patothebest.thetowers.arena.Arena;
import me.patothebest.thetowers.arena.ItemDropper;
import me.patothebest.gamecore.gui.inventory.GUIPage;
import me.patothebest.gamecore.gui.inventory.button.IncrementingButton;
import me.patothebest.gamecore.gui.inventory.button.IncrementingButtonAction;
import me.patothebest.gamecore.gui.inventory.button.PlaceHolder;
import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.thetowers.language.Lang;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

class EditDropperIntervalUI extends GUIPage {

    private final Arena arena;
    private final ItemDropper itemDropper;

    EditDropperIntervalUI(Plugin plugin, Player player, Arena arena, ItemDropper itemDropper) {
        super(plugin, player, Lang.GUI_EDIT_INTERVAL_TITLE.getMessage(player), 9);
        this.arena = arena;
        this.itemDropper = itemDropper;
        build();
    }

    @Override
    public void buildPage() {
        IncrementingButtonAction onUpdateItemSize = (amount) ->  {
            if(itemDropper.getInterval()+amount <= 0) {
                itemDropper.setInterval(1);
            } else {
                itemDropper.setInterval(itemDropper.getInterval() + amount);
            }

            arena.save();
            refresh();
        };

        addButton(new IncrementingButton(-10, onUpdateItemSize), 1);
        addButton(new IncrementingButton(-5, onUpdateItemSize), 2);
        addButton(new IncrementingButton(-1, onUpdateItemSize), 3);
        addButton(new IncrementingButton(1, onUpdateItemSize), 5);
        addButton(new IncrementingButton(5, onUpdateItemSize), 6);
        addButton(new IncrementingButton(10, onUpdateItemSize), 7);

        addButton(new SimpleButton(new ItemStackBuilder().createBackItem(getPlayer()), () -> new EditDropperUI(plugin, player, arena, itemDropper)), 0);
        addButton(new PlaceHolder(new ItemStackBuilder(itemDropper.getItemStack()).name(Lang.GUI_EDIT_INTERVAL_ITEM_NAME.getMessage(getPlayer()).replace("%name%", itemDropper.getName())).lore(Lang.GUI_EDIT_INTERVAL_ITEM_LORE.getMessage(getPlayer()).replace("%interval%", itemDropper.getInterval() + "").split("\n"))), 4);
        addButton(new SimpleButton(new ItemStackBuilder().material(Material.TNT).name(Lang.GUI_EDIT_INTERVAL_RESET.getMessage(getPlayer())), () -> {
            itemDropper.setInterval(0);
            refresh();
            arena.save();
        }), 8);
    }

}
