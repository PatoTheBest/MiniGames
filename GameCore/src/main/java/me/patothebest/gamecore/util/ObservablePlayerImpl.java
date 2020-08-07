package me.patothebest.gamecore.util;

import me.patothebest.gamecore.player.CorePlayer;
import me.patothebest.gamecore.player.modifiers.PlayerModifier;

import java.util.Vector;

public class ObservablePlayerImpl implements ObservablePlayer {

    private final Vector<PlayerObserver> obs;

    /**
     * Construct an ObservablePlayer with zero Observers.
     */

    public ObservablePlayerImpl() {
        obs = new Vector<>();
    }

    @Override
    public synchronized void addObserver(PlayerObserver o) {
        if (o == null)
            throw new NullPointerException();
        if (!obs.contains(o)) {
            obs.addElement(o);
        }
    }

    @Override
    public synchronized void deleteObserver(PlayerObserver o) {
        obs.removeElement(o);
    }

    @Override
    public void notifyObservers(PlayerModifier modifiedType, Object... args) {
        /*
         * a temporary array buffer, used as a snapshot of the state of
         * current Observers.
         */
        Object[] arrLocal;

        synchronized (this) {
            /* We don't want the PlayerObserver doing callbacks into
             * arbitrary code while holding its own Monitor.
             * The code where we extract each ObservablePlayer from
             * the Vector and store the state of the PlayerObserver
             * needs synchronization, but notifying observers
             * does not (should not).  The worst result of any
             * potential race-condition here is that:
             * 1) a newly-added PlayerObserver will miss a
             *   notification in progress
             * 2) a recently unregistered PlayerObserver will be
             *   wrongly notified when it doesn't care
             */
            arrLocal = obs.toArray();
        }

        for (Object anArrLocal : arrLocal) {
            ((PlayerObserver) anArrLocal).update((CorePlayer) this, modifiedType, args);
        }
    }

    @Override
    public synchronized void deleteObservers() {
        obs.removeAllElements();
    }

    @Override
    public synchronized int countObservers() {
        return obs.size();
    }
}
