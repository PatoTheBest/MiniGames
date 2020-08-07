package me.patothebest.gamecore.feature;

import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.event.arena.ArenaEvent;
import me.patothebest.gamecore.event.player.ArenaPlayerEvent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.hanging.HangingEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleEvent;
import org.bukkit.event.weather.WeatherEvent;
import org.bukkit.event.world.WorldEvent;

/**
 * The Interface Feature.
 */
public interface Feature extends Listener {

    /**
     * Initialize feature.
     */
    void initializeFeature();

    /**
     * Stop feature.
     */
    void stopFeature();

    /**
     * Sets the getArena().
     *
     * @param arena the new arena
     */
    void setArena(AbstractArena arena);

    /**
     * Gets the arena
     * 
     * @return the arena
     */
    AbstractArena getArena();

    /**
     * Checks if the feature is already registered
     *
     * @return true, if it has already been registered
     */
    boolean hasBeenRegistered();

    /**
     * Destroys the feature
     */
    default void destroy() {setArena(null);}

    default boolean isEventInArena(BlockEvent event) {
        return isLocationInArena(event.getBlock());
    }

    default boolean isEventInArena(EntityTargetEvent event) {
        return event.getTarget() != null && isLocationInArena(event.getTarget().getLocation());
    }

    default boolean isEventInArena(EntityDamageByEntityEvent event) {
        return isEntityInArena(event.getDamager());
    }

    default boolean isEventInArena(PlayerInteractEvent event) {
        return isPlayingInArena(event.getPlayer());
    }

    default boolean isEventInArena(EntityEvent event) {
        return isEntityInArena(event.getEntity());
    }

    default boolean isEventInArena(HangingEvent event) {
        return isEntityInArena(event.getEntity());
    }

    default boolean isEventInArena(VehicleEvent event) {
        return isEntityInArena(event.getVehicle());
    }

    default boolean isEventInArena(WeatherEvent event) {
        return getArena().getWorld() == event.getWorld();
    }


    default boolean isEventInArena(WorldEvent event) {
        return getArena().getWorld() == event.getWorld();
    }

    default boolean isPlayingInArena(BlockBreakEvent event) {
        return isPlayingInArena(event.getPlayer());
    }

    default boolean isPlayingInArena(BlockPlaceEvent event) {
        return isPlayingInArena(event.getPlayer());
    }

    default boolean isPlayingInArena(InventoryInteractEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return false;
        }

        return isPlayingInArena((Player) event.getWhoClicked());
    }

    default boolean isPlayingInArena(PlayerEvent event) {
        return isPlayingInArena(event.getPlayer());
    }

    default boolean isPlayingInArena(ArenaPlayerEvent event) {
        return isPlayingInArena(event.getPlayer());
    }

    default boolean isPlayingInArena(ArenaEvent event) {
        return event.getArena() == getArena();
    }

    default boolean isPlayingInArena(EntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return isEntityInArena(event.getEntity());
        }

        return isPlayingInArena((Player) event.getEntity());
    }

    default boolean isEntityInArena(Entity entity) {
        if (entity instanceof Player) {
            return isPlayingInArena((Player) entity);
        }

        return isLocationInArena(entity.getLocation());
    }

    default boolean isLocationInArena(Block world) {
        return isLocationInArena(world.getLocation());
    }

    default boolean isLocationInArena(Location location) {
        return isEventInArena(location.getWorld());
//        if (!isEventInArena(location.getWorld())) {
//            return false;
//        }
//
//        if (getArena().getArea().contains(location)) {
//            return true;
//        }
//
//        return getArena().getLobbyArea() != null && getArena().getLobbyArea().contains(location);
    }

    default boolean isEventInArena(World world) {
        return world == getArena().getWorld();
    }

    default boolean isPlayingInArena(Player player) {
        return getArena().getPlayers().contains(player);
    }

    default boolean isSpectatingInArena(Player player) {
        return getArena().getSpectators().contains(player);
    }
}