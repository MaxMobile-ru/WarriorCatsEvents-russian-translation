package net.snowteb.warriorcats_events.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.snowteb.warriorcats_events.block.ModBlocks;
import net.snowteb.warriorcats_events.clan.WCEPlayerDataProvider;
import net.snowteb.warriorcats_events.item.ModItems;
import net.snowteb.warriorcats_events.skills.ISkillData;
import net.snowteb.warriorcats_events.skills.PlayerSkillProvider;

import java.util.HashMap;
import java.util.Map;

public class WCEStoneBlock extends DirectionalBlock {

    public static final IntegerProperty CARVED =
            IntegerProperty.create("carved", 1, 3);

    private static final Map<BlockPos, Integer> WORLD_HITS = new HashMap<>();

    public WCEStoneBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH).setValue(CARVED, 1));
    }

    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pPlayer.getItemInHand(pHand).is(ModItems.CLAWS.get())) {
            int carved = pState.getValue(CARVED);
            if (!pLevel.isClientSide()) {
                int hits = WORLD_HITS.merge(pPos, 1, Integer::sum);

                int clawsLevel = pPlayer.getCapability(PlayerSkillProvider.SKILL_DATA)
                        .map(ISkillData::getDMGLevel).orElse(0)/2;

                if (hits >= 10 - clawsLevel) {
                    WORLD_HITS.remove(pPos);
                    pLevel.destroyBlock(pPos, false);

                    if (carved < 3) {
                        BlockState state = ModBlocks.CARVED_STONE.get().defaultBlockState()
                                .setValue(WCEStoneBlock.FACING, pState.getValue(FACING))
                                .setValue(CARVED, carved + 1);
                        pLevel.setBlockAndUpdate(pPos, state);
                    }

                    ItemStack newStack = new ItemStack(ModItems.PEBBLES_ITEM.get(), carved < 3 ? 2 : 3);

                    ItemEntity itemEntity = new ItemEntity(pLevel,  pPos.getX(), pPos.getY(), pPos.getZ(), newStack);
                    pLevel.addFreshEntity(itemEntity);
                } else {
                    Vec3 position = pPos.getCenter();
                    ((ServerLevel) pLevel).sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, pState),
                            position.x, position.y, position.z, 30, 0.0, 0.0, 0.0, 0.5);
                }

            }

            return InteractionResult.SUCCESS;
        }

        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING).add(CARVED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(CARVED, 1);
    }

}
