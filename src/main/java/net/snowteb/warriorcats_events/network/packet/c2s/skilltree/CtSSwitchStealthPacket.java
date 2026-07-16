package net.snowteb.warriorcats_events.network.packet.c2s.skilltree;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.attachments.CapabilityManager;
import net.snowteb.warriorcats_events.attachments.ModAttachments;

public class CtSSwitchStealthPacket implements CustomPacketPayload {

    public CtSSwitchStealthPacket() {}

    public static void encode(CtSSwitchStealthPacket msg, FriendlyByteBuf buf) {}

    public static CtSSwitchStealthPacket decode(FriendlyByteBuf buf) {
        return new CtSSwitchStealthPacket();
    }

    public static void handle(CtSSwitchStealthPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) ctx.player();

            CapabilityManager.attachmentProvider(player, ModAttachments.PLAYER_STEALTH, cap -> {
                if (!cap.isUnlocked()) {
                    player.sendSystemMessage(Component.translatable("skills.stealth_not_unlocked")
                            .withStyle(ChatFormatting.RED));
                    return;
                }

                if (cap.isOn()) {
                    cap.setOn(false);
                    player.sendSystemMessage(Component.translatable("skills.stealth_off").withStyle(ChatFormatting.RED));
                } else {
                    cap.setOn(true);
                    player.sendSystemMessage(Component.translatable("skills.stealth_on").withStyle(ChatFormatting.GREEN));
                }

                cap.sync(player);
            });


        });
    }

    public static final Type<CtSSwitchStealthPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "switch_stealth"));

    public static final StreamCodec<FriendlyByteBuf, CtSSwitchStealthPacket> CODEC =
            StreamCodec.of(
                    (buf, pkt) -> encode(pkt, buf),
                    buf -> decode(buf)
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
