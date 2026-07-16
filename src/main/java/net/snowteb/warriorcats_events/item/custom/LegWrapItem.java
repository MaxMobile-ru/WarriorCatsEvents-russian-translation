package net.snowteb.warriorcats_events.item.custom;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.snowteb.warriorcats_events.diseases.kinds.BrokenPaw;
import net.snowteb.warriorcats_events.diseases.DiseaseTypes;
import net.snowteb.warriorcats_events.diseases.Diseaseable;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LegWrapItem extends ArmorItem {

    private String tooltipKey = "";

    public LegWrapItem(String tooltipKey) {
        super(ArmorMaterials.IRON, Type.BOOTS, new Properties().stacksTo(1).durability(240));
        this.tooltipKey = tooltipKey;
    }


    @Override
    public EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EquipmentSlot.FEET;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return ImmutableMultimap.of();
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player instanceof Diseaseable<?> diseaseable) {
            if (level instanceof ServerLevel sLevel) {
                if (diseaseable.getDisease(DiseaseTypes.BROKEN_PAW) instanceof BrokenPaw brokenPaw) {
                    if (!brokenPaw.isBoneWrapped()) {

                        brokenPaw.setWrapBone(true, diseaseable);

                        brokenPaw.onRemoveOrAddWrap(player, sLevel);

                        stack.shrink(1);
                        return InteractionResultHolder.success(stack);
                    } else {
                        player.displayClientMessage(Component.translatable("item.warriorcats_events.leg_wrap.already_wraped").withStyle(ChatFormatting.GRAY), true);
                    }
                } else {
                    player.displayClientMessage(Component.translatable("item.warriorcats_events.leg_wrap.no_broken_bone").withStyle(ChatFormatting.GRAY), true);
                }
            }

        }

        return InteractionResultHolder.fail(stack);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack pStack, Player pPlayer, LivingEntity pInteractionTarget, InteractionHand pUsedHand) {

        if (pInteractionTarget instanceof Diseaseable<?> diseaseable) {
            if (pPlayer.level() instanceof ServerLevel sLevel) {
                if (diseaseable.getDisease(DiseaseTypes.BROKEN_PAW) instanceof BrokenPaw brokenPaw) {
                    if (!brokenPaw.isBoneWrapped()) {

                        brokenPaw.setWrapBone(true, diseaseable);

                        brokenPaw.onRemoveOrAddWrap(pInteractionTarget, sLevel);

                        pStack.shrink(1);
                        return InteractionResult.SUCCESS;
                    } else {
                        pPlayer.displayClientMessage(Component.translatable("item.warriorcats_events.leg_wrap.already_wraped").withStyle(ChatFormatting.GRAY), true);
                    }

                } else {
                    pPlayer.displayClientMessage(Component.translatable("item.warriorcats_events.leg_wrap.no_broken_bone").withStyle(ChatFormatting.GRAY), true);
                }
            }

        }


        return super.interactLivingEntity(pStack, pPlayer, pInteractionTarget, pUsedHand);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return "warriorcats_events:textures/empty.png";
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {

        String raw = Component.translatable(this.tooltipKey).getString();

        String[] lines = raw.split("\\\\n");

        for (String line : lines) {
            pTooltipComponents.add(Component.literal(line).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

}
