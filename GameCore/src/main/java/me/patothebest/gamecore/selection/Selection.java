package me.patothebest.gamecore.selection;

import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.vector.Cuboid;
import org.bukkit.Location;

public class Selection {

    private Location pointA;
    private Location pointB;

    public Location getPointA() {
        return pointA;
    }

    public Selection setPointA(Location pointA) {
        this.pointA = pointA;
        return this;
    }

    public Location getPointB() {
        return pointB;
    }

    public Selection setPointB(Location pointB) {
        this.pointB = pointB;
        return this;
    }

    public boolean arePointsSet() {
        return pointA != null && pointB != null;
    }

    public Cuboid toCubiod(String name, AbstractArena arena) {
        return new Cuboid(name, pointA, pointB, arena);
    }
}
