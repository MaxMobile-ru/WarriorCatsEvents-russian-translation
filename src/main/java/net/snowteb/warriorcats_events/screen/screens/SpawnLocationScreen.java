package net.snowteb.warriorcats_events.screen.screens;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.snowteb.warriorcats_events.WCEClient;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.client.ClientClanData;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.c2s.others.CtSTeleportToLocationPacket;
import net.snowteb.warriorcats_events.screen.widgets.ToggleButton;
import net.snowteb.warriorcats_events.sound.ModSounds;
import net.snowteb.warriorcats_events.zconfig.WCEClientConfig;

public class SpawnLocationScreen extends Screen {
    private int textCooldown = 0;

    private float menuX;
    private final float targetX = 0;


    private ToggleButton somewhereButton;
    private ToggleButton taigaButton;
    private ToggleButton plainsButton;
    private ToggleButton savannahButton;
    private ToggleButton forestButton;
    private ToggleButton taigaVillageButton;
    private ToggleButton plainsVillageButton;
    private ToggleButton savannahVillageButton;
    private ToggleButton junglePyramidButton;

    private Button saveButton;

    public SpawnLocationScreen() {
        super(Component.literal("Spawn Location"));
        menuX = 500;
    }


    private static final ResourceLocation BANNER =
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/clan_setup/banner.png");

    private static final ResourceLocation BG_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/clan_setup/background_scene.png");


    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        boolean somewhereTooltip = pMouseX >= centerX - 125 && pMouseY >= centerY - 10
                && pMouseX <= centerX - 45 && pMouseY <= centerY + 10;


        if (menuX > targetX) {
            menuX -= (menuX) * 0.03f;
            if (menuX < targetX) menuX = targetX;
        }

        pGuiGraphics.blit(
                BG_TEXTURE,
                0,
                0,
                0,
                0,
                this.width, this.height,
                this.width, this.height
        );

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(menuX, 0, 0);

        pGuiGraphics.blit(WCEClient.WCE_TITLE,
                centerX - 250 - this.width,
                centerY - 62, 0, 0,
                250, 125,250,125);

        String morphName = ClientClanData.get().getMorphName();
        Component journeyText;
        int genderValue = ClientClanData.get().getGenderData();

        if (genderValue == 0) {
            journeyText = Component.translatable("screen.spawn.began_journey_male", morphName);
        } else if (genderValue == 1) {
            journeyText = Component.translatable("screen.spawn.began_journey_female", morphName);
        } else {
            journeyText = Component.translatable("screen.spawn.began_journey_their", morphName);
        }

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(centerX, centerY - 35, 0);
        pGuiGraphics.pose().scale(1.2f, 1.2f, 1.2f);
        pGuiGraphics.drawCenteredString(Minecraft.getInstance().font,
                journeyText,
                0, 0, 0xFFFFFFFF);
        pGuiGraphics.pose().popPose();

        if (textCooldown > 0) {
            pGuiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("screen.spawn.fields_empty"),
                    centerX - 55, centerY + 75, 0xFFFF0000);
        }

        if (somewhereTooltip) pGuiGraphics.renderTooltip(Minecraft.getInstance().font,
                Component.translatable("screen.spawn.default_loc_tip")
                        .withStyle(ChatFormatting.GRAY)
                , pMouseX, pMouseY);

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        pGuiGraphics.pose().popPose();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            ModPackets.sendToServer(new CtSTeleportToLocationPacket(32));

            if (WCEClientConfig.CLIENT.AMBIENT_MUSIC.get()) {
                Minecraft mc = Minecraft.getInstance();
                mc.getMusicManager().startPlaying(WCEClient.GENERATIONS_BG_MUSIC);
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void tick() {

        if (textCooldown > 0) {
            textCooldown--;
        }

        super.tick();
    }

    @Override
    protected void init() {

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        int offsetX = 105;
        int offsetY = 10;

        somewhereButton = new ToggleButton(
                centerX - 230 + offsetX, centerY - 20 + offsetY, 80, 20,
                Component.translatable("screen.spawn.somewhere").getString(),
                btn -> selectLocation(somewhereButton)
        );
        taigaButton = new ToggleButton(
                centerX - 145 + offsetX, centerY - 20 + offsetY, 80, 20,
                Component.translatable("biome.minecraft.taiga").getString(),
                btn -> selectLocation(taigaButton)
        );
        plainsButton = new ToggleButton(
                centerX - 60 + offsetX, centerY - 20 + offsetY, 80, 20,
                Component.translatable("biome.minecraft.plains").getString(),
                btn -> selectLocation(plainsButton)
        );
        savannahButton = new ToggleButton(
                centerX - 230 + offsetX, centerY + 5 + offsetY, 80, 20,
                Component.translatable("biome.minecraft.savanna").getString(),
                btn -> selectLocation(savannahButton)
        );
        forestButton = new ToggleButton(
                centerX - 145 + offsetX, centerY + 5 + offsetY, 80, 20,
                Component.translatable("biome.minecraft.forest").getString(),
                btn -> selectLocation(forestButton)
        );
        taigaVillageButton = new ToggleButton(
                centerX - 60 + offsetX, centerY + 5 + offsetY, 80, 20,
                Component.translatable("screen.spawn.taiga_village").getString(),
                btn -> selectLocation(taigaVillageButton)
        );
        plainsVillageButton = new ToggleButton(
                centerX - 230 + offsetX, centerY + 30 + offsetY, 80, 20,
                Component.translatable("screen.spawn.plains_village").getString(),
                btn -> selectLocation(plainsVillageButton)
        );
        savannahVillageButton = new ToggleButton(
                centerX - 145 + offsetX, centerY + 30 + offsetY, 80, 20,
                Component.translatable("screen.spawn.savannah_village").getString(),
                btn -> selectLocation(savannahVillageButton)
        );
        junglePyramidButton = new ToggleButton(
                centerX - 60 + offsetX, centerY + 30 + offsetY, 80, 20,
                Component.translatable("screen.spawn.jungle_pyramid").getString(),
                btn -> selectLocation(junglePyramidButton)
        );

        this.addRenderableWidget(somewhereButton);
        this.addRenderableWidget(taigaButton);
        this.addRenderableWidget(plainsButton);
        this.addRenderableWidget(savannahButton);
        this.addRenderableWidget(forestButton);
        this.addRenderableWidget(taigaVillageButton);
        this.addRenderableWidget(plainsVillageButton);
        this.addRenderableWidget(savannahVillageButton);
        this.addRenderableWidget(junglePyramidButton);


        saveButton = Button.builder(
                Component.translatable("screen.spawn.finish"),
                btn -> onFinish()
        ).bounds(centerX - 40, centerY + 85, 80, 20).build();

        this.addRenderableWidget(saveButton);
    }

    private void selectLocation(ToggleButton selected) {
        somewhereButton.setSelected(false);
        taigaButton.setSelected(false);
        plainsButton.setSelected(false);
        savannahButton.setSelected(false);
        forestButton.setSelected(false);
        taigaVillageButton.setSelected(false);
        plainsVillageButton.setSelected(false);
        savannahVillageButton.setSelected(false);
        junglePyramidButton.setSelected(false);

        selected.setSelected(true);
    }

    private void onFinish() {

        boolean isLocationSelected =
                (somewhereButton.isSelected() || taigaButton.isSelected()
                        || plainsButton.isSelected() || savannahButton.isSelected()
                        || forestButton.isSelected() || taigaVillageButton.isSelected()
                        || plainsVillageButton.isSelected() || savannahVillageButton.isSelected()
                        || junglePyramidButton.isSelected());

        if (!isLocationSelected) {
            textCooldown = 100;
            return;
        }

        int locationValue = 0;

        if (somewhereButton.isSelected()) {
            locationValue = 0;
        }
        if (taigaButton.isSelected()) {
            locationValue = 1;
        }
        if (plainsButton.isSelected()) {
            locationValue = 2;
        }
        if (savannahButton.isSelected()) {
            locationValue = 3;
        }
        if (forestButton.isSelected()) {
            locationValue = 4;
        }
        if (taigaVillageButton.isSelected()) {
            locationValue = 5;
        }
        if (plainsVillageButton.isSelected()) {
            locationValue = 6;
        }
        if (savannahVillageButton.isSelected()) {
            locationValue = 7;
        }
        if (junglePyramidButton.isSelected()) {
            locationValue = 8;
        }

        if (WCEClientConfig.CLIENT.AMBIENT_MUSIC.get()) {
            Minecraft mc = Minecraft.getInstance();
            mc.getMusicManager().startPlaying(WCEClient.GENERATIONS_BG_MUSIC);
        }

        ModPackets.sendToServer(new CtSTeleportToLocationPacket(locationValue));

        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            if (locationValue != 0) {
                player.sendSystemMessage(Component.translatable("screen.spawn.teleporting")
                        .withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
                player.sendSystemMessage(Component.literal(""));
            }
        }

        WCEClient.nextMenuSound();


        this.minecraft.setScreen(null);
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

}
