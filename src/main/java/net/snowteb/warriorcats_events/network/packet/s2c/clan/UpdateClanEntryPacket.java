package net.snowteb.warriorcats_events.network.packet.s2c.clan;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.clan.ClanData;
import net.snowteb.warriorcats_events.client.ClientClans;
import net.snowteb.warriorcats_events.client.ClientPacketHandles;

import java.util.function.Supplier;

public class UpdateClanEntryPacket {
    private final ClientClans.Entry entry;

    public UpdateClanEntryPacket(ClientClans.Entry entry) {
        this.entry = entry;
    }


    public UpdateClanEntryPacket(FriendlyByteBuf buf) {
        this.entry = ClientClans.Entry.readFromBuf(buf);
    }


    public void toBytes(FriendlyByteBuf buf) {
        this.entry.writeToBuf(buf);
    }


    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ClientPacketHandles.updateClanEntry(this.entry);
        });
        ctx.setPacketHandled(true);
    }
}
