package net.snowteb.warriorcats_events.network.packet.c2s.others;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.attachments.*;
import net.snowteb.warriorcats_events.diseases.DiseaseManager;
import net.snowteb.warriorcats_events.entity.ModEntities;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCGenetics;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import tocraft.walkers.api.PlayerShape;

public class CtSSwitchShape implements CustomPacketPayload {

    public CtSSwitchShape() {

    }

    public CtSSwitchShape(FriendlyByteBuf buf) {
    }

    public void toBytes(FriendlyByteBuf buf) {
    }

    public boolean handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {

            ServerPlayer player = (ServerPlayer) ctx.player();

            WCatEntity cat = WCEPlayerDataUtils.createShape(player, player.level());

            LivingEntity current = PlayerShape.getCurrentShape(player);

            if (!(current instanceof WCatEntity)) {
                PlayerShape.updateShapes(player, cat);
                CapabilityManager.attachmentProvider(player, ModAttachments.PLAYER_SKILL, cap -> {
                    PlayerSkill.reviveAttributes(player, cap);
                });
            } else {
                PlayerShape.updateShapes(player, null);
                CapabilityManager.attachmentProvider(player, ModAttachments.PLAYER_SKILL, cap -> {
                    PlayerSkill.removeAttributes(player);
                });
            }

            DiseaseManager.refreshData(player);


        });
        return true;
    }

    public static final Type<CtSSwitchShape> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "switch_shape"));

    public static final StreamCodec<FriendlyByteBuf, CtSSwitchShape> CODEC =
            StreamCodec.of(
                    (buf, pkt) -> pkt.toBytes(buf),
                    buf -> new CtSSwitchShape(buf)
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
