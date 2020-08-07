package me.patothebest.gamecore.injector;

import com.google.inject.AbstractModule;
import me.patothebest.gamecore.CorePlugin;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * A Guice module used by our plugins that can be
 * installed in guice
 *
 * @param <T> the generic type
 */
public class BukkitModule<T extends CorePlugin> extends AbstractModule {

    /** The plugin. */
    private final T abstractJavaPlugin;

    /**
     * Instantiates a new bukkit module.
     *
     * @param abstractJavaPlugin the plugin
     */
    public BukkitModule(T abstractJavaPlugin) {
        this.abstractJavaPlugin = abstractJavaPlugin;
    }

    /* (non-Javadoc)
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
        bind(Plugin.class).toInstance(abstractJavaPlugin);
        bind(JavaPlugin.class).toInstance(abstractJavaPlugin);
        bind(CorePlugin.class).toInstance(abstractJavaPlugin);
        abstractJavaPlugin.configureCore(binder());
    }
}
