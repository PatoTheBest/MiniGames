package me.patothebest.gamecore.stats;

import me.patothebest.gamecore.util.SerializableObject;

import java.util.Map;

public class StatRecord implements SerializableObject {

    private int entryId;
    private final int playerId;
    private final String statName;
    private int stats;
    private int week;
    private int month;
    private int year;

    public StatRecord(int entryId, int playerId, String statName) {
        this.entryId = entryId;
        this.playerId = playerId;
        this.statName = statName;
    }

    public StatRecord(int playerId, Map<String, Object> data) {
        this.entryId = -1;
        this.playerId = playerId;
        this.statName = (String) data.get("stat-name");
        this.stats = (int) data.get("stats");
        this.week = (int) data.get("week");
        this.month = (int) data.get("month");
        this.year = (int) data.get("year");
    }

    public int getEntryId() {
        return entryId;
    }

    public void setEntryId(int entryId) {
        this.entryId = entryId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getStats() {
        return stats;
    }

    public void setStats(int stats) {
        this.stats = stats;
    }

    public void addStats(int amount) {
        this.stats += amount;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getStatName() {
        return statName;
    }

    @Override
    public void serialize(Map<String, Object> data) {
        data.put("stat-name", statName);
        data.put("stats", stats);
        data.put("week", week);
        data.put("month", month);
        data.put("year", year);
    }
}
