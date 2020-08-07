package me.patothebest.skywars;

import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import me.patothebest.gamecore.cosmetics.cage.CageModule;
import me.patothebest.gamecore.feature.features.chests.refill.ChestRefillFeature;
import me.patothebest.gamecore.file.CoreConfig;
import me.patothebest.gamecore.injector.AbstractBukkitModule;
import me.patothebest.gamecore.kit.Kit;
import me.patothebest.gamecore.kit.NullKit;
import me.patothebest.gamecore.player.CorePlayer;
import me.patothebest.gamecore.player.PlayerFactory;
import me.patothebest.skywars.arena.ArenaModule;
import me.patothebest.skywars.commands.ConvertCommands;
import me.patothebest.skywars.commands.MidChestsCommand;
import me.patothebest.skywars.commands.SetupArenaCommands;
import me.patothebest.skywars.commands.SkyWarsCommand;
import me.patothebest.skywars.commands.SpawnsCommand;
import me.patothebest.skywars.commands.TeamSetupCommands;
import me.patothebest.skywars.lang.LocaleManager;
import me.patothebest.skywars.placeholder.SkyWarsPlaceholderModule;
import me.patothebest.skywars.player.SkyWarsPlayer;
import me.patothebest.skywars.stats.StatsModule;
import me.patothebest.skywars.config.Config;

public class SkyWarsModule extends AbstractBukkitModule<SkyWars> {

    public SkyWarsModule(SkyWars plugin) {
        super(plugin);
    }

    @Override
    protected void configure() {
        bind(SkyWars.class).toInstance(plugin);

        install(new FactoryModuleBuilder().implement(CorePlayer.class, SkyWarsPlayer.class).build(PlayerFactory.class));
        install(new ArenaModule(plugin));
        install(new SkyWarsPlaceholderModule(plugin));
        install(new StatsModule(plugin));
        install(new CageModule(plugin));

        bind(CoreConfig.class).to(Config.class).in(Singleton.class);
        plugin.registerModule(Config.class);

        bind(Kit.class).annotatedWith(Names.named("DefaultKit")).to(NullKit.class);

        registerModule(SkyWarsCommand.class);
        registerModule(TeamSetupCommands.Parent.class);
        registerModule(ConvertCommands.Parent.class);
        registerModule(SetupArenaCommands.class);
        registerModule(MidChestsCommand.class);
        registerModule(MidChestsCommand.Parent.class);
        registerModule(SpawnsCommand.class);
        registerModule(SpawnsCommand.Parent.class);
        registerModule(LocaleManager.class);
        registerDynamicModule(ChestRefillFeature.class);
    }
}
