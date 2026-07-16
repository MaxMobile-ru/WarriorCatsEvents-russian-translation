package net.snowteb.warriorcats_events.entity.custom;

import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.snowteb.warriorcats_events.entity.ModEntities;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.item.ModItems;
import net.snowteb.warriorcats_events.item.custom.MossBallItem;

/**
 * Thanks to jmilthedude for allowing the use of their Bouncy Balls code for the making of this entity.
 * This piece of code is a modified and adapted copy of their BouncyBallEntity.java
 * This piece of code is under the same license as the original code, MIT License.
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2024
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

public class MossBallEntity extends ThrowableItemProjectile {

    private static final EntityDataAccessor<Float> BOUNCE =
            SynchedEntityData.defineId(MossBallEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> LIFE =
            SynchedEntityData.defineId(MossBallEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<ItemStack> STACK =
            SynchedEntityData.defineId(MossBallEntity.class, EntityDataSerializers.ITEM_STACK);

    private static final EntityDataAccessor<Integer> WATER =
            SynchedEntityData.defineId(MossBallEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> HONEY =
            SynchedEntityData.defineId(MossBallEntity.class, EntityDataSerializers.INT);

    public MossBallEntity(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    public MossBallEntity(Level level, LivingEntity owner) {
        super(ModEntities.MOSS_BALL.get(), owner, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.MOSS_BALL.get();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(BOUNCE, 1f);
        builder.define(LIFE, 0);
        builder.define(STACK, new ItemStack(ModItems.MOSS_BALL.get()));
        builder.define(WATER, 0);
        builder.define(HONEY, 0);
    }

    public int getWater() {
        return this.entityData.get(WATER);
    }

    public void setWater(int water) {
        this.entityData.set(WATER, water);
    }

    public int getHoney() {
        return this.entityData.get(HONEY);
    }

    public void setHoney(int honey) {
        this.entityData.set(HONEY, honey);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            this.handleLife();
        } else {
            if (this.tickCount % (10 - (this.getWater() / 2)) == 0 && this.getWater() > 0) {

                this.level().addParticle(ParticleTypes.FALLING_DRIPSTONE_WATER,
                        this.position().x, this.position().y + 0.2, this.position().z,
                        0.0D, 0.0D, 0.0D);


            }

            if (this.tickCount % (10 - (this.getHoney() / 2)) == 0 && this.getHoney() > 0) {

                this.level().addParticle(ParticleTypes.FALLING_HONEY,
                        this.position().x, this.position().y + 0.1, this.position().z,
                        0.0D, 0.0D, 0.0D);

            }
        }
    }

    private void handleLife() {
        this.setLife(this.getLife() + 1);
        if (this.getLife() >= 6000) {
            this.spawnAtLocation(this.getItem());
            this.discard();
        }
        if (this.isInLava()) {
            ServerLevel world = (ServerLevel) this.level();
            world.playSound(this, this.blockPosition(),
                    SoundEvents.FIRE_EXTINGUISH, SoundSource.NEUTRAL,
                    0.5f, 1 + world.getRandom().nextFloat() * 0.4F + 0.8F
            );
            this.discard();
        } else if (this.isInWater()) {
            if (this.level().getRandom().nextInt(70) == 0) {
                if (this.getWater() < 10) this.setWater(this.getWater() + 1);
            }
            this.setDeltaMovement(this.getDeltaMovement().add(new Vec3(0,0.036,0)));
            this.setLife(this.getLife() + 6);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        Entity entity = pResult.getEntity();

        if (entity instanceof Player || entity instanceof MossBallEntity) return;
        this.setDeltaMovement(Vec3.ZERO);
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        if (this.isInFluidType() && pResult.getDirection() == Direction.UP) {
            this.setDeltaMovement(Vec3.ZERO);
            return;
        }

        this.bounce(pResult);
    }

    private void bounce(BlockHitResult blockHitResult) {
        Vec3 velocity = this.getDeltaMovement();
        Vec3i hitVector = blockHitResult.getDirection().getNormal();
        Vec3 normal = new Vec3(hitVector.getX(), hitVector.getY(), hitVector.getZ());

        double dotProduct = velocity.dot(normal);

        Vec3 reflectionVector = new Vec3(velocity.x - 2 * dotProduct * normal.x, velocity.y - 2 * dotProduct * normal.y, velocity.z - 2 * dotProduct * normal.z);

        double newSpeed = this.getAndDecrementBounce(this.getCoefficient());

        reflectionVector = reflectionVector.multiply(newSpeed, newSpeed, newSpeed);

        this.setDeltaMovement(reflectionVector);
        if (!this.level().isClientSide() && !this.isInFluidType()) {
            ServerLevel level = (ServerLevel) this.level();
            level.playSound(this, this.blockPosition(),
                    SoundEvents.SLIME_JUMP_SMALL, SoundSource.NEUTRAL,
                    this.getBounce() * .7f, this.getBounce() * 1.5f
            );
        }
    }

    private void setBounce(float bounce) {
        this.entityData.set(BOUNCE, bounce);
    }

    private float getBounce() {
        return this.entityData.get(BOUNCE);
    }

    private float getAndDecrementBounce(float dampening) {
        this.entityData.set(BOUNCE, this.getBounce() * dampening);
        return this.getBounce();
    }

    private float getCoefficient() {

        float waterFactor = Mth.clamp(this.getWater() / 10f, 0f, 1f);

        float constant = 0.55f;
        if (this.getHoney() > 0) constant = 0.85f;
        return Math.max(constant * (1 - waterFactor), 0.2f);
    }

    public int getLife() {
        return this.entityData.get(LIFE);
    }

    public void setLife(int life) {
        this.entityData.set(LIFE, life);
    }

    @Override
    public ItemStack getItem() {
        return this.entityData.get(STACK);
    }

    public void setStack(ItemStack stack) {
        this.entityData.set(STACK, stack);
        this.setItem(stack);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("Bounce", this.getBounce());
        compound.putInt("Life", this.getLife());
        compound.put("Stack", this.getItem().save(this.registryAccess(), new CompoundTag()));
        compound.putInt("Water", this.getWater());
        compound.putInt("Honey", this.getHoney());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        if (pCompound.contains("Bounce")) {
            this.setBounce(pCompound.getFloat("Bounce"));
        }

        if (pCompound.contains("Life")) {
            this.setLife(pCompound.getInt("Life"));
        }

        if (pCompound.contains("Stack")) {
            this.setStack(ItemStack.parseOptional(this.registryAccess(),pCompound.getCompound("Stack")));
        } else {
            this.setStack(new ItemStack(ModItems.MOSS_BALL.get()));
        }

        if (pCompound.contains("Water")) {
            this.setWater(pCompound.getInt("Water"));
        }

        if (pCompound.contains("Honey")) {
            this.setHoney(pCompound.getInt("Honey"));
        }
    }

    @Override
    public InteractionResult interact(Player pPlayer, InteractionHand pHand) {

        if ((this.level() instanceof ServerLevel sLevel)) {
            if (pHand == InteractionHand.MAIN_HAND) {
                if (!pPlayer.isUsingItem() && this.getLife() > 10){
                    ItemStack stack = this.getItem();

                    MossBallItem.setWaterLevel(stack, this.getWater());
                    MossBallItem.setHoneyLevel(stack, this.getHoney());

                    if (!pPlayer.addItem(stack)) {
                        pPlayer.drop(stack, false);
                    }

                    this.discard();

                    sLevel.playSound(null, pPlayer.blockPosition(), SoundEvents.ITEM_PICKUP,
                            SoundSource.PLAYERS, 0.4f, 1.8f);

                    return InteractionResult.SUCCESS;
                }
            }
        }

        return super.interact(pPlayer, pHand);
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (!this.level().isClientSide()) {
            Entity entity = pSource.getEntity();

            if (entity instanceof LivingEntity livingEntity) {
                Vec3 direction = this.position().subtract(livingEntity.position()).normalize();

                double strength = 0.7;
                if (livingEntity instanceof WCatEntity cat) {
                    float chance = cat.getRandom().nextFloat() * 0.4f;
                    strength = 0.1 + chance;
                }

                this.setDeltaMovement(
                        direction.x * strength,
                        direction.y * 0.6 + 0.2,
                        direction.z * strength
                );

                this.setBounce(1.0f);

                this.setLife(Mth.clamp(this.getLife() - 100, 0, this.getLife()));

                if (!this.level().isClientSide() && !this.isInFluidType()) {
                    ServerLevel level = (ServerLevel) this.level();
                    level.playSound(this, this.blockPosition(),
                            SoundEvents.SLIME_JUMP_SMALL, SoundSource.NEUTRAL,
                            this.getBounce() * .4f, this.getBounce() * 1.3f
                    );
                }
            }
        }

        return super.hurt(pSource, pAmount);
    }

    public void kickBall(LivingEntity livingEntity) {
        if (!this.level().isClientSide()) {
            {
                Vec3 direction = this.position().subtract(livingEntity.position()).normalize();

                double strength = 0.5;
                if (livingEntity instanceof WCatEntity cat) {
                    float chance = cat.getRandom().nextFloat() * 0.3f;
                    strength = 0.1 + chance;
                }

                this.setDeltaMovement(
                        direction.x * strength,
                        direction.y * 0.6 + 0.2,
                        direction.z * strength
                );

                this.setBounce(1.0f);

                this.setLife(Mth.clamp(this.getLife() - 100, 0, this.getLife()));
                this.markHurt();

                if (!this.level().isClientSide() && !this.isInFluidType()) {
                    ServerLevel level = (ServerLevel) this.level();
                    level.playSound(this, this.blockPosition(),
                            SoundEvents.SLIME_JUMP_SMALL, SoundSource.NEUTRAL,
                            this.getBounce() * .4f, this.getBounce() * 1.3f
                    );
                }
            }
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        super.onSyncedDataUpdated(pKey);
    }
}