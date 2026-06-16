package net.snowteb.warriorcats_events.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
import java.util.Map;

public class NautilusNestBlock extends NestBlock {
    public NautilusNestBlock(Properties properties) {
        super(properties);
    }

    protected static final VoxelShape NB_BASE = Block.box(0, 0, 0, 16, 1, 16);

    protected static final VoxelShape WEST_WALL_1 = Block.box(0, 0, 0,  16, 9, 1);
    protected static final VoxelShape WEST_WALL_2 = Block.box(0, 0, 15, 16, 9, 16);
    protected static final VoxelShape WEST_WALL_3 = Block.box(0, 0, 0,  1,  9, 16);

    protected static final VoxelShape EAST_WALL_1 = Block.box(0,  0, 0,  16, 9, 1);
    protected static final VoxelShape EAST_WALL_2 = Block.box(0,  0, 15, 16, 9, 16);
    protected static final VoxelShape EAST_WALL_3 = Block.box(15, 0, 0,  16, 9, 16);

    protected static final VoxelShape NORTH_WALL_1 = Block.box(0,  0, 0, 1,  9, 16);
    protected static final VoxelShape NORTH_WALL_2 = Block.box(15, 0, 0, 16, 9, 16);
    protected static final VoxelShape NORTH_WALL_3 = Block.box(0,  0, 0, 16, 9, 1);

    protected static final VoxelShape SOUTH_WALL_1 = Block.box(0,  0, 0,  1,  9, 16);
    protected static final VoxelShape SOUTH_WALL_2 = Block.box(15, 0, 0,  16, 9, 16);
    protected static final VoxelShape SOUTH_WALL_3 = Block.box(0,  0, 15, 16, 9, 16);

    protected static final Map<Direction, List<VoxelShape>> SHAPES_BY_FACING = Map.of(
            Direction.WEST,  List.of(NB_BASE, WEST_WALL_1,  WEST_WALL_2,  WEST_WALL_3),
            Direction.EAST,  List.of(NB_BASE, EAST_WALL_1,  EAST_WALL_2,  EAST_WALL_3),
            Direction.NORTH, List.of(NB_BASE, NORTH_WALL_1, NORTH_WALL_2, NORTH_WALL_3),
            Direction.SOUTH, List.of(NB_BASE, SOUTH_WALL_1, SOUTH_WALL_2, SOUTH_WALL_3)
    );

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        List<VoxelShape> shapes = SHAPES_BY_FACING.getOrDefault(state.getValue(FACING),
                SHAPES_BY_FACING.get(Direction.WEST));
        return shapes.stream().reduce(Shapes.empty(), Shapes::or);
    }
}
