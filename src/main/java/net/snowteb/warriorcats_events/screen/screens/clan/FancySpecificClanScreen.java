package net.snowteb.warriorcats_events.screen.screens.clan;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FastColor;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.ChunkPos;
import net.snowteb.warriorcats_events.WCEClient;
import net.snowteb.warriorcats_events.clan.ClanData;
import net.snowteb.warriorcats_events.clan.ClanObjects;
import net.snowteb.warriorcats_events.client.ClientClanData;
import net.snowteb.warriorcats_events.client.ClientClans;
import net.snowteb.warriorcats_events.client.ClientTerritoryData;
import net.snowteb.warriorcats_events.entity.ModEntities;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCGenetics;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.managers.ClanSymbol;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.c2s.cats.KickNPCPacket;
import net.snowteb.warriorcats_events.network.packet.c2s.clan.*;
import net.snowteb.warriorcats_events.screen.screens.AbstractWCEScreen;
import net.snowteb.warriorcats_events.screen.screens.createmorph.*;
import net.snowteb.warriorcats_events.sound.ModSounds;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class FancySpecificClanScreen extends AbstractWCEScreen {
    private ClientClans.Entry clan;

    int menuMinX;
    int menuMinY;
    int menuMaxX;
    int menuMaxY;

    private FancySelectableButton logsButton;
    private FancySelectableButton membersButton;
    private FancySelectableButton clanButton;
    private FancySelectableButton invitesButton;
    private FancySelectableButton manageButton;

    public void setClan(ClientClans.Entry entry) {
        this.clan = entry;
        init();
    }

    public enum ClanTab {
        CLAN,
        MEMBERS,
        LOGS,
        INVITES,
        MANAGE,

        ;

        public static String toString(ClanTab t) {
            return t.name();
        }

        public static ClanTab fromString(String s) {
            return ClanTab.valueOf(s);
        }
    }
    private ClanTab selectedTab = ClanTab.CLAN;

    private FancySimpleButton backButton;

    private final boolean hasRequestedToJoin;

    private final boolean isMember;

    public FancySpecificClanScreen(ClientClans.Entry entry) {
        this(entry, ClanTab.CLAN);
    }

    public FancySpecificClanScreen(ClientClans.Entry entry, ClanTab tab) {
        super();
        this.clan = entry;
        this.selectedTab = tab;
        this.hasRequestedToJoin = (Minecraft.getInstance().player != null && clan.getClan().joinRequests.stream()
                .anyMatch(joinRequest -> joinRequest.getApplicantUUID().equals(Minecraft.getInstance().player.getUUID())));
        this.isMember = (Minecraft.getInstance().player != null && clan.getClan().members.get(Minecraft.getInstance().player.getUUID()) != null);
        menuY = -200;
    }

    private float menuY;
    private final float targetY = 0;

    @Override
    protected void init() {

        if (this.clan == null) {
            Minecraft.getInstance().setScreen(null);
            return;
        }

        int centerX = width / 2;
        int centerY = height / 2;

        menuMinX = centerX - 150;
        menuMinY = centerY - 95;
        menuMaxX = centerX + 150;
        menuMaxY = centerY + 105;

        {
            int buttonsWidth = 50;
            int buttonsHeight = 17;

            int buttonGap = 10;
            int totalButtonWidth = (buttonsWidth * 5) + (buttonGap * 4);

            int buttonX = centerX - totalButtonWidth / 2;
            int buttonY = centerY - 115;

            int buttonEscalate = buttonsWidth + buttonGap;

            logsButton = new FancySelectableButton(
                    buttonsWidth, buttonsHeight,
                    buttonX, buttonY,
                    Component.translatable("screen.clan.logs"),
                    b -> {
                        selectMenuTab(b, ClanTab.LOGS);

                        drawLogsSheet();
                    }, 0.8f, clan.getClan().color, "", true
            );

            buttonX += buttonEscalate;

            membersButton = new FancySelectableButton(
                    buttonsWidth, buttonsHeight,
                    buttonX, buttonY,
                    Component.translatable("screen.clan.members"),
                    b -> {
                        selectMenuTab(b, ClanTab.MEMBERS);

                        drawMembersSheet();
                    }, 0.8f, clan.getClan().color, "", true
            );

            buttonX += buttonEscalate;

            clanButton = new FancySelectableButton(
                    buttonsWidth, buttonsHeight,
                    buttonX, buttonY,
                    Component.translatable("screen.clan"),
                    b -> {
                        selectMenuTab(b, ClanTab.CLAN);

                        drawClanSheet();
                    }, 1f, clan.getClan().color, "", true
            );

            buttonX += buttonEscalate;

            invitesButton = new FancySelectableButton(
                    buttonsWidth, buttonsHeight,
                    buttonX, buttonY,
                    Component.translatable("screen.clan.requests"),
                    b -> {
                        selectMenuTab(b, ClanTab.INVITES);

                        drawRequestsSheet();
                    }, 0.8f, clan.getClan().color, "", true
            );

            buttonX += buttonEscalate;

            manageButton = new FancySelectableButton(
                    buttonsWidth, buttonsHeight,
                    buttonX, buttonY,
                    Component.translatable("screen.clan.manage"),
                    b -> {
                        selectMenuTab(b, ClanTab.MANAGE);

                        drawManageSheet();
                    }, 0.8f, clan.getClan().color, "", true
            );

            backButton = new FancySimpleButton(
                    40,15,
                    centerX - 20, centerY + 100,
                    Component.translatable("screen.clan.back"),
                    but -> {
                        WCEClient.playLocalSound(ModSounds.MENU_OPEN.get(), SoundSource.NEUTRAL, 0.8f,1.3f);
                        Minecraft.getInstance().setScreen(new FancyClanListScreen(false));
                    },
                    0.8f, true
            );
        }


        switch (selectedTab) {
            case MEMBERS -> {
                selectMenuTab(membersButton, ClanTab.MEMBERS);
                drawMembersSheet();
            }
            case LOGS -> {
                selectMenuTab(logsButton, ClanTab.LOGS);
                drawLogsSheet();
            }
            case INVITES -> {
                selectMenuTab(invitesButton, ClanTab.INVITES);
                drawRequestsSheet();
            }
            case MANAGE -> {
                selectMenuTab(manageButton, ClanTab.MANAGE);
                drawManageSheet();
            }
            default -> {
                selectMenuTab(clanButton, ClanTab.CLAN);
                drawClanSheet();
            }
        }


    }

    private void drawClanSheet() {
        drawMainButtons();

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        int menuWidth = menuMaxX - menuMinX;
        int menuHeight = menuMaxY - menuMinY;

        FancySubRenderablesScrollSquare square = new FancySubRenderablesScrollSquare(
                menuMinX, menuMinY,
                menuWidth, menuHeight
        );
        square.setRenderSquare(false);

        int y = centerY;

        FancyLambdaRenderable symbolAndText = new FancyLambdaRenderable(centerX, y, 0,
                (pGuiGraphics, mouseX, mouseY, pPartialTick) -> {

                    if (clan.getClan().name != null && clan.getClan().color != 0) {
                        pGuiGraphics.pose().pushPose();
                        pGuiGraphics.pose().translate(0,-90,0);
                        float scale = 1.2f;
                        pGuiGraphics.pose().scale(scale, scale, scale);
                        pGuiGraphics.drawCenteredString(Minecraft.getInstance().font,
                                clan.getClan().name,
                                0,0, clan.getClan().color);
                        pGuiGraphics.pose().popPose();
                    }

                    pGuiGraphics.hLine(-70, 70, -78, 0xFFFFFFFF);

                    pGuiGraphics.pose().pushPose();
                    pGuiGraphics.pose().translate(0, -76, 0);
                    float uuidScale = 0.475f;
                    pGuiGraphics.pose().scale(uuidScale,uuidScale,uuidScale);
                    pGuiGraphics.drawCenteredString(Minecraft.getInstance().font,
                            Component.literal(clan.getClan().clanUUID.toString()).withStyle(ChatFormatting.GRAY),
                            0, 0, 0x99FFFFFF);
                    pGuiGraphics.pose().popPose();

                    if (clan.getClan().leaderName != null) {
                        pGuiGraphics.pose().pushPose();
                        pGuiGraphics.pose().translate(0,-70,0);
                        float scale = 0.8f;
                        pGuiGraphics.pose().scale(scale, scale, scale);
                        pGuiGraphics.drawCenteredString(Minecraft.getInstance().font,
                                Component.translatable("screen.clanmanage.leader", clan.getClan().leaderName),
                                0, 0, 0xFFFFFF);
                        pGuiGraphics.pose().popPose();
                    }

                    renderClanSymbol(pGuiGraphics, 0, 0, 1f, -1);

                    drawCenteredMultilineString(pGuiGraphics,
                            Component.literal(clan.getClan().clanBioSentence).withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC),
                            0,15, 140, 0xFFFFFFFF);

                    pGuiGraphics.hLine((-menuWidth/2) + 10, (menuWidth/2) - 10, 95, 0xFFFFFFFF);

                });

        symbolAndText.setHeight(135);

        square.addWidget(symbolAndText);

        y += 105;

        FancyStringWidget clanInfoTitle = new FancyStringWidget(
                Component.translatable("screen.clan.information"),
                menuMinX, menuMaxX, y, 1.1f, true, 10
        );

        square.addWidget(clanInfoTitle);

        y += 30;


        FancyStringWidget statsTitle = new FancyStringWidget(
                Component.translatable("screen.clan.stats"),
                menuMinX, menuMinX + 100, y, 0.9f, true, 10
        );

        FancyStringWidget leaderTitle = new FancyStringWidget(
                Component.translatable("screen.clan.leader"),
                menuMaxX - 120, menuMaxX - 20, y, 0.9f, true, 10
        );

        square.addWidget(statsTitle);


        y += 20;

        FancyLambdaRenderable clanStatsText = new FancyLambdaRenderable(menuMinX + 10, y, 0,
                (pGuiGraphics, mouseX, mouseY, pPartialTick) -> {

                    int npcCount = clan.getClan().clanCats.size();
                    int playerCount = clan.getClan().members.size();
                    int territorySize = -1;
                    var territory = ClientTerritoryData.CLIENT_TERRITORIES.get(clan.getClan().clanUUID);
                    if (territory != null) {
                        territorySize = territory.claimedTerritory().size();
                    }

                    pGuiGraphics.pose().pushPose();
                    float scale = 0.8f;
                    pGuiGraphics.pose().scale(scale, scale, scale);

                    int yText = 0;
                    int xText = 10;
                    int textSpacing = 15;

                    pGuiGraphics.drawString(this.font,
                            Component.translatable("screen.clan.member_count", npcCount + playerCount),
                            xText, yText,
                            0xFFFFFFFF);

                    yText += textSpacing;

                    pGuiGraphics.drawString(this.font,
                            Component.translatable("screen.clan.players", playerCount),
                            xText, yText,
                            0xFFFFFFFF);

                    yText += textSpacing;

                    pGuiGraphics.drawString(this.font,
                            Component.translatable("screen.clan.npcs", npcCount),
                            xText, yText,
                            0xFFFFFFFF);

                    yText += textSpacing;

                    if (territorySize != -1) {
                        pGuiGraphics.drawString(this.font,
                                Component.translatable("screen.clan.claimed_chunks", territorySize),
                                xText, yText,
                                0xFFFFFFFF);

                        yText += textSpacing;
                    }


                    pGuiGraphics.pose().popPose();
                });

        clanStatsText.setHeight(50);
        square.addWidget(clanStatsText);

        ClanObjects.Player leader = (clan.getPlayers().values().stream()
                .filter(player -> {
                    ClanData.Clan.Member member = clan.getMember(player.getUUID());
                    if (member != null) {
                        return member.getPermissions() == ClanData.ClanPermissions.OWNER;
                    }
                    return false;
                }).findFirst()).orElse(null);

        if (leader != null) {
            square.addWidget(leaderTitle);

            int subSquareWidth = menuWidth/3;

            FancySubRenderablesSquare subSquare = new FancySubRenderablesSquare(
                    menuMaxX - 20 - subSquareWidth, y,
                    subSquareWidth, menuHeight/2
            );

            y += subSquare.getHeight() + 5;

            FancyStringWidget leaderName = new FancyStringWidget(
                    Component.literal(leader.getMorphName()),
                    subSquare.getLeft() + 10, subSquare.getRight() - 10,
                    subSquare.getTop() + 10,
                    1.0f, true,
                    70
            );
            subSquare.addWidget(leaderName);

            FancyLambdaRenderable leaderLambda = new FancyLambdaRenderable(
                    subSquare.getLeft() + subSquare.getWidth()/2, subSquare.getTop() + 25, 0,
                    (pGuiGraphics, mouseX, mouseY, pPartialTick) -> {

                        pGuiGraphics.pose().pushPose();
                        float scale = 0.8f;
                        pGuiGraphics.pose().scale(scale, scale, scale);

                        pGuiGraphics.drawCenteredString(
                                this.font,
                                leader.getUsername(),
                                0,0,
                                0xFF888888
                                );

                        WCGenetics.PackedGeneticData data = leader.getGenetics();
                        if (data != null) {
                            WCatEntity cat = new WCatEntity(ModEntities.WCAT.get(), Minecraft.getInstance().level);

                            cat.setPlayerBoundUuid(UUID.nameUUIDFromBytes(ModEntities.WCAT.get().toString().getBytes()));
                            cat.setShowMorphName(false);

                            cat.setGender(1);
                            cat.setAge(0);
                            cat.setAgeInMoons(12);

                            cat.setOnGeneticalSkin(data.onGeneticalSkin);
                            cat.getGeneticsModule().setGenetics(data.genetics);
                            cat.getGeneticsModule().setGeneticalVariants(data.variants);
                            cat.getGeneticsModule().setChimeraGenetics(data.chimerasGenetics);
                            cat.getGeneticsModule().setGeneticalVariantsChimera(data.chimeraVariants);
                            cat.setVariant(data.morphSkin);
                            cat.setOnGround(true);
                            cat.setYRot(0);
                            cat.yHeadRot = 0;
                            cat.yBodyRot = 0;

                            pGuiGraphics.pose().pushPose();
                            int yCatTranslate = 70;
                            pGuiGraphics.pose().translate(0,yCatTranslate,0);
                            float scale2 = 6f;
                            pGuiGraphics.pose().scale(scale2, scale2, scale2);
                            InventoryScreen.renderEntityInInventoryFollowsMouse(pGuiGraphics,
                                    0,0,10,
                                    -mouseX, -(mouseY - yCatTranslate*scale + 20),
                                    cat
                            );
                            pGuiGraphics.pose().popPose();

                        }

                        pGuiGraphics.pose().popPose();
                    });

            subSquare.addWidget(leaderLambda);

            String clanMsgString = clan.getClan().leaderClanMessage;
            if (!clanMsgString.isEmpty()) {
                String finalMsgString = "\"" + clanMsgString + "\"";

                float scale = 0.8f;
                int maxWidth = 100;
                Component text = Component.literal(finalMsgString)
                        .withStyle(ChatFormatting.ITALIC)
                        .withStyle(ChatFormatting.GRAY);
                List<FormattedCharSequence> textLines = this.font.split(text, (int) (maxWidth/scale));

                FancyLambdaRenderable clanMessage = new FancyLambdaRenderable(
                        subSquare.getLeft() + subSquareWidth / 2, y, 0,
                        (pGuiGraphics, mouseX, mouseY, pPartialTick) -> {

                            pGuiGraphics.pose().pushPose();

                            pGuiGraphics.pose().scale(scale, scale, scale);


                            int clanMsgY = 0;
                            for (FormattedCharSequence line : textLines) {
                                pGuiGraphics.drawCenteredString(this.font, line, 0, clanMsgY, 0xFFFFFFFF);
                                clanMsgY += font.lineHeight;
                            }

                            pGuiGraphics.pose().popPose();
                        }
                );

                clanMessage.setHeight(textLines.size()*9 + 10);

                square.addWidget(clanMessage);
            } else {
                square.addHeight(10);
            }

            square.addWidget(subSquare);


        }

        FancyLambdaRenderable hLine3 = new FancyLambdaRenderable(
                centerX, square.getContentBottom(), 0,
                (pGuiGraphics, mouseX, mouseY, pPartialTick) -> {
                    pGuiGraphics.hLine((-menuWidth/2) + 10, (menuWidth/2) - 10, 0, 0xFFFFFFFF);
                });
        square.addWidget(hLine3);

        y += 30;

        if (isMember) {
            FancyStringWidget memberShipTitle = new FancyStringWidget(
                    Component.translatable("screen.clan.membership"),
                    menuMinX, menuMaxX, y, 1.1f, true, 10
            );

            y += 20;

            square.addWidget(memberShipTitle);

            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {

                ClanObjects.Player clanPlayer = clan.getPlayer(player.getUUID());
                if (clanPlayer != null) {
                    Component nameplate = Component.empty()
                            .append(clanPlayer.getMorphName())
                            .append("(")
                            .append(clanPlayer.getUsername())
                            .append(")");



                    y += 20;

                    ClanData.Clan.Member clanMember = clan.getMember(clanPlayer.getUUID());

                    if (clanMember != null) {

                        FancyStringWidget detailsTitle = new FancyStringWidget(
                                Component.translatable("screen.clan.details"),
                                menuMinX, menuMinX + 100, y, 0.9f, true, 10
                        );

                        square.addWidget(detailsTitle);

                        FancyStringWidget playerNameTitle = new FancyStringWidget(
                                nameplate,
                                menuMaxX - 120, menuMaxX - 20, y, 0.9f, true, 10
                        );
                        square.addWidget(playerNameTitle);

                        y += 20;

                        FancyLambdaRenderable playerSettingsText = new FancyLambdaRenderable(menuMinX + 10, y, 0,
                                (pGuiGraphics, mouseX, mouseY, pPartialTick) -> {
                                    pGuiGraphics.pose().pushPose();
                                    float scale = 0.8f;
                                    pGuiGraphics.pose().scale(scale, scale, scale);

                                    int yText = 0;
                                    int xText = 10;
                                    int textSpacing = 15;

                                    pGuiGraphics.drawString(this.font,
                                            Component.translatable("screen.clanmanage.rank", clanMember.getRank().getString()),
                                            xText, yText,
                                            0xFFFFFFFF);

                                    yText += textSpacing;

                                    pGuiGraphics.drawString(this.font,
                                            Component.translatable("screen.clanmanage.perms", clanMember.getPermissions().getString()),
                                            xText, yText,
                                            0xFFFFFFFF);

                                    yText += textSpacing;

                                    pGuiGraphics.pose().popPose();
                                });

                        square.addWidget(playerSettingsText);

                        int subSquareWidth = menuWidth/3;

                        FancySubRenderablesSquare subSquare = new FancySubRenderablesSquare(
                                menuMaxX - 20 - subSquareWidth, y,
                                subSquareWidth, menuHeight/2
                        );

                        y += subSquare.getHeight() + 10;

                        FancyStringWidget playerName = new FancyStringWidget(
                                Component.literal(clanPlayer.getMorphName()),
                                subSquare.getLeft() + 10, subSquare.getRight() - 10,
                                subSquare.getTop() + 10,
                                1.0f, true,
                                70
                        );
                        subSquare.addWidget(playerName);

                        FancyLambdaRenderable playerLambda = new FancyLambdaRenderable(
                                subSquare.getLeft() + subSquare.getWidth()/2, subSquare.getTop() + 25, 0,
                                (pGuiGraphics, mouseX, mouseY, pPartialTick) -> {

                                    pGuiGraphics.pose().pushPose();
                                    float scale = 0.8f;
                                    pGuiGraphics.pose().scale(scale, scale, scale);

                                    WCGenetics.PackedGeneticData data = clanPlayer.getGenetics();
                                    if (data != null) {
                                        WCatEntity cat = new WCatEntity(ModEntities.WCAT.get(), Minecraft.getInstance().level);

                                        cat.setPlayerBoundUuid(UUID.nameUUIDFromBytes(ModEntities.WCAT.get().toString().getBytes()));
                                        cat.setShowMorphName(false);

                                        cat.setGender(1);
                                        cat.setAge(0);
                                        cat.setAgeInMoons(12);

                                        cat.setOnGeneticalSkin(data.onGeneticalSkin);
                                        cat.getGeneticsModule().setGenetics(data.genetics);
                                        cat.getGeneticsModule().setGeneticalVariants(data.variants);
                                        cat.getGeneticsModule().setChimeraGenetics(data.chimerasGenetics);
                                        cat.getGeneticsModule().setGeneticalVariantsChimera(data.chimeraVariants);
                                        cat.setVariant(data.morphSkin);
                                        cat.setOnGround(true);
                                        cat.setYRot(0);
                                        cat.yHeadRot = 0;
                                        cat.yBodyRot = 0;

                                        pGuiGraphics.pose().pushPose();
                                        int yCatTranslate = 70;
                                        pGuiGraphics.pose().translate(0,yCatTranslate,0);
                                        float scale2 = 6f;
                                        pGuiGraphics.pose().scale(scale2, scale2, scale2);
                                        InventoryScreen.renderEntityInInventoryFollowsMouse(pGuiGraphics,
                                                0,0,10,
                                                -mouseX, -(mouseY - yCatTranslate*scale + 20),
                                                cat
                                        );
                                        pGuiGraphics.pose().popPose();

                                    }

                                    pGuiGraphics.pose().popPose();
                                });

                        subSquare.addWidget(playerLambda);

                        square.addWidget(subSquare);
                    }
                }

                FancyLambdaRenderable shadow = new FancyLambdaRenderable(
                        (pGuiGraphics, mouseX, mouseY, pPartialTick) -> {
                            pGuiGraphics.pose().pushPose();
                            pGuiGraphics.pose().translate(0,0,1500);
                            pGuiGraphics.fill(0,0, width, height, 0x66000000);
                            pGuiGraphics.pose().popPose();
                        });
                int leaveButtonWidth = 150;
                int leaveButtonHeight = 20;
                FancySimpleButton leaveButton = new FancySimpleButton(
                        leaveButtonWidth, leaveButtonHeight,
                        (square.getLeft() + square.getWidth()/2) - leaveButtonWidth/2, y,
                        Component.translatable("screen.clan.leave"),
                        but -> {
                            toggleSubMenu(square, shadow, true);
                            FancySubRenderablesSquare subSquare = createSubSquare(20,-60);
                            addTitleToSquare(subSquare,
                                    Component.translatable("screen.clan.leave_clan_confirmation",
                                            Component.literal(clan.getClan().name).withStyle(Style.EMPTY.withColor(clan.getClan().color)))
                            );

                            int subSquareCenterX = subSquare.getLeft() + subSquare.getWidth()/2;
                            int subSquareCenterY = subSquare.getTop() + subSquare.getHeight()/2;

                            squareConfirmationButtons(subSquare,
                                    cancel -> {
                                        toggleSubMenu(square, shadow, false);
                                        this.removeWidget(subSquare);
                                    },
                                    confirm -> {
                                        this.enqueue(() -> {
                                            player.connection.sendCommand("wce clan leave");
                                            this.onClose();
                                        });
                                        toggleSubMenu(square, shadow, false);
                                        this.removeWidget(subSquare);
                                    }
                            );



                            this.addRenderableWidget(subSquare);
                        }, 0.8f, true
                );

                square.addWidget(leaveButton);


                FancyLambdaRenderable hLine4 = new FancyLambdaRenderable(
                        centerX, square.getContentBottom() + 10, 0,
                        (pGuiGraphics, mouseX, mouseY, pPartialTick) -> {
                            pGuiGraphics.hLine((-menuWidth/2) + 10, (menuWidth/2) - 10, 0, 0xFFFFFFFF);
                        });
                square.addWidget(hLine4);

                square.addHeight(10);

            }
        }

        this.addRenderableWidget(square);
    }

    private void renderClanSymbol(GuiGraphics pGuiGraphics, int x, int y, float scale, int color) {

        float a = ((color >> 24) & 0xFF) / 255f;
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;

        pGuiGraphics.pose().pushPose();
        scale = Math.max(scale, 0.1f);
        pGuiGraphics.pose().translate((-25 + x)/scale, (-50 + y)/scale, 0);
        pGuiGraphics.pose().scale(scale, scale, scale);
        pGuiGraphics.setColor(r, g, b, a);
        pGuiGraphics.blit(
                ClanSymbol.SPRITE,
                0, 0,
                ClanSymbol.getSymbolCoordinate(clan.getClan().clanSymbolIndex), 0,
                ClanSymbol.SYMBOL_SIZE, ClanSymbol.SYMBOL_SIZE,
                ClanSymbol.SYMBOL_SIZE * ClanSymbol.SYMBOLS_AMOUNT,
                ClanSymbol.SYMBOL_SIZE
        );
        pGuiGraphics.setColor(1f, 1f, 1f, 1f);
        pGuiGraphics.pose().popPose();
    }

    private void drawMembersSheet() {
        drawMainButtons();

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        int menuWidth = menuMaxX - menuMinX;
        int menuHeight = menuMaxY - menuMinY;

        FancySubRenderablesScrollSquare square = new FancySubRenderablesScrollSquare(
                menuMinX, menuMinY,
                menuWidth, menuHeight
        );
        square.setRenderSquare(false);

        int y = menuMinY + 10;

        FancyStringWidget title = new FancyStringWidget(
                Component.translatable("screen.clan.members"),
                menuMinX, menuMaxX, y,
                1.2f,
                true,
                0
        );

        square.addWidget(title);

        y+=20;

        int listWidth = 110;
        int listHeight = 120;

        {
            FancyLambdaRenderable hLine1 = new FancyLambdaRenderable(
                    centerX, y, 0,
                    (pGuiGraphics, mouseX, mouseY, pPartialTick) -> {
                        pGuiGraphics.hLine((-menuWidth / 2) + 10, (menuWidth / 2) - 10, 0, 0xFFFFFFFF);
                    });

            square.addWidget(hLine1);

            y += 10;

            FancyStringWidget playersTitle = new FancyStringWidget(
                    Component.translatable("screen.clan.players", clan.getClan().members.size()),
                    menuMinX, menuMaxX, y,
                    1.0f,
                    true,
                    0
            );

            square.addWidget(playersTitle);

            y += 20;



            FancyMemberScrollList playersList = new FancyMemberScrollList(
                    this.minecraft,
                    listWidth, listHeight,
                    y, y + listHeight,
                    20
            );
            playersList.setLeftPos(menuMinX + 10);
            playersList.setRenderBackground(false);
            playersList.setRenderTopAndBottom(false);

            FancyEditBox playersFilter = new FancyEditBox(
                    this.font,
                    playersList.getLeft(), y - 15,
                    (int) (playersList.getWidth()*0.6), 10
                    );
            playersFilter.setHint(
                    Component.empty()
                            .append("<")
                            .append(Component.translatable("screen.clan.filter"))
                            .append(">")
                            .withStyle(ChatFormatting.GRAY)
            );
            playersFilter.setResponder(txt -> {
                playersList.setScrollAmount(0);
                playersList.filter(memberEntry -> memberEntry.getName().toLowerCase().contains(txt.toLowerCase()));
            });

            square.addWidget(playersFilter);

            FancySubRenderablesSquare subPlayerSquare = new FancySubRenderablesSquare(
                    menuMaxX - 15 - (listWidth - 10), y,
                    (listWidth - 10), listHeight);

            for (ClanObjects.Player player : clan.getPlayers().values()) {
                playersList.addMember(
                        player.getUUID(),
                        Component.literal(player.getMorphName()),
                        player.getGenetics(),
                        1,
                        () -> {
                            subPlayerSquare.clearWidgets();
                            square.removeWidget(subPlayerSquare);

                            FancyStringWidget playerNameplate = new FancyStringWidget(
                                    Component.literal(player.getMorphName()),
                                    subPlayerSquare.getLeft(), subPlayerSquare.getRight(),
                                    subPlayerSquare.getOriginalYPos0() + 5,
                                    0.9f, true, 0
                            );

                            subPlayerSquare.addWidget(playerNameplate);

                            FancySimpleButton closeButton = new FancySimpleButton(
                                    15, 8,
                                    subPlayerSquare.getLeft(), subPlayerSquare.getOriginalYPos0() - 8,
                                    Component.literal("×"),
                                    button -> {
                                        subPlayerSquare.clearWidgets();
                                        square.removeWidget(subPlayerSquare);
                                    }, 0.8f
                            );
                            subPlayerSquare.addWidget(closeButton);

                            FancyLambdaRenderable playerRenderable = new FancyLambdaRenderable(
                                    subPlayerSquare.getLeft() + subPlayerSquare.getWidth() / 2, subPlayerSquare.getOriginalYPos0(), 0,
                                    (pGuiGraphics, mouseX, mouseY, pPartialTick) -> {
                                        pGuiGraphics.pose().pushPose();
                                        float scale = 0.6f;
                                        pGuiGraphics.pose().scale(scale, scale, scale);
                                        int textY = 30;

                                        pGuiGraphics.drawCenteredString(this.font, player.getUsername(), 0, textY, 0xFFaaaaaa);

                                        int yCatTranslate = 110;
                                        {
                                            WCatEntity cat = new WCatEntity(ModEntities.WCAT.get(), Minecraft.getInstance().level);

                                            cat.setPlayerBoundUuid(UUID.nameUUIDFromBytes(ModEntities.WCAT.get().toString().getBytes()));
                                            cat.setShowMorphName(false);
                                            cat.setGender(1);
                                            cat.setAge(0);
                                            cat.setAgeInMoons(12);
                                            WCGenetics.PackedGeneticData data = player.getGenetics();
                                            cat.setOnGeneticalSkin(data.onGeneticalSkin);
                                            cat.getGeneticsModule().setGenetics(data.genetics);
                                            cat.getGeneticsModule().setGeneticalVariants(data.variants);
                                            cat.getGeneticsModule().setChimeraGenetics(data.chimerasGenetics);
                                            cat.getGeneticsModule().setGeneticalVariantsChimera(data.chimeraVariants);
                                            cat.setVariant(data.morphSkin);
                                            cat.setOnGround(true);
                                            cat.setYRot(0);
                                            cat.yHeadRot = 0;
                                            cat.yBodyRot = 0;

                                            pGuiGraphics.pose().pushPose();
                                            pGuiGraphics.pose().translate(0, yCatTranslate, 0);
                                            float scale2 = 8f;
                                            pGuiGraphics.pose().scale(scale2, scale2, scale2);
                                            InventoryScreen.renderEntityInInventoryFollowsMouse(pGuiGraphics,
                                                    0, 0, 10,
                                                    -mouseX, -(mouseY - yCatTranslate * scale + 20),
                                                    cat
                                            );
                                            pGuiGraphics.pose().popPose();
                                        }

                                        textY += yCatTranslate - 10;
                                        int textSpacing = 11;

                                        ClanData.Clan.Member member = clan.getMember(player.getUUID());
                                        if (member != null) {
                                            pGuiGraphics.drawCenteredString(this.font, player.getUsername(), 0, textY, 0xFFaaaaaa);
                                            textY += textSpacing;
                                            pGuiGraphics.drawCenteredString(this.font, member.getPermissions().getString(), 0, textY, 0xFFaaaaaa);
                                            textY += textSpacing;
                                            pGuiGraphics.drawCenteredString(this.font, member.getRank().getString(), 0, textY, 0xFFaaaaaa);
                                        }
                                        pGuiGraphics.pose().popPose();
                                    });

                            subPlayerSquare.addWidget(playerRenderable);


                            square.addWidget(subPlayerSquare);
                        }
                );
            }

            square.addWidget(playersList);
        }

        y = square.getContentBottom() + 10;

        {
            FancyLambdaRenderable hLine2 = new FancyLambdaRenderable(
                    centerX, y, 0,
                    (pGuiGraphics, mouseX, mouseY, pPartialTick) -> {
                        pGuiGraphics.hLine((-menuWidth / 2) + 10, (menuWidth / 2) - 10, 0, 0xFFFFFFFF);
                    });

            square.addWidget(hLine2);

            y += 10;

            FancyStringWidget npcsTitle = new FancyStringWidget(
                    Component.translatable("screen.clan.npcs", clan.getClan().clanCats.size()),
                    menuMinX, menuMaxX, y,
                    1.0f,
                    true,
                    0
            );

            square.addWidget(npcsTitle);

            y += 20;

            FancyMemberScrollList npcList = new FancyMemberScrollList(
                    this.minecraft,
                    listWidth, listHeight,
                    y, y + listHeight,
                    20
            );
            npcList.setLeftPos(menuMinX + 10);
            npcList.setRenderBackground(false);
            npcList.setRenderTopAndBottom(false);

            FancyEditBox npcFilter = new FancyEditBox(
                    this.font,
                    npcList.getLeft(), y - 15,
                    (int) (npcList.getWidth()*0.6), 10
            );
            npcFilter.setHint(
                    Component.empty()
                            .append("<")
                            .append(Component.translatable("screen.clan.filter"))
                            .append(">")
                            .withStyle(ChatFormatting.GRAY)
            );
            npcFilter.setResponder(txt -> {
                npcList.setScrollAmount(0);
                npcList.filter(memberEntry -> memberEntry.getName().toLowerCase().contains(txt.toLowerCase()));
            });

            square.addWidget(npcFilter);

            FancySubRenderablesSquare subNPCSquare = new FancySubRenderablesSquare(
                    menuMaxX - 15 - (listWidth - 10), y,
                    (listWidth - 10), listHeight);

            for (ClanData.ClanCat clanCat : clan.getClan().clanCats.values()) {
                WCGenetics.PackedGeneticData data = new WCGenetics.PackedGeneticData(
                        clanCat.genetics, clanCat.variants,
                        clanCat.chimeraGenetics, clanCat.chimeraVariants,
                        clanCat.onGeneticalSkin, clanCat.catVariant
                );
                npcList.addMember(
                        clanCat.catUUID,
                        clanCat.catName,
                        data,
                        clanCat.catGenderValue,
                        () -> {
                            subNPCSquare.clearWidgets();
                            square.removeWidget(subNPCSquare);

                            TextColor txtColor = clanCat.catName.getStyle().getColor();
                            int color = txtColor != null ? (txtColor.getValue() | 0xFF000000) : -1;

                            FancyStringWidget npcNamePlate = new FancyStringWidget(
                                    clanCat.catName,
                                    subNPCSquare.getLeft(), subNPCSquare.getRight(),
                                    subNPCSquare.getOriginalYPos0() + 5,
                                    color,
                                    0.9f, true, 0
                            );

                            subNPCSquare.addWidget(npcNamePlate);

                            FancySimpleButton closeButton = new FancySimpleButton(
                                    15, 8,
                                    subNPCSquare.getLeft(), subNPCSquare.getOriginalYPos0() - 8,
                                    Component.literal("×"),
                                    button -> {
                                        subNPCSquare.clearWidgets();
                                        square.removeWidget(subNPCSquare);
                                    }, 0.8f
                            );
                            subNPCSquare.addWidget(closeButton);

                            FancyLambdaRenderable npcRenderable = new FancyLambdaRenderable(
                                    subNPCSquare.getLeft() + subNPCSquare.getWidth() / 2, subNPCSquare.getOriginalYPos0(), 0,
                                    (pGuiGraphics, mouseX, mouseY, pPartialTick) -> {
                                        pGuiGraphics.pose().pushPose();
                                        float scale = 0.6f;
                                        pGuiGraphics.pose().scale(scale, scale, scale);
                                        int textY = 30;

                                        int yCatTranslate = 110;
                                        {
                                            WCatEntity cat = new WCatEntity(ModEntities.WCAT.get(), Minecraft.getInstance().level);

                                            cat.setPlayerBoundUuid(UUID.nameUUIDFromBytes(ModEntities.WCAT.get().toString().getBytes()));
                                            cat.setShowMorphName(false);
                                            cat.setAge(0);
                                            cat.setAgeInMoons(12);
                                            cat.setGender(clanCat.catGenderValue);
                                            cat.setVariant(data.morphSkin);
                                            cat.setOnGeneticalSkin(data.onGeneticalSkin);
                                            cat.getGeneticsModule().setGenetics(data.genetics);
                                            cat.getGeneticsModule().setChimeraGenetics(data.chimerasGenetics);
                                            cat.getGeneticsModule().setGeneticalVariants(data.variants);
                                            cat.getGeneticsModule().setGeneticalVariantsChimera(data.chimeraVariants);
                                            cat.setOnGround(true);
                                            cat.setYRot(0);
                                            cat.yHeadRot = 0;
                                            cat.yBodyRot = 0;

                                            pGuiGraphics.pose().pushPose();
                                            pGuiGraphics.pose().translate(0, yCatTranslate, 0);
                                            float scale2 = 8f;
                                            pGuiGraphics.pose().scale(scale2, scale2, scale2);
                                            InventoryScreen.renderEntityInInventoryFollowsMouse(pGuiGraphics,
                                                    0, 0, 10,
                                                    -mouseX, -(mouseY - yCatTranslate * scale + 20),
                                                    cat
                                            );
                                            pGuiGraphics.pose().popPose();
                                        }

                                        textY += yCatTranslate - 10;
                                        int textSpacing = 11;

                                        {
                                            pGuiGraphics.drawCenteredString(this.font, clanCat.catRank, 0, textY, 0xFFaaaaaa);
                                            textY += textSpacing;
                                            pGuiGraphics.drawCenteredString(this.font, clanCat.catGender, 0, textY, 0xFFaaaaaa);
                                            textY += textSpacing;
                                            {
                                                List<FormattedCharSequence> list = font.split(clanCat.catParents, (int) ((int) (subNPCSquare.getWidth() /scale) * 0.8f));
                                                for (FormattedCharSequence line : list) {
                                                    pGuiGraphics.drawCenteredString(this.font, line, 0, textY, 0xFFaaaaaa);
                                                    textY += font.lineHeight;
                                                }
                                                textY += 2;
                                            }
                                            pGuiGraphics.drawCenteredString(this.font, clanCat.catAge, 0, textY, 0xFFaaaaaa);

                                        }
                                        pGuiGraphics.pose().popPose();
                                    });

                            subNPCSquare.addWidget(npcRenderable);


                            square.addWidget(subNPCSquare);
                        }
                );
            }

            square.addWidget(npcList);

        }

        this.addRenderableWidget(square);

    }

    private void drawLogsSheet() {
        drawMainButtons();

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        int menuWidth = menuMaxX - menuMinX;
        int menuHeight = menuMaxY - menuMinY;

        FancySubRenderablesScrollSquare square = new FancySubRenderablesScrollSquare(
                menuMinX, menuMinY,
                menuWidth, menuHeight
        );
        square.setRenderSquare(false);

        int y = menuMinY + 10;

        FancyStringWidget title = new FancyStringWidget(
                Component.translatable("screen.clan.logs"),
                menuMinX, menuMaxX, y,
                1.2f,
                true,
                0
        );

        square.addWidget(title);


        y+=20;

        FancyLambdaRenderable hLine1 = new FancyLambdaRenderable(
                centerX, y, 0,
                (pGuiGraphics, mouseX, mouseY, pPartialTick) -> {
                    pGuiGraphics.hLine((-menuWidth / 2) + 10, (menuWidth / 2) - 10, 0, 0xFFFFFFFF);
                });

        square.addWidget(hLine1);

        y += 25;

        if (!isMember && !clan.getClan().publicClanLogs) {
            FancyLambdaRenderable logsPrivateText = new FancyLambdaRenderable(
                    menuMinX + menuWidth/2, menuMinY + menuHeight/2, 0,
                    (pGuiGraphics, mouseX, mouseY, pPartialTick) -> {

                        List<FormattedCharSequence> lines = this.font.split(
                                Component.translatable("screen.clan.logs_private_msg").withStyle(ChatFormatting.GRAY),
                                menuWidth - 50);

                        int txtY = 0;
                        for (FormattedCharSequence line : lines) {
                            pGuiGraphics.drawCenteredString(this.font,
                                    line,
                                    0,txtY,0xFFFFFFFF);
                            txtY += font.lineHeight;
                        }

                    }
            );
            square.addWidget(logsPrivateText);

            this.addRenderableWidget(square);
            return;
        }

        int squareSpacing = 15;
        int logListX1 = square.getLeft() + squareSpacing;
        int logListX2 = square.getRight() - squareSpacing;
        int logListWidth = logListX2 - logListX1;


        int logListY1 = y;
        int logListY2 = square.getBottom() - 10;
        int logListHeight = logListY2 - logListY1;

        FancyLogScrollList logList = new FancyLogScrollList(
                this.minecraft,
                logListWidth, logListHeight,
                logListY1, logListY2
        );
        logList.setLogs(clan, 0.8f);
        logList.setRenderBackground(false);
        logList.setRenderTopAndBottom(false);
        logList.setLeftPos(logListX1);

        square.addWidget(logList);


        y -= 20;

        List<FancySelectableButton> filtersList = new ArrayList<>();

        int buttonCount = 5;
        int spacing = 5;
        int buttonWidth = (logListWidth - spacing * (buttonCount - 1)) / buttonCount;
        int step = buttonWidth + spacing;
        int xButtonPos = logListX1;

        FancySelectableButton filterAll = new FancySelectableButton(
                buttonWidth, 15,
                xButtonPos, y,
                Component.translatable("clan.loglist.all"),
                but -> {
                    for (FancySelectableButton button : filtersList) {
                        button.setSelected(false);
                    }
                    but.setSelected(true);

                    logList.filter(clanLogEntry -> true);
                },
                0.8f, 0, "", true
        );

        xButtonPos += step;

        FancySelectableButton filterDeaths = new FancySelectableButton(
                buttonWidth, 15,
                xButtonPos, y,
                Component.translatable("clan.loglist.deaths"),
                but -> {
                    for (FancySelectableButton button : filtersList) {
                        button.setSelected(false);
                    }
                    but.setSelected(true);

                    logList.filter(clanLogEntry -> clanLogEntry.type() == ClanObjects.ClanLogEntry.Type.DEATHS);
                },
                0.8f, 0, "", true
        );

        xButtonPos += step;

        FancySelectableButton filterPlayers = new FancySelectableButton(
                buttonWidth, 15,
                xButtonPos, y,
                Component.translatable("clan.loglist.players"),
                but -> {
                    for (FancySelectableButton button : filtersList) {
                        button.setSelected(false);
                    }
                    but.setSelected(true);

                    logList.filter(clanLogEntry -> clanLogEntry.type() == ClanObjects.ClanLogEntry.Type.PLAYERS);
                },
                0.8f, 0, "", true
        );

        xButtonPos += step;

        FancySelectableButton filterOthers = new FancySelectableButton(
                buttonWidth, 15,
                xButtonPos, y,
                Component.translatable("clan.loglist.others"),
                but -> {
                    for (FancySelectableButton button : filtersList) {
                        button.setSelected(false);
                    }
                    but.setSelected(true);

                    logList.filter(clanLogEntry -> clanLogEntry.type() == ClanObjects.ClanLogEntry.Type.OTHERS);
                },
                0.8f, 0, "", true
        );

        xButtonPos += step;

        FancySelectableButton filterCustom = new FancySelectableButton(
                buttonWidth, 15,
                xButtonPos, y,
                Component.translatable("clan.loglist.custom"),
                but -> {
                    for (FancySelectableButton button : filtersList) {
                        button.setSelected(false);
                    }
                    but.setSelected(true);

                    logList.filter(clanLogEntry -> clanLogEntry.type() == ClanObjects.ClanLogEntry.Type.CUSTOM);
                },
                0.8f, 0, "", true
        );

        filterAll.setSelected(true);

        filtersList.add(filterAll);
        filtersList.add(filterDeaths);
        filtersList.add(filterPlayers);
        filtersList.add(filterOthers);
        filtersList.add(filterCustom);

        for (FancySelectableButton button : filtersList) {
            square.addWidget(button);
        }

        if (clan.canManage()){
            y -= 25;

            FancySimpleButton sendLogMenu = new FancySimpleButton(
                    50, 15,
                    logListX1, y,
                    Component.translatable("screen.clan.send_a_log"),
                    but -> {
                        drawSendLogMenu(square);
                    }, 0.8f
            );

            square.addWidget(sendLogMenu);
        }




        this.addRenderableWidget(square);
    }

    private void drawSendLogMenu(FancySubRenderablesScrollSquare square) {
        square.setActive(false);

        FancyLambdaRenderable shadow = new FancyLambdaRenderable(
                (pGuiGraphics, mouseX, mouseY, pPartialTick) -> {
                    pGuiGraphics.pose().pushPose();
                    pGuiGraphics.pose().translate(0,0,1500);
                    pGuiGraphics.fill(0,0, width, height, 0x66000000);
                    pGuiGraphics.pose().popPose();
                });

        this.addRenderableWidget(shadow);

        int centerX = width / 2;
        int centerY = height / 2;

        int sendLogSquareWidth = 200;
        int sendLogSquareHeight = 150;
        int sendLogSquareX1 = centerX - sendLogSquareWidth/2;
        int sendLogSquareY1 = centerY - sendLogSquareHeight/2;


        FancySubRenderablesSquare sendLogSquare = new FancySubRenderablesSquare(
            sendLogSquareX1, sendLogSquareY1, sendLogSquareWidth, sendLogSquareHeight
        );

        sendLogSquare.setZ(1500);

        int buttonsWidth = (sendLogSquareWidth - 30)/2;
        int buttonsHeight = 15;

        int squareCenterX = sendLogSquareX1 + sendLogSquareWidth/2;
        int squareCenterY = sendLogSquareY1 + sendLogSquareHeight/2;


        FancyStringWidget title = new FancyStringWidget(
                Component.translatable("screen.clan.send_a_log"),
                sendLogSquareX1 + 5, sendLogSquareX1 + sendLogSquareWidth - 5,
                sendLogSquareY1 + 10, 1.1f, true, 0
        );

        sendLogSquare.addWidget(title);


        int boxWidth = (int) (0.8f*sendLogSquareWidth);
        FancyEditBox logBox = new FancyEditBox(
                this.font,
                squareCenterX - boxWidth/2, squareCenterY,
                boxWidth, 15
        );
        logBox.setHint(Component.translatable("screen.clan.message_hint").withStyle(ChatFormatting.DARK_GRAY));
        logBox.setMaxLength(250);

        sendLogSquare.addWidget(logBox);

        FancyLambdaRenderable messageWritten = new FancyLambdaRenderable(
                squareCenterX, sendLogSquareY1, 0,
                (pGuiGraphics, mouseX, mouseY, pPartialTick) -> {

                    String text = logBox.getValue();

                    float textScale = 0.7f;

                    List<FormattedCharSequence> lines = this.font.split(
                            FormattedText.of(text), (int) ((sendLogSquareWidth - 10)/textScale)
                    );


                    pGuiGraphics.pose().pushPose();
                    pGuiGraphics.pose().translate(0, 30, 0);
                    pGuiGraphics.pose().scale(textScale, textScale, textScale);
                    int msgY = 0;
                    for (FormattedCharSequence line : lines) {
                        pGuiGraphics.drawCenteredString(this.font, line, 0, msgY, 0xFFFFFFFF);
                        msgY += font.lineHeight;
                    }
                    pGuiGraphics.pose().popPose();

                }
        );

        sendLogSquare.addWidget(messageWritten);

        FancySimpleButton sendButton = new FancySimpleButton(
                buttonsWidth, buttonsHeight,
                squareCenterX + 5, squareCenterY + 30,
                Component.empty()
                        .append("\uD83D\uDCE8 ")
                        .append(Component.translatable("screen.clan.send")),
                but -> {
                    if (logBox.getValue().isEmpty()) {
                        this.displayMessage(Component.translatable("screen.error.clan_log_empty"), true);
                    } else {
                        this.enqueue(() -> {
                            ModPackets.sendToServer(new CtSRegisterLogPacket(logBox.getValue(), clan.getClan().clanUUID));
                        });

                        this.removeWidget(sendLogSquare);
                        this.removeWidget(shadow);

                        square.setActive(true);
                    }

                }, 0.9f, true);

        sendLogSquare.addWidget(sendButton);

        FancySimpleButton cancelButton = new FancySimpleButton(
                buttonsWidth, buttonsHeight,
                squareCenterX - 5 - buttonsWidth, squareCenterY + 30,
                Component.empty()
                        .append("❌ ")
                        .append(Component.translatable("screen.button.cancel")),
                but -> {
                    this.removeWidget(sendLogSquare);
                    this.removeWidget(shadow);

                    square.setActive(true);
                }, 0.9f, true);

        sendLogSquare.addWidget(cancelButton);

        this.addRenderableWidget(sendLogSquare);

    }

    private void drawRequestsSheet() {
        drawMainButtons();

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        int menuWidth = menuMaxX - menuMinX;
        int menuHeight = menuMaxY - menuMinY;

        FancySubRenderablesScrollSquare square = new FancySubRenderablesScrollSquare(
                menuMinX, menuMinY,
                menuWidth, menuHeight
        );
        square.setRenderSquare(false);

        int y = menuMinY + 10;

        FancyStringWidget title = new FancyStringWidget(
                Component.translatable("screen.clan.join_requests"),
                menuMinX, menuMaxX, y,
                1.2f,
                true,
                0
        );

        square.addWidget(title);

        y+=20;

        FancyLambdaRenderable hLine1 = new FancyLambdaRenderable(
                centerX, y, 0,
                (pGuiGraphics, mouseX, mouseY, pPartialTick) -> {
                    pGuiGraphics.hLine((-menuWidth / 2) + 10, (menuWidth / 2) - 10, 0, 0xFFFFFFFF);
                });

        square.addWidget(hLine1);

        y += 10;

        boolean shouldBeAbleToRequest = ClientClanData.get().getCurrentClanUUID().equals(ClanData.EMPTY_UUID);


        if (clan.canManage()) {
            int squareSpacing = 15;
            int logListX1 = square.getLeft() + squareSpacing;
            int logListX2 = square.getRight() - squareSpacing;
            int logListWidth = logListX2 - logListX1;


            int logListY1 = y;
            int logListY2 = square.getBottom() - 10;
            int logListHeight = logListY2 - logListY1;

            FancyRequestsScrollList requestList = new FancyRequestsScrollList(
                    this.minecraft,
                    logListWidth, logListHeight,
                    logListY1, logListY2,
                    50
            );
            requestList.setRequests(clan.getClan().joinRequests);
            requestList.setRenderBackground(false);
            requestList.setRenderTopAndBottom(false);
            requestList.setLeftPos(logListX1);

            square.addWidget(requestList);
        } else if (hasRequestedToJoin) {
            FancyLambdaRenderable msgRender = new FancyLambdaRenderable(
                    centerX, square.getOriginalYPos0() + square.getHeight()/2, 0,
                    (pGuiGraphics, mouseX, mouseY, pPartialTick) -> {
                        Component text = Component.translatable("screen.clan.request_pending")
                                .withStyle(ChatFormatting.GRAY)
                                .withStyle(ChatFormatting.ITALIC);

                        float scale = 1f;
                        List<FormattedCharSequence> lines = this.font.split(text, (int) ((square.getWidth()*0.6f)/scale));

                        pGuiGraphics.pose().pushPose();
                        pGuiGraphics.pose().scale(scale, scale, scale);
                        int textY = 0;
                        for (FormattedCharSequence line : lines) {
                            pGuiGraphics.drawCenteredString(this.font, line, 0, textY, 0xFFFFFFFF);
                            textY += font.lineHeight;
                        }
                        pGuiGraphics.pose().popPose();
                    }
            );

            square.addWidget(msgRender);

        } else if (shouldBeAbleToRequest) {
            if (!clan.getClan().allowJoinRequests) {
                FancyLambdaRenderable msgRender = new FancyLambdaRenderable(
                        centerX, square.getOriginalYPos0() + square.getHeight()/2, 0,
                        (pGuiGraphics, mouseX, mouseY, pPartialTick) -> {
                            Component text = Component.translatable("screen.clan.requests_disabled")
                                    .withStyle(ChatFormatting.GRAY)
                                    .withStyle(ChatFormatting.ITALIC);

                            float scale = 1f;
                            List<FormattedCharSequence> lines = this.font.split(text, (int) ((square.getWidth()*0.6f)/scale));

                            pGuiGraphics.pose().pushPose();
                            pGuiGraphics.pose().scale(scale, scale, scale);
                            int textY = 0;
                            for (FormattedCharSequence line : lines) {
                                pGuiGraphics.drawCenteredString(this.font, line, 0, textY, 0xFFFFFFFF);
                                textY += font.lineHeight;
                            }
                            pGuiGraphics.pose().popPose();
                        }
                );

                square.addWidget(msgRender);
            } else {
                FancyStringWidget requestText = new FancyStringWidget(
                        Component.translatable("screen.clan.request_to_join", clan.getClan().name),
                        square.getLeft(), square.getRight(),
                        y,
                        0.9f,true, 100
                );
                square.addWidget(requestText);
                y += 60;

                int editBoxWidth = 100;
                FancyEditBox messageBox = new FancyEditBox(
                        this.font,
                        centerX - editBoxWidth/2, y,
                        editBoxWidth, 15
                );
                messageBox.setHint(Component.translatable("screen.clan.message_hint")
                        .withStyle(ChatFormatting.DARK_GRAY));
                messageBox.setMaxLength(180);

                square.addWidget(messageBox);
                y += 20;

                int buttonWidth = 60;
                FancySimpleButton sendRequestButton = new FancySimpleButton(
                        buttonWidth, 15,
                        centerX - buttonWidth/2, y,
                        Component.empty()
                                .append("\uD83D\uDCE8 ")
                                .append(Component.translatable("screen.clan.send")),
                        but -> {
                            this.enqueue(() -> {
                                ModPackets.sendToServer(new RequestClanJoinPacket(messageBox.getValue(), clan.getClan().clanUUID));
                            });
                        }, 0.9f, true);

                square.addWidget(sendRequestButton);

                FancyLambdaRenderable msgRender = new FancyLambdaRenderable(
                        centerX, y - 60, 0,
                        (pGuiGraphics, mouseX, mouseY, pPartialTick) -> {

                            String msg = messageBox.getValue().isEmpty() ? "..." : messageBox.getValue();
                            Component text = Component.literal("\"" + msg + "\"");

                            float scale = 0.8f;
                            List<FormattedCharSequence> lines = this.font.split(text, (int) ((square.getWidth()*0.6f)/scale));

                            pGuiGraphics.pose().pushPose();
                            pGuiGraphics.pose().scale(scale, scale, scale);
                            int textY = 0;
                            for (FormattedCharSequence line : lines) {
                                pGuiGraphics.drawCenteredString(this.font, line, 0, textY, 0xFFFFFFFF);
                                textY += font.lineHeight;
                            }
                            pGuiGraphics.pose().popPose();
                        }
                );

                square.addWidget(msgRender);

            }

        } else {
            FancyLambdaRenderable msgRender = new FancyLambdaRenderable(
                    centerX, square.getOriginalYPos0() + square.getHeight()/2, 0,
                    (pGuiGraphics, mouseX, mouseY, pPartialTick) -> {
                        Component text = Component.translatable("screen.clan.no_permissions")
                                .withStyle(ChatFormatting.GRAY)
                                .withStyle(ChatFormatting.ITALIC);

                        float scale = 1f;
                        List<FormattedCharSequence> lines = this.font.split(text, (int) ((square.getWidth()*0.6f)/scale));

                        pGuiGraphics.pose().pushPose();
                        pGuiGraphics.pose().scale(scale, scale, scale);
                        int textY = 0;
                        for (FormattedCharSequence line : lines) {
                            pGuiGraphics.drawCenteredString(this.font, line, 0, textY, 0xFFFFFFFF);
                            textY += font.lineHeight;
                        }
                        pGuiGraphics.pose().popPose();
                    }
            );

            square.addWidget(msgRender);
        }


        this.addRenderableWidget(square);
    }

    private void drawManageSheet() {
        drawMainButtons();

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        int menuWidth = menuMaxX - menuMinX;
        int menuHeight = menuMaxY - menuMinY;

        FancySubRenderablesScrollSquare square = new FancySubRenderablesScrollSquare(
                menuMinX + 1, menuMinY + 40,
                menuWidth - 2, menuHeight - 56
        );

        FancyLambdaRenderable shadow = new FancyLambdaRenderable(
                (pGuiGraphics, mouseX, mouseY, pPartialTick) -> {
                    pGuiGraphics.pose().pushPose();
                    pGuiGraphics.pose().translate(0,0,1500);
                    pGuiGraphics.fill(0,0, width, height, 0x66000000);
                    pGuiGraphics.pose().popPose();
                });

        int y = menuMinY + 10;

        FancyStringWidget title = new FancyStringWidget(
                Component.translatable("screen.clan.manage"),
                menuMinX, menuMaxX, y,
                1.2f,
                true,
                0
        );

        this.addRenderableWidget(title);

        y+=20;

        FancyLambdaRenderable hLine1 = new FancyLambdaRenderable(
                centerX, y, 0,
                (pGuiGraphics, mouseX, mouseY, pPartialTick) -> {
                    pGuiGraphics.hLine((-menuWidth / 2) + 10, (menuWidth / 2) - 10, 0, 0xFFFFFFFF);
                });

        this.addRenderableWidget(hLine1);

        y += 20;

        FancyLambdaRenderable hLine2 = new FancyLambdaRenderable(
                centerX, y, 0,
                (pGuiGraphics, mouseX, mouseY, pPartialTick) -> {
                    pGuiGraphics.hLine((-menuWidth / 2) + 10, (menuWidth / 2) - 10, 0, 0xFFFFFFFF);
                });

        square.addWidget(hLine2);

        y += 10;

        if (!clan.canManage()) {
            FancyLambdaRenderable noPermsText = new FancyLambdaRenderable(
                    menuMinX + menuWidth/2, menuMinY + menuHeight/2, 0,
                    (pGuiGraphics, mouseX, mouseY, pPartialTick) -> {

                        List<FormattedCharSequence> lines = this.font.split(
                                Component.translatable("clan.no_permissions").withStyle(ChatFormatting.GRAY),
                                menuWidth - 50);

                        int txtY = 0;
                        for (FormattedCharSequence line : lines) {
                            pGuiGraphics.drawCenteredString(this.font,
                                    line,
                                    0,txtY,0xFFFFFFFF);
                            txtY += font.lineHeight;
                        }

                    }
            );
            square.addWidget(noPermsText);

            this.addRenderableWidget(square);
            return;
        }

        int menuButtonsWidth = 90;
        int menuButtonsHeight = 15;
        int buttonsSpacing =  menuButtonsHeight + 10;

        int editBoxWidth = 100;

        {

            FancyStringWidget clanTitle = new FancyStringWidget(
                    Component.translatable("screen.clan"),
                    menuMinX, menuMaxX, y,
                    1.0f,
                    true,
                    0
            );

            square.addWidget(clanTitle);

            y += 20;

            FancySimpleButton renameClanButton = new FancySimpleButton(
                    menuButtonsWidth, menuButtonsHeight,
                    menuMinX + 10, y,
                    Component.translatable("screen.clan.rename_clan"),
                    button -> {

                        toggleSubMenu(square, shadow, true);
                        FancySubRenderablesSquare subSquare = createSubSquare(0,0);
                        addTitleToSquare(subSquare, Component.translatable("screen.clan.rename_clan"));

                        int subSquareCenterX = subSquare.getLeft() + subSquare.getWidth()/2;
                        int subSquareCenterY = subSquare.getTop() + subSquare.getHeight()/2;

                        FancyEditBox clanNameField = new FancyEditBox(
                                this.font,
                                (subSquareCenterX) - editBoxWidth/2, subSquareCenterY,
                                editBoxWidth, 15
                        );
                        clanNameField.setMaxLength(20);
                        clanNameField.setHint(Component.translatable("screen.clanmanage.new_name")
                                .withStyle(ChatFormatting.DARK_GRAY));

                        subSquare.addWidget(clanNameField);

                        FancyLambdaRenderable renderNewName = new FancyLambdaRenderable(
                                subSquareCenterX, subSquare.getOriginalYPos0() + 30, 0,
                                (pGuiGraphics, mouseX, mouseY, pPartialTick) -> {
                                    String newNameString = clanNameField.getValue().isEmpty() ? "..." : clanNameField.getValue();

                                    Component text = Component.translatable("screen.clan.renaming_from_to",
                                            Component.literal(clan.getClan().name).withStyle(Style.EMPTY.withColor(clan.getClan().color)),
                                            Component.literal(newNameString).withStyle(Style.EMPTY.withColor(clan.getClan().color))
                                            );

                                    List<FormattedCharSequence> lines = font.split(text, subSquare.getWidth() - 20);

                                    int txtSubY = 0;
                                    for (FormattedCharSequence line : lines) {
                                        pGuiGraphics.drawCenteredString(font, line, 0,txtSubY, 0xFFFFFFFF);
                                        txtSubY += font.lineHeight;
                                    }

                                }
                        );

                        subSquare.addWidget(renderNewName);

                        squareConfirmationButtons(subSquare,
                                cancel -> {
                                    toggleSubMenu(square, shadow, false);
                                    this.removeWidget(subSquare);
                                },
                                confirm -> {
                                    if (clanNameField.getValue().isEmpty()) {
                                        displayMessage(Component.translatable("screen.clan.content_empty"), true);
                                        return;
                                    }
                                    this.enqueue(() -> {
                                        ModPackets.sendToServer(new RenameClanPacket(clanNameField.getValue()));
                                    });
                                    toggleSubMenu(square, shadow, false);
                                    this.removeWidget(subSquare);
                                }
                        );



                        this.addRenderableWidget(subSquare);
                    }, 0.8f
            );
            square.addWidget(renameClanButton);

            FancySwitchButton allowRequestsSwitch = new FancySwitchButton(
                    menuMaxX - 10 - FancySwitchButton.getKeyBasedWidth("screen.clan.joinrequests", 0.8f), y,
                    15,
                    "screen.clan.joinrequests",
                    clan.getClan().allowJoinRequests,
                    but -> {
                        this.enqueue(() -> {
                            ModPackets.sendToServer(
                                    ManageClanPacket.setAllowRequests(clan.getClan().clanUUID,
                                            ((FancySwitchButton) but).getValue()));
                        });
                    }, 0.8f
            );

            square.addWidget(allowRequestsSwitch);

            y += buttonsSpacing;

            FancySimpleButton changeColorButton = new FancySimpleButton(
                    menuButtonsWidth, menuButtonsHeight,
                    menuMinX + 10, y,
                    Component.translatable("screen.clan.change_color"),
                    button -> {
                        toggleSubMenu(square, shadow, true);
                        FancySubRenderablesSquare subSquare = createSubSquare(0,40);
                        addTitleToSquare(subSquare, Component.translatable("screen.clan.change_color"));

                        int subSquareCenterX = subSquare.getLeft() + subSquare.getWidth()/2;
                        int subSquareCenterY = subSquare.getTop() + subSquare.getHeight()/2;

                        FancyFloatSlider rSlider = new FancyFloatSlider(
                                subSquareCenterX - editBoxWidth / 2, subSquareCenterY,
                                editBoxWidth, 15,
                                0.0f, 0.75f,
                                0.75f, slider -> {}
                        );
                        FancyFloatSlider gSlider = new FancyFloatSlider(
                                subSquareCenterX - editBoxWidth / 2, subSquareCenterY + 20,
                                editBoxWidth, 15,
                                0.0f, 0.75f,
                                0.75f, slider -> {}
                        );
                        FancyFloatSlider bSlider = new FancyFloatSlider(
                                subSquareCenterX - editBoxWidth / 2, subSquareCenterY + 40,
                                editBoxWidth, 15,
                                0.0f, 0.75f,
                                0.75f, slider -> {}
                        );
                        subSquare.addWidget(rSlider);
                        subSquare.addWidget(gSlider);
                        subSquare.addWidget(bSlider);

                        FancyLambdaRenderable logoRender = new FancyLambdaRenderable(
                                subSquareCenterX, subSquareCenterY - 10, 0,
                                (pGuiGraphics, mouseX, mouseY, pPartialTick) -> {

                                    float[] rgb = ensureMinLuminance(
                                            (rSlider.getActualValue()),
                                            (gSlider.getActualValue()),
                                            (bSlider.getActualValue()),
                                            0.30f);

                                    int newColor = FastColor.ARGB32.color(
                                            255,
                                            (int) (rgb[0]*255),
                                            (int) (rgb[1]*255),
                                            (int) (rgb[2]*255)
                                    );

                                    renderClanSymbol(pGuiGraphics, -40,0,1f, clan.getClan().color);

                                    pGuiGraphics.drawCenteredString(this.font, "→", 0,-30,-1);

                                    renderClanSymbol(pGuiGraphics, 40,0,1f, newColor);

                                    int lettersX = -(editBoxWidth/2) - 10;
                                    pGuiGraphics.drawString(this.font, "R:", lettersX,15,0xFFFF0000);
                                    pGuiGraphics.drawString(this.font, "G:", lettersX,35,0xFF00FF00);
                                    pGuiGraphics.drawString(this.font, "B:", lettersX,55,0xFF0000FF);
                                }
                        );

                        subSquare.addWidget(logoRender);

                        squareConfirmationButtons(subSquare,
                                cancel -> {
                                    toggleSubMenu(square, shadow, false);
                                    this.removeWidget(subSquare);
                                },
                                confirm -> {
                                    toggleSubMenu(square, shadow, false);
                                    this.removeWidget(subSquare);

                                    float[] rgb = ensureMinLuminance(
                                            (rSlider.getActualValue()),
                                            (gSlider.getActualValue()),
                                            (bSlider.getActualValue()),
                                            0.30f);

                                    int newColor = FastColor.ARGB32.color(
                                            255,
                                            (int) (rgb[0]*255),
                                            (int) (rgb[1]*255),
                                            (int) (rgb[2]*255)
                                    );

                                    this.enqueue(() -> {
                                        ModPackets.sendToServer(ManageClanPacket.setClanColor(clan.getClan().clanUUID, newColor));
                                    });
                                }
                        );



                        this.addRenderableWidget(subSquare);
                    }, 0.8f
            );
            square.addWidget(changeColorButton);

            FancySwitchButton publicLogsSwitch = new FancySwitchButton(
                    menuMaxX - 10 - FancySwitchButton.getKeyBasedWidth("screen.clan.publiclogs", 0.8f), y,
                    15,
                    "screen.clan.publiclogs",
                    clan.getClan().publicClanLogs,
                    but -> {
                        this.enqueue(() -> {
                            ModPackets.sendToServer(
                                    ManageClanPacket.setPublicLogs(clan.getClan().clanUUID,
                                            ((FancySwitchButton) but).getValue()));
                        });
                    }, 0.8f
            );

            square.addWidget(publicLogsSwitch);

            y += buttonsSpacing;

            FancySimpleButton setClanBio = new FancySimpleButton(
                    menuButtonsWidth, menuButtonsHeight,
                    menuMinX + 10, y,
                    Component.translatable("screen.clan.set_clan_bio"),
                    button -> {
                        toggleSubMenu(square, shadow, true);
                        FancySubRenderablesSquare subSquare = createSubSquare(0,0);
                        addTitleToSquare(subSquare, Component.translatable("screen.clan.set_clan_bio"));

                        int subSquareCenterX = subSquare.getLeft() + subSquare.getWidth()/2;
                        int subSquareCenterY = subSquare.getTop() + subSquare.getHeight()/2;

                        FancyEditBox clanBioField = new FancyEditBox(
                                this.font,
                                (subSquareCenterX) - editBoxWidth/2, subSquareCenterY,
                                editBoxWidth, 15
                        );
                        clanBioField.setMaxLength(130);
                        clanBioField.setHint(Component.translatable("screen.clan.bio_hint")
                                .withStyle(ChatFormatting.DARK_GRAY));

                        subSquare.addWidget(clanBioField);

                        FancyLambdaRenderable renderNewBio = new FancyLambdaRenderable(
                                subSquareCenterX, subSquare.getOriginalYPos0() + 30, 0,
                                (pGuiGraphics, mouseX, mouseY, pPartialTick) -> {
                                    String newBioString = clanBioField.getValue().isEmpty() ? "..." : clanBioField.getValue();

                                    Component text = Component.literal("\"" + newBioString + "\"");
                                    float scale = 0.8f;

                                    List<FormattedCharSequence> lines = font.split(text, (int) ((subSquare.getWidth() - 20)/scale));

                                    pGuiGraphics.pose().pushPose();
                                    pGuiGraphics.pose().scale(scale, scale, scale);
                                    int txtSubY = 0;
                                    for (FormattedCharSequence line : lines) {
                                        pGuiGraphics.drawCenteredString(font, line, 0,txtSubY, 0xFFFFFFFF);
                                        txtSubY += font.lineHeight;
                                    }
                                    pGuiGraphics.pose().popPose();

                                }
                        );

                        subSquare.addWidget(renderNewBio);

                        squareConfirmationButtons(subSquare,
                                cancel -> {
                                    toggleSubMenu(square, shadow, false);
                                    this.removeWidget(subSquare);
                                },
                                confirm -> {
                                    if (clanBioField.getValue().isEmpty()) {
                                        displayMessage(Component.translatable("screen.clan.content_empty"), true);
                                        return;
                                    }
                                    this.enqueue(() -> {
                                        ModPackets.sendToServer(ManageClanPacket.setClanBio(clan.getClan().clanUUID, clanBioField.getValue()));
                                    });
                                    toggleSubMenu(square, shadow, false);
                                    this.removeWidget(subSquare);
                                }
                        );



                        this.addRenderableWidget(subSquare);
                    }, 0.8f
            );
            square.addWidget(setClanBio);

            FancySwitchButton protectTerritorySwitch = new FancySwitchButton(
                    menuMaxX - 10 - FancySwitchButton.getKeyBasedWidth("screen.clan.protect_territory", 0.8f), y,
                    15,
                    "screen.clan.protect_territory",
                    clan.getClan().protectTerritory,
                    but -> {
                        this.enqueue(() -> {
                            ModPackets.sendToServer(
                                    ManageClanPacket.setProtectTerritory(clan.getClan().clanUUID,
                                            ((FancySwitchButton) but).getValue()));
                        });
                    }, 0.8f
            );

            square.addWidget(protectTerritorySwitch);

            y += buttonsSpacing;

            FancySimpleButton setLeaderMessageButton = new FancySimpleButton(
                    menuButtonsWidth, menuButtonsHeight,
                    menuMinX + 10, y,
                    Component.translatable("screen.clan.set_leader_message"),
                    button -> {
                        toggleSubMenu(square, shadow, true);
                        FancySubRenderablesSquare subSquare = createSubSquare(0,0);
                        addTitleToSquare(subSquare, Component.translatable("screen.clan.set_leader_message"));

                        int subSquareCenterX = subSquare.getLeft() + subSquare.getWidth()/2;
                        int subSquareCenterY = subSquare.getTop() + subSquare.getHeight()/2;

                        FancyEditBox leaderMessageField = new FancyEditBox(
                                this.font,
                                (subSquareCenterX) - editBoxWidth/2, subSquareCenterY,
                                editBoxWidth, 15
                        );
                        leaderMessageField.setMaxLength(150);
                        leaderMessageField.setHint(Component.translatable("screen.clan.message_hint")
                                .withStyle(ChatFormatting.DARK_GRAY));

                        subSquare.addWidget(leaderMessageField);

                        FancyLambdaRenderable renderNewLeaderMessage = new FancyLambdaRenderable(
                                subSquareCenterX, subSquare.getOriginalYPos0() + 30, 0,
                                (pGuiGraphics, mouseX, mouseY, pPartialTick) -> {
                                    String newMessageString = leaderMessageField.getValue().isEmpty() ? "..." : leaderMessageField.getValue();

                                    Component text = Component.literal("\"" + newMessageString + "\"")
                                            .withStyle(ChatFormatting.ITALIC);
                                    float scale = 0.8f;

                                    List<FormattedCharSequence> lines = font.split(text, (int) ((subSquare.getWidth() - 20)/scale));

                                    pGuiGraphics.pose().pushPose();
                                    pGuiGraphics.pose().scale(scale, scale, scale);
                                    int txtSubY = 0;
                                    for (FormattedCharSequence line : lines) {
                                        pGuiGraphics.drawCenteredString(font, line, 0,txtSubY, 0xFFFFFFFF);
                                        txtSubY += font.lineHeight;
                                    }
                                    pGuiGraphics.pose().popPose();

                                }
                        );

                        subSquare.addWidget(renderNewLeaderMessage);

                        squareConfirmationButtons(subSquare,
                                cancel -> {
                                    toggleSubMenu(square, shadow, false);
                                    this.removeWidget(subSquare);
                                },
                                confirm -> {
                                    this.enqueue(() -> {
                                        ModPackets.sendToServer(ManageClanPacket.setLeaderMessage(clan.getClan().clanUUID, leaderMessageField.getValue()));
                                    });
                                    toggleSubMenu(square, shadow, false);
                                    this.removeWidget(subSquare);
                                }
                        );

                        this.addRenderableWidget(subSquare);
                    }, 0.8f
            );
            square.addWidget(setLeaderMessageButton);

            FancySwitchButton protectContainersSwitch = new FancySwitchButton(
                    menuMaxX - 10 - FancySwitchButton.getKeyBasedWidth("screen.clan.protect_containers", 0.8f), y,
                    15,
                    "screen.clan.protect_containers",
                    clan.getClan().protectContainers,
                    but -> {
                        this.enqueue(() -> {
                            ModPackets.sendToServer(
                                    ManageClanPacket.setProtectContainers(clan.getClan().clanUUID,
                                            ((FancySwitchButton) but).getValue()));
                        });
                    }, 0.8f
            );

            square.addWidget(protectContainersSwitch);

            y += buttonsSpacing;

            FancySimpleButton disbandClanButton = new FancySimpleButton(
                    menuButtonsWidth, menuButtonsHeight,
                    menuMinX + 10, y,
                    Component.translatable("screen.clan.disband_clan"),
                    button -> {
                        toggleSubMenu(square, shadow, true);
                        FancySubRenderablesSquare subSquare = createSubSquare(20,-80);
                        addTitleToSquare(subSquare, Component.translatable("screen.clan.disband_confirm",
                                Component.literal(clan.getClan().name).withStyle(Style.EMPTY.withColor(clan.getClan().color))));

                        squareConfirmationButtons(subSquare,
                                cancel -> {
                                    toggleSubMenu(square, shadow, false);
                                    this.removeWidget(subSquare);
                                },
                                confirm -> {
                                    this.enqueue(() -> {
                                        if (Minecraft.getInstance().player == null) return;
                                        Minecraft.getInstance().player.connection.sendCommand("wce clan disband");
                                        this.onClose();
                                    });
                                    toggleSubMenu(square, shadow, false);
                                    this.removeWidget(subSquare);
                                }
                        );

                        this.addRenderableWidget(subSquare);
                    }, 0.8f
            );
            square.addWidget(disbandClanButton);

            y += buttonsSpacing;

        }

        FancyLambdaRenderable hLine3 = new FancyLambdaRenderable(
                centerX, y, 0,
                (pGuiGraphics, mouseX, mouseY, pPartialTick) -> {
                    pGuiGraphics.hLine((-menuWidth / 2) + 10, (menuWidth / 2) - 10, 0, 0xFFFFFFFF);
                });

        square.addWidget(hLine3);

        y += 10;

        {
            FancyStringWidget territoryTitle = new FancyStringWidget(
                    Component.translatable("screen.clan.territory"),
                    menuMinX, menuMaxX, y,
                    1.0f,
                    true,
                    0
            );

            square.addWidget(territoryTitle);

            y += 20;

            LocalPlayer player =  Minecraft.getInstance().player;
            boolean isTheCore = false;
            boolean isInOwnTerritory = false;
            ChunkPos currentChunkPos;
            String currentChunkPosName = "";

            if (player != null) {
                currentChunkPos = player.chunkPosition();
                ClientTerritoryData.ClientClanTerritories clanTerritory = ClientTerritoryData.CLIENT_TERRITORIES.get(clan.getClan().clanUUID);
                if (clanTerritory != null) {
                    if (clanTerritory.core() != null) {
                        if (clanTerritory.core().equals(currentChunkPos)) {
                            isTheCore = true;
                        }
                    }

                    for (ClientTerritoryData.ClientChunk chunk : clanTerritory.claimedTerritory()) {
                        if (chunk.chunkPos.equals(currentChunkPos)) {
                            currentChunkPosName = chunk.name;
                            isInOwnTerritory = true;
                            break;
                        }
                    }

                }
            } else {
                currentChunkPos = new ChunkPos(0, 0);
            }

            boolean finalIsInOwnTerritory = isInOwnTerritory;
            boolean finalIsTheCore = isTheCore;
            String finalTerritoryName = currentChunkPosName;

            FancySimpleButton claimTerritoryButton = new FancySimpleButton(
                    menuButtonsWidth, menuButtonsHeight,
                    menuMinX + 10, y,
                    Component.translatable("screen.clanmanage.claimterritory"),
                    button -> {

                        toggleSubMenu(square, shadow, true);
                        FancySubRenderablesSquare subSquare = createSubSquare(0,0);
                        addTitleToSquare(subSquare, Component.translatable("screen.clanmanage.claimterritory"));

                        int subSquareCenterX = subSquare.getLeft() + subSquare.getWidth()/2;
                        int subSquareCenterY = subSquare.getTop() + subSquare.getHeight()/2;

                        FancyEditBox territoryName = new FancyEditBox(
                                this.font,
                                (subSquareCenterX) - editBoxWidth/2, subSquareCenterY,
                                editBoxWidth, 15
                        );
                        territoryName.setMaxLength(25);
                        territoryName.setHint(Component.translatable("screen.clanmanage.optional_name")
                                .withStyle(ChatFormatting.DARK_GRAY));

                        subSquare.addWidget(territoryName);

                        FancyLambdaRenderable renderCost = new FancyLambdaRenderable(
                                subSquareCenterX, subSquare.getOriginalYPos0() + 30, 0,
                                (pGuiGraphics, mouseX, mouseY, pPartialTick) -> {
                                    Component text = Component.translatable("screen.clanmanage.cost","450-850XP");

                                    List<FormattedCharSequence> lines = font.split(text, subSquare.getWidth() - 20);

                                    int txtSubY = 0;
                                    for (FormattedCharSequence line : lines) {
                                        pGuiGraphics.drawCenteredString(font, line, 0,txtSubY, 0xFFFFFFFF);
                                        txtSubY += font.lineHeight;
                                    }

                                }
                        );

                        subSquare.addWidget(renderCost);

                        squareConfirmationButtons(subSquare,
                                cancel -> {
                                    toggleSubMenu(square, shadow, false);
                                    this.removeWidget(subSquare);
                                },
                                confirm -> {
                                    this.enqueue(() -> {
                                        ModPackets.sendToServer(new CtSClaimTerritory(territoryName.getValue()));
                                    });

                                    toggleSubMenu(square, shadow, false);
                                    this.removeWidget(subSquare);
                                }
                        );



                        this.addRenderableWidget(subSquare);
                    }, 0.8f
            );
            square.addWidget(claimTerritoryButton);
            y += buttonsSpacing;


            FancySimpleButton unclaimTerritoryButton = new FancySimpleButton(
                    menuButtonsWidth, menuButtonsHeight,
                    menuMinX + 10, y,
                    Component.translatable("screen.clanmanage.unclaimterritory"),
                    button -> {
                        toggleSubMenu(square, shadow, true);
                        FancySubRenderablesSquare subSquare = createSubSquare(0,0);
                        addTitleToSquare(subSquare, Component.translatable("screen.clanmanage.unclaimterritory"));

                        int subSquareCenterX = subSquare.getLeft() + subSquare.getWidth()/2;
                        int subSquareCenterY = subSquare.getTop() + subSquare.getHeight()/2;

                        FancyLambdaRenderable renderMessage = new FancyLambdaRenderable(
                                subSquareCenterX, subSquare.getOriginalYPos0() + 30, 0,
                                (pGuiGraphics, mouseX, mouseY, pPartialTick) -> {
                                    Component message;
                                    if (finalIsInOwnTerritory) {
                                        if (finalIsTheCore) {
                                            message = Component.translatable("screen.clanmanage.unclaim_chunk_core");
                                        } else {
                                            message = Component.translatable("screen.clanmanage.unclaim_chunk");
                                        }
                                    } else {
                                        message = Component.translatable("screen.clanmanage.unclaim_chunk_not_claimed");
                                    }

                                    List<FormattedCharSequence> lines = font.split(message, subSquare.getWidth() - 20);

                                    int txtSubY = 0;
                                    for (FormattedCharSequence line : lines) {
                                        pGuiGraphics.drawCenteredString(font, line, 0,txtSubY, 0xFFFFFFFF);
                                        txtSubY += font.lineHeight;
                                    }

                                }
                        );

                        subSquare.addWidget(renderMessage);

                        squareConfirmationButtons(subSquare,
                                cancel -> {
                                    toggleSubMenu(square, shadow, false);
                                    this.removeWidget(subSquare);
                                },
                                confirm -> {
                                    if (!finalIsInOwnTerritory) return;

                                    this.enqueue(() -> {
                                        ModPackets.sendToServer(new CtSUnclaimTerritory(currentChunkPos));
                                    });

                                    toggleSubMenu(square, shadow, false);
                                    this.removeWidget(subSquare);
                                }
                        );
                        Renderable confirm = subSquare.getWidgets().get(subSquare.getWidgets().size() - 1);
                        if (confirm instanceof FancySimpleButton butConfirm) {
                            butConfirm.active = finalIsInOwnTerritory;
                        }

                        this.addRenderableWidget(subSquare);
                    }, 0.8f
            );
            square.addWidget(unclaimTerritoryButton);
            y += buttonsSpacing;

            FancySimpleButton renameChunkButton = new FancySimpleButton(
                    menuButtonsWidth, menuButtonsHeight,
                    menuMinX + 10, y,
                    Component.translatable("screen.clan.rename_chunk"),
                    button -> {
                        toggleSubMenu(square, shadow, true);
                        FancySubRenderablesSquare subSquare = createSubSquare(0,0);
                        addTitleToSquare(subSquare, Component.translatable("screen.clan.rename_chunk"));

                        int subSquareCenterX = subSquare.getLeft() + subSquare.getWidth()/2;
                        int subSquareCenterY = subSquare.getTop() + subSquare.getHeight()/2;

                        FancyEditBox territoryName = new FancyEditBox(
                                this.font,
                                (subSquareCenterX) - editBoxWidth/2, subSquareCenterY,
                                editBoxWidth, 15
                        );
                        territoryName.setMaxLength(25);
                        territoryName.setHint(Component.translatable("screen.clanmanage.optional_name")
                                .withStyle(ChatFormatting.DARK_GRAY));

                        subSquare.addWidget(territoryName);


                        FancyLambdaRenderable renderName = new FancyLambdaRenderable(
                                subSquareCenterX, subSquare.getOriginalYPos0() + 30, 0,
                                (pGuiGraphics, mouseX, mouseY, pPartialTick) -> {
                                    Component message;
                                    String newName = territoryName.getValue().isEmpty() ? "   " : territoryName.getValue();
                                    String oldName = finalTerritoryName.isEmpty() ? "   " : finalTerritoryName;
                                    if (finalIsInOwnTerritory) {
                                        message = Component.literal("\"" + oldName + "\" -> \"" + newName + "\"");
                                    } else {
                                        message = Component.translatable("screen.clanmanage.unclaim_chunk_not_claimed");
                                    }

                                    List<FormattedCharSequence> lines = font.split(message, subSquare.getWidth() - 20);

                                    int txtSubY = 0;
                                    for (FormattedCharSequence line : lines) {
                                        pGuiGraphics.drawCenteredString(font, line, 0,txtSubY, 0xFFFFFFFF);
                                        txtSubY += font.lineHeight;
                                    }

                                }
                        );

                        subSquare.addWidget(renderName);

                        squareConfirmationButtons(subSquare,
                                cancel -> {
                                    toggleSubMenu(square, shadow, false);
                                    this.removeWidget(subSquare);
                                },
                                confirm -> {
                                    this.enqueue(() -> {
                                       ModPackets.sendToServer(new CtSRenameTerritoryChunk(currentChunkPos, territoryName.getValue()));
                                    });

                                    toggleSubMenu(square, shadow, false);
                                    this.removeWidget(subSquare);
                                }
                        );
                        Renderable confirm = subSquare.getWidgets().get(subSquare.getWidgets().size() - 1);
                        if (confirm instanceof FancySimpleButton butConfirm) {
                            butConfirm.active = finalIsInOwnTerritory;
                        }

                        this.addRenderableWidget(subSquare);
                    }, 0.8f
            );
            square.addWidget(renameChunkButton);
            y += buttonsSpacing;

        }

        FancyLambdaRenderable hLine4 = new FancyLambdaRenderable(
                centerX, y, 0,
                (pGuiGraphics, mouseX, mouseY, pPartialTick) -> {
                    pGuiGraphics.hLine((-menuWidth / 2) + 10, (menuWidth / 2) - 10, 0, 0xFFFFFFFF);
                });

        square.addWidget(hLine4);

        y += 10;

        {
            int listWidth = 110;
            int listHeight = 140;

            FancyStringWidget playersTitle = new FancyStringWidget(
                    Component.translatable("screen.clan.manage_players"),
                    menuMinX, menuMaxX, y,
                    1.0f,
                    true,
                    0
            );

            square.addWidget(playersTitle);

            y += 40;

            FancyMemberScrollList playersList = new FancyMemberScrollList(
                    this.minecraft,
                    listWidth, listHeight,
                    y, y + listHeight,
                    20
            );
            playersList.setLeftPos(menuMinX + 10);
            playersList.setRenderBackground(false);
            playersList.setRenderTopAndBottom(false);

            FancyEditBox playersFilter = new FancyEditBox(
                    this.font,
                    playersList.getLeft(), y - 15,
                    playersList.getWidth(), 10
            );
            playersFilter.setHint(
                    Component.empty()
                            .append("<")
                            .append(Component.translatable("screen.clan.filter"))
                            .append(">")
                            .withStyle(ChatFormatting.GRAY)
            );
            playersFilter.setResponder(txt -> {
                playersList.setScrollAmount(0);
                playersList.filter(memberEntry -> memberEntry.getName().toLowerCase().contains(txt.toLowerCase()));
            });

            square.addWidget(playersFilter);

            FancySubRenderablesSquare subPlayerSquare = new FancySubRenderablesSquare(
                    menuMaxX - 15 - (listWidth - 10), y,
                    (listWidth - 10), listHeight);

            for (ClanObjects.Player player : clan.getPlayers().values()) {
                playersList.addMember(
                        player.getUUID(),
                        Component.literal(player.getMorphName()),
                        player.getGenetics(),
                        1,
                        () -> {
                            subPlayerSquare.clearWidgets();
                            square.removeWidget(subPlayerSquare);

                            managePlayerSubsquareWidgets(player, subPlayerSquare, square);

                            square.addWidget(subPlayerSquare);
                        }
                );
            }

            square.addWidget(playersList);

            y += listHeight + 10;
        }

        FancyLambdaRenderable hLine5 = new FancyLambdaRenderable(
                centerX, y, 0,
                (pGuiGraphics, mouseX, mouseY, pPartialTick) -> {
                    pGuiGraphics.hLine((-menuWidth / 2) + 10, (menuWidth / 2) - 10, 0, 0xFFFFFFFF);
                });

        square.addWidget(hLine5);

        y += 10;

        {
            int listWidth = 110;
            int listHeight = 100;

            FancyStringWidget npcTitle = new FancyStringWidget(
                    Component.translatable("screen.clan.manage_npcs"),
                    menuMinX, menuMaxX, y,
                    1.0f,
                    true,
                    0
            );

            square.addWidget(npcTitle);

            y += 40;

            FancyMemberScrollList npcList = new FancyMemberScrollList(
                    this.minecraft,
                    listWidth, listHeight,
                    y, y + listHeight,
                    20
            );
            npcList.setLeftPos(menuMinX + 10);
            npcList.setRenderBackground(false);
            npcList.setRenderTopAndBottom(false);

            FancyEditBox npcFilter = new FancyEditBox(
                    this.font,
                    npcList.getLeft(), y - 15,
                    npcList.getWidth(), 10
            );
            npcFilter.setHint(
                    Component.empty()
                            .append("<")
                            .append(Component.translatable("screen.clan.filter"))
                            .append(">")
                            .withStyle(ChatFormatting.GRAY)
            );
            npcFilter.setResponder(txt -> {
                npcList.setScrollAmount(0);
                npcList.filter(memberEntry -> memberEntry.getName().toLowerCase().contains(txt.toLowerCase()));
            });

            square.addWidget(npcFilter);

            FancySubRenderablesSquare subNPCSquare = new FancySubRenderablesSquare(
                    menuMaxX - 15 - (listWidth - 10), y,
                    (listWidth - 10), listHeight);

            for (ClanData.ClanCat clanCat : clan.getClan().clanCats.values()) {
                WCGenetics.PackedGeneticData data = new WCGenetics.PackedGeneticData(
                        clanCat.genetics, clanCat.variants,
                        clanCat.chimeraGenetics, clanCat.chimeraVariants,
                        clanCat.onGeneticalSkin, clanCat.catVariant
                );
                npcList.addMember(
                        clanCat.catUUID,
                        clanCat.catName,
                        data,
                        clanCat.catGenderValue,
                        () -> {
                            subNPCSquare.clearWidgets();
                            square.removeWidget(subNPCSquare);

                            TextColor txtColor = clanCat.catName.getStyle().getColor();
                            int color = txtColor != null ? (txtColor.getValue() | 0xFF000000) : -1;

                            FancyStringWidget npcNamePlate = new FancyStringWidget(
                                    clanCat.catName,
                                    subNPCSquare.getLeft(), subNPCSquare.getRight(),
                                    subNPCSquare.getOriginalYPos0() + 5,
                                    color,
                                    0.9f, true, 0
                            );

                            subNPCSquare.addWidget(npcNamePlate);

                            FancySimpleButton closeButton = new FancySimpleButton(
                                    15, 8,
                                    subNPCSquare.getLeft(), subNPCSquare.getOriginalYPos0() - 8,
                                    Component.literal("×"),
                                    button -> {
                                        subNPCSquare.clearWidgets();
                                        square.removeWidget(subNPCSquare);
                                    }, 0.8f
                            );
                            subNPCSquare.addWidget(closeButton);

                            FancyLambdaRenderable npcRenderable = new FancyLambdaRenderable(
                                    subNPCSquare.getLeft() + subNPCSquare.getWidth() / 2, subNPCSquare.getOriginalYPos0(), 0,
                                    (pGuiGraphics, mouseX, mouseY, pPartialTick) -> {
                                        pGuiGraphics.pose().pushPose();
                                        float scale = 0.6f;
                                        pGuiGraphics.pose().scale(scale, scale, scale);
                                        int textY = 30;

                                        int textSpacing = 11;
                                        {
                                            pGuiGraphics.drawCenteredString(this.font, clanCat.catRank, 0, textY, 0xFFaaaaaa);
                                            textY += textSpacing;
                                            pGuiGraphics.drawCenteredString(this.font, clanCat.catGender, 0, textY, 0xFFaaaaaa);
                                            textY += textSpacing;
                                            {
                                                List<FormattedCharSequence> list = font.split(clanCat.catParents, (int) ((int) (subNPCSquare.getWidth() /scale) * 0.8f));
                                                for (FormattedCharSequence line : list) {
                                                    pGuiGraphics.drawCenteredString(this.font, line, 0, textY, 0xFFaaaaaa);
                                                    textY += font.lineHeight;
                                                }
                                                textY += 2;
                                            }
                                            pGuiGraphics.drawCenteredString(this.font, clanCat.catAge, 0, textY, 0xFFaaaaaa);
                                            textY += textSpacing;
                                            pGuiGraphics.drawCenteredString(this.font, Component.translatable("screen.clan.last_seen",clanCat.lastSeen.toShortString()), 0, textY, 0xFFaaaaaa);
                                        }
                                        pGuiGraphics.pose().popPose();
                                    });

                            subNPCSquare.addWidget(npcRenderable);


                            int yMenuButtons = (subNPCSquare.getOriginalYPos0() + subNPCSquare.getHeight()/2) + 10;
                            int xMenuButtons = (subNPCSquare.getLeft() + subNPCSquare.getWidth()/2) - menuButtonsWidth/2;
                            float menuButtonsScale = 0.6f;


                            FancySimpleButton kickNPCbutton = new FancySimpleButton(
                                    menuButtonsWidth, menuButtonsHeight,
                                    xMenuButtons, yMenuButtons,
                                    Component.translatable("screen.clanmanage.kickplayer"),
                                    but -> {

                                        toggleSubMenu(square, shadow, true);

                                        FancySubRenderablesSquare subSquare = createSubSquare(60,-40);
                                        addTitleToSquare(subSquare, Component.translatable(
                                                "screen.clan.confirm_npc_kick",
                                                clanCat.catName
                                        ));

                                        squareConfirmationButtons(subSquare,
                                                cancel -> {
                                                    toggleSubMenu(square, shadow, false);
                                                    this.removeWidget(subSquare);
                                                },
                                                confirm -> {

                                                    this.enqueue(() -> {
                                                        ModPackets.sendToServer(new KickNPCPacket(clanCat.catUUID, clan.getClan().clanUUID, "kick"));
                                                    });
                                                    toggleSubMenu(square, shadow, false);
                                                    this.removeWidget(subSquare);
                                                }
                                        );
                                        this.addRenderableWidget(subSquare);

                                    }, menuButtonsScale, true
                            );
                            subNPCSquare.addWidget(kickNPCbutton);

                            yMenuButtons += menuButtonsHeight + 5;

                            FancySimpleButton disownNPCbutton = new FancySimpleButton(
                                    menuButtonsWidth, menuButtonsHeight,
                                    xMenuButtons, yMenuButtons,
                                    Component.translatable("screen.clan.disown_cat"),
                                    but -> {

                                        toggleSubMenu(square, shadow, true);

                                        FancySubRenderablesSquare subSquare = createSubSquare(60,-40);
                                        addTitleToSquare(subSquare, Component.translatable(
                                                "screen.clan.confirm_disown_cat",
                                                clanCat.catName
                                        ));

                                        squareConfirmationButtons(subSquare,
                                                cancel -> {
                                                    toggleSubMenu(square, shadow, false);
                                                    this.removeWidget(subSquare);
                                                },
                                                confirm -> {
                                                    this.enqueue(() -> {
                                                        ModPackets.sendToServer(new KickNPCPacket(clanCat.catUUID, clan.getClan().clanUUID, "disown"));
                                                    });
                                                    toggleSubMenu(square, shadow, false);
                                                    this.removeWidget(subSquare);
                                                }
                                        );
                                        this.addRenderableWidget(subSquare);

                                    }, menuButtonsScale, true
                            );
                            subNPCSquare.addWidget(disownNPCbutton);

                            square.addWidget(subNPCSquare);
                        }
                );
            }

            square.addWidget(npcList);

            y += listHeight + 10;
        }


        this.addRenderableWidget(square);
    }

    private void managePlayerSubsquareWidgets(ClanObjects.Player player, FancySubRenderablesSquare subPlayerSquare, FancySubRenderablesScrollSquare square) {
        playerSubsquareBaseWidgets(player, subPlayerSquare, square);

        FancyLambdaRenderable playerRenderable2 = new FancyLambdaRenderable(
                subPlayerSquare.getLeft() + subPlayerSquare.getWidth() / 2, subPlayerSquare.getOriginalYPos0(), 0,
                (pGuiGraphics, mouseX, mouseY, pPartialTick) -> {
                    pGuiGraphics.pose().pushPose();
                    float scale = 0.6f;
                    pGuiGraphics.pose().scale(scale, scale, scale);
                    int textY = 50;
                    int textSpacing = 10;

                    ClanData.Clan.Member member = clan.getMember(player.getUUID());
                    if (member != null) {
                        pGuiGraphics.drawCenteredString(this.font, member.getPermissions().getString(), 0, textY, 0xFFaaaaaa);
                        textY += textSpacing;
                        pGuiGraphics.drawCenteredString(this.font, member.getRank().getString(), 0, textY, 0xFFaaaaaa);
                    }
                    pGuiGraphics.pose().popPose();
                });
        subPlayerSquare.addWidget(playerRenderable2);


        int menuButtonsWidth = (int) (subPlayerSquare.getWidth()*0.7);
        int menuButtonsHeight = 8;
        int yMenuButtons = (subPlayerSquare.getOriginalYPos0() + subPlayerSquare.getHeight()/2) - 20;
        int xMenuButtons = (subPlayerSquare.getLeft() + subPlayerSquare.getWidth()/2) - menuButtonsWidth/2;
        int ySpacing = 5 + menuButtonsHeight;
        float menuButtonsScale = 0.6f;

        FancySimpleButton changePermsMenuButton = new FancySimpleButton(
                menuButtonsWidth, menuButtonsHeight,
                xMenuButtons, yMenuButtons,
                Component.translatable("screen.clanmanage.changeperms"),
                but -> {
                    subPlayerSquare.clearWidgets();
                    square.removeWidget(subPlayerSquare);

                    playerSubsquareBaseWidgets(player, subPlayerSquare, square);
                    changePermsPlayerSubmenu(player, subPlayerSquare, square,
                            menuButtonsWidth, menuButtonsHeight, xMenuButtons,
                            menuButtonsScale, ySpacing);

                    square.addWidget(subPlayerSquare);
                }, menuButtonsScale, true
        );
        subPlayerSquare.addWidget(changePermsMenuButton);
        yMenuButtons+= ySpacing;

        FancySimpleButton changeRankMenuButton = new FancySimpleButton(
                menuButtonsWidth, menuButtonsHeight,
                xMenuButtons, yMenuButtons,
                Component.translatable("screen.clanmanage.changerank"),
                but -> {
                    subPlayerSquare.clearWidgets();
                    square.removeWidget(subPlayerSquare);

                    playerSubsquareBaseWidgets(player, subPlayerSquare, square);
                    changeRankPlayerSubmenu(player, subPlayerSquare, square,
                            menuButtonsWidth, menuButtonsHeight, xMenuButtons,
                            menuButtonsScale, ySpacing);

                    square.addWidget(subPlayerSquare);
                }, menuButtonsScale, true
        );
        subPlayerSquare.addWidget(changeRankMenuButton);
        yMenuButtons+= ySpacing;

        FancySimpleButton kickMenuButton = new FancySimpleButton(
                menuButtonsWidth, menuButtonsHeight,
                xMenuButtons, yMenuButtons,
                Component.translatable("screen.clanmanage.kickplayer"),
                but -> {

                    FancyLambdaRenderable shadow = new FancyLambdaRenderable(
                            (pGuiGraphics, mouseX, mouseY, pPartialTick) -> {
                                pGuiGraphics.pose().pushPose();
                                pGuiGraphics.pose().translate(0,0,1500);
                                pGuiGraphics.fill(0,0, width, height, 0x66000000);
                                pGuiGraphics.pose().popPose();
                            });

                    toggleSubMenu(square, shadow, true);

                    FancySubRenderablesSquare subSquare = createSubSquare(60,-40);
                    addTitleToSquare(subSquare, Component.translatable(
                            "screen.clan.confirm_kick",
                            Component.literal(player.getMorphName()).withStyle(ChatFormatting.GOLD),
                            Component.literal(clan.getClan().name).withStyle(Style.EMPTY.withColor(clan.getClan().color))
                    ));

                    squareConfirmationButtons(subSquare,
                            cancel -> {
                                toggleSubMenu(square, shadow, false);
                                this.removeWidget(subSquare);
                            },
                            confirm -> {

                                this.enqueue(() -> {
                                    ModPackets.sendToServer(new CtSManageClanMemberPacket("kick", "none",
                                            player.getUUID(), player.getMorphName()));
                                });
                                toggleSubMenu(square, shadow, false);
                                this.removeWidget(subSquare);
                            }
                    );

                    this.addRenderableWidget(subSquare);

                }, menuButtonsScale, true
        );
        subPlayerSquare.addWidget(kickMenuButton);
        yMenuButtons+= ySpacing;


    }

    private void changePermsPlayerSubmenu(ClanObjects.Player player, FancySubRenderablesSquare subPlayerSquare,
                                          FancySubRenderablesScrollSquare square, int menuButtonsWidth,
                                          int menuButtonsHeight, int xMenuButtons, float menuButtonsScale, int ySpacing) {

        int ySubMenu = (subPlayerSquare.getOriginalYPos0() + subPlayerSquare.getHeight()/2) - 40;
        FancySimpleButton makeAdmin = new FancySimpleButton(
                menuButtonsWidth, menuButtonsHeight,
                xMenuButtons, ySubMenu,
                Component.translatable("screen.clanmanage.make_admin"),
                subBut -> {
                    subPlayerSquare.clearWidgets();
                    square.removeWidget(subPlayerSquare);

                    this.enqueue(() -> {
                        ModPackets.sendToServer(new CtSManageClanMemberPacket(
                                "changeperms",
                                "admin",
                                player.getUUID(),
                                player.getMorphName()));
                    });

                    managePlayerSubsquareWidgets(player, subPlayerSquare, square);

                    square.addWidget(subPlayerSquare);
                }, menuButtonsScale, true
        );
        subPlayerSquare.addWidget(makeAdmin);
        ySubMenu += ySpacing;

        FancySimpleButton makeMember = new FancySimpleButton(
                menuButtonsWidth, menuButtonsHeight,
                xMenuButtons, ySubMenu,
                Component.translatable("screen.clanmanage.make_member"),
                subBut -> {
                    subPlayerSquare.clearWidgets();
                    square.removeWidget(subPlayerSquare);

                    this.enqueue(() -> {
                        ModPackets.sendToServer(new CtSManageClanMemberPacket(
                                "changeperms",
                                "member",
                                player.getUUID(),
                                player.getMorphName()));
                    });

                    managePlayerSubsquareWidgets(player, subPlayerSquare, square);

                    square.addWidget(subPlayerSquare);
                }, menuButtonsScale, true
        );
        subPlayerSquare.addWidget(makeMember);
        ySubMenu += ySpacing;

        FancySimpleButton makeGuest = new FancySimpleButton(
                menuButtonsWidth, menuButtonsHeight,
                xMenuButtons, ySubMenu,
                Component.translatable("screen.clanmanage.make_guest"),
                subBut -> {
                    subPlayerSquare.clearWidgets();
                    square.removeWidget(subPlayerSquare);

                    this.enqueue(() -> {
                        ModPackets.sendToServer(new CtSManageClanMemberPacket(
                                "changeperms",
                                "guest",
                                player.getUUID(),
                                player.getMorphName()));
                    });

                    managePlayerSubsquareWidgets(player, subPlayerSquare, square);

                    square.addWidget(subPlayerSquare);
                }, menuButtonsScale, true
        );
        subPlayerSquare.addWidget(makeGuest);


        FancySimpleButton backButton = new FancySimpleButton(
                15, 8,
                subPlayerSquare.getRight() - 15, subPlayerSquare.getOriginalYPos0() - 8,
                Component.literal("←"),
                button -> {
                    subPlayerSquare.clearWidgets();
                    square.removeWidget(subPlayerSquare);
                    managePlayerSubsquareWidgets(player, subPlayerSquare, square);
                    square.addWidget(subPlayerSquare);
                }, 0.8f
        );
        subPlayerSquare.addWidget(backButton);
    }

    private void changeRankPlayerSubmenu(ClanObjects.Player player, FancySubRenderablesSquare subPlayerSquare,
                                         FancySubRenderablesScrollSquare square, int menuButtonsWidth,
                                         int menuButtonsHeight, int xMenuButtons, float menuButtonsScale, int ySpacing) {

        int ySubMenu = (subPlayerSquare.getOriginalYPos0() + subPlayerSquare.getHeight()/2) - 40;

        List<Component> buttonNames = List.of(
                Component.translatable("screen.clanmanage.make_kit"),
                Component.translatable("screen.clanmanage.make_app"),
                Component.translatable("screen.clanmanage.make_warrior"),
                Component.translatable("screen.clanmanage.make_elder"),
                Component.translatable("screen.clanmanage.make_queen"),
                Component.translatable("screen.clanmanage.make_med_app"),
                Component.translatable("screen.clanmanage.make_medicine"),
                Component.translatable("screen.clanmanage.make_deputy")
        );

        List<String> rankKeys = List.of(
                "kit",
                "apprentice",
                "warrior",
                "elder",
                "queen",
                "medapp",
                "medicine",
                "deputy"
        );

        for (int i = 0; i < rankKeys.size(); i++) {

            int finalI = i;
            FancySimpleButton toAdd = new FancySimpleButton(
                    menuButtonsWidth, menuButtonsHeight,
                    xMenuButtons, ySubMenu,
                    buttonNames.get(finalI),
                    subBut -> {
                        subPlayerSquare.clearWidgets();
                        square.removeWidget(subPlayerSquare);

                        this.enqueue(() -> {
                            ModPackets.sendToServer(new CtSManageClanMemberPacket(
                                    "changerank",
                                    rankKeys.get(finalI),
                                    player.getUUID(),
                                    player.getMorphName()));
                        });

                        managePlayerSubsquareWidgets(player, subPlayerSquare, square);

                        square.addWidget(subPlayerSquare);
                    }, menuButtonsScale, true
            );
            subPlayerSquare.addWidget(toAdd);
            ySubMenu += ySpacing;
        }


        FancySimpleButton backButton = new FancySimpleButton(
                15, 8,
                subPlayerSquare.getRight() - 15, subPlayerSquare.getOriginalYPos0() - 8,
                Component.literal("←"),
                button -> {
                    subPlayerSquare.clearWidgets();
                    square.removeWidget(subPlayerSquare);
                    managePlayerSubsquareWidgets(player, subPlayerSquare, square);
                    square.addWidget(subPlayerSquare);
                }, 0.8f
        );
        subPlayerSquare.addWidget(backButton);


    }


    private void playerSubsquareBaseWidgets(ClanObjects.Player player, FancySubRenderablesSquare subPlayerSquare, FancySubRenderablesScrollSquare square) {
        FancyStringWidget playerNameplate = new FancyStringWidget(
                Component.literal(player.getMorphName()),
                subPlayerSquare.getLeft(), subPlayerSquare.getRight(),
                subPlayerSquare.getOriginalYPos0() + 5,
                0.9f, true, 0
        );

        subPlayerSquare.addWidget(playerNameplate);

        FancySimpleButton closeButton = new FancySimpleButton(
                15, 8,
                subPlayerSquare.getLeft(), subPlayerSquare.getOriginalYPos0() - 8,
                Component.literal("×"),
                button -> {
                    subPlayerSquare.clearWidgets();
                    square.removeWidget(subPlayerSquare);
                }, 0.8f
        );
        subPlayerSquare.addWidget(closeButton);

        FancyLambdaRenderable playerRenderable = new FancyLambdaRenderable(
                subPlayerSquare.getLeft() + subPlayerSquare.getWidth() / 2, subPlayerSquare.getOriginalYPos0(), 0,
                (pGuiGraphics, mouseX, mouseY, pPartialTick) -> {
                    pGuiGraphics.pose().pushPose();
                    float scale = 0.6f;
                    pGuiGraphics.pose().scale(scale, scale, scale);
                    int textY = 30;
                    pGuiGraphics.drawCenteredString(this.font,"<" + player.getUsername() + ">", 0, textY, 0xFFaaaaaa);

                    pGuiGraphics.pose().popPose();
                });

        subPlayerSquare.addWidget(playerRenderable);
    }

    public static float[] ensureMinLuminance(float r, float g, float b, float minBrightness) {
        float brightness = colorBrightness(r, g, b, minBrightness);
        if (brightness >= minBrightness || brightness <= 0f) {
            if (brightness <= 0f) {
                float boost = minBrightness;
                return new float[]{boost, boost, boost};
            }
            return new float[]{r, g, b};
        }
        float scale = minBrightness / brightness;
        return new float[]{
                Math.min(1f, r * scale),
                Math.min(1f, g * scale),
                Math.min(1f, b * scale)
        };
    }

    private static float colorBrightness(float r, float g, float b, float min) {
        float multiplier = 1f - min;
        return multiplier * r + multiplier * g + multiplier * b;
    }

    private FancySubRenderablesSquare createSubSquare(int xExtension, int yExtension) {

        int centerX = width / 2;
        int centerY = height / 2;

        int subSquareWidth = 200 + xExtension;
        int subSquareHeight = 150 + yExtension;
        int subSquareX1 = centerX - subSquareWidth/2;
        int subSquareY1 = centerY - (subSquareHeight/2);


        FancySubRenderablesSquare subSquare = new FancySubRenderablesSquare(
                subSquareX1, subSquareY1,
                subSquareWidth, subSquareHeight
        );

        subSquare.setZ(1500);

        return subSquare;
    }

    private void squareConfirmationButtons(FancySubRenderablesSquare subSquare,
                                           Consumer<FancySimpleButton> cancelAction,
                                           Consumer<FancySimpleButton> confirmAction) {

        int squareWidth = subSquare.getWidth();
        int squareCenterX = subSquare.getLeft() + squareWidth/2;
        int buttonsY = subSquare.getOriginalYPos1() - 30;

        int buttonsWidth = (squareWidth - 30) /2;
        int buttonsHeight = 15;

        FancySimpleButton cancel = new FancySimpleButton(
                buttonsWidth, buttonsHeight,
                squareCenterX - 5 - buttonsWidth, buttonsY,
                Component.empty()
                        .append("❌ ")
                        .append(Component.translatable("screen.clanmanage.cancel")),
                cancelAction::accept,0.9f, true
        );

        FancySimpleButton confirm = new FancySimpleButton(
                buttonsWidth, buttonsHeight,
                squareCenterX + 5, buttonsY,
                Component.empty()
                        .append("✔ ")
                        .append(Component.translatable("screen.clanmanage.confirm")),
                confirmAction::accept,0.9f, true
        );

        subSquare.addWidget(cancel);
        subSquare.addWidget(confirm);
    }

    private void toggleSubMenu(FancySubRenderablesScrollSquare square, FancyLambdaRenderable shadow, boolean value) {
        if (value) {
            square.setActive(false);
            this.addRenderableWidget(shadow);
        } else {
            square.setActive(true);
            this.removeWidget(shadow);
        }
    }

    private void addTitleToSquare(FancySubRenderablesSquare square, Component text) {
        FancyStringWidget title = new FancyStringWidget(
                text,
                square.getLeft() + 10, square.getRight() - 10,
                square.getOriginalYPos0() + 10,
                1f, true, square.getHeight() - 24
        );

        square.addWidget(title);
    }

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);

        if (menuY < targetY) {
            menuY += (targetY - menuY) * 0.10f;
            if (menuY > targetY) menuY = targetY;
        }

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(0, menuY, 0);

        for (Renderable renderable : this.renderables) {
            if (renderable instanceof SubRenderable sub) {
                sub.adjustYPos((int) menuY);
            }
        }

        int menuWidth = menuMaxX - menuMinX;
        int menuHeight = menuMaxY - menuMinY;

        {
            pGuiGraphics.fill(
                    menuMinX, menuMinY,
                    menuMaxX, menuMaxY,
                    clan.getClan().color);

            pGuiGraphics.fillGradient(
                    menuMinX, menuMinY,
                    menuMaxX, menuMinY + menuHeight / 2,
                    0xbd000000, 0x01000000);
            pGuiGraphics.fillGradient(
                    menuMinX, menuMinY + menuHeight / 2,
                    menuMaxX, menuMaxY,
                    0x01000000, 0xbd000000);

            pGuiGraphics.renderOutline(
                    menuMinX - 1, menuMinY - 1,
                    menuWidth + 2,
                    menuHeight + 2,
                    clan.getClan().color);

            pGuiGraphics.renderOutline(
                    menuMinX, menuMinY,
                    menuWidth,
                    menuHeight,
                    0xccBF9728
            );

        }

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        pGuiGraphics.pose().popPose();

    }

    private void drawMainButtons(){
        this.clearWidgets();

        this.addRenderableWidget(logsButton);
        this.addRenderableWidget(membersButton);
        this.addRenderableWidget(clanButton);
        this.addRenderableWidget(invitesButton);
        this.addRenderableWidget(manageButton);

        this.addRenderableWidget(backButton);

    }

    private void selectMenuTab(FancySelectableButton selected, ClanTab tab) {
        this.membersButton.setSelected(false);
        this.logsButton.setSelected(false);
        this.clanButton.setSelected(false);
        this.invitesButton.setSelected(false);
        this.manageButton.setSelected(false);

        this.selectedTab = tab;

        selected.setSelected(true);
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

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public int getErrorXOffset() {
        return 0;
    }

    @Override
    public int getErrorYOffset() {
        return -75;
    }

    @Override
    public float getErrorScale() {
        return 0.7f;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {

        for (Renderable renderable : this.renderables) {
            if (renderable instanceof GuiEventListener listener) {
                if (listener.mouseScrolled(pMouseX, pMouseY, pDelta)) {
                    return true;
                }
            }
        }

        return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

        if (this.getFocused() instanceof EditBox box) {
            return box.keyPressed(keyCode, scanCode, modifiers);
        } else if (this.getFocused() instanceof FancySubRenderablesSquare square) {
            if (square.getFocusedWidget() instanceof EditBox box) {
                return box.keyPressed(keyCode, scanCode, modifiers);
            }
        }

        if (keyCode == Minecraft.getInstance().options.keyInventory.getKey().getValue()) {
            this.onClose();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

}
