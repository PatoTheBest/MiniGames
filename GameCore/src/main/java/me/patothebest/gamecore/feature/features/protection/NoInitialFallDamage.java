package me.patothebest.gamecore.feature.features.protection;

import me.patothebest.gamecore.feature.AbstractFeature;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;

public class NoInitialFallDamage extends AbstractFeature {

    private final ArrayList<Entity> noFallEntities = new ArrayList<>();

    public NoInitialFallDamage() { }

    @Override
    public void initializeFeature() {
        super.initializeFeature();
        noFallEntities.clear();
    }

    @EventHandler
    public void onFall(EntityDamageEvent event) {
        if(event.getCause() != EntityDamageEvent.DamageCause.FALL) {
            return;
        }

        if(!isPlayingInArena(event)) {
            return;
        }

        if(noFallEntities.contains(event.getEntity())) {
            return;
        }

        noFallEntities.add(event.getEntity());
        event.setCancelled(true);
    }
}
