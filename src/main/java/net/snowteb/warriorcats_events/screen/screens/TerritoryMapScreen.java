package net.snowteb.warriorcats_events.screen.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.client.ClientTerritoryData;
import net.snowteb.warriorcats_events.screen.widgets.FloatSliderButton;
import net.snowteb.warriorcats_events.screen.widgets.GradientButton;
import net.snowteb.warriorcats_events.zconfig.WCEServerConfig;

import java.util.*;

import static net.snowteb.warriorcats_events.client.ClientTerritoryData.CLIENT_TERRITORIES;

public class TerritoryMapScreen extends Screen {

    private Screen parent;
    private Map<ChunkPos ,ClientRenderTerritory> territoryMap = new HashMap<>();

    private ChunkPos playerPosition;

    private FloatSliderButton scaleSlider;
    private GradientButton backButton;

    private List<Component> clanList = new ArrayList<>();

    public TerritoryMapScreen(Screen parent) {
        super(Component.literal("Territory Map"));
        this.parent = parent;
    }

    @Override
    protected void init() {

        int centerX = width / 2;
        int centerY = height / 2;

        clanList.clear();
        clanList.add(Component.translatable("screen.map.clanlist"));
        clanList.add(Component.empty());
        territoryMap.clear();

        for (ClientTerritoryData.ClientClanTerritories entry : CLIENT_TERRITORIES.values()) {
            for (ClientTerritoryData.ClientChunk clientChunk : entry.claimedTerritory()) {
                boolean isCore = clientChunk.chunkPos.equals(entry.core());

                float maxTime = Math.max(1, WCEServerConfig.SERVER.MAX_TERRITORY_TIME.get()*60*20);
                float progress = (float) clientChunk.time / maxTime;

                int alpha = isCore ? 255 : (int) Mth.clamp(255 * progress, 3, 255);

                String chunkName = clientChunk.name;
                if (isCore) chunkName = "\uD83C\uDF1F " + entry.clanName() + " \uD83C\uDF1F";

                territoryMap.put(clientChunk.chunkPos, new ClientRenderTerritory(clientChunk.chunkPos, chunkName, entry.color(), isCore, alpha));
            }
            clanList.add(Component.literal(entry.clanName()).withStyle(Style.EMPTY.withColor(entry.color())));
        }



        if (Minecraft.getInstance().player != null) {
            LocalPlayer localPlayer = Minecraft.getInstance().player;
            playerPosition = localPlayer.chunkPosition();

            if (localPlayer.level().dimension() != Level.OVERWORLD) this.onClose();
        }

        if (playerPosition == null) this.onClose();

        scaleSlider = new FloatSliderButton(centerX + 50, centerY - 110,
                100, 20,
                0.6f, 1.5f, 1f, Component.translatable("screen.map.scale").getString());

        backButton = new GradientButton(
                centerX - 150, centerY + 75,
                50, 15,
                Component.translatable("screen.map.return"),
                btn -> {
                    this.onClose();
                }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                80, 20, 0xFFFFFFFF, 0.83f
        );

        this.addRenderableWidget(scaleSlider);
        this.addRenderableWidget(backButton);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {

        int centerX = width / 2;
        int centerY = height / 2;

        this.renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        {
            pGuiGraphics.pose().pushPose();

            pGuiGraphics.pose().translate(0, -10, 0);

            pGuiGraphics.fill(centerX - 155, centerY - 105, centerX + 155, centerY + 105, 0xFF202020);
            pGuiGraphics.renderOutline(centerX - 155, centerY - 105, 310, 210, 0x33ffffff);


            pGuiGraphics.enableScissor(centerX - 150, centerY - 110, centerX + 200, centerY + 90);

            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate(centerX, centerY, 0);

            float scale = 1f;
            if (scaleSlider != null) scale = scaleSlider.getActualValue();
            pGuiGraphics.pose().scale(scale, scale, scale);

            int maxChunksX = (int) (width / (8 * scale) / 4);
            int maxChunksZ = (int) (height / (8 * scale) / 3);

            for (int i = -maxChunksX; i <= maxChunksX; i++) {

                for (int j = -maxChunksZ; j <= maxChunksZ; j++) {
                    ChunkPos chunkPos = new ChunkPos(playerPosition.x + i, playerPosition.z + j);
                    if (territoryMap.containsKey(chunkPos)) {
                        int color = territoryMap.get(chunkPos).color;
                        color = (territoryMap.get(chunkPos).timeAlpha << 24) | (color & 0x00FFFFFF);

                        float localMouseX = (pMouseX - (centerX)) / scale;
                        float localMouseY = (pMouseY - (centerY - 10)) / scale;

                        boolean mouseOver = localMouseX >= i*10 - 5 && localMouseX <= i*10 + 4 &&
                                localMouseY >= j*10 - 5 && localMouseY <= j*10 + 4;

                        if (mouseOver) {
                            int colorToShow = territoryMap.get(chunkPos).color;
                            String textToShow = territoryMap.get(chunkPos).territoryName;
                            if (!textToShow.isEmpty()) {
                                pGuiGraphics.renderTooltip(this.font, Component.literal(textToShow).withStyle(Style.EMPTY.withColor(colorToShow)),
                                        (int) localMouseX, (int) localMouseY);
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


                    } else {
                        pGuiGraphics.fill(0 + i * 10 - 4, 0 + j * 10 - 4,
                                0 + i * 10 + 4, 0 + j * 10 + 4,
                                0x22000000);

                    }
                }
            }


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

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(centerX - 150, centerY - 110, 0);
        float scale = 1.2f;
        pGuiGraphics.pose().scale(scale, scale, scale);
        pGuiGraphics.drawString(this.font, Component.translatable("screen.map"), 0, 0, 0xFFFFFFFF);

        pGuiGraphics.pose().popPose();


        pGuiGraphics.pose().pushPose();


        boolean shouldShowList = pMouseX > centerX - 62 && pMouseX < centerX - 12
                                 && pMouseY > centerY - 113 && pMouseY < centerY - 97;
        pGuiGraphics.renderTooltip(this.font, Component.translatable("screen.map.clanlist_u"), centerX - 70, centerY - 97);

        pGuiGraphics.pose().translate(centerX - 70, centerY - 97, 0);
        float tooltipScale = 0.8f;
        pGuiGraphics.pose().scale(tooltipScale, tooltipScale, tooltipScale);

        int localMouseX2 = (int) ((pMouseX - (centerX - 70)) / tooltipScale);
        int localMouseY2 = (int) ((pMouseY - (centerY - 97)) / tooltipScale);

        if (shouldShowList) {
            pGuiGraphics.pose().translate(0, 0, 400);
            pGuiGraphics.renderTooltip(this.font, clanList, Optional.empty(), localMouseX2 - 5, localMouseY2 + 15);
        }
        pGuiGraphics.pose().popPose();

        for(Renderable renderable : this.renderables) {
            renderable.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        }

    }

    @Override
    public void onClose() {
        super.onClose();
        Minecraft.getInstance().setScreen(parent);
    }

    @Override
    public void tick() {
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer != null) {
            playerPosition = localPlayer.chunkPosition();
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private record ClientRenderTerritory(ChunkPos pos, String territoryName, int color, boolean isCore, int timeAlpha){}
}
