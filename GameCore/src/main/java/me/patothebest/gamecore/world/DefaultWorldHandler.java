package me.patothebest.gamecore.world;

import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public class DefaultWorldHandler implements WorldHandler {

    @Override
    public CompletableFuture<World> loadWorld(AbstractArena arena, String worldName, World.Environment environment) {
        CompletableFuture<World> completableFuture = new CompletableFuture<>();
        WorldCreator worldCreator = new WorldCreator(worldName);
        worldCreator.environment(environment);
        worldCreator.generateStructures(false);
        worldCreator.generator(ArenaWorld.chunkGenerator);
        completableFuture.complete(Bukkit.createWorld(worldCreator));
        return completableFuture;
    }

    @Override
    public boolean hasAsyncSupport() {
        return false;
    }

    @Override
    public boolean decompressWorld(AbstractArena arena, File worldZipFile, File tempWorld) {
        // unzip the world
        if (!Utils.unZip(worldZipFile.getPath(), tempWorld.getPath())) {
            return false;
        }
        new File(tempWorld, "uid.dat").delete();
        return true;
    }

    @Override
    public void deleteWorld(File tempWorld) {
        // delete the world directory
        Utils.deleteFolder(tempWorld);
    }

    @Override
    public boolean unloadWorld(World world) {
        return Bukkit.unloadWorld(world, false);
    }
}
