package me.patothebest.gamecore.cosmetics.victoryeffects;

import com.google.common.base.Preconditions;
import me.patothebest.gamecore.cosmetics.shop.AbstractShopItem;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class VictoryEffectItem extends AbstractShopItem {

    private final VictoryEffectType victoryEffectType;
    private final ItemStack displayItem;

    VictoryEffectItem(String configName, Map<String, Object> data) {
        super(configName, data);
        this.displayItem = Utils.itemStackFromString((String) data.get("display-item"));
        this.victoryEffectType = Utils.getEnumValueFromString(VictoryEffectType.class, (String) data.get("effect"));
        Preconditions.checkNotNull(displayItem, "Material " + data.get("display-item") + " not found!");
        Preconditions.checkNotNull(displayItem, "Effect " + data.get("effect") + " not found!");
    }

    @Override
    public ItemStack getDisplayItem() {
        return displayItem;
    }

    public void display(IPlayer player) {
        victoryEffectType.getVictoryEffect().display(player);
    }
}
