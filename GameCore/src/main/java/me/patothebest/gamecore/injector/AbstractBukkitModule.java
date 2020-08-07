package me.patothebest.gamecore.injector;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.modules.Module;

/**
 * The Class AbstractBukkitModule.
 *
 * @param <T> the plugin type
 */
public abstract class AbstractBukkitModule<T extends CorePlugin> extends AbstractModule {

    /** The plugin. */
    protected final T plugin;

    /**
     * Instantiates a new abstract bukkit module.
     *
     * @param plugin the plugin
     */
    public AbstractBukkitModule(T plugin) {
        this.plugin = plugin;
    }

    /**
     * Registers a module.
     *
     * @param moduleClazz the module class
     */
    protected void registerModule(Class<? extends Module> moduleClazz) {
        binder().bind(moduleClazz).in(Singleton.class);
        plugin.registerModule(moduleClazz);
    }

    /**
     * Registers a module that is not a singleton.
     *
     * @param moduleClazz the module class
     */
    protected void registerDynamicModule(Class<? extends Module> moduleClazz) {
        binder().bind(moduleClazz);
        plugin.registerModule(moduleClazz);
    }
}
