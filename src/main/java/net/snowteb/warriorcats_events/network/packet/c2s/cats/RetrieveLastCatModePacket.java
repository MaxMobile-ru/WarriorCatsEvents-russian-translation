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

public class RetrieveLastCatModePacket implements CustomPacketPayload {
    private final int entityId;

    public RetrieveLastCatModePacket(int entityId) {
        this.entityId = entityId;
    }

    public static void encode(RetrieveLastCatModePacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.entityId);
    }

    public static RetrieveLastCatModePacket decode(FriendlyByteBuf buf) {
        return new RetrieveLastCatModePacket(
                buf.readInt()
        );
    }

    public static void handle(RetrieveLastCatModePacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) ctx.player();

            ServerLevel level = player.serverLevel();
            Entity e = level.getEntity(msg.entityId);

            if (e instanceof WCatEntity cat) {
                cat.mode = cat.lastMode;
                if (cat.lastMode != WCatEntity.CatMode.SIT) {
                    cat.setInSittingPose(false);
                }
                cat.lastMode = cat.mode;

                cat.lookAtLeaderFlag = false;
                cat.isLookingAtLeader = false;
            }

        });
    }

    public static final Type<RetrieveLastCatModePacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "retrieve_last_cat_mode"));

    public static final StreamCodec<FriendlyByteBuf, RetrieveLastCatModePacket> CODEC =
            StreamCodec.of(
                    (buf, pkt) ->encode(pkt, buf),
                    buf -> decode(buf)
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
