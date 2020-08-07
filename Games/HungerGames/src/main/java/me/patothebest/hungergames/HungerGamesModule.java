package me.patothebest.hungergames;

import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import me.patothebest.gamecore.feature.features.chests.refill.ChestRefillFeature;
import me.patothebest.gamecore.file.CoreConfig;
import me.patothebest.gamecore.injector.AbstractBukkitModule;
import me.patothebest.gamecore.kit.Kit;
import me.patothebest.gamecore.kit.NullKit;
import me.patothebest.gamecore.player.CorePlayer;
import me.patothebest.gamecore.player.PlayerFactory;
import me.patothebest.hungergames.arena.ArenaModule;
import me.patothebest.hungergames.commands.ConvertCommands;
import me.patothebest.hungergames.commands.HungerGamesCommand;
import me.patothebest.hungergames.commands.MidChestsCommand;
import me.patothebest.hungergames.commands.SetupArenaCommands;
import me.patothebest.hungergames.commands.SpawnsCommand;
import me.patothebest.hungergames.commands.SupplyDropsCommand;
import me.patothebest.hungergames.commands.TeamSetupCommands;
import me.patothebest.hungergames.config.Config;
import me.patothebest.hungergames.feature.SupplyDropFeature;
import me.patothebest.hungergames.lang.LocaleManager;
import me.patothebest.hungergames.player.HungerGamesPlayer;
import me.patothebest.hungergames.stats.StatsModule;
import me.patothebest.hungergames.placeholder.HungerGamesPlaceholderModule;

public class HungerGamesModule extends AbstractBukkitModule<HungerGames> {

    public HungerGamesModule(HungerGames plugin) {
        super(plugin);
    }

    @Override
    protected void configure() {
        bind(HungerGames.class).toInstance(plugin);

        install(new FactoryModuleBuilder().implement(CorePlayer.class, HungerGamesPlayer.class).build(PlayerFactory.class));
        install(new ArenaModule(plugin));
        install(new HungerGamesPlaceholderModule(plugin));
        install(new StatsModule(plugin));

        bind(CoreConfig.class).to(Config.class).in(Singleton.class);
        plugin.registerModule(Config.class);

        bind(Kit.class).annotatedWith(Names.named("DefaultKit")).to(NullKit.class);

        registerModule(HungerGamesCommand.class);
        registerModule(TeamSetupCommands.Parent.class);
        registerModule(ConvertCommands.Parent.class);
        registerModule(SetupArenaCommands.class);
        registerModule(MidChestsCommand.class);
        registerModule(MidChestsCommand.Parent.class);
        registerModule(SpawnsCommand.class);
        registerModule(SpawnsCommand.Parent.class);
        registerModule(SupplyDropsCommand.class);
        registerModule(SupplyDropsCommand.Parent.class);
        registerModule(LocaleManager.class);
        registerDynamicModule(ChestRefillFeature.class);
        registerDynamicModule(SupplyDropFeature.class);
    }
}
