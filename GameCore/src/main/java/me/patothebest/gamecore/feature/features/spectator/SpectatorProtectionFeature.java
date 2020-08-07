package me.patothebest.gamecore.feature.features.spectator;

import com.google.inject.Inject;
import me.patothebest.gamecore.feature.AbstractFeature;
import me.patothebest.gamecore.player.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;

public class SpectatorProtectionFeature extends AbstractFeature {

    private final PlayerManager playerManager;

    @Inject private SpectatorProtectionFeature(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if(!isSpectating(event.getPlayer())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if(!isSpectating(event.getPlayer())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        if(!isSpectating(event.getPlayer())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        if(!isSpectating(event.getPlayer())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onFill(PlayerBucketFillEvent event) {
        if(!isSpectating(event.getPlayer())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onEmpty(PlayerBucketEmptyEvent event) {
        if(!isSpectating(event.getPlayer())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if(!isSpectating((Player) event.getEntity())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onProjectileshoot(ProjectileLaunchEvent event) {
        if(!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity().getShooter();

        if(!isSpectating(player)) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onDamageSpectator(EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Player)) {
            return;
        }

        if(!isSpectating((Player) event.getEntity())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onDamagePlayer(EntityDamageByEntityEvent event) {
        if(!(event.getDamager() instanceof Player)) {
            return;
        }

        if(!isSpectating((Player) event.getDamager())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onDamagePlayer(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Player)) {
            return;
        }

        if(!isSpectating((Player) event.getEntity())) {
            return;
        }

        event.setCancelled(true);

        if(event.getCause() == EntityDamageEvent.DamageCause.FIRE || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK || event.getCause() == EntityDamageEvent.DamageCause.LAVA) {
            event.getEntity().setFireTicks(0);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if(!isSpectating(event.getPlayer())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onInteract(VehicleEnterEvent event) {
        if (!(event.getEntered() instanceof Player)) {
            return;
        }

        if(!isSpectating((Player) event.getEntered())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onTarget(EntityTargetEvent event) {
        if (!(event.getTarget() instanceof Player)) {
            return;
        }

        if (isSpectating((Player) event.getTarget())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void checkBorder(PlayerMoveEvent event) {
        if(event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockY() == event.getTo().getBlockY() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        if(!isSpectating(event.getPlayer())) {
            return;
        }

        if(event.getTo().getBlockY() <= 0) {
            event.getPlayer().teleport(arena.getSpectatorLocation());
            return;
        }

        if (arena.getArea() == null) {
            return;
        }

        if(!arena.getArea().contains(event.getTo())) {
            event.setTo(event.getFrom());
        }
    }

    private boolean isSpectating(Player player) {
        return playerManager.getPlayer(player) != null && playerManager.getPlayer(player).getCurrentArena() == arena && playerManager.getPlayer(player).getCurrentArena().getSpectators().contains(player);
    }
}