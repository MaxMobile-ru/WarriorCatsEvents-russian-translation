package net.snowteb.warriorcats_events.flappycat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.snowteb.warriorcats_events.WCEClient;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.entity.ModEntities;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCGenetics;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.screen.screens.createmorph.FancySimpleButton;
import net.snowteb.warriorcats_events.screen.screens.createmorph.FancyStringWidget;
import net.snowteb.warriorcats_events.screen.screens.createmorph.FancySubRenderablesSquare;
import net.snowteb.warriorcats_events.sound.ModSounds;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import tocraft.walkers.api.PlayerShape;

import java.util.*;

public class FlappyCatScreen extends Screen {
    float ySpeed = 0;
    boolean paused;
    boolean firstJoin = true;
    int tickCount = 0;
    int gracePeriod = 0;
    int dying = 0;

    float movementSpeed = 1f;

    float flappyCatY = 0;

    private final WCatEntity flappyCat;
    private final Screen parent;
    private final RandomSource random = RandomSource.create();

    private FancySubRenderablesSquare restartMenu;
    private FancySubRenderablesSquare startMenu;
    private FancySubRenderablesSquare leaderboardMenu;

    private int currentScore = 0;

    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/flappy_cat_bg.png");

    List<Obstacle> obstacles = new ArrayList<>();
    private final LoopingScreenSound musicInstance;

    public FlappyCatScreen(Screen parent) {
        super(Component.literal("Flappy Cat"));
        this.parent = parent;
        this.paused = true;

        List<SoundEvent> sounds = new ArrayList<>();
        sounds.add(SoundEvents.MUSIC_DISC_CAT.value());
        sounds.add(SoundEvents.MUSIC_DISC_CHIRP.value());
        sounds.add(SoundEvents.MUSIC_DISC_MELLOHI.value());
        sounds.add(SoundEvents.MUSIC_DISC_MALL.value());
        sounds.add(SoundEvents.MUSIC_DISC_STAL.value());

        Minecraft.getInstance().getMusicManager().stopPlaying();
        musicInstance = new LoopingScreenSound(sounds.get(this.random.nextInt(sounds.size())));
        Minecraft.getInstance().getSoundManager().play(musicInstance);

        this.flappyCat = new WCatEntity(ModEntities.WCAT.get(), Minecraft.getInstance().level);
        this.flappyCat.setOnGround(true);
        this.flappyCat.setAnImage(true);
        this.flappyCat.setAFlyingImage(true);
        this.flappyCat.setGender(1);
        this.flappyCat.setAge(0);
        this.flappyCat.setAgeInMoons(12);
        this.flappyCat.setYRot(0);
        this.flappyCat.yHeadRot = 0;
        this.flappyCat.yBodyRot = 0;
        this.flappyCat.setXRot(0);

        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            if (PlayerShape.getCurrentShape(player) instanceof WCatEntity cat){
                this.flappyCat.setOnGeneticalSkin(cat.isOnGeneticalSkin());
                this.flappyCat.setVariant(cat.getVariant());
                this.flappyCat.getGeneticsModule().setGenetics(new WCGenetics(cat.getGeneticsModule().getGenetics()));
                this.flappyCat.getGeneticsModule().setChimeraGenetics(new WCGenetics(cat.getGeneticsModule().getChimeraGenetics()));
                this.flappyCat.getGeneticsModule().setGeneticalVariants(new WCGenetics.GeneticalVariants(cat.getGeneticsModule().getGenVariants()));
                this.flappyCat.getGeneticsModule().setGeneticalVariantsChimera(new WCGenetics.GeneticalChimeraVariants(cat.getGeneticsModule().getChimeraGenVariants()));
                this.flappyCat.setPose(cat.getPose());
            } else {
                WCGenetics.RandomizedGenetics gens = WCGenetics.RandomizedGenetics.randomize(player.getRandom());
                this.flappyCat.setOnGeneticalSkin(true);
                this.flappyCat.setVariant(0);
                this.flappyCat.getGeneticsModule().setGenetics(new WCGenetics(gens.genetics));
                this.flappyCat.getGeneticsModule().setChimeraGenetics(new WCGenetics(gens.chimeraGenetics));
                this.flappyCat.getGeneticsModule().setGeneticalVariants(new WCGenetics.GeneticalVariants(gens.variants));
                this.flappyCat.getGeneticsModule().setGeneticalVariantsChimera(new WCGenetics.GeneticalChimeraVariants(gens.chimeraVariants));
            }
        }

    }

    @Override
    protected void init() {
        drawMainMenu();

        super.init();
    }

    private void start() {
        firstJoin = false;
        ySpeed = 0;
        paused = false;
        currentScore = 0;
        flappyCatY = (float) this.height /2;
        gracePeriod = 20;
        this.obstacles.clear();
    }

    private void lose() {
        WCEClient.playLocalSound(ModSounds.WILDCAT_SCREAM.get(), SoundSource.MASTER, 1f, 1f);
        dying = 40;

        this.stop();
    }

    private void stop() {
        ySpeed = 0;
        paused = true;
    }

    private void drawMainMenu() {
        this.clearWidgets();

        int centerX = width / 2;
        int centerY = height / 2;

        int menuWidth = 150;
        int menuHeight = 200;

        float textScale = 1f;

        {
            this.startMenu = new FancySubRenderablesSquare(centerX - menuWidth / 2, centerY - menuHeight / 2,
                    menuWidth, menuHeight);

            FancyStringWidget startText = new FancyStringWidget(Component.translatable("screen.options.flappycat"),
                    centerX - menuWidth / 2, centerX + menuWidth / 2, centerY - 80, 1.4f, true, 30);
            this.startMenu.addWidget(startText);

            FancySimpleButton startButton = new FancySimpleButton(80, 20, centerX - 40, centerY - 40,
                    Component.translatable("screen.flappycat.start"), b -> {
                this.clearWidgets();
                this.start();
            }, textScale, true);
            this.startMenu.addWidget(startButton);

            FancyStringWidget highestScore = new FancyStringWidget(Component.translatable("screen.flappycat.highest_score", FlappyCatClientData.getClientScore()),
                    centerX - menuWidth / 2, centerX + menuWidth / 2, centerY - 60, 1.1f, true, 30);
            this.startMenu.addWidget(highestScore);

            FancySimpleButton leaderboardButton = new FancySimpleButton(80, 20, centerX - 40, centerY - 10,
                    Component.translatable("screen.flappycat.leaderboard"), b -> {
                this.drawLeaderBoard(this::drawMainMenu);
            }, textScale, true);
            this.startMenu.addWidget(leaderboardButton);

            FancySimpleButton exitButton = new FancySimpleButton(80, 20, centerX - 40, centerY + 60,
                    Component.translatable("screen.flappycat.exit"), b -> {
                this.onClose();
            }, textScale, true);
            this.startMenu.addWidget(exitButton);

            this.flappyCatY = (float) -this.height /2;
            this.addRenderableWidget(startMenu);
        }
    }

    private void drawRestartMenu(boolean isHighest, int lastScore) {
        int centerX = width / 2;
        int centerY = height / 2;

        int menuWidth = 170;
        int menuHeight = 200;

        float textScale = 1f;


        this.clearWidgets();
        this.restartMenu = new FancySubRenderablesSquare(centerX - menuWidth / 2, centerY - menuHeight / 2,
                menuWidth, menuHeight);

        Component text = isHighest ? Component.translatable("screen.flappycat.highest_score", currentScore)
                : Component.translatable("screen.flappycat.score", currentScore);

        FancyStringWidget startText = new FancyStringWidget(text,
                centerX - menuWidth / 2, centerX + menuWidth / 2, centerY - 80, 1.4f, true, 30);
        this.restartMenu.addWidget(startText);
        if (isHighest) {
            FancyStringWidget lastScoreText = new FancyStringWidget(Component.translatable("screen.flappycat.last_score", lastScore),
                    centerX - menuWidth / 2, centerX + menuWidth / 2, centerY - 60, 1.1f, true, 30);
            this.restartMenu.addWidget(lastScoreText);
        }

        FancySimpleButton startButton = new FancySimpleButton(80, 20, centerX - 40, centerY - 40,
                Component.translatable("screen.flappycat.restart"), b -> {
            this.clearWidgets();
            this.start();
        }, textScale, true);
        this.restartMenu.addWidget(startButton);

        FancySimpleButton leaderboardButton = new FancySimpleButton(80, 20, centerX - 40, centerY - 10,
                Component.translatable("screen.flappycat.leaderboard"), b -> {
            this.drawLeaderBoard(() -> this.drawRestartMenu(isHighest, lastScore));
        }, textScale, true);
        this.restartMenu.addWidget(leaderboardButton);

        FancySimpleButton exitButton = new FancySimpleButton(80, 20, centerX - 40, centerY + 60,
                Component.translatable("screen.flappycat.exit"), b -> {
            this.onClose();
        }, textScale, true);
        this.restartMenu.addWidget(exitButton);

        this.addRenderableWidget(restartMenu);

    }

    private void drawLeaderBoard(Runnable onExit) {
        this.clearWidgets();

        List<Component> entries = new ArrayList<>();
        Map<String, Integer> scores = FlappyCatClientData.getTop10Scores();

        Queue<Map.Entry<String, Integer>> queue = new LinkedList<>(scores.entrySet());

        for (int i = 0; i < 10; i++) {
            Component text;
            if (!queue.isEmpty()) {
                Map.Entry<String, Integer> entry = queue.poll();
                text = Component.literal(i+1 + " - " + entry.getKey() + " : " + entry.getValue());
            } else {
                text = Component.literal(i+1 + " - ------" + " : " + "---");
            }

            entries.add(text);
        }

        int centerX = width / 2;
        int centerY = height / 2;

        int menuWidth = 150;
        int menuHeight = 200;

        float textScale = 1f;

        this.leaderboardMenu = new FancySubRenderablesSquare(centerX - menuWidth / 2, centerY - menuHeight / 2,
                menuWidth, menuHeight);

        FancyStringWidget startText = new FancyStringWidget(Component.translatable("screen.flappycat.leaderboard"),
                centerX - menuWidth / 2, centerX + menuWidth / 2, centerY - 90, 1.4f, true, 30);
        this.leaderboardMenu.addWidget(startText);

        int y = centerY - 65;
        for (Component component : entries) {
            FancyStringWidget entry = new FancyStringWidget(component,
                    centerX - (menuWidth / 2) + 10, centerX + (menuWidth / 2) - 10, y, 0.7f, true, 0);
            this.leaderboardMenu.addWidget(entry);
            y += entry.getWidgetHeight() + 5;
        }

        FancySimpleButton exitButton = new FancySimpleButton(80, 20, centerX - 40, centerY + 70,
                Component.translatable("screen.flappycat.back"), b -> {
            onExit.run();
        }, textScale, true);
        this.leaderboardMenu.addWidget(exitButton);

        this.addRenderableWidget(leaderboardMenu);

    }

    private float offsetBG1 = 0;
    private float offsetBG2 = 0;

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {


        int centerX = width / 2;
        int centerY = height / 2;

        if (!this.paused) {
            flappyCatY += ySpeed;
            flappyCatY = Mth.clamp(flappyCatY, -8, this.height - 20);
            this.offsetBG1 -= 0.5f;
            this.offsetBG2 -= 0.5f;
            if (((int) this.offsetBG1) <= -width) {
                this.offsetBG1 = width;
            }
            if (((int) this.offsetBG2) <= -width * 2) {
                this.offsetBG2 = 0;
            }
        } else if (flappyCatY < this.height && !firstJoin) {
            flappyCatY -= Mth.clamp(flappyCatY, 2f, 30f)*0.1f;
        }

        int flappyMinX = FlappyCatScreen.this.width / 2 - 18;
        int flappyMaxX = FlappyCatScreen.this.width / 2 + 18;
        int flappyMinY = (int) (FlappyCatScreen.this.height - flappyCatY) - 20;
        int flappyMaxY = flappyMinY + 16;
        boolean verticalBorder = flappyMinY < 1  || flappyMaxY > FlappyCatScreen.this.height + 3;
        if (verticalBorder && !paused) {
            {
                FlappyCatScreen.this.lose();
            }
        }

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(offsetBG1, 0, 0);
        pGuiGraphics.blit(BACKGROUND, 0, 0, 0, 0, width, height, width, height);
        pGuiGraphics.pose().popPose();



        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(offsetBG2 + width, 0, 0);
        pGuiGraphics.blit(BACKGROUND, 0, 0, 0, 0, width, height, width, height);
        pGuiGraphics.pose().popPose();



        {

            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate(centerX, -flappyCatY, 0);

            renderFlappyCat(pGuiGraphics, 30);

            pGuiGraphics.pose().popPose();
        }

        for (Obstacle obstacle : this.obstacles) {
            obstacle.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        }

        int color = currentScore > FlappyCatClientData.getClientScore() ? 0xFFE8D966 : 0xFFFFFFFF;

        pGuiGraphics.drawString(this.font, "Score: " + currentScore, 5,5, color);
        pGuiGraphics.drawString(this.font, "Highest: " + FlappyCatClientData.getClientScore(), 5,15, 0xFFFFFFFF);

        for (Renderable renderable : this.renderables) {
            renderable.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        }
    }

    private void renderFlappyCat(GuiGraphics pGuiGraphics, int scale) {
        float f = (float)Math.atan(-(width / (360.0F)));
        float f1 = (float)Math.atan(0 / (40.0F));

        Quaternionf quaternionf = (new Quaternionf()).rotateZ((float) Math.PI);
        Quaternionf quaternionf1 = (new Quaternionf()).rotateX(f1 * 20.0F * ((float) Math.PI / 180F));
        quaternionf.mul(quaternionf1);
        float f2 = flappyCat.yBodyRot;
        float f3 = flappyCat.getYRot();
        float f4 = flappyCat.getXRot();
        float f5 = flappyCat.yHeadRotO;
        float f6 = flappyCat.yHeadRot;

        float magicNumber = 100f;


        flappyCat.yBodyRot = 180.0F + f * magicNumber;
        flappyCat.setYRot(180.0F + f * 40.0F);
        flappyCat.setXRot(-f1 * 20f);
        flappyCat.yHeadRot = flappyCat.getYRot() - 45;
        flappyCat.yHeadRotO = flappyCat.getYRot();
        InventoryScreen.renderEntityInInventory(pGuiGraphics, 0, height, scale, new Vector3f(0,0,0), quaternionf, quaternionf1, flappyCat);
        flappyCat.yBodyRot = f2;
        flappyCat.setYRot(f3);
        flappyCat.setXRot(f4);
        flappyCat.yHeadRotO = f5;
        flappyCat.yHeadRot = f6;
    }


    int interval = 80;

    @Override
    public void tick() {
        Minecraft.getInstance().getMusicManager().stopPlaying();

        if (dying > 0) {
            dying--;
            if (dying == 0) {
                int lastScore = FlappyCatClientData.getClientScore();
                boolean highest = FlappyCatClientData.trySetClientScore(currentScore);

                drawRestartMenu(highest, lastScore);
            }
        }

        if (paused) return;
        this.tickCount++;

        if (gracePeriod > 0) gracePeriod--;


        if (gracePeriod == 0){
            ySpeed -= (ySpeed * 0.005f + 0.5f);
        }

        ySpeed = Mth.clamp(ySpeed, -10, 10);

        interval--;
        if (interval <= 0) {
            this.obstacles.add(new Obstacle(this.random));
            this.interval = Math.max(10, (int) (80 - (20 * movementSpeed)));
        }

        if (movementSpeed < 5f) movementSpeed += 0.0005f;

        Iterator<Obstacle> iterator = this.obstacles.iterator();
        while (iterator.hasNext()) {
            Obstacle obstacle = iterator.next();
            obstacle.tick();
            if (obstacle.x < -this.width) iterator.remove();
        }



        super.tick();
    }

    private boolean spacePressed = false;

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == GLFW.GLFW_KEY_SPACE) {
            if (!spacePressed) {
                if (gracePeriod == 0) ySpeed = 2.2f;
                spacePressed = true;
            }
            return true;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == GLFW.GLFW_KEY_SPACE) {
            spacePressed = false;
            return true;
        }
        return super.keyReleased(pKeyCode, pScanCode, pModifiers);
    }


    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void removed() {
        super.removed();
        if (musicInstance != null) {
            Minecraft.getInstance().getSoundManager().stop(musicInstance);
        }

        FlappyCatClientData.uploadScore();
    }

    private class Obstacle implements Renderable {
        private static final ResourceLocation DIRT_BG = ResourceLocation.withDefaultNamespace("textures/block/dirt.png");

        float x;
        float x0;

        int yCenter;
        int yOpening;
        int touching = 0;
        private boolean passed = false;
        public boolean markedLost = false;
        public Obstacle(RandomSource random) {
            this.x = FlappyCatScreen.this.width*1.5f;
            this.yOpening = 60 + 10*(random.nextInt(4));

            int minStub = 30;

            int minCenter = (yOpening / 2) + minStub;
            int maxCenter = FlappyCatScreen.this.height - minStub - (yOpening / 2);

            int bottomClamp = FlappyCatScreen.this.height - minStub - (yOpening / 2);
            int upperClamp = minStub + yOpening/2;
            {
                this.yCenter = minCenter + random.nextInt(maxCenter - minCenter + 1);
            }

            this.yCenter = Mth.clamp(this.yCenter, upperClamp, bottomClamp);

        }

        private void pass() {
            this.passed = true;
            FlappyCatScreen.this.currentScore++;
            if (FlappyCatScreen.this.currentScore % 10 == 0) {
                WCEClient.playLocalSound(SoundEvents.PLAYER_LEVELUP, SoundSource.MASTER, 0.7f, 1.2f);
            }
            WCEClient.playLocalSound(SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.MASTER, 0.5f, 1f);
        }


        @Override
        public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
            if (!FlappyCatScreen.this.paused){
                x -= (0.8f * FlappyCatScreen.this.movementSpeed);
            }

            int flappyMinX = FlappyCatScreen.this.width / 2 - 18;
            int flappyMaxX = FlappyCatScreen.this.width / 2 + 18;
            int flappyMinY = (int) (FlappyCatScreen.this.height - flappyCatY) - 20;
            int flappyMaxY = flappyMinY + 16;

            int freeMinY = yCenter - yOpening/2;
            int freeMaxY = yCenter + yOpening/2;
            int freeMinX = (int) (x - 15);
            int freeMaxX = (int) (x + 15);

            if (!markedLost) {

                if ((flappyMinX < freeMaxX && flappyMinX > freeMinX) || (flappyMaxX > freeMinX && flappyMaxX < freeMaxX)) {
                    if (flappyMinY < freeMinY || flappyMaxY > freeMaxY) {
                        touching++;
                        if (touching > 4) {
                            FlappyCatScreen.this.lose();
                            markedLost = true;
                        }
                    }
                }


            }

            if (markedLost) {
                pGuiGraphics.setColor(1f, 0.5f, 0.5f, 1f);
            }

            if (freeMaxX < flappyMinX && !this.passed) this.pass();

            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate(x, 0, 0);
            {
                int sourceSize = 32;
                blitRepeating(pGuiGraphics, ResourceLocation.withDefaultNamespace("textures/block/stone.png"),
                        -15, 0,
                        30, freeMinY,
                        0, 0,
                        sourceSize, sourceSize);

                blitRepeating(pGuiGraphics, ResourceLocation.withDefaultNamespace("textures/block/stone.png"),
                        -15, freeMaxY,
                        30, height - freeMaxY,
                        0, 0,
                        sourceSize, sourceSize);

                pGuiGraphics.fillGradient(
                        -15, 0,
                        15, freeMinY,
                        0x99000000, 0
                );

                pGuiGraphics.fillGradient(
                        -15, freeMaxY,
                        15, height,
                        0, 0x99000000
                );

                pGuiGraphics.renderOutline(-15, 0,
                        30, freeMinY,
                        0xFF888888);

                pGuiGraphics.renderOutline(-15, freeMaxY,
                        30, height - freeMaxY,
                        0xFF888888);

                int secondBarHeight = 15;

                blitRepeating(pGuiGraphics, ResourceLocation.withDefaultNamespace("textures/block/stone_bricks.png"),
                        -18, freeMinY - secondBarHeight,
                        36, secondBarHeight,
                        0, 0,
                        sourceSize, sourceSize);

                blitRepeating(pGuiGraphics, ResourceLocation.withDefaultNamespace("textures/block/stone_bricks.png"),
                        -18, freeMaxY,
                        36, secondBarHeight,
                        0, 0,
                        sourceSize, sourceSize);

                pGuiGraphics.renderOutline(-18, freeMinY - secondBarHeight,
                        36, secondBarHeight,
                        0xFFaaaaaa);

                pGuiGraphics.renderOutline(-18, freeMaxY,
                        36, secondBarHeight,
                        0xFFaaaaaa);


                pGuiGraphics.setColor(1,1,1,1);
            }
            pGuiGraphics.pose().popPose();

        }
        public void tick() {
        }

        private static void blitRepeating(GuiGraphics guiGraphics, ResourceLocation texture, int x, int y, int width, int height, int uOffset, int vOffset, int textureWidth, int textureHeight) {
            if (width <= 0 || height <= 0) return;

            for (int tileX = 0; tileX < width; tileX += textureWidth) {
                int drawWidth = Math.min(textureWidth, width - tileX);

                for (int tileY = 0; tileY < height; tileY += textureHeight) {
                    int drawHeight = Math.min(textureHeight, height - tileY);

                    guiGraphics.blit(
                            texture,
                            x + tileX, y + tileY,
                            uOffset, vOffset,
                            drawWidth, drawHeight,
                            textureWidth, textureHeight
                    );
                }
            }
        }
    }

    private static class LoopingScreenSound extends AbstractTickableSoundInstance {
        public LoopingScreenSound(SoundEvent sound) {
            super(sound, SoundSource.MUSIC, SoundInstance.createUnseededRandom());
            this.looping = true;
            this.delay = 0;
            this.volume = 0.4f;
            this.pitch = 1.0f;
            this.relative = true;
        }

        @Override
        public void tick() {
        }
    }

}
