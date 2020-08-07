package me.patothebest.gamecore.util;

import me.patothebest.gamecore.player.CorePlayer;
import me.patothebest.gamecore.player.modifiers.PlayerModifier;

/**
 * A class can implement the <code>PlayerObserver</code> interface when it
 * wants to be informed of changes in observable objects.
 */
public interface PlayerObserver {

    /**
     * This method is called whenever the observed object is changed. An
     * application calls an <tt>ObservablePlayer</tt> object's
     * <code>notifyObservers</code> method to have all the object's
     * observers notified of the change.
     *
     * @param o       the observable object.
     * @param updatedValue the updated type
     */
    void update(CorePlayer o, PlayerModifier updatedValue, Object... extraArgs);
}
