package me.patothebest.thetowers.stats;

import com.google.inject.multibindings.Multibinder;
import me.patothebest.thetowers.stats.statistics.PointsScoredStatistic;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.injector.AbstractBukkitModule;
import me.patothebest.gamecore.stats.Statistic;

public class TheTowersStatsModule extends AbstractBukkitModule<CorePlugin> {

    public TheTowersStatsModule(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    protected void configure() {
        Multibinder<Statistic> statisticMultibinder = Multibinder.newSetBinder(binder(), Statistic.class);
        statisticMultibinder.addBinding().to(PointsScoredStatistic.class);
    }
}
