package net.snowteb.warriorcats_events.screen.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.snowteb.warriorcats_events.managers.ClanSymbol;

public class ImageScrollList extends AbstractSelectionList<ImageScrollList.MemberEntry> {

    public ImageScrollList(Minecraft mc, int width, int height, int top, int bottom) {

        super(mc, width, height, top, bottom, 35);

    }

    @Override
    protected int getScrollbarPosition() {
        return this.getLeft() + this.getRowWidth() + 20;
    }


    @Override
    public int getRowWidth() {
        return this.width - 20;
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {

    }

    public void clearOptions(){
        this.clearEntries();
    }

    public void addOption(int index) {
        this.addEntry(new MemberEntry(index));
    }

    public MemberEntry getSelectedEntry() {
        return this.getSelected();
    }

    public class MemberEntry extends AbstractSelectionList.Entry<MemberEntry> {

        private int varID;

        public MemberEntry(int id) {
            this.varID = id;
        }

        public int getId() {
            return varID;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            ImageScrollList.this.setSelected(this);

            return true;
        }

        @Override
        public void render(GuiGraphics pGuiGraphics, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pHovering, float pPartialTick) {
            boolean selected = ImageScrollList.this.getSelected() == this;
            int color = selected ? 0xFFf0ed43 : (pHovering ? 0xFFFFAA00 : 0xFFFFFFFF);


            int bgColor = 0x22000000;
            pGuiGraphics.fill(pLeft, pTop, pLeft + width, pTop + itemHeight, bgColor);


            pGuiGraphics.blit(
                    ClanSymbol.SPRITE,
                    pWidth/2 + pLeft - 13, pTop + 3,
                    (float) ClanSymbol.getSymbolCoordinate(varID) / 2, 0,
                    ClanSymbol.SYMBOL_SIZE / 2, ClanSymbol.SYMBOL_SIZE/2,
                    (ClanSymbol.SYMBOL_SIZE * ClanSymbol.SYMBOLS_AMOUNT) / 2,
                    ClanSymbol.SYMBOL_SIZE/2
            );

        }
    }

}



