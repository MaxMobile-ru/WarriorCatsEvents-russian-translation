package net.snowteb.warriorcats_events.network.packet.c2s.others;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.clan.WCEPlayerData;
import net.snowteb.warriorcats_events.clan.WCEPlayerDataProvider;
import net.snowteb.warriorcats_events.entity.custom.EagleEntity;
import net.snowteb.warriorcats_events.sound.ModSounds;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class CallEaglesPacket {

    public static List<Integer> eagleIDs = List.of();

    public CallEaglesPacket(List<Integer> eagles) {
        eagleIDs = eagles;
    }

    public static void encode(CallEaglesPacket msg, FriendlyByteBuf buf) {
        buf.writeVarInt(eagleIDs.size());
        for (int id : eagleIDs) buf.writeVarInt(id);

    }

    public static CallEaglesPacket decode(FriendlyByteBuf buf) {
        int size1 = buf.readVarInt();
        List<Integer> idsEagle = new ArrayList<>();
        for (int i = 0; i < size1; i++) idsEagle.add(buf.readVarInt());

        return new CallEaglesPacket(idsEagle);
    }

    public static void handle(CallEaglesPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {

            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            ServerLevel level = player.serverLevel();

            int eaglesCalled = 0;

            for (int i : eagleIDs) {
                Entity entity = level.getEntity(i);
                if (entity instanceof EagleEntity eagle && eagle.getOwner() == player) {
//                    eagle.moveTargetPoint = player.blockPosition().getCenter();
                    eagle.addEffect(new MobEffectInstance(MobEffects.GLOWING, 20, 0));
                    eagle.isOwnerCalling = true;
                    eagle.setNewControlMode(EagleEntity.Control.FLY);
                    eaglesCalled++;
                }
            }

            float pitch = 0.9f;
            WCEPlayerData.Age morphAge = player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                    .map(WCEPlayerData::getMorphAge).orElse(WCEPlayerData.Age.ADULT);
            switch (morphAge) {
                case KIT -> pitch = 1.3f;
                case APPRENTICE -> pitch = 1.1f;
                case ADULT -> pitch = 0.9f;
            }

            if (eaglesCalled > 0) {
                level.playSound(null, player.blockPosition(), ModSounds.LEADER_CALL.get(), SoundSource.PLAYERS, 0.7F, pitch);
                player.displayClientMessage(Component.translatable("generic.eagles_called",
                                Component.literal(String.valueOf(eaglesCalled)).withStyle(ChatFormatting.GOLD))
                        ,true
                );
            } else {
                player.displayClientMessage(Component.empty()
                        .append(Component.translatable("generic.no_eagles_in_range").withStyle(ChatFormatting.GRAY)),true
                );
            }


        });

        ctx.get().setPacketHandled(true);
    }

}

