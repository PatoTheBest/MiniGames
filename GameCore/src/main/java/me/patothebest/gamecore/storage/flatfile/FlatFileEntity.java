package me.patothebest.gamecore.storage.flatfile;

import me.patothebest.gamecore.storage.StorageEntity;
import me.patothebest.gamecore.storage.StorageException;

/**
 *
 * The interface used for all FlatFile entities
 * <p>
 * The entity ony requires a Map<String, Object> for
 * loading and saving the player
 * <p>
 * The player is loaded as a Map<String, Object> and
 * from there we just need to cast the objects or parse
 * the objects
 * <p>
 * To save the player, all the information is stored in
 * a single Map<String, Object> which is saved into
 * YAML format.
 */
public interface FlatFileEntity extends StorageEntity<PlayerProfileFile, PlayerProfileFile, StorageException> {

}