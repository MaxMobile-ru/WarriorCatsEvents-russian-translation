package net.snowteb.warriorcats_events.screen.screens;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.common.util.LazyOptional;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.c2s.skilltree.*;
import net.snowteb.warriorcats_events.network.packet.c2s.clan.ReqMorphStatsPacket;
import net.snowteb.warriorcats_events.skills.ISkillData;
import net.snowteb.warriorcats_events.skills.PlayerSkill;
import net.snowteb.warriorcats_events.skills.PlayerSkillProvider;
import net.snowteb.warriorcats_events.stealth.PlayerStealthProvider;
import net.snowteb.warriorcats_events.screen.widgets.ModButton;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class SkillScreen extends Screen {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/skilltree_gui.png");

    String nextSpeedLevelCost;
    String nextHPLevelCost;
    String nextDMGLevelCost;
    String nextJumpLevelCost;
    String nextToughnessLevel;

    String stealthCost;
    String climbCost;
    Component stealthSwitchText;

    int currentSpeedLevel;
    int currentHPLevel;
    int currentDMGLevel;

    int currentJumpLevel;
    int currentToughnessLevel;

    private int waitTicksUntilScreen = 5;
    private boolean statsScreenClicked = false;

    private int imageHeight;
    private int imageWidth;


    public SkillScreen() {
        super(Component.literal("Skills"));
    }

    @Override
    protected void init() {
        defineText();

        addOrRefreshWidgets();

        this.imageWidth = 256;
        this.imageHeight = 166;

    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        guiGraphics.blit(TEXTURE,
                (width - imageWidth) / 2,
                (height - imageHeight - 50) / 2,
                0, 0,
                imageWidth, imageHeight,
                256, 166
        );
        guiGraphics.drawCenteredString(
                this.font,
                Component.translatable("screen.skill_tree"),
                centerX,
                centerY - 118,
                0xFFFFFF
        );
        guiGraphics.drawString(
                this.font,
                "Moonpool",
                this.width / 2 - 124,
                this.height / 2 + 45,
                0x20526885
        );


        if (currentSpeedLevel < PlayerSkill.maxSpeedLevel || currentHPLevel < PlayerSkill.maxHPLevel) {

            Component Text1 = Component.literal("");
            Component Text2 = Component.literal("");
            if (currentSpeedLevel < PlayerSkill.maxSpeedLevel) {
                Text1 = Component.translatable("screen.skill_tree.requiered_speed_max").append("\n");
            }
            if (currentHPLevel < PlayerSkill.maxHPLevel) {
                Text2 = Component.translatable("screen.skill_tree.requiered_hp_max");
            }


            float scale = 0.55f;
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(centerX - 40, centerY - 35, 0);
            guiGraphics.pose().scale(scale, scale, 1.0f);
            int x = (int) (this.width / 2 + 30 / scale);
            int y = (int) (this.height / 2 + 20 / scale);

            Component text = Text1.copy().append(Text2);


            int maxWidth = 200;
            List<FormattedCharSequence> lines = this.font.split(text, maxWidth);

            int lineHeight = this.font.lineHeight;
            int yOffset = 0;

            for (FormattedCharSequence line : lines) {
                guiGraphics.drawCenteredString(
                        this.font,
                        line,
                        0,
                        0 + yOffset,
                        0xff1900
                );
                yOffset += lineHeight;
            }
            guiGraphics.pose().popPose();
        }


        if (currentDMGLevel < PlayerSkill.maxDMGLevel || currentHPLevel < PlayerSkill.maxHPLevel) {


            Component Text1 = Component.literal("");
            Component Text2 = Component.literal("");
            if (currentDMGLevel < PlayerSkill.maxDMGLevel) {
                Text1 = Component.translatable("screen.skill_tree.requiered_claws_max").append("\n");
            }
            if (currentHPLevel < PlayerSkill.maxHPLevel) {
                Text2 = Component.translatable("screen.skill_tree.requiered_hp_max");
            }

            float scale = 0.55f;
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(centerX + 42, centerY - 35, 0);
            guiGraphics.pose().scale(scale, scale, 1.0f);

            Component text = Text1.copy().append(Text2);


            int maxWidth = 200;
            List<FormattedCharSequence> lines = this.font.split(text, maxWidth);

            int lineHeight = this.font.lineHeight;
            int yOffset = 0;

            for (FormattedCharSequence line : lines) {
                guiGraphics.drawCenteredString(
                        this.font,
                        line,
                        0,
                        0 + yOffset,
                        0xff1900
                );
                yOffset += lineHeight;
            }
            guiGraphics.pose().popPose();
        }

        if (currentToughnessLevel < PlayerSkill.maxArmorLevel || currentJumpLevel < PlayerSkill.maxJumpLevel) {

            Component Text1 = Component.literal("");
            Component Text2 = Component.literal("");
            if (currentToughnessLevel < PlayerSkill.maxArmorLevel) {
                Text1 = Component.translatable("screen.skill_tree.requiered_pelt_max").append("\n");
            }
            if (currentJumpLevel < PlayerSkill.maxJumpLevel) {
                Text2 = Component.translatable("screen.skill_tree.requiered_jump_max");
            }


            float scale = 0.55f;
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(centerX, centerY + 5, 0);
            guiGraphics.pose().scale(scale, scale, 1.0f);

            Component text = Text1.copy().append(Text2);


            int maxWidth = 200;
            List<FormattedCharSequence> lines = this.font.split(text, maxWidth);

            int lineHeight = this.font.lineHeight;
            int yOffset = 0;

            for (FormattedCharSequence line : lines) {
                guiGraphics.drawCenteredString(
                        this.font,
                        line,
                        0,
                        0 + yOffset,
                        0xff1900
                );
                yOffset += lineHeight;
            }
            guiGraphics.pose().popPose();
        }

        if (currentSpeedLevel < PlayerSkill.maxSpeedLevel || currentJumpLevel < PlayerSkill.maxJumpLevel) {

            Component Text1 = Component.literal("");
            Component Text2 = Component.literal("");
            if (currentSpeedLevel < PlayerSkill.maxSpeedLevel) {
                Text1 = Component.translatable("screen.skill_tree.requiered_speed_max").append("\n");
            }
            if (currentJumpLevel < PlayerSkill.maxJumpLevel) {
                Text2 = Component.translatable("screen.skill_tree.requiered_jump_max");
            }


            float scale = 0.55f;
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(centerX - 83, centerY + 5, 0);
            guiGraphics.pose().scale(scale, scale, 1.0f);
            int x = (int) (this.width / 2 + 30 / scale);
            int y = (int) (this.height / 2 + 20 / scale);

            Component text = Text1.copy().append(Text2);


            int maxWidth = 200;
            List<FormattedCharSequence> lines = this.font.split(text, maxWidth);

            int lineHeight = this.font.lineHeight;
            int yOffset = 0;

            for (FormattedCharSequence line : lines) {
                guiGraphics.drawCenteredString(
                        this.font,
                        line,
                        0,
                        0 + yOffset,
                        0xff1900
                );
                yOffset += lineHeight;
            }
            guiGraphics.pose().popPose();
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == GLFW.GLFW_KEY_E) {
            this.onClose();
            return true;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public void tick() {

        addOrRefreshWidgets();

        if (waitTicksUntilScreen <= 0) {
            Minecraft.getInstance().setScreen(new MorphGrowthScreen());
        }
        if (statsScreenClicked && waitTicksUntilScreen > 0) {
            waitTicksUntilScreen--;
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void defineText() {

        LocalPlayer player = this.minecraft.player;

        if (player != null) {

            currentSpeedLevel = player.getCapability(PlayerSkillProvider.SKILL_DATA)
                    .map(ISkillData::getSpeedLevel)
                    .orElse(player.getPersistentData().getInt("skill_speed_level"));
            currentHPLevel = player.getCapability(PlayerSkillProvider.SKILL_DATA)
                    .map(ISkillData::getHPLevel)
                    .orElse(player.getPersistentData().getInt("skill_hp_level"));
            currentDMGLevel = player.getCapability(PlayerSkillProvider.SKILL_DATA)
                    .map(ISkillData::getDMGLevel)
                    .orElse(player.getPersistentData().getInt("skill_dmg_level"));
            currentJumpLevel = player.getCapability(PlayerSkillProvider.SKILL_DATA)
                    .map(ISkillData::getJumpLevel)
                    .orElse(player.getPersistentData().getInt("skill_jump_level"));
            currentToughnessLevel = player.getCapability(PlayerSkillProvider.SKILL_DATA)
                    .map(ISkillData::getArmorLevel)
                    .orElse(player.getPersistentData().getInt("skill_armor_level"));

            LazyOptional<ISkillData> cap = player.getCapability(PlayerSkillProvider.SKILL_DATA);
            cap.ifPresent(data -> {

                        nextSpeedLevelCost = getNextLevelCost(data.getSpeedLevel(), PlayerSkill.maxSpeedLevel, PlayerSkill.getDefaultSpeedCost());
                        nextHPLevelCost = getNextLevelCost(data.getHPLevel(), PlayerSkill.maxHPLevel, PlayerSkill.getDefaultHPCost());
                        nextDMGLevelCost = getNextLevelCost(data.getDMGLevel(), PlayerSkill.maxDMGLevel, PlayerSkill.getDefaultDMGCost());
                        nextJumpLevelCost = getNextLevelCost(data.getJumpLevel(), PlayerSkill.maxJumpLevel, PlayerSkill.getDefaultJumpCost());
                        nextToughnessLevel = getNextLevelCost(data.getArmorLevel(), PlayerSkill.maxArmorLevel, PlayerSkill.getDefaultArmorCost());

                        player.getCapability(PlayerStealthProvider.STEALTH_MODE).ifPresent(stealth -> {

                            if (stealth.isUnlocked()) {
                                stealthCost = "MAX";
                            } else {
                                stealthSwitchText = Component.literal("--");
                                stealthCost = PlayerSkill.getDefaultStealthCost() + "xp";
                            }

                            if (stealth.isOn()) {
                                stealthSwitchText = Component.translatable("screen.skill_tree.on").withStyle(ChatFormatting.GREEN);
                            } else {
                                stealthSwitchText = Component.translatable("screen.skill_tree.off").withStyle(ChatFormatting.YELLOW);
                            }


                        });

                        if (data.isClimbUnlocked()) {
                            climbCost = "MAX";
                        } else {
                            climbCost = PlayerSkill.getDefaultClimbCost() + "xp";
                        }

                    }
            );


        }
    }

    private String getNextLevelCost(int level, int maxLevel, int defaultCost) {

        String result;
        if (level < maxLevel) {
            result = (defaultCost * (level + 1)) + "xp";
        } else {
            result = "MAX";
        }
        return result;
    }

    private void addOrRefreshWidgets() {

        defineText();

        this.clearWidgets();

        this.addRenderableWidget(Button.builder(
                Component.translatable("screen.skill_tree.morph_stats"),
                btn -> {
                    ModPackets.sendToServer(new ReqMorphStatsPacket());
                    statsScreenClicked = true;

                }
        ).bounds(5, 5, 80, 20).build());

        this.addRenderableWidget(new ModButton(
                this.width / 2 - 40,
                this.height / 2 - 17,
                80, 20,
                Component.translatable("screen.skill_tree.stealth_cost", stealthCost),
                b -> {
                    ModPackets.sendToServer(new CtSUnlockStealthPacket());

                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/modbutton.png"),
                80, 20
        ));

        this.addRenderableWidget(new ModButton(
                this.width / 2 + 43,
                this.height / 2 + 33,
                80, 20,
                Component.translatable("screen.skill_tree.reset_tree"),
                b -> {
                    ModPackets.sendToServer(new ResetSkillsPacket());
                    ModPackets.sendToServer(new ReqSkillDataPacket());
                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/modbutton.png"),
                80, 20
        ));

        this.addRenderableWidget(new ModButton(
                this.width / 2 + 45,
                this.height / 2 - 97,
                80, 20,
                Component.translatable("screen.skill_tree.claws_cost", nextDMGLevelCost),
                b -> {
                    ModPackets.sendToServer(new CtSMoreDMGPacket());
                    ModPackets.sendToServer(new ReqSkillDataPacket());

                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/modbutton.png"),
                80, 20
        ));

        this.addRenderableWidget(new ModButton(
                this.width / 2 - 40,
                this.height / 2 - 97,
                80, 20,
                Component.translatable("screen.skill_tree.hp_cost", nextHPLevelCost),
                b -> {
                    ModPackets.sendToServer(new CtSMoreHPPacket());
                    ModPackets.sendToServer(new ReqSkillDataPacket());

                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/modbutton.png"),
                80, 20
        ));

        this.addRenderableWidget(new ModButton(
                this.width / 2 - 124,
                this.height / 2 - 97,
                80, 20,
                Component.translatable("screen.skill_tree.speed_cost", nextSpeedLevelCost),
                b -> {
                    ModPackets.sendToServer(new CtSMoreSpeedPacket());
                    ModPackets.sendToServer(new ReqSkillDataPacket());

                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/modbutton.png"),
                80, 20
        ));
        this.addRenderableWidget(new ModButton(
                this.width / 2 - 81,
                this.height / 2 - 57,
                80, 20,
                Component.translatable("screen.skill_tree.jump_cost", nextJumpLevelCost),
                b -> {
                    ModPackets.sendToServer(new CtSMoreJumpPacket());
                    ModPackets.sendToServer(new ReqSkillDataPacket());

                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/modbutton.png"),
                80, 20
        ));
        this.addRenderableWidget(new ModButton(
                this.width / 2 + 2,
                this.height / 2 - 57,
                80, 20,
                Component.translatable("screen.skill_tree.pelt_cost", nextToughnessLevel),
                b -> {
                    ModPackets.sendToServer(new CtSMoreArmorPacket());
                    ModPackets.sendToServer(new ReqSkillDataPacket());

                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/modbutton.png"),
                80, 20
        ));
        this.addRenderableWidget(new ModButton(
                this.width / 2 + 45,
                this.height / 2 - 17,
                20, 20,
                stealthSwitchText,
                b -> {
                    ModPackets.sendToServer(new CtSSwitchStealthPacket());

                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/modbutton.png"),
                20, 20
        ));

        this.addRenderableWidget(new ModButton(
                this.width / 2 - 124,
                this.height / 2 - 17,
                80, 20,
                Component.translatable("screen.skill_tree.climb_cost", climbCost),
                b -> {
                    ModPackets.sendToServer(new CtSUnlockClimbPacket());
                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/modbutton.png"),
                80, 20
        ));


    }

}
