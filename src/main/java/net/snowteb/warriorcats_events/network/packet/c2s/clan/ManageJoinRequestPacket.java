package net.snowteb.warriorcats_events.network.packet.c2s.clan;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.clan.ClanData;
import net.snowteb.warriorcats_events.clan.ClanObjects;
import net.snowteb.warriorcats_events.clanplayerdata.WCEPlayerData;
import net.snowteb.warriorcats_events.clanplayerdata.WCEPlayerDataProvider;
import net.snowteb.warriorcats_events.client.ClientClans;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.s2c.clan.DisplayClanMenuMessage;
import net.snowteb.warriorcats_events.network.packet.s2c.clan.S2CSyncClanDataPacket;
import net.snowteb.warriorcats_events.network.packet.s2c.clan.UpdateClanEntryPacket;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class ManageJoinRequestPacket {

    private final ClanObjects.JoinRequest request;
    private final String action;

    public ManageJoinRequestPacket(ClanObjects.JoinRequest request, String action) {
        this.request = request;
        this.action = action;
    }

    public static ManageJoinRequestPacket decode(FriendlyByteBuf buf) {
        CompoundTag tag = buf.readNbt();
        String action = buf.readUtf();
        if (tag == null) {
            return new ManageJoinRequestPacket(new ClanObjects.JoinRequest(
                    null, null,
                    null, null,
                    ""), action);
        }
        ClanObjects.JoinRequest joinRequest = ClanObjects.JoinRequest.deserializeSingleRequest(tag);

        return new ManageJoinRequestPacket(joinRequest, action);
    }

    public static void encode(ManageJoinRequestPacket packet, FriendlyByteBuf buf) {
        buf.writeNbt(packet.request.serializeSingleRequest());
        buf.writeUtf(packet.action);
    }

    public static void handle(ManageJoinRequestPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {

            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            ServerLevel level = player.serverLevel().getServer().overworld();

            ClanData data = ClanData.get(level);
            ClanData.Clan clan = data.getClan(packet.request.getClanUUID());
            if (clan == null) return;
            boolean canManage = data.canManage(clan, player.getUUID());
            if (!canManage) return;

            Component messageToDisplay = Component.empty();

            switch (packet.action) {
                case "accept" -> {
                    packet.request.accept(level, player);
                    messageToDisplay = Component.translatable("clan.join_request.accepted");
                }
                case "deny" -> {
                    packet.request.decline(level, player);
                    messageToDisplay = Component.translatable("clan.join_request.denied");
                }
            }

            if (!messageToDisplay.getString().isEmpty()) {
                ModPackets.sendToPlayer(new DisplayClanMenuMessage(messageToDisplay, false), player);
            }


            data.updateScreen(clan, player);

        });

        ctx.get().setPacketHandled(true);
    }
}

