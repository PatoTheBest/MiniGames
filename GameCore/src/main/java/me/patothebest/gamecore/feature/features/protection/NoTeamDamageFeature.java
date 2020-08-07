package me.patothebest.gamecore.feature.features.protection;

import me.patothebest.gamecore.arena.AbstractGameTeam;
import me.patothebest.gamecore.feature.AbstractFeature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;

public class NoTeamDamageFeature extends AbstractFeature {

    @EventHandler
    public void onTeamDamage(EntityDamageByEntityEvent event) {
        if(event.getEntity().getType() != EntityType.PLAYER || event.getDamager().getType() != EntityType.PLAYER) {
            return;
        }

        if(!isPlayingInArena((Player) event.getEntity()) || !isPlayingInArena((Player) event.getDamager())) {
            return;
        }

        AbstractGameTeam damagedTeam = arena.getTeam((Player) event.getEntity());
        AbstractGameTeam damagerTeam = arena.getTeam((Player) event.getDamager());

        if(!damagedTeam.equals(damagerTeam)) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onProjectileTeamDamage(EntityDamageByEntityEvent event) {
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

        AbstractGameTeam damagedTeam = arena.getTeam((Player) event.getEntity());
        AbstractGameTeam damagerTeam = arena.getTeam(playerShooter);

        if(!damagedTeam.equals(damagerTeam)) {
            return;
        }

        event.setCancelled(true);
    }
}