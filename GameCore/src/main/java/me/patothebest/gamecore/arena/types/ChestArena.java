package me.patothebest.gamecore.arena.types;

import me.patothebest.gamecore.feature.features.chests.refill.ChestLocation;
import me.patothebest.gamecore.vector.ArenaLocation;

import java.util.List;

public interface ChestArena {

    List<ArenaLocation> getArenaChests(ChestLocation chestLocation);

}
