package net.snowteb.warriorcats_events.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.event.UpdateCheck;
import net.snowteb.warriorcats_events.zconfig.WCEClientConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {

    @Redirect(method = "<init>(ZLnet/minecraft/client/gui/components/LogoRenderer;)V",
            at = @At(value = "NEW", target = "net/minecraft/client/renderer/PanoramaRenderer"))
    private PanoramaRenderer redirectPanorama(CubeMap original) {
        if (WCEClientConfig.CLIENT.CUSTOM_PANORAMA.get()) {
            return new PanoramaRenderer(
                    new CubeMap(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID,"textures/gui/title/background/panorama"))
            );
        } else {
            return new PanoramaRenderer(original);
        }
    }

    @Shadow
    boolean fading;

    @Shadow
    long fadeInStart;


    @Inject(method = "render", at = @At("TAIL"))
    private void renderMainMenu(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick, CallbackInfo ci) {

        // Hawk tuah

        float f = this.fading ? (float)(Util.getMillis() - this.fadeInStart) / 1000.0F : 1.0F;
        float f1 = this.fading ? Mth.clamp(f - 1.0F, 0.0F, 1.0F) : 1.0F;

        RenderSystem.enableBlend();

        pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, f1);

        Minecraft mc = Minecraft.getInstance();
        int width = mc.getWindow().getGuiScaledWidth();
        Font font = mc.font;

        ResourceLocation logo = ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/wce_logo.png");
        ResourceLocation cat = ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/cat_overlay.png");

        int centerX = width / 2;

        pGuiGraphics.pose().pushPose();

        float x = centerX - 123;
        float y = 13 + 22.5f;

        pGuiGraphics.pose().translate(x, y,400);
        pGuiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(-20f));
        pGuiGraphics.blit(logo,
                -25,-25,
                0, 0,
                45, 45,
                45, 45
        );

        pGuiGraphics.pose().popPose();

        pGuiGraphics.blit(cat,
                centerX + 25,0,
                0, 0,
                55, 55,
                55, 55
        );

        pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

        RenderSystem.disableBlend();

        if (UpdateCheck.updateAvailable) {
            pGuiGraphics.drawString(font,
                    Component.literal("New WCE version available: " + UpdateCheck.latestVersion).withStyle(Style.EMPTY.withColor(0xfffb00))
                    , 2, 2, 0);
        } else {
            pGuiGraphics.drawString(font,
                    Component.translatable("wce.translation_credits").withStyle(Style.EMPTY.withColor(0x5EA6FF))
                    , 2, 2, 0);
        }
    }
}