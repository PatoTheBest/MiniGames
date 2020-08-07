package me.patothebest.gamecore.player.types;

import me.patothebest.gamecore.player.IPlayer;

public interface EloPlayer extends IPlayer {

    /**
     * Gets the elo from the player
     *
     * @return the elo
     */
    int getElo();

    /**
     * Sets the player elo
     *
     * @param newElo the new elo
     */
    void setElo(int newElo);

}
