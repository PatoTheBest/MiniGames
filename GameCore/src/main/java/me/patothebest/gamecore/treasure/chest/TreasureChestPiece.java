package me.patothebest.gamecore.treasure.chest;

import me.patothebest.gamecore.itemstack.Material;

import java.util.Optional;

public class TreasureChestPiece {

    private final Material material;
    private final byte data;

    public TreasureChestPiece(Optional<Material> material) {
        this.material = material.orElse(null);
        this.data = 0;
    }

    public TreasureChestPiece(Optional<Material> material, byte data) {
        this.material = material.orElse(null);
        this.data = data;
    }

    /**
     * Gets the piece material
     *
     * @return the material
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Gets the piece data
     *
     * @return the data
     */
    public byte getData() {
        return data;
    }
}