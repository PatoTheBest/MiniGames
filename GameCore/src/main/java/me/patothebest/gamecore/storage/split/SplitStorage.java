package me.patothebest.gamecore.storage.split;

import com.google.inject.Injector;
import me.patothebest.gamecore.storage.flatfile.IFlatFileStorage;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.file.CoreConfig;
import me.patothebest.gamecore.kit.Kit;
import me.patothebest.gamecore.player.CorePlayer;
import me.patothebest.gamecore.storage.Storage;
import me.patothebest.gamecore.storage.StorageType;
import me.patothebest.gamecore.storage.mysql.IMySQLStorage;
import me.patothebest.gamecore.storage.none.NullStorage;
import me.patothebest.gamecore.util.Callback;
import org.bukkit.inventory.PlayerInventory;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.HashMap;
import java.util.Map;

public class SplitStorage implements Storage {

    private final Map<SplitType, Storage> splitTypeStorageMap = new HashMap<>();

    @Inject private SplitStorage(CoreConfig coreConfig, Provider<Injector> injector) {
        for (SplitType splitType : SplitType.values()) {
            StorageType storageType = Utils.getEnumValueFromString(StorageType.class, coreConfig.getString("split-storage." + splitType.name().toLowerCase()));

            if (storageType == null) {
                // unknown storage type
                // fallback into no storage type
                storageType = StorageType.NONE;
            }

            Storage storage = null;

            switch (storageType) {
                case FLATFILE:
                    storage = injector.get().getInstance(IFlatFileStorage.class);
                    break;
                case MYSQL:
                    storage = injector.get().getInstance(IMySQLStorage.class);
                    break;
                case SPLIT:
                    throw new IllegalArgumentException("Type cannot be split");
                case NONE:
                    storage = new NullStorage();
                    break;
            }

            splitTypeStorageMap.put(splitType, storage);
        }
    }

    @Override
    public void loadPlayers(Callback<CorePlayer> playerCallback) {
        splitTypeStorageMap.get(SplitType.PLAYERS).loadPlayers(playerCallback);
    }

    @Override
    public void load(CorePlayer player, boolean async) {
        splitTypeStorageMap.get(SplitType.PLAYERS).load(player, async);
    }

    @Override
    public void save(CorePlayer player) {
        splitTypeStorageMap.get(SplitType.PLAYERS).save(player);
    }

    @Override
    public void unCache(CorePlayer player) {
        splitTypeStorageMap.get(SplitType.PLAYERS).unCache(player);
    }

    @Override
    public void delete(CorePlayer player) {
        splitTypeStorageMap.get(SplitType.PLAYERS).delete(player);
    }

    @Override
    public void loadKits(Map<String, Kit> kitMap) {
        splitTypeStorageMap.get(SplitType.KITS).loadKits(kitMap);
    }

    @Override
    public Kit createKit(String name, PlayerInventory playerInventory) {
        return splitTypeStorageMap.get(SplitType.KITS).createKit(name, playerInventory);
    }

    @Override
    public void saveKits(Map<String, Kit> kitMap) {
        splitTypeStorageMap.get(SplitType.KITS).saveKits(kitMap);
    }

    @Override
    public void saveKit(Kit kit) {
        splitTypeStorageMap.get(SplitType.KITS).saveKit(kit);
    }

    @Override
    public void deleteKit(Kit kit) {
        splitTypeStorageMap.get(SplitType.KITS).deleteKit(kit);
    }

    @Override
    public void enableStorage() {
        splitTypeStorageMap.values().forEach(Storage::enableStorage);
    }

    @Override
    public void disableStorage() {
        splitTypeStorageMap.values().forEach(Storage::disableStorage);
    }

    @Override
    public void postEnable() {
        splitTypeStorageMap.values().forEach(Storage::postEnable);
    }

    @Override
    public void preDisableStorage() {
        splitTypeStorageMap.values().forEach(Storage::preDisableStorage);
    }

    public Map<SplitType, Storage> getSplitTypeStorageMap() {
        return splitTypeStorageMap;
    }
}
