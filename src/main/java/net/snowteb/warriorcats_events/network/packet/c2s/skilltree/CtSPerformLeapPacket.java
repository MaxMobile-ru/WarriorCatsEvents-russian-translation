package net.snowteb.warriorcats_events.network.packet.c2s.skilltree;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.attachments.CapabilityManager;
import net.snowteb.warriorcats_events.attachments.ModAttachments;
import net.snowteb.warriorcats_events.attachments.WCEPlayerData;
import net.snowteb.warriorcats_events.diseases.Diseaseable;
import net.snowteb.warriorcats_events.sound.ModSounds;
import net.snowteb.warriorcats_events.zconfig.WCEServerConfig;

public class CtSPerformLeapPacket implements CustomPacketPayload {

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

    public static void handle(CtSPerformLeapPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) ctx.player();

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

            int jumpSkillLeapPower = player.getData(ModAttachments.PLAYER_SKILL).getJumpLevel();
            WCEPlayerData.Age morphAge = player.getData(ModAttachments.PLAYER_WCE_DATA).getMorphAge();

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

            CapabilityManager.attachmentProvider(player, ModAttachments.PLAYER_SKILL, cap -> {
                cap.setLeaping(true);
                cap.setLeapPower(packet.power);
            });

            player.hurtMarked = true;
        });

    }

    public static final Type<CtSPerformLeapPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "perform_leap"));

    public static final StreamCodec<FriendlyByteBuf, CtSPerformLeapPacket> CODEC =
            StreamCodec.of(
                    (buf, pkt) -> encode(pkt, buf),
                    buf -> decode(buf)
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
