package net.snowteb.warriorcats_events.screen.widgets;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.snowteb.warriorcats_events.WarriorCatsEvents;

import java.util.List;

public class ChangelogScrollList extends AbstractSelectionList<ChangelogScrollList.LogEntry> {

    public ChangelogScrollList(Minecraft mc, int width, int height, int top, int bottom, int itemHeight) {
        super(mc, width, height, top, bottom, itemHeight);
    }

    @Override
    protected int getScrollbarPosition() {
        return this.getLeft() + this.getRowWidth() + 6;
    }

    @Override
    public int getRowWidth() {
        return this.width - 10;
    }

    @Override
    public void updateNarration(NarrationElementOutput narration) {}

    public void setLogs(List<String> logs) {
        this.clearEntries();

        int i = 0;
        for (String log : logs) {
            this.addEntry(new LogEntry(log, Minecraft.getInstance().font, this.getRowWidth(), i, i == logs.size() - 1));
            i++;
        }
    }

    @Override
    protected int getRowTop(int pIndex) {
        int y = this.y0 + 4 - (int)this.getScrollAmount() + this.headerHeight;

        for (int i = 0; i < pIndex; i++) {
            y += this.getEntry(i).itemHeight + 4;
        }

        return y;
    }

    @Override
    protected int getRowBottom(int pIndex) {
        return this.getRowTop(pIndex) + this.getEntry(pIndex).itemHeight;
    }

    @Override
    protected int getMaxPosition() {
        int total = this.headerHeight;

        for (int i = 0; i < this.getItemCount(); i++) {
            total += this.getEntry(i).itemHeight + 4;
        }

        return total + 70;
    }



    @Override
    protected void renderItem(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick, int pIndex, int pLeft, int pTop, int pWidth, int pHeight) {
        pHeight = this.getEntry(pIndex).itemHeight;
        super.renderItem(pGuiGraphics, pMouseX, pMouseY, pPartialTick, pIndex, pLeft, pTop, pWidth, pHeight);
    }

    public static class LogEntry extends Entry<LogEntry> {

        private final String log;
        private List<FormattedCharSequence> lines;
        private final int itemHeight;
        private final boolean lastItem;

        private static final ResourceLocation finalLineImage = ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/changelog_cat.png");

        public LogEntry(String log, Font font, int width, int index, boolean lastItem) {
            this.log = log;
            this.lastItem = lastItem;

            boolean formatted = log.startsWith("$");

            if (formatted) {
                itemHeight = 20;
            } else {
                itemHeight = 7*font.split(FormattedText.of(log), (int) (((width*(1/0.7f)) - 14)) ).size() + 7;
            }
        }

        @Override
        public void render(GuiGraphics guiGraphics, int index, int top, int left,
                           int width, int height, int mouseX, int mouseY,
                           boolean hovering, float partialTick) {

            Minecraft mc = Minecraft.getInstance();

            String text = this.log;

            guiGraphics.fill(left, top, left + width, top + height, 0x22000000);

            float scale = 0.7f;
            if (text.startsWith("$(#) ")) {
                scale = 1.2f;
            }
            if (text.startsWith("$(##) ")) {
                scale = 1.1f;
            }
            if (text.startsWith("$(/#) ")) {
                scale = 0.9f;
            }

            int color = 0xFFFFFF;
            if (text.startsWith("$(#) ") || text.startsWith("$(##)")) {
                color = ChatFormatting.GOLD.getColor();
            }
            if (text.startsWith("$(/#) ")) color = ChatFormatting.GRAY.getColor();


            text = text.replace("$(/#) ", "");
            text = text.replace("$(#) ", "");
            text = text.replace("$(##) ", "");

            if (lines == null) {
                lines = mc.font.split(FormattedText.of(text), (int) ((width*(1/scale)) - 14));
            }

            int y = top + 4;


            int y2 = 0;
            if (lastItem) {
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(left + width / 2f, y, 0);
                guiGraphics.pose().scale(scale, scale, scale);
                for (FormattedCharSequence line : lines) {
                    guiGraphics.drawCenteredString(mc.font, line, 0, y2, color);
                    y2 += mc.font.lineHeight;
                }

                int baseW = 400;
                int baseH = 220;
                float imgScale = 0.3f;
                int finalW = (int) (baseW*imgScale);
                int finalH = (int) (baseH*imgScale);

                guiGraphics.pose().translate(0,10,0);
                guiGraphics.blit(finalLineImage, -finalW/2,0,
                        0,0,
                        finalW, finalH,
                        finalW, finalH);

            } else {
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(left + 6, y, 0);
                guiGraphics.pose().scale(scale, scale, scale);
                for (FormattedCharSequence line : lines) {
                    guiGraphics.drawString(mc.font, line, 0, y2, color);
                    y2 += mc.font.lineHeight;
                }

            }

            guiGraphics.pose().popPose();

        }


    }
}

