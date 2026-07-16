package net.snowteb.warriorcats_events.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.snowteb.warriorcats_events.client.ClientPacketHandles;
import net.snowteb.warriorcats_events.item.ModItems;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WarriorNametag extends Item {
    public WarriorNametag(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);

        if (pPlayer.isShiftKeyDown() && itemstack.is(ModItems.WARRIOR_NAMETAG.get())) {
            if (pLevel.isClientSide()) {
                ClientPacketHandles.openNametagScreen(itemstack, pUsedHand);
            }

            return InteractionResultHolder.success(pPlayer.getItemInHand(pUsedHand));
        }

        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if (!pStack.hasCustomHoverName()) {
            Component shiftRightClick = Component.translatable("generic.shift_right_click").append(" ");

            pTooltipComponents.add(shiftRightClick.copy().append(Component.translatable("item.warriorcats_events.warrior_nametag.tip").withStyle(ChatFormatting.GRAY)));

            pTooltipComponents.add(Component.empty());
        }
        String raw = Component.translatable("item.warriorcats_events.warrior_nametag.tooltip").getString();

        String[] lines = raw.split("\\\\n");

        for (String line : lines) {
            pTooltipComponents.add(Component.literal(line).withStyle(ChatFormatting.GRAY));
        }
    }
}
