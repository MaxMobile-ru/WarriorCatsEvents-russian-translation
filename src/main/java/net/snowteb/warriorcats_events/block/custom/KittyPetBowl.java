package net.snowteb.warriorcats_events.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.util.LazyOptional;
import net.snowteb.warriorcats_events.block.entity.KittypetBowlBlockEntity;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.s2c.others.ThirstDataSyncStCPacket;
import net.snowteb.warriorcats_events.thirst.PlayerThirst;
import net.snowteb.warriorcats_events.thirst.PlayerThirstProvider;
import net.snowteb.warriorcats_events.util.ModTags;
import net.snowteb.warriorcats_events.zconfig.WCEPreyItemsConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class KittyPetBowl extends BaseEntityBlock implements SimpleWaterloggedBlock {

    /**
     * white /c
     * orange
     * magenta
     * blue
     * yellow /t
     * lime
     * pink /t
     * black (gray)
     * red /t
     */

    public static final EnumProperty<BowlState> BOWL_STATE = EnumProperty.create("bowl_state", BowlState.class);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    protected static final VoxelShape SHAPE_1 = Block.box(2.0D, 0.0D, 8.0D, 8.0D, 1.35D, 14.0D);
    protected static final VoxelShape SHAPE_2 = Block.box(2.0D, 0.0D, 2.0D, 8.0D, 1.35D, 8.0D);
    protected static final VoxelShape SHAPE_3 = Block.box(8.0D, 0.0D, 2.0D, 14.0D, 1.35D, 8.0D);
    protected static final VoxelShape SHAPE_4 = Block.box(8.0D, 0.0D, 8.0D, 14.0D, 1.35D, 14.0D);

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new KittypetBowlBlockEntity(pPos, pState);
    }

    public enum BowlState implements StringRepresentable {
        EMPTY("empty"),

        FOOD_FULL("food_full"),
        FOOD_HALF("food_half"),
        FOOD_LOW("food_low"),

        WATER_FULL("water_full"),
        WATER_HALF("water_half"),
        WATER_LOW("water_low"),

        ;

        private final String name;

        BowlState(String pName) {
            this.name = pName;
        }

        public String toString() {
            return this.name;
        }

        public String getSerializedName() {
            return this.name;
        }
    }


    public KittyPetBowl(Properties pProperties) {
        super(pProperties.sound(SoundType.STONE));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(BOWL_STATE, BowlState.EMPTY));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, BOWL_STATE);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {

        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(BOWL_STATE, BowlState.EMPTY);
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
        return switch (direction) {
            case NORTH -> SHAPE_1;
            case SOUTH -> SHAPE_3;
            case EAST -> SHAPE_2;
            case WEST -> SHAPE_4;
            default -> SHAPE_1;
        };
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return pLevel.getBlockState(pPos.below()).isFaceSturdy(pLevel, pPos.below(), Direction.UP);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {

        if (pDirection == Direction.DOWN && !this.canSurvive(pState, pLevel, pPos)) {
            return Blocks.AIR.defaultBlockState();
        }

        return super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {

        if (!pLevel.isClientSide() && pHand == InteractionHand.MAIN_HAND) {

            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);

            if (blockEntity instanceof KittypetBowlBlockEntity bowlEntity) {

                ServerLevel serverLevel = (ServerLevel) pLevel;

                boolean interactItemInHand = pPlayer.getItemInHand(pHand).is(ModTags.Items.FILL_BOWL)
                        || pPlayer.getItemInHand(pHand).is(ModTags.Items.ADDITIONAL_PREY)
                        || WCEPreyItemsConfig.PREY_ITEMS.contains(pPlayer.getItemInHand(pHand).getItem())
                        || pPlayer.getItemInHand(pHand).is(Items.WATER_BUCKET)
                        || pPlayer.getItemInHand(pHand).is(Items.POTION);

                boolean handled = false;

                if (interactItemInHand && (bowlEntity.canRefillFood() || bowlEntity.canRefillWater())) {

                    if (bowlEntity.canRefillFood() && (pPlayer.getItemInHand(pHand).is(ModTags.Items.FILL_BOWL) ||
                            pPlayer.getItemInHand(pHand).is(ModTags.Items.ADDITIONAL_PREY)
                            || WCEPreyItemsConfig.PREY_ITEMS.contains(pPlayer.getItemInHand(pHand).getItem()))) {

                        bowlEntity.performFillFood(serverLevel, getCenterOfBowl(pPos, pState));

                        if (!pPlayer.getAbilities().instabuild) {
                            pPlayer.getItemInHand(pHand).shrink(1);
                        }

                    } else if (bowlEntity.canRefillWater()){

                        if (pPlayer.getItemInHand(pHand).is(Items.WATER_BUCKET)) {
                            bowlEntity.performFillWater(serverLevel, getCenterOfBowl(pPos, pState), false);

                            if (!pPlayer.getAbilities().instabuild) {
                                pPlayer.setItemInHand(pHand, new ItemStack(Items.BUCKET));
                            }

                        }

                        if (pPlayer.getItemInHand(pHand).is(Items.POTION)
                                && PotionUtils.getPotion(pPlayer.getItemInHand(pHand)) == Potions.WATER) {
                            bowlEntity.performFillWater(serverLevel, getCenterOfBowl(pPos, pState), true);

                            if (!pPlayer.getAbilities().instabuild) {
                                pPlayer.setItemInHand(pHand, new ItemStack(Items.GLASS_BOTTLE));
                            }
                        }

                    }
                    updateBlockStateFromEntity(bowlEntity, pState, pLevel, pPos);

                    return InteractionResult.SUCCESS;

                } else {

                    var thirstCap = pPlayer.getCapability(PlayerThirstProvider.PLAYER_THIRST).orElse(new PlayerThirst());

                    if (bowlEntity.hasWater() && thirstCap.canDrink()) {
                        if (pLevel instanceof ServerLevel sLevel) {
                            if (pPlayer instanceof ServerPlayer sPlayer) {
                                bowlEntity.drinkFromBowl(sLevel, getCenterOfBowl(pPos, pState), sPlayer);
                            }
                        }
                        handled = true;
                    }

                    if (bowlEntity.hasFood() && pPlayer.getFoodData().needsFood()) {
                        if (pLevel instanceof ServerLevel sLevel) {
                            if (pPlayer instanceof ServerPlayer sPlayer) {
                                bowlEntity.eatFromBowl(sLevel, getCenterOfBowl(pPos, pState), sPlayer);
                            }
                        }
                        handled = true;
                    }

                    updateBlockStateFromEntity(bowlEntity, pState, pLevel, pPos);

                }


                if (handled) return InteractionResult.CONSUME;

            }


        }

        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }


    public void updateBlockStateFromEntity(KittypetBowlBlockEntity bowlEntity, BlockState pState, Level pLevel, BlockPos pPos) {
        BlockState newState = pState;

        if (bowlEntity.hasFood()) {
            var foodLevel = Mth.ceil(bowlEntity.getFoodLevel());

            if (foodLevel == 3) newState = newState.setValue(BOWL_STATE, BowlState.FOOD_FULL);
            if (foodLevel == 2) newState = newState.setValue(BOWL_STATE, BowlState.FOOD_HALF);
            if (foodLevel == 1) newState = newState.setValue(BOWL_STATE, BowlState.FOOD_LOW);
        } else if (bowlEntity.hasWater()) {
            var waterLevel = Mth.ceil(bowlEntity.getWaterLevel());

            if (waterLevel == 3) newState = newState.setValue(BOWL_STATE, BowlState.WATER_FULL);
            if (waterLevel == 2) newState = newState.setValue(BOWL_STATE, BowlState.WATER_HALF);
            if (waterLevel == 1) newState = newState.setValue(BOWL_STATE, BowlState.WATER_LOW);
        } else {
            if (bowlEntity.isBowlEmpty()) newState = newState.setValue(BOWL_STATE, BowlState.EMPTY);
        }

        pLevel.setBlockAndUpdate(pPos, newState);

    }

    @Override
    public boolean canPlaceLiquid(BlockGetter pLevel, BlockPos pPos, BlockState pState, Fluid pFluid) {
        return false;
    }

    public Vec3 getCenterOfBowl(BlockPos pPos, BlockState pState) {
        Direction facing = pState.getValue(FACING);

        Vec3 pos = switch (facing) {
            case WEST -> new Vec3(pPos.getX() + 0.65, pPos.getY() - 0.1, pPos.getZ() + 0.65);
            case NORTH -> new Vec3(pPos.getX() + 0.325, pPos.getY() - 0.1, pPos.getZ() + 0.65);
            case EAST -> new Vec3(pPos.getX() + 0.325, pPos.getY() - 0.1, pPos.getZ() + 0.325);
            case SOUTH -> new Vec3(pPos.getX() + 0.65, pPos.getY() - 0.1, pPos.getZ() + 0.325);
            default -> pPos.getCenter();
        };

        return pos;
    }

}
