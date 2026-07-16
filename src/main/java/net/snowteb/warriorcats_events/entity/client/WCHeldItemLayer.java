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
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.item.ModItems;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

@OnlyIn(Dist.CLIENT)
public class WCHeldItemLayer extends GeoRenderLayer<WCatEntity> {
    private final SquirrelAccessoryModel accessoryModel = new SquirrelAccessoryModel();


    public WCHeldItemLayer(GeoRenderer<WCatEntity> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void renderForBone(PoseStack poseStack, WCatEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {

        if (!bone.getName().equals("head")) return;

        ItemStack itemstack = animatable.getItemBySlot(EquipmentSlot.MAINHAND);
        ItemStack offhandStack = animatable.getItemBySlot(EquipmentSlot.OFFHAND);

        if (!itemstack.isEmpty()) {
            if (!(itemstack.is(ModItems.WHISKERS.get())
                    || itemstack.is(ModItems.CLAWS.get()))) {
                poseStack.pushPose();
                poseStack.translate(0.0D, 0.48D, -0.85D);

                poseStack.mulPose(Axis.XP.rotationDegrees(90f));

                if (itemstack.getItem() instanceof BlockItem) {
                    poseStack.scale(0.12f, 0.12f, 0.12f);
                    poseStack.mulPose(Axis.XP.rotationDegrees(-90f));
                    poseStack.translate(0.0D, -0.3D, 0.0D);
                } else {
                    poseStack.mulPose(Axis.ZP.rotationDegrees(90f + 45f));
                    poseStack.scale(0.35f, 0.35f, 0.35f);
                    if (itemstack.getItem() instanceof SwordItem) {
                        poseStack.translate(0.26D, 0.34D, -0.0D);
                        poseStack.mulPose(Axis.ZP.rotationDegrees(+30f));
                    } else if (itemstack.getItem() instanceof ShieldItem) {
                        poseStack.mulPose(Axis.ZP.rotationDegrees(+45f));
                        poseStack.mulPose(Axis.XP.rotationDegrees(-90f));
                        poseStack.mulPose(Axis.ZP.rotationDegrees(+90f));
                        poseStack.translate(0.3D, 0.5D, 0.55D);
                        poseStack.mulPose(Axis.YP.rotationDegrees(-15f));

                    }
                }
                Minecraft.getInstance().getItemRenderer()
                        .renderStatic(itemstack, ItemDisplayContext.NONE, packedLight,
                                packedOverlay, poseStack, bufferSource, animatable.level(), 0);


                poseStack.popPose();
                buffer = bufferSource.getBuffer(renderType);
            }
        }
        if (!offhandStack.isEmpty()) {
            if (!(offhandStack.is(ModItems.WHISKERS.get())
                    || offhandStack.is(ModItems.CLAWS.get()))) {

                poseStack.pushPose();
                poseStack.translate(0.0D, 0.785D, -0.645D);

                poseStack.mulPose(Axis.XP.rotationDegrees(90f));

                if (offhandStack.getItem() instanceof BlockItem) {
                    poseStack.scale(0.12f, 0.12f, 0.12f);
                    poseStack.mulPose(Axis.XP.rotationDegrees(-90f));
                    poseStack.translate(0.0D, -0.3D, 0.0D);
                } else {
                    poseStack.mulPose(Axis.ZP.rotationDegrees(90f + 45f));
                    poseStack.translate(0.0D, -0.0D, 0.09D);
                    poseStack.scale(0.25f, 0.25f, 0.25f);
                    if (offhandStack.getItem() instanceof ShieldItem) {
                        poseStack.mulPose(Axis.ZP.rotationDegrees(+45f));
                        poseStack.mulPose(Axis.XP.rotationDegrees(-90f));
                        poseStack.mulPose(Axis.ZP.rotationDegrees(+90f));
                        poseStack.translate(0.9D, 0.5D, -0.25D);
                        poseStack.mulPose(Axis.YP.rotationDegrees(50f));
                    }
                }
                Minecraft.getInstance().getItemRenderer()
                        .renderStatic(offhandStack, ItemDisplayContext.NONE, packedLight,
                                packedOverlay, poseStack, bufferSource, animatable.level(), 0);


                poseStack.popPose();
                buffer = bufferSource.getBuffer(renderType);
            }
        }


    }
}
