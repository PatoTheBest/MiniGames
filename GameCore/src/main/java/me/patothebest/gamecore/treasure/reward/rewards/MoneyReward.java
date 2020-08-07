package me.patothebest.gamecore.treasure.reward.rewards;

import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.treasure.reward.Reward;
import me.patothebest.gamecore.treasure.reward.RewardData;
import org.bukkit.configuration.ConfigurationSection;

import javax.inject.Inject;

public class MoneyReward extends Reward {

    private int minAmount = 1;
    private int maxAmount = 1;

    @Inject private MoneyReward() {
        hologramMessage = "+%amount% Coins";
        displayItem = new ItemStackBuilder(Material.SUNFLOWER);
    }

    @Override
    public boolean parse(ConfigurationSection configurationSection) {
        boolean parsedSuper = super.parse(configurationSection);
        boolean parsed = true;

        if (configurationSection.isSet("min-amount")) {
            minAmount = configurationSection.getInt("min-amount");
        } else {
            parsed = false;
            System.err.println("Reward " + configurationSection.getCurrentPath() + " is missing min-amount!");
        }

        if (configurationSection.isSet("max-amount")) {
            maxAmount = configurationSection.getInt("max-amount");
        } else {
            parsed = false;
            System.err.println("Reward " + configurationSection.getCurrentPath() + " is missing max-amount!");
        }

        return parsed && parsedSuper;
    }

    @Override
    public RewardData give(IPlayer player) {
        int amount = Utils.randInt(minAmount, maxAmount);
        if (player.giveMoney(amount)) {
            System.err.println("No compatible economy plugin has been found!");
            return new RewardData("Error", new ItemStackBuilder(Material.BARRIER));
        }

        return new RewardData(hologramMessage.replace("%amount%", amount + ""), displayItem);
    }

    /**
     * Sets the minimum amount of items the player should get
     */
    public void setMinAmount(int minAmount) {
        this.minAmount = minAmount;
    }

    /**
     * Sets the maximum amount of items the player should get
     */
    public void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
    }
}
