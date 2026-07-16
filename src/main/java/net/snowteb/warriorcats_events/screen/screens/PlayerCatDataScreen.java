package net.snowteb.warriorcats_events.screen.screens;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;
import net.snowteb.warriorcats_events.WCEClient;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.clan.WCEPlayerData;
import net.snowteb.warriorcats_events.client.ClientClanData;
import net.snowteb.warriorcats_events.diseases.Diseaseable;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.c2s.clan.EditProfilePacket;
import net.snowteb.warriorcats_events.network.packet.c2s.others.PlayerKitPacket;
import net.snowteb.warriorcats_events.sound.ModSounds;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.UUID;

public class PlayerCatDataScreen extends Screen {
    private final WCEPlayerData.PackedData data;
    private final UUID targetUUID;

    Component preText = Component.literal("✧ ").withStyle(ChatFormatting.GOLD);

    Component name = Component.literal("...");

    Component clanName = Component.literal("...");
    Component genderText = Component.literal("...");
    Component ageText = Component.literal("...");

    Component catMate = Component.literal("...");

    boolean interactionCooldownTooltip = false;

    private final int myKitCooldown;

    private final boolean editingProfile;

    private static final ResourceLocation CAT_NAME_TOAST =
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/clan_setup/catname_toast.png");

    private static final ResourceLocation BIO_TOAST =
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/clan_setup/bio_toast.png");


    private static final ResourceLocation SOCIALHEARTS_EMPTY =
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/clan_setup/socialhearts_empty.png");

    private static final ResourceLocation HEARTS_FILL =
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/clan_setup/hearts_fill.png");


    private Button setBioButton;
    private Button setGenderButton;

    private String menuKey = "";
    private EditBox bioBox;
    private Button sendBioButton;

    private EditBox genderBox;
    private Button sendGenderButton;

    private Button closeMenus;

    public PlayerCatDataScreen(WCEPlayerData.PackedData playerData, UUID targetUUID, int myKitCooldown, boolean editingProfile) {
        super(Component.empty());
        this.data = playerData;
        this.targetUUID = targetUUID;
        this.myKitCooldown = myKitCooldown;
        this.editingProfile = editingProfile;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        name = Component.literal(data.name);
        genderText = Component.literal(data.gender);


        catMate = Component.translatable("screen.catdata.mate", Component.literal(data.mateName).withStyle(ChatFormatting.AQUA));

        {
            clanName = Component.translatable("screen.catdata.clan", Component.literal(data.clanName));
        }

        ageText = Component.nullToEmpty(data.age.name());

        drawMainMenu();


        super.init();
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        if (!menuKey.isEmpty()) {
            pGuiGraphics.fill(centerX - 100, centerY - 10,
                    centerX + 100, centerY + 80, 0xff151518);
            pGuiGraphics.renderOutline(centerX - 100, centerY - 10, 200, 90, 0xff555566);

            String text = "";
            if (menuKey.equals("bio")) {
                text = Component.translatable("screen.playercatdata.character_bio").getString();
            } else if (menuKey.equals("gender")) {
                text = Component.translatable("screen.playercatdata.character_gender").getString();
            }

            pGuiGraphics.drawCenteredString(this.font, text, centerX, centerY, 0xff888899);

            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate(centerX, centerY + 38, 0);
            float scale = 0.7f;
            pGuiGraphics.pose().scale(scale, scale, scale);
            pGuiGraphics.drawCenteredString(this.font, Component.translatable("screen.playercatdata.leave_empty_tip"), 0, 0, 0x55888899);
            pGuiGraphics.pose().popPose();


        }

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        int xPositionPanel = 10;

        pGuiGraphics.blit(CAT_NAME_TOAST, 0, 0, 0, 0
                , 172, 45, 172, 45);

        {
            int x = 161;
            int y = 56;
            pGuiGraphics.blit(BIO_TOAST, 11, 50, 0, 0,
                    x, y, x, y);

            String bio = data.bio;

            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate((float) x/2, y, 0);
            float scale = 0.65f;
            pGuiGraphics.pose().scale(scale, scale, scale);

            if (editingProfile) {
                if (menuKey.equals("bio")) {
                    if (!bioBox.getValue().isEmpty()) {
                        bio = bioBox.getValue();
                    }
                }
            }

            if (!bio.isEmpty()) {
                List<FormattedCharSequence> lines;
                lines = Minecraft.getInstance().font.split(FormattedText.of(bio), (int) ((165 - 25)* (1/scale)));
                int color = 0xbbbbbb;

                int top = 0;

                int y1 = top + 4;

                for (FormattedCharSequence line : lines) {
                    pGuiGraphics.drawCenteredString(this.font, line, (int) (12*(1/scale)), y1, color);
                    y1 += Minecraft.getInstance().font.lineHeight;
                }
            } else {
                bio = "No bio yet...";
                int color = 0x777777;

                int top = 0;

                int y1 = top + 4;

                Component text = Component.literal(bio).withStyle(Style.EMPTY.withColor(color))
                        .withStyle(ChatFormatting.ITALIC);

                pGuiGraphics.drawCenteredString(this.font, text, (int) (12 * (1 / scale)), y1, color);

            }

//            pGuiGraphics.drawCenteredString(this.font, bio, 0, 0, 0xFFFFFF);
            pGuiGraphics.pose().popPose();
        }

        float scale1 = 1.25f;
        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().scale(scale1, scale1, 1f);
        pGuiGraphics.drawCenteredString(Minecraft.getInstance().font, name, 75, 11, 0xFFFFFF);
        pGuiGraphics.pose().popPose();

        if (interactionCooldownTooltip) {
            pGuiGraphics.renderTooltip(Minecraft.getInstance().font, Component.translatable("screen.playercatdata.cant_have_kits"), this.width, 30);
        }

        Player player = Minecraft.getInstance().level.getPlayerByUUID(targetUUID);
        if (player != null) {
            int health = (int) ((player.getHealth() / player.getMaxHealth())*100);
            pGuiGraphics.blit(SOCIALHEARTS_EMPTY, 43, 28,
                    0, 0
                    , 99, 9,
                    99, 9);

            pGuiGraphics.enableScissor(43, 28, 43 + (health), 37);
            pGuiGraphics.blit(HEARTS_FILL, 43, 28,
                    0, 0
                    , 99, 9,
                    99, 9);
            pGuiGraphics.disableScissor();
        }

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().scale(0.9f,0.9f,0.9f);


        {
            int yOffset = 72;
            pGuiGraphics.renderTooltip(Minecraft.getInstance().font, Component.empty().append(preText).append(clanName), xPositionPanel, 70 + yOffset);
            pGuiGraphics.renderTooltip(Minecraft.getInstance().font, Component.empty().append(preText).append(genderText), xPositionPanel, 90 + yOffset);
            pGuiGraphics.renderTooltip(Minecraft.getInstance().font, Component.empty().append(preText).append(ageText), xPositionPanel, 110 + yOffset);
            pGuiGraphics.renderTooltip(Minecraft.getInstance().font, Component.empty().append(preText).append(catMate), xPositionPanel, 130 + yOffset);
        }
        pGuiGraphics.pose().popPose();


        if (player instanceof Diseaseable<?> diseaseable) {
            WCEClient.renderDiseaseTooltipsUtD(diseaseable, pGuiGraphics, 170, 16, pMouseX, pMouseY);
        }

    }

    @Override
    public void tick() {
        super.tick();
    }

    private void drawMainMenu() {
        this.clearWidgets();

        int centerX = width / 2;
        int centerY = height / 2;

        menuKey = "";

//        boolean isAnyNonBinary = data.gender == 2 || ClientClanData.get().getGenderData() == 2;

//        boolean canInitiallyHaveKits = isAnyNonBinary || (data.gender != ClientClanData.get().getGenderData());

        if (Minecraft.getInstance().player != null) {
            if (!targetUUID.equals(Minecraft.getInstance().player.getUUID())) {
                if (ClientClanData.get().getMateUUID() != null) {
                    if (ClientClanData.get().getMateUUID().equals(targetUUID)) {
                        if (myKitCooldown <= 0 && data.kitCooldown <= 0 /*&& canInitiallyHaveKits*/) {
                            this.addRenderableWidget(Button.builder(
                                    Component.translatable("screen.catdata.have_kits"),
                                    btn -> {
                                        ModPackets.sendToServer(new PlayerKitPacket(targetUUID));

                                        this.onClose();
                                    }
                            ).bounds(this.width - 85, 10, 80, 20).build());
                        } else {
                            interactionCooldownTooltip = true;
                        }
                    }
                }
            }
        }


        if (editingProfile) {
            setBioButton = Button.builder(
                    Component.translatable("screen.playercatdata.set_bio"),
                    btn -> {
                        bioMenu();

                    }
            ).bounds(this.width - 85, 10, 80, 20).build();

            setGenderButton = Button.builder(
                    Component.translatable("screen.playercatdata.set_gender"),
                    btn -> {
                        genderMenu();

                    }
            ).bounds(this.width - 85, 40, 80, 20).build();

            this.addRenderableWidget(setBioButton);
            this.addRenderableWidget(setGenderButton);
        }


        this.addRenderableWidget(Button.builder(
                Component.translatable("screen.playercatdata.close"),
                btn -> {
                    this.onClose();
                }
        ).bounds(this.width - 85, this.height - 30, 80, 20).build());

    }

    private void bioMenu() {
        int centerX = width / 2;
        int centerY = height / 2;
        this.removeWidget(bioBox);
        this.removeWidget(sendBioButton);
        this.removeWidget(closeMenus);

        setBioButton.active = false;
        setGenderButton.active = false;
        menuKey = "bio";

        bioBox = new EditBox(
                this.font,
                centerX - 60, centerY + 20,
                120, 15,
                Component.literal("Bio")
        );
        bioBox.setHint(Component.translatable("screen.playercatdata.bio_here").withStyle(ChatFormatting.DARK_GRAY).withStyle(ChatFormatting.ITALIC));
        bioBox.setMaxLength(245);

        sendBioButton = Button.builder(
                Component.translatable("screen.playercatdata.save"),
                btn -> {
                    this.onClose();
                    WCEClient.playLocalSound(ModSounds.MENU_ACCEPT.get(), SoundSource.AMBIENT, 0.2f, 1.0f);

                    ModPackets.sendToServer(new EditProfilePacket(menuKey, bioBox.getValue()));
                }
        ).bounds(centerX - 65, centerY + 50, 60, 20).build();

        closeMenus = Button.builder(
                Component.translatable("screen.playercatdata.close"),
                btn -> {
                    drawMainMenu();
                    setBioButton.active = true;
                    setGenderButton.active = true;

                }
        ).bounds(centerX + 5, centerY + 50, 60, 20).build();

        this.addRenderableWidget(bioBox);
        this.addRenderableWidget(sendBioButton);
        this.addRenderableWidget(closeMenus);
    }

    private void genderMenu() {
        int centerX = width / 2;
        int centerY = height / 2;
        this.removeWidget(genderBox);
        this.removeWidget(sendGenderButton);
        this.removeWidget(closeMenus);

        setBioButton.active = false;
        setGenderButton.active = false;
        menuKey = "gender";

        genderBox = new EditBox(
                this.font,
                centerX - 60, centerY + 20,
                120, 15,
                Component.literal("Gender")
        );
        genderBox.setHint(Component.translatable("screen.playercatdata.gender_here").withStyle(ChatFormatting.DARK_GRAY).withStyle(ChatFormatting.ITALIC));
        genderBox.setMaxLength(30);

        sendGenderButton = Button.builder(
                Component.translatable("screen.playercatdata.save"),
                btn -> {
                    this.onClose();
                    WCEClient.playLocalSound(ModSounds.MENU_ACCEPT.get(), SoundSource.AMBIENT, 0.2f, 1.0f);

                    ModPackets.sendToServer(new EditProfilePacket(menuKey, genderBox.getValue()));
                }
        ).bounds(centerX - 65, centerY + 50, 60, 20).build();

        closeMenus = Button.builder(
                Component.translatable("screen.playercatdata.close"),
                btn -> {
                    drawMainMenu();
                    setBioButton.active = true;
                    setGenderButton.active = true;

                }
        ).bounds(centerX + 5, centerY + 50, 60, 20).build();

        this.addRenderableWidget(genderBox);
        this.addRenderableWidget(sendGenderButton);
        this.addRenderableWidget(closeMenus);
    }


    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        super.onClose();
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {

        boolean isTyping = (bioBox != null && bioBox.isFocused()) || (genderBox != null && genderBox.isFocused());

        if (!isTyping) {
            if (pKeyCode == GLFW.GLFW_KEY_E) {
                this.onClose();
                return true;
            }
            if (pKeyCode == GLFW.GLFW_KEY_T) {
                this.onClose();
                return true;
            }
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }
}
