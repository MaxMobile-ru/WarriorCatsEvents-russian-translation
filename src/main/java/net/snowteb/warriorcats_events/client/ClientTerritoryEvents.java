package net.snowteb.warriorcats_events.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.compat.CompatibilitiesClient;
import net.snowteb.warriorcats_events.zconfig.WCEClientConfig;
import net.snowteb.warriorcats_events.zconfig.WCEServerConfig;

@Mod.EventBusSubscriber(modid = WarriorCatsEvents.MODID, value = Dist.CLIENT)
public class ClientTerritoryEvents {


    public static boolean isIsInATerritory() {
        return isInATerritory;
    }

    private static boolean isInATerritory = false;
    private static String clanName = "";
    private static int clanColor = 0;
    private static String territoryName = "";
    private static int territoryTime = 0;

    public static int currentBarWidth = 0;
    public static int currentBarStart;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer localPlayer = mc.player;
        if (localPlayer == null) return;

        ClientTerritoryData.clientTerritoryTick();

        if (localPlayer.tickCount % 10 == 0) {
            isInATerritory = false;

            for (ClientTerritoryData.ClientClanTerritories clanTerritory : ClientTerritoryData.CLIENT_TERRITORIES.values()) {
                for (ClientTerritoryData.ClientChunk chunk : clanTerritory.claimedTerritory()) {
                    if (localPlayer.chunkPosition().equals(chunk.chunkPos)) {
                        isInATerritory = true;
                        clanName = clanTerritory.clanName();
                        clanColor = clanTerritory.color();
                        territoryName = chunk.name;
                        territoryTime = chunk.time;
                        break;
                    }
                }
                if (isInATerritory) break;
            }
        }

    }

    @SubscribeEvent
    public static void overlayRender(RenderGuiOverlayEvent.Post event) {
        LocalPlayer player = Minecraft.getInstance().player;
        Minecraft mcinstance = Minecraft.getInstance();
        if (player == null) return;
        if (!mcinstance.isWindowActive()) return;
        if (mcinstance.screen != null) return;
        if (mcinstance.level == null) return;

        if (!WCEClientConfig.CLIENT.DISPLAY_TERRITORY.get()) return;
        GuiGraphics pGuiGraphics = event.getGuiGraphics();

        pGuiGraphics.pose().pushPose();
        if (CompatibilitiesClient.shouldRenderSereneSeasonsOverlay(mcinstance.level)) {
            pGuiGraphics.pose().translate(0, 15, 0);
        }

        if (isInATerritory) {

            int width = mcinstance.getWindow().getGuiScaledWidth();
            int height = mcinstance.getWindow().getGuiScaledHeight();
            int centerX = width / 2;
            int centerY = height / 2 + 10;

            String text1 = Component.translatable("blockentity.clan.clanterritory", clanName).getString();
            String text2 = territoryName;
            int extraY = text2.isEmpty() ? 0 : 7;

            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate(width - mcinstance.font.width(text1) - 20, 5, 0);
            float scale = 1.1f;
            pGuiGraphics.pose().scale(scale, scale, scale);
            pGuiGraphics.drawString(mcinstance.font, text1, 0, 0, clanColor);
            pGuiGraphics.pose().popPose();

            int barStart = width - mcinstance.font.width(text1) - 20;
            currentBarStart = barStart;
            int barEndMax = width - 5;

            float maxTime = Math.max(1, WCEServerConfig.SERVER.MAX_TERRITORY_TIME.get()*60*20);

            int barWidth = barEndMax - barStart;
            currentBarWidth = barWidth;
            float progress = (float) territoryTime / maxTime;
            int barEnd = barStart + (int)(barWidth * progress);

            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate(0, extraY - 5, 0);

            pGuiGraphics.fill(barStart, 26,  barEndMax, 30, 0xFF333333);
            pGuiGraphics.fill(barStart, 26, barEnd, 30, clanColor);

            float percentage = (float) territoryTime / (WCEServerConfig.SERVER.MAX_TERRITORY_TIME.get()*20*60);
            String percentageString = String.format("%.1f", percentage*100) + "%";
            if (percentage <= 0) percentageString = Component.translatable("clan.client_time_unsynced").getString();

            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate(barStart, 32, 0);
            float counterScale = 0.6f;
            pGuiGraphics.pose().scale(counterScale, counterScale, counterScale);
            pGuiGraphics.drawString(mcinstance.font, percentageString, 0, 0, 0xFF777777);
            pGuiGraphics.pose().popPose();

            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate(width - (((float) mcinstance.font.width(text2))) - 20, 15, 0);
            pGuiGraphics.drawString(mcinstance.font, territoryName, 0,0, 0xFFaaaaaa);
            pGuiGraphics.pose().popPose();

            pGuiGraphics.pose().popPose();

        } else {

            int width = mcinstance.getWindow().getGuiScaledWidth();
            int height = mcinstance.getWindow().getGuiScaledHeight();
            int centerX = width / 2;
            int centerY = height / 2 + 10;

            String text = Component.translatable("clan.territory_unclaimmed").getString();
            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate(width - mcinstance.font.width(text) - 20, 5, 0);
            float scale = 1.1f;
            pGuiGraphics.pose().scale(scale, scale, scale);
            pGuiGraphics.drawString(mcinstance.font, text, 0, 0, 0xFFFFFF);
            pGuiGraphics.pose().popPose();
        }

        pGuiGraphics.pose().popPose();
    }
}