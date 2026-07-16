package net.snowteb.warriorcats_events.network.packet.c2s.skilltree;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.clan.WCEPlayerData;
import net.snowteb.warriorcats_events.clan.WCEPlayerDataProvider;
import net.snowteb.warriorcats_events.diseases.Diseaseable;
import net.snowteb.warriorcats_events.skills.ISkillData;
import net.snowteb.warriorcats_events.skills.PlayerSkillProvider;
import net.snowteb.warriorcats_events.sound.ModSounds;
import net.snowteb.warriorcats_events.zconfig.WCEServerConfig;

import java.util.function.Supplier;

public class CtSPerformLeapPacket {

    private final int power;

    public CtSPerformLeapPacket(int power) {
        this.power = power;
    }

    public static CtSPerformLeapPacket decode(FriendlyByteBuf buf) {
        int variant = buf.readInt();
        return new CtSPerformLeapPacket(variant);
    }

    public static void encode(CtSPerformLeapPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.power);
    }

    public static void handle(CtSPerformLeapPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            if (!WCEServerConfig.SERVER.LEAP_SERVER.get()) {
                player.displayClientMessage(Component.translatable("generic.leap_disabled").withStyle(ChatFormatting.RED), true);
                return;
            }

            if (player instanceof Diseaseable<?> diseaseable) {
                if (!diseaseable.canLeap()) {
                    return;
                }
            }

            float leapPower = (float) packet.power /100;

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
            double strength = (0.8D + (leapPower * 0.45D)*leapPowerMultiplier)*finalMultiplier;
            Vec3 velocity = look.scale(strength);
            player.level().playSound(null, player.blockPosition(), ModSounds.SHORT_WOOSH.get(),  SoundSource.PLAYERS, 0.7F, 4.8F);
            player.level().playSound(null, player.blockPosition(), SoundEvents.GRASS_STEP,  SoundSource.PLAYERS, 1.0F, 1.0F);

            BlockParticleOption particle = new BlockParticleOption(ParticleTypes.BLOCK,
                    player.level().getBlockState(player.blockPosition().below()));
            ((ServerLevel) player.level()).sendParticles(particle,
                    player.getX(), player.getY(), player.getZ(),
                    30,
                    0.3,0.2,0.3, 0.3);
            ((ServerLevel) player.level()).sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    player.getX(), player.getY(), player.getZ(),
                    5,
                    0.1,0.1,0.1, 0.1);

            ((ServerLevel) player.level()).sendParticles(ParticleTypes.SWEEP_ATTACK,
                    player.getX(), player.getY(), player.getZ(),
                    1,
                    0.1,0.1,0.1, 0.1);

            player.setDeltaMovement(
                    velocity.x,
                    Math.max(0.35D, velocity.y + 0.15D),
                    velocity.z
            );

            player.getCapability(PlayerSkillProvider.SKILL_DATA).ifPresent(cap -> {
               cap.setLeaping(true);
               cap.setLeapPower(packet.power);
            });

            player.hurtMarked = true;
        });

        ctx.get().setPacketHandled(true);
    }
}
