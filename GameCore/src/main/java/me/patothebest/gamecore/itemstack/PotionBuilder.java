package me.patothebest.gamecore.itemstack;

import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

/**
 * The PotionBuilder.
 */
@SuppressWarnings("deprecation")
public class PotionBuilder extends Potion {

    /**
     * Instantiates a new potion builder.
     */
    public PotionBuilder() {
        super(PotionType.SPEED);
    }

    /**
     * Sets the effect type to the potion.
     *
     * @param type the potion type
     * @return the potion builder
     */
    public PotionBuilder effect(PotionType type) {
        setType(type);
        return this;
    }

    /**
     * Makes the potion splash.
     *
     * @param splash if the potion should be a splash potion
     * @return the potion builder
     */
    public PotionBuilder splash(boolean splash) {
        setSplash(splash);
        return this;
    }

    /**
     * Sets the potion's level.
     *
     * @param level the level
     * @return the potion builder
     */
    public PotionBuilder level(int level) {
        setLevel(level);
        return this;
    }

    /**
     * Sets if the potion should have an extended duration.
     *
     * @param isExtended if the potion should have an extended duration
     * @return the potion builder
     */
    public PotionBuilder extendedDuration(boolean isExtended) {
        setHasExtendedDuration(isExtended);
        return this;
    }

}
