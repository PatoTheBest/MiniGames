package me.patothebest.gamecore.feature.features.kits;

import com.google.inject.Inject;
import me.patothebest.gamecore.scheduler.PluginScheduler;
import me.patothebest.gamecore.feature.AbstractFeature;
import me.patothebest.gamecore.kit.KitManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerRespawnEvent;

public class RespawnWithKitFeature extends AbstractFeature {

    private final KitManager kitManager;
    private final PluginScheduler pluginScheduler;

    @Inject private RespawnWithKitFeature(KitManager kitManager, PluginScheduler pluginScheduler) {
        this.kitManager = kitManager;
        this.pluginScheduler = pluginScheduler;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent event) {
        if(!isPlayingInArena(event)) {
            return;
        }

        kitManager.applyKit(event.getPlayer(), arena.getTeam(event.getPlayer()));

        pluginScheduler.runTaskLater(() -> {
            kitManager.applyPotionEffects(event.getPlayer());
        }, 1L);
    }
}