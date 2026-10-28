package net.snowteb.warriorcats_events.screen.screens.clan;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.snowteb.warriorcats_events.screen.screens.createmorph.SubRenderable;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiPredicate;

public class FancyEditBox extends EditBox implements SubRenderable {

    private final int originalY0;
    private final int originalY1;
    private final int originalX0;
    private final int originalX1;
    private BiPredicate<Double, Double> isClickableIn;

    public FancyEditBox(Font font, int x, int y, int width, int height) {
        super(font, x, y, width, height, Component.empty());
        this.originalY0 = y;
        this.originalY1 = y + height;
        this.originalX0 = x;
        this.originalX1 = x + width;
    }

    @Override
    public void adjustYPos(int yOffset) {
        this.setY(originalY0 + yOffset);
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
    public void adjustXPos(int xOffset) {
        this.setX(originalX0 + xOffset);
    }

    @Override
    public int getOriginalXPos0() {
        return originalX0;
    }

    @Override
    public int getOriginalXPos1() {
        return originalX1;
    }

    @Override
    public @Nullable BiPredicate<Double, Double> isClickableIn() {
        return this.isClickableIn;
    }

}
