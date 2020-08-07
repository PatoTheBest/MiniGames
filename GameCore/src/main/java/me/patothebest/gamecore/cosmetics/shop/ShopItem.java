package me.patothebest.gamecore.cosmetics.shop;

import me.patothebest.gamecore.util.NameableObject;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface ShopItem extends NameableObject {

    List<String> getDescription();

    int getPrice();

    ItemStack getDisplayItem();

    String getPermission();

    String getDisplayName();

    boolean isPermanent();

    default boolean isFree() {
        return getPrice() <= 0;
    }
}
