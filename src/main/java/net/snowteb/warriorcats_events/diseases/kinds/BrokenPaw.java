package net.snowteb.warriorcats_events.diseases.kinds;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.snowteb.warriorcats_events.diseases.Disease;
import net.snowteb.warriorcats_events.diseases.DiseaseTypes;
import net.snowteb.warriorcats_events.diseases.Diseaseable;
import net.snowteb.warriorcats_events.effect.ModEffects;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.item.ModItems;
import net.snowteb.warriorcats_events.sound.ModSounds;
import tocraft.walkers.api.PlayerShape;

public class BrokenPaw extends Disease<BrokenPaw> {

    private int painLevel;
    private Vec3 lastPos;

    public int maxPainLevel = 4;
    public boolean boneWrapped = false;

    public void setPainLevel(int painLevel) {
        this.painLevel = Mth.clamp(painLevel, 0, maxPainLevel);
    }

    public void boneWrappedTick(Diseaseable<?> diseaseable) {
        if (diseaseable.getEntity().level().isClientSide()) return;
        if (!boneWrapped) return;
        if (diseaseable.getEntity().tickCount % 100 != 0) return;
        if (!canHeal()) return;
        this.heal(diseaseable.getEntity());
    }

    public boolean applyComfreyPoultice(Diseaseable<?> diseaseable) {
        if (!boneWrapped) return false;
        if (this.getHealingCooldown() > 20) {
            this.setHealingCooldown(this.getHealingCooldown() - 20);
            diseaseable.onChange();
            return true;
        }
        return false;
    }

    public void setWrapBone(boolean wrapBone, Diseaseable<?> diseaseable) {
        if (wrapBone == boneWrapped) return;
        if (wrapBone) this.setHealingCooldown(getType().getMaxTimeSeconds() / (getType().getMaxHealLevel()*2));
        boneWrapped = wrapBone;
        diseaseable.onChange();
    }

    public boolean isBoneWrapped() {
        return boneWrapped;
    }

    public void onRemoveOrAddWrap(LivingEntity entity, ServerLevel sLevel) {
        sLevel.playSound(null, entity.blockPosition(),
                SoundEvents.ARMOR_EQUIP_ELYTRA.value(), SoundSource.NEUTRAL, 1.0F, 1.0F);

        sLevel.playSound(null, entity.blockPosition(),
                SoundEvents.BONE_MEAL_USE, SoundSource.NEUTRAL, 1.0F, 1.0F);

        ItemStack stack = new ItemStack(ModItems.LEG_WRAP.get());

        sLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, stack.copy()),
                entity.getX(), entity.getY(), entity.getZ(), 5,
                0.2, 0.2, 0.2, 0.2);
    }

    public int getPainLevel() {
        if (!isBoneWrapped()) return Math.max(3, painLevel);
        return painLevel;
    }

    public void setLastPos(Vec3 lastPos) {
        this.lastPos = lastPos;
    }

    public boolean isEntityMoving(Vec3 pos) {
        if (lastPos == null) return false;

        return lastPos.distanceToSqr(pos) > 0.0001;
    }

    @Override
    public boolean heal(LivingEntity entity) {
        if (!canHeal()) return false;

        if (entity.level() instanceof ServerLevel sLevel) {
            sLevel.sendParticles(
                    ParticleTypes.HAPPY_VILLAGER,
                    entity.getX(), entity.getY(), entity.getZ(),
                    5, 0.2, 0.2, 0.2, 0.02
            );
        }

        this.setHealedLevel(this.getHealedLevel() + 1);
        maxPainLevel = Math.max(maxPainLevel-1, 0);
        this.setHealingCooldown((int) (getType().getMaxTimeSeconds() / (getType().getMaxHealLevel()*1.5)));
        if (entity instanceof Diseaseable<?> diseaseable) {
            diseaseable.onChange();
        }

        if (isHealed() && entity instanceof ServerPlayer player) {
            player.sendSystemMessage(
                    Component.translatable("managers.disease_no_longer_has_a", getType().getName().getString())
                            .withStyle(ChatFormatting.GREEN)
            );
        }

        return true;
    }

    public void hurt(LivingEntity entity) {
        if (getHealedLevel() >= getType().getMaxHealLevel() - 1) return;
        if (entity.getHealth() > 6) {
            float amount;
            if (entity instanceof Player) {
                amount = 2f + getPainLevel();
            } else {
                amount = 1f + getPainLevel();
            }

            if (amount < entity.getHealth()-7) entity.hurt(entity.damageSources().fall(), amount);

        }

        setPainLevel(getPainLevel() + 1);
    }

    @Override
    public void saveNBT(CompoundTag diseaseTag) {
        super.saveNBT(diseaseTag);
        diseaseTag.putInt("painLevel", painLevel);
        diseaseTag.putBoolean("boneWrapped", boneWrapped);
    }

    @Override
    public void loadNBT(CompoundTag diseaseTag) {
        super.loadNBT(diseaseTag);
        this.painLevel = diseaseTag.getInt("painLevel");
        this.boneWrapped = diseaseTag.getBoolean("boneWrapped");
    }

    @Override
    public <T extends LivingEntity> void onAdd(Diseaseable<T> tDiseaseable, boolean organic) {
        LivingEntity entity = tDiseaseable.getEntity();
        if (entity.level().isClientSide()) return;

        entity.addEffect(new MobEffectInstance(ModEffects.NUMB_EFFECT, 100, 0, false, false));
        entity.level().playSound(null, entity.blockPosition(),
                ModSounds.WILDCAT_SCREAM.get(), SoundSource.PLAYERS,
                1f, 1f);

        if (entity instanceof ServerPlayer player) {
            player.displayClientMessage(Component.translatable("managers.disease_broken_bone").withStyle(ChatFormatting.RED),
                    false);
            if (PlayerShape.getCurrentShape(player) instanceof WCatEntity cat) {
                cat.setAnimIndex(10);
                player.getPersistentData().putInt("wcat_animation_playing", player.server.getTickCount() + 10);
                PlayerShape.updateShapes(player, cat);
                this.setPainLevel(maxPainLevel);
            }
        }

        if (organic) {
            tDiseaseable.addDisease(DiseaseTypes.CHILLS, true);
        }

    }

    @Override
    public <T extends LivingEntity> void onRemove(Diseaseable<T> tDiseaseable) {
        super.onRemove(tDiseaseable);
        if (this.isBoneWrapped()) {
            if (tDiseaseable.getEntity().level() instanceof ServerLevel sLevel) {
                LivingEntity entity = tDiseaseable.getEntity();
                this.onRemoveOrAddWrap(entity, sLevel);
            }
        }
    }

    @Override
    public boolean allowClimb(Diseaseable<?> tDiseaseable) {
        if (tDiseaseable.getEntity() instanceof Player player) {
            player.displayClientMessage(Component.translatable("managers.disease_cant_climb_broken_paw")
                    .withStyle(ChatFormatting.YELLOW), true);
            return false;
        }
        return super.allowClimb(tDiseaseable);
    }

    @Override
    public boolean allowLeap(Diseaseable<?> tDiseaseable) {
        if (tDiseaseable.getEntity() instanceof Player player) {
            player.displayClientMessage(Component.translatable("managers.disease_cant_leap_broken_paw")
                    .withStyle(ChatFormatting.YELLOW), true);
            return false;
        }
        return super.allowLeap(tDiseaseable);
    }

    @Override
    protected void defaultTickEvent(Diseaseable<?> diseaseable, boolean shouldUpdate) {
        super.defaultTickEvent(diseaseable, shouldUpdate);
        this.boneWrappedTick(diseaseable);
    }

    @Override
    public boolean shouldShowUnhealed() {
        return canHeal() && !isBoneWrapped();
    }

    @Override
    public <T extends LivingEntity> boolean allowReducedFallDamage(Diseaseable<T> tDiseaseable) {
        return false;
    }
}
