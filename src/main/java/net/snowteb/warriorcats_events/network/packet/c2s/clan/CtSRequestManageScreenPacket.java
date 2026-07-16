package net.snowteb.warriorcats_events.network.packet.c2s.clan;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.attachments.ModAttachments;
import net.snowteb.warriorcats_events.clan.ClanData;
import net.snowteb.warriorcats_events.client.ClanInfo;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCGenetics;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.s2c.clan.S2CManageClanPacket;

import java.util.*;

public class CtSRequestManageScreenPacket implements CustomPacketPayload {

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

    public static void handle(CtSRequestManageScreenPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {

            ServerPlayer player = (ServerPlayer) ctx.player();

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
                        age = String.valueOf(clanMember.getData(ModAttachments.PLAYER_WCE_DATA).getMorphAge());
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
    }


    public static final Type<CtSRequestManageScreenPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "request_manage_screen"));

    public static final StreamCodec<FriendlyByteBuf, CtSRequestManageScreenPacket> CODEC =
            StreamCodec.of(
                    (buf, pkt) -> encode(pkt, buf),
                    buf -> decode(buf)
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

