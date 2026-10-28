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
import net.snowteb.warriorcats_events.clan.ClanObjects;
import net.snowteb.warriorcats_events.client.ClientClans;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.s2c.clan.DisplayClanMenuMessage;
import net.snowteb.warriorcats_events.network.packet.s2c.clan.UpdateClanEntryPacket;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class KickNPCPacket {
    private final UUID catUUID;
    private final UUID clanUUID;
    private final String actionKey;

    public KickNPCPacket(UUID catUUID, UUID clanUUID, String actionKey) {
        this.catUUID = catUUID;
        this.clanUUID = clanUUID;
        this.actionKey = actionKey;
    }

    public static void encode(KickNPCPacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.catUUID);
        buf.writeUUID(msg.clanUUID);
        buf.writeUtf(msg.actionKey);
    }

    public static KickNPCPacket decode(FriendlyByteBuf buf) {
        return new KickNPCPacket(buf.readUUID(),  buf.readUUID(), buf.readUtf());
    }

    public static void handle(KickNPCPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            ServerLevel level = player.serverLevel();

            ClanData data = ClanData.get(level.getServer().overworld());
            ClanData.Clan clan = data.getClan(msg.clanUUID);

            if (msg.actionKey.equals("kick")) {
                ClanData.ClanCat clanCat = null;

                if (clan != null) {
                    if (!data.canManage(clan, player.getUUID())) {
                        ModPackets.sendToPlayer(new DisplayClanMenuMessage(Component.translatable("clan.no_permissions"), true), player);
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

                    ModPackets.sendToPlayer(new DisplayClanMenuMessage(message, false), player);
                    data.registerLog(level, clan.clanUUID, message, ClanObjects.ClanLogEntry.Type.OTHERS);

                    data.updateScreen(clan, player);
                }
            } else if (msg.actionKey.equals("disown")) {
                WCatEntity cat = (WCatEntity) level.getEntity(msg.catUUID);
                if (cat == null) {
                    ModPackets.sendToPlayer(new DisplayClanMenuMessage(Component.translatable("argument.entity.notfound.entity"), true), player);
                    return;
                }
                if (cat.getOwner() != player) {
                    ModPackets.sendToPlayer(new DisplayClanMenuMessage(Component.translatable("generic.cat_not_owned"), true), player);
                    return;
                }

                data.removeClanCatFromClan(clan.clanUUID, cat);
                cat.disown();
                ModPackets.sendToPlayer(new DisplayClanMenuMessage(Component.translatable("generic.cat_disowned",
                        cat.getName()),
                        false), player);
                data.updateScreen(clan, player);

            }

        });
        ctx.get().setPacketHandled(true);
    }
}
