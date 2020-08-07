package me.patothebest.gamecore.cosmetics.cage.model;

import me.patothebest.gamecore.cosmetics.cage.CageStructure;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.model.AbstractModel;
import me.patothebest.gamecore.nms.NMS;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class CageModel extends AbstractModel {

    private final CageStructure cage;

    public CageModel(NMS nms, CageStructure cage, Location location) {
        this.nms = nms;
        this.cage = cage;
        this.originLocation = location;
    }

    @Override
    public void spawn() {
        //loop through x values
        for (int x = 0; x < cage.getStructure().length; x++) {
            //loop through y values for the current x value
            for (int y = 0; y < cage.getStructure()[x].length; y++) {
                //loop through all z values for the current x and y value
                for (int z = 0; z < cage.getStructure()[x][y].length; z++) {
                    //checks if the block is not air
                    if (cage.getStructure()[x][y][z] != 0) {
                        Vector offset = new Vector(x - cage.getOffset(), y - 1, z - cage.getOffset());
                        addPiece(offset, new ItemStackBuilder(Material.GLASS));
                    }
                }
            }
        }
    }


}
