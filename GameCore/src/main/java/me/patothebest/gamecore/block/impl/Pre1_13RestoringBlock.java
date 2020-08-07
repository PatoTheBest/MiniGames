package me.patothebest.gamecore.block.impl;

import me.patothebest.gamecore.nms.NMS;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import javax.annotation.Nullable;

public class Pre1_13RestoringBlock extends RestoringBlock {

    private final MaterialData originalData;

    public Pre1_13RestoringBlock(NMS nms, Block block, @Nullable ItemStack toItem, long expireDelay) {
        super(nms, block, toItem, expireDelay);
        this.originalData = block.getState().getData().clone();
    }

    @Override
    public void restore() {
        nms.setBlock(block, originalItem);
        block.getState().setData(originalData);
        block.getState().update();
    }
}
