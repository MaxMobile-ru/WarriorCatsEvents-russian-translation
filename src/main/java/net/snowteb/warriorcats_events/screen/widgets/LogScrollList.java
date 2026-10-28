package net.snowteb.warriorcats_events.screen.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.util.FormattedCharSequence;
import net.snowteb.warriorcats_events.clan.ClanObjects;

import java.util.List;

public class LogScrollList extends AbstractSelectionList<LogScrollList.LogEntry> {

    public LogScrollList(Minecraft mc, int width, int height, int top, int bottom, int itemHeight) {
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

    public void setLogs(List<ClanObjects.ClanLogEntry> logs) {
        this.clearEntries();

        for (int i = logs.size() - 1; i >= 0; i--) {
            this.addEntry(new LogEntry(logs.get(i)));
        }
    }


    public static class LogEntry extends AbstractSelectionList.Entry<LogEntry> {

        private final ClanObjects.ClanLogEntry log;
        private List<FormattedCharSequence> lines;

        public LogEntry(ClanObjects.ClanLogEntry log) {
            this.log = log;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return false;
        }

        @Override
        public void render(GuiGraphics guiGraphics, int index, int top, int left,
                           int width, int height,
                           int mouseX, int mouseY,
                           boolean hovering, float partialTick) {

            Minecraft mc = Minecraft.getInstance();

            guiGraphics.fill(left, top, left + width, top + height, 0x22000000);

            if (lines == null) {
                lines = mc.font.split(log.message(), width - 12);
            }

            int y = top + 4;

            for (FormattedCharSequence line : lines) {
                guiGraphics.drawString(mc.font, line, left + 6, y, 0xFFFFFF);
                y += mc.font.lineHeight;
            }
        }


    }
}

