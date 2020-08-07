package me.patothebest.thetowers.arena;

import javax.annotation.Nonnull;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class PointList extends AbstractList<PointList.Entry> {

    private final ArrayList<Entry> internalList = new ArrayList<>();

    public void sort() {
        internalList.sort((o1, o2) -> Integer.compare(o2.points, o1.points));
    }

    public boolean containsPlayer(String player) {
        for (Entry entry : internalList) {
            if (entry.player.equals(player)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Entry get(int index) {
        return internalList.get(index);
    }

    @Override
    public int size() {
        return internalList.size();
    }

    public void addPlayer(GameTeam gameTeam, String player) {
        internalList.add(new Entry(gameTeam, player));
        sort();
    }

    @Override
    public void clear() {
        internalList.clear();
    }

    public void addPoints(String player, int points) {
        for (Entry entry : internalList) {
            if (entry.player.equals(player)) {
                entry.points += points;
                return;
            }
        }

        throw new IllegalArgumentException(player + " is not on the list!");
    }

    public int getPoints(String player) {
        for (Entry entry : internalList) {
            if (entry.player.equals(player)) {
                return entry.points;
            }
        }

        return -1;
    }

    public int getPosition(String player) {
        for (int i = 0; i < internalList.size(); i++) {
            if (internalList.get(i).player.equals(player)) {
                return i;
            }
        }

        return -1;
    }

    public LinkedList<String> getPlayersOrdered() {
        LinkedList<String> linkedList = new LinkedList<>();
        for (Entry entry : internalList) {
            linkedList.add(entry.getPlayer());
        }
        return linkedList;
    }

    @Override
    @Nonnull
    public Iterator<Entry> iterator() {
        return internalList.iterator();
    }

    public static class Entry {

        private final GameTeam gameTeam;
        private final String player;
        private int points;

        private Entry(GameTeam gameTeam, String player) {
            this.gameTeam = gameTeam;
            this.player = player;
            this.points = 0;
        }

        public int getPoints() {
            return points;
        }

        public String getPlayer() {
            return player;
        }

        public GameTeam getGameTeam() {
            return gameTeam;
        }
    }
}
