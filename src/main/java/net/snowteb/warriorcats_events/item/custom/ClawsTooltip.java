package net.snowteb.warriorcats_events.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.attachments.ModAttachments;
import net.snowteb.warriorcats_events.block.ModBlocks;
import net.snowteb.warriorcats_events.block.custom.MakeshiftBedBlock;
import net.snowteb.warriorcats_events.attachments.WCEPlayerData;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.item.ModItems;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.s2c.others.StCFishingScreenPacket;
import net.snowteb.warriorcats_events.particles.WCEParticles;
import net.snowteb.warriorcats_events.sound.ModSounds;
import net.snowteb.warriorcats_events.managers.CarryPlayerRequestManager;
import tocraft.walkers.api.PlayerShape;

import java.util.List;
import java.util.Optional;

import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;
import static net.snowteb.warriorcats_events.block.custom.MakeshiftBedBlock.STATE;

public class ClawsTooltip extends ShearsItem {
    private static final ResourceLocation ATTACK_DAMAGE_UUID = ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "claws_damage");
    private static final ResourceLocation ATTACK_SPEED_UUID = ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "claws_speed");
    private final String tooltipKey;

    public ClawsTooltip(Properties properties, String tooltipKey) {
        super(properties);
        this.tooltipKey = tooltipKey;
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        float modifier = clawsDamageModifier(stack);

        return ItemAttributeModifiers.builder()
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(
                                ATTACK_DAMAGE_UUID,
                                3.0 * modifier,
                                AttributeModifier.Operation.ADD_VALUE
                        ),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(
                                ATTACK_SPEED_UUID,
                                1.0f + (0.6f * modifier),
                                AttributeModifier.Operation.ADD_VALUE
                        ),
                        EquipmentSlotGroup.MAINHAND
                )
                .build();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        String raw = (Component.translatable(tooltipKey).getString());

        String[] lines = raw.split("\\\\n");

        for (String line : lines) {
            tooltipComponents.add(Component.literal(line).withStyle(ChatFormatting.GRAY));
        }

        if (InventoryScreen.hasShiftDown()) {
            Component shiftRightClick = Component.translatable("generic.shift_right_click").append(" ");
            Component rightClick = Component.translatable("generic.right_click").append(" ");

            tooltipComponents.add(Component.empty());
            tooltipComponents.add(shiftRightClick.copy().append(Component.translatable("item.warriorcats_events.claws.tip2").withStyle(ChatFormatting.GRAY)));
            tooltipComponents.add(rightClick.copy().append(Component.translatable("item.warriorcats_events.claws.tip3").withStyle(ChatFormatting.GRAY)));
            tooltipComponents.add(shiftRightClick.copy().append(Component.translatable("item.warriorcats_events.claws.tip4").withStyle(ChatFormatting.GRAY)));
            tooltipComponents.add(rightClick.copy().append(Component.translatable("item.warriorcats_events.claws.tip5").withStyle(ChatFormatting.GRAY)));
            tooltipComponents.add(rightClick.copy().append(Component.translatable("item.warriorcats_events.claws.tip6").withStyle(ChatFormatting.GRAY)));
            tooltipComponents.add(rightClick.copy().append(Component.translatable("item.warriorcats_events.claws.tip7").withStyle(ChatFormatting.GRAY)));
            tooltipComponents.add(shiftRightClick.copy().append(Component.translatable("item.warriorcats_events.claws.tip8").withStyle(ChatFormatting.GRAY)));
            tooltipComponents.add(rightClick.copy().append(Component.translatable("item.warriorcats_events.claws.tip9").withStyle(ChatFormatting.GRAY)));

        } else {
            tooltipComponents.add(Component.empty());
            tooltipComponents.add((Component.translatable("item.warriorcats_events.claws.display_tips").withStyle(ChatFormatting.DARK_PURPLE)));
        }

    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        return ItemAbilities.DEFAULT_AXE_ACTIONS.contains(itemAbility)
                || ItemAbilities.DEFAULT_SHIELD_ACTIONS.contains(itemAbility)
                || super.canPerformAction(stack, itemAbility);
    }

    /**
     * Allows performing axe actions and shield actions.
     */



    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (state.is(BlockTags.LEAVES)) {
            return 2.0F;
        }
        return 1.0f;
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return false;
    }

    public boolean canAttackBlock(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer) {
        return !pPlayer.isCreative();
    }

    /**
     * Too hard to explain
     * It is basically the same thing axes do with logs, but instead of damaging the tool, this will repair the claws.
     */
    public InteractionResult useOn(UseOnContext pContext) {

        Level level = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        Player player = pContext.getPlayer();
        BlockState blockstate = level.getBlockState(blockpos);
        Optional<BlockState> optional = Optional.ofNullable(blockstate.getToolModifiedState(pContext, ItemAbilities.AXE_STRIP, false));
        ItemStack itemstack = pContext.getItemInHand();
        Optional<BlockState> optional3 = Optional.empty();


        if (player != null) {
            if (!level.isClientSide) {
                if (blockstate.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
                    boolean isUnderTree = false;
                    int count = 0;
                    for (int i = 1; i <= 15; i++) {
                        BlockPos checkPos = player.blockPosition().above(i);
                        BlockState state = level.getBlockState(checkPos);

                        if (state.is(BlockTags.LEAVES)) {
                            count++;
                        }
                        if (count >= 2) {
                            isUnderTree = true;
                            break;
                        }
                    }
                    if (isUnderTree) {
                        BlockState thisState = level.getBlockState(player.blockPosition());
                        BlockPos playerPos = player.blockPosition();

                        int value = 0;
                        boolean wasAir = false;
                        if (thisState.isAir()) {
                            BlockState newState = ModBlocks.MAKESHIFT_BED.get().defaultBlockState();

                            level.setBlockAndUpdate(playerPos, newState.setValue(STATE, value).setValue(FACING, player.getDirection().getOpposite()));

                            level.sendBlockUpdated(player.blockPosition(), thisState, newState, 3);
                            wasAir = true;

                        } else if (thisState.getBlock() instanceof MakeshiftBedBlock) {
                            value = thisState.getValue(STATE);
                            if (value == 3) return InteractionResult.PASS;

                            if (level.getRandom().nextFloat() < 0.2f) {
                                value = Mth.clamp(value + 1, 0, 3);

                                BlockState newState = ModBlocks.MAKESHIFT_BED.get().defaultBlockState()
                                        .setValue(STATE, value)
                                        .setValue(FACING, player.getDirection().getOpposite());

                                level.setBlockAndUpdate(playerPos, newState.setValue(STATE, value));

                                level.sendBlockUpdated(player.blockPosition(), thisState, newState, 3);
                            }
                        }

                        if (level instanceof ServerLevel sLevel) {

                            Vec3 exactPos = playerPos.getCenter().add(0, -0.5, 0);

                            sLevel.sendParticles(
                                    new BlockParticleOption(ParticleTypes.BLOCK, ModBlocks.MAKESHIFT_BED.get().defaultBlockState()),
                                    exactPos.x, exactPos.y, exactPos.z,
                                    20, 0.0, 0.0, 0.0, 0.1
                            );

                            Vec3 exactPos2 = blockpos.getCenter().add(0, 0.55, 0);

                            sLevel.sendParticles(
                                    new BlockParticleOption(ParticleTypes.BLOCK, ModBlocks.MAKESHIFT_BED.get().defaultBlockState()),
                                    exactPos2.x, exactPos2.y, exactPos2.z,
                                    20, 0.0, 0.0, 0.0, 0.1
                            );

                            itemstack.hurtAndBreak(1, sLevel, player, (p) -> {});

                            if (value == 3 || wasAir) {
                                sLevel.sendParticles(
                                        ParticleTypes.HAPPY_VILLAGER,
                                        exactPos.x, exactPos.y + 0.3, exactPos.z,
                                        10, 0.2, 0.2, 0.2, 0.1
                                );

                                sLevel.playSound(null, player.blockPosition(),
                                        SoundEvents.CHERRY_LEAVES_BREAK, SoundSource.BLOCKS,
                                        1.0F, 1.0F);
                            } else {
                                sLevel.playSound(null, player.blockPosition(),
                                        SoundEvents.CHERRY_LEAVES_BREAK, SoundSource.BLOCKS,
                                        0.4F, 1.0F);
                            }

                        }

                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }

        if (player != null){
            if (blockstate.is(Blocks.MOSSY_COBBLESTONE)) {
                if (!level.isClientSide()) {
                    itemstack.hurtAndBreak(3,((ServerLevel) player.level()) ,player, (p) -> {});
                    ItemStack itemstack1 = Items.MOSS_BLOCK.asItem().getDefaultInstance();
                    if (level.getRandom().nextFloat() < 0.05F) {
                        itemstack1 = Items.BROWN_DYE.asItem().getDefaultInstance();
                        itemstack1.set(DataComponents.CUSTOM_NAME, Component.empty()
                                .append("Fox dung")
                                .append(Component.literal(" (Really stinky)").withStyle(ChatFormatting.GRAY)));
                    }
                    ItemEntity itemEntity = new ItemEntity(level, blockpos.getX(), blockpos.getY(), blockpos.getZ(), itemstack1);
                    itemEntity.setItem(itemstack1.copyWithCount(1));

                    Vec3 spawnPos = Vec3.atCenterOf(blockpos);

                    Vec3 direction = player.position().subtract(spawnPos)
                            .normalize().scale(0.35);

                    itemEntity.setDeltaMovement(direction);
                    level.addFreshEntity(itemEntity);


                    level.setBlock(blockpos, Blocks.COBBLESTONE.defaultBlockState(), 11);
                    level.gameEvent(GameEvent.BLOCK_CHANGE, blockpos, GameEvent.Context.of(player, blockstate));

                    if (level instanceof ServerLevel sLevel) {
                        Vec3 position = blockpos.getCenter();
                        sLevel.playSound(null, blockpos,  ModSounds.SCRAPING_WOOD.get(),
                                SoundSource.BLOCKS, 0.5F, 1F);
                        sLevel.sendParticles(
                                WCEParticles.HERBS_FALL.get(),
                                position.x, position.y - 0.1, position.z,
                                40, 0.1, 0.0, 0.1, 0.005);
                    }
                }
                return InteractionResult.sidedSuccess(level.isClientSide());
            } else if (blockstate.is(Blocks.MOSSY_STONE_BRICKS)) {
                if (!level.isClientSide()) {

                    itemstack.hurtAndBreak(3, ((ServerLevel) player.level()), player, (p) -> {});

                    ItemStack itemstack1 = Items.MOSS_BLOCK.asItem().getDefaultInstance();
                    if (level.getRandom().nextFloat() < 0.05F) {
                        itemstack1 = Items.BROWN_DYE.asItem().getDefaultInstance();
                        itemstack1.set(DataComponents.CUSTOM_NAME, Component.empty()
                                .append("Fox dung")
                                .append(Component.literal(" (Really stinky)").withStyle(ChatFormatting.GRAY)));
                    }
                    ItemEntity itemEntity = new ItemEntity(level, blockpos.getX(), blockpos.getY(), blockpos.getZ(), itemstack1);
                    itemEntity.setItem(itemstack1.copyWithCount(1));

                    Vec3 spawnPos = Vec3.atCenterOf(blockpos);

                    Vec3 direction = player.position().subtract(spawnPos)
                            .normalize().scale(0.35);

                    itemEntity.setDeltaMovement(direction);
                    level.addFreshEntity(itemEntity);


                    level.setBlock(blockpos, Blocks.STONE_BRICKS.defaultBlockState(), 11);
                    level.gameEvent(GameEvent.BLOCK_CHANGE, blockpos, GameEvent.Context.of(player, blockstate));

                    if (level instanceof ServerLevel sLevel) {
                        Vec3 position = blockpos.getCenter();
                        sLevel.playSound(null, blockpos,  ModSounds.SCRAPING_WOOD.get(),
                                SoundSource.BLOCKS, 0.5F, 1F);
                        sLevel.sendParticles(
                                WCEParticles.HERBS_FALL.get(),
                                position.x, position.y - 0.1, position.z,
                                10, 0.1, 0.0, 0.1, 0.005);
                    }
                }
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
        }


        if (optional.isPresent()) {
            level.playSound(null, blockpos, SoundEvents.CROSSBOW_QUICK_CHARGE_3.value(), SoundSource.BLOCKS, 0.7F, 1.2F);
            level.playSound(null, blockpos, ModSounds.SCRAPING_WOOD.get(), SoundSource.BLOCKS, 0.5F, 1F);
            optional3 = optional;
        }

        if (optional3.isPresent()) {
            if (player instanceof ServerPlayer) {
                CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer) player, blockpos, itemstack);
                level.playSound(null, blockpos, SoundEvents.CHERRY_WOOD_FALL, SoundSource.BLOCKS, 0.7F, 1.0F);
            }

            if (player != null) {
                if (!level.isClientSide()) {
                    itemstack.hurtAndBreak(-3, ((ServerLevel) player.level()) ,player, (p) -> {});

                    if (level.getRandom().nextFloat() < 0.01F) {
                        ItemStack itemstack1 = Items.MOSS_BLOCK.asItem().getDefaultInstance();
                        if (level.getRandom().nextFloat() < 0.05F) {
                            itemstack1 = Items.BROWN_DYE.asItem().getDefaultInstance();
                            itemstack1.set(DataComponents.CUSTOM_NAME, Component.empty()
                                    .append("Fox dung")
                                    .append(Component.literal(" (Really stinky)").withStyle(ChatFormatting.GRAY)));
                        }
                        ItemEntity itemEntity = new ItemEntity(level, blockpos.getX(), blockpos.getY(), blockpos.getZ(), itemstack1);
                        itemEntity.setItem(itemstack1.copyWithCount(level.random.nextInt(2) + 1));

                        Vec3 spawnPos = Vec3.atCenterOf(blockpos);

                        Vec3 direction = player.position().subtract(spawnPos)
                                .normalize().scale(0.35);

                        itemEntity.setDeltaMovement(direction);
                        level.addFreshEntity(itemEntity);

                    }
                }
            }

            if (!level.isClientSide() && level.getRandom().nextInt(40) == 0) {
                level.setBlock(blockpos, optional.get(), 11);
                level.gameEvent(GameEvent.BLOCK_CHANGE, blockpos, GameEvent.Context.of(player, optional3.get()));
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        } else {

            return InteractionResult.PASS;
        }


    }

    private float clawsDamageModifier(ItemStack stack) {
        if (!stack.is(ModItems.CLAWS.get())) {
            return 1.0f;
        }

        float max = stack.getMaxDamage();
        float remaining = max - stack.getDamageValue();
        float durabilityPercent = remaining / max;

        return 0.2f + (0.8f * durabilityPercent);
    }

    /**
     * Every time you hurt an entity, damage the tool.
     */
    public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
        if (pAttacker instanceof Player player && player.getAbilities().instabuild) return true;

        damageTool(pStack, 1, pAttacker);
        return true;
    }

    public void damageTool(ItemStack stack, int amount, LivingEntity attacker) {

        Holder<Enchantment> unbr =
                attacker.level()
                        .registryAccess()
                        .lookupOrThrow(Registries.ENCHANTMENT)
                        .getOrThrow(Enchantments.UNBREAKING);

        int unbreaking = stack.getEnchantmentLevel(unbr);
        if (unbreaking > 0) {
            if (attacker.level().random.nextInt(unbreaking) != 0) {
                amount = 0;
            }
        }

        if (amount > 0) {
            int newDamage = stack.getDamageValue() + amount;
            if (newDamage >= stack.getMaxDamage()) {
                newDamage = stack.getMaxDamage() - 1;
            }
            stack.setDamageValue(newDamage);
        }
    }

    private boolean isRiverOrOcean(Level level, BlockPos origin) {

        int radius = 10;
        int depth = 5;

        int waterCount = 0;
        int total = 0;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = 0; y > -depth; y--) {

                    BlockPos check = origin.offset(x, y, z);
                    total++;

                    if (level.getBlockState(check).getFluidState().isSource()) {
                        waterCount++;
                    }
                }
            }
        }

        float ratio = (float) waterCount / total;

        return ratio > 0.306f;
    }


    /**
     * If you use right click, then start using the item.
     * And since we previously defined the item can perform shield actions, then it will perform the shield action.
     */
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);

        BlockHitResult hit = Item.getPlayerPOVHitResult(
                pLevel,
                pPlayer,
                ClipContext.Fluid.SOURCE_ONLY
        );

        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = hit.getBlockPos();
            BlockState state = pLevel.getBlockState(pos);

            if (state.getFluidState().isSource() && pPlayer.isShiftKeyDown()) {

                if (!pLevel.isClientSide) {
                    if (PlayerShape.getCurrentShape(pPlayer) instanceof Animal) {
                        if (pPlayer.getItemInHand(InteractionHand.MAIN_HAND).getDamageValue() >= this.getMaxDamage(itemstack) - 5) {
                            pPlayer.displayClientMessage(Component.translatable("item.warriorcats_events.claws.too_damaged").withStyle(ChatFormatting.RED), true);
                            return InteractionResultHolder.fail(itemstack);
                        }
                        boolean canFish = isRiverOrOcean(pLevel, pos);

                        if (canFish) {
                            pPlayer.getCooldowns().addCooldown(this, 20 * 8);
                            if (!pLevel.isClientSide && pPlayer instanceof ServerPlayer sPlayer) {
                                ModPackets.sendToPlayer(new StCFishingScreenPacket(pos), sPlayer);
                                pLevel.playSound(
                                        null,
                                        sPlayer.blockPosition(),
                                        SoundEvents.AMBIENT_UNDERWATER_ENTER,
                                        SoundSource.PLAYERS,
                                        0.8F,
                                        1.0F
                                );
                                pLevel.playSound(
                                        null,
                                        sPlayer.blockPosition(),
                                        SoundEvents.CAT_EAT,
                                        SoundSource.PLAYERS,
                                        0.8F,
                                        1.0F
                                );

                                ((ServerLevel) pLevel).sendParticles(
                                        ParticleTypes.SPLASH,
                                        sPlayer.getX(),
                                        sPlayer.getY() + 0.5,
                                        sPlayer.getZ(),
                                        30,
                                        0.4, 0.2, 0.4,
                                        0.02
                                );
                            }


                        } else {
                            pPlayer.displayClientMessage(
                                    Component.translatable("item.warriorcats_events.claws.not_fish_here")
                                            .withStyle(ChatFormatting.YELLOW),
                                    true
                            );
                        }
                    }
                }

                return InteractionResultHolder.success(itemstack);
            }
        }

        pPlayer.startUsingItem(pHand);
        pLevel.playSound(pPlayer, pPlayer.blockPosition(), SoundEvents.MOSS_STEP, SoundSource.PLAYERS, 0.7F, 0.6F);

        return InteractionResultHolder.consume(itemstack);
    }

    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.BLOCK;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player playerIn, LivingEntity entity, InteractionHand hand) {

        if (playerIn.isShiftKeyDown() && entity instanceof Player targetPlayer) {
            if ((playerIn.level() instanceof ServerLevel)) {

                WCEPlayerData.Age targetAge = targetPlayer.getData(ModAttachments.PLAYER_WCE_DATA).getMorphAge();

                WCEPlayerData.Age playerAge = playerIn.getData(ModAttachments.PLAYER_WCE_DATA).getMorphAge();

                if (playerAge == WCEPlayerData.Age.ADULT && (targetAge == WCEPlayerData.Age.KIT || targetAge == WCEPlayerData.Age.APPRENTICE)) {

                    if (PlayerShape.getCurrentShape(targetPlayer) instanceof WCatEntity && PlayerShape.getCurrentShape(playerIn) instanceof WCatEntity) {
                        String morphName = playerIn.getData(ModAttachments.PLAYER_WCE_DATA).getMorphName();

                        String morphNameTarget = targetPlayer.getData(ModAttachments.PLAYER_WCE_DATA).getMorphName();

                        CarryPlayerRequestManager.request((ServerPlayer) targetPlayer, (ServerPlayer) playerIn);

                        playerIn.sendSystemMessage(
                                Component.translatable("item.warriorcats_events.claws.carry_request_sent",
                                        Component.literal(morphNameTarget).withStyle(ChatFormatting.AQUA)
                                                .append(Component.literal( "(" + targetPlayer.getName().getString() + ")").withStyle(ChatFormatting.GRAY))
                                ));

                        targetPlayer.sendSystemMessage(
                                Component.translatable("item.warriorcats_events.claws.carry_request_received",
                                        Component.literal(morphName).withStyle(ChatFormatting.AQUA)
                                                .append(Component.literal( "(" + playerIn.getName().getString() + ")").withStyle(ChatFormatting.GRAY))
                                ));

                        targetPlayer.sendSystemMessage(
                                CarryPlayerRequestManager.getMessage()
                        );

                        return InteractionResult.SUCCESS;

                    }

                }
            }
        }

        return super.interactLivingEntity(stack, playerIn, entity, hand);
    }

    @Override
    public boolean isEnchantable(ItemStack pStack) {
        return true;
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return (enchantment == Enchantments.SHARPNESS)
                || (enchantment == Enchantments.FIRE_ASPECT)
                || (enchantment == Enchantments.UNBREAKING)
                || (enchantment == Enchantments.LUCK_OF_THE_SEA)
                ;
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return 15;
    }
}

