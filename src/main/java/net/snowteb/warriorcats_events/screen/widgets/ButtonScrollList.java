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
        super(mc, width, bottom-top, top, itemHeight);
    }

    public void addButton(Component label, Runnable action, Component tooltip) {
        addEntry(new Entry(label, action, tooltip));
    }

    @Override
    public int getRowWidth() {
        return width - 20;
    }

    @Override
    protected int getScrollbarPosition() {
        return getX() + width - 6;
    }

    @Override
    protected void renderSelection(GuiGraphics guiGraphics, int top, int width, int height, int outerColor, int innerColor) {

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

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    public class Entry extends AbstractSelectionList.Entry<Entry> {
        private final Button button;
        private final Component tooltip;

        public Entry(Component label, Runnable action, Component tooltip) {
            this.button = Button.builder(label, b -> {
                        minecraft.setScreen(null);
                        action.run();
                    })
                    .width(ButtonScrollList.this.getRowWidth())
                    .build();
            this.tooltip = tooltip;
        }

//        @Override
//        public List<? extends GuiEventListener> children() {
//            return List.of(button);
//        }
//
//        @Override
//        public List<? extends NarratableEntry> narratables() {
//            return List.of(button);
//        }


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
            if (button.isHovered() && !tooltip.getString().isEmpty()) {
                List<FormattedCharSequence> text = minecraft.font.split(this.tooltip, 200);
                ButtonScrollList.this.setTooltip(text);
            }
        }
    }
}
