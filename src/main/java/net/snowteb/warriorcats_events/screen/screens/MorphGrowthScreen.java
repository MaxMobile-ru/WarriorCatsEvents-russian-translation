package net.snowteb.warriorcats_events.screen.screens;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.snowteb.warriorcats_events.clan.WCEPlayerData;
import net.snowteb.warriorcats_events.client.ClientClanData;
import net.snowteb.warriorcats_events.client.MorphStatsClientData;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.c2s.skilltree.ReqSkillDataPacket;
import net.snowteb.warriorcats_events.network.packet.c2s.clan.UpdateClanDataPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MorphGrowthScreen extends Screen {

    private int squirrelKilled; //kit & app
    private int mouseKilled; // kit & app
    private int pigeonKilled; // app
    private int badgerKilled; // app

    private int mossCollected; // kit & app
    private int feathersCollected; // kit

    private int minutesPlayed; // kit & app
    private int hoursSurvived; // kit & app

    private String morphAgeKey = "";

    private Button growButton;


    public MorphGrowthScreen() {
        super(Component.literal(ClientClanData.get().getMorphName()));
    }

    public void applySync() {
        this.squirrelKilled = MorphStatsClientData.squirrelKilled;
        this.mouseKilled = MorphStatsClientData.mouseKilled;
        this.pigeonKilled = MorphStatsClientData.pigeonKilled;
        this.badgerKilled = MorphStatsClientData.badgerKilled;

        this.mossCollected = MorphStatsClientData.mossColected;
        this.feathersCollected = MorphStatsClientData.feathersCollected;

        this.minutesPlayed = (((MorphStatsClientData.timePlayed) / 20) / 60);
        this.hoursSurvived = ((((MorphStatsClientData.timeSurvived) / 20) / 60) / 60);

        WCEPlayerData.Age morphAge = ClientClanData.get().getMorphAge();

        if (morphAge != null) {
            if (morphAge == WCEPlayerData.Age.KIT) {
                morphAgeKey = "kit";
            } else if (morphAge == WCEPlayerData.Age.APPRENTICE) {
                morphAgeKey = "apprentice";
            } else {
                morphAgeKey = "adult";
            }
        }
    }

    @Override
    protected void init() {
        applySync();
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        this.addRenderableWidget(Button.builder(
                Component.literal("< Back"),
                (btn) -> {
                    Minecraft.getInstance().setScreen(new SkillScreen());
                    ModPackets.sendToServer(new ReqSkillDataPacket());
                }
        ).bounds(5, 5, 80, 20).build());


        growButton = Button.builder(
                Component.literal("Grow"),
                btn -> {
                    if (Objects.equals(morphAgeKey, "kit")) {
                        performKitToApp();
                    } else if (Objects.equals(morphAgeKey, "apprentice")) {
                        performAppToAdult();
                    }
                    Minecraft.getInstance().setScreen(null);
                }
        ).bounds(centerX-40, centerY + 0, 80, 20).build();
        if (!Objects.equals(morphAgeKey, "") && !Objects.equals(morphAgeKey, "adult")) {
            this.addRenderableWidget(growButton);
        }
        growButtonState();

    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(pGuiGraphics);
        int centerX = this.width / 2;
        int centerY = this.height / 2;



        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().scale(0.8f,0.8f,0.8f);
        requirementsList(pGuiGraphics, (int) (centerX*1.25f), (int) ((centerY*1.25f) + 30));
        pGuiGraphics.pose().popPose();

        int textWidth = 160;
        int boxTop = centerY - 120;
        int boxBottom = boxTop + 230;
        int boxLeft = centerX - (textWidth / 2) - 16;
        int boxRight = centerX + (textWidth / 2) + 16;

        pGuiGraphics.fill(boxLeft, boxTop, boxRight, boxBottom, 0x50000000);

        pGuiGraphics.renderOutline(boxLeft, boxTop, boxRight - boxLeft, boxBottom - boxTop, 0xFFFFFFFF);

        int textY = boxTop + 8;
        pGuiGraphics.drawCenteredString(font, this.title, centerX, textY, 0xFFFFFFFF);
        int lineY = textY + 14;
        pGuiGraphics.hLine(boxLeft + 8, boxRight - 8, lineY, 0xFFFFFFFF);

        textY += 20;
        pGuiGraphics.drawCenteredString(this.font, Component.translatable("screen.morphgrowth.squirrel_killed", squirrelKilled), centerX, textY, 0xDDDDFF);
        textY += 10;
        pGuiGraphics.drawCenteredString(this.font, Component.translatable("screen.morphgrowth.mice_killed", mouseKilled), centerX, textY, 0xDDDDFF);
        textY += 10;
        pGuiGraphics.drawCenteredString(this.font, Component.translatable("screen.morphgrowth.pigeon_killed", pigeonKilled), centerX, textY, 0xDDDDFF);
        textY += 10;
        pGuiGraphics.drawCenteredString(this.font, Component.translatable("screen.morphgrowth.badger_killed", badgerKilled), centerX, textY, 0xDDDDFF);
        textY += 10;
        pGuiGraphics.drawCenteredString(this.font, Component.translatable("screen.morphgrowth.feathers_collected",  feathersCollected), centerX, textY, 0xDDDDFF);
        textY += 10;
        pGuiGraphics.drawCenteredString(this.font, Component.translatable("screen.morphgrowth.time_played", minutesPlayed), centerX, textY, 0xDDDDFF);
        textY += 10;
        pGuiGraphics.drawCenteredString(this.font, Component.translatable("screen.morphgrowth.time_survived", hoursSurvived), centerX, textY, 0xDDDDFF);


        super.render(pGuiGraphics, mouseX, mouseY, partialTick);
    }

    private void performKitToApp() {
        ModPackets.sendToServer(new UpdateClanDataPacket(0));
    }

    private void performAppToAdult() {
        ModPackets.sendToServer(new UpdateClanDataPacket(1));
    }

    private void growButtonState() {

        boolean canGrow = false;

        switch (morphAgeKey) {
            case "kit" -> {
                if (ClientClanData.get().getMorphAge() == WCEPlayerData.Age.KIT) {
                    if (squirrelKilled >= 5
                            && mouseKilled >= 10
//                            && mossCollected >= 10
                            && feathersCollected >= 5
                            && minutesPlayed >= 60
                            && hoursSurvived >= 1
                    ) canGrow = true;
                }
            }
            case "apprentice" -> {
                if (ClientClanData.get().getMorphAge() == WCEPlayerData.Age.APPRENTICE) {
                    if (squirrelKilled >= 15
                            && mouseKilled >= 30
                            && pigeonKilled >= 1
                            && badgerKilled >= 1
//                            && mossCollected >= 20
                            && minutesPlayed >= 120
                            && hoursSurvived >= 2
                    ) canGrow = true;
                }
            }
        }

        growButton.active = canGrow;
        growButton.setFocused(false);
    }

    private void requirementsList(GuiGraphics pGuiGraphics, int centerX, int startY) {

        List<Component> lines = new ArrayList<>();

        if (ClientClanData.get().getMorphAge() == WCEPlayerData.Age.KIT) {
            if (squirrelKilled < 5) lines.add(Component.translatable("screen.morphgrowth.squirrel_requierement", 5).withStyle(ChatFormatting.RED));
            if (mouseKilled < 10) lines.add(Component.translatable("screen.morphgrowth.mice_requierement", 10).withStyle(ChatFormatting.RED));
            if (feathersCollected < 5) lines.add(Component.translatable("screen.morphgrowth.feather_requierement", 5).withStyle(ChatFormatting.RED));
            if (minutesPlayed < 60) lines.add(Component.translatable("screen.morphgrowth.time_requierement", 60).withStyle(ChatFormatting.RED));
            if (hoursSurvived < 1) lines.add(Component.translatable("screen.morphgrowth.time_survived_requierement", 60).withStyle(ChatFormatting.RED));
        }
        else if (ClientClanData.get().getMorphAge() == WCEPlayerData.Age.APPRENTICE) {
            if (squirrelKilled < 15) lines.add(Component.translatable("screen.morphgrowth.squirrel_requierement", 15).withStyle(ChatFormatting.RED));
            if (mouseKilled < 30) lines.add(Component.translatable("screen.morphgrowth.mice_requierement", 30).withStyle(ChatFormatting.RED));
            if (pigeonKilled < 1) lines.add(Component.translatable("screen.morphgrowth.pigeon_requierement", 1).withStyle(ChatFormatting.RED));
            if (badgerKilled < 1) lines.add(Component.translatable("screen.morphgrowth.badger_requierement", 1).withStyle(ChatFormatting.RED));
            if (minutesPlayed < 120) lines.add(Component.translatable("screen.morphgrowth.time_requierement", 120).withStyle(ChatFormatting.RED));
            if (hoursSurvived < 2) lines.add(Component.translatable("screen.morphgrowth.time_survived_requierement", 120).withStyle(ChatFormatting.RED));
        }



        int y = startY;

        for (Component line : lines) {
            pGuiGraphics.drawCenteredString(this.font, line, centerX, y, 0xDDDDFF);
            y += 10;
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}

