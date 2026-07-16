package net.snowteb.warriorcats_events.network.packet.c2s.others;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.attachments.ModAttachments;
import net.snowteb.warriorcats_events.attachments.WCEPlayerData;
import net.snowteb.warriorcats_events.entity.custom.EagleEntity;
import net.snowteb.warriorcats_events.sound.ModSounds;

import java.util.ArrayList;
import java.util.List;

public class CallEaglesPacket implements CustomPacketPayload {

    public final List<Integer> eagleIDs;

    public CallEaglesPacket(List<Integer> eagles) {
        eagleIDs = eagles;
    }

    public static void encode(CallEaglesPacket msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.eagleIDs.size());
        for (int id : msg.eagleIDs) buf.writeVarInt(id);

    }

    public static CallEaglesPacket decode(FriendlyByteBuf buf) {
        int size1 = buf.readVarInt();
        List<Integer> idsEagle = new ArrayList<>();
        for (int i = 0; i < size1; i++) idsEagle.add(buf.readVarInt());

        return new CallEaglesPacket(idsEagle);
    }

    public static void handle(CallEaglesPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {

            ServerPlayer player = (ServerPlayer) ctx.player();

            ServerLevel level = player.serverLevel();

            int eaglesCalled = 0;

            for (int i : msg.eagleIDs) {
                Entity entity = level.getEntity(i);
                if (entity instanceof EagleEntity eagle && eagle.getOwner() == player) {
                    eagle.addEffect(new MobEffectInstance(MobEffects.GLOWING, 20, 0));
                    eagle.isOwnerCalling = true;
                    eagle.setNewControlMode(EagleEntity.Control.FLY);
                    eaglesCalled++;
                }
            }

            float pitch = 0.9f;
            WCEPlayerData.Age morphAge = player.getData(ModAttachments.PLAYER_WCE_DATA).getMorphAge();
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
    }

    public static final Type<CallEaglesPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "call_eagles"));

    public static final StreamCodec<FriendlyByteBuf, CallEaglesPacket> CODEC =
            StreamCodec.of(
                    (buf, pkt) -> encode(pkt, buf),
                    buf -> decode(buf)
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

