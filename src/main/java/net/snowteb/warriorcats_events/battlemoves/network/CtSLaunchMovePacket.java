package net.snowteb.warriorcats_events.battlemoves.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.battlemoves.BattleMoveType;
import net.snowteb.warriorcats_events.battlemoves.BattleMoveTypeAccessor;
import net.snowteb.warriorcats_events.battlemoves.BattleMoveTypes;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.s2c.skilltree.SyncSkillDataPacket;
import net.snowteb.warriorcats_events.skills.PlayerSkill;
import net.snowteb.warriorcats_events.skills.PlayerSkillProvider;

import java.util.function.Supplier;

public class CtSLaunchMovePacket {

    private final int moveIndex;

    public CtSLaunchMovePacket(int moveIndex) {
        this.moveIndex = moveIndex;
    }

    public static CtSLaunchMovePacket decode(FriendlyByteBuf buf) {
        return new CtSLaunchMovePacket(buf.readInt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(moveIndex);
    }

    public static void handle(CtSLaunchMovePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            player.getCapability(PlayerSkillProvider.SKILL_DATA).ifPresent(data -> {
                BattleMoveTypeAccessor accessor = data.getBattleMovesStore().getSlot(msg.moveIndex);
                if (accessor == null) {
                    player.sendSystemMessage(Component.literal("Move not on slot"));
                    data.getBattleMovesStore().unlockMove(BattleMoveTypes.SLASH.getId());
                    data.getBattleMovesStore().setInSlot(BattleMoveTypes.SLASH.getId(), 1);
                    return;
                }

                accessor.run(player);

                ModPackets.sendToPlayer(new SyncSkillDataPacket((PlayerSkill) data), player);
            });

        });
        ctx.get().setPacketHandled(true);
    }
}
