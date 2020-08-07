package me.patothebest.gamecore.itemstack;

import me.patothebest.gamecore.util.Utils;
import org.bukkit.inventory.ItemStack;

public class StainedGlass {

    public static final ItemStack WHITE = Material.WHITE_STAINED_GLASS.parseItem();
    public static final ItemStack ORANGE = Material.ORANGE_STAINED_GLASS.parseItem();
    public static final ItemStack MAGENTA = Material.MAGENTA_STAINED_GLASS.parseItem();
    public static final ItemStack LIGHT_BLUE = Material.LIGHT_BLUE_STAINED_GLASS.parseItem();
    public static final ItemStack YELLOW = Material.YELLOW_STAINED_GLASS.parseItem();
    public static final ItemStack LIME = Material.LIME_STAINED_GLASS.parseItem();
    public static final ItemStack PINK = Material.PINK_STAINED_GLASS.parseItem();
    public static final ItemStack GRAY = Material.GRAY_STAINED_GLASS.parseItem();
    public static final ItemStack LIGHT_GRAY = Material.LIGHT_GRAY_STAINED_GLASS.parseItem();
    public static final ItemStack CYAN = Material.CYAN_STAINED_GLASS.parseItem();
    public static final ItemStack PURPLE = Material.PURPLE_STAINED_GLASS.parseItem();
    public static final ItemStack BLUE = Material.BLUE_STAINED_GLASS.parseItem();
    public static final ItemStack BROWN = Material.BROWN_STAINED_GLASS.parseItem();
    public static final ItemStack GREEN = Material.GREEN_STAINED_GLASS.parseItem();
    public static final ItemStack RED = Material.RED_STAINED_GLASS.parseItem();
    public static final ItemStack BLACK = Material.BLACK_STAINED_GLASS.parseItem();

    public static ItemStack getRandom() {
        switch (Utils.randInt(0, 15)) {
            case 0:
                return WHITE;
            case 1:
                return ORANGE;
            case 2:
                return MAGENTA;
            case 3:
                return LIGHT_BLUE;
            case 4:
                return YELLOW;
            case 5:
                return LIME;
            case 6:
                return PINK;
            case 7:
                return GRAY;
            case 8:
                return LIGHT_GRAY;
            case 9:
                return CYAN;
            case 10:
                return PURPLE;
            case 11:
                return BLUE;
            case 12:
                return BROWN;
            case 13:
                return GREEN;
            case 14:
                return RED;
            case 15:
                return BLACK;
        }

        return WHITE;
    }
}
