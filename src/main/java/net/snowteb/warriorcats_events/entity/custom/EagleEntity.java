package net.snowteb.warriorcats_events.entity.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.effect.ModEffects;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.item.ModItems;
import net.snowteb.warriorcats_events.sound.ModSounds;
import net.snowteb.warriorcats_events.util.ModTags;
import net.snowteb.warriorcats_events.zconfig.WCEServerConfig;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;
import tocraft.walkers.api.PlayerShape;

import java.util.*;
import java.util.function.Predicate;

public class EagleEntity extends FlyingMob implements GeoEntity, OwnableEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public AttackPhase attackPhase = AttackPhase.CIRCLE;
    public Vec3 moveTargetPoint = Vec3.ZERO;
    public BlockPos anchorPoint = BlockPos.ZERO;

    public boolean isOwnerCalling = false;

    private boolean orderedToSit;

    protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNERUUID_ID =
            SynchedEntityData.defineId(EagleEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID =
            SynchedEntityData.defineId(EagleEntity.class, EntityDataSerializers.BYTE);


    public enum Control {
        FLY,
        WALK
    }

    public static EntityDataAccessor<Integer> CONTROL_MODE =
            SynchedEntityData.defineId(EagleEntity.class, EntityDataSerializers.INT);

    public boolean isLatching = false;

    public boolean isLatching() {
        return this.isLatching;
    }

    public Control getControlMode() {
        return Control.values()[this.entityData.get(CONTROL_MODE)];
    }

    public void setControlMode(Control mode) {
        this.entityData.set(CONTROL_MODE, mode.ordinal());
    }

    public int latchingTimer = 300;

    EagleMoveControl flyControl = new EagleMoveControl(this);
    EagleWalkControl walkControl = new EagleWalkControl(this);

    int dontDropOwnerTicks = 0;

    private int switchControlTicks = 600 + this.random.nextInt(600);

    private boolean wasBornAgressive = true;

    public EagleEntity(EntityType<? extends EagleEntity> type, Level world) {
        super(type, world);

        if (this.getRandom().nextBoolean()) {
            this.moveControl = flyControl;
            this.setControlMode(Control.FLY);
        } else {
            this.moveControl = walkControl;
            this.setControlMode(Control.WALK);
        }

        if (this.getRandom().nextFloat() < 0.15) {
            this.wasBornAgressive = false;
        }

        this.lookControl = new EagleLookControl(this);
    }

    public static enum AttackPhase {
        CIRCLE,
        SWOOP;
    }


    public class EagleAttackStrategy extends Goal {
        private int nextSweepTick;

        @Override
        public boolean canUse() {
            LivingEntity livingentity = EagleEntity.this.getTarget();
            if (EagleEntity.this.getControlMode() != Control.FLY) return false;
            return livingentity != null && EagleEntity.this.canAttack(livingentity, TargetingConditions.DEFAULT);
        }

        @Override
        public boolean canContinueToUse() {
            if (EagleEntity.this.getControlMode() != Control.FLY) return false;

            return super.canContinueToUse();
        }

        @Override
        public void start() {
            this.nextSweepTick = this.adjustedTickDelay(10);
            EagleEntity.this.attackPhase = AttackPhase.CIRCLE;
            this.setAnchorAboveTarget();
        }

        @Override
        public void stop() {
            EagleEntity.this.anchorPoint = EagleEntity.this.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, EagleEntity.this.anchorPoint).above(10 + EagleEntity.this.random.nextInt(20));
        }

        @Override
        public void tick() {

            if (EagleEntity.this.attackPhase == AttackPhase.CIRCLE) {
                --this.nextSweepTick;
                if (this.nextSweepTick <= 0) {
                    EagleEntity.this.attackPhase = AttackPhase.SWOOP;
                    this.setAnchorAboveTarget();
                    this.nextSweepTick = this.adjustedTickDelay((8 + EagleEntity.this.random.nextInt(4)) * 20);
//                    EagleEntity.this.playSound(ModSounds.EAGLE_CALL.get(), 5.0F, 0.95F + EagleEntity.this.random.nextFloat() * 0.1F);
                }
            }

        }

        private void setAnchorAboveTarget() {
            EagleEntity.this.anchorPoint = EagleEntity.this.getTarget().blockPosition().above(20 + EagleEntity.this.random.nextInt(20));
            if (EagleEntity.this.anchorPoint.getY() < EagleEntity.this.level().getSeaLevel()) {
                EagleEntity.this.anchorPoint = new BlockPos(EagleEntity.this.anchorPoint.getX(), EagleEntity.this.level().getSeaLevel() + 1, EagleEntity.this.anchorPoint.getZ());
            }

        }
    }

    public abstract class EagleMoveToTarget extends Goal {
        public EagleMoveToTarget() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public void tick() {
            super.tick();
        }

        protected boolean touchingTarget() {
            return EagleEntity.this.moveTargetPoint.distanceToSqr(EagleEntity.this.getX(), EagleEntity.this.getY(), EagleEntity.this.getZ()) < 4.0D;
        }
    }

    public class EagleSweepAttackGoal extends EagleMoveToTarget {

        @Override
        public boolean canUse() {
            if (EagleEntity.this.getControlMode() != Control.FLY) return false;

            return EagleEntity.this.getTarget() != null && EagleEntity.this.attackPhase == AttackPhase.SWOOP && !EagleEntity.this.isLatching;
        }

        @Override
        public boolean canContinueToUse() {
            if (EagleEntity.this.getControlMode() != Control.FLY) return false;

            LivingEntity livingentity = EagleEntity.this.getTarget();
            if (livingentity == null) {
                return false;
            } else if (!livingentity.isAlive()) {
                return false;
            } else {
                if (livingentity instanceof Player player) {
                    if (!(PlayerShape.getCurrentShape(player) instanceof WCatEntity)) {
                        return false;
                    }

                    if (player.isSpectator() || player.isCreative()) {
                        return false;
                    }
                } else if (livingentity instanceof WCatEntity cat) {
                    if (!cat.isBaby()) {
                        return false;
                    }
                }


                if (!this.canUse()) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public void start() {
        }

        @Override
        public void stop() {
            EagleEntity.this.setTarget(null);
            EagleEntity.this.attackPhase = AttackPhase.CIRCLE;
        }

        @Override
        public void tick() {
            LivingEntity livingentity = EagleEntity.this.getTarget();
            if (livingentity != null) {

                EagleEntity.this.moveTargetPoint = new Vec3(livingentity.getX(), livingentity.getY(0.5D), livingentity.getZ());
                if (EagleEntity.this.getBoundingBox().inflate(0.2F).intersects(livingentity.getBoundingBox())) {
                    EagleEntity.this.doHurtTarget(livingentity);

                    if (!livingentity.hasEffect(ModEffects.EAGLE_ESCAPIST)) {
                        livingentity.startRiding(EagleEntity.this, true);
                        livingentity.hurtMarked = true;
                        livingentity.setDeltaMovement(Vec3.ZERO);

                        EagleEntity.this.isLatching = true;
                        if (livingentity instanceof WCatEntity cat && cat.isBaby()) {
                            EagleEntity.this.latchingTimer = 60;
                        }
                        EagleEntity.this.setDeltaMovement(EagleEntity.this.getDeltaMovement().add(0.0D, 2.0D, 0.0D));
                    }

                    EagleEntity.this.moveTargetPoint = EagleEntity.this.position().add(0, 20, 0);

                    EagleEntity.this.attackPhase = AttackPhase.CIRCLE;

                    if (!EagleEntity.this.isSilent()) {
                        EagleEntity.this.playSound(ModSounds.EAGLE_ATTACK.get(), 1.0F, 0.95F + EagleEntity.this.random.nextFloat() * 0.1F);
                    }
                } else if (EagleEntity.this.horizontalCollision || EagleEntity.this.hurtTime > 0) {
                    EagleEntity.this.attackPhase = AttackPhase.CIRCLE;
                }

            }
        }
    }


    private int ownerCallForTicks = 0;

    @Override
    public void tick() {

        if (!this.level().isClientSide()) {

            if (this.isOwnerCalling) {
                ownerCallForTicks--;
                if (ownerCallForTicks <= 0) {
                    this.setNewControlMode(Control.FLY);

                    this.switchControlTicks = 600 + this.random.nextInt(900);

                    this.anchorPoint = this.blockPosition().above(15);
                    this.isOwnerCalling = false;
                }
                this.setTarget(null);
                if (this.getOwner() != null) {
                    if (this.distanceTo(this.getOwner()) < 5) {
                        this.setNewControlMode(Control.WALK);

                        this.switchControlTicks = 600 + this.random.nextInt(900);

                        this.anchorPoint = this.blockPosition().above(15);
                        this.getNavigation().stop();

                        this.isOwnerCalling = false;
                    } else {
                        this.moveTargetPoint = this.getOwner().blockPosition().getCenter();
                        this.anchorPoint = this.getOwner().blockPosition().above(15);
                        this.setDeltaMovement(EagleEntity.this.getDeltaMovement().add(0.0D, 0.05D, 0.0D));
                        this.getNavigation().moveTo(this.getOwner(), 1f);
                    }
                } else {
                    this.setNewControlMode(Control.FLY);

                    this.switchControlTicks = 600 + this.random.nextInt(900);

                    this.anchorPoint = this.blockPosition().above(15);
                    this.isOwnerCalling = false;
                }
            } else {
                this.ownerCallForTicks = 300;
            }

            if (this.isLeashed() && this.getLeashHolder() != null) {
                Entity holder = this.getLeashHolder();

                double dist = this.distanceTo(holder);

                if (dist > 4.0D) {
                    Vec3 dir = new Vec3(holder.getX() - this.getX(),
                            holder.getY() - this.getY(),
                            holder.getZ() - this.getZ())
                            .normalize();

                    this.setDeltaMovement(this.getDeltaMovement().add(dir.scale(dist*0.04)));
                    this.move(MoverType.SELF, this.getDeltaMovement());
                }

                if (this.getControlMode() == Control.FLY) {
                    this.setNewControlMode(Control.WALK);
                }
            }


            if (this.isTame()) {
                if (this.getFirstPassenger() != null && this.getFirstPassenger() == this.getOwner()) {
                    if (this.dontDropOwnerTicks > 0) this.dontDropOwnerTicks--;

                    if ((level().getBlockState(this.blockPosition().below()).isSolid() || this.isInWater()) && this.dontDropOwnerTicks <= 0) {
                        this.getFirstPassenger().stopRiding();
                    }

                } else {
                    this.dontDropOwnerTicks = 100;
                }
            }

            if (this.isLatching && this.getControlMode() != Control.FLY) {
                this.setControlMode(Control.FLY);
            }

            if (this.getControlMode() == Control.FLY) {
                this.moveControl = flyControl;
                this.setNoGravity(true);
            } else {
                this.moveControl = walkControl;
                this.setNoGravity(false);
                this.setOnGround(true);
                this.setDeltaMovement(this.getDeltaMovement().add(0, -0.1, 0));
            }

            if (this.getTarget() != null && this.getControlMode() == Control.WALK) {
                this.setControlMode(Control.FLY);
            }

            if (this.switchControlTicks > 0) {
                this.switchControlTicks--;
            } else {
                if (this.getTarget() == null) {
                    this.navigation.stop();
                    if (this.getControlMode() == Control.FLY) {
                        this.setNewControlMode(Control.WALK);
                    } else {
                        this.setNewControlMode(Control.FLY);
                        this.anchorPoint = this.blockPosition().above(20);
                    }
                    this.switchControlTicks = 600 + this.random.nextInt(900);
                }
            }
        }

        if (!this.level().isClientSide()){
            if (this.isLatching) {
                latchingTimer--;
                if (this.tickCount % 20 == 0) {
                    if (EagleEntity.this.getFirstPassenger() != null) {
                        if (this.getFirstPassenger() instanceof WCatEntity cat) {
                            cat.hurt(this.damageSources().genericKill(), 0.01f);
                        } else {
                            EagleEntity.this.getFirstPassenger().hurt(EagleEntity.this.damageSources().genericKill(), 1f);
                        }
                    }
                }

                if (latchingTimer <= 0) {
                    this.isLatching = false;
                    this.setTarget(null);
                    if (this.getFirstPassenger() != null) {
                        if (this.getFirstPassenger() instanceof WCatEntity entity) {
                            entity.addEffect(new MobEffectInstance(ModEffects.EAGLE_ESCAPIST, 100));
                        }
                        this.ejectPassengers();
                        this.playSound(ModSounds.EAGLE_CALL.get(), 10.0F, 0.95F + this.random.nextFloat() * 0.1F);
                    }
                }
            } else {
                this.latchingTimer = 100;
            }
        }

        if (!this.level().isClientSide()) {

            if (this.tickCount % 5 == 0 && !this.wasBornAgressive && !this.isFlying()) {
                if (WCEServerConfig.SERVER.CAN_EAGLES_BE_TAMED.get()) {
                    List<Player> playersClose = this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(5F), player -> {
                        return !this.isOwnedBy(player) && !this.isTame() && (!this.canFinallyTame.getOrDefault(player.getUUID(), false));
                    });

                    if (!playersClose.isEmpty()) {
                        for (Player player : playersClose) {
                            if (player.isShiftKeyDown()) {
                                if (this.tamingStage.get(player.getUUID()) == null) {
                                    this.tamingStage.put(player.getUUID(), 0);
                                } else {
                                    this.tamingStage.put(player.getUUID(), this.tamingStage.get(player.getUUID()) + 1);
                                    if (this.level() instanceof ServerLevel sLevel) {
                                        sLevel.sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(),
                                                15, 0.1f, 0.2f, 0.1f, 0.025f);

                                    }
                                }

                                if (this.tamingStage.get(player.getUUID()) >= 100) {
                                    if (this.getRandom().nextFloat() < 0.04F) {
                                        this.tamingStage.remove(player.getUUID());
                                        this.canFinallyTame.put(player.getUUID(), true);
                                        player.displayClientMessage(Component.translatable("entity.eagle_starts_trust").withStyle(ChatFormatting.GREEN), true);
                                        if (this.level() instanceof ServerLevel sLevel) {
                                            sLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, this.getX(), this.getY() + 0.5, this.getZ(),
                                                    30, 0.2f, 0.2f, 0.2f, 0.1f);
                                            sLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, player.getX(), player.getY() + 0.5, player.getZ(),
                                                    30, 0.2f, 0.2f, 0.2f, 0.1f);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }

        super.tick();
        if (this.level().isClientSide && this.isFlying()) {
            float f = Mth.cos((float)(this.getUniqueFlapTickOffset() + this.tickCount) * 30.448451F * ((float)Math.PI / 180F) + (float)Math.PI);
            float f1 = Mth.cos((float)(this.getUniqueFlapTickOffset() + this.tickCount + 1) * 30.448451F * ((float)Math.PI / 180F) + (float)Math.PI);
            if (f > 0.0F && f1 <= 0.0F) {
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.PHANTOM_FLAP, this.getSoundSource(), 0.5F + this.random.nextFloat() * 0.045F, 1.25F + this.random.nextFloat() * 0.05F, false);
            }

        }
    }


    @Override
    public int getAmbientSoundInterval() {
        if (this.getRandom().nextFloat() < 0.50f) return 180 + this.getRandom().nextInt(10)*20;
        else return 220 + this.getRandom().nextInt(10)*30;
    }

    public void setNewControlMode(Control newMode) {
        if (this.getControlMode() != newMode) {
            this.setControlMode(newMode);

            this.setDeltaMovement(Vec3.ZERO);
            this.setSpeed(0);

            if (newMode == Control.FLY) {
                this.setNoGravity(true);
                this.moveControl = flyControl;
            } else {
                this.setNoGravity(false);
                this.moveControl = walkControl;
            }
        } else {
            this.setControlMode(newMode);
        }
    }


    public int getUniqueFlapTickOffset() {
        return this.getId() * 3;
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
    }

    @Override
    public boolean canRiderInteract() {
        return false;
    }

    @Override
    public void rideTick() {
        super.rideTick();
    }

    public class EagleCircleAroundAnchorGoal extends EagleMoveToTarget {
        private float angle;
        private float distance;
        private float height;
        private float clockwise;

        @Override
        public boolean canUse() {
            if (EagleEntity.this.getControlMode() != Control.FLY) return false;

            return EagleEntity.this.getTarget() == null || EagleEntity.this.attackPhase == AttackPhase.CIRCLE;
        }

        @Override
        public boolean canContinueToUse() {
            if (EagleEntity.this.getControlMode() != Control.FLY) return false;

            return super.canContinueToUse();
        }

        @Override
        public void start() {
            this.distance = 5.0F + EagleEntity.this.random.nextFloat() * 10.0F;
            this.height = -4.0F + EagleEntity.this.random.nextFloat() * 9.0F;
            this.clockwise = EagleEntity.this.random.nextBoolean() ? 1.0F : -1.0F;
            this.selectNext();
        }

        @Override
        public void tick() {
            if (EagleEntity.this.random.nextInt(this.adjustedTickDelay(350)) == 0) {
                this.height = -4.0F + EagleEntity.this.random.nextFloat() * 9.0F;
            }

            if (EagleEntity.this.random.nextInt(this.adjustedTickDelay(250)) == 0) {
                ++this.distance;
                if (this.distance > 15.0F) {
                    this.distance = 5.0F;
                    this.clockwise = -this.clockwise;
                }
            }

            if (EagleEntity.this.random.nextInt(this.adjustedTickDelay(450)) == 0) {
                this.angle = EagleEntity.this.random.nextFloat() * 2.0F * (float)Math.PI;
                this.selectNext();
            }

            if (this.touchingTarget()) {
                this.selectNext();
            }

            if (EagleEntity.this.moveTargetPoint.y < EagleEntity.this.getY() && !EagleEntity.this.level().isEmptyBlock(EagleEntity.this.blockPosition().below(1))) {
                this.height = Math.max(1.0F, this.height);
                this.selectNext();
            }

            if (EagleEntity.this.moveTargetPoint.y > EagleEntity.this.getY() && !EagleEntity.this.level().isEmptyBlock(EagleEntity.this.blockPosition().above(1))) {
                this.height = Math.min(-1.0F, this.height);
                this.selectNext();
            }

        }

        private void selectNext() {
            if (BlockPos.ZERO.equals(EagleEntity.this.anchorPoint)) {
                EagleEntity.this.anchorPoint = EagleEntity.this.blockPosition();
            }

            this.angle += this.clockwise * 15.0F * ((float)Math.PI / 180F);
            EagleEntity.this.moveTargetPoint = Vec3.atLowerCornerOf(EagleEntity.this.anchorPoint).add((double)(this.distance * Mth.cos(this.angle)), (double)(-4.0F + this.height), (double)(this.distance * Mth.sin(this.angle)));
        }
    }


    public class EagleAttackTargetsGoal extends Goal {
        private int nextScanTick = reducedTickDelay(20);

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean canUse() {
            if (!EagleEntity.this.wasBornAgressive && EagleEntity.this.getLastHurtByMob() == null) return false;

            if (this.nextScanTick > 0) {
                --this.nextScanTick;
                return false;
            } else {
                this.nextScanTick = reducedTickDelay(80);
                Predicate<LivingEntity> predicate =
                        entity -> ((entity instanceof Player player && PlayerShape.getCurrentShape(player) instanceof WCatEntity)
                                || (entity instanceof WCatEntity cat && cat.isBaby()) || (entity instanceof SquirrelEntity || entity instanceof MouseEntity || entity instanceof PigeonEntity))
                                && !entity.hasEffect(ModEffects.EAGLE_ESCAPIST) && !entity.isInvisible() && entity.getVehicle() == null;

                List<LivingEntity> list2 = EagleEntity.this.level().getEntitiesOfClass(LivingEntity.class,  EagleEntity.this.getBoundingBox().inflate(48.0D, 48.0D, 48.0D), predicate);

                if (!list2.isEmpty()) {
                    list2.sort(Comparator.<Entity, Double>comparing(Entity::getY).reversed());

                    for(LivingEntity entity : list2) {
                        if (EagleEntity.this.canAttack(entity, TargetingConditions.DEFAULT)) {
                            EagleEntity.this.setTarget(entity);
                            EagleEntity.this.setControlMode(Control.FLY);

                            if (!EagleEntity.this.wasBornAgressive) {
                                if (entity != EagleEntity.this.getLastHurtByMob()) {
                                    return false;
                                }
                            }

                            return true;
                        }
                    }
                }

                return false;
            }
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean canContinueToUse() {
            LivingEntity livingentity = EagleEntity.this.getTarget();
            return livingentity != null && EagleEntity.this.canAttack(livingentity, TargetingConditions.DEFAULT);
        }
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        this.anchorPoint = this.blockPosition().above(5);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    public boolean canAttack(LivingEntity pTarget) {
        if (this.level().getDifficulty() == Difficulty.PEACEFUL) return false;

        if (this.isTame()) {
            if (this.getOwner() == pTarget) {
                return false;
            }
        }
        return super.canAttack(pTarget);
    }

    public static AttributeSupplier.Builder setAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30D)
                .add(Attributes.ATTACK_SPEED, 1.0f)
                .add(Attributes.ATTACK_DAMAGE, 1f)
                .add(Attributes.MOVEMENT_SPEED, 2.0f)
                .add(Attributes.FLYING_SPEED, 2.0f);
    }

    @Override
    protected void registerGoals() {
//        this.preyTarget = new NearestAttackableTargetGoal<>(this, Animal.class, 10, false, false, (target) -> {
//            return this.getTarget() == null && ((target instanceof MouseEntity) || (target instanceof SquirrelEntity));
//        });
//        this.enemyTarget = new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, false, false, (target) -> {
//            return this.getTarget() == null && this.wasBornAgressive && ((target instanceof WCatEntity cat && (cat.isBaby()))
//                    ||
//                    (target instanceof Player player && PlayerShape.getCurrentShape(player) instanceof WCatEntity));
//        });


        this.goalSelector.addGoal(1, new EagleAttackStrategy());
        this.goalSelector.addGoal(2, new EagleSweepAttackGoal());
        this.goalSelector.addGoal(3, new EagleCircleAroundAnchorGoal());
        this.goalSelector.addGoal(4, new EagleGroundStrollGoal(this));
        this.goalSelector.addGoal(5, new EagleLookAroundGoal(this));

        this.targetSelector.addGoal(1, new EagleAttackTargetsGoal());


    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>
                (this, "controller", 0, this::predicate));

    }

    @Override
    protected int getBaseExperienceReward() {
        return 30 + 5*this.random.nextInt(4);
    }

    public boolean isFlying() {
        return !this.onGround() && !this.isInFluidType();
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {
        if (this.isFlying()) {
            if (this.isLatching) {

            } else {
                tAnimationState.getController().setAnimation(RawAnimation.begin()
                        .then("animation.eagle.flying", Animation.LoopType.LOOP));
                return PlayState.CONTINUE;
            }
        }

        if (tAnimationState.isMoving()) {
            if (this.onGround()) {
                tAnimationState.getController().setAnimation(RawAnimation.begin()
                        .then("animation.eagle.walk", Animation.LoopType.LOOP));
                return PlayState.CONTINUE;
            }
        }

        if (!tAnimationState.isMoving() && !this.isFlying()) {
            tAnimationState.getController().setAnimation(RawAnimation.begin()
                    .then("animation.eagle.idle", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        return PlayState.CONTINUE;
    }


    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        ItemStack itemEarned = new ItemStack(ModItems.EAGLE_MEAT_FOOD.get());
        if (itemEarned.isEmpty()) return;

        ItemEntity drop = new ItemEntity(this.level(), this.getX(), this.getY() + 0.2, this.getZ(),
                itemEarned.copyWithCount(1 + this.getRandom().nextInt(4)*2));

        if (this.level() instanceof ServerLevel sLevel) {
            sLevel.addFreshEntity(drop);

            if (this.random.nextFloat() < 0.05f) {
                this.spawnAtLocation(ModItems.GOLDEN_EAGLE_SKULL.get());
            }
        }

        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("AX", this.anchorPoint.getX());
        pCompound.putInt("AY", this.anchorPoint.getY());
        pCompound.putInt("AZ", this.anchorPoint.getZ());

        pCompound.putBoolean("Agressive", this.wasBornAgressive);

        if (this.getControlMode() != null) {
            pCompound.putInt("ControlMode", this.getControlMode().ordinal());
        } else {
            pCompound.putInt("ControlMode", 0);
        }


        if (this.getOwnerUUID() != null) {
            pCompound.putUUID("Owner", this.getOwnerUUID());
        }

        pCompound.putBoolean("Sitting", this.orderedToSit);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("AX")) {
            this.anchorPoint = new BlockPos(pCompound.getInt("AX"), pCompound.getInt("AY"), pCompound.getInt("AZ"));
        }

        if (pCompound.contains("Agressive")) {
            this.wasBornAgressive = pCompound.getBoolean("Agressive");
        }

        if (pCompound.contains("ControlMode")) {
            this.setControlMode(Control.values()[pCompound.getInt("ControlMode")]);
        }


        UUID uuid;
        if (pCompound.hasUUID("Owner")) {
            uuid = pCompound.getUUID("Owner");
        } else {
            String s = pCompound.getString("Owner");
            uuid = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), s);
        }

        if (uuid != null) {
            try {
                this.setOwnerUUID(uuid);
                this.setTame(true);
            } catch (Throwable throwable) {
                this.setTame(false);
            }
        }

        this.orderedToSit = pCompound.getBoolean("Sitting");
        this.setInSittingPose(this.orderedToSit);
    }


    @Override
    public boolean canBeLeashed() {
        if (!this.isTame()) {
            return false;
        }
        return super.canBeLeashed();
    }

    protected void spawnTamingParticles(boolean pTamed) {
        ParticleOptions particleoptions = ParticleTypes.HEART;
        if (!pTamed) {
            particleoptions = ParticleTypes.SMOKE;
        }

        for(int i = 0; i < 7; ++i) {
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;
            this.level().addParticle(particleoptions, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
        }

    }

    @Override
    public void handleEntityEvent(byte pId) {
        if (pId == 7) {
            this.spawnTamingParticles(true);
        } else if (pId == 6) {
            this.spawnTamingParticles(false);
        } else {
            super.handleEntityEvent(pId);
        }

    }

    public boolean isTame() {
        return (this.entityData.get(DATA_FLAGS_ID) & 4) != 0;
    }

    public void setTame(boolean pTamed) {
        byte b0 = this.entityData.get(DATA_FLAGS_ID);
        if (pTamed) {
            this.entityData.set(DATA_FLAGS_ID, (byte)(b0 | 4));
        } else {
            this.entityData.set(DATA_FLAGS_ID, (byte)(b0 & -5));
        }

    }

    public void tame(Player pPlayer) {
        this.setTame(true);
        this.setOwnerUUID(pPlayer.getUUID());

    }

    public boolean isInSittingPose() {
        return (this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
    }

    public void setInSittingPose(boolean pSitting) {
        byte b0 = this.entityData.get(DATA_FLAGS_ID);
        if (pSitting) {
            this.entityData.set(DATA_FLAGS_ID, (byte)(b0 | 1));
        } else {
            this.entityData.set(DATA_FLAGS_ID, (byte)(b0 & -2));
        }

    }

    public void setOwnerUUID(@Nullable UUID pUuid) {
        this.entityData.set(DATA_OWNERUUID_ID, Optional.ofNullable(pUuid));
    }

    @Override
    public @Nullable UUID getOwnerUUID() {
        return this.entityData.get(DATA_OWNERUUID_ID).orElse(null);
    }

    @Override
    public @Nullable PlayerTeam getTeam() {
        if (this.isTame()) {
            LivingEntity livingentity = this.getOwner();
            if (livingentity != null) {
                return livingentity.getTeam();
            }
        }
        return super.getTeam();
    }

    public boolean isOwnedBy(LivingEntity pEntity) {
        return pEntity == this.getOwner();
    }

    @Override
    public boolean isAlliedTo(Entity pEntity) {
        if (this.isTame()) {
            LivingEntity owner = this.getOwner();

            if (pEntity == owner) {
                return true;
            }

            if (owner != null && owner.isAlliedTo(pEntity)) {
                return true;
            }

            if (pEntity instanceof TamableAnimal animal) {
                UUID ownerUUID = this.getOwnerUUID();
                UUID otherOwnerUUID = animal.getOwnerUUID();

                if (ownerUUID != null && ownerUUID.equals(otherOwnerUUID)) {
                    return true;
                }
            }
        }

        return super.isAlliedTo(pEntity);
    }

    @Override
    public boolean canAttack(LivingEntity pLivingentity, TargetingConditions pCondition) {

        if (this.isAlliedTo(pLivingentity)) {
            return false;
        }

        if (this.isTame() && pLivingentity instanceof Player) {
            return false;
        }

        if (this.isTame() && pLivingentity instanceof WCatEntity) {
            return false;
        }

        return super.canAttack(pLivingentity, pCondition);
    }

    @Override
    public void die(DamageSource pDamageSource) {
        super.die(pDamageSource);
        Component deathMessage = this.getCombatTracker().getDeathMessage();
        super.die(pDamageSource);

        if (this.dead)
            if (!this.level().isClientSide && this.level().getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES) && this.getOwner() instanceof ServerPlayer) {
                this.getOwner().sendSystemMessage(deathMessage);
            }

    }

    public boolean isOrderedToSit() {
        return this.orderedToSit;
    }

    public void setOrderedToSit(boolean pOrderedToSit) {
        this.orderedToSit = pOrderedToSit;
    }


    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(CONTROL_MODE, 0);
        builder.define(DATA_FLAGS_ID, (byte)0);
        builder.define(DATA_OWNERUUID_ID, Optional.empty());
        super.defineSynchedData(builder);
    }

    @Override
    protected boolean isSunBurnTick() {
        return false;
    }

    @Override
    public void aiStep() {

        if (this.isInWater()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0, 0.125, 0));
        }

        super.aiStep();
    }

    public class EagleLookControl extends LookControl {
        public EagleLookControl(Mob pMob) {
            super(pMob);
        }

        @Override
        public void tick() {
            if (((EagleEntity) this.mob).getControlMode() != Control.WALK) return;
            super.tick();
        }
    }

    public class EagleMoveControl extends MoveControl {

        public EagleMoveControl(EagleEntity pMob) {
            super(pMob);
        }

        public void tick() {
            if (EagleEntity.this.horizontalCollision) {
                EagleEntity.this.setYRot(EagleEntity.this.getYRot() + 180.0F);
                this.mob.setSpeed(0.1F);
            }

            double d0 = EagleEntity.this.moveTargetPoint.x - EagleEntity.this.getX();
            double d1 = EagleEntity.this.moveTargetPoint.y - EagleEntity.this.getY();
            if (EagleEntity.this.getControlMode() == Control.WALK) {
                d1 = 0;
            }
            double d2 = EagleEntity.this.moveTargetPoint.z - EagleEntity.this.getZ();
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            if (Math.abs(d3) > (double)1.0E-5F) {
                double d4 = 1.0D - Math.abs(d1 * (double)0.7F) / d3;
                d0 *= d4;
                d2 *= d4;
                d3 = Math.sqrt(d0 * d0 + d2 * d2);
                double d5 = Math.sqrt(d0 * d0 + d2 * d2 + d1 * d1);
                float f = EagleEntity.this.getYRot();
                float f1 = (float)Mth.atan2(d2, d0);
                float f2 = Mth.wrapDegrees(EagleEntity.this.getYRot() + 90.0F);
                float f3 = Mth.wrapDegrees(f1 * (180F / (float)Math.PI));
                EagleEntity.this.setYRot(Mth.approachDegrees(f2, f3, 4.0F) - 90.0F);
                EagleEntity.this.yBodyRot = EagleEntity.this.getYRot();
                if (Mth.degreesDifferenceAbs(f, EagleEntity.this.getYRot()) < 3.0F) {
                    this.mob.setSpeed(Mth.approach(this.mob.getSpeed(), 1.8F, 0.005F * (1.8F / this.mob.getSpeed())));
                } else {
                    this.mob.setSpeed(Mth.approach(this.mob.getSpeed(), 0.2F, 0.025F));
                }

                float f4 = (float)(-(Mth.atan2(-d1, d3) * (double)(180F / (float)Math.PI)));
                EagleEntity.this.setXRot(f4);
                float f5 = EagleEntity.this.getYRot() + 90.0F;
                double d6 = (double)(this.mob.getSpeed() * Mth.cos(f5 * ((float)Math.PI / 180F))) * Math.abs(d0 / d5);
                double d7 = (double)(this.mob.getSpeed() * Mth.sin(f5 * ((float)Math.PI / 180F))) * Math.abs(d2 / d5);
                double d8 = (double)(this.mob.getSpeed() * Mth.sin(f4 * ((float)Math.PI / 180F))) * Math.abs(d1 / d5);
                Vec3 vec3 = EagleEntity.this.getDeltaMovement();
                EagleEntity.this.setDeltaMovement(vec3.add((new Vec3(d6, d8, d7)).subtract(vec3).scale(0.2D)));
            }

        }

    }

    public Vec3 getGroundTarget() {
        for (int i = 0; i < 10; i++) {
            double x = this.getX() + (random.nextDouble() - 0.5) * 10;
            double z = this.getZ() + (random.nextDouble() - 0.5) * 10;
            BlockPos pos = new BlockPos((int)x, (int)this.getY(), (int)z);

            BlockPos ground = this.level().getHeightmapPos(
                    Heightmap.Types.MOTION_BLOCKING, pos);

            if (this.level().isEmptyBlock(ground.above())) {
                return Vec3.atCenterOf(ground.above());
            }
        }
        return this.position();
    }

    public class EagleWalkControl extends MoveControl {

        public EagleWalkControl(Mob pMob) {
            super(pMob);
            this.speedModifier = 0.5f;
        }
        public void tick() {
            super.tick();
        }

    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return super.createNavigation(level);
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        this.moveControl = flyControl;
        this.setNewControlMode(Control.FLY);
        this.setNoGravity(true);

        if (!this.wasBornAgressive) {
            if (pSource.getEntity() != null) {
                this.setTarget((LivingEntity) pSource.getEntity());

                if (pSource.getEntity() instanceof Player player) {
                    this.canFinallyTame.put(player.getUUID(), false);
                    this.tamingStage.put(player.getUUID(), 0);
                }
            }
        }

        if (this.getFirstPassenger() != null) {
            if (this.getRandom().nextFloat() < 0.77 && pSource.getEntity() != this.getFirstPassenger()) {
                this.getFirstPassenger().stopRiding();
                if (this.getFirstPassenger() instanceof LivingEntity entity) {
                    entity.addEffect(new MobEffectInstance(ModEffects.EAGLE_ESCAPIST, 100));
                }
            }
        }

        return super.hurt(pSource, pAmount);
    }

    public static class EagleGroundStrollGoal extends Goal {

        private final EagleEntity mob;
        private int cooldown = 0;

        public EagleGroundStrollGoal(EagleEntity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {

            if (cooldown > 0) {
                cooldown--;
                return false;
            }

            if (mob.getControlMode() != Control.WALK) return false;

            return true;
        }

        @Override
        public void start() {
            this.cooldown = 60 + mob.getRandom().nextInt(100);
            Vec3 target = mob.getGroundTarget();

            mob.getNavigation().moveTo(target.x, target.y, target.z, 0.25);
            stop();
        }

        @Override
        public boolean canContinueToUse() {
            return mob.getControlMode() == Control.WALK && !mob.getNavigation().isDone();
        }
    }

    public static class EagleLookAroundGoal extends RandomLookAroundGoal {
        private final EagleEntity eagle;

        public EagleLookAroundGoal(EagleEntity mob) {
            super(mob);
            this.eagle = mob;
            this.setFlags(EnumSet.of(Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (this.eagle.getControlMode() != Control.WALK) return false;
            if (this.eagle.getFirstPassenger() != null) return false;

           if (this.eagle.getRandom().nextFloat() < 0.1f) {
               return true;
           }
           return false;
        }

        @Override
        public boolean canContinueToUse() {
            if (this.eagle.getControlMode() != Control.WALK) return false;
            return super.canContinueToUse();
        }
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    class EagleBodyRotationControl extends BodyRotationControl {
        public EagleBodyRotationControl(Mob pMob) {
            super(pMob);
        }

        @Override
        public void clientTick() {
            if (EagleEntity.this.getControlMode() == Control.FLY) {
                EagleEntity.this.yHeadRot = EagleEntity.this.yBodyRot;
                EagleEntity.this.yBodyRot = EagleEntity.this.getYRot();
            } else {
                super.clientTick();
            }
        }
    }

    @Override
    protected BodyRotationControl createBodyControl() {
        return new EagleBodyRotationControl(this);
    }

//    @Override
//    protected float getStandingEyeHeight(Pose pPose, EntityDimensions pSize) {
//        return pSize.height * 0.35F;
//    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.EAGLE_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return ModSounds.EAGLE_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.EAGLE_DEATH.get();
    }

//    @Override
//    public MobType getMobType() {
//        return MobType.UNDEFINED;
//    }

    private float flightSpeedModifier;

    public void setAdditionalSpeed(float additionalSpeed) {
        this.flightSpeedModifier += additionalSpeed;
    }

    public void setFlySpeed(float speed) {
        this.flightSpeedModifier = speed;
    }

    public float getFlySpeed() {
        return this.flightSpeedModifier;
    }

    @Override
    public void travel(Vec3 travelVector) {

        if (this.isVehicle() && this.getControllingPassenger() instanceof Player player) {

            float forward = player.zza;

            if (this.isFlying()) {
                if ((forward < 0) && this.getFlySpeed() > 0.00F) {
                    this.setAdditionalSpeed(-0.010f);
                }
                if ((this.getFlySpeed() > 0.4207)) {
                    this.setAdditionalSpeed(-0.010f);
                }

                if (this.getFlySpeed() < 0.420F) {
                    this.setAdditionalSpeed(0.0014f);
                }
            } else {
                this.setFlySpeed(0f);
            }


            this.setYRot(player.getYRot());
            this.yRotO = this.getYRot();
            this.setXRot(player.getXRot() * -0.7F);

            this.setYHeadRot(player.getYHeadRot());

            Vec3 look = player.getLookAngle();

            this.setDeltaMovement(look.scale(0.1 + this.getFlySpeed()));
            this.move(MoverType.SELF, this.getDeltaMovement());
            return;
        } else {
            this.setFlySpeed(0f);
        }

        super.travel(travelVector);
    }

    @Override
    protected void removePassenger(Entity pPassenger) {
        super.removePassenger(pPassenger);

        if (!this.level().isClientSide() && !this.wasBornAgressive && this.isTame()) {
            this.setDeltaMovement(Vec3.ZERO);

            this.moveTargetPoint = this.position().add(0, 20, 0);
            this.anchorPoint = this.blockPosition().above(15);

            if (pPassenger instanceof Player player) {
                player.addEffect(new MobEffectInstance(ModEffects.EAGLE_ESCAPIST, 100));
            }
        }

        if (pPassenger instanceof Player) pPassenger.stopRiding();
    }

    @Override
    protected void positionRider(Entity passenger, MoveFunction callback) {
        Vec3 pasPos = this.getPassengerRidingPosition(passenger);
        Vec3 vehPos = passenger.getVehicleAttachmentPoint(this);
        callback.accept(passenger, pasPos.x - vehPos.x, pasPos.y - vehPos.y - 1.5f, pasPos.z - vehPos.z);
    }

    @Override
    protected boolean canRide(Entity pVehicle) {
        return super.canRide(pVehicle);
    }

    @Override
    public LivingEntity getControllingPassenger() {
        if (this.isLatching()) return null;
        Entity entity = this.getFirstPassenger();
        if (entity != null && entity != this.getOwner()) return null;

        return entity instanceof LivingEntity ? (LivingEntity) entity : null;
    }

    Map<UUID, Integer> tamingStage = new HashMap<>();
    Map<UUID, Boolean> canFinallyTame = new HashMap<>();

    @Override
    protected InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {

        if (!this.level().isClientSide()) {
            if (PlayerShape.getCurrentShape(pPlayer) instanceof WCatEntity) {
                if (!this.isTame() && !this.wasBornAgressive && !this.isFlying() && !this.isLatching()) {
                    if (pPlayer.getItemInHand(InteractionHand.MAIN_HAND).is(ModItems.SHREDDED_MEAT.get()) && this.canFinallyTame.get(pPlayer.getUUID())) {
                        if (!pPlayer.getAbilities().instabuild) {
                            pPlayer.getItemInHand(InteractionHand.MAIN_HAND).shrink(1);
                        }

                        int tameRoll = this.random.nextInt(15);

                        if (tameRoll == 0) {
                            this.tame(pPlayer);
                            this.level().broadcastEntityEvent(this, (byte) 7);
                            pPlayer.displayClientMessage(Component.translatable("entity.eagle_fully_trusts").withStyle(ChatFormatting.AQUA), true);

                            if (pPlayer instanceof ServerPlayer serverPlayer) {
                                MinecraftServer server = serverPlayer.getServer();
                                if (server != null) {
                                    AdvancementHolder adv = server.getAdvancements()
                                            .get(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID,"tame_eagle"));
                                    if (adv != null) {
                                        serverPlayer.getAdvancements().award(adv, "tame_eagle");
                                    }
                                }
                            }

                        } else {
                            this.level().broadcastEntityEvent(this, (byte) 6);
                        }

                        if (this.level() instanceof ServerLevel sLevel) {
                            sLevel.playSound(null, this.blockPosition(), SoundEvents.CAT_EAT, SoundSource.NEUTRAL, 0.6F, 1.0F);
                            sLevel.playSound(null, this.blockPosition(), SoundEvents.GENERIC_EAT, SoundSource.NEUTRAL, 0.6F, 1.0F);
                            sLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(pPlayer.getItemInHand(InteractionHand.MAIN_HAND).copy().getItem())), this.getX(), this.getY() + 0.5, this.getZ(), 30, 0.4f, 0.4f, 0.4f, 0.1f);
                        }

                        this.gameEvent(GameEvent.EAT);
                        return InteractionResult.SUCCESS;
                    }
                }

                if (pPlayer.getItemInHand(InteractionHand.MAIN_HAND).is(ModTags.Items.PREY)) {
                    if (this.isTame() && this.getHealth() < this.getMaxHealth()) {
                        double currentHealth = this.getHealth();

                        if (!pPlayer.getAbilities().instabuild) {
                            pPlayer.getItemInHand(InteractionHand.MAIN_HAND).shrink(1);
                        }

                        this.heal(4);

                        double newHealth = this.getHealth();

                        int difference = (int) (newHealth - currentHealth);

                        if (this.level() instanceof ServerLevel sLevel) {
                            sLevel.playSound(null, this.blockPosition(), SoundEvents.CAT_EAT, SoundSource.NEUTRAL, 0.6F, 1.0F);
                            sLevel.playSound(null, this.blockPosition(), SoundEvents.GENERIC_EAT, SoundSource.NEUTRAL, 0.6F, 1.0F);
                            sLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(pPlayer.getItemInHand(InteractionHand.MAIN_HAND).getItem())), this.getX(), this.getY() + 0.5, this.getZ(), 30, 0.4f, 0.4f, 0.4f, 0.1f);
                            sLevel.sendParticles(ParticleTypes.HEART, this.getX(), this.getY() + 0.5, this.getZ(), difference, 0.4f, 0.4f, 0.4f, 0.1f);
                        }

                        return InteractionResult.SUCCESS;
                    }
                }

                if (this.isTame() && WCEServerConfig.SERVER.CAN_EAGLES_BE_TAMED.get()) {
                    if (!this.wasBornAgressive && !this.isLatching() && this.isOwnedBy(pPlayer)) {
                        pPlayer.startRiding(this, true);
                        pPlayer.hurtMarked = true;
                        pPlayer.setDeltaMovement(Vec3.ZERO);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }

        return super.mobInteract(pPlayer, pHand);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public boolean isPersistenceRequired() {
        return true;
    }
}
