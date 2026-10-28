package net.snowteb.warriorcats_events.screen.screens.clan;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.snowteb.warriorcats_events.WCEClient;
import net.snowteb.warriorcats_events.entity.ModEntities;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCGenetics;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.screen.screens.createmorph.SubRenderable;
import net.snowteb.warriorcats_events.sound.ModSounds;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class FancyMemberScrollList extends AbstractSelectionList<FancyMemberScrollList.MemberEntry>  implements SubRenderable {

    public static final Quaternionf rotation = new Quaternionf(0.0F, 0.0F, 0.0F, 0.0F);
    public static final Quaternionf pose = new Quaternionf(1.9F, 0.0F, 0.6F, 0.0F);

    final int originalYPos0;
    final int originalXPos0;
    final int originalYPos1;
    final int originalXPos1;
    private BiPredicate<Double, Double> isClickableIn;
    private final List<MemberEntry> originalList = new ArrayList<>();

    public FancyMemberScrollList(Minecraft mc, int width, int height, int top, int bottom, int itemHeight) {
        super(mc, width, height, top, bottom, itemHeight);
        originalYPos0 = top;
        originalYPos1 = bottom;
        originalXPos0 = 0;
        originalXPos1 = width;
    }

    public void addMember(UUID memberUUID, Component namePlate, WCGenetics.PackedGeneticData data, int gender, Runnable onPress) {
        MemberEntry add = new MemberEntry(memberUUID, namePlate, data, gender, onPress);
        this.addEntry(add);
        originalList.add(add);
    }

    public void filter(Predicate<MemberEntry> entryPredicate) {
        List<MemberEntry> newList = originalList.stream().filter(entryPredicate).toList();
        clearEntries();
        addAllMembers(newList);
    }

    private void addAllMembers(List<MemberEntry> list) {
        for (MemberEntry entry : list) {
            this.addEntry(entry);
        }
    }

    @Override
    protected void renderSelection(GuiGraphics pGuiGraphics, int pTop, int pWidth, int pHeight, int pOuterColor, int pInnerColor) {
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        pGuiGraphics.fill(this.x0, this.y0,
                this.x0 + width, this.y1,
                0xFF292929);

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

    @Override
    protected int getScrollbarPosition() {
        return this.getRight() - 6;
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
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
    }

    @Override
    public int getRowWidth() {
        return this.width;
    }

    public MemberEntry getSelectedEntry() {
        return this.getSelected();
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

    public class MemberEntry extends Entry<MemberEntry> {

        private final Component name;
        private final UUID id;
        private final WCatEntity cat;
        private final Runnable onPress;

        public MemberEntry(UUID uuid, Component namePlate, WCGenetics.PackedGeneticData data, int gender, Runnable onPress) {
            this.name = namePlate;
            this.id = uuid;
            this.onPress = onPress;

            {
                this.cat = new WCatEntity(ModEntities.WCAT.get(), Minecraft.getInstance().level);
                this.cat.setAnImage(true);
                this.cat.setGender(gender);
                this.cat.setVariant(data.morphSkin);
                this.cat.setOnGround(true);
                this.cat.setYRot(0);
                this.cat.yHeadRot = 0;
                this.cat.yBodyRot = 0;
                this.cat.setXRot(0);
                this.cat.setNoAi(true);

                this.cat.setOnGeneticalSkin(data.onGeneticalSkin);
                this.cat.getGeneticsModule().setGenetics(data.genetics);
                this.cat.getGeneticsModule().setChimeraGenetics(data.chimerasGenetics);
                this.cat.getGeneticsModule().setGeneticalVariants(data.variants);
                this.cat.getGeneticsModule().setGeneticalVariantsChimera(data.chimeraVariants);
            }
        }

        public UUID getId() {
            return id;
        }

        public String getName() {
            return this.name.getString();
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            FancyMemberScrollList.this.setSelected(this);
            WCEClient.playLocalSound(ModSounds.MENU_CLICK.get(), SoundSource.NEUTRAL, 0.1f,1.3f);

            this.onPress.run();

            return true;
        }

        @Override
        public void render(GuiGraphics pGuiGraphics, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pHovering, float pPartialTick) {
            boolean selected = FancyMemberScrollList.this.getSelected() == this;


            int bgColor = selected ? 0x33FFFFFF : (pHovering ? 0x22FFFFFF : 0x11FFFFFF);

            int left = pLeft + 2;
            int top = pTop;
            int right = pLeft + pWidth - 12;
            int bottom = pTop + pHeight;

            pGuiGraphics.fill(left, top, right, bottom, bgColor);

            pGuiGraphics.renderOutline(left, top, right - left, bottom - top, bgColor);


            int color = selected ? 0xFFFFDE57 : (pHovering ? 0xFFFFF8DE : 0xFFF2DCAE);

            {
                pGuiGraphics.pose().pushPose();
                pGuiGraphics.pose().translate(left + 5, top + (float) (bottom - top) / 3,0);
                float textScale = 0.7f;
                pGuiGraphics.pose().scale(textScale, textScale, textScale);
                pGuiGraphics.drawString(
                        Minecraft.getInstance().font,
                        name,
                        0,
                        0,
                        color
                );

                pGuiGraphics.pose().popPose();
            }


            {
                int imageX = left + ((right-left) - 12);
                int centerY = pTop + 14;

                cat.setYRot(0.0F);
                cat.setXRot(0.0F);
                cat.setYHeadRot(0.0F);
                cat.yRotO = 0.0F;
                cat.xRotO = 0.0F;
                cat.yHeadRotO = 0.0F;
                cat.yBodyRot = 0.0F;
                cat.yBodyRotO = 0.0F;
                pGuiGraphics.pose().pushPose();
                pGuiGraphics.pose().translate(imageX, centerY, 0);
                float scale = 0.8f;
                pGuiGraphics.pose().scale(scale, scale, scale);
                InventoryScreen.renderEntityInInventory(pGuiGraphics, 0, 0, 4, pose, rotation, cat);
                pGuiGraphics.pose().popPose();

            }

        }
    }

}


