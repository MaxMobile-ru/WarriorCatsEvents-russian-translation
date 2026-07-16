package net.snowteb.warriorcats_events.network.packet.c2s.cats;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;

import java.util.function.Supplier;

public class CatSetModePacket {
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

    public static void handle(CatSetModePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

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
        ctx.get().setPacketHandled(true);
    }
}
