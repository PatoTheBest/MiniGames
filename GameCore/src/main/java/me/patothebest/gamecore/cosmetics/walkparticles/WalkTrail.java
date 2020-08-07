package me.patothebest.gamecore.cosmetics.walkparticles;

import fr.mrmicky.fastparticle.ParticleType;
import me.patothebest.gamecore.cosmetics.shop.AbstractShopItem;
import me.patothebest.gamecore.file.ParserValidations;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class WalkTrail extends AbstractShopItem {

    private final ParticleType particleType;
    private final ItemStack displayItem;
    private final int interval;
    private final int amount;

    public WalkTrail(String configName, Map<String, Object> data) {
        super(configName, data);
        this.particleType = Utils.getEnumValueFromString(ParticleType.class, (String) data.get("effect"));
        this.displayItem = Utils.itemStackFromString((String) data.get("display-item"));
        this.interval = (int) data.getOrDefault("interval", 2);
        this.amount = (int) data.getOrDefault("amount", 1);

        ParserValidations.isTrue(particleType.isSupported(), "The particle effect " + particleType.getName() + " is not supported on this version.");
    }

    @Override
    public ItemStack getDisplayItem() {
        return displayItem;
    }

    public ParticleType getParticleType() {
        return particleType;
    }

    public int getInterval() {
        return interval;
    }

    public int getAmount() {
        return amount;
    }
}
