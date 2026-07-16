package net.snowteb.warriorcats_events.entity.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.item.ModItems;
import net.snowteb.warriorcats_events.sound.ModSounds;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;

public class PigeonEntity extends Parrot implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final EntityDataAccessor<Integer> PIGEON_VARIANT =
            SynchedEntityData.defineId(PigeonEntity.class, EntityDataSerializers.INT);

    public PigeonEntity(EntityType<? extends PigeonEntity> type, Level world) {
        super(type, world);
        this.moveControl = new FlyingMoveControl(this, 10, false);
    }


    public class FlyAway extends Goal {

        private final Mob mob;
        private final double speed;
        private final double range = 9;
        private LivingEntity nearestThreat;
        private int checkCooldown = 0;

        public FlyAway(Mob mob, double speed) {
            this.mob = mob;
            this.speed = speed;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {

            if (checkCooldown > 0) {
                checkCooldown--;
                return false;
            }
            checkCooldown = 5 + mob.getRandom().nextInt(5);

            /**
             * Only if the entity is not invisible.
             */
            TargetingConditions conditions = TargetingConditions.forNonCombat()
                    .selector(e -> !e.isInvisible())
                    .range(range);

            /**
             * Finds the nearest player that succeeds the conditions and the nearest Wild Cat in certain range
             */
            Player nearestPlayer = mob.level().getNearestPlayer(conditions, mob);

            WCatEntity nearestWCat = mob.level().getNearestEntity(
                    WCatEntity.class,
                    conditions,
                    mob,
                    mob.getX(), mob.getY(), mob.getZ(),
                    mob.getBoundingBox().inflate(range)
            );

            if (nearestWCat != null && !shouldScareFrom(nearestWCat)) {
                nearestWCat = null;
            }

            /**
             * Decides whether the nearest threat is the player or the wild cat
             */
            nearestThreat = pickNearest(nearestPlayer, nearestWCat);

            return nearestThreat != null;
        }

        private LivingEntity pickNearest(LivingEntity e1, LivingEntity e2) {
            if (e1 == null) return e2;
            if (e2 == null) return e1;
            double d1 = e1.distanceTo(mob);
            double d2 = e2.distanceTo(mob);

            return d1 < d2 ? e1 : e2;
        }

        /**
         * Once it starts to fly away, it picks a position in the air and moves to it.
         */
        @Override
        public void start() {
            Vec3 escapePos = getEscapePos();
            if (escapePos != null) {
                mob.getNavigation().moveTo(escapePos.x, escapePos.y, escapePos.z, speed);
            }
        }

        private Vec3 getEscapePos() {
            Vec3 mobPos = mob.position();
            Vec3 targetPos = nearestThreat.position();

            Vec3 dir = mobPos.subtract(targetPos).normalize();
            return mobPos.add(dir.scale(10)).add(0, 4, 0);
        }
    }

    /**
     * An indicator that makes sure the cat it should fly away from is in Wander mode.
     */
    private boolean shouldScareFrom(WCatEntity cat) {
        return cat.mode == WCatEntity.CatMode.WANDER && !cat.onHuntingPatrolFlag;
    }


    /**
     * Every 4 seconds in average (80 ticks), it performs the goal.
     * Picks a random position in the air and moves to it.
     */
    public class RandomFlyingGoal extends Goal {
        private final Mob mob;
        private final double speed;

        public RandomFlyingGoal(Mob mob, double speed) {
            this.mob = mob;
            this.speed = speed;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return mob.getRandom().nextInt(80) == 0;
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            Vec3 vec = this.getRandomAirPos();
            if (vec != null) {
                mob.getNavigation().moveTo(vec.x, vec.y, vec.z, speed);
            }
        }

        private Vec3 getRandomAirPos() {
            RandomSource rand = mob.getRandom();
            double x = mob.getX() + (rand.nextDouble() - 0.5) * 16;
            double y = mob.getY() + rand.nextInt(6) + 4;
            double z = mob.getZ() + (rand.nextDouble() - 0.5) * 16;
            return new Vec3(x, y, z);
        }
    }


    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PIGEON_VARIANT, 0);
    }

    public int getPigeonVariant() {
        return this.entityData.get(PIGEON_VARIANT);
    }

    public void setPigeonVariant(int id) {
        this.entityData.set(PIGEON_VARIANT, id);
    }



    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, SpawnGroupData livingdata, CompoundTag tag) {
        setPigeonVariant(this.random.nextInt(3));
        return super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
    }



    public static AttributeSupplier.Builder setAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 2D)
                .add(Attributes.ATTACK_SPEED, 1.0f)
                .add(Attributes.ATTACK_DAMAGE, 1f)
                .add(Attributes.MOVEMENT_SPEED, 0.2f)
                .add(Attributes.FLYING_SPEED, 0.4f);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1,new PanicGoal(this,1.25D));
        this.goalSelector.addGoal(2, new FlyAway(this, 3.5D));
        this.goalSelector.addGoal(3,new WaterAvoidingRandomStrollGoal(this,1.0D));
        this.goalSelector.addGoal(4,new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(5, new RandomFlyingGoal(this, 2.0));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

   @Override
    protected PathNavigation createNavigation(Level level) {
        return new FlyingPathNavigation(this, level);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>
                (this, "controller", 0, this::predicate));

    }

    @Override
    public int getExperienceReward() {
        return 30 + 5*this.random.nextInt(4);
    }


    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {
        if (this.isFlying()) {
            tAnimationState.getController().setAnimation(RawAnimation.begin()
                    .then("animation.pigeon.fly", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        if (tAnimationState.isMoving()) {
            tAnimationState.getController().setAnimation(RawAnimation.begin()
                    .then("animation.pigeon.walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        tAnimationState.getController().setAnimation(RawAnimation.begin()
                .then("animation.pigeon.idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }


    @Override
    protected void dropCustomDeathLoot(DamageSource pSource, int pLooting, boolean pRecentlyHit) {
        ItemStack itemEarned = new ItemStack(ModItems.PIGEON_FEATHER.get());

        if (itemEarned.isEmpty()) return;

        ItemEntity drop = new ItemEntity(this.level(), this.getX(), this.getY() + 0.2, this.getZ(),
                itemEarned.copyWithCount(this.getRandom().nextInt(4)));
        if (this.level() instanceof ServerLevel sLevel) {
            sLevel.addFreshEntity(drop);
        }

        super.dropCustomDeathLoot(pSource, pLooting, pRecentlyHit);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public @Nullable SoundEvent getAmbientSound() {
        return ModSounds.PIGEON_AMBIENT.get();
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return ModSounds.PIGEON_AMBIENT.get();
    }

    @Override
    protected @NotNull SoundEvent getHurtSound(DamageSource pDamageSource) {
        return ModSounds.PIGEON_AMBIENT.get();
    }
}
