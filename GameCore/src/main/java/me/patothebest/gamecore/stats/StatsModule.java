package me.patothebest.gamecore.stats;

import com.google.inject.multibindings.Multibinder;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.stats.statistics.BlocksPlacedStatistic;
import me.patothebest.gamecore.stats.statistics.DeathStatistic;
import me.patothebest.gamecore.stats.statistics.KillStatistic;
import me.patothebest.gamecore.stats.statistics.LosesStatistic;
import me.patothebest.gamecore.stats.statistics.WinsStatistic;
import me.patothebest.gamecore.stats.storage.StatsFlatFileEntity;
import me.patothebest.gamecore.stats.storage.StatsMySQLEntity;
import me.patothebest.gamecore.storage.StorageModule;
import me.patothebest.gamecore.storage.flatfile.FlatFileEntity;
import me.patothebest.gamecore.storage.mysql.MySQLEntity;

public class StatsModule extends StorageModule {

    public StatsModule(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    protected void configure() {
        super.configure();

        registerModule(StatsManager.class);
        registerModule(StatsCommand.class);

        Multibinder<Statistic> statisticMultibinder = Multibinder.newSetBinder(binder(), Statistic.class);
        statisticMultibinder.addBinding().to(DeathStatistic.class);
        statisticMultibinder.addBinding().to(BlocksPlacedStatistic.class);
        statisticMultibinder.addBinding().to(KillStatistic.class);
        statisticMultibinder.addBinding().to(LosesStatistic.class);
        statisticMultibinder.addBinding().to(WinsStatistic.class);
    }

    @Override
    protected Class<? extends FlatFileEntity> getFlatFileEntity() {
        return StatsFlatFileEntity.class;
    }

    @Override
    protected Class<? extends MySQLEntity> getSQLEntity() {
        return StatsMySQLEntity.class;
    }
}
