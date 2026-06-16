package net.snowteb.warriorcats_events.entity.client;

import net.minecraft.resources.ResourceLocation;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.entity.custom.LizardTailEntity;
import software.bernie.geckolib.model.GeoModel;

public class LizardTailModel extends GeoModel<LizardTailEntity> {
    @Override
    public ResourceLocation getModelResource(LizardTailEntity LizardTailEntity) {
        return ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "geo/lizard_tail.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LizardTailEntity lizardEntity) {
        return ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/lizard/lizard" + lizardEntity.getVariant() + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(LizardTailEntity lizardEntity) {
        return ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "animations/lizard_tail.animation.json");
    }

}
