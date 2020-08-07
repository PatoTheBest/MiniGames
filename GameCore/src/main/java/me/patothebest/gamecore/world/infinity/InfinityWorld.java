package me.patothebest.gamecore.world.infinity;

import com.grinderwolf.swm.api.exceptions.CorruptedWorldException;
import com.grinderwolf.swm.api.exceptions.NewerFormatException;
import com.grinderwolf.swm.api.exceptions.UnknownWorldException;
import com.grinderwolf.swm.api.exceptions.WorldInUseException;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeChunk;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.nms.CraftSlimeWorld;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.world.ArenaWorld;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class InfinityWorld extends ArenaWorld {

    private final SlimeLoader loader;
    private final SlimeWorld slimeWorld;
    private final World world;
    private Set<Long> chunkCoordinates;

    public InfinityWorld(AbstractArena arena, SlimeLoader loader, SlimeWorld slimeWorld, World world) {
        super(arena);
        this.loader = loader;
        this.slimeWorld = slimeWorld;
        this.world = world;
    }

    @Override
    public boolean decompressWorld() {
        return super.decompressWorld();
    }

    @Override
    public void loadWorld(boolean permanentWorld) {
        if (permanentWorld) {
            throw new UnsupportedOperationException("Permanent worlds are not supported!");
        }

        try {
            byte[] serializedWorld = loader.loadWorld(arena.getName(), true);
            Vector offset = arena.getOffset();
            Map<Long, SlimeChunk> slimeChunkMap = SlimeWorldUtils.deserializeWorld(offset.getBlockX(), offset.getBlockZ(), arena.getWorldName(), serializedWorld);
            chunkCoordinates = slimeChunkMap.keySet();
            arena.getPluginScheduler().ensureSync(() -> ((CraftSlimeWorld)slimeWorld).getChunks().putAll(slimeChunkMap));
        } catch (UnknownWorldException | WorldInUseException | IOException | NewerFormatException | CorruptedWorldException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void unloadWorld(boolean save) {
        if (save) {
            throw new UnsupportedOperationException("Saving the world is not supported");
        }

        Map<Long, SlimeChunk> chunks = ((CraftSlimeWorld) slimeWorld).getChunks();
        for (Long chunkCoordinate : chunkCoordinates) {
            SlimeChunk slimeChunk = chunks.get(chunkCoordinate);
            if (slimeChunk != null) {
                world.unloadChunk(slimeChunk.getX(), slimeChunk.getZ(), false);
                chunks.remove(chunkCoordinate);
            }
        }
        chunkCoordinates.clear();
    }

    @Override
    public void deleteWorld() { }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public File getTempWorld() {
        return null;
    }

    @Override
    public File getWorldZipFile() {
        return null;
    }
}
