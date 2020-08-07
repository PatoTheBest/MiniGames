package me.patothebest.gamecore.guis.kit;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.gui.inventory.GUIPage;
import me.patothebest.gamecore.gui.inventory.button.BackButton;
import me.patothebest.gamecore.gui.inventory.button.IncrementingButton;
import me.patothebest.gamecore.gui.inventory.button.IncrementingButtonAction;
import me.patothebest.gamecore.gui.inventory.button.PlaceHolder;
import me.patothebest.gamecore.kit.Kit;
import org.bukkit.entity.Player;

public class EditPriceUI extends GUIPage {

    private final Kit kit;
    private final KitUIFactory kitUIFactory;

    @Inject private EditPriceUI(CorePlugin plugin, @Assisted Player player, @Assisted Kit kit, KitUIFactory kitUIFactory) {
        super(plugin, player, CoreLang.GUI_EDIT_PRICE_TITLE.getMessage(player), 9);
        this.kit = kit;
        this.kitUIFactory = kitUIFactory;
        build();
    }

    @Override
    public void buildPage() {
        IncrementingButtonAction onUpdateCost = (amount) ->  {
            kit.setCost(kit.getCost() + amount < 0 ? 0 : kit.getCost() + amount);
            kit.save();
            refresh();
        };

        addButton(new IncrementingButton(-100, onUpdateCost), 1);
        addButton(new IncrementingButton(-10, onUpdateCost), 2);
        addButton(new IncrementingButton(-1, onUpdateCost), 3);
        addButton(new IncrementingButton(1, onUpdateCost), 5);
        addButton(new IncrementingButton(10, onUpdateCost), 6);
        addButton(new IncrementingButton(100, onUpdateCost), 7);

        addButton(new BackButton(getPlayer(), () -> kitUIFactory.createEditKitGUI(player, kit)), 0);
        addButton(new PlaceHolder(new ItemStackBuilder(kit.getDisplayItem()).name(CoreLang.GUI_EDIT_PRICE_KIT_ITEM.replace(getPlayer(), kit.getKitName())).lore(CoreLang.GUI_EDIT_PRICE_LORE.replace(getPlayer(), kit.getCost()))), 4);
        addButton(new SimpleButton(new ItemStackBuilder().material(Material.TNT).name(getPlayer(), CoreLang.GUI_EDIT_PRICE_RESET), () -> {
            kit.setCost(0);
            kit.save();
            refresh();
        }), 8);
    }
}