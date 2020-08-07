package me.patothebest.thetowers.arena;

import com.google.inject.assistedinject.FactoryModuleBuilder;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.arena.ArenaFactory;
import me.patothebest.gamecore.arena.ArenaManager;
import me.patothebest.gamecore.arena.ArenaState;
import me.patothebest.gamecore.injector.AbstractBukkitModule;

public class ArenaModule extends AbstractBukkitModule<CorePlugin> {

    public ArenaModule(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder().implement(AbstractArena.class, Arena.class).build(ArenaFactory.class));
        registerModule(ArenaManager.class);

        ArenaState.configureInGameJoinable();
    }
}
