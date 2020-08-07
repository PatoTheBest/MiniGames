package me.patothebest.gamecore.addon;

import com.google.inject.Inject;
import me.patothebest.gamecore.event.EventRegistry;
import me.patothebest.gamecore.modules.Module;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;

public abstract class Addon implements Listener, Module {

    private boolean enabled = false;
    @Inject private EventRegistry registry;

    /**
     * Configures a specific addon
     * <p>
     * This method is called by the {@link AddonManager} when
     * reading the values from the {@link AddonFile}.
     * <p>
     * If the addon is not enabled, this method will not be called.
     *
     * @param addonConfigSection the configuration section where the extra
     *                           configuration of the addon is located.
     */
    public abstract void configure(ConfigurationSection addonConfigSection);

    /**
     * Gets the config path of the addon
     * <p>
     * The path is usually the addon name. This path is used when the
     * {@link AddonManager} looks in the {@link AddonFile} if the module
     * should be enabled or not.
     *
     * @return the config path.
     */
    public abstract String getConfigPath();

    /**
     * Gets if the addon is enabled or not
     *
     * @return true if the addon is enabled or not
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Enables the module to be active, registers the events
     */
    public void enable() {
        registry.registerListener(this);
        this.enabled = true;
    }

    /**
     * Disables the module, unregisters the events
     */
    public void disable() {
        if(!enabled) {
            return;
        }

        registry.unRegisterListener(this);
        this.enabled = false;
    }
}
