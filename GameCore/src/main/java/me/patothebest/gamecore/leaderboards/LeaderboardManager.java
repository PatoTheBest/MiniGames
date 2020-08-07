package me.patothebest.gamecore.leaderboards;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.patothebest.gamecore.modules.ModuleName;
import me.patothebest.gamecore.event.EventRegistry;
import me.patothebest.gamecore.event.other.LeaderboardUpdateEvent;
import me.patothebest.gamecore.file.CoreConfig;
import me.patothebest.gamecore.logger.InjectLogger;
import me.patothebest.gamecore.modules.ActivableModule;
import me.patothebest.gamecore.modules.ReloadableModule;
import me.patothebest.gamecore.scheduler.PluginScheduler;
import me.patothebest.gamecore.stats.StatPeriod;
import me.patothebest.gamecore.stats.Statistic;
import me.patothebest.gamecore.stats.StatsManager;
import me.patothebest.gamecore.storage.StorageManager;
import me.patothebest.gamecore.storage.mysql.MySQLStorage;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Logger;

@Singleton
@ModuleName("Leaderboard Manager")
public class LeaderboardManager implements ActivableModule, Runnable, ReloadableModule {

    private final Map<Class<? extends Statistic>, Map<StatPeriod, LinkedList<TopEntry>>> top10Map = new HashMap<>();
    private final CoreConfig coreConfig;
    private final EventRegistry eventRegistry;
    private final StorageManager storageManager;
    private final PluginScheduler pluginScheduler;
    private final StatsManager statsManager;
    private BukkitTask currentTask;
    @InjectLogger private Logger logger;
    private boolean hasLoadedAll;

    @Inject private LeaderboardManager(CoreConfig coreConfig, EventRegistry eventRegistry, StorageManager storageManager, PluginScheduler pluginScheduler, StatsManager statsManager) {
        this.coreConfig = coreConfig;
        this.eventRegistry = eventRegistry;
        this.storageManager = storageManager;
        this.pluginScheduler = pluginScheduler;
        this.statsManager = statsManager;
    }

    @Override
    public void onPreEnable() {
        for (Statistic statistic : statsManager.getStatistics()) {
            Map<StatPeriod, LinkedList<TopEntry>> topEntryMap = new HashMap<>();
            for (StatPeriod value : StatPeriod.values()) {
                topEntryMap.put(value, new LinkedList<>());
            }
            top10Map.put(statistic.getClass(), topEntryMap);
        }

    }

    @Override
    public void onPostEnable() {
        if (!storageManager.arePlayersOnDatabase()) {
            return;
        }

        if(!coreConfig.getBoolean("leaderboard.enabled")) {
            return;
        }

        hasLoadedAll = false;
        int interval = 20 * 60 * coreConfig.getInt("leaderboard.interval");
        currentTask = pluginScheduler.runTaskTimerAsynchronously(this, 0L, interval); // run every x minutes
    }

    @Override
    public void run() {
        MySQLStorage mySQLStorage = storageManager.getMySQLStorage();

        logger.fine("Getting all stats...");
        mySQLStorage.getConnectionHandler().executeSQLQuery(connection -> {
            for (Statistic statistic : statsManager.getStatistics()) {
                logger.finer("Getting stat " + statistic.getStatName());
                if(statistic.hasWeeklyAndMonthlyStats()) {
                    logger.finest("Getting weekly stat " + statistic.getStatName());
                    LinkedList<TopEntry> top10Weekly = getTop10Map().get(statistic.getClass()).get(StatPeriod.WEEK);
                    try {
                        LinkedList<TopEntry> top10Weekly1 = statistic.getTop10Weekly(connection);
                        top10Weekly.clear();
                        top10Weekly.addAll(top10Weekly1);
                    } catch (Throwable e) {
                        if (connection.isClosed()) {
                            logger.warning("Aborting leaderboard stat collection because connection was closed. (Reload?)");
                            return;
                        }

                        logger.severe("Could not get weekly stats " + statistic.getStatName() + "!");
                        e.printStackTrace();
                    }

                    logger.finest("Getting monthly stat " + statistic.getStatName());
                    LinkedList<TopEntry> top10Monthly = getTop10Map().get(statistic.getClass()).get(StatPeriod.MONTH);
                    try {
                        LinkedList<TopEntry> top10Monthly1 = statistic.getTop10Monthly(connection);
                        top10Monthly.clear();
                        top10Monthly.addAll(top10Monthly1);
                    } catch (Throwable e) {
                        if (connection.isClosed()) {
                            logger.warning("Aborting leaderboard stat collection because connection was closed. (Reload?)");
                            return;
                        }

                        logger.severe("Could not get monthly stats " + statistic.getStatName() + "!");
                        e.printStackTrace();
                    }
                }

                logger.finest("Getting all time stat " + statistic.getStatName());
                LinkedList<TopEntry> top10AllTime = getTop10Map().get(statistic.getClass()).get(StatPeriod.ALL);
                try {
                    LinkedList<TopEntry> top10AllTime1 = statistic.getTop10AllTime(connection);
                    top10AllTime.clear();
                    top10AllTime.addAll(top10AllTime1);
                } catch (Throwable e) {
                    if (connection.isClosed()) {
                        logger.warning("Aborting leaderboard stat collection because connection was closed. (Reload?)");
                        return;
                    }

                    logger.severe("Could not get all time stats " + statistic.getStatName() + "!");
                    e.printStackTrace();
                }
            }
        }, false);

        eventRegistry.callEvent(new LeaderboardUpdateEvent());
        hasLoadedAll = true;
        logger.fine("Got all stats!");
    }

    @Override
    public void onDisable() {
        if(currentTask != null) {
            currentTask.cancel();
        }
    }

    @Override
    public void onReload() {
        onDisable();
        top10Map.clear();
        onPreEnable();
        pluginScheduler.runTaskAsynchronously(this::onPostEnable);
    }

    @Override
    public String getReloadName() {
        return "leaderboard";
    }

    public Map<Class<? extends Statistic>, Map<StatPeriod, LinkedList<TopEntry>>> getTop10Map() {
        return top10Map;
    }

    public boolean hasLoadedAll() {
        return hasLoadedAll;
    }
}
