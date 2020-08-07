package me.patothebest.gamecore.feature.features.spectator;

import com.google.inject.Inject;
import me.patothebest.gamecore.scheduler.PluginScheduler;
import me.patothebest.gamecore.combat.CombatDeathEvent;
import me.patothebest.gamecore.feature.AbstractFeature;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.util.Vector;

public class DeathSpectatorFeature extends AbstractFeature {

    private final PluginScheduler pluginScheduler;

    @Inject private DeathSpectatorFeature(PluginScheduler pluginScheduler) {
        this.pluginScheduler = pluginScheduler;
    }

    @EventHandler
    public void onDeath(CombatDeathEvent event) {
        if (!isPlayingInArena(event)) {
            return;
        }

        Player player = event.getPlayer();
        player.setHealth(20);
        player.setFallDistance(0);
        player.setVelocity(new Vector(0, 0, 0));

        boolean shouldTeleport = false;

        if(event.getPlayer().getLocation().getY() < 1 || !arena.getArea().contains(event.getPlayer().getLocation())) {
            shouldTeleport = true;
            event.getDrops().clear();
        } else {
            switch (event.getDeathCause()) {
                case BORDER:
                case OUT_OF_WORLD:
                    event.getDrops().clear();
                case FALLING_BLOCK:
                case IN_WALL:
                    shouldTeleport = true;
                    break;
            }
        }

        arena.changeToSpectator(event.getPlayer(), shouldTeleport);
        arena.checkWin();
        event.setCancelled(true);
    }
}