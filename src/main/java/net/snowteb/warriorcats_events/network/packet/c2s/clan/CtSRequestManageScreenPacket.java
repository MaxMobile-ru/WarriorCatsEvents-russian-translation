package net.snowteb.warriorcats_events.network.packet.c2s.clan;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.clan.ClanData;
import net.snowteb.warriorcats_events.clan.WCEPlayerData;
import net.snowteb.warriorcats_events.clan.WCEPlayerDataProvider;
import net.snowteb.warriorcats_events.client.ClanInfo;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCGenetics;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.s2c.clan.S2CManageClanPacket;

import java.util.*;
import java.util.function.Supplier;

public class CtSRequestManageScreenPacket {

    private final UUID clanuuid;

    public CtSRequestManageScreenPacket(UUID clanuuid) {
        this.clanuuid = clanuuid;
    }

    public static CtSRequestManageScreenPacket decode(FriendlyByteBuf buf) {
        UUID  clanuuid = buf.readUUID();
        return new CtSRequestManageScreenPacket(clanuuid);
    }

    public static void encode(CtSRequestManageScreenPacket packet, FriendlyByteBuf buf) {
        buf.writeUUID(packet.clanuuid);
    }

    public static void handle(CtSRequestManageScreenPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {

            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            ServerLevel level = player.serverLevel().getServer().overworld();
            ClanData data = ClanData.get(level);

            ClanData.Clan clan = data.getClan(packet.clanuuid);

            if (clan != null) {
                if (!data.canManage(clan, player.getUUID())) return;

                Map<UUID, ClanInfo.Member> playersInClan = new HashMap<>();
                for (UUID uuid : clan.members.keySet()) {
                    ServerPlayer clanMember = player.serverLevel().getServer()
                            .getPlayerList().getPlayer(uuid);

                    String morphName = data.playerMorphNames.getOrDefault(uuid, "Unknown");
                    WCGenetics.PackedGeneticData morphData = data.playerMorphData.getOrDefault(uuid, WCGenetics.PackedGeneticData.empty());
                    String rank = String.valueOf(clan.members.get(uuid));
                    String perms = String.valueOf(clan.memberPerms.get(uuid));
                    String age = "Undefined";
                    boolean isOnline = false;

                    if (clanMember != null) {
                        age = String.valueOf(clanMember.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                                .map(WCEPlayerData::getMorphAge).orElse(WCEPlayerData.Age.ADULT));
                        isOnline = true;
                    }

                    ClanInfo.Member member = new ClanInfo.Member(uuid, morphName, rank, perms, age, isOnline,
                            morphData.genetics, morphData.variants, morphData.chimerasGenetics, morphData.chimeraVariants,
                            morphData.onGeneticalSkin, morphData.morphSkin);

                    playersInClan.put(uuid, member);
                }

                ClanInfo clanInfo = new ClanInfo(clan.clanUUID, clan.name, clan.color, clan.leaderName, clan.clanBioSentence,
                        clan.members.size(), playersInClan, clan.clanSymbolIndex);

                ModPackets.sendToPlayer(new S2CManageClanPacket(clanInfo), player);

            } else {
                player.sendSystemMessage(Component.translatable("clan.player_not_clan").withStyle(ChatFormatting.GRAY));
            }

        });

        ctx.get().setPacketHandled(true);
    }


}

