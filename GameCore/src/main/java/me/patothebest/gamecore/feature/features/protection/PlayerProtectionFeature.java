package me.patothebest.gamecore.feature.features.protection;

import me.patothebest.gamecore.feature.AbstractFeature;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class PlayerProtectionFeature extends AbstractFeature {

    @EventHandler
    public void onPlayerFoodChange(FoodLevelChangeEvent event) {
        if(!isEventInArena(event)) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if(!isEventInArena(event)) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if(!isEventInArena(event)) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        if(!isPlayingInArena(event)) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if(!isEventInArena(event)) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if(!isEventInArena(event)) {
            return;
        }

        event.setCancelled(true);
    }
}
