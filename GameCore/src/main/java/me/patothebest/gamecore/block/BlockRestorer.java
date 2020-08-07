package me.patothebest.gamecore.block;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.patothebest.gamecore.block.impl.Post1_13RestoringBlock;
import me.patothebest.gamecore.block.impl.Pre1_13RestoringBlock;
import me.patothebest.gamecore.block.impl.RestoringBlock;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.modules.ActivableModule;
import me.patothebest.gamecore.modules.ListenerModule;
import me.patothebest.gamecore.modules.ModuleName;
import me.patothebest.gamecore.nms.NMS;
import me.patothebest.gamecore.util.WrappedBukkitRunnable;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.HashMap;

@Singleton
@ModuleName("Block Restorer")
public class BlockRestorer extends WrappedBukkitRunnable implements ListenerModule, ActivableModule, Runnable {

    private final Provider<NMS> nmsProvider;
    private final HashMap<Block, RestoringBlock> blocks = new HashMap<>();
    private final Plugin plugin;

    @Inject private BlockRestorer(Provider<NMS> nmsProvider, Plugin plugin) {
        this.nmsProvider = nmsProvider;
        this.plugin = plugin;
    }

    @Override
    public void onEnable() {
        runTaskTimer(plugin, 0L, 1L);
    }

    @Override
    public void run() {
        blocks.values().removeIf(RestoringBlock::checkExpiration);
    }

    @Override
    public void onDisable() {
        cancel();
        for (RestoringBlock value : blocks.values()) {
            value.restore();
        }
        blocks.clear();
    }

    public void restore(final Block block) {
        if (!contains(block)) {
            return;
        }
        
        blocks.remove(block).restore();
    }

    public void changeBlockTemporarily(final Block block, @Nullable ItemStack toItem, long changeTimeInMillis) {
        if (Material.isNewVersion()) {
            blocks.put(block, new Post1_13RestoringBlock(nmsProvider.get(), block, toItem, changeTimeInMillis));
        } else {
            blocks.put(block, new Pre1_13RestoringBlock(nmsProvider.get(), block, toItem, changeTimeInMillis));
        }
    }

    public boolean contains(final Block block) {
        return getBlocks().containsKey(block);
    }

    public HashMap<Block, RestoringBlock> getBlocks() {
        return blocks;
    }
}