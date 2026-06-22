package net.snowteb.warriorcats_events.entity.custom;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TurtleEggBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.snowteb.warriorcats_events.block.ModBlocks;
import net.snowteb.warriorcats_events.block.entity.LizardEggBlockEntity;
import net.snowteb.warriorcats_events.entity.ModEntities;
import net.snowteb.warriorcats_events.item.ModItems;
import net.snowteb.warriorcats_events.sound.ModSounds;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import tocraft.walkers.api.PlayerShape;

import java.util.Comparator;
import java.util.List;

public class LizardEntity extends TamableAnimal implements GeoEntity {

    public static final int MAX_VARIANTS = 7;
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public LizardEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public enum LizardMode {
        SIT,
        FOLLOW,
        WANDER
    }

    public LizardMode mode =  LizardMode.WANDER;

    private static final EntityDataAccessor<Integer> LIZARD_VARIANT =
            SynchedEntityData.defineId(LizardEntity.class, EntityDataSerializers.INT);


    int tailGrowthTime = 0;
    private static final EntityDataAccessor<Boolean> LIZARD_TAIL =
            SynchedEntityData.defineId(LizardEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> LIZARD_LAYING_EGG
            = SynchedEntityData.defineId(LizardEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> LIZARD_DIGGING
            = SynchedEntityData.defineId(LizardEntity.class, EntityDataSerializers.BOOLEAN);

    private boolean hasEgg = false;
    private int layEggCounter = 0;

    private boolean hasEgg() {
        return this.hasEgg;
    }

    private void setHasEgg(boolean b) {
        this.hasEgg = b;
    }

    private void setLayingEgg(boolean b) {
        this.entityData.set(LIZARD_LAYING_EGG, b);
    }

    private boolean isLayingEgg() {
        return this.entityData.get(LIZARD_LAYING_EGG);
    }


    public static AttributeSupplier.Builder setAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 8D)
                .add(Attributes.ATTACK_SPEED, 1.0f)
                .add(Attributes.ATTACK_DAMAGE, 0.2f)
                .add(Attributes.MOVEMENT_SPEED, 0.30f);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.0D));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new LizardBreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new LizardLayEggGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new LizardDigItemGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new AvoidEntityGoal<>(this, LivingEntity.class,
                3.0F, 0.8D, 0.8D,
                e -> !this.isTame() && ((e instanceof Player player && !player.isDiscrete()) || (e instanceof WCatEntity && shouldScareFrom((WCatEntity)e))) ));
        this.goalSelector.addGoal(5, new LizardFollowGoal(this, 1.0D, 5.0F, 3.0F, false));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(7, new LizardRidesPlayersGoal(this));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
    }


    private static class LizardRidesPlayersGoal extends Goal {

        private final LizardEntity lizard;
        private Player toRide;

        private LizardRidesPlayersGoal(LizardEntity lizardEntity) {
            this.lizard = lizardEntity;
        }

        @Override
        public boolean canUse() {
            if (!lizard.isTame()) return false;

            if (lizard.isPassenger()) return false;

            if (lizard.mode !=  LizardMode.WANDER) return false;

            if (lizard.getRandom().nextInt(8000) != 0) return false;

            toRide = findPlayer();

            return toRide != null;
        }

        @Override
        public boolean canContinueToUse() {
            return toRide != null
                    && toRide.isAlive()
                    && !lizard.isPassenger()
                    && lizard.distanceTo(toRide) < 20;
        }

        @Override
        public void start() {
            lizard.getNavigation().stop();
        }

        @Override
        public void stop() {
            toRide = null;
        }

        @Override
        public void tick() {
            if (toRide == null) {
                stop();
                return;
            }

            if (!lizard.getNavigation().isInProgress()) lizard.getNavigation().moveTo(toRide, 1);

            if (lizard.distanceTo(toRide) < 1.5) {
                lizard.getNavigation().stop();
                if (toRide.getFirstPassenger() == null) {
                    lizard.startRiding(toRide);
                    lizard.level().playSound(null, lizard.blockPosition(), SoundEvents.ITEM_PICKUP,
                            SoundSource.AMBIENT, 0.7f, 1.2f);
                }
                stop();
            }

        }

        private Player findPlayer() {
            List<Player> players = lizard.level().getEntitiesOfClass(Player.class,
                    lizard.getBoundingBox().inflate(15),
                    player -> player.getFirstPassenger() == null
                            && PlayerShape.getCurrentShape(player) instanceof WCatEntity
                            && !player.isInvisible() && !player.isPassenger() && player.isAlive());

            return players.stream()
                    .min(Comparator.comparingDouble(lizard::distanceToSqr))
                    .orElse(null);
        }
    }

    private static class LizardFollowGoal extends FollowOwnerGoal {
        private final TamableAnimal entity;
        public LizardFollowGoal(TamableAnimal pTamable, double pSpeedModifier, float pStartDistance, float pStopDistance, boolean pCanFly) {
            super(pTamable, pSpeedModifier, pStartDistance, pStopDistance);
            this.entity = pTamable;
        }

        @Override
        public boolean canUse() {
            if (entity instanceof LizardEntity lizard && lizard.mode != LizardMode.FOLLOW) return false;
            return super.canUse();
        }
    }

    private boolean shouldScareFrom(WCatEntity cat) {
        return !this.isTame() && cat.mode == WCatEntity.CatMode.WANDER;
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return ModEntities.LIZARD.get().create(pLevel);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(LIZARD_VARIANT, 0);
        builder.define(LIZARD_TAIL, true);
        builder.define(LIZARD_LAYING_EGG, false);
        builder.define(LIZARD_DIGGING, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("variant", this.getVariant());
        pCompound.putBoolean("hasTail", this.hasTail());
        pCompound.putInt("tailGrowthTime", this.tailGrowthTime);
        if (this.mode != null) pCompound.putInt("mode", this.mode.ordinal());
        pCompound.putBoolean("hasEgg", this.hasEgg);
        pCompound.putInt("digCooldown", this.digCooldown);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        if (pCompound.contains("variant")) {
            this.setVariant(pCompound.getInt("variant"));
        }
        if (pCompound.contains("hasTail")) {
            this.setTail(pCompound.getBoolean("hasTail"));
        }
        if (pCompound.contains("tailGrowthTime")) {
            this.tailGrowthTime = pCompound.getInt("tailGrowthTime");
        }
        if (pCompound.contains("mode")) {
            this.mode = LizardMode.values()[pCompound.getInt("mode")];
            if (this.mode == null) this.mode = LizardMode.WANDER;
        }
        if (pCompound.contains("hasEgg")) {
            this.hasEgg = pCompound.getBoolean("hasEgg");
        }
        if (pCompound.contains("digCooldown")) {
            this.digCooldown = pCompound.getInt("digCooldown");
        }

    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.isAlive() &&
                (
                        (this.isLayingEgg() && this.layEggCounter >= 1 && this.layEggCounter % 5 == 0) ||
                                (this.isDigging() && this.digCounter >= 1 && this.digCounter % 5 == 0)
                )) {
            BlockPos blockpos = this.blockPosition();

            if (this.getNavigation().isDone()) {
                this.level().levelEvent(2001, blockpos, Block.getId(this.level().getBlockState(blockpos.below())));
            }
        }

    }

    public int getVariant() {
        return this.entityData.get(LIZARD_VARIANT);
    }

    public void setVariant(int id) {
        this.entityData.set(LIZARD_VARIANT, id);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        setVariant(this.random.nextInt(MAX_VARIANTS));
        setRandomDigCooldown();
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>
                (this, "controller", 0, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {

        tAnimationState.getController().transitionLength(3);

        if (this.isDigging() || this.isLayingEgg()) {
            tAnimationState.getController().setAnimation(RawAnimation.begin().
                    then("animation.lizard.dig", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        if (this.isInSittingPose()) {
            tAnimationState.getController().setAnimation(RawAnimation.begin().
                    then("animation.lizard.sit", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        if (tAnimationState.isMoving()) {
            tAnimationState.getController().setAnimation(RawAnimation.begin().
                    then("animation.lizard.walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        tAnimationState.getController().setAnimation(RawAnimation.begin().
                then("animation.lizard.idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public boolean isFood(ItemStack pStack) {
        return pStack.is(Items.SPIDER_EYE) || pStack.is(Items.FERMENTED_SPIDER_EYE);
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        if (pHand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
        ItemStack itemStack = pPlayer.getItemInHand(pHand);

        if (this.isTame()) {
            if (!this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
                this.spawnAtLocation(this.getItemBySlot(EquipmentSlot.MAINHAND));
                this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                return InteractionResult.SUCCESS;
            }

            if (this.isFood(itemStack) && this.getHealth() < this.getMaxHealth()) {
                this.heal(2);
                if (!pPlayer.getAbilities().instabuild) itemStack.shrink(1);

                this.gameEvent(GameEvent.EAT, this);
                return InteractionResult.SUCCESS;
            } else {

                InteractionResult interactionresult = super.mobInteract(pPlayer, pHand);
                if ((!interactionresult.consumesAction() || this.isBaby()) && this.isOwnedBy(pPlayer) && !this.level().isClientSide()) {
                    LivingEntity shape = PlayerShape.getCurrentShape(pPlayer);
                    boolean canCarry = shape instanceof WCatEntity;

                    if (pPlayer.isShiftKeyDown() && canCarry) {
                        this.startRiding(pPlayer, true);
                        this.level().playSound(null, this.blockPosition(), SoundEvents.ITEM_PICKUP,
                                SoundSource.AMBIENT, 0.5f, 1.5f);

                    } else {
                        switch (this.mode) {
                            case WANDER -> {
                                {
                                    String mode = "following";
                                    String name = this.hasCustomName() ? this.getCustomName().getString() : this.getName().getString();
                                    pPlayer.displayClientMessage(Component.literal(name + " is " + mode), true);
                                }
                                this.setOrderedToSit(false);
                                this.mode = LizardMode.FOLLOW;
                            }
                            case FOLLOW -> {

                                {
                                    String mode = "sitting";
                                    String name = this.hasCustomName() ? this.getCustomName().getString() : this.getName().getString();
                                    pPlayer.displayClientMessage(Component.literal(name + " is " + mode), true);
                                }
                                this.setOrderedToSit(true);
                                this.mode = LizardMode.SIT;
                            }
                            case SIT -> {
                                {
                                    String mode = "wandering";
                                    String name = this.hasCustomName() ? this.getCustomName().getString() : this.getName().getString();
                                    pPlayer.displayClientMessage(Component.literal(name + " is " + mode), true);
                                }
                                this.setOrderedToSit(false);
                                this.mode = LizardMode.WANDER;
                            }
                        }

                        this.setJumping(false);
                        this.getNavigation().stop();
                        this.setTarget(null);
                        return InteractionResult.SUCCESS;
                    }
                } else {
                    return interactionresult;
                }
            }
        } else if (itemStack.is(Items.SPIDER_EYE) && !this.level().isClientSide()) {
            if (!pPlayer.getAbilities().instabuild) itemStack.shrink(1);

            if (this.random.nextInt(5) == 0) {
                this.tame(pPlayer);
                this.navigation.stop();
                this.setOrderedToSit(true);
                this.level().broadcastEntityEvent(this, (byte)7);
            } else {
                this.level().broadcastEntityEvent(this, (byte)6);
            }

            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(pPlayer, pHand);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }


    public void setTail(boolean tail) {
        this.getEntityData().set(LIZARD_TAIL, tail);
    }

    public boolean hasTail() {
        return this.getEntityData().get(LIZARD_TAIL);
    }

    @Override
    public void rideTick() {
        super.rideTick();

        Entity vehicle = this.getVehicle();
        if (vehicle == null) return;

        float yawDeg;

        if (vehicle instanceof LivingEntity) {
            yawDeg = ((LivingEntity) vehicle).yBodyRot;
        } else {
            yawDeg = vehicle.getYRot();
        }

        float sideYaw = yawDeg + 10F;

        this.setYRot(sideYaw);
        this.setYHeadRot(sideYaw);

    }

    private int rideGraceTime = 0;

    @Override
    public boolean startRiding(Entity pEntity, boolean pForce) {
        this.rideGraceTime = 20;
        return super.startRiding(pEntity, pForce);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource pSource) {
        if (pSource.is(DamageTypes.IN_WALL) && this.isPassenger()) {
            return true;
        }
        return super.isInvulnerableTo(pSource);
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide()) {

            if (this.rideGraceTime > 0) rideGraceTime--;

            if (this.isPassenger() && this.rideGraceTime <= 0) {
                Entity vehicle = this.getVehicle();
                if (vehicle instanceof Player player) {
                    if (player.isShiftKeyDown() || !(PlayerShape.getCurrentShape(player) instanceof WCatEntity)) {
                        this.setPos(
                                player.getX(),
                                player.getY(),
                                player.getZ()
                        );
                        this.stopRiding();
                    }
                }
            }


            if (tailGrowthTime > 0) {
                tailGrowthTime--;

                if (tailGrowthTime <= 0) {
                    this.setTail(true);
                }
            }
        }
        super.tick();
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (!this.level().isClientSide()) {
            if (pSource.getEntity() instanceof LivingEntity && this.hasTail()) {
                this.setTail(false);
                LizardTailEntity.spawn(this.level(), this);
                this.tailGrowthTime = 120*20 + (20* this.getRandom().nextInt(60));
            }
        }

        return super.hurt(pSource, pAmount);
    }

    @Override
    protected int getBaseExperienceReward() {
        return 5 + 3*this.random.nextInt(3);
    }

    @Override
    public int getAmbientSoundInterval() {
        return 1400 + 20*this.random.nextInt(30);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.LIZARD_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.GENERIC_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.GENERIC_DEATH;
    }


    static class LizardBreedGoal extends BreedGoal {
        private final LizardEntity lizard;

        LizardBreedGoal(LizardEntity mob, double pSpeedModifier) {
            super(mob, pSpeedModifier);
            this.lizard = mob;
        }

        public boolean canUse() {
            return super.canUse() && !this.lizard.hasEgg();
        }

        protected void breed() {
            ServerPlayer serverplayer = this.animal.getLoveCause();
            if (serverplayer == null && this.partner.getLoveCause() != null) {
                serverplayer = this.partner.getLoveCause();
            }

            if (serverplayer != null) {
                serverplayer.awardStat(Stats.ANIMALS_BRED);
                CriteriaTriggers.BRED_ANIMALS.trigger(serverplayer, this.animal, this.partner, null);
            }

            this.lizard.setHasEgg(true);
            this.animal.setAge(6000);
            this.partner.setAge(6000);
            this.animal.resetLove();
            this.partner.resetLove();
            RandomSource randomsource = this.animal.getRandom();
            if (this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                this.level.addFreshEntity(new ExperienceOrb(this.level, this.animal.getX(), this.animal.getY(), this.animal.getZ(), randomsource.nextInt(7) + 1));
            }

        }
    }



    static class LizardLayEggGoal extends MoveToBlockGoal {
        private final LizardEntity lizard;

        LizardLayEggGoal(LizardEntity mob, double pSpeedModifier) {
            super(mob, pSpeedModifier, 20);
            this.lizard = mob;
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean canUse() {
            return this.lizard.hasEgg() && super.canUse();
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean canContinueToUse() {
            return super.canContinueToUse() && this.lizard.hasEgg();
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void tick() {
            super.tick();
            BlockPos blockpos = this.lizard.blockPosition();
            if (!this.lizard.isInWater() && this.isReachedTarget()) {
                if (this.lizard.layEggCounter < 1) {
                    this.lizard.setLayingEgg(true);
                } else if (this.lizard.layEggCounter > this.adjustedTickDelay(200)) {
                    Level level = this.lizard.level();
                    level.playSound(null, blockpos, SoundEvents.TURTLE_LAY_EGG, SoundSource.BLOCKS, 0.3F, 0.9F + level.getRandom().nextFloat() * 0.2F);
                    BlockPos blockpos1 = this.blockPos.above();
                    BlockState blockstate = ModBlocks.LIZARD_EGG_BLOCK.get().defaultBlockState().setValue(TurtleEggBlock.EGGS, Integer.valueOf(this.lizard.getRandom().nextInt(4) + 1));
                    level.setBlock(blockpos1, blockstate, 3);

                    BlockEntity blockentity = level.getBlockEntity(blockpos1);
                    if (blockentity instanceof LizardEggBlockEntity lizardeggblockentity) {
                        lizardeggblockentity.setOwnerUUID(this.lizard.getOwnerUUID());
                    }

                    level.gameEvent(GameEvent.BLOCK_PLACE, blockpos1, GameEvent.Context.of(this.lizard, blockstate));
                    this.lizard.setHasEgg(false);
                    this.lizard.setLayingEgg(false);
                    this.lizard.setInLoveTime(600);
                }

                if (this.lizard.isLayingEgg()) {
                    ++this.lizard.layEggCounter;
                }
            }

        }

        protected boolean isValidTarget(LevelReader pLevel, BlockPos pPos) {
            int light = pLevel.getMaxLocalRawBrightness(pPos.above());
            return pLevel.isEmptyBlock(pPos.above()) && light < 11;
        }

        @Override
        public double acceptedDistance() {
            return 1.5;
        }
    }

    static class LizardDigItemGoal extends MoveToBlockGoal {
        private final LizardEntity lizard;

        public LizardDigItemGoal(LizardEntity pMob, double pSpeedModifier) {
            super(pMob, pSpeedModifier, 15);
            this.lizard = pMob;
        }

        @Override
        public boolean canUse() {
            if (!lizard.isTame()) return false;
            if (lizard.isPassenger()) return false;
            if (!lizard.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) return false;
            if (lizard.digCooldown > 0) lizard.digCooldown--;
            return !lizard.isLayingEgg() && lizard.canDigItem() && !lizard.hasEgg() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && lizard.canDigItem();
        }

        @Override
        public void stop() {
            super.stop();
            if (this.lizard.isDigging()) {
                this.lizard.setDigging(false);
            }
        }

        public void tick() {
            super.tick();
            BlockPos blockpos = this.lizard.blockPosition();
            if (!this.lizard.isInWater() && this.isReachedTarget()) {
                if (this.lizard.digCounter < 1) {
                    this.lizard.setDigging(true);
                } else if (this.lizard.digCounter > this.adjustedTickDelay(100)) {
                    Level level = this.lizard.level();
                    level.playSound(null, blockpos, SoundEvents.TURTLE_LAY_EGG, SoundSource.BLOCKS, 0.3F, 0.9F + level.getRandom().nextFloat() * 0.2F);

                    this.lizard.setItemSlot(EquipmentSlot.MAINHAND, this.getRandomItem());
                    this.lizard.setRandomDigCooldown();
                    this.lizard.setDigging(false);
                }

                if (this.lizard.isDigging()) {
                    this.lizard.digCounter++;
                }
            }

        }

        private ItemStack getRandomItem() {
            RandomSource random = this.lizard.getRandom();
            record ItemDrop(Item item, int maxAmount) {}

            List<ItemDrop> possibilities = List.of(
                    new ItemDrop(Items.IRON_INGOT, 3),
                    new ItemDrop(Items.IRON_INGOT, 3),
                    new ItemDrop(Items.IRON_INGOT, 3),
                    new ItemDrop(Items.IRON_INGOT, 3),
                    new ItemDrop(Items.GOLD_INGOT, 2),
                    new ItemDrop(Items.GOLD_INGOT, 2),
                    new ItemDrop(Items.GOLD_INGOT, 2),
                    new ItemDrop(Items.DIAMOND, 2),
                    new ItemDrop(Items.DIAMOND, 2),
                    new ItemDrop(Items.HEART_OF_THE_SEA, 1),
                    new ItemDrop(Items.HEART_OF_THE_SEA, 1),
                    new ItemDrop(Items.EMERALD, 1),
                    new ItemDrop(Items.EMERALD, 1),
                    new ItemDrop(Items.NETHERITE_INGOT, 1),
                    new ItemDrop(ModItems.STRANGE_SHINY_STONE.get(), 3),
                    new ItemDrop(Items.NAUTILUS_SHELL, 2),
                    new ItemDrop(Items.NAUTILUS_SHELL, 2),
                    new ItemDrop(Items.NAUTILUS_SHELL, 2),
                    new ItemDrop(Items.LAPIS_LAZULI, 5),
                    new ItemDrop(Items.LAPIS_LAZULI, 5),
                    new ItemDrop(Items.LAPIS_LAZULI, 5),
                    new ItemDrop(ModItems.ANIMAL_TOOTH.get(), 2),
                    new ItemDrop(ModItems.ANIMAL_TOOTH.get(), 2),
                    new ItemDrop(Items.BRICK, 1),
                    new ItemDrop(Items.BRICK, 1),
                    new ItemDrop(Items.BRICK, 1),
                    new ItemDrop(Items.BRICK, 1),
                    new ItemDrop(Items.AIR, 1),
                    new ItemDrop(Items.AIR, 1),
                    new ItemDrop(Items.AIR, 1),
                    new ItemDrop(Items.AIR, 1),
                    new ItemDrop(Items.AIR, 1),
                    new ItemDrop(Items.AIR, 1),
                    new ItemDrop(Items.AIR, 1),
                    new ItemDrop(Items.AIR, 1),
                    new ItemDrop(Items.AIR, 1)
            );

            ItemDrop drop = possibilities.get(random.nextInt(possibilities.size()));

            ItemStack dropstack = new ItemStack(drop.item(), 1 + random.nextInt(drop.maxAmount()));

            return dropstack;
        }

        @Override
        protected boolean isValidTarget(LevelReader pLevel, BlockPos pPos) {
            return pLevel.getBlockState(pPos).is(BlockTags.MINEABLE_WITH_SHOVEL);
        }

        @Override
        public double acceptedDistance() {
            return 1.5;
        }
    }

    private int digCooldown = 0;

    private boolean canDigItem() {
        return getDigCooldown() <= 0;
    }

    private int getDigCooldown() {
        return digCooldown;
    }

    private void setDigCooldown(int pCooldown) {
        digCooldown = pCooldown;
    }

    private void setRandomDigCooldown() {
        setDigCooldown((20 * 60 * 6) + 20*60*this.getRandom().nextInt(5));
    }

    private int digCounter = 0;

    private boolean isDigging() {
        return this.entityData.get(LIZARD_DIGGING);
    }

    private void setDigging(boolean pDigging) {
        this.digCounter = pDigging ? 1 : 0;
        this.entityData.set(LIZARD_DIGGING, pDigging);
    }


}
