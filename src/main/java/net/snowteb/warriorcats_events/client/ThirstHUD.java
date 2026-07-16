package net.snowteb.warriorcats_events.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.zconfig.WCEServerConfig;
import tocraft.walkers.api.PlayerShape;

public class ThirstHUD {

    private static final ResourceLocation FILLED_THIRST = ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID,
            "textures/hud/filled.png");
    private static final ResourceLocation HALF_THIRST = ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID,
            "textures/hud/half.png");
    private static final ResourceLocation EMPTY_THIRST = ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID,
            "textures/hud/empty.png");

    public static final IGuiOverlay HUD_THIRST = ((gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        if (!WCEServerConfig.SERVER.THIRST.get()) return;
        var player = net.minecraft.client.Minecraft.getInstance().player;
        if (player == null || player.isCreative() || player.isSpectator()) return;
        if (!(PlayerShape.getCurrentShape(player) instanceof Animal)) return;
        Minecraft mc = Minecraft.getInstance();
        if (!mc.isWindowActive()) return;
        if (mc.level == null) return;
        if (mc.screen != null) {
            if (!(mc.screen instanceof ChatScreen)) return;
        }

        int x = screenWidth / 2 + 7;
        int y = screenHeight - 51;

        /**
         * If the player has an air supply lower than the max, then move the HUD 9 pixels above.
         * This since the air supply bar only shows when the air is lower than 10.
         */

        if (player.getAirSupply() < player.getMaxAirSupply()) {
            y -= 9;
        }
        if (player.getVehicle() != null) {
            if (player.getVehicle() instanceof LivingEntity entity) {
                if (entity.getMaxHealth() > 20) {
                    y -= 11;
                }
                if (entity.getMaxHealth() > 40) {
                    y -= 11;
                }
            }
        }

        int thirst = ClientThirstData.getPlayerThirst();
        int iconCount = 10;

        boolean lowThirst = thirst <= 5;
        boolean extraLowThirst = thirst <= 3;

        int tickCount = Minecraft.getInstance().player.tickCount;

        for (int i = 0; i < iconCount; i++) {
            int index = iconCount - 1 - i;
            int thirstRemaining = thirst - index * 2;

            ResourceLocation texture;
            if (thirstRemaining >= 2) texture = FILLED_THIRST;
            else if (thirstRemaining == 1) texture = HALF_THIRST;
            else texture = EMPTY_THIRST;

            /**
             * Different offsets depending on the thirst remaining.
             * This is what makes the bar shake.
             */
            int yOffset = 0;
            if (lowThirst) {
                yOffset = (int) (Math.sin((tickCount + i) * 1.0) * 2);
            }
            if (extraLowThirst) {
                yOffset = (int) (Math.sin((tickCount + i) * 3.0) * 2);
            }

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            guiGraphics.blit(texture, x + i * 8, y + yOffset, 0, 0, 14, 14, 14, 14);

            RenderSystem.disableBlend();
        }

    }
    );

}
