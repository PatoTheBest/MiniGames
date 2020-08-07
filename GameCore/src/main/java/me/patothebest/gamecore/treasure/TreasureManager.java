package me.patothebest.gamecore.treasure;

import com.google.inject.Inject;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.modules.ModuleName;
import me.patothebest.gamecore.treasure.chest.TreasureChest;
import me.patothebest.gamecore.treasure.chest.TreasureChestLocation;
import me.patothebest.gamecore.treasure.type.TreasureType;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.logger.InjectLogger;
import me.patothebest.gamecore.modules.ActivableModule;
import me.patothebest.gamecore.modules.ListenerModule;
import me.patothebest.gamecore.modules.ReloadableModule;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.util.CacheCollection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ModuleName("Treasure Chest Manager")
public class TreasureManager implements ActivableModule, ListenerModule, ReloadableModule {

    private final List<TreasureChestLocation> treasureLocations = new ArrayList<>();
    private final CacheCollection<Player> playerCache = new CacheCollection<>(cacheBuilder -> cacheBuilder.expireAfterWrite(1, TimeUnit.MINUTES));
    private final TreasureFile treasureFile;
    private final TreasureConfigFile treasureConfigFile;
    private final TreasureFactory treasureFactory;
    private final PlayerManager playerManager;
    @InjectLogger private Logger logger;

    @Inject private TreasureManager(TreasureFile treasureFile, TreasureConfigFile treasureConfigFile, TreasureFactory treasureFactory, PlayerManager playerManager) {
        this.treasureFile = treasureFile;
        this.treasureConfigFile = treasureConfigFile;
        this.treasureFactory = treasureFactory;
        this.playerManager = playerManager;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onEnable() {
        treasureLocations.clear();

        if (treasureFile.get("chests") == null) {
            return;
        }

        treasureLocations.addAll(Utils.deserializeList((List<Map<String, Object>>) treasureFile.get("chests"), treasureFactory::createLocation));
    }

    @Override
    public void onDisable() {
        treasureLocations.forEach(treasureLocation -> {
            if(treasureLocation.getHologram() != null && treasureLocation.getHologram().isAlive()) {
                treasureLocation.getHologram().delete();
            }

            TreasureChest currentTreasureChest = treasureLocation.getCurrentTreasureChest();

            if (currentTreasureChest != null) {
                currentTreasureChest.forceDestroy();
            }
        });

        treasureFile.set("chests", Utils.serializeList(treasureLocations));
        treasureFile.save();
    }

    @Override
    public void onReload() {
        onDisable();
        treasureFile.load();
        treasureConfigFile.load();
        treasureConfigFile.onPreEnable();
        onEnable();
    }

    @Override
    public String getReloadName() {
        return "treasure";
    }

    @EventHandler
    public void openChest(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        if (event.getClickedBlock().getType() != Material.CHEST.parseMaterial()) {
            return;
        }

        for (TreasureChestLocation treasureLocation : treasureLocations) {
            if(treasureLocation.getLocation().equals(event.getClickedBlock().getLocation())) {
                event.setCancelled(true);

                if(TreasureType.VOTE.isEnabled()) {
                    treasureFactory.createMenuWithVote(playerManager.getPlayer(event.getPlayer()), treasureLocation);
                } else {
                    treasureFactory.createMenuWithoutVote(playerManager.getPlayer(event.getPlayer()), treasureLocation);
                }
            }
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        if (event.getClickedBlock() == null) {
            return;
        }

        if (event.getClickedBlock().getType() != Material.CHEST.parseMaterial()) {
            return;
        }

        if (!playerCache.contains(event.getPlayer())) {
            return;
        }

        TreasureChestLocation location = treasureFactory.createLocation(event.getClickedBlock().getLocation());
        treasureLocations.add(location);
        playerCache.remove(event.getPlayer());
        CoreLang.TREASURE_ADDED.sendMessage(event.getPlayer());
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @EventHandler
    public void onChestOpen(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        if (event.getClickedBlock().getType() != Material.CHEST.parseMaterial()) {
            return;
        }

        TreasureChest treasureChest = getChest(event.getPlayer());

        if (treasureChest == null) {
            return;
        }

        if (!treasureChest.getChests().contains(event.getClickedBlock().getState())) {
            return;
        }

        event.setCancelled(true);
        treasureChest.openChest(event.getClickedBlock());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockY() == event.getTo().getBlockY() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        for (TreasureChestLocation treasureLocation : treasureLocations) {
            if(treasureLocation.canOpen()) {
                continue;
            }

            if (Utils.offset(treasureLocation.getLocation(), event.getTo()) > 2 && event.getPlayer().getName().equalsIgnoreCase(treasureLocation.getCurrentTreasureChest().getPlayer().getName())) {
                //Utils.bounceTo(event.getPlayer(), treasureLocation.getLocation());
                event.getPlayer().teleport(event.getFrom());
            }
        }
    }

    @EventHandler
    public void onMove2(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockY() == event.getTo().getBlockY() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        for (TreasureChestLocation treasureLocation : treasureLocations) {
            if(treasureLocation.canOpen()) {
                continue;
            }

            if (Utils.offset(treasureLocation.getLocation(), event.getTo()) < 4 && !event.getPlayer().getName().equalsIgnoreCase(treasureLocation.getCurrentTreasureChest().getPlayer().getName())) {
                event.getPlayer().setVelocity(new Vector(treasureLocation.getLocation().getX() - event.getFrom().getBlockX(), -4, treasureLocation.getLocation().getZ() - event.getFrom().getBlockZ()).normalize().multiply(-1));
            }
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        for (TreasureChest treasureChest : getTreasureChests()) {
            for (Entity item : treasureChest.getItems()) {
                if (item.getUniqueId() == event.getItem().getUniqueId()) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        TreasureChest treasureChest = getChest(event.getPlayer());

        if (treasureChest == null) {
            return;
        }

        treasureChest.forceDestroy();
    }

    private List<TreasureChest> getTreasureChests() {
        return treasureLocations.stream().filter(TreasureChestLocation::isNotAvailable).map(TreasureChestLocation::getCurrentTreasureChest).collect(Collectors.toList());
    }

    private TreasureChest getChest(Player player) {
        return Utils.getFromCollection(getTreasureChests(), treasureChest -> treasureChest != null && treasureChest.getPlayer().getPlayer() == player);
    }

    /**
     * Gets the player cache used to know which player should
     * we listen to, when they right click a chest
     * <p>
     * This collection is a cache which automatically removes
     * any element added withing 1 minute of write
     *
     * @return the player cache
     */
    public Collection<Player> getPlayerCache() {
        return playerCache;
    }
}
