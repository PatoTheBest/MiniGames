package me.patothebest.gamecore.storage.mysql;

import me.patothebest.gamecore.player.CorePlayer;
import me.patothebest.gamecore.player.modifiers.PlayerModifier;
import me.patothebest.gamecore.storage.StorageEntity;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * The interface used for all MySQL entities
 * <p>
 * The entity ony requires a connection for
 * loading and saving the player
 */
public interface MySQLEntity extends StorageEntity<Connection, Connection, SQLException> {

    /**
     * Updates a player specific part. This takes a string as the
     * part. The string can be anything, such as stats, kills, deaths,
     * etc.
     * <p>
     * This method will be handled by each {@link MySQLEntity} accordingly,
     * Usually only one of the entities will update.
     *
     * @param player      the player
     * @param connection  the connection to use
     * @param updatedType the updated part
     * @param args        the extra args
     *
     * @throws SQLException in case any exceptions occur
     */
    void updatePlayer(CorePlayer player, Connection connection, PlayerModifier updatedType, Object... args) throws SQLException;

    /**
     * Gets all the statements needed to create the
     * table(s) for the specific entity.
     *
     * @return the create table statements
     */
    String[] getCreateTableStatements();


}