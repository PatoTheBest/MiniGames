package me.patothebest.gamecore.stats;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.patothebest.gamecore.file.CoreConfig;
import me.patothebest.gamecore.logger.InjectLogger;
import me.patothebest.gamecore.logger.Logger;
import me.patothebest.gamecore.modules.ActivableModule;
import me.patothebest.gamecore.modules.ModuleName;
import me.patothebest.gamecore.modules.ModulePriority;
import me.patothebest.gamecore.modules.ReloadableModule;
import me.patothebest.gamecore.event.EventRegistry;
import me.patothebest.gamecore.util.Priority;

import java.util.Set;

@Singleton
@ModulePriority(priority = Priority.HIGH)
@ModuleName("Stats Manager")
public class StatsManager implements ActivableModule, ReloadableModule {

    private final CoreConfig coreConfig;
    private final Set<Statistic> statistics;
    private final EventRegistry registry;
    @InjectLogger private Logger logger;

    @Inject private StatsManager(CoreConfig coreConfig, Set<Statistic> statistics, EventRegistry registry) {
        this.coreConfig = coreConfig;
        this.statistics = statistics;
        this.registry = registry;
    }

    @Override
    public void onEnable() {
        for (Statistic statistic : statistics) {
            if(!coreConfig.getBoolean("stats." + statistic.getStatName())) {
                continue;
            }

            registry.registerListener(statistic);
            statistic.setEnabled(true);
            logger.config("Registered stat {0}", statistic.getStatName());
        }
    }

    public Statistic getStatisticByClass(Class<? extends Statistic> statClass) {
        for (Statistic statistic : statistics) {
            if(statistic.getClass() == statClass) {
                return statistic;
            }
        }

        throw new IllegalArgumentException("Stat " + statClass + " is not registered!");
    }

    public Statistic getStatisticByName(String name) {
        for (Statistic statistic : statistics) {
            if(statistic.getStatName().equalsIgnoreCase(name)) {
                return statistic;
            }
        }

        return null;
    }

    @Override
    public void onDisable() {
        for (Statistic statistic : statistics) {
            registry.unRegisterListener(statistic);
            statistic.setEnabled(false);
        }
    }

    @Override
    public void onReload() {
        onDisable();
        onEnable();
    }

    @Override
    public String getReloadName() {
        return "stats";
    }

    public Set<Statistic> getStatistics() {
        return statistics;
    }
}
