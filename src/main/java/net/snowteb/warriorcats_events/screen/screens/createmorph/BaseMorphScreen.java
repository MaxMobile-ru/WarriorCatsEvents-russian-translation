package net.snowteb.warriorcats_events.screen.screens.createmorph;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.snowteb.warriorcats_events.WCEClient;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.client.ClientClanData;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCGenetics;
import net.snowteb.warriorcats_events.screen.screens.WCEOptionsScreen;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;

public class BaseMorphScreen extends Screen {

    WCGenetics genetics = new WCGenetics();
    WCGenetics.GeneticalVariants variants = new WCGenetics.GeneticalVariants();
    WCGenetics chimeraGenetics = new WCGenetics();
    WCGenetics.GeneticalChimeraVariants chimeraVariants = new WCGenetics.GeneticalChimeraVariants();
    boolean onGeneticalSkin = false;
    int presetVariant = 0;
    public float menuX = 0;

    public static final ResourceLocation DECORATIONS =
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/createmorph/decorations.png");
    public static final ResourceLocation MINI_DECORATIONS =
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/createmorph/mini_decorations.png");



    int otherListHeight = 0;

    final List<Renderable> subSectionRenderables = new ArrayList<>();

    protected int otherListsMinX = 0;
    protected int otherListsMinY = 0;
    protected int otherListsMaxX = 0;
    protected int otherListsMaxY = 0;

    protected int currentOtherListYPos = 0;

    public BaseMorphScreen(Component pTitle) {
        super(pTitle);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        pGuiGraphics.blit(WCEClient.WCE_TITLE,
                centerX - 250 - this.width,
                centerY - 62, 0, 0,
                250, 125, 250, 125);

        pGuiGraphics.blit(WCEClient.WCE_TITLE,
                centerX + this.width,
                centerY - 62, 0, 0,
                250, 125, 250, 125);

        renderSubRenderables(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        {
            RenderSystem.enableBlend();
            pGuiGraphics.setColor(1f,1f,1f,0.2f);

            int imgY = this.otherListsMaxY;
            int imgX = this.width - 3 - 17;
            int finalSize = this.otherListsMaxY + 30 - imgY - 5;

            pGuiGraphics.blit(ResourceLocation.fromNamespaceAndPath(
                            WarriorCatsEvents.MODID, "textures/gui/wce_title_logo.png"),
                    imgX - (otherListsMaxX - otherListsMinX)/2, imgY,
                    0, 0,
                    finalSize, finalSize,
                    finalSize, finalSize);
            pGuiGraphics.setColor(1f,1f,1f,1f);
            RenderSystem.disableBlend();
        }

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        renderError(pGuiGraphics);
    }


    private void renderSubRenderables(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderOtherListsScrollbar(pGuiGraphics);

        pGuiGraphics.enableScissor((int) (otherListsMinX + 1 + menuX), otherListsMinY + 1,
                (int) (otherListsMaxX - 1 + menuX), otherListsMaxY - 1);
        {
            for (Renderable renderable : this.subSectionRenderables) {
                if (renderable instanceof SubRenderable sub) {
                    if (sub.isClickableIn() == null) {
                        sub.setClickableIn((mouseX, mouseY) ->
                                mouseX >= otherListsMinX && mouseX <= otherListsMaxX &&
                                        mouseY >= otherListsMinY && mouseY <= otherListsMaxY);
                    }
                    if (renderable instanceof FancyButtonScrollList list) {
                        list.setMenuX((int) menuX);
                    }

                    sub.adjustYPos(-currentOtherListYPos);
                    renderable.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
                }
            }

        }
        pGuiGraphics.disableScissor();

        int color = 0x990D0C08;

        int yBlend = 8;
        pGuiGraphics.fillGradient(RenderType.guiOverlay(), this.otherListsMinX + 1, this.otherListsMinY + 1,
                this.otherListsMaxX + 4, this.otherListsMinY + yBlend,
                color, 0, 0);

        pGuiGraphics.fillGradient(RenderType.guiOverlay(), this.otherListsMinX + 1, this.otherListsMaxY - yBlend,
                this.otherListsMaxX + 4, this.otherListsMaxY - 1,
                0, color, 0);

    }

    private void renderOtherListsScrollbar(GuiGraphics pGuiGraphics) {
        int visibleHeight = otherListsMaxY - otherListsMinY;
        int contentHeight = getOtherListHeight();

        int maxScroll = Math.max(0, contentHeight - visibleHeight);

        if (maxScroll == 0) return;

        int thumbHeight = Math.max(
                15,
                (int)((float)visibleHeight * visibleHeight / contentHeight)
        );
        float scrollProgress = (float) currentOtherListYPos / maxScroll;

        int thumbTravel = visibleHeight - thumbHeight;

        int thumbY = otherListsMinY + (int)(scrollProgress * thumbTravel);
        int barX = otherListsMaxX;

        pGuiGraphics.fill(
                barX,
                otherListsMinY + 1,
                barX + 4,
                otherListsMaxY -1,
                0x44000000
        );

        pGuiGraphics.fill(
                barX,
                thumbY + 1,
                barX + 4,
                Math.min(thumbY + thumbHeight - 1, otherListsMaxY - 1),
                0xAAFFFFFF
        );
    }


    <T extends GuiEventListener & Renderable & NarratableEntry> int addSubRenderable(T pWidget, int yPos) {
        if (pWidget == null) return yPos;

        this.subSectionRenderables.add(pWidget);
        this.addWidget(pWidget);

        int height = 0;
        if (pWidget instanceof SubRenderable sub) {
            height = sub.getWidgetHeight();
        }

        return yPos + height + getSubRenderableSpacing();
    }

    int removeSubRenderable(GuiEventListener pListener, int yPos) {
        if (pListener instanceof Renderable) {
            this.subSectionRenderables.remove((Renderable)pListener);
        }
        this.removeWidget(pListener);

        int height = 0;
        if (pListener instanceof SubRenderable sub) {
            height = sub.getWidgetHeight();
        }

        return yPos - height;
    }

    void clearSubRenderables() {
        for (Renderable widget : subSectionRenderables) {
            if (widget instanceof GuiEventListener listener) {
                removeWidget(listener);
            }
        }
        subSectionRenderables.clear();
    }

    public int getSubRenderableSpacing() {
        return 5;
    }

    void recalculateListHeight() {
        int maxYWidget = otherListsMinY;

        for (Renderable renderable : this.subSectionRenderables) {
            if (renderable instanceof SubRenderable sub) {
                int widgetY1 = sub.getOriginalYPos1();
                if (widgetY1 > maxYWidget) {
                    maxYWidget = widgetY1;
                }
            }
        }

        this.otherListHeight = maxYWidget - otherListsMinY;
    }

    public int getOtherListHeight() {
        return otherListHeight + 20;
    }



    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            if (!ClientClanData.get().isFirstLoginHandled()) {
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        boolean superMs = super.mouseScrolled(pMouseX, pMouseY, pDelta);

        if (!superMs) {
            if (pMouseX > otherListsMinX && pMouseX < otherListsMaxX + 4
                    && pMouseY > otherListsMinY && pMouseY < otherListsMaxY) {

                int visibleHeight = otherListsMaxY - otherListsMinY;
                int maxScroll = Math.max(0, getOtherListHeight() - visibleHeight);

                int scrollSpeed = 15;

                currentOtherListYPos -= (int) (pDelta * scrollSpeed);
                currentOtherListYPos = Mth.clamp(currentOtherListYPos, 0, maxScroll);

                return true;
            }
        }
        return superMs;
    }

    boolean draggingScrollbar = false;
    int dragOffsetY = 0;

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (pButton == 0) {

            int visibleHeight = otherListsMaxY - otherListsMinY;
            int maxScroll = Math.max(0, getOtherListHeight() - visibleHeight);

            int thumbHeight = Math.max(
                    15,
                    (int)((float)visibleHeight * visibleHeight / getOtherListHeight())
            );

            float progress = maxScroll == 0
                    ? 0
                    : (float) currentOtherListYPos / maxScroll;

            int thumbTravel = visibleHeight - thumbHeight;
            int thumbY = otherListsMinY + (int)(progress * thumbTravel);

            int barX = otherListsMaxX;

            if (pMouseX >= barX && pMouseX <= barX + 4 &&
                    pMouseY >= thumbY && pMouseY <= thumbY + thumbHeight) {

                draggingScrollbar = true;
                dragOffsetY = (int)pMouseY - thumbY;
                return true;
            }
        }
        if (!super.mouseClicked(pMouseX, pMouseY, pButton)) {
            for (GuiEventListener listener : this.children()) {
                if (listener instanceof EditBox box) {
                    box.setFocused(false);
                }
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (draggingScrollbar) {

            int visibleHeight = otherListsMaxY - otherListsMinY;
            int maxScroll = Math.max(0, getOtherListHeight() - visibleHeight);

            if (maxScroll <= 0)
                return true;

            int thumbHeight = Math.max(
                    15,
                    (int)((float)visibleHeight * visibleHeight / getOtherListHeight())
            );

            int thumbTravel = visibleHeight - thumbHeight;

            int thumbTop = (int)pMouseY - dragOffsetY;
            thumbTop = Mth.clamp(
                    thumbTop,
                    otherListsMinY,
                    otherListsMinY + thumbTravel
            );

            float progress = (float)(thumbTop - otherListsMinY) / thumbTravel;

            currentOtherListYPos = (int)(progress * maxScroll);

            return true;
        }
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        draggingScrollbar = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }


    void drawMenuBackground(GuiGraphics pGuiGraphics) {
        int gradientColor = 0x32ABA06D;
        int gLenght = 40;

        int internalSpace = 3;

        pGuiGraphics.fill(internalSpace, internalSpace,
                this.width - internalSpace, this.height - internalSpace,
                0x33000000);
        pGuiGraphics.renderOutline(internalSpace, internalSpace,
                this.width - internalSpace * 2, this.height - internalSpace * 2,
                0x55EBD798);

        pGuiGraphics.fillGradient(internalSpace + 1, internalSpace + 1,
                this.width - internalSpace - 1, internalSpace + gLenght,
                gradientColor, 0);

        pGuiGraphics.fillGradient(internalSpace + 1, this.height - internalSpace - gLenght,
                this.width - internalSpace - 1, this.height - internalSpace - 1,
                0, gradientColor);

        WCEOptionsScreen.fillGradientHorizontal(pGuiGraphics,
                internalSpace + 1, internalSpace + 1,
                internalSpace + gLenght, this.height - internalSpace - 1,
                0, gradientColor, 0);

        WCEOptionsScreen.fillGradientHorizontal(pGuiGraphics,
                this.width - gLenght, internalSpace + 1,
                this.width - internalSpace - 1, this.height - internalSpace - 1,
                0, 0, gradientColor);
    }

    @Override
    public void tick() {
        for (Renderable renderable : this.renderables) {
            if (renderable instanceof SubRenderable sub) {
                sub.tick();
            }
        }
        for (Renderable renderable : this.subSectionRenderables) {
            if (renderable instanceof SubRenderable sub) {
                sub.tick();
            }
        }
        if (displayErrorTime > 0) displayErrorTime--;
    }


    protected int displayErrorTime = 0;
    private Component displayErrorText = Component.empty();
    private boolean isAnError = false;

    public void displayMessage(Component msg, boolean isAnError, int time) {
        displayErrorTime = time;
        displayErrorText = msg;
        this.isAnError = isAnError;
    }

    public void displayMessage(Component msg, boolean isAnError) {
        this.displayMessage(msg, isAnError, 140);
    }

    private void renderError(GuiGraphics pGuiGraphics) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        if (displayErrorTime > 0) {
            pGuiGraphics.pose().pushPose();

            float scale = 0.7f;

            List<FormattedCharSequence> lines = font.split(displayErrorText, (int) (140 * (1/scale)));

            int textWidth = 0;

            for (FormattedCharSequence line : lines) {
                if (font.width(line) > textWidth) textWidth = font.width(line);
            }

            int fillOffset = 6;

            int fillx0 = (-textWidth / 2 - fillOffset);
            int fillx1 = (textWidth / 2 + fillOffset);
            int filly0 = (-fillOffset / 2) - (isAnError ? 12 : 0);
            int filly1 = (9 * lines.size() + fillOffset / 2);

            pGuiGraphics.pose().translate(otherListsMinX - 7 - (float) (fillx1 - fillx0)/2*scale - 5, centerY - 72 - (isAnError ? 0 : 10), 1000);
            pGuiGraphics.pose().scale(scale, scale, scale);

            int color = isAnError ? 0xFFEB7C7C : 0xFFFFFFA0;

            float alpha = (float) displayErrorTime / 20;

            float finalAlpha = Mth.clamp(alpha, 0f, 1f);

            pGuiGraphics.setColor(1f,1f,1f, finalAlpha);
            {

                int shadowOffset = -4;

                pGuiGraphics.fill(fillx0 - 1 + shadowOffset, filly0 + shadowOffset,
                        fillx1 + 1 + shadowOffset, filly1 + shadowOffset + 1, 0x44000000);


                pGuiGraphics.fill(fillx0 - 1, filly0, fillx1 + 1, filly1, 0xFF29241D);
                pGuiGraphics.fill(fillx0, filly0 - 1, fillx1, filly1 + 1, 0xFF29241D);
                pGuiGraphics.fillGradient(fillx0, filly0, fillx1, filly1, 0xFFD4A068,  0xFFFFD4A8);
                pGuiGraphics.fill(fillx0 + 1, filly0 + 1, fillx1 - 1, filly1 - 1, 0xFF29241D);


                int y = 1;

                if (isAnError) {
                    pGuiGraphics.drawCenteredString(font, "⚠", 0, y - 11, 0xFFFFCB5C);
                }
                for (FormattedCharSequence line : lines) {
                    pGuiGraphics.drawCenteredString(font, line, 0,  y, color);
                    y += 9;
                }

            }
            pGuiGraphics.setColor(1f,1f,1f,1f);

            pGuiGraphics.pose().popPose();
        }
    }

    boolean catFreeView = false;

    public void renderCat(GuiGraphics pGuiGraphics, int pX, int pY, int pScale, float pMouseX, float pMouseY, LivingEntity pEntity) {
        float f = (float)Math.atan((double)(pMouseX / (catFreeView ? 20F : 40.0F)));
        float f1 = (float)Math.atan((double)(pMouseY / (catFreeView ? 20F : 40.0F)));

        Quaternionf quaternionf = (new Quaternionf()).rotateZ((float) Math.PI);
        Quaternionf quaternionf1 = (new Quaternionf()).rotateX(f1 * 20.0F * ((float) Math.PI / 180F));
        quaternionf.mul(quaternionf1);
        float f2 = pEntity.yBodyRot;
        float f3 = pEntity.getYRot();
        float f4 = pEntity.getXRot();
        float f5 = pEntity.yHeadRotO;
        float f6 = pEntity.yHeadRot;

        float magicNumber = catFreeView ? 100F : 20F;


        pEntity.yBodyRot = 180.0F + f * magicNumber;
        pEntity.setYRot(180.0F + f * 40.0F);
        pEntity.setXRot(-f1 * 20f);
        pEntity.yHeadRot = pEntity.getYRot();
        pEntity.yHeadRotO = pEntity.getYRot();
        InventoryScreen.renderEntityInInventory(pGuiGraphics, pX, pY, pScale, quaternionf, quaternionf1, pEntity);
        pEntity.yBodyRot = f2;
        pEntity.setYRot(f3);
        pEntity.setXRot(f4);
        pEntity.yHeadRotO = f5;
        pEntity.yHeadRot = f6;

    }
}
