package me.patothebest.skywars.stats;

import com.google.inject.multibindings.Multibinder;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.injector.AbstractBukkitModule;
import me.patothebest.gamecore.stats.Statistic;
import me.patothebest.skywars.stats.statistics.solo.SoloDeathStatistic;
import me.patothebest.skywars.stats.statistics.solo.SoloKillStatistic;
import me.patothebest.skywars.stats.statistics.solo.SoloLosesStatistic;
import me.patothebest.skywars.stats.statistics.solo.SoloWinsStatistic;
import me.patothebest.skywars.stats.statistics.team.TeamDeathStatistic;
import me.patothebest.skywars.stats.statistics.team.TeamKillStatistic;
import me.patothebest.skywars.stats.statistics.team.TeamLosesStatistic;
import me.patothebest.skywars.stats.statistics.team.TeamWinsStatistic;

public class StatsModule extends AbstractBukkitModule<CorePlugin> {

    public StatsModule(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    protected void configure() {
        Multibinder<Statistic> statisticMultibinder = Multibinder.newSetBinder(binder(), Statistic.class);

        // Solo
        statisticMultibinder.addBinding().to(SoloDeathStatistic.class);
        statisticMultibinder.addBinding().to(SoloKillStatistic.class);
        statisticMultibinder.addBinding().to(SoloLosesStatistic.class);
        statisticMultibinder.addBinding().to(SoloWinsStatistic.class);

        // Team
        statisticMultibinder.addBinding().to(TeamDeathStatistic.class);
        statisticMultibinder.addBinding().to(TeamKillStatistic.class);
        statisticMultibinder.addBinding().to(TeamLosesStatistic.class);
        statisticMultibinder.addBinding().to(TeamWinsStatistic.class);
    }
}
