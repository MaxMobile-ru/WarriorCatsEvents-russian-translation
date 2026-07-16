package net.snowteb.warriorcats_events.screen.screens.createmorph;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.snowteb.warriorcats_events.screen.screens.WCEOptionsScreen;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiPredicate;
import java.util.function.Consumer;

public class FancyFloatSlider extends AbstractSliderButton implements SubRenderable{

    private final float min;
    private final float max;
    private final String text;
    private final float scale;
    private final Consumer<FancyFloatSlider> onSetValue;

    public FancyFloatSlider(int x, int y, int width, int height,
                            float min, float max, float initialValue,
                            Consumer<FancyFloatSlider> onSetValue,
                            Component text) {
        this(x, y, width, height, min, max, initialValue, text, 0.85f, onSetValue);
    }

    public FancyFloatSlider(int x, int y, int width, int height,
                            float min, float max, float initialValue,
                            Consumer<FancyFloatSlider> onSetValue) {
        this(x, y, width, height, min, max, initialValue, Component.empty(), 0.85f, onSetValue);
    }

    public FancyFloatSlider(int x, int y, int width, int height,
                            float min, float max, float initialValue,
                            Component text, float scale,
                            Consumer<FancyFloatSlider> onSetValue) {

        super(x, y, width, height, Component.empty(), (initialValue - min) / (max - min));

        this.min = min;
        this.max = max;
        this.text = text.getString();
        this.scale = scale;

        this.originalYPos0 = y;
        this.originalYPos1 = originalYPos0 + height;
        this.onSetValue = onSetValue;

        updateMessage();
    }

    @Override
    public void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        Minecraft minecraft = Minecraft.getInstance();

        int colorGradient = 0xFFC9B763;

        pGuiGraphics.fillGradient(this.getX(), this.getY(),
                this.width + this.getX(),this.height + this.getY(),
                0x44000000, colorGradient);

        pGuiGraphics.fillGradient(this.getX(), this.getY(),
                this.width + this.getX(),this.height + this.getY(),
                0, 0x55000000);

        {
            translateGradient();
            int gradientX = this.getX() + Math.round(gradientOffset);

            WCEOptionsScreen.fillGradientHorizontal(pGuiGraphics, this.getX(), this.getY(),
                    gradientX,this.height + this.getY(),
                    0, 0xFFFFF6E8, 0x00BDAD5C);
        }

        pGuiGraphics.renderOutline(this.getX(), this.getY(), this.width,this.height, this.isHoveredOrFocused() ? 0xFFFFFFA0 : 0x61F2DCAE);


        if (isHovered() && active) {
            pGuiGraphics.fill(this.getX(), this.getY(),
                    this.getX() + this.width,
                    this.getY() + this.height,
                    0x20FFFFFF);
        }

        pGuiGraphics.blitNineSliced(SLIDER_LOCATION,
                this.getX() + (int)(this.value * (double)(this.width - 8)), this.getY(),
                8, this.getHeight(),
                20, 4,
                200, 20,
                0, this.getHandleTextureY());

        pGuiGraphics.pose().pushPose();
        int centerX = this.getX() + this.getWidth() / 2;
        int centerY = (int) (this.getY() + ((float) this.getHeight() / 2)*scale) - 1;
        pGuiGraphics.pose().translate(centerX, centerY, 0);
        pGuiGraphics.pose().scale(scale, scale, scale);
        pGuiGraphics.drawCenteredString(minecraft.font, this.getMessage(),
                0, 0, this.active ? 0xF2DCAE : 0xFF555555);
        pGuiGraphics.pose().popPose();

    }

    private float gradientOffset = 0;

    private void translateGradient() {
        float target = (float) (this.getWidth() * this.value*0.66f);
        gradientOffset = Mth.lerp(0.125F, gradientOffset, target);
    }



    @Override
    protected void updateMessage() {
        if (text.isEmpty()) {
            this.setMessage(Component.literal(String.format("%.2f", getActualValue())));
        } else {
            this.setMessage(Component.literal(text + String.format(": %.2f", getActualValue())));
        }
    }

    @Override
    protected void applyValue() {
        onSetValue.accept(this);
    }

    public float getActualValue() {
        return min + (float)value * (max - min);
    }


    private final int originalYPos0;
    private final int originalYPos1;

    @Override
    public void adjustYPos(int yOffset) {
        this.setY(this.originalYPos0 + yOffset);
    }

    @Override
    public int getOriginalYPos0() {
        return originalYPos0;
    }

    @Override
    public int getOriginalYPos1() {
        return originalYPos1;
    }

    private BiPredicate<Double, Double> isClickableIn;

    @Override
    public void setClickableIn(BiPredicate<Double, Double> predicate) {
        this.isClickableIn = predicate;
    }

    @Override
    public @Nullable BiPredicate<Double, Double> isClickableIn() {
        return isClickableIn;
    }
}
