package me.patothebest.gamecore.cosmetics.projectiletrails;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import fr.mrmicky.fastparticle.FastParticle;
import me.patothebest.gamecore.modules.ModuleName;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.modules.ActivableModule;
import me.patothebest.gamecore.util.CacheCollection;
import me.patothebest.gamecore.util.WrappedBukkitRunnable;
import org.bukkit.Location;
import org.bukkit.entity.Projectile;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

@Singleton
@ModuleName("Projectile Tracker")
public class ProjectileTracker extends WrappedBukkitRunnable implements ActivableModule {

    private final Collection<TrackedProjectile> trackedProjectiles = new CacheCollection<>(cacheBuilder -> cacheBuilder.expireAfterWrite(5, TimeUnit.MINUTES));
    private final CorePlugin plugin;

    @Inject private ProjectileTracker(CorePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onEnable() {
        runTaskTimer(plugin, 0L, 1L);
    }

    @Override
    public void run() {
        for (TrackedProjectile trackedProjectile : trackedProjectiles) {
            if(!trackedProjectile.isValid()) {
                trackedProjectiles.remove(trackedProjectile);
                return;
            }

            if(!trackedProjectile.canSpawn()) {
                return;
            }

            Location location = trackedProjectile.getProjectile().getLocation();
            FastParticle.spawnParticle(location.getWorld(), trackedProjectile.getParticleType(), location, 1, 0, 0, 0, 0);
        }
    }

    public void trackProjectile(Projectile projectile, ProjectileTrail projectileTrail) {
        trackedProjectiles.add(new TrackedProjectile(projectile, projectileTrail));
    }
}
