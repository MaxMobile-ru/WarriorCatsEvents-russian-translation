package net.snowteb.warriorcats_events.network.packet.c2s.others;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.clan.ClanData;
import net.snowteb.warriorcats_events.clan.WCEPlayerData;
import net.snowteb.warriorcats_events.clan.WCEPlayerDataProvider;
import net.snowteb.warriorcats_events.managers.PlayerKittingRequestManager;

import java.util.UUID;
import java.util.function.Supplier;

public class PlayerKitPacket {

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

    public static void handle(PlayerKitPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {

            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            ServerLevel level = player.serverLevel();
            Entity entity = level.getEntity(msg.targetUUID);

            if (!(entity instanceof ServerPlayer targetPlayer)) return;

            String myMorphName = player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                    .map(WCEPlayerData::getMorphName).orElse("Unnamed");
            String targetMorphName = targetPlayer.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                    .map(WCEPlayerData::getMorphName).orElse("Unnamed");

            WCEPlayerData.Age myAge = player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                    .map(WCEPlayerData::getMorphAge).orElse(WCEPlayerData.Age.ADULT);
            WCEPlayerData.Age targetAge = targetPlayer.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                    .map(WCEPlayerData::getMorphAge).orElse(WCEPlayerData.Age.ADULT);

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

        ctx.get().setPacketHandled(true);
    }

}

