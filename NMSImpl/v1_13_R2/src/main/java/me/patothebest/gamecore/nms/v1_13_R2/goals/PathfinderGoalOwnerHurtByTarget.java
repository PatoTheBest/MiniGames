package me.patothebest.gamecore.nms.v1_13_R2.goals;

import net.minecraft.server.v1_13_R2.EntityCreature;
import net.minecraft.server.v1_13_R2.EntityLiving;
import net.minecraft.server.v1_13_R2.PathfinderGoalTarget;
import org.bukkit.event.entity.EntityTargetEvent;

public class PathfinderGoalOwnerHurtByTarget extends PathfinderGoalTarget {

    private final EntityLiving owner;
    private EntityLiving target;
    private int c;

    public PathfinderGoalOwnerHurtByTarget(EntityLiving owner, EntityCreature entityCreature) {
        super(entityCreature, false);
        this.owner = owner;
        this.a(1);
    }

    public boolean a() {
        this.target = owner.getLastDamager();
        int i = owner.cg();
        return i != this.c && this.a(this.target, false);
    }

    public void c() {
        this.e.setGoalTarget(this.target, EntityTargetEvent.TargetReason.TARGET_ATTACKED_OWNER, true);
        if(owner != null) {
            this.c = owner.cg();
        }

        super.c();
    }
}
