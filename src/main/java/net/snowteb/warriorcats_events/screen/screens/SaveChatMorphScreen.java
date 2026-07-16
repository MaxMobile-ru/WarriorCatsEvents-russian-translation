package net.snowteb.warriorcats_events.screen.screens;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.client.ClientStoredMorphs;
import net.snowteb.warriorcats_events.entity.ModEntities;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCGenetics;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.screen.widgets.GradientButton;
import org.lwjgl.glfw.GLFW;

import java.util.UUID;

public class SaveChatMorphScreen extends Screen {

    private final WCGenetics genetics;
    private final WCGenetics.GeneticalVariants variants;
    private final WCGenetics chimeraGenetics;
    private final WCGenetics.GeneticalChimeraVariants chimeraVariants;
    private final boolean onGeneticalSkin;
    private final int presetVariant;

    private final String morphKey;

    private EditBox morphKeyBox;
    private GradientButton saveButton;
    private GradientButton closeButton;

    private int displayErrorTime = 0;
    private String displayErrorText = "";
    private boolean isAnError = false;

    public SaveChatMorphScreen(String key, WCGenetics genetics, WCGenetics.GeneticalVariants variants,
                               WCGenetics chimeraGenetics, WCGenetics.GeneticalChimeraVariants chimeraVariants,
                               boolean onGeneticalSkin, int presetVariant) {
        super(Component.empty());

        this.genetics = genetics;
        this.variants = variants;
        this.chimeraGenetics = chimeraGenetics;
        this.chimeraVariants = chimeraVariants;
        this.morphKey = key;

        this.onGeneticalSkin = onGeneticalSkin;
        this.presetVariant = presetVariant;
    }

    @Override
    protected void init() {
        int centerX = width / 2;
        int centerY = height / 2;

        morphKeyBox = new EditBox(
                this.font,
                centerX - 50, centerY + 65,
                100, 15,
                Component.literal("Key/Name")
        );
        morphKeyBox.setCanLoseFocus(true);
        morphKeyBox.setHint(Component.translatable("screen.chatmorph.name_hint").withStyle(ChatFormatting.DARK_GRAY));
        morphKeyBox.setValue(morphKey);

        saveButton = new GradientButton(
                centerX - 70, centerY + 90,
                60, 15,
                Component.translatable("screen.chatmorph.save"),
                btn -> {
                    saveMorph();

                }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                80, 20, 1f
        );

        closeButton = new GradientButton(
                centerX + 10, centerY + 90,
                60, 15,
                Component.translatable("screen.chatmorph.close"),
                btn -> {
                    this.onClose();
                }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                80, 20, 1f
        );

        this.addRenderableWidget(saveButton);
        this.addRenderableWidget(closeButton);
        this.addRenderableWidget(morphKeyBox);

    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {

        this.renderBackground(pGuiGraphics);

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        {
            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate(0, -70, 0);
            WCatEntity entityToRender = new WCatEntity(ModEntities.WCAT.get(), Minecraft.getInstance().level);

            entityToRender.setPlayerBoundUuid(UUID.nameUUIDFromBytes(ModEntities.WCAT.get().toString().getBytes()));
            entityToRender.setShowMorphName(false);

            entityToRender.setOnGeneticalSkin(onGeneticalSkin);
            entityToRender.setVariant(presetVariant);
            entityToRender.getGeneticsModule().setGenetics(genetics);
            entityToRender.getGeneticsModule().setGeneticalVariants(variants);
            entityToRender.getGeneticsModule().setChimeraGenetics(chimeraGenetics);
            entityToRender.getGeneticsModule().setGeneticalVariantsChimera(chimeraVariants);

            entityToRender.setGender(1);

            entityToRender.setOnGround(true);

            entityToRender.setYRot(0);
            entityToRender.yHeadRot = 0;
            entityToRender.yBodyRot = 0;


            pGuiGraphics.renderOutline(centerX - 91, centerY - 41, 182, 157, 0x44FFFFFF);
            pGuiGraphics.fill(centerX - 90, centerY - 40, centerX + 90, centerY + 115, 0x57000000);

            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate(centerX, centerY - 35, 0);
            pGuiGraphics.pose().scale(1.4f,1.4f, 0);
            pGuiGraphics.drawCenteredString(this.font, morphKey, 0,0 , 0xFFFFFFFF);
            pGuiGraphics.pose().popPose();

            {
                pGuiGraphics.pose().pushPose();

                pGuiGraphics.pose().translate(centerX, centerY + 90, 0);

                float scale = 3.5f;

                pGuiGraphics.pose().scale(scale, scale, scale);

                InventoryScreen.renderEntityInInventoryFollowsMouse(
                        pGuiGraphics,
                        0,
                        0,
                        30,
                        (float) (centerX - pMouseX),
                        (float) ((centerY + 15) - pMouseY - 50),
                        entityToRender
                );

                pGuiGraphics.pose().popPose();
            }
            pGuiGraphics.pose().popPose();

        }

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        if (displayErrorTime > 0) {
            ChatFormatting color;
            if (isAnError) color = ChatFormatting.RED;
            else color = ChatFormatting.GREEN;
            int xOffset = font.width(displayErrorText) / 2;
            int xO = centerX - xOffset - 12;

            float alpha = (float) displayErrorTime / 20;

            float finalAlpha = Mth.clamp(alpha, 0f, 1f);

            pGuiGraphics.setColor(1f, 1f, 1f, finalAlpha);
            pGuiGraphics.renderTooltip(font, Component.literal(displayErrorText).withStyle(color), xO, centerY + 100);
            pGuiGraphics.setColor(1f, 1f, 1f, 1f);
        }
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == GLFW.GLFW_KEY_E) {
            if (morphKeyBox != null && !morphKeyBox.isHoveredOrFocused()) {
            this.onClose();
            return true;
            }
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void displayError(String error, boolean isAnError) {
        displayErrorTime = 100;
        displayErrorText = error;
        this.isAnError = isAnError;
    }

    private void saveMorph() {

        WCGenetics geneticsCopy = genetics;

        WCGenetics geneticsChimeraCopy = chimeraGenetics;

        WCGenetics.GeneticalVariants genVariants = variants;

        WCGenetics.GeneticalChimeraVariants chimeraVariantsCopy = chimeraVariants;

        ClientStoredMorphs.MorphsFile.MorphData data =
                new ClientStoredMorphs.MorphsFile.MorphData(geneticsCopy, geneticsChimeraCopy, genVariants, chimeraVariantsCopy, onGeneticalSkin, presetVariant);

        if (!morphKeyBox.getValue().equals("")) {
            boolean success = ClientStoredMorphs.add(morphKeyBox.getValue(), data, false);

            if (success) {
                this.onClose();
                Minecraft.getInstance().player.displayClientMessage(
                        Component.translatable("screen.chatmorph.morph_saved", morphKeyBox.getValue()).withStyle(ChatFormatting.GREEN), true
                );
            } else {
                displayError(Component.translatable("screen.chatmorph.morph_already_exists", morphKeyBox.getValue()).getString(), true);
            }
        } else {
            displayError(Component.translatable("screen.chatmorph.no_key_provided").getString(), true);
        }

    }

    @Override
    public void tick() {
        if (displayErrorTime > 0) displayErrorTime--;
        super.tick();
    }
}
