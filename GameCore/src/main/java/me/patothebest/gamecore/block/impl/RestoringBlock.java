package me.patothebest.gamecore.block.impl;

import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.nms.NMS;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

public abstract class RestoringBlock {

    protected final NMS nms;
    protected final Block block;
    protected final long expireTime;
    protected final ItemStack originalItem;

    public RestoringBlock(NMS nms, Block block, @Nullable ItemStack toItem, long expireDelay) {
        this.nms = nms;
        this.block = block;
        this.expireTime = expireDelay + System.currentTimeMillis();
        this.originalItem = new ItemStackBuilder(block.getType()).data(block.getData());

        if (toItem != null) {
            nms.setBlock(block, toItem);
        }
    }

    public boolean checkExpiration() {
        if (System.currentTimeMillis() < this.expireTime) {
            return false;
        }

        this.restore();
        return true;
    }

    public abstract void restore();
}