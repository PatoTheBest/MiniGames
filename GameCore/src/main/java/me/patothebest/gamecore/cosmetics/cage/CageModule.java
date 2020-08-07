package me.patothebest.gamecore.cosmetics.cage;

import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.injector.AbstractBukkitModule;

public class CageModule extends AbstractBukkitModule<CorePlugin> {

    public CageModule(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    protected void configure() {
        registerModule(CageManager.class);
    }
}
