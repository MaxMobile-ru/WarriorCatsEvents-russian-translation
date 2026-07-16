package net.snowteb.warriorcats_events.entity.custom;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.entity.ModEntities;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.item.ModItems;
import net.snowteb.warriorcats_events.sound.ModSounds;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.*;


public class BadgerEntity extends Animal implements GeoEntity {

    private static final EntityDataAccessor<Boolean> ATTACKING =
            SynchedEntityData.defineId(BadgerEntity.class, EntityDataSerializers.BOOLEAN);
    public int attackAnimationTimeout = 0;
    private Goal preyTarget;
    private Goal enemyTarget;

    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public BadgerEntity(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return false;
    }

    public static AttributeSupplier.Builder setAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 38D)
                .add(Attributes.ATTACK_SPEED, 1.2f)
                .add(Attributes.ATTACK_DAMAGE, 5.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.30f)
                .add(Attributes.ARMOR, 13.0f);
    }

    @Override
    protected void registerGoals() {
        this.preyTarget = new NearestAttackableTargetGoal<>(this, Animal.class, 10, false, false, (target) -> {
            return (target instanceof MouseEntity || target instanceof SquirrelEntity);
        });
        this.enemyTarget = new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, false, false, (target) -> {
            return (target instanceof WCatEntity || target instanceof Player);
        });

        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new BadgerAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(0, new HurtByTargetGoal(this));
//        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(1, this.enemyTarget);
        this.targetSelector.addGoal(2, this.preyTarget);
    }


    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ATTACKING, false);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return ModEntities.BADGER.get().create(pLevel);
    }


    /**
     * This is in charge of the animations
     */

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>
                (this, "controller", 0, this::predicate));
        controllers.add(new AnimationController<>
                (this, "attackController", 0, this::attackPredicate));
    }

    private <T extends GeoAnimatable> PlayState attackPredicate(AnimationState<T> state) {
        var controller = state.getController();
        if (this.isAttacking() && attackAnimationTimeout <= 0) {
            attackAnimationTimeout = 100;
            controller.setAnimation(RawAnimation.begin()
                    .then("animation.badger.attack", Animation.LoopType.PLAY_ONCE));
            controller.forceAnimationReset();
            return PlayState.CONTINUE;
        } else {
            --this.attackAnimationTimeout;
        }

        if(!this.isAttacking()){
            return PlayState.STOP;
        }
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {
        if(tAnimationState.isMoving()) {
            tAnimationState.getController().setAnimation(RawAnimation.begin().
                    then("animation.badger.walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        tAnimationState.getController().setAnimation(RawAnimation.begin().
                then("animation.badger.idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }


    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        if (this.level() instanceof ServerLevel) {
            if (this.random.nextFloat() < 0.025f) {
                this.spawnAtLocation(ModItems.BADGER_SKULL.get());
            }
        }

        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
    }

    @Override
    protected ResourceKey<LootTable> getDefaultLootTable() {
        return ResourceKey.create(
                Registries.LOOT_TABLE,
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "entities/badger")
        );
    }

    @Override
    protected int getBaseExperienceReward() {
        return 80 + 10*this.random.nextInt(5);
    }

    /**
     * Indicators that allow the entity to perform the attack animation
     */
    public void setAttacking(boolean attacking) {
        this.entityData.set(ATTACKING, attacking);
    }

    public boolean isAttacking() {
        return this.entityData.get(ATTACKING);
    }

    @Override
    public boolean canAttack(LivingEntity pTarget) {
        if (this.level().getDifficulty() == Difficulty.PEACEFUL) return false;
        return super.canAttack(pTarget);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.BADGER_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.BADGER_HURT_ATTACK.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.BADGER_SCREECH.get();
    }

}
