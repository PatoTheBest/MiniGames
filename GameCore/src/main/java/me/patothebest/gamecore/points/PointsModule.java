package me.patothebest.gamecore.points;

import com.google.inject.multibindings.Multibinder;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.injector.AbstractBukkitModule;
import me.patothebest.gamecore.points.entities.PointsFlatFileEntity;
import me.patothebest.gamecore.points.entities.PointsMySQLEntity;
import me.patothebest.gamecore.storage.flatfile.FlatFileEntity;
import me.patothebest.gamecore.storage.mysql.MySQLEntity;
import me.patothebest.gamecore.stats.Statistic;

public class PointsModule extends AbstractBukkitModule<CorePlugin> {

    public PointsModule(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    protected void configure() {
        Multibinder<MySQLEntity> mySQLEntityMultibinder = Multibinder.newSetBinder(binder(), MySQLEntity.class);
        mySQLEntityMultibinder.addBinding().to(PointsMySQLEntity.class);

        Multibinder<FlatFileEntity> flatFileEntityMultibinder = Multibinder.newSetBinder(binder(), FlatFileEntity.class);
        flatFileEntityMultibinder.addBinding().to(PointsFlatFileEntity.class);

        Multibinder<Statistic> statisticMultibinder = Multibinder.newSetBinder(binder(), Statistic.class);
        statisticMultibinder.addBinding().to(PointsStatistic.class);

        registerModule(PointsManager.class);
    }
}
