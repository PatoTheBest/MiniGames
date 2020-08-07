package me.patothebest.hungergames.feature;

import com.google.inject.Inject;
import fr.mrmicky.fastparticle.FastParticle;
import fr.mrmicky.fastparticle.ParticleType;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.feature.AbstractRunnableFeature;
import me.patothebest.gamecore.feature.features.chests.refill.ChestFile;
import me.patothebest.gamecore.feature.features.chests.refill.ChestType;
import me.patothebest.gamecore.file.CoreConfig;
import me.patothebest.gamecore.modules.Module;
import me.patothebest.gamecore.util.Sounds;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.vector.ArenaLocation;
import me.patothebest.hungergames.arena.Arena;
import me.patothebest.hungergames.arena.ChestLocations;
import me.patothebest.hungergames.lang.Lang;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.util.Vector;

import java.util.List;

public class SupplyDropFeature extends AbstractRunnableFeature implements Module {

    private final static double STEP = Math.PI / 20;
    private final static double TWO_PI = Math.PI * 2;

    private final ChestFile supplyChestFile;
    private final CorePlugin plugin;
    private final int minAmount;
    private final int maxAmount;
    private boolean hasDropped = false;

    private FallingBlock fallingBlock;
    private double angle = 0;
    private Vector velocity = new Vector(0, -0.2, 0);
    private int ticks = 0;
    private boolean landed = false;
    private Location chestLocation;

    @Inject private SupplyDropFeature(CorePlugin plugin, CoreConfig config) {
        supplyChestFile = new ChestFile(plugin, ChestLocations.SUPPLY, ChestType.NORMAL);
        this.plugin = plugin;
        minAmount = config.getInt("supply-drops.min-items-amount", -1);
        maxAmount = config.getInt("supply-drops.max-items-amount", -1);
    }

    @Override
    public void run() {
        if (!landed && fallingBlock.isDead()) {
            cancel();
            return;
        }

        Location location = landed ? chestLocation : fallingBlock.getLocation();
        double offset = landed ? 0.5 : 0;
        if (!landed) {
            fallingBlock.setVelocity(velocity);
        }

        angle %= TWO_PI;
        double x = Math.cos(angle) * 1.25;
        double y = location.getY() + 0.75 + (Math.cos(angle) * 0.5);
        double z = Math.sin(angle) * 1.25;

        FastParticle.spawnParticle(location.getWorld(), ParticleType.FLAME, x + location.getX() + offset, y, z + location.getZ() + offset, 1, 0, 0, 0, 0);
        FastParticle.spawnParticle(location.getWorld(), ParticleType.FLAME, -x + location.getX() + offset, y, -z + location.getZ() + offset, 1, 0, 0, 0, 0);

        if (!landed && ticks % 2 == 0) {
            if (ParticleType.CAMPFIRE_SIGNAL_SMOKE.isSupported()) {
                FastParticle.spawnParticle(location.getWorld(), ParticleType.CAMPFIRE_SIGNAL_SMOKE, location.getX() + 0.5, y, location.getZ() + 0.5, 1, 0, 0, 0, 1);
            }
        }

        if (!landed && ticks++ % 5 == 0) {
            Sounds.ENTITY_FIREWORK_ROCKET_LAUNCH.play(location);
        }

        angle += STEP;
    }

    @EventHandler
    public void onChestOpen(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        if (event.getClickedBlock().getType() != me.patothebest.gamecore.itemstack.Material.CHEST.parseMaterial()) {
            return;
        }

        if (!isEventInArena(event)) {
            return;
        }

        if (event.getClickedBlock().getLocation().equals(chestLocation)) {
            cancel();
        }
    }

    @EventHandler
    public void onSupplyDropLand(EntityChangeBlockEvent event) {
        if (!isEventInArena(event)) {
            return;
        }

        if (!(event.getEntity() instanceof FallingBlock)) {
            return;
        }

        FallingBlock fallingBlock = (FallingBlock) event.getEntity();
        if (this.fallingBlock != fallingBlock) {
            return;
        }

        event.setCancelled(true);
        event.getBlock().setType(Material.CHEST);
        Location location = fallingBlock.getLocation();
        Inventory blockInventory = ((Chest) event.getBlock().getState()).getBlockInventory();
        supplyChestFile.fill(blockInventory, minAmount, maxAmount);
        Sounds.BLOCK_ANVIL_LAND.play(location);
        FastParticle.spawnParticle(location.getWorld(), ParticleType.SMOKE_LARGE, location, 3, 0, 0, 0, 0);
        fallingBlock.remove();
        chestLocation = event.getBlock().getLocation();
        landed = true;
    }

    @Override
    public void initializeFeature() {
        if (hasDropped) {
            return;
        }

        hasDropped = true;
        List<ArenaLocation> supplyDrops = ((Arena) arena).getSupplyDrops();
        ArenaLocation location = Utils.getRandomElementFromList(supplyDrops);

        if (location == null) {
            return;
        }

        arena.sendMessageToArena(player -> Lang.SUPPLY_DROP_INCOMING.replace(player, location.getX() + ", " + location.getY() + ", " + location.getZ()));
        arena.playSound(Sounds.BLOCK_NOTE_BLOCK_PLING);
        Location add = location.clone().add(0.5, 100, 0.5);
        fallingBlock = arena.getWorld().spawnFallingBlock(add, Material.NOTE_BLOCK, (byte) 0);
        fallingBlock.setDropItem(false);
        runTaskTimer(plugin, 0L, 1L);
    }

    @Override
    public void stopFeature() {
        super.stopFeature();
        fallingBlock = null;
        hasDropped = false;
        landed = false;
        chestLocation = null;
    }
}
