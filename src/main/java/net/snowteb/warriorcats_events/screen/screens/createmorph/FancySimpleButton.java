package net.snowteb.warriorcats_events.screen.screens.createmorph;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiPredicate;

/**
 * A custom button that allows to have a custom texture and other aesthetic settings.
 */
public class FancySimpleButton extends AbstractButton implements SubRenderable {

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
        void onPress(FancySimpleButton button);
    }

    private final PressAction onPress;
    private float textScale = 0.82f;
    private int color = 0xFFFFFFFF;
    private int clickedTime = 0;
    private final String underText;
    private final boolean centered;

    public static final WidgetSprites FANCY_SPRITES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID,"fancy_button"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID,"fancy_button_highlighted")
    );

    public FancySimpleButton(int width, int height,
                             int x, int y,
                             Component text,
                             PressAction onPress,
                             float textScale,
                             String underText,
                             boolean centered) {

        super(x, y, width, height, text);
        this.originalYPos0 = y;
        this.originalYPos1 = originalYPos0 + height;

        this.onPress = onPress;
        this.textScale = textScale;
        this.color = 0x61F2DCAE;
        this.underText = underText;
        this.centered = centered;
    }

    public FancySimpleButton(int width, int height,
                             int x, int y,
                             Component text,
                             PressAction onPress,
                             float textScale) {

        this(width, height, x, y, text, onPress, textScale, "", false);
    }

    public FancySimpleButton(int width, int height,
                             int x, int y,
                             Component text,
                             PressAction onPress,
                             float textScale,
                             boolean centered) {

        this(width, height, x, y, text, onPress, textScale, "", centered);
    }


    public boolean wasClicked() {
        return clickedTime > 0;
    }

    @Override
    public void tick() {
        if (clickedTime > 0) {
            clickedTime--;
        }
    }

    @Override
    public void onPress() {
        this.clickedTime = 7;
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

        boolean hovered = this.isHovered();

        {
            RenderSystem.enableBlend();
            pGuiGraphics.setColor(1,1,1,1f);

            pGuiGraphics.blitSprite(FANCY_SPRITES.get(this.active, hovered),
                    this.getX(), this.getY(), this.getWidth(), this.getHeight());

            pGuiGraphics.setColor(1,1,1,1);
            RenderSystem.disableBlend();
        }


        int alpha = 0x55;
        int colorGradient = this.wasClicked() ? 0xFFC9B763 : (alpha << 24) | (0xFFE6D1A5 & 0xFFFFFF);

        pGuiGraphics.fillGradient(this.getX(), this.getY(),
                this.width + this.getX(), this.height + this.getY(),
                0x44000000, colorGradient);


        pGuiGraphics.fillGradient(this.getX(), this.getY(),
                this.width + this.getX(), this.height + this.getY(),
                0, 0x55000000);


        translateGradient(this.wasClicked());
        int gradientY = this.getY() + Math.round(gradientOffset);

        pGuiGraphics.fillGradient(this.getX(), gradientY,
                this.width + this.getX(), this.height + this.getY(),
                0x00BDAD5C, 0xFFFFF6E8);

        lerpColor(wasClicked());
        pGuiGraphics.renderOutline(this.getX(), this.getY(), this.width, this.height, color);


        if (hovered && active) {
            pGuiGraphics.fill(this.getX(), this.getY(),
                    this.getX() + this.width,
                    this.getY() + this.height,
                    0x20FFFFFF);
        }

        lerpScale(hovered && active);
        float scale = this.textScale;
        Font font = Minecraft.getInstance().font;
        int textWidth = font.width(this.getMessage());

        int maxTextWidth = this.getWidth() - 10;

        float finalScale = scale;
        if (textWidth > 0 && maxTextWidth > 0) {
            float scaledTextWidth = textWidth * scale;
            if (scaledTextWidth > maxTextWidth) {
                finalScale = maxTextWidth / (float) textWidth;
            }
        }

        finalScale = finalScale + scaleOffset*finalScale;

        pGuiGraphics.pose().pushPose();
        int yMiddlePos = (this.getY() + ((this.height - 5)/2));
        int yPos = (this.getY() + 3);
        int finalYPos = this.underText.isEmpty() ? yMiddlePos : yPos;
        int msgXPos = this.centered ? (this.getX() + getWidth()/2) : (this.getX() + 5);
        pGuiGraphics.pose().translate(msgXPos, finalYPos, 0);
        pGuiGraphics.pose().scale(finalScale, finalScale, 1.0f);

        if (centered) {
            pGuiGraphics.drawCenteredString(
                    font,
                    this.getMessage(),
                    0,
                    0,
                    this.active ? (hovered ? 0xFFFFA0 : 0xF2DCAE) : 0xFF555555
            );
        } else {
            pGuiGraphics.drawString(
                    font,
                    this.getMessage(),
                    0,
                    0,
                    this.active ? (hovered ? 0xFFFFA0 : 0xF2DCAE) : 0xFF555555
            );
        }

        pGuiGraphics.pose().popPose();

        if (!this.underText.isEmpty()) {

            String rawText = this.underText;
            int maxWidthPx = this.getWidth() - 7;
            int startY = this.getY() + font.lineHeight + 1;
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
            pGuiGraphics.pose().scale(scale2 + scaleOffset * 0.35f, scale2 + scaleOffset * 0.35f, 1.0f);

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
            pGuiGraphics.fill(this.getX(), this.getY(), this.width + this.getX(), this.height + this.getY(), 0, 0x55000000);
        }


    }

    private float gradientOffset = this.height;

    private void translateGradient(boolean value) {
        float target = value ? this.height - ((float) this.height / 4) : this.height;
        gradientOffset = Mth.lerp(0.125F, gradientOffset, target);
    }

    private float scaleOffset = 0;

    private void lerpScale(boolean value) {
        float target = value ? 0.045f : 0;
        scaleOffset = Mth.lerp(0.125F, scaleOffset, target);
    }

    private void lerpColor(boolean value) {
        float lerp = 0.1f;
        int from = color;
        int target = value ? 0xFFFFFFA0 : 0x61F2DCAE;

        int a1 = (from >> 24) & 0xFF;
        int r1 = (from >> 16) & 0xFF;
        int g1 = (from >> 8) & 0xFF;
        int b1 = from & 0xFF;

        int a2 = (target >> 24) & 0xFF;
        int r2 = (target >> 16) & 0xFF;
        int g2 = (target >> 8) & 0xFF;
        int b2 = target & 0xFF;

        int a = (int) Mth.lerp(lerp, a1, a2);
        int r = (int) Mth.lerp(lerp, r1, r2);
        int g = (int) Mth.lerp(lerp, g1, g2);
        int b = (int) Mth.lerp(lerp, b1, b2);

        color = (a << 24) | (r << 16) | (g << 8) | b;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narration) {
    }
}
