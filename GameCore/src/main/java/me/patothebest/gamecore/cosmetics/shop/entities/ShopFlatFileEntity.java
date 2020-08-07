package me.patothebest.gamecore.cosmetics.shop.entities;

import com.google.inject.Inject;
import me.patothebest.gamecore.cosmetics.shop.ShopRegistry;
import me.patothebest.gamecore.player.CorePlayer;
import me.patothebest.gamecore.storage.StorageException;
import me.patothebest.gamecore.storage.flatfile.FlatFileEntity;
import me.patothebest.gamecore.storage.flatfile.PlayerProfileFile;

public class ShopFlatFileEntity implements FlatFileEntity {

    private final ShopRegistry shopRegistry;

    @Inject private ShopFlatFileEntity(ShopRegistry shopRegistry) {
        this.shopRegistry = shopRegistry;
    }

    @Override
    public void loadPlayer(CorePlayer player, PlayerProfileFile playerProfileFile) throws StorageException {

    }

    @Override
    public void savePlayer(CorePlayer player, PlayerProfileFile playerProfileFile) throws StorageException {

    }
}
