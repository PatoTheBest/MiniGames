package me.patothebest.gamecore.storage.converter;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import me.patothebest.gamecore.kit.Kit;
import me.patothebest.gamecore.modules.Module;
import me.patothebest.gamecore.storage.Storage;
import me.patothebest.gamecore.storage.StorageType;
import me.patothebest.gamecore.storage.flatfile.IFlatFileStorage;
import me.patothebest.gamecore.storage.mysql.IMySQLStorage;
import me.patothebest.gamecore.storage.split.SplitType;

import java.util.HashMap;
import java.util.Map;

import static me.patothebest.gamecore.storage.StorageType.*;

public class StorageConverter implements Module {

    private final Provider<Injector> injector;

    @Inject private StorageConverter(Provider<Injector> injector) {
        this.injector = injector;
    }

    public void convert(SplitType splitType, StorageType sourceStorageType, StorageType destinationStorageType) {
        if(sourceStorageType != StorageType.FLATFILE && sourceStorageType != MYSQL) {
            throw new IllegalArgumentException("Storage type must be either MySQL or FlatFile!");
        }

        if(destinationStorageType != StorageType.FLATFILE && destinationStorageType != MYSQL) {
            throw new IllegalArgumentException("Storage type must be either MySQL or FlatFile!");
        }

        if(destinationStorageType == sourceStorageType) {
            throw new IllegalArgumentException("Source and destination storage type cannot be the same!");
        }

        Storage sourceStorage = getStorage(sourceStorageType);
        Storage destinationStorage = getStorage(destinationStorageType);

        sourceStorage.enableStorage();
        destinationStorage.enableStorage();

        if(splitType == SplitType.KITS) {
            Map<String, Kit> kits = new HashMap<>();
            sourceStorage.loadKits(kits);
            destinationStorage.saveKits(kits);
        } else {
            // TODO: finish this
            sourceStorage.loadPlayers(player -> {
                destinationStorage.save(player);
                destinationStorage.unCache(player);
            });
        }

        sourceStorage.preDisableStorage();
        sourceStorage.disableStorage();
        destinationStorage.preDisableStorage();
        destinationStorage.disableStorage();
    }

    private Storage getStorage(StorageType storageType) {
        switch (storageType) {
            case FLATFILE:
                return injector.get().getInstance(IFlatFileStorage.class);
            case MYSQL:
                return injector.get().getInstance(IMySQLStorage.class);
        }

        throw new IllegalStateException("Unhandled storage type " + storageType);
    }
}
