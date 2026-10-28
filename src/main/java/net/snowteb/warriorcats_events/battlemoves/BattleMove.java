package net.snowteb.warriorcats_events.battlemoves;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.managers.Sequence;
import org.jetbrains.annotations.ApiStatus;
import tocraft.walkers.api.PlayerShape;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public abstract class BattleMove <T extends BattleMove<T>> {

    private BattleMoveType<T> type;

    public final BattleMoveType<T> getType() {
        return type;
    }

    @ApiStatus.Internal
    protected final void setType(BattleMoveType<T> tDiseaseType) {
        this.type = tDiseaseType;
    }

    public void onTrigger(Player player) {
        if (canTrigger(player)) {
            if (player instanceof ServerPlayer) {
                serverTrigger(player.getUUID(), ((ServerPlayer) player).serverLevel()).run();
            } else {
                clientTrigger(player.getUUID(), player.level()).run();
            }
        }
    }

    public abstract Sequence clientTrigger(UUID playerUUID, Level level);

    protected boolean canTrigger(Player player) {
        return true;
    }

    public abstract Sequence serverTrigger(UUID playerUUID, ServerLevel level);

    public abstract int getCooldown(Player player);

    public abstract boolean tryTakeCost(Player player);

    public abstract Component getCostFailedMessage(Player player);

    public String getAnimation(ServerPlayer player) {
        return "";
    }

    public boolean animationLoops(ServerPlayer player) {
        return false;
    }

    public void unlock(Player player) {
        if (tryTakeCost(player)) {

        } else {
            player.sendSystemMessage(
                    Component.empty()
                            .append("⚠ | ")
                            .append(getCostFailedMessage(player))

                            .withStyle(ChatFormatting.RED)
            );
        }
    }



    public static List<Entity> getEntitiesInCone(Player player, double range, double halfAngleDeg,
                                                 Predicate<Entity> filter) {
        Level level = player.level();
        Vec3 origin = player.getEyePosition();
        Vec3 look = player.getLookAngle().normalize();

        AABB searchBox = new AABB(origin, origin).inflate(range);
        double cosThreshold = Math.cos(Math.toRadians(halfAngleDeg));

        return level.getEntities(player, searchBox, entity -> {
            if (entity == player || !filter.test(entity)) return false;

            Vec3 toEntity = entity.position().add(0, entity.getBbHeight() * 0.5, 0).subtract(origin);
            double distSq = toEntity.lengthSqr();
            if (distSq > range * range || distSq < 1.0E-4) return false;

            double dot = toEntity.normalize().dot(look);
            return dot >= cosThreshold;
        });
    }

    public static List<Entity> getEntitiesInCone(Player player, double range, double halfAngleDeg) {
        return getEntitiesInCone(player, range, halfAngleDeg, e -> true);
    }

    public void setShapeAnimation(ServerPlayer player) {
        LivingEntity shape = PlayerShape.getCurrentShape(player);
        if (!(shape instanceof WCatEntity cat)) return;
        BattleMoveType.MoveAnimation animation = new BattleMoveType.MoveAnimation(this.getAnimation(player), this.animationLoops(player));
        cat.setMoveAnimation(animation);
        PlayerShape.updateShapes(player, cat);
    }

    public void clearShapeAnimation(ServerPlayer player) {
        LivingEntity shape = PlayerShape.getCurrentShape(player);
        if (!(shape instanceof WCatEntity cat)) return;
        BattleMoveType.MoveAnimation animation = BattleMoveType.MoveAnimation.empty();
        cat.setMoveAnimation(animation);
        PlayerShape.updateShapes(player, cat);
    }

}
