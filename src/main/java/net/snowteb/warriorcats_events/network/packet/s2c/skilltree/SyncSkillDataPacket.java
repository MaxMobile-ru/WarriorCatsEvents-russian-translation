package net.snowteb.warriorcats_events.network.packet.s2c.skilltree;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.skills.ISkillData;
import net.snowteb.warriorcats_events.skills.PlayerSkill;
import net.snowteb.warriorcats_events.skills.PlayerSkillProvider;
import net.minecraftforge.common.util.LazyOptional;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public class SyncSkillDataPacket {
    private final PlayerSkill playerSkill;

    public SyncSkillDataPacket(PlayerSkill playerSkill) {
        this.playerSkill = playerSkill;
    }

    public SyncSkillDataPacket(FriendlyByteBuf buf) {
        CompoundTag tag = buf.readNbt();
        PlayerSkill playerSkill = new PlayerSkill();
        if (tag != null){
            playerSkill.deserializeNBT(tag);
        }
        this.playerSkill = playerSkill;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(playerSkill.serializeNBT());
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) return;

            LazyOptional<ISkillData> cap = player.getCapability(PlayerSkillProvider.SKILL_DATA);
            cap.ifPresent(data -> {
                data.copyFrom(playerSkill);
            });

        });
        return true;
    }
}
