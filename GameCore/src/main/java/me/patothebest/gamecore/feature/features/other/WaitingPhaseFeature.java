package me.patothebest.gamecore.feature.features.other;

import com.google.inject.Inject;
import me.patothebest.gamecore.cosmetics.cage.Cage;
import me.patothebest.gamecore.event.player.ArenaLeaveEvent;
import me.patothebest.gamecore.event.player.LobbyJoinEvent;
import me.patothebest.gamecore.event.player.PlayerSelectItemEvent;
import me.patothebest.gamecore.feature.AbstractFeature;
import me.patothebest.gamecore.phase.phases.CagePhase;
import me.patothebest.gamecore.scheduler.PluginScheduler;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;

public class WaitingPhaseFeature extends AbstractFeature {

    private final Map<Player, Location> playerLocationMap = new HashMap<>();
    private final PluginScheduler pluginScheduler;

    @Inject private WaitingPhaseFeature(PluginScheduler pluginScheduler) {
        this.pluginScheduler = pluginScheduler;
    }

    @EventHandler
    public void onItemUpdate(PlayerSelectItemEvent event) {
        if(!isPlayingInArena(event.getPlayer().getPlayer())) {
            return;
        }

        if (!(arena.getPhase() instanceof CagePhase)) {
            return;
        }

        if(!(event.getShopItem() instanceof Cage)) {
            return;
        }

        ((CagePhase) arena.getPhase()).updateCage(event.getPlayer().getPlayer());
    }

    @EventHandler
    public void onPlayerLeave(ArenaLeaveEvent event) {
        playerLocationMap.remove(event.getPlayer().getPlayer());
    }

    @EventHandler
    public void onPlayerJoin(LobbyJoinEvent event) {
        if(!isPlayingInArena(event.getPlayer())) {
            return;
        }

        playerLocationMap.put(event.getPlayer(), event.getPlayer().getLocation());
    }

    @Override
    public void initializeFeature() {
        super.initializeFeature();

        pluginScheduler.scheduleSyncDelayedTask(() -> {
            for (Player player : arena.getPlayers()) {
                playerLocationMap.put(player, player.getLocation());
            }
        }, 1L);
    }

    @Override
    public void stopFeature() {
        super.stopFeature();
        playerLocationMap.clear();
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (arena.getPhase() instanceof CagePhase) {
            if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockY() <= event.getTo().getBlockY() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
                return;
            }

            if (!isPlayingInArena(event.getPlayer())) {
                return;
            }

            Location location = playerLocationMap.get(event.getPlayer());

            if (location == null) {
                return;
            }

            if (event.getTo().getBlockY() == location.getBlockY()) {
                return;
            }

            event.setTo(location);
        } else {
            if (event.getFrom().getX() == event.getTo().getX() && event.getFrom().getZ() == event.getTo().getZ()) {
                return;
            }

            if (!isPlayingInArena(event.getPlayer())) {
                return;
            }

            event.setTo(event.getFrom());
        }
    }
}
