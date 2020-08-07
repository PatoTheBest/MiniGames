package me.patothebest.gamecore.points;

import me.patothebest.gamecore.leaderboards.TopEntry;
import me.patothebest.gamecore.points.entities.PointsQueries;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.stats.Statistic;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

public class PointsStatistic implements Statistic {

    private boolean enabled;

    @Override
    public void updateStat(Player player, int amount) { }

    @Override
    public void updateStat(IPlayer player, int amount) { }

    @Override
    public LinkedList<TopEntry> getTop10Monthly(Connection connection) throws SQLException {
        return null;
    }

    @Override
    public LinkedList<TopEntry> getTop10Weekly(Connection connection) throws SQLException {
        return null;
    }

    @Override
    public LinkedList<TopEntry> getTop10AllTime(Connection connection) throws SQLException {
        PreparedStatement selectTop10AllTime = connection.prepareStatement(PointsQueries.SELECT_TOP_10_POINTS);
        ResultSet selectTop10AllTimeResults = selectTop10AllTime.executeQuery();
        LinkedList<TopEntry> top10AllTime = new LinkedList<>();

        while (selectTop10AllTimeResults.next()) {
            top10AllTime.add(new TopEntry(selectTop10AllTimeResults.getString("name"), selectTop10AllTimeResults.getInt("points")));
        }

        selectTop10AllTimeResults.close();
        selectTop10AllTime.close();
        return top10AllTime;
    }

    @Override
    public boolean hasWeeklyAndMonthlyStats() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String getStatName() {
        return "points";
    }
}
