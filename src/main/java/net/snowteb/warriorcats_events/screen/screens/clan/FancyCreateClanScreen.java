package net.snowteb.warriorcats_events.screen.screens.clan;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.managers.ClanSymbol;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.c2s.clan.C2SRegisterClanPacket;
import net.snowteb.warriorcats_events.screen.screens.AbstractWCEScreen;
import net.snowteb.warriorcats_events.screen.screens.createmorph.FancyFloatSlider;
import net.snowteb.warriorcats_events.screen.widgets.ImageScrollList;

import java.util.ArrayList;
import java.util.List;

public class FancyCreateClanScreen extends AbstractWCEScreen {
    private final String morphName;

    private EditBox clanNameBox;
    private EditBox clanSentenceBox;

    FancyFloatSlider rSlider;
    FancyFloatSlider gSlider;
    FancyFloatSlider bSlider;

    private ImageScrollList symbolList;

    public static final ResourceLocation BG_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/clan_setup/background_scene.png");

    public FancyCreateClanScreen(String morphName) {
        super();
        this.morphName = morphName;
    }

    @Override
    protected void init() {

        int centerX = width /2;
        int centerY = height /2;

        clanNameBox = new EditBox(
                this.font,
                centerX - 40, centerY - 35,
                80, 15,
                Component.translatable("screen.createclan.clan_name")
        );
        clanNameBox.setMaxLength(20);
        clanNameBox.setHint(Component.literal("'Bengalclan'").withStyle(ChatFormatting.DARK_GRAY).withStyle(ChatFormatting.ITALIC));
        this.addRenderableWidget(clanNameBox);

        clanSentenceBox = new EditBox(
                this.font,
                centerX - 60, centerY - 12,
                120, 15,
                Component.translatable("screen.createclan.clan_sentence")
        );
        clanSentenceBox.setHint(Component.literal("'Bengalclan is...'").withStyle(ChatFormatting.DARK_GRAY).withStyle(ChatFormatting.ITALIC));
        clanSentenceBox.setMaxLength(130);
        this.addRenderableWidget(clanSentenceBox);


        int sliderWidth = 100;
        int sliderHeight = 15;
        int sliderYPos = centerY + 20;
        int sliderSpacing = sliderHeight + 5;

        rSlider = new FancyFloatSlider(
                centerX - sliderWidth / 2, sliderYPos,
                sliderWidth, sliderHeight,
                0.0f, 0.75f,
                0.75f, slider -> {}
        );
        sliderYPos += sliderSpacing;
        gSlider = new FancyFloatSlider(
                centerX - sliderWidth / 2, sliderYPos,
                sliderWidth, sliderHeight,
                0.0f, 0.75f,
                0.75f, slider -> {}
        );
        sliderYPos += sliderSpacing;
        bSlider = new FancyFloatSlider(
                centerX - sliderWidth / 2, sliderYPos,
                sliderWidth, sliderHeight,
                0.0f, 0.75f,
                0.75f, slider -> {}
        );
        this.addRenderableWidget(rSlider);
        this.addRenderableWidget(gSlider);
        this.addRenderableWidget(bSlider);

        FancyLambdaRenderable rgbRender = new FancyLambdaRenderable(
                centerX, centerY + 20, 0,
                (pGuiGraphics, mouseX, mouseY, pPartialTick) -> {
                    int lettersX = -(sliderWidth/2) - 10;
                    pGuiGraphics.drawString(this.font, "R:", lettersX, 5,0xFFFF0000);
                    pGuiGraphics.drawString(this.font, "G:", lettersX, 25,0xFF00FF00);
                    pGuiGraphics.drawString(this.font, "B:", lettersX, 45,0xFF0000FF);
                }
        );
        this.addRenderableWidget(rgbRender);

        symbolList = new ImageScrollList(
                Minecraft.getInstance(),
                80,
                100,
                centerY - 35,
                centerY + 80
        );
        symbolList.setRenderBackground(false);
        symbolList.setRenderTopAndBottom(false);
        symbolList.setLeftPos(centerX + 15 + sliderWidth / 2);

        for (int i = 0; i < ClanSymbol.SYMBOLS_AMOUNT; i++) {
            symbolList.addOption(i);
        }
        this.addRenderableWidget(symbolList);


        Button saveButton = Button.builder(
                Component.translatable("screen.createclan.register"),
                btn -> saveAndSend()
        ).bounds(centerX - 40, centerY + 85, 80, 20).build();

        this.addRenderableWidget(saveButton);


    }


    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        int centerX = width / 2;
        int centerY = height / 2;

        pGuiGraphics.blit(BG_TEXTURE, 0, 0, 0, 0, this.width, this.height, this.width, this.height);

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        int symbolToShow = 0;
        if (symbolList.getSelected() != null) {
            symbolToShow = symbolList.getSelected().getId();
        }

        float[] rgb = FancySpecificClanScreen.ensureMinLuminance(
                (rSlider.getActualValue()),
                (gSlider.getActualValue()),
                (bSlider.getActualValue()),
                0.30f);

        float r = rgb[0];
        float g = rgb[1];
        float b = rgb[2];

        int color = FastColor.ARGB32.color(
                255,
                (int) (rgb[0]*255),
                (int) (rgb[1]*255),
                (int) (rgb[2]*255)
        );

        pGuiGraphics.setColor(r, g, b, 1f);

        pGuiGraphics.blit(
                ClanSymbol.SPRITE,
                centerX - 25, centerY - 110,
                ClanSymbol.getSymbolCoordinate(symbolToShow), 0,
                ClanSymbol.SYMBOL_SIZE, ClanSymbol.SYMBOL_SIZE,
                ClanSymbol.SYMBOL_SIZE * ClanSymbol.SYMBOLS_AMOUNT,
                ClanSymbol.SYMBOL_SIZE
        );

        pGuiGraphics.setColor(1f, 1f, 1f, 1f);


        Component text = Component.translatable("screen.createclan.and_founded",
                        Component.literal(morphName).withStyle(ChatFormatting.WHITE),
                        Component.literal(!clanNameBox.getValue().trim().isEmpty() ? clanNameBox.getValue().trim() : "...").withStyle(Style.EMPTY.withColor(color)));

        pGuiGraphics.drawCenteredString(this.font, text, centerX, centerY - 50, 0xFFFFFF);

        boolean showSentenceToolTip = clanSentenceBox != null && clanSentenceBox.isHovered();

        boolean showClanNameTooltip = clanNameBox != null && clanNameBox.isHovered();

        if (showSentenceToolTip) {
            pGuiGraphics.renderTooltip(this.font,
                    Component.translatable("screen.createclan.bio_tip").withStyle(ChatFormatting.GRAY),
                    pMouseX, pMouseY);
        }

        if (showClanNameTooltip) {
            List<Component> textWithNewlines = new ArrayList<>();
            textWithNewlines.add(Component.translatable("screen.createclan.clan_name_tip").withStyle(ChatFormatting.GRAY));
            pGuiGraphics.renderComponentTooltip(this.font, textWithNewlines, pMouseX, pMouseY);
        }

    }

    private void saveAndSend(){
        if (clanNameBox.getValue().isEmpty()) {
            this.displayMessage(Component.translatable("screen.createclan.fields_empty"), true);
            return;
        }

        String clanName = clanNameBox.getValue().trim();
        String clanSentence = clanSentenceBox.getValue().trim();

        int symbolIndex = 0;
        if (symbolList.getSelected() != null) {
            symbolIndex = symbolList.getSelected().getId();
        }

        float[] rgb = FancySpecificClanScreen.ensureMinLuminance(
                (rSlider.getActualValue()),
                (gSlider.getActualValue()),
                (bSlider.getActualValue()),
                0.30f);

        int color = FastColor.ARGB32.color(
                255,
                (int) (rgb[0]*255),
                (int) (rgb[1]*255),
                (int) (rgb[2]*255)
        );

        ModPackets.sendToServer(new C2SRegisterClanPacket(color, clanName, clanSentence, symbolIndex));
        Minecraft.getInstance().setScreen(null);

    }

    @Override
    public int getErrorXOffset() {
        return 0;
    }

    @Override
    public int getErrorYOffset() {
        return -80;
    }

    @Override
    public float getErrorScale() {
        return 0.9f;
    }

}
