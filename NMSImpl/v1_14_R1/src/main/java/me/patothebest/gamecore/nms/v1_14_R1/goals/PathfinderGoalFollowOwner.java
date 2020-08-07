package me.patothebest.gamecore.nms.v1_14_R1.goals;

import net.minecraft.server.v1_14_R1.BlockPosition;
import net.minecraft.server.v1_14_R1.EntityInsentient;
import net.minecraft.server.v1_14_R1.EntityLiving;
import net.minecraft.server.v1_14_R1.IBlockData;
import net.minecraft.server.v1_14_R1.IWorldReader;
import net.minecraft.server.v1_14_R1.MathHelper;
import net.minecraft.server.v1_14_R1.NavigationAbstract;
import net.minecraft.server.v1_14_R1.PathEntity;
import net.minecraft.server.v1_14_R1.PathType;
import net.minecraft.server.v1_14_R1.PathfinderGoal;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;

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
        this.entity.getControllerLook().a(this.entityOwner, 10.0F, (float) this.entity.M());
        if (--this.someNumber <= 0) {
            this.someNumber = 10;
            if (!this.navigation.a(this.entityOwner, 1.0D)) {
                if (this.entity.h(this.entityOwner) >= 144.0D) {
                    int locationX = MathHelper.floor(this.entityOwner.locX) - 2;
                    int locationZ = MathHelper.floor(this.entityOwner.locZ) - 2;
                    int boundingBox = MathHelper.floor(this.entityOwner.getBoundingBox().minY);

                    for (int x = 0; x <= 4; ++x) {
                        for (int z = 0; z <= 4; ++z) {
                            if ((x < 1 || z < 1 || x > 3 || z > 3) && this.a(new BlockPosition(locationX + x, boundingBox + 1, locationZ + z))) {
                                this.entity.setPositionRotation((float) (locationX + x) + 0.5F, boundingBox, (float) (locationZ + z) + 0.5F, this.entity.yaw, this.entity.pitch);
                                this.navigation.o();
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    protected boolean a(BlockPosition blockposition) {
        IBlockData iblockdata = this.world.getType(blockposition);
        return iblockdata.a(this.world, blockposition, this.entity.getEntityType()) && this.world.isEmpty(blockposition.up()) && this.world.isEmpty(blockposition.up(2));
    }
}