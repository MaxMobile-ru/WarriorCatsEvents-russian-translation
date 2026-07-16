package net.snowteb.warriorcats_events.network.packet.c2s.clan;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.clan.ClanData;
import net.snowteb.warriorcats_events.clan.WCEPlayerData;
import net.snowteb.warriorcats_events.clan.WCEPlayerDataProvider;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.s2c.clan.S2CSyncClanDataPacket;

import java.util.UUID;
import java.util.function.Supplier;

public class C2SRegisterClanPacket {

    private final int color;
    private final String  name;
    private final String  clanSentence;
    private final int clanSymbolIndex;

    public C2SRegisterClanPacket(int color, String name, String clanSentence, int clanSymbolIndex) {
        this.color = color;
        this.name = name;
        this.clanSentence = clanSentence;
        this.clanSymbolIndex = clanSymbolIndex;
    }

    public static C2SRegisterClanPacket decode(FriendlyByteBuf buf) {
        int color = buf.readInt();
        String name = buf.readUtf();
        String clanSentence = buf.readUtf();
        int clanSymbolIndex = buf.readInt();
        return new C2SRegisterClanPacket(color, name,  clanSentence,  clanSymbolIndex);
    }

    public static void encode(C2SRegisterClanPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.color);
        buf.writeUtf(packet.name);
        buf.writeUtf(packet.clanSentence);
        buf.writeInt(packet.clanSymbolIndex);
    }

    public static void handle(C2SRegisterClanPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {

            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            ServerLevel level = player.serverLevel().getServer().overworld();
            ClanData data = ClanData.get(level);

            if (packet.name.isEmpty()) return;
            if (data.getClanByName(packet.name) != null) {
                player.sendSystemMessage(Component.translatable("clan.clan_already_exists").withStyle(ChatFormatting.YELLOW));
                return;
            }

            UUID leaderUUID = player.getUUID();
            String leaderName = player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                    .map(WCEPlayerData::getMorphName).orElse(player.getName().getString());

            String finalClanSentence = "";
            if (!packet.clanSentence.isEmpty()) {
                finalClanSentence = "\"" + packet.clanSentence + "\"";
            }

            ClanData.Clan clan = data.createClan(packet.name, packet.color, leaderUUID, leaderName, finalClanSentence, packet.clanSymbolIndex);
            data.syncTerritoriesToClients(level);

            player.sendSystemMessage(Component.translatable("clan.clan_succesfully_created").withStyle(ChatFormatting.GRAY));

            player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA).ifPresent(cap -> {
                cap.setCurrentClanUUID(clan.clanUUID);
                cap.setClanName(clan.name);

                ModPackets.sendToPlayer(new S2CSyncClanDataPacket(cap), player);
            });

            Component clanCreatedLog = Component.translatable("clan.player_founded_clan",
                    ClanData.logFormattedPlayerName(player),
                    Component.literal(clan.name).withStyle(Style.EMPTY.withColor(clan.color)));

            data.registerLog(level, clan.clanUUID, clanCreatedLog);

            level.getAllEntities().forEach(entity -> {
                if (entity instanceof WCatEntity cat) {
                    if (cat.isOwnedBy(player)) {
                        cat.setClanUUID(clan.clanUUID);
                        data.addClanCat(clan, cat);

                        Component catJoinedClanLog = Component.translatable("clan.player_joined_log",
                                cat.hasCustomName() ? cat.getCustomName().copy() : Component.translatable("generic.catname"),
                                Component.literal(clan.name).withStyle(Style.EMPTY.withColor(clan.color)));


                        if (cat.level() instanceof ServerLevel serverLevel) {
                            data.registerLog(serverLevel, clan.clanUUID, catJoinedClanLog);
                        }
                    }
                }
            });

            data.setDirty();
        });

        ctx.get().setPacketHandled(true);
    }
}

