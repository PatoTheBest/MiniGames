package me.patothebest.gamecore.experience;

import me.patothebest.gamecore.experience.entities.ExperienceQueries;
import me.patothebest.gamecore.leaderboards.TopEntry;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.stats.Statistic;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

public class ExperienceStatistic implements Statistic {

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
        PreparedStatement selectTop10AllTime = connection.prepareStatement(ExperienceQueries.SELECT_TOP_10_EXPERIENCE);
        ResultSet selectTop10AllTimeResults = selectTop10AllTime.executeQuery();
        LinkedList<TopEntry> top10AllTime = new LinkedList<>();

        while (selectTop10AllTimeResults.next()) {
            top10AllTime.add(new TopEntry(selectTop10AllTimeResults.getString("name"), (int) selectTop10AllTimeResults.getLong("experience")));
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
        return "experience";
    }
}
