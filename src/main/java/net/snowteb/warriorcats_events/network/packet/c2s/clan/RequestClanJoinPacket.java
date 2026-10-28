package net.snowteb.warriorcats_events.network.packet.c2s.clan;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.clan.ClanData;
import net.snowteb.warriorcats_events.clan.ClanObjects;
import net.snowteb.warriorcats_events.client.ClientClans;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.s2c.clan.DisplayClanMenuMessage;
import net.snowteb.warriorcats_events.network.packet.s2c.clan.UpdateClanEntryPacket;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class RequestClanJoinPacket {

    private final String message;
    private final UUID clanUUID;

    public RequestClanJoinPacket(String message, UUID clanUUID) {
        this.message = message;
        this.clanUUID = clanUUID;
    }

    public static RequestClanJoinPacket decode(FriendlyByteBuf buf) {
        return new RequestClanJoinPacket(buf.readUtf(200), buf.readUUID());
    }

    public static void encode(RequestClanJoinPacket packet, FriendlyByteBuf buf) {
        buf.writeUtf(packet.message);
        buf.writeUUID(packet.clanUUID);
    }

    public static void handle(RequestClanJoinPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {

            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            ServerLevel level = player.serverLevel().getServer().overworld();

            ClanData data = ClanData.get(level);
            ClanData.Clan clan = data.getClan(packet.clanUUID);

            Component messageToDisplay;

            if (data.requestJoin(player, packet.clanUUID, level, packet.message)) {
                messageToDisplay = Component.translatable("clan.join_request_sent");
                if (!messageToDisplay.getString().isEmpty()) {
                    ModPackets.sendToPlayer(new DisplayClanMenuMessage(messageToDisplay, false), player);
                }
            }

            data.updateScreen(clan, player);

        });

        ctx.get().setPacketHandled(true);
    }
}

