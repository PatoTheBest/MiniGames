package me.patothebest.gamecore.world;

import me.patothebest.gamecore.itemstack.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class BlankChunkGenerator extends ChunkGenerator {

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    //This is where you stick your populators - these will be covered in another tutorial
    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return new ArrayList<>();
    }
    //This needs to be set to return true to override minecraft's default behaviour
    @Override
    public boolean canSpawn(World world, int x, int z) {
        return true;
    }

    @Override
    public ChunkData generateChunkData(World world, Random random, int cx, int cz, BiomeGrid biome) {
        ChunkData chunkData = createChunkData(world);

        for (int x = 0; x <= 15; x++) {
            for (int z = 0; z <= 15; z++) {
                biome.setBiome(x, z, Biome.PLAINS);
            }
        }

        if(cx == 0 && cz == 0) {
            chunkData.setBlock(0, 100, 0, Material.BEDROCK.parseMaterial());
        }

        return chunkData;
    }
}