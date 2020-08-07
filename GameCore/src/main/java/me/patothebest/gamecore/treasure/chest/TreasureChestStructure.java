package me.patothebest.gamecore.treasure.chest;

import me.patothebest.gamecore.treasure.type.TreasureType;

import java.util.HashMap;
import java.util.Map;

public class TreasureChestStructure {

    public final static char[][][][] STRUCTURE;
    public final static Map<TreasureType, Map<TreasureChestStructurePiece, TreasureChestPiece>> PIECES = new HashMap<>();

    private TreasureChestStructure() {}

    static {
        char[][][] structure1;
        structure1 = new char[10][][];
        structure1[0] = new char[][]{{'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}};
        structure1[1] = new char[][]{{'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}};
        structure1[2] = new char[][]{{'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}};
        structure1[3] = new char[][]{{'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'g', 'a', 'a', 'a'}};
        structure1[4] = new char[][]{{'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}};
        structure1[5] = new char[][]{{'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}};
        structure1[6] = new char[][]{{'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}};

        char[][][] structure2;
        structure2 = new char[10][][];
        structure2[0] = new char[][]{{'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}};
        structure2[1] = new char[][]{{'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}};
        structure2[2] = new char[][]{{'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'i', 'i', 'i', 'a', 'a'}};
        structure2[3] = new char[][]{{'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'i', 'a', 'i', 'a', 'a'}};
        structure2[4] = new char[][]{{'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'i', 'i', 'i', 'a', 'a'}};
        structure2[5] = new char[][]{{'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}};
        structure2[6] = new char[][]{{'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}};

        char[][][] structure3;
        structure3 = new char[10][][];
        structure3[0] = new char[][]{{'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}};
        structure3[1] = new char[][]{{'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'o', 'o', 'a', 'o', 'o', 'a'}};
        structure3[2] = new char[][]{{'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'o', 'a', 'a', 'a', 'o', 'a'}};
        structure3[3] = new char[][]{{'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}};
        structure3[4] = new char[][]{{'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'o', 'a', 'a', 'a', 'o', 'a'}};
        structure3[5] = new char[][]{{'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'o', 'o', 'a', 'o', 'o', 'a'}};
        structure3[6] = new char[][]{{'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}};

        char[][][] structure4;
        structure4 = new char[10][][];
        structure4[0] = new char[][]{{'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}};
        structure4[1] = new char[][]{{'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'c', 'a', 'a', 'a'}};
        structure4[2] = new char[][]{{'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}};
        structure4[3] = new char[][]{{'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'c', 'a', 'a', 'a', 'c', 'a'}};
        structure4[4] = new char[][]{{'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}};
        structure4[5] = new char[][]{{'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'c', 'a', 'a', 'a'}};
        structure4[6] = new char[][]{{'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}};

        char[][][] structure5;
        structure5 = new char[10][][];
        structure5[0] = new char[][]{{'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}};
        structure5[1] = new char[][]{{'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'w', 'w', 'a', 'w', 'w', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}};
        structure5[2] = new char[][]{{'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'w', 'a', 'a', 'a', 'w', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}};
        structure5[3] = new char[][]{{'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}};
        structure5[4] = new char[][]{{'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'w', 'a', 'a', 'a', 'w', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}};
        structure5[5] = new char[][]{{'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'w', 'w', 'a', 'w', 'w', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}};
        structure5[6] = new char[][]{{'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}};

        char[][][] structure6;
        structure6 = new char[10][][];
        structure6[0] = new char[][]{{'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'u', 'a', 'a', 'a'}, {'a', 'a', 'a', 'p', 'a', 'a', 'a'}, {'a', 'a', 'a', 'p', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}};
        structure6[1] = new char[][]{{'a', 'a', 'a', 't', 'a', 'a', 'a'}, {'a', 'a', 'a', 'd', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}};
        structure6[2] = new char[][]{{'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}};
        structure6[3] = new char[][]{{'a', 't', 'a', 'a', 'a', 't', 'a'}, {'u', 'd', 'a', 'a', 'a', 'd', 'u'}, {'p', 'a', 'a', 'a', 'a', 'a', 'p'}, {'p', 'a', 'a', 'a', 'a', 'a', 'p'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}};
        structure6[4] = new char[][]{{'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}};
        structure6[5] = new char[][]{{'a', 'a', 'a', 't', 'a', 'a', 'a'}, {'a', 'a', 'a', 'd', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}};
        structure6[6] = new char[][]{{'a', 'a', 'a', 'a', 'a', 'a', 'a'}, {'a', 'a', 'a', 'u', 'a', 'a', 'a'}, {'a', 'a', 'a', 'p', 'a', 'a', 'a'}, {'a', 'a', 'a', 'p', 'a', 'a', 'a'}, {'a', 'a', 'a', 'a', 'a', 'a', 'a'}};

        STRUCTURE = new char[][][][]{structure1, structure2, structure3, structure4, structure5, structure6};
    }
}
