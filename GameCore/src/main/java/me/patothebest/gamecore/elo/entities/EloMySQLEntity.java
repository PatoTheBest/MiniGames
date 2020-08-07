package me.patothebest.gamecore.elo.entities;

import me.patothebest.gamecore.player.CorePlayer;
import me.patothebest.gamecore.player.modifiers.EloModifier;
import me.patothebest.gamecore.player.modifiers.PlayerModifier;
import me.patothebest.gamecore.player.types.EloPlayer;
import me.patothebest.gamecore.storage.mysql.MySQLEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EloMySQLEntity implements MySQLEntity {

    /**
     * Loads the player's elo
     * <p>
     * A record is inserted if the record doesn't
     * exist.
     *
     * @param player     the player loaded
     * @param connection the database connection
     */
    @Override
    public void loadPlayer(CorePlayer player, Connection connection) throws SQLException {
        PreparedStatement selectUser = connection.prepareStatement(Queries.SELECT);
        selectUser.setInt(1, player.getPlayerId());

        ResultSet resultSet = selectUser.executeQuery();
        if (!resultSet.next()) {
            PreparedStatement createUser = connection.prepareStatement(Queries.INSERT_RECORD);
            createUser.setInt(1, player.getPlayerId());
            createUser.executeUpdate();
        } else {
            ((EloPlayer)player).setElo(resultSet.getInt("elo"));
        }

        resultSet.close();
        selectUser.close();
    }

    /**
     * Updates the player's elo in the database
     *
     * @param player     the player to save
     * @param connection the database connection
     */
    @Override
    public void savePlayer(CorePlayer player, Connection connection) throws SQLException {
        PreparedStatement updateUser = connection.prepareStatement(Queries.UPDATE);
        updateUser.setInt(1, ((EloPlayer)player).getElo());
        updateUser.setInt(2, player.getPlayerId());

        updateUser.executeUpdate();
        updateUser.close();
    }

    @Override
    public void updatePlayer(CorePlayer player, Connection connection, PlayerModifier updatedType, Object... args) throws SQLException {
        if(updatedType != EloModifier.MODIFY) {
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
        return new String[]{Queries.CREATE_TABLE};
    }
}