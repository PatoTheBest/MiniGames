package me.patothebest.gamecore.elo;

import me.patothebest.gamecore.elo.entities.EloFlatFileEntity;
import me.patothebest.gamecore.elo.entities.EloMySQLEntity;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.storage.StorageModule;
import me.patothebest.gamecore.storage.flatfile.FlatFileEntity;
import me.patothebest.gamecore.storage.mysql.MySQLEntity;

public class EloModule extends StorageModule {

    public EloModule(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    protected void configure() {
        super.configure();

        registerModule(EloManager.class);
    }

    @Override
    protected Class<? extends FlatFileEntity> getFlatFileEntity() {
        return EloFlatFileEntity.class;
    }

    @Override
    protected Class<? extends MySQLEntity> getSQLEntity() {
        return EloMySQLEntity.class;
    }
}
