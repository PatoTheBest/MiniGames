package me.patothebest.gamecore.treasure.reward;

import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.player.IPlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public abstract class Reward {

    private int chance;
    protected String hologramMessage;
    protected ItemStack displayItem;

    /**
     * Gives the reward to the player
     *
     * @param player the player to give the reward
     */
    public abstract RewardData give(IPlayer player);

    /**
     * Parses the reward information from the config
     *
     * @param configurationSection the configuration section
     * @return true if the reward has been parsed successfully
     */
    public boolean parse(ConfigurationSection configurationSection) {
        if(!configurationSection.isSet("chance")) {
            System.err.println("Reward " + configurationSection.getCurrentPath() + " is missing chance!");
            return false;
        }

        if(configurationSection.isSet("hologram-message")) {
            hologramMessage = configurationSection.getString("hologram-message");
        } else {
            if(hologramMessage == null || hologramMessage.isEmpty()) {
                System.err.println("Reward " + configurationSection.getCurrentPath() + " is missing hologram-message!");
                return false;
            }
        }

        if(configurationSection.isSet("display-item")) {
            displayItem = Utils.itemStackFromString(configurationSection.getString("display-item"));
        } else {
            if(displayItem == null) {
                System.err.println("Reward " + configurationSection.getCurrentPath() + " is missing display-item!");
                return false;
            }
        }

        chance = configurationSection.getInt("chance");
        return true;
    }

    /**
     * Checks to see if the player can get this reward or not
     *
     * @return true if the player can get the reward
     */
    public boolean canGiveReward(IPlayer player) {
        return true;
    }

    /**
     * Sets the chance
     */
    public void setChance(int chance) {
        this.chance = chance;
    }

    /**
     * Gets the chance
     *
     * @return the chance
     */
    public int getChance() {
        return chance;
    }
}
