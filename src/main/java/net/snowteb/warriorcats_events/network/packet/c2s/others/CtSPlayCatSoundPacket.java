package net.snowteb.warriorcats_events.network.packet.c2s.others;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.clan.WCEPlayerData;
import net.snowteb.warriorcats_events.clan.WCEPlayerDataProvider;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.sound.ModSounds;
import tocraft.walkers.api.PlayerShape;

import java.util.function.Supplier;

public class CtSPlayCatSoundPacket {
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

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {

            ServerPlayer player = context.getSender();
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

            WCEPlayerData.Age morphAge = player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                    .map(WCEPlayerData::getMorphAge).orElse(WCEPlayerData.Age.ADULT);

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
}
