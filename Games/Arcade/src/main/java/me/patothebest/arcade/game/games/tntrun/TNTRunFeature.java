package me.patothebest.arcade.game.games.tntrun;

import com.google.inject.Inject;
import me.patothebest.arcade.Arcade;
import me.patothebest.gamecore.feature.AbstractRunnableFeature;
import me.patothebest.gamecore.itemstack.Material;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;

import java.util.ArrayList;
import java.util.List;

public class TNTRunFeature extends AbstractRunnableFeature {

    private final Arcade plugin;
    private final static int MAX_TICKS = 6;
    @SuppressWarnings("unchecked")
    private final List<Block>[] blocks = new ArrayList[MAX_TICKS];
    private int tick = 0;

    @Inject private TNTRunFeature(Arcade plugin) {
        this.plugin = plugin;
        for (int i = 0; i < MAX_TICKS; i++) {
            blocks[i] = new ArrayList<>();
        }
    }

    @Override
    public void initializeFeature() {
        runTaskTimer(plugin, 0L, 1L);
    }

    @Override
    public void stopFeature() {
        for (List<Block> block : blocks) {
            block.clear();
        }
        cancel();
    }

    @Override
    public void run() {
        for (Player player : arena.getPlayers()) {
            Block blockBelow = getBlockBelow(player);
            if (blockBelow != null) {
                blocks[tick].add(blockBelow);
            }
        }

        tick++;
        tick%= MAX_TICKS;

        for (Block block : blocks[tick]) {
            if (block.getType() != Material.AIR.parseMaterial()) {
                block.setType(Material.AIR.parseMaterial(), false);
                block.getRelative(BlockFace.DOWN).setType(Material.AIR.parseMaterial(), false);
            }
        }
        blocks[tick].clear();
    }

    private final static double[] OFFSETS = {0.3, -0.3};
    private Block getBlockBelow(Player player) {
        Location location = player.getLocation();
        double x = location.getX();
        double z = location.getZ();
        int flooredX = NumberConversions.floor(x);
        int flooredZ = NumberConversions.floor(z);
        World world = player.getWorld();
        double yBelow = player.getLocation().getY() - 0.0001;

        Block block = new Location(world, x, yBelow, z).getBlock();
        if (block.getType() != Material.AIR.parseMaterial()) {
            return block;
        }

        for (double offsetX : OFFSETS) {
            for (double offsetZ : OFFSETS) {
                if (NumberConversions.floor(x + offsetX) != flooredX ||
                    NumberConversions.floor(z + offsetZ) != flooredZ) {
                    Block block2 = new Location(world, x + offsetX, yBelow, z + offsetZ).getBlock();
                    if (block2.getType() != Material.AIR.parseMaterial()) {
                        return block2;
                    }
                }
            }
        }

        return null;
    }
}