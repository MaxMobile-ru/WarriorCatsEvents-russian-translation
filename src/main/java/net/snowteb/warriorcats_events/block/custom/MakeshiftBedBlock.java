package net.snowteb.warriorcats_events.block.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.snowteb.warriorcats_events.block.ModBlocks;
import net.snowteb.warriorcats_events.block.entity.MakeshiftBedBlockEntity;

import javax.annotation.Nullable;

public class MakeshiftBedBlock extends BedBlock {
    protected static final VoxelShape BASE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 1.0D, 14.0D);

    protected static final VoxelShape SHAPE = Shapes.or(BASE);

    public static final IntegerProperty STATE =
            IntegerProperty.create("state", 0, 3);

    public MakeshiftBedBlock(Properties properties) {
        super(DyeColor.RED, properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH)
                .setValue(PART, BedPart.HEAD)
                .setValue(OCCUPIED, false)
                .setValue(STATE, 0)
        );
    }

    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {

        int value = state.getValue(STATE);
        if (player.getItemInHand(InteractionHand.MAIN_HAND).is(ModBlocks.MAKESHIFT_BED.get().asItem()) && value < 3) {
            if (!level.isClientSide) {

                    value = Mth.clamp(value + 1, 0, 3);

                    level.setBlockAndUpdate(pos, state.setValue(STATE, value));
                    player.swing(InteractionHand.MAIN_HAND);
                    if (!player.getAbilities().instabuild) player.getItemInHand(InteractionHand.MAIN_HAND).shrink(1);

                if (level instanceof ServerLevel sLevel) {

                    Vec3 exactPos = pos.getCenter().add(0, -0.5, 0);

                    sLevel.sendParticles(
                            new BlockParticleOption(ParticleTypes.BLOCK, state),
                            exactPos.x, exactPos.y, exactPos.z,
                            20, 0.0, 0.0, 0.0, 0.1
                    );
                    sLevel.playSound(null, player.blockPosition(), SoundEvents.CHERRY_LEAVES_BREAK,
                            SoundSource.BLOCKS, 1.0F, 1.0F);
                    if (value == 3) {
                        sLevel.sendParticles(
                                ParticleTypes.HAPPY_VILLAGER,
                                exactPos.x, exactPos.y + 0.3, exactPos.z,
                                10, 0.2, 0.2, 0.2, 0.1
                        );
                    }
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (value == 3) {
            BlockState headState = state.setValue(PART, BedPart.HEAD);
            return super.use(headState, level, pos, player, hand, hit);
        } else {
            player.displayClientMessage(
                    Component.translatable("blockinteraction.makeshiftnest.fail").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC),
                    true);
        }
        return InteractionResult.PASS;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(PART, BedPart.HEAD)
                .setValue(FACING, Direction.NORTH)
                .setValue(STATE, 0);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, PART, OCCUPIED, STATE);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MakeshiftBedBlockEntity(pos, state, this.getColor());
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
    }

    @Override
    public void playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
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
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return pLevel.getBlockState(pPos.below()).isFaceSturdy(pLevel, pPos.below(), Direction.UP);
    }


}