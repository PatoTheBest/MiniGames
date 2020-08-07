package me.patothebest.gamecore.cosmetics.cage;

import com.google.inject.Provider;
import me.patothebest.gamecore.nms.NMS;
import me.patothebest.gamecore.util.Callback;
import org.bukkit.Location;
import org.bukkit.block.Block;

public enum CageStructure {

    INDIVIDUAL(new int[3][5][3], structure -> {
        structure[0] = new int[][] { { 2, 2, 2 }, { 2, 2, 2 }, { 2, 2, 2 }, { 2, 2, 2 }, { 2, 2, 2 } };
        structure[1] = new int[][] { { 2, 1, 2 }, { 2, 0, 2 }, { 2, 0, 2 }, { 2, 0, 2 }, { 2, 1, 2 } };
        structure[2] = new int[][] { { 2, 2, 2 }, { 2, 2, 2 }, { 2, 2, 2 }, { 2, 2, 2 }, { 2, 2, 2 } };
    }),

   TEAM(new int[5][5][5], structure -> {
        structure[0] = new int[][] { { 2, 2, 2, 2, 2 }, { 2, 2, 2, 2, 2 }, { 2, 2, 2, 2, 2 }, { 2, 2, 2, 2, 2 }, { 2, 2, 2, 2, 2 } };
        structure[1] = new int[][] { { 2, 1, 1, 1, 2 }, { 2, 0, 0, 0, 2 }, { 2, 0, 0, 0, 2 }, { 2, 0, 0, 0, 2 }, { 2, 1, 1, 1, 2 } };
        structure[2] = new int[][] { { 2, 1, 1, 1, 2 }, { 2, 0, 0, 0, 2 }, { 2, 0, 0, 0, 2 }, { 2, 0, 0, 0, 2 }, { 2, 1, 1, 1, 2 } };
        structure[3] = new int[][] { { 2, 1, 1, 1, 2 }, { 2, 0, 0, 0, 2 }, { 2, 0, 0, 0, 2 }, { 2, 0, 0, 0, 2 }, { 2, 1, 1, 1, 2 } };
        structure[4] = new int[][] { { 2, 2, 2, 2, 2 }, { 2, 2, 2, 2, 2 }, { 2, 2, 2, 2, 2 }, { 2, 2, 2, 2, 2 }, { 2, 2, 2, 2, 2 } };
    }),

    MEGA(new int[7][5][7], structure -> {
        structure[0] = new int[][] { { 2, 2, 2, 2, 2, 2, 2 }, { 2, 2, 2, 2, 2, 2, 2 }, { 2, 2, 2, 2, 2, 2, 2 }, { 2, 2, 2, 2, 2, 2, 2 }, { 2, 2, 2, 2, 2, 2, 2 }, { 2, 2, 2, 2, 2, 2, 2 } };
        structure[1] = new int[][] { { 2, 1, 1, 1, 1, 1, 2 }, { 2, 0, 0, 0, 0, 0, 2 }, { 2, 0, 0, 0, 0, 0, 2 }, { 2, 0, 0, 0, 0, 0, 2 }, { 2, 0, 0, 0, 0, 0, 2 }, { 2, 1, 1, 1, 1, 1, 2 } };
        structure[2] = new int[][] { { 2, 1, 1, 1, 1, 1, 2 }, { 2, 0, 0, 0, 0, 0, 2 }, { 2, 0, 0, 0, 0, 0, 2 }, { 2, 0, 0, 0, 0, 0, 2 }, { 2, 0, 0, 0, 0, 0, 2 }, { 2, 1, 1, 1, 1, 1, 2 } };
        structure[3] = new int[][] { { 2, 1, 1, 1, 1, 1, 2 }, { 2, 0, 0, 0, 0, 0, 2 }, { 2, 0, 0, 0, 0, 0, 2 }, { 2, 0, 0, 0, 0, 0, 2 }, { 2, 0, 0, 0, 0, 0, 2 }, { 2, 1, 1, 1, 1, 1, 2 } };
        structure[4] = new int[][] { { 2, 1, 1, 1, 1, 1, 2 }, { 2, 0, 0, 0, 0, 0, 2 }, { 2, 0, 0, 0, 0, 0, 2 }, { 2, 0, 0, 0, 0, 0, 2 }, { 2, 0, 0, 0, 0, 0, 2 }, { 2, 1, 1, 1, 1, 1, 2 } };
        structure[5] = new int[][] { { 2, 1, 1, 1, 1, 1, 2 }, { 2, 0, 0, 0, 0, 0, 2 }, { 2, 0, 0, 0, 0, 0, 2 }, { 2, 0, 0, 0, 0, 0, 2 }, { 2, 0, 0, 0, 0, 0, 2 }, { 2, 1, 1, 1, 1, 1, 2 } };
        structure[6] = new int[][] { { 2, 2, 2, 2, 2, 2, 2 }, { 2, 2, 2, 2, 2, 2, 2 }, { 2, 2, 2, 2, 2, 2, 2 }, { 2, 2, 2, 2, 2, 2, 2 }, { 2, 2, 2, 2, 2, 2, 2 }, { 2, 2, 2, 2, 2, 2, 2 } };
    }),

    ;

    private final int[][][] structure;

    CageStructure(int[][][] structure, Callback<int[][][]> callback) {
        this.structure = structure;
        callback.call(structure);
    }

    public int getOffset() {
        return (structure.length-1)/2;
    }

    public int[][][] getStructure() {
        return structure;
    }

    public void createCage(Provider<NMS> nms, Cage cageType, Location location) {        //loop through x values
        for (int x = 0; x < structure.length; x++) {
            //loop through y values for the current x value
            for (int y = 0; y < structure[x].length; y++) {
                //loop through all z values for the current x and y value
                for (int z = 0; z < structure[x][y].length; z++) {
                    Location offsetLocation = location.clone();
                    //goes to new location in relation to start
                    offsetLocation.add(x, y, z);
                    Block block = offsetLocation.getBlock();
                    //checks if the block is not air
                    if (structure[x][y][z] != 0) {
                        //if it isn't it will replace it with the id at the x y z coordinate
                        switch (structure[x][y][z]) {
                            case 1:
                                nms.get().setBlock(block, cageType.getInnerMaterial());
                                break;
                            case 2:
                                nms.get().setBlock(block, cageType.getOuterMaterial());
                                break;
                        }
                    }
                }
            }
        }
    }
}
