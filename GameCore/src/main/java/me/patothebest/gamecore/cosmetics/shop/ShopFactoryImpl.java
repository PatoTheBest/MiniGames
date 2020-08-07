package me.patothebest.gamecore.cosmetics.shop;

import com.google.inject.Inject;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.inject.Provider;

public class ShopFactoryImpl implements ShopFactory {

    private final CorePlugin plugin;
    private final PlayerManager playerManager;
    private final Provider<Economy> economyProvider;

    @Inject private ShopFactoryImpl(CorePlugin plugin, PlayerManager playerManager, Provider<Economy> economyProvider) {
        this.plugin = plugin;
        this.playerManager = playerManager;
        this.economyProvider = economyProvider;
    }

    @Override
    public <ShopItemType extends ShopItem, PlayerType extends IPlayer> ShopMenu<ShopItemType, PlayerType>  createShopMenu(Player player, ShopManager<ShopItemType> shopManager) {
        return new ShopMenu<>(plugin, player, shopManager, playerManager, this);
    }

    @Override
    public <ShopItemType extends ShopItem, PlayerType extends IPlayer> ShopMenuUses<ShopItemType, PlayerType>  createUsesShopMenu(Player player, ItemStack displayItem, ShopManager<ShopItemType> shopManager, ShopItemType shopItemType) {
        return new ShopMenuUses<>(plugin, player, displayItem, shopManager, shopItemType, playerManager, this, economyProvider);
    }
}
