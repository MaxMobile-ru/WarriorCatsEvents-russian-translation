package net.snowteb.warriorcats_events.screen.screens;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.snowteb.warriorcats_events.WCEClient;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.client.ClanInfo;
import net.snowteb.warriorcats_events.client.ClientClanCache;
import net.snowteb.warriorcats_events.entity.ModEntities;
import net.snowteb.warriorcats_events.entity.custom.WCatEntity;
import net.snowteb.warriorcats_events.managers.ClanSymbol;
import net.snowteb.warriorcats_events.network.packet.c2s.clan.CtSRequestManageScreenPacket;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.c2s.clan.CtSRegisterLogPacket;
import net.snowteb.warriorcats_events.screen.widgets.*;
import net.snowteb.warriorcats_events.sound.ModSounds;
import org.lwjgl.glfw.GLFW;

import java.util.UUID;

import static net.snowteb.warriorcats_events.screen.screens.ManageClanScreen.pose;
import static net.snowteb.warriorcats_events.screen.screens.ManageClanScreen.rotation;

public class SpecificClanScreen extends Screen {
    private final UUID specificUUID;
    private ClanInfo clan;

    private String activeScreen = "";

    private DataScrollList membersList;
    private MemberScrollList NPCmembersList;
    private LogScrollList clanLogsList;

    private GradientToggleButton membersButton;
    private GradientToggleButton logsButton;
    private GradientToggleButton manageButton;

    private GradientToggleButton players;
    private GradientToggleButton npcs;

    private GradientToggleButton backButton;

    private ModButton searchBarActiveButton;

    private boolean searchBarActive = false;
    private boolean sendLogBarActive = false;

    private EditBox searchBar;
    private EditBox sendLogBar;

    String errorMessage = "";
    int showErrorMessageFor;

    public SpecificClanScreen(String pTitle, UUID clanUUID) {
        super(Component.literal(pTitle));
        this.specificUUID = clanUUID;
    }


    private float menuY;
    private final float targetY = 0;


    @Override
    protected void init() {
        menuY = -200;

        this.clan = ClientClanCache.getClan(specificUUID);
        if (this.clan == null) {
            Minecraft.getInstance().setScreen(null);
            return;
        }

        int centerX = width / 2;
        int centerY = height / 2;

        membersButton = new GradientToggleButton(
                centerX - 140,
                centerY - 120,
                80, 20,
                Component.literal("Members"),
                btn -> {
                    selectFirstOptions(membersButton);
                    drawShowPlayerOrCatsMembers();
                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                80, 20, clan.color
        );

        logsButton = new GradientToggleButton(
                centerX - 40,
                centerY - 120,
                80, 20,
                Component.literal("Logs"),
                btn -> {
                    selectFirstOptions(logsButton);
                    drawShowLogsMembers();
                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                80, 20,  clan.color
        );

        manageButton = new GradientToggleButton(
                centerX + 60,
                centerY - 120,
                80, 20,
                Component.literal("Manage"),
                btn -> {
                    selectFirstOptions(manageButton);
                    if (clan.canManage) {
                        onClose();
                        ModPackets.sendToServer(new CtSRequestManageScreenPacket(clan.uuid));
                    } else {
                        errorMessage = "You don't have permission to do that";
                        showErrorMessageFor = 100;
                    }

                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                80, 20,  clan.color
        );




        membersList = new DataScrollList(
                Minecraft.getInstance(),
                140,
                this.height - 60,
                centerY + 0,
                centerY + 80,
                20
        );
        this.membersList.setRenderTopAndBottom(false);
        this.membersList.setLeftPos(centerX - 80);
        membersList.setMembers(clan.memberMorphNames);
        this.membersList.setRenderBackground(false);

        NPCmembersList = new MemberScrollList(
                Minecraft.getInstance(),
                140,
                this.height - 60,
                centerY + 0,
                centerY + 80,
                20
        );
        this.NPCmembersList.setRenderTopAndBottom(false);
        this.NPCmembersList.setLeftPos(centerX - 80);
        this.NPCmembersList.setRenderBackground(false);



        for (ClanInfo.ClientClanCat cat : clan.clanCats) {
            NPCmembersList.addOption(cat.name, cat.uuid, cat.variant, true, cat.onGeneticalSkin,
                    cat.genetics, cat.chimeraGenetics, cat.variants, cat.chimeraVariants);
        }

        clanLogsList = new LogScrollList(
                Minecraft.getInstance(),
                190,
                this.height - 60,
                centerY - 40,
                centerY + 80,
                70
        );
        this.clanLogsList.setRenderTopAndBottom(false);
        this.clanLogsList.setLeftPos(centerX - 100);
        clanLogsList.setLogs(clan.clanLogs);
        this.clanLogsList.setRenderBackground(false);

        backButton = new GradientToggleButton(
                centerX - 20, centerY + 88, 40, 15, Component.literal("Back"),
                btn -> {
                    WCEClient.playLocalSound(ModSounds.MENU_OPEN.get(), SoundSource.NEUTRAL, 0.8f,1.3f);
                    Minecraft.getInstance().setScreen(new ClanListScreen(false));
                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                80, 20, 0.8f,  clan.color
        );

        searchBarActiveButton = new ModButton(
                centerX - 35, centerY - 16, 10, 10, Component.literal("\uD83D\uDD0D"),
                btn -> {
                    this.addRenderableWidget(searchBar);
                    searchBarActive = true;
                    this.removeWidget(searchBarActiveButton);
                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                80, 20, 1.0f
        );


        drawMainScreen();

//        this.addRenderableWidget(this.membersList);


    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);

        if (menuY < targetY) {
            menuY += (targetY - menuY) * 0.10f;
            if (menuY > targetY) menuY = targetY;
        }

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(0, menuY, 0);

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);


        int centerX = width / 2;
        int centerY = height / 2;

        if (clan == null) {
            pGuiGraphics.drawCenteredString(
                    Minecraft.getInstance().font,
                    "Clan data not loaded",
                    centerX,
                    centerY,
                    0xFF5555
            );
            return;
        }

        if (showErrorMessageFor > 0) {
            pGuiGraphics.drawCenteredString(this.font, errorMessage, centerX, this.height - 12, 0xFFFF0000);
        }

        if (clan.canManage) pGuiGraphics.drawString(Minecraft.getInstance().font,
                Component.literal("Manager").withStyle(ChatFormatting.GRAY), 2, 2, 0xFFFFFF);
        if (Minecraft.getInstance().player != null) {
            {
                pGuiGraphics.pose().pushPose();
                pGuiGraphics.pose().translate(centerX, this.height-8, 0);
                pGuiGraphics.pose().scale(0.7f,0.7f,0.8f);
                pGuiGraphics.drawCenteredString(Minecraft.getInstance().font,
                        Component.literal(clan.uuid.toString()).withStyle(ChatFormatting.GRAY), 0, 0, 0x99FFFFFF);
                pGuiGraphics.pose().popPose();
            }
        }




        int alpha = 0xBD;
        int colorGradient = (alpha << 24) | (clan.color & 0xFFFFFF);

        centerY += 6;

        pGuiGraphics.fillGradient(centerX - 100, centerY - 100, centerX + 100, centerY + 100, 0xbd000000, colorGradient);
        pGuiGraphics.fillGradient(centerX - 100, centerY - 100, centerX + 100, centerY + 100, 0, 0xbd000000);
        pGuiGraphics.renderOutline(centerX - 100, centerY - 100, 200, 200, 0xFFFFFFFF);


        if (NPCmembersList.getSelected() != null && activeScreen.equals("cats")) {
            ClanInfo.ClientClanCat cat = clan.getClanCat(NPCmembersList.getSelected().getId());

            pGuiGraphics.fillGradient(centerX + 110, centerY - 100, this.width -10, centerY + 100, 0xbd000000, colorGradient);
            pGuiGraphics.fillGradient(centerX + 110, centerY - 100, this.width -10, centerY + 100, 0, 0xbd000000);
            pGuiGraphics.renderOutline(centerX + 110, centerY - 100,(this.width - (centerX + 110)) - 10, 200, 0xFFFFFFFF);

            if (cat != null) {

                WCatEntity entityToRender = new WCatEntity(ModEntities.WCAT.get(), Minecraft.getInstance().level);
                entityToRender.setAnImage(true);
                entityToRender.setPlayerBoundUuid(UUID.nameUUIDFromBytes(ModEntities.WCAT.get().toString().getBytes()));
                entityToRender.setShowMorphName(false);
                entityToRender.setVariant(cat.variant);

                entityToRender.setOnGeneticalSkin(cat.onGeneticalSkin);
                entityToRender.setGenetics(cat.genetics);
                entityToRender.setChimeraGenetics(cat.chimeraGenetics);
                entityToRender.setGeneticalVariants(cat.variants);
                entityToRender.setGeneticalVariantsChimera(cat.chimeraVariants);

                entityToRender.setOnGround(true);
                entityToRender.setYRot(0);
                entityToRender.yHeadRot = 0;
                entityToRender.yBodyRot = 0;
                entityToRender.setXRot(0);


                int infoCenterX = (centerX + 110) + ((this.width - 10) - (centerX + 110)) / 2;

                InventoryScreen.renderEntityInInventory(pGuiGraphics, infoCenterX, centerY - 20, 12, pose , rotation, entityToRender);

                pGuiGraphics.drawCenteredString(Minecraft.getInstance().font, cat.name,  infoCenterX, centerY - 90, 0xFFFFFFFF);
                pGuiGraphics.hLine(centerX + 120, this.width -20, centerY-78, 0xFFFFFFFF);

                pGuiGraphics.drawCenteredString(Minecraft.getInstance().font, cat.age,  infoCenterX, centerY - 10, 0xFFFFFFFF);
                pGuiGraphics.drawCenteredString(Minecraft.getInstance().font, cat.rank,  infoCenterX, centerY + 5, 0xFFFFFFFF);
                pGuiGraphics.drawCenteredString(Minecraft.getInstance().font, cat.gender,  infoCenterX, centerY + 20, 0xFFFFFFFF);
                drawCenteredMultilineString(pGuiGraphics, cat.parents, infoCenterX,centerY + 35, ((this.width - 10) - (centerX + 110)) - 20, 0xFFFFFFFF);


            }
        }

        if (sendLogBarActive) {
            int renderBGwidth = (centerX - 110) - 10;

            pGuiGraphics.drawCenteredString(Minecraft.getInstance().font,
                    Component.literal("Send a log").withStyle(ChatFormatting.WHITE), 10 + renderBGwidth / 2, centerY - 90, 0xFFFFFF);



            pGuiGraphics.fillGradient(10, centerY-100, 10 + renderBGwidth, centerY-100+90, 0, 0x88000000);

            pGuiGraphics.renderOutline(10, centerY-100, renderBGwidth, 90, 0x40FFFFFF);
        }


        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);


        if (activeScreen.equals("main")) {
            drawCenteredMultilineString(pGuiGraphics, Component.literal(clan.clanSentence).withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC), centerX,centerY + 15, 140, 0xFFFFFFFF);

            pGuiGraphics.blit(
                    ClanSymbol.SPRITE,
                    centerX - 25, centerY - 50,
                    ClanSymbol.getSymbolCoordinate(clan.symbolIndex), 0,
                    ClanSymbol.SYMBOL_SIZE, ClanSymbol.SYMBOL_SIZE,
                    ClanSymbol.SYMBOL_SIZE * ClanSymbol.SYMBOLS_AMOUNT,
                    ClanSymbol.SYMBOL_SIZE
            );
        }

        if (activeScreen.equals("players") || activeScreen.equals("cats") || activeScreen.equals("members")) {
            pGuiGraphics.renderOutline(centerX-80, centerY-10, 160, 90, 0x40FFFFFF);
            pGuiGraphics.drawCenteredString(Minecraft.getInstance().font,
                    "Members: " + (clan.memberCount + clan.clanCats.size()), centerX, centerY - 35, 0xFFFFFF);

            if (activeScreen.equals("players")) {
                pGuiGraphics.drawCenteredString(Minecraft.getInstance().font,"Players: " + (clan.memberCount) , centerX, centerY - 20, 0xFFFFFF);

            }
            if (activeScreen.equals("cats") && !searchBarActive) {
                pGuiGraphics.drawCenteredString(Minecraft.getInstance().font,
                        "NPCs: " + (clan.clanCats.size()), centerX, centerY - 20, 0xFFFFFF);

            }

        }

        pGuiGraphics.hLine(centerX-70, centerX+70, centerY-78, 0xFFFFFFFF);

        if (clan.name != null && clan.color != 0) pGuiGraphics.drawCenteredString(Minecraft.getInstance().font,
                clan.name, centerX, centerY - 90, clan.color);

        if (clan.leaderName != null) pGuiGraphics.drawCenteredString(Minecraft.getInstance().font,
                "Leader: " + clan.leaderName, centerX, centerY - 70, 0xFFFFFF);


        pGuiGraphics.pose().popPose();

    }

    private void drawMainScreen(){
        this.clearWidgets();
        activeScreen = "main";

        this.addRenderableWidget(membersButton);
        this.addRenderableWidget(logsButton);
        this.addRenderableWidget(manageButton);


        int centerX = width / 2;
        int centerY = height / 2;

//        this.addRenderableWidget(
//                Button.builder(Component.literal("Back"), btn -> {
//                    Minecraft.getInstance().setScreen(new ClanListScreen());
//                }).bounds(centerX - 20, centerY + 70, 40, 20).run()
//        );

        this.addRenderableWidget(backButton);

    }

    private void drawShowPlayerOrCatsMembers(){
        this.clearWidgets();
        activeScreen = "members";
        sendLogBarActive = false;

        int centerX = width / 2;
        int centerY = height / 2;

        npcs = new GradientToggleButton(
                centerX - 65,
                centerY - 50,
                60, 15,
                Component.literal("NPCs"),
                btn -> {
                    selectMemberType(npcs);
                    drawShowCatsMembers();
                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                80, 15,0.8f,  clan.color
        );

        players = new GradientToggleButton(
                centerX + 5,
                centerY - 50,
                60, 15,
                Component.literal("Players"),
                btn -> {
                    selectMemberType(players);
                    drawShowPlayerMembers();
                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                80, 20,0.8f,  clan.color
        );

        this.addRenderableWidget(players);
        this.addRenderableWidget(npcs);

        this.addRenderableWidget(membersButton);
        this.addRenderableWidget(logsButton);
        this.addRenderableWidget(manageButton);

        this.addRenderableWidget(backButton);

        drawShowCatsMembers();
        npcs.setSelected(true);
    }

    private void drawShowCatsMembers(){
        this.removeWidget(NPCmembersList);
        this.removeWidget(membersList);
        this.removeWidget(searchBar);
        this.removeWidget(searchBarActiveButton);

        searchBarActive = false;

        activeScreen = "cats";

        int centerX = width / 2;
        int centerY = height / 2;

        searchBar = new EditBox(
                this.font,
                centerX - 35,
                centerY - 16,
                70,
                10,
                Component.literal("Search")
        );

        searchBar.setValue("");
        searchBar.setResponder(this::filterNPCList);
        searchBar.setHint(Component.literal("<Search>").withStyle(ChatFormatting.DARK_GRAY));

        filterNPCList("");


        this.addRenderableWidget(searchBarActiveButton);

        this.addRenderableWidget(NPCmembersList);
    }

    private void drawShowPlayerMembers(){
        this.removeWidget(NPCmembersList);
        this.removeWidget(membersList);
        this.removeWidget(searchBar);
        this.removeWidget(searchBarActiveButton);

        searchBarActive = false;

        activeScreen = "players";

        this.addRenderableWidget(membersList);
    }
    private void drawShowLogsMembers(){
        this.clearWidgets();
        activeScreen = "logs";

        NPCmembersList.setSelected(null);

        int centerX = width / 2;
        int centerY = height / 2;

        this.addRenderableWidget(backButton);

        if (clan.canManage) {
            this.addRenderableWidget(new GradientToggleButton(
                    centerX + 50, centerY + 88, 45, 15, Component.literal("Write Log"),
                    btn -> {
                        drawSendLogWidgets();
                    },
                    ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.8f,  clan.color
            ));
        }

        this.addRenderableWidget(membersButton);
        this.addRenderableWidget(logsButton);
        this.addRenderableWidget(manageButton);

        this.addRenderableWidget(clanLogsList);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (searchBar != null && searchBar.isFocused()) {
            return searchBar.charTyped(codePoint, modifiers);
        }
        if (sendLogBar != null && sendLogBar.isFocused()) {
            return sendLogBar.charTyped(codePoint, modifiers);
        }

        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (searchBar != null && searchBar.isFocused()) {
            return searchBar.keyPressed(keyCode, scanCode, modifiers);
        }
        if (sendLogBar != null && sendLogBar.isFocused()) {
            return sendLogBar.keyPressed(keyCode, scanCode, modifiers);
        }

        if (keyCode == GLFW.GLFW_KEY_E) {
            this.onClose();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }


    private void selectFirstOptions(GradientToggleButton selected) {
        this.logsButton.setSelected(false);
        this.membersButton.setSelected(false);
        this.manageButton.setSelected(false);

        selected.setSelected(true);
    }

    private void selectMemberType(GradientToggleButton selected) {
        this.players.setSelected(false);
        this.npcs.setSelected(false);

        selected.setSelected(true);
    }

    private void filterNPCList(String text) {
        NPCmembersList.clearOptions();

        String lower = text.toLowerCase();

        for (ClanInfo.ClientClanCat cat : clan.clanCats) {
            if (cat.name.toLowerCase().contains(lower)) {
                NPCmembersList.addOption(cat.name, cat.uuid, cat.variant, true, cat.onGeneticalSkin,
                        cat.genetics, cat.chimeraGenetics, cat.variants, cat.chimeraVariants);
            }
        }
    }

    private void drawCenteredMultilineString(GuiGraphics gui, String text, int centerX, int startY, int maxWidth, int color) {
        var font = Minecraft.getInstance().font;
        var lines = font.split(Component.literal(text), maxWidth);

        int lineHeight = font.lineHeight;

        for (int i = 0; i < lines.size(); i++) {
            gui.drawCenteredString(
                    font,
                    lines.get(i),
                    centerX,
                    startY + (i * lineHeight),
                    color
            );
        }
    }

    private void drawCenteredMultilineString(GuiGraphics gui, Component text, int centerX, int startY, int maxWidth, int color) {
        var font = Minecraft.getInstance().font;
        var lines = font.split(text, maxWidth);

        int lineHeight = font.lineHeight;

        for (int i = 0; i < lines.size(); i++) {
            gui.drawCenteredString(
                    font,
                    lines.get(i),
                    centerX,
                    startY + (i * lineHeight),
                    color
            );
        }
    }



    private void drawSendLogWidgets() {
        this.clearWidgets();
        drawShowLogsMembers();

        int centerX = width / 2;
        int centerY = height / 2;

        int logBarWidth = (centerX - 115) - 15;

        sendLogBar = new EditBox(
                this.font,
                15,
                centerY - 70,
                logBarWidth,
                20,
                Component.literal("Message")
        );

        sendLogBar.setMaxLength(140);
        sendLogBar.setHint(Component.literal("<message>").withStyle(ChatFormatting.DARK_GRAY));

        this.addRenderableWidget(Button.builder(
                Component.literal("Send"),
                btn -> {
                    if (sendLogBar.getValue().isEmpty()) {
                        errorMessage = "The content is empty.";
                        showErrorMessageFor = 100;
                    } else {
                        ModPackets.sendToServer(new CtSRegisterLogPacket(sendLogBar.getValue(), clan.uuid));
                        this.onClose();
                    }
                }
        ).bounds(logBarWidth/2 -5, centerY - 35, 40, 20).build());

        this.addRenderableWidget(sendLogBar);
        sendLogBarActive = true;

    }

    @Override
    public void tick() {

        if (showErrorMessageFor > 0) {
            showErrorMessageFor--;
        }
    }
}
