package me.patothebest.gamecore.nms.v1_16_R3;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;

public class EnchantGlow extends EnchantmentWrapper {

    private static Enchantment glow;

    public EnchantGlow(int id) {
        super("glow_enchant");
    }

    @Override
    public boolean canEnchantItem(ItemStack item) {
        return true;
    }

    @Override
    public boolean conflictsWith(Enchantment other) {
        return false;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return null;
    }

    @Override
    public int getMaxLevel() {
        return 10;
    }

    @Override
    public String getName() {
        return "Glow";
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    public static Enchantment getGlow() {
        if (glow != null) {
            return glow;
        }

        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        glow = new EnchantGlow(255);
        try {
            Enchantment.registerEnchantment(glow);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return glow;
    }

    public static void addGlow(ItemStack item) {
        item.addEnchantment(getGlow(), 1);
    }
}