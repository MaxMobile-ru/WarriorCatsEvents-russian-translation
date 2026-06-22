package net.snowteb.warriorcats_events.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.snowteb.warriorcats_events.clan.ClanData;
import net.snowteb.warriorcats_events.clan.WCEPlayerData;
import net.snowteb.warriorcats_events.clan.WCEPlayerDataProvider;
import net.snowteb.warriorcats_events.managers.ClanInviteManager;

import java.util.UUID;

public class OpClanCommands {

    private static final SuggestionProvider<CommandSourceStack> CLAN_SUGGESTIONS =
            (ctx, builder) -> {
                ServerLevel level = ctx.getSource().getLevel();
                ClanData data = ClanData.get(level);
                return SharedSuggestionProvider.suggest(data.clans.values().stream()
                        .map(clan -> clan.normalizedName).toList(), builder);
            };


    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("wce")
                        .then(Commands.literal("clan")
                                .then(Commands.literal("adminForceDelete").requires(source -> source.hasPermission(3))
                                        .then(Commands.argument("clan", StringArgumentType.word())
                                                .suggests(CLAN_SUGGESTIONS)
                                                .executes(ctx ->
                                                        method(
                                                                ctx.getSource(),
                                                                StringArgumentType.getString(ctx, "clan")
                                                        )
                                                )
                                        )
                                )
                        )

        );


        dispatcher.register(
                Commands.literal("wce")
                        .then(Commands.literal("clan")
                                .then(Commands.literal("adminInvite")
                                        .requires(source -> source.hasPermission(3))
                                        .then(Commands.argument("clan", StringArgumentType.word())
                                                .suggests(CLAN_SUGGESTIONS)
                                                .then(Commands.argument("player", EntityArgument.player())
                                                        .executes(ctx ->
                                                                method2(
                                                                        ctx.getSource(),
                                                                        StringArgumentType.getString(ctx, "clan"),
                                                                        EntityArgument.getPlayer(ctx, "player")
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
        );
    }


    private static int method(CommandSourceStack source, String clanName) throws CommandSyntaxException {

        ServerLevel level = source.getLevel();
        ClanData data = ClanData.get(level);

        ClanData.Clan targetClan = data.getClanByName(clanName);

        if (targetClan == null) {
            source.sendFailure(Component.literal("The provided clan does not exist.").withStyle(ChatFormatting.RED));
            return 0;
        }

        int color = targetClan.color;
        String clanRealName = targetClan.name;

        data.deleteClan(level, targetClan.clanUUID);

        source.sendSystemMessage(
                Component.empty()
                                .append(Component.literal("Clan successfully deleted: ").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal(clanRealName).withStyle(Style.EMPTY.withColor(color))));


        return 1;
    }


    private static int method2(CommandSourceStack source, String clanName, ServerPlayer invitedPlayer) throws CommandSyntaxException {

        ServerLevel level = source.getLevel();
        ClanData data = ClanData.get(level);

        ClanData.Clan clan = data.getClanByName(clanName);

        if (clan == null) {
            source.sendFailure(Component.literal("The provided clan does not exist.").withStyle(ChatFormatting.RED));
            return 0;
        }

        ServerPlayer sPlayer = source.getPlayerOrException();
        if (invitedPlayer == null) return 0;

        String hostMorphName = sPlayer.getName().getString();

        String invitedMorphName = invitedPlayer.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                .map(WCEPlayerData::getMorphName).orElse("");

        UUID currentClanId = invitedPlayer.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                .map(WCEPlayerData::getCurrentClanUUID).orElse(ClanData.EMPTY_UUID);

        if (invitedPlayer == sPlayer) {
            sPlayer.sendSystemMessage(Component.literal("You can't invite yourself to a clan.").withStyle(ChatFormatting.GRAY));
            return 0;
        }

        if (ClanInviteManager.getInvite(invitedPlayer) != null) {
            sPlayer.sendSystemMessage(Component.literal("The target already has an invite pending.").withStyle(ChatFormatting.YELLOW));
            return 0;
        }

        if (!currentClanId.equals(ClanData.EMPTY_UUID)) {
            if (data.getClan(currentClanId) ==  null) {
                sPlayer.sendSystemMessage(Component.literal("The target is in a clan that doesn't exist. Resetting their clan info...").withStyle(ChatFormatting.GRAY));

                invitedPlayer.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA).ifPresent(cap -> {
                    cap.setCurrentClanUUID(ClanData.EMPTY_UUID);
                });
            } else {
                sPlayer.sendSystemMessage(Component.literal("The target is already in a clan.").withStyle(ChatFormatting.YELLOW));
            }
        }

        sPlayer.sendSystemMessage(
                Component.empty()
                        .append(Component.literal("You have invited "))
                        .append(Component.literal(invitedMorphName).withStyle(ChatFormatting.GOLD))
                        .append(" to ")
                        .append(Component.literal(clan.name).withStyle(Style.EMPTY.withColor(clan.color))
                        ));

        invitedPlayer.sendSystemMessage(
                Component.empty()
                        .append(Component.literal(hostMorphName).withStyle(ChatFormatting.AQUA))
                        .append(" has invited you to ")
                        .append(clan.name).withStyle(Style.EMPTY.withColor(clan.color))
        );

        ClanInviteManager.invite(invitedPlayer, clan.clanUUID, sPlayer);

        invitedPlayer.sendSystemMessage(
                Component.empty()
                        .append(
                                Component.literal("[ACCEPT]")
                                        .withStyle(style -> style
                                                .withColor(ChatFormatting.GREEN)
                                                .withItalic(true)
                                                .withUnderlined(true)
                                                .withClickEvent(
                                                        new ClickEvent(
                                                                ClickEvent.Action.RUN_COMMAND,
                                                                "/wce clan invite accept"
                                                        )
                                                )
                                                .withHoverEvent(
                                                        new HoverEvent(
                                                                HoverEvent.Action.SHOW_TEXT,
                                                                Component.literal("Accept the clan invite")
                                                                        .withStyle(ChatFormatting.GREEN)
                                                        )
                                                )
                                        )
                        )

                        .append("       ")

                        .append(
                                Component.literal("[DENY]")
                                        .withStyle(style -> style
                                                .withColor(ChatFormatting.RED)
                                                .withItalic(true)
                                                .withUnderlined(true)
                                                .withClickEvent(
                                                        new ClickEvent(
                                                                ClickEvent.Action.RUN_COMMAND,
                                                                "/wce clan invite deny"
                                                        )
                                                )
                                                .withHoverEvent(
                                                        new HoverEvent(
                                                                HoverEvent.Action.SHOW_TEXT,
                                                                Component.literal("Decline the clan invite")
                                                                        .withStyle(ChatFormatting.RED)
                                                        )
                                                )
                                        )
                        )
        );

        return 1;
    }

}
