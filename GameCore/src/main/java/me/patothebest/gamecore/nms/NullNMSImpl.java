package me.patothebest.gamecore.nms;

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

public class NullNMSImpl implements NMS {

    public void makePet(LivingEntity entity, UUID toFollow, String name) {
        // empty method to prevent null errors
    }

    @Override
    public EntityType getEntityFromEgg(ItemStack itemStack) {
        return null;
    }

    @Override
    public EnderDragon spawnEnderdragon(Location location) {
        return location.getWorld().spawn(location, EnderDragon.class);
    }

    @Override
    public ArmorStand spawnProvisionalArmorStand(Location location) {
        return null;
    }

    @Override
    public void setPositionAndRotation(Entity entity, double posX, double posY, double posZ, float pitch, float yaw) {

    }

    @Override
    public void playChestAction(Block b, boolean open) {
        
    }

    @Override
    public Entity spawnItem(ItemStack itemStack, Location blockLocation) {
        return null;
    }

    @Override
    public void setBlock(Block block, ItemStack itemStack) {

    }

    @Override
    public Block getBlockAttachedToSign(Block sign) {
        return null;
    }

    @Override
    public Enchantment getGlowEnchant() {
        return null;
    }

    @Override
    public void setDirectionalBlockData(Block block, BlockFace dir, boolean upsidedown) {

    }

    @Override
    public FallingBlock spawnFallingBlock(Location location, Material material) {
        return null;
    }
}
