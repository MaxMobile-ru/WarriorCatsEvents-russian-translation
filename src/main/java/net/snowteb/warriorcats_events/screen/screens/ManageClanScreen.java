package net.snowteb.warriorcats_events.screen.screens;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.ChunkPos;
import net.snowteb.warriorcats_events.WCEClient;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.client.ClanInfo;
import net.snowteb.warriorcats_events.client.ClientPacketHandles;
import net.snowteb.warriorcats_events.client.ClientTerritoryData;
import net.snowteb.warriorcats_events.entity.ModEntities;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.c2s.clan.CtSClaimTerritory;
import net.snowteb.warriorcats_events.network.packet.c2s.clan.CtSManageClanMemberPacket;
import net.snowteb.warriorcats_events.network.packet.c2s.clan.CtSUnclaimTerritory;
import net.snowteb.warriorcats_events.managers.ClanSymbol;
import net.snowteb.warriorcats_events.network.packet.c2s.clan.RenameClanPacket;
import net.snowteb.warriorcats_events.screen.widgets.GradientButton;
import net.snowteb.warriorcats_events.screen.widgets.MemberScrollList;
import net.snowteb.warriorcats_events.screen.widgets.ModButton;
import net.snowteb.warriorcats_events.sound.ModSounds;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public class ManageClanScreen extends Screen {

    private final ClanInfo clanInfo;
    private MemberScrollList membersList;
    private ClanInfo.Member selectedMember;
    private String currentMenu = "";
    private String currentPermissionLevel = "";

    private ModButton changeRank;
    private ModButton kickPlayer;
    private ModButton changePerms;
    private ModButton closeTab;

    private GradientButton claimTerritory;
    private GradientButton unclaimTerritory;

    private ModButton closeClaimTerritory;

    private ModButton saveAndClaim;
    private ModButton saveAndUnclaim;

    private EditBox territoryName;

    private ChunkPos currentChunkPos;
    private boolean isInOwnTerritory = false;
    private boolean isTheCore = false;

    private ModButton renameClan;

    private EditBox renameBox;

    private Button backButton;


    public static final Quaternionf rotation = new Quaternionf(0.0F, 0.0F, 0.0F, 0.0F);
    public static final Quaternionf pose = new Quaternionf(1.9F, 0.0F, 0.6F, 0.0F);

    private float menuY;

    public ManageClanScreen(ClanInfo clanInfo) {
        super(Component.literal("Manage Clan"));
        this.clanInfo = clanInfo;
    }

    @Override
    protected void init() {
        int centerX = width / 2;
        int centerY = height / 2;

        ClanInfo.Member thisMember = clanInfo.getMember(Minecraft.getInstance().player.getUUID());
        if (thisMember != null) {
            currentPermissionLevel = thisMember.getPerms();
        }

        claimTerritory = new GradientButton(
                centerX - 85, 10,
                80, 20,
                Component.translatable("screen.clanmanage.claimterritory"),
                btn -> {
                    drawClaimChunkMenu();
                }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                80, 20, 0xFF00104C
        );

        unclaimTerritory = new GradientButton(
                centerX + 5, 10,
                80, 20,
                Component.translatable("screen.clanmanage.unclaimterritory"),
                btn -> {
                    drawUnclaimChunkMenu();
                }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                80, 20, 0xFF00104C
        );

        this.membersList = new MemberScrollList(
                Minecraft.getInstance(),
                100,
                20,
                centerY + 0,
                centerY + 80,
                20
        );
//        this.membersList.setRenderTopAndBottom(false);
        this.membersList.setX(((20)));

        for (ClanInfo.Member member : clanInfo.playersInClan.values()) {
            membersList.addOption(member.getPlayerMorphName(), member.getPlayerUUID());
        }

        kickPlayer = new ModButton(
                this.width - 140,
                centerY + 20,
                100, 20,
                Component.translatable("screen.clanmanage.kickplayer"),
                btn -> {
                    drawConfirmKickMenu();
                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                80, 20
        );

        changeRank = new ModButton(
                this.width - 140,
                centerY + 45,
                100, 20,
                Component.translatable("screen.clanmanage.changerank"),
                btn -> {
                    drawChangeRankMenu();
                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                80, 20
        );

        changePerms = new ModButton(
                this.width - 140,
                centerY + 70,
                100, 20,
                Component.translatable("screen.clanmanage.changeperms"),
                btn -> {
                    drawChangePermsMenu();
                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                80, 20
        );

        int locX = this.width - 135;
        int initialY = (this.height / 2) + 5;
        closeTab = new ModButton(
                locX - 25,
                initialY - 90,
                20, 20,
                Component.literal("X").withStyle(ChatFormatting.GRAY),
                btn -> {
                    this.membersList.setSelected(null);
                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                20, 20
        );

        closeTab.visible = false;
        changeRank.visible = false;
        changePerms.visible = false;
        kickPlayer.visible = false;

        LocalPlayer player =  Minecraft.getInstance().player;

        if (player != null) {
            currentChunkPos = player.chunkPosition();
            ClientTerritoryData.ClientClanTerritories clanTerritory = ClientTerritoryData.CLIENT_TERRITORIES.get(clanInfo.uuid);
            if (clanTerritory != null) {

                if (clanTerritory.core() != null) {
                    if (clanTerritory.core().equals(currentChunkPos)) {
                        isTheCore = true;
                    }
                }


                for (ClientTerritoryData.ClientChunk chunk : clanTerritory.claimedTerritory()) {
                    if (chunk.chunkPos.equals(currentChunkPos)) {
                        isInOwnTerritory = true;
                        break;
                    }
                }
            }
        }

        renameClan = new ModButton(
                50,
                10,
                60, 10,
                Component.translatable("screen.clanmanage.rename"),
                btn -> {
                    drawRenameMenu();
                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                60, 10, 0.8f, true
        );

        backButton = Button.builder(
                Component.translatable("screen.clanmanage.back"),
                btn -> {
                    this.onClose();
                    WCEClient.playLocalSound(ModSounds.MENU_ACCEPT.get(), SoundSource.AMBIENT, 0.2f, 1.0f);
                    ClientPacketHandles.openSpecificClan(clanInfo.name, clanInfo.uuid);
                }
        ).bounds(10, this.height - 25, 60, 15).build();

        drawMainMenu();

        menuY = 500;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {

        if (menuY > 0) {
            menuY -= (menuY) * 0.10f;
            if (menuY < 0) menuY = 0;
        }

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(0, menuY, 0);

        this.renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        int centerX = width / 2;
        int centerY = height / 2;

        pGuiGraphics.fill(10, centerY - 100, 150, centerY + 95, 0, 0xFF000017);
        pGuiGraphics.fillGradient(10, centerY - 100, 150, centerY + 95, 0x8800104C, 0);
        pGuiGraphics.renderOutline(10, centerY - 100, 140, 195, 0xFFFFFFFF);
        pGuiGraphics.renderOutline(19, centerY - 1, 110, 82, 0x88FFFFFF);


        pGuiGraphics.pose().pushPose();

        pGuiGraphics.pose().translate(
                80 - (font.width(clanInfo.name) * 1.2f / 2f),
                centerY - 94,
                0
        );

        pGuiGraphics.pose().mulPose(new Matrix4f().scaling(1.2f, 1.2f, 1f));

        font.drawInBatch(
                clanInfo.name,
                0,
                0,
                clanInfo.color,
                false,
                pGuiGraphics.pose().last().pose(),
                pGuiGraphics.bufferSource(),
                Font.DisplayMode.NORMAL,
                0,
                15728880
        );

        pGuiGraphics.pose().popPose();



        pGuiGraphics.drawCenteredString(Minecraft.getInstance().font, Component.translatable("screen.clanmanage.leader", clanInfo.leaderName).withStyle(ChatFormatting.GRAY),
                80, centerY - 80, 0);

        pGuiGraphics.drawCenteredString(Minecraft.getInstance().font, Component.translatable("screen.clanmanage.perms", currentPermissionLevel).withStyle(ChatFormatting.DARK_GRAY),
                80, centerY - 70, 0);

        int color = ChatFormatting.GRAY.getColor();

        float a = ((color >> 24) & 0xFF) / 255f;
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;

        pGuiGraphics.setColor(r, g, b, a);

        pGuiGraphics.blit(
                ClanSymbol.SPRITE,
                63, centerY - 55,
                (float) ((float) ClanSymbol.getSymbolCoordinate(clanInfo.symbolIndex) / 1.5), (float) 0,
                (int) (ClanSymbol.SYMBOL_SIZE / 1.5), (int) (ClanSymbol.SYMBOL_SIZE/1.5),
                (int) ((ClanSymbol.SYMBOL_SIZE * ClanSymbol.SYMBOLS_AMOUNT) / 1.5),
                (int) (ClanSymbol.SYMBOL_SIZE/1.5)
        );

        pGuiGraphics.setColor(1, 1, 1, 1);

        pGuiGraphics.drawCenteredString(Minecraft.getInstance().font, Component.translatable("screen.clanmanage.player_count", clanInfo.memberCount).withStyle(ChatFormatting.GRAY),
                80, centerY - 15, 0);




        if (membersList.getSelected() != null) {
            this.selectedMember = clanInfo.getMember(membersList.getSelected().getId());
        }

        changeRank.visible = false;
        kickPlayer.visible = false;
        changePerms.visible = false;
        closeTab.visible = false;

        if (selectedMember != null) {

            int infoX = this.width - 90;
            int infoY = centerY - 65;

            pGuiGraphics.fill(this.width - 160, centerY - 100, this.width - 20, centerY + 95, 0, 0xFF000017);
            pGuiGraphics.fillGradient(this.width - 160, centerY - 100, this.width - 20, centerY + 95, 0x8800104C, 0);
            pGuiGraphics.renderOutline(this.width - 160, centerY - 100, 140, 195, 0xFFFFFFFF);



            WCatEntity entityToRender = new WCatEntity(ModEntities.WCAT.get(), Minecraft.getInstance().level);

            entityToRender.setAnImage(false);
            entityToRender.setPlayerBoundUuid(selectedMember.getPlayerUUID());
            entityToRender.setShowMorphName(false);

            entityToRender.setOnGeneticalSkin(selectedMember.isOnGeneticalSkin());
            entityToRender.setVariant(selectedMember.getMorphVariant());
            entityToRender.setGender(1);

            entityToRender.getGeneticsModule().setGenetics(selectedMember.getGenetics());
            entityToRender.getGeneticsModule().setGeneticalVariants(selectedMember.getVariants());
            entityToRender.getGeneticsModule().setChimeraGenetics(selectedMember.getChimeraGenetics());
            entityToRender.getGeneticsModule().setGeneticalVariantsChimera(selectedMember.getChimeraVariants());

            entityToRender.setOnGround(true);
            entityToRender.setYRot(0);
            entityToRender.yHeadRot = 0;
            entityToRender.yBodyRot = 0;
            entityToRender.setXRot(0);

            int entityScale = 12;
            int entityYLocation = centerY - 20;
            if (currentMenu.equals("changeRank")) {
                entityScale = 10;
                entityYLocation -= 13;
            }

            InventoryScreen.renderEntityInInventory(pGuiGraphics, infoX+2, entityYLocation, entityScale, new Vector3f(0,0,0), pose, rotation, entityToRender);

            pGuiGraphics.pose().pushPose();

            pGuiGraphics.pose().translate(
                    (infoX) - (font.width(selectedMember.getPlayerMorphName()) * 1.2f / 2f),
                    infoY - 25 ,
                    0
            );

            pGuiGraphics.pose().mulPose(new Matrix4f().scaling(1.2f, 1.2f, 1f));

            font.drawInBatch(
                    selectedMember.getPlayerMorphName(),
                    0,
                    0,
                    0xFFFFFF,
                    false,
                    pGuiGraphics.pose().last().pose(),
                    pGuiGraphics.bufferSource(),
                    Font.DisplayMode.NORMAL,
                    0,
                    15728880
            );

            pGuiGraphics.pose().popPose();

            pGuiGraphics.drawCenteredString(font, selectedMember.getPerms(), infoX, infoY - 15, 0xAAAAAA);

            infoY += 50;

            if (currentMenu.equals("main")) {
                pGuiGraphics.drawCenteredString(font, Component.translatable("screen.clanmanage.rank", selectedMember.getRank()), infoX, infoY, 0xAAAAAA);
                infoY += 12;

                pGuiGraphics.drawCenteredString(font, Component.translatable("screen.clanmanage.age", selectedMember.getPlayerMorphAge()), infoX, infoY, 0xAAAAAA);
                infoY += 12;

                pGuiGraphics.drawCenteredString(font,
                        (selectedMember.isPlayerOnline() ?
                                Component.translatable("screen.clanmanage.online") : Component.translatable("screen.clanmanage.offline")),
                        infoX,
                        infoY,
                        selectedMember.isPlayerOnline() ? 0x009100 : 0x910000
                );

                if (selectedMember.isPlayerOnline()) {
                    if (Minecraft.getInstance().player != null) {
                        if (!selectedMember.getPlayerUUID().equals(Minecraft.getInstance().player.getUUID())) {
                            changeRank.visible = true;
                            kickPlayer.visible = true;
                            changePerms.visible = true;
                            closeTab.visible = true;
                        }
                    }
                }
            }

            if (currentMenu.equals("confirmKick")) {
                Component text = Component.translatable("screen.clanmanage.kick_confirm",
                        Component.literal(selectedMember.getPlayerMorphName()).withStyle(ChatFormatting.GOLD),
                        Component.literal(clanInfo.name).withStyle(Style.EMPTY.withColor(clanInfo.color)));


                int textWidth = 130;

                List<FormattedCharSequence> lines = font.split(text, textWidth);

                for (int i = 0; i < lines.size(); i++) {
                    pGuiGraphics.drawCenteredString(
                            font,
                            lines.get(i),
                            infoX,
                            infoY + (i * font.lineHeight),
                            0xFFFFFF
                    );
                }

            }

        }

        if (currentMenu.equals("claimChunk") || currentMenu.equals("unclaimChunk") || currentMenu.equals("renameClan")) {

            pGuiGraphics.fill(centerX - 70, centerY - 80, centerX + 70, centerY + 65, 0, 0xFF000017);
            pGuiGraphics.fillGradient(centerX - 70, centerY - 80, centerX + 70, centerY + 65, 0x8800104C, 0);
            pGuiGraphics.renderOutline(centerX - 70, centerY - 80, 140, 145, 0xFFFFFFFF);

            if (currentMenu.equals("claimChunk")) {
                pGuiGraphics.drawCenteredString(this.font,
                        Component.translatable("screen.clanmanage.claim_territory_for").withStyle(ChatFormatting.GRAY),
                        centerX, centerY - 70, 0xFFFFFF);

                pGuiGraphics.drawCenteredString(this.font,
                        Component.literal(clanInfo.name).withStyle(Style.EMPTY.withColor(clanInfo.color)),
                        centerX, centerY - 60, 0xFFFFFF);

                pGuiGraphics.drawCenteredString(this.font,
                        Component.translatable("screen.clanmanage.cost","450-850XP").withStyle(ChatFormatting.GRAY),
                        centerX, centerY - 40, 0xFFFFFF);
            }

            if (currentMenu.equals("unclaimChunk")){
                Component message;
                if (isInOwnTerritory) {
                    if (isTheCore) {
                        message = Component.translatable("screen.clanmanage.unclaim_chunk_core");
                    } else {
                        message = Component.translatable("screen.clanmanage.unclaim_chunk");
                    }
                    saveAndUnclaim.visible = true;
                } else {
                    message = Component.translatable("screen.clanmanage.unclaim_chunk_not_claimed");
                    saveAndUnclaim.visible = false;
                }
                int y = 60;

                List<FormattedCharSequence> wrapped = this.font.split(message, 120);
                for (FormattedCharSequence subLine : wrapped) {

                    pGuiGraphics.drawCenteredString(this.font,subLine, centerX, y, 0xFFFFFFFF);

                    y += this.font.lineHeight;
                }
            }

            if (currentMenu.equals("renameClan")) {
                Component message = Component.translatable("Renaming clan");

                int y = 60;

                List<FormattedCharSequence> wrapped = this.font.split(message, 120);
                for (FormattedCharSequence subLine : wrapped) {

                    pGuiGraphics.drawCenteredString(this.font,subLine, centerX, y, 0xFFFFFFFF);

                    y += this.font.lineHeight;
                }
            }
        }

        for (Renderable renderable : renderables) {
            renderable.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        }

        pGuiGraphics.pose().popPose();

    }

    private void drawMainMenu() {
        this.clearWidgets();
        currentMenu = "main";

        this.addRenderableWidget(changeRank);
        this.addRenderableWidget(kickPlayer);
        this.addRenderableWidget(changePerms);
        this.addRenderableWidget(membersList);

        this.addRenderableWidget(renameClan);

        this.addRenderableWidget(claimTerritory);
        this.addRenderableWidget(unclaimTerritory);
        this.addRenderableWidget(backButton);
    }

    private void drawChangeRankMenu() {
        this.clearWidgets();
        currentMenu = "changeRank";

        int locX = this.width - 135;
        int initialY = (this.height / 2) - 28;

        this.addRenderableWidget(membersList);

        this.addRenderableWidget(new ModButton(
                locX - 25,
                initialY - 73,
                20, 20,
                Component.literal("←"),
                btn -> {
                    drawMainMenu();
                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                20, 20, 1.1f
        ));

        this.addRenderableWidget(new ModButton(
                locX,
                initialY,
                90, 13,
                Component.translatable("screen.clanmanage.make_kit"),
                btn -> {
                    ModPackets.sendToServer(new CtSManageClanMemberPacket("changerank", "kit", selectedMember.getPlayerUUID(), selectedMember.getPlayerMorphName()));
                    this.onClose();
                    },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                80, 20
        ));

        initialY += 15;

        this.addRenderableWidget(new ModButton(
                locX,
                initialY,
                90, 13,
                Component.translatable("screen.clanmanage.make_app"),
                btn -> {
                    ModPackets.sendToServer(new CtSManageClanMemberPacket("changerank", "apprentice", selectedMember.getPlayerUUID(), selectedMember.getPlayerMorphName()));
                    this.onClose();
                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                90, 20
        ));
        initialY += 15;

        this.addRenderableWidget(new ModButton(
                locX,
                initialY,
                90, 13,
                Component.translatable("screen.clanmanage.make_warrior"),
                btn -> {
                    ModPackets.sendToServer(new CtSManageClanMemberPacket("changerank", "warrior", selectedMember.getPlayerUUID(), selectedMember.getPlayerMorphName()));
                    this.onClose();
                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                80, 20
        ));
        initialY += 15;

        this.addRenderableWidget(new ModButton(
                locX,
                initialY,
                90, 13,
                Component.translatable("screen.clanmanage.make_elder"),
                btn -> {
                    ModPackets.sendToServer(new CtSManageClanMemberPacket("changerank", "elder", selectedMember.getPlayerUUID(), selectedMember.getPlayerMorphName()));
                    this.onClose();
                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                80, 20
        ));
        initialY += 15;

        this.addRenderableWidget(new ModButton(
                locX,
                initialY,
                90, 13,
                Component.translatable("screen.clanmanage.make_queen"),
                btn -> {
                    ModPackets.sendToServer(new CtSManageClanMemberPacket("changerank", "queen", selectedMember.getPlayerUUID(), selectedMember.getPlayerMorphName()));
                    this.onClose();
                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                80, 20
        ));
        initialY += 15;

        this.addRenderableWidget(new ModButton(
                locX,
                initialY,
                90, 13,
                Component.translatable("screen.clanmanage.make_med_app"),
                btn -> {
                    ModPackets.sendToServer(new CtSManageClanMemberPacket("changerank", "medapp", selectedMember.getPlayerUUID(), selectedMember.getPlayerMorphName()));
                    this.onClose();
                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                80, 20
        ));
        initialY += 15;

        this.addRenderableWidget(new ModButton(
                locX,
                initialY,
                90, 13,
                Component.translatable("screen.clanmanage.make_medicine"),
                btn -> {
                    ModPackets.sendToServer(new CtSManageClanMemberPacket("changerank", "medicine", selectedMember.getPlayerUUID(), selectedMember.getPlayerMorphName()));
                    this.onClose();
                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                80, 20
        ));
        initialY += 15;

        this.addRenderableWidget(new ModButton(
                locX,
                initialY,
                90, 13,
                Component.translatable("screen.clanmanage.make_deputy"),
                btn -> {
                    ModPackets.sendToServer(new CtSManageClanMemberPacket("changerank", "deputy", selectedMember.getPlayerUUID(), selectedMember.getPlayerMorphName()));
                    this.onClose();
                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                80, 20
        ));

    }

    private void drawChangePermsMenu() {
        this.clearWidgets();
        currentMenu = "changePerms";

        int locX = this.width - 135;
        int initialY = (this.height / 2) -10;

        this.addRenderableWidget(membersList);

        this.addRenderableWidget(new ModButton(
                locX - 25,
                initialY - 90,
                20, 20,
                Component.literal("←"),
                btn -> {
                    drawMainMenu();
                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                20, 20, 1.1f
        ));

        this.addRenderableWidget(new ModButton(
                locX,
                initialY,
                90, 15,
                Component.translatable("screen.clanmanage.make_admin"),
                btn -> {
                    ModPackets.sendToServer(new CtSManageClanMemberPacket("changeperms", "admin", selectedMember.getPlayerUUID(), selectedMember.getPlayerMorphName()));
                    this.onClose();
                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                80, 20
        ));

        initialY += 20;

        this.addRenderableWidget(new ModButton(
                locX,
                initialY,
                90, 15,
                Component.translatable("screen.clanmanage.make_member"),
                btn -> {
                    ModPackets.sendToServer(new CtSManageClanMemberPacket("changeperms", "member", selectedMember.getPlayerUUID(), selectedMember.getPlayerMorphName()));
                    this.onClose();
                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                90, 20
        ));
        initialY += 20;

        this.addRenderableWidget(new ModButton(
                locX,
                initialY,
                90, 15,
                Component.translatable("screen.clanmanage.make_guest"),
                btn -> {
                    ModPackets.sendToServer(new CtSManageClanMemberPacket("changeperms", "guest", selectedMember.getPlayerUUID(), selectedMember.getPlayerMorphName()));
                    this.onClose();
                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                80, 20
        ));


    }

    private void drawConfirmKickMenu() {
        this.clearWidgets();
        currentMenu = "confirmKick";

        int locX = this.width - 135;
        int initialY = (this.height / 2) + 5;

        this.addRenderableWidget(membersList);

        this.addRenderableWidget(new ModButton(
                locX - 25,
                initialY - 105,
                20, 20,
                Component.literal("←"),
                btn -> {
                    drawMainMenu();
                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                20, 20,1.1f
        ));

        initialY += 20;

        this.addRenderableWidget(new ModButton(
                locX,
                initialY,
                90, 20,
                Component.translatable("screen.clanmanage.confirm"),
                btn -> {
                    ModPackets.sendToServer(new CtSManageClanMemberPacket("kick", "none", selectedMember.getPlayerUUID(), selectedMember.getPlayerMorphName()));
                    this.onClose();
                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                80, 20
        ));

        initialY += 30;

        this.addRenderableWidget(new ModButton(
                locX,
                initialY,
                90, 20,
                Component.translatable("screen.clanmanage.cancel"),
                btn -> {
                    drawMainMenu();
                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                90, 20
        ));
    }

    private void drawClaimChunkMenu() {
        this.removeWidget(territoryName);
        this.removeWidget(closeClaimTerritory);
        this.removeWidget(saveAndClaim);
        this.removeWidget(saveAndUnclaim);

        currentMenu = "claimChunk";

        int centerY = this.height / 2;
        int centerX = this.width / 2;


        territoryName = new EditBox(this.font, centerX - 50, centerY,
                100, 20,
                Component.translatable("screen.clanmanage.optional_name"));
        territoryName.setHint(Component.translatable("screen.clanmanage.optional_name").withStyle(ChatFormatting.DARK_GRAY));
        territoryName.setMaxLength(25);

        closeClaimTerritory = new ModButton(
                centerX - 65,
                centerY + 40,
                60, 15,
                Component.translatable("screen.clanmanage.back"),
                btn -> {
                    drawMainMenu();
                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                80, 20
        );

        saveAndClaim = new ModButton(
                centerX + 5,
                centerY + 40,
                60, 15,
                Component.translatable("screen.clanmange.claim"),
                btn -> {
                    saveAndClaimChunk();
                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                80, 20
        );

        this.addRenderableWidget(saveAndClaim);
        this.addRenderableWidget(closeClaimTerritory);
        this.addRenderableWidget(territoryName);

    }

    private void drawRenameMenu() {
//        this.clearWidgets();
        currentMenu = "renameClan";

        renameClan.active = false;
        claimTerritory.active = false;
        unclaimTerritory.active = false;

        int centerY = this.height / 2;
        int centerX = this.width / 2;

        this.addRenderableWidget(new ModButton(
                centerX - 65,
                centerY + 40,
                60, 15,
                Component.translatable("screen.clanmanage.back"),
                btn -> {
                    drawMainMenu();
                    renameClan.active = true;
                    claimTerritory.active = true;
                    unclaimTerritory.active = true;
                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                80, 20
        ));

        this.addRenderableWidget(new ModButton(
                centerX + 5,
                centerY + 40,
                60, 15,
                Component.translatable("screen.clanmanage.save"),
                btn -> {
                    if (renameBox != null) {
                        ModPackets.sendToServer(new RenameClanPacket(renameBox.getValue()));
                        this.onClose();
                    }
                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                80, 20
        ));

        renameBox = new EditBox(this.font, centerX - 50, centerY,
                100, 20,
                Component.empty());

        renameBox.setHint(Component.translatable("screen.clanmanage.new_name").withStyle(ChatFormatting.DARK_GRAY));

        renameBox.setMaxLength(20);

        this.addRenderableWidget(renameBox);
    }

    private void saveAndClaimChunk() {
        if (territoryName != null) {
            String chunkName = territoryName.getValue();

            ModPackets.sendToServer(new CtSClaimTerritory(chunkName));
            this.onClose();
        }
    }

    private void drawUnclaimChunkMenu() {
        this.removeWidget(territoryName);
        this.removeWidget(closeClaimTerritory);
        this.removeWidget(saveAndClaim);
        this.removeWidget(saveAndUnclaim);

        currentMenu = "unclaimChunk";

        int centerY = this.height / 2;
        int centerX = this.width / 2;

        closeClaimTerritory = new ModButton(
                centerX - 65,
                centerY + 40,
                60, 15,
                Component.translatable("screen.clanmanage.back"),
                btn -> {
                    drawMainMenu();
                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                80, 20
        );

        saveAndUnclaim = new ModButton(
                centerX + 5,
                centerY + 40,
                60, 15,
                Component.translatable("screen.clanmanage.unclaim"),
                btn -> {
                    saveAndUnclaimChunk();
                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                80, 20
        );

        this.addRenderableWidget(saveAndUnclaim);
        this.addRenderableWidget(closeClaimTerritory);

    }

    private void saveAndUnclaimChunk() {
        ModPackets.sendToServer(new CtSUnclaimTerritory(currentChunkPos));
        this.onClose();
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (territoryName != null && territoryName.isFocused()) {
            if (territoryName.keyPressed(pKeyCode, pScanCode, pModifiers)) {
                return true;
            }
        }

        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(-800, -800, 0);
        guiGraphics.pose().scale(100f,100f,1f);
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.pose().popPose();
    }

}
