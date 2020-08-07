package me.patothebest.gamecore.guis;

import com.google.inject.assistedinject.FactoryModuleBuilder;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.cosmetics.shop.ShopFactory;
import me.patothebest.gamecore.cosmetics.shop.ShopFactoryImpl;
import me.patothebest.gamecore.guis.grouppermissible.PermissionGroupUIFactory;
import me.patothebest.gamecore.guis.kit.KitUIFactory;
import me.patothebest.gamecore.injector.AbstractBukkitModule;
import me.patothebest.gamecore.feature.features.gameoptions.GameOptionsGUIFactory;

public class GUIModule extends AbstractBukkitModule<CorePlugin> {

    public GUIModule(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder().build(UserGUIFactory.class));
        install(new FactoryModuleBuilder().build(AdminGUIFactory.class));
        install(new FactoryModuleBuilder().build(KitUIFactory.class));
        install(new FactoryModuleBuilder().build(PermissionGroupUIFactory.class));
        install(new FactoryModuleBuilder().build(GameOptionsGUIFactory.class));
        bind(ShopFactory.class).to(ShopFactoryImpl.class);
    }
}
