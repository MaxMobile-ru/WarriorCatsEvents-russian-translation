package net.snowteb.warriorcats_events.network.packet.c2s.clan;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.attachments.ModAttachments;
import net.snowteb.warriorcats_events.clan.ClanData;

public class RenameClanPacket implements CustomPacketPayload {

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

    public static void handle(RenameClanPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {

            ServerPlayer player = (ServerPlayer) ctx.player();

            ServerLevel level = player.serverLevel().getServer().overworld();
            ClanData data = ClanData.get(level);



            ClanData.Clan clan = data.getClan(player.getData(ModAttachments.PLAYER_WCE_DATA).getCurrentClanUUID());

            if (clan != null) {
                if (!data.canManage(clan, player.getUUID())) return;


                if (packet.name.isEmpty()) return;
                if (data.getClanByName(packet.name) != null) {
                    player.sendSystemMessage(Component.translatable("clan.clan_already_exists").withStyle(ChatFormatting.YELLOW));
                    return;
                }

                String oldName = clan.name;

                boolean success = data.renameClan(clan, packet.name, level);
                if (!success) return;

                player.sendSystemMessage(Component.translatable("clan.clan_renamed").withStyle(ChatFormatting.GRAY));

                String morphName = player.getData(ModAttachments.PLAYER_WCE_DATA).getMorphName();

                Component clanCreatedLog = Component.translatable("clan.clan_renamed_log",
                        ClanData.logFormattedPlayerName(player),
                        Component.literal(oldName).withStyle(Style.EMPTY.withColor(clan.color)),
                        Component.literal(packet.name).withStyle(Style.EMPTY.withColor(clan.color)));

                data.registerLog(level, clan.clanUUID, clanCreatedLog);

                data.setDirty();

            } else {
                player.sendSystemMessage(Component.translatable("clan.player_not_clan").withStyle(ChatFormatting.GRAY));
            }


        });
    }


    public static final Type<RenameClanPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "rename_clan"));

    public static final StreamCodec<FriendlyByteBuf, RenameClanPacket> CODEC =
            StreamCodec.of(
                    (buf, pkt) -> encode(pkt, buf),
                    buf -> decode(buf)
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

