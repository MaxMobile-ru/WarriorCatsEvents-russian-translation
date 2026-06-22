package net.snowteb.warriorcats_events.screen.menus;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.snowteb.warriorcats_events.WCEClient;
import net.snowteb.warriorcats_events.WarriorCatsEvents;

public class EmoteMenu {

    private static ResourceLocation currentMouseTexture =
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/hud/mouse_wheel_unclicked.png");

    private static int lastToggleTick = 0;

    private static boolean showHelper = true;

    public void render(GuiGraphics guiGraphics) {

        if (!Minecraft.getInstance().options.hideGui && WCEClient.isRenderingEmoteMenu && Minecraft.getInstance().screen == null) {
            if (Minecraft.getInstance().level != null && Minecraft.getInstance().player != null) {

                ResourceLocation mouseClick = ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID,
                        "textures/hud/mouse_wheel_clicked.png");
                ResourceLocation mouseUnclick = ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID,
                        "textures/hud/mouse_wheel_unclicked.png");

                int tick = Minecraft.getInstance().player.tickCount;
                if (tick - lastToggleTick >= 10) {
                    lastToggleTick = tick;
                    currentMouseTexture = currentMouseTexture.equals(mouseClick) ? mouseUnclick : mouseClick;
                }

                int centerX = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2;
                int centerY = Minecraft.getInstance().getWindow().getGuiScaledHeight() / 2;

                int panelWidth = 270;
                int panelHeight = 50;

                int left = centerX - panelWidth / 2;
                int top = centerY - panelHeight / 2;



                int minIndex = -1;
                if (WarriorCatsEvents.Collaborators.isContributor(Minecraft.getInstance().player.getUUID())) minIndex = -3;



                WCEClient.emoteOffset = Mth.clamp(WCEClient.emoteOffset, minIndex, WCEClient.EmoteIndexData.MAX_EMOTES);

                if (WCEClient.emoteOffset != 0) showHelper = false;

                guiGraphics.fillGradient(left, top, left + panelWidth, top + panelHeight, 0x44333333, 0x114b2300);
                guiGraphics.renderOutline(left, top, panelWidth, panelHeight, 0xFF999999);

                for (int i = -2; i <= 2; i++) {

                    int index = WCEClient.emoteOffset + i;

                    if (index >= minIndex && index < WCEClient.EmoteIndexData.MAX_EMOTES + 1) {

                        int drawX = centerX + (i * 50);
                        int drawY = centerY;

                        PoseStack pose = guiGraphics.pose();

                        pose.pushPose();
                        pose.translate(drawX, drawY - 20, 0);

                        guiGraphics.renderOutline(-20, 0, 40, 40, 0xFF555555);

                        guiGraphics.blit(WCEClient.EmoteIndexData.getIcon(index) ,
                                -16, 6, 0, 0,
                                32, 32, 32, 32);

                        pose.scale(0.5f, 0.5f, 1f);
                        guiGraphics.drawCenteredString(
                                Minecraft.getInstance().font,
                                WCEClient.EmoteIndexData.getText(index),
                                0,
                                5,
                                0xdddddd
                        );
                        pose.popPose();
                    }
                }


                if (showHelper) {
                    guiGraphics.blit(
                            currentMouseTexture,
                            centerX + 137, centerY + 4,
                            0, 0,
                            9, 19,
                            9, 19
                    );

                }

                guiGraphics.renderOutline(
                        centerX - 20,
                        centerY - 20,
                        40,
                        40,
                        0xFFFFFFFF
                );
            }
        }

    }
}
