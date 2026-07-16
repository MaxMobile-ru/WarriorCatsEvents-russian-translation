package net.snowteb.warriorcats_events.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class KittypetNestBlock extends NestBlock {
    public KittypetNestBlock(Properties properties) {
        super(properties);
    }

    protected static final VoxelShape KB_BASE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 2.0D, 13.0D);

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return KB_BASE;
    }
}
