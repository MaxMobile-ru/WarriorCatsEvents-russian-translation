package net.snowteb.warriorcats_events.network.packet.c2s.skilltree;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.attachments.CapabilityManager;
import net.snowteb.warriorcats_events.attachments.ModAttachments;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.s2c.skilltree.StCStealthSyncPacket;
import net.snowteb.warriorcats_events.zconfig.WCEServerConfig;

public class CtSToggleStealthPacket implements CustomPacketPayload {

    private boolean state;

    public CtSToggleStealthPacket(boolean state) {
        this.state = state;
    }

    public CtSToggleStealthPacket(FriendlyByteBuf buf) {
        this.state = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(state);
    }

    public boolean handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {

            ServerPlayer player = (ServerPlayer) ctx.player();

            CapabilityManager.attachmentProvider(player, ModAttachments.PLAYER_STEALTH, cap -> {
                if (!cap.isUnlocked() || !cap.isOn()) {
                    return;
                }
                if (!WCEServerConfig.SERVER.SKILL_TREE_SERVER.get()) {
                    player.sendSystemMessage(Component.translatable("generic.skill_tree_disabled").withStyle(ChatFormatting.RED));
                    return;
                }
                cap.setStealthOn(state);

                if (state) {
                    player.level().playSound(
                            null,
                            player.blockPosition(),
                            SoundEvents.AZALEA_HIT,
                            SoundSource.PLAYERS,
                            0.6f,
                            1f
                    );
                } else {
                    player.level().playSound(
                            null,
                            player.blockPosition(),
                            SoundEvents.AZALEA_BREAK,
                            SoundSource.PLAYERS,
                            0.6f,
                            0.8f
                    );
                }
                ModPackets.sendToPlayer(
                        new StCStealthSyncPacket(cap.isUnlocked(), cap.isStealthOn(), cap.isOn()), player);
                player.setInvisible(state);
            });

        });

        return true;
    }

    public static final Type<CtSToggleStealthPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "toggle_stealth"));

    public static final StreamCodec<FriendlyByteBuf, CtSToggleStealthPacket> CODEC =
            StreamCodec.of(
                    (buf, pkt) -> pkt.toBytes(buf),
                    buf -> new CtSToggleStealthPacket(buf)
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
