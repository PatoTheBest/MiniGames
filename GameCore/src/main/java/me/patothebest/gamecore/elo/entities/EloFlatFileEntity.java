package me.patothebest.gamecore.elo.entities;

import me.patothebest.gamecore.player.CorePlayer;
import me.patothebest.gamecore.player.types.EloPlayer;
import me.patothebest.gamecore.storage.StorageException;
import me.patothebest.gamecore.storage.flatfile.FlatFileEntity;
import me.patothebest.gamecore.storage.flatfile.PlayerProfileFile;

public class EloFlatFileEntity implements FlatFileEntity {

    /**
     * Loads the player's elo
     *
     * @param player the player loaded
     * @param file   the player's profile file
     */
    @Override
    public void loadPlayer(CorePlayer player, PlayerProfileFile file) throws StorageException {
        ((EloPlayer)player).setElo(file.getInt("elo"));
    }

    /**
     * Saves the player's elo
     *
     * @param player the player being saved
     * @param file   the player's profile file
     */
    @Override
    public void savePlayer(CorePlayer player, PlayerProfileFile file) throws StorageException {
        file.set("elo", ((EloPlayer)player).getElo());
    }
}
