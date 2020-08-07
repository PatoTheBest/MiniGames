package me.patothebest.gamecore.points.entities;

import me.patothebest.gamecore.storage.StorageException;
import me.patothebest.gamecore.storage.flatfile.FlatFileEntity;
import me.patothebest.gamecore.storage.flatfile.PlayerProfileFile;
import me.patothebest.gamecore.player.CorePlayer;

public class PointsFlatFileEntity implements FlatFileEntity {

    /**
     * Loads the player's points
     *
     * @param player the player loaded
     * @param file   the player's profile file
     */
    @Override
    public void loadPlayer(CorePlayer player, PlayerProfileFile file) throws StorageException {
        player.setPoints(file.getInt("points"));
    }

    /**
     * Saves the player's points
     *
     * @param player the player being saved
     * @param file   the player's profile file
     */
    @Override
    public void savePlayer(CorePlayer player, PlayerProfileFile file) throws StorageException {
        file.set("points", player.getPoints());
    }
}
