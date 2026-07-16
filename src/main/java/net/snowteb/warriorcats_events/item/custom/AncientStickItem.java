package net.snowteb.warriorcats_events.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.snowteb.warriorcats_events.entity.custom.EagleEntity;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.s2c.cats.OpenAncientStickScreenPacket;

import javax.annotation.Nullable;
import java.util.List;

public class AncientStickItem extends Item {

    public AncientStickItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {

        if (!pLevel.isClientSide && pUsedHand == InteractionHand.MAIN_HAND) {

            List<Integer> catIDs = pLevel.getEntitiesOfClass(
                    WCatEntity.class,
                    pPlayer.getBoundingBox().inflate(45),
                    cat -> cat.isAlive() && cat.getOwner() == pPlayer
            ).stream().map(Entity::getId).toList();

            List<Integer> eagleIDs = pLevel.getEntitiesOfClass(
                    EagleEntity.class,
                    pPlayer.getBoundingBox().inflate(60),
                    eagle -> eagle.isAlive() && eagle.getOwner() == pPlayer
            ).stream().map(Entity::getId).toList();


            if (pPlayer instanceof ServerPlayer sPlayer) {
                ModPackets.sendToPlayer(new OpenAncientStickScreenPacket(catIDs, eagleIDs), sPlayer);
            }

        }
        return InteractionResultHolder.sidedSuccess(pPlayer.getItemInHand(pUsedHand), pLevel.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.warriorcats_events.ancient_stick.tip").withStyle(ChatFormatting.GRAY));
    }

}
