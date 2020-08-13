package me.patothebest.gamecore.util;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerList implements Set<Player> {

    private final Map<Integer, Player> players = new ConcurrentHashMap<>();

    @Override
    public int size() {
        return players.size();
    }

    @Override
    public boolean isEmpty() {
        return players.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        if (!(o instanceof Player)) {
            return false;
        }

        return players.containsKey(((Player)o).getEntityId());
    }

    @NotNull
    @Override
    public Iterator<Player> iterator() {
        return players.values().iterator();
    }

    @Override
    public Object[] toArray() {
        return players.values().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return players.values().toArray(a);
    }

    @Override
    public boolean add(Player player) {
        players.put(player.getEntityId(), player);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (!(o instanceof Player)) {
            throw new IllegalArgumentException("Expected player but instead got: " + o.getClass());
        }

        return players.remove(((Player)o).getEntityId()) != null;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return players.values().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Player> c) {
        for (Player player : c) {
            add(player);
        }

        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        for (Object o : c) {
            remove(o);
        }

        return true;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        players.clear();
    }

    public List<Player> toJavaList() {
        return new ArrayList<>(players.values());
    }
}