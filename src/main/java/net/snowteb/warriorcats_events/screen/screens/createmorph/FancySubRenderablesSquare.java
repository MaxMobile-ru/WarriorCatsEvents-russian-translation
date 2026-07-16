package net.snowteb.warriorcats_events.screen.screens.createmorph;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.snowteb.warriorcats_events.screen.screens.WCEOptionsScreen;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

public class FancySubRenderablesSquare implements Renderable, SubRenderable, GuiEventListener, NarratableEntry {
    private int x0;
    private int y0;
    private int width;
    private int height;

    private final int originalY0;
    private final int originalY1;
    private BiPredicate<Double, Double> isClickableIn;

    private final List<Renderable> subRenderables = new ArrayList<>();
    private GuiEventListener focused;
    private boolean dragging;


    public FancySubRenderablesSquare(int x0, int y0, int width, int height) {
        this.x0 = x0;
        this.y0 = y0;
        this.width = width;
        this.height = height;

        this.originalY0 = y0;
        this.originalY1 = originalY0 + height;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(0, 0, 1000);
        this.renderSquare(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        pGuiGraphics.pose().translate(0,0,200);
        this.renderWidgets(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        pGuiGraphics.pose().popPose();
    }

    private void renderSquare(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        boolean isMouseOver = pMouseX > x0 - 1 && pMouseX < x0 + width
                && pMouseY > y0 - 1 && pMouseY < y0 + height;

        int gradientColor = isMouseOver ? 0x38ABA06D : 0x32ABA06D;
        int gLenght = 30;

        pGuiGraphics.fill(x0, y0,
                x0 + this.width, y0 + this.height,
                0xee000000);

        pGuiGraphics.renderOutline(x0, y0,
                this.width, this.height,
                isMouseOver ? 0x94FFFFA0 : 0x88FFFFA0);

        pGuiGraphics.fillGradient(x0 + 1, y0 + 1,
                x0 + this.width - 1, y0 + gLenght,
                gradientColor, 0);

        pGuiGraphics.fillGradient(x0 + 1, y0 + this.height - gLenght,
                x0 + this.width - 1, y0 + this.height - 1,
                0, gradientColor);

        WCEOptionsScreen.fillGradientHorizontal(pGuiGraphics,
                x0 + 1, y0 + 1,
                x0 + gLenght, y0 + this.height - 1,
                0, gradientColor, 0);

        WCEOptionsScreen.fillGradientHorizontal(pGuiGraphics,
                this.width - gLenght, y0 + 1,
                x0 + this.width - 1, y0 + this.height - 1,
                0, 0, gradientColor);
    }

    private void renderWidgets(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        for (Renderable renderable :  this.getWidgets()) {
            renderable.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        }
    }

    @Override
    public boolean isMouseOver(double pMouseX, double pMouseY) {
        for (Renderable renderable :  this.getWidgets()) {
            if (renderable instanceof GuiEventListener listener) {
                if (listener.isMouseOver(pMouseX, pMouseY)) return true;
            }
        }
        return GuiEventListener.super.isMouseOver(pMouseX, pMouseY);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        for (Renderable renderable :  this.getWidgets()) {
            if (renderable instanceof GuiEventListener listener) {
                if (listener.mouseClicked(pMouseX, pMouseY, pButton)) {
                    listener.setFocused(true);
                    this.setFocusedWidget(listener);
                    return true;
                }
            }
        }
        if (!GuiEventListener.super.mouseClicked(pMouseX, pMouseY, pButton)) {
            for (Renderable renderable :  this.getWidgets()) {
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
        for (Renderable renderable :  this.getWidgets()) {
            if (renderable instanceof GuiEventListener listener) {
                if (listener.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY)) return true;
            }
        }
        return GuiEventListener.super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double scrollX, double scrollY) {
        for (Renderable renderable :  this.getWidgets()) {
            if (renderable instanceof GuiEventListener listener) {
                if (listener.mouseScrolled(pMouseX, pMouseY, scrollX, scrollY)) return true;
            }
        }
        return GuiEventListener.super.mouseScrolled(pMouseX, pMouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        for (Renderable renderable :  this.getWidgets()) {
            if (renderable instanceof GuiEventListener listener) {
                if (listener.mouseReleased(pMouseX, pMouseY, pButton)) return true;
            }
        }
        return GuiEventListener.super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    public void mouseMoved(double pMouseX, double pMouseY) {
        for (Renderable renderable :  this.getWidgets()) {
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
        for (Renderable renderable :  this.getWidgets()) {
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

    public boolean addWidget(Renderable renderable) {
        return this.getWidgets().add(renderable);
    }

    public boolean removeWidget(Renderable renderable) {
        return this.getWidgets().remove(renderable);
    }

    public void clearWidgets() {
        this.setFocusedWidget(null);
        this.getWidgets().clear();
    }

    public void setFocusedWidget(GuiEventListener listener) {
        if (this.focused != null && this.focused != listener) {
            this.focused.setFocused(false);
        }
        if (listener != null) {
            listener.setFocused(true);
        }
        this.focused = listener;
    }

    public boolean isDragging() {
        return dragging;
    }

    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    @Nullable
    public GuiEventListener getFocusedWidget() {
        return this.focused;
    }

    public List<Renderable> getWidgets() {
        return this.subRenderables;
    }

    @Override
    public void adjustYPos(int yOffset) {
        this.y0 = originalY0 + yOffset;
    }

    @Override
    public int getOriginalYPos0() {
        return originalY0;
    }

    @Override
    public int getOriginalYPos1() {
        return originalY1;
    }

    @Override
    public void setClickableIn(BiPredicate<Double, Double> predicate) {
        this.isClickableIn = predicate;
    }

    @Override
    public @Nullable BiPredicate<Double, Double> isClickableIn() {
        return this.isClickableIn;
    }


    @Override
    public void setFocused(boolean pFocused) {

    }

    @Override
    public boolean isFocused() {
        return this.focused != null;
    }

    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
    }
}
