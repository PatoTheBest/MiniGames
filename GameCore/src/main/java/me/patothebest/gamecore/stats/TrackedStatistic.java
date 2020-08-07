package me.patothebest.gamecore.stats;

import me.patothebest.gamecore.util.Utils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TrackedStatistic {

    private final List<StatRecord> statRecords = new CopyOnWriteArrayList<>();

    public List<StatRecord> getStatRecords() {
        return statRecords;
    }

    public int getWeekly() {
        for (StatRecord statRecord : statRecords) {
            if(statRecord.getWeek() == Utils.getWeekOfTheYear()) {
                return statRecord.getStats();
            }
        }

        return 0;
    }

    public int getMonthly() {
        for (StatRecord statRecord : statRecords) {
            if(statRecord.getMonth() == Utils.getMonthOfTheYear()) {
                return statRecord.getStats();
            }
        }

        return 0;
    }

    public int getAllTime() {
        for (StatRecord statRecord : statRecords) {
            if(statRecord.getYear() == 0) {
                return statRecord.getStats();
            }
        }

        return 0;
    }
}
