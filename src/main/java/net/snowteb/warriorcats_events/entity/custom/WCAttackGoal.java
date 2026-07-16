package net.snowteb.warriorcats_events.entity.custom;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.level.block.Blocks;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;

/**
 * This is the same as BadgerAttackGoal, read that one to understand.
 */
public class WCAttackGoal extends MeleeAttackGoal {
    private final WCatEntity entity;
    private int attackDelay = 10;
    private int ticksUntilNextAttack = 10;
    private boolean shouldCountTillNextAttack = false;

    BlockParticleOption particle = new BlockParticleOption(
            ParticleTypes.BLOCK,
            Blocks.REDSTONE_BLOCK.defaultBlockState()
    );

    public WCAttackGoal(PathfinderMob pMob, double pSpeedModifier, boolean pFollowingTargetEvenIfNotSeen) {
        super(pMob, pSpeedModifier, pFollowingTargetEvenIfNotSeen);
        entity = ((WCatEntity)pMob);
    }


    @Override
    public void start() {
        super.start();
        this.mob.clearRestriction();
        attackDelay = 17;
        ticksUntilNextAttack = 17;
    }

    @Override
    public void tick() {
        super.tick();
        if (shouldCountTillNextAttack) {
            this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
        }
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {

        if (distToEnemySqr <= this.getAttackReachSqr(enemy)) {

            if (!shouldCountTillNextAttack) {
                shouldCountTillNextAttack = true;
                ticksUntilNextAttack = attackDelay;
            }

            if (ticksUntilNextAttack == attackDelay) {
                entity.setAttacking(true);
            }

            if (ticksUntilNextAttack <= 0) {
                this.mob.getLookControl().setLookAt(enemy.getX(), enemy.getEyeY(), enemy.getZ());
                performAttack(enemy);

                ticksUntilNextAttack = attackDelay;
            }

        } else {

            shouldCountTillNextAttack = false;
            ticksUntilNextAttack = attackDelay;
            entity.setAttacking(false);
            entity.attackAnimationTimeout = 0;
        }
    }


    @Override
    protected double getAttackReachSqr(LivingEntity target) {
        return 7.3;
    }


    protected void resetAttackCooldown() {
        this.ticksUntilNextAttack = this.adjustedTickDelay(attackDelay);
    }

    protected boolean isTimeToAttack() {
        return this.ticksUntilNextAttack <= 0;
    }

    protected int getTicksUntilNextAttack() {
        return this.ticksUntilNextAttack;
    }


    protected void performAttack(LivingEntity pEnemy) {
        this.resetAttackCooldown();
        this.mob.swing(InteractionHand.MAIN_HAND);
        this.mob.doHurtTarget(pEnemy);
        if (!mob.level().isClientSide()){
            ServerLevel level = ((ServerLevel) mob.level());
            level.sendParticles(particle,
                    pEnemy.getX(), pEnemy.getY(), pEnemy.getZ(),
                    10,
                    0.1,0.2,0.1, 0.1);
            if (level.random.nextInt(3) == 0) {
                level.playSound(mob, mob.blockPosition(), SoundEvents.CAT_HISS, SoundSource.HOSTILE, 0.6F, 1.0F);
            }
        }

    }

    @Override
    public boolean canUse() {
        if (this.mob instanceof WCatEntity cat) {
            if (cat.mode == WCatEntity.CatMode.SIT) return false;
            if (cat.isResting()) return false;
        }

        if (this.mob.getVehicle() != null & this.mob.getVehicle() instanceof EagleEntity) return false;

        return super.canUse();
    }

    @Override
    public void stop() {
        entity.setAttacking(false);
        super.stop();
    }
}
