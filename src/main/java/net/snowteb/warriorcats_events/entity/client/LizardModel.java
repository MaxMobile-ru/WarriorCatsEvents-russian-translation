package net.snowteb.warriorcats_events.entity.client;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.entity.custom.LizardEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class LizardModel extends GeoModel<LizardEntity> {
    @Override
    public ResourceLocation getModelResource(LizardEntity lizardEntity) {
        return ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "geo/lizard.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LizardEntity lizardEntity) {
        return ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/lizard/lizard" + lizardEntity.getVariant() + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(LizardEntity lizardEntity) {
        return ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "animations/lizard.animation.json");
    }

    @Override
    public void setCustomAnimations(LizardEntity animatable, long instanceId, AnimationState<LizardEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("mainHead");

        this.getBone("actual_tail").ifPresent(
                bone -> bone.setHidden(!animatable.hasTail())
        );

        if (head != null) {
            EntityModelData entityModelData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotX(entityModelData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityModelData.netHeadYaw() * Mth.DEG_TO_RAD);
        }
    }
}
