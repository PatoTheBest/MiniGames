package me.patothebest.gamecore.treasure.entities;

import me.patothebest.gamecore.player.CorePlayer;
import me.patothebest.gamecore.storage.StorageException;
import me.patothebest.gamecore.storage.flatfile.FlatFileEntity;
import me.patothebest.gamecore.storage.flatfile.PlayerProfileFile;
import me.patothebest.gamecore.treasure.type.TreasureType;

public class TreasureFlatFileEntity implements FlatFileEntity {

    /**
     * Loads a player's treasure keys
     *
     * @param player the player loaded
     * @param file   the player's profile file
     */
    @Override
    public void loadPlayer(CorePlayer player, PlayerProfileFile file) throws StorageException {
        for (TreasureType treasureType : TreasureType.values()) {
            player.setKeys(treasureType, file.getInt("keys." + treasureType.name().toLowerCase(), 0));
        }
    }

    /**
     * Saves a player's treasure keys
     *
     * @param player the player being saved
     * @param file   the player's profile file
     */
    @Override
    public void savePlayer(CorePlayer player, PlayerProfileFile file) throws StorageException {
        for (TreasureType treasureType : TreasureType.values()) {
            file.set("keys." + treasureType.name().toLowerCase(), player.getTreasureKeyMap().containsKey(treasureType) ? player.getKeys(treasureType) : 0);
        }
    }
}
