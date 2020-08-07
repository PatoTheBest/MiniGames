package me.patothebest.gamecore.nms.v1_10_R1;

import com.google.common.collect.Sets;
import me.patothebest.gamecore.nms.v1_10_R1.goals.PathfinderGoalBlazeFireball;
import me.patothebest.gamecore.nms.v1_10_R1.goals.PathfinderGoalFollowOwner;
import me.patothebest.gamecore.nms.v1_10_R1.goals.PathfinderGoalGhastAttackTarget;
import me.patothebest.gamecore.nms.v1_10_R1.goals.PathfinderGoalOwnerHurtByTarget;
import me.patothebest.gamecore.nms.v1_10_R1.goals.PathfinderGoalOwnerHurtTarget;
import me.patothebest.gamecore.nms.v1_10_R1.goals.PathfinderGoalShulkerShoot;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.nms.NMS;
import me.patothebest.gamecore.util.Utils;
import net.minecraft.server.v1_10_R1.BlockPosition;
import net.minecraft.server.v1_10_R1.DragonControllerPhase;
import net.minecraft.server.v1_10_R1.Entity;
import net.minecraft.server.v1_10_R1.EntityArmorStand;
import net.minecraft.server.v1_10_R1.EntityBlaze;
import net.minecraft.server.v1_10_R1.EntityCreature;
import net.minecraft.server.v1_10_R1.EntityCreeper;
import net.minecraft.server.v1_10_R1.EntityEnderDragon;
import net.minecraft.server.v1_10_R1.EntityGhast;
import net.minecraft.server.v1_10_R1.EntityInsentient;
import net.minecraft.server.v1_10_R1.EntityItem;
import net.minecraft.server.v1_10_R1.EntityShulker;
import net.minecraft.server.v1_10_R1.EntitySkeleton;
import net.minecraft.server.v1_10_R1.EntityWitch;
import net.minecraft.server.v1_10_R1.NBTTagCompound;
import net.minecraft.server.v1_10_R1.PathfinderGoalArrowAttack;
import net.minecraft.server.v1_10_R1.PathfinderGoalBowShoot;
import net.minecraft.server.v1_10_R1.PathfinderGoalFloat;
import net.minecraft.server.v1_10_R1.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_10_R1.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_10_R1.PathfinderGoalSelector;
import net.minecraft.server.v1_10_R1.TileEntityChest;
import net.minecraft.server.v1_10_R1.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_10_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Directional;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Sign;
import org.bukkit.material.Stairs;
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
                nms_entity.setCustomName(name);
                nms_entity.setCustomNameVisible(true);


                PathfinderGoalSelector goal = (PathfinderGoalSelector) goalSelector.get(nms_entity);
                PathfinderGoalSelector target = (PathfinderGoalSelector) targetSelector.get(nms_entity);

                Utils.setFinalField(gsa, goal, Sets.newLinkedHashSet());
                Utils.setFinalField(gsa, target, Sets.newLinkedHashSet());

                goal.a(1, new PathfinderGoalFloat((EntityInsentient) nms_entity));
                goal.a(2, new PathfinderGoalFollowOwner((EntityInsentient) nms_entity, toFollow));

                boolean meleeAttack = false;

                if(nms_entity instanceof EntitySkeleton) {
                    goal.a(4, new PathfinderGoalBowShoot((EntitySkeleton) nms_entity, 1.0D, 20, 15.0F));
                } else if(nms_entity instanceof EntityWitch) {
                    goal.a(4, new PathfinderGoalArrowAttack((EntityWitch) nms_entity, 1.0D, 60, 10.0F));
                } else if(nms_entity instanceof EntityShulker) {
                    goal.a(4, new PathfinderGoalShulkerShoot((EntityShulker) nms_entity));
                } else if(nms_entity instanceof EntityBlaze) {
                    goal.a(4, new PathfinderGoalBlazeFireball((EntityBlaze) nms_entity));
                } else if(nms_entity instanceof EntityCreeper) {
                    Utils.setFieldValue(EntityCreeper.class, "maxFuseTicks", nms_entity, 1);
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
        dragon.getDragonControllerManager().setControllerPhase(DragonControllerPhase.a);
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
    public EntityType getEntityFromEgg(ItemStack itemStack) {
        if (itemStack.getType() != org.bukkit.Material.MONSTER_EGG) {
            return null;
        }

        net.minecraft.server.v1_10_R1.ItemStack stack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tagCompound = stack.getTag();
        if (tagCompound != null) {
            @SuppressWarnings("deprecation")
            EntityType type = EntityType.fromName(tagCompound.getCompound("EntityTag").getString("id"));
            return type;
        }

        return null;
    }

    @Override
    public void playChestAction(Block b, boolean open) {
        Location location = b.getLocation();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        World world = ((CraftWorld) location.getWorld()).getHandle();
        TileEntityChest tileChest = (TileEntityChest) world.getTileEntity(new BlockPosition(x, y, z));
        world.playBlockAction(new BlockPosition(x, y, z), tileChest.getBlock(), 1, open ? 1 : 0);
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
    public void setBlock(Block block, ItemStack itemStack) {
        block.setTypeIdAndData(itemStack.getTypeId(), (byte) itemStack.getDurability(), false);
    }

    @Override
    public Block getBlockAttachedToSign(Block sign) {
        Sign s = (Sign) sign.getState().getData();
        return sign.getRelative(s.getAttachedFace());
    }

    @Override
    public Enchantment getGlowEnchant() {
        return EnchantGlow.getGlow();
    }

    @Override
    public void setDirectionalBlockData(Block block, BlockFace dir, boolean upsideDown) {
        BlockState state = block.getState();
        MaterialData materialData = state.getData();
        Directional directional = (Directional) materialData;
        directional.setFacingDirection(dir);

        if (upsideDown) {
            Stairs stairs = (Stairs) materialData;
            stairs.setInverted(true);
        }

        state.setData(materialData);
        state.update();
    }

    @Override
    public FallingBlock spawnFallingBlock(Location location, Material material) {
        return location.getWorld().spawnFallingBlock(location, material.parseMaterial(), material.getData());
    }
}
