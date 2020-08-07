package me.patothebest.gamecore.cosmetics.shop;

import me.patothebest.gamecore.storage.StorageModule;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.cosmetics.shop.entities.ShopFlatFileEntity;
import me.patothebest.gamecore.cosmetics.shop.entities.ShopMySQLEntity;
import me.patothebest.gamecore.storage.flatfile.FlatFileEntity;
import me.patothebest.gamecore.storage.mysql.MySQLEntity;

public class ShopModule extends StorageModule {

    public ShopModule(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    protected void configure() {
        super.configure();
        registerModule(ShopRegistry.class);
        registerModule(ShopCommand.Parent.class);
    }

    @Override
    protected Class<? extends FlatFileEntity> getFlatFileEntity() {
        return ShopFlatFileEntity.class;
    }

    @Override
    protected Class<? extends MySQLEntity> getSQLEntity() {
        return ShopMySQLEntity.class;
    }
}
