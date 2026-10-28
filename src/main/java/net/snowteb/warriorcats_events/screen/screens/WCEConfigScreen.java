package net.snowteb.warriorcats_events.screen.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.snowteb.warriorcats_events.WCEClient;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.compat.CompatibilitiesClient;
import net.snowteb.warriorcats_events.screen.screens.createmorph.FancyDoubleSelectableButtonList;
import net.snowteb.warriorcats_events.screen.widgets.GradientSwitchButton;
import net.snowteb.warriorcats_events.screen.widgets.GradientToggleButton;
import net.snowteb.warriorcats_events.zconfig.WCEClientConfig;
import org.lwjgl.glfw.GLFW;

import static net.snowteb.warriorcats_events.screen.screens.clan.FancyCreateClanScreen.BG_TEXTURE;


@OnlyIn(Dist.CLIENT)
public class WCEConfigScreen extends Screen {

    private final Screen parent;

    FancyDoubleSelectableButtonList buttonList;

    private boolean leapTemp;
    private boolean ownMorphNameTemp;
    private boolean ambientMusicTemp;
    private boolean chatBubblesTemp;
    private boolean ownChatBubblesTemp;
    private boolean displayTerritoryTemp;
    private boolean customPanoramaTemp;
    private boolean sereneSeasonsOverlayTemp;

    public WCEConfigScreen(Screen parent) {
        super(Component.literal("Warrior Cats Events"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = width / 2;
        int centerY = height / 2;

        leapTemp = WCEClientConfig.CLIENT.LEAP.get();
        ownMorphNameTemp = WCEClientConfig.CLIENT.OWN_MORPH_NAME.get();
        ambientMusicTemp = WCEClientConfig.CLIENT.AMBIENT_MUSIC.get();
        chatBubblesTemp = WCEClientConfig.CLIENT.MORPH_CHAT_BUBBLES.get();
        ownChatBubblesTemp = WCEClientConfig.CLIENT.OWN_CHAT_BUBBLES.get();
        displayTerritoryTemp = WCEClientConfig.CLIENT.DISPLAY_TERRITORY.get();
        customPanoramaTemp = WCEClientConfig.CLIENT.CUSTOM_PANORAMA.get();
        sereneSeasonsOverlayTemp = WCEClientConfig.CLIENT.SERENE_SEASONS_OVERLAY.get();

        centerY -= 10;

        int listWidth = 278;

        buttonList = new FancyDoubleSelectableButtonList(this.minecraft,
                listWidth, 100, centerY - 29, centerY + 90, 30);

        buttonList.setLeftPos(centerX - listWidth/2);
        buttonList.setRenderBackground(false);
        buttonList.setRenderTopAndBottom(false);

        buttonList.addButtons(
                new FancyDoubleSelectableButtonList.ButtonEntry(
                        Component.translatable("screen.config.toggle_leap"),
                        b -> leapTemp = !leapTemp,
                        leapTemp),
                new FancyDoubleSelectableButtonList.ButtonEntry(
                        Component.translatable("screen.config.self_morph_name"),
                        b -> ownMorphNameTemp = !ownMorphNameTemp,
                        ownMorphNameTemp));

        buttonList.addButtons(
                new FancyDoubleSelectableButtonList.ButtonEntry(
                        Component.translatable("screen.config.bg_music"),
                        b -> ambientMusicTemp = !ambientMusicTemp,
                        ambientMusicTemp),
                new FancyDoubleSelectableButtonList.ButtonEntry(
                        Component.translatable("screen.config.chat_bubbles"),
                        b -> chatBubblesTemp = !chatBubblesTemp,
                        chatBubblesTemp));

        buttonList.addButtons(
                new FancyDoubleSelectableButtonList.ButtonEntry(
                        Component.translatable("screen.config.own_chat_bubbles"),
                        b -> ownChatBubblesTemp = !ownChatBubblesTemp,
                        ownChatBubblesTemp),
                new FancyDoubleSelectableButtonList.ButtonEntry(
                        Component.translatable("screen.config.display_territory"),
                        b -> displayTerritoryTemp = !displayTerritoryTemp,
                        displayTerritoryTemp));

        buttonList.addButtons(
                new FancyDoubleSelectableButtonList.ButtonEntry(
                        Component.translatable("screen.config.panorama"),
                        b -> customPanoramaTemp = !customPanoramaTemp,
                        customPanoramaTemp),
                CompatibilitiesClient.SERENESEASONS_LOADED ?
                new FancyDoubleSelectableButtonList.ButtonEntry(
                        Component.translatable("screen.config.seasons_overlay"),
                        b -> sereneSeasonsOverlayTemp = !sereneSeasonsOverlayTemp,
                        sereneSeasonsOverlayTemp) : null);

        this.addRenderableWidget(buttonList);


        GradientToggleButton doneButton = new GradientToggleButton(
                centerX - 20, centerY + 100, 40, 17,
                Component.translatable("screen.config.done"),
                btn -> {
                    save();
                }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                60, 20, 1f, 0xFFFFFFFF
        );

        GradientToggleButton changelogButton = new GradientToggleButton(
                centerX + 50, centerY + 100, 80, 17,
                Component.translatable("screen.config.changelog"),
                btn -> {
                    Minecraft.getInstance().setScreen(new WCEChangelogScreen(this));
                }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                60, 20, 1f, 0xFFFFFFFF
        );


        this.addRenderableWidget(changelogButton);

        this.addRenderableWidget(doneButton);

    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {

        int centerX = width / 2;
        int centerY = height / 2;

        pGuiGraphics.blit(BG_TEXTURE, 0, 0, 0, 0, this.width, this.height, this.width, this.height);

        float scale = 0.78f;

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(centerX - (125 * scale), centerY - (165 * scale), 0);

        pGuiGraphics.pose().scale(scale, scale, scale);

        pGuiGraphics.blit(WCEClient.WCE_TITLE,
                0,
                0, 0, 0,
                250, 125, 250, 125);

        pGuiGraphics.pose().popPose();

        pGuiGraphics.renderOutline(centerX - 140, centerY - 40, 280, 150, 0x11FFFFFF);


        int x0 = centerX - 139;
        int y0 = centerY - 40;
        int x1 = centerX + 139;
        int y1 = centerY + 109;
        pGuiGraphics.fill(x0, y0, x1, y1, 0x44000000);
        pGuiGraphics.fillGradient(x0, y0, x1, y0 + 6, 0x88000000, 0x01000000);
        pGuiGraphics.fillGradient(x0, y1 - 6, x1, y1, 0x01000000, 0x88000000);


        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        pGuiGraphics.fillGradient(x0, buttonList.getBottom() - 6, x1, buttonList.getBottom(), 0x01000000, 0x88000000);



    }

    private void save() {

        WCEClientConfig.CLIENT.LEAP.set(leapTemp);
        WCEClientConfig.CLIENT.OWN_MORPH_NAME.set(ownMorphNameTemp);
        WCEClientConfig.CLIENT.AMBIENT_MUSIC.set(ambientMusicTemp);
        WCEClientConfig.CLIENT.MORPH_CHAT_BUBBLES.set(chatBubblesTemp);
        WCEClientConfig.CLIENT.OWN_CHAT_BUBBLES.set(ownChatBubblesTemp);
        WCEClientConfig.CLIENT.DISPLAY_TERRITORY.set(displayTerritoryTemp);
        WCEClientConfig.CLIENT.CUSTOM_PANORAMA.set(customPanoramaTemp);
        WCEClientConfig.CLIENT.SERENE_SEASONS_OVERLAY.set(sereneSeasonsOverlayTemp);

        WCEClientConfig.SPEC.save();
        onClose();
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.onClose();
            return true;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }
}
