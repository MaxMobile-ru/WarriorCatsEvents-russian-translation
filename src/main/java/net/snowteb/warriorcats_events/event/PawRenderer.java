package net.snowteb.warriorcats_events.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderArmEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.entity.client.WCAccesoriesLayer;
import net.snowteb.warriorcats_events.entity.client.WCRenderer;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.item.custom.ClawsTooltip;
import software.bernie.geckolib.model.GeoModel;
import tocraft.walkers.api.PlayerShape;

@Mod.EventBusSubscriber(modid = WarriorCatsEvents.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class PawRenderer {

    @SubscribeEvent
    public static void renderHand(RenderArmEvent event) {
        if (!(PlayerShape.getCurrentShape(event.getPlayer()) instanceof WCatEntity cat)) return;
        Minecraft mc = Minecraft.getInstance();

        AbstractClientPlayer player = event.getPlayer();
        cat.setItemSlot(EquipmentSlot.HEAD, player.getItemBySlot(EquipmentSlot.HEAD));
        cat.setItemSlot(EquipmentSlot.CHEST, player.getItemBySlot(EquipmentSlot.CHEST));
        cat.setItemSlot(EquipmentSlot.LEGS, player.getItemBySlot(EquipmentSlot.LEGS));
        cat.setItemSlot(EquipmentSlot.FEET, player.getItemBySlot(EquipmentSlot.FEET));

        EntityRenderer<? super WCatEntity> entityRenderer =
                mc.getEntityRenderDispatcher().getRenderer(cat);

        if (!(entityRenderer instanceof WCRenderer renderer)) return;

        GeoModel<WCatEntity> model = renderer.getGeoModel();

        if (event.getArm() == HumanoidArm.RIGHT) {
            renderRightPaw(event, cat, renderer, model);
        } else {
            renderLeftPaw(event, cat, renderer, model);
        }

        event.setCanceled(true);

    }

    private static void renderRightPaw(RenderArmEvent event, WCatEntity cat, WCRenderer renderer, GeoModel<WCatEntity> model) {
        model.getBone("front_right_leg").ifPresentOrElse(bone -> {
            PoseStack poseStack = event.getPoseStack();

            poseStack.pushPose();

//            float bone0rotX = bone.getRotX(), bone0rotY = bone.getRotY(), bone0rotZ = bone.getRotZ();
//            float bone0posX = bone.getPosX(), bone0posY = bone.getPosY(), bone0posZ = bone.getPosZ();

            {
                poseStack.translate(-1.1, -1.5, 1.3);

                float scale = 3f;
                poseStack.scale(scale, scale, scale);

                poseStack.mulPose(Axis.XP.rotationDegrees(40));
                poseStack.mulPose(Axis.YP.rotationDegrees(60));
                poseStack.mulPose(Axis.ZP.rotationDegrees(110));

                poseStack.translate(-0, -1, 0);

                poseStack.mulPose(Axis.ZP.rotationDegrees(15));
                poseStack.mulPose(Axis.XP.rotationDegrees(25));

                float scale2 = 0.8f;
                poseStack.scale(scale2, scale2, scale2);
            }

            VertexConsumer buffer = event.getMultiBufferSource().getBuffer(
                    RenderType.entityCutoutNoCull(
                            renderer.getTextureLocation(cat)
                    )
            );

            RenderType renderType =  RenderType.entityCutoutNoCull(
                    renderer.getTextureLocation(cat)
            );

            bone.setRotX(0); bone.setRotY(0); bone.setRotZ(0);
            bone.setPosX(0); bone.setPosY(0); bone.setPosZ(0);

            model.getBone("front_right_legDOWN").ifPresent(b -> {

//                float bone1rotX = b.getRotX(), bone1rotY = b.getRotY(), bone1rotZ = b.getRotZ();
//                float bone1posX = b.getPosX(), bone1posY = b.getPosY(), bone1posZ = b.getPosZ();

                b.setRotX(0); b.setRotY(0); b.setRotZ(0);
                b.setPosX(0); b.setPosY(0); b.setPosZ(0);

                renderer.renderRecursively(
                        poseStack,
                        cat,
                        bone,
                        renderType,
                        event.getMultiBufferSource(),
                        buffer,
                        false,
                        0.0F,
                        event.getPackedLight(),
                        OverlayTexture.NO_OVERLAY,
                        1.0F,
                        1.0F,
                        1.0F,
                        1.0F
                );

                renderer.getRenderLayers().stream().filter(lay -> lay instanceof WCAccesoriesLayer)
                        .findFirst().ifPresent(layer -> {
                            layer.renderForBone(
                                    poseStack,
                                    cat,
                                    bone,
                                    renderType,
                                    event.getMultiBufferSource(),
                                    buffer,
                                    0.0F,
                                    event.getPackedLight(),
                                    OverlayTexture.NO_OVERLAY
                            );
                        });

//                bone.setRotX(bone0rotX); bone.setRotY(bone0rotY); bone.setRotZ(bone0rotZ);
//                bone.setPosX(bone0posX); bone.setPosY(bone0posY); bone.setPosZ(bone0posZ);
//                b.setRotX(bone1rotX); b.setRotY(bone1rotY); b.setRotZ(bone1rotZ);
//                b.setPosX(bone1posX); b.setPosY(bone1posY); b.setPosZ(bone1posZ);
                bone.setRotX(0); bone.setRotY(0); bone.setRotZ(0);
                bone.setPosX(0); bone.setPosY(0); bone.setPosZ(0);
                b.setRotX(0); b.setRotY(0); b.setRotZ(0);
                b.setPosX(0); b.setPosY(0); b.setPosZ(0);

            });



            poseStack.popPose();
        }, () -> model.getBakedModel(model.getModelResource(cat)));
    }

    private static void renderLeftPaw(RenderArmEvent event, WCatEntity cat, WCRenderer renderer, GeoModel<WCatEntity> model) {
        model.getBone("front_left_leg").ifPresentOrElse(bone -> {
            PoseStack poseStack = event.getPoseStack();

            poseStack.pushPose();

//            float bone0rotX = bone.getRotX(), bone0rotY = bone.getRotY(), bone0rotZ = bone.getRotZ();
//            float bone0posX = bone.getPosX(), bone0posY = bone.getPosY(), bone0posZ = bone.getPosZ();

            {
                poseStack.translate(-1.5, 1.4, 2.0);

                float scale = 3f;
                poseStack.scale(scale, scale, scale);

                poseStack.mulPose(Axis.XP.rotationDegrees(40));
                poseStack.mulPose(Axis.YP.rotationDegrees(60));
                poseStack.mulPose(Axis.ZP.rotationDegrees(110));

                poseStack.translate(-0.6, -0.3, 0.2);

                poseStack.mulPose(Axis.ZP.rotationDegrees(15));
                poseStack.mulPose(Axis.XP.rotationDegrees(25));
                poseStack.mulPose(Axis.YP.rotationDegrees(150));

                float scale2 = 0.8f;
                poseStack.scale(scale2, scale2, scale2);

                poseStack.translate(0.14, -0.0, 0.0);
            }

            VertexConsumer buffer = event.getMultiBufferSource().getBuffer(
                    RenderType.entityCutoutNoCull(
                            renderer.getTextureLocation(cat)
                    )
            );

            RenderType renderType =  RenderType.entityCutoutNoCull(
                    renderer.getTextureLocation(cat)
            );

            bone.setRotX(0); bone.setRotY(0); bone.setRotZ(0);
            bone.setPosX(0); bone.setPosY(0); bone.setPosZ(0);

            model.getBone("front_left_leg2down").ifPresent(b -> {

//                float bone1rotX = b.getRotX(), bone1rotY = b.getRotY(), bone1rotZ = b.getRotZ();
//                float bone1posX = b.getPosX(), bone1posY = b.getPosY(), bone1posZ = b.getPosZ();

                b.setRotX(0); b.setRotY(0); b.setRotZ(0);
                b.setPosX(0); b.setPosY(0); b.setPosZ(0);

                renderer.renderRecursively(
                        poseStack,
                        cat,
                        bone,
                        renderType,
                        event.getMultiBufferSource(),
                        buffer,
                        false,
                        0.0F,
                        event.getPackedLight(),
                        OverlayTexture.NO_OVERLAY,
                        1.0F,
                        1.0F,
                        1.0F,
                        1.0F
                );

                renderer.getRenderLayers().stream().filter(lay -> lay instanceof WCAccesoriesLayer)
                        .findFirst().ifPresent(layer -> {
                            layer.renderForBone(
                                    poseStack,
                                    cat,
                                    bone,
                                    renderType,
                                    event.getMultiBufferSource(),
                                    buffer,
                                    0.0F,
                                    event.getPackedLight(),
                                    OverlayTexture.NO_OVERLAY
                            );
                        });

                bone.setRotX(0); bone.setRotY(0); bone.setRotZ(0);
                bone.setPosX(0); bone.setPosY(0); bone.setPosZ(0);
                b.setRotX(0); b.setRotY(0); b.setRotZ(0);
                b.setPosX(0); b.setPosY(0); b.setPosZ(0);

            });



            poseStack.popPose();
        }, () -> model.getBakedModel(model.getModelResource(cat)));
    }



    public static void renderItemInPaw(ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack) {
        if (itemStack.getItem() instanceof BlockItem) return;
        if (itemStack.getItem() instanceof BowItem) return;
        if (itemStack.getItem() instanceof ClawsTooltip) return;

        if (!leftHand) {
            poseStack.translate(-0.71,0.25,0.3);

            poseStack.mulPose(Axis.YP.rotationDegrees(90));
            poseStack.mulPose(Axis.ZP.rotationDegrees(90));
            poseStack.mulPose(Axis.ZP.rotationDegrees(180));
            poseStack.mulPose(Axis.XP.rotationDegrees(25));

        }

    }



}
