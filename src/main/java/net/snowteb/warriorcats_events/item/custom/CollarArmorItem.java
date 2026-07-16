package net.snowteb.warriorcats_events.item.custom;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.datacomponents.ModDataComponents;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class CollarArmorItem extends ArmorItem {

    private static final ResourceLocation ARMOR_UUID =
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "collar_armor_armor");

    public CollarArmorItem() {
        super(ArmorMaterials.IRON, Type.CHESTPLATE,
                new Properties().stacksTo(1).durability(380)
                        .attributes(ItemAttributeModifiers.builder().add(Attributes.ARMOR,
                                new AttributeModifier(ARMOR_UUID, 4.0,
                                        AttributeModifier.Operation.ADD_VALUE),
                                EquipmentSlotGroup.CHEST).build()
                        )
        );
    }


    @Override
    public EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EquipmentSlot.CHEST;
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        EquipmentSlot slot = this.getEquipmentSlot();

        ItemStack equipped = player.getItemBySlot(slot);

        if (equipped.isEmpty()) {
            player.setItemSlot(slot, stack.copy());
            stack.shrink(1);
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }

        return InteractionResultHolder.fail(stack);
    }

    @Override
    public @Nullable ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
        return ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png");
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        Style style = Style.EMPTY.withColor(ChatFormatting.DARK_PURPLE).withItalic(true);

        if (stack.getOrDefault(ModDataComponents.HAS_BELL.get(), Boolean.FALSE)) {
            tooltipComponents.add(Component.literal("Bell").withStyle(style));
        }

        if (stack.getOrDefault(ModDataComponents.HAS_SPIKES.get(), Boolean.FALSE)) {
            tooltipComponents.add(Component.literal("Spikes").withStyle(style));
        }

        if (stack.getOrDefault(ModDataComponents.HAS_GLOW.get(), Boolean.FALSE)) {
            tooltipComponents.add(Component.literal("Glow").withStyle(style));
        }
    }

    public boolean hasBell(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.HAS_BELL.get(), Boolean.FALSE);
    }

    public boolean hasSpikes(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.HAS_SPIKES.get(), Boolean.FALSE);
    }

    public boolean hasGlow(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.HAS_GLOW.get(), Boolean.FALSE);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return 15;
    }

}
