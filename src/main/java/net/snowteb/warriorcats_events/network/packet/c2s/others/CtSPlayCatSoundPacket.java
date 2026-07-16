package net.snowteb.warriorcats_events.network.packet.c2s.others;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.attachments.ModAttachments;
import net.snowteb.warriorcats_events.attachments.WCEPlayerData;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.sound.ModSounds;
import tocraft.walkers.api.PlayerShape;

public class CtSPlayCatSoundPacket implements CustomPacketPayload {
    private final int soundIndex;

    public CtSPlayCatSoundPacket(int soundIndex) {
        this.soundIndex = soundIndex;
    }

    public CtSPlayCatSoundPacket(FriendlyByteBuf buf) {
        this.soundIndex = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.soundIndex);
    }

    public boolean handle(IPayloadContext ctx) {

        ctx.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) ctx.player();

            ServerLevel level = player.serverLevel().getLevel();

            if (!(PlayerShape.getCurrentShape(player) instanceof WCatEntity)) return;

            SoundEvent sound;
            switch (soundIndex) {
                case 1 -> sound = SoundEvents.CAT_HISS;
                case 2 -> sound = SoundEvents.CAT_AMBIENT;
                case 3 -> sound = SoundEvents.CAT_PURR;
                case 4 -> sound = SoundEvents.CAT_PURREOW;
                case 5 -> sound = SoundEvents.CAT_STRAY_AMBIENT;
                case 6 -> sound = SoundEvents.CAT_HURT;
                case 7 -> sound = ModSounds.WILDCAT_SCREAM.get();
                default -> sound = null;
            }

            WCEPlayerData.Age morphAge = player.getData(ModAttachments.PLAYER_WCE_DATA).getMorphAge();

            float pitch = switch (morphAge) {
                case APPRENTICE ->  1.2F;
                case KIT -> 1.4f;
                case ADULT -> 1f;
            };

            if (sound != null) {
                level.playSound(null, player.blockPosition(), sound,
                        SoundSource.PLAYERS, 0.7f, pitch);
            }

            SimpleParticleType type;

            switch (soundIndex) {
                case 1 -> type = ParticleTypes.SMOKE;
                case 3 -> type = ParticleTypes.HEART;
                case 4 -> type = ParticleTypes.HEART;

                default -> type = null;
            }

            int count = switch (soundIndex) {
                case 1 -> 10;
                case 3 -> 10;
                case 4 -> 5;

                default -> 0;
            };

            if (type != null) {
                level.sendParticles(
                        type, player.getX(), player.getY() + 0.5, player.getZ(),
                        count, 0.4, 0.2, 0.4,0.005);
            }

        });
        return true;
    }

    public static final Type<CtSPlayCatSoundPacket> TYPE
            = new Type<>(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "play_cat_sound"));

    public static final StreamCodec<FriendlyByteBuf, CtSPlayCatSoundPacket> CODEC =
            StreamCodec.of(
                    (buf, pkt) -> pkt.toBytes(buf),
                    buf -> new CtSPlayCatSoundPacket(buf)
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
