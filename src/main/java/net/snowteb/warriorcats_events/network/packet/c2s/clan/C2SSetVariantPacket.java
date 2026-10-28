package net.snowteb.warriorcats_events.network.packet.c2s.clan;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.clanplayerdata.WCEPlayerDataProvider;

import java.util.function.Supplier;

public class C2SSetVariantPacket {

    private final int variant;

    public C2SSetVariantPacket(int variant) {
        this.variant = variant;
    }

    public static C2SSetVariantPacket decode(FriendlyByteBuf buf) {
        int variant = buf.readInt();
        return new C2SSetVariantPacket(variant);
    }

    public static void encode(C2SSetVariantPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.variant);
    }

    public static void handle(C2SSetVariantPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA).ifPresent(cap -> {
                cap.setVariantData(packet.variant);
            });
        });

        ctx.get().setPacketHandled(true);
    }
}
