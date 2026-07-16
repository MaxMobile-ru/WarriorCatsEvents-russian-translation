package net.snowteb.warriorcats_events.compat.jei;

import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.minecraft.client.renderer.Rect2i;
import net.snowteb.warriorcats_events.screen.screens.HerbMixingRockScreen;

import java.util.List;

public class HerbMixingScreenHandler implements IGuiContainerHandler<HerbMixingRockScreen> {

    @Override
    public List<Rect2i> getGuiExtraAreas(HerbMixingRockScreen screen) {
        return List.of(
                new Rect2i(
                        0,
                        0,
                        screen.width,
                        screen.height
                )
        );
    }
}

