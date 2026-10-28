package net.snowteb.warriorcats_events.network.packet.s2c.others;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.client.ClientPacketHandles;

import java.util.UUID;
import java.util.function.Supplier;

public class SendBubbleMessagePacket {
    private final UUID sender;
    private final Component message;

    public SendBubbleMessagePacket(UUID sender, Component message) {
        this.sender = sender;
        this.message = message;
    }

    public SendBubbleMessagePacket(FriendlyByteBuf buf) {
        this.sender = buf.readUUID();
        this.message = buf.readComponent();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(this.sender);
        buf.writeComponent(this.message);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ClientPacketHandles.addBubbleMessage(sender, message);
        });
        return true;
    }


}
