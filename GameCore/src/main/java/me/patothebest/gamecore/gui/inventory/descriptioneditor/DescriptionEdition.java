package me.patothebest.gamecore.gui.inventory.descriptioneditor;

import me.patothebest.gamecore.gui.inventory.page.GenericGUI;

import java.util.List;

public interface DescriptionEdition extends GenericGUI {

    List<String> getDescription();

    void onUpdate();

}
