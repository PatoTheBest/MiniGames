package me.patothebest.gamecore.feature.features.gameoptions;

import me.patothebest.gamecore.feature.Feature;
import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import org.bukkit.entity.Player;

public interface GameOption extends Feature {

    SimpleButton getButton(Player player);

}
