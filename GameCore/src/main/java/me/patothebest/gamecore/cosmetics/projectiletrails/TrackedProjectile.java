package me.patothebest.gamecore.cosmetics.projectiletrails;

import fr.mrmicky.fastparticle.ParticleType;
import org.bukkit.entity.Projectile;

import java.lang.ref.WeakReference;

class TrackedProjectile {

    private final WeakReference<Projectile> projectile;
    private final ProjectileTrail projectileTrail;
    private long groundTime = 0;
    private int ticksLived;

    TrackedProjectile(Projectile projectile, ProjectileTrail projectileTrail) {
        this.projectile = new WeakReference<>(projectile);
        this.projectileTrail = projectileTrail;
    }

    boolean isValid() {
        if(projectile.get() == null || projectile.get().isDead()) {
            return false;
        }

        if(!projectile.get().isOnGround()) {
            return true;
        }

        if(groundTime == 0) {
            groundTime = System.currentTimeMillis();
        }

        return System.currentTimeMillis() - groundTime < getDurationInMillis();
    }

    Projectile getProjectile() {
        return projectile.get();
    }

    ParticleType getParticleType() {
        return projectileTrail.getParticleType();
    }

    int getDurationInMillis() {
        return projectileTrail.getDurationInMillis();
    }

    boolean canSpawn() {
        return ticksLived++ % projectileTrail.getInterval() == 0;
    }
}
