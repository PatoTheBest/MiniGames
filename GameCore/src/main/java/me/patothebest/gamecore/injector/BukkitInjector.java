package me.patothebest.gamecore.injector;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import me.patothebest.gamecore.CorePlugin;

/**
 * The class that creates the injector
 *
 * @param <T> the plugin
 */
public class BukkitInjector<T extends CorePlugin> {

    /** The injector. */
    private final Injector injector;

    /**
     * Instantiates a new injector.
     *
     * @param abstractJavaPlugin the plugin
     */
    public BukkitInjector(T abstractJavaPlugin) {
        this.injector = Guice.createInjector(Stage.PRODUCTION, new BukkitModule<>(abstractJavaPlugin));
    }
}
