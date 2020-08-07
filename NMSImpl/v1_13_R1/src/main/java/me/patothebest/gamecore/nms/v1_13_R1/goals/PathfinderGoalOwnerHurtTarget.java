package me.patothebest.gamecore.nms.v1_13_R1.goals;

import net.minecraft.server.v1_13_R1.EntityCreature;
import net.minecraft.server.v1_13_R1.EntityLiving;
import net.minecraft.server.v1_13_R1.PathfinderGoalTarget;
import org.bukkit.event.entity.EntityTargetEvent;

public class PathfinderGoalOwnerHurtTarget extends PathfinderGoalTarget {

    private final EntityLiving owner;
    private EntityLiving target;
    private int number;

    public PathfinderGoalOwnerHurtTarget(EntityLiving owner, EntityCreature entityCreature) {
        super(entityCreature, false);
        this.owner = owner;
        this.a(1);
    }

    @Override
    public boolean a() {
        this.target = owner.getLastDamager();
        int i = owner.cg();
        return i != this.number && this.a(this.target, false);
    }

    @Override
    public void c() {
        this.e.setGoalTarget(this.target, EntityTargetEvent.TargetReason.OWNER_ATTACKED_TARGET, true);
        this.number = owner.cg();
        super.c();
    }
}
