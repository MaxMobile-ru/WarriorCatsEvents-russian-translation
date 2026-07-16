package net.snowteb.warriorcats_events.screen.screens.createmorph;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiPredicate;

/**
 * A custom button that allows to have a custom texture and other aesthetic settings.
 */
public class FancySelectableButton extends AbstractButton implements SubRenderable {

    private BiPredicate<Double, Double> isClickableIn;
    private BiPredicate<Double, Double> isHoverableIn;

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

    public void setHoverableIn(BiPredicate<Double, Double> predicate) {
        this.isHoverableIn = predicate;
    }

    public boolean isHoverableIn(double pMouseX, double pMouseY) {
        if (this.isHoverableIn == null) return true;
        return this.isHoverableIn.test(pMouseX, pMouseY);
    }

    @Override
    public @Nullable BiPredicate<Double, Double> isClickableIn() {
        return isClickableIn;
    }

    public interface PressAction {
        void onPress(FancySelectableButton button);
    }

    private final PressAction onPress;
    private float textScale = 0.82f;
    private int color = 0xFFFFFF;
    private boolean selected;
    private final String underText;

    public FancySelectableButton(int width, int height,
                                 Component text,
                                 PressAction onPress,
                                 float textScale, int color,
                                 String underText) {
        this(width, height, 0, 0, text, onPress,  textScale, color, underText);
    }

    public FancySelectableButton(int width, int height,
                                 int x, int y,
                                 Component text,
                                 PressAction onPress,
                                 float textScale, int color,
                                 String underText) {

        super(x, y, width, height, text);
        this.originalYPos0 = y;
        this.originalYPos1 = originalYPos0 + height;

        this.onPress = onPress;
        this.textScale = textScale;
        this.color = color;
        this.underText = underText;

    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean value) {
        this.selected = value;
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
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }

    @Override
    public boolean isMouseOver(double pMouseX, double pMouseY) {
        return super.isMouseOver(pMouseX, pMouseY);
    }

    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int mouseX, int mouseY, float partialTicks) {

        boolean hovered = this.isHovered() && this.isHoverableIn(mouseX, mouseY);

        int alpha = 0x55;
        int colorGradient = this.isSelected() ? 0xFFC9B763 : (alpha << 24) | (0xFFE6D1A5 & 0xFFFFFF);

        {
            RenderSystem.enableBlend();
            pGuiGraphics.setColor(1,1,1,1f);
            pGuiGraphics.blitNineSliced(FancySimpleButton.FANCY_BUTTON_TEXTURE,
                    this.getX(), this.getY(),
                    this.getWidth(), this.getHeight(),
                    20, 4,
                    200, 20,
                    0, 46 + 20 * (hovered ? 2 : 1));
            pGuiGraphics.setColor(1,1,1,1);
            RenderSystem.disableBlend();
        }


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

        pGuiGraphics.renderOutline(this.getX(), this.getY(), this.width,this.height, selected ? 0xFFFFFFA0 : 0x61F2DCAE);


        if (hovered && active) {
            pGuiGraphics.fill(this.getX(), this.getY(),
                    this.getX() + this.width,
                    this.getY() + this.height,
                    0x20FFFFFF);
        }

        lerpScale(hovered);
        float scale = this.textScale + scaleOffset;

        pGuiGraphics.pose().pushPose();
        int yMiddlePos = (this.getY() + (this.height - 5) / 2);
        int yPos = (this.getY() + 4);
        int finalYPos = this.underText.isEmpty() ? yMiddlePos : yPos;
        pGuiGraphics.pose().translate((this.getX() + 5), finalYPos, 0);
        pGuiGraphics.pose().scale(scale, scale, 1.0f);
        pGuiGraphics.drawString(
                Minecraft.getInstance().font,
                this.getMessage(),
                0,
                0,
                this.active ? (hovered ? 0xFFFFA0 : 0xF2DCAE) : 0xFF555555
        );


        pGuiGraphics.pose().popPose();

        if (!this.underText.isEmpty()) {
            Font font = Minecraft.getInstance().font;

            String rawText = this.underText;
            int maxWidthPx = this.getWidth() - 7;
            int startY = this.getY() + font.lineHeight + 2;
            int maxHeight = (this.getY() + this.height) - startY - 4;

            float maxScale = 0.53f;
            float minScale = 0.5f;
            float scaleStep = 0.05f;
            int maxLines = 3;

            List<FormattedCharSequence> list = null;
            float scale2 = maxScale;

            for (int lineCount = 1; lineCount <= maxLines; lineCount++) {
                float testScale = maxScale;
                List<FormattedCharSequence> testList = null;
                boolean fits = false;

                while (testScale >= minScale) {
                    int wrapWidth = (int) (maxWidthPx / testScale);
                    testList = font.split(FormattedText.of(rawText), wrapWidth);

                    int lineHeight = font.lineHeight - 2 + 1;
                    int estimatedHeight = (int) (testList.size() * lineHeight * testScale);

                    if (testList.size() <= lineCount && estimatedHeight <= maxHeight) {
                        fits = true;
                        break;
                    }
                    testScale -= scaleStep;
                }

                if (fits) {
                    scale2 = testScale;
                    list = testList;
                    break;
                }
            }

            if (list == null) {
                int wrapWidth = (int) (maxWidthPx / minScale);
                list = font.split(FormattedText.of(rawText), wrapWidth);
                scale2 = minScale;
            }

            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate(this.getX() + 5, startY, 0);
            pGuiGraphics.pose().scale(scale2 + scaleOffset*0.35f, scale2 + scaleOffset*0.35f, 1.0f);

            int lineHeight = font.lineHeight - 2 + 1;
            int y$ = 0;

            for (FormattedCharSequence cs : list) {
                pGuiGraphics.drawString(
                        font,
                        cs,
                        0,
                        y$,
                        this.active ? (hovered ? 0xFFFFFF : 0xB5B59C) : 0xFF555555
                );
                y$ += lineHeight;
            }

            pGuiGraphics.pose().popPose();
        }

        if (!this.active) {
            pGuiGraphics.fill(this.getX(), this.getY(), this.width + this.getX(),this.height + this.getY(), 0, 0x55000000);
        }


    }

    private float gradientOffset = this.height;

    private void translateGradient(boolean value) {
        float target = value ? this.height - ((float) this.height /4) : this.height;
        gradientOffset = Mth.lerp(0.125F, gradientOffset, target);
    }

    private float scaleOffset = 0;
    private void lerpScale(boolean value) {
        float target = value ? 0.045f : 0;
        scaleOffset = Mth.lerp(0.125F, scaleOffset, target);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narration) {}
}
