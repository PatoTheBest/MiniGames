package me.patothebest.gamecore.cosmetics.cage;

import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.cosmetics.shop.AbstractShopItem;
import me.patothebest.gamecore.file.ParserException;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.StainedGlass;
import me.patothebest.gamecore.util.FinalSupplier;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.function.Supplier;

public class Cage extends AbstractShopItem {

    private final Supplier<ItemStack> outerMaterialSupplier;
    private final Supplier<ItemStack> innerMaterialSupplier;

    public Cage(Material innerMaterial, Material outerMaterial) {
        super();
        outerMaterialSupplier = new FinalSupplier<>(new ItemStackBuilder(outerMaterial));
        innerMaterialSupplier = new FinalSupplier<>(new ItemStackBuilder(innerMaterial));
    }

    public Cage(String configName, Map<String, Object> data) {
        super(configName, data);
        if(((String)data.get("outer-material")).equalsIgnoreCase("rainbow")) {
            outerMaterialSupplier = StainedGlass::getRandom;
        } else {
            ItemStack itemStack = Utils.itemStackFromString((String) data.get("outer-material"));

            if(itemStack == null) {
                throw new ParserException("Outer material of cage " + configName + " could not be parsed!");
            }

            outerMaterialSupplier = () -> itemStack;
        }

        if(((String)data.get("inner-material")).equalsIgnoreCase("rainbow")) {
            innerMaterialSupplier = StainedGlass::getRandom;
        } else {
            ItemStack itemStack = Utils.itemStackFromString((String) data.get("inner-material"));

            if(itemStack == null) {
                throw new ParserException("Inner material of cage " + configName + " could not be parsed!");
            }

            innerMaterialSupplier = () -> itemStack;
        }
    }

    public ItemStack getOuterMaterial() {
        return outerMaterialSupplier.get();
    }

    public ItemStack getInnerMaterial() {
        return innerMaterialSupplier.get();
    }

    @Override
    public ItemStack getDisplayItem() {
        return getOuterMaterial();
    }
}
