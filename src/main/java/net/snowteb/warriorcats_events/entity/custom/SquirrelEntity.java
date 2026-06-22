package net.snowteb.warriorcats_events.entity.custom;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.entity.ModEntities;
import net.snowteb.warriorcats_events.item.ModItems;
import net.snowteb.warriorcats_events.sound.ModSounds;
import net.snowteb.warriorcats_events.util.MoveToLogsGoal;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

public class SquirrelEntity extends Animal implements GeoEntity {

    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public SquirrelEntity(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier.Builder setAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 2D)
                .add(Attributes.ATTACK_SPEED, 1.0f)
                .add(Attributes.ATTACK_DAMAGE, 1f)
                .add(Attributes.MOVEMENT_SPEED, 0.27f);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.3D));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, LivingEntity.class,
                4.0F, 1.2D, 1.5D,
                e -> e instanceof Player || (e instanceof WCatEntity && shouldScareFrom((WCatEntity)e))));
        this.goalSelector.addGoal(4, new MoveToLogsGoal(this, 1.0D, 15));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }


    private boolean shouldScareFrom(WCatEntity cat) {
        return cat.mode == WCatEntity.CatMode.WANDER;
    }


    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return ModEntities.SQUIRREL.get().create(pLevel);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>
                (this, "controller", 0, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {
        if(tAnimationState.isMoving()) {
            tAnimationState.getController().setAnimation(RawAnimation.begin().
                    then("animation.squirrel.run", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        tAnimationState.getController().setAnimation(RawAnimation.begin().
                then("animation.squirrel.idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource damageSource, int lootingMultiplier, boolean recentlyHit) {

        if (this.level() instanceof ServerLevel) {
            if (this.random.nextFloat() < 0.015f) {
                this.spawnAtLocation(ModItems.SQUIRREL_SKULL.get());
            }
        }

        super.dropCustomDeathLoot(damageSource, lootingMultiplier, recentlyHit);
    }

    @Override
    protected ResourceLocation getDefaultLootTable() {
        return ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "entities/squirrel");
    }


    @Override
    public int getExperienceReward() {
        return 20 + 3*this.random.nextInt(5);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.SQUIRREL_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.SQUIRREL_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.SQUIRREL_DEATH.get();
    }




}
