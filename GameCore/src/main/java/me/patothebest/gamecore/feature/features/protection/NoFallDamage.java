package me.patothebest.gamecore.feature.features.protection;

import me.patothebest.gamecore.feature.AbstractFeature;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

public class NoFallDamage extends AbstractFeature {

    public NoFallDamage() { }

    @Override
    public void initializeFeature() {
        super.initializeFeature();
    }

    @EventHandler
    public void onFall(EntityDamageEvent event) {
        if(event.getCause() != EntityDamageEvent.DamageCause.FALL) {
            return;
        }

        if(!isPlayingInArena(event)) {
            return;
        }

        event.setCancelled(true);
    }
}
