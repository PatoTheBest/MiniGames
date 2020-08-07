package me.patothebest.gamecore.gui.inventory.button;

import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import org.bukkit.entity.Player;

public class BackButton extends SimpleButton {

    public BackButton(Player player) {
        super(new ItemStackBuilder().createBackItem(player));
    }

    public BackButton(Player player, ButtonAction buttonAction) {
        super(new ItemStackBuilder().createBackItem(player), buttonAction);
    }
}