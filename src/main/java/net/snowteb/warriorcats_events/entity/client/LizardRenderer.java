package net.snowteb.warriorcats_events.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.entity.custom.LizardEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class LizardRenderer extends GeoEntityRenderer<LizardEntity> {

    public LizardRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new LizardModel());
        this.shadowRadius = 0.1F;
        this.addRenderLayer(new LizardHeldItemLayer(this));
    }

    public boolean isAccessory = false;

    @Override
    public ResourceLocation getTextureLocation(LizardEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/lizard/lizard" + animatable.getVariant() + ".png");
    }

    @Override
    public void render(LizardEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {

        if (entity.isPassenger() && !isAccessory) return;

        poseStack.pushPose();

        if(entity.isBaby()) {
            poseStack.scale(0.5F, 0.5F, 0.5F);
        } else {
            poseStack.scale(1.0F, 1.0F, 1.0F);
        }

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        poseStack.popPose();
    }
}
