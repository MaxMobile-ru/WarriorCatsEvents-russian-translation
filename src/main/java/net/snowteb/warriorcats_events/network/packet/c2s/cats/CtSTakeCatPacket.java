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

import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

public class CtSTakeCatPacket {
    private final int catID;

    public CtSTakeCatPacket(int catID) {
        this.catID = catID;
    }

    public static CtSTakeCatPacket decode(FriendlyByteBuf buf) {
        int catID = buf.readInt();
        return new CtSTakeCatPacket(catID);
    }

    public static void encode(CtSTakeCatPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.catID);
    }

    public static void handle(CtSTakeCatPacket msg, Supplier<NetworkEvent.Context> ctx) {
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

            if (!data.canCommandWarriors(clan, player.getUUID())) {
                player.sendSystemMessage(Component.translatable("clan.member_perms_requiered").withStyle(ChatFormatting.RED));
                return;
            }


            WCatEntity cat = (WCatEntity) level.getEntity(msg.catID);
            if (cat == null) return;

            boolean canAlsoCommand = data.canCommandWarriors(clan, player.getUUID()) && cat.getClanUUID().equals(clanUUID) && !Objects.equals(cat.getOwnerUUID(), player.getUUID());

            if (canAlsoCommand) {

                cat.setOwnerUUID(player.getUUID());

                String morphName = player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                        .map(WCEPlayerData::getMorphName).orElse(player.getName().getString());

                Component message = Component.translatable("clan.player_took_cat",
                        ClanData.logFormattedPlayerName(player),
                        cat.hasCustomName() ? cat.getCustomName().copy() : Component.translatable("generic.catname"));

                cat.registerClanLog(message);


                Component catName = cat.hasCustomName() ? cat.getCustomName() : Component.translatable("generic.catname");

                player.displayClientMessage(Component.translatable("generic.cat_will_now_listen",
                                catName), true);
            }


        });
        ctx.get().setPacketHandled(true);
    }

}
