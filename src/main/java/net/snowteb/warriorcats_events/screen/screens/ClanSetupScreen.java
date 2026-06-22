package net.snowteb.warriorcats_events.screen.screens;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.snowteb.warriorcats_events.WCEClient;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.clan.WCEPlayerData;
import net.snowteb.warriorcats_events.client.ClientClanData;
import net.snowteb.warriorcats_events.entity.custom.WCatEntity;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.c2s.clan.SaveClanDataPacket;
import net.snowteb.warriorcats_events.screen.widgets.SwitchButton;
import net.snowteb.warriorcats_events.screen.widgets.ToggleButton;
import net.snowteb.warriorcats_events.sound.ModSounds;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClanSetupScreen extends Screen {
    private int textCooldown = 0;

    private EditBox morphPrefixBox;
    private EditBox morphSufixBox;

    private ToggleButton ageKit;
    private ToggleButton ageApprentice;
    private ToggleButton ageAdult;

    private ToggleButton genderMale;
    private ToggleButton genderFemale;
    private ToggleButton genderNone;

    private Button saveButton;

//    private Button randomizeButton;

    //    private SwitchButton automaticSufix;
    private SwitchButton useSufixes;
    private Button setRandomSufix;
    private Button setRandomPrefix;

    private float animationTime = 0f;
    private float duration = 20f;
    private boolean closing = false;

    private final float startX = 0;
    private final float endX = -700;
    private float menuX = 0;

    public ClanSetupScreen() {
        super(Component.literal("Clan Setup"));
    }


    private static final ResourceLocation BANNER =
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/clan_setup/banner.png");

    private static final ResourceLocation BG_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/clan_setup/background_scene.png");


    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {

        pGuiGraphics.blit(BG_TEXTURE, 0, 0, 0, 0, this.width, this.height, this.width, this.height);


        if (closing) {
            animationTime += pPartialTick;

            float progress = Math.min(animationTime / duration, 1f);

            float eased = progress * progress * progress;

            menuX = startX + (endX - startX) * eased;

            if (progress >= 1f) {
                onSave();
            }
        }


        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(menuX, 0, 0);

        int centerx = (this.width) / 2;
        int centery = (this.height) / 2;

        pGuiGraphics.blit(WCEClient.WCE_TITLE,
                centerx - 125 + this.width,
                centery - 62, 0, 0,
                250, 125, 250, 125);


        if (morphPrefixBox.isHovered()) {
            if (useSufixes.getValue()) {
                pGuiGraphics.renderTooltip(Minecraft.getInstance().font,
                        Component.empty()
                                .append(Component.literal("The prefix of your morph. eg: ").withStyle(ChatFormatting.GRAY))
                                .append(Component.literal("'Bengal'").withStyle(ChatFormatting.YELLOW))
                                .append(Component.literal("pelt").withStyle(ChatFormatting.GRAY))
                        , pMouseX, pMouseY);
            } else {
                pGuiGraphics.renderTooltip(Minecraft.getInstance().font,
                        Component.empty()
                                .append(Component.literal("The name of your morph. eg: ").withStyle(ChatFormatting.GRAY))
                                .append(Component.literal("'Daisy'").withStyle(ChatFormatting.YELLOW))

                        , pMouseX, pMouseY);
            }
        }

        if (morphSufixBox.isHovered() && useSufixes.getValue()) pGuiGraphics.renderTooltip(Minecraft.getInstance().font,
                Component.empty()
                        .append(Component.literal("The suffix of your morph. eg: Bengal").withStyle(ChatFormatting.GRAY))
                        .append(Component.literal("'pelt'").withStyle(ChatFormatting.YELLOW))
                , pMouseX, pMouseY);

        if (useSufixes.isHovered()) {
            List<Component> tooltip = new ArrayList<>();

            tooltip.add(Component.literal("Controls whether your morph uses")
                    .withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.literal("<prefix> + <suffix> warrior names")
                    .withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.literal("or a single-word name.")
                    .withStyle(ChatFormatting.GRAY));

            pGuiGraphics.renderTooltip(
                    Minecraft.getInstance().font,
                    tooltip, Optional.empty(),
                    pMouseX, pMouseY
            );
        }

        if (ageKit.isHovered() || ageApprentice.isHovered() || ageAdult.isHovered()) {
            pGuiGraphics.renderTooltip(Minecraft.getInstance().font,
                    Component.literal("The age your character begins with.")
                            .withStyle(ChatFormatting.GRAY)
                    , pMouseX, pMouseY);
        }

        String morphPrefix = morphPrefixBox.getValue().trim();
        String morphSufix = morphSufixBox.getValue().trim();
//        String clanName = clanNameBox.getValue().trim();
        Boolean isUseSufixes = useSufixes.getValue();

        String sufix = "";
        if (isUseSufixes) {
            if (ageKit.isSelected()) {
                sufix = "kit";
            } else if (ageApprentice.isSelected()) {
                sufix = "paw";
            } else {
                sufix = morphSufixBox.getValue();
            }
        }
        String morphNameShow = "...";
        String genderS = "";
        if (genderFemale.isSelected()) {
            genderS = " ♀";
        } else if (genderMale.isSelected()) {
            genderS = " ♂";
        }

        if (!morphPrefix.isEmpty()) {
            morphNameShow = morphPrefix + sufix + genderS;
        }
//        if (!clanNameBox.getValue().isEmpty()) {
//            clanNameShow = clanNameBox.getValue();
//        }

//        pGuiGraphics.drawString(Minecraft.getInstance().font,
//                "''I am " + morphNameShow + " from " +  clanNameShow + "!''",
//                centerx-217, centery - 54, 0xFFFFFFFF);

        pGuiGraphics.drawCenteredString(Minecraft.getInstance().font,
                "''I am " + morphNameShow + "!''",
                centerx, centery - 54, 0xFFFFFFFF);

//        if (clanName.isEmpty()) {
//            pGuiGraphics.drawString(Minecraft.getInstance().font, "<Warriorclan>", centerx-217, centery - 4, 0xFF7d7d7d);
//        }


        if (textCooldown > 0) {
            pGuiGraphics.drawString(Minecraft.getInstance().font, "Some fields are empty",
                    centerx - 55, centery + 75, 0xFFFF0000);
        }

////        boolean autoSufix = automaticSufix.getValue();
//        String sufixText = WCatEntity.SUFIXES[randomSufix];
//
////        if (autoSufix) {
//            morphSufixBox.setValue(sufixText);
//        }


        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);


        int xPosition = centerx-90;
        int yPosition = 0;

        float scale = 0.8f;
        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(xPosition, yPosition, 0);
        pGuiGraphics.pose().scale(scale, scale, scale);
        pGuiGraphics.blit(
                BANNER,
                0, 0,
                0, 0,
                200, 56,
                200, 56
        );
        pGuiGraphics.pose().popPose();

        pGuiGraphics.pose().popPose();


    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            if (!ClientClanData.get().isFirstLoginHandled()) {
                return true;
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

        Minecraft.getInstance().getTutorial().setStep(TutorialSteps.NONE);

        morphPrefixBox = new EditBox(
                this.font,
                centerX - 120, centerY - 40,
                90, 20,
                Component.literal("Prefix")
        );
        morphPrefixBox.setMaxLength(13);
        this.addRenderableWidget(morphPrefixBox);

        morphSufixBox = new EditBox(
                this.font,
                centerX - 20, centerY - 40,
                60, 20,
                Component.literal("Suffix")
        );
        morphSufixBox.setMaxLength(12);
        this.addRenderableWidget(morphSufixBox);

//        automaticSufix = new SwitchButton(
//                centerX + 20, centerY - 40,
//                50, 20,
//                "Random Suffix",
//                false,
//                btn -> {
//                    randomSufix = Minecraft.getInstance().player.getRandom().nextInt(WCatEntity.SUFIXES.length);
//                    if (automaticSufix.getValue()) useSufixes.setValue(true);
//                }
//        );

//        randomizeButton = Button.builder(
//                Component.literal("@"),
//                btn -> randomSufix = Minecraft.getInstance().player.getRandom().nextInt(WCatEntity.SUFIXES.length)
//        ).bounds(centerX + 75, centerY - 40, 20, 20).run();
//        this.addRenderableWidget(randomizeButton);

        setRandomPrefix = Button.builder(
                Component.literal("Random Prefix"),
                btn -> {

                    int value = Minecraft.getInstance().player.getRandom().nextInt(WCatEntity.PREFIXES.length);
                    String prefix = WCatEntity.PREFIXES[value];
                    morphPrefixBox.setValue(prefix);
                }
        ).bounds(centerX - 85, centerY - 15, 80, 15).build();

        setRandomSufix = Button.builder(
                Component.literal("Random Suffix"),
                btn -> {
                    if (useSufixes.getValue()) {
                        int value = Minecraft.getInstance().player.getRandom().nextInt(WCatEntity.SUFIXES.length);
                        String sufix = WCatEntity.SUFIXES[value];
                        morphSufixBox.setValue(sufix);
                    } else {
                        morphSufixBox.setValue("");
                    }

                }
        ).bounds(centerX + 5, centerY - 15, 80, 15).build();

        this.addRenderableWidget(setRandomSufix);
        this.addRenderableWidget(setRandomPrefix);

        if (ClientClanData.get().isFirstLoginHandled()) {
            String prefix = ClientClanData.get().getPrefix();
            String sufix = ClientClanData.get().getSufix();
            String name = ClientClanData.get().getMorphName();

            if (ClientClanData.get().isUseSufixes()) {
                morphPrefixBox.setValue(prefix);
                morphSufixBox.setValue(sufix);
            } else {
                morphPrefixBox.setValue(name);
            }
        }

        morphSufixBox.setHint(Component.literal("<Suffix>").withStyle(ChatFormatting.DARK_GRAY));
        morphPrefixBox.setHint(Component.literal("<Prefix>").withStyle(ChatFormatting.DARK_GRAY));
        useSufixes = new SwitchButton(
                centerX + 50, centerY - 40,
                70, 20,
                "Use suffixes",
                true,
                btn -> {
                    if (useSufixes.getValue()) {
                        morphSufixBox.setHint(Component.literal("<Suffix>").withStyle(ChatFormatting.DARK_GRAY));
                        morphPrefixBox.setHint(Component.literal("<Prefix>").withStyle(ChatFormatting.DARK_GRAY));

                        morphPrefixBox.setX(morphPrefixBox.getX() - 35);
                        useSufixes.setX(useSufixes.getX() + 35);
                        setRandomSufix.visible = true;
                        morphSufixBox.visible = true;
                        setRandomPrefix.setX(centerX - 85);

                    } else {
                        morphPrefixBox.setHint(Component.literal("<Name>").withStyle(ChatFormatting.DARK_GRAY));
                        morphSufixBox.setHint(Component.empty());
                        morphSufixBox.setValue("");

                        morphPrefixBox.setX(morphPrefixBox.getX() + 35);
                        useSufixes.setX(useSufixes.getX() - 35);
                        setRandomSufix.visible = false;
                        morphSufixBox.visible = false;
                        setRandomPrefix.setX(centerX - 40);

                    }
                }
        );
        this.addRenderableWidget(useSufixes);


        ageKit = new ToggleButton(
                centerX - 110, centerY + 20, 60, 20,
                "Kit",
                btn -> selectAge(ageKit)
        );

        ageApprentice = new ToggleButton(
                centerX - 40, centerY + 20, 80, 20,
                "Apprentice",
                btn -> selectAge(ageApprentice)
        );

        ageAdult = new ToggleButton(
                centerX + 50, centerY + 20, 60, 20,
                "Adult",
                btn -> selectAge(ageAdult)
        );

        genderMale = new ToggleButton(
                centerX - 110, centerY + 45, 70, 20,
                "Tom-cat",
                btn -> selectGender(genderMale)
        );
        genderFemale = new ToggleButton(
                centerX - 35, centerY + 45, 70, 20,
                "She-cat",
                btn -> selectGender(genderFemale)
        );
        genderNone = new ToggleButton(
                centerX + 40, centerY + 45, 70, 20,
                "Non-binary",
                btn -> selectGender(genderNone)
        );





//        this.addRenderableWidget(automaticSufix);

        this.addRenderableWidget(genderMale);
        this.addRenderableWidget(genderFemale);
        this.addRenderableWidget(genderNone);

        this.addRenderableWidget(ageKit);
        this.addRenderableWidget(ageApprentice);
        this.addRenderableWidget(ageAdult);


        saveButton = Button.builder(
                Component.literal("Next"),
                btn -> {
                    onBeforeSave();
                }
        ).bounds(centerX - 40, centerY + 85, 80, 20).build();

        this.addRenderableWidget(saveButton);
    }

    private void selectAge(ToggleButton selected) {
        ageKit.setSelected(false);
        ageApprentice.setSelected(false);
        ageAdult.setSelected(false);

        selected.setSelected(true);
    }

    private void selectGender(ToggleButton selected) {
        genderFemale.setSelected(false);
        genderMale.setSelected(false);
        genderNone.setSelected(false);

        selected.setSelected(true);
    }

    private void onBeforeSave() {
//        String clanName = clanNameBox.getValue().trim();

        String morphPrefix = morphPrefixBox.getValue().trim();
        String morphSufix = morphSufixBox.getValue().trim();
        boolean isAgeSelected = (ageKit.isSelected() || ageApprentice.isSelected() || ageAdult.isSelected());
//        boolean autoSufix = automaticSufix.getValue();
//
//        if (clanName.isEmpty() || morphPrefix.isEmpty()
//                || (useSufixes.getValue() && morphSufix.isEmpty())
//                || !isAgeSelected) {
//            textCooldown = 100;
//            return;
//        }

        if (morphPrefix.isEmpty()
                || (useSufixes.getValue() && morphSufix.isEmpty())
                || !isAgeSelected) {
            textCooldown = 100;
            return;
        }

        WCEClient.nextMenuSound();


        closing = true;
        animationTime = 0f;
    }

    private void onSave() {
//        String clanName = clanNameBox.getValue().trim();
        String morphPrefix = morphPrefixBox.getValue().trim();
        String morphSufix = morphSufixBox.getValue().trim();
        boolean isAgeSelected = (ageKit.isSelected() || ageApprentice.isSelected() || ageAdult.isSelected());
//        boolean autoSufix = automaticSufix.getValue();

//        if (clanName.isEmpty() || morphPrefix.isEmpty()
//                || (useSufixes.getValue() && morphSufix.isEmpty())
//                || !isAgeSelected) {
//            textCooldown = 100;
//            return;
//        }

        if (morphPrefix.isEmpty()
                || (useSufixes.getValue() && morphSufix.isEmpty())
                || !isAgeSelected) {
            textCooldown = 100;
            return;
        }

        String sufix = "";
        if (useSufixes.getValue()) {
            if (ageKit.isSelected()) {
                sufix = "kit";
            } else if (ageApprentice.isSelected()) {
                sufix = "paw";
            } else {
                sufix = morphSufixBox.getValue().trim();
            }
        }


        String Name = morphPrefixBox.getValue().trim() + sufix;


        WCEPlayerData data = new WCEPlayerData();

        data.setPrefix(morphPrefixBox.getValue());
        data.setSufix(morphSufixBox.getValue());
        data.setMorphName(Name);
        data.setUseSufixes(useSufixes.getValue());

        int variant = 0;
        data.setVariantData(variant);


        if (genderFemale.isSelected()) {
            data.setGenderData(1);
        } else if (genderMale.isSelected()) {
            data.setGenderData(0);
        } else {
            data.setGenderData(2);
        }


        if (ageKit.isSelected()) {
            data.setMorphAge(WCEPlayerData.Age.KIT);
        } else if (ageApprentice.isSelected()) {
            data.setMorphAge(WCEPlayerData.Age.APPRENTICE);
        } else {
            data.setMorphAge(WCEPlayerData.Age.ADULT);
        }

        if (ClientClanData.get().isFirstLoginHandled()) {
            data.setFirstLoginHandled(true);
        }



        this.minecraft.setScreen(null);
        if (!ClientClanData.get().isFirstLoginHandled()) {
            this.minecraft.setScreen(new CreateMorphGeneticsScreen(false));
        }

        ModPackets.sendToServer(new SaveClanDataPacket(data));

    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

}