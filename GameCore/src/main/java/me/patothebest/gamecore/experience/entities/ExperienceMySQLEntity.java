package me.patothebest.gamecore.experience.entities;

import me.patothebest.gamecore.player.CorePlayer;
import me.patothebest.gamecore.player.modifiers.ExperienceModifier;
import me.patothebest.gamecore.player.modifiers.PlayerModifier;
import me.patothebest.gamecore.storage.mysql.MySQLEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ExperienceMySQLEntity implements MySQLEntity {

    /**
     * Loads the player's experience
     * <p>
     * A record is inserted if the record doesn't
     * exist.
     *
     * @param player     the player loaded
     * @param connection the database connection
     */
    @Override
    public void loadPlayer(CorePlayer player, Connection connection) throws SQLException {
        PreparedStatement selectUser = connection.prepareStatement(ExperienceQueries.SELECT);
        selectUser.setInt(1, player.getPlayerId());

        ResultSet resultSet = selectUser.executeQuery();
        if (!resultSet.next()) {
            PreparedStatement createUser = connection.prepareStatement(ExperienceQueries.INSERT_RECORD);
            createUser.setInt(1, player.getPlayerId());
            createUser.executeUpdate();
        } else {
            player.setExperience(resultSet.getLong("experience"));
        }

        resultSet.close();
        selectUser.close();
    }

    /**
     * Updates the player's experience in the database
     *
     * @param player     the player to save
     * @param connection the database connection
     */
    @Override
    public void savePlayer(CorePlayer player, Connection connection) throws SQLException { }

    @Override
    public void updatePlayer(CorePlayer player, Connection connection, PlayerModifier updatedType, Object... args) throws SQLException {
        if (!(updatedType instanceof ExperienceModifier)) {
            return;
        }

        ExperienceModifier modifier = (ExperienceModifier) updatedType;
        String query = null;
        switch (modifier) {
            case SET_EXPERIENCE:
                query = ExperienceQueries.UPDATE_SET;
                break;
            case ADD_EXPERIENCE:
                query = ExperienceQueries.UPDATE_ADD;
                break;
            case REMOVE_EXPERIENCE:
                query = ExperienceQueries.UPDATE_REMOVE;
                break;
        }
        PreparedStatement updateUser = connection.prepareStatement(query);
        updateUser.setLong(1, (Long) args[0]);
        updateUser.setInt(2, player.getPlayerId());

        updateUser.executeUpdate();
        updateUser.close();
    }

    /**
     * Gets all the statements needed to create the
     * table(s) for the specific entity.
     *
     * @return the create table statements
     */
    @Override
    public String[] getCreateTableStatements() {
        return new String[]{ExperienceQueries.CREATE_TABLE};
    }
}