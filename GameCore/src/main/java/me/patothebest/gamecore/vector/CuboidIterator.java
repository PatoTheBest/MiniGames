package me.patothebest.gamecore.vector;

import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Iterator;

class CuboidIterator implements Iterator<Block> {

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    private final World w;
    private final int baseX;
    private final int baseY;
    private final int baseZ;
    private final int sizeX;
    private final int sizeY;
    private final int sizeZ;
    private int x, y, z;

    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //

    public CuboidIterator(World w, int x1, int y1, int z1, int x2, int y2, int z2) {
        this.w = w;
        this.baseX = x1;
        this.baseY = y1;
        this.baseZ = z1;
        this.sizeX = Math.abs(x2 - x1) + 1;
        this.sizeY = Math.abs(y2 - y1) + 1;
        this.sizeZ = Math.abs(z2 - z1) + 1;
        this.x = this.y = this.z = 0;
    }

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    public boolean hasNext() {
        return this.x < this.sizeX && this.y < this.sizeY && this.z < this.sizeZ;
    }

    @Override
    public Block next() {
        Block b = this.w.getBlockAt(this.baseX + this.x, this.baseY + this.y, this.baseZ + this.z);
        if (++x >= this.sizeX) {
            this.x = 0;
            if (++this.y >= this.sizeY) {
                this.y = 0;
                ++this.z;
            }
        }
        return b;
    }

    @Override
    public void remove() { }
}