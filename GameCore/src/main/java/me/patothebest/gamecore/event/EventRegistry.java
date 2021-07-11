package me.patothebest.gamecore.event;

import com.google.inject.Inject;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.modules.ListenerModule;
import me.patothebest.gamecore.modules.Module;
import me.patothebest.gamecore.scheduler.PluginScheduler;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredListener;

import java.util.logging.Level;

public class EventRegistry implements Module {

    private final CorePlugin plugin;
    private final PluginScheduler pluginScheduler;

    @Inject private EventRegistry(CorePlugin plugin, PluginScheduler pluginScheduler) {
        this.plugin = plugin;
        this.pluginScheduler = pluginScheduler;
    }

    public void registerListener(Listener listener) {
        if(listener instanceof ListenerModule) {
            throw new IllegalArgumentException("Listener " + listener + " cannot be a ListenerModule!");
        }

        plugin.registerListener(listener);
    }

    public void unRegisterListener(Listener listener) {
        HandlerList.unregisterAll(listener);
    }

    public <T extends Event> T callEvent(T event) {
        plugin.getServer().getPluginManager().callEvent(event);
        return event;
    }

    public void callSyncEvent(Event event) {
        pluginScheduler.ensureSync(() -> plugin.getServer().getPluginManager().callEvent(event));
    }

    public void callAsyncEvent(Event event) {
        pluginScheduler.ensureAsync(() -> plugin.getServer().getPluginManager().callEvent(event));
    }

    public <T extends Event> T callEventOnlyOnPlugin(T event) {
        HandlerList handlers = event.getHandlers();
        RegisteredListener[] listeners = handlers.getRegisteredListeners();

        for (RegisteredListener registration : listeners) {
            if (!registration.getPlugin().isEnabled()) {
                continue;
            }

            if(!(registration.getPlugin() instanceof CorePlugin)) {
                continue;
            }

            try {
                registration.callEvent(event);
            } catch (Throwable ex) {
                Bukkit.getServer().getLogger().log(Level.SEVERE, "Could not pass event " + event.getEventName() + " to " + registration.getPlugin().getDescription().getFullName(), ex);
            }
        }

        return event;
    }
}
