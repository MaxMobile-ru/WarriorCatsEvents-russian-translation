package net.snowteb.warriorcats_events.managers;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.snowteb.warriorcats_events.clan.ClanData;
import net.snowteb.warriorcats_events.clanplayerdata.WCEPlayerData;
import net.snowteb.warriorcats_events.clanplayerdata.WCEPlayerDataProvider;
import net.snowteb.warriorcats_events.util.CuteTextUtils;

import java.util.UUID;

public class MorphNameUtils {

    public static Component getChatMorphName(ServerPlayer player) {
        String morphName = player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                .map(WCEPlayerData::getMorphName).orElse(player.getGameProfile().getName());

        UUID clanUUID = player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                .map(WCEPlayerData::getCurrentClanUUID).orElse(ClanData.EMPTY_UUID);

        String age = String.valueOf(player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                .map(WCEPlayerData::getMorphAge).orElse(WCEPlayerData.Age.ADULT));

        boolean useFancyFont = player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                .map(WCEPlayerData::isUsingFancyFont).orElse(false);

        ClanData data = ClanData.get(player.getServer().overworld());
        ClanData.Clan clan = data.getClan(clanUUID);
        int color = 0xFFFFFF;
        String clanName = "No clan";
        String rank = "No rank";

        if (clan != null) {
            color = clan.color;
            clanName = clan.name;
            rank = clan.members.get(player.getUUID()).getRank().getString();
        }

        Component finalMorphName1 = Component.empty()
                .append(Component.literal(morphName).withStyle(Style.EMPTY.withColor(color)))
                .append(Component.literal(" ").withStyle(Style.EMPTY.withColor(color)));

        Component hoverText = Component.empty()
                .append(Component.literal(morphName).withStyle(ChatFormatting.GOLD))
                .append(Component.literal("\n"))
                .append(Component.literal("From: "))
                .append(Component.literal(clanName).withStyle(Style.EMPTY.withColor(color)))
                .append(Component.literal("\n"))
                .append(Component.literal("Rank: "))
                .append(Component.literal(rank))
                .append(Component.literal("\n"))
                .append(Component.literal("Age: "))
                .append(Component.literal(age));

        Component finalMorphName2 = finalMorphName1.copy().withStyle(style ->
                style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText))
        );


        int grayColor = 0x888888;

        Component newName = Component.empty()
                .append(finalMorphName2.copy())
                .append(Component.literal("").withStyle(Style.EMPTY.withColor(grayColor)))
                .append(Component.literal("| ").withStyle(ChatFormatting.WHITE))
                .append(CuteTextUtils.reformatToSmallText(player, useFancyFont).copy().withStyle(Style.EMPTY.withColor(grayColor)))
                .append(Component.literal("").withStyle(Style.EMPTY.withColor(grayColor)));

        return newName;
    }

}
