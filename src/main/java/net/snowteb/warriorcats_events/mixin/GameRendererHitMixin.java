package net.snowteb.warriorcats_events.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.snowteb.warriorcats_events.item.ModItems;
import net.snowteb.warriorcats_events.util.ModTags;
import net.snowteb.warriorcats_events.zconfig.WCEClientConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererHitMixin {

    @Final
    @Shadow
    Minecraft minecraft;

    @Inject(method = "pick", at = @At("TAIL"))
    private void modifyPick(float partialTicks, CallbackInfo ci) {

        Player player = minecraft.player;
        if (player == null) return;
        if (player.isCreative() || player.isSpectator()) return;

        if (!(player.getItemInHand(InteractionHand.MAIN_HAND).is(ModItems.CLAWS.get())
        || (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof SwordItem))) return;

        if (player.isShiftKeyDown()) return;

        HitResult hit = minecraft.hitResult;

        if (hit instanceof BlockHitResult blockHit) {

            BlockState state = player.level().getBlockState(blockHit.getBlockPos());

            if (state.is(ModTags.Blocks.HIT_THROUGH_BLOCKS)) {

                Vec3 eyePos = player.getEyePosition(partialTicks);
                Vec3 look = player.getLookAngle();
                double reach = minecraft.player.blockInteractionRange();

                Vec3 end = eyePos.add(look.scale(reach));

                AABB box = player.getBoundingBox().expandTowards(look.scale(reach)).inflate(1.0D);

                EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(
                        player,
                        eyePos,
                        end,
                        box,
                        e -> !e.isSpectator() && e.isPickable(),
                        reach
                );

                if (entityHit != null) {
                    minecraft.hitResult = entityHit;
                } else {
                    minecraft.hitResult = BlockHitResult.miss(end, Direction.getNearest(look.x, look.y, look.z),
                            BlockPos.containing(end));
                }
            }
        }
    }
}
