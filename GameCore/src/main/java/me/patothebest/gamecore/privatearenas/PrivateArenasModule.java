package me.patothebest.gamecore.privatearenas;

import com.google.inject.assistedinject.FactoryModuleBuilder;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.injector.AbstractBukkitModule;
import me.patothebest.gamecore.privatearenas.ui.PrivateArenaUIFactory;

public class PrivateArenasModule extends AbstractBukkitModule<CorePlugin> {

    public PrivateArenasModule(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder().build(PrivateArenaUIFactory.class));
        registerModule(PrivateArenasCommand.class);
        registerModule(PrivateArenasManager.class);
    }
}
