package me.patothebest.thetowers.guis.setup;

import me.patothebest.gamecore.PluginConfig;
import me.patothebest.gamecore.gui.anvil.AnvilSlot;
import me.patothebest.gamecore.gui.inventory.GUIPage;
import me.patothebest.gamecore.gui.inventory.button.AnvilButton;
import me.patothebest.gamecore.gui.inventory.button.AnvilButtonAction;
import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.thetowers.language.Lang;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class MainMenu extends GUIPage {

    public MainMenu(Plugin plugin, Player player) {
        super(plugin, player, Lang.GUI_MAIN_TITLE.getMessage(player), 9);
        build();
    }

    @Override
    public void buildPage() {
        ItemStackBuilder createArena = new ItemStackBuilder().material(Material.EMERALD).name(Lang.GUI_MAIN_CREATE_ARENA.getMessage(getPlayer()));
        ItemStackBuilder editArenaButton = new ItemStackBuilder().material(Material.WRITABLE_BOOK).name(Lang.GUI_MAIN_EDIT_ARENA.getMessage(getPlayer()));

        addButton(new AnvilButton(createArena, new AnvilButtonAction() {
            @Override
            public void onConfirm(String output) {
                player.chat("/" + PluginConfig.BASE_COMMAND + " setup createarena " + output);
                player.closeInventory();
            }

            @Override
            public void onCancel() {
                new MainMenu(plugin, player);
            }
        }).slot(AnvilSlot.INPUT_LEFT, new ItemStackBuilder().material(Material.MAP).name("Arena")), 3);
        addButton(new SimpleButton(editArenaButton, () -> new ChooseArenaToEditGUI(plugin, player)), 5);
    }

}
