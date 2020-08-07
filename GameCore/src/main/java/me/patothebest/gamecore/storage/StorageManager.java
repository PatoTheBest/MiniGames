package me.patothebest.gamecore.storage;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.modules.ModuleName;
import me.patothebest.gamecore.storage.flatfile.IFlatFileStorage;
import me.patothebest.gamecore.storage.mysql.IMySQLStorage;
import me.patothebest.gamecore.storage.mysql.MySQLStorage;
import me.patothebest.gamecore.storage.none.NullStorage;
import me.patothebest.gamecore.storage.split.SplitStorage;
import me.patothebest.gamecore.storage.split.SplitType;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.file.CoreConfig;
import me.patothebest.gamecore.logger.InjectLogger;
import me.patothebest.gamecore.modules.ActivableModule;
import me.patothebest.gamecore.modules.ModulePriority;
import me.patothebest.gamecore.modules.ReloadableModule;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.util.Priority;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Singleton
@ModulePriority(priority = Priority.HIGH)
@ModuleName("Storage Manager")
public class StorageManager implements ActivableModule, ReloadableModule {

    private final CorePlugin plugin;
    private final CoreConfig coreConfig;
    private final Provider<PlayerManager> playerManager;
    private final Provider<Injector> injector;
    @InjectLogger private Logger logger;

    private Storage storage;
    private boolean useUUIDs;

    @Inject private StorageManager(CorePlugin plugin, CoreConfig coreConfig, Provider<PlayerManager> playerManagerProvider, Provider<Injector> injector) {
        this.plugin = plugin;
        this.coreConfig = coreConfig;
        this.playerManager = playerManagerProvider;
        this.injector = injector;
    }

    @Override
    public void onPreEnable() {
        StorageType storageType = Utils.getEnumValueFromString(StorageType.class, coreConfig.getString("storage.type"));
        useUUIDs = coreConfig.getBoolean("storage.use-uuids");

        if (storageType == null) {
            // unknown storage type
            // fallback into no storage type
            storageType = StorageType.NONE;
        }

        switch (storageType) {
            case FLATFILE:
                storage = injector.get().getInstance(IFlatFileStorage.class);
                break;
            case MYSQL:
                storage = injector.get().getInstance(IMySQLStorage.class);
                break;
            case SPLIT:
                storage = injector.get().getInstance(SplitStorage.class);
                break;
            case NONE:
                storage = new NullStorage();
                break;
        }
    }

    @Override
    public void onEnable() {
        storage.enableStorage();
        Bukkit.getOnlinePlayers().forEach(playerManager.get()::loadPlayer); // reload fix
    }

    @Override
    public void onPostEnable() {
        storage.postEnable();
    }

    @Override
    public void onDisable() {
        storage.preDisableStorage();
        Bukkit.getOnlinePlayers().forEach(playerManager.get()::destroyPlayer); // reload fix
        storage.disableStorage();
    }

    @Override
    public void onReload() {
        reloadStorage();
    }

    @Override
    public String getReloadName() {
        return "storage";
    }

    public void reloadStorage() {
        reloadStorage(false);
    }

    public void reloadStorage(boolean becauseOfFailSafe) {
        Map<Player, IPlayer> tempMap = new HashMap<>();
        Bukkit.getOnlinePlayers().forEach(player -> tempMap.put(player, playerManager.get().getPlayer(player)));

        onDisable();
        if(becauseOfFailSafe) {
            storage = new NullStorage();
        } else {
            onPreEnable();
        }

        onEnable();

        for (Player player : Bukkit.getOnlinePlayers()) {
            IPlayer oldPlayer = tempMap.get(player);
            IPlayer newPlayer = playerManager.get().getPlayer(player);

            if(oldPlayer == null) {
                continue;
            }

            if (oldPlayer.getKit() != null) {
                newPlayer.setKit(oldPlayer.getKit());
            }

            if (oldPlayer.getCurrentArena() != null) {
                newPlayer.setCurrentArena(oldPlayer.getCurrentArena());

                if (oldPlayer.getGameTeam() != null) {
                    newPlayer.setGameTeam(oldPlayer.getGameTeam());
                }

                newPlayer.reinitializeAfterReload();
            }
        }

        onPostEnable();
    }

    /**
     * This boolean will return true for players that need saving when
     * they leave the server.
     * <p>
     * If the storage engine is a database (mysql), all the data is being
     * saved live, meaning all changes are saved instantly on the database,
     * so there will be no need in saving the data again. This is for many
     * reasons, the big one is to prevent race conditions with other servers.
     * For example, player joins Server A, the data is loaded on join. If the
     * data was to be saved on quit, sometimes, if the player joins Server B,
     * the data would load before it was saved from Server A, which led to data
     * loss.
     * <p>
     * If the storage engine is flatfile, then there is no need to worry about
     * race conditions, so we can safely load the data on join and save the data
     * on quit.
     *
     * @return true if the player needs saving
     */
    public boolean doPlayersNeedSaving() {
        if(storage instanceof IFlatFileStorage) {
            return true;
        }

        if(storage instanceof SplitStorage) {
            return ((SplitStorage) storage).getSplitTypeStorageMap().get(SplitType.PLAYERS) instanceof IFlatFileStorage;
        }

        return false;
    }

    public boolean arePlayersOnDatabase() {
        return !doPlayersNeedSaving();
    }

    public Storage getStorage() {
        return storage;
    }

    public MySQLStorage getMySQLStorage() {
        if(storage instanceof MySQLStorage) {
            return (MySQLStorage) storage;
        }

        if(storage instanceof SplitStorage) {
            if(((SplitStorage)storage).getSplitTypeStorageMap().get(SplitType.PLAYERS) instanceof MySQLStorage) {
                return (MySQLStorage) ((SplitStorage)storage).getSplitTypeStorageMap().get(SplitType.PLAYERS);
            }

            if(((SplitStorage)storage).getSplitTypeStorageMap().get(SplitType.KITS) instanceof MySQLStorage) {
                return (MySQLStorage) ((SplitStorage)storage).getSplitTypeStorageMap().get(SplitType.KITS);
            }
        }

        return null;
    }

    public boolean isUseUUIDs() {
        return useUUIDs;
    }

    public CorePlugin getPlugin() {
        return plugin;
    }
}
