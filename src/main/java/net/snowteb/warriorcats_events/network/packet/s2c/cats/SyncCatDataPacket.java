package net.snowteb.warriorcats_events.network.packet.s2c.cats;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;

public class SyncCatDataPacket implements CustomPacketPayload {
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

    public static void handle(SyncCatDataPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) ctx.player();

            ServerLevel level = player.serverLevel();
            Entity e = level.getEntity(msg.entityId);

            if (e instanceof WCatEntity cat) {
                cat.syncFriendshipToPlayer(player);
            }

        });
    }

    public static final Type<SyncCatDataPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "sync_cat_data"));

    public static final StreamCodec<FriendlyByteBuf, SyncCatDataPacket> CODEC =
            StreamCodec.of(
                    (buf, pkt) ->encode(pkt, buf),
                    buf -> decode(buf)
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
