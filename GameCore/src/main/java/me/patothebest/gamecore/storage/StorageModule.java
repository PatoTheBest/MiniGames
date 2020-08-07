package me.patothebest.gamecore.storage;

import com.google.inject.multibindings.Multibinder;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.injector.AbstractBukkitModule;
import me.patothebest.gamecore.storage.flatfile.FlatFileEntity;
import me.patothebest.gamecore.storage.mysql.MySQLEntity;

public abstract class StorageModule extends AbstractBukkitModule<CorePlugin> {

    public StorageModule(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    protected void configure() {
        Multibinder<MySQLEntity> mySQLEntityMultibinder = Multibinder.newSetBinder(binder(), MySQLEntity.class);
        mySQLEntityMultibinder.addBinding().to(getSQLEntity());

        Multibinder<FlatFileEntity> flatFileEntityMultibinder = Multibinder.newSetBinder(binder(), FlatFileEntity.class);
        flatFileEntityMultibinder.addBinding().to(getFlatFileEntity());
    }

    protected abstract Class<? extends FlatFileEntity> getFlatFileEntity();
    protected abstract Class<? extends MySQLEntity> getSQLEntity();
}
