package me.patothebest.gamecore.listener;

import com.google.inject.Inject;
import me.patothebest.gamecore.scheduler.PluginScheduler;
import me.patothebest.gamecore.modules.ListenerModule;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.WorldUnloadEvent;

import java.lang.ref.WeakReference;

public class LeakListener implements ListenerModule {

    private final PluginScheduler pluginScheduler;

    @Inject private LeakListener(PluginScheduler pluginScheduler) {
        this.pluginScheduler = pluginScheduler;
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) {
        WeakReference<World> worldWeakReference = new WeakReference<>(event.getWorld());

        pluginScheduler.runTaskLater(() -> {
            if(worldWeakReference.get() != null) {
                System.err.println("World is still in memory! Attempting to gc...");
                System.gc();
            }
        }, 20*10L);
    }
}
