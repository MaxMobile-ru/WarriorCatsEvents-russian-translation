package net.snowteb.warriorcats_events.screen.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.sounds.SoundSource;
import net.snowteb.warriorcats_events.WCEClient;
import net.snowteb.warriorcats_events.entity.ModEntities;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCGenetics;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.sound.ModSounds;

import java.util.UUID;

import static net.snowteb.warriorcats_events.screen.screens.ManageClanScreen.pose;
import static net.snowteb.warriorcats_events.screen.screens.ManageClanScreen.rotation;

public class MemberScrollList extends AbstractSelectionList<MemberScrollList.MemberEntry> {

    public MemberScrollList(Minecraft mc, int width, int height, int top, int bottom, int itemHeight) {

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

    public void clearOptions(){
        this.clearEntries();
    }

    public void addOption(String name, UUID id) {
        this.addEntry(new MemberEntry(name, id));
    }

    public void addOption(String name, UUID id, int varID, boolean usingVar,
                          boolean onGeneticalSkin, WCGenetics genetics, WCGenetics chimeraGens,
                          WCGenetics.GeneticalVariants variants, WCGenetics.GeneticalChimeraVariants chimeraVariants) {
        this.addEntry(new MemberEntry(name, id, varID, usingVar, onGeneticalSkin, genetics, chimeraGens, variants, chimeraVariants));
    }

    public MemberEntry getSelectedEntry() {
        return this.getSelected();
    }

    public class MemberEntry extends AbstractSelectionList.Entry<MemberEntry> {

        private final String name;
        private final UUID id;
        private int varID;
        private boolean usingVariant = false;
        private WCatEntity wCatEntity;

        public MemberEntry(String name, UUID id) {
            this.name = name;
            this.id = id;
        }

        public MemberEntry(String name, UUID id, int varID, boolean usingVariant, boolean onGeneticalSkin, WCGenetics genetics, WCGenetics chimeraGens,
                           WCGenetics.GeneticalVariants variants, WCGenetics.GeneticalChimeraVariants chimeraVariants) {
            this.name = name;
            this.id = id;
            this.varID = varID;
            this.usingVariant = usingVariant;

            if (usingVariant) {
                this.wCatEntity = new WCatEntity(ModEntities.WCAT.get(), Minecraft.getInstance().level);
                this.wCatEntity.setAnImage(true);
                this.wCatEntity.setVariant(varID);
                this.wCatEntity.setOnGround(true);
                this.wCatEntity.setYRot(0);
                this.wCatEntity.yHeadRot = 0;
                this.wCatEntity.yBodyRot = 0;
                this.wCatEntity.setXRot(0);

                this.wCatEntity.setOnGeneticalSkin(onGeneticalSkin);
                this.wCatEntity.getGeneticsModule().setGenetics(genetics);
                this.wCatEntity.getGeneticsModule().setChimeraGenetics(chimeraGens);
                this.wCatEntity.getGeneticsModule().setGeneticalVariants(variants);
                this.wCatEntity.getGeneticsModule().setGeneticalVariantsChimera(chimeraVariants);
            }
        }

        public UUID getId() {
            return id;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            MemberScrollList.this.setSelected(this);
            WCEClient.playLocalSound(ModSounds.MENU_CLICK.get(), SoundSource.NEUTRAL, 0.1f,1.3f);


            return true;
        }

        @Override
        public void render(GuiGraphics pGuiGraphics, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pHovering, float pPartialTick) {
            boolean selected = MemberScrollList.this.getSelected() == this;
            int color = selected ? 0xFFf0ed43 : (pHovering ? 0xFFFFAA00 : 0xFFFFFFFF);


            int bgColor = 0x22000000;
            pGuiGraphics.fill(pLeft, pTop, pLeft + width, pTop + height, bgColor);

            pGuiGraphics.drawString(
                    Minecraft.getInstance().font,
                    name,
                    pLeft + 6,
                    pTop + 6,
                    color
            );

            if (usingVariant) {

                int imageX = pLeft + (pWidth - 15);
                int centerY = pTop + 14;

                InventoryScreen.renderEntityInInventory(pGuiGraphics, imageX, centerY, 3, pose, rotation, wCatEntity);

            }
        }
    }

}


