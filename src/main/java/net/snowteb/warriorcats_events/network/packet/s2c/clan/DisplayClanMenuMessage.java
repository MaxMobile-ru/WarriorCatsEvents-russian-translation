package net.snowteb.warriorcats_events.network.packet.s2c.clan;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.client.ClientPacketHandles;

import java.util.function.Supplier;

public class DisplayClanMenuMessage {
    private final Component message;
    private final boolean isAnError;

    public DisplayClanMenuMessage(Component message, boolean isAnError) {
        this.message = message;
        this.isAnError = isAnError;
    }


    public DisplayClanMenuMessage(FriendlyByteBuf buf) {
        this.isAnError = buf.readBoolean();
        this.message = buf.readComponent();
    }


    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(isAnError);
        buf.writeComponent(message);
    }


    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ClientPacketHandles.displayScreenMessage(this.isAnError, this.message);
        });
        ctx.setPacketHandled(true);
    }
}
