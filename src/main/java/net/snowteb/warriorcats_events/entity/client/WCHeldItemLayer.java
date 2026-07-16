package net.snowteb.warriorcats_events.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.item.ModItems;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

@OnlyIn(Dist.CLIENT)
public class WCHeldItemLayer extends GeoRenderLayer<WCatEntity> {

    private Matrix4f capturedHeadMatrix = null;

    public WCHeldItemLayer(GeoRenderer<WCatEntity> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void renderForBone(PoseStack poseStack, WCatEntity animatable, GeoBone bone,
                              RenderType renderType, MultiBufferSource bufferSource,
                              VertexConsumer buffer, float partialTick,
                              int packedLight, int packedOverlay) {

        if (!bone.getName().equals("head")) return;

        ItemStack mainhand = animatable.getItemBySlot(EquipmentSlot.MAINHAND);
        ItemStack offhand  = animatable.getItemBySlot(EquipmentSlot.OFFHAND);

        boolean mainhandValid = !mainhand.isEmpty()
                && !mainhand.is(ModItems.WHISKERS.get())
                && !mainhand.is(ModItems.CLAWS.get());
        boolean offhandValid  = !offhand.isEmpty()
                && !offhand.is(ModItems.WHISKERS.get())
                && !offhand.is(ModItems.CLAWS.get());

        if (mainhandValid || offhandValid) {
            capturedHeadMatrix = new Matrix4f(poseStack.last().pose());
        }
    }

    @Override
    public void render(PoseStack poseStack, WCatEntity animatable, BakedGeoModel bakedModel,
                       RenderType renderType, MultiBufferSource bufferSource,
                       VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {

        if (capturedHeadMatrix == null) return;

        Matrix4f relativeTransform = new Matrix4f(poseStack.last().pose())
                .invert().mul(capturedHeadMatrix);

        ItemStack mainhand = animatable.getItemBySlot(EquipmentSlot.MAINHAND);
        ItemStack offhand  = animatable.getItemBySlot(EquipmentSlot.OFFHAND);

        if (!mainhand.isEmpty()
                && !mainhand.is(ModItems.WHISKERS.get())
                && !mainhand.is(ModItems.CLAWS.get())) {

            poseStack.pushPose();
            poseStack.mulPose(relativeTransform);
            poseStack.translate(0.0D, 0.48D, -0.85D);
            poseStack.mulPose(Axis.XP.rotationDegrees(90f));

            if (mainhand.getItem() instanceof BlockItem) {
                poseStack.scale(0.12f, 0.12f, 0.12f);
                poseStack.mulPose(Axis.XP.rotationDegrees(-90f));
                poseStack.translate(0.0D, -0.3D, 0.0D);
            } else {
                poseStack.mulPose(Axis.ZP.rotationDegrees(90f + 45f));
                poseStack.scale(0.35f, 0.35f, 0.35f);
                if (mainhand.getItem() instanceof SwordItem) {
                    poseStack.translate(0.26D, 0.34D, 0.0D);
                    poseStack.mulPose(Axis.ZP.rotationDegrees(30f));
                } else if (mainhand.getItem() instanceof ShieldItem) {
                    poseStack.mulPose(Axis.ZP.rotationDegrees(45f));
                    poseStack.mulPose(Axis.XP.rotationDegrees(-90f));
                    poseStack.mulPose(Axis.ZP.rotationDegrees(90f));
                    poseStack.translate(0.3D, 0.5D, 0.55D);
                    poseStack.mulPose(Axis.YP.rotationDegrees(-15f));
                }
            }

            Minecraft.getInstance().getItemRenderer()
                    .renderStatic(mainhand, ItemDisplayContext.NONE, packedLight,
                            packedOverlay, poseStack, bufferSource, animatable.level(), 0);
            poseStack.popPose();
        }

        if (!offhand.isEmpty()
                && !offhand.is(ModItems.WHISKERS.get())
                && !offhand.is(ModItems.CLAWS.get())) {

            poseStack.pushPose();
            poseStack.mulPose(relativeTransform);
            poseStack.translate(0.0D, 0.785D, -0.645D);
            poseStack.mulPose(Axis.XP.rotationDegrees(90f));

            if (offhand.getItem() instanceof BlockItem) {
                poseStack.scale(0.12f, 0.12f, 0.12f);
                poseStack.mulPose(Axis.XP.rotationDegrees(-90f));
                poseStack.translate(0.0D, -0.3D, 0.0D);
            } else {
                poseStack.mulPose(Axis.ZP.rotationDegrees(90f + 45f));
                poseStack.translate(0.0D, 0.0D, 0.09D);
                poseStack.scale(0.25f, 0.25f, 0.25f);
                if (offhand.getItem() instanceof ShieldItem) {
                    poseStack.mulPose(Axis.ZP.rotationDegrees(45f));
                    poseStack.mulPose(Axis.XP.rotationDegrees(-90f));
                    poseStack.mulPose(Axis.ZP.rotationDegrees(90f));
                    poseStack.translate(0.9D, 0.5D, -0.25D);
                    poseStack.mulPose(Axis.YP.rotationDegrees(50f));
                }
            }

            Minecraft.getInstance().getItemRenderer()
                    .renderStatic(offhand, ItemDisplayContext.NONE, packedLight,
                            packedOverlay, poseStack, bufferSource, animatable.level(), 0);
            poseStack.popPose();
        }

        capturedHeadMatrix = null;
    }
}
