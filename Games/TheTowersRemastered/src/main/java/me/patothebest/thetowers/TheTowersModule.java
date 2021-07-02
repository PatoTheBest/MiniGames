package me.patothebest.thetowers;

import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import me.patothebest.gamecore.commands.setup.CreateArenaCommand;
import me.patothebest.gamecore.commands.setup.TeamSetupCommands;
import me.patothebest.gamecore.file.CoreConfig;
import me.patothebest.gamecore.injector.AbstractBukkitModule;
import me.patothebest.gamecore.kit.Kit;
import me.patothebest.gamecore.kit.defaults.KitDefault;
import me.patothebest.gamecore.player.CorePlayer;
import me.patothebest.gamecore.player.PlayerFactory;
import me.patothebest.thetowers.animation.AnimationModule;
import me.patothebest.thetowers.arena.ArenaModule;
import me.patothebest.thetowers.command.CommandManager;
import me.patothebest.thetowers.command.commands.admin.DebugCommands;
import me.patothebest.thetowers.command.commands.setup.ProtectedAreasCommand;
import me.patothebest.thetowers.command.commands.setup.TheTowersSetupCommand;
import me.patothebest.thetowers.command.commands.user.ProjectileTrailsCommand;
import me.patothebest.thetowers.file.Config;
import me.patothebest.thetowers.file.FileManager;
import me.patothebest.thetowers.language.LocaleManager;
import me.patothebest.thetowers.listener.VaultHandler;
import me.patothebest.thetowers.placeholder.TheTowersPlaceholderModule;
import me.patothebest.thetowers.player.TheTowersPlayer;
import me.patothebest.thetowers.stats.TheTowersStatsModule;

public class TheTowersModule extends AbstractBukkitModule<TheTowersRemastered> {

    public TheTowersModule(TheTowersRemastered plugin) {
        super(plugin);
    }

    @Override
    protected void configure() {
        bind(TheTowersRemastered.class).toInstance(plugin);

        install(new FactoryModuleBuilder().implement(CorePlayer.class, TheTowersPlayer.class).build(PlayerFactory.class));
        install(new AnimationModule(plugin));
        install(new ArenaModule(plugin));
        install(new TheTowersStatsModule(plugin));
        install(new TheTowersPlaceholderModule(plugin));

        bind(CoreConfig.class).to(Config.class).in(Singleton.class);
        plugin.registerModule(Config.class); // for having the ability to reload config
        bind(Kit.class).annotatedWith(Names.named("DefaultKit")).to(KitDefault.class);

        registerModule(VaultHandler.class);
//        registerModule(LicenseManager.class);
        registerModule(CommandManager.class);
        registerModule(TheTowersSetupCommand.class);
        registerModule(TeamSetupCommands.Parent.class);
        registerModule(ProtectedAreasCommand.Parent.class);
        registerModule(ProjectileTrailsCommand.class);
        registerModule(LocaleManager.class);
        registerModule(FileManager.class);
        registerModule(DebugCommands.class);
        registerModule(CreateArenaCommand.class);
    }
}