package net.snowteb.warriorcats_events.network.packet.c2s.clan;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.clan.ClanData;
import net.snowteb.warriorcats_events.clan.WCEPlayerData;
import net.snowteb.warriorcats_events.clan.WCEPlayerDataProvider;

import java.util.UUID;
import java.util.function.Supplier;

public class CtSUnclaimTerritory {
    private final ChunkPos chunkPos;

    public CtSUnclaimTerritory(ChunkPos chunkPos) {
        this.chunkPos = chunkPos;
    }

    public static CtSUnclaimTerritory decode(FriendlyByteBuf buf) {
        int x = buf.readInt();
        int z = buf.readInt();
        ChunkPos chunkPos = new ChunkPos(x, z);
        return new CtSUnclaimTerritory(chunkPos);
    }

    public static void encode(CtSUnclaimTerritory packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.chunkPos.x);
        buf.writeInt(packet.chunkPos.z);
    }

    public static void handle(CtSUnclaimTerritory packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            ServerLevel overworldLevel = player.getServer().overworld();

            UUID uuid = player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                    .map(WCEPlayerData::getCurrentClanUUID).orElse(ClanData.EMPTY_UUID);

            String morphName = player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                    .map(WCEPlayerData::getMorphName).orElse(player.getGameProfile().getName());

            if (player.level().dimension() != Level.OVERWORLD) {
                player.sendSystemMessage(Component.translatable("territory.not_available_in_dimensions").withStyle(ChatFormatting.RED));
                return;
            }

            if (uuid.equals(ClanData.EMPTY_UUID)) {
                player.sendSystemMessage(Component.translatable("clan.player_not_clan").withStyle(ChatFormatting.RED));
                return;
            }

            ClanData data = ClanData.get(overworldLevel);
            ClanData.Clan clan = data.getClan(uuid);
            if (clan == null) {
                player.sendSystemMessage(Component.literal("Invalid clan provided.").withStyle(ChatFormatting.RED));
                return;
            }

            ChunkPos currentPosition = packet.chunkPos;

            if (!data.canManage(clan, player.getUUID())) {
                player.sendSystemMessage(Component.translatable("clan.no_permissions").withStyle(ChatFormatting.RED));
                return;
            }

            if (!clan.claimedTerritory.containsKey(currentPosition)) {
                player.sendSystemMessage(Component.translatable("territory.not_claimed").withStyle(ChatFormatting.RED));
                return;
            }

            Component log = Component.translatable("territory.territory_unclaimed_log",
                    ClanData.logFormattedPlayerName(player),
                    Component.literal(clan.claimedTerritory.get(currentPosition).name).withStyle(ChatFormatting.LIGHT_PURPLE),
                    Component.literal(
                            String.format("X=%d, Z=%d", currentPosition.x, currentPosition.z)
                    ).withStyle(ChatFormatting.AQUA));


            boolean result = data.unclaimChunk(clan.clanUUID, currentPosition, overworldLevel);
            if (result) {
                data.registerLog(overworldLevel, clan.clanUUID, log);
                player.sendSystemMessage(Component.translatable("territory.success_unclaimed").withStyle(ChatFormatting.GREEN));
                data.syncTerritoriesToClients(overworldLevel);
            } else {
                player.sendSystemMessage(Component.literal("Territory couldn't be accessed.").withStyle(ChatFormatting.RED));
            }

        });

        ctx.get().setPacketHandled(true);
    }

}
