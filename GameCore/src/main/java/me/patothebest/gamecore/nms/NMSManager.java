package me.patothebest.gamecore.nms;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.modules.ActivableModule;
import me.patothebest.gamecore.modules.ModuleName;
import me.patothebest.gamecore.logger.InjectLogger;
import me.patothebest.gamecore.util.EnchantGlow;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
@ModuleName("NMS Manager")
public class NMSManager implements ActivableModule {

    // -------------------------------------------- //
    // FIELD
    // -------------------------------------------- //

    private final CorePlugin corePlugin;
    private NMS nmsImplementation;
    @InjectLogger private Logger logger;

    @Inject private NMSManager(CorePlugin corePlugin) {
        this.corePlugin = corePlugin;
    }

    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //


    @Override
    public void onEnable() {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();

        // Get full package string of CraftServer.
        // org.bukkit.craftbukkit.version
        String version = packageName.substring(packageName.lastIndexOf('.') + 1);
        // Get the last element of the package

        logger.info("Server version " + version + " detected!");
        logger.fine(ChatColor.YELLOW + "Attempting to load NMS...");

        try {
            final Class<?> clazz = Class.forName("me.patothebest.gamecore.nms." + version + ".NMSImpl");
            // Check if we have a NMSHandler class at that location.
            if (NMS.class.isAssignableFrom(clazz)) { // Make sure it actually implements NMS
                this.nmsImplementation = (NMS) clazz.getConstructor().newInstance(); // Set our handler
            } else {
                // class is not instance of NMS
                logger.severe(ChatColor.RED + "Could not instantiate NMS classes for version " + version + "!");
                nmsImplementation = new NullNMSImpl();
                return;
            }

            EnchantGlow.setGlow(nmsImplementation.getGlowEnchant());
            logger.info("Successfully instantiated NMS classes!");
        } catch (final Exception e) {
            logger.log(Level.SEVERE, ChatColor.RED + "Could not instantiate NMS classes for version " + version + "!", e);
            nmsImplementation = new NullNMSImpl();
        }
    }

    // -------------------------------------------- //
    // GETTER
    // -------------------------------------------- //

    NMS getImplementation() {
        return nmsImplementation;
    }
}
