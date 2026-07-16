package net.snowteb.warriorcats_events.network.packet.c2s.skilltree;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.s2c.skilltree.StCStealthSyncPacket;
import net.snowteb.warriorcats_events.stealth.PlayerStealthProvider;

import java.util.function.Supplier;

public class CtSSwitchStealthPacket {

    public CtSSwitchStealthPacket() {
    }

    public static void encode(CtSSwitchStealthPacket msg, FriendlyByteBuf buf) {
    }

    public static CtSSwitchStealthPacket decode(FriendlyByteBuf buf) {
        return new CtSSwitchStealthPacket();
    }

    public static void handle(CtSSwitchStealthPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();

            if (player == null) return;

            player.getCapability(PlayerStealthProvider.STEALTH_MODE).ifPresent(cap -> {

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
                ModPackets.sendToPlayer(
                        new StCStealthSyncPacket(cap.isUnlocked(), cap.isStealthOn(), cap.isOn()), player);
            });


        });
        ctx.get().setPacketHandled(true);
    }

}
