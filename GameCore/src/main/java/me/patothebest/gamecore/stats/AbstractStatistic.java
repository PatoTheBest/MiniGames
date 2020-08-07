package me.patothebest.gamecore.stats;

import com.google.inject.Inject;
import me.patothebest.gamecore.leaderboards.LeaderboardQueries;
import me.patothebest.gamecore.leaderboards.TopEntry;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.player.modifiers.StatsModifier;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.event.EventRegistry;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

@SuppressWarnings("Duplicates")
public abstract class AbstractStatistic implements Statistic {

    @Inject protected PlayerManager playerManager;
    @Inject private EventRegistry registry;

    private boolean enabled;

    protected IPlayer getPlayer(Player player) {
        return playerManager.getPlayer(player);
    }

    @Override
    public void updateStat(Player player, int amount) {
        updateStat(getPlayer(player), amount);
    }

    @Override
    public void updateStat(IPlayer player, int amount) {
        if(!enabled) {
            return;
        }

        if (player.getCurrentArena() != null && player.getCurrentArena().isDisableStats()) {
            return;
        }

        if(!player.getStatistics().containsKey(getClass())) {
            player.getStatistics().put(getClass(), new TrackedStatistic());
        }

        TrackedStatistic trackedStatistic = player.getStatistics().get(getClass());

        StatRecord allTimeRecord = null;
        StatRecord thisWeekRecord = null;

        for (StatRecord statRecord : trackedStatistic.getStatRecords()) {
            if(statRecord.getYear() == 0) {
                allTimeRecord = statRecord;
                continue;
            }

            if(statRecord.getWeek() == Utils.getWeekOfTheYear()) {
                thisWeekRecord = statRecord;
            }
        }

        if(allTimeRecord == null && thisWeekRecord == null) {
            allTimeRecord = new StatRecord(-1, player.getPlayerId(), getStatName());
            allTimeRecord.setYear(0);
            allTimeRecord.setMonth(0);
            allTimeRecord.setWeek(0);
            allTimeRecord.setStats(amount);
            trackedStatistic.getStatRecords().add(allTimeRecord);

            thisWeekRecord = new StatRecord(-1, player.getPlayerId(), getStatName());
            thisWeekRecord.setYear(Utils.getYear());
            thisWeekRecord.setMonth(Utils.getMonthOfTheYear());
            thisWeekRecord.setWeek(Utils.getWeekOfTheYear());
            thisWeekRecord.setStats(amount);
            trackedStatistic.getStatRecords().add(thisWeekRecord);
            player.notifyObservers(StatsModifier.INSERT, allTimeRecord, thisWeekRecord);
        } else if(allTimeRecord == null) {
            allTimeRecord = new StatRecord(-1, player.getPlayerId(), getStatName());
            allTimeRecord.setYear(0);
            allTimeRecord.setMonth(0);
            allTimeRecord.setWeek(0);
            allTimeRecord.setStats(amount);
            trackedStatistic.getStatRecords().add(allTimeRecord);
            player.notifyObservers(StatsModifier.INSERT, allTimeRecord);

            thisWeekRecord.addStats(amount);
            player.notifyObservers(StatsModifier.UPDATE_ADD, amount, thisWeekRecord);
        } else if(thisWeekRecord == null) {
            thisWeekRecord = new StatRecord(-1, player.getPlayerId(), getStatName());
            thisWeekRecord.setYear(Utils.getYear());
            thisWeekRecord.setMonth(Utils.getMonthOfTheYear());
            thisWeekRecord.setWeek(Utils.getWeekOfTheYear());
            thisWeekRecord.setStats(amount);
            trackedStatistic.getStatRecords().add(thisWeekRecord);
            player.notifyObservers(StatsModifier.INSERT, thisWeekRecord);

            allTimeRecord.addStats(amount);
            player.notifyObservers(StatsModifier.UPDATE_ADD, amount, allTimeRecord);
        } else {
            thisWeekRecord.addStats(amount);
            allTimeRecord.addStats(amount);
            player.notifyObservers(StatsModifier.UPDATE_ADD, amount, allTimeRecord, thisWeekRecord);
        }

        registry.callEvent(new StatsUpdateEvent(player, this, amount));
    }

    @Override
    public LinkedList<TopEntry> getTop10Weekly(Connection connection) throws SQLException {
        PreparedStatement selectTop10Weekly = connection.prepareStatement(LeaderboardQueries.SELECT_WEEKLY);
        selectTop10Weekly.setString(1, getStatName());
        selectTop10Weekly.setInt(2, Utils.getWeekOfTheYear());
        selectTop10Weekly.setInt(3, Utils.getYear());
        ResultSet selectTop10WeeklyResults = selectTop10Weekly.executeQuery();
        LinkedList<TopEntry> top10Weekly = new LinkedList<>();

        while (selectTop10WeeklyResults.next()) {
            top10Weekly.add(new TopEntry(selectTop10WeeklyResults.getString("name"), selectTop10WeeklyResults.getInt("stat")));
        }

        selectTop10WeeklyResults.close();
        selectTop10Weekly.close();
        return top10Weekly;
    }

    @Override
    public LinkedList<TopEntry> getTop10Monthly(Connection connection) throws SQLException {
        PreparedStatement selectTop10Monthly = connection.prepareStatement(LeaderboardQueries.SELECT_MONTHLY);
        selectTop10Monthly.setString(1, getStatName());
        selectTop10Monthly.setInt(2, Utils.getMonthOfTheYear());
        selectTop10Monthly.setInt(3, Utils.getYear());
        ResultSet selectTop10MonthlyResults = selectTop10Monthly.executeQuery();
        LinkedList<TopEntry> top10Monthly = new LinkedList<>();

        while (selectTop10MonthlyResults.next()) {
            top10Monthly.add(new TopEntry(selectTop10MonthlyResults.getString("name"), selectTop10MonthlyResults.getInt("stat")));
        }

        selectTop10MonthlyResults.close();
        selectTop10Monthly.close();
        return top10Monthly;
    }

    @Override
    public LinkedList<TopEntry> getTop10AllTime(Connection connection) throws SQLException {
        PreparedStatement selectTop10AllTime = connection.prepareStatement(LeaderboardQueries.SELECT_ALL_TIME);
        selectTop10AllTime.setString(1, getStatName());
        ResultSet selectTop10AllTimeResults = selectTop10AllTime.executeQuery();
        LinkedList<TopEntry> top10AllTime = new LinkedList<>();

        while (selectTop10AllTimeResults.next()) {
            top10AllTime.add(new TopEntry(selectTop10AllTimeResults.getString("name"), selectTop10AllTimeResults.getInt("stat")));
        }

        selectTop10AllTimeResults.close();
        selectTop10AllTime.close();
        return top10AllTime;
    }

    @Override
    public boolean hasWeeklyAndMonthlyStats() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
