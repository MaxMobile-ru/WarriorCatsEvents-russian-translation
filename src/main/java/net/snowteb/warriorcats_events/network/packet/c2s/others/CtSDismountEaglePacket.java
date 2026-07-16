package net.snowteb.warriorcats_events.network.packet.c2s.others;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.effect.ModEffects;

import java.util.function.Supplier;

public class CtSDismountEaglePacket {

    public CtSDismountEaglePacket() {
    }

    public CtSDismountEaglePacket(FriendlyByteBuf buf) {
    }

    public void toBytes(FriendlyByteBuf buf) {
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {

            ServerPlayer player = context.getSender();
            ServerLevel level = player.serverLevel().getLevel();

            if (player.getVehicle() != null) {
                level.playSound(null, player.blockPosition(), SoundEvents.CAT_HISS, SoundSource.PLAYERS, 0.7f ,1.0f);

                player.stopRiding();

                level.sendParticles(ParticleTypes.CLOUD,
                        player.getX(), player.getY(), player.getZ(),
                15,0,0,0,0.5);

                player.addEffect(new MobEffectInstance(ModEffects.EAGLE_ESCAPIST.get(), 100));
            }

        });
        return true;
    }
}
