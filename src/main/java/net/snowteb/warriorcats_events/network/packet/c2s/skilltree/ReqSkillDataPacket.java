package net.snowteb.warriorcats_events.network.packet.c2s.skilltree;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.s2c.skilltree.SyncSkillDataPacket;
import net.snowteb.warriorcats_events.skills.PlayerSkill;
import net.snowteb.warriorcats_events.skills.PlayerSkillProvider;

import java.util.function.Supplier;

public class ReqSkillDataPacket {

    public ReqSkillDataPacket() {}

    public ReqSkillDataPacket(FriendlyByteBuf buf) {}

    public void toBytes(FriendlyByteBuf buf) {}

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;

            player.getCapability(PlayerSkillProvider.SKILL_DATA).ifPresent(data -> {
                ModPackets.sendToPlayer(new SyncSkillDataPacket((PlayerSkill) data), player);
            });

        });
        return true;
    }
}
