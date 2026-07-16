package net.snowteb.warriorcats_events.screen.screens;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.c2s.clan.C2SRegisterClanPacket;
import net.snowteb.warriorcats_events.managers.ClanSymbol;
import net.snowteb.warriorcats_events.screen.widgets.ToggleButton;
import net.snowteb.warriorcats_events.screen.widgets.ImageScrollList;

import java.util.ArrayList;
import java.util.List;

public class CreateClanScreen extends Screen {
    private int color = 0xFFffffff;
    private final String morphName;

    private int textCooldown = 0;

    private ToggleButton orangeButton;
    private ToggleButton redButton;
    private ToggleButton cyanButton;
    private ToggleButton pinkButton;
    private ToggleButton sandButton;
    private ToggleButton greenButton;
    private ToggleButton grayButton;
    private ToggleButton yellowButton;
    private ToggleButton whiteButton;
    private ToggleButton limeButton;
    private ToggleButton aquaButton;
    private ToggleButton skyButton;
    private ToggleButton deepBlueButton;
    private ToggleButton ultraPurpleButton;
    private ToggleButton caramelButton;
    private ToggleButton mossButton;
    private ToggleButton seaButton;
    private ToggleButton burdeosButton;
    private ToggleButton soilButton;
    private ToggleButton lilyButton;
    private ToggleButton cakePinkButton;
    private ToggleButton salmonButton;


    private EditBox clanNameBox;
    private EditBox clanSentenceBox;

    private Button saveButton;

    private ImageScrollList symbolList;

    public static final ResourceLocation BG_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/clan_setup/background_scene.png");
    public static final ResourceLocation BANNER =
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/clan_setup/banner.png");


    private enum Colors {

        ORANGE(0xFFffb325),
        RED(0xFFff2125),
        TURQUOISE(0xFF4ffdff),
        PINK(0xFFff4ffc),
        SAND(0xFFeaf2b3),
        GREEN(0xFF2de649),
        LIME(0xFF92ff21),
        AQUA(0xFF21ffa4),
        SKY(0xFF21ccff),
        DEEP_BLUE(0xFF5021ff),
        ULTRA_PURPLE(0xFFad21ff),
        CARAMEL(0xFFff2173),
        MOSS(0xFF79af6c),
        SEA(0xFF766cc6),
        BURDEOS(0xFF9c2929),
        SOIL(0xFF877151),
        LILY(0xFFce7cff),
        CAKE_PINK(0xFFffa8d1),
        SALMON(0xFFff7070),
        GRAY(0xFF808080),
        YELLOW(0xFFffee40),
        WHITE(0xFFffffff);

        private final int color;

        Colors(int color) {
            this.color = color;
        }

        public int getColor() {
            return color;
        }
    }



    public CreateClanScreen(String morphName) {
        super(Component.literal("Create Clan"));
        this.morphName = morphName;
    }

    @Override
    protected void init() {
        int centerX = width /2;
        int centerY = height /2;

        int buttonDimension = 15;

        int initialX = centerX - 78;
        int incrementX = buttonDimension + 5;


        int infoX = initialX;
        int infoY = centerY + 10;

        symbolList = new ImageScrollList(
                Minecraft.getInstance(),
                80,
                50,
                centerY - 40,
                centerY + 80
        );
        symbolList.setRenderBackground(false);
        symbolList.setRenderTopAndBottom(false);
        symbolList.setLeftPos(centerX + 100);

        for (int i = 0; i < ClanSymbol.SYMBOLS_AMOUNT; i++) {
            symbolList.addOption(i);
        }

        clanNameBox = new EditBox(
                this.font,
                centerX - 40, centerY - 35,
                80, 15,
                Component.translatable("screen.createclan.clan_name")
        );
        clanNameBox.setMaxLength(20);
        clanNameBox.setHint(Component.literal("'Bengalclan'").withStyle(ChatFormatting.DARK_GRAY).withStyle(ChatFormatting.ITALIC));


        clanSentenceBox = new EditBox(
                this.font,
                centerX - 60, centerY - 12,
                120, 15,
                Component.translatable("screen.createclan.clan_sentence")
        );
        clanSentenceBox.setHint(Component.literal("'Bengalclan is...'").withStyle(ChatFormatting.DARK_GRAY).withStyle(ChatFormatting.ITALIC));
        clanSentenceBox.setMaxLength(130);


        orangeButton = new ToggleButton(
                infoX, infoY, buttonDimension, buttonDimension,
                "",
                btn -> selectColor(orangeButton), Colors.ORANGE.getColor()
        );

        infoX += incrementX;

        redButton = new ToggleButton(
                infoX, infoY, buttonDimension, buttonDimension,
                "",
                btn -> selectColor(redButton), Colors.RED.getColor()
        );

        infoX += incrementX;

        cyanButton = new ToggleButton(
                infoX, infoY, buttonDimension, buttonDimension,
                "",
                btn -> selectColor(cyanButton), Colors.TURQUOISE.getColor()
        );

        infoX += incrementX;

        pinkButton = new ToggleButton(
                infoX, infoY, buttonDimension, buttonDimension,
                "",
                btn -> selectColor(pinkButton), Colors.PINK.getColor()
        );

        infoX += incrementX;

        sandButton = new ToggleButton(
                infoX, infoY, buttonDimension, buttonDimension,
                "",
                btn -> selectColor(sandButton), Colors.SAND.getColor()
        );

        infoX += incrementX;

        limeButton = new ToggleButton(
                infoX, infoY, buttonDimension, buttonDimension,
                "",
                btn -> selectColor(limeButton), Colors.LIME.getColor()
        );

        infoX += incrementX;

        aquaButton = new ToggleButton(
                infoX, infoY, buttonDimension, buttonDimension,
                "",
                btn -> selectColor(aquaButton), Colors.AQUA.getColor()
        );

        infoX += incrementX;

        skyButton = new ToggleButton(
                infoX, infoY, buttonDimension, buttonDimension,
                "",
                btn -> selectColor(skyButton), Colors.SKY.getColor()
        );

        infoX = initialX;
        infoY += incrementX;

        deepBlueButton = new ToggleButton(
                infoX, infoY, buttonDimension, buttonDimension,
                "",
                btn -> selectColor(deepBlueButton), Colors.DEEP_BLUE.getColor()
        );

        infoX += incrementX;

        ultraPurpleButton = new ToggleButton(
                infoX, infoY, buttonDimension, buttonDimension,
                "",
                btn -> selectColor(ultraPurpleButton), Colors.ULTRA_PURPLE.getColor()
        );

        infoX += incrementX;

        caramelButton = new ToggleButton(
                infoX, infoY, buttonDimension, buttonDimension,
                "",
                btn -> selectColor(caramelButton), Colors.CARAMEL.getColor()
        );

        infoX += incrementX;

        mossButton = new ToggleButton(
                infoX, infoY, buttonDimension, buttonDimension,
                "",
                btn -> selectColor(mossButton), Colors.MOSS.getColor()
        );

        infoX += incrementX;

        seaButton = new ToggleButton(
                infoX, infoY, buttonDimension, buttonDimension,
                "",
                btn -> selectColor(seaButton), Colors.SEA.getColor()
        );

        infoX += incrementX;

        burdeosButton = new ToggleButton(
                infoX, infoY, buttonDimension, buttonDimension,
                "",
                btn -> selectColor(burdeosButton), Colors.BURDEOS.getColor()
        );

        infoX += incrementX;

        soilButton = new ToggleButton(
                infoX, infoY, buttonDimension, buttonDimension,
                "",
                btn -> selectColor(soilButton), Colors.SOIL.getColor()
        );

        infoX += incrementX;

        lilyButton = new ToggleButton(
                infoX, infoY, buttonDimension, buttonDimension,
                "",
                btn -> selectColor(lilyButton), Colors.LILY.getColor()
        );

        infoX = initialX;
        infoY += incrementX;

        cakePinkButton = new ToggleButton(
                infoX, infoY, buttonDimension, buttonDimension,
                "",
                btn -> selectColor(cakePinkButton), Colors.CAKE_PINK.getColor()
        );

        infoX += incrementX;

        salmonButton = new ToggleButton(
                infoX, infoY, buttonDimension, buttonDimension,
                "",
                btn -> selectColor(salmonButton), Colors.SALMON.getColor()
        );

        infoX += incrementX;

        greenButton = new ToggleButton(
                infoX, infoY, buttonDimension, buttonDimension,
                "",
                btn -> selectColor(greenButton), Colors.GREEN.getColor()
        );

        infoX += incrementX;

        yellowButton = new ToggleButton(
                infoX, infoY, buttonDimension, buttonDimension,
                "",
                btn -> selectColor(yellowButton),  Colors.YELLOW.getColor()
        );

        infoX += incrementX;

        grayButton = new ToggleButton(
                infoX, infoY, buttonDimension, buttonDimension,
                "",
                btn -> selectColor(grayButton), Colors.GRAY.getColor()
        );

        infoX += incrementX;

        whiteButton = new ToggleButton(
                infoX, infoY, buttonDimension, buttonDimension,
                "",
                btn -> selectColor(whiteButton),  Colors.WHITE.getColor()
        );

        infoX += incrementX;

        saveButton = Button.builder(
                Component.literal("Register"),
                btn -> saveAndSend()
        ).bounds(centerX-40, centerY + 85, 80, 20).build();

        this.addRenderableWidget(saveButton);

        this.addRenderableWidget(orangeButton);
        this.addRenderableWidget(redButton);
        this.addRenderableWidget(cyanButton);
        this.addRenderableWidget(pinkButton);
        this.addRenderableWidget(sandButton);
        this.addRenderableWidget(greenButton);
        this.addRenderableWidget(grayButton);
        this.addRenderableWidget(yellowButton);
        this.addRenderableWidget(whiteButton);
        this.addRenderableWidget(limeButton);
        this.addRenderableWidget(aquaButton);
        this.addRenderableWidget(skyButton);
        this.addRenderableWidget(deepBlueButton);
        this.addRenderableWidget(ultraPurpleButton);
        this.addRenderableWidget(caramelButton);
        this.addRenderableWidget(mossButton);
        this.addRenderableWidget(seaButton);
        this.addRenderableWidget(burdeosButton);
        this.addRenderableWidget(soilButton);
        this.addRenderableWidget(lilyButton);
        this.addRenderableWidget(cakePinkButton);
        this.addRenderableWidget(salmonButton);


        this.addRenderableWidget(clanNameBox);
        this.addRenderableWidget(clanSentenceBox);
        this.addRenderableWidget(symbolList);

        super.init();
    }

    private void selectColor(ToggleButton selected) {
        orangeButton.setSelected(false);
        redButton.setSelected(false);
        cyanButton.setSelected(false);
        pinkButton.setSelected(false);
        sandButton.setSelected(false);
        greenButton.setSelected(false);
        grayButton.setSelected(false);
        yellowButton.setSelected(false);
        whiteButton.setSelected(false);
        limeButton.setSelected(false);
        aquaButton.setSelected(false);
        skyButton.setSelected(false);
        deepBlueButton.setSelected(false);
        ultraPurpleButton.setSelected(false);
        caramelButton.setSelected(false);
        mossButton.setSelected(false);
        soilButton.setSelected(false);
        lilyButton.setSelected(false);
        cakePinkButton.setSelected(false);
        salmonButton.setSelected(false);
        seaButton.setSelected(false);
        burdeosButton.setSelected(false);

        if (selected.equals(orangeButton)) {
            color = Colors.ORANGE.getColor();
        } else if (selected.equals(redButton)) {
            color = Colors.RED.getColor();
        } else if (selected.equals(cyanButton)) {
            color = Colors.TURQUOISE.getColor();
        } else if (selected.equals(pinkButton)) {
            color = Colors.PINK.getColor();
        } else if (selected.equals(sandButton)) {
            color = Colors.SAND.getColor();
        } else if (selected.equals(greenButton)) {
            color = Colors.GREEN.getColor();
        } else if (selected.equals(grayButton)) {
            color = Colors.GRAY.getColor();
        } else if (selected.equals(yellowButton)) {
            color = Colors.YELLOW.getColor();
        } else if (selected.equals(whiteButton)) {
            color = Colors.WHITE.getColor();
        } else if (selected.equals(limeButton)) {
            color = Colors.LIME.getColor();
        } else if (selected.equals(aquaButton)) {
            color = Colors.AQUA.getColor();
        } else if (selected.equals(skyButton)) {
            color = Colors.SKY.getColor();
        } else if (selected.equals(deepBlueButton)) {
            color = Colors.DEEP_BLUE.getColor();
        } else if (selected.equals(ultraPurpleButton)) {
            color = Colors.ULTRA_PURPLE.getColor();
        } else if (selected.equals(caramelButton)) {
            color = Colors.CARAMEL.getColor();
        } else if (selected.equals(mossButton)) {
            color = Colors.MOSS.getColor();
        } else if (selected.equals(soilButton)) {
            color = Colors.SOIL.getColor();
        } else if (selected.equals(lilyButton)) {
            color = Colors.LILY.getColor();
        } else if (selected.equals(cakePinkButton)) {
            color = Colors.CAKE_PINK.getColor();
        } else if (selected.equals(salmonButton)) {
            color = Colors.SALMON.getColor();
        } else if (selected.equals(seaButton)) {
            color = Colors.SEA.getColor();
        } else if (selected.equals(burdeosButton)) {
            color = Colors.BURDEOS.getColor();
        }

        selected.setSelected(true);
    }


    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        int centerx = width / 2;
        int centery = height / 2;

        pGuiGraphics.blit(BG_TEXTURE, 0, 0, 0, 0, this.width, this.height, this.width, this.height);

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        int symbolToShow = 0;
        if (symbolList.getSelectedEntry() != null) {
            symbolToShow = symbolList.getSelectedEntry().getId();
        }

        float a = ((color >> 24) & 0xFF) / 255f;
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;

        pGuiGraphics.setColor(r, g, b, a);

        pGuiGraphics.blit(
                ClanSymbol.SPRITE,
                centerx - 25, centery - 110,
                ClanSymbol.getSymbolCoordinate(symbolToShow), 0,
                ClanSymbol.SYMBOL_SIZE, ClanSymbol.SYMBOL_SIZE,
                ClanSymbol.SYMBOL_SIZE * ClanSymbol.SYMBOLS_AMOUNT,
                ClanSymbol.SYMBOL_SIZE
        );

        pGuiGraphics.setColor(1f, 1f, 1f, 1f);


        Component text = Component.translatable("screen.createclan.and_founded",
                        Component.literal(morphName).withStyle(ChatFormatting.WHITE),
                        Component.literal(!clanNameBox.getValue().trim().isEmpty() ? clanNameBox.getValue().trim() : "...").withStyle(Style.EMPTY.withColor(color)))
                        ;

        pGuiGraphics.drawCenteredString(Minecraft.getInstance().font, text, centerx, centery - 50, 0xFFFFFF);

        boolean showSentenceToolTip = pMouseX > centerx - 60 && pMouseX < centerx + 60
                                    && pMouseY > centery - 12 && pMouseY < centery + 3;

        boolean showClanNameTooltip = pMouseX > centerx - 40 && pMouseX < centerx + 40
                && pMouseY > centery - 35 && pMouseY < centery - 20;

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

        int xPosition = -230;
        int yPosition = -120;

        pGuiGraphics.enableScissor(10, centery + yPosition, 143, centery + yPosition + 37);
        pGuiGraphics.blit(
                BANNER,
                10, centery - 120,
                0, 0,
                800, 225,
                133, 37
        );
        pGuiGraphics.disableScissor();

        if (textCooldown > 0) {
            pGuiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("screen.createclan.fields_empty"),
                    centerx - 55, centery + 75, 0xFFFF0000);
        }

    }

    private void saveAndSend(){
        if (clanNameBox.getValue().isEmpty()) {
            textCooldown = 100;
            return;
        }

        String clanName = clanNameBox.getValue().trim();
        String clanSentence = clanSentenceBox.getValue().trim();

        int symbolIndex = 0;
        if (symbolList.getSelectedEntry() != null) {
            symbolIndex = symbolList.getSelectedEntry().getId();
        }

        ModPackets.sendToServer(new C2SRegisterClanPacket(color, clanName, clanSentence, symbolIndex));
        Minecraft.getInstance().setScreen(null);

    }

    @Override
    public void tick() {
        if (textCooldown > 0) {
            textCooldown--;
        }
        super.tick();
    }

}
