package me.patothebest.gamecore.quests.entities;

import me.patothebest.gamecore.player.CorePlayer;
import me.patothebest.gamecore.storage.StorageException;
import me.patothebest.gamecore.storage.flatfile.FlatFileEntity;
import me.patothebest.gamecore.storage.flatfile.PlayerProfileFile;

public class QuestsFlatFileEntity implements FlatFileEntity {

    /**
     * Loads the player's quests
     *
     * @param player the player loaded
     * @param file   the player's profile file
     */
    @Override
    public void loadPlayer(CorePlayer player, PlayerProfileFile file) throws StorageException {

    }

    /**
     * Saves the player's quests
     *
     * @param player the player being saved
     * @param file   the player's profile file
     */
    @Override
    public void savePlayer(CorePlayer player, PlayerProfileFile file) throws StorageException {

    }
}
