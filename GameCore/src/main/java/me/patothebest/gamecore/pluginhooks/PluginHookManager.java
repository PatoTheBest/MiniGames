package me.patothebest.gamecore.pluginhooks;

import com.google.inject.Singleton;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.modules.ModuleName;
import me.patothebest.gamecore.file.PluginHookFile;
import me.patothebest.gamecore.logger.InjectLogger;
import me.patothebest.gamecore.modules.ActivableModule;
import org.bukkit.ChatColor;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
@ModuleName("Plugin Hook Manager")
public class PluginHookManager implements ActivableModule {

    private final CorePlugin plugin;
    private final PluginHookFile pluginHookFile;
    private final Map<String, PluginHookProvider> pluginHookClasses;
    private final List<PluginHook> pluginHooks;
    @InjectLogger private Logger logger;

    @Inject public PluginHookManager(CorePlugin plugin, PluginHookFile pluginHookFile, Map<String, PluginHookProvider> pluginHookClasses) {
        this.plugin = plugin;
        this.pluginHookFile = pluginHookFile;
        this.pluginHooks = new ArrayList<>();
        this.pluginHookClasses = pluginHookClasses;
    }

    @Override
    public void onEnable() {
        logger.info(ChatColor.YELLOW + "Attempting to hook into supported plugins...");

        pluginHookClasses.forEach((key, value) -> {
            if (!plugin.getServer().getPluginManager().isPluginEnabled(key)) {
                return;
            }

            if (!pluginHookFile.getBoolean(key + ".enabled")) {
                logger.log(Level.CONFIG, "Not enabling " + key + " because it's disabled in the config");
                return;
            }

            try {
                logger.log(Level.FINE, "Attempting to hook into " + key + "...");
                PluginHook pluginHook = plugin.getInjector().getInstance(value.getPluginHookClass());
                pluginHooks.add(pluginHook);
                pluginHook.onHook(pluginHookFile.getConfigurationSection(key + ".configuration"));
                logger.info("Hooked into " + pluginHook.getPluginName() + "!");
            } catch (Throwable t) {
                logger.log(Level.SEVERE, "Unable to hook into " + key, t);
            }
        });
    }

    public boolean isHookLoaded(Class<? extends PluginHook> clazz) {
        return pluginHooks.stream().filter(pluginHook -> pluginHook.getClass().equals(clazz)).findFirst().orElse(null) != null;
    }

    @SuppressWarnings("unchecked")
    public <T extends PluginHook> T getHook(Class<T> clazz) {
        return (T) pluginHooks.stream().filter(pluginHook -> pluginHook.getClass().equals(clazz)).findFirst().orElseThrow(() -> new IllegalArgumentException(clazz + " is not found! Custom hook?"));
    }
}