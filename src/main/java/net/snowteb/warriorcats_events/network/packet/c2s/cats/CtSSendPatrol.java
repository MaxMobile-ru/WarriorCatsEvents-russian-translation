package net.snowteb.warriorcats_events.network.packet.c2s.cats;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.block.entity.TreeStumpBlockEntity;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CtSSendPatrol implements CustomPacketPayload {
    private final int deputyID;
    private final List<Integer> entityIds;

    private final int patrolType;

    private final List<ChunkPos> territoryToPatrol;

    private final boolean isCatSentByDeputy;

    public CtSSendPatrol(int entityId, List<Integer> entityIds, int patrolType, List<ChunkPos> territoryToPatrol, boolean isCatSentByDeputy) {
        this.deputyID = entityId;
        this.entityIds = entityIds;
        this.patrolType = patrolType;
        this.territoryToPatrol = territoryToPatrol;
        this.isCatSentByDeputy = isCatSentByDeputy;
    }

    public static void encode(CtSSendPatrol msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.deputyID);
        buf.writeInt(msg.entityIds.size());
        for (Integer entityId : msg.entityIds) {
            buf.writeInt(entityId);
        }

        buf.writeInt(msg.patrolType);

        buf.writeInt(msg.territoryToPatrol.size());
        for (ChunkPos pos : msg.territoryToPatrol) {
            buf.writeInt(pos.x);
            buf.writeInt(pos.z);
        }
        buf.writeBoolean(msg.isCatSentByDeputy);

    }

    public static CtSSendPatrol decode(FriendlyByteBuf buf) {
        int entityId = buf.readInt();

        int entityIdsSize = buf.readInt();

        List<Integer> entityIds = new ArrayList<>();
        for (int i = 0; i < entityIdsSize; i++) {
            entityIds.add(buf.readInt());
        }

        int patrolType = buf.readInt();

        int territoryToPatrolSize = buf.readInt();
        List<ChunkPos> territoryToPatrol = new ArrayList<>();
        for (int i = 0; i < territoryToPatrolSize; i++) {
            territoryToPatrol.add(new ChunkPos(buf.readInt(), buf.readInt()));
        }
        boolean isCatSentByDeputy = buf.readBoolean();

        return new CtSSendPatrol(entityId, entityIds, patrolType, territoryToPatrol, isCatSentByDeputy);
    }

    public static void handle(CtSSendPatrol msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) ctx.player();

            ServerLevel level = player.serverLevel();
            Entity e = level.getEntity(msg.deputyID);

            if (msg.isCatSentByDeputy) {
                if (!msg.entityIds.isEmpty()) {
                    Entity entity = level.getEntity(msg.entityIds.get(0));
                    sendCatByDeputy(entity, player, level, msg);
                }
                return;
            }

            if (e instanceof WCatEntity deputy) {
                String catName = deputy.hasCustomName() ? "<" + deputy.getCustomName().getString() + "> " : "<???> ";

                BlockPos homePos = deputy.getHomePosition();

                if (homePos == null || homePos.equals(BlockPos.ZERO)) {
                    player.sendSystemMessage(Component.translatable("generic.deputy_has_no_home").withStyle(ChatFormatting.RED));

                    deputy.mode = deputy.lastMode;
                    if (deputy.lastMode != WCatEntity.CatMode.SIT) {
                        deputy.setInSittingPose(false);
                    }
                    deputy.lastMode = deputy.mode;

                    return;
                }

                List<BlockPos> toPatrol = new ArrayList<>();

                for (int i = 0; i < 4; i++) {
                    ChunkPos chunk = new ChunkPos(player.chunkPosition().x + i + 1, player.chunkPosition().z + i + 1);
                    BlockPos testHeightMap = new BlockPos(chunk.getMinBlockX() + 8, 0, chunk.getMinBlockZ() + 8);
                    BlockPos center = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, testHeightMap);
                    toPatrol.add(center);
                }

                String message = switch (deputy.getPersonality()) {
                    case INDEPENDENT -> "I will get it done.";
                    case SHY -> "S-sure! I'll let them know.";
                    case FRIENDLY -> "Of course! On my way.";
                    case HUMBLE -> "You can count on me.";
                    case GRUMPY -> "I'll tell those mouse-brains.";
                    case RECKLESS -> "I'll get it done right now.";
                    case CALM -> "As you wish.";
                    case AMBITIOUS -> "Sure, leave it to me.";
                    case CAUTIOUS -> "I'll see what i can do.";
                    case NONE -> "";
                };
                player.sendSystemMessage(Component.literal(catName + message));


                Map<Integer, BlockPos> finalMap = new HashMap<>();

                int i=0;
                for (ChunkPos chunkPos : msg.territoryToPatrol) {
                    LevelChunk chunk = level.getChunk(chunkPos.x, chunkPos.z);
                    BlockPos toGo = null;
                    for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                        if (blockEntity instanceof TreeStumpBlockEntity) {
                            toGo = blockEntity.getBlockPos();
                        }
                    }

                    if (toGo == null) {
                        BlockPos testHeightMap = new BlockPos(chunkPos.getMinBlockX() + 8, 0, chunkPos.getMinBlockZ() + 8);
                        toGo = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, testHeightMap);
                    }

                    finalMap.put(i, toGo);
                    i++;
                }

                List<WCatEntity> catsToPatrol = new ArrayList<>();
                for (Integer id : msg.entityIds) {
                    Entity ent = level.getEntity(id);
                    if (ent instanceof WCatEntity) {
                        catsToPatrol.add((WCatEntity) ent);
                    }
                }

                deputy.setDeputyToSetPatrols(finalMap, catsToPatrol, msg.patrolType);

                int cats = catsToPatrol.size();
                int chunks = finalMap.size();

                Component playerMessage = Component.translatable("generic.cats_sent_to_patrol",
                        cats,
                        chunks).withStyle(ChatFormatting.GREEN);

                player.displayClientMessage(playerMessage,true);

            }

        });
    }

    private static void sendCatByDeputy(Entity e, ServerPlayer player, ServerLevel level, CtSSendPatrol msg) {
        if (e instanceof WCatEntity catSent) {
            String catName = catSent.hasCustomName() ? "<" + catSent.getCustomName().getString() + "> " : "<???> ";

            BlockPos homePos = catSent.getHomePosition();

            if (homePos == null || homePos.equals(BlockPos.ZERO)) {
                player.sendSystemMessage(Component.translatable("generic.cat_has_no_home").withStyle(ChatFormatting.RED));

                catSent.mode = catSent.lastMode;
                if (catSent.lastMode != WCatEntity.CatMode.SIT) {
                    catSent.setInSittingPose(false);
                }
                catSent.lastMode = catSent.mode;

                return;
            }

            List<BlockPos> toPatrol = new ArrayList<>();

            for (int i = 0; i < 4; i++) {
                ChunkPos chunk = new ChunkPos(player.chunkPosition().x + i + 1, player.chunkPosition().z + i + 1);
                BlockPos testHeightMap = new BlockPos(chunk.getMinBlockX() + 8, 0, chunk.getMinBlockZ() + 8);
                BlockPos center = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, testHeightMap);
                toPatrol.add(center);
            }

            String message = switch (catSent.getPersonality()) {
                case INDEPENDENT -> "I will get it done.";
                case SHY -> "S-sure! On the way.";
                case FRIENDLY -> "Of course! On my way.";
                case HUMBLE -> "You can count on me.";
                case GRUMPY -> "Fine.";
                case RECKLESS -> "I'll go right now.";
                case CALM -> "As you wish.";
                case AMBITIOUS -> "Sure, leave it to me.";
                case CAUTIOUS -> "I'll see what I can do.";
                case NONE -> "";
            };
            player.sendSystemMessage(Component.literal(catName + message));


            Map<Integer, BlockPos> finalMap = new HashMap<>();

            int i=0;
            for (ChunkPos chunkPos : msg.territoryToPatrol) {
                LevelChunk chunk = level.getChunk(chunkPos.x, chunkPos.z);
                BlockPos toGo = null;
                for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                    if (blockEntity instanceof TreeStumpBlockEntity) {
                        toGo = blockEntity.getBlockPos();
                    }
                }

                if (toGo == null) {
                    BlockPos testHeightMap = new BlockPos(chunkPos.getMinBlockX() + 8, 0, chunkPos.getMinBlockZ() + 8);
                    toGo = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, testHeightMap);
                }

                finalMap.put(i, toGo);
                i++;
            }

            if (msg.patrolType == 0) {
                catSent.setOnBorderPatrol(finalMap);
            } else {
                catSent.setOnHuntingPatrol(finalMap);
            }

            int chunks = finalMap.size();

            Component playerMessage = Component.translatable("generic.cat_sent_to_patrol",
                    catName,
                    chunks).withStyle(ChatFormatting.GREEN);

            player.displayClientMessage(playerMessage,true);

        }
    }


    public static final Type<CtSSendPatrol> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "send_patrol"));

    public static final StreamCodec<FriendlyByteBuf, CtSSendPatrol> CODEC =
            StreamCodec.of(
                    (buf, pkt) -> encode(pkt, buf),
                    buf -> decode(buf)
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
