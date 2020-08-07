package me.patothebest.gamecore.feature.features.gameoptions.time;

import me.patothebest.gamecore.itemstack.StainedClay;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.lang.interfaces.ILang;
import org.bukkit.inventory.ItemStack;

public enum TimeOfTheDay {

    MORNING(CoreLang.GUI_TIME_VOTE_MORNING, StainedClay.YELLOW, 0, 1),
    NOON(CoreLang.GUI_TIME_VOTE_NOON, StainedClay.ORANGE, 6000, 3),
    SUNSET(CoreLang.GUI_TIME_VOTE_SUNSET, StainedClay.LIGHT_GRAY, 12000, 5),
    MIDNIGHT(CoreLang.GUI_TIME_VOTE_MIDNIGHT, StainedClay.GRAY, 18000, 7);

    private final ILang langMessage;
    private final ItemStack stainedClay;
    private final int time;
    private final int slot;

    TimeOfTheDay(ILang langMessage, ItemStack stainedClay, int time, int slot) {
        this.langMessage = langMessage;
        this.stainedClay = stainedClay;
        this.time = time;
        this.slot = slot;
    }

    public ILang getLangMessage() {
        return langMessage;
    }

    public ItemStack getItem() {
        return stainedClay;
    }

    public int getTime() {
        return time;
    }

    public int getSlot() {
        return slot;
    }
}
