//package net.snowteb.warriorcats_events.managers;
//
//import net.minecraft.core.BlockPos;
//import net.minecraft.core.Position;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.server.level.ServerPlayer;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.saveddata.SavedData;
//
//import java.util.*;
//
//public class SmartNavigation extends SavedData {
//
//    private Map<Integer,PathNode> worldPaths = new HashMap<>();
//    private PathNode lastPathNode = null;
//
//    public static class PathNode {
//        private final int id;
//        private final BlockPos pos;
//        private final Set<Integer> connections = new HashSet<>();
//
//        PathNode(int id, BlockPos pos) {
//            this.id = id;
//            this.pos = pos;
//        }
//
//        public void addConnection(int connection) {
//            connections.add(connection);
//        }
//    }
//
//    public static void setPath(ServerLevel level, ServerPlayer player) {
//        BlockPos location = player.blockPosition();
//        SmartNavigation data = SmartNavigation.get(level);
//
//        int id = data.worldPaths.size();
//        PathNode node = new PathNode(id, location);
//        if (data.lastPathNode != null) {
//            node.addConnection(data.lastPathNode.id);
//            data.worldPaths.get(data.lastPathNode.id).addConnection(node.id);
//        }
//        data.lastPathNode = node;
//        data.worldPaths.put(id, node);
//    }
//
//    public static void tickkkk(ServerLevel level, ServerPlayer player) {
//        if (level.dimension() != Level.OVERWORLD) return;
//
//        BlockPos location = player.blockPosition();
//        SmartNavigation data = SmartNavigation.get(level);
//        if (level.getGameTime() % 80 != 0) return;
//
//        PathNode nearest = null;
//        for (PathNode node : data.worldPaths.values()) {
//            if (nearest == null) {
//                nearest = node;
//                continue;
//            }
//
//            if ((location.distToCenterSqr(node.pos.getX(), node.pos.getY(), node.pos.getZ())
//                    <
//                    location.distToCenterSqr(nearest.pos.getX(), nearest.pos.getY(), nearest.pos.getZ()))) {
//                nearest = node;
//            }
//        }
//
//        data.lastPathNode = nearest;
//    }
//
//    public static List<BlockPos> buildPath(BlockPos current, BlockPos destination, ServerLevel level) {
//        SmartNavigation data = SmartNavigation.get(level);
//
//        List<BlockPos> path = new ArrayList<>();
//
//        path.add(current);
//
//        PathNode start = null;
//        for  (PathNode node : data.worldPaths.values()) {
//            if (start == null) {
//                if (node.pos.distToCenterSqr(current.getX(), current.getY(), current.getZ()) < 32 * 32) {
//                    start = node;
//                }
//            } else {
//                if (node.pos.distToCenterSqr(current.getX(), current.getY(), current.getZ()) <
//                        node.pos.distToCenterSqr(start.pos.getX(), start.pos.getY(), start.pos.getZ())) {
//                    start = node;
//                }
//            }
//        }
//
//        if (start == null) {
//            path.add(destination);
//            return path;
//        }
//
//        PathNode studying = start;
//        boolean shouldContinue = true;
//        while (shouldContinue) {
//
//
//            double lastDistance = Double.MAX_VALUE;
//            if (studying != null) {
//                for (int i : studying.connections) {
//                    PathNode c = data.worldPaths.get(i);
//                    BlockPos cPos = c.pos;
//
//                    if (studying.pos.distToCenterSqr(cPos.getX(), cPos.getY(), cPos.getZ()) < lastDistance) {
//
//
//                    } else {
//                        shouldContinue = false;
//                        break;
//                    }
//                }
//            }
//
//            studying =
//        }
//
//
//        path.add(destination);
//
//        return path;
//    }
//
//
//    public static SmartNavigation get(ServerLevel level) {
//        return level.getDataStorage().computeIfAbsent(
//                SmartNavigation::load,
//                SmartNavigation::new,
//                "wce_smart_paths"
//        );
//    }
//
//
//    public static SmartNavigation load(CompoundTag tag) {
//        return null;
//    }
//
//    @Override
//    public CompoundTag save(CompoundTag pCompoundTag) {
//        return null;
//    }
//
//
//}
