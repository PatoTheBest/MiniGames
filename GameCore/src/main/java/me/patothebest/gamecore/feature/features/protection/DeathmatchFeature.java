package me.patothebest.gamecore.feature.features.protection;

import com.google.inject.Inject;
import me.patothebest.gamecore.event.player.PlayerThrowTNTEvent;
import me.patothebest.gamecore.feature.AbstractRunnableFeature;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.util.Sounds;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.projectiles.ProjectileSource;

public class DeathmatchFeature extends AbstractRunnableFeature {

    private final Plugin plugin;
    private final int deathmatchTime = 10;

    private int secondsRemaining = 0;
    private boolean deathmatchStarting = true;

    @Inject private DeathmatchFeature(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (secondsRemaining <= 0) {
            arena.sendMessageToArena(CoreLang.DEATHMATCH_STARTED::getMessage);
            arena.playSound(Sounds.ENTITY_WITHER_SPAWN);
            deathmatchStarting = false;
            cancel();
            return;
        }

        arena.sendMessageToArena(player -> {
            String secondLang = (secondsRemaining == 1 ? CoreLang.SECOND.getMessage(player) : CoreLang.SECONDS.getMessage(player));
            return CoreLang.DEATHMATCH_STARTING.replace(player, secondsRemaining, secondLang);
        });

        secondsRemaining--;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!deathmatchStarting) {
            return;
        }

        if(event.getEntity().getType() != EntityType.PLAYER || event.getDamager().getType() != EntityType.PLAYER) {
            return;
        }

        if(!isPlayingInArena((Player) event.getEntity()) || !isPlayingInArena((Player) event.getDamager())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onProjectileTeamDamage(EntityDamageByEntityEvent event) {
        if (!deathmatchStarting) {
            return;
        }

        if(event.getEntity().getType() != EntityType.PLAYER || event.getDamager().getType() == EntityType.PLAYER) {
            return;
        }

        if(!(event.getDamager() instanceof Projectile)) {
            return;
        }

        if(!isPlayingInArena((Player) event.getEntity())) {
            return;
        }

        ProjectileSource shooter = ((Projectile)event.getDamager()).getShooter();
        if(!(shooter instanceof Player)) {
            return;
        }

        Player playerShooter = (Player) shooter;

        if(!isPlayingInArena(playerShooter)) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onTNTThrow(PlayerThrowTNTEvent event) {
        if (!deathmatchStarting) {
            return;
        }

        if (event.getArena() != arena) {
            return;
        }

        event.setCancelled(true);
        CoreLang.DEATHMATCH_YOU_CANNOT_THROW_TNT.sendMessage(event.getPlayer());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (!deathmatchStarting) {
            return;
        }

        if (event.getFrom().getX() == event.getTo().getX() && event.getFrom().getZ() == event.getTo().getZ()) {
            return;
        }

        if (!isPlayingInArena(event.getPlayer())) {
            return;
        }

        event.setTo(event.getFrom());
    }

    @Override
    public void initializeFeature() {
        if (!deathmatchStarting) {
            return;
        }

        secondsRemaining = 10;
        runTaskTimer(plugin, 0L, 20L);
    }

    @Override
    public void stopFeature() {
        deathmatchStarting = true;
    }

}
