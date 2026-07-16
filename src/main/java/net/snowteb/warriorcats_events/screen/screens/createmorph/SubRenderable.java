package net.snowteb.warriorcats_events.screen.screens.createmorph;

import org.jetbrains.annotations.Nullable;

import java.util.function.BiPredicate;

public interface SubRenderable {
    void adjustYPos(int yOffset);
    int getOriginalYPos0();
    int getOriginalYPos1();

    void setClickableIn(BiPredicate<Double, Double> predicate);

    default void tick(){}

    default int getWidgetHeight() {
        return getOriginalYPos1() - getOriginalYPos0();
    }

    @Nullable
    BiPredicate<Double, Double> isClickableIn();
}
