package net.snowteb.warriorcats_events.block.custom;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.snowteb.warriorcats_events.attachments.ModAttachments;
import net.snowteb.warriorcats_events.block.ModBlocks;
import net.snowteb.warriorcats_events.block.entity.NestBlockEntity;
import net.snowteb.warriorcats_events.clan.ClanData;
import net.snowteb.warriorcats_events.attachments.WCEPlayerData;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.item.ModItems;
import net.snowteb.warriorcats_events.particles.WCEParticles;
import net.snowteb.warriorcats_events.sound.ModSounds;

public class NestBlock extends BedBlock {
    protected static final VoxelShape BASE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 1.0D, 14.0D);

    protected static final VoxelShape SHAPE = Shapes.or(BASE);

    public NestBlock(BlockBehaviour.Properties properties) {
        super(DyeColor.RED, properties);

        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(FACING, Direction.NORTH)
                        .setValue(PART, BedPart.HEAD)
                        .setValue(OCCUPIED, false)
        );
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            if (player.getItemInHand(InteractionHand.MAIN_HAND).is(ModBlocks.LAVENDER_PETALS.get().asItem())) {
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof NestBlockEntity nestBlockEntity && !nestBlockEntity.getAssignedUUID().equals(ClanData.EMPTY_UUID)) {
                    LivingEntity entity = nestBlockEntity.getAssignedEntity(level);
                    if (entity != null) {
                        if (entity instanceof WCatEntity cat) {
                            cat.setHomePosition(BlockPos.ZERO);
                            if (cat.getOwner() instanceof Player owner && owner != player) {

                                Component message = Component.translatable("blockinteraction.nestblock.clean",
                                        cat.hasCustomName() ? cat.getCustomName() : Component.translatable("generic.catname"));

                                owner.displayClientMessage(message.copy().withStyle(ChatFormatting.YELLOW), true);

                            }
                            nestBlockEntity.resetAssigned();
                            if (!player.getAbilities().instabuild) player.getItemInHand(InteractionHand.MAIN_HAND).shrink(1);
                            level.sendBlockUpdated(pos, state, state, 3);

                            if (level instanceof ServerLevel sLevel) {
                                Vec3 position = pos.getCenter();
                                sLevel.sendParticles(
                                        WCEParticles.LAVENDER.get(),
                                        position.x, pos.getY() + 0.2, position.z,
                                        15, 0.4, 0.0, 0.4,0.005);
                                sLevel.playSound(
                                        null, position.x, position.y, position.z,
                                        SoundEvents.PINK_PETALS_PLACE, SoundSource.BLOCKS,
                                        0.6F, 0.9F
                                );
                            }
                        }
                        if (entity instanceof Player other && other != player) {
                            player.displayClientMessage(Component.translatable("blockinteraction.nestblock.reset_another_player").withStyle(ChatFormatting.GRAY), true);
                        } else {
                            nestBlockEntity.resetAssigned();
                            if (!player.getAbilities().instabuild) player.getItemInHand(InteractionHand.MAIN_HAND).shrink(1);
                            level.sendBlockUpdated(pos, state, state, 3);

                            if (level instanceof ServerLevel sLevel) {
                                Vec3 position = pos.getCenter();
                                sLevel.sendParticles(
                                        WCEParticles.LAVENDER.get(),
                                        position.x, pos.getY() + 0.2, position.z,
                                        15, 0.4, 0.0, 0.4,0.005);
                                sLevel.playSound(
                                        null, position.x, position.y, position.z,
                                        SoundEvents.PINK_PETALS_PLACE, SoundSource.BLOCKS,
                                        0.6F, 0.9F
                                );
                            }
                        }
                    } else {
                        nestBlockEntity.resetAssigned();
                        level.sendBlockUpdated(pos, state, state, 3);


                        if (!player.getAbilities().instabuild) player.getItemInHand(InteractionHand.MAIN_HAND).shrink(1);
                        if (level instanceof ServerLevel sLevel) {
                            Vec3 position = pos.getCenter();
                            sLevel.sendParticles(
                                    WCEParticles.LAVENDER.get(),
                                    position.x, pos.getY() + 0.2, position.z,
                                    15, 0.4, 0.0, 0.4,0.005);
                            sLevel.playSound(
                                    null, position.x, position.y, position.z,
                                    SoundEvents.PINK_PETALS_PLACE, SoundSource.BLOCKS,
                                    0.6F, 0.9F
                            );
                        }
                    }
                    return InteractionResult.SUCCESS;
                }
            }

            if (player.getItemInHand(InteractionHand.MAIN_HAND).is(ModItems.ANCIENT_STICK.get())) {
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof NestBlockEntity nestBlockEntity && !nestBlockEntity.getAssignedUUID().equals(ClanData.EMPTY_UUID)) {
                    LivingEntity entity = nestBlockEntity.getAssignedEntity(level);
                    if (entity != null) {
                        if (entity instanceof WCatEntity cat) {
                            if (level instanceof ServerLevel sLevel) {
                                if (player instanceof ServerPlayer serverPlayer) {
                                    callCat(cat, sLevel, serverPlayer);
                                }
                            }
                        }
                        if (entity instanceof Player) {
                            player.displayClientMessage(Component.translatable("blockinteraction.nestblock.callplayers").withStyle(ChatFormatting.GRAY), false);
                        }
                    } else {
                        player.displayClientMessage(Component.translatable("blockinteraction.nestblock.no_cat_found").withStyle(ChatFormatting.GRAY), false);
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }

        if (level.getBlockEntity(pos) instanceof NestBlockEntity mbEntity) {

            if (!mbEntity.getAssignedUUID().equals(ClanData.EMPTY_UUID)) {
                if (!level.isClientSide) {
                    if (mbEntity.getAssignedEntity(level) != player) {
                        player.displayClientMessage(Component.translatable("blockinteraction.nestblock.already_owned")
                                .withStyle(ChatFormatting.RED), true);
                        return InteractionResult.FAIL;
                    }
                }
            } else {
                if (player.getItemInHand(InteractionHand.MAIN_HAND).is(ModItems.WHISKERS.get())) {

                    if (level instanceof ServerLevel sLevel) {
                        int chunkRadius = (200 >> 4) + 1;
                        int centerChunkX = player.blockPosition().getX() >> 4;
                        int centerChunkZ = player.blockPosition().getZ() >> 4;

                        for (int dx = -chunkRadius; dx <= chunkRadius; dx++) {
                            for (int dz = -chunkRadius; dz <= chunkRadius; dz++) {

                                int chunkX = centerChunkX + dx;
                                int chunkZ = centerChunkZ + dz;

                                LevelChunk chunk = sLevel.getChunkSource().getChunkNow(chunkX, chunkZ);
                                if (chunk == null) continue;

                                for (BlockEntity be : chunk.getBlockEntities().values()) {
                                    if (be instanceof NestBlockEntity mossBed) {

                                        if (be.getBlockPos().distSqr(player.blockPosition()) <= (200 * 200)) {

                                            if (mossBed.getAssignedUUID().equals(player.getUUID())) {
                                                mossBed.resetAssigned();
                                                mossBed.setChanged();

                                                Vec3 centerOfThisMossbed = mossBed.getBlockPos().getCenter();
                                                sLevel.sendParticles(
                                                        ParticleTypes.SMOKE,
                                                        centerOfThisMossbed.x, mossBed.getBlockPos().getY() + 0.5, centerOfThisMossbed.z,
                                                        10, 0.2, 0.5, 0.2, 0.02
                                                );

                                                BlockState bstate = level.getBlockState(mossBed.getBlockPos());
                                                level.sendBlockUpdated(mossBed.getBlockPos(), bstate, bstate, 3);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    mbEntity.assignNest(player.getUUID());

                    String morphName = player.getData(ModAttachments.PLAYER_WCE_DATA).getMorphName();

                    mbEntity.setCatName(morphName + " (" + player.getName().getString() +")");

                    player.displayClientMessage(Component.translatable("blockinteraction.nestblock.claim")
                            .withStyle(ChatFormatting.GREEN), true);

                    if (level instanceof ServerLevel sLevel) {
                        Vec3 centerOfThisMossbed = mbEntity.getBlockPos().getCenter();

                        sLevel.sendParticles(
                                ParticleTypes.HAPPY_VILLAGER,
                                centerOfThisMossbed.x, pos.getY() + 0.5, centerOfThisMossbed.z,
                                10, 0.2, 0.5, 0.2, 0.02
                        );
                    }

                    mbEntity.setChanged();
                    level.sendBlockUpdated(pos, state, state, 3);

                    return InteractionResult.SUCCESS;
                }
            }
        }
        BlockState headState = state.setValue(PART, BedPart.HEAD);

        InteractionResult result = super.useWithoutItem(headState, level, pos, player, hitResult);

        return result;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection();
        return this.defaultBlockState()
                .setValue(FACING, direction)
                .setValue(PART, BedPart.HEAD);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new NestBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state,
                            @Nullable LivingEntity placer, ItemStack stack) {
    }


    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        if (!pState.canSurvive(pLevel, pCurrentPos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return pState;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (blockEntity instanceof NestBlockEntity nestBlockEntity) {
            LivingEntity entity = nestBlockEntity.getAssignedEntity(pLevel);
            if (entity != null && pState.getBlock() != pNewState.getBlock()) {
                if (entity instanceof WCatEntity cat) {
                    cat.setHomePosition(BlockPos.ZERO);
                    if (cat.getOwner() instanceof Player player) {
                        if (!pLevel.isClientSide) {

                            Component message = Component.translatable("blockinteraction.nestblock.cat_lost_nest",
                                    cat.hasCustomName() ? cat.getCustomName() : Component.translatable("generic.catname") );

                            player.sendSystemMessage(message.copy().withStyle(ChatFormatting.YELLOW));

                        }
                    }
                }
                if (entity instanceof Player player) {
                    if (!pLevel.isClientSide) {
                        player.sendSystemMessage(Component.translatable("blockinteraction.nestblock.nest_removed").withStyle(ChatFormatting.GRAY));
                    }
                }
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return pLevel.getBlockState(pPos.below()).isFaceSturdy(pLevel, pPos.below(), Direction.UP);
    }


    public static void callCat(WCatEntity cat, ServerLevel level, ServerPlayer player) {
        WCatEntity.CatMode mode = WCatEntity.CatMode.FOLLOW;

        float pitch = 0.9f;
        WCEPlayerData.Age morphAge = player.getData(ModAttachments.PLAYER_WCE_DATA).getMorphAge();

        switch (morphAge) {
            case KIT -> pitch = 1.3f;
            case APPRENTICE -> pitch = 1.1f;
            case ADULT -> pitch = 0.9f;
        }

        cat.leaderCallingToFollowFlag = true;

        level.playSound(null, player.blockPosition(), ModSounds.LEADER_CALL.get(), SoundSource.PLAYERS, 0.8F, pitch);

        if (!player.isCreative()) {
            ItemStack iteminHand = player.getItemInHand(InteractionHand.MAIN_HAND);
            if (iteminHand.is(ModItems.ANCIENT_STICK.get())) {
                iteminHand.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                player.getCooldowns().addCooldown(iteminHand.getItem(), 20 * 8);
            }
        }

        cat.sendModeMessage(player, mode);

    }

}