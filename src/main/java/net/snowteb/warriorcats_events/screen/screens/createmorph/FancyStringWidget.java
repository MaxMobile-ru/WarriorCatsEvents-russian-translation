package net.snowteb.warriorcats_events.screen.screens.createmorph;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.snowteb.warriorcats_events.screen.screens.WCEOptionsScreen;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiPredicate;

public class FancyStringWidget implements Renderable, SubRenderable, GuiEventListener, NarratableEntry {
    private final Component text;
    private final int color;
    private final float scale;

    private int x0 = 0;
    private int y0 = 0;
    private int x1 = 0;
    private int y1 = 0;

    private final boolean centered;

    private final int originalY0;
    private final int originalY1;

    private final int hoverAreaYExtension;

    public FancyStringWidget(Component text, int x0, int x1, int y0, int color, float scale, boolean centered, int hoverAreaYExtension) {
        this.text = text;
        this.color = color;
        this.scale = scale;
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = (int) (y0 + (9*scale)) + 2;
        this.centered = centered;

        this.originalY0 = y0;
        this.originalY1 = y1;

        this.hoverAreaYExtension = hoverAreaYExtension;
    }

    public FancyStringWidget(Component text, int x0, int x1, int y0, float scale, boolean centered, int hoverAreaYExtension) {
        this(text, x0, x1, y0,0xFFF2DCAE, scale, centered, hoverAreaYExtension);
    }

    public FancyStringWidget(Component text, int x0, int x1, int y0, int hoverAreaYExtension) {
        this(text, x0, x1, y0,0xFFF2DCAE, 1f, false, hoverAreaYExtension);
    }

    private float lineX1 = 0;

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {

        Minecraft mc = Minecraft.getInstance();

        int areaWidth = x1 - x0;

        int xPos = centered ? x0 + areaWidth / 2 : x0;

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(xPos, y0 + 1, 0);

        int textWidth = mc.font.width(text);
        float scale = this.scale;
        if (textWidth * scale > areaWidth) {
            scale = (float) areaWidth / textWidth;
        }

        pGuiGraphics.pose().scale(scale, scale, scale);

        if (!centered) {
            pGuiGraphics.drawString(mc.font, text, 0, 0, color);
        } else {
            pGuiGraphics.drawCenteredString(mc.font, text, 0, 0, color);
        }

        pGuiGraphics.pose().popPose();

        int lineBaseColor = 0x33FFFFFF;
        int lineFadeColor = 0x00FFFFFF;

        if (!centered) {
            int lineX1Pos = (int) (x0 + mc.font.width(text)*scale) + 3;
            translateLine(this.isHovered(pMouseX, pMouseY), x0-4, lineX1Pos);

            pGuiGraphics.hLine(x0-4, lineX1Pos, y1, lineBaseColor);
            WCEOptionsScreen.fillGradientHorizontal(pGuiGraphics, x0-4, y1, Math.max(Math.round(lineX1), x0-4), y1 + 1, 0,color, lineFadeColor);
        } else {
            int lineWidth = x1 - x0;
            int centerX = x0 + (x1 - x0)/2;
            int centeredX0 = (int) (centerX - mc.font.width(text)*scale/2) - 3;
            int centeredX1 = (int) (centerX + mc.font.width(text)*scale/2) + 3;

            int lineX1Pos = centeredX1 + 3;

            translateLine(this.isHovered(pMouseX, pMouseY), centerX, lineX1Pos);

            pGuiGraphics.hLine(centeredX0, centeredX1, y1, lineBaseColor);

            int rightExtent = Mth.clamp(Math.round(lineX1), centerX, centeredX1);
            WCEOptionsScreen.fillGradientHorizontal(pGuiGraphics, centerX, y1, rightExtent, y1 + 1, 0, color, lineFadeColor);

            int mirroredX = 2 * centerX - Math.round(lineX1);
            int leftExtent = Mth.clamp(mirroredX, centeredX0, centerX);
            WCEOptionsScreen.fillGradientHorizontal(pGuiGraphics, leftExtent, y1, centerX, y1 + 1, 0, lineFadeColor, color);
        }

    }

    private void translateLine(boolean value, int tg, int maxLength) {
        int target = value ? maxLength : tg;
        lineX1 = Mth.lerp(0.12F, lineX1, target);
    }

    @Override
    public void adjustYPos(int yOffset) {
        this.y0 = originalY0 + yOffset;
        this.y1 = originalY1 + yOffset;
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
    }

    @Override
    public @Nullable BiPredicate<Double, Double> isClickableIn() {
        return null;
    }

    @Override
    public void setFocused(boolean pFocused) {
    }

    @Override
    public boolean isFocused() {
        return false;
    }

    public boolean isHovered(int pMouseX, int pMouseY) {
        return pMouseX > x0 && pMouseX < x1 && pMouseY > y0 - 3 && pMouseY < y1 + 3 + hoverAreaYExtension;
    }

    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
    }
}
