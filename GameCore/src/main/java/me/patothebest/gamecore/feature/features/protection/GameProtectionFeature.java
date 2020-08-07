package me.patothebest.gamecore.feature.features.protection;

import com.google.inject.Inject;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.feature.AbstractFeature;
import me.patothebest.gamecore.player.PlayerManager;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

import java.util.Iterator;
import java.util.List;

public class GameProtectionFeature extends AbstractFeature {

    private final PlayerManager playerManager;

    @Inject protected GameProtectionFeature(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if(!isPlayingInArena(event)) {
            return;
        }

        if(isBlockProtected(playerManager.getPlayer(event.getPlayer().getName()).getCurrentArena(), event.getBlock(), event)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if(!isPlayingInArena(event)) {
            return;
        }

        if(isBlockProtected(playerManager.getPlayer(event.getPlayer().getName()).getCurrentArena(), event.getBlock(), event)) {
            event.setCancelled(true);
            return;
        }


        if(playerManager.getPlayer(event.getPlayer().getName()).getCurrentArena().getArea().contains(event.getBlock().getLocation())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onBucketPlace(PlayerBucketEmptyEvent event) {
        handleBucketEvent(event);
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        handleBucketEvent(event);
    }

    private void handleBucketEvent(PlayerBucketEvent event) {
        if(!isPlayingInArena(event)) {
            return;
        }

        if(isBlockProtected(playerManager.getPlayer(event.getPlayer().getName()).getCurrentArena(), event.getBlockClicked(), event)) {
            event.setCancelled(true);
            return;
        }


        if(playerManager.getPlayer(event.getPlayer().getName()).getCurrentArena().getArea().contains(event.getBlockClicked().getRelative(event.getBlockFace()).getLocation())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onLiquidSpread(BlockFromToEvent event) {
        if(!isEventInArena(event)) {
            return;
        }

        if(isBlockProtected(arena, event.getToBlock(), event)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityBlockForm(EntityBlockFormEvent event) {
        if(!isEventInArena(event)) {
            return;
        }

        if(isBlockProtected(arena, event.getBlock(), event)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockForm(EntityChangeBlockEvent event) {
        if(!isEventInArena(event)) {
            return;
        }

        if(isBlockProtected(arena, event.getBlock(), event)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        if(!isEventInArena(event)) {
            return;
        }

        event.getBlocks().forEach(block -> {
            if(!arena.getArea().contains(block.getRelative(event.getDirection()).getLocation())) {
                event.setCancelled(true);
                return;
            }

            if(isBlockProtected(arena, block.getRelative(event.getDirection()), event)) {
                event.setCancelled(true);
            }
        });
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {
        if(!isEventInArena(event)) {
            return;
        }

        event.getBlocks().forEach(block -> {
            if(!arena.getArea().contains(block.getLocation())) {
                event.setCancelled(true);
                return;
            }

            if(isBlockProtected(arena, block, event)) {
                event.setCancelled(true);
            }
        });
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        checkExplosion(event.blockList(), event);
    }

    @EventHandler
    public void onExplode(BlockExplodeEvent event) {
        checkExplosion(event.blockList(), event);
    }

    private void checkExplosion(List<Block> blockListSupplier, Event event) {
        if(blockListSupplier.isEmpty()) {
            return;
        }

        if(!isLocationInArena(blockListSupplier.get(0))) {
            return;
        }

        Iterator<Block> iterator = blockListSupplier.iterator();
        while(iterator.hasNext()) {
            Block block = iterator.next();
            if(!arena.getArea().contains(block.getLocation())) {
                iterator.remove();
                continue;
            }

            if(isBlockProtected(arena, block, event)) {
                iterator.remove();
                continue;
            }

            if(block.getType() != Material.CHEST.parseMaterial() && block.getType() != Material.TRAPPED_CHEST.parseMaterial()) {
                continue;
            }

            iterator.remove();
        }
    }

    protected boolean isBlockProtected(AbstractArena arena, Block block, Event event) {
        return !arena.getArea().contains(block);
    }
}
