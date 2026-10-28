package net.snowteb.warriorcats_events.battlemoves.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.util.Mth;
import net.snowteb.warriorcats_events.battlemoves.BattleMoveTypeAccessor;
import net.snowteb.warriorcats_events.screen.screens.createmorph.SubRenderable;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiPredicate;

public class BattleMovesSlotWidget implements Renderable, SubRenderable, GuiEventListener, NarratableEntry {

    private int x0;
    private int y0;
    private int width;
    private int height;

    private final int originalYpos0;
    private final int originalXpos0;

    private final BattleMovesCarouselWidget carousel;

    @Nullable
    private BattleMoveTypeAccessor accessor;

    private boolean pulsating = false;
    private int rendertime;
    private int index = -1;

    BattleMovesSlotWidget(int x, int y, int width, int height, BattleMovesCarouselWidget carousel,  BattleMoveTypeAccessor accessor) {
        this.x0 = x;
        this.y0 = y;
        this.width = width;
        this.height = height;

        this.originalYpos0 = y;
        this.originalXpos0 = x;

        this.carousel = carousel;
        this.accessor = accessor;
    }

    public void setAccessor(@Nullable BattleMoveTypeAccessor accessor) {
        this.accessor = accessor;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        int whiteColor = 0x66FFFFFF;
        this.rendertime++;
        if (rendertime > (Integer.MAX_VALUE - 1000)) this.rendertime = 0;

        if (pulsating) {
            float pulse = (Mth.sin(this.rendertime * 0.03F) + 1F) * 0.5F;
            int alpha = (int) (pulse * 255F);
            int color = (alpha << 24) | 0xC9B763;
            pGuiGraphics.fillGradient(x0, y0, x0 + width, y0 + height, 0x01000000, color);
        }

        pGuiGraphics.fill(x0,y0, x0 + width, y0 + height, whiteColor);
        pGuiGraphics.renderOutline(x0,y0, width, height, whiteColor);
        if (accessor != null) {

        } else {

        }
    }

    @Override
    public void setFocused(boolean pFocused) {

    }

    @Override
    public boolean isFocused() {
        return false;
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
        this.y0 = this.originalYpos0 + yOffset;
    }

    @Override
    public int getOriginalYPos0() {
        return originalYpos0;
    }

    @Override
    public int getOriginalYPos1() {
        return originalYpos0 + height;
    }

    @Override
    public void setClickableIn(BiPredicate<Double, Double> predicate) {

    }

    @Override
    public void adjustXPos(int xOffset) {
        this.x0 = originalXpos0 +  xOffset;
    }

    @Override
    public int getOriginalXPos0() {
        return originalXpos0;
    }

    @Override
    public int getOriginalXPos1() {
        return originalXpos0 + width;
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
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {

        if (!isMouseOver(pMouseX, pMouseY)) return false;

        // assign move through network
        {

        }

        carousel.setMoveToAssign(null);

        return GuiEventListener.super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    public void setPulsating(boolean b) {
        this.pulsating = b;
    }

    public void setIndex(int i) {
        this.index = i;
    }
}
