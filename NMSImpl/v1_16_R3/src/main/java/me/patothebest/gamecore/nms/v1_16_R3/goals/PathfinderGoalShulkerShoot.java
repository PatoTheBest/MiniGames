package me.patothebest.gamecore.nms.v1_16_R3.goals;

import net.minecraft.server.v1_16_R3.EntityLiving;
import net.minecraft.server.v1_16_R3.EntityShulker;
import net.minecraft.server.v1_16_R3.EntityShulkerBullet;
import net.minecraft.server.v1_16_R3.EnumDifficulty;
import net.minecraft.server.v1_16_R3.PathfinderGoal;
import net.minecraft.server.v1_16_R3.SoundEffects;

import java.util.EnumSet;
import java.util.Random;

public class PathfinderGoalShulkerShoot extends PathfinderGoal {

    private final EntityShulker entityShulker;
    private final Random random = new Random();
    private int b;

    public PathfinderGoalShulkerShoot(EntityShulker entityShulker) {
        this.entityShulker = entityShulker;
        this.a(EnumSet.of(Type.MOVE, Type.LOOK));
    }

    public boolean a() {
        EntityLiving var1 = entityShulker.getGoalTarget();
        return (var1 != null && var1.isAlive()) && entityShulker.world.getDifficulty() != EnumDifficulty.PEACEFUL;
    }

    public void c() {
        this.b = 20;
        entityShulker.a(100);
    }

    public void d() {
        entityShulker.a(0);
    }

    public void e() {
        if (entityShulker.world.getDifficulty() != EnumDifficulty.PEACEFUL) {
            --this.b;
            EntityLiving var1 = entityShulker.getGoalTarget();
            entityShulker.getControllerLook().a(var1, 180.0F, 180.0F);
            double var2 = entityShulker.h(var1);
            if (var2 < 400.0D) {
                if (this.b <= 0) {
                    this.b = 20 + random.nextInt(10) * 20 / 2;
                    EntityShulkerBullet var4 = new EntityShulkerBullet(entityShulker.world, entityShulker, var1, entityShulker.eL().n());
                    entityShulker.world.addEntity(var4);
                    entityShulker.playSound(SoundEffects.ENTITY_SHULKER_SHOOT, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
                }
            } else {
                entityShulker.setGoalTarget(null);
            }

            super.e();
        }
    }
}