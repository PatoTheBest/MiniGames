package me.patothebest.gamecore.arena.types;

import me.patothebest.gamecore.vector.ArenaLocation;

import java.util.List;

public interface SpawneableArena {

    List<ArenaLocation> getSpawns();

    int getSpawnHeight();

}
