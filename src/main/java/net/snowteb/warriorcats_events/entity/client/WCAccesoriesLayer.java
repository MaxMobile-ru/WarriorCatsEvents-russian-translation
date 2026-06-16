package net.snowteb.warriorcats_events.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.compat.CompatibilitiesClient;
import net.snowteb.warriorcats_events.entity.custom.LizardEntity;
import net.snowteb.warriorcats_events.entity.custom.WCGenetics;
import net.snowteb.warriorcats_events.entity.custom.WCatEntity;
import net.snowteb.warriorcats_events.item.ModItems;
import net.snowteb.warriorcats_events.item.custom.ButterflyWingArmorItem;
import net.snowteb.warriorcats_events.item.custom.CatSocksArmorItem;
import net.snowteb.warriorcats_events.item.custom.CollarArmorItem;
import net.snowteb.warriorcats_events.item.custom.FeathersArmorItem;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.RenderUtil;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class WCAccesoriesLayer extends GeoRenderLayer<WCatEntity> {
    private final SquirrelAccessoryModel squirrelAccessoryModel = new SquirrelAccessoryModel();

    private final AccessoryModel flowerCrownAccesoryModel = new AccessoryModel(
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "geo/wcat.flowercrown.geo.json"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/accessories/flowercrown.png")
    );

    private final AccessoryModel leafManeAccesoryModel = new AccessoryModel(
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "geo/leaf_mane.geo.json"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/accessories/leaf_mane.png")
    );

    private final AccessoryModel flowerAccesoryModel = new AccessoryModel(
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "geo/wcat.floweraccessory.geo.json"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/accessories/flower_accessory.png")
    );

    private final AccessoryModel tailVinesModel = new AccessoryModel(
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "geo/wcat.tail_vine_wrap.geo2.json"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/accessories/green_texture.png")
    );

    private final AccessoryModel socksModel = new AccessoryModel(
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "geo/wcat.socks.geo.json"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/accessories/cat_socks.png")
    );

    private final AccessoryModel leafShieldModel = new AccessoryModel(
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "geo/wcat.leaf_shield.geo.json"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/accessories/leaf_shield.png")
    );

    private final AccessoryModel tailLichenModel = new AccessoryModel(
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "geo/wcat.tail_lichen.geo.json"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/accessories/tail_lichen.png")
    );

    private final AccessoryModel dandelionModel = new AccessoryModel(
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "geo/wcat.head_dandelion.geo.json"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/accessories/head_dandelion.png")
    );


    private final AccessoryModel bodyFeathersModelUp = new AccessoryModel(
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "geo/wcat.body_feathers_up.geo.json"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/accessories/chicken_feathers.png")
    );
    private final AccessoryModel bodyFeathersModelMid = new AccessoryModel(
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "geo/wcat.body_feathers_mid.geo.json"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/accessories/chicken_feathers.png")
    );
    private final AccessoryModel bodyFeathersModelDown = new AccessoryModel(
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "geo/wcat.body_feathers_down.geo.json"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/accessories/chicken_feathers.png")
    );

    private final AccessoryModel bodyVultureFeathersModelMid = new AccessoryModel(
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "geo/wcat.body_vulture_feathers_mid.geo.json"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/accessories/black_vulture_feathers.png")
    );
    private final AccessoryModel bodyVultureFeathersModelUp = new AccessoryModel(
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "geo/wcat.body_vulture_feathers_up.geo.json"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/accessories/black_vulture_feathers.png")
    );

    private final AccessoryModel collarModel = new AccessoryModel(
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "geo/wcat.collar.geo.json"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/accessories/collar_0.png")
    );

    private final AccessoryModel berryModel = new AccessoryModel(
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "geo/wcat.head_berry.geo.json"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/accessories/head_sweetberry.png")
    );

    private final AccessoryModel catHatModel = new AccessoryModel(
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "geo/wcat.hat.geo.json"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/accessories/cat_hat.png")
    );


    private final AccessoryModel catBowModel = new AccessoryModel(
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "geo/wcat.head_bow.geo.json"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/accessories/red_bow.png")
    );

    private final AccessoryModel skullMaskModel = new AccessoryModel(
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "geo/wcat.skull_mask.geo.json"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/accessories/skull_mask.png")
    );

    private final AccessoryModel pawWrapModel0 = new AccessoryModel(
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "geo/wcat.paw_wrap_0.geo.json"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/accessories/paw_wrap.png")
    );
    private final AccessoryModel pawWrapModel1 = new AccessoryModel(
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "geo/wcat.paw_wrap_1.geo.json"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/accessories/paw_wrap.png")
    );

    private final AccessoryModel butterflyWingModel = new AccessoryModel(
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "geo/wcat.head_butterfly_wing.geo.json"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/accessories/bluemorphowing.png")
    );


    private final ElytraModel elytraModel = new ElytraModel();


    private final AccessoryRenderer crownRenderer;
    private final AccessoryRenderer leafmaneRenderer;
    private final AccessoryRenderer flowerAccesoryRenderer;
    private final AccessoryRenderer tailVinesRenderer;
    private final AccessoryRenderer socksRenderer;
    private final AccessoryRenderer leafShieldRenderer;
    private final AccessoryRenderer tailLichenRenderer;
    private final AccessoryRenderer dandelionRenderer;
    private final AccessoryRenderer bodyFeathersRenderer;
    private final AccessoryRenderer bodyVultureFeathersRenderer;
    private final AccessoryRenderer collarRenderer;
    private final AccessoryRenderer berryRenderer;
    private final AccessoryRenderer elytraRenderer;
    private final AccessoryRenderer catHatRenderer;
    private final AccessoryRenderer catBowRenderer;
    private final AccessoryRenderer skullMaskRenderer;
    private final AccessoryRenderer pawWrapRenderer;
    private final AccessoryRenderer butterflyWingRenderer;
    private final LizardRenderer lizRenderer;




    public WCAccesoriesLayer(GeoRenderer<WCatEntity> entityRendererIn, EntityRendererProvider.Context context) {
        super(entityRendererIn);
        this.crownRenderer = new AccessoryRenderer(context, flowerCrownAccesoryModel);
        this.leafmaneRenderer = new AccessoryRenderer(context, leafManeAccesoryModel);
        this.flowerAccesoryRenderer = new AccessoryRenderer(context, flowerAccesoryModel);
        this.tailVinesRenderer = new AccessoryRenderer(context, tailVinesModel);
        this.socksRenderer = new AccessoryRenderer(context, socksModel);
        this.leafShieldRenderer = new AccessoryRenderer(context, leafShieldModel);
        this.tailLichenRenderer = new AccessoryRenderer(context, tailLichenModel);
        this.dandelionRenderer = new AccessoryRenderer(context, dandelionModel);

        this.bodyFeathersRenderer = new AccessoryRenderer(context, bodyFeathersModelUp);
        this.bodyVultureFeathersRenderer = new AccessoryRenderer(context, bodyVultureFeathersModelUp);

        this.collarRenderer = new AccessoryRenderer(context, collarModel);

        this.berryRenderer = new AccessoryRenderer(context, berryModel);
        this.catHatRenderer = new AccessoryRenderer(context, catHatModel);
        this.catBowRenderer = new AccessoryRenderer(context, catBowModel);
        this.skullMaskRenderer = new AccessoryRenderer(context, skullMaskModel);

        this.elytraRenderer = new AccessoryRenderer(context, elytraModel);
        this.pawWrapRenderer = new AccessoryRenderer(context, pawWrapModel0);
        this.butterflyWingRenderer = new AccessoryRenderer(context, butterflyWingModel);
        this.lizRenderer = new LizardRenderer(context);
    }

    private final Map<String, BoneMatrix> capturedBoneMatrices = new HashMap<>();

    private record BoneMatrix(Matrix4f matrix4f, GeoBone bone){};

    @Override
    public void renderForBone(PoseStack poseStack, WCatEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {

        String name = bone.getName();
        switch (name) {
            case "head", "tail", "tailsub", "tail2",
                 "moreup", "up", "bodydown2",
                 "front_left_leg2down", "front_right_legDOWN",
                 "front_right_legUPPER" ->
                    capturedBoneMatrices.put(name, new  BoneMatrix(new Matrix4f(poseStack.last().pose()), bone));
        }
    }

    @Override
    public void render(PoseStack poseStack, WCatEntity animatable, BakedGeoModel bK, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {

        if (capturedBoneMatrices.isEmpty()) return;

        Matrix4f entityMatrix = new Matrix4f(poseStack.last().pose());

        if ((capturedBoneMatrices.get("bodydown2") != null) && (animatable.hasCustomName() && animatable.getCustomName().getString().contains("squirrellover"))) {
            var bakedModel = squirrelAccessoryModel.getBakedModel(squirrelAccessoryModel.getModelResource(animatable));

            BoneMatrix current = capturedBoneMatrices.get("bodydown2");

            poseStack.pushPose();
            poseStack.mulPose(getRelative(entityMatrix, current));

            RenderType accessoryRenderType = RenderType.entityCutout(squirrelAccessoryModel.getTextureResource(animatable));

            VertexConsumer accessoryBuffer = bufferSource.getBuffer(accessoryRenderType);
            RenderUtil.translateToPivotPoint(poseStack, current.bone);
            RenderUtil.rotateMatrixAroundBone(poseStack, current.bone);


            poseStack.translate(0.0D, -0.185D, -0.3D);

            float scale = 0.15F;
            poseStack.scale(scale, scale, scale);
            poseStack.mulPose(Axis.XP.rotationDegrees(180f));


            float interpolatedYaw = Mth.lerp(partialTick, animatable.yBodyRotO, animatable.yBodyRot);


            poseStack.mulPose(Axis.YP.rotationDegrees(interpolatedYaw + 180f));
            getRenderer().reRender(
                    bakedModel,
                    poseStack,
                    bufferSource,
                    animatable,
                    accessoryRenderType,
                    accessoryBuffer,
                    partialTick,
                    packedLight,
                    packedOverlay,
                    0xFFFFFFFF
            );

            poseStack.popPose();
            buffer = bufferSource.getBuffer(renderType);
        }

        boolean hasCrown = animatable
                .getItemBySlot(EquipmentSlot.HEAD)
                .is(ModItems.FLOWER_CROWN.get());
        boolean hasMane = animatable
                .getItemBySlot(EquipmentSlot.HEAD)
                .is(ModItems.LEAF_MANE.get());
        boolean hasFlower = animatable
                .getItemBySlot(EquipmentSlot.HEAD)
                .is(ModItems.HEAD_FLOWER.get());
        boolean hasDandelion = animatable
                .getItemBySlot(EquipmentSlot.HEAD)
                .is(ModItems.HEAD_DANDELION.get());
        boolean hasLeaf = animatable
                .getItemBySlot(EquipmentSlot.HEAD)
                .is(ModItems.HEAD_LEAF.get());

        boolean hasTailVines = animatable
                .getItemBySlot(EquipmentSlot.LEGS)
                .is(ModItems.TAIL_VINES.get());
        boolean hasDrapedTailVines = animatable
                .getItemBySlot(EquipmentSlot.LEGS)
                .is(ModItems.DRAPED_TAIL_VINES.get());

        boolean hasHat = animatable
                .getItemBySlot(EquipmentSlot.HEAD)
                .is(ModItems.CAT_HAT.get());

        boolean hasChestBow = animatable
                .getItemBySlot(EquipmentSlot.CHEST)
                .is(ModItems.CAT_BLACK_BOW.get());

        boolean hasSkullMask = animatable
                .getItemBySlot(EquipmentSlot.HEAD)
                .is(ModItems.SKULL_MASK.get());




        if (CompatibilitiesClient.hasCuriosItem(animatable.getPlayerBoundUuid(), ModItems.FLOWER_CROWN.get())) hasCrown = true;
        if (CompatibilitiesClient.hasCuriosItem(animatable.getPlayerBoundUuid(), ModItems.LEAF_MANE.get())) hasMane = true;
        if (CompatibilitiesClient.hasCuriosItem(animatable.getPlayerBoundUuid(), ModItems.HEAD_FLOWER.get())) hasFlower = true;
        if (CompatibilitiesClient.hasCuriosItem(animatable.getPlayerBoundUuid(), ModItems.HEAD_DANDELION.get())) hasDandelion = true;
        if (CompatibilitiesClient.hasCuriosItem(animatable.getPlayerBoundUuid(), ModItems.HEAD_LEAF.get())) hasLeaf = true;
        if (CompatibilitiesClient.hasCuriosItem(animatable.getPlayerBoundUuid(), ModItems.TAIL_VINES.get())) hasTailVines = true;
        if (CompatibilitiesClient.hasCuriosItem(animatable.getPlayerBoundUuid(), ModItems.DRAPED_TAIL_VINES.get())) hasDrapedTailVines = true;
        if (CompatibilitiesClient.hasCuriosItem(animatable.getPlayerBoundUuid(), ModItems.CAT_HAT.get())) hasHat = true;
        if (CompatibilitiesClient.hasCuriosItem(animatable.getPlayerBoundUuid(), ModItems.CAT_BLACK_BOW.get())) hasChestBow = true;
        if (CompatibilitiesClient.hasCuriosItem(animatable.getPlayerBoundUuid(), ModItems.SKULL_MASK.get())) hasSkullMask = true;

        boolean hasBerry = animatable
                .getItemBySlot(EquipmentSlot.HEAD)
                .is(ModItems.HEAD_SWEETBERRY.get()) || animatable
                .getItemBySlot(EquipmentSlot.HEAD)
                .is(ModItems.HEAD_GLOWBERRY.get());

        if (CompatibilitiesClient.hasCuriosItem(animatable.getPlayerBoundUuid(), ModItems.HEAD_GLOWBERRY.get())
                || CompatibilitiesClient.hasCuriosItem(animatable.getPlayerBoundUuid(), ModItems.HEAD_SWEETBERRY.get())) {
            hasBerry = true;
        }

        boolean hasBow = animatable
                .getItemBySlot(EquipmentSlot.HEAD)
                .is(ModItems.CAT_PINK_BOW.get()) || animatable
                .getItemBySlot(EquipmentSlot.HEAD)
                .is(ModItems.CAT_RED_BOW.get());

        boolean hasLegWrap = animatable.isWrappedPaw()
                || animatable.getItemBySlot(EquipmentSlot.FEET).is(ModItems.LEG_WRAP.get())
                || CompatibilitiesClient.hasCuriosItem(animatable.getPlayerBoundUuid(), ModItems.LEG_WRAP.get());

        if (CompatibilitiesClient.hasCuriosItem(animatable.getPlayerBoundUuid(), ModItems.CAT_PINK_BOW.get())
                || CompatibilitiesClient.hasCuriosItem(animatable.getPlayerBoundUuid(), ModItems.CAT_RED_BOW.get())) {
            hasBow = true;
        }

        if (capturedBoneMatrices.get("head") != null) {

            BoneMatrix current =  capturedBoneMatrices.get("head");

            if (hasCrown) {
                var bakedModel = flowerCrownAccesoryModel.getBakedModel(flowerCrownAccesoryModel.getModelResource(animatable));

                poseStack.pushPose();
                poseStack.mulPose(getRelative(entityMatrix, current));


                poseStack.translate(0.0D, 0.61D, -0.6D);
                poseStack.mulPose(Axis.XP.rotationDegrees(15f));
                float scale = 0.87f;
                poseStack.scale(scale, scale, scale);


                RenderType accessoryRenderType = RenderType.entityCutout(flowerCrownAccesoryModel.getTextureResource(animatable));

                VertexConsumer accessoryBuffer = bufferSource.getBuffer(accessoryRenderType);

                float interpolatedYaw = Mth.lerp(partialTick, animatable.yBodyRotO, animatable.yBodyRot);
                poseStack.mulPose(Axis.YP.rotationDegrees(interpolatedYaw + 180f));


                crownRenderer.reRender(
                        bakedModel,
                        poseStack,
                        bufferSource,
                        animatable,
                        accessoryRenderType,
                        accessoryBuffer,
                        partialTick,
                        packedLight,
                        packedOverlay,
                        0xFFFFFFFF
                );

                poseStack.popPose();
                buffer = bufferSource.getBuffer(renderType);
            }
            if (hasMane) {
                var bakedModel = leafManeAccesoryModel.getBakedModel(leafManeAccesoryModel.getModelResource(animatable));

                poseStack.pushPose();
                poseStack.mulPose(getRelative(entityMatrix, current));

                poseStack.translate(0.0D, 0.50D, -0.349D);
                poseStack.mulPose(Axis.XP.rotationDegrees(-82f));
                float scale = 0.95f;
                poseStack.scale(scale, scale, scale);


                RenderType accessoryRenderType = RenderType.entityCutout(leafManeAccesoryModel.getTextureResource(animatable));

                VertexConsumer accessoryBuffer = bufferSource.getBuffer(accessoryRenderType);

                float interpolatedYaw = Mth.lerp(partialTick, animatable.yBodyRotO, animatable.yBodyRot);
                poseStack.mulPose(Axis.YP.rotationDegrees(interpolatedYaw + 180f));


                leafmaneRenderer.reRender(
                        bakedModel,
                        poseStack,
                        bufferSource,
                        animatable,
                        accessoryRenderType,
                        accessoryBuffer,
                        partialTick,
                        packedLight,
                        packedOverlay,
                        0xFFFFFFFF
                );

                poseStack.popPose();
                buffer = bufferSource.getBuffer(renderType);
            }
            if (hasDandelion) {
                var bakedModel = dandelionModel.getBakedModel(dandelionModel.getModelResource(animatable));

                poseStack.pushPose();
                poseStack.mulPose(getRelative(entityMatrix, current));

                poseStack.translate(0.098D, 0.64D, -0.51D);
                poseStack.mulPose(Axis.ZP.rotationDegrees(-92f));
                poseStack.mulPose(Axis.YP.rotationDegrees(25f));
                poseStack.mulPose(Axis.XP.rotationDegrees(5f));

                float scale = 0.8f;
                poseStack.scale(scale, scale, scale);


                RenderType accessoryRenderType = RenderType.entityCutout(dandelionModel.getTextureResource(animatable));

                VertexConsumer accessoryBuffer = bufferSource.getBuffer(accessoryRenderType);

                float interpolatedYaw = Mth.lerp(partialTick, animatable.yBodyRotO, animatable.yBodyRot);
                poseStack.mulPose(Axis.YP.rotationDegrees(interpolatedYaw + 180f));


                dandelionRenderer.reRender(
                        bakedModel,
                        poseStack,
                        bufferSource,
                        animatable,
                        accessoryRenderType,
                        accessoryBuffer,
                        partialTick,
                        packedLight,
                        packedOverlay,
                        0xFFFFFFFF
                );

                poseStack.popPose();
                buffer = bufferSource.getBuffer(renderType);
            }
            if (hasFlower) {
                var bakedModel = flowerAccesoryModel.getBakedModel(flowerAccesoryModel.getModelResource(animatable));

                poseStack.pushPose();
                poseStack.mulPose(getRelative(entityMatrix, current));

                poseStack.translate(-0.135D, 0.70D, -0.519D);

                float scale = 1.00f;
                poseStack.scale(scale, scale, scale);

                poseStack.mulPose(Axis.ZP.rotationDegrees(62f));

                RenderType accessoryRenderType = RenderType.entityCutout(flowerAccesoryModel.getTextureResource(animatable));

                VertexConsumer accessoryBuffer = bufferSource.getBuffer(accessoryRenderType);

                float interpolatedYaw = Mth.lerp(partialTick, animatable.yBodyRotO, animatable.yBodyRot);
                poseStack.mulPose(Axis.YP.rotationDegrees(interpolatedYaw + 180f));


                flowerAccesoryRenderer.reRender(
                        bakedModel,
                        poseStack,
                        bufferSource,
                        animatable,
                        accessoryRenderType,
                        accessoryBuffer,
                        partialTick,
                        packedLight,
                        packedOverlay,
                        0xFFFFFFFF
                );

                poseStack.popPose();
                buffer = bufferSource.getBuffer(renderType);

            }
            if (hasBerry) {
                ItemStack stack = animatable.getItemBySlot(EquipmentSlot.HEAD);
                ItemStack glowberry = CompatibilitiesClient.getCuriosItem(animatable.getPlayerBoundUuid(), ModItems.HEAD_GLOWBERRY.get());
                ItemStack sweetberry = CompatibilitiesClient.getCuriosItem(animatable.getPlayerBoundUuid(), ModItems.HEAD_SWEETBERRY.get());

                if (!glowberry.isEmpty()) {
                    stack = glowberry;
                } else if (!sweetberry.isEmpty()) {
                    stack = sweetberry;
                }

                int light = packedLight;

                if (stack.is(ModItems.HEAD_GLOWBERRY.get())) {
                    berryModel.texture = AccessoryModel.BERRY_TEXTURES[1];
                    light = 255;
                }
                else berryModel.texture = AccessoryModel.BERRY_TEXTURES[0];

                var bakedModel = berryModel.getBakedModel(berryModel.getModelResource(animatable));

                poseStack.pushPose();
                poseStack.mulPose(getRelative(entityMatrix, current));

                poseStack.translate(0.17D, 0.55D, -0.50D);

                float scale = 1.00f;
                poseStack.scale(scale, scale, scale);

                RenderType accessoryRenderType = RenderType.entityCutout(berryModel.getTextureResource(animatable));

                VertexConsumer accessoryBuffer = bufferSource.getBuffer(accessoryRenderType);

                float interpolatedYaw = Mth.lerp(partialTick, animatable.yBodyRotO, animatable.yBodyRot);
                poseStack.mulPose(Axis.YP.rotationDegrees(interpolatedYaw + 180f));

                berryRenderer.reRender(
                        bakedModel,
                        poseStack,
                        bufferSource,
                        animatable,
                        accessoryRenderType,
                        accessoryBuffer,
                        partialTick,
                        light,
                        packedOverlay,
                        0xFFFFFFFF
                );

                poseStack.popPose();
                buffer = bufferSource.getBuffer(renderType);
            }
            if (hasHat) {
                var bakedModel = catHatModel.getBakedModel(catHatModel.getModelResource(animatable));

                poseStack.pushPose();
                poseStack.mulPose(getRelative(entityMatrix, current));

                poseStack.translate(0.0D, 0.68D, -0.6D);
                float scale = 0.25f;
                poseStack.scale(scale, scale, scale);


                RenderType accessoryRenderType = RenderType.entityCutout(catHatModel.getTextureResource(animatable));

                VertexConsumer accessoryBuffer = bufferSource.getBuffer(accessoryRenderType);

                float interpolatedYaw = Mth.lerp(partialTick, animatable.yBodyRotO, animatable.yBodyRot);
                poseStack.mulPose(Axis.YP.rotationDegrees(interpolatedYaw + 180f));


                catHatRenderer.reRender(
                        bakedModel,
                        poseStack,
                        bufferSource,
                        animatable,
                        accessoryRenderType,
                        accessoryBuffer,
                        partialTick,
                        packedLight,
                        packedOverlay,
                        0xFFFFFFFF
                );

                poseStack.popPose();
                buffer = bufferSource.getBuffer(renderType);
            }

            if (hasBow) {
                ItemStack stack = animatable.getItemBySlot(EquipmentSlot.HEAD);
                ItemStack redBow = CompatibilitiesClient.getCuriosItem(animatable.getPlayerBoundUuid(), ModItems.CAT_RED_BOW.get());
                ItemStack pinkBow = CompatibilitiesClient.getCuriosItem(animatable.getPlayerBoundUuid(), ModItems.CAT_PINK_BOW.get());

                if (!redBow.isEmpty()) {
                    stack = redBow;
                } else if (!pinkBow.isEmpty()) {
                    stack = pinkBow;
                }

                if (stack.is(ModItems.CAT_RED_BOW.get())) {
                    catBowModel.texture = AccessoryModel.HEAD_BOW_TEXTURES[1];
                }
                else catBowModel.texture = AccessoryModel.HEAD_BOW_TEXTURES[0];

                var bakedModel = catBowModel.getBakedModel(catBowModel.getModelResource(animatable));

                poseStack.pushPose();
                poseStack.mulPose(getRelative(entityMatrix, current));

                poseStack.translate(0.045D, 0.59D, -0.65D);
                poseStack.mulPose(Axis.YP.rotationDegrees(205f));
                poseStack.mulPose(Axis.XP.rotationDegrees(-25f));

                float scale = 0.30f;
                poseStack.scale(scale, scale, scale);

                RenderType accessoryRenderType = RenderType.entityCutout(catBowModel.getTextureResource(animatable));

                VertexConsumer accessoryBuffer = bufferSource.getBuffer(accessoryRenderType);

                float interpolatedYaw = Mth.lerp(partialTick, animatable.yBodyRotO, animatable.yBodyRot);
                poseStack.mulPose(Axis.YP.rotationDegrees(interpolatedYaw + 180f));

                catBowRenderer.reRender(
                        bakedModel,
                        poseStack,
                        bufferSource,
                        animatable,
                        accessoryRenderType,
                        accessoryBuffer,
                        partialTick,
                        packedLight,
                        packedOverlay,
                        0xFFFFFFFF
                );

                poseStack.popPose();
                buffer = bufferSource.getBuffer(renderType);
            }

            if (hasSkullMask) {
                var bakedModel = skullMaskModel.getBakedModel(skullMaskModel.getModelResource(animatable));

                poseStack.pushPose();
                poseStack.mulPose(getRelative(entityMatrix, current));

                poseStack.translate(0.0D, 0.427D, -0.57D);
                float scale = 1f;
                poseStack.scale(scale, scale, scale);


                RenderType accessoryRenderType = RenderType.entityCutout(skullMaskModel.getTextureResource(animatable));

                VertexConsumer accessoryBuffer = bufferSource.getBuffer(accessoryRenderType);

                float interpolatedYaw = Mth.lerp(partialTick, animatable.yBodyRotO, animatable.yBodyRot);
                poseStack.mulPose(Axis.YP.rotationDegrees(interpolatedYaw + 180f));


                skullMaskRenderer.reRender(
                        bakedModel,
                        poseStack,
                        bufferSource,
                        animatable,
                        accessoryRenderType,
                        accessoryBuffer,
                        partialTick,
                        packedLight,
                        packedOverlay,
                        0xFFFFFFFF
                );

                poseStack.popPose();
                buffer = bufferSource.getBuffer(renderType);
            }

        }

        if (capturedBoneMatrices.get("tail") != null) {
            BoneMatrix current = capturedBoneMatrices.get("tail");

            if (hasTailVines) {
                var bakedModel = tailVinesModel.getBakedModel(tailVinesModel.getModelResource(animatable));

                poseStack.pushPose();
                poseStack.mulPose(getRelative(entityMatrix, current));

                float scale = 0.25f;
                poseStack.scale(scale, scale, scale);

                poseStack.mulPose(Axis.XP.rotationDegrees(90f));

                RenderUtil.translateMatrixToBone(poseStack, current.bone);

                poseStack.translate(0.00D, 1.82D, -2.00D);
                poseStack.mulPose(Axis.YP.rotationDegrees(90f));


                RenderType accessoryRenderType = RenderType.entityCutout(tailVinesModel.getTextureResource(animatable));

                VertexConsumer accessoryBuffer = bufferSource.getBuffer(accessoryRenderType);

                float interpolatedYaw = Mth.lerp(partialTick, animatable.yBodyRotO, animatable.yBodyRot);
                poseStack.mulPose(Axis.YP.rotationDegrees(interpolatedYaw + 180f));


                tailVinesRenderer.reRender(
                        bakedModel,
                        poseStack,
                        bufferSource,
                        animatable,
                        accessoryRenderType,
                        accessoryBuffer,
                        partialTick,
                        packedLight,
                        packedOverlay,
                        0xFFFFFFFF
                );

                poseStack.popPose();

                buffer = bufferSource.getBuffer(renderType);
            }
        }

        if (capturedBoneMatrices.get("tailsub") != null) {
            BoneMatrix current = capturedBoneMatrices.get("tailsub");

            if (hasTailVines && !WCGenetics.Bobtail.isBobtail(animatable.getGenetics().bobtail)) {
                var bakedModel = tailVinesModel.getBakedModel(tailVinesModel.getModelResource(animatable));

                poseStack.pushPose();
                poseStack.mulPose(getRelative(entityMatrix, current));

                float scale = 0.25f;
                poseStack.scale(scale, scale, scale);

                poseStack.mulPose(Axis.XP.rotationDegrees(90f));

                RenderUtil.translateMatrixToBone(poseStack, current.bone);

                poseStack.translate(0.00D, 1.82D, -1.00D);
                poseStack.mulPose(Axis.YP.rotationDegrees(90f));


                RenderType accessoryRenderType = RenderType.entityCutout(tailVinesModel.getTextureResource(animatable));

                VertexConsumer accessoryBuffer = bufferSource.getBuffer(accessoryRenderType);

                float interpolatedYaw = Mth.lerp(partialTick, animatable.yBodyRotO, animatable.yBodyRot);
                poseStack.mulPose(Axis.YP.rotationDegrees(interpolatedYaw + 180f));


                tailVinesRenderer.reRender(
                        bakedModel,
                        poseStack,
                        bufferSource,
                        animatable,
                        accessoryRenderType,
                        accessoryBuffer,
                        partialTick,
                        packedLight,
                        packedOverlay,
                        0xFFFFFFFF
                );

                poseStack.popPose();

                buffer = bufferSource.getBuffer(renderType);
            }
        }

        if (capturedBoneMatrices.get("tail2") != null) {
            BoneMatrix current = capturedBoneMatrices.get("tail2");

            if (hasTailVines) {
                var bakedModel = tailVinesModel.getBakedModel(tailVinesModel.getModelResource(animatable));

                poseStack.pushPose();
                poseStack.mulPose(getRelative(entityMatrix, current));

                float scale = 0.25f;
                poseStack.scale(scale, scale, scale);

                poseStack.mulPose(Axis.XP.rotationDegrees(90f));

                RenderUtil.translateMatrixToBone(poseStack, current.bone);

                poseStack.translate(0.00D, 1.82D, 0.00D);
                poseStack.mulPose(Axis.YP.rotationDegrees(90f));


                RenderType accessoryRenderType = RenderType.entityCutout(tailVinesModel.getTextureResource(animatable));

                VertexConsumer accessoryBuffer = bufferSource.getBuffer(accessoryRenderType);

                float interpolatedYaw = Mth.lerp(partialTick, animatable.yBodyRotO, animatable.yBodyRot);
                poseStack.mulPose(Axis.YP.rotationDegrees(interpolatedYaw + 180f));


                tailVinesRenderer.reRender(
                        bakedModel,
                        poseStack,
                        bufferSource,
                        animatable,
                        accessoryRenderType,
                        accessoryBuffer,
                        partialTick,
                        packedLight,
                        packedOverlay,
                        0xFFFFFFFF
                );

                poseStack.popPose();

                poseStack.pushPose();
                poseStack.mulPose(getRelative(entityMatrix, current));

                poseStack.scale(scale, scale, scale);

                poseStack.mulPose(Axis.XP.rotationDegrees(90f));

                RenderUtil.translateMatrixToBone(poseStack, current.bone);

                poseStack.translate(0.00D, 1.82D, 1.00D);
                poseStack.mulPose(Axis.YP.rotationDegrees(90f));

                poseStack.mulPose(Axis.YP.rotationDegrees(interpolatedYaw + 180f));


                tailVinesRenderer.reRender(
                        bakedModel,
                        poseStack,
                        bufferSource,
                        animatable,
                        accessoryRenderType,
                        accessoryBuffer,
                        partialTick,
                        packedLight,
                        packedOverlay,
                        0xFFFFFFFF
                );

                poseStack.popPose();

                buffer = bufferSource.getBuffer(renderType);
            }
        }


        ItemStack socksStack = ItemStack.EMPTY;
        ItemStack feetStack = animatable.getItemBySlot(EquipmentSlot.FEET);
        if (feetStack.getItem() instanceof CatSocksArmorItem) {
            socksStack = feetStack;
        }

        if (socksStack.isEmpty()) {
            ItemStack curiosStack = CompatibilitiesClient.getCuriosItem(
                    animatable.getPlayerBoundUuid(),
                    CatSocksArmorItem.class
            );

            if (!curiosStack.isEmpty()) {
                socksStack = curiosStack;
            }
        }

        if (socksStack.getItem() instanceof CatSocksArmorItem socks) {

            float red = 1.0f;
            float green = 1.0f;
            float blue = 1.0f;

            if (socks.equals(ModItems.BLACK_CAT_SOCKS.get())) {
                red = 0.2f;
                green = 0.2f;
                blue = 0.2f;
            } else if (socks.equals(ModItems.WHITE_CAT_SOCKS.get())) {
                red = 1f;
                green = 1f;
                blue = 1f;
            } else if (socks.equals(ModItems.BLUE_CAT_SOCKS.get())) {
                red = 0.6f;
                green = 0.6f;
                blue = 1f;
            } else if (socks.equals(ModItems.GREEN_CAT_SOCKS.get())) {
                red = 0.6f;
                green = 1.0f;
                blue = 0.6f;
            } else if (socks.equals(ModItems.PINK_CAT_SOCKS.get())) {
                red = 0.9f;
                green = 0.6f;
                blue = 1f;
            } else if (socks.equals(ModItems.ORANGE_CAT_SOCKS.get())) {
                red = 1.0f;
                green = 0.6f;
                blue = 0.4f;
            }

            int colour = FastColor.ABGR32.color(255, (int) (red*255), (int) (green*255), (int) (blue*255));


            if (capturedBoneMatrices.get("front_left_leg2down") != null) {
                BoneMatrix current =  capturedBoneMatrices.get("front_left_leg2down");

                var bakedModel = socksModel.getBakedModel(socksModel.getModelResource(animatable));

                poseStack.pushPose();
                poseStack.mulPose(getRelative(entityMatrix, current));

                RenderUtil.translateMatrixToBone(poseStack, current.bone);

                float scale = 0.52f;
                poseStack.scale(scale, scale, scale);



                poseStack.translate(-0.13D, 0.00D, -0.48D);
                poseStack.mulPose(Axis.YP.rotationDegrees(90f));


                RenderType accessoryRenderType = RenderType.entityCutout(socksModel.getTextureResource(animatable));

                VertexConsumer accessoryBuffer = bufferSource.getBuffer(accessoryRenderType);

                float interpolatedYaw = Mth.lerp(partialTick, animatable.yBodyRotO, animatable.yBodyRot);
                poseStack.mulPose(Axis.YP.rotationDegrees(interpolatedYaw + 180f));



                socksRenderer.reRender(
                        bakedModel,
                        poseStack,
                        bufferSource,
                        animatable,
                        accessoryRenderType,
                        accessoryBuffer,
                        partialTick,
                        packedLight,
                        packedOverlay,
                        colour
                );

                buffer = bufferSource.getBuffer(renderType);
                poseStack.popPose();
            }
            if (capturedBoneMatrices.get("front_right_legDOWN") != null) {
                BoneMatrix current = capturedBoneMatrices.get("front_right_legDOWN");

                var bakedModel = socksModel.getBakedModel(socksModel.getModelResource(animatable));

                poseStack.pushPose();
                poseStack.mulPose(getRelative(entityMatrix, current));

                RenderUtil.translateMatrixToBone(poseStack, current.bone);

                float scale = 0.52f;
                poseStack.scale(scale, scale, scale);



                poseStack.translate(0.13D, 0.00D, -0.48D);
                poseStack.mulPose(Axis.YP.rotationDegrees(90f));


                RenderType accessoryRenderType = RenderType.entityCutout(socksModel.getTextureResource(animatable));

                VertexConsumer accessoryBuffer = bufferSource.getBuffer(accessoryRenderType);

                float interpolatedYaw = Mth.lerp(partialTick, animatable.yBodyRotO, animatable.yBodyRot);
                poseStack.mulPose(Axis.YP.rotationDegrees(interpolatedYaw + 180f));


                socksRenderer.reRender(
                        bakedModel,
                        poseStack,
                        bufferSource,
                        animatable,
                        accessoryRenderType,
                        accessoryBuffer,
                        partialTick,
                        packedLight,
                        packedOverlay,
                        colour
                );

                buffer = bufferSource.getBuffer(renderType);
                poseStack.popPose();
            }
        }

        if (capturedBoneMatrices.get("head") != null) {
            BoneMatrix current = capturedBoneMatrices.get("head");

            if (hasLeaf) {
                var bakedModel = leafShieldModel.getBakedModel(leafShieldModel.getModelResource(animatable));

                poseStack.pushPose();
                poseStack.mulPose(getRelative(entityMatrix, current));

                poseStack.translate(0.0D, 0.67D, -0.65D);

                float scale = 0.28f;
                poseStack.scale(scale, scale, scale);


                RenderType accessoryRenderType = RenderType.entityCutout(leafShieldModel.getTextureResource(animatable));

                VertexConsumer accessoryBuffer = bufferSource.getBuffer(accessoryRenderType);

                float interpolatedYaw = Mth.lerp(partialTick, animatable.yBodyRotO, animatable.yBodyRot);
                poseStack.mulPose(Axis.YP.rotationDegrees(interpolatedYaw + 180f));


                leafShieldRenderer.reRender(
                        bakedModel,
                        poseStack,
                        bufferSource,
                        animatable,
                        accessoryRenderType,
                        accessoryBuffer,
                        partialTick,
                        packedLight,
                        packedOverlay,
                        0xFFFFFFFF
                );

                poseStack.popPose();
                buffer = bufferSource.getBuffer(renderType);
            }
        }



        if (capturedBoneMatrices.get("tail") != null) {
            BoneMatrix current = capturedBoneMatrices.get("tail");

            if (hasDrapedTailVines) {
                var bakedModel = tailLichenModel.getBakedModel(tailLichenModel.getModelResource(animatable));

                poseStack.pushPose();
                poseStack.mulPose(getRelative(entityMatrix, current));

                float scale = 0.51f;
                poseStack.scale(scale, scale, scale);

                poseStack.mulPose(Axis.XP.rotationDegrees(90f));
//                poseStack.mulPose(Axis.XP.rotationDegrees(15f));

                RenderUtil.translateMatrixToBone(poseStack, current.bone);

                poseStack.translate(0.00D, 0.67D, -0.85D);
//                poseStack.mulPose(Axis.YP.rotationDegrees(90f));


                RenderType accessoryRenderType = RenderType.entityCutout(tailLichenModel.getTextureResource(animatable));

                VertexConsumer accessoryBuffer = bufferSource.getBuffer(accessoryRenderType);

                float interpolatedYaw = Mth.lerp(partialTick, animatable.yBodyRotO, animatable.yBodyRot);
                poseStack.mulPose(Axis.YP.rotationDegrees(interpolatedYaw + 180f));


                tailLichenRenderer.reRender(
                        bakedModel,
                        poseStack,
                        bufferSource,
                        animatable,
                        accessoryRenderType,
                        accessoryBuffer,
                        partialTick,
                        packedLight,
                        packedOverlay,
                        0xFFFFFFFF
                );

                poseStack.popPose();

                buffer = bufferSource.getBuffer(renderType);
            }
        }

        if (capturedBoneMatrices.get("tailsub") != null) {
            BoneMatrix current = capturedBoneMatrices.get("tailsub");

            if (hasDrapedTailVines && !WCGenetics.Bobtail.isBobtail(animatable.getGenetics().bobtail)) {
                var bakedModel = tailLichenModel.getBakedModel(tailLichenModel.getModelResource(animatable));

                poseStack.pushPose();
                poseStack.mulPose(getRelative(entityMatrix, current));

                float scale = 0.51f;

                poseStack.scale(scale, scale, scale);

                poseStack.mulPose(Axis.XP.rotationDegrees(90f));

                RenderUtil.translateMatrixToBone(poseStack, current.bone);

                poseStack.translate(0.00D, 0.67D, -0.50D);
                poseStack.mulPose(Axis.YP.rotationDegrees(180f));


                RenderType accessoryRenderType = RenderType.entityCutout(tailLichenModel.getTextureResource(animatable));

                VertexConsumer accessoryBuffer = bufferSource.getBuffer(accessoryRenderType);

                float interpolatedYaw = Mth.lerp(partialTick, animatable.yBodyRotO, animatable.yBodyRot);
                poseStack.mulPose(Axis.YP.rotationDegrees(interpolatedYaw + 180f));


                tailLichenRenderer.reRender(
                        bakedModel,
                        poseStack,
                        bufferSource,
                        animatable,
                        accessoryRenderType,
                        accessoryBuffer,
                        partialTick,
                        packedLight,
                        packedOverlay,
                        0xFFFFFFFF
                );

                poseStack.popPose();

                buffer = bufferSource.getBuffer(renderType);
            }
        }

        if (capturedBoneMatrices.get("tail2") != null) {
            BoneMatrix current = capturedBoneMatrices.get("tail2");

            if (hasDrapedTailVines) {
                var bakedModel = tailLichenModel.getBakedModel(tailLichenModel.getModelResource(animatable));

                poseStack.pushPose();
                poseStack.mulPose(getRelative(entityMatrix, current));

                float scale = 0.51f;

                poseStack.scale(scale, scale, scale);

                poseStack.mulPose(Axis.XP.rotationDegrees(90f));

                RenderUtil.translateMatrixToBone(poseStack, current.bone);

                poseStack.translate(0.00D, 0.67D, 0.10D);


                RenderType accessoryRenderType = RenderType.entityCutout(tailLichenModel.getTextureResource(animatable));

                VertexConsumer accessoryBuffer = bufferSource.getBuffer(accessoryRenderType);

                float interpolatedYaw = Mth.lerp(partialTick, animatable.yBodyRotO, animatable.yBodyRot);
                poseStack.mulPose(Axis.YP.rotationDegrees(interpolatedYaw + 180f));


                tailLichenRenderer.reRender(
                        bakedModel,
                        poseStack,
                        bufferSource,
                        animatable,
                        accessoryRenderType,
                        accessoryBuffer,
                        partialTick,
                        packedLight,
                        packedOverlay,
                        0xFFFFFFFF
                );

                poseStack.popPose();



                buffer = bufferSource.getBuffer(renderType);
            }
        }


        ItemStack featherStack = ItemStack.EMPTY;
        ItemStack featherChestStack = animatable.getItemBySlot(EquipmentSlot.CHEST);
        if (featherChestStack.getItem() instanceof FeathersArmorItem) {
            featherStack = featherChestStack;
        }

        if (featherStack.isEmpty()) {
            ItemStack curiosStack = CompatibilitiesClient.getCuriosItem(
                    animatable.getPlayerBoundUuid(),
                    FeathersArmorItem.class
            );

            if (!curiosStack.isEmpty()) {
                featherStack = curiosStack;
            }
        }

        if (featherStack.getItem() instanceof FeathersArmorItem featherArmor) {
            if (featherArmor == ModItems.VULTURE_BODY_FEATHERS.get()) {

                if (capturedBoneMatrices.get("moreup") != null) {
                    BoneMatrix current = capturedBoneMatrices.get("moreup");

                    var bakedModel = bodyVultureFeathersModelUp.getBakedModel(bodyVultureFeathersModelUp.getModelResource(animatable));

                    poseStack.pushPose();
                    poseStack.mulPose(getRelative(entityMatrix, current));

                    float scale = 1f;
                    poseStack.translate(0.00D, -0.01D, 0.00D);

                    poseStack.scale(scale, scale, scale);

                    poseStack.mulPose(Axis.XP.rotationDegrees(0f));

                    RenderUtil.translateMatrixToBone(poseStack, current.bone);

                    RenderType accessoryRenderType = RenderType.entityCutout(bodyVultureFeathersModelUp.getTextureResource(animatable));

                    VertexConsumer accessoryBuffer = bufferSource.getBuffer(accessoryRenderType);

                    float interpolatedYaw = Mth.lerp(partialTick, animatable.yBodyRotO, animatable.yBodyRot);
                    poseStack.mulPose(Axis.YP.rotationDegrees(interpolatedYaw + 180f));


                    bodyVultureFeathersRenderer.reRender(
                            bakedModel,
                            poseStack,
                            bufferSource,
                            animatable,
                            accessoryRenderType,
                            accessoryBuffer,
                            partialTick,
                            packedLight,
                            packedOverlay,
                            0xFFFFFFFF
                    );

                    poseStack.popPose();

                    buffer = bufferSource.getBuffer(renderType);
                }
                if (capturedBoneMatrices.get("up") != null) {
                    BoneMatrix current = capturedBoneMatrices.get("up");
                    var bakedModel = bodyVultureFeathersModelMid.getBakedModel(bodyVultureFeathersModelMid.getModelResource(animatable));

                    poseStack.pushPose();
                    poseStack.mulPose(getRelative(entityMatrix, current));

                    float scale = 1f;
                    poseStack.translate(0.00D, -0.01D, 0.00D);

                    poseStack.scale(scale, scale, scale);

                    poseStack.mulPose(Axis.XP.rotationDegrees(0f));

                    RenderUtil.translateMatrixToBone(poseStack, current.bone);

                    RenderType accessoryRenderType = RenderType.entityCutout(bodyVultureFeathersModelMid.getTextureResource(animatable));

                    VertexConsumer accessoryBuffer = bufferSource.getBuffer(accessoryRenderType);

                    float interpolatedYaw = Mth.lerp(partialTick, animatable.yBodyRotO, animatable.yBodyRot);
                    poseStack.mulPose(Axis.YP.rotationDegrees(interpolatedYaw + 180f));


                    bodyVultureFeathersRenderer.reRender(
                            bakedModel,
                            poseStack,
                            bufferSource,
                            animatable,
                            accessoryRenderType,
                            accessoryBuffer,
                            partialTick,
                            packedLight,
                            packedOverlay,
                            0xFFFFFFFF
                    );

                    poseStack.popPose();

                    buffer = bufferSource.getBuffer(renderType);
                }

            }

            if (featherArmor != ModItems.VULTURE_BODY_FEATHERS.get()){
                bodyFeathersModelUp.texture = AccessoryModel.FEATHER_TEXTURES[5];
                bodyFeathersModelMid.texture = AccessoryModel.FEATHER_TEXTURES[5];
                bodyFeathersModelDown.texture = AccessoryModel.FEATHER_TEXTURES[5];

                if (featherArmor == ModItems.BLUE_PARROT_BODY_FEATHERS.get()) {
                    bodyFeathersModelUp.texture = AccessoryModel.FEATHER_TEXTURES[3];
                    bodyFeathersModelMid.texture = AccessoryModel.FEATHER_TEXTURES[3];
                    bodyFeathersModelDown.texture = AccessoryModel.FEATHER_TEXTURES[3];
                } else if (featherArmor == ModItems.LIGHTBLUE_PARROT_BODY_FEATHERS.get()) {
                    bodyFeathersModelUp.texture = AccessoryModel.FEATHER_TEXTURES[4];
                    bodyFeathersModelMid.texture = AccessoryModel.FEATHER_TEXTURES[4];
                    bodyFeathersModelDown.texture = AccessoryModel.FEATHER_TEXTURES[4];
                } else if (featherArmor == ModItems.GRAY_PARROT_BODY_FEATHERS.get()) {
                    bodyFeathersModelUp.texture = AccessoryModel.FEATHER_TEXTURES[1];
                    bodyFeathersModelMid.texture = AccessoryModel.FEATHER_TEXTURES[1];
                    bodyFeathersModelDown.texture = AccessoryModel.FEATHER_TEXTURES[1];
                } else if (featherArmor == ModItems.GREEN_PARROT_BODY_FEATHERS.get()) {
                    bodyFeathersModelUp.texture = AccessoryModel.FEATHER_TEXTURES[2];
                    bodyFeathersModelMid.texture = AccessoryModel.FEATHER_TEXTURES[2];
                    bodyFeathersModelDown.texture = AccessoryModel.FEATHER_TEXTURES[2];
                } else if (featherArmor == ModItems.RED_PARROT_BODY_FEATHERS.get()) {
                    bodyFeathersModelUp.texture = AccessoryModel.FEATHER_TEXTURES[0];
                    bodyFeathersModelMid.texture = AccessoryModel.FEATHER_TEXTURES[0];
                    bodyFeathersModelDown.texture = AccessoryModel.FEATHER_TEXTURES[0];
                } else if (featherArmor == ModItems.PIGEON_BODY_FEATHERS.get()) {
                    bodyFeathersModelUp.texture = AccessoryModel.FEATHER_TEXTURES[6];
                    bodyFeathersModelMid.texture = AccessoryModel.FEATHER_TEXTURES[6];
                    bodyFeathersModelDown.texture = AccessoryModel.FEATHER_TEXTURES[6];
                } else if (featherArmor == ModItems.CROW_BODY_FEATHERS.get()) {
                    bodyFeathersModelUp.texture = AccessoryModel.FEATHER_TEXTURES[7];
                    bodyFeathersModelMid.texture = AccessoryModel.FEATHER_TEXTURES[7];
                    bodyFeathersModelDown.texture = AccessoryModel.FEATHER_TEXTURES[7];
                } else if (featherArmor == ModItems.GOLDFINCH_BODY_FEATHERS.get()) {
                    bodyFeathersModelUp.texture = AccessoryModel.FEATHER_TEXTURES[8];
                    bodyFeathersModelMid.texture = AccessoryModel.FEATHER_TEXTURES[8];
                    bodyFeathersModelDown.texture = AccessoryModel.FEATHER_TEXTURES[8];
                } else if (featherArmor == ModItems.CARDINAL_BODY_FEATHERS.get()) {
                    bodyFeathersModelUp.texture = AccessoryModel.FEATHER_TEXTURES[9];
                    bodyFeathersModelMid.texture = AccessoryModel.FEATHER_TEXTURES[9];
                    bodyFeathersModelDown.texture = AccessoryModel.FEATHER_TEXTURES[9];
                }

                if (capturedBoneMatrices.get("moreup") != null) {
                    BoneMatrix current = capturedBoneMatrices.get("moreup");

                    var bakedModel = bodyFeathersModelUp.getBakedModel(bodyFeathersModelUp.getModelResource(animatable));

                    poseStack.pushPose();
                    poseStack.mulPose(getRelative(entityMatrix, current));

                    float scale = 1f;
                    poseStack.translate(0.00D, -0.01D, 0.00D);

                    poseStack.scale(scale, scale, scale);

                    poseStack.mulPose(Axis.XP.rotationDegrees(0f));

                    RenderUtil.translateMatrixToBone(poseStack, current.bone);

                    RenderType accessoryRenderType = RenderType.entityCutout(bodyFeathersModelUp.getTextureResource(animatable));

                    VertexConsumer accessoryBuffer = bufferSource.getBuffer(accessoryRenderType);

                    float interpolatedYaw = Mth.lerp(partialTick, animatable.yBodyRotO, animatable.yBodyRot);
                    poseStack.mulPose(Axis.YP.rotationDegrees(interpolatedYaw + 180f));


                    bodyFeathersRenderer.reRender(
                            bakedModel,
                            poseStack,
                            bufferSource,
                            animatable,
                            accessoryRenderType,
                            accessoryBuffer,
                            partialTick,
                            packedLight,
                            packedOverlay,
                            0xFFFFFFFF
                    );

                    poseStack.popPose();

                    buffer = bufferSource.getBuffer(renderType);
                }
                if (capturedBoneMatrices.get("up") != null) {
                    BoneMatrix current = capturedBoneMatrices.get("up");
                    var bakedModel = bodyFeathersModelMid.getBakedModel(bodyFeathersModelMid.getModelResource(animatable));

                    poseStack.pushPose();
                    poseStack.mulPose(getRelative(entityMatrix, current));

                    float scale = 1f;
                    poseStack.translate(0.00D, -0.01D, 0.00D);

                    poseStack.scale(scale, scale, scale);

                    poseStack.mulPose(Axis.XP.rotationDegrees(0f));

                    RenderUtil.translateMatrixToBone(poseStack, current.bone);

                    RenderType accessoryRenderType = RenderType.entityCutout(bodyFeathersModelMid.getTextureResource(animatable));

                    VertexConsumer accessoryBuffer = bufferSource.getBuffer(accessoryRenderType);

                    float interpolatedYaw = Mth.lerp(partialTick, animatable.yBodyRotO, animatable.yBodyRot);
                    poseStack.mulPose(Axis.YP.rotationDegrees(interpolatedYaw + 180f));


                    bodyFeathersRenderer.reRender(
                            bakedModel,
                            poseStack,
                            bufferSource,
                            animatable,
                            accessoryRenderType,
                            accessoryBuffer,
                            partialTick,
                            packedLight,
                            packedOverlay,
                            0xFFFFFFFF
                    );

                    poseStack.popPose();

                    buffer = bufferSource.getBuffer(renderType);
                }
                if (capturedBoneMatrices.get("bodydown2") != null) {
                    BoneMatrix current = capturedBoneMatrices.get("bodydown2");
                    var bakedModel = bodyFeathersModelDown.getBakedModel(bodyFeathersModelDown.getModelResource(animatable));

                    poseStack.pushPose();
                    poseStack.mulPose(getRelative(entityMatrix, current));

                    float scale = 1f;
                    poseStack.translate(0.00D, +0.50D, -0.32D);

                    poseStack.scale(scale, scale, scale);

                    poseStack.mulPose(Axis.XP.rotationDegrees(90f));

                    RenderUtil.translateMatrixToBone(poseStack, current.bone);

                    RenderType accessoryRenderType = RenderType.entityCutout(bodyFeathersModelDown.getTextureResource(animatable));

                    VertexConsumer accessoryBuffer = bufferSource.getBuffer(accessoryRenderType);

                    float interpolatedYaw = Mth.lerp(partialTick, animatable.yBodyRotO, animatable.yBodyRot);
                    poseStack.mulPose(Axis.YP.rotationDegrees(interpolatedYaw + 180f));


                    bodyFeathersRenderer.reRender(
                            bakedModel,
                            poseStack,
                            bufferSource,
                            animatable,
                            accessoryRenderType,
                            accessoryBuffer,
                            partialTick,
                            packedLight,
                            packedOverlay,
                            0xFFFFFFFF
                    );

                    poseStack.popPose();

                    buffer = bufferSource.getBuffer(renderType);
                }
            }

        }

        if (capturedBoneMatrices.get("moreup") != null) {
            BoneMatrix current = capturedBoneMatrices.get("moreup");

            boolean hasCollar = false;
            boolean hasSpikes;
            boolean hasBell;

            int light = packedLight;

            boolean spaceKitty = false;


            ItemStack collarStack = ItemStack.EMPTY;
            ItemStack bodyCollarStack = animatable.getItemBySlot(EquipmentSlot.CHEST);
            if (bodyCollarStack.getItem() instanceof CollarArmorItem) {
                collarStack = bodyCollarStack;
            }

            if (collarStack.isEmpty()) {
                ItemStack curiosStack = CompatibilitiesClient.getCuriosItem(
                        animatable.getPlayerBoundUuid(), CollarArmorItem.class
                );

                if (!curiosStack.isEmpty()) {
                    collarStack = curiosStack;
                }
            }

            if (collarStack.getItem() instanceof CollarArmorItem collar) {

                hasCollar = true;

                if (collar.hasSpikes(collarStack)) {
                    hasSpikes = true;
                } else {
                    hasSpikes = false;
                }

                if (collar.hasBell(collarStack)) {
                    hasBell = true;
                } else {
                    hasBell = false;
                }

                if (collar.hasGlow(collarStack)) {
                    light = 255;
                }


                if (collar == ModItems.BLACK_CAT_COLLAR.get()) {
                    collarModel.texture = AccessoryModel.COLLAR_TEXTURES[0];
                } else if (collar == ModItems.BROWN_CAT_COLLAR.get()) {
                    collarModel.texture = AccessoryModel.COLLAR_TEXTURES[1];
                } else if (collar == ModItems.WHITE_CAT_COLLAR.get()) {
                    collarModel.texture = AccessoryModel.COLLAR_TEXTURES[2];
                } else if (collar == ModItems.PINK_CAT_COLLAR.get()) {
                    collarModel.texture = AccessoryModel.COLLAR_TEXTURES[3];
                } else if (collar == ModItems.ORANGE_CAT_COLLAR.get()) {
                    collarModel.texture = AccessoryModel.COLLAR_TEXTURES[4];
                } else if (collar == ModItems.RED_CAT_COLLAR.get()) {
                    collarModel.texture = AccessoryModel.COLLAR_TEXTURES[5];
                } else if (collar == ModItems.BLUE_CAT_COLLAR.get()) {
                    collarModel.texture = AccessoryModel.COLLAR_TEXTURES[6];
                } else if (collar == ModItems.PURPLE_CAT_COLLAR.get()) {
                    collarModel.texture = AccessoryModel.COLLAR_TEXTURES[7];
                }

                if (collarStack.has(DataComponents.CUSTOM_NAME)) {
                    if (collarStack.getHoverName().getString().toLowerCase(Locale.ROOT).equals("space kitty")) {
                        spaceKitty = true;
                    }
                }

            } else {
                hasSpikes = false;
                hasBell = false;
            }

            if (hasCollar) {

                var bakedModel = collarModel.getBakedModel(collarModel.getModelResource(animatable));

                collarModel.getBone("bell").ifPresent(bell -> { bell.setHidden(!hasBell);});
                collarModel.getBone("spikes").ifPresent(spikes -> { spikes.setHidden(!hasSpikes);});

                poseStack.pushPose();
                poseStack.mulPose(getRelative(entityMatrix, current));

                poseStack.translate(0.0D, 0.30D, -0.44D);
                poseStack.mulPose(Axis.ZP.rotationDegrees(0f));
                poseStack.mulPose(Axis.YP.rotationDegrees(0f));
                poseStack.mulPose(Axis.XP.rotationDegrees(0f));

                float scale = 1f;
                poseStack.scale(scale, scale, scale);

                RenderType accessoryRenderType = RenderType.entityCutoutNoCull(collarModel.getTextureResource(animatable));

                if (spaceKitty) accessoryRenderType = RenderType.endGateway();

                VertexConsumer accessoryBuffer = bufferSource.getBuffer(accessoryRenderType);

                float interpolatedYaw = Mth.lerp(partialTick, animatable.yBodyRotO, animatable.yBodyRot);
                poseStack.mulPose(Axis.YP.rotationDegrees(interpolatedYaw + 180f));

                collarRenderer.reRender(
                        bakedModel,
                        poseStack, bufferSource, animatable, accessoryRenderType, accessoryBuffer,
                        partialTick, light, packedOverlay,
                        0xFFFFFFFF
                );

                poseStack.popPose();
                buffer = bufferSource.getBuffer(renderType);
            }

            if (hasChestBow) {
                catBowModel.texture = AccessoryModel.HEAD_BOW_TEXTURES[2];

                var bakedModel = catBowModel.getBakedModel(catBowModel.getModelResource(animatable));

                poseStack.pushPose();
                poseStack.mulPose(getRelative(entityMatrix, current));

                poseStack.translate(0.0D, 0.14D, -0.480D);
                poseStack.mulPose(Axis.YP.rotationDegrees(180f));
                float scale = 0.45f;
                poseStack.scale(scale, scale, scale);

                RenderType accessoryRenderType = RenderType.entityCutout(catBowModel.getTextureResource(animatable));

                VertexConsumer accessoryBuffer = bufferSource.getBuffer(accessoryRenderType);

                float interpolatedYaw = Mth.lerp(partialTick, animatable.yBodyRotO, animatable.yBodyRot);
                poseStack.mulPose(Axis.YP.rotationDegrees(interpolatedYaw + 180f));


                catBowRenderer.reRender(
                        bakedModel,
                        poseStack,
                        bufferSource,
                        animatable,
                        accessoryRenderType,
                        accessoryBuffer,
                        partialTick,
                        packedLight,
                        packedOverlay,
                        0xFFFFFFFF
                );

                poseStack.popPose();
                buffer = bufferSource.getBuffer(renderType);
            }
        }

        if (capturedBoneMatrices.get("up") != null) {
            BoneMatrix current = capturedBoneMatrices.get("up");

            ItemStack elytraStack = ItemStack.EMPTY;
            ItemStack bodyElytraStack = animatable.getItemBySlot(EquipmentSlot.CHEST);
            if (bodyElytraStack.getItem() instanceof ElytraItem) {
                elytraStack = bodyElytraStack;
            }

            if (elytraStack.isEmpty()) {
                ItemStack curiosStack = CompatibilitiesClient.getCuriosItem(
                        animatable.getPlayerBoundUuid(), ElytraItem.class
                );

                if (!curiosStack.isEmpty()) {
                    elytraStack = curiosStack;
                }
            }

            if (elytraStack.getItem() instanceof ElytraItem) {
                var bakedModel = elytraModel.getBakedModel(elytraModel.getModelResource(animatable));

                if (Minecraft.getInstance().level != null) {
                    Player player = Minecraft.getInstance().level.getPlayerByUUID(animatable.getPlayerBoundUuid());
                    elytraModel.setTexture(resolveElytraTexture(player, elytraStack));
                    var processor = elytraModel.getAnimationProcessor();
                    var leftWing = processor.getBone("left_wing");
                    var rightWing = processor.getBone("right_wing");
                    if (leftWing != null && rightWing != null) {
                        if (player != null) {
                            boolean flying = player.isFallFlying();

                            if (flying) {
                                float angle = (float) ((60  + 60*-Mth.abs(player.getXRot()/90))* animatable.getDeltaMovement().length()/1.5f);
                                leftWing.setRotZ((float)Math.toRadians(194 + angle));
                                rightWing.setRotZ(-(float)Math.toRadians(194 + angle));

                            } else {
                                leftWing.setRotZ((float)Math.toRadians(194));
                                rightWing.setRotZ(-(float)Math.toRadians(194));
                            }
                        }
                    }
                }



                poseStack.pushPose();
                poseStack.mulPose(getRelative(entityMatrix, current));

                float scale = 0.8f;

                poseStack.translate(0.00D, 0.43D, -0.0D);

                poseStack.scale(scale, scale, scale);

                poseStack.mulPose(Axis.XP.rotationDegrees(-5f));
                poseStack.mulPose(Axis.YP.rotationDegrees(180f));

                RenderUtil.translateMatrixToBone(poseStack, current.bone);

                RenderType accessoryRenderType = RenderType.entityCutoutNoCull(elytraModel.getTextureResource(animatable));

                if (WarriorCatsEvents.Collaborators.isContributor(animatable.getPlayerBoundUuid())) {
                    if (elytraStack.has(DataComponents.CUSTOM_NAME) && elytraStack.getHoverName().getString().equalsIgnoreCase("space kitty")) {
                        accessoryRenderType = RenderType.endGateway();
                    }
                }

                VertexConsumer accessoryBuffer = bufferSource.getBuffer(accessoryRenderType);

                float interpolatedYaw = Mth.lerp(partialTick, animatable.yBodyRotO, animatable.yBodyRot);
                poseStack.mulPose(Axis.YP.rotationDegrees(interpolatedYaw + 180f));

                elytraRenderer.reRender(
                        bakedModel,
                        poseStack,
                        bufferSource,
                        animatable,
                        accessoryRenderType,
                        accessoryBuffer,
                        partialTick,
                        packedLight,
                        packedOverlay,
                        0xFFFFFFFF
                );

                poseStack.popPose();

                buffer = bufferSource.getBuffer(renderType);
            }
        }

        if (hasLegWrap) {
            if (capturedBoneMatrices.get("front_right_legDOWN") != null) {
                BoneMatrix current = capturedBoneMatrices.get("front_right_legDOWN");

                var bakedModel = pawWrapModel0.getBakedModel(pawWrapModel0.getModelResource(animatable));

                poseStack.pushPose();
                poseStack.mulPose(getRelative(entityMatrix, current));

                poseStack.translate(0.07D, 0.0D, -0.249D);
                poseStack.mulPose(Axis.YP.rotationDegrees(0f));
                float scale = 0.5f;
                poseStack.scale(scale, scale, scale);

                RenderType accessoryRenderType = RenderType.entityCutout(pawWrapModel0.getTextureResource(animatable));

                VertexConsumer accessoryBuffer = bufferSource.getBuffer(accessoryRenderType);

                float interpolatedYaw = Mth.lerp(partialTick, animatable.yBodyRotO, animatable.yBodyRot);
                poseStack.mulPose(Axis.YP.rotationDegrees(interpolatedYaw + 180f));


                pawWrapRenderer.reRender(
                        bakedModel,
                        poseStack,
                        bufferSource,
                        animatable,
                        accessoryRenderType,
                        accessoryBuffer,
                        partialTick,
                        packedLight,
                        packedOverlay,
                        0xFFFFFFFF
                );

                poseStack.popPose();
                buffer = bufferSource.getBuffer(renderType);
            }

            if (capturedBoneMatrices.get("front_right_legUPPER") != null) {
                BoneMatrix current = capturedBoneMatrices.get("front_right_legUPPER");
                var bakedModel = pawWrapModel1.getBakedModel(pawWrapModel1.getModelResource(animatable));

                poseStack.pushPose();
                poseStack.mulPose(getRelative(entityMatrix, current));

                poseStack.translate(0.07D, 0.0D, -0.249D);
                poseStack.mulPose(Axis.YP.rotationDegrees(0f));
                float scale = 0.5f;
                poseStack.scale(scale, scale, scale);

                RenderType accessoryRenderType = RenderType.entityCutout(pawWrapModel1.getTextureResource(animatable));

                VertexConsumer accessoryBuffer = bufferSource.getBuffer(accessoryRenderType);

                float interpolatedYaw = Mth.lerp(partialTick, animatable.yBodyRotO, animatable.yBodyRot);
                poseStack.mulPose(Axis.YP.rotationDegrees(interpolatedYaw + 180f));

                pawWrapRenderer.reRender(
                        bakedModel,
                        poseStack,
                        bufferSource,
                        animatable,
                        accessoryRenderType,
                        accessoryBuffer,
                        partialTick,
                        packedLight,
                        packedOverlay,
                        0xFFFFFFFF
                );

                poseStack.popPose();
                buffer = bufferSource.getBuffer(renderType);
            }
        }

        ItemStack butterflyWing = ItemStack.EMPTY;
        ItemStack butterflyArmorWing = animatable.getItemBySlot(EquipmentSlot.HEAD);
        if (butterflyArmorWing.getItem() instanceof ButterflyWingArmorItem) {
            butterflyWing = butterflyArmorWing;
        }

        if (featherStack.isEmpty()) {
            ItemStack curiosStack = CompatibilitiesClient.getCuriosItem(
                    animatable.getPlayerBoundUuid(),
                    ButterflyWingArmorItem.class
            );

            if (!curiosStack.isEmpty()) {
                butterflyWing = curiosStack;
            }
        }

        if (!butterflyWing.isEmpty()) {
            if (butterflyWing.is(ModItems.BLUE_MORPHO_WING.get())) {
                butterflyWingModel.texture = AccessoryModel.BUTTERFLYWING_TEXTURES[0];
            } else if (butterflyWing.is(ModItems.GOLIATH_BIRDWING_WING.get())) {
                butterflyWingModel.texture = AccessoryModel.BUTTERFLYWING_TEXTURES[1];
            } else if (butterflyWing.is(ModItems.MONARCH_WING.get())) {
                butterflyWingModel.texture = AccessoryModel.BUTTERFLYWING_TEXTURES[2];
            } else {
                butterflyWingModel.texture = AccessoryModel.BUTTERFLYWING_TEXTURES[3];
            }

            if (capturedBoneMatrices.get("head") != null) {

                BoneMatrix current =  capturedBoneMatrices.get("head");

                var bakedModel = butterflyWingModel.getBakedModel(butterflyWingModel.getModelResource(animatable));

                poseStack.pushPose();
                poseStack.mulPose(getRelative(entityMatrix, current));

                poseStack.translate(-0.159D, 0.5D, -0.6D);
                poseStack.mulPose(Axis.YP.rotationDegrees(-20f));
                float scale = 0.8f;
                poseStack.scale(scale, scale, scale);


                RenderType accessoryRenderType = RenderType.entityCutout(butterflyWingModel.getTextureResource(animatable));

                VertexConsumer accessoryBuffer = bufferSource.getBuffer(accessoryRenderType);

                float interpolatedYaw = Mth.lerp(partialTick, animatable.yBodyRotO, animatable.yBodyRot);
                poseStack.mulPose(Axis.YP.rotationDegrees(interpolatedYaw + 180f));


                butterflyWingRenderer.reRender(
                        bakedModel,
                        poseStack,
                        bufferSource,
                        animatable,
                        accessoryRenderType,
                        accessoryBuffer,
                        partialTick,
                        packedLight,
                        packedOverlay,
                        0xFFFFFFFF
                );

                poseStack.popPose();
                buffer = bufferSource.getBuffer(renderType);
            }
        }


        if (Minecraft.getInstance().level != null && Minecraft.getInstance().level.getPlayerByUUID(animatable.getPlayerBoundUuid()) != null) {
            Player player = Minecraft.getInstance().level.getPlayerByUUID(animatable.getPlayerBoundUuid());

            if (animatable.getFirstPassenger() instanceof LizardEntity lizardEntity) {

                if (capturedBoneMatrices.get("head") != null) {
                    BoneMatrix current =  capturedBoneMatrices.get("head");

                    poseStack.pushPose();
                    poseStack.mulPose(getRelative(entityMatrix, current));

                    poseStack.translate(-0.00D, 0.678D, -0.6D);
                    float scale = 1.3f;
                    poseStack.scale(scale, scale, scale);

                    float interpolatedYaw = Mth.lerp(partialTick, animatable.yBodyRotO, animatable.yBodyRot);
                    poseStack.mulPose(Axis.YP.rotationDegrees(interpolatedYaw + 180f));

                    lizRenderer.isAccessory = true;
                    lizRenderer.render(lizardEntity, 0, partialTick,
                            poseStack, bufferSource, packedLight);
                    lizRenderer.isAccessory = false;

                    poseStack.popPose();
                    buffer = bufferSource.getBuffer(renderType);
                }
            }
        }


        capturedBoneMatrices.clear();

    }

    private Matrix4f getRelative(Matrix4f entityMatrix, BoneMatrix boneMatrix) {
        Matrix4f captured = boneMatrix.matrix4f;
        if (captured == null) return null;
        return new Matrix4f(entityMatrix).invert().mul(captured);
    }

    public static ResourceLocation resolveElytraTexture(LivingEntity entity, ItemStack stack) {

        if (WarriorCatsEvents.Collaborators.isContributor(entity.getUUID())) {
            if (stack.has(DataComponents.CUSTOM_NAME) && stack.getHoverName().getString().equalsIgnoreCase("wce")) {
                return ElytraModel.DEV_TEXTURE2;
            }
            if (stack.has(DataComponents.CUSTOM_NAME) && stack.getHoverName().getString().equalsIgnoreCase("starclan")) {
                return ElytraModel.DEV_TEXTURE;
            }
        }

        if (entity instanceof AbstractClientPlayer player) {

            PlayerSkin skin = player.getSkin();

            if (skin.elytraTexture() != null) {
                return skin.elytraTexture();
            }

            if (skin.capeTexture() != null && player.isModelPartShown(PlayerModelPart.CAPE)) {
                return skin.capeTexture();
            }
        }

        return ResourceLocation.withDefaultNamespace("textures/entity/elytra.png");
    }
}
