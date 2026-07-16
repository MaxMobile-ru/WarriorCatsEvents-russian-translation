package net.snowteb.warriorcats_events.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.snowteb.warriorcats_events.diseases.kinds.BrokenPaw;
import net.snowteb.warriorcats_events.diseases.Disease;
import net.snowteb.warriorcats_events.diseases.DiseaseTypes;
import net.snowteb.warriorcats_events.diseases.Diseaseable;
import net.snowteb.warriorcats_events.entity.custom.EagleEntity;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.s2c.others.SyncDiseasesPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tocraft.walkers.api.PlayerShape;

import java.util.ArrayList;
import java.util.List;

@Mixin(Player.class)
public class PlayerEntityMixin implements Diseaseable<Player> {

    @Inject(method = "rideTick", at = @At("TAIL"))
    public void redirectRidePosition(CallbackInfo ci) {

        Player self = (Player) (Object) this;

        if (self.getVehicle() != null) {
            Entity entityVehicle = self.getVehicle();

            if (entityVehicle instanceof Player catVehicle && PlayerShape.getCurrentShape(self) instanceof WCatEntity) {
                float yawDeg;
                float pitchDeg;

                yawDeg = catVehicle.yBodyRot;
                pitchDeg = catVehicle.getXRot();

                double yaw = Math.toRadians(-yawDeg);

                double dirX = Math.sin(yaw);
                double dirZ = Math.cos(yaw);

                double distance = 0.66;

                double offsetY = 0.15;

                double pitch = Math.toRadians(pitchDeg);

                double verticalOffset = Math.sin(-pitch) * 0.4;

                double offsetX = dirX * distance + (verticalOffset / 5);
                double offsetZ = dirZ * distance + (verticalOffset / 5);

                self.setPos(
                        catVehicle.getX() + offsetX,
                        catVehicle.getY() + offsetY + verticalOffset,
                        catVehicle.getZ() + offsetZ
                );

                float sideYaw = yawDeg + 200F;

                self.setOnGround(false);
                self.setYRot(sideYaw);
                self.setYHeadRot(sideYaw);
            }

        }

    }

    @Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
    public void negateWallDamage(DamageSource source, CallbackInfoReturnable<Boolean> ci) {
        if (source.is(DamageTypes.IN_WALL)) {
            Player self = (Player) (Object) this;
            if (self.getVehicle() instanceof Player) {
                ci.setReturnValue(true);
            }
        }
    }

    @Inject(method = "wantsToStopRiding", at = @At("HEAD"), cancellable = true)
    public void blockStopRiding(CallbackInfoReturnable<Boolean> cir) {
        Player player =  (Player) (Object) this;
        if (player.getVehicle() != null) {
            if (player.getVehicle() instanceof EagleEntity eagle && eagle.isLatching()) {
                cir.setReturnValue(false);
            }
        }
    }

    @Unique
    private List<Disease<?>> wce$diseaseList = new ArrayList<>();
    @Override
    public List<Disease<?>> getList() {
        return wce$diseaseList;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        this.diseaseTick();
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void saveData(CompoundTag pCompound, CallbackInfo ci) {
        this.writeDiseasesNBT(pCompound);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readData(CompoundTag pCompound, CallbackInfo ci) {
        this.loadDiseasesNBT(pCompound);
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void syncedData(CallbackInfo ci) {
        Player player =  (Player) (Object) this;
//        player.getEntityData().define(BROKEN_PAW, false);
//        player.getEntityData().define(WRAPPED_PAW, false);
    }

    @Override
    public void onChange() {
        if (getEntity() instanceof ServerPlayer player) {
            if (player instanceof Diseaseable<?> diseaseable) {
                int id = player.getId();
                CompoundTag tag = diseaseable.diseaseData();

                ModPackets.sendToPlayer(new SyncDiseasesPacket(id, tag), player);
            }
        }

        Player player = (Player) (Object) this;
        if (!player.level().isClientSide()) {
            wce$setBrokenPaw(player);
            wce$setWrappedPaw(player);
        }
    }

    @Unique
    public void wce$setBrokenPaw(Player player) {
        boolean isBrokenPaw = this.hasDisease(DiseaseTypes.BROKEN_PAW);
//        player.getEntityData().set(BROKEN_PAW, isBrokenPaw);
        if (player instanceof ServerPlayer sPlayer) {
            if (PlayerShape.getCurrentShape(player) instanceof WCatEntity cat) {
                cat.setBrokenPaw(isBrokenPaw);
                PlayerShape.updateShapes(sPlayer, cat);
            }
        }
    }

    @Unique
    public void wce$setWrappedPaw(Player player) {
        boolean isPawWrapped = false;
        if (this.getDisease(DiseaseTypes.BROKEN_PAW) instanceof BrokenPaw brokenPaw) {
            isPawWrapped = brokenPaw.isBoneWrapped();
//            player.getEntityData().set(WRAPPED_PAW, isPawWrapped);
        }
        if (player instanceof ServerPlayer sPlayer) {
            if (PlayerShape.getCurrentShape(player) instanceof WCatEntity cat) {
                cat.setWrappedPaw(isPawWrapped);
                PlayerShape.updateShapes(sPlayer, cat);
            }
        }
    }

}
