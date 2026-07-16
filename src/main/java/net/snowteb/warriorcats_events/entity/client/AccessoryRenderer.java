package net.snowteb.warriorcats_events.entity.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class AccessoryRenderer extends GeoEntityRenderer<WCatEntity> {

    public AccessoryRenderer(EntityRendererProvider.Context ctx,  GeoModel<WCatEntity> model) {
        super(ctx, model);
    }

}
