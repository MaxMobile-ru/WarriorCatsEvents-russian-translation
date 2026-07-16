package net.snowteb.warriorcats_events.network.packet.c2s.cats;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.block.custom.NestBlock;
import net.snowteb.warriorcats_events.block.entity.NestBlockEntity;
import net.snowteb.warriorcats_events.clan.ClanData;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;

import java.util.UUID;
import java.util.function.Supplier;

public class CatHomeActionsPacket {
    private final int entityId;
    private final int key;

    public CatHomeActionsPacket(int entityId, int key) {
        this.entityId = entityId;
        this.key = key;
    }

    public static void encode(CatHomeActionsPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.entityId);
        buf.writeInt(msg.key);
    }

    public static CatHomeActionsPacket decode(FriendlyByteBuf buf) {
        return new CatHomeActionsPacket(
                buf.readInt(),
                buf.readInt()
        );
    }

    public static void handle(CatHomeActionsPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            ServerLevel level = player.serverLevel();
            Entity e = level.getEntity(msg.entityId);
            int actionID = msg.key;

            String message;


            if (e instanceof WCatEntity cat) {
                String catName = cat.hasCustomName() ? "<" + cat.getCustomName().getString() + "> " : "<???> ";

                if (actionID == 0) {
                    BlockPos homePos = cat.getHomePosition();
                    if (homePos == null || homePos.equals(BlockPos.ZERO)) {
                        player.sendSystemMessage(Component.translatable("generic.cat_has_no_home").withStyle(ChatFormatting.RED));

                        cat.mode = cat.lastMode;
                        if (cat.lastMode != WCatEntity.CatMode.SIT) {
                            cat.setInSittingPose(false);
                        }
                        cat.lastMode = cat.mode;

                        return;
                    }

                    if (cat.distanceToSqr(homePos.getX(), homePos.getY(), homePos.getZ()) > 159000) {
                        if (cat.getRank() != WCatEntity.Rank.KIT) {
                            message = switch (cat.getPersonality()) {
                                case INDEPENDENT -> "U-uh... Where are we again?";
                                case SHY -> "I-I don't want to get lost...";
                                case FRIENDLY -> "Sorry, I think we are too far.";
                                case HUMBLE -> "Sorry, I don't think I can make it.";
                                case GRUMPY ->  "Don't be a mouse-brained, have you seen where we are?";
                                case RECKLESS -> "You must be joking.";
                                case CALM -> "Sorry, but we are too far.";
                                case AMBITIOUS -> "I think we are too far.";
                                case CAUTIOUS -> "We are too far, I don't want to get lost.";
                                case NONE -> "";
                            };
                            player.sendSystemMessage(Component.literal(catName + message));
                        }

                        cat.mode = cat.lastMode;
                        if (cat.lastMode != WCatEntity.CatMode.SIT) {
                            cat.setInSittingPose(false);
                        }
                        cat.lastMode = cat.mode;

                        player.sendSystemMessage(Component.translatable("generic.home_too_far").withStyle(ChatFormatting.GRAY));
                        return;
                    }
                    if (cat.getRank() != WCatEntity.Rank.KIT) {
                        message = switch (cat.getPersonality()) {
                            case INDEPENDENT -> "I will find my way home, see you later.";
                            case SHY -> "O-okay, see you later then";
                            case FRIENDLY -> "Sure! Good luck on your journey!";
                            case HUMBLE -> "Sure, I will go back home.";
                            case GRUMPY ->  "Huh, if you say so.";
                            case RECKLESS -> "Don't get lost on your way back!";
                            case CALM -> "Sure, good luck.";
                            case AMBITIOUS -> "Pft. Fine.";
                            case CAUTIOUS -> "I will be on guard when I get back.";
                            case NONE -> "";
                        };
                        player.sendSystemMessage(Component.literal(catName + message));
                    }

                    cat.mode = WCatEntity.CatMode.WANDER;
                    cat.setInSittingPose(false);
                    cat.setOrderedToSit(false);
                    cat.returnHomeFlag = true;

                } else if (actionID == 1) {

                    int result = msg.setHome(cat, player);

                    msg.retrieveMode(cat);

                    if (result != 1) {
                        if (result == -1) {
                        }
                        if (result == 0) {
                            player.sendSystemMessage(Component.translatable("generic.no_nests_nearby").withStyle(ChatFormatting.GRAY));
                        }
                        if (result == 2) {
                            player.sendSystemMessage(Component.translatable("generic.cat_already_asigned_nest").withStyle(ChatFormatting.GRAY));
                        }
                        return;
                    }

                    if (cat.getRank() != WCatEntity.Rank.KIT) {
                        message = switch (cat.getPersonality()) {
                            case INDEPENDENT -> "I can remember on my own, thanks.";
                            case SHY -> "O-okay! I will remember.";
                            case FRIENDLY -> "Sure! This will be my home now.";
                            case HUMBLE -> "I couldn't ask for a better place.";
                            case GRUMPY ->  "This place? Huh, alright.";
                            case RECKLESS -> "This will be my place then!";
                            case CALM -> "Sure, I will remember this place.";
                            case AMBITIOUS -> "I hope we expand this place soon!";
                            case CAUTIOUS -> "If you think it's safe, then it's alright.";
                            case NONE -> "";
                        };
                        player.sendSystemMessage(Component.literal(catName + message));
                    }




                    player.sendSystemMessage(Component.translatable("generic.home_pos_set", catName).withStyle(ChatFormatting.GREEN));

                }
            }

        });
        ctx.get().setPacketHandled(true);
    }

    private int setHome(WCatEntity cat, ServerPlayer player) {

        Level level = cat.level();
        BlockPos catPos = cat.blockPosition();
        int area = 3;

        BlockPos closestPos = null;
        NestBlockEntity closestBed = null;
        double closestDistance = Double.MAX_VALUE;

        for (BlockPos pos : BlockPos.betweenClosed(
                catPos.offset(-area, -area, -area),
                catPos.offset(area, area, area))) {

            BlockState state = level.getBlockState(pos);

            if (state.getBlock() instanceof NestBlock) {

                BlockEntity be = level.getBlockEntity(pos);

                if (be instanceof NestBlockEntity mossBed) {

                    double distance = pos.getCenter().distanceToSqr(cat.position());

                    if (distance < closestDistance) {
                        closestDistance = distance;
                        closestPos = pos.immutable();
                        closestBed = mossBed;
                    }
                }
            }
        }

        if (closestBed != null) {
            UUID uuid = closestBed.getAssignedUUID();
            Vec3 centerOfBlockPos = closestPos.getCenter();

            if (uuid.equals(ClanData.EMPTY_UUID)) {

                if (!level.isClientSide()) {
                    level.sendBlockUpdated(closestPos, level.getBlockState(closestPos),
                            level.getBlockState(closestPos), 3);
                }
                cat.setHomePosition(closestPos);

                if (level instanceof ServerLevel sLevel) {

                    sLevel.sendParticles(
                            ParticleTypes.HAPPY_VILLAGER,
                            centerOfBlockPos.x, closestPos.getY() + 0.5, centerOfBlockPos.z,
                            15, 0.1, 0.5, 0.1, 0.02
                    );

                    sLevel.sendParticles(
                            ParticleTypes.HAPPY_VILLAGER,
                            cat.getX(), cat.getY() + 0.5, cat.getZ(),
                            10, 0.2, 0.5, 0.2, 0.02
                    );


                    int chunkRadius = (200 >> 4) + 1;
                    int centerChunkX = catPos.getX() >> 4;
                    int centerChunkZ = catPos.getZ() >> 4;

                    for (int dx = -chunkRadius; dx <= chunkRadius; dx++) {
                        for (int dz = -chunkRadius; dz <= chunkRadius; dz++) {

                            int chunkX = centerChunkX + dx;
                            int chunkZ = centerChunkZ + dz;

                            LevelChunk chunk = sLevel.getChunkSource().getChunkNow(chunkX, chunkZ);
                            if (chunk == null) continue;

                            for (BlockEntity be : chunk.getBlockEntities().values()) {
                                if (be instanceof NestBlockEntity mossBed) {

                                    if (be.getBlockPos().distSqr(catPos) <= (200 * 200)) {

                                        if (mossBed.getAssignedUUID().equals(cat.getUUID())) {
                                            mossBed.resetAssigned();
                                            mossBed.setChanged();

                                            Vec3 centerOfThisMossbed = mossBed.getBlockPos().getCenter();
                                            sLevel.sendParticles(
                                                    ParticleTypes.SMOKE,
                                                    centerOfThisMossbed.x, mossBed.getBlockPos().getY() + 0.5, centerOfThisMossbed.z,
                                                    10, 0.2, 0.5, 0.2, 0.02
                                            );

                                            BlockState state = level.getBlockState(mossBed.getBlockPos());
                                            level.sendBlockUpdated(mossBed.getBlockPos(), state, state, 3);
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
                closestBed.assignNest(cat.getUUID());
                closestBed.setCatName(cat.hasCustomName() ? cat.getCustomName().getString() : "Unnamed cat");
                closestBed.setChanged();

                cat.setHomePosition(closestPos);

                BlockState state = level.getBlockState(closestPos);
                level.sendBlockUpdated(closestPos, state, state, 3);
                return 1;
            } else {
                // BED OCCUPIED
                if (level instanceof ServerLevel sLevel) {

                    sLevel.sendParticles(
                            ParticleTypes.SMOKE,
                            centerOfBlockPos.x, closestPos.getY() + 0.5, centerOfBlockPos.z,
                            10, 0.2, 0.5, 0.2, 0.02
                    );

                    player.sendSystemMessage(Component.translatable("generic.nest_occupied_by", closestBed.getCatName()).withStyle(ChatFormatting.RED));

                }
                if (uuid.equals(cat.getUUID())) {
                    return 2;
                }

                return -1;
            }
        } else {
            // NO BED NEARBY
            if (level instanceof ServerLevel sLevel) {
                sLevel.sendParticles(
                        ParticleTypes.SMOKE,
                        cat.getX(), cat.getY() + 0.5, cat.getZ(),
                        10, 0.2, 0.5, 0.2, 0.02
                );
            }
            return 0;
        }
    }

    private void retrieveMode(WCatEntity cat) {
        if (cat instanceof WCatEntity) {
            cat.mode = cat.lastMode;
            if (cat.lastMode != WCatEntity.CatMode.SIT) {
                cat.setInSittingPose(false);
            }
            cat.lastMode = cat.mode;

            cat.lookAtLeaderFlag = false;
            cat.isLookingAtLeader = false;
        }
    }
}
