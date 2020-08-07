package me.patothebest.gamecore.nms.v1_16_R1.goals;

import net.minecraft.server.v1_16_R1.BlockPosition;
import net.minecraft.server.v1_16_R1.EntityInsentient;
import net.minecraft.server.v1_16_R1.EntityLiving;
import net.minecraft.server.v1_16_R1.IWorldReader;
import net.minecraft.server.v1_16_R1.NavigationAbstract;
import net.minecraft.server.v1_16_R1.PathEntity;
import net.minecraft.server.v1_16_R1.PathType;
import net.minecraft.server.v1_16_R1.PathfinderGoal;
import net.minecraft.server.v1_16_R1.PathfinderNormal;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.event.entity.EntityTeleportEvent;

import java.util.UUID;

public class PathfinderGoalFollowOwner extends PathfinderGoal {

    protected final IWorldReader world;
    private final EntityInsentient entity;
    private PathEntity path;
    private final UUID playerUUID;
    private EntityLiving entityOwner;
    private final NavigationAbstract navigation;
    private int someNumber;
    private float i;

    public PathfinderGoalFollowOwner(EntityInsentient entitycreature, UUID playerUUID) {
        this.entity = entitycreature;
        this.world = entitycreature.world;
        this.playerUUID = playerUUID;
        this.navigation = entity.getNavigation();
    }

    @Override
    public boolean a() {
        if(this.entity.getGoalTarget() != null) {
            return false;
        }

        if (Bukkit.getPlayer(playerUUID) == null) {
            entityOwner = null;
            return path != null;
        }

        entityOwner = ((CraftPlayer) Bukkit.getPlayer(playerUUID)).getHandle();

        return !(this.entity.h(entityOwner) < (double) (5.0f * 5.0f));
    }

    @Override
    public boolean b() {
        return !this.navigation.n() && this.entity.h(this.entityOwner) > (double) (2.0f * 2.0f);
    }

    @Override
    public void c() {
        this.someNumber = 0;
        this.i = this.entity.a(PathType.WATER);
        this.entity.a(PathType.WATER, 0.0F);
    }

    @Override
    public void d() {
        this.entityOwner = null;
        this.navigation.q();
        this.entity.a(PathType.WATER, this.i);
    }

    @Override
    public void e() {
        this.entity.getControllerLook().a(this.entityOwner, 10.0F, (float) this.entity.eo());
        if (--this.someNumber <= 0) {
            this.someNumber = 10;
            if (this.entity.h(this.entityOwner) >= 144.0D) {
                this.g();
            } else {
                this.navigation.a(this.entityOwner, 1.0F);
            }
        }
    }

    private void g() {
        BlockPosition blockposition = entityOwner.getChunkCoordinates();

        for(int i = 0; i < 10; ++i) {
            int j = this.a(-3, 3);
            int k = this.a(-1, 1);
            int l = this.a(-3, 3);
            boolean flag = this.a(blockposition.getX() + j, blockposition.getY() + k, blockposition.getZ() + l);
            if (flag) {
                return;
            }
        }

    }

    private boolean a(int i, int j, int k) {
        if (Math.abs((double)i - this.entityOwner.locX()) < 2.0D && Math.abs((double)k - this.entityOwner.locZ()) < 2.0D) {
            return false;
        } else if (!this.a(new BlockPosition(i, j, k))) {
            return false;
        } else {
            CraftEntity entity = this.entity.getBukkitEntity();
            Location to = new Location(entity.getWorld(), (float)i + 0.5F, j, (float)k + 0.5F, this.entity.yaw, this.entity.pitch);
            EntityTeleportEvent event = new EntityTeleportEvent(entity, entity.getLocation(), to);
            this.entity.world.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return false;
            } else {
                to = event.getTo();
                this.entity.setPositionRotation(to.getX(), to.getY(), to.getZ(), to.getYaw(), to.getPitch());
                this.navigation.o();
                return true;
            }
        }
    }

    private boolean a(BlockPosition blockposition) {
        PathType pathtype = PathfinderNormal.a(this.world, blockposition.i());
        if (pathtype != PathType.WALKABLE) {
            return false;
        } else {
            BlockPosition blockposition1 = blockposition.b(new BlockPosition(this.entity.getChunkCoordinates()));
            return this.world.getCubes(this.entity, this.entity.getBoundingBox().a(blockposition1));
        }
    }

    private int a(int i, int j) {
        return this.entity.getRandom().nextInt(j - i + 1) + i;
    }

}