package net.snowteb.warriorcats_events.network.packet.c2s.clan;

import net.minecraft.ChatFormatting;
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
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.s2c.clan.DisplayClanMenuMessage;
import net.snowteb.warriorcats_events.network.packet.s2c.clan.UpdateClanEntryPacket;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class RenameClanPacket {

    private final String  name;

    public RenameClanPacket(String name) {
        this.name = name;
    }

    public static RenameClanPacket decode(FriendlyByteBuf buf) {

        String name = buf.readUtf();

        return new RenameClanPacket(name);
    }

    public static void encode(RenameClanPacket packet, FriendlyByteBuf buf) {
        buf.writeUtf(packet.name);

    }

    public static void handle(RenameClanPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {

            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            ServerLevel level = player.serverLevel().getServer().overworld();
            ClanData data = ClanData.get(level);



            ClanData.Clan clan = data.getClan(player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                    .map(WCEPlayerData::getCurrentClanUUID).orElse(ClanData.EMPTY_UUID));

            if (clan != null) {
                if (!data.canManage(clan, player.getUUID())) {
                    Component error = Component.translatable("clan.no_permissions");
                    ModPackets.sendToPlayer(new DisplayClanMenuMessage(error, true), player);
                    return;
                }


                if (packet.name.isEmpty()) return;
                if (data.getClanByName(packet.name) != null) {
                    Component error = Component.translatable("clan.clan_already_exists");
                    ModPackets.sendToPlayer(new DisplayClanMenuMessage(error, true), player);
                    return;
                }

                String oldName = clan.name;

                boolean success = data.renameClan(clan, packet.name, level);
                if (!success) return;

                Component successMsg = Component.translatable("clan.clan_renamed");
                ModPackets.sendToPlayer(new DisplayClanMenuMessage(successMsg, false), player);

                Component clanCreatedLog = Component.translatable("clan.clan_renamed_log",
                        ClanData.logFormattedPlayerName(player),
                        Component.literal(oldName).withStyle(Style.EMPTY.withColor(clan.color)),
                        Component.literal(packet.name).withStyle(Style.EMPTY.withColor(clan.color)));

                data.registerLog(level, clan.clanUUID, clanCreatedLog, ClanObjects.ClanLogEntry.Type.PLAYERS);

                data.setDirty();

                data.updateScreen(clan, player);

            } else {
                player.sendSystemMessage(Component.translatable("clan.player_not_clan").withStyle(ChatFormatting.GRAY));
            }


        });

        ctx.get().setPacketHandled(true);
    }
}

