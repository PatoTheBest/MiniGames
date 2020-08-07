package me.patothebest.gamecore.stats;

import me.patothebest.gamecore.leaderboards.TopEntry;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.util.NameableObject;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;

public interface Statistic extends Listener, NameableObject {

    void updateStat(Player player, int amount);

    void updateStat(IPlayer player, int amount);

    LinkedList<TopEntry> getTop10Weekly(Connection connection) throws SQLException;

    LinkedList<TopEntry> getTop10Monthly(Connection connection) throws SQLException;

    LinkedList<TopEntry> getTop10AllTime(Connection connection) throws SQLException;

    boolean hasWeeklyAndMonthlyStats();

    boolean isEnabled();

    void setEnabled(boolean enabled);

    String getStatName();

    @Override
    default String getName() {
        return getStatName();
    }
}
