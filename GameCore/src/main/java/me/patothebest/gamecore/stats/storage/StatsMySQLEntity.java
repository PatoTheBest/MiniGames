package me.patothebest.gamecore.stats.storage;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.patothebest.gamecore.player.CorePlayer;
import me.patothebest.gamecore.player.modifiers.PlayerModifier;
import me.patothebest.gamecore.player.modifiers.StatsModifier;
import me.patothebest.gamecore.storage.mysql.MySQLEntity;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.stats.StatRecord;
import me.patothebest.gamecore.stats.Statistic;
import me.patothebest.gamecore.stats.StatsManager;
import me.patothebest.gamecore.stats.TrackedStatistic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class will handle all stats loading/saving
 * for MySQL storage tpe
 */
@Singleton
public class StatsMySQLEntity implements MySQLEntity {

    private final StatsManager statsManager;

    @Inject private StatsMySQLEntity(StatsManager statsManager) {
        this.statsManager = statsManager;
    }

    /**
     * Loads a player's extra information
     *
     * @param player     the player loaded
     * @param connection connection
     */
    @Override
    public void loadPlayer(CorePlayer player, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(StatsQueries.SELECT);
        preparedStatement.setInt(1, player.getPlayerId());
        preparedStatement.setInt(2, Utils.getMonthOfTheYear());
        preparedStatement.setInt(3, Utils.getYear());
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            StatRecord statRecord = new StatRecord(resultSet.getInt("entry_id"), player.getPlayerId(), resultSet.getString("stat_name"));
            statRecord.setStats(resultSet.getInt("stat"));
            statRecord.setWeek(resultSet.getInt("week"));
            statRecord.setMonth(resultSet.getInt("month"));
            statRecord.setYear(resultSet.getInt("year"));

            Statistic statistic = statsManager.getStatisticByName(statRecord.getStatName());

            if(statistic == null) {
                continue;
            }

            if (!player.getStatistics().containsKey(statistic.getClass())) {
                player.getStatistics().put(statistic.getClass(), new TrackedStatistic());
            }

            player.getStatistics().get(statistic.getClass()).getStatRecords().add(statRecord);
        }

        resultSet.close();
        preparedStatement.close();
    }

    /**
     * Saves a player's stats
     * <p>
     * Currently it's not used
     *
     * @param player     the player to save
     * @param connection the connection
     */
    @Override
    public void savePlayer(CorePlayer player, Connection connection) throws SQLException {
        // TODO: Figure out if this is needed or not
    }

    /**
     * Updates a player's stats.
     * <p>
     * The args must be {@link StatRecord} objects
     *
     * @param player      the player
     * @param connection  the connection to use
     * @param updatedType the updated part
     * @param args        the stat records to manipulate
     *
     * @throws SQLException in case any exceptions occur
     */
    @Override
    public void updatePlayer(CorePlayer player, Connection connection, PlayerModifier updatedType, Object... args) throws SQLException {
        if (!(updatedType instanceof StatsModifier)) {
            return;
        }

        if (updatedType == StatsModifier.INSERT) {
            for (Object arg : args) {
                StatRecord statRecord = (StatRecord) arg;

                PreparedStatement statement = connection.prepareStatement(StatsQueries.INSERT, Statement.RETURN_GENERATED_KEYS);
                statement.setInt(1, player.getPlayerId());
                statement.setString(2, statRecord.getStatName());
                statement.setInt(3, statRecord.getStats());
                statement.setInt(4, statRecord.getWeek());
                statement.setInt(5, statRecord.getMonth());
                statement.setInt(6, statRecord.getYear());
                statement.executeUpdate();
                ResultSet rs = statement.getGeneratedKeys();

                if (rs.next()) {
                    statRecord.setEntryId(rs.getInt(1));
                } else {
                    throw new SQLException("Could not get the id of the newly inserted stat record!");
                }
            }

            return;
        }

        if (updatedType == StatsModifier.UPDATE_ADD) {
            PreparedStatement preparedStatement = connection.prepareStatement(StatsQueries.UPDATE);

            for (int i = 1; i < args.length; i++) {
                StatRecord statRecord = (StatRecord) args[i];
                preparedStatement.setInt(1, (Integer) args[0]);
                preparedStatement.setInt(2, statRecord.getEntryId());
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
            preparedStatement.close();
        }
    }

    /**
     * Gets all the statements needed to create the
     * table(s) for the stats.
     *
     * @return the create table statements
     */
    @Override
    public String[] getCreateTableStatements() {
        return new String[]{StatsQueries.CREATE_TABLE};
    }

}
