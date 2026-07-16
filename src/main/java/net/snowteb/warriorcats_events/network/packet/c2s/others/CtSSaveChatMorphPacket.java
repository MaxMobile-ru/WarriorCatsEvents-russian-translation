package net.snowteb.warriorcats_events.network.packet.c2s.others;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.s2c.others.StCFinallySaveMorph;
import net.snowteb.warriorcats_events.managers.ServerPlayerMorphsCache;

public class CtSSaveChatMorphPacket implements CustomPacketPayload {

    private final String Key;

    public CtSSaveChatMorphPacket(String key) {
        this.Key = key;
    }

    public static CtSSaveChatMorphPacket decode(FriendlyByteBuf buf) {

        String key = buf.readUtf();

        return new CtSSaveChatMorphPacket( key);
    }

    public static void encode(CtSSaveChatMorphPacket packet, FriendlyByteBuf buf) {

        buf.writeUtf(packet.Key);

    }

    public static void handle(CtSSaveChatMorphPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) ctx.player();

            ServerPlayerMorphsCache.ServerMorphData data = ServerPlayerMorphsCache.getMorphsCache().get(packet.Key);

            if (data == null) {
                player.sendSystemMessage(Component.literal("Morph not found.").withStyle(ChatFormatting.RED));
                return;
            }

            ModPackets.sendToPlayer(new StCFinallySaveMorph(packet.Key, data.genetics, data.variants, data.chimeraGenetics, data.chimeraVariants, data.onGeneticalSkin, data.presetVariant), player);

        });
    }

    public static final Type<CtSSaveChatMorphPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "save_chat_morph"));

    public static final StreamCodec<FriendlyByteBuf, CtSSaveChatMorphPacket> CODEC =
            StreamCodec.of(
                    (buf, pkt) -> encode(pkt, buf),
                    buf -> decode(buf)
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
