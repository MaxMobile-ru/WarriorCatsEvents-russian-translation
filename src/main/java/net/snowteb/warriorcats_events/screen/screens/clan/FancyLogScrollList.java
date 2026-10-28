package net.snowteb.warriorcats_events.screen.screens.clan;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.snowteb.warriorcats_events.clan.ClanObjects;
import net.snowteb.warriorcats_events.client.ClientClans;
import net.snowteb.warriorcats_events.screen.screens.createmorph.SubRenderable;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class FancyLogScrollList extends AbstractSelectionList<FancyLogScrollList.LogEntry> implements SubRenderable {

    @Nullable
    private List<ClanObjects.ClanLogEntry> logs;

    private float scale = 1F;

    final int originalYPos0;
    final int originalXPos0;
    final int originalYPos1;
    final int originalXPos1;
    private BiPredicate<Double, Double> isClickableIn;

    private int color = 0xFF000000;

    public FancyLogScrollList(Minecraft mc, int width, int height, int top, int bottom) {
        super(mc, width, height, top, bottom, 20);
        this.originalYPos0 = top;
        this.originalYPos1 = top + height;
        this.originalXPos0 = 0;
        this.originalXPos1 = width;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        pGuiGraphics.renderOutline(
                this.getLeft(),  this.getTop() - 1,
                this.getWidth(), this.getHeight() + 2,
                0xFFFFFFFF
        );

    }

    @Override
    protected int getScrollbarPosition() {
        return this.getRight() -6;
    }

    @Override
    public int getRowWidth() {
        return this.width;
    }

    @Override
    protected int getRowTop(int pIndex) {
        int y = this.y0 + 4 - (int)this.getScrollAmount() + this.headerHeight;

        for (int i = 0; i < pIndex; i++) {
            y += this.getEntry(i).size + 4;
        }

        return y;
    }

    @Override
    protected int getRowBottom(int pIndex) {
        return this.getRowTop(pIndex) + this.getEntry(pIndex).size;
    }

    @Override
    protected int getMaxPosition() {
        int total = this.headerHeight;

        for (int i = 0; i < this.getItemCount(); i++) {
            total += this.getEntry(i).size + 4;
        }

        return total + 10;
    }

    @Override
    public void updateNarration(NarrationElementOutput narration) {}

    public void setLogs(ClientClans.Entry entry, float scale) {
        this.clearEntries();

        List<ClanObjects.ClanLogEntry> logs = entry.getClan().logs;
        this.logs = logs;
        this.scale = scale;
        this.color = entry.getClan().color | 0xFF000000;

        for (int i = logs.size() - 1; i >= 0; i--) {
            this.addEntry(new LogEntry(logs.get(i), scale));
        }
    }

    public void filter(Predicate<ClanObjects.ClanLogEntry> filter) {
        this.clearEntries();
        this.setScrollAmount(0);

        if (this.logs != null) {
            for (int i = logs.size() - 1; i >= 0; i--) {
                ClanObjects.ClanLogEntry log = logs.get(i);
                if (filter.test(log)) {
                    this.addEntry(new LogEntry(log, this.scale));
                }
            }
        }

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
    public void adjustXPos(int xOffset) {
        this.setLeftPos(originalXPos0 + xOffset);
    }

    @Override
    public int getOriginalXPos0() {
        return originalXPos0;
    }

    @Override
    public int getOriginalXPos1() {
        return originalXPos1;
    }

    @Override
    public BiPredicate<Double, Double> isClickableIn() {
        return isClickableIn;
    }


    public class LogEntry extends Entry<LogEntry> {

        private final List<FormattedCharSequence> lines;
        private final float scale;
        private final int size;

        public LogEntry(ClanObjects.ClanLogEntry log, float scale) {
            Minecraft mc = Minecraft.getInstance();

            int listRowLeft = FancyLogScrollList.this.getRowLeft();
            int listRowWidth = FancyLogScrollList.this.getRowWidth();
            int x1 = listRowLeft + 8;
            int x2 = listRowLeft + listRowWidth - 14;

            int textWidth = x2 - x1;

            this.lines = mc.font.split(log.message(), (int) (textWidth / scale));
            this.scale = scale;

            this.size = (int) Math.max(10 + (9*lines.size())*scale, 20);
        }

        public int getSize() {
            return this.size;
        }

        @Override
        public void render(GuiGraphics guiGraphics, int index, int top, int left,
                           int width, int height,
                           int mouseX, int mouseY,
                           boolean hovering, float partialTick) {

            Minecraft mc = Minecraft.getInstance();

            int clanColor = FancyLogScrollList.this.color;

            int x1 = left + 2;
            int y1 = top;
            int x2 = left + width - 12;
            int y2 = top + size;
            int logWidth = x2 - x1;
            int logHeight = y2 - y1;

            guiGraphics.fill(x1, y1, x2, y2, clanColor);

            guiGraphics.renderOutline(x1, y1, x2 - x1, y2 - y1, clanColor);
            guiGraphics.renderOutline(x1, y1, x2 - x1, y2 - y1, 0x99000000);

            guiGraphics.fillGradient(x1 + 1, y1 + 1, x2 - 1, y1 + logHeight, clanColor, 0x01000000);
            guiGraphics.fillGradient(x1 + 1, y2 - logHeight, x2 - 1, y2 - 1, 0x01000000, clanColor);

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(left + 6, top + 4, 0);
            guiGraphics.pose().scale(scale, scale, scale);
            int y = 0;
            for (FormattedCharSequence line : lines) {
                guiGraphics.drawString(mc.font, line,0,y, 0xFFFFFF);
                y += mc.font.lineHeight;
            }

            guiGraphics.pose().popPose();

        }


    }
}

