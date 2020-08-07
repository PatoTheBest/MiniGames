package me.patothebest.gamecore.model;

import me.patothebest.gamecore.util.Utils;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class ModelPiece {

    private final ArmorStand armorStand;
    private final AbstractModel abstractModel;
    private final Vector offset;
    private final ItemStack blockType;

    public ModelPiece(AbstractModel abstractModel, Vector offset, ItemStack blockType) {
        this.abstractModel = abstractModel;
        this.offset = offset.multiply(0.6);
        this.blockType = blockType;

        armorStand = (ArmorStand) abstractModel.originLocation.getWorld().spawnEntity(abstractModel.getLocation(), EntityType.ARMOR_STAND);
        armorStand.setGravity(false);
        armorStand.setVisible(false);
//        armorStand.setInvulnerable(true);
        armorStand.setHelmet(blockType);
        Utils.setAiEnabled(armorStand, false);
        updateLocation();
    }

    public void updateLocation() {
        Location location = abstractModel.getLocation();
        Vector tmpOffset = offset.clone();

        Utils.rotateX(tmpOffset, Math.toRadians(-location.getPitch()));
        Utils.rotateY(tmpOffset, Math.toRadians(-location.getYaw()));

        location.add(tmpOffset).add(0, -0.75, 0);

        armorStand.setHeadPose(new EulerAngle(Math.toRadians(abstractModel.getLocation().getPitch()), 0, 0));
        abstractModel.nms.setPositionAndRotation(armorStand, location.getX(), location.getY(), location.getZ(), location.getYaw(), 0);
    }

    public void destroy() {
        armorStand.remove();
    }
}
