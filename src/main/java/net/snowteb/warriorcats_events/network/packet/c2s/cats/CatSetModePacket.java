package net.snowteb.warriorcats_events.network.packet.c2s.cats;

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

public class CatSetModePacket implements CustomPacketPayload {
    private final int entityId;
    private final WCatEntity.CatMode catMode;

    public CatSetModePacket(int entityId,  WCatEntity.CatMode catMode) {
        this.entityId = entityId;
        this.catMode = catMode;
    }

    public static void encode(CatSetModePacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.entityId);
        buf.writeEnum(msg.catMode);
    }

    public static CatSetModePacket decode(FriendlyByteBuf buf) {
        return new CatSetModePacket(
                buf.readInt(),
                buf.readEnum(WCatEntity.CatMode.class)
        );
    }

    public static void handle(CatSetModePacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) ctx.player();

            ServerLevel level = player.serverLevel();
            Entity e = level.getEntity(msg.entityId);

            if (e instanceof WCatEntity cat) {
                cat.mode = msg.catMode;
                if (msg.catMode == WCatEntity.CatMode.WANDER) {
                    cat.wanderCenter = cat.blockPosition();
                }
                if (msg.catMode != WCatEntity.CatMode.SIT) {
                    cat.setInSittingPose(false);
                } else {
                    cat.setInSittingPose(true);
                }
                cat.sendModeMessage(player);
            }

        });
    }

    public static final Type<CatSetModePacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "cat_set_mode"));

    public static final StreamCodec<FriendlyByteBuf, CatSetModePacket> CODEC =
            StreamCodec.of(
                    (buf, pkt) ->encode(pkt, buf),
                    buf -> decode(buf)
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
