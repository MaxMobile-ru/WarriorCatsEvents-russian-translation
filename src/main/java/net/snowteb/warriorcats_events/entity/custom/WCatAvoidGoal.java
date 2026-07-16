package net.snowteb.warriorcats_events.entity.custom;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;

public class WCatAvoidGoal extends AvoidEntityGoal<WCatEntity> {
    public WCatAvoidGoal(PathfinderMob mob, float maxDistance, double walkSpeed, double sprintSpeed) {
        super(mob, WCatEntity.class, maxDistance, walkSpeed, sprintSpeed);
    }

    public Class<WCatEntity> getAvoidClass() {
        return this.avoidClass;
    }
}

