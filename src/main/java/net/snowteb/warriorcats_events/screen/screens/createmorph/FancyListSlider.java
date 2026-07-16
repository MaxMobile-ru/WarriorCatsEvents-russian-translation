package net.snowteb.warriorcats_events.screen.screens.createmorph;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.snowteb.warriorcats_events.screen.screens.WCEOptionsScreen;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class FancyListSlider extends AbstractSliderButton implements SubRenderable{

    private final int min;
    private final int max;
    private final float scale;
    private final Consumer<FancyListSlider> onSetValue;

    private final List<FancyListEntry> entryList;

    public record FancyListEntry(Component msg, String key, Predicate<String> matcher){
    }

    public static class EntryBuilder {
        private final List<FancyListEntry> rawList;

        private EntryBuilder() {
            rawList = new ArrayList<>();
        }

        public static EntryBuilder builder() {
            return new EntryBuilder();
        }

        public EntryBuilder add(Component text, String key, Predicate<String> matcher) {
            rawList.add(new FancyListEntry(text, key, matcher));
            return this;
        }

        public List<FancyListEntry> build() {
            return rawList;
        }
    }

    public FancyListSlider(int x, int y, int width, int height,
                           List<FancyListEntry> list, Consumer<FancyListSlider> onSetValue) {

        this(x, y, width, height, 0, list.size()-1, 0, 0.85f, onSetValue, list);
    }

    private FancyListSlider(int x, int y, int width, int height,
                           int min, int max, int initialValue,
                            float scale,
                           Consumer<FancyListSlider> onSetValue, List<FancyListEntry> list) {

        super(x, y, width, height, Component.empty(), (double) (initialValue - min) / (max - min));

        this.min = min;
        this.max = max;
        this.scale = scale;

        this.originalYPos0 = y;
        this.originalYPos1 = originalYPos0 + height;
        this.onSetValue = onSetValue;

        this.entryList = list;

        updateMessage();
    }

    @Override
    protected void updateMessage() {
        FancyListEntry entry = this.entryList.get(getActualValue());
        if (entry == null) {
            this.setMessage(Component.literal(String.valueOf(getActualValue())));
        } else {
            this.setMessage(entry.msg());
        }
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

    public String getActualValueKey() {
        FancyListEntry entry = this.entryList.get(getActualValue());
        if (entry == null) return "null-178128";
        return entry.key();
    }

    public void setInitialValue(String key) {
        for (int i = 0; i < this.entryList.size(); i++) {
            FancyListEntry entry = this.entryList.get(i);
            if (entry.matcher().test(key)) {
                this.setValue(i);
                break;
            }
        }
    }

    @Override
    protected void applyValue() {
        onSetValue.accept(this);
    }

    private int getActualValue() {
        return (int) (min + (float)value * (max - min));
    }

    public void setValue(int value) {
        this.value = (double)(value - min) / (max - min);
        this.value = Math.max(0.0, Math.min(1.0, this.value));
        updateMessage();
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
