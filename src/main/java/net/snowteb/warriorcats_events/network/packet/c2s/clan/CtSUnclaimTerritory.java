package net.snowteb.warriorcats_events.network.packet.c2s.clan;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.attachments.ModAttachments;
import net.snowteb.warriorcats_events.clan.ClanData;

import java.util.UUID;

public class CtSUnclaimTerritory implements CustomPacketPayload {
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

    public static void handle(CtSUnclaimTerritory packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) ctx.player();

            ServerLevel overworldLevel = player.getServer().overworld();

            UUID uuid = player.getData(ModAttachments.PLAYER_WCE_DATA).getCurrentClanUUID();

            String morphName = player.getData(ModAttachments.PLAYER_WCE_DATA).getMorphName();

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
    }

    public static final Type<CtSUnclaimTerritory> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "unclaim_territory"));

    public static final StreamCodec<FriendlyByteBuf, CtSUnclaimTerritory> CODEC =
            StreamCodec.of(
                    (buf, pkt) -> encode(pkt, buf),
                    buf -> decode(buf)
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
