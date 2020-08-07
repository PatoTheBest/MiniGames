package me.patothebest.gamecore.arena.modes.random;

import com.google.inject.Inject;
import com.google.inject.Provider;
import me.patothebest.gamecore.PluginConfig;
import me.patothebest.gamecore.arena.ArenaManager;
import me.patothebest.gamecore.event.EventRegistry;
import me.patothebest.gamecore.file.CoreConfig;
import me.patothebest.gamecore.logger.InjectLogger;
import me.patothebest.gamecore.modules.ActivableModule;
import me.patothebest.gamecore.modules.ListenerModule;
import me.patothebest.gamecore.modules.ModuleName;
import me.patothebest.gamecore.modules.ReloadPriority;
import me.patothebest.gamecore.modules.ReloadableModule;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.pluginhooks.PluginHookManager;
import me.patothebest.gamecore.scheduler.PluginScheduler;
import me.patothebest.gamecore.sign.SignManager;
import me.patothebest.gamecore.util.Priority;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.world.WorldHandler;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@ReloadPriority(priority = Priority.LOW)
@ModuleName("Random Arena Mode")
public class RandomArenaMode implements ActivableModule, ListenerModule, ReloadableModule, Runnable {

    private boolean enabled;
    private final CoreConfig coreConfig;
    private final PluginScheduler pluginScheduler;
    private final SignManager signManager;
    private final EventRegistry eventRegistry;
    private final ArenaManager arenaManager;
    private final PlayerManager playerManager;
    private final Provider<WorldHandler> worldHandlerProvider;
    private final List<RandomArenaGroup> groups = new ArrayList<>();
    private final PluginHookManager pluginHookManager;
    @InjectLogger private Logger logger;

    private volatile boolean asyncEnabled;

    @Inject
    private RandomArenaMode(CoreConfig coreConfig, PluginScheduler pluginScheduler, SignManager signManager, EventRegistry eventRegistry, ArenaManager arenaManager, PlayerManager playerManager, Provider<WorldHandler> worldHandlerProvider, PluginHookManager pluginHookManager) {
        this.coreConfig = coreConfig;
        this.pluginScheduler = pluginScheduler;
        this.signManager = signManager;
        this.eventRegistry = eventRegistry;
        this.arenaManager = arenaManager;
        this.playerManager = playerManager;
        this.worldHandlerProvider = worldHandlerProvider;
        this.pluginHookManager = pluginHookManager;
    }

    @Override
    public void onPreEnable() {
        File[] files = new File(".").listFiles();
        if (files != null) {
            for (File file : files) {
                if (!file.isDirectory()) {
                    continue;
                }

                if (file.getName().contains(PluginConfig.WORLD_PREFIX + "rand_") ||
                        file.getName().contains(PluginConfig.WORLD_PREFIX.toLowerCase() + "rand_") ||
                        file.getName().startsWith("temp_" + PluginConfig.WORLD_PREFIX.toLowerCase() + "")) {
                    Utils.deleteFolder(file);
                    logger.log(Level.INFO, "Deleted old temp world " + file.getName());
                }
            }
        }

        enabled = false;
        ConfigurationSection randomArenaSection = coreConfig.getConfigurationSection("random-arena-mode");

        if (randomArenaSection == null) {
            return;
        }

        if (!randomArenaSection.getBoolean("enabled")) {
            return;
        }

        if (!randomArenaSection.isSet("arena-groups")) {
            return;
        }

        enabled = true;
        arenaManager.setLoadMaps(false);
    }

    @Override
    public void onPostEnable() {
        if (!enabled) {
            return;
        }

        ConfigurationSection randomArenaSection = coreConfig.getConfigurationSection("random-arena-mode");
        for (String groupName : randomArenaSection.getConfigurationSection("arena-groups").getKeys(false)) {
            RandomArenaGroup group;

            //if (pluginHookManager.isHookLoaded(SlimeWorldManagerHook.class)) {
            //    group = new SlimeRandomArenaGroup(groupName, logger, pluginHookManager, pluginScheduler, signManager, randomArenaSection.getConfigurationSection("arena-groups." + groupName), arenaManager, worldHandlerProvider, playerManager);
            //} else {
                group = new RandomArenaGroup(groupName, logger, pluginScheduler, signManager, randomArenaSection.getConfigurationSection("arena-groups." + groupName), arenaManager, worldHandlerProvider, playerManager);
            //}

            group.init();
            eventRegistry.registerListener(group);
            groups.add(group);
        }

        if (worldHandlerProvider.get().hasAsyncSupport()) {
            asyncEnabled = true;
            pluginScheduler.runTaskAsynchronously(this);
        }
    }

    @Override
    public void run() {
        try {
            while (asyncEnabled) {
                for (RandomArenaGroup group : groups) {
                    try {
                        group.run();
                    } catch (Exception e) {
                        logger.log(Level.SEVERE, "Could not run arena queue!", e);
                    }
                }

                Thread.sleep(250);
            }
        } catch (InterruptedException ignored) { }
    }

    @Override
    public void onDisable() {
        for (RandomArenaGroup group : groups) {
            eventRegistry.unRegisterListener(group);
            group.destroy();
        }

        groups.clear();
        asyncEnabled = false;
    }

    @Override
    public void onReload() {
        onDisable();
        onPreEnable();
        onPostEnable();
    }

    @Override
    public String getReloadName() {
        return "random-mode";
    }

    public List<RandomArenaGroup> getGroups() {
        return groups;
    }
}