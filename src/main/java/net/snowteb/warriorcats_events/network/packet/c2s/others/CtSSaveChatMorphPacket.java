package net.snowteb.warriorcats_events.network.packet.c2s.others;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.s2c.others.StCFinallySaveMorph;
import net.snowteb.warriorcats_events.managers.ServerPlayerMorphsCache;

import java.util.function.Supplier;

public class CtSSaveChatMorphPacket {

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

    public static void handle(CtSSaveChatMorphPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            ServerPlayerMorphsCache.ServerMorphData data = ServerPlayerMorphsCache.getMorphsCache().get(packet.Key);

            if (data == null) {
                player.sendSystemMessage(Component.literal("Morph not found.").withStyle(ChatFormatting.RED));
                return;
            }

            ModPackets.sendToPlayer(new StCFinallySaveMorph(packet.Key, data.genetics, data.variants,
                    data.chimeraGenetics, data.chimeraVariants, data.onGeneticalSkin, data.presetVariant), player);

        });

        ctx.get().setPacketHandled(true);
    }

}
