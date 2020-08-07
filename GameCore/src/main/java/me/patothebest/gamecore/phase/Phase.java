package me.patothebest.gamecore.phase;

import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.arena.ArenaState;
import me.patothebest.gamecore.feature.Feature;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Map;

/**
 * The Interface Phase.
 *
 * @param <Arena> the arena
 */
public interface Phase<Arena extends AbstractArena> extends Listener {

    /**
     * Configure the phase.
     */
    default void configure() {}

    /**
     * Start the phase.
     */
    void start();

    /**
     * Stop the phase.
     */
    void stop();

    /**
     * Register a feature.
     *
     * @param <T> the feature
     * @param featureClass the feature class
     * @return the instantiated feature
     */
    <T extends Feature> T registerFeature(Class<T> featureClass);

    /**
     * Can players join.
     *
     * @return true, if players can join
     */
    boolean canJoin();

    /**
     * Sets if players can join.
     *
     * @param canJoin if players can join
     */
    void setCanJoin(boolean canJoin);

    /**
     * Checks if it inherits the features from last phase.
     *
     * @return true, if it inherits the features from last phase
     */
    boolean isPreviousPhaseFeatures();

    /**
     * Sets if the phase should inherit the features from last phase
     *
     * @param previousPhaseFeatures if it should inherit
     */
    void setPreviousPhaseFeatures(boolean previousPhaseFeatures);

    /**
     * Removes a feature.
     *
     * @param feature the feature
     */
    void removeFeature(Feature feature);

    /**
     * Gets the next phase.
     *
     * @return the next phase
     */
    Phase getNextPhase();

    /**
     * Gets the previous phase.
     *
     * @return the previous phase
     */
    Phase getPreviousPhase();

    /**
     * Gets the features.
     *
     * @return the features
     */
    Map<Class<? extends Feature>, Feature> getFeatures();

    /**
     * Sets the next phase.
     *
     * @param nextPhase the new next phase
     */
    void setNextPhase(Phase nextPhase);

    /**
     * Sets the previous phase.
     *
     * @param previousPhase the new next phase
     */
    void setPreviousPhase(Phase previousPhase);


    /**
     * On player join method.
     *
     * @param player the player
     */
    default void playerJoin(Player player) {}

    /**
     * On player leave method.
     *
     * @param player the player
     */
    default void playerLeave(Player player) {}

    /**
     * Sets the arena.
     *
     * @param arena the new arena
     */
    void setArena(Arena arena);

    /**
     * This method will return the arena state the phase is in
     * <p>
     * This state will automatically be set on the arena
     *
     * @return the arenaState
     */
    ArenaState getArenaState();

    /**
     * Destroys the feature
     */
    void destroy();
}
