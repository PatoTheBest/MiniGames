package me.patothebest.gamecore.treasure.chest;

import me.patothebest.gamecore.util.DoubleCallback;
import org.bukkit.block.Block;

public enum TreasureChestStructurePiece {
    
    // The lamp block
    LAMP('l', "lamp"),
    // The base block where the player will be standing
    BASE('g', "base", (treasureChest, block) -> treasureChest.setCenter(block.getLocation())),
    // The inner ring surrounding the base block
    INNER('i', "inner"),
    // The outer ring surrounding the inner ring excluding the blocks below the chest
    OUTER('o', "outer"),
    // The blocks below the chests
    BELOW_CHESTS('c', "below-chests"),
    // The blocks enclosing the chests
    WALLS('w', "walls"),
    // The pillars from the sides
    PILLARS('p', "pillars"),
    // The stairs
    NORMAL_STAIRS('u', "stairs", TreasureChest::setStairsDirection),
    UPSIDE_DOWN_STAIRS('d', "stairs", TreasureChest::setUpsideDownStairsDirection),
    // The block that goes on top of the stairs that is on top of the pillars
    TOP('t', "top")

    ;

    private final char keyChar;
    private final String configPath;
    private DoubleCallback<TreasureChest, Block> doubleCallback;

    TreasureChestStructurePiece(char keyChar, String configPath) {
        this.keyChar = keyChar;
        this.configPath = configPath;
    }

    TreasureChestStructurePiece(char keyChar, String configPath, DoubleCallback<TreasureChest, Block> doubleCallback) {
        this.keyChar = keyChar;
        this.configPath = configPath;
        this.doubleCallback = doubleCallback;
    }

    public static TreasureChestStructurePiece getStructurePiece(char keyChar) {
        for (TreasureChestStructurePiece treasureChestStructurePiece : TreasureChestStructurePiece.values()) {
            if(treasureChestStructurePiece.keyChar == keyChar) {
                return treasureChestStructurePiece;
            }
        }

        return null;
    }

    /**
     * Gets the config path
     *
     * @return the config path
     */
    public String getConfigPath() {
        return configPath;
    }

    /**
     * Gets the TreasureChest Callback
     * <p>
     * This callback is used for things like the glass
     * block being set as the center block and the stairs
     * having to be set in a specific direction, facing the
     * center block.
     *
     * @return value of treasureChestCallback
     */
    public DoubleCallback<TreasureChest, Block> getTreasureChestCallBack() {
        return doubleCallback;
    }
}
