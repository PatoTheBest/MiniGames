package me.patothebest.gamecore.guis.kit;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.gui.anvil.AnvilSlot;
import me.patothebest.gamecore.gui.inventory.button.AnvilButton;
import me.patothebest.gamecore.gui.inventory.button.AnvilButtonAction;
import me.patothebest.gamecore.gui.inventory.page.GUIMultiPage;
import me.patothebest.gamecore.kit.Kit;
import me.patothebest.gamecore.kit.KitManager;
import me.patothebest.gamecore.player.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class ChooseKitToEditGUI extends GUIMultiPage {

    private final KitManager kitManager;
    private final PlayerManager playerManager;
    private final KitUIFactory kitUIFactory;

    @Inject private ChooseKitToEditGUI(Plugin plugin, @Assisted Player player, KitManager kitManager, PlayerManager playerManager, KitUIFactory kitUIFactory) {
        super(plugin, player, CoreLang.GUI_CHOOSE_KIT_TITLE.getMessage(player), 54);
        this.kitManager = kitManager;
        this.playerManager = playerManager;
        this.kitUIFactory = kitUIFactory;
        build();
    }

    @Override
    protected void buildContent() {
        final int[] slot = {0};
        kitManager.getKits().values().stream().skip(currentPage * pageSize).limit(pageSize).forEach(kit -> {
            ItemStack item = new ItemStackBuilder(kit.finalDisplayItem(playerManager.getPlayer(getPlayer()), false)).name(CoreLang.GUI_CHOOSE_KIT_EDIT_ITEM.getMessage(getPlayer()).replace("%kit%", kit.getKitName()));
            addButton(new SimpleButton(item, () -> kitUIFactory.createEditKitGUI(player, kit)), slot[0]);
            slot[0]++;
        });

        addButton(new AnvilButton(new ItemStackBuilder().material(Material.EMERALD).name(CoreLang.GUI_CHOOSE_KIT_CREATE_ITEM.getMessage(getPlayer()))).action(new AnvilButtonAction() {
            @Override
            public void onConfirm(String output) {
                if (kitManager.kitExists(output)) {
                    player.sendMessage(CoreLang.KIT_ALREADY_EXISTS.getMessage(getPlayer()));
                    return;
                }

                Kit kit = kitManager.createKit(output, player.getInventory());
                player.sendMessage(CoreLang.KIT_CREATED.getMessage(getPlayer()));
                kitUIFactory.createEditKitGUI(player, kit);
            }

            @Override
            public void onCancel() {
                new ChooseKitToEditGUI(plugin, player, kitManager, playerManager, kitUIFactory);
            }
        }).slot(AnvilSlot.INPUT_LEFT, new ItemStackBuilder().material(Material.NAME_TAG).name("Kit")), 51);
    }

    @Override
    protected int getListCount() {
        return kitManager.getKits().size();
    }
}