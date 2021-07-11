package me.patothebest.gamecore.experience.entities;

import me.patothebest.gamecore.player.CorePlayer;
import me.patothebest.gamecore.storage.StorageException;
import me.patothebest.gamecore.storage.flatfile.FlatFileEntity;
import me.patothebest.gamecore.storage.flatfile.PlayerProfileFile;

public class ExperienceFlatFileEntity implements FlatFileEntity {

    /**
     * Loads the player's experience
     *
     * @param player the player loaded
     * @param file   the player's profile file
     */
    @Override
    public void loadPlayer(CorePlayer player, PlayerProfileFile file) throws StorageException {
        player.setExperience(file.getInt("experience"));
    }

    /**
     * Saves the player's experience
     *
     * @param player the player being saved
     * @param file   the player's profile file
     */
    @Override
    public void savePlayer(CorePlayer player, PlayerProfileFile file) throws StorageException {
        file.set("experience", player.getExperience());
    }
}
