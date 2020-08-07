package me.patothebest.gamecore.nms.v1_8_R1.goals;

import net.minecraft.server.v1_8_R1.Block;
import net.minecraft.server.v1_8_R1.BlockPosition;
import net.minecraft.server.v1_8_R1.Blocks;
import net.minecraft.server.v1_8_R1.EntityInsentient;
import net.minecraft.server.v1_8_R1.EntityLiving;
import net.minecraft.server.v1_8_R1.IBlockData;
import net.minecraft.server.v1_8_R1.MathHelper;
import net.minecraft.server.v1_8_R1.Navigation;
import net.minecraft.server.v1_8_R1.NavigationAbstract;
import net.minecraft.server.v1_8_R1.PathEntity;
import net.minecraft.server.v1_8_R1.PathfinderGoal;
import net.minecraft.server.v1_8_R1.World;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;

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
        return !this.navigation.m() && this.entity.h(this.enityOwner) > (double) (2.0f * 2.0f);
    }

    @Override
    public void c() {
        this.i = 0;
        ((Navigation)this.entity.getNavigation()).e();
        ((Navigation)this.entity.getNavigation()).a(false);
    }

    @Override
    public void d() {
        this.navigation.n();
        ((Navigation)this.entity.getNavigation()).a(true);
    }

    @Override
    public void e() {
        this.entity.getControllerLook().a(this.enityOwner, 10.0F, (float) this.entity.bP());
        if (--this.someNumber <= 0) {
            this.someNumber = 10;
            if (!this.navigation.a(this.enityOwner, 1.0D)) {
                if (this.entity.h(this.enityOwner) >= 144.0D) {
                    int locationX = MathHelper.floor(this.enityOwner.locX) - 2;
                    int locationZ = MathHelper.floor(this.enityOwner.locZ) - 2;
                    int boundingBox = MathHelper.floor(this.enityOwner.getBoundingBox().b);

                    for (int x = 0; x <= 4; ++x) {
                        for (int z = 0; z <= 4; ++z) {
                            if ((x < 1 || z < 1 || x > 3 || z > 3) && World.a(this.enityOwner.getWorld(), new BlockPosition(locationX + x, boundingBox - 1, locationZ + z)) && this.a(new BlockPosition(locationX + x, boundingBox, locationZ + z)) && this.a(new BlockPosition(locationX + x, boundingBox + 1, locationZ + z))) {
                                this.entity.setPositionRotation((float) (locationX + x) + 0.5F, boundingBox, (float) (locationZ + z) + 0.5F, this.entity.yaw, this.entity.pitch);
                                this.navigation.n();
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean a(BlockPosition var1) {
        IBlockData var2 = this.entity.world.getType(var1);
        Block var3 = var2.getBlock();
        return var3 == Blocks.AIR || !var3.d();
    }
}