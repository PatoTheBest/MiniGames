package me.patothebest.gamecore.arena;

public class ArenaGroup {

    public final static ArenaGroup DEFAULT_GROUP = new ArenaGroup("none");

    private final String name;

    public ArenaGroup(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
