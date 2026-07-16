package net.snowteb.warriorcats_events.network.packet.c2s.cats;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.clan.ClanData;
import net.snowteb.warriorcats_events.clan.WCEPlayerData;
import net.snowteb.warriorcats_events.clan.WCEPlayerDataProvider;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.s2c.cats.StCOpenPatrolScreenPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class CtSRequestPatrolData {
    private final int deputyID;
    private final boolean isValidDeputy;
    public CtSRequestPatrolData(int deputyID, boolean isValidDeputy) {
        this.deputyID = deputyID;
        this.isValidDeputy = isValidDeputy;
    }

    public static CtSRequestPatrolData decode(FriendlyByteBuf buf) {
        int deputyID = buf.readInt();
        boolean isDeputy = buf.readBoolean();
        return new CtSRequestPatrolData(deputyID, isDeputy);
    }

    public static void encode(CtSRequestPatrolData packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.deputyID);
        buf.writeBoolean(packet.isValidDeputy);
    }

    public static void handle(CtSRequestPatrolData msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            ServerLevel level = player.serverLevel();

            UUID clanUUID = player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                    .map(WCEPlayerData::getCurrentClanUUID).orElse(ClanData.EMPTY_UUID);

            if (clanUUID.equals(ClanData.EMPTY_UUID)) {
                player.sendSystemMessage(Component.translatable("clan.player_not_clan").withStyle(ChatFormatting.RED));
                return;
            }

            ClanData data = ClanData.get(level.getServer().overworld());
            ClanData.Clan clan = data.getClan(clanUUID);
            if (clan == null) {
                player.sendSystemMessage(Component.literal("Invalid clan provided.").withStyle(ChatFormatting.RED));
                return;
            }

            List<WCatEntity> catsInRange = player.level().getEntitiesOfClass(WCatEntity.class, player.getBoundingBox().inflate(30D), found -> {
                    return (found.getRank() == WCatEntity.Rank.WARRIOR || found.getRank() == WCatEntity.Rank.DEPUTY || found.getRank() == WCatEntity.Rank.APPRENTICE) &&
                            (found.getHealth() > found.getMaxHealth() / 2) &&
                            !(found.onBorderPatrolFlag || found.onHuntingPatrolFlag || found.returnHomeFlag || found.tellingCatsToPatrol)
                            && (found.isOwnedBy(player) || data.canCommandWarriors(clan, player.getUUID()))
                            && found.getClanUUID().equals(clanUUID)
                            && !found.getClanUUID().equals(ClanData.EMPTY_UUID)
                            && found.getOwnerUUID() != null
                            && found.hasHomePosition();
                });

            List<Integer> idList = new ArrayList<>();
            for (WCatEntity cat : catsInRange) {
                idList.add(cat.getId());
            }

            ModPackets.sendToPlayer(new StCOpenPatrolScreenPacket(idList, clanUUID, msg.deputyID, msg.isValidDeputy), player);

        });
        ctx.get().setPacketHandled(true);
    }

}
