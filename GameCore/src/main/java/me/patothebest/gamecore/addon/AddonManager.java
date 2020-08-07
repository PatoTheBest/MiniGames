package me.patothebest.gamecore.addon;

import com.google.inject.Inject;
import me.patothebest.gamecore.logger.InjectLogger;
import me.patothebest.gamecore.modules.ActivableModule;
import me.patothebest.gamecore.modules.ModuleName;
import me.patothebest.gamecore.modules.ReloadableModule;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@ModuleName("Addon Manager")
public class AddonManager implements ActivableModule, ReloadableModule {

    private final AddonFile addonFile;
    private final Set<Addon> addons;
    @InjectLogger private Logger logger;

    @Inject private AddonManager(AddonFile addonFile, Set<Addon> addons) {
        this.addonFile = addonFile;
        this.addons = addons;
    }

    @Override
    public void onEnable() {
        int addonCount = 0;
        for (Addon addon : addons) {
            if(!addonFile.isSet(addon.getConfigPath())) {
                continue;
            }

            ConfigurationSection addonConfigurationSection = addonFile.getConfigurationSection(addon.getConfigPath());

            if(!addonConfigurationSection.getBoolean("enabled")) {
                logger.config("Not enabling " + addon.getConfigPath() + " since it's disabled in the config");
                continue;
            }

            logger.log(Level.CONFIG, "Configuring addon {0}", addon.getConfigPath());
            try {
                addon.configure(addonConfigurationSection);
                addon.enable();
                logger.log(Level.CONFIG, "Enabled addon {0}", addon.getConfigPath());
                addonCount++;
            } catch (Throwable t) {
                logger.log(Level.SEVERE, "Could not load addon " + addon.getConfigPath() + "!", t);
            }
        }
        logger.log(Level.INFO, "Loaded {0} addons!", addonCount);
    }

    @Override
    public void onReload() {
        onDisable();
        addonFile.load();
        onEnable();
    }

    @Override
    public String getReloadName() {
        return "addons";
    }

    public boolean isAddonEnabled(Class<? extends Addon> addonClass) {
        return getAddon(addonClass).isEnabled();
    }

    public <T extends Addon> T getAddon(Class<T> addonClass) {
        for (Addon addon : addons) {
            if(addon.getClass() == addonClass) {
                return (T) addon;
            }
        }

        throw new IllegalArgumentException("Addon " + addonClass.getSimpleName() + " is not registered!");
    }

    @Override
    public void onDisable() {
        for (Addon addon : addons) {
            try {
                addon.disable();
            } catch (Throwable t) {
                logger.log(Level.SEVERE, "Could not disable addon " + addon.getConfigPath() + "!", t);
            }
        }
    }
}
