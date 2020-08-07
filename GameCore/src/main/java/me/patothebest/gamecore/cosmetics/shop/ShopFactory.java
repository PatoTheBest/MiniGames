package me.patothebest.gamecore.cosmetics.shop;

import me.patothebest.gamecore.player.IPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface ShopFactory {

    <ShopItemType extends ShopItem, PlayerType extends IPlayer> ShopMenu<ShopItemType, PlayerType> createShopMenu(Player player, ShopManager<ShopItemType> shopManager);

    <ShopItemType extends ShopItem, PlayerType extends IPlayer> ShopMenuUses<ShopItemType, PlayerType>  createUsesShopMenu(Player player, ItemStack displayItem, ShopManager<ShopItemType> shopManager, ShopItemType shopItemType);

}
