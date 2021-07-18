package me.patothebest.gamecore.nms.v1_16_R3.goals;

import net.minecraft.server.v1_16_R3.EntityGhast;
import net.minecraft.server.v1_16_R3.EntityLargeFireball;
import net.minecraft.server.v1_16_R3.EntityLiving;
import net.minecraft.server.v1_16_R3.PathfinderGoal;
import net.minecraft.server.v1_16_R3.Vec3D;
import net.minecraft.server.v1_16_R3.World;

public class PathfinderGoalGhastAttackTarget extends PathfinderGoal {

    private final EntityGhast ghast;
    public int a;

    public PathfinderGoalGhastAttackTarget(EntityGhast entityghast) {
        this.ghast = entityghast;
    }

    public boolean a() {
        return this.ghast.getGoalTarget() != null;
    }

    public void c() {
        this.a = 0;
    }

    public void d() {
        this.ghast.t(false);
    }

    public void e() {
        EntityLiving entityliving = this.ghast.getGoalTarget();
        if (entityliving.h(this.ghast) < 4096.0D && this.ghast.hasLineOfSight(entityliving)) {
            World world = this.ghast.world;
            ++this.a;
            if (this.a == 10) {
                world.a(null, 1015, this.ghast.getChunkCoordinates(), 0);
            }

            if (this.a == 20) {
                Vec3D vec3d = this.ghast.f(1.0F);
                double d2 = entityliving.locX() - (this.ghast.locX() + vec3d.x * 4.0D);
                double d3 = entityliving.getBoundingBox().minY + (double) (entityliving.getHeight() / 2.0F) - (0.5D + this.ghast.locY() + (double) (this.ghast.getHeight() / 2.0F));
                double d4 = entityliving.locZ() - (this.ghast.locZ() + vec3d.z * 4.0D);

                if (!this.ghast.isSilent()) {
                    world.a(null, 1016, this.ghast.getChunkCoordinates(), 0);
                }

                EntityLargeFireball entitylargefireball = new EntityLargeFireball(world, this.ghast, d2, d3, d4);
                entitylargefireball.bukkitYield = (float) (entitylargefireball.yield = this.ghast.getPower());
                entitylargefireball.setPosition(this.ghast.locX() + vec3d.x * 4.0D, this.ghast.e(0.5D) + 0.5D, entitylargefireball.locZ() + vec3d.z * 4.0D);
                world.addEntity(entitylargefireball);
                this.a = -40;
            }
        } else if (this.a > 0) {
            --this.a;
        }

        this.ghast.t(this.a > 10);
    }
}