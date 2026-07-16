package net.snowteb.warriorcats_events.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tocraft.walkers.api.PlayerShape;

@Mixin(AvoidEntityGoal.class)
public abstract class CatAvoidMixin {

    @Shadow
    protected PathfinderMob mob;
    @Shadow @Nullable
    protected LivingEntity toAvoid;

    @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
    private void extraCanUse(CallbackInfoReturnable<Boolean> cir) {

        if (!(mob instanceof Cat)) return;

        if (!(toAvoid instanceof Player player)) return;

        if (PlayerShape.getCurrentShape(player) instanceof WCatEntity) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
