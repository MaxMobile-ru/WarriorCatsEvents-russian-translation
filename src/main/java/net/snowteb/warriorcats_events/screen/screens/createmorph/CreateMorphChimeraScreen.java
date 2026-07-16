package net.snowteb.warriorcats_events.screen.screens.createmorph;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.snowteb.warriorcats_events.WCEClient;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.client.ClientClanData;
import net.snowteb.warriorcats_events.client.ClientStoredMorphs;
import net.snowteb.warriorcats_events.entity.ModEntities;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCGenetics;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.c2s.cats.SummonCustomCatPacket;
import net.snowteb.warriorcats_events.network.packet.c2s.clan.SavePlayerGeneticsPacket;
import net.snowteb.warriorcats_events.network.packet.c2s.others.CtSShareMorphToChat;
import net.snowteb.warriorcats_events.screen.screens.SpawnLocationScreen;
import net.snowteb.warriorcats_events.zconfig.WCEServerConfig;
import tocraft.walkers.api.PlayerShape;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static net.snowteb.warriorcats_events.screen.screens.CreateClanScreen.BG_TEXTURE;

public class CreateMorphChimeraScreen extends BaseMorphScreen {

    private float animationTime = 0f;
    private float duration = 20f;
    private boolean closing = false;
    private final float startX = 0;
    private final float endX = 700;

    private int minY = 0;
    private int maxY = 0;


    private FancyButtonScrollList optionsList;

    private FancySimpleButton saveAndNext;

    private final boolean isSummoning;

    public CreateMorphChimeraScreen(WCGenetics.PackedGeneticData data, boolean isSummoning) {
        super(Component.literal("Create Morph Genetics"));
        menuX = 500;

        this.genetics = new WCGenetics(data.genetics);
        this.variants = new WCGenetics.GeneticalVariants(data.variants);
        this.chimeraGenetics = new WCGenetics(data.chimerasGenetics);
        this.chimeraVariants = new WCGenetics.GeneticalChimeraVariants(data.chimeraVariants);
        this.onGeneticalSkin = data.onGeneticalSkin;

        this.isSummoning = isSummoning;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        minY = centerY - 90;
        maxY = centerY + 45;

        drawFirstOptionsGenetical();


        int middleMinX = optionsList.getWidth() + optionsList.getLeft() + 7;
        int middleMinY = optionsList.getTop();
        int middleMaxX = this.width - (this.width / 4) - 25;
        int middleMaxY = maxY + 45;

        FancySelectableButton freeView = new FancySelectableButton(
                21, 12,
                middleMinX + 3,middleMaxY - 15,
                Component.literal("\uD83D\uDC41↺"), button -> {
            catFreeView = button.isSelected();
        }, 1f, 0xFFE6D1A5, "");
        freeView.setSelected(catFreeView);

        this.addRenderableWidget(freeView);


        setupBaseValues();
        drawSubRenderables(true);


        int saveWidth = (otherListsMaxX - otherListsMinX)/2;
        int saveHeight = 18;
        int saveX = otherListsMaxX - saveWidth;

        Component saveText = Component.translatable("screen.button.done");

        saveAndNext = new FancySimpleButton(saveWidth,saveHeight,
                saveX, this.optionsList.getTop() - 5 - 16,
                Component.empty()
                        .append("⬅ ")
                        .append(saveText),
                b -> {
                    closing = true;
                    animationTime = 0f;
                    WCEClient.nextMenuSound();
        }, 0.9f);

        this.addRenderableWidget(saveAndNext);

        FancyStringWidget chimerismText = new FancyStringWidget(
                Component.translatable("screen.cat.chimerism"),
                optionsList.getLeft(), optionsList.getRight(), middleMinY - 20,
                1.3f,true, optionsList.getHeight() + 35
        );

        this.addRenderableWidget(chimerismText);
    }


    private void drawFirstOptionsGenetical() {
        this.removeWidget(optionsList);

        optionsList = new FancyButtonScrollList(minecraft, this.width / 4 - 10,
                height - 100, minY,
                maxY + 44, 33);

        optionsList.setLeftPos(6);

        optionsList.setRenderBackground(false);
        optionsList.setRenderTopAndBottom(false);

        optionsList.addButton(Component.translatable("screen.cat.base_section"), () -> {
                    drawSubRenderables(true);
                }, Component.translatable("screen.cat.base_section_und"),
                Component.translatable("screen.cat.base_section_ins"), "base");

        optionsList.addButton(Component.translatable("screen.cat.orange_section"), () -> {
                    drawSubRenderables(true);
                }, Component.translatable("screen.cat.orange_section_und"),
                Component.translatable("screen.cat.orange_section_ins"), "orange");

        optionsList.addButton(Component.translatable("screen.cat.white_section"), () -> {
                    drawSubRenderables(true);
                }, Component.translatable("screen.cat.white_section_und"),
                Component.translatable("screen.cat.white_section_ins"), "white");

        optionsList.addButton(Component.translatable("screen.cat.albinism_section"), () -> {
                    drawSubRenderables(true);
                }, Component.translatable("screen.cat.albinism_section_und"),
                Component.translatable("screen.cat.albinism_section_ins"), "albinism");

        optionsList.addButton(Component.translatable("screen.cat.dilute_section"), () -> {
                    drawSubRenderables(true);
                }, Component.translatable("screen.cat.dilute_section_und"),
                Component.translatable("screen.cat.dilute_section_ins"), "dilute");

        optionsList.addButton(Component.translatable("screen.cat.agouti_section"), () -> {
                    drawSubRenderables(true);
                }, Component.translatable("screen.cat.agouti_section_und"),
                Component.translatable("screen.cat.agouti_section_ins"), "agouti");

        optionsList.addButton(Component.translatable("screen.cat.silver_section"), () -> {
                    drawSubRenderables(true);
                }, Component.translatable("screen.cat.silver_section_und"),
                Component.translatable("screen.cat.silver_section_ins"), "silver");

        optionsList.addButton(Component.translatable("screen.cat.extra_section"), () -> {
                    drawSubRenderables(true);
                }, Component.translatable("screen.cat.extra_section_und"),
                Component.translatable("screen.cat.extra_section_ins"), "extra");

        optionsList.setSelected(optionsList.getEntryByKey("base"));

        this.addRenderableWidget(optionsList);
    }

    private void setupBaseValues() {

        int otherListsWidth = this.width / 4 + 12;

        currentOtherListYPos = 0;

        otherListsMinX = this.width - otherListsWidth - 6;
        otherListsMaxX = this.width - 12;
        otherListsMinY = this.optionsList.getTop();
        otherListsMaxY = maxY + 45;
    }
    
    private void drawSubRenderables(boolean onWidgetsReload) {
        this.clearSubRenderables();
        if (onWidgetsReload) this.onOtherWidgetsReload();

        int padding = getSubRenderableSpacing();

        int xPos0 = otherListsMinX + padding;
        int yPos0 = otherListsMinY + padding;

        int xPos1 = otherListsMaxX - padding;
        int yPos1 = otherListsMaxY - padding;

        int width = xPos1 - xPos0;
        int height = yPos1 - yPos0;

        int yPos = yPos0 + 10;

        if (this.optionsList.getSelectedKey().equals("base")) {
            AddSubRenderables.Chimerism.addBaseWidgets(xPos0, xPos1, yPos, width, height, padding, this);
        } else if (this.optionsList.getSelectedKey().equals("orange")) {
            AddSubRenderables.Chimerism.addOrangeWidgets(xPos0, xPos1, yPos, width, height, padding, this);
        } else if (this.optionsList.getSelectedKey().equals("white")) {
            AddSubRenderables.Chimerism.addWhiteWidgets(xPos0, xPos1, yPos, width, height, padding, this);
        } else if (this.optionsList.getSelectedKey().equals("albinism")) {
            AddSubRenderables.Chimerism.addAlbinoWidgets(xPos0, xPos1, yPos, width, height, padding, this);
        } else if (this.optionsList.getSelectedKey().equals("dilute")) {
            AddSubRenderables.Chimerism.addDiluteWidgets(xPos0, xPos1, yPos, width, height, padding, this);
        } else if (this.optionsList.getSelectedKey().equals("agouti")) {
            AddSubRenderables.Chimerism.addAgoutiWidgets(xPos0, xPos1, yPos, width, height, padding, this);
        } else if (this.optionsList.getSelectedKey().equals("silver")) {
            AddSubRenderables.Chimerism.addSilverWidgets(xPos0, xPos1, yPos, width, height, padding, this);
        } else if (this.optionsList.getSelectedKey().equals("extra")) {
            AddSubRenderables.Chimerism.addExtrasWidgets(xPos0, xPos1, yPos, width, height, padding, this);
        }

        this.recalculateListHeight();
    }

    private void onOtherWidgetsReload() {
        currentOtherListYPos = 0;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {

        pGuiGraphics.blit(BG_TEXTURE, 0, 0, 0, 0, this.width, this.height, this.width, this.height);

        handleScreenOffset(pPartialTick);

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(menuX, 0, 0);

        this.optionsList.setMenuX((int) menuX);

        drawMenuBackground(pGuiGraphics);

        drawOtherListsBackground(pGuiGraphics);

        renderCharacter(pGuiGraphics, pMouseX, pMouseY);

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        renderSectionText(pGuiGraphics);

        renderDecorations(pGuiGraphics);

        pGuiGraphics.pose().popPose();
    }


    private void renderDecorations(GuiGraphics pGuiGraphics) {
        int pieceWidth = 60;
        int pieceHeight = 30;

        int miniPieceWidth = 40;
        int miniPieceHeight = 28;

        int textureWidth = 60;
        int textureHeight = 180;

        int miniTextureWidth = 40;
        int miniTextureHeight = 168;

        float scale = 0.8f;

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(0, 0, 300);
        RenderSystem.enableBlend();

        pGuiGraphics.setColor(0.9f,0.8f,0.7f,1f);

        int x = optionsList.getLeft() - 4;
        int y = optionsList.getTop() - 11;

        {
            pGuiGraphics.blit(DECORATIONS,
                    x, y,
                    (int) (pieceWidth * scale), (int) (pieceHeight * scale),
                    0, (pieceHeight * 5) * scale,
                    (int) -(pieceWidth * scale), (int) (pieceHeight * scale),
                    (int) (textureWidth * scale), (int) (textureHeight * scale));
        }
        {
            x = optionsList.getRight() - pieceWidth + 15;
            y = optionsList.getBottom() - 14;

            pGuiGraphics.blit(DECORATIONS,
                    x, y,
                    (int) (pieceWidth * scale), (int) (pieceHeight * scale),
                    0, (pieceHeight * 3) * scale,
                    (int) -(pieceWidth * scale), (int) -(pieceHeight * scale),
                    (int) (textureWidth * scale), (int) (textureHeight * scale));

            x = optionsList.getRight() - miniPieceWidth + 12;
            y = optionsList.getTop() - 13;

            pGuiGraphics.blit(MINI_DECORATIONS,
                    x, y,
                    (int) (miniPieceWidth * scale), (int) (miniPieceHeight * scale),
                    0, (miniPieceHeight * 5) * scale,
                    (int) (miniPieceWidth * scale), (int) (miniPieceHeight * scale),
                    (int) (miniPieceWidth * scale), (int) (miniTextureHeight * scale));
        }
        {
            int minX = optionsList.getWidth() + optionsList.getLeft();
            int maxX = this.width - (this.width / 4) - 5 - 10;

            int xPos0 = minX + 7;
            int xPos1 = maxX - 10;

            int yPos0 = optionsList.getTop();
            int yPos1 = maxY + 45;

            x = xPos0 - 5;
            y = yPos1 - 16;

            pGuiGraphics.blit(DECORATIONS,
                    x, y,
                    (int) (pieceWidth * scale), (int) (pieceHeight * scale),
                    0, (pieceHeight * 5) * scale,
                    (int) -(pieceWidth * scale), (int) -(pieceHeight * scale),
                    (int) (textureWidth * scale), (int) (textureHeight * scale));

            x = xPos1 - pieceWidth + 15;
            y = yPos0 - 8;

            pGuiGraphics.blit(DECORATIONS,
                    x, y,
                    (int) (pieceWidth * scale), (int) (pieceHeight * scale),
                    0, (pieceHeight * 1) * scale,
                    (int) -(pieceWidth * scale), (int) (pieceHeight * scale),
                    (int) (textureWidth * scale), (int) (textureHeight * scale));


        }

        {

            x = otherListsMinX - 3;
            y = otherListsMinY - 10;

            pGuiGraphics.blit(DECORATIONS,
                    x, y,
                    (int) (pieceWidth * scale), (int) (pieceHeight * scale),
                    0, (pieceHeight * 2) * scale,
                    (int) (pieceWidth * scale), (int) (pieceHeight * scale),
                    (int) (textureWidth * scale), (int) (textureHeight * scale));

            x = otherListsMaxX - pieceWidth + 18;
            y = otherListsMaxY - 11;

            pGuiGraphics.blit(DECORATIONS,
                    x, y,
                    (int) (pieceWidth * scale), (int) (pieceHeight * scale),
                    0, (pieceHeight * 1) * scale,
                    (int) -(pieceWidth * scale), (int) -(pieceHeight * scale),
                    (int) (textureWidth * scale), (int) (textureHeight * scale));


            x = otherListsMaxX - miniPieceWidth + 16;
            y = otherListsMinY - 12;

            pGuiGraphics.blit(MINI_DECORATIONS,
                    x, y,
                    (int) (miniPieceWidth * scale), (int) (miniPieceHeight * scale),
                    0, (miniPieceHeight * 2) * scale,
                    (int) -(miniPieceWidth * scale), (int) (miniPieceHeight * scale),
                    (int) (miniTextureWidth * scale), (int) (miniTextureHeight * scale));

            x = otherListsMinX - 8;
            y = otherListsMaxY - 13;

            pGuiGraphics.blit(MINI_DECORATIONS,
                    x, y,
                    (int) (miniPieceWidth * scale), (int) (miniPieceHeight * scale),
                    0, (miniPieceHeight * 1) * scale,
                    (int) (miniPieceWidth * scale), (int) (miniPieceHeight * scale),
                    (int) (miniTextureWidth * scale), (int) (miniTextureHeight * scale));


        }
        {
            x = saveAndNext.getX() - 3;
            y = saveAndNext.getY() - 10;

            scale = 0.7f;

            pGuiGraphics.blit(MINI_DECORATIONS,
                    x, y,
                    (int) (miniPieceWidth * scale), (int) (miniPieceHeight * scale),
                    0, (miniPieceHeight * 4) * scale,
                    (int) -(miniPieceWidth * scale), (int) (miniPieceHeight * scale),
                    (int) (miniTextureWidth * scale), (int) (miniTextureHeight * scale));

        }


        pGuiGraphics.setColor(1,1,1,1);

        RenderSystem.disableBlend();
        pGuiGraphics.pose().popPose();


    }




    private void renderSectionText(GuiGraphics pGuiGraphics) {
        if (this.optionsList.getSelected() != null) {
            FancyButtonScrollList.Entry entry = this.optionsList.getSelected();

            int minX = optionsList.getWidth() + optionsList.getLeft();
            int maxX = this.width - (this.width / 4) - 5 - 40;

            int xPos0 = minX + 7;
            int xPos1 = maxX - 10;

            int yPos0 = optionsList.getTop();
            int yPos1 = maxY;

            int xOffset = 22;

            int color = 0xFFF2DCAE;

            int iconSize = 20;

            String path = "textures/gui/createmorph/icon_" + entry.key + ".png";
            float a = ((color >> 24) & 0xFF) / 255f;
            float r = ((color >> 16) & 0xFF) / 255f;
            float g = ((color >> 8) & 0xFF) / 255f;
            float b = (color & 0xFF) / 255f;

            pGuiGraphics.setColor(r, g, b, a);
            pGuiGraphics.blit(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, path),
                    xPos0, yPos0 - xOffset,
                    0, 0,
                    iconSize, iconSize,
                    iconSize, iconSize);
            pGuiGraphics.setColor(1, 1, 1, 1);


            float scale1 = 1.1f;
            float scale2 = 0.5f;


            int maxWidth = xPos1 - xPos0 - xOffset;
            List<FormattedCharSequence> list =
                    this.font.split(FormattedText.of(entry.instruction),
                            (int) ((maxWidth / scale1) / scale2));

            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate(xPos0 + xOffset, yPos0 - 12 - (5 * list.size()), 0);
            pGuiGraphics.pose().scale(scale1, scale1, scale1);
            pGuiGraphics.drawString(this.font, entry.button.getMessage(), 0, 0, color);

            pGuiGraphics.pose().translate(0, 10, 0);
            pGuiGraphics.pose().scale(scale2, scale2, scale2);

            {

                int y$ = 0;
                for (FormattedCharSequence cs : list) {
                    pGuiGraphics.drawString(this.font, cs, 0, y$, 0xD4D4B4);
                    y$ += 8;
                }
            }

            pGuiGraphics.pose().popPose();
        }
    }

    private void drawOtherListsBackground(GuiGraphics pGuiGraphics) {
        {
            int x0 = otherListsMinX;
            int x1 = otherListsMaxX;

            int y0 = otherListsMinY;
            int y1 = otherListsMaxY;

            pGuiGraphics.fill(x0, y0, x1 + 5, y1, 0x77000000);
            pGuiGraphics.renderOutline(x0, y0, x1 - x0 + 5, y1 - y0, 0x44EBD798);
        }

        {
            int thisWidth2 = this.width / 4 - 10;
            int x02 = 6;
            int x12 = x02 + thisWidth2;

            int y02 = this.optionsList.getTop() - 1;
            int y12 = maxY + 44 + 1;

            pGuiGraphics.fill(x02, y02, x12, y12, 0x55000000);
            pGuiGraphics.renderOutline(x02, y02, x12 - x02, y12 - y02, 0x44EBD798);
        }
    }

    private float characterBoxZoom = 0;
    private float targetZoom = 0;

    private void renderCharacter(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        characterBoxZoom = Mth.lerp(0.05f, characterBoxZoom, targetZoom);

        WCatEntity entityToRender = new WCatEntity(ModEntities.WCAT.get(), Minecraft.getInstance().level);

        entityToRender.setPlayerBoundUuid(UUID.nameUUIDFromBytes(ModEntities.WCAT.get().toString().getBytes()));
        entityToRender.setShowMorphName(false);

        entityToRender.setOnGeneticalSkin(onGeneticalSkin);
        entityToRender.setGender(1);
        entityToRender.setAge(0);
        entityToRender.setAgeInMoons(12);

        entityToRender.getGeneticsModule().setGenetics(genetics);
        entityToRender.getGeneticsModule().setGeneticalVariants(variants);
        entityToRender.getGeneticsModule().setChimeraGenetics(chimeraGenetics);
        entityToRender.getGeneticsModule().setGeneticalVariantsChimera(chimeraVariants);

        entityToRender.setOnGround(true);

        if (optionsList.getSelected() != null) {
            try {
                presetVariant = Integer.parseInt(optionsList.getSelectedKey());
            } catch (NumberFormatException ignored) {
            }
        }

        entityToRender.setVariant(presetVariant);

        entityToRender.setYRot(0);
        entityToRender.yHeadRot = 0;
        entityToRender.yBodyRot = 0;

        int minX = optionsList.getWidth() + optionsList.getLeft();
        int maxX = this.width - (this.width / 4) - 5 - 10;

        int xPos0 = minX + 7;
        int xPos1 = maxX - 10;

        int yPos0 = optionsList.getTop();
        int yPos1 = maxY + 45;


        int centerBoxX = xPos0 + (xPos1 - xPos0) / 2;
        int centerBoxY = yPos0 + (yPos1 - yPos0) / 2;

        pGuiGraphics.renderOutline(xPos0, yPos0, xPos1 - xPos0, yPos1 - yPos0, 0x44EBD798);
        pGuiGraphics.fill(xPos0, yPos0, xPos1, yPos1, 0x45000000);

        if (!pauseCharacter) {
            staticMouseX = Mth.lerpInt(0.2f, staticMouseX, (int) (pMouseX + 3 - menuX));
            staticMouseY = Mth.lerpInt(0.15f, staticMouseY, pMouseY);
        } else {
            pGuiGraphics.pose().pushPose();
            float scale = 0.6f;

            pGuiGraphics.pose().translate(xPos0 + 2, yPos0 + 2, 800);
            pGuiGraphics.pose().scale(scale, scale, scale);
            Component pauseText = Component.empty()
                    .append(" ⏸ ")
                    .append(Component.translatable("screen.cat.morph_paused"));

            List<FormattedCharSequence> lines = font.split(pauseText, (int) ((xPos1 - xPos0) /scale));

            int pauseY = 0;
            for (FormattedCharSequence line : lines) {
                pGuiGraphics.drawString(this.font, line,
                        0, pauseY, 0x88FFFFFF);
                pauseY += 9;
            }
            pGuiGraphics.pose().popPose();
        }

        pMouseX = staticMouseX;
        pMouseY = staticMouseY;


        {
            pGuiGraphics.pose().pushPose();

            pGuiGraphics.pose().translate(centerBoxX, centerBoxY + 40, 0);

            float scale = 3.5f + characterBoxZoom;

            pGuiGraphics.pose().scale(scale, scale, scale);

            pGuiGraphics.enableScissor((int) (xPos0 + 1 + menuX), yPos0 + 1, (int) (xPos1 - 1 + menuX), yPos1 - 1);
            renderCat(
                    pGuiGraphics,
                    0,
                    5,
                    30,
                    (float) (centerBoxX - pMouseX),
                    (float) ((centerBoxY - 3) - pMouseY),
                    entityToRender
            );
            pGuiGraphics.disableScissor();

            pGuiGraphics.pose().popPose();
        }
    }

    private void handleScreenOffset(float pPartialTick) {
        if (closing) {
            animationTime += pPartialTick;

            float progress = Math.min(animationTime / duration, 1f);

            float eased = progress * progress * progress;

            menuX = startX + (endX - startX) * eased;

            if (progress >= 1f) {
                back();
            }
        } else {
            if (menuX > 0) {
                menuX -= (menuX) * 0.03f;
                if (menuX < 0) menuX = 0;
            }
        }
    }

    private void back() {
        WCGenetics geneticsCopy = new WCGenetics(this.genetics);
        WCGenetics geneticsChimeraCopy = new WCGenetics(this.chimeraGenetics);
        WCGenetics.GeneticalVariants variantsCopy = new WCGenetics.GeneticalVariants(this.variants);
        WCGenetics.GeneticalChimeraVariants variantsChimeraCopy = new WCGenetics.GeneticalChimeraVariants(this.chimeraVariants);

        WCGenetics.PackedGeneticData data = new WCGenetics.PackedGeneticData(geneticsCopy, variantsCopy, geneticsChimeraCopy, variantsChimeraCopy, true, 0);
        Minecraft.getInstance().setScreen(new CreateMorphScreen(data, this.isSummoning));

    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        boolean superMs = super.mouseScrolled(pMouseX, pMouseY, pDelta);

        if (!superMs) {
            int minX = optionsList.getWidth() + optionsList.getLeft() + 1;
            int maxX = this.width - (this.width / 4) - 6 - 10;

            int xPos0 = minX + 8;
            int xPos1 = maxX - 11;

            int yPos0 = optionsList.getTop();
            int yPos1 = maxY + 45;

            if (pMouseX > xPos0 && pMouseX < xPos1 &&
                    pMouseY > yPos0 && pMouseY < yPos1) {
                targetZoom = Mth.clamp(characterBoxZoom + (float)pDelta*2f, -1, 7.5f);
            }
        }
        return superMs;
    }

    private boolean pauseCharacter = false;
    private int staticMouseX = 0;
    private int staticMouseY = 0;
    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (!super.mouseClicked(pMouseX, pMouseY, pButton)) {
            int minX = optionsList.getWidth() + optionsList.getLeft() + 1;
            int maxX = this.width - (this.width / 4) - 6 - 10;

            int xPos0 = minX + 8;
            int xPos1 = maxX - 11;

            int yPos0 = optionsList.getTop();
            int yPos1 = maxY + 45;

            if (pMouseX > xPos0 && pMouseX < xPos1 &&
                    pMouseY > yPos0 && pMouseY < yPos1) {
                pauseCharacter = !pauseCharacter;
            }
            return false;
        }
        return true;
    }

}
