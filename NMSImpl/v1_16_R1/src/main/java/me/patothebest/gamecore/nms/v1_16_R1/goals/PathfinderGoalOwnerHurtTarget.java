package me.patothebest.gamecore.nms.v1_16_R1.goals;

import net.minecraft.server.v1_16_R1.EntityCreature;
import net.minecraft.server.v1_16_R1.EntityLiving;
import net.minecraft.server.v1_16_R1.PathfinderGoalTarget;
import net.minecraft.server.v1_16_R1.PathfinderTargetCondition;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.EnumSet;

public class PathfinderGoalOwnerHurtTarget extends PathfinderGoalTarget {

    private final EntityLiving owner;
    private EntityLiving target;
    private int number;

    public PathfinderGoalOwnerHurtTarget(EntityLiving owner, EntityCreature entityCreature) {
        super(entityCreature, false);
        this.owner = owner;
        this.a(EnumSet.of(Type.TARGET));
    }

    @Override
    public boolean a() {
        this.target = owner.getLastDamager();
        int i = owner.db();
        return i != this.number && this.a(this.target, PathfinderTargetCondition.a);
    }

    @Override
    public void c() {
        this.e.setGoalTarget(this.target, EntityTargetEvent.TargetReason.OWNER_ATTACKED_TARGET, true);
        this.number = owner.db();
        super.c();
    }
}
