package net.snowteb.warriorcats_events.entity.client;

import net.minecraft.resources.ResourceLocation;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class AccessoryModel extends GeoModel<WCatEntity> {
    private final ResourceLocation model;
    public ResourceLocation texture;

    public static final ResourceLocation[] FEATHER_TEXTURES = {
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/accessories/red_parrot_feathers.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/accessories/gray_parrot_feathers.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/accessories/green_parrot_feathers.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/accessories/blue_parrot_feathers.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/accessories/light_blue_parrot_feathers.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/accessories/chicken_feathers.png"),

            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/accessories/pigeon_feathers.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/accessories/crow_feathers.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/accessories/american_goldfinch_feathers.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/accessories/red_cardinal_feathers.png"),
    };


    public static final ResourceLocation[] BERRY_TEXTURES = {
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/accessories/head_sweetberry.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/accessories/head_glowberry.png")
    };

    public static final ResourceLocation[] HEAD_BOW_TEXTURES = {
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/accessories/pink_bow.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/accessories/red_bow.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/accessories/cat_black_bow.png")
    };

    public static final ResourceLocation[] BUTTERFLYWING_TEXTURES = {
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/accessories/bluemorphowing.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/accessories/goliathbirdwingwing.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/accessories/monarchwing.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/accessories/tigerswallowwing.png")
    };

    public AccessoryModel(ResourceLocation model,
                          ResourceLocation texture) {
        this.model = model;
        this.texture = texture;
    }

    @Override
    public ResourceLocation getModelResource(WCatEntity animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(WCatEntity animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(WCatEntity squirrelEntity) {
        return ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "animations/empty.animation.json");
    }

    @Override
    public void setCustomAnimations(WCatEntity animatable, long instanceId, AnimationState<WCatEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
    }
}
