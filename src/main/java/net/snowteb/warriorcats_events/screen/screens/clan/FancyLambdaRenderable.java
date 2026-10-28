package net.snowteb.warriorcats_events.screen.screens.clan;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.snowteb.warriorcats_events.screen.screens.createmorph.SubRenderable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiPredicate;

public class FancyLambdaRenderable implements Renderable, SubRenderable, GuiEventListener, NarratableEntry {

    private int x;
    private int y;
    private int z;

    private final int originalY0;
    private final int originalX0;
    private int y1 = 0;
    private int x1 = 0;
    private final LambdaRender render;

    public FancyLambdaRenderable(int x, int y, int z, LambdaRender render) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.render = render;

        this.originalY0 = y;
        this.originalX0 = x;
    }

    public FancyLambdaRenderable(LambdaRender render) {
        this(0,0,0,render);
    }

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(x, y, z);
        pMouseX -= x;
        pMouseY -= y;
        this.render.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        pGuiGraphics.pose().popPose();
    }


    @Override
    public void adjustYPos(int yOffset) {
        this.y = originalY0 + yOffset;
    }

    @Override
    public int getOriginalYPos0() {
        return originalY0;
    }

    @Override
    public int getOriginalYPos1() {
        return y1;
    }

    public FancyLambdaRenderable setHeight(int height) {
        height = Math.abs(height);

        this.y1 = originalY0 + height;
        return this;
    }

    public FancyLambdaRenderable bounds(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    @Override
    public void setClickableIn(BiPredicate<Double, Double> predicate) {
    }

    @Override
    public void adjustXPos(int xOffset) {
        this.x = originalX0 + xOffset;
    }

    @Override
    public int getOriginalXPos0() {
        return originalX0;
    }

    @Override
    public int getOriginalXPos1() {
        return x1;
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

    @Override
    public @NotNull NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(@NotNull NarrationElementOutput pNarrationElementOutput) {
    }

    public interface LambdaRender {
        void render(GuiGraphics pGuiGraphics, int mouseX, int mouseY, float pPartialTick);
    }
}
