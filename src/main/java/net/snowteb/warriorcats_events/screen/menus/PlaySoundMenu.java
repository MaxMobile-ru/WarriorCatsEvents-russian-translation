package net.snowteb.warriorcats_events.screen.menus;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.snowteb.warriorcats_events.WCEClient;

public class PlaySoundMenu {

    public void render(GuiGraphics guiGraphics) {

        if (!Minecraft.getInstance().options.hideGui && WCEClient.isRenderingSoundMenu && Minecraft.getInstance().screen == null) {
            if (Minecraft.getInstance().level != null && Minecraft.getInstance().player != null) {

                int centerX = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2;
                int centerY = Minecraft.getInstance().getWindow().getGuiScaledHeight() / 2;

                int panelWidth = 75;
                int panelHeight = 92;

                centerY += 40;

                int left = (centerX - panelWidth / 2) + 2;
                int top = (centerY - panelHeight / 2) - 18;

                PoseStack firstPose = guiGraphics.pose();

                firstPose.pushPose();
                firstPose.translate(centerX, centerY - 63, 0);
                firstPose.scale(0.8f, 0.8f, 1f);
                guiGraphics.drawCenteredString(
                        Minecraft.getInstance().font,
                        Component.translatable("screen.playsound"),
                        0,
                        5,
                        0xFFFFFF
                );
                firstPose.popPose();

                int barLength = 78 / ((WCEClient.MAX_SOUNDS));

                int barOffset = WCEClient.soundOffset * ((78 - barLength) / (WCEClient.MAX_SOUNDS));

                guiGraphics.fill(centerX + 37, (centerY - 50) + barOffset, centerX + 40, (centerY - 50 + barLength) + barOffset, 0xFFFFFFFF);

                int minIndex = 0;

                WCEClient.soundOffset = Mth.clamp(WCEClient.soundOffset, minIndex, WCEClient.MAX_SOUNDS);

                guiGraphics.fillGradient(left, top, left + panelWidth, top + panelHeight, 0x09000000, 0x09000000);
                guiGraphics.renderOutline(left, top, panelWidth, panelHeight, 0xFF999999);

                for (int i = -2; i <= 2; i++) {

                    int index = WCEClient.soundOffset + i;

                    if (index >= minIndex && index < WCEClient.MAX_SOUNDS + 1) {

                        int drawX = centerX;
                        int drawY = centerY + (i * 15);

                        PoseStack pose = guiGraphics.pose();

                        pose.pushPose();
                        pose.translate(drawX, drawY - 20, 0);

                        guiGraphics.renderOutline(-30, 0, 60, 14, 0xFF555555);

                        int color = 0xff777777;
                        if (i == 0) {
                            color = 0xffFFFFFF;
                        }

                        pose.scale(0.8f, 0.8f, 1f);
                        guiGraphics.drawCenteredString(
                                Minecraft.getInstance().font,
                                WCEClient.SoundIndexData.getText(index),
                                0,
                                5,
                                color
                        );
                        pose.popPose();
                    }
                }

                guiGraphics.renderOutline(
                        centerX - 30,
                        centerY - 20,
                        60,
                        14,
                        0xFFFFFFFF
                );
            }
        }

    }
}
