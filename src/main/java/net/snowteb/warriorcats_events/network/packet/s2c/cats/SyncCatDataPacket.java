package net.snowteb.warriorcats_events.network.packet.s2c.cats;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;

import java.util.function.Supplier;

public class SyncCatDataPacket {
    private final int entityId;

    public SyncCatDataPacket(int entityId) {
        this.entityId = entityId;
    }

    public static void encode(SyncCatDataPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.entityId);
    }

    public static SyncCatDataPacket decode(FriendlyByteBuf buf) {
        return new SyncCatDataPacket(
                buf.readInt()
        );
    }

    public static void handle(SyncCatDataPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            ServerLevel level = player.serverLevel();
            Entity e = level.getEntity(msg.entityId);

            if (e instanceof WCatEntity cat) {
                cat.syncFriendshipToPlayer(player);
            }

        });
        ctx.get().setPacketHandled(true);
    }
}
