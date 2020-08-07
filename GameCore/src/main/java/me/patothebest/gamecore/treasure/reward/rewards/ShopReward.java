package me.patothebest.gamecore.treasure.reward.rewards;

import com.google.inject.Inject;
import me.patothebest.gamecore.cosmetics.shop.ShopItem;
import me.patothebest.gamecore.cosmetics.shop.ShopManager;
import me.patothebest.gamecore.cosmetics.shop.ShopRegistry;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.logger.InjectParentLogger;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.treasure.TreasureManager;
import me.patothebest.gamecore.treasure.reward.Reward;
import me.patothebest.gamecore.treasure.reward.RewardData;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ShopReward extends Reward {

    private int minAmount = 1;
    private int maxAmount = 1;
    private final List<ShopItem> shopItems = new ArrayList<>();
    private final ShopRegistry shopRegistry;
    private final PlayerManager playerManager;
    @InjectParentLogger(parent = TreasureManager.class) private Logger logger;

    @Inject private ShopReward(ShopRegistry shopRegistry, PlayerManager playerManager) {
        this.shopRegistry = shopRegistry;
        this.playerManager = playerManager;
        hologramMessage = "%amount%%shop-name% %item%";
        displayItem = new ItemStackBuilder(Material.BARRIER);
    }

    @Override
    public boolean parse(ConfigurationSection configurationSection) {
        boolean parsedSuper = super.parse(configurationSection);
        boolean parsed = false;

        if(configurationSection.isSet("shop-type")) {
            String shopType = configurationSection.getString("shop-type");
            ShopManager<?> shopManager = shopRegistry.getShopManagersNamesMap().get(shopType);

            if(shopManager != null) {
                if (configurationSection.isSet("shop-item")) {
                    ShopItem shopItem = shopManager.getShopItemsMap().get(configurationSection.getString("shop-item"));

                    if(shopItem != null) {
                        shopItems.add(shopItem);
                        parsed = true;
                    } else {
                        logger.log(Level.SEVERE, "Shop item {0} is not found in reward {1}", new Object[]{ configurationSection.getString("shop-item"), configurationSection.getCurrentPath() });
                    }
                }

                if (configurationSection.isSet("shop-items")) {
                    for (String shopItemName : configurationSection.getStringList("shop-items")) {
                        ShopItem shopItem = shopManager.getShopItemsMap().get(shopItemName);

                        if(shopItem != null) {
                            shopItems.add(shopItem);
                            parsed = true;
                        } else {

                            logger.log(Level.SEVERE, "Shop item {0} is not found in reward {1}", new Object[]{ configurationSection.getString("shop-item"), configurationSection.getCurrentPath() });
                        }
                    }
                }
            } else {
                parsed = false;
                logger.log(Level.SEVERE, "Reward {0} shop-type is invalid! Unknown shop-type {1}", new Object[]{ configurationSection.getCurrentPath(), shopType });
            }
        } else {
            parsed = false;
            logger.log(Level.SEVERE, "Reward {0} is missing shop-type!", new Object[]{ configurationSection.getCurrentPath() });
        }

        if (configurationSection.isSet("min-amount")) {
            minAmount = configurationSection.getInt("min-amount");
        } else {
            parsed = false;
            logger.log(Level.SEVERE, "Reward {0} is missing min-amount!", new Object[]{ configurationSection.getCurrentPath() });
        }

        if (configurationSection.isSet("max-amount")) {
            maxAmount = configurationSection.getInt("max-amount");
        } else {
            parsed = false;
            logger.log(Level.SEVERE, "Reward {0} is missing max-amount!", new Object[]{ configurationSection.getCurrentPath() });
        }

        return parsed && parsedSuper;
    }

    @Override
    public boolean canGiveReward(IPlayer player) {
        List<ShopItem> shopItemsForPlayer = new ArrayList<>(shopItems);
        shopItemsForPlayer.removeIf(shopItem -> shopItem.isPermanent() && player.canUse(shopItem));
        return !shopItemsForPlayer.isEmpty();
    }

    @Override
    public RewardData give(IPlayer player) {
        List<ShopItem> shopItemsForPlayer = new ArrayList<>(shopItems);
        shopItemsForPlayer.removeIf(shopItem -> shopItem.isPermanent() && player.canUse(shopItem));
        ShopItem shopItem = Utils.getRandomElementFromList(shopItemsForPlayer);

        if(shopItem == null) {
            logger.log(Level.WARNING, "Shop Item is null in reward ShopReward for player!", player.getName());
            return new RewardData("Error", new ItemStackBuilder(Material.BARRIER));
        }

        int amount = -1;

        if(!shopItem.isPermanent()) {
            amount = Utils.randInt(minAmount, maxAmount);
            player.buyItemUses(shopItem, amount);
        } else {
            player.buyItemPermanently(shopItem);
        }

        return new RewardData(hologramMessage
                .replace("%item%", shopItem.getDisplayName())
                .replace("%amount%", amount == -1 ? "" : amount + " ")
                .replace("%shop-name%", shopRegistry.getShopItemsManagers().get(shopItem.getClass()).getName().getMessage(player)), shopItem.getDisplayItem());
    }
}