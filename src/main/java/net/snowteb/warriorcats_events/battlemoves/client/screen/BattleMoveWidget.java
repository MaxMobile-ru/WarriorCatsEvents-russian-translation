package net.snowteb.warriorcats_events.battlemoves.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.snowteb.warriorcats_events.battlemoves.BattleMoveType;
import net.snowteb.warriorcats_events.screen.screens.clan.FancyLambdaRenderable;
import net.snowteb.warriorcats_events.screen.screens.createmorph.FancyStringWidget;
import net.snowteb.warriorcats_events.screen.screens.createmorph.SubRenderable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

import static net.snowteb.warriorcats_events.screen.screens.WCEOptionsScreen.fillGradientHorizontal;

public class BattleMoveWidget implements Renderable, SubRenderable, GuiEventListener, NarratableEntry {

    private float x0;
    private int xOffset = 0;
    private int y0;
    private final int width;
    private final int height;


    private final int originalY0;
    private final int originalX0;

    final BattleMoveType<?> battleMoveType;
    private final boolean unlocked;

    private float scale = 1f;
    private float scaleOffset;

    private final List<Renderable> widgets =  new ArrayList<>();
    private boolean focused = false;
    private int index = -1;

    private final BattleMovesCarouselWidget  carousel;
    private GuiEventListener focusedWidget;
    private int renderTime;
    private boolean pulsating;

    public BattleMoveWidget(int x0, int y0, int width, int height, BattleMoveType<?> battleMoveType, boolean unlocked, BattleMovesCarouselWidget carousel) {
        this.width = width;
        this.x0 = x0;
        this.originalX0 = x0;
        this.y0 = y0;
        this.height = height;
        this.battleMoveType = battleMoveType;
        this.originalY0 = y0;
        this.unlocked = unlocked;
        this.carousel = carousel;

        init();
    }

    private void init() {

        FancyStringWidget title = new FancyStringWidget(
                battleMoveType.getName(),
                 10, width - 10,
                y0 + 10, 1f, true, height - y0
        );
        addWidget(title);

        FancyLambdaRenderable lambdaRenderable = new FancyLambdaRenderable(
                width/2, y0 + 40, 0,
                (pGuiGraphics, mouseX, mouseY, pPartialTick) -> {

                    int iconSize = 25;

                    pGuiGraphics.blit(
                            battleMoveType.getIcon(),
                            -iconSize/2, -iconSize/2,
                            0,0,
                            iconSize,iconSize,
                            iconSize,iconSize
                    );


                    float descScale = 0.6f;
                    List<FormattedCharSequence> description = Minecraft.getInstance().font
                            .split(battleMoveType.getDescription(), (int) (width/descScale) - 15);


                    pGuiGraphics.pose().pushPose();
                    pGuiGraphics.pose().translate(0, 20, 0);
                    pGuiGraphics.pose().scale(descScale, descScale, descScale);
                    int y = 0;
                    for (FormattedCharSequence s : description) {
                        pGuiGraphics.drawCenteredString(Minecraft.getInstance().font, s, 0, y, 0xFFFFFF);
                        y += 9;
                    }
                    pGuiGraphics.pose().popPose();

                }
        );

        addWidget(lambdaRenderable);




    }

    public void setPulsating(boolean pulsating) {
        this.pulsating = pulsating;
    }

    public void addWidget(Renderable widget) {
        this.widgets.add(widget);
    }

    public void setXOffset(int x) {
        this.xOffset = x;
    }

    public void setScaleOffset(float scale) {
        this.scaleOffset = scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public int getX() {
        return Math.round(x0);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {

        this.renderTime++;
        if (renderTime > (Integer.MAX_VALUE - 1000)) this.renderTime = 0;

        float target = originalX0 + xOffset;
        x0 = Mth.lerp(0.1f, x0, target);

        if (Math.abs(x0 - target) < 0.5f) {
            x0 = target;
        }

        float drawX = x0;
        float drawY = y0;

        float targetScale = 1f + scaleOffset;
        scale = Mth.lerp(0.1f, scale, targetScale);

        float centerX = drawX + width / 2f;
        float centerY = drawY + height / 2f;

        int typeColor = this.battleMoveType.getCategory().getColor();

        pGuiGraphics.pose().pushPose();

        pGuiGraphics.pose().translate(centerX, centerY, 0);
        pGuiGraphics.pose().scale(scale, scale, scale);
        pGuiGraphics.pose().translate(-width / 2f, -height / 2f, 0);

        int gradientCenterX = width / 2;
        int gradientCenterY = height / 4;

        pGuiGraphics.fill(0, 0, width, height, typeColor);


        pGuiGraphics.fillGradient(0, 0, width, gradientCenterY, 0x99000000, 0x01000000);
        pGuiGraphics.fillGradient(0, gradientCenterY, width, height, 0x01000000, 0x99000000);
        fillGradientHorizontal(pGuiGraphics, 0, 0, gradientCenterX, height, 0, 0x99000000, 0x01000000);
        fillGradientHorizontal(pGuiGraphics, gradientCenterX, 0, width, height, 0, 0x01000000, 0x99000000);

        if (pulsating) {
            float pulse = (Mth.sin(this.renderTime * 0.03F) + 1F) * 0.5F;
            int alpha = (int) (pulse * 255F);
            int color = (alpha << 24) | 0xC9B763;
            pGuiGraphics.fillGradient(0, 0, width, height, 0x01000000, color);
        }

        pGuiGraphics.renderOutline(1, 1, width-2, height-2, 0x55FFFFFF);

        if (!this.focused) {
            pGuiGraphics.fill(0, 0, width, height, 0x99000000);
        }

        pGuiGraphics.pose().popPose();


        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(centerX, centerY, 0);
        pGuiGraphics.pose().scale(scale, scale, scale);
        pGuiGraphics.pose().translate(-centerX, -centerY, 0);
        for (Renderable renderable : widgets) {
            if (renderable instanceof SubRenderable sub) sub.adjustXPos((int) x0);
            renderable.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        }
        pGuiGraphics.pose().popPose();

    }

    @Override
    public void setFocused(boolean pFocused) {
        this.focused = pFocused;
        for (Renderable renderable : widgets) {
            if (renderable instanceof AbstractWidget widget) widget.active = pFocused;
        }
    }

    @Override
    public boolean isFocused() {
        return focused;
    }

    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
    }

    @Override
    public void adjustYPos(int yOffset) {
        this.y0 = getOriginalYPos0() + yOffset;
    }

    @Override
    public int getOriginalYPos0() {
        return originalY0;
    }

    @Override
    public int getOriginalYPos1() {
        return originalY0 + height;
    }

    @Override
    public void setClickableIn(BiPredicate<Double, Double> predicate) {

    }

    @Override
    public @Nullable BiPredicate<Double, Double> isClickableIn() {
        return null;
    }

    @Override
    public boolean  isMouseOver(double pMouseX, double pMouseY) {
        return pMouseX >= x0 && pMouseX < x0 + width
                && pMouseY >= y0 && pMouseY < y0 + height;
    }

    @Override
    public void tickkkk() {
        for (Renderable renderable : widgets) {
            if (renderable instanceof SubRenderable sub) sub.tickkkk();
        }
    }

    @Override
    public void adjustXPos(int xOffset) {
        this.x0 = originalX0 + xOffset;
        for (Renderable renderable : widgets) {
            if (renderable instanceof SubRenderable sub) sub.adjustXPos(xOffset);
        }
    }

    @Override
    public int getOriginalXPos0() {
        return this.originalX0;
    }

    @Override
    public int getOriginalXPos1() {
        return this.originalX0 + width;
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {


        if (!isMouseOver(pMouseX, pMouseY)) {
            return false;
        }

        if (!this.focused) {
            this.carousel.setIndex(this.index);
            return false;
        }

        for (Renderable renderable :  this.widgets) {
            if (renderable instanceof GuiEventListener listener) {
                if (listener.mouseClicked(pMouseX, pMouseY, pButton)) {
                    listener.setFocused(true);
                    this.setFocusedWidget(listener);
//                    this.setDragging(true);
                    return true;
                }
            }
        }
        if (!GuiEventListener.super.mouseClicked(pMouseX, pMouseY, pButton)) {
            for (Renderable renderable :  this.widgets) {
                if (renderable instanceof GuiEventListener listener) {
                    listener.setFocused(false);
                    this.setFocusedWidget(null);
                }
            }

            boolean isMouseOver = pMouseX > x0 - 1 && pMouseX < x0 + width
                    && pMouseY > y0 - 1 && pMouseY < y0 + height;
            return isMouseOver;
        }

        return false;
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        return false;
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        for (Renderable renderable :  this.widgets) {
            if (renderable instanceof GuiEventListener listener) {
                if (listener.isMouseOver(pMouseX, pMouseY)) {
                    if (listener.mouseScrolled(pMouseX, pMouseY, pDelta)) return true;
                }
            }
        }
        return GuiEventListener.super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        for (Renderable renderable :  this.widgets) {
            if (renderable instanceof GuiEventListener listener) {
                if (listener.mouseReleased(pMouseX, pMouseY, pButton)) return true;
            }
        }
        return GuiEventListener.super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    public void mouseMoved(double pMouseX, double pMouseY) {
        for (Renderable renderable :  this.widgets) {
            if (renderable instanceof GuiEventListener listener) {
                listener.mouseMoved(pMouseX, pMouseY);
            }
        }
        GuiEventListener.super.mouseMoved(pMouseX, pMouseY);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (this.getFocusedWidget() != null) {
            return (this.getFocusedWidget().keyPressed(pKeyCode, pScanCode, pModifiers));
        }
        return GuiEventListener.super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        for (Renderable renderable :  this.widgets) {
            if (renderable instanceof GuiEventListener listener) {
                if (listener.keyReleased(pKeyCode, pScanCode, pModifiers)) return true;
            }
        }
        return GuiEventListener.super.keyReleased(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean charTyped(char pCodePoint, int pModifiers) {
        if (this.getFocusedWidget() != null) {
            return (this.getFocusedWidget().charTyped(pCodePoint, pModifiers));
        }
        return GuiEventListener.super.charTyped(pCodePoint, pModifiers);
    }

    public void setFocusedWidget(GuiEventListener listener) {
        if (this.focusedWidget != null && this.focusedWidget != listener) {
            this.focusedWidget.setFocused(false);
        }
        if (listener != null) {
            listener.setFocused(true);
        }
        this.focusedWidget = listener;
    }

    @Nullable
    public GuiEventListener getFocusedWidget() {
        return this.focusedWidget;
    }

    public void setIndex(int i) {
        this.index = i;
    }

}
