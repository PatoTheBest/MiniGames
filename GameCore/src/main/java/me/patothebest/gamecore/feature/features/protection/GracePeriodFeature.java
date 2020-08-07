package me.patothebest.gamecore.feature.features.protection;

import com.google.inject.Inject;
import me.patothebest.gamecore.feature.AbstractRunnableFeature;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.util.Sounds;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.projectiles.ProjectileSource;

public class GracePeriodFeature extends AbstractRunnableFeature {

    private final Plugin plugin;
    private int graceTime = 20;
    private int secondsRemaining = 0;
    private boolean gracePeriod = true;

    @Inject private GracePeriodFeature(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (secondsRemaining <= 0) {
            arena.sendMessageToArena(CoreLang.GRACE_PERIOD_ENDED::getMessage);
            arena.playSound(Sounds.ENTITY_ENDER_DRAGON_GROWL);
            gracePeriod = false;
            cancel();
            return;
        }

        if (secondsRemaining % 5 == 0 || secondsRemaining < 5) {
            arena.sendMessageToArena(player -> {
                String secondLang = (secondsRemaining == 1 ? CoreLang.SECOND.getMessage(player) : CoreLang.SECONDS.getMessage(player));
                return CoreLang.GRACE_PERIOD_ENDING.replace(player, secondsRemaining, secondLang);
            });
        }

        secondsRemaining--;
    }

    @EventHandler
    public void onTeamDamage(EntityDamageByEntityEvent event) {
        if (!gracePeriod) {
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
        if (!gracePeriod) {
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

    @Override
    public void initializeFeature() {
        if (!gracePeriod) {
            return;
        }

        secondsRemaining = graceTime;
        runTaskTimer(plugin, 0L, 20L);
    }

    @Override
    public void stopFeature() {
        gracePeriod = true;
    }

    public void setGraceTime(int graceTime) {
        this.graceTime = graceTime;
    }
}
