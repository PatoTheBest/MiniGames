package me.patothebest.gamecore.nms;

import com.google.inject.Provides;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.injector.AbstractBukkitModule;

public class NMSModule extends AbstractBukkitModule<CorePlugin> {

    public NMSModule(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    protected void configure() {
        registerModule(NMSManager.class);
    }

    @Provides NMS getNMSImplementation(NMSManager nmsManager) {
        return nmsManager.getImplementation();
    }
}
