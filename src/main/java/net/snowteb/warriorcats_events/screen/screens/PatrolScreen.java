package net.snowteb.warriorcats_events.screen.screens;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.snowteb.warriorcats_events.client.ClientTerritoryData;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.c2s.cats.CtSSendPatrol;
import net.snowteb.warriorcats_events.network.packet.c2s.cats.RetrieveLastCatModePacket;
import net.snowteb.warriorcats_events.screen.widgets.CatSelectionScrollList;
import net.snowteb.warriorcats_events.screen.widgets.TerritorySelectionScrollList;
import net.snowteb.warriorcats_events.screen.widgets.FloatSliderButton;
import net.snowteb.warriorcats_events.zconfig.WCEServerConfig;

import java.util.*;
import java.util.List;

import static net.snowteb.warriorcats_events.client.ClientTerritoryData.CLIENT_TERRITORIES;
import static net.snowteb.warriorcats_events.client.ClientTerritoryData.isAdjacentToCore;

public class PatrolScreen extends Screen {
    private final List<WCatEntity> cats;
    private List<ChunkPos> selected = new ArrayList<>();

    private boolean clickedPos = false;

    private record ClientRenderTerritory(ChunkPos pos, String territoryName, int color, boolean isCore, int timeAlpha){}

    private Map<ChunkPos ,ClientRenderTerritory> territoryMap = new HashMap<>();
    private ChunkPos core;


    private FloatSliderButton scaleSlider;
    private final LocalPlayer localPlayer;

    private final UUID clanUUID;

    private Button backButton;
    private Button sendBorderPatrol;
    private Button sendHuntingPatrol;

    private TerritorySelectionScrollList selectedChunks;

    private CatSelectionScrollList catSelection;

    private final int currentCatID;

    private int displayErrorTime = 0;
    private String displayErrorText = "";
    private boolean isAnError = false;

    private final boolean isPlayerDeputy;

    public PatrolScreen(List<WCatEntity> cats, UUID clanUUID, int deputyID, boolean deputy) {
        super(Component.empty());
        this.cats = cats;
        this.localPlayer = Minecraft.getInstance().player;
        this.clanUUID = clanUUID;
        this.currentCatID = deputyID;
        this.isPlayerDeputy = deputy;
    }

    @Override
    protected void init() {
        super.init();

        if (localPlayer == null) {
            Minecraft.getInstance().setScreen(null);
            return;
        }

        int centerX = width / 2;
        int centerY = height / 2;

        scaleSlider = new FloatSliderButton(centerX - 75, centerY - 105,
                100, 20,
                0.6f, 1.0f, 0.8f, Component.translatable("screen.patrol.scale").getString());

        ClientTerritoryData.ClientClanTerritories territory = CLIENT_TERRITORIES.get(clanUUID);


        for (ClientTerritoryData.ClientChunk clientChunk : territory.claimedTerritory()) {
            boolean isCore = clientChunk.chunkPos.equals(territory.core());

            float maxTime = Math.max(1, WCEServerConfig.SERVER.MAX_TERRITORY_TIME.get() * 60 * 20);
            float progress = (float) clientChunk.time / maxTime;

            int alpha = isCore ? 255 : (int) Mth.clamp(255 * progress, 3, 255);

            String chunkName = clientChunk.name;
            if (isCore) {
                chunkName = "\uD83C\uDF1F " + territory.clanName() + " \uD83C\uDF1F";
                core = clientChunk.chunkPos;
            }
            boolean isInDistance = Math.abs(clientChunk.chunkPos.x - localPlayer.chunkPosition().x) < 8 &&
                            Math.abs(clientChunk.chunkPos.z - localPlayer.chunkPosition().z) < 8;

            if (isInDistance) {
                territoryMap.put(clientChunk.chunkPos, new ClientRenderTerritory(clientChunk.chunkPos, chunkName, territory.color(), isCore, alpha));
            }
        }

        backButton = Button.builder(
                Component.translatable("screen.patrol.close"),
                btn -> {
                    Entity ent = Minecraft.getInstance().level.getEntity(currentCatID);
                    if (ent instanceof WCatEntity) {
                        Minecraft.getInstance().setScreen(new CatDataScreen(Component.empty(), (WCatEntity) ent, isPlayerDeputy));
                    }
                }
        ).bounds(10, this.height - 30, 60, 20).build();

        selectedChunks = new TerritorySelectionScrollList(Minecraft.getInstance(),
                110, 250,
                centerY - 110, centerY - 10,
                50, clanUUID);
        selectedChunks.setLinkedList(new ArrayList<>(this.selected));
        selectedChunks.updateSelection();
        selectedChunks.setRenderBackground(true);
        selectedChunks.setRenderTopAndBottom(false);
        selectedChunks.setLeftPos(centerX + 90 - 50);


        catSelection = new CatSelectionScrollList(Minecraft.getInstance(),
                110, 250,
                centerY + 0, centerY + 65,
                40);

        List<Integer> ids = new ArrayList<>();
        for (WCatEntity cat : cats) {
            ids.add(cat.getId());
        }

        catSelection.setAvailableIDs(ids);
        catSelection.setRenderBackground(true);
        catSelection.setRenderTopAndBottom(false);
        catSelection.setLeftPos(centerX + 90 - 50);

        sendBorderPatrol = Button.builder(
                Component.translatable("screen.patrol.border_patrol"),
                btn -> {
                    sendPatrol(0);
                }
        ).bounds(centerX-85, this.height - 30, 80, 20).build();

        sendHuntingPatrol = Button.builder(
                Component.translatable("screen.patrol.hunting_patrol"),
                btn -> {
                    sendPatrol(1);
                }
        ).bounds(centerX+5, this.height - 30, 80, 20).build();

        this.addRenderableWidget(scaleSlider);
        this.addRenderableWidget(backButton);
        this.addRenderableWidget(selectedChunks);
        if (!isPlayerDeputy) this.addRenderableWidget(catSelection);
        this.addRenderableWidget(sendBorderPatrol);
        this.addRenderableWidget(sendHuntingPatrol);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {

        int centerX = width / 2;
        int centerY = height / 2;

        this.renderBackground(pGuiGraphics);

        int offset = -50;

        {
            pGuiGraphics.pose().pushPose();

            pGuiGraphics.pose().translate(0, -20, 0);

            pGuiGraphics.fill(centerX - 80 + offset, centerY - 90, centerX + 80 + offset, centerY + 85, 0xee202020);
            pGuiGraphics.renderOutline(centerX - 80 + offset, centerY - 90, 160, 175, 0x33ffffff);


            pGuiGraphics.enableScissor(centerX - 70 + offset, centerY - 80, centerX + 70 + offset, centerY + 70);

            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate(centerX + offset, centerY, 0);

            float scale = 1f;
            if (scaleSlider != null) scale = scaleSlider.getActualValue();
            pGuiGraphics.pose().scale(scale, scale, scale);

            int maxChunksX = (int) (width / (8 * scale) / 7);
            int maxChunksZ = (int) (height / (8 * scale) / 4);


            for (int i = -maxChunksX; i <= maxChunksX; i++) {

                for (int j = -maxChunksZ; j <= maxChunksZ; j++) {
                    ChunkPos chunkPos = new ChunkPos(localPlayer.chunkPosition().x + i, localPlayer.chunkPosition().z + j);

                    if (territoryMap.containsKey(chunkPos)) {
                        int color = territoryMap.get(chunkPos).color;
                        color = (territoryMap.get(chunkPos).timeAlpha << 24) | (color & 0x00FFFFFF);

                        float localMouseX = (pMouseX - (centerX + offset)) / scale;
                        float localMouseY = (pMouseY - (centerY - 20)) / scale;

                        boolean mouseOver = localMouseX >= i*10 - 5 && localMouseX <= i*10 + 4 &&
                                localMouseY >= j*10 - 5 && localMouseY <= j*10 + 4;

                        if (clickedPos) {
                            if (mouseOver) {
                                if (selected.contains(chunkPos)) {
                                    selected.remove(chunkPos);
                                } else {
                                    if (!territoryMap.get(chunkPos).isCore() || isAdjacentToCore(chunkPos, core)) {
                                        selected.add(chunkPos);
                                    }
                                }
                                selectedChunks.setLinkedList(new ArrayList<>(selected));
                                selectedChunks.updateSelection();
                                clickedPos = false;
                            }
                        }

                        if (mouseOver) {
                            pGuiGraphics.fill(0 + i * 10 - 4, 0 + j * 10 - 4,
                                    0 + i * 10 + 4, 0 + j * 10 + 4,
                                    0xccFFFFFF);

                            int colorToShow = territoryMap.get(chunkPos).color;
                            String textToShow = territoryMap.get(chunkPos).territoryName;
                            if (!textToShow.isEmpty()) {
                                pGuiGraphics.renderTooltip(this.font, Component.literal(textToShow).withStyle(Style.EMPTY.withColor(colorToShow)),
                                        (int) localMouseX - font.width(textToShow)/2 - 10, (int) localMouseY - 3);

                            }
                        }


                        pGuiGraphics.fill(0 + i * 10 - 4, 0 + j * 10 - 4,
                                0 + i * 10 + 4, 0 + j * 10 + 4,
                                color);

                        if (territoryMap.get(chunkPos).isCore()) {
                            pGuiGraphics.fill(0 + i * 10 - 4, 0 + j * 10 - 4,
                                    0 + i * 10 + 4, 0 + j * 10 + 4,
                                    0xaaFFFFFF);
                        }

                        if (selected.contains(chunkPos)) {
                            pGuiGraphics.renderOutline(0 + i * 10 - 4, 0 + j * 10 - 4,
                                    8, 8,
                                    0xFF3333FF);
                            pGuiGraphics.renderOutline(0 + i * 10 - 5, 0 + j * 10 - 5,
                                    10, 10,
                                    0xFFaa3333);
                        }


                    } else {
                        pGuiGraphics.fill(0 + i * 10 - 4, 0 + j * 10 - 4,
                                0 + i * 10 + 4, 0 + j * 10 + 4,
                                0x22000000);

                    }
                }
            }
            clickedPos = false;


            pGuiGraphics.fill(0 - 2, 0 - 2,
                    0 + 2, 0 + 2,
                    0xbb008888);
            pGuiGraphics.renderOutline(0 - 2, 0 - 2, 4, 4, 0xbbbb0000);
            pGuiGraphics.pose().scale(0.5f, 0.5f, 0.5f);
            pGuiGraphics.drawString(this.font, Component.translatable("screen.patrol.you_are_here"), 0, -8, 0xFFFFFFFF);

            pGuiGraphics.pose().popPose();

            pGuiGraphics.disableScissor();

            pGuiGraphics.pose().popPose();
        }



        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        displayPatrolStats(pGuiGraphics, centerX, centerY);

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(centerX + 100,centerY - 8,0);
        float scale = 0.8f;
        pGuiGraphics.pose().scale(scale, scale, scale);

        int catsSelectedCount = catSelection.getSelectedIDs().size();
        int chunksSelectedCount = selected.size();

        pGuiGraphics.drawCenteredString(this.font, Component.translatable("screen.patrol.cats_selected",catsSelectedCount), 0, 0, 0xaaFFFFFF);
        pGuiGraphics.drawCenteredString(this.font, Component.translatable("screen.patrol.chunks_selected",chunksSelectedCount), 0, -137, 0xaaFFFFFF);

        pGuiGraphics.pose().popPose();

        if (displayErrorTime > 0) {
            ChatFormatting color;
            if (isAnError) color = ChatFormatting.RED;
            else color = ChatFormatting.GREEN;
            int xOffset = font.width(displayErrorText)/2;
            int xO = centerX - xOffset - 12;

            float alpha = (float) displayErrorTime / 20;

            float finalAlpha = Mth.clamp(alpha, 0f, 1f);

            pGuiGraphics.setColor(1f,1f,1f, finalAlpha);
            pGuiGraphics.renderTooltip(font, Component.literal(displayErrorText).withStyle(color), xO, centerY + 100);
            pGuiGraphics.setColor(1f,1f,1f,1f);
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        clickedPos = true;

        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public void onClose() {
        ModPackets.sendToServer(new RetrieveLastCatModePacket(this.currentCatID));
        super.onClose();
    }

    private void sendPatrol(int patrolType) {
        List<Integer> entityIDs = new ArrayList<>(catSelection.getSelectedIDs());
        if (this.isPlayerDeputy) entityIDs.add(this.currentCatID);

        if (entityIDs.isEmpty()) {
            displayError(Component.translatable("screen.patrol.no_cats_selected").getString(), true);
            return;
        }

        List<ChunkPos> territoryToPatrol = new ArrayList<>(selected);
        if (territoryToPatrol.isEmpty()) {
            displayError(Component.translatable("screen.patrol.no_chunks_selected").getString(), true);
            return;
        }

//        ModPackets.sendToServer(new RetrieveLastCatModePacket(deputyID));

        ModPackets.sendToServer(new CtSSendPatrol(currentCatID, entityIDs, patrolType, territoryToPatrol, isPlayerDeputy));

        Minecraft.getInstance().setScreen(null);
    }

    private void displayPatrolStats(GuiGraphics pGuiGraphics, int centerX, int centerY) {
        if (catSelection.getSelectedIDs().isEmpty()) {
            return;
        }
        if (selected.isEmpty()) {
            return;
        }

        float danger = 0f;
        List<Integer> selectedCats = catSelection.getSelectedIDs();

        float pressure = (float) selected.size() / selectedCats.size();
        float avgTime = 0f;

        for (ChunkPos pos : selected) {
            ClientTerritoryData.ClientClanTerritories territory = CLIENT_TERRITORIES.get(clanUUID);
            if (territory == null) return;

            ClientTerritoryData.ClientChunk chunk = null;

            for (ClientTerritoryData.ClientChunk chunk1 : territory.claimedTerritory()) {
                if (chunk1.chunkPos.equals(pos)) {
                    chunk = chunk1;
                    break;
                }
            }
            if (chunk == null) return;

            float percentage = ((float) chunk.time / (WCEServerConfig.SERVER.MAX_TERRITORY_TIME.get()*20*60))*100;

            avgTime += percentage;

        }

        avgTime = avgTime / selected.size();

        danger += pressure * 0.4f;
        danger += (avgTime / 100f) * 0.4f;

        if (selectedCats.size() >= 5) {
            danger *= 0.6f;
        } else if (selectedCats.size() >= 4) {
            danger *= 0.8f;
        } else {
            danger *= 1.2f;
        }

        danger = Mth.clamp(danger, 0f, 1f);


        String dangerPercentage = String.format("%.1f", danger*100);
        String dangerText;
        ChatFormatting dangerColor;
        if (danger >= 0.9) {
            dangerText = Component.translatable("screen.patrol.high_danger").getString();
            dangerColor = ChatFormatting.RED;
        } else if (danger >= 0.6) {
            dangerText = Component.translatable("screen.patrol.moderate_danger").getString();
            dangerColor = ChatFormatting.GOLD;
        } else if (danger >= 0.3) {
            dangerText = Component.translatable("screen.patrol.medium_danger").getString();
            dangerColor = ChatFormatting.YELLOW;
        } else {
            dangerText = Component.translatable("screen.patrol.low_danger").getString();
            dangerColor = ChatFormatting.GREEN;
        }

        Component dangerComp = Component.translatable("screen.patrol.danger",
                        Component.literal(dangerText).withStyle(dangerColor)
                .append(Component.literal(" " + dangerPercentage + "%").withStyle(ChatFormatting.GRAY)));

        int timeForBorder = (int) (selected.size()*1.25D);
        int timeForHunting = (int) (selected.size()*2.25D);

        Component forBorder = Component.translatable("screen.patrol.estimated_time", timeForBorder);

        Component forHunting = Component.translatable("screen.patrol.estimated_time", timeForHunting);

        pGuiGraphics.drawCenteredString(this.font, dangerComp, centerX, this.height - 55, 0xFFFFFFFF);

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(centerX, this.height - 37, 0);
        float scale1 = 0.75f;
        pGuiGraphics.pose().scale(scale1, scale1, scale1);
        pGuiGraphics.drawCenteredString(this.font, forBorder, - 60, 0, 0xFFFFFFFF);

        pGuiGraphics.drawCenteredString(this.font, forHunting, + 60, 0, 0xFFFFFFFF);
        pGuiGraphics.pose().popPose();
    }


    private void displayError(String error, boolean isAnError) {
        displayErrorTime = 100;
        displayErrorText = error;
        this.isAnError = isAnError;
    }

    @Override
    public void tick() {
        if (displayErrorTime > 0) displayErrorTime--;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
