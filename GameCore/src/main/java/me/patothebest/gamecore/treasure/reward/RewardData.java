package me.patothebest.gamecore.treasure.reward;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public class RewardData {

    private final String name;
    private final ItemStack itemStack;
    private boolean rareItem;

    public RewardData(String name, ItemStack itemStack) {
        this.name = ChatColor.translateAlternateColorCodes('&', name);
        this.itemStack = itemStack;
    }

    /**
     * Gets the name of the item
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the item representing the reward
     *
     * @return the item
     */
    public ItemStack getItemStack() {
        return itemStack;
    }

    /**
     * Gets if the reward is a rare item
     *
     * @return if it is rare
     */
    public boolean isRareItem() {
        return rareItem;
    }

    public RewardData rareItem(boolean rare) {
        this.rareItem = rare;
        return this;
    }
}
