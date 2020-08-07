package me.patothebest.gamecore.storage;

import me.patothebest.gamecore.player.CorePlayer;

/**
 * /**
 * Class representing a storage entity
 * <p>
 * A storage entity is an interface which can't be
 * implemented directly, each storage type has its
 * own implementation such as MySQLEntity and that
 * interface must be implemented.
 * <p>
 * The storage entity is used to load additional player
 * information such as cage type, mystery boxes, etc
 *
 * @param <LoadData> the data needed for loading the
 *                   extra information
 * @param <SaveData> the data needed for saving the
 *                   extra information
 */
public interface StorageEntity<LoadData, SaveData, T extends Throwable> {

    /**
     * Loads a player's extra information
     *
     * @param player   the player loaded
     * @param loadData the extra data needed
     */
    void loadPlayer(CorePlayer player, LoadData loadData) throws T;

    /**
     * Saves a player's extra information
     *
     * @param player   the player to save
     * @param saveData the extra data needed
     */
    void savePlayer(CorePlayer player, SaveData saveData) throws T;

}