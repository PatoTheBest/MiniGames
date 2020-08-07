package me.patothebest.gamecore.privatearenas;

import me.patothebest.gamecore.arena.AbstractArena;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PrivateArena {

    private final String owner;
    private final List<String> coHosts = new ArrayList<>();
    private AbstractArena arena;

    public PrivateArena(String owner, AbstractArena arena) {
        this.owner = owner;
        this.arena = arena;
        coHosts.add(owner);
    }

    public Player getOwner() {
        return Bukkit.getPlayer(owner);
    }

    public String getOwnerName() {
        return owner;
    }

    public AbstractArena getArena() {
        return arena;
    }

    public List<String> getWhitelistedPlayers() {
        return arena.getWhitelistedPlayers();
    }

    public List<String> getCoHosts() {
        return coHosts;
    }

    public void setArena(AbstractArena arena) {
        this.arena = arena;
    }
}
