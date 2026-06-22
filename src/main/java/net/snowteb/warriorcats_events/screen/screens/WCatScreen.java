package net.snowteb.warriorcats_events.screen.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.entity.custom.WCatEntity;
import net.snowteb.warriorcats_events.screen.menus.WCatMenu;

import static net.snowteb.warriorcats_events.screen.screens.ManageClanScreen.pose;
import static net.snowteb.warriorcats_events.screen.screens.ManageClanScreen.rotation;

public class WCatScreen extends AbstractContainerScreen<WCatMenu>  {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/catinv_gui.png");

    private final WCatEntity cat;

    public WCatScreen(WCatMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.cat = pMenu.cat;
    }

    @Override
    protected void init() {
        super.init();
        this.imageWidth = 236;
        this.imageHeight = 210;
        this.inventoryLabelY = 74;
        this.titleLabelY = 23;
        this.titleLabelX = 3;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y - 20, 0, 0, imageWidth, imageHeight, 236, 210);

    }


    @Override
    public void render(GuiGraphics pGuiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, mouseX, mouseY, delta);
        renderTooltip(pGuiGraphics, mouseX, mouseY);

        int centerX = (this.width) / 2;
        int centerY = (this.height) / 2;

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(centerX, centerY - 65, 30);
        InventoryScreen.renderEntityInInventoryFollowsMouse(pGuiGraphics, 0, 0, 60, 40, 0, cat);
        pGuiGraphics.pose().popPose();
    }
}
