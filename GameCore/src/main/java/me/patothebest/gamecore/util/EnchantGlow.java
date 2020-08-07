package me.patothebest.gamecore.util;

import org.bukkit.enchantments.Enchantment;

public class EnchantGlow {

    private static Enchantment glow;

    public static void setGlow(Enchantment glow) {
        EnchantGlow.glow = glow;
    }

    public static Enchantment getGlow() {
        return glow;
    }
}