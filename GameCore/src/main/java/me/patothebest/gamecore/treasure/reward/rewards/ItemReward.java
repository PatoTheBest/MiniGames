package me.patothebest.gamecore.treasure.reward.rewards;

import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.treasure.reward.Reward;
import me.patothebest.gamecore.treasure.reward.RewardData;
import org.bukkit.configuration.ConfigurationSection;

public class ItemReward extends Reward {

    private Material material;
    private int minAmount = 1;
    private int maxAmount = 1;

    public ItemReward() {
        hologramMessage = "%amount% %material%";
    }

    @Override
    public boolean parse(ConfigurationSection configurationSection) {
        boolean parsed = true;

        if(configurationSection.isSet("material")) {
            material = Material.matchMaterial(configurationSection.getString("material").toUpperCase()).orElse(Material.DIRT);
            displayItem = new ItemStackBuilder(material);
        } else {
            System.err.println("Reward " + configurationSection.getCurrentPath() + " is missing material!");
            parsed = false;
        }

        if (configurationSection.isSet("min-amount")) {
            minAmount = configurationSection.getInt("min-amount");
        }

        if (configurationSection.isSet("max-amount")) {
            maxAmount = configurationSection.getInt("max-amount");
        }

        return parsed && super.parse(configurationSection);
    }

    @Override
    public RewardData give(IPlayer player) {
        int amount = Utils.randInt(minAmount, maxAmount);
        player.getPlayer().getInventory().addItem(new ItemStackBuilder(material).amount(amount));

        return new RewardData(hologramMessage.replace("%amount%", amount + "").replace("%material%", Utils.capitalizeFirstLetter(material.name())), displayItem);
    }
}
