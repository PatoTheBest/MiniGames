package me.patothebest.gamecore.feature.features.chests.refill;

import org.bukkit.inventory.ItemStack;

class ChestItem {

    private final ItemStack item;
    private final int chance;

    ChestItem(ItemStack item, int chance) {
        this.item = item;
        this.chance = chance;
    }

    public ItemStack getItem() {
        return item;
    }

    int getChance() {
        return chance;
    }

    @Override
    public String toString() {
        return "ChestItem{" +
                "item=" + item +
                ", chance=" + chance +
                '}';
    }
}