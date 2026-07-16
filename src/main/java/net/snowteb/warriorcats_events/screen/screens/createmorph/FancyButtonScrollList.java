package net.snowteb.warriorcats_events.screen.screens.createmorph;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

public class FancyButtonScrollList extends AbstractSelectionList<FancyButtonScrollList.Entry> implements SubRenderable {

    final int originalYPos0;
    final int originalYPos1;
    private BiPredicate<Double, Double> isClickableIn;


    public FancyButtonScrollList(Minecraft mc, int width, int height, int top, int bottom, int itemHeight) {
        super(mc, width, bottom-top, top, itemHeight);
        originalYPos0 = top;
        originalYPos1 = bottom;
    }

    @Override
    protected void renderListBackground(GuiGraphics guiGraphics) {
    }

    public void addButton(@NotNull Component label, Runnable action,
                          @NotNull Component underText, Component instruction,
                          String key) {
        this.addButton(label, action, underText, instruction, key, 0.7f);
    }

    public void addButton(@NotNull Component label, Runnable action,
                          @NotNull Component underText, Component instruction,
                          String key, float scale) {
        addEntry(new Entry(label.getString(), action, underText.getString(), instruction.getString(), key, scale));
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
        return getX() + width - 6;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    @Override
    protected void renderDecorations(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
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

    int menuX = 0;

    public void setMenuX(int i) {
        this.menuX = i;
    }

    @Override
    protected void enableScissor(GuiGraphics pGuiGraphics) {
        pGuiGraphics.enableScissor(this.getX() + menuX, this.getY(), this.getRight() + menuX, this.getY() + height);
    }

    @Override
    public void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        pGuiGraphics.fill(this.getX(), this.getY(),
                this.getX() + width, this.getY() + height,
                0x55000000);

        super.renderWidget(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        pGuiGraphics.setColor(0.25F, 0.25F, 0.25F, 1.0F);
        pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

        int color = 0x990D0C08;

        pGuiGraphics.fillGradient(RenderType.guiOverlay(), this.getX(), this.getY(), this.getRight(), this.getY() + 6, color, 0, 0);
        pGuiGraphics.fillGradient(RenderType.guiOverlay(), this.getX(), this.getBottom() - 6, this.getRight(), this.getBottom(), 0, color, 0);

        pGuiGraphics.renderOutline(this.getX(), this.getY() - 1,
                this.width, this.getBottom() - this.getY() + 2,
                0xFF59533B);
    }

    private List<FormattedCharSequence> tooltip = new ArrayList<>();
    private FancySelectableButton selected = null;

    public void renderButtonTooltip(GuiGraphics pGuiGraphics, int mouseX, int mouseY) {
        if (tooltip != null) {
            pGuiGraphics.renderTooltip(minecraft.font, tooltip, mouseX, mouseY);
        }
        tooltip = null;
    }

    public void setCurrentInstruction(List<FormattedCharSequence> tooltip) {
        this.tooltip = tooltip;
    }

    @Nullable
    public List<FormattedCharSequence> getCurrentInstruction() {
        return tooltip;
    }

    @Override
    public void setSelected(@Nullable FancyButtonScrollList.Entry pSelected) {
        super.setSelected(pSelected);
        if (pSelected != null) {
            selectButton(pSelected.button);
        } else {
            selectButton(null);
        }
    }

    public void selectButton(FancySelectableButton button) {
        for (Entry entry : this.children()) {
            entry.button.setSelected(false);
        }

        if (button != null) button.setSelected(true);
        this.selected = button;
    }

    public String getSelectedKey() {
        return this.getSelected() != null ? this.getSelected().key : "null-fad87862f";
    }

    public boolean isKeySelected(String key) {
        return this.getSelected() != null && this.getSelected().key.equals(key);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double scrollX, double scrollY) {
        if (this.isClickableIn() != null) {
            if (!this.isClickableIn().test(pMouseX, pMouseY)) {
                return false;
            }
        }
        return super.mouseScrolled(pMouseX, pMouseY, scrollX, scrollY);
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
        this.setY(getOriginalYPos0() + yOffset);
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

    public class Entry extends AbstractSelectionList.Entry<Entry> {
        final FancySelectableButton button;
        final String instruction;
        public final String key;

        public Entry(String label, Runnable action, String tooltip, String instruction, String key, float scale) {
            FancySelectableButton button =
                    new FancySelectableButton(
                            FancyButtonScrollList.this.getRowWidth(),
                            FancyButtonScrollList.this.itemHeight - 5,
                            Component.literal(label),
                            b -> action.run(),
                            scale,
                            0xFFE6D1A5,
                            tooltip
                    );


            button.setHoverableIn((pMouseX, pMouseY) -> {
                return pMouseX >  FancyButtonScrollList.this.getX() && pMouseX < FancyButtonScrollList.this.getX() + FancyButtonScrollList.this.getWidth()
                        && pMouseY > FancyButtonScrollList.this.getY() - 1 && pMouseY < FancyButtonScrollList.this.getBottom();
            });

            this.button = button;
            this.instruction = instruction;
            this.key = key;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (FancyButtonScrollList.this.isClickableIn() != null) {
                if (!FancyButtonScrollList.this.isClickableIn().test(mouseX, mouseY)) {
                    return false;
                }
            }
            FancyButtonScrollList.this.setSelected(this);

            return this.button.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public void render(GuiGraphics pGuiGraphics, int pIndex, int pTop, int pLeft,
                           int pWidth, int pHeight, int pMouseX, int pMouseY,
                           boolean pHovering, float pPartialTick) {

            button.setX(pLeft - 5);
            button.setY(pTop);
            button.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
            if (button.isSelected() && !instruction.isEmpty()) {
                List<FormattedCharSequence> text = minecraft.font.split(FormattedText.of(this.instruction), 200);
                FancyButtonScrollList.this.setCurrentInstruction(text);
            }

        }
    }
}
