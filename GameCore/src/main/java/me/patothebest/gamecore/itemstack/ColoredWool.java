package me.patothebest.gamecore.itemstack;

import me.patothebest.gamecore.util.Utils;
import org.bukkit.inventory.ItemStack;

public class ColoredWool {

    public static final ItemStack WHITE = Material.WHITE_WOOL.parseItem();
    public static final ItemStack ORANGE = Material.ORANGE_WOOL.parseItem();
    public static final ItemStack MAGENTA = Material.MAGENTA_WOOL.parseItem();
    public static final ItemStack LIGHT_BLUE = Material.LIGHT_BLUE_WOOL.parseItem();
    public static final ItemStack YELLOW = Material.YELLOW_WOOL.parseItem();
    public static final ItemStack LIME = Material.LIME_WOOL.parseItem();
    public static final ItemStack PINK = Material.PINK_WOOL.parseItem();
    public static final ItemStack GRAY = Material.GRAY_WOOL.parseItem();
    public static final ItemStack LIGHT_GRAY = Material.LIGHT_GRAY_WOOL.parseItem();
    public static final ItemStack CYAN = Material.CYAN_WOOL.parseItem();
    public static final ItemStack PURPLE = Material.PURPLE_WOOL.parseItem();
    public static final ItemStack BLUE = Material.BLUE_WOOL.parseItem();
    public static final ItemStack BROWN = Material.BROWN_WOOL.parseItem();
    public static final ItemStack GREEN = Material.GREEN_WOOL.parseItem();
    public static final ItemStack RED = Material.RED_WOOL.parseItem();
    public static final ItemStack BLACK = Material.BLACK_WOOL.parseItem();

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

    public static Material getRandomMaterial() {
        switch (Utils.randInt(0, 15)) {
            case 0:
                return Material.WHITE_WOOL;
            case 1:
                return Material.ORANGE_WOOL;
            case 2:
                return Material.MAGENTA_WOOL;
            case 3:
                return Material.LIGHT_BLUE_WOOL;
            case 4:
                return Material.YELLOW_WOOL;
            case 5:
                return Material.LIME_WOOL;
            case 6:
                return Material.PINK_WOOL;
            case 7:
                return Material.GRAY_WOOL;
            case 8:
                return Material.LIGHT_GRAY_WOOL;
            case 9:
                return Material.CYAN_WOOL;
            case 10:
                return Material.PURPLE_WOOL;
            case 11:
                return Material.BLUE_WOOL;
            case 12:
                return Material.BROWN_WOOL;
            case 13:
                return Material.GREEN_WOOL;
            case 14:
                return Material.RED_WOOL;
            case 15:
                return Material.BLACK_WOOL;
        }

        return Material.WHITE_WOOL;
    }

}
