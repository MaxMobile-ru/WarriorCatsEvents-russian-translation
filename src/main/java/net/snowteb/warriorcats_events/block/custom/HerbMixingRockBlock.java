package net.snowteb.warriorcats_events.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.snowteb.warriorcats_events.block.entity.HerbMixingRockBlockEntity;
import net.snowteb.warriorcats_events.particles.WCEParticles;
import net.snowteb.warriorcats_events.screen.menus.HerbMixingMenu;
import org.jetbrains.annotations.Nullable;
import tocraft.walkers.api.PlayerShape;

public class HerbMixingRockBlock extends HorizontalDirectionalBlock implements EntityBlock {

    protected static final VoxelShape HITBOX = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 5.0D, 14.0D);

    public HerbMixingRockBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(
                this.stateDefinition.any().setValue(FACING, Direction.NORTH)
        );
    }

    private static final MapCodec<HerbMixingRockBlock> CODEC = simpleCodec(HerbMixingRockBlock::new);

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }


    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            if (PlayerShape.getCurrentShape(player) instanceof Animal) {

                if (!level.isClientSide()) {
                    BlockEntity entity = level.getBlockEntity(pos);
                    if (entity instanceof HerbMixingRockBlockEntity) {
                        player.openMenu((MenuProvider) entity, pos);
                    }
                }

                return InteractionResult.CONSUME;
            } else {
                return InteractionResult.PASS;
            }
        }

    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState pState) {
        return false;
    }

    public MenuProvider getMenuProvider(BlockState pState, Level pLevel, BlockPos pPos) {
        return new SimpleMenuProvider((id, inv, player) -> {
            return new HerbMixingMenu(id, inv, pLevel.getBlockEntity(pPos), new SimpleContainerData(9));
        }, Component.empty());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public boolean canHarvestBlock(BlockState state, BlockGetter level, BlockPos pos, Player player) {
        return true;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return HITBOX;
    }


    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new HerbMixingRockBlockEntity(pPos, pState);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {

        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof HerbMixingRockBlockEntity) {
                ((HerbMixingRockBlockEntity) blockEntity).dropInventory();
            }
        }

        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {

        if (pLevel.getBlockEntity(pPos) instanceof HerbMixingRockBlockEntity blockEntity) {
            if (blockEntity.isNotEmpty()) {
                Vec3 position = pPos.getCenter();

                Direction facing = pState.getValue(HerbMixingRockBlock.FACING);

                position = switch (facing) {
                    case NORTH -> position.add(0,0,0.1);
                    case SOUTH -> position.add(0,0,-0.1);
                    case WEST -> position.add(0.1,0,0);
                    case EAST -> position.add(-0.1,0,0);
                    default -> position;
                };

                pLevel.sendParticles(
                        WCEParticles.HERBS.get(),
                        position.x, position.y - 0.05, position.z,
                        1, 0.0, 0.0, 0.0, 0.005);
            }
        }

        pLevel.scheduleTick(pPos, this, 15);
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston) {
        super.onPlace(pState, pLevel, pPos, pOldState, pMovedByPiston);
        if (!pLevel.isClientSide()) {
            pLevel.scheduleTick(pPos, this, 15);
        }
    }
}
