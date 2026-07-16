package net.snowteb.warriorcats_events.screen.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;

import java.util.ArrayList;
import java.util.List;

public class CatSelectionScrollList extends AbstractSelectionList<CatSelectionScrollList.CatEntry> {

    private final List<Integer> selectedIDs = new ArrayList<>();

    public CatSelectionScrollList(Minecraft mc, int width, int height, int top, int bottom, int itemHeight) {
        super(mc, width, height, top, bottom, itemHeight);
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

    public void addOption(int id, int index) {
        this.addEntry(new CatEntry(id));
    }

    public CatEntry getSelectedEntry() {
        return this.getSelected();
    }

    public List<Integer> getSelectedIDs() {
        return this.selectedIDs;
    }

    public void setAvailableIDs(List<Integer> availableIDs) {

        this.children().clear();

        for (int i = 0; i < availableIDs.size(); i++) {
            this.addEntry(new CatEntry(availableIDs.get(i)));
        }
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        pGuiGraphics.renderOutline(this.getLeft(), this.getTop(), this.width, this.getBottom() - this.getTop(), 0x33FFFFFF);
    }

    public class CatEntry extends Entry<CatEntry> {

        private final int id;

        public CatEntry(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (CatSelectionScrollList.this.selectedIDs.contains(this.id)) {
                CatSelectionScrollList.this.selectedIDs.remove((Integer) this.id);
            } else {
                CatSelectionScrollList.this.selectedIDs.add(this.id);
            }

            return false;
        }

        @Override
        public void render(GuiGraphics pGuiGraphics, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pHovering, float pPartialTick) {
            boolean selected = CatSelectionScrollList.this.getSelected() == this;
            int color = selected ? 0xFFf0ed43 : (pHovering ? 0xFFFFAA00 : 0xFFFFFFFF);

            if (Minecraft.getInstance().level == null) return;
            ClientLevel level = Minecraft.getInstance().level;

            Entity entity = level.getEntity(this.id);
            if  (entity == null) return;

            if (entity instanceof WCatEntity cat) {
                Font font = Minecraft.getInstance().font;

                Component name = cat.hasCustomName() ? cat.getCustomName() : Component.literal("Unnamed");

                String moodText = switch (cat.getMood()) {
                    case HAPPY -> "Feels happy";
                    case CALM -> "Feels calm";
                    case SAD -> "Feels sad";
                    case STRESSED -> "Feels stressed";
                };

                pGuiGraphics.drawString(font, name, pLeft, pTop + 6, 0);

                pGuiGraphics.pose().pushPose();
                pGuiGraphics.pose().translate(pLeft, pTop + 16, 0);
                float scale1 = 0.8f;
                pGuiGraphics.pose().scale(scale1, scale1, scale1);

                pGuiGraphics.drawString(font, String.format("%.0f", cat.getHealth()) + " HP", 0, 0, 0xaaFFFFFF);
                pGuiGraphics.drawString(font, moodText, 0,  8, 0xaaFFFFFF);

                pGuiGraphics.pose().popPose();


                if (CatSelectionScrollList.this.selectedIDs.contains(this.id)) {
                    pGuiGraphics.renderOutline(pLeft - 5, pTop, pWidth + 10,
                            itemHeight-2, 0xFFFFFFFF);
                } else {
                    pGuiGraphics.renderOutline(pLeft - 5, pTop, pWidth + 10,
                            itemHeight-2, 0x18FFFFFF);
                }
            }
        }
    }

}

