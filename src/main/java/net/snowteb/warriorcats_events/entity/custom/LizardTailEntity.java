package net.snowteb.warriorcats_events.entity.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.snowteb.warriorcats_events.entity.ModEntities;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;

public class LizardTailEntity extends Animal implements GeoEntity {

    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public LizardTailEntity(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }


    public static void spawn(Level level, LizardEntity owner) {
        LizardTailEntity tail = ModEntities.LIZARD_TAIL.get().create(level);
        if (tail == null) return;

        {
            tail.setYRot(owner.getYRot());
            tail.setXRot(owner.getXRot());

            tail.setYBodyRot(owner.yBodyRot);
            tail.setYHeadRot(owner.getYHeadRot());

            tail.setRot(owner.getYRot(), owner.getXRot());
        }

        tail.setOwner(owner);
        tail.setPos(owner.position());
        level.addFreshEntity(tail);
        tail.setBaby(owner.isBaby());



        tail.setVariant(owner.getVariant());
        level.playSound(null, tail.blockPosition(), SoundEvents.BEEHIVE_EXIT,
                SoundSource.NEUTRAL, 0.5F, 1.0F);
    }


    private int ageTicks = 20*15;

    private static final EntityDataAccessor<Integer> LIZARD_VARIANT =
            SynchedEntityData.defineId(LizardTailEntity.class, EntityDataSerializers.INT);


    public static AttributeSupplier.Builder setAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 2D)
                .add(Attributes.ATTACK_SPEED, 0.0f)
                .add(Attributes.ATTACK_DAMAGE, 0.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.30f);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(LIZARD_VARIANT, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("variant", this.getVariant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("variant")) {
            this.setVariant(pCompound.getInt("variant"));
        }
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }


    public int getVariant() {
        return this.entityData.get(LIZARD_VARIANT);
    }

    public void setVariant(int id) {
        this.entityData.set(LIZARD_VARIANT, id);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        setVariant(this.random.nextInt(LizardEntity.MAX_VARIANTS));
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>
                (this, "controller", 0, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {

        tAnimationState.getController().setAnimationSpeed(1.2);

        tAnimationState.getController().setAnimation(RawAnimation.begin().
                then("animation.lizard_tail.tweak", Animation.LoopType.LOOP));

        return PlayState.CONTINUE;
    }

    @Override
    public void aiStep() {
        if (this.onGround() && this.verticalCollision && this.tickCount % 20 == 0) {
            this.setDeltaMovement(this.getDeltaMovement().add((this.random.nextFloat() * 2.0F - 1.0F) * 0.05F, 0.25F, (this.random.nextFloat() * 2.0F - 1.0F) * 0.05F));
            this.setOnGround(false);
            this.hasImpulse = true;
            this.playSound(SoundEvents.SALMON_FLOP, this.getSoundVolume(), this.getVoicePitch());
        }
        super.aiStep();
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }


    private LivingEntity owner;

    public void setOwner(LivingEntity entity) {
        this.owner = entity;
    }

    public LivingEntity getOwner() {
        return owner;
    }

    @Override
    public void tick() {
        ageTicks--;

        if (this.tickCount % 20 == 0) {
            boolean shouldDespawn = ageTicks <= 0 || (this.getOwner() == null || !this.getOwner().isAlive());
            if (shouldDespawn) {
                this.kill();
            }
        }

        super.tick();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.GENERIC_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.EMPTY;
    }

}
