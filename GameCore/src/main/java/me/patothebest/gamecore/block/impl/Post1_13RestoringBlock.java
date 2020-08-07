package me.patothebest.gamecore.block.impl;

import me.patothebest.gamecore.nms.NMS;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

public class Post1_13RestoringBlock extends RestoringBlock {

    private final BlockData blockData;

    public Post1_13RestoringBlock(NMS nms, Block block, @Nullable ItemStack toItem, long expireDelay) {
        super(nms, block, toItem, expireDelay);
        this.blockData = block.getBlockData().clone();
    }

    @Override
    public void restore() {
        nms.setBlock(block, originalItem);
        block.setBlockData(blockData);
        block.getState().update();
    }
}
