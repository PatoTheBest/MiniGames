package me.patothebest.gamecore.player;

import java.util.UUID;

/**
 * Class used to identify the player in a conversion or in an offline
 * value update
 */
public class PlayerIdentity {

    private final String name;
    private final UUID uuid;

    public PlayerIdentity(String name, UUID uuid) {
        this.name = name;
        this.uuid = uuid;
    }

    /**
     * Gets the player's name
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the player's uuid
     *
     * @return the uuid
     */
    public UUID getUuid() {
        return uuid;
    }
}
