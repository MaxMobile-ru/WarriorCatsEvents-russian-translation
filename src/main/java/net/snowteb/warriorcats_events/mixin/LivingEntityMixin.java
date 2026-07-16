package net.snowteb.warriorcats_events.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.snowteb.warriorcats_events.WCEClient;
import net.snowteb.warriorcats_events.clan.ClanData;
import net.snowteb.warriorcats_events.climbing.ClimbingConstants;
import net.snowteb.warriorcats_events.diseases.DiseaseTypes;
import net.snowteb.warriorcats_events.diseases.Diseaseable;
import net.snowteb.warriorcats_events.effect.ModEffects;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.managers.ClimbDataAccessor;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.s2c.others.SyncExhaustionPacket;
import net.snowteb.warriorcats_events.sound.ModSounds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tocraft.walkers.api.PlayerShape;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements ClimbDataAccessor {

    @ModifyArg(method = "hurt", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;actuallyHurt(Lnet/minecraft/world/damagesource/DamageSource;F)V"), index = 1)
    public float reduceFallDamageEffect(DamageSource source, float amount) {
        LivingEntity entity = (LivingEntity)(Object)this;

        if (source.is(DamageTypes.FALL)) {
            if (entity.hasEffect(ModEffects.EAGLE_ESCAPIST)) {
                amount = 0.25f*amount;
            }
        }

        if (source.is(DamageTypes.FALL)) {
            if (entity instanceof Diseaseable<?> diseaseable) {
                boolean isAShape = entity instanceof WCatEntity cat && !(cat.getPlayerBoundUuid().equals(ClanData.EMPTY_UUID));
                if (amount > 15f && !isAShape) {
                    if (diseaseable.getEntity().getRandom().nextFloat() < 0.65f) {
                        diseaseable.addDisease(DiseaseTypes.BROKEN_PAW, true);
                    }
                }
            }
        }


        return amount;
    }

    @Inject(method = "startSleeping", at = @At("TAIL"))
    public void startSleep(BlockPos pPos, CallbackInfo ci) {
        LivingEntity entity =  (LivingEntity) (Object) this;
        if (entity instanceof ServerPlayer sPlayer) {
            if (PlayerShape.getCurrentShape(sPlayer) instanceof WCatEntity catShape) {
                catShape.setAnimIndex(9);
                PlayerShape.updateShapes(sPlayer, catShape);
                sPlayer.getPersistentData().putInt("wcat_animation_playing", sPlayer.server.getTickCount() + 10);
            }
        }
    }


    @Unique
    private static final EntityDataAccessor<Boolean> wce$CLIMBING =
            SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.BOOLEAN);

    @Unique
    private static final EntityDataAccessor<Integer> wce$GRACE_TIMER =
            SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.INT);

    @Unique
    private static final EntityDataAccessor<Integer> wce$CLIMBING_TICKS =
            SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.INT);

    @Unique
    private static final int MAX_CLIMB_TIME = 800;

    @Inject(method = "onClimbable", at = @At("HEAD"), cancellable = true)
    protected void allowPlayerClimb(CallbackInfoReturnable<Boolean> cir) {
        if ((LivingEntity) (Object) this instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);
            if (shape instanceof WCatEntity) {
                if (this.wce$isClimbing()) cir.setReturnValue(true);
            }
        }
    }


    @Inject(method = "handleOnClimbable", at = @At("HEAD"), cancellable = true)
    protected void handleClimb(CallbackInfoReturnable<Vec3> cir) {
        if ((LivingEntity) (Object) this instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);

            if (shape instanceof WCatEntity && this.wce$isClimbing()) {

                player.resetFallDistance();

                Vec3 pDeltaMovement = player.getDeltaMovement();

                double d0 = Mth.clamp(pDeltaMovement.x, -0.15F, 0.15F);
                double d1 = Mth.clamp(pDeltaMovement.z, -0.15F, 0.15F);
                double d2 = Math.max(pDeltaMovement.y, -0.15F);
                if (d2 < 0.0D) {
                    d2 = wce$getFallVelocity(this.wce$getClimbTick());
                }
                if (player.isShiftKeyDown()) d2 -= 0.025D;

                pDeltaMovement = new Vec3(d0, d2, d1);

                cir.setReturnValue(pDeltaMovement);
            }
        }
    }

    @Unique
    private double wce$getFallVelocity(int climbTicks) {
        return -0.01D - (0.1D* wce$getClimbCoefficientToOne(climbTicks));
    }

    @ModifyConstant(method = "handleRelativeFrictionAndCalculateMovement", constant = @Constant(doubleValue = 0.2D))
    private double modifyClimbSpeed(double original) {
        if ((LivingEntity) (Object) this instanceof Player player) {
            if (PlayerShape.getCurrentShape(player) instanceof WCatEntity) {
                if (this.wce$isClimbing()) {
                    if (this.wce$getClimbTick() < MAX_CLIMB_TIME - 160) {
                        return 0.07D + (0.08D * wce$getClimbCoefficientToZero(this.wce$getClimbTick()));
                    } else {
                        return wce$getFallVelocity(this.wce$getClimbTick());
                    }
                }
            }
        }
        return original;
    }

    @Unique
    private double wce$getClimbCoefficientToZero(int climbTime) {
        return (double) (MAX_CLIMB_TIME - climbTime) / MAX_CLIMB_TIME;
    }

    @Unique
    private double wce$getClimbCoefficientToOne(int climbTime) {
        return (double) (climbTime) / MAX_CLIMB_TIME;
    }

    @Unique
    private void wce$climbManager() {

        if ((LivingEntity) (Object) this instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);
            if (shape instanceof WCatEntity && this.wce$isClimbing()) {
                wce$manageClimbTick(player);
            }
        }
    }

    @Unique
    private int wce$exhaustionLevel = 0;
    @Unique
    private static final int MAX_EXHAUSTION_LEVEL = 100;
    @Unique
    private boolean wce$isTired = false;
    @Unique
    private void wce$exhaustionManager() {
        if ((LivingEntity) (Object) this instanceof ServerPlayer player) {
            if (player.tickCount % 20 == 0){
                LivingEntity shape = PlayerShape.getCurrentShape(player);

                int sample = wce$exhaustionLevel;

                if (shape instanceof WCatEntity && this.wce$isClimbing()) {
                    if (wce$exhaustionLevel < MAX_EXHAUSTION_LEVEL) wce$exhaustionLevel++;
                    if (player instanceof Diseaseable<?> diseaseable
                            && diseaseable.hasDisease(DiseaseTypes.SORE_PADS)) wce$exhaustionLevel += 3;

                    float exhaustionToAdd = 3f * wce$getExhaustionCoefficientToOne(wce$exhaustionLevel);

                    float coefficient = wce$getExhaustionCoefficientToOne(wce$exhaustionLevel);
                    int climbTickToAdd = (int) (200 * coefficient);

                    if (coefficient > 0.5f) this.wce$setClimbTick(this.wce$getClimbTick() + climbTickToAdd);

                    player.getFoodData().addExhaustion(exhaustionToAdd);

                    if (wce$exhaustionLevel > 80) {
                        player.displayClientMessage(
                                Component.translatable("generics.paws_feel_tired")
                                        .withStyle(ChatFormatting.RED)
                                        .withStyle(ChatFormatting.ITALIC)
                                ,true);
                        this.wce$isTired = true;
                        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2, false, false));
                    }

                } else {
                    if (wce$exhaustionLevel > 0) {
                        if ((wce$exhaustionLevel < 15) && this.wce$isTired) {
                            player.displayClientMessage(
                                    Component.translatable("generic.strenght_returns")
                                            .withStyle(ChatFormatting.GREEN)
                                            .withStyle(ChatFormatting.ITALIC)
                                    ,true);

                            this.wce$isTired = false;
                        }
                        wce$exhaustionLevel -= 3;
                        wce$exhaustionLevel = Math.max(0, wce$exhaustionLevel);
                    }
                }

                if (sample != wce$exhaustionLevel) {
                    ModPackets.sendToPlayer(new SyncExhaustionPacket(wce$exhaustionLevel), player);
                }
            }
        }
    }

    @Unique
    private float wce$getExhaustionCoefficientToOne(int exhaustionLevel) {
        return (float) Math.pow(((double) (exhaustionLevel) / MAX_EXHAUSTION_LEVEL), 3);
    }


    @Unique
    private double wce$oldY = 0;

    @Unique
    private void wce$manageClimbTick(Player player) {
        AABB box = player.getBoundingBox();
        Level level = player.level();

        double yMin = box.minY + 0.1;
        double yMax = box.maxY - 0.1;

        double offset = 0.1;

        boolean touchingWall = false;

        Vec3 wallNormal = Vec3.ZERO;

        BlockState chosenBlock = Blocks.AIR.defaultBlockState();

        for (double y = yMin; y <= yMax; y += 0.5) {
            BlockPos pos = BlockPos.containing(box.maxX + offset, y, player.getZ());
            BlockState state = level.getBlockState(pos);
            if (wce$isValidWall(state, level, pos)) {
                chosenBlock = state;
                touchingWall = true;
                wallNormal = new Vec3(-1, 0, 0);
                break;
            }
        }
        for (double y = yMin; y <= yMax && !touchingWall; y += 0.5) {
            BlockPos pos = BlockPos.containing(box.minX - offset, y, player.getZ());
            BlockState state = level.getBlockState(pos);
            if (wce$isValidWall(state, level, pos)) {
                chosenBlock = state;
                touchingWall = true;
                wallNormal = new Vec3(1, 0, 0);
                break;
            }
        }
        for (double y = yMin; y <= yMax && !touchingWall; y += 0.5) {
            BlockPos pos = BlockPos.containing(player.getX(), y, box.maxZ + offset);
            BlockState state = level.getBlockState(pos);
            if (wce$isValidWall(state, level, pos)) {
                chosenBlock = state;
                touchingWall = true;
                wallNormal = new Vec3(0, 0, -1);
                break;
            }
        }
        for (double y = yMin; y <= yMax && !touchingWall; y += 0.5) {
            BlockPos pos = BlockPos.containing(player.getX(), y, box.minZ - offset);
            BlockState state = level.getBlockState(pos);
            if (wce$isValidWall(state, level, pos)) {
                chosenBlock = state;
                touchingWall = true;
                wallNormal = new Vec3(0, 0, 1);
                break;
            }
        }

        if (wallNormal != Vec3.ZERO) {
            double yaw = Math.toDegrees(Math.atan2(wallNormal.x, -wallNormal.z));
            player.yBodyRot = (float) yaw;

            float body = player.yBodyRot;
            float head = player.yHeadRot;
            float diff = Mth.wrapDegrees(head - body);
            diff = Mth.clamp(diff, -60.0F, 60.0F);
            player.yHeadRot = body + diff;
        }

        double current = player.getY() - this.wce$oldY;
        boolean goingUp = current > 0.01;
        this.wce$oldY = player.getY();

        if (!level.isClientSide()) {
            boolean isCat = PlayerShape.getCurrentShape(player) instanceof WCatEntity;
            if (touchingWall && isCat) {
                this.wce$setClimbing(true);

                if (this.wce$getGraceTimer() > 0) this.wce$setGraceTimer(this.wce$getGraceTimer() - 1);

                boolean successClimb = !player.onGround();
                if (successClimb) this.wce$setGraceTimer(3);
                else if (wce$getGraceTimer() <= 0) wce$stopClimb();

                if (!player.getAbilities().instabuild) player.getFoodData().addExhaustion(0.015f);

                if (goingUp){
                    if (!player.getAbilities().instabuild) player.getFoodData().addExhaustion(0.020f);
                }


            } else if (this.wce$getGraceTimer() > 0) {
                this.wce$setGraceTimer(this.wce$getGraceTimer() - 1);
                this.wce$setClimbing(true);
            } else {
                this.wce$setClimbing(false);
            }

            if (this.wce$isClimbing()) this.wce$setClimbTick(this.wce$getClimbTick() + 1);
            if (this.wce$isClimbing() && ClimbingConstants.isHardSurface(chosenBlock)) this.wce$setClimbTick(this.wce$getClimbTick() + 1);

            if (this.wce$getClimbTick() > MAX_CLIMB_TIME) this.wce$stopClimb();

        } else {

            if (level.getGameTime() % 10 == 0){
                Vec3 pos = player.position();
                level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, chosenBlock)
                        ,pos.x, pos.y + 0.3, pos.z, 0.1, 0.1, 0.1);
                level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, chosenBlock)
                        ,pos.x, pos.y - 0.2, pos.z, 0.1, 0.1, 0.1);

                WCEClient.playLocalSound(ModSounds.SCRAPING_WOOD.get(), SoundSource.PLAYERS, 0.1f, 0.5f);
                if (goingUp) WCEClient.playLocalSound(chosenBlock.getSoundType().getStepSound(),
                        SoundSource.PLAYERS, 0.3f, 1.2f, pos);
            }
        }
    }

    @Unique
    private int wce$sprintTime = 0;
    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {

        LivingEntity self = (LivingEntity) (Object) this;
        if (!self.level().isClientSide()) {
            if (self.isSprinting() && self.onGround()) wce$sprintTime++;
            else if (wce$sprintTime > 1) wce$sprintTime -= 2;
        }


        this.wce$climbManager();
        this.wce$exhaustionManager();
    }

    @Inject(method = "aiStep", at = @At("TAIL"))
    public void aiStep(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self instanceof Player player && this.wce$isClimbing()) {
            float body = player.yBodyRot;
            float head = player.yHeadRot;
            float diff = Mth.wrapDegrees(head - body);
            diff = Mth.clamp(diff, -60.0F, 60.0F);
            player.yHeadRot = body + diff;
        }
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    public void defineClimbingData(SynchedEntityData.Builder builder, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        builder.define(wce$CLIMBING, false);
        builder.define(wce$GRACE_TIMER, 0);
        builder.define(wce$CLIMBING_TICKS, 0);

    }

    @Inject(method = "tickHeadTurn", at = @At("HEAD"), cancellable = true)
    public void cancelTurn(float pYRot, float pAnimStep, CallbackInfoReturnable<Float> cir) {
        if (this.wce$isClimbing()) {
            cir.cancel();
            cir.setReturnValue(pAnimStep);
        }
    }

    @Unique
    private boolean wce$isValidWall(BlockState state, Level level, BlockPos pos) {
        return state.isCollisionShapeFullBlock(level, pos)
                && ClimbingConstants.isValidClimbable(state);
    }










    @Override
    public boolean wce$isClimbing() {
        LivingEntity self = (LivingEntity) (Object) this;
        return self.getEntityData().get(wce$CLIMBING);
    }

    @Override
    public void wce$setClimbing(boolean value) {
        LivingEntity self = (LivingEntity) (Object) this;
        self.getEntityData().set(wce$CLIMBING, value);
    }

    @Override
    public int wce$getClimbTick() {
        LivingEntity self = (LivingEntity) (Object) this;
        return self.getEntityData().get(wce$CLIMBING_TICKS);
    }

    @Override
    public void wce$setClimbTick(int value) {
        LivingEntity self = (LivingEntity) (Object) this;
        self.getEntityData().set(wce$CLIMBING_TICKS, value);
    }

    @Override
    public int wce$getGraceTimer() {
        LivingEntity self = (LivingEntity) (Object) this;
        return self.getEntityData().get(wce$GRACE_TIMER);
    }

    @Override
    public void wce$setGraceTimer(int value) {
        LivingEntity self = (LivingEntity) (Object) this;
        self.getEntityData().set(wce$GRACE_TIMER, value);
    }

    @Override
    public void wce$startClimb() {
        this.wce$setClimbing(true);
        this.wce$setGraceTimer(15);
        this.wce$setClimbTick(0);
    }

    @Override
    public void wce$stopClimb() {
        this.wce$setClimbing(false);
        this.wce$setGraceTimer(0);
        this.wce$setClimbTick(0);
    }

    @Override
    public int wce$getExhaustion() {
        return wce$exhaustionLevel;
    }

    @Override
    public int wce$getSprintTime() {
        return wce$sprintTime;
    }

}
