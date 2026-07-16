package net.snowteb.warriorcats_events.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.snowteb.warriorcats_events.entity.custom.LizardEntity;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

@OnlyIn(Dist.CLIENT)
public class LizardHeldItemLayer extends GeoRenderLayer<LizardEntity> {

    public LizardHeldItemLayer(GeoRenderer<LizardEntity> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void renderForBone(PoseStack poseStack, LizardEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {

        if (!bone.getName().equals("head")) return;

        ItemStack itemstack = animatable.getItemBySlot(EquipmentSlot.MAINHAND);

        if (!itemstack.isEmpty()) {
            if (!(itemstack.getItem() instanceof BlockItem)) {
                poseStack.pushPose();
                poseStack.translate(0.0D, 0.08D, -0.18D);
                poseStack.mulPose(Axis.XP.rotationDegrees(90f));
                poseStack.scale(0.15f, 0.15f, 0.15f);


                Minecraft.getInstance().getItemRenderer()
                        .renderStatic(itemstack, ItemDisplayContext.NONE, packedLight,
                                packedOverlay, poseStack, bufferSource, animatable.level(), 0);


                poseStack.popPose();
                buffer = bufferSource.getBuffer(renderType);
            }
        }

    }
}
