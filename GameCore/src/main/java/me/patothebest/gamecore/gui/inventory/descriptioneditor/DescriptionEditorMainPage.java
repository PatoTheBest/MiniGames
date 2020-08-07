package me.patothebest.gamecore.gui.inventory.descriptioneditor;

import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.gui.anvil.AnvilSlot;
import me.patothebest.gamecore.gui.inventory.button.AnvilButton;
import me.patothebest.gamecore.gui.inventory.button.AnvilButtonAction;
import me.patothebest.gamecore.gui.inventory.page.GUIMultiPage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class DescriptionEditorMainPage extends GUIMultiPage {

    private final DescriptionEdition descriptionEdition;

    public DescriptionEditorMainPage(Plugin plugin, Player player, DescriptionEdition descriptionEdition) {
        super(plugin, player, CoreLang.GUI_DESCRIPTION_EDITOR_MAIN_TITLE.getMessage(player));
        this.descriptionEdition = descriptionEdition;
        build();
    }

    @Override
    protected void buildContent() {
        final int[] slot = {0};
        descriptionEdition.getDescription().stream().skip(pageSize*currentPage).limit(pageSize).forEach(descriptionLine -> {
            addButton(new SimpleButton(new ItemStackBuilder().material(Material.WRITABLE_BOOK).name(ChatColor.translateAlternateColorCodes('&', descriptionLine))).action(() -> new DescriptionEditorLine(plugin, player, descriptionEdition, descriptionLine)), slot[0]);
            slot[0]++;
        });

        addButton(new SimpleButton(new ItemStackBuilder().createBackItem(getPlayer())).action(descriptionEdition::onBack), 47);
        addButton(new AnvilButton(new ItemStackBuilder().material(Material.EMERALD).name(getPlayer(), CoreLang.GUI_DESCRIPTION_EDITOR_MAIN_TITLE)).action(new AnvilButtonAction() {
            @Override
            public void onConfirm(String output) {
                descriptionEdition.getDescription().add(output);
                descriptionEdition.onUpdate();
                new DescriptionEditorLine(plugin, player, descriptionEdition, output);
            }

            @Override
            public void onCancel() {
                new DescriptionEditorMainPage(plugin, player, descriptionEdition);
            }
        }).slot(AnvilSlot.INPUT_LEFT, new ItemStackBuilder().material(Material.NAME_TAG).name("lore")),51);
    }

    @Override
    protected int getListCount() {
        return descriptionEdition.getDescription().size();
    }

}
