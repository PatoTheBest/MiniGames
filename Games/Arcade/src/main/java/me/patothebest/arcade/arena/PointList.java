package me.patothebest.arcade.arena;

import org.bukkit.entity.Player;

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

    public boolean containsPlayer(Player player) {
        for (Entry entry : internalList) {
            if (entry.player == player) {
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

    public void addPlayer(Player player) {
        internalList.add(new Entry(player));
        sort();
    }

    @Override
    public void clear() {
        internalList.clear();
    }

    public void addPoints(Player player, int points) {
        for (Entry entry : internalList) {
            if (entry.player == player) {
                entry.points += points;
                return;
            }
        }

        throw new IllegalArgumentException(player.getName() + " is not on the list!");
    }

    public int getPoints(Player player) {
        for (Entry entry : internalList) {
            if (entry.player == player) {
                return entry.points;
            }
        }

        return -1;
    }

    public int getPosition(Player player) {
        for (int i = 0; i < internalList.size(); i++) {
            if (internalList.get(i).player == player) {
                return i;
            }
        }

        return -1;
    }

    public LinkedList<Player> getPlayersOrdered() {
        LinkedList<Player> linkedList = new LinkedList<>();
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

        private final Player player;
        private int points;

        private Entry(Player player) {
            this.player = player;
            this.points = 0;
        }

        public int getPoints() {
            return points;
        }

        public Player getPlayer() {
            return player;
        }
    }
}
