package me.patothebest.gamecore;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.OptionalBinder;
import me.patothebest.gamecore.addon.AddonModule;
import me.patothebest.gamecore.animation.CoreAnimationModule;
import me.patothebest.gamecore.arena.ArenaGroup;
import me.patothebest.gamecore.arena.modes.bungee.AdvancedBungeeMode;
import me.patothebest.gamecore.arena.modes.bungee.AdvancedBungeeModeCommand;
import me.patothebest.gamecore.arena.modes.bungee.BungeeHandler;
import me.patothebest.gamecore.arena.modes.bungee.BungeeMode;
import me.patothebest.gamecore.arena.modes.bungee.BungeeModeCommand;
import me.patothebest.gamecore.arena.modes.random.RandomArenaModeModule;
import me.patothebest.gamecore.arena.option.OptionCommand;
import me.patothebest.gamecore.block.BlockRestorer;
import me.patothebest.gamecore.combat.CombatModule;
import me.patothebest.gamecore.command.CommandsManager;
import me.patothebest.gamecore.command.impl.CommandManager;
import me.patothebest.gamecore.commands.ConfirmCommand;
import me.patothebest.gamecore.commands.DebugCommand;
import me.patothebest.gamecore.commands.admin.AdminArenaCommands;
import me.patothebest.gamecore.commands.admin.GeneralAdminCommand;
import me.patothebest.gamecore.commands.admin.RenameCommand;
import me.patothebest.gamecore.cosmetics.projectiletrails.ProjectileManager;
import me.patothebest.gamecore.cosmetics.projectiletrails.ProjectileTracker;
import me.patothebest.gamecore.cosmetics.victoryeffects.VictoryManager;
import me.patothebest.gamecore.cosmetics.walkparticles.WalkTrailsManager;
import me.patothebest.gamecore.encouragement.EncouragementManager;
import me.patothebest.gamecore.event.EventRegistry;
import me.patothebest.gamecore.experience.ExperienceModule;
import me.patothebest.gamecore.file.PluginHookFile;
import me.patothebest.gamecore.ghost.GhostFactory;
import me.patothebest.gamecore.guis.GUIModule;
import me.patothebest.gamecore.injector.AbstractBukkitModule;
import me.patothebest.gamecore.kit.KitManager;
import me.patothebest.gamecore.logger.LoggerModule;
import me.patothebest.gamecore.menu.MenuModule;
import me.patothebest.gamecore.nms.NMSModule;
import me.patothebest.gamecore.permission.PermissionGroupManager;
import me.patothebest.gamecore.placeholder.PlaceHolderModule;
import me.patothebest.gamecore.player.PlayerSkinCache;
import me.patothebest.gamecore.pluginhooks.PluginHooksModule;
import me.patothebest.gamecore.points.PointsModule;
import me.patothebest.gamecore.privatearenas.PrivateArenasModule;
import me.patothebest.gamecore.scheduler.PluginScheduler;
import me.patothebest.gamecore.scoreboard.ScoreboardFile;
import me.patothebest.gamecore.scoreboard.ScoreboardManager;
import me.patothebest.gamecore.selection.SelectionManager;
import me.patothebest.gamecore.sign.SignModule;
import me.patothebest.gamecore.storage.CoreStorageModule;
import me.patothebest.gamecore.storage.Storage;
import me.patothebest.gamecore.storage.StorageManager;
import me.patothebest.gamecore.treasure.TreasureModule;
import me.patothebest.gamecore.world.WorldHandler;
import me.patothebest.gamecore.world.WorldManager;
import org.bukkit.command.CommandSender;

import java.util.Set;

public class CoreModule<T extends CorePlugin> extends AbstractBukkitModule<T> {

    public CoreModule(T abstractJavaPlugin) {
        super(abstractJavaPlugin);
    }

    @Override
    protected void configure() {
        bind(CommandManager.class).toInstance(plugin.getCommandManager());
        bind(new TypeLiteral<CommandsManager<CommandSender>>() {}).toInstance(plugin.getCommandManager().getCommandManager());

        // default arena groups, this can be overwritten
        OptionalBinder.newOptionalBinder(binder(), new TypeLiteral<Set<ArenaGroup>>() {})
                .setDefault().toInstance(ImmutableSet.of(ArenaGroup.DEFAULT_GROUP));

        install(new LoggerModule(plugin));
        install(new CoreStorageModule(plugin));
        install(new CoreAnimationModule(plugin));
        install(new PluginHooksModule(plugin));
        install(new PlaceHolderModule(plugin));
        install(new NMSModule(plugin));
        install(new AddonModule(plugin));
        install(new SignModule(plugin));
        install(new GUIModule(plugin));
        install(new TreasureModule(plugin));
        install(new PointsModule(plugin));
        install(new CombatModule(plugin));
        install(new MenuModule(plugin));
        install(new RandomArenaModeModule(plugin));
        install(new PrivateArenasModule(plugin));
        install(new ExperienceModule(plugin));

//        registerModule(LeakListener.class);

        registerModule(PlayerSkinCache.class);
        registerModule(PermissionGroupManager.class);
        registerModule(KitManager.class);
        registerModule(DebugCommand.class);
        registerModule(ConfirmCommand.class);
        registerModule(ScoreboardFile.class);
        registerModule(ScoreboardManager.class);
        registerModule(BlockRestorer.class);
        registerModule(BungeeMode.class);
        registerModule(AdvancedBungeeMode.class);
        registerModule(AdvancedBungeeModeCommand.Parent.class);
        registerModule(BungeeHandler.class);
        registerModule(RenameCommand.class);
        registerModule(EventRegistry.class);
        registerModule(GhostFactory.class);
        registerModule(BungeeModeCommand.class);
        registerModule(GeneralAdminCommand.class);
        registerModule(AdminArenaCommands.class);
        registerModule(ProjectileManager.class);
        registerModule(ProjectileTracker.class);
        registerModule(WalkTrailsManager.class);
        registerModule(VictoryManager.class);
        registerModule(OptionCommand.Parent.class);
        registerModule(WorldManager.class);
        registerModule(EncouragementManager.class);
        registerModule(PluginScheduler.class);
        registerModule(PluginHookFile.class);
    }

    @Provides public SelectionManager getSelectionManager(CorePlugin plugin) {
        return plugin.getSelectionManager();
    }

    @Provides public WorldHandler getWorldHandler(CorePlugin plugin) {
        return plugin.getWorldHandler();
    }

    @Provides public Storage getStorage(StorageManager storageManager) {
        return storageManager.getStorage();
    }
}