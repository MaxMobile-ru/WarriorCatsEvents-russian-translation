package net.snowteb.warriorcats_events.block.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.snowteb.warriorcats_events.block.entity.TreeStumpBlockEntity;
import net.snowteb.warriorcats_events.clan.ClanData;
import net.snowteb.warriorcats_events.clan.WCEPlayerData;
import net.snowteb.warriorcats_events.clan.WCEPlayerDataProvider;
import net.snowteb.warriorcats_events.particles.WCEParticles;
import net.snowteb.warriorcats_events.zconfig.WCEServerConfig;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class TreeStumpBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    protected static final VoxelShape BASE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 11.0D, 12.0D);

    protected static final VoxelShape SHAPE = Shapes.or(BASE);

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public enum StumpVariant implements StringRepresentable {
        OAK("oak"),
        SPRUCE("spruce"),
        BIRCH("birch"),
        JUNGLE("jungle"),
        ACACIA("acacia"),
        DARK_OAK("dark_oak"),
        CHERRY("cherry"),
        ;

        private final String name;

        StumpVariant(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }

    public static final EnumProperty<StumpVariant> VARIANT =
            EnumProperty.create("variant", StumpVariant.class);

    public TreeStumpBlock(Properties pProperties) {
        super(pProperties.strength(-1.0F, 3600000.0F).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(VARIANT, StumpVariant.OAK)
                .setValue(WATERLOGGED, false));
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, VARIANT, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());

        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED)
                ? Fluids.WATER.getSource(false)
                : super.getFluidState(pState);
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
        if (pState.getValue(WATERLOGGED)) {
            pLevel.scheduleTick(pPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }
        return super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
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
        return SHAPE;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pLevel.getBlockEntity(pPos) instanceof TreeStumpBlockEntity treeStumpBlockEntity) {
            UUID clanUUID = treeStumpBlockEntity.getOwnerClanUUID();
            if (clanUUID != ClanData.EMPTY_UUID) {
                ClanData data = ClanData.get(pLevel.getServer().overworld());
                ClanData.Clan clan =  data.getClan(clanUUID);
                if (clan != null) {
                    if (clan.claimedTerritory.containsKey(treeStumpBlockEntity.getTerritoryPos())) {
                        Vec3 position = pPos.getCenter();
                        if (pRandom.nextFloat() < 0.5f) {
                            pLevel.sendParticles(
                                    WCEParticles.HERBS.get(),
                                    position.x, position.y - 0.05, position.z,
                                    3, 0.0, 0.0, 0.0, 0.005);
                        }

                        int time = clan.claimedTerritory.get(treeStumpBlockEntity.getTerritoryPos()).timeToReclaim;
                        treeStumpBlockEntity.setTimeUntilRenewScent(time);
                        pLevel.sendBlockUpdated(pPos, pState, pState, 2);

                        if (time <= 0) {
                            AABB box = new AABB(pPos.getX() - 3, pPos.getY() - 2, pPos.getZ() - 3, pPos.getX() + 3, pPos.getY() + 2, pPos.getZ() + 3);
                            List<Player> list = pLevel.getEntitiesOfClass(Player.class, box, player -> {
                                if (!player.isShiftKeyDown()) return false;

                                UUID playerClanUUID = player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                                        .map(WCEPlayerData::getCurrentClanUUID).orElse(ClanData.EMPTY_UUID);
                                if (playerClanUUID.equals(treeStumpBlockEntity.getOwnerClanUUID())) {
                                    if (data.canCommandWarriors(clan, player.getUUID())) {
                                        return true;
                                    }
                                }
                                return false;
                            });

                            if (!list.isEmpty()) {
                                int progress = treeStumpBlockEntity.getProgressToReclaim();
                                treeStumpBlockEntity.setProgressToReclaim(progress + 10);

                                for (Player player : list) {
                                    Vec3 pos = player.position();
                                    pLevel.sendParticles(
                                            WCEParticles.HERBS.get(),
                                            pos.x, pos.y - 0.05, pos.z,
                                            1, 0.0, 0.0, 0.0, 0.005);
                                }

                                if (treeStumpBlockEntity.getProgressToReclaim() >= 100) {

                                    double distance = 20;
                                    String closestPlayer = "";
                                    String closestMorphName = "";

                                    for (Player player : list) {
                                        Component message = Component.translatable("blockinteraction.treestump.territoryremarked",
                                                clan.claimedTerritory.get(treeStumpBlockEntity.getTerritoryPos()).name);

                                        player.sendSystemMessage(message.copy().withStyle(ChatFormatting.GREEN));

                                        if (pPos.getCenter().distanceTo(player.position()) < distance) {
                                            closestPlayer = player.getName().getString();
                                            closestMorphName = player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                                                    .map(WCEPlayerData::getMorphName).orElse(closestPlayer);
                                            distance = pPos.getCenter().distanceTo(player.position());
                                        }
                                    }

                                    data.reclaimChunk(clan.clanUUID, treeStumpBlockEntity.getTerritoryPos(), pLevel);
                                    Vec3 pos = pPos.getCenter();
                                    pLevel.sendParticles(
                                            ParticleTypes.HAPPY_VILLAGER,
                                            pos.x, pos.y - 0.05, pos.z,
                                            10, 0.3, 0.3, 0.3, 0.005);
                                    pLevel.playSound(null, pPos, SoundEvents.AZALEA_LEAVES_BREAK,
                                            SoundSource.BLOCKS, 0.7F, 1.0f);

                                    if (clan.claimedTerritory.get(treeStumpBlockEntity.getTerritoryPos()) != null) {
                                        float percentage = (float) clan.claimedTerritory.get(treeStumpBlockEntity.getTerritoryPos()).time
                                                / (WCEServerConfig.SERVER.MAX_TERRITORY_TIME.get()*20*60);
                                        String percentageString = String.format("%.1f", percentage*100) + "%";

                                        ChunkPos claimedPos = clan.claimedTerritory.get(treeStumpBlockEntity.getTerritoryPos()).chunkPos;

//                                        Component log = Component.empty()
//                                                .append(Component.literal(closestMorphName).withStyle(ChatFormatting.AQUA))
//                                                .append(Component.literal("(").withStyle(ChatFormatting.DARK_GRAY))
//                                                .append(Component.literal(closestPlayer).withStyle(ChatFormatting.DARK_GRAY))
//                                                .append(Component.literal(")").withStyle(ChatFormatting.DARK_GRAY))
//                                                .append(" has remarked territory ")
//                                                .append(Component.literal(clan.claimedTerritory.get(treeStumpBlockEntity.getTerritoryPos()).name + " ").withStyle(ChatFormatting.GOLD))
//                                                .append("at ")
//                                                .append(Component.literal(
//                                                        String.format("X=%d, Z=%d", claimedPos.x, claimedPos.z)
//                                                ).withStyle(ChatFormatting.AQUA))
//                                                .append(", new percentage: ")
//                                                .append(Component.literal(percentageString).withStyle(ChatFormatting.GOLD));

                                        Component log1 = Component.translatable("blockinteraction.treestump.territoryremarkedlog",
                                                Component.empty()
                                                .append(Component.literal(closestMorphName).withStyle(ChatFormatting.AQUA))
                                                .append(Component.literal("(").withStyle(ChatFormatting.DARK_GRAY))
                                                .append(Component.literal(closestPlayer).withStyle(ChatFormatting.DARK_GRAY))
                                                .append(Component.literal(")").withStyle(ChatFormatting.DARK_GRAY)),
                                                Component.literal(clan.claimedTerritory.get(treeStumpBlockEntity.getTerritoryPos()).name).withStyle(ChatFormatting.GOLD),
                                                Component.literal(
                                                        String.format("X=%d, Z=%d", claimedPos.x, claimedPos.z)
                                                ).withStyle(ChatFormatting.AQUA),
                                                Component.literal(percentageString).withStyle(ChatFormatting.GOLD)
                                                );


                                        data.registerLog(pLevel.getServer().overworld(), clanUUID, log1);

                                    }

                                }

                                pLevel.sendBlockUpdated(pPos, pState, pState, 2);

                            } else {
                                if (treeStumpBlockEntity.getProgressToReclaim() != 0) {
                                    treeStumpBlockEntity.setProgressToReclaim(0);
                                    pLevel.sendBlockUpdated(pPos, pState, pState, 2);
                                }
                            }
                        }
                    } else {
                        if (treeStumpBlockEntity.getTimeUntilRenewScent() != (WCEServerConfig.SERVER.MAX_TERRITORY_TIME.get()*20*60)/8) {
                            treeStumpBlockEntity.setTimeUntilRenewScent((WCEServerConfig.SERVER.MAX_TERRITORY_TIME.get()*20*60)/8);
                            pLevel.sendBlockUpdated(pPos, pState, pState, 2);
                        }
                    }
                } else {
                    if (treeStumpBlockEntity.getTimeUntilRenewScent() != (WCEServerConfig.SERVER.MAX_TERRITORY_TIME.get()*20*60)/8) {
                        treeStumpBlockEntity.setTimeUntilRenewScent((WCEServerConfig.SERVER.MAX_TERRITORY_TIME.get()*20*60)/8);
                        pLevel.sendBlockUpdated(pPos, pState, pState, 2);
                    }
                }
            }
        }
        pLevel.scheduleTick(pPos, this, 40);
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston) {
        super.onPlace(pState, pLevel, pPos, pOldState, pMovedByPiston);

        if (!pLevel.isClientSide()) {
            pLevel.scheduleTick(pPos, this, 40);

            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof TreeStumpBlockEntity treeStumpBlockEntity) {
                treeStumpBlockEntity.setTerritoryPos(pLevel.getChunkAt(pPos).getPos());
            }
        }
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new TreeStumpBlockEntity(pPos, pState);
    }

    @Override
    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        return false;
    }
}
