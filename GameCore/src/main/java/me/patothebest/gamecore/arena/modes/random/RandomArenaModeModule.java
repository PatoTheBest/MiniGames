package me.patothebest.gamecore.arena.modes.random;

import com.google.inject.assistedinject.FactoryModuleBuilder;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.injector.AbstractBukkitModule;

public class RandomArenaModeModule extends AbstractBukkitModule<CorePlugin> {

    public RandomArenaModeModule(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder().build(RandomArenaUIFactory.class));
        registerModule(RandomArenaMode.class);
        registerModule(RandomArenaModeCommand.Parent.class);
    }
}
