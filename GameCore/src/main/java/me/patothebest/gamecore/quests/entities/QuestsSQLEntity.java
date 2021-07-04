package me.patothebest.gamecore.quests.entities;

import com.google.inject.Inject;
import me.patothebest.gamecore.player.CorePlayer;
import me.patothebest.gamecore.player.modifiers.PlayerModifier;
import me.patothebest.gamecore.player.modifiers.QuestModifier;
import me.patothebest.gamecore.quests.ActiveQuest;
import me.patothebest.gamecore.quests.Quest;
import me.patothebest.gamecore.quests.QuestManager;
import me.patothebest.gamecore.quests.QuestsStatus;
import me.patothebest.gamecore.storage.mysql.MySQLEntity;
import me.patothebest.gamecore.util.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QuestsSQLEntity implements MySQLEntity {

    private final QuestManager questManager;

    @Inject private QuestsSQLEntity(QuestManager questManager) {
        this.questManager = questManager;
    }

    /**
     * Loads the player's quests
     * <p>
     * A record is inserted if the record doesn't
     * exist.
     *
     * @param player     the player loaded
     * @param connection the database connection
     */
    @Override
    public void loadPlayer(CorePlayer player, Connection connection) throws SQLException {
        PreparedStatement selectUser = connection.prepareStatement(QuestQueries.SELECT);
        selectUser.setInt(1, player.getPlayerId());

        ResultSet resultSet = selectUser.executeQuery();
        while (resultSet.next()) {
            String questName = resultSet.getString("quest_name");
            Quest quest = questManager.getQuest(questName);

            if (quest == null) {
                Utils.printError(
                    "Warning: Quest does not exist! ",
                    "Quest: " + questName,
                    "While loading for player: " + player.getName()
                );
                continue;
            }

            int entryId = resultSet.getInt("entry_id");
            long started = resultSet.getLong("started");
            int status = resultSet.getInt("status");
            int goalProgres = resultSet.getInt("goal_progress");

            if (quest.getCooldown() + started < System.currentTimeMillis()) {
                continue; // don't even bother parsing quests that already passed
            }

            ActiveQuest activeQuest = new ActiveQuest(player, quest, entryId, started, goalProgres, QuestsStatus.fromCode(status));
            player.activateQuest(activeQuest);
        }

        resultSet.close();
        selectUser.close();
    }

    /**
     * Updates the player's quests in the database
     *
     * @param player     the player to save
     * @param connection the database connection
     */
    @Override
    public void savePlayer(CorePlayer player, Connection connection) throws SQLException { }

    @Override
    public void updatePlayer(CorePlayer player, Connection connection, PlayerModifier updatedType, Object... args) throws SQLException {
        if (!(updatedType instanceof QuestModifier)) {
            return;
        }

        QuestModifier modifier = (QuestModifier) updatedType;
        ActiveQuest activeQuest = (ActiveQuest) args[0];
        if (modifier == QuestModifier.START_QUEST) {
            PreparedStatement statement = connection.prepareStatement(QuestQueries.INSERT_RECORD);
            statement.setInt(1, player.getPlayerId());
            statement.setString(2, activeQuest.getQuest().getName());
            statement.setLong(3, activeQuest.getStartDate());
            statement.setInt(4, activeQuest.getQuestsStatus().getStatusCode());
            statement.setInt(5, activeQuest.getProgress());
            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();

            if (rs.next()) {
                activeQuest.setEntryId(rs.getInt(1));
            } else {
                throw new SQLException("Could not get the id of the newly inserted stat record!");
            }
        } else if (modifier == QuestModifier.UPDATE_PROGRESS){
            PreparedStatement statement = connection.prepareStatement(QuestQueries.UPDATE_PROGRESS);
            statement.setInt(1, (Integer) args[1]);
            statement.setInt(2, activeQuest.getEntryId());
            statement.executeUpdate();
        } else if (modifier == QuestModifier.UPDATE_STATUS){
            PreparedStatement statement = connection.prepareStatement(QuestQueries.UPDATE_STATUS);
            statement.setInt(1, activeQuest.getQuestsStatus().getStatusCode());
            statement.setInt(2, activeQuest.getEntryId());
            statement.executeUpdate();
        }
    }

    /**
     * Gets all the statements needed to create the
     * table(s) for the specific entity.
     *
     * @return the create table statements
     */
    @Override
    public String[] getCreateTableStatements() {
        return new String[]{QuestQueries.CREATE_TABLE};
    }
}