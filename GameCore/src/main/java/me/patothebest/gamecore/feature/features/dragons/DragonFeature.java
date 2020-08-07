package me.patothebest.gamecore.feature.features.dragons;

import com.google.inject.Inject;
import com.google.inject.Provider;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.nms.NMS;
import me.patothebest.gamecore.arena.types.CentrableArena;
import me.patothebest.gamecore.feature.AbstractRunnableFeature;
import org.bukkit.Location;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DragonFeature extends AbstractRunnableFeature {

    private final List<EnderDragon> dragons = new CopyOnWriteArrayList<>();
    private final CorePlugin plugin;
    private final Provider<NMS> nmsProvider;
    private final int spawnDelay = 25; // 25 seconds between each dragon spawn

    @Inject private DragonFeature(CorePlugin plugin, Provider<NMS> nmsProvider) {
        this.plugin = plugin;
        this.nmsProvider = nmsProvider;
    }

    @Override
    public void run() {
        Location centerLocation = ((CentrableArena) arena).getCenterLocation();
        EnderDragon dragon = nmsProvider.get().spawnEnderdragon(centerLocation);
        dragons.add(dragon);
    }

    @Override
    public void initializeFeature() {
        runTaskTimer(plugin, 0L, spawnDelay*20L);
    }

    @Override
    public void stopFeature() {
        cancel();
        dragons.forEach(Entity::remove);
        dragons.clear();
    }
}
