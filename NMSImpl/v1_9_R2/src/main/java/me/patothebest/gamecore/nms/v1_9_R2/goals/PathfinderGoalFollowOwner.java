package me.patothebest.gamecore.nms.v1_9_R2.goals;

import net.minecraft.server.v1_9_R2.BlockPosition;
import net.minecraft.server.v1_9_R2.EntityInsentient;
import net.minecraft.server.v1_9_R2.EntityLiving;
import net.minecraft.server.v1_9_R2.IBlockData;
import net.minecraft.server.v1_9_R2.Material;
import net.minecraft.server.v1_9_R2.MathHelper;
import net.minecraft.server.v1_9_R2.NavigationAbstract;
import net.minecraft.server.v1_9_R2.PathEntity;
import net.minecraft.server.v1_9_R2.PathType;
import net.minecraft.server.v1_9_R2.PathfinderGoal;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;

import java.util.UUID;

public class PathfinderGoalFollowOwner extends PathfinderGoal {

    private final EntityInsentient entity;
    private PathEntity path;
    private final UUID playerUUID;
    private EntityLiving enityOwner;
    private final NavigationAbstract navigation;
    private int someNumber;
    private float i;

    public PathfinderGoalFollowOwner(EntityInsentient entitycreature, UUID playerUUID) {
        this.entity = entitycreature;
        this.playerUUID = playerUUID;
        this.navigation = entity.getNavigation();
    }

    @Override
    public boolean a() {
        if(this.entity.getGoalTarget() != null) {
            return false;
        }

        if (Bukkit.getPlayer(playerUUID) == null) {
            enityOwner = null;
            return path != null;
        }

        enityOwner = ((CraftPlayer) Bukkit.getPlayer(playerUUID)).getHandle();

        return !(this.entity.h(enityOwner) < (double) (5.0f * 5.0f));
    }

    @Override
    public boolean b() {
        return !this.navigation.n() && this.entity.h(this.enityOwner) > (double) (2.0f * 2.0f);
    }

    @Override
    public void c() {
        this.someNumber = 0;
        this.i = this.entity.a(PathType.WATER);
        this.entity.a(PathType.WATER, 0.0F);
    }

    @Override
    public void d() {
        this.enityOwner = null;
        this.navigation.o();
        this.entity.a(PathType.WATER, this.i);
    }

    @Override
    public void e() {
        this.entity.getControllerLook().a(this.enityOwner, 10.0F, (float) this.entity.N());
        if (--this.someNumber <= 0) {
            this.someNumber = 10;
            if (!this.navigation.a(this.enityOwner, 1.0D)) {
                if (this.entity.h(this.enityOwner) >= 144.0D) {
                    int locationX = MathHelper.floor(this.enityOwner.locX) - 2;
                    int locationZ = MathHelper.floor(this.enityOwner.locZ) - 2;
                    int boundingBox = MathHelper.floor(this.enityOwner.getBoundingBox().b);

                    for (int x = 0; x <= 4; ++x) {
                        for (int z = 0; z <= 4; ++z) {
                            if ((x < 1 || z < 1 || x > 3 || z > 3) && this.enityOwner.getWorld().getType(new BlockPosition(locationX + x, boundingBox - 1, locationZ + z)).q() && this.a(new BlockPosition(locationX + x, boundingBox, locationZ + z)) && this.a(new BlockPosition(locationX + x, boundingBox + 1, locationZ + z))) {
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

    private boolean a(BlockPosition var1) {
        IBlockData var2 = this.enityOwner.getWorld().getType(var1);
        return var2.getMaterial() == Material.AIR || !var2.h();
    }
}