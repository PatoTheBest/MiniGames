package me.patothebest.gamecore.leaderboards.holograms;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.patothebest.gamecore.event.other.LeaderboardUpdateEvent;
import me.patothebest.gamecore.file.CoreConfig;
import me.patothebest.gamecore.file.ParserException;
import me.patothebest.gamecore.logger.InjectLogger;
import me.patothebest.gamecore.modules.ActivableModule;
import me.patothebest.gamecore.modules.ListenerModule;
import me.patothebest.gamecore.modules.ModuleName;
import me.patothebest.gamecore.modules.ReloadableModule;
import me.patothebest.gamecore.pluginhooks.PluginHookManager;
import me.patothebest.gamecore.pluginhooks.hooks.HolographicDisplaysHook;
import me.patothebest.gamecore.scheduler.PluginScheduler;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Singleton
@ModuleName("Leader Holograms Manager")
public class LeaderHologramManager implements ActivableModule, ReloadableModule, ListenerModule {

    private final List<LeaderHologram> holograms = new ArrayList<>();
    private final HolographicFactory factory;
    private final PluginScheduler pluginScheduler;
    private final PluginHookManager pluginHookManager;
    private final CoreConfig config;
    private final HolographicFile holoFile;
    @InjectLogger private Logger logger;

    @Inject private LeaderHologramManager(HolographicFactory factory, PluginScheduler pluginScheduler, PluginHookManager pluginHookManager, CoreConfig config, HolographicFile holoFile) {
        this.factory = factory;
        this.pluginScheduler = pluginScheduler;
        this.pluginHookManager = pluginHookManager;
        this.config = config;
        this.holoFile = holoFile;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onPostEnable() {
        if(!config.getBoolean("leaderboard.enabled")) {
            return;
        }

        if (!pluginHookManager.isHookLoaded(HolographicDisplaysHook.class)) {
            logger.info("Holographic displays not found. Leader Holograms won't be enabled.");
            return;
        }

        List<Map<String, Object>> hologramsData = (List<Map<String, Object>>) holoFile.get("holograms");

        if (hologramsData == null || hologramsData.isEmpty()) {
            return;
        }

        logger.info(ChatColor.YELLOW + "Loading leaderboard holograms...");
        for (Map<String, Object> holoData : hologramsData) {
            try {
                holograms.add(factory.createLeaderHologram(holoData));
            } catch (ParserException e) {
                Utils.printError("Could not parse leaderboard hologram", e.getMessage());
            } catch (Throwable t) {
                logger.severe(ChatColor.RED + "Could not load leaderboard hologram");
                t.printStackTrace();
            }
        }
        logger.info("Loaded " + holograms.size() + " hologram(s)");
    }

    @Override
    public void onDisable() {
        for (LeaderHologram hologram : holograms) {
            hologram.destroy();
        }
        holograms.clear();
    }

    @EventHandler
    public void onStatsUpdate(LeaderboardUpdateEvent event) {
        pluginScheduler.ensureSync(() -> {
            for (LeaderHologram hologram : holograms) {
                hologram.updateHolograms();
            }
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        for (LeaderHologram hologram : holograms) {
            hologram.playerQuit(event.getPlayer());
        }
    }

    @Override
    public void onReload() {
        onDisable();
        onPostEnable();
    }

    @Override
    public String getReloadName() {
        return "leaderholograms";
    }

    public void saveData() {
        List<Map<String, Object>> hologramsData = new ArrayList<>();
        for (LeaderHologram sign : holograms) {
            hologramsData.add(sign.serialize());
        }
        holoFile.set("holograms", hologramsData);
        holoFile.save();
    }

    public void createHologram(String name, Location location) {
        holograms.add(factory.createLeaderHologram(name, location));
    }

    public LeaderHologram getHologram(String name) {
        for (LeaderHologram hologram : holograms) {
            if (hologram.getName().equalsIgnoreCase(name)) {
                return hologram;
            }
        }

        return null;
    }

    public List<LeaderHologram> getHolograms() {
        return holograms;
    }
}
