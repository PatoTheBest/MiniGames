package me.patothebest.gamecore.storage;

import com.google.inject.multibindings.Multibinder;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.cosmetics.shop.ShopModule;
import me.patothebest.gamecore.injector.AbstractBukkitModule;
import me.patothebest.gamecore.storage.converter.ConverterCommand;
import me.patothebest.gamecore.storage.flatfile.FlatFileEntity;
import me.patothebest.gamecore.storage.flatfile.FlatFileStorage;
import me.patothebest.gamecore.storage.flatfile.IFlatFileStorage;
import me.patothebest.gamecore.storage.mysql.IMySQLStorage;
import me.patothebest.gamecore.storage.mysql.MySQLEntity;
import me.patothebest.gamecore.storage.mysql.MySQLStorage;
import me.patothebest.gamecore.kit.KitModule;
import me.patothebest.gamecore.leaderboards.LeaderboardModule;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.stats.StatsModule;

public class CoreStorageModule extends AbstractBukkitModule<CorePlugin> {

    public CoreStorageModule(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    protected void configure() {
        // core storage implementations
        Multibinder.newSetBinder(binder(), MySQLEntity.class);
        Multibinder.newSetBinder(binder(), FlatFileEntity.class);
        bind(IMySQLStorage.class).to(MySQLStorage.class);
        bind(IFlatFileStorage.class).to(FlatFileStorage.class);

        install(new KitModule(plugin));
        install(new StatsModule(plugin));
        install(new ShopModule(plugin));
        install(new LeaderboardModule(plugin));

        registerModule(StorageManager.class);
        registerModule(ConverterCommand.class);
        registerModule(PlayerManager.class);
    }
}
