package me.patothebest.gamecore.nms.v1_13_R2;

import com.google.common.collect.Sets;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.nms.v1_13_R2.goals.PathfinderGoalBlazeFireball;
import me.patothebest.gamecore.nms.v1_13_R2.goals.PathfinderGoalFollowOwner;
import me.patothebest.gamecore.nms.v1_13_R2.goals.PathfinderGoalGhastAttackTarget;
import me.patothebest.gamecore.nms.v1_13_R2.goals.PathfinderGoalOwnerHurtByTarget;
import me.patothebest.gamecore.nms.v1_13_R2.goals.PathfinderGoalOwnerHurtTarget;
import me.patothebest.gamecore.nms.NMS;
import me.patothebest.gamecore.nms.v1_13_R2.goals.PathfinderGoalShulkerShoot;
import me.patothebest.gamecore.util.Utils;
import net.minecraft.server.v1_13_R2.BlockPosition;
import net.minecraft.server.v1_13_R2.DragonControllerPhase;
import net.minecraft.server.v1_13_R2.Entity;
import net.minecraft.server.v1_13_R2.EntityArmorStand;
import net.minecraft.server.v1_13_R2.EntityBlaze;
import net.minecraft.server.v1_13_R2.EntityCreature;
import net.minecraft.server.v1_13_R2.EntityCreeper;
import net.minecraft.server.v1_13_R2.EntityEnderDragon;
import net.minecraft.server.v1_13_R2.EntityGhast;
import net.minecraft.server.v1_13_R2.EntityInsentient;
import net.minecraft.server.v1_13_R2.EntityItem;
import net.minecraft.server.v1_13_R2.EntityShulker;
import net.minecraft.server.v1_13_R2.EntitySkeletonAbstract;
import net.minecraft.server.v1_13_R2.EntityWitch;
import net.minecraft.server.v1_13_R2.NBTTagCompound;
import net.minecraft.server.v1_13_R2.PathfinderGoalArrowAttack;
import net.minecraft.server.v1_13_R2.PathfinderGoalBowShoot;
import net.minecraft.server.v1_13_R2.PathfinderGoalFloat;
import net.minecraft.server.v1_13_R2.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_13_R2.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_13_R2.PathfinderGoalSelector;
import net.minecraft.server.v1_13_R2.PathfinderGoalSwell;
import net.minecraft.server.v1_13_R2.TileEntityChest;
import net.minecraft.server.v1_13_R2.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_13_R2.util.CraftChatMessage;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.util.UUID;

public class NMSImpl implements NMS {

    private Field gsa;
    private Field goalSelector;
    private Field targetSelector;

    public NMSImpl() {
        try {
            gsa = PathfinderGoalSelector.class.getDeclaredField("b");
            gsa.setAccessible(true);

            goalSelector = EntityInsentient.class.getDeclaredField("goalSelector");
            goalSelector.setAccessible(true);

            targetSelector = EntityInsentient.class.getDeclaredField("targetSelector");
            targetSelector.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    public void makePet(LivingEntity entity, UUID toFollow, String name) {
        try {
            Entity nms_entity = ((CraftLivingEntity) entity).getHandle();
            entity.setCanPickupItems(true);
            if (nms_entity instanceof EntityInsentient) {
                nms_entity.setCustomName(CraftChatMessage.fromStringOrNull(name));
                nms_entity.setCustomNameVisible(true);

                PathfinderGoalSelector goal = (PathfinderGoalSelector) goalSelector.get(nms_entity);
                PathfinderGoalSelector target = (PathfinderGoalSelector) targetSelector.get(nms_entity);

                Utils.setFinalField(gsa, goal, Sets.newLinkedHashSet());
                Utils.setFinalField(gsa, target, Sets.newLinkedHashSet());

                goal.a(1, new PathfinderGoalFloat((EntityInsentient) nms_entity));
                goal.a(2, new PathfinderGoalFollowOwner((EntityInsentient) nms_entity, toFollow));

                boolean meleeAttack = false;

                if(nms_entity instanceof EntitySkeletonAbstract) {
                    goal.a(4, new PathfinderGoalBowShoot((EntitySkeletonAbstract) nms_entity, 1.0D, 20, 15.0F));
                } else if(nms_entity instanceof EntityWitch) {
                    goal.a(4, new PathfinderGoalArrowAttack((EntityWitch) nms_entity, 1.0D, 60, 10.0F));
                } else if(nms_entity instanceof EntityShulker) {
                    goal.a(4, new PathfinderGoalShulkerShoot((EntityShulker) nms_entity));
                } else if(nms_entity instanceof EntityBlaze) {
                    goal.a(4, new PathfinderGoalBlazeFireball((EntityBlaze) nms_entity));
                } else if(nms_entity instanceof EntityCreeper) {
                    goal.a(4, new PathfinderGoalSwell((EntityCreeper) nms_entity));
                    goal.a(5, new PathfinderGoalMeleeAttack((EntityCreature) nms_entity, 1.0D, false));
                } else if(nms_entity instanceof EntityGhast) {
                    goal.a(4, new PathfinderGoalGhastAttackTarget((EntityGhast) nms_entity));
                } else  {
                    meleeAttack = true;
                }

                if(nms_entity instanceof EntityCreature) {
                    if(meleeAttack) {
                        goal.a(4, new PathfinderGoalMeleeAttack((EntityCreature) nms_entity, 1.0D, false));
                    }

                    target.a(1, new PathfinderGoalOwnerHurtByTarget(((CraftPlayer) Bukkit.getPlayer(toFollow)).getHandle(), (EntityCreature) nms_entity));
                    target.a(2, new PathfinderGoalOwnerHurtTarget(((CraftPlayer)Bukkit.getPlayer(toFollow)).getHandle(), (EntityCreature) nms_entity));
                    target.a(3, new PathfinderGoalHurtByTarget((EntityCreature) nms_entity, true));
                }
            } else {
                throw new IllegalArgumentException(entity.getType().getName() + " is not an instance of an EntityInsentient.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public EnderDragon spawnEnderdragon(Location location) {
        World w = ((CraftWorld) location.getWorld()).getHandle();
        EntityEnderDragon dragon = new EntityEnderDragon(w);
        dragon.getDragonControllerManager().setControllerPhase(DragonControllerPhase.HOLDING_PATTERN);
        dragon.setLocation(location.getX(), location.getY(), location.getZ(), w.random.nextFloat() * 360.0F, 0.0F);
        w.addEntity(dragon);
        return (EnderDragon) dragon.getBukkitEntity();
    }

    @Override
    public ArmorStand spawnProvisionalArmorStand(Location location) {
        EntityArmorStand entityArmorStand = new EntityArmorStand(((CraftWorld) location.getWorld()).getHandle()) {
            // disable saving
            @Override
            public boolean c(NBTTagCompound nbttagcompound) {
                return false;
            }

            // disable saving
            @Override
            public boolean d(NBTTagCompound nbttagcompound) {
                return false;
            }
        };

        entityArmorStand.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        ((CraftWorld) location.getWorld()).getHandle().addEntity(entityArmorStand, CreatureSpawnEvent.SpawnReason.CUSTOM);
        return (ArmorStand) entityArmorStand.getBukkitEntity();
    }

    @Override
    public void setPositionAndRotation(org.bukkit.entity.Entity entity, double posX, double posY, double posZ, float pitch, float yaw) {
        ((CraftEntity)entity).getHandle().setPositionRotation(posX, posY, posZ, pitch, yaw);
    }

    @Override
    public void playChestAction(Block b, boolean open) {
        Location location = b.getLocation();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        World world = ((CraftWorld) location.getWorld()).getHandle();
        TileEntityChest tileChest = (TileEntityChest) world.getTileEntity(new BlockPosition(x, y, z));
        world.playBlockAction(new BlockPosition(x, y, z), tileChest.getBlock().getBlock(), 1, open ? 1 : 0);
    }

    @Override
    public org.bukkit.entity.Entity spawnItem(org.bukkit.inventory.ItemStack itemStack, Location blockLocation) {
        EntityItem ei = new EntityItem(
                ((CraftWorld)blockLocation.clone().add(0.5D, 1.2D, 0.5D).getWorld()).getHandle(),
                blockLocation.clone().add(0.5D, 1.2D, 0.5D).getX(),
                blockLocation.clone().add(0.5D, 1.2D, 0.5D).getY(),
                blockLocation.clone().add(0.5D, 1.2D, 0.5D).getZ(),
                CraftItemStack.asNMSCopy(itemStack)) {

            private boolean a(EntityItem entityitem) {
                return false;
            }
        };
        ei.getBukkitEntity().setVelocity(new Vector(0.0D, 0.25D, 0.0D));
        ei.pickupDelay = Integer.MAX_VALUE;
        ei.getBukkitEntity().setCustomName(UUID.randomUUID().toString());
        ei.pickupDelay = 20;

        ((CraftWorld) blockLocation.clone().add(0.5D, 1.2D, 0.5D).getWorld()).getHandle().addEntity(ei);

        return ei.getBukkitEntity();
    }

    @Override
    public EntityType getEntityFromEgg(ItemStack itemStack) {
        if (!itemStack.getType().name().contains("SPAWN_EGG")) {
            return null;
        }

        if (itemStack.getType() == org.bukkit.Material.ZOMBIE_PIGMAN_SPAWN_EGG) {
            return EntityType.PIG_ZOMBIE;
        }

        return EntityType.valueOf(itemStack.getType().name().replace("_SPAWN_EGG", ""));
    }

    @Override
    public void setBlock(Block block, ItemStack itemStack) {
        block.setType(itemStack.getType(), false);
    }

    @Override
    public Block getBlockAttachedToSign(Block sign) {
        if (!(sign.getState().getBlockData() instanceof WallSign)) {
            return sign.getRelative(BlockFace.DOWN);
        }

        WallSign s = (WallSign) sign.getState().getBlockData();
        return sign.getRelative(s.getFacing().getOppositeFace());
    }

    @Override
    public Enchantment getGlowEnchant() {
        return EnchantGlow.getGlow();
    }


    @Override
    public void setDirectionalBlockData(Block block, BlockFace dir, boolean upsidedown) {
        Directional directional = (Directional) block.getBlockData();
        directional.setFacing(dir.getOppositeFace());
        block.setBlockData(directional);

        if (upsidedown) {
            Bisected bisected = (Bisected) block.getBlockData();
            bisected.setHalf(Bisected.Half.TOP);
            block.setBlockData(bisected);
        }
    }

    @Override
    public FallingBlock spawnFallingBlock(Location location, Material material) {
        return location.getWorld().spawnFallingBlock(location, Bukkit.createBlockData(material.parseMaterial()));
    }
}
