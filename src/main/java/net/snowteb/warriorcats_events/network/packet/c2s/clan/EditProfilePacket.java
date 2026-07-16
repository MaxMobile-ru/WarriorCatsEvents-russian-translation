package net.snowteb.warriorcats_events.network.packet.c2s.clan;

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
import net.snowteb.warriorcats_events.attachments.WCEPlayerDataUtils;
import tocraft.walkers.api.PlayerShape;

public class EditProfilePacket implements CustomPacketPayload {

    private final String key;
    private final String newValue;


    public EditProfilePacket(String key, String newValue) {
        this.key = key;
        this.newValue = newValue;
    }

    public static EditProfilePacket decode(FriendlyByteBuf buf) {
        String key = buf.readUtf();
        String newValue = buf.readUtf();

        return new EditProfilePacket(key, newValue);
    }

    public static void encode(EditProfilePacket packet, FriendlyByteBuf buf) {

        buf.writeUtf(packet.key);
        buf.writeUtf(packet.newValue);
    }

    public static void handle(EditProfilePacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) ctx.player();

            CapabilityManager.attachmentProvider(player, ModAttachments.PLAYER_WCE_DATA, cap -> {

                if (packet.key.equals("bio")) {
                    cap.setCharacterBio(packet.newValue);
                } else if (packet.key.equals("gender")) {
                    cap.setGenderText(packet.newValue);
                    if (!packet.newValue.isEmpty()) {
                        cap.setGenderData(2);
                    }
                } else {
                    player.sendSystemMessage(Component.literal("Invalid key provided."));
                }

                PlayerShape.updateShapes(player, WCEPlayerDataUtils.createShape(player, player.serverLevel()));
            });


            player.server.getCommands().performPrefixedCommand(
                    player.createCommandSourceStack(),
                    "wce info profile"
            );

        });
    }


    public static final Type<EditProfilePacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "edit_profile"));

    public static final StreamCodec<FriendlyByteBuf, EditProfilePacket> CODEC =
            StreamCodec.of(
                    (buf, pkt) -> encode(pkt, buf),
                    buf -> decode(buf)
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
