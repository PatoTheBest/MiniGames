package me.patothebest.gamecore.menu;

import com.google.inject.assistedinject.FactoryModuleBuilder;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.injector.AbstractBukkitModule;

public class MenuModule extends AbstractBukkitModule<CorePlugin> {

    public MenuModule(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder().build(MenuFactory.class));
        registerModule(MenuManager.class);
        registerModule(MenuCommand.Parent.class);
    }
}
