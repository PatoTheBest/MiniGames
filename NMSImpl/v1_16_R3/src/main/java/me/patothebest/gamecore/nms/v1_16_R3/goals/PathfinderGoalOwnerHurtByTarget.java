package me.patothebest.gamecore.nms.v1_16_R3.goals;

import net.minecraft.server.v1_16_R3.EntityCreature;
import net.minecraft.server.v1_16_R3.EntityLiving;
import net.minecraft.server.v1_16_R3.PathfinderGoalTarget;
import net.minecraft.server.v1_16_R3.PathfinderTargetCondition;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.EnumSet;

public class PathfinderGoalOwnerHurtByTarget extends PathfinderGoalTarget {

    private static final PathfinderTargetCondition targetCondition = (new PathfinderTargetCondition()).c().e();
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
        int i = owner.da();
        return i != this.c && this.a(this.target, targetCondition);
    }

    public void c() {
        this.e.setGoalTarget(this.target, EntityTargetEvent.TargetReason.TARGET_ATTACKED_OWNER, true);
        this.c = owner.da();
        super.c();
    }
}
