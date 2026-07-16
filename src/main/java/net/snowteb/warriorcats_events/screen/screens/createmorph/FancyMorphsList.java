package net.snowteb.warriorcats_events.screen.screens.createmorph;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.snowteb.warriorcats_events.client.ClientStoredMorphs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiPredicate;

public class FancyMorphsList extends AbstractSelectionList<FancyMorphsList.Entry> implements SubRenderable {

    final int originalYPos0;
    final int originalYPos1;
    private BiPredicate<Double, Double> isClickableIn;

    public FancyMorphsList(Minecraft mc, int width, int height, int top, int bottom, int itemHeight) {
        super(mc, width, height, top, bottom, itemHeight);
        originalYPos0 = top;
        originalYPos1 = bottom;
    }

    public void addMorph(@NotNull String name, ClientStoredMorphs.MorphsFile.MorphData morphData, Runnable onClick) {
        this.addEntry(new Entry(name, morphData, onClick));
    }

    @Override
    public int getRowWidth() {
        return width - 20;
    }

    @Nullable
    public Entry getEntryByKey(String key) {
        return this.children().stream().filter(e -> e.key.equals(key)).findFirst().orElse(null);
    }

    @Override
    protected int getScrollbarPosition() {
        return getLeft() + width - 6;
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
    }

    @Override
    protected void renderDecorations(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        super.renderDecorations(pGuiGraphics, pMouseX, pMouseY);
    }

    @Override
    protected void renderBackground(GuiGraphics pGuiGraphics) {
        super.renderBackground(pGuiGraphics);
    }

    @Override
    protected void renderHeader(GuiGraphics pGuiGraphics, int pX, int pY) {
        super.renderHeader(pGuiGraphics, pX, pY);
    }

    @Override
    protected void renderSelection(GuiGraphics pGuiGraphics, int pTop, int pWidth, int pHeight, int pOuterColor, int pInnerColor) {
    }

    @Override
    protected void renderItem(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick, int pIndex, int pLeft, int pTop, int pWidth, int pHeight) {
        super.renderItem(pGuiGraphics, pMouseX, pMouseY, pPartialTick, pIndex, pLeft, pTop, pWidth, pHeight);
    }

    @Override
    protected void renderList(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (this.children().isEmpty()) {
            int i = this.getRowLeft();
            float scale = 0.8f;
            Component msg = Component.translatable("screen.button.stored_morphs_suggestion");
            List<FormattedCharSequence> lines =
                    this.minecraft.font.split(msg, (int) (this.getRowWidth() * (1/scale)));

            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate(i, this.y0 + 10, 0);
            pGuiGraphics.pose().scale(scale, scale, scale);

            int y = 0;
            for (FormattedCharSequence line : lines) {
                pGuiGraphics.drawString(this.minecraft.font,
                        line, 0, y, 0x66E6D1A5);
                y += 9;
            }

            pGuiGraphics.pose().popPose();
        }
        super.renderList(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {

        {
            RenderSystem.enableBlend();
            pGuiGraphics.setColor(1,1,1,0.7f);
            pGuiGraphics.blitNineSliced(FancySimpleButton.FANCY_BUTTON_TEXTURE,
                    this.getLeft(), this.getTop(),
                    this.getWidth() - 5, this.getOriginalYPos1() - this.getOriginalYPos0() + 1,
                    20, 5,
                    200, 20,
                    0, 46 + 20 * (1));
            pGuiGraphics.setColor(1,1,1,1);
            RenderSystem.disableBlend();
        }

        pGuiGraphics.fill(this.x0, this.y0,
                this.x0 + width, this.y1,
                0x55000000);

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        pGuiGraphics.setColor(0.25F, 0.25F, 0.25F, 1.0F);
        pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

        int color = 0x990D0C08;

        pGuiGraphics.fillGradient(RenderType.guiOverlay(), this.x0, this.y0, this.x1, this.y0 + 6, color, 0, 0);
        pGuiGraphics.fillGradient(RenderType.guiOverlay(), this.x0, this.y1 - 6, this.x1, this.y1, 0, color, 0);

        pGuiGraphics.renderOutline(this.x0, this.y0 - 1,
                this.width, this.y1 - this.y0 + 2,
                0xFF59533B);
    }

    private FancyMorphButton selected = null;

    @Override
    public void setSelected(@Nullable FancyMorphsList.Entry pSelected) {
        super.setSelected(pSelected);
        if (pSelected != null) {
            selectButton(pSelected.button);
        } else {
            selectButton(null);
        }
    }

    public void selectButton(FancyMorphButton button) {
        for (Entry entry : this.children()) {
            entry.button.setSelected(false);
        }

        if (button != null) button.setSelected(true);
        this.selected = button;
    }

    public String getSelectedKey() {
        return this.getSelected() != null ? this.getSelected().key : "null";
    }

    public boolean isKeySelected(String key) {
        return this.getSelected() != null && this.getSelected().key.equals(key);
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
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (this.isClickableIn() != null) {
            if (!this.isClickableIn().test(pMouseX, pMouseY)) {
                return false;
            }
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public void adjustYPos(int yOffset) {
        this.y0 = getOriginalYPos0() + yOffset;
        this.y1 = getOriginalYPos1() + yOffset;
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

    int menuX = 0;

    public void setMenuX(int i) {
        this.menuX = i;
    }

    @Override
    protected void enableScissor(GuiGraphics pGuiGraphics) {
        pGuiGraphics.enableScissor(this.x0 + menuX, this.y0, this.x1 + menuX, this.y1);
    }

    @Override
    public void tick() {
        for (Entry entry : this.children()) {
            entry.button.tick();
        }
    }

    public class Entry extends AbstractSelectionList.Entry<Entry> {
        final FancyMorphButton button;
        public final String key;

        public Entry(String key, ClientStoredMorphs.MorphsFile.MorphData morphData, Runnable onClick) {

            this.button = new FancyMorphButton(
                    FancyMorphsList.this.getRowWidth(), FancyMorphsList.this.itemHeight - 5,
                    0,0,
                    key,
                    0.75f,
                    0xFFE6D1A5,
                    morphData,
                    onClick
            );

            this.button.setHoverableIn((pMouseX, pMouseY) -> {
                return pMouseX >  FancyMorphsList.this.getLeft() && pMouseX < FancyMorphsList.this.getLeft() + FancyMorphsList.this.getWidth()
                        && pMouseY > FancyMorphsList.this.getTop() - 1 && pMouseY < FancyMorphsList.this.getBottom();
            });

            this.key = key;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (FancyMorphsList.this.isClickableIn() != null) {
                if (!FancyMorphsList.this.isClickableIn().test(mouseX, mouseY)) {
                    return false;
                }
            }
            FancyMorphsList.this.setSelected(this);

            return this.button.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean isMouseOver(double pMouseX, double pMouseY) {
            return super.isMouseOver(pMouseX, pMouseY);
        }

        @Override
        public void render(GuiGraphics pGuiGraphics, int pIndex, int pTop, int pLeft,
                           int pWidth, int pHeight, int pMouseX, int pMouseY,
                           boolean pHovering, float pPartialTick) {

            button.setX(pLeft - 5);
            button.setY(pTop);
            button.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        }
    }


}
