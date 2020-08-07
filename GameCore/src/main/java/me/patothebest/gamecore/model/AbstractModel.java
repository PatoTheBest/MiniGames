package me.patothebest.gamecore.model;

import me.patothebest.gamecore.nms.NMS;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractModel {

    private final List<ModelPiece> modelPieces = new ArrayList<>();
    protected Location originLocation;
    protected NMS nms;

    public abstract void spawn();

    public void addPiece(Vector offset, ItemStack blockType) {
        modelPieces.add(new ModelPiece(this, offset, blockType));
    }

    public Location getLocation() {
        return originLocation.clone();
    }

    public void updateRotation(Location location) {
        originLocation.setPitch(location.getPitch());
        originLocation.setYaw(location.getYaw());
//        originLocation = location;

        modelPieces.forEach(ModelPiece::updateLocation);
    }

    public void destroy() {
        modelPieces.forEach(ModelPiece::destroy);
    }
}
