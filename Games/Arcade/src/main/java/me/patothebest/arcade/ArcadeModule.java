package me.patothebest.arcade;

import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import me.patothebest.arcade.arena.ArenaModule;
import me.patothebest.arcade.commands.ArcadeCommand;
import me.patothebest.arcade.commands.SetupArenaCommands;
import me.patothebest.arcade.commands.TeamSetupCommands;
import me.patothebest.arcade.config.Config;
import me.patothebest.arcade.lang.LocaleManager;
import me.patothebest.arcade.placeholder.ArcadePlaceholderModule;
import me.patothebest.arcade.player.ArcadePlayer;
import me.patothebest.gamecore.commands.setup.CreateArenaCommand;
import me.patothebest.gamecore.feature.features.chests.refill.ChestRefillFeature;
import me.patothebest.gamecore.file.CoreConfig;
import me.patothebest.gamecore.injector.AbstractBukkitModule;
import me.patothebest.gamecore.kit.Kit;
import me.patothebest.gamecore.kit.NullKit;
import me.patothebest.gamecore.player.CorePlayer;
import me.patothebest.gamecore.player.PlayerFactory;

public class ArcadeModule extends AbstractBukkitModule<Arcade> {

    public ArcadeModule(Arcade plugin) {
        super(plugin);
    }

    @Override
    protected void configure() {
        bind(Arcade.class).toInstance(plugin);

        install(new FactoryModuleBuilder().implement(CorePlayer.class, ArcadePlayer.class).build(PlayerFactory.class));
        install(new ArenaModule(plugin));
        install(new ArcadePlaceholderModule(plugin));

        bind(CoreConfig.class).to(Config.class).in(Singleton.class);
        plugin.registerModule(Config.class);

        bind(Kit.class).annotatedWith(Names.named("DefaultKit")).to(NullKit.class);

        registerModule(ArcadeCommand.class);
        registerModule(TeamSetupCommands.Parent.class);
        registerModule(SetupArenaCommands.class);
        registerModule(LocaleManager.class);
        registerModule(CreateArenaCommand.class);
        registerDynamicModule(ChestRefillFeature.class);
    }
}
