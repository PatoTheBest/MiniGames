package me.patothebest.gamecore.itemstack;

import me.patothebest.gamecore.util.Utils;
import org.bukkit.inventory.ItemStack;

public class StainedClay {

    public static final ItemStack WHITE = Material.WHITE_TERRACOTTA.parseItem();
    public static final ItemStack ORANGE = Material.ORANGE_TERRACOTTA.parseItem();
    public static final ItemStack MAGENTA = Material.MAGENTA_TERRACOTTA.parseItem();
    public static final ItemStack LIGHT_BLUE = Material.LIGHT_BLUE_TERRACOTTA.parseItem();
    public static final ItemStack YELLOW = Material.YELLOW_TERRACOTTA.parseItem();
    public static final ItemStack LIME = Material.LIME_TERRACOTTA.parseItem();
    public static final ItemStack PINK = Material.PINK_TERRACOTTA.parseItem();
    public static final ItemStack GRAY = Material.GRAY_TERRACOTTA.parseItem();
    public static final ItemStack LIGHT_GRAY = Material.LIGHT_GRAY_TERRACOTTA.parseItem();
    public static final ItemStack CYAN = Material.CYAN_TERRACOTTA.parseItem();
    public static final ItemStack PURPLE = Material.PURPLE_TERRACOTTA.parseItem();
    public static final ItemStack BLUE = Material.BLUE_TERRACOTTA.parseItem();
    public static final ItemStack BROWN = Material.BROWN_TERRACOTTA.parseItem();
    public static final ItemStack GREEN = Material.GREEN_TERRACOTTA.parseItem();
    public static final ItemStack RED = Material.RED_TERRACOTTA.parseItem();
    public static final ItemStack BLACK = Material.BLACK_TERRACOTTA.parseItem();

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
                return Material.WHITE_TERRACOTTA;
            case 1:
                return Material.ORANGE_TERRACOTTA;
            case 2:
                return Material.MAGENTA_TERRACOTTA;
            case 3:
                return Material.LIGHT_BLUE_TERRACOTTA;
            case 4:
                return Material.YELLOW_TERRACOTTA;
            case 5:
                return Material.LIME_TERRACOTTA;
            case 6:
                return Material.PINK_TERRACOTTA;
            case 7:
                return Material.GRAY_TERRACOTTA;
            case 8:
                return Material.LIGHT_GRAY_TERRACOTTA;
            case 9:
                return Material.CYAN_TERRACOTTA;
            case 10:
                return Material.PURPLE_TERRACOTTA;
            case 11:
                return Material.BLUE_TERRACOTTA;
            case 12:
                return Material.BROWN_TERRACOTTA;
            case 13:
                return Material.GREEN_TERRACOTTA;
            case 14:
                return Material.RED_TERRACOTTA;
            case 15:
                return Material.BLACK_TERRACOTTA;
        }

        return Material.WHITE_TERRACOTTA;
    }

}
