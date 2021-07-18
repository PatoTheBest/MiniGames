package me.patothebest.gamecore.nms.v1_15_R1.goals;

import net.minecraft.server.v1_15_R1.EntityCreature;
import net.minecraft.server.v1_15_R1.EntityLiving;
import net.minecraft.server.v1_15_R1.PathfinderGoalTarget;
import net.minecraft.server.v1_15_R1.PathfinderTargetCondition;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.EnumSet;

public class PathfinderGoalOwnerHurtByTarget extends PathfinderGoalTarget {

    private final EntityLiving owner;
    private EntityLiving target;
    private int c;

    public PathfinderGoalOwnerHurtByTarget(EntityLiving owner, EntityCreature entityCreature) {
        super(entityCreature, false);
        this.owner = owner;
        this.a(EnumSet.of(Type.TARGET));
    }

    public boolean a() {
        this.target = owner.getLastDamager();
        int i = owner.cI();
        return i != this.c && this.a(this.target, PathfinderTargetCondition.a);
    }

    public void c() {
        this.e.setGoalTarget(this.target, EntityTargetEvent.TargetReason.TARGET_ATTACKED_OWNER, true);
        this.c = owner.cI();
        super.c();
    }
}
