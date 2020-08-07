package me.patothebest.gamecore.gui.inventory.itemeditor;

import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.gui.inventory.GUIPage;
import me.patothebest.gamecore.gui.inventory.button.IncrementingButton;
import me.patothebest.gamecore.gui.inventory.button.IncrementingButtonAction;
import me.patothebest.gamecore.gui.inventory.button.PlaceHolder;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class ChangeItemAmount extends GUIPage {

    private final ItemStackBuilder itemStack;
    private final UpdateAction updateAction;

    public ChangeItemAmount(Plugin plugin, Player player, ItemStackBuilder itemStack, UpdateAction updateAction) {
        super(plugin, player, CoreLang.GUI_CHANGE_AMOUNT_TITLE.getMessage(player), 9);
        this.itemStack = itemStack;
        this.updateAction = updateAction;
        build();
    }

    @Override
    public void buildPage() {
        IncrementingButtonAction onUpdateItemSize = (amount) ->  {
          if(itemStack.getAmount()+amount <= 0) {
            itemStack.amount(1);
          } else if(itemStack.getAmount()+amount > 64) {
              itemStack.amount(64);
          } else {
              itemStack.amount(itemStack.getAmount() + amount);
          }

          updateAction.onUpdate(itemStack);
          refresh();
        };

        addButton(new IncrementingButton(-10, onUpdateItemSize), 1);
        addButton(new IncrementingButton(-5, onUpdateItemSize), 2);
        addButton(new IncrementingButton(-1, onUpdateItemSize), 3);
        addButton(new IncrementingButton(1, onUpdateItemSize), 5);
        addButton(new IncrementingButton(5, onUpdateItemSize), 6);
        addButton(new IncrementingButton(10, onUpdateItemSize), 7);

        addButton(new SimpleButton(new ItemStackBuilder().createBackItem(getPlayer()), () -> new ItemMainPage(plugin, player, itemStack, updateAction)), 0);
        addButton(new PlaceHolder(itemStack), 4);
        addButton(new SimpleButton(new ItemStackBuilder().material(Material.TNT).name(CoreLang.GUI_CHANGE_AMOUNT_RESET.getMessage(getPlayer())), () -> {
            itemStack.setAmount(1);
            updateAction.onUpdate(itemStack);
            refresh();
        }), 8);
    }

}
