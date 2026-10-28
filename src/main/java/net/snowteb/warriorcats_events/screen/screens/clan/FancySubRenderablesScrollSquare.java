package net.snowteb.warriorcats_events.screen.screens.clan;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.snowteb.warriorcats_events.screen.screens.createmorph.FancySubRenderablesSquare;
import net.snowteb.warriorcats_events.screen.screens.createmorph.SubRenderable;

public class FancySubRenderablesScrollSquare extends FancySubRenderablesSquare {

    private int contentHeight = 0;
    private int contentScrollY = 0;
    private int dragOffsetY = 0;
    private boolean renderSquare = true;
    private int extraHeight = 0;

    private boolean scrollingSquare = false;

    public FancySubRenderablesScrollSquare(int x0, int y0, int width, int height) {
        super(x0, y0, width, height);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(0, 0, 1300);
        renderScrollbar(pGuiGraphics);
        pGuiGraphics.pose().popPose();
    }

    @Override
    protected void renderWidgets(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        pGuiGraphics.enableScissor(getX0() + 1, getY0() + 1,
                getX0() + getWidth() - 1, getY0() + getHeight() - 1);

        for (Renderable renderable : this.getWidgets()) {
            if (renderable instanceof SubRenderable sub) {
                sub.adjustYPos(-contentScrollY);
            }
            renderable.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        }

        pGuiGraphics.disableScissor();
    }

    @Override
    protected void renderSquare(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (!renderSquare) return;
        super.renderSquare(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    public void setRenderSquare(boolean renderSquare) {
        this.renderSquare = renderSquare;
    }

    private void renderScrollbar(GuiGraphics pGuiGraphics) {
        int maxScroll = getMaxScroll();
        if (maxScroll == 0) return;

        int thumbHeight = getThumbHeight();
        int thumbY = getThumbY();
        int barX = getX0() + getWidth() - 4;

        pGuiGraphics.fill(
                barX,
                getY0() + 1,
                barX + 4,
                getY0() + getHeight() - 1,
                0x44000000
        );

        pGuiGraphics.fill(
                barX,
                thumbY + 1,
                barX + 4,
                Math.min(thumbY + thumbHeight - 1, getY0() + getHeight() - 1),
                0xAAFFFFFF
        );
    }

    private void recalculateContentHeight() {
        int maxY = getOriginalYPos0();
        for (Renderable renderable : this.getWidgets()) {
            if (renderable instanceof SubRenderable sub) {
                int y1 = sub.getOriginalYPos1();
                if (y1 > maxY) maxY = y1;
            }
        }
        this.contentHeight = (maxY - getOriginalYPos0()) + extraHeight;
    }

    public void addHeight(int extraHeight) {
        this.extraHeight += extraHeight;
        recalculateContentHeight();
    }

    public int getMaxScroll() {
        return Math.max(0, contentHeight - getHeight()) + (contentHeight > this.getHeight() ? 10 : 0);
    }

    public int getContentHeight() {
        return contentHeight;
    }

    public int getContentBottom() {
        return getOriginalYPos0() + contentHeight;
    }

    private int getThumbHeight() {
        int usableContent = Math.max(contentHeight, getHeight() + 1);
        return Math.max(15, (int) ((float) getHeight() * getHeight() / usableContent));
    }

    private int getThumbY() {
        int maxScroll = getMaxScroll();
        float progress = maxScroll == 0 ? 0 : (float) contentScrollY / maxScroll;
        int thumbTravel = getHeight() - getThumbHeight();
        return getY0() + (int) (progress * thumbTravel);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        this.scrollingSquare = false;

        if (pButton == 0 && getMaxScroll() > 0) {
            int thumbHeight = getThumbHeight();
            int thumbY = getThumbY();
            int barX = getX0() + getWidth() - 4;

            if (pMouseX >= barX && pMouseX <= barX + 4 &&
                    pMouseY >= thumbY && pMouseY <= thumbY + thumbHeight) {
                this.scrollingSquare = true;
                this.dragOffsetY = (int) pMouseY - thumbY;
                return true;
            }
        }

        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (scrollingSquare) {
            int maxScroll = getMaxScroll();
            if (maxScroll <= 0) {
                this.setDragging(false);
                return true;
            }

            int thumbHeight = getThumbHeight();
            int thumbTravel = getHeight() - thumbHeight;

            int thumbTop = (int) pMouseY - dragOffsetY;
            thumbTop = Mth.clamp(thumbTop, getY0(), getY0() + thumbTravel);

            float progress = thumbTravel == 0 ? 0 : (float) (thumbTop - getY0()) / thumbTravel;
            contentScrollY = (int) (progress * maxScroll);

            return true;
        }

        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        boolean mouseScrolled = super.mouseScrolled(pMouseX, pMouseY, pDelta);

        if (mouseScrolled) return true;

        boolean isMouseOver = pMouseX > getX0() - 1 && pMouseX < getX0() + getWidth()
                && pMouseY > getY0() - 1 && pMouseY < getY0() + getHeight();

        if (isMouseOver) {
            int maxScroll = getMaxScroll();
            if (maxScroll > 0) {
                int scrollSpeed = 15;
                contentScrollY -= (int) (pDelta * scrollSpeed);
                contentScrollY = Mth.clamp(contentScrollY, 0, maxScroll);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        if (this.scrollingSquare) {
            this.scrollingSquare = false;
            return true;
        }

        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean addWidget(Renderable renderable) {
        if (!(renderable instanceof SubRenderable)) return false;

        this.getWidgets().add(renderable);
        recalculateContentHeight();
        return true;
    }

    @Override
    public boolean removeWidget(Renderable renderable) {
        super.removeWidget(renderable);
        recalculateContentHeight();
        return true;
    }

    @Override
    public void clearWidgets() {
        this.setFocusedWidget(null);
        this.getWidgets().clear();
        this.contentScrollY = 0;
        recalculateContentHeight();
    }


}
