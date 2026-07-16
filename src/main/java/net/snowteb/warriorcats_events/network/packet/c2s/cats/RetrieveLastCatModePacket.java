package net.snowteb.warriorcats_events.network.packet.c2s.cats;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;

import java.util.function.Supplier;

public class RetrieveLastCatModePacket {
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

    public static void handle(RetrieveLastCatModePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

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
        ctx.get().setPacketHandled(true);
    }
}
