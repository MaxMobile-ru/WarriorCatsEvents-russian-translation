package net.snowteb.warriorcats_events.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.snowteb.warriorcats_events.clan.WCEPlayerData;
import net.snowteb.warriorcats_events.clan.WCEPlayerDataProvider;
import net.snowteb.warriorcats_events.diseases.Diseaseable;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.s2c.clan.S2CSyncClanDataPacket;
import net.snowteb.warriorcats_events.network.packet.s2c.others.OpenPlayerCatDataScreenPacket;
import net.snowteb.warriorcats_events.network.packet.s2c.others.SyncDiseasesPacket;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class WhiskersItem extends Item {

    private String tooltipKey = "";

    public WhiskersItem(Properties properties, String tooltipKey) {
        super(properties);
        this.tooltipKey = tooltipKey;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack pStack, Player pPlayer, LivingEntity pInteractionTarget, InteractionHand pUsedHand) {

        if (pInteractionTarget instanceof Player target) {
            if (!pPlayer.level().isClientSide()) {

                String name = target.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                        .map(WCEPlayerData::getMorphName).orElse("Unnamed");
                String clanName = target.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                        .map(cap -> cap.getClanName(pPlayer.level())).orElse("No clan");
                String gender = target.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                        .map(WCEPlayerData::getGenderText).orElse("Unspecified");

                String mateName = (target.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                        .map(WCEPlayerData::getMateName).orElse(Component.literal("No mate"))).getString();

                WCEPlayerData.Age age = target.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                        .map(WCEPlayerData::getMorphAge).orElse(WCEPlayerData.Age.ADULT);

                int targetKitCooldown = target.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                        .map(WCEPlayerData::getPlayerKitsCooldown).orElse(1);

                int myKitCooldown = pPlayer.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                        .map(WCEPlayerData::getPlayerKitsCooldown).orElse(1);

                String bio = (target.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                        .map(WCEPlayerData::getCharacterBio).orElse(""));

                WCEPlayerData.PackedData targetData =
                        new WCEPlayerData.PackedData(name, clanName, gender, mateName, age, targetKitCooldown, bio);

                UUID targetUUID = target.getUUID();

                if (target instanceof Diseaseable<?> diseaseable) {
                    if (pPlayer instanceof ServerPlayer sPlayer) {
                        ModPackets.sendToPlayer(new SyncDiseasesPacket(target.getId(), diseaseable.diseaseData()), sPlayer);
                    }
                }

                pPlayer.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA).ifPresent(data -> {
                    ModPackets.sendToPlayer(new S2CSyncClanDataPacket(data), (ServerPlayer) pPlayer);
                });

                ModPackets.sendToPlayer(new OpenPlayerCatDataScreenPacket(targetData, targetUUID, myKitCooldown, false), (ServerPlayer) pPlayer);

            }

            return InteractionResult.SUCCESS;
        }


        return super.interactLivingEntity(pStack, pPlayer, pInteractionTarget, pUsedHand);
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (pIsSelected) return;
        if (pEntity.tickCount % 400 != 0) return;
        if (pStack.getDamageValue() <= 0) return;

        pStack.setDamageValue(pStack.getDamageValue() - 1);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        String raw = Component.translatable(this.tooltipKey).getString();

        String[] lines = raw.split("\\\\n");

        for (String line : lines) {
            tooltip.add(Component.literal(line).withStyle(ChatFormatting.GRAY));
        }

        if (InventoryScreen.hasShiftDown()) {
            Component shiftRightClick = Component.translatable("generic.shift_right_click").append(" ");
            Component rightClick = Component.translatable("generic.right_click").append(" ");

            tooltip.add(Component.empty());
            tooltip.add(rightClick.copy().append(Component.translatable("item.warriorcats_events.whiskers.tip1").withStyle(ChatFormatting.GRAY)));
            tooltip.add(shiftRightClick.copy().append(Component.translatable("item.warriorcats_events.whiskers.tip2").withStyle(ChatFormatting.GRAY)));
            tooltip.add(rightClick.copy().append(Component.translatable("item.warriorcats_events.whiskers.tip3").withStyle(ChatFormatting.GRAY)));
            tooltip.add(rightClick.copy().append(Component.translatable("item.warriorcats_events.whiskers.tip4").withStyle(ChatFormatting.GRAY)));

        } else {
            tooltip.add(Component.empty());
            tooltip.add((Component.translatable("item.warriorcats_events.claws.display_tips").withStyle(ChatFormatting.DARK_PURPLE)));
        }
    }

}
