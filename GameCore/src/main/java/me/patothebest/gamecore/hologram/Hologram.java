package me.patothebest.gamecore.hologram;

import org.apache.commons.lang.Validate;
import org.bukkit.entity.ArmorStand;

import java.lang.ref.WeakReference;

public class Hologram {

    private final WeakReference<ArmorStand> armorStand;

    public Hologram(ArmorStand armorStandParam) {
        armorStand = new WeakReference<>(armorStandParam);
        armorStand.get().setVisible(false);
        armorStand.get().setGravity(false);
        armorStand.get().setMarker(true);
        armorStand.get().setCustomNameVisible(true);
        armorStand.get().setRemoveWhenFarAway(false);
    }

    public void setName(String name) {
        validate();
        armorStand.get().setCustomName(name);
    }

    public void hide() {
        validate();
        armorStand.get().setCustomNameVisible(false);
    }

    public void show() {
        validate();
        armorStand.get().setCustomNameVisible(true);
    }

    public void delete() {
        validate();
        armorStand.get().remove();
    }

    public boolean isAlive() {
        return armorStand.get() != null && !armorStand.get().isDead();
    }

    private void validate() {
        Validate.notNull(armorStand.get(), "Armor stand is null");
        Validate.isTrue(!armorStand.get().isDead(), "Armor stand is dead");
    }

    public WeakReference<ArmorStand> getArmorStand() {
        return armorStand;
    }
}
