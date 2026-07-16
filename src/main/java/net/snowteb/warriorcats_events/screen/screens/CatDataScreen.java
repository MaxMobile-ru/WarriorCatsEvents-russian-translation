package net.snowteb.warriorcats_events.screen.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.snowteb.warriorcats_events.WCEClient;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.clan.ClanData;
import net.snowteb.warriorcats_events.client.ClientClanData;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.c2s.cats.*;
import net.snowteb.warriorcats_events.network.packet.s2c.cats.SyncCatDataPacket;
import net.snowteb.warriorcats_events.screen.widgets.ModButton;
import org.lwjgl.glfw.GLFW;

import static net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity.AGE_SYNC;

public class CatDataScreen extends Screen {
    WCatEntity wCatEntity;
    Component preText = Component.literal("✧ ").withStyle(ChatFormatting.GOLD);

    Component name = Component.literal("...");
    String nameToString = "...";
    Component clanName = Component.literal("...");
    Component genderText = Component.literal("...");
    Component ageText = Component.literal("...");
    Component personalityText = Component.literal("...");
    Component rankText = Component.literal("...");
    Component catMate = Component.literal("...");
    Component expectingText = Component.literal("...");
    Component KitTime = Component.literal("...");
    Component moodText = Component.literal("...");
    int friendshipLevel = 0;
    Component friendshipLevelText = Component.literal("...");
    Component helloSentence = Component.literal("...");
    Component setModeSentence = Component.literal("...");
    Component catMother = Component.literal("...");
    Component catFather = Component.literal("...");
    Component parentsText = Component.literal("...");


    boolean expectingKits;
    boolean interactionCooldownTooltip = false;
    float kittingTime;
    float moons;
    int ticks = 0;
    double friendshipPointsPixels;

    private String activeMenu = "main";

    private static final ResourceLocation CAT_NAME_TOAST =
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/clan_setup/cat_toast.png");


    private static final ResourceLocation SOCIALHEARTS_EMPTY =
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/clan_setup/socialhearts_empty.png");

    private static final ResourceLocation SOCIALHEARTS_FILL =
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/clan_setup/socialhearts_fill.png");
    private static final ResourceLocation HEARTS_FILL =
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/clan_setup/hearts_fill.png");

    private final boolean isPlayerValidDeputy;

    public CatDataScreen(Component pTitle, WCatEntity cat, boolean isDeputy) {
        super(pTitle);
        this.wCatEntity = cat;
        this.isPlayerValidDeputy = isDeputy;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        int xPositionPanel = 10;


        pGuiGraphics.blit(CAT_NAME_TOAST, 0, 0, 0, 0
                , 172, 57, 172, 57);


        pGuiGraphics.blit(SOCIALHEARTS_EMPTY, 43, 28,
                0, 0
                , 99, 9,
                99, 9);

        RenderSystem.enableBlend();
        pGuiGraphics.setColor(1f,1f,1f,0.2f);
        pGuiGraphics.blit(SOCIALHEARTS_FILL, 43, 28,
                0, 0,
                99, 9,
                99, 9);
        pGuiGraphics.setColor(1f,1f,1f,1f);
        RenderSystem.disableBlend();

        pGuiGraphics.enableScissor( 43, 28, 43 + (friendshipLevel), 37);
        pGuiGraphics.blit(SOCIALHEARTS_FILL, 43, 28,
                0, 0
                , 99, 9,
                99, 9);
        pGuiGraphics.disableScissor();



        int health = (int) ((wCatEntity.getHealth() / wCatEntity.getMaxHealth())*100);

        pGuiGraphics.blit(SOCIALHEARTS_EMPTY, 43, 40,
                0, 0
                , 99, 9,
                99, 9);

        pGuiGraphics.enableScissor( 43, 40, 43 + (health), 49);
        pGuiGraphics.blit(HEARTS_FILL, 43, 40,
                0, 0
                , 99, 9,
                99, 9);
        pGuiGraphics.disableScissor();


        if (name != null) {
            if (interactionCooldownTooltip && wCatEntity.isTame()) {
                pGuiGraphics.renderTooltip(Minecraft.getInstance().font, Component.empty().append(preText).append(name).append(" already interacted recently."), this.width, 30);
            }
        }


        {

            if (activeMenu.equals("interact")) {
                int textWidth = Math.max(Minecraft.getInstance().font.width(helloSentence), 70);
                int boxTop = centerY - 50;
                int boxBottom = boxTop + 110;
                int boxLeft = centerX - (textWidth / 2) - 16;
                int boxRight = centerX + (textWidth / 2) + 16;

                pGuiGraphics.fill(boxLeft, boxTop, boxRight, boxBottom, 0x50000000);

                pGuiGraphics.renderOutline(boxLeft, boxTop, boxRight - boxLeft, boxBottom - boxTop, 0xFFFFFFFF);

                int textY = boxTop + 8;
                if (helloSentence != null) {
                    pGuiGraphics.drawCenteredString(font, helloSentence, centerX, textY, 0xFFFFFFFF);
                }
                int lineY = textY + 14;
                pGuiGraphics.hLine(boxLeft + 8, boxRight - 8, lineY, 0xFFFFFFFF);

            }

            if (activeMenu.equals("mode")) {
                if (setModeSentence != null) {
                    int textWidth = Math.max(Minecraft.getInstance().font.width(setModeSentence), 85);
                    int boxTop = centerY - 50;
                    int boxBottom = boxTop + 90;
                    int boxLeft = centerX - (textWidth / 2) - 16;
                    int boxRight = centerX + (textWidth / 2) + 16;

                    pGuiGraphics.fill(boxLeft, boxTop, boxRight, boxBottom, 0x50000000);

                    pGuiGraphics.renderOutline(boxLeft, boxTop, boxRight - boxLeft, boxBottom - boxTop, 0xFFFFFFFF);

                    int textY = boxTop + 8;
                    pGuiGraphics.drawCenteredString(font, setModeSentence, centerX, textY, 0xFFFFFFFF);
                    int lineY = textY + 14;
                    pGuiGraphics.hLine(boxLeft + 8, boxRight - 8, lineY, 0xFFFFFFFF);
                }
            }

            if (activeMenu.equals("home")) {
                if (setModeSentence != null) {
                    int textWidth = Math.max(Minecraft.getInstance().font.width(setModeSentence), 85);
                    int boxTop = centerY - 50;
                    int boxBottom = boxTop + 90;
                    int boxLeft = centerX - (textWidth / 2) - 16;
                    int boxRight = centerX + (textWidth / 2) + 16;

                    pGuiGraphics.fill(boxLeft, boxTop, boxRight, boxBottom, 0x50000000);

                    pGuiGraphics.renderOutline(boxLeft, boxTop, boxRight - boxLeft, boxBottom - boxTop, 0xFFFFFFFF);

                    int textY = boxTop + 8;
                    pGuiGraphics.drawCenteredString(font, setModeSentence, centerX, textY, 0xFFFFFFFF);
                    int lineY = textY + 14;
                    pGuiGraphics.hLine(boxLeft + 8, boxRight - 8, lineY, 0xFFFFFFFF);
                }
            }
        }
        float scale1 = 1.25f;
        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().scale(scale1, scale1, 1f);
        pGuiGraphics.drawCenteredString(Minecraft.getInstance().font, name, 75, 11, 0xFFFFFF);
        pGuiGraphics.pose().popPose();


        {
            if (friendshipLevel >= 0 && wCatEntity.isTame()) {
                friendshipLevelText = Component.empty().append(Component.translatable("screen.catdata.clanmates"))
                        .append(Component.literal( " (" + friendshipLevel + "/100)").withStyle(ChatFormatting.GRAY));
            }
            if (friendshipLevel > 20) {
                friendshipLevelText = Component.empty().append(Component.translatable("screen.catdata.trusted_clanmates"))
                        .append(Component.literal( " (" + friendshipLevel + "/100)").withStyle(ChatFormatting.GRAY));
            }
            if (friendshipLevel > 40) {
                friendshipLevelText = Component.empty().append(Component.translatable("screen.catdata.frieds"))
                        .append(Component.literal( " (" + friendshipLevel + "/100)").withStyle(ChatFormatting.GRAY));
            }
            if (friendshipLevel > 60) {
                friendshipLevelText = Component.empty().append(Component.translatable("screen.catdata.good_friends"))
                        .append(Component.literal( " (" + friendshipLevel + "/100)").withStyle(ChatFormatting.GRAY));
            }
            if (friendshipLevel > 80) {
                friendshipLevelText = Component.empty().append(Component.translatable("screen.catdata.real_friends"))
                        .append(Component.literal( " (" + friendshipLevel + "/100)").withStyle(ChatFormatting.GRAY));
            }
            if (friendshipLevel > 95) {
                friendshipLevelText = Component.empty().append(Component.translatable("screen.catdata.best_friends"))
                        .append(Component.literal( " (" + friendshipLevel + "/100)").withStyle(ChatFormatting.GRAY));
                if (Minecraft.getInstance().player != null) {
                    if (ClientClanData.get().getMateUUID() != null) {
                        if (ClientClanData.get().getMateUUID().equals(wCatEntity.getUUID())) {
                            friendshipLevelText = Component.empty().append(Component.translatable("screen.catdata.mates"))
                                    .append(Component.literal( " (" + friendshipLevel + "/100)").withStyle(ChatFormatting.GRAY));
                        }
                    }
                }
            }
        }


        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().scale(0.9f,0.9f,0.9f);

        boolean renderFriendshipText = pMouseX >= 43 && pMouseX <= 142 && pMouseY >= 28 && pMouseY <= 37 && wCatEntity.isTame();

        if (renderFriendshipText) {
            pGuiGraphics.renderTooltip(Minecraft.getInstance().font, Component.empty().append(preText).append(friendshipLevelText) , pMouseX, pMouseY);
        }


        {
            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate(0, 70, 0);
            float scale = 0.9f;
            pGuiGraphics.pose().scale(scale, scale, scale);
            int yOffset = 15;
            pGuiGraphics.renderTooltip(Minecraft.getInstance().font, Component.empty().append(preText).append(clanName), xPositionPanel, 0 + yOffset);
            pGuiGraphics.renderTooltip(Minecraft.getInstance().font, Component.empty().append(preText).append(genderText), xPositionPanel, 20 + yOffset);
            pGuiGraphics.renderTooltip(Minecraft.getInstance().font, Component.empty().append(preText).append(parentsText), xPositionPanel, 40 + yOffset);
            pGuiGraphics.renderTooltip(Minecraft.getInstance().font, Component.empty().append(preText).append(ageText), xPositionPanel, 60 + yOffset);
            pGuiGraphics.renderTooltip(Minecraft.getInstance().font, Component.empty().append(preText).append(personalityText), xPositionPanel, 80 + yOffset);
            pGuiGraphics.renderTooltip(Minecraft.getInstance().font, Component.empty().append(preText).append(moodText), xPositionPanel, 100 + yOffset);
            pGuiGraphics.renderTooltip(Minecraft.getInstance().font, Component.empty().append(preText).append(rankText), xPositionPanel, 120 + yOffset);
            pGuiGraphics.renderTooltip(Minecraft.getInstance().font, Component.empty().append(preText).append(catMate), xPositionPanel, 140 + yOffset);
            pGuiGraphics.renderTooltip(Minecraft.getInstance().font, Component.empty().append(preText).append(expectingText), xPositionPanel, 160 + yOffset);
            if (expectingKits) {
                pGuiGraphics.renderTooltip(Minecraft.getInstance().font, Component.empty().append(preText).append(KitTime), xPositionPanel, 180 + yOffset);
            }
            pGuiGraphics.pose().popPose();
        }

        pGuiGraphics.pose().popPose();

        for (Renderable renderable : this.renderables) {
            renderable.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        }

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(0,0,300);
        WCEClient.renderDiseaseTooltipsUtD(wCatEntity, pGuiGraphics, 170, 16, pMouseX, pMouseY);
        pGuiGraphics.pose().popPose();

    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        name = wCatEntity.hasCustomName() ? wCatEntity.getCustomName() : Component.literal("Unknown cat");
        nameToString = wCatEntity.hasCustomName() ? wCatEntity.getCustomName().getString() : "Unknown cat";
        genderText = wCatEntity.isMale() ? Component.literal("Tom-cat") : Component.literal("She-cat");

        catMother = wCatEntity.getMother();
        catFather = wCatEntity.getFather();

        if (catMother.equals(Component.literal("None")) && catFather.equals(Component.literal("None"))) {
            parentsText = Component.translatable("screen.catdata.no_parents");
        } else {
            if (catMother.equals(Component.literal("None")) && !catFather.equals(Component.literal("None"))) {
                parentsText = Component.translatable("screen.catdata.one_parent",
                        catFather.copy().withStyle(ChatFormatting.AQUA));
            } else if (!catMother.equals(Component.literal("None")) && catFather.equals(Component.literal("None"))) {
                parentsText = Component.translatable("screen.catdata.one_parent",
                        catMother.copy().withStyle(ChatFormatting.AQUA));
            } else {
                parentsText = Component.translatable("screen.catdata.both_parents",
                        catMother.copy().withStyle(ChatFormatting.AQUA),
                        catFather.copy().withStyle(ChatFormatting.AQUA));
            }
        }

        if (wCatEntity.getMate().equals(Component.literal("None"))) {
            catMate = Component.translatable("screen.catdata.no_mate");
        } else {
            catMate = Component.translatable("screen.catdata.mate",
                    wCatEntity.getMate().copy().withStyle(ChatFormatting.AQUA));
        }

        if (wCatEntity.getClan().equals(Component.literal("None")) || wCatEntity.getClan() == null || wCatEntity.getClan().getString().isEmpty()) {
            clanName = Component.translatable("screen.catdata.no_clan");
        } else {
            clanName = Component.translatable("screen.catdata.clan", wCatEntity.getClan().copy());
        }

        if (wCatEntity.getPersonality() != null) {
            if (wCatEntity.getRank() == WCatEntity.Rank.KIT) {
                switch (wCatEntity.getPersonality()) {
                    case CAUTIOUS -> helloSentence = Component.literal("Meow");
                    case CALM ->  helloSentence = Component.literal("Meow!");
                    case AMBITIOUS -> helloSentence = Component.literal("Meow!");
                    case RECKLESS -> helloSentence = Component.literal("Meow!");
                    case SHY ->  helloSentence = Component.literal("Mrrow");
                    case GRUMPY ->  helloSentence = Component.literal("...");
                    case HUMBLE ->   helloSentence = Component.literal("Meow");
                    case FRIENDLY ->   helloSentence = Component.literal("Meow!");
                    case INDEPENDENT ->   helloSentence = Component.literal("Meow");
                }
            } else if (wCatEntity.getRank() == WCatEntity.Rank.APPRENTICE) {
                switch (wCatEntity.getPersonality()) {
                    case CAUTIOUS -> helloSentence = Component.literal("Hey ").append(ClientClanData.get().getMorphName());
                    case CALM ->  helloSentence = Component.literal("Hello, ").append(ClientClanData.get().getMorphName());
                    case AMBITIOUS -> helloSentence = Component.literal("Hello, ").append(ClientClanData.get().getMorphName());
                    case RECKLESS -> helloSentence = Component.literal("Hi ").append(ClientClanData.get().getMorphName()).append("!");
                    case SHY ->  helloSentence = Component.literal("Oh, h-hi ").append(ClientClanData.get().getMorphName()).append("!");
                    case GRUMPY ->  helloSentence = Component.literal("Huh, ").append(ClientClanData.get().getMorphName());
                    case HUMBLE ->   helloSentence = Component.literal("Hi ").append(ClientClanData.get().getMorphName()).append("!");
                    case FRIENDLY ->   helloSentence = Component.literal("").append(ClientClanData.get().getMorphName()).append("!");
                    case INDEPENDENT ->   helloSentence = Component.literal("Hi, ").append(ClientClanData.get().getMorphName());
                }
            } else {
                switch (wCatEntity.getPersonality()) {
                    case CAUTIOUS -> helloSentence = Component.literal("Oh, hey ").append(ClientClanData.get().getMorphName());
                    case CALM ->  helloSentence = Component.literal("Hello, ").append(ClientClanData.get().getMorphName());
                    case AMBITIOUS -> helloSentence = Component.literal("Hello there, ").append(ClientClanData.get().getMorphName());
                    case RECKLESS -> helloSentence = Component.literal("Hi ").append(ClientClanData.get().getMorphName()).append("!");
                    case SHY ->  helloSentence = Component.literal("Oh, h-hi ").append(ClientClanData.get().getMorphName()).append("!");
                    case GRUMPY ->  helloSentence = Component.literal("Hello, any good news, ").append(ClientClanData.get().getMorphName()).append("?");
                    case HUMBLE ->   helloSentence = Component.literal("Hi ").append(ClientClanData.get().getMorphName()).append("!");
                    case FRIENDLY ->   helloSentence = Component.literal("").append(ClientClanData.get().getMorphName()).append("! So good to see you!");
                    case INDEPENDENT ->   helloSentence = Component.literal("Greetings, ").append(ClientClanData.get().getMorphName());
                }
            }
        }


        if (wCatEntity.getRank() == WCatEntity.Rank.KIT) {
            setModeSentence = switch (wCatEntity.getPersonality()) {
                case NONE -> Component.empty();
                case CALM -> Component.literal("Meow?");
                case GRUMPY -> Component.literal("...?");
                case CAUTIOUS -> Component.literal("Meow?");
                case INDEPENDENT -> Component.literal("Meow!");
                case FRIENDLY -> Component.literal("").append(ClientClanData.get().getMorphName()).append("?");
                case SHY -> Component.literal("M-meow?");
                case AMBITIOUS -> Component.literal("Adventure!");
                case HUMBLE -> Component.literal("Meow?");
                case RECKLESS -> Component.literal("Meow!");
            };
        } else if (wCatEntity.getRank() == WCatEntity.Rank.APPRENTICE || wCatEntity.isBaby()) {
            setModeSentence = switch (wCatEntity.getPersonality()) {
                case NONE -> Component.empty();
                case CALM -> Component.literal("Yes ").append(ClientClanData.get().getMorphName()).append("?");
                case GRUMPY -> Component.literal("Huh?");
                case CAUTIOUS -> Component.literal("How can I be of help?");
                case INDEPENDENT -> Component.literal("Yes ").append(ClientClanData.get().getMorphName()).append("!");
                case FRIENDLY -> Component.literal("").append(ClientClanData.get().getMorphName()).append("!");
                case SHY -> Component.literal("M-me? Yes!");
                case AMBITIOUS -> Component.literal("Are we going on an adventure, ").append(ClientClanData.get().getMorphName()).append("?!");
                case HUMBLE -> Component.literal("How can I help, ").append(ClientClanData.get().getMorphName()).append("?");
                case RECKLESS -> Component.literal("I'm ready!");
            };
        } else {
            setModeSentence = switch (wCatEntity.getPersonality()) {
                case NONE -> Component.empty();
                case CALM -> Component.literal("What can i do for you, ").append(ClientClanData.get().getMorphName()).append("?");
                case GRUMPY -> Component.literal("What do you want?");
                case CAUTIOUS -> Component.literal("How can I help you, ").append(ClientClanData.get().getMorphName()).append("?");
                case INDEPENDENT -> Component.literal("What do you need from me?");
                case FRIENDLY -> Component.literal("Tell me what I can do for you, ").append(ClientClanData.get().getMorphName()).append("!");
                case SHY -> Component.literal("M-me? Sure, what do you need?");
                case AMBITIOUS -> Component.literal("Yes, ").append(ClientClanData.get().getMorphName()).append("?");
                case HUMBLE -> Component.literal("I'll do my best, what can I do, ").append(ClientClanData.get().getMorphName()).append("?");
                case RECKLESS -> Component.literal("Trouble? Action? How can I help?");
            };
        }

        rankText = switch (wCatEntity.getRank()) {
            case NONE -> Component.literal("Loner");
            case KIT -> Component.literal("Kit");
            case APPRENTICE -> Component.literal("Apprentice");
            case WARRIOR -> Component.literal("Warrior");
            case MEDICINE -> Component.literal("Medicine Cat");
            case DEPUTY -> Component.literal("Deputy");
        };

        personalityText = switch (wCatEntity.getPersonality()) {
            case NONE -> Component.translatable("screen.catdata.personality_none");
            case CALM -> Component.translatable("screen.catdata.personality_calm");
            case GRUMPY -> Component.translatable("screen.catdata.personality_grumpy");
            case CAUTIOUS -> Component.translatable("screen.catdata.personality_cautious");
            case INDEPENDENT -> Component.translatable("screen.catdata.personality_independent");
            case FRIENDLY -> Component.translatable("screen.catdata.personality_friendly");
            case SHY -> Component.translatable("screen.catdata.personality_shy");
            case AMBITIOUS -> Component.translatable("screen.catdata.personality_ambitious");
            case HUMBLE -> Component.translatable("screen.catdata.personality_humble");
            case RECKLESS -> Component.translatable("screen.catdata.personality_reckless");
        };

        if (wCatEntity.hasCustomName()) {
            moodText = switch (wCatEntity.getMood()) {
                case HAPPY -> Component.translatable("screen.catdata.mood_happy", name);
                case CALM -> Component.translatable("screen.catdata.mood_calm", name);
                case SAD -> Component.translatable("screen.catdata.mood_sad", name);
                case STRESSED -> Component.translatable("screen.catdata.mood_stressed", name);
            };
        } else {
            moodText = switch (wCatEntity.getMood()) {
                case HAPPY -> Component.translatable("screen.catdata.mood_happy", Component.translatable("generic.this_cat"));
                case CALM -> Component.translatable("screen.catdata.mood_calm", Component.translatable("generic.this_cat"));
                case SAD -> Component.translatable("screen.catdata.mood_sad", Component.translatable("generic.this_cat"));
                case STRESSED -> Component.translatable("screen.catdata.mood_stressed", Component.translatable("generic.this_cat"));
            };
        }


        activeMenu = "main";
        drawMainMenu();


        super.init();
    }

    @Override
    public void tick() {

        ticks++;
        if (ticks % 20 == 0) {
            if (wCatEntity.getAge() < 0) {
                moons = wCatEntity.getEntityData().get(AGE_SYNC);
                ageText = Component.translatable("screen.catdata.age_moons" ,String.format("%.2f", moons));
            } else {
                ageText = Component.translatable("screen.catdata.fully_grown");
            }

            kittingTime = ((wCatEntity.getKittingTime()) - wCatEntity.getKittingTicks()) / (20f * 60f);

            if (wCatEntity.getKittingTicks() > 20) {
                KitTime = Component.translatable("screen.catdata.kitting_time",String.format("%.2f", kittingTime));
            } else {
                KitTime = Component.translatable("screen.catdata.not_expecting_kits");
            }

            expectingKits = wCatEntity.isExpectingKits();
            expectingText = expectingKits ? Component.translatable("screen.catdata.expecting_kits") : Component.translatable("screen.catdata.not_expecting_kits");

            if (Minecraft.getInstance().player != null) {
                friendshipLevel = wCatEntity.getEntityData().get(WCatEntity.FRIENDSHIP_SYNC);
                friendshipPointsPixels = friendshipLevel/20;


            }

        }

        super.tick();
    }

    private void drawMainMenu() {
        this.clearWidgets();

        ModPackets.sendToServer(new SyncCatDataPacket(wCatEntity.getId()));

        if (wCatEntity.isTame() && wCatEntity.getInteractionCooldown() <=0) {
            this.addRenderableWidget(Button.builder(
                    Component.translatable("screen.catdata.interact"),
                    btn -> {
                        activeMenu = "interact";
                        drawInteractMenu();
                    }
            ).bounds(this.width - 85, 10, 80, 20).build());
        } else {
            interactionCooldownTooltip = true;
        }

        if (wCatEntity.isTame() && wCatEntity.getOwner() == Minecraft.getInstance().player) {
            this.addRenderableWidget(Button.builder(
                    Component.translatable("screen.catdata.home"),
                    btn -> {
                        activeMenu = "mode";
                        drawModeMenu();
                    }
            ).bounds(this.width - 85, 35, 80, 20).build());

            this.addRenderableWidget(Button.builder(
                    Component.translatable("screen.catdata.home"),
                    btn -> {
                        activeMenu = "home";
                        drawHomeMenu();
                    }
            ).bounds(this.width - 85, 60, 80, 20).build());
        } else {
            if (!wCatEntity.getClanUUID().equals(ClanData.EMPTY_UUID)){
                if (wCatEntity.getClanUUID().equals(ClientClanData.get().getCurrentClanUUID())) {
                    this.addRenderableWidget(Button.builder(
                            Component.translatable("screen.catdata.take"),
                            btn -> {
                                ModPackets.sendToServer(new CtSTakeCatPacket(wCatEntity.getId()));
                                onClose();
                            }
                    ).bounds(this.width - 85, 35, 80, 20).build());
                }
            }
        }

        this.addRenderableWidget(Button.builder(
                Component.translatable("screen.catdata.close"),
                btn -> {
                    this.onClose();
                }
        ).bounds(this.width - 85, this.height - 30, 80, 20).build());

        if ((wCatEntity.isTame() && wCatEntity.getOwner() == Minecraft.getInstance().player && wCatEntity.getRank() == WCatEntity.Rank.DEPUTY) || isPlayerValidDeputy){
            this.addRenderableWidget(Button.builder(
                    Component.translatable("screen.catdata.patrol"),
                    btn -> {
                        ModPackets.sendToServer(new CtSRequestPatrolData(wCatEntity.getId(), isPlayerValidDeputy));
                        Minecraft.getInstance().setScreen(null);
                    }
            ).bounds(this.width - 85, 85, 80, 20).build());
        }

    }

    private void drawInteractMenu() {
        this.clearWidgets();
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        this.addRenderableWidget(Button.builder(
                Component.translatable("screen.catdata.back"),
                btn -> {
                    activeMenu = "main";
                    drawMainMenu();
                }
        ).bounds(this.width - 85, 10, 80, 20).build());



        this.addRenderableWidget(new ModButton(
                centerX - 40,
                centerY - 20,
                80, 15,
                Component.translatable("screen.catdata.give_prey"),
                b ->  {
                    ModPackets.sendToServer(new PerformInteractionPacket(wCatEntity.getId(), WCatEntity.CatInteraction.GIVE_ITEM));
                    this.onClose();
                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                80, 20, 0.9f
        ));
        this.addRenderableWidget(new ModButton(
                centerX - 40,
                centerY + 0,
                80, 15,
                Component.translatable("screen.catdata.show_affection"),
                b ->  {
                    ModPackets.sendToServer(new PerformInteractionPacket(wCatEntity.getId(), WCatEntity.CatInteraction.SHOW_AFFECTION));
                    this.onClose();
                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                80, 20, 0.9f
        ));
        if (wCatEntity.getRank() != WCatEntity.Rank.KIT && wCatEntity.getRank() != WCatEntity.Rank.APPRENTICE) {
            this.addRenderableWidget(new ModButton(
                    centerX - 40,
                    centerY + 20,
                    80, 15,
                    Component.translatable("screen.catdata.talk"),
                    b ->  {
                        ModPackets.sendToServer(new PerformInteractionPacket(wCatEntity.getId(), WCatEntity.CatInteraction.TALK));
                        this.onClose();
                    },
                    ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20,0.9f
            ));
        }

        if (Minecraft.getInstance().player != null) {
            if (ClientClanData.get().getMateUUID() != null) {
                if (ClientClanData.get().getMateUUID().equals(wCatEntity.getUUID())) {
                    this.addRenderableWidget(new ModButton(
                            centerX - 40,
                            centerY + 40,
                            80, 15,
                            Component.translatable("screen.catdata.have_kits"),
                            b ->  {
                                if (wCatEntity.getKittingInteractCooldown() > 0) {
                                    Minecraft.getInstance().player.displayClientMessage(
                                            Component.translatable("generic.cat_had_kits_recently",
                                                    wCatEntity.hasCustomName() ? wCatEntity.getCustomName().copy()
                                                            : Component.translatable("generic.this_cat").withStyle(ChatFormatting.YELLOW))
                                            , true
                                    );
                                    this.onClose();
                                } else {
                                    ModPackets.sendToServer(new KittingInteractionPacket(wCatEntity.getId()));
                                    this.onClose();
                                }
                            },
                            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                            80, 20,0.9f
                    ));
                }
            }
        }


    }

    private void drawModeMenu() {
        this.clearWidgets();
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        this.addRenderableWidget(Button.builder(
                Component.translatable("screen.catdata.back"),
                btn -> {
                    activeMenu = "main";
                    drawMainMenu();
                }
        ).bounds(this.width - 85, 35, 80, 20).build());



        this.addRenderableWidget(new ModButton(
                centerX - 40,
                centerY - 20,
                80, 15,
                Component.translatable("screen.catdata.follow"),
                b ->  {
                    ModPackets.sendToServer(new CatSetModePacket(wCatEntity.getId(), WCatEntity.CatMode.FOLLOW));
                    Minecraft.getInstance().setScreen(null);
                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                80, 20,0.9f
        ));
        this.addRenderableWidget(new ModButton(
                centerX - 40,
                centerY,
                80, 15,
                Component.translatable("screen.catdata.stay"),
                b ->  {
                    ModPackets.sendToServer(new CatSetModePacket(wCatEntity.getId(), WCatEntity.CatMode.SIT));
                    Minecraft.getInstance().setScreen(null);
                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                80, 20, 0.9f
        ));
        this.addRenderableWidget(new ModButton(
                centerX - 55,
                centerY + 20,
                110, 15,
                Component.translatable("screen.catdata.wander"),
                b ->  {
                    ModPackets.sendToServer(new CatSetModePacket(wCatEntity.getId(), WCatEntity.CatMode.WANDER));
                    Minecraft.getInstance().setScreen(null);
                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                80, 20, 0.9f
        ));

    }

    private void drawHomeMenu() {
        this.clearWidgets();
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        this.addRenderableWidget(Button.builder(
                Component.translatable("screen.catdata.back"),
                btn -> {
                    activeMenu = "main";
                    drawMainMenu();
                }
        ).bounds(this.width - 85, 60, 80, 20).build());



        this.addRenderableWidget(new ModButton(
                centerX - 40,
                centerY - 20,
                80, 15,
                Component.translatable("screen.catdata.sethome"),
                b ->  {
                    ModPackets.sendToServer(new CatHomeActionsPacket(wCatEntity.getId(), 1));
                    Minecraft.getInstance().setScreen(null);
                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                80, 20,0.9f
        ));
        this.addRenderableWidget(new ModButton(
                centerX - 40,
                centerY,
                80, 15,
                Component.translatable("screen.catdata.return_home"),
                b ->  {
                    ModPackets.sendToServer(new CatHomeActionsPacket(wCatEntity.getId(), 0));
                    Minecraft.getInstance().setScreen(null);
                },
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                80, 20, 0.9f
        ));

    }


    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        ModPackets.sendToServer(new RetrieveLastCatModePacket(wCatEntity.getId()));
        super.onClose();
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == GLFW.GLFW_KEY_E) {
            this.onClose();
            return true;
        }
        if (pKeyCode == GLFW.GLFW_KEY_T) {
            this.onClose();
            return true;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }
}
