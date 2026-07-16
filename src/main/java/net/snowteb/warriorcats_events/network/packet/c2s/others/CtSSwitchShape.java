package net.snowteb.warriorcats_events.network.packet.c2s.others;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.clan.WCEPlayerData;
import net.snowteb.warriorcats_events.clan.WCEPlayerDataProvider;
import net.snowteb.warriorcats_events.clan.WCEPlayerDataUtils;
import net.snowteb.warriorcats_events.diseases.DiseaseManager;
import net.snowteb.warriorcats_events.entity.ModEntities;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCGenetics;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.skills.PlayerSkill;
import net.snowteb.warriorcats_events.skills.PlayerSkillProvider;
import tocraft.walkers.api.PlayerShape;

import java.util.function.Supplier;

public class CtSSwitchShape {

    public CtSSwitchShape() {

    }

    public CtSSwitchShape(FriendlyByteBuf buf) {
    }

    public void toBytes(FriendlyByteBuf buf) {
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {

            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            WCatEntity cat = WCEPlayerDataUtils.createShape(player, player.level());

            LivingEntity current = PlayerShape.getCurrentShape(player);

            if (!(current instanceof WCatEntity)) {
                PlayerShape.updateShapes(player, cat);
                player.getCapability(PlayerSkillProvider.SKILL_DATA).ifPresent(skillProvider -> {
                    PlayerSkill.reviveAttributes(player, skillProvider);
                });
            } else {
                PlayerShape.updateShapes(player, null);
                player.getCapability(PlayerSkillProvider.SKILL_DATA).ifPresent(skillProvider -> {
                    PlayerSkill.removeAttributes(player);
                });
            }

            DiseaseManager.refreshData(player);


        });
        ctx.get().setPacketHandled(true);
        return true;
    }

}
