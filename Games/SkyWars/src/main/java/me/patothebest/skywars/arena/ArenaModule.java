package me.patothebest.skywars.arena;

import com.google.common.collect.ImmutableSet;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.OptionalBinder;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.arena.ArenaFactory;
import me.patothebest.gamecore.arena.ArenaGroup;
import me.patothebest.gamecore.arena.ArenaManager;
import me.patothebest.gamecore.feature.features.chests.refill.ChestLocation;
import me.patothebest.gamecore.injector.AbstractBukkitModule;

import java.util.Set;

public class ArenaModule extends AbstractBukkitModule<CorePlugin> {

    public ArenaModule(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder().implement(AbstractArena.class, Arena.class).build(ArenaFactory.class));
        registerModule(ArenaManager.class);

        // Bind arena groups
        OptionalBinder.newOptionalBinder(binder(), new TypeLiteral<Set<ArenaGroup>>() {})
                .setBinding().toInstance(ImmutableSet.of(ArenaType.SOLO, ArenaType.TEAM));

        bind(new TypeLiteral<Set<ChestLocation>>() {})
                .toInstance(ImmutableSet.of(ChestLocations.ISLAND, ChestLocations.MID));
    }
}
