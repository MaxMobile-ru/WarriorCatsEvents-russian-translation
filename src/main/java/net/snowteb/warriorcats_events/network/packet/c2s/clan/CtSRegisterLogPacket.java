package net.snowteb.warriorcats_events.network.packet.c2s.clan;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.clan.ClanData;
import net.snowteb.warriorcats_events.commands.WCECommandHandles;

import java.util.*;

public class CtSRegisterLogPacket implements CustomPacketPayload {

    private final String message;
    private final UUID clanuuid;

    public CtSRegisterLogPacket(String message, UUID clanuuid) {
        this.message = message;
        this.clanuuid = clanuuid;
    }

    public static CtSRegisterLogPacket decode(FriendlyByteBuf buf) {
        String message = buf.readUtf(140);
        UUID  clanuuid = buf.readUUID();
        return new CtSRegisterLogPacket(message, clanuuid);
    }

    public static void encode(CtSRegisterLogPacket packet, FriendlyByteBuf buf) {
        buf.writeUtf(packet.message);
        buf.writeUUID(packet.clanuuid);
    }

    public static void handle(CtSRegisterLogPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {

            ServerPlayer player = (ServerPlayer) ctx.player();

            ServerLevel level = player.serverLevel().getServer().overworld();
            ClanData data = ClanData.get(level);

            ClanData.Clan clan = data.getClan(packet.clanuuid);

            if (clan != null) {
                if (!data.canManage(clan, player.getUUID())) return;

                Component finalMessage = Component.empty()
                                .append(Component.literal("[").withStyle(ChatFormatting.DARK_GRAY))
                                .append(Component.literal(player.getDisplayName().getString()).withStyle(ChatFormatting.DARK_GRAY))
                                .append(Component.literal("] ").withStyle(ChatFormatting.DARK_GRAY))
                        .append(parseColoredText(packet.message, data).copy());

                data.registerLog(player.serverLevel().getServer().overworld(), clan.clanUUID, finalMessage);

                player.displayClientMessage(Component.translatable("clan.log_registered").withStyle(ChatFormatting.GREEN), false);


                WCECommandHandles.clanList(player, true, false);

//                ModPackets.sendToPlayer(new OpenSpecificClanScreen(clan.name, clan.clanUUID), player);

            } else {
                player.sendSystemMessage(Component.translatable("clan.player_not_clan").withStyle(ChatFormatting.GRAY));
            }

        });

    }

    public static Component parseColoredText(String text, ClanData data) {

        MutableComponent result = Component.empty();

        String[] words = text.split("\\s+");

        Collection<String> morphNames = data.playerMorphNames.values();
        Set<String> clanCatNames = new HashSet<>();

        for (ClanData.Clan clan : data.getAllClans()) {
            for (UUID uuid : clan.clanCats.keySet()) {
                clanCatNames.add(clan.clanCats.get(uuid).catName
                                .getString()
                        .replace(" ♂", "")
                        .replace(" ♀", "")
                        .replace(" ", "")
                );

            }
        }

        Set<String> clanNameSet = new HashSet<>();
        for (ClanData.Clan clan : data.clans.values()) {
            clanNameSet.add(clan.name);
        }

        for (int i = 0; i < words.length; i++) {

            String rawWord = words[i];

            String cleanWord = rawWord.replaceAll("^[^a-zA-Z0-9]+|[^a-zA-Z0-9]+$", "");

            if (cleanWord.endsWith("'s")) {
                cleanWord = cleanWord.substring(0, cleanWord.length() - 2);
            }

            ChatFormatting color = ChatFormatting.WHITE;

            if (clanNameSet.contains(cleanWord)) {
                color = ChatFormatting.GOLD;
            } else if (morphNames.contains(cleanWord)) {
                color = ChatFormatting.AQUA;
            }
            else if (clanCatNames.contains(cleanWord)) {
                color = ChatFormatting.GREEN;
            }

            MutableComponent part = Component.literal(rawWord).withStyle(color);
            result.append(part);

            if (i < words.length - 1) {
                result.append(Component.literal(" "));
            }
        }

        return result;
    }

    public static final Type<CtSRegisterLogPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "register_log"));

    public static final StreamCodec<FriendlyByteBuf, CtSRegisterLogPacket> CODEC =
            StreamCodec.of(
                    (buf, pkt) -> encode(pkt, buf),
                    buf -> decode(buf)
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

