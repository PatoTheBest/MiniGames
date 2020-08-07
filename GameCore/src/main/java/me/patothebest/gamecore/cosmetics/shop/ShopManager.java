package me.patothebest.gamecore.cosmetics.shop;

import me.patothebest.gamecore.lang.interfaces.ILang;

import java.util.Collection;
import java.util.Map;

public interface ShopManager<ShopItemType extends ShopItem> {

    Collection<ShopItemType> getShopItems();

    ILang getTitle();

    ILang getName();

    Map<String, ShopItemType> getShopItemsMap();

    Class<ShopItemType> getShopItemClass();

    String getShopName();

}
