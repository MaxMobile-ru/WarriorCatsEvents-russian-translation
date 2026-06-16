package net.snowteb.warriorcats_events.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.TurtleEggBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.snowteb.warriorcats_events.block.entity.LizardEggBlockEntity;
import net.snowteb.warriorcats_events.entity.ModEntities;
import net.snowteb.warriorcats_events.entity.custom.LizardEntity;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class LizardEggBlock extends TurtleEggBlock implements EntityBlock {
    public LizardEggBlock(Properties pProperties) {
        super(pProperties);
    }

    private static final VoxelShape LIZARD_MULTIPLE_EGGS_AABB = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 2.0D, 10.0D);

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return LIZARD_MULTIPLE_EGGS_AABB;
    }

    @Override
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (this.shouldUpdateLizardHatchLevel(pLevel)) {
            int i = pState.getValue(HATCH);
            if (i < 2) {
                pLevel.playSound(null, pPos, SoundEvents.TURTLE_EGG_CRACK, SoundSource.BLOCKS, 0.7F, 0.9F + pRandom.nextFloat() * 0.2F);
                pLevel.setBlock(pPos, pState.setValue(HATCH, Integer.valueOf(i + 1)), 2);
            } else {
                pLevel.playSound(null, pPos, SoundEvents.TURTLE_EGG_HATCH, SoundSource.BLOCKS, 0.7F, 0.9F + pRandom.nextFloat() * 0.2F);
                pLevel.removeBlock(pPos, false);

                UUID ownerUUID = pLevel.getBlockEntity(pPos) instanceof LizardEggBlockEntity be ? be.getOwnerUUID() : null;

                for(int j = 0; j < pState.getValue(EGGS); ++j) {
                    pLevel.levelEvent(2001, pPos, Block.getId(pState));
                    LizardEntity lizard = ModEntities.LIZARD.get().create(pLevel);

                    if (lizard != null) {
                        lizard.finalizeSpawn(pLevel, pLevel.getCurrentDifficultyAt(pPos),
                                MobSpawnType.MOB_SUMMONED, null);
                        if (ownerUUID != null) {
                            lizard.setTame(true, false);
                            lizard.setOwnerUUID(ownerUUID);
                        }
                        lizard.mode = LizardEntity.LizardMode.WANDER;
                        lizard.setAge(-24000);
                        lizard.moveTo((double)pPos.getX() + 0.3D + (double)j * 0.2D, pPos.getY(), (double)pPos.getZ() + 0.3D, 0.0F, 0.0F);
                        pLevel.addFreshEntity(lizard);
                    }
                }
            }
        }
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
    }

    private boolean shouldUpdateLizardHatchLevel(Level pLevel) {
        float f = pLevel.getTimeOfDay(1.0F);
        if ((double)f < 0.69D && (double)f > 0.65D) {
            return true;
        } else {
            return pLevel.random.nextInt(200) == 0;
        }
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new LizardEggBlockEntity(pPos, pState);
    }
}
