package me.patothebest.gamecore.treasure.entities;

import me.patothebest.gamecore.player.CorePlayer;
import me.patothebest.gamecore.player.modifiers.PlayerModifier;
import me.patothebest.gamecore.player.modifiers.TreasureModifier;
import me.patothebest.gamecore.storage.mysql.MySQLEntity;
import me.patothebest.gamecore.treasure.type.TreasureType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TreasureMySQLEntity implements MySQLEntity {

    /**
     * Loads a player's treasure keys
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

            for (TreasureType treasureType : TreasureType.values()) {
                player.setKeys(treasureType, 0);
            }
        } else for (TreasureType treasureType : TreasureType.values()) {
            player.setKeys(treasureType, resultSet.getInt(treasureType.name().toLowerCase()));
        }

        resultSet.close();
        selectUser.close();
    }

    /**
     * Updates a player's treasure keys in the database
     *
     * @param player     the player to save
     * @param connection the database connection
     */
    @Override
    public void savePlayer(CorePlayer player, Connection connection) throws SQLException {
        PreparedStatement updateUser = connection.prepareStatement(Queries.UPDATE);

        for (int i = 0; i < TreasureType.values().length; i++) {
            updateUser.setInt(i + 1, player.getKeys(TreasureType.values()[i]));
        }

        updateUser.setInt(6, player.getPlayerId());

        updateUser.executeUpdate();
        updateUser.close();
    }

    @Override
    public void updatePlayer(CorePlayer player, Connection connection, PlayerModifier updatedType, Object... args) throws SQLException {
        if(updatedType != TreasureModifier.MODIFY) {
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