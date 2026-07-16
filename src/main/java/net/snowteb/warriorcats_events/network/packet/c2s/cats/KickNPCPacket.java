package net.snowteb.warriorcats_events.network.packet.c2s.cats;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.clan.ClanData;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;

import java.util.UUID;
import java.util.function.Supplier;

public class KickNPCPacket {
    private final UUID catUUID;
    private final UUID clanUUID;

    public KickNPCPacket(UUID catUUID, UUID clanUUID) {
        this.catUUID = catUUID;
        this.clanUUID = clanUUID;
    }

    public static void encode(KickNPCPacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.catUUID);
        buf.writeUUID(msg.clanUUID);
    }

    public static KickNPCPacket decode(FriendlyByteBuf buf) {
        return new KickNPCPacket(buf.readUUID(),  buf.readUUID());
    }

    public static void handle(KickNPCPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            ServerLevel level = player.serverLevel();

            ClanData data = ClanData.get(level.getServer().overworld());
            ClanData.Clan clan = data.getClan(msg.clanUUID);

            ClanData.ClanCat clanCat = null;

            if (clan != null) {
                if (!data.canManage(clan, player.getUUID())) {
                    player.sendSystemMessage(Component.translatable("clan.no_permissions")
                            .withStyle(ChatFormatting.RED));
                    return;
                }

                clanCat = clan.clanCats.remove(msg.catUUID);
            }

            if (level.getEntity(msg.catUUID) instanceof WCatEntity cat) {
                cat.setRank(WCatEntity.Rank.NONE);
                cat.sendRankMessage(player);
            }


            if (clanCat != null) {
                Component message = Component.translatable("clan.player_removed",
                        clanCat.catName,
                        Component.literal(clan.name).withStyle(Style.EMPTY.withColor(clan.color)));

                player.sendSystemMessage(message);
                data.registerLog(level, clan.clanUUID, message);
            }

        });
        ctx.get().setPacketHandled(true);
    }
}
