package me.patothebest.gamecore.points.entities;

import me.patothebest.gamecore.storage.mysql.MySQLEntity;
import me.patothebest.gamecore.player.CorePlayer;
import me.patothebest.gamecore.player.modifiers.PlayerModifier;
import me.patothebest.gamecore.player.modifiers.PointsModifier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PointsMySQLEntity implements MySQLEntity {

    /**
     * Loads the player's points
     * <p>
     * A record is inserted if the record doesn't
     * exist.
     *
     * @param player     the player loaded
     * @param connection the database connection
     */
    @Override
    public void loadPlayer(CorePlayer player, Connection connection) throws SQLException {
        PreparedStatement selectUser = connection.prepareStatement(PointsQueries.SELECT);
        selectUser.setInt(1, player.getPlayerId());

        ResultSet resultSet = selectUser.executeQuery();
        if (!resultSet.next()) {
            PreparedStatement createUser = connection.prepareStatement(PointsQueries.INSERT_RECORD);
            createUser.setInt(1, player.getPlayerId());
            createUser.executeUpdate();
        } else {
            player.setPoints(resultSet.getInt("points"));
        }

        resultSet.close();
        selectUser.close();
    }

    /**
     * Updates the player's points in the database
     *
     * @param player     the player to save
     * @param connection the database connection
     */
    @Override
    public void savePlayer(CorePlayer player, Connection connection) throws SQLException {
        PreparedStatement updateUser = connection.prepareStatement(PointsQueries.UPDATE);
        updateUser.setInt(1, player.getPoints());
        updateUser.setInt(2, player.getPlayerId());

        updateUser.executeUpdate();
        updateUser.close();
    }

    @Override
    public void updatePlayer(CorePlayer player, Connection connection, PlayerModifier updatedType, Object... args) throws SQLException {
        if (updatedType != PointsModifier.MODIFY) {
            return;
        }

        savePlayer(player, connection);
    }

    /**
     * Gets all the statements needed to create the
     * table(s) for the specific entity.
     *
     * @return the create table statements
     */
    @Override
    public String[] getCreateTableStatements() {
        return new String[]{PointsQueries.CREATE_TABLE};
    }
}