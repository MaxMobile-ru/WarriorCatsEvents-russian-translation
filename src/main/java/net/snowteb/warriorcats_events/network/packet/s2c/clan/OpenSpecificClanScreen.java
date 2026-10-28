package net.snowteb.warriorcats_events.network.packet.s2c.clan;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.client.ClientClans;
import net.snowteb.warriorcats_events.client.ClientPacketHandles;

import java.util.UUID;
import java.util.function.Supplier;

public class OpenSpecificClanScreen {

    private final ClientClans.Entry entry;

    public OpenSpecificClanScreen(ClientClans.Entry entry) {
        this.entry = entry;
    }

    public static void encode(OpenSpecificClanScreen msg, FriendlyByteBuf buf) {
        msg.entry.writeToBuf(buf);
    }

    public static OpenSpecificClanScreen decode(FriendlyByteBuf buf) {
        return new OpenSpecificClanScreen(ClientClans.Entry.readFromBuf(buf));
    }

    public static void handle(OpenSpecificClanScreen msg,
                              Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {
            ClientPacketHandles.openSpecificClan(msg.entry);
        });

        ctx.get().setPacketHandled(true);
    }
}



