package net.snowteb.warriorcats_events.entity.client;

import net.minecraft.resources.ResourceLocation;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import software.bernie.geckolib.model.GeoModel;

public class SquirrelAccessoryModel extends GeoModel<WCatEntity> {
    @Override
    public ResourceLocation getModelResource(WCatEntity squirrelEntity) {
        return ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "geo/squirrel.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WCatEntity squirrelEntity) {
        return ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/squirrel.png");
    }

    @Override
    public ResourceLocation getAnimationResource(WCatEntity squirrelEntity) {
        return ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "animations/squirrel.animation.json");
    }

}
