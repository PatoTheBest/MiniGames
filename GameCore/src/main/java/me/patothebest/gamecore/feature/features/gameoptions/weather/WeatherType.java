package me.patothebest.gamecore.feature.features.gameoptions.weather;

import me.patothebest.gamecore.itemstack.StainedClay;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.lang.interfaces.ILang;
import org.bukkit.inventory.ItemStack;

public enum WeatherType {

    SUN(CoreLang.GUI_WEATHER_VOTE_SUN, StainedClay.YELLOW, false, 2),
    RAIN(CoreLang.GUI_WEATHER_VOTE_RAIN, StainedClay.LIGHT_BLUE, true, 6);

    private final ILang langMessage;
    private final ItemStack stainedClay;
    private final boolean storm;
    private final int slot;

    WeatherType(ILang langMessage, ItemStack stainedClay, boolean storm, int slot) {
        this.langMessage = langMessage;
        this.stainedClay = stainedClay;
        this.storm = storm;
        this.slot = slot;
    }

    public ILang getLangMessage() {
        return langMessage;
    }

    public ItemStack getItem() {
        return stainedClay;
    }

    public boolean isStorm() {
        return storm;
    }

    public int getSlot() {
        return slot;
    }
}
