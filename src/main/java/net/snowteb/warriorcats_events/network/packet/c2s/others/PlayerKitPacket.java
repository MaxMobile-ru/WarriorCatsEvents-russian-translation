package net.snowteb.warriorcats_events.network.packet.c2s.others;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.attachments.ModAttachments;
import net.snowteb.warriorcats_events.attachments.WCEPlayerData;
import net.snowteb.warriorcats_events.clan.ClanData;
import net.snowteb.warriorcats_events.managers.PlayerKittingRequestManager;

import java.util.UUID;

public class PlayerKitPacket implements CustomPacketPayload {

    private final UUID targetUUID;

    public PlayerKitPacket(UUID targetUUID) {
        this.targetUUID = targetUUID;
    }

    public static void encode(PlayerKitPacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.targetUUID);
    }

    public static PlayerKitPacket decode(FriendlyByteBuf buf) {
        return new PlayerKitPacket(
                buf.readUUID()
        );
    }

    public static void handle(PlayerKitPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {

            ServerPlayer player = (ServerPlayer) ctx.player();

            ServerLevel level = player.serverLevel();
            Entity entity = level.getEntity(msg.targetUUID);

            if (!(entity instanceof ServerPlayer targetPlayer)) return;

            String myMorphName = player.getData(ModAttachments.PLAYER_WCE_DATA).getMorphName();

            String targetMorphName = targetPlayer.getData(ModAttachments.PLAYER_WCE_DATA).getMorphName();

            WCEPlayerData.Age myAge = player.getData(ModAttachments.PLAYER_WCE_DATA).getMorphAge();

            WCEPlayerData.Age targetAge = targetPlayer.getData(ModAttachments.PLAYER_WCE_DATA).getMorphAge();

            if (myAge != WCEPlayerData.Age.ADULT) {
                player.sendSystemMessage(Component.translatable("generic.cat_not_old_enough", myMorphName)
                        .withStyle(ChatFormatting.RED));
                return;
            } else if (targetAge != WCEPlayerData.Age.ADULT) {
                player.sendSystemMessage(Component.translatable("generic.cat_not_old_enough", targetMorphName)
                        .withStyle(ChatFormatting.RED));
                return;
            }

            PlayerKittingRequestManager.request(targetPlayer, player);

            player.sendSystemMessage(
                    Component.translatable("managers.kit_request",
                            ClanData.logFormattedPlayerName(targetPlayer))
            );

            targetPlayer.sendSystemMessage(
                    Component.translatable("managers.kit_request_received",
                            ClanData.logFormattedPlayerName(player))
            );

            targetPlayer.sendSystemMessage(
                    PlayerKittingRequestManager.getMessage()
            );

        });
    }

    public static final Type<PlayerKitPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "player_kit_packet"));

    public static final StreamCodec<FriendlyByteBuf, PlayerKitPacket> CODEC =
            StreamCodec.of(
                    (buf, pkt) -> encode(pkt, buf),
                    buf -> decode(buf)
            );


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

