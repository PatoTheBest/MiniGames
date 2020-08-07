package me.patothebest.gamecore.world;

import me.patothebest.gamecore.arena.AbstractArena;
import org.bukkit.World;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public interface WorldHandler {

    CompletableFuture<World> loadWorld(AbstractArena arena, String worldName, World.Environment environment);

    boolean decompressWorld(AbstractArena arena, File worldZipFile, File tempWorld);

    void deleteWorld(File tempWorld);

    boolean unloadWorld(World world);

    boolean hasAsyncSupport();
}
