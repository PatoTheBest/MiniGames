package me.patothebest.gamecore.nms;

import me.patothebest.gamecore.hologram.Hologram;
import me.patothebest.gamecore.itemstack.Material;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * The Interface NMS.
 */
public interface NMS {

    /**
     * Makes a pet.
     *
     * @param entity the entity
     * @param toFollow the player to follow
     * @param name the name of the pet
     */
    void makePet(LivingEntity entity, UUID toFollow, String name);

    /**
     * Gets the entity type from an egg.
     *
     * @param itemStack the monster egg item
     * @return the entity from egg
     */
    EntityType getEntityFromEgg(ItemStack itemStack);

    EnderDragon spawnEnderdragon(Location location);

    default void setPosition(Entity entity, Location location) {
        setPositionAndRotation(entity, location.getX(), location.getY(), location.getZ(), location.getPitch(), location.getYaw());
    }

    default void setPosition(Entity entity, double posX, double posY, double posZ) {
        setPositionAndRotation(entity, posX, posY, posZ, 0, 0);
    }

    ArmorStand spawnProvisionalArmorStand(Location location);

    default Hologram createHologram(Location location) {
        return new Hologram(spawnProvisionalArmorStand(location));
    }

    void setPositionAndRotation(Entity entity, double posX, double posY, double posZ, float pitch, float yaw);

    void playChestAction(Block b, boolean open);

    org.bukkit.entity.Entity spawnItem(ItemStack itemStack, Location blockLocation);

    void setBlock(Block block, ItemStack itemStack);

    Block getBlockAttachedToSign(Block sign);

    Enchantment getGlowEnchant();

    void setDirectionalBlockData(Block block, BlockFace dir, boolean upsidedown);

    FallingBlock spawnFallingBlock(Location location, Material material);
}
