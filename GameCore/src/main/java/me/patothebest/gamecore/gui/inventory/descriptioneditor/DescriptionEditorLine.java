package me.patothebest.gamecore.gui.inventory.descriptioneditor;

import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.gui.anvil.AnvilSlot;
import me.patothebest.gamecore.gui.inventory.GUIPage;
import me.patothebest.gamecore.gui.inventory.button.AnvilButton;
import me.patothebest.gamecore.gui.inventory.button.AnvilButtonAction;
import me.patothebest.gamecore.gui.inventory.button.BackButton;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class DescriptionEditorLine extends GUIPage {

    private final DescriptionEdition descriptionEdition;
    private final String line;

    public DescriptionEditorLine(Plugin plugin, Player player, DescriptionEdition descriptionEdition, String line) {
        super(plugin, player, CoreLang.GUI_DESCRIPTION_EDITOR_TITLE.getMessage(player), 9);
        this.descriptionEdition = descriptionEdition;
        this.line = line;
        build();
    }

    @Override
    protected void buildPage() {
        addButton(new BackButton(getPlayer()).action(() -> new DescriptionEditorMainPage(plugin, player, descriptionEdition)), 0);

        addButton(new AnvilButton(new ItemStackBuilder().material(Material.NAME_TAG).name(getPlayer(), CoreLang.GUI_DESCRIPTION_EDITOR_RENAME)).action(new AnvilButtonAction() {
            @Override
            public void onConfirm(String output) {
                descriptionEdition.getDescription().remove(line);
                descriptionEdition.getDescription().add(output);
                descriptionEdition.onUpdate();
            }

            @Override
            public void onCancel() {
                new DescriptionEditorLine(plugin, player, descriptionEdition, line);
            }
        }).slot(AnvilSlot.INPUT_LEFT, new ItemStackBuilder().material(Material.NAME_TAG).name(line)), 4);

        addButton(new SimpleButton(new ItemStackBuilder().material(Material.TNT).name(getPlayer(), CoreLang.GUI_DESCRIPTION_EDITOR_DELETE)).action(() -> {
            descriptionEdition.getDescription().remove(line);
            descriptionEdition.onUpdate();
            new DescriptionEditorLine(plugin, player, descriptionEdition, line);
        }), 8);
    }

}
