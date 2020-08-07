package me.patothebest.gamecore.injector;

/**
 * The Interface Activatable.
 */
public interface Activatable {

    /**
     * Method called on plugin enable.
     */
    default void onEnable() {}

    /**
     * Method called on plugin disable.
     */
    default void onDisable() {}

}
