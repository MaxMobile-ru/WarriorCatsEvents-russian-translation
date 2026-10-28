package net.snowteb.warriorcats_events.network.packet.s2c.clan;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.clanplayerdata.WCEPlayerData;
import net.snowteb.warriorcats_events.client.ClientClanData;

import java.util.function.Supplier;

public class S2CSyncClanDataPacket {

    private final WCEPlayerData data;

    public S2CSyncClanDataPacket(WCEPlayerData data) {
        this.data = data;
    }

    public static S2CSyncClanDataPacket decode(FriendlyByteBuf buf) {
        WCEPlayerData data = new WCEPlayerData();
        CompoundTag tag = buf.readNbt();
        if (tag != null) {
            data.loadNBT(tag);
        }
        return new S2CSyncClanDataPacket(data);
    }

    public static void encode(S2CSyncClanDataPacket packet, FriendlyByteBuf buf) {
        CompoundTag tag = new CompoundTag();
        packet.data.saveNBT(tag);
        buf.writeNbt(tag);
    }

    public static void handle(S2CSyncClanDataPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientClanData.set(packet.data);
        });

        ctx.get().setPacketHandled(true);
    }
}
