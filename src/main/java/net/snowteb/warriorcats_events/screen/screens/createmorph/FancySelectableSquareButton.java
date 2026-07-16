package net.snowteb.warriorcats_events.screen.screens.createmorph;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiPredicate;

/**
 * A custom button that allows to have a custom texture and other aesthetic settings.
 */
public class FancySelectableSquareButton extends AbstractButton implements SubRenderable {

    private BiPredicate<Double, Double> isClickableIn;

    final int originalYPos0;
    final int originalYPos1;

    @Override
    public void adjustYPos(int yOffset) {
        this.setY(this.getOriginalYPos0() + yOffset);
    }

    @Override
    public int getOriginalYPos0() {
        return this.originalYPos0;
    }

    @Override
    public int getOriginalYPos1() {
        return this.originalYPos1;
    }

    @Override
    public void setClickableIn(BiPredicate<Double, Double> predicate) {
        isClickableIn = predicate;
    }

    @Override
    public @Nullable BiPredicate<Double, Double> isClickableIn() {
        return isClickableIn;
    }

    public interface PressAction {
        void onPress(FancySelectableSquareButton button);
    }

    private final PressAction onPress;
    private int color = 0xFFFFFF;
    private boolean selected;
    private final String key;

    public FancySelectableSquareButton(int x, int y, int size, PressAction onPress, int color, String key) {
        super(x, y, size, size/2, Component.empty());
        this.originalYPos0 = y;
        this.originalYPos1 = originalYPos0 + height;

        this.onPress = onPress;
        this.color = color;
        this.key = key;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean value) {
        this.selected = value;
    }

    public String getKey() {
        return key;
    }

    public boolean matchesKey(String key) {
        return key.equals(getKey());
    }

    public void selectIfMatches(String key) {
        this.setSelected(matchesKey(key));
    }


    @Override
    public void onPress() {
        this.selected = !this.selected;
        this.onPress.onPress(this);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean isMouseOver(double pMouseX, double pMouseY) {
        return super.isMouseOver(pMouseX, pMouseY);
    }

    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int mouseX, int mouseY, float partialTicks) {

        boolean hovered = this.isHoveredOrFocused();

        int alpha = 0xcc;
        int colorGradient = this.isSelected() ? color : (alpha << 24) | (color & 0xFFFFFF);

        pGuiGraphics.fillGradient(this.getX(), this.getY(),
                this.width + this.getX(),this.height + this.getY(),
                0x44000000, colorGradient);
        pGuiGraphics.fillGradient(this.getX(), this.getY(),
                this.width + this.getX(),this.height + this.getY(),
                0x44000000, colorGradient);


        pGuiGraphics.fillGradient(this.getX(), this.getY(),
                this.width + this.getX(),this.height + this.getY(),
                0, 0x55000000);

        {
            translateGradient(this.isSelected());
            int gradientY = this.getY() + Math.round(gradientOffset);

            pGuiGraphics.fillGradient(this.getX(), gradientY,
                    this.width + this.getX(),this.height + this.getY(),
                    0x00BDAD5C, 0xFFFFF6E8);
        }

        pGuiGraphics.renderOutline(this.getX(), this.getY(),
                this.width,this.height,
                selected ? (this.isActive() ? 0xFFFFFFA0 : 0x88FFFFA0) : 0x61F2DCAE);


        if (hovered && active) {
            pGuiGraphics.fill(this.getX(), this.getY(),
                    this.getX() + this.width,
                    this.getY() + this.height,
                    0x20FFFFFF);
        }

        if (!this.active) {
            pGuiGraphics.fill(this.getX(), this.getY(),
                    this.width + this.getX(),this.height + this.getY(),
                    0, 0x77000000);
        }


    }

    private float gradientOffset = this.height;

    private void translateGradient(boolean value) {
        float target = value ? this.height - ((float) this.height /4) : this.height;
        gradientOffset = Mth.lerp(0.125F, gradientOffset, target);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narration) {}
}
