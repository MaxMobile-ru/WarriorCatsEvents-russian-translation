package net.snowteb.warriorcats_events.screen.screens.createmorph;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.function.BiPredicate;

public class FancySwitchButton extends Button implements SubRenderable {

    private boolean value;
    private float scale = 1.0f;

    private final Component leftLabel;
    private final Component rightLabel;

    final int targetX;
    float xSwitchPos = 0;
    final int xBlockSize;

    final int originalYPos0;
    final int originalYPos1;

    private BiPredicate<Double, Double> isClickableIn;

    public FancySwitchButton(int x, int y, int height, String key, boolean initialValue, OnPress onPress, int color, float scale) {
        super(x, y, FancySwitchButton.getKeyBasedWidth(key, scale), height, Component.empty(), onPress, DEFAULT_NARRATION);
        this.originalYPos0 = y;
        this.originalYPos1 = originalYPos0 + height;

        Component left = Component.translatable(key + ".false");
        Component right = Component.translatable(key + ".true");

        this.leftLabel = left;
        this.rightLabel = right;

        this.setValue(initialValue);
        this.xBlockSize = FancySwitchButton.getKeyBasedBlockWidth(key, scale);
        this.targetX = this.getWidth() - xBlockSize;

        this.scale = scale;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public void toggle() {
        this.value = !this.value;
    }

    @Override
    public void onPress() {
        toggle();
        super.onPress();
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (this.isClickableIn() != null) {
            if (!this.isClickableIn().test(pMouseX, pMouseY)) {
                return false;
            }
        }

        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        if (this.isClickableIn() != null) {
            if (!this.isClickableIn().test(pMouseX, pMouseY)) {
                return false;
            }
        }
        return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }

    @Override
    public boolean isMouseOver(double pMouseX, double pMouseY) {
        if (this.isClickableIn() != null) {
            if (!this.isClickableIn().test(pMouseX, pMouseY)) {
                return false;
            }
        }
        return super.isMouseOver(pMouseX, pMouseY);
    }

    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int mouseX, int mouseY, float partialTick) {

        int alpha = 0x55;
        int colorGradient =  0xFFC9B763;

        {
            RenderSystem.enableBlend();
            pGuiGraphics.setColor(1,1,1,1f);
            pGuiGraphics.blitNineSliced(FancySimpleButton.FANCY_BUTTON_TEXTURE,
                    this.getX(), this.getY(),
                    this.getWidth(), this.getHeight(),
                    20, 5,
                    200, 20,
                    0, 46 + 20 * (this.isHovered() ? 2 : 1));
            pGuiGraphics.setColor(1,1,1,1);
            RenderSystem.disableBlend();
        }

        pGuiGraphics.fillGradient(this.getX(), this.getY(),
                this.width + this.getX(),this.height + this.getY(),
                0x44000000, colorGradient);
        pGuiGraphics.fillGradient(this.getX(), this.getY(),
                this.width + this.getX(),this.height + this.getY(),
                0, 0x55000000);

        if (this.value) {
            pGuiGraphics.fillGradient(this.getX(), this.height + this.getY() - 5,
                    this.width + this.getX(),this.height + this.getY(),
                    0x00BDAD5C, 0xFFFFF6E8);
        }

        Component text = this.value ? this.rightLabel : this.leftLabel;

        int textWidth = Minecraft.getInstance().font.width(text);
        int locationX = value ? this.getX() + 5: this.getX() + 5 + xBlockSize;

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(locationX, (this.getY() + (float) (this.height - 5) / 2), 0);
        pGuiGraphics.pose().scale(scale, scale, 1.0f);

        pGuiGraphics.drawString(
                Minecraft.getInstance().font,
                text,
                0,
                0,
                this.active ? (this.isHoveredOrFocused() ? 0xFFFFA0 : 0xF2DCAE) : 0xFF555555
        );

        pGuiGraphics.pose().popPose();


        int color = value ? 0xFFFFFFA0 : 0x61F2DCAE;

        pGuiGraphics.renderOutline(getX(), getY(), width, height, color);

        pGuiGraphics.pose().pushPose();


        pGuiGraphics.pose().popPose();

        pGuiGraphics.pose().pushPose();
        translatePose(pGuiGraphics.pose(), this.value);
        pGuiGraphics.blitNineSliced(WIDGETS_LOCATION,
                this.getX() - 1, this.getY() - 1,
                xBlockSize + 2, this.getHeight() + 2,
                20, 4,
                200, 20,
                0, this.getTextureY());
        pGuiGraphics.pose().popPose();

    }

    private void translatePose(PoseStack pose, boolean value) {
        int target = value ? this.targetX : 0;

        xSwitchPos = Mth.lerp(0.1F, xSwitchPos, target);

        pose.translate(xSwitchPos, 0, 300);
    }

    private int getTextureY() {
        int i = 1;
        if (!this.active) {
            i = 0;
        } else if (this.isHoveredOrFocused()) {
            i = 2;
        }

        return 46 + i * 20;
    }

    public static int getKeyBasedWidth(String key, float scale) {
        Font font = Minecraft.getInstance().font;

        Component left = Component.translatable(key + ".false");
        Component right = Component.translatable(key + ".true");

        int textWidth = Math.round(Math.max(font.width(left), font.width(right)) * scale);

        return Math.max(textWidth + getKeyBasedBlockWidth(key, scale), 20) + 10;
    }

    public static int getKeyBasedBlockWidth(String key, float scale) {
        Font font = Minecraft.getInstance().font;

        Component left = Component.translatable(key + ".false");
        Component right = Component.translatable(key + ".true");

        int textWidth = Math.round(Math.max(font.width(left), font.width(right)) * scale);

        return Math.max(textWidth / 5, 5);
    }

    @Override
    public void adjustYPos(int yOffset) {
        this.setY(this.getOriginalYPos0() + yOffset);
    }

    @Override
    public int getOriginalYPos0() {
        return originalYPos0;
    }

    @Override
    public int getOriginalYPos1() {
        return originalYPos1;
    }

    @Override
    public void setClickableIn(BiPredicate<Double, Double> predicate) {
        isClickableIn = predicate;
    }

    @Override
    public BiPredicate<Double, Double> isClickableIn() {
        return isClickableIn;
    }
}

