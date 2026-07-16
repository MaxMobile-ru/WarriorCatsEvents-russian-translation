package net.snowteb.warriorcats_events.entity.client;

import net.minecraft.resources.ResourceLocation;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class ElytraModel extends GeoModel<WCatEntity> {

    private ResourceLocation texture;

    public static final ResourceLocation DEV_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/accessories/elytra_dev.png");
    public static final ResourceLocation DEV_TEXTURE2 =
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/accessories/elytra_dev2.png");

    public ElytraModel() {
        texture = ResourceLocation.withDefaultNamespace("textures/entity/elytra.png");
    }

    @Override
    public ResourceLocation getModelResource(WCatEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "geo/wcat.elytra.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WCatEntity animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(WCatEntity squirrelEntity) {
        return ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "animations/elytra.animation.json");
    }

    @Override
    public void setCustomAnimations(WCatEntity animatable, long instanceId, AnimationState<WCatEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

    }

    public void setTexture(ResourceLocation texture) {
        this.texture = texture;
    }
}
