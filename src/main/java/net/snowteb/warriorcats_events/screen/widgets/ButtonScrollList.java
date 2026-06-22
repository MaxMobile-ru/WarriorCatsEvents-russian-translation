package net.snowteb.warriorcats_events.screen.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class ButtonScrollList extends AbstractSelectionList<ButtonScrollList.Entry> {

    public ButtonScrollList(Minecraft mc, int width, int height, int top, int bottom, int itemHeight) {
        super(mc, width, height, top, bottom, itemHeight);
    }

    public void addButton(String label, Runnable action, String tooltip) {
        addEntry(new Entry(label, action, tooltip));
    }

    @Override
    public int getRowWidth() {
        return width - 20;
    }

    @Override
    protected int getScrollbarPosition() {
        return getLeft() + width - 6;
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {

    }

    private List<FormattedCharSequence> tooltip;
    public void renderButtonTooltip(GuiGraphics pGuiGraphics, int mouseX, int mouseY) {
        if (tooltip != null) {
            pGuiGraphics.renderTooltip(minecraft.font, tooltip, mouseX, mouseY);
        }
        tooltip = null;
    }

    public void setTooltip(List<FormattedCharSequence> tooltip) {
        this.tooltip = tooltip;
    }

    public class Entry extends AbstractSelectionList.Entry<Entry> {
        private final Button button;
        private final String tooltip;

        public Entry(String label, Runnable action, String tooltip) {
            this.button = Button.builder(Component.literal(label), b -> {
                        minecraft.setScreen(null);
                        action.run();
                    })
                    .width(ButtonScrollList.this.getRowWidth())
                    .build();
            this.tooltip = tooltip;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return this.button.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public void render(GuiGraphics pGuiGraphics, int pIndex, int pTop, int pLeft,
                           int pWidth, int pHeight, int pMouseX, int pMouseY,
                           boolean pHovering, float pPartialTick) {
            button.setX(pLeft - 5);
            button.setY(pTop);
            button.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
            if (button.isHovered() && !tooltip.isEmpty()) {
                List<FormattedCharSequence> text = minecraft.font.split(FormattedText.of(this.tooltip), 200);
                ButtonScrollList.this.setTooltip(text);
            }
        }
    }
}
