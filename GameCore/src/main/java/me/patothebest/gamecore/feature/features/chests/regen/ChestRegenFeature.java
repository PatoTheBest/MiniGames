package me.patothebest.gamecore.feature.features.chests.regen;

import com.google.inject.Inject;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.file.CoreConfig;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.feature.AbstractRunnableFeature;
import me.patothebest.gamecore.player.PlayerManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

public class ChestRegenFeature extends AbstractRunnableFeature {

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    private final CorePlugin corePlugin;
    private final CoreConfig coreConfig;
    private final PlayerManager playerManager;
    private final List<RegenChest> chests = new ArrayList<>();
    private final List<Location> placedChests = new ArrayList<>();

    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //

    @Inject private ChestRegenFeature(CorePlugin corePlugin, CoreConfig coreConfig, PlayerManager playerManager) {
        this.corePlugin = corePlugin;
        this.coreConfig = coreConfig;
        this.playerManager = playerManager;
    }

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    public void run() {
        // regen each chest
        chests.forEach(RegenChest::regen);
    }

    // -------------------------------------------- //
    // LISTENERS
    // -------------------------------------------- //

    @EventHandler
    public void onChestOpen(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (event.getClickedBlock().getType() != Material.CHEST && event.getClickedBlock().getType() != Material.TRAPPED_CHEST) {
            return;
        }

        if(playerManager.getPlayer(event.getPlayer()).getCurrentArena() != arena) {
            return;
        }

        Chest chest = (Chest) event.getClickedBlock().getState();
        if (placedChests.contains(event.getClickedBlock().getLocation())) {
            return;
        }

        if (chests.stream().filter(regenChest -> regenChest.getChest().equals(chest)).findFirst().orElse(null) != null) {
            return;
        }

        if(event.getClickedBlock().getType() == Material.CHEST) {
            chests.add(new RegenChest(chest));
        } else {
            chests.add(new NoRegenChest(chest));
        }


        for(BlockFace blockFace : BlockFace.values()) {
            if(blockFace != BlockFace.NORTH && blockFace != BlockFace.SOUTH && blockFace != BlockFace.EAST && blockFace != BlockFace.WEST) {
                continue;
            }

            Block block = event.getClickedBlock().getRelative(blockFace);
            if(block.getType() != Material.CHEST && block.getType() != Material.TRAPPED_CHEST) {
                continue;
            }

            if(block.getType() != event.getClickedBlock().getType()) {
                continue;
            }

            Chest adjacentChest = (Chest) block.getState();
            chests.add(new NoRegenChest(adjacentChest));
        }
    }


    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if(playerManager.getPlayer(event.getPlayer()).getCurrentArena() != arena) {
            return;
        }

        if(arena.getArea().contains(event.getBlockPlaced().getLocation())) {
            if(event.getBlockPlaced().getType() == Material.CHEST || event.getBlockPlaced().getType() == Material.TRAPPED_CHEST) {
                for(BlockFace blockFace : BlockFace.values()) {
                    if(blockFace != BlockFace.NORTH && blockFace != BlockFace.SOUTH && blockFace != BlockFace.EAST && blockFace != BlockFace.WEST) {
                        continue;
                    }

                    Block block = event.getBlockPlaced().getRelative(blockFace);
                    if(block.getType() != Material.CHEST && block.getType() != Material.TRAPPED_CHEST) {
                        continue;
                    }

                    if(block.getType() != event.getBlockPlaced().getType()) {
                        continue;
                    }

                    Chest adjacentChest = (Chest) block.getState();
                    if(!placedChests.contains(adjacentChest.getLocation())) {
                        event.getPlayer().sendMessage(CoreLang.CANNOT_PLACE_CHEST_ADJACENT.getMessage(event.getPlayer()));
                        event.setCancelled(true);
                        return;
                    }
                }

                placedChests.add(event.getBlockPlaced().getLocation());
            }

            return;
        }

        event.setCancelled(true);
    }


    @EventHandler
    public void onChestBreak(BlockBreakEvent event) {
        if(playerManager.getPlayer(event.getPlayer()).getCurrentArena() != arena) {
            return;
        }

        if(event.getBlock().getType() != Material.CHEST && event.getBlock().getType() != Material.TRAPPED_CHEST) {
            return;
        }

        if(placedChests.contains(event.getBlock().getLocation())) {
            placedChests.remove(event.getBlock().getLocation());
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        checkExplosion(event::blockList);
    }

    @EventHandler
    public void onExplode(BlockExplodeEvent event) {
        checkExplosion(event::blockList);
    }

    private void checkExplosion(Supplier<List<Block>> blockListSupplier) {
        if(blockListSupplier.get().isEmpty()) {
            return;
        }

        if(!blockListSupplier.get().get(0).getWorld().getName().equalsIgnoreCase(arena.getWorld().getName())) {
            return;
        }

        Iterator<Block> iterator = blockListSupplier.get().iterator();
        while(iterator.hasNext()) {
            Block block = iterator.next();

            if(block.getType() != Material.CHEST && block.getType() != Material.TRAPPED_CHEST) {
                continue;
            }

            if(placedChests.contains(block.getLocation())) {
                continue;
            }

            iterator.remove();
        }
    }

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    public void initializeFeature() {
        // If chest regeneration is on...
        if (coreConfig.getBoolean("chest-regeneration.enabled")) {
            // ...start the task

            // get the interval rate and schedule a task with it
            int interval = coreConfig.getInt("chest-regeneration.interval");
            runTaskTimer(corePlugin, interval * 20, interval * 20 * 60);
        }
    }

    public List<RegenChest> getChests() {
        return chests;
    }

    public List<Location> getPlacedChests() {
        return placedChests;
    }
}
