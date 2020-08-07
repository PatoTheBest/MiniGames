package me.patothebest.gamecore.nms.v1_16_R1.goals;

import me.patothebest.gamecore.util.Utils;
import net.minecraft.server.v1_16_R1.DataWatcherObject;
import net.minecraft.server.v1_16_R1.EntityBlaze;
import net.minecraft.server.v1_16_R1.EntityHuman;
import net.minecraft.server.v1_16_R1.EntityLiving;
import net.minecraft.server.v1_16_R1.EntitySmallFireball;
import net.minecraft.server.v1_16_R1.GenericAttributes;
import net.minecraft.server.v1_16_R1.MathHelper;
import net.minecraft.server.v1_16_R1.PathfinderGoal;

import java.util.EnumSet;

public class PathfinderGoalBlazeFireball extends PathfinderGoal {

    private static final DataWatcherObject<Byte> dataWatcherObject = Utils.getFieldValue(EntityBlaze.class, "d", null);
    private final EntityBlaze a;
    private int b;
    private int c;
    private int d;

    public PathfinderGoalBlazeFireball(EntityBlaze var0) {
        this.a = var0;
        this.a(EnumSet.of(Type.MOVE, Type.LOOK));
    }

    public boolean a() {
        EntityLiving var0 = this.a.getGoalTarget();
        return var0 != null && var0.isAlive() && this.a.d(var0);
    }

    public void c() {
        this.b = 0;
    }

    public void d() {
        t(false);
        this.d = 0;
    }

    public void e() {
        --this.c;
        EntityLiving var0 = this.a.getGoalTarget();
        if (var0 != null) {
            boolean var1 = this.a.getEntitySenses().a(var0);
            if (var1) {
                this.d = 0;
            } else {
                ++this.d;
            }

            double var2 = this.a.h(var0);
            if (var2 < 4.0D) {
                if (!var1) {
                    return;
                }

                if (this.c <= 0) {
                    this.c = 20;
                    this.a.attackEntity(var0);
                }

                this.a.getControllerMove().a(var0.locX(), var0.locY(), var0.locZ(), 1.0D);
            } else if (var2 < this.g() * this.g() && var1) {
                double var4 = var0.locX() - this.a.locX();
                double var6 = var0.e(0.5D) - this.a.e(0.5D);
                double var8 = var0.locZ() - this.a.locZ();
                if (this.c <= 0) {
                    ++this.b;
                    if (this.b == 1) {
                        this.c = 60;
                        t(true);
                    } else if (this.b <= 4) {
                        this.c = 6;
                    } else {
                        this.c = 100;
                        this.b = 0;
                        t(false);
                    }

                    if (this.b > 1) {
                        float var10 = MathHelper.c(MathHelper.sqrt(var2)) * 0.5F;
                        if (!this.a.isSilent()) {
                            this.a.world.a((EntityHuman)null, 1018, this.a.getChunkCoordinates(), 0);
                        }

                        for(int var11 = 0; var11 < 1; ++var11) {
                            EntitySmallFireball var12 = new EntitySmallFireball(this.a.world, this.a, var4 + this.a.getRandom().nextGaussian() * (double)var10, var6, var8 + this.a.getRandom().nextGaussian() * (double)var10);
                            var12.setPosition(var12.locX(), this.a.e(0.5D) + 0.5D, var12.locZ());
                            this.a.world.addEntity(var12);
                        }
                    }
                }

                this.a.getControllerLook().a(var0, 10.0F, 10.0F);
            } else if (this.d < 5) {
                this.a.getControllerMove().a(var0.locX(), var0.locY(), var0.locZ(), 1.0D);
            }

            super.e();
        }
    }

    private double g() {
        return this.a.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).getValue();
    }

    private void t(boolean var0) {
        byte var1 = a.getDataWatcher().get(dataWatcherObject);
        if (var0) {
            var1 = (byte) (var1 | 1);
        } else {
            var1 &= -2;
        }

        a.getDataWatcher().set(dataWatcherObject, var1);
    }
}