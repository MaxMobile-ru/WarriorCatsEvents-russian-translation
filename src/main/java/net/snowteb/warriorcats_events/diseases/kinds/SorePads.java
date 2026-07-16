package net.snowteb.warriorcats_events.diseases.kinds;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.snowteb.warriorcats_events.clan.WCEPlayerData;
import net.snowteb.warriorcats_events.clan.WCEPlayerDataProvider;
import net.snowteb.warriorcats_events.diseases.Disease;
import net.snowteb.warriorcats_events.diseases.Diseaseable;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.skills.ISkillData;
import net.snowteb.warriorcats_events.skills.PlayerSkillProvider;
import tocraft.walkers.api.PlayerShape;

public class SorePads extends Disease<SorePads> {

    private Vec3 lastPos;

    public void setLastPos(Vec3 lastPos) {
        this.lastPos = lastPos;
    }

    public boolean isEntityMoving(Vec3 pos) {
        if (lastPos == null) return false;

        return lastPos.distanceToSqr(pos) > 0.0001;
    }

    @Override
    public <T extends LivingEntity> void onAdd(Diseaseable<T> tDiseaseable, boolean organic) {
        if (tDiseaseable.getEntity() instanceof Player player && PlayerShape.getCurrentShape(player) instanceof WCatEntity) {
            player.displayClientMessage(
                    Component.translatable("managers.disease_paw_pads_hurt")
                            .withStyle(ChatFormatting.GRAY)
                            .withStyle(ChatFormatting.ITALIC),
                    true);
        }
        super.onAdd(tDiseaseable, organic);
    }

    @Override
    public boolean allowClimb(Diseaseable<?> tDiseaseable) {
        if (this.getLevel() > 1) {
            if (tDiseaseable.getEntity() instanceof Player player && player.getRandom().nextFloat() < 0.3*(this.getLevel() - 1)) {
                player.displayClientMessage(Component.translatable("managers.disease_cant_climb_sore_pads")
                        .withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC), true);
                return false;
            }
        }
        return super.allowClimb(tDiseaseable);
    }

    @Override
    public boolean allowLeap(Diseaseable<?> tDiseaseable) {
        if (this.getLevel() > 1) {
            LivingEntity livingEntity = tDiseaseable.getEntity();
            if (livingEntity.getRandom().nextFloat() < 0.2*(this.getLevel() - 1) && livingEntity instanceof Player player) {
                player.displayClientMessage(Component.translatable("managers.disease_cant_leap_sore_pads")
                        .withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC), true);

                float leapPower = (float) 10 /100;

                int jumpSkillLeapPower = player.getCapability(PlayerSkillProvider.SKILL_DATA)
                        .map(ISkillData::getJumpLevel).orElse(0);
                WCEPlayerData.Age morphAge = player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                        .map(WCEPlayerData::getMorphAge).orElse(WCEPlayerData.Age.ADULT);

                float finalMultiplier = switch (morphAge) {
                    case KIT -> 0.33f;
                    case APPRENTICE -> 0.67f;
                    case ADULT -> 1.0f;
                };

                float leapPowerMultiplier = (float) (0.10 + (jumpSkillLeapPower*0.30));

                Vec3 look = player.getLookAngle().normalize();
                double strength = (0.2D + (leapPower * 0.45D)*leapPowerMultiplier)*finalMultiplier;
                Vec3 velocity = look.scale(strength);
                player.level().playSound(null, player.blockPosition(), SoundEvents.GRASS_STEP,  SoundSource.PLAYERS, 1.0F, 1.0F);

                BlockParticleOption particle = new BlockParticleOption(ParticleTypes.BLOCK,
                        player.level().getBlockState(player.blockPosition().below()));
                ((ServerLevel) player.level()).sendParticles(particle,
                        player.getX(), player.getY(), player.getZ(),
                        30,
                        0.3,0.2,0.3, 0.3);

                ((ServerLevel) player.level()).sendParticles(ParticleTypes.SMOKE,
                        player.getX(), player.getY(), player.getZ(),
                        10,
                        0.1,0.1,0.1, 0.1);

                player.setDeltaMovement(
                        velocity.x,
                        Math.max(0.35D, velocity.y + 0.15D),
                        velocity.z
                );

                player.hurt(player.damageSources().fall(), this.getLevel() - 1);

                return false;
            }
        }
        return super.allowLeap(tDiseaseable);
    }

    @Override
    public <T extends LivingEntity> boolean allowReducedFallDamage(Diseaseable<T> tDiseaseable) {
        return false;
    }

}
