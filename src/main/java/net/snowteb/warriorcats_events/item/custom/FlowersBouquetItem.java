package net.snowteb.warriorcats_events.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.snowteb.warriorcats_events.clan.ClanData;
import net.snowteb.warriorcats_events.clanplayerdata.WCEPlayerData;
import net.snowteb.warriorcats_events.clanplayerdata.WCEPlayerDataProvider;
import net.snowteb.warriorcats_events.util.ItemWithToolTip;
import net.snowteb.warriorcats_events.managers.PlayerMateRequestManager;

import java.util.UUID;

public class FlowersBouquetItem extends ItemWithToolTip {
    public FlowersBouquetItem(Properties properties, String tooltipKey) {
        super(properties, tooltipKey);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack pStack, Player pPlayer, LivingEntity pInteractionTarget, InteractionHand pUsedHand) {

        if (pInteractionTarget instanceof Player target && pUsedHand == InteractionHand.MAIN_HAND) {
            if (!pPlayer.level().isClientSide()) {

                WCEPlayerData.Age myAge = pPlayer.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                        .map(WCEPlayerData::getMorphAge).orElse(WCEPlayerData.Age.ADULT);
                WCEPlayerData.Age targetAge = target.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                        .map(WCEPlayerData::getMorphAge).orElse(WCEPlayerData.Age.ADULT);

                UUID myMate = pPlayer.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                        .map(WCEPlayerData::getMateUUID).orElse(ClanData.EMPTY_UUID);
                UUID targetMate = target.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                        .map(WCEPlayerData::getMateUUID).orElse(ClanData.EMPTY_UUID);

                String myMorphName = pPlayer.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                        .map(WCEPlayerData::getMorphName).orElse("Unnamed");
                String targetMorphName = target.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                        .map(WCEPlayerData::getMorphName).orElse("Unnamed");

                if (myAge != WCEPlayerData.Age.ADULT) {
                    pPlayer.sendSystemMessage(Component.literal(myMorphName + " is not old enough for this.")
                            .withStyle(ChatFormatting.RED));
                    return InteractionResult.FAIL;
                } else if (targetAge != WCEPlayerData.Age.ADULT) {
                    pPlayer.sendSystemMessage(Component.literal(targetMorphName + " is not old enough for this.")
                            .withStyle(ChatFormatting.RED));
                    return InteractionResult.FAIL;
                }

                if (!myMate.equals(ClanData.EMPTY_UUID)) {
                    pPlayer.sendSystemMessage(Component.literal(myMorphName + " already has a mate. Try using /wce mate divorce")
                            .withStyle(ChatFormatting.RED));
                    return InteractionResult.FAIL;
                } else if (!targetMate.equals(ClanData.EMPTY_UUID)) {
                    pPlayer.sendSystemMessage(Component.literal(targetMorphName + " already has a mate.")
                            .withStyle(ChatFormatting.RED));
                    return InteractionResult.FAIL;
                }

                PlayerMateRequestManager.request((ServerPlayer) target, (ServerPlayer) pPlayer);

                pPlayer.getItemInHand(InteractionHand.MAIN_HAND).shrink(1);

                pPlayer.sendSystemMessage(
                        Component.empty()
                                .append("Mate request sent to ")
                                .append(Component.literal(targetMorphName).withStyle(ChatFormatting.AQUA)
                                        .append(Component.literal( "(" + target.getName().getString() + ")").withStyle(ChatFormatting.GRAY))
                                ));

                target.sendSystemMessage(
                        Component.empty()
                                .append(Component.literal(myMorphName).withStyle(ChatFormatting.AQUA)
                                        .append(Component.literal( "(" + pPlayer.getName().getString() + ")").withStyle(ChatFormatting.GRAY))
                                        .append(" wants you to be their mate! ")
                                ));

                target.sendSystemMessage(
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
                                                                        "/wce mate accept"
                                                                )
                                                        )
                                                        .withHoverEvent(
                                                                new HoverEvent(
                                                                        HoverEvent.Action.SHOW_TEXT,
                                                                        Component.literal("Accept")
                                                                                .withStyle(ChatFormatting.GREEN)
                                                                )
                                                        )
                                                )
                                )

                                .append("       ")

                                .append(
                                        Component.literal("[DECLINE]")
                                                .withStyle(style -> style
                                                        .withColor(ChatFormatting.RED)
                                                        .withItalic(true)
                                                        .withUnderlined(true)
                                                        .withClickEvent(
                                                                new ClickEvent(
                                                                        ClickEvent.Action.RUN_COMMAND,
                                                                        "/wce mate decline"
                                                                )
                                                        )
                                                        .withHoverEvent(
                                                                new HoverEvent(
                                                                        HoverEvent.Action.SHOW_TEXT,
                                                                        Component.literal("Decline")
                                                                                .withStyle(ChatFormatting.RED)
                                                                )
                                                        )
                                                )
                                )
                );

            }

            return InteractionResult.SUCCESS;
        }

        return super.interactLivingEntity(pStack, pPlayer, pInteractionTarget, pUsedHand);
    }
}
