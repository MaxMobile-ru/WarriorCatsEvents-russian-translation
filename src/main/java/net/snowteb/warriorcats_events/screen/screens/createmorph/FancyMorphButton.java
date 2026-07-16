package net.snowteb.warriorcats_events.screen.screens.createmorph;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.snowteb.warriorcats_events.client.ClientStoredMorphs;
import net.snowteb.warriorcats_events.entity.ModEntities;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCGenetics;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.function.BiPredicate;

import static net.snowteb.warriorcats_events.screen.screens.ManageClanScreen.pose;
import static net.snowteb.warriorcats_events.screen.screens.ManageClanScreen.rotation;

public class FancyMorphButton extends AbstractButton implements SubRenderable {

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

    @Override
    public @Nullable BiPredicate<Double, Double> isClickableIn() {
        return isClickableIn;
    }

    public void setHoverableIn(BiPredicate<Double, Double> predicate) {
        this.isHoverableIn = predicate;
    }

    public boolean isHoverableIn(double pMouseX, double pMouseY) {
        if (this.isHoverableIn == null) return true;
        return this.isHoverableIn.test(pMouseX, pMouseY);
    }

    private float textScale = 0.82f;
    private int color = 0xFFFFFF;
    private boolean selected;

    private final WCatEntity morph;

    private final Runnable onClick;

    public FancyMorphButton(int width, int height,
                            int x, int y,
                            String name,
                            float textScale, int color,
                            ClientStoredMorphs.MorphsFile.MorphData data,
                            Runnable onClick) {

        super(x, y, width, height, Component.literal(name));
        this.originalYPos0 = y;
        this.originalYPos1 = originalYPos0 + height;
        this.onClick = onClick;

        this.morph = new WCatEntity(ModEntities.WCAT.get(), Minecraft.getInstance().level);
        this.morph.setAnImage(true);
        this.morph.setOnGround(true);
        this.morph.setGender(1);
        this.morph.setAge(0);
        this.morph.setAgeInMoons(12);
        this.morph.setYRot(0);
        this.morph.yHeadRot = 0;
        this.morph.yBodyRot = 0;
        this.morph.setXRot(0);

        this.morph.setOnGeneticalSkin(data.onGeneticalSkin());
        this.morph.setVariant(data.presetVariant());
        this.morph.getGeneticsModule().setGenetics(new WCGenetics(data.genetics()));
        this.morph.getGeneticsModule().setChimeraGenetics(new WCGenetics(data.chimeraGenetics()));
        this.morph.getGeneticsModule().setGeneticalVariants(new WCGenetics.GeneticalVariants(data.variants()));
        this.morph.getGeneticsModule().setGeneticalVariantsChimera(new WCGenetics.GeneticalChimeraVariants(data.chimeraVariants()));

        this.textScale = textScale;
        this.color = color;
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
    }

    @Override
    public void onClick(double pMouseX, double pMouseY) {
        super.onClick(pMouseX, pMouseY);
        this.onClick.run();
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

        pGuiGraphics.fillGradient(this.getX(), this.getY(),
                this.width + this.getX(), this.height + this.getY(),
                0x44000000, colorGradient);


        pGuiGraphics.fillGradient(this.getX(), this.getY(),
                this.width + this.getX(), this.height + this.getY(),
                0, 0x55000000);

        {
            translateGradient(this.isSelected());
            int gradientY = this.getY() + Math.round(gradientOffset);

            pGuiGraphics.fillGradient(this.getX(), gradientY,
                    this.width + this.getX(), this.height + this.getY(),
                    0x00BDAD5C, 0xFFFFF6E8);
        }

        pGuiGraphics.renderOutline(this.getX(), this.getY(), this.width, this.height, selected ? 0xFFFFFFA0 : 0x61F2DCAE);


        if (hovered && active) {
            pGuiGraphics.fill(this.getX(), this.getY(),
                    this.getX() + this.width,
                    this.getY() + this.height,
                    0x20FFFFFF);
        }

        int textStartX = this.getX() + 3;
        int imageX = this.getX() + (this.getWidth() - 10);

        int textMaxWidth = (imageX - 11) - textStartX;

        lerpScale(hovered);

        float scale = this.textScale;

        Font font = Minecraft.getInstance().font;
        int textWidth = font.width(this.getMessage());

        float finalScale = scale;
        if (textWidth > 0) {
            float scaledTextWidth = textWidth * scale;
            if (scaledTextWidth > textMaxWidth) {
                finalScale = (textMaxWidth / (float) textWidth);
            }
        }
        finalScale = finalScale + scaleOffset*finalScale;

        pGuiGraphics.pose().pushPose();
        int yPos = (this.getY() + 4);
        pGuiGraphics.pose().translate(textStartX, yPos, 300);
        pGuiGraphics.pose().scale(finalScale, finalScale, 1.0f);
        pGuiGraphics.drawString(
                Minecraft.getInstance().font,
                this.getMessage(),
                0,
                0,
                this.active ? (hovered ? 0xFFFFA0 : 0xF2DCAE) : 0xFF555555
        );

        pGuiGraphics.pose().popPose();

        int centerY = this.getY() + this.getHeight()/2 + 5;
        pGuiGraphics.pose().pushPose();

        pGuiGraphics.pose().translate(imageX, centerY, 200);

        float scale2 = scale + scaleOffset - 0.02f;

        pGuiGraphics.pose().scale(scale2, scale2, scale2);

        InventoryScreen.renderEntityInInventory(pGuiGraphics,
                0, 0, 4, new Vector3f(0,0,0),
                pose, rotation, morph);

        pGuiGraphics.pose().popPose();


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

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narration) {
    }
}