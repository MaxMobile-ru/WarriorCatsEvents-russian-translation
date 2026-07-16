package net.snowteb.warriorcats_events.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.snowteb.warriorcats_events.screen.menus.StoneCraftingTableMenu;
import tocraft.walkers.api.PlayerShape;

public class StoneCraftingTable extends HorizontalDirectionalBlock {
    private static final Component CONTAINER_TITLE = Component.translatable("block.warriorcats_events.stone_crafting_table");

    protected static final VoxelShape NORTH_TABLE = Block.box(2.0D, 0.0D, 6.0D, 13.0D, 6.0D, 14.0D);
    protected static final VoxelShape NORTH_ROCK1 = Block.box(0.0D, 0.0D, 0.0D, 4.0D, 2.0D, 4.0D);
    protected static final VoxelShape NORTH_ROCK2 = Block.box(12.0D, 0.0D, 1.0D, 15.0D, 2.0D, 5.0D);

    protected static final VoxelShape SOUTH_TABLE = Block.box(3.0D, 0.0D, 2.0D, 14.0D, 6.0D, 10.0D);
    protected static final VoxelShape SOUTH_ROCK1 = Block.box(12.0D, 0.0D, 12D, 16D, 2D, 16D);
    protected static final VoxelShape SOUTH_ROCK2 = Block.box(1D, 0D, 11D, 4D, 2D, 15D);

    protected static final VoxelShape WEST_TABLE = Block.box(6.0D, 0.0D, 3.0D, 14.0D, 6.0D, 14.0D);
    protected static final VoxelShape WEST_ROCK1 = Block.box(0, 0, 12, 4, 2, 16);
    protected static final VoxelShape WEST_ROCK2 = Block.box(1, 0, 1, 5, 2, 4);

    protected static final VoxelShape EAST_TABLE = Block.box(2.0D, 0.0D, 2.0D, 10.0D, 6.0D, 13.0D);
    protected static final VoxelShape EAST_ROCK1 = Block.box(12, 0, 0, 16, 2, 4);
    protected static final VoxelShape EAST_ROCK2 = Block.box(11, 0, 12, 15, 2, 15);

    protected static final VoxelShape SHAPE_NORTH = Shapes.or(NORTH_TABLE, NORTH_ROCK1, NORTH_ROCK2);
    protected static final VoxelShape SHAPE_SOUTH = Shapes.or(SOUTH_TABLE, SOUTH_ROCK1, SOUTH_ROCK2);
    protected static final VoxelShape SHAPE_WEST = Shapes.or(WEST_TABLE, WEST_ROCK1, WEST_ROCK2);
    protected static final VoxelShape SHAPE_EAST = Shapes.or(EAST_TABLE, EAST_ROCK1, EAST_ROCK2);

    public StoneCraftingTable(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(
                this.stateDefinition.any().setValue(FACING, Direction.NORTH)
        );
    }

    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            if (PlayerShape.getCurrentShape(pPlayer) instanceof Animal) {

                pPlayer.openMenu(pState.getMenuProvider(pLevel, pPos));
                pPlayer.awardStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
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
            return new StoneCraftingTableMenu(id, inv, ContainerLevelAccess.create(pLevel, pPos));
        }, CONTAINER_TITLE);
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
        Direction direction = pState.getValue(FACING);
        switch (direction) {
            case EAST:
                return SHAPE_EAST;
            case SOUTH:
                return SHAPE_SOUTH;
            case WEST:
                return SHAPE_WEST;
            case NORTH:
                return SHAPE_NORTH;
        }
        return SHAPE_NORTH;
    }

}
