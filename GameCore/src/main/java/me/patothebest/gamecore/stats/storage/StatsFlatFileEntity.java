package me.patothebest.gamecore.stats.storage;

import com.google.inject.Inject;
import me.patothebest.gamecore.player.CorePlayer;
import me.patothebest.gamecore.storage.StorageException;
import me.patothebest.gamecore.storage.flatfile.FlatFileEntity;
import me.patothebest.gamecore.storage.flatfile.PlayerProfileFile;
import me.patothebest.gamecore.stats.StatRecord;
import me.patothebest.gamecore.stats.Statistic;
import me.patothebest.gamecore.stats.StatsManager;
import me.patothebest.gamecore.stats.TrackedStatistic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsFlatFileEntity implements FlatFileEntity {

    private final StatsManager statsManager;

    @Inject private StatsFlatFileEntity(StatsManager statsManager) {
        this.statsManager = statsManager;
    }

    /**
     * Loads a player's stats
     *
     * @param player            the player loaded
     * @param playerProfileFile the player's profile file
     */
    @SuppressWarnings("unchecked")
    @Override
    public void loadPlayer(CorePlayer player, PlayerProfileFile playerProfileFile) throws StorageException {
        if (!playerProfileFile.isSet("stats")) {
            return;
        }

        if (true) {return; } // TODO: FIX

        Map<String, List<Map<String, Object>>> stats = (Map<String, List<Map<String, Object>>>) playerProfileFile.get("stats");
        stats.forEach((statType, records) -> {
            Statistic statistic = statsManager.getStatisticByName(statType);
            TrackedStatistic trackedStatistic = new TrackedStatistic();
            player.getStatistics().put(statistic.getClass(), trackedStatistic);

            for (Map<String, Object> record : records) {
                StatRecord statRecord = new StatRecord(player.getPlayerId(), record);
                trackedStatistic.getStatRecords().add(statRecord);
            }
        });
    }

    /**
     * Saves a player's stats
     *
     * @param player            the player being saved
     * @param playerProfileFile the player's profile file
     */
    @Override
    public void savePlayer(CorePlayer player, PlayerProfileFile playerProfileFile) throws StorageException {
        Map<String, List<Map<String, Object>>> statMap = new HashMap<>();

        player.getStatistics().forEach((statisticType, trackedStatistic) -> {
            List<Map<String, Object>> specificStats = new ArrayList<>();

            for (StatRecord statRecord : trackedStatistic.getStatRecords()) {
                specificStats.add(statRecord.serialize());
            }

            statMap.put(statsManager.getStatisticByClass(statisticType).getStatName(), specificStats);
        });

        if (!statMap.isEmpty()) {
            playerProfileFile.set("stats", statMap);
        }
    }
}
