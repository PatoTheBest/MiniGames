package me.patothebest.gamecore.util;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an object that may be serialized.
 */
public interface SerializableObject extends ConfigurationSerializable {

    /**
     * Creates a Map representation of this class.
     * <p>
     * This class must provide a method to restore this class, as defined in
     * the {@link ConfigurationSerializable} interface javadocs.
     *
     * @return Map containing the current state of this class
     */
    @Override
    default Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        serialize(data);
        return data;
    }

    void serialize(Map<String, Object> data);
}
