package net.snowteb.warriorcats_events.event;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.snowteb.warriorcats_events.WCEClient;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.client.EntityChatBubbleManager;
import net.snowteb.warriorcats_events.client.LeapClientState;
import net.snowteb.warriorcats_events.compat.CompatibilitiesClient;
import net.snowteb.warriorcats_events.diseases.Disease;
import net.snowteb.warriorcats_events.diseases.Diseaseable;
import net.snowteb.warriorcats_events.entity.custom.EagleEntity;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.managers.ClimbDataAccessor;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.c2s.clan.EmoteMorphPacket;
import net.snowteb.warriorcats_events.network.packet.c2s.others.*;
import net.snowteb.warriorcats_events.network.packet.c2s.skilltree.ReqSkillDataPacket;
import net.snowteb.warriorcats_events.screen.menus.EmoteMenu;
import net.snowteb.warriorcats_events.screen.screens.SkillScreen;
import net.snowteb.warriorcats_events.screen.menus.PlaySoundMenu;
import net.snowteb.warriorcats_events.screen.screens.WCEOptionsScreen;
import net.snowteb.warriorcats_events.skills.ISkillData;
import net.snowteb.warriorcats_events.skills.PlayerSkillProvider;
import net.snowteb.warriorcats_events.skills.StealthClientState;
import net.snowteb.warriorcats_events.sound.ModSounds;
import net.snowteb.warriorcats_events.stealth.PlayerStealthProvider;
import net.snowteb.warriorcats_events.util.ModKeybinds;
import net.snowteb.warriorcats_events.zconfig.WCEServerConfig;
import org.lwjgl.glfw.GLFW;
import tocraft.walkers.api.PlayerShape;

import java.util.List;

import static net.minecraft.client.renderer.LevelRenderer.getLightColor;
import static net.snowteb.warriorcats_events.WCEClient.*;

@Mod.EventBusSubscriber(modid = WarriorCatsEvents.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEventsForge {


    private static Button extraButton;
    private static int waterCooldown = 0;
    private static boolean waterPressed;

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {

        if ((event.getKey() == GLFW.GLFW_KEY_LEFT_SHIFT || event.getKey() == GLFW.GLFW_KEY_RIGHT_SHIFT) && event.getAction() == GLFW.GLFW_PRESS) {
            if (isBeingLatched) {
                setFreeCounter += 16;
            }
        }

        if (ModKeybinds.OPTIONS_KEY.isDown() && event.getAction() == GLFW.GLFW_PRESS) {
            Minecraft.getInstance().setScreen(new WCEOptionsScreen());
        }


    }


    @SubscribeEvent
    public static void onScroll(InputEvent.MouseScrollingEvent event) {
        Minecraft mc = Minecraft.getInstance();

        if (isRenderingEmoteMenu && mc.screen == null) {

            emoteOffset -= (int) event.getScrollDelta();

            if (WarriorCatsEvents.Collaborators.isContributor(Minecraft.getInstance().player.getUUID())) {
                emoteOffset = Mth.clamp(emoteOffset, -3, EmoteIndexData.MAX_EMOTES);
            } else {
                emoteOffset = Mth.clamp(emoteOffset, -1, EmoteIndexData.MAX_EMOTES);
            }

            WCEClient.playLocalSound(ModSounds.MENU_CLICK.get(), SoundSource.NEUTRAL, 0.1f, 1.3f);

            event.setCanceled(true);
        } else if (isRenderingSoundMenu && mc.screen == null) {
            soundOffset -= (int) event.getScrollDelta();

            soundOffset = Mth.clamp(soundOffset, 0, MAX_SOUNDS);


            WCEClient.playLocalSound(ModSounds.MENU_CLICK.get(), SoundSource.NEUTRAL, 0.1f, 1.3f);

            event.setCanceled(true);
        }
    }



    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (mc.player == null) return;
        if (mc.level == null) return;
        /**
         * If the skill is unlocked and on, and the player is shifting, send the info to the StealthClientState.
         */

        if (newParticleTime > 0) newParticleTime--;

        EntityChatBubbleManager.tick();

        player.getCapability(PlayerStealthProvider.STEALTH_MODE).ifPresent(cap -> {
            if (!cap.isUnlocked() || !cap.isOn()) return;

            boolean shifting = mc.options.keyShift.isDown();
            if (player instanceof ClimbDataAccessor data && data.wce$isClimbing()) shifting = false;
            StealthClientState.tick(shifting);
        });

        {
            if (player.getVehicle() != null) {
                if (player.getVehicle() instanceof EagleEntity eagle && !eagle.isOwnedBy(player)) {
                    if (!isBeingLatched) {
                        isBeingLatched = true;
                        setFreeCounter = 60;
                    }
                } else {
                    isBeingLatched = false;
                }
            } else {
                if (isBeingLatched) isBeingLatched = false;
            }

            if (!isBeingLatched) {
                setFreeCounter = 0;
            } else {

                if (setFreeCounter >= 199) {
                    isBeingLatched = false;
                    setFreeCounter = 0;
                    ModPackets.sendToServer(new CtSDismountEaglePacket());

                }

                if (setFreeCounter > 0) {
                    setFreeCounter--;
                }
            }

        }


        boolean shifting = mc.options.keyShift.isDown();
        LeapClientState.tick(shifting);


        /**
         * Keybinds with cooldowns.
         * This is so holding down the key doesn't send 20 packets per second and breaks the whole game.
         */
        if (waterCooldown > 0) waterCooldown--;

        if (WCEServerConfig.SERVER.THIRST.get() && (ModKeybinds.WATERDRINK_KEY.isDown() && waterCooldown == 0)) {
            if (!waterPressed) {
                ModPackets.sendToServer(new WaterPacket());
                waterPressed = true;
                waterCooldown = 7;
            }
        } else {
            waterPressed = false;
        }


        if (EMOTES_HUD_MENU_KEY.consumeClick()) {

            if (isRenderingEmoteMenu) {

                int selected = emoteOffset;

                if (selected == -1) {
                    ModPackets.sendToServer(new CtSSwitchShape());
                } else {
                    ModPackets.sendToServer(new EmoteMorphPacket(selected));
                }

            } else {
                WCEClient.playLocalSound(ModSounds.MENU_OPEN.get(), SoundSource.NEUTRAL, 0.4f, 1f);
            }

            if (isRenderingSoundMenu) isRenderingSoundMenu = false;


            emoteOffset = 0;
            isRenderingEmoteMenu = !isRenderingEmoteMenu;
        }

        if (ModKeybinds.HISSING_KEY.consumeClick()) {
            if (isRenderingSoundMenu) {
                int selected = soundOffset;
                if (selected != 0) {
                    ModPackets.sendToServer(new CtSPlayCatSoundPacket(selected));
                }
            } else {
                WCEClient.playLocalSound(ModSounds.MENU_OPEN.get(), SoundSource.NEUTRAL, 0.4f, 1f);
            }

            if (isRenderingEmoteMenu) isRenderingEmoteMenu = false;

            soundOffset = 0;
            isRenderingSoundMenu = !isRenderingSoundMenu;
        }

        if (ModKeybinds.CLIMB_KEY.consumeClick()) {
            if (PlayerShape.getCurrentShape(player) instanceof WCatEntity) {
                boolean climbUnlocked = player.getCapability(PlayerSkillProvider.SKILL_DATA)
                        .map(ISkillData::isClimbUnlocked).orElse(false);
                if (climbUnlocked) {
                    if (WCEServerConfig.SERVER.SKILL_TREE_SERVER.get()) {
                        if (climbCooldown <= 0) {
                            if (player instanceof ClimbDataAccessor dataAccessor) {
                                if (dataAccessor.wce$isClimbing()) return;
                            }
                            if (!hasClimbableSurface(player)) return;

                            ModPackets.sendToServer(new CtSClimbPacket());
                            climbCooldown = CLIM_COOLDOWN;
                        } else {
                            displayCannotClimb = 20;
                            cannotClimbBlink = true;
                            cannotClimbBlinkCount = 6;
                        }
                    } else {
                        player.sendSystemMessage(Component.literal("Skill tree is disabled for this world.")
                                .withStyle(ChatFormatting.RED));
                    }
                } else {
                    player.displayClientMessage(Component.literal("Climbing skill has not been unlocked yet!")
                            .withStyle(ChatFormatting.YELLOW), true);
                }
            }
        }

        WCEClient.climbClientTick();

        if (isRenderingEmoteMenu && (mc.screen != null || mc.player == null)) {
            isRenderingEmoteMenu = false;
            emoteOffset = 0;
        }

        if (isRenderingSoundMenu && (mc.screen != null || mc.player == null)) {
            isRenderingSoundMenu = false;
            soundOffset = 0;
        }

    }

    private static boolean hasClimbableSurface(Player player) {

        Level level = player.level();
        BlockPos pos = player.blockPosition();

        int playerY = pos.getY();

        Direction[] dirs = {
                Direction.NORTH,
                Direction.SOUTH,
                Direction.EAST,
                Direction.WEST
        };

        for (Direction dir : dirs) {

            BlockPos base = pos.relative(dir);

            for (int dy = 0; dy <= 0; dy++) {

                BlockPos check = base.above(dy);

                BlockState state = level.getBlockState(check);

                if (!state.isCollisionShapeFullBlock(level, check)) continue;

                int y = check.getY();

                if (y != playerY) continue;

                return true;
            }
        }

        return false;
    }

    @SubscribeEvent
    public static void onClientLogin(ClientPlayerNetworkEvent.LoggingIn event) {
        ModPackets.sendToServer(new CompareVersionsPacket(WarriorCatsEvents.MOD_VERSION));

        if (event.getPlayer().level().isClientSide()) {

            if (UpdateCheck.updateAvailable) {

                event.getPlayer().sendSystemMessage(
                        Component.literal("[Warrior Cats Events] New ")
                                .append(
                                        Component.literal("version")
                                                .withStyle(style -> style.withColor(ChatFormatting.GREEN)
                                                        .withUnderlined(true).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
                                                                "https://modrinth.com/mod/warrior-cats-events"
                                                        ))
                                                )
                                )
                                .append(" available: "
                                        + UpdateCheck.latestVersion).withStyle(style -> style.withColor(0xfffb00)
                                ));

                if (!UpdateCheck.update_message.isEmpty()){
                    event.getPlayer().sendSystemMessage(
                            Component.empty()
                                    .append(Component.literal("[Warrior Cats Events] ")
                                            .withStyle(style -> style.withColor(0xfffb00)))
                                    .append(Component.literal(" Update message: ").withStyle(ChatFormatting.BOLD))
                    );
                    event.getPlayer().sendSystemMessage(Component.literal(UpdateCheck.update_message).withStyle(ChatFormatting.GRAY));
                    event.getPlayer().sendSystemMessage(Component.literal(""));
                }

            }
        }
    }

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof InventoryScreen screen)) return;
        if (!WCEServerConfig.SERVER.SKILL_TREE_SERVER.get()) return;

        int x = screen.getGuiLeft() + screen.getXSize() - 152;
        int y = screen.getGuiTop() - 18;

        extraButton = Button.builder(
                Component.translatable("wce.button.skill_tree"),
                btn -> {
                    Minecraft.getInstance().setScreen(new SkillScreen());
                    ModPackets.sendToServer(new ReqSkillDataPacket());
                }
        ).bounds(x, y, 52, 16).build();

        event.addListener(extraButton);
    }

    @SubscribeEvent
    public static void onScreenRender(ScreenEvent.Render.Post event) {
        if (!(event.getScreen() instanceof InventoryScreen)) return;
        if (extraButton == null) return;

        AbstractContainerScreen<?> container =
                (AbstractContainerScreen<?>) event.getScreen();

        int x = container.getGuiLeft() + container.getXSize() - 152;
        int y = container.getGuiTop() - 18;

        extraButton.setPosition(x, y);
    }

    @SubscribeEvent
    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        Player player = event.getEntity();

        if (player.isSleeping()) {
             {
                if (PlayerShape.getCurrentShape(player) instanceof WCatEntity) {
                    PoseStack poseStack = event.getPoseStack();
                    poseStack.pushPose();
                    poseStack.scale(0.95F, 0.95F, 0.95F);
                    poseStack.translate(0.0D, 0.0D, -0.0D);

                    if (player != Minecraft.getInstance().player) {
                        poseStack.translate(0.0D, -0.6D, 0.0D);
                        player.setOnGround(true);
                    }
                    poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
                    poseStack.mulPose(Axis.YP.rotationDegrees(180f));

                    player.swinging = false;
                }

            }
        }
    }

    @SubscribeEvent
    public static void onRenderPlayerPost(RenderPlayerEvent.Post event) {
        Player player = event.getEntity();

        if (player.isSleeping()) {
            {

                if (PlayerShape.getCurrentShape(player) instanceof WCatEntity)  event.getPoseStack().popPose();

            }
        }
    }

    private static ResourceLocation currentMouseTexture =
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/hud/mouse_unclicked.png");
    private static ResourceLocation currentRightMouseTexture =
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/hud/mouse_right_unclicked.png");

    private static ResourceLocation climbingIcon =
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/hud/climbing_icon.png");

    private static ResourceLocation exhaustionBarSprite =
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/hud/exhaustion_level_bar.png");


    private static ResourceLocation climbBarSprite =
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/hud/climb_cooldown_bar.png");

    private static int lastToggleTick = 0;


    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        LocalPlayer player = Minecraft.getInstance().player;
        Minecraft mcinstance = Minecraft.getInstance();
        if (player == null) return;
        if (!mcinstance.isWindowActive()) return;
        if (mcinstance.level == null) return;

        if (isRenderingEmoteMenu) {
            new EmoteMenu().render(event.getGuiGraphics());
        } else if (isRenderingSoundMenu) {
            new PlaySoundMenu().render(event.getGuiGraphics());
        }

        CompatibilitiesClient.sereneSeasonsOverlay(event.getGuiGraphics(), player.level());

        diseaseOverlay(event, player, mcinstance);

        climbOverlay(event, mcinstance, player);

        eagleUnlatchOverlay(event, mcinstance);

        footprintOverlay(event, mcinstance);

        sprintLeapOverlay(event, mcinstance);

        leapOverlay(event, mcinstance, player);

    }

    private static void diseaseOverlay(RenderGuiOverlayEvent.Post event, LocalPlayer player, Minecraft mcinstance) {
        if (!WCEServerConfig.SERVER.DISEASES.get()) return;

        if (player instanceof Diseaseable<?> diseaseable) {
            List<Disease<?>> list = diseaseable.getList();
            if (!list.isEmpty() && mcinstance.screen == null) {
                GuiGraphics pGuiGraphics = event.getGuiGraphics();
                int width = mcinstance.getWindow().getGuiScaledWidth();
                int height = mcinstance.getWindow().getGuiScaledHeight();

                pGuiGraphics.pose().pushPose();
                pGuiGraphics.pose().translate(0, height - 5, 0);
                float scale = 0.8f;
                pGuiGraphics.pose().scale(scale, scale, scale);

                int y1 = 0;
                for (Disease<?> disease : list) {
                    {
                        Component finalText = Component.literal("   ").withStyle(Style.EMPTY.withColor(WCEClient.diseaseTextColor(disease)));

                        if (!disease.canHeal()) {
                            finalText = finalText.copy()
                                    .append(Component.literal("| ").withStyle(ChatFormatting.DARK_GRAY))
                                    .append(Component.literal("⏱").withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.BOLD));
                        } else {
                            finalText = finalText.copy()
                                    .append(Component.literal("| ").withStyle(ChatFormatting.DARK_GRAY));

                            if (disease.shouldShowUnhealed()) {
                                for (int i = 0; i < disease.getCures().size(); i++) {
                                    finalText = finalText.copy().append("   ");
                                }
                            } else {
                                finalText = finalText.copy().append("   ");
                            }
                        }

                        pGuiGraphics.renderTooltip(Minecraft.getInstance().font,
                                finalText,
                                0, y1);

                        int iconSize = 10;
                        pGuiGraphics.pose().pushPose();
                        pGuiGraphics.pose().translate(0, y1, 401);
                        pGuiGraphics.blit(disease.getType().getIcon(),
                                iconSize + 1, -iconSize - 3,
                                0,0,
                                iconSize, iconSize,
                                iconSize, iconSize);

                        if (disease.shouldShowUnhealed()) {
                            int x = 0;

                            float scale2 = 0.70f;
                            int offset = Minecraft.getInstance().font.width(finalText);
                            pGuiGraphics.pose().translate(offset, -iconSize - 4, 0);
                            pGuiGraphics.pose().scale(scale2, scale2, scale2);
                            for (ItemStack stack : disease.getCures()) {

                                pGuiGraphics.renderItem(stack, x, 1);

                                x -= iconSize + 8;
                            }

                        }

                        pGuiGraphics.pose().popPose();
                    }
                    y1 -= 18;
                }
                pGuiGraphics.pose().popPose();
            }
        }
    }

    private static void climbOverlay(RenderGuiOverlayEvent.Post event, Minecraft mcinstance, LocalPlayer player) {
        if (exhaustionLevel > 0 || climbCooldown > 0) {
            GuiGraphics pGuiGraphics = event.getGuiGraphics();

            int width = mcinstance.getWindow().getGuiScaledWidth();

            int centerX = width / 2;

            int textureWidth = 182;
            int textureHeight = 15;
            int barHeight = 5;

            int xPosition = centerX - (textureWidth / 2);
            int yPosition = 3;

            if (exhaustionLevel > 0) {
                pGuiGraphics.blit(exhaustionBarSprite, xPosition, yPosition,
                        0, 0,
                        textureWidth, barHeight,
                        textureWidth, textureHeight);

                int currentFillBarOffset = barHeight;

                float coefficient = (float) exhaustionLevel / MAX_EXHAUSTION_LEVEL;

                if (exhaustionLevel > 70) currentFillBarOffset = barHeight * 2;

                pGuiGraphics.enableScissor(xPosition, yPosition, (int) ((xPosition + (textureWidth * coefficient))), yPosition + barHeight);
                pGuiGraphics.blit(exhaustionBarSprite, xPosition, yPosition,
                        0, currentFillBarOffset,
                        textureWidth, barHeight,
                        textureWidth, textureHeight);
                pGuiGraphics.disableScissor();

                pGuiGraphics.pose().pushPose();
                pGuiGraphics.pose().translate(centerX, 9, 0);
                float scale = 0.6f;
                pGuiGraphics.pose().scale(scale, scale, scale);

                if (exhaustionLevel > 80 && player instanceof ClimbDataAccessor data && data.wce$isClimbing()) {
                    int y = 30;
                    if (climbCooldown > 0) y = 43;
                    pGuiGraphics.drawCenteredString(mcinstance.font, Component.translatable("generic.losing_grip"),
                            0, y, 0xFF0000);
                }
                pGuiGraphics.pose().popPose();
            }

            float red = 1f;
            float green = 1f;
            float blue = 1f;

            if (exhaustionLevel > 50) {
                green = 0.8f;
                blue = 0.0f;
            }

            if (exhaustionLevel > 70) {
                red = 0.9f;
                green = 0.1f;
                blue = 0.0f;
            }
            if (cannotClimbBlink) {
                red = 1f;
                green = 0.0f;
                blue = 0.0f;
            }

            pGuiGraphics.setColor(red, green, blue, 1f);
            pGuiGraphics.blit(climbingIcon, centerX - 10,8,
                    0,0,
                    20,20,
                    20,20);
            pGuiGraphics.setColor(1f,1f,1f,1f);

            if (climbCooldown > 0){
                float progress = (float) (CLIM_COOLDOWN - climbCooldown) / CLIM_COOLDOWN;
                int length = (int) (11 * progress);

                RenderSystem.enableBlend();
                pGuiGraphics.enableScissor(centerX - 6, 8, centerX + (5 - length), 28);
                pGuiGraphics.setColor(0f, 0f, 0f, 0.5f);
                pGuiGraphics.blit(climbingIcon, centerX - 10, 8,
                        0, 0,
                        20, 20,
                        20, 20);
                pGuiGraphics.setColor(1f, 1f, 1f, 1f);
                pGuiGraphics.disableScissor();
                RenderSystem.disableBlend();

                pGuiGraphics.blit(climbBarSprite, centerX - 21, 28,
                        0, 0,
                        42, 5,
                        42, 15);

                int length2 = (int) (42*progress);

                int offset = 5;
                if (cannotClimbBlink) offset = 10;

                pGuiGraphics.enableScissor(centerX - 21, 28, centerX + (21 - length2), 33);
                pGuiGraphics.blit(climbBarSprite, centerX - 21, 28,
                        0, offset,
                        42, 5,
                        42, 15);
                pGuiGraphics.disableScissor();
            }

        }
    }

    private static void leapOverlay(RenderGuiOverlayEvent.Post event, Minecraft mcinstance, LocalPlayer player) {
        if (LeapClientState.getLeapPowerCounter() <= 0) return;
        if (!LeapClientState.isLeapActive()) return;
        if (LeapClientState.getSprintingCounter() > 200) return;

        GuiGraphics guiGraphics = event.getGuiGraphics();

        int width = mcinstance.getWindow().getGuiScaledWidth();
        int height = mcinstance.getWindow().getGuiScaledHeight();

        ResourceLocation emptyBar = ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID,
                "textures/hud/leapbar_empty.png");
        ResourceLocation fillBar = ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID,
                "textures/hud/leapbar_fill.png");
        ResourceLocation mouseClick = ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID,
                "textures/hud/mouse_clicked.png");
        ResourceLocation mouseUnclick = ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID,
                "textures/hud/mouse_unclicked.png");
        ResourceLocation mouseRightClick = ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID,
                "textures/hud/mouse_right_clicked.png");
        ResourceLocation mouseRightUnclick = ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID,
                "textures/hud/mouse_right_unclicked.png");

        ResourceLocation unlockedCross = ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID,
                "textures/hud/targetnotlocked_cross.png");
        ResourceLocation lockedCross = ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID,
                "textures/hud/targetlocked_cross.png");

        int leapPower = LeapClientState.getLeapPowerCounter();

        ResourceLocation currentCrossTexture = LeapClientState.isLockingTarget() ? lockedCross : unlockedCross;

        guiGraphics.blit(
                emptyBar,
                width - 16, height - 106,
                0, 0,
                14, 103,
                14, 103
        );

        int centerX = width / 2;
        int centerY = height / 2;

        float leapPowerPercentage = (float) leapPower / 100;

        guiGraphics.enableScissor(width - 16, (int) (height - (106 * leapPowerPercentage)), width - 2, height);
        guiGraphics.blit(
                fillBar,
                width - 16, height - 106,
                0, 0,
                14, 103,
                14, 103
        );
        guiGraphics.disableScissor();

        int tick = mcinstance.player.tickCount;
        if (tick - lastToggleTick >= 10) {
            lastToggleTick = tick;
            currentMouseTexture = currentMouseTexture.equals(mouseClick) ? mouseUnclick : mouseClick;
            currentRightMouseTexture = currentRightMouseTexture.equals(mouseRightClick) ? mouseRightUnclick : mouseRightClick;
        }

        guiGraphics.blit(
                currentMouseTexture,
                width - 27, height - 22,
                0, 0,
                9, 19,
                9, 19

        );

        guiGraphics.blit(
                currentCrossTexture,
                centerX - 21, centerY - 21,
                0, 0,
                41, 41,
                41, 41
        );

        if (!LeapClientState.isLockingTarget() && player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
            guiGraphics.drawString(
                    Minecraft.getInstance().font,
                    Component.translatable("generic.lock_target"),
                    4,
                    height - 15,
                    0xFFFFFF
            );
            guiGraphics.blit(
                    currentRightMouseTexture,
                    68, height - 24,
                    0,0,
                    9,19,
                    9,19
            );
        }
    }

    private static void sprintLeapOverlay(RenderGuiOverlayEvent.Post event, Minecraft mcinstance) {
        if (LeapClientState.getSprintingCounter() > 100) {
            GuiGraphics guiGraphics = event.getGuiGraphics();
            int width = mcinstance.getWindow().getGuiScaledWidth();
            int height = mcinstance.getWindow().getGuiScaledHeight();

            ResourceLocation emptyBar = ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID,
                    "textures/hud/sprintbar_empty.png");
            ResourceLocation fillBar = ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID,
                    "textures/hud/sprintbar_fill.png");
            ResourceLocation readyBar = ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID,
                    "textures/hud/sprintbar_ready.png");



            guiGraphics.blit(
                    emptyBar,
                    width - 16, height - 106,
                    0, 0,
                    14, 103,
                    14, 103
            );

            int centerX = width / 2;
            int centerY = height / 2;
            int sprintPower = (int) (LeapClientState.getSprintingCounter() - 100);

            float sprintPowerPercentage = (float) sprintPower / 200;

            guiGraphics.enableScissor(width - 16, (int) (height - (106 * sprintPowerPercentage)), width - 2, height);
            guiGraphics.blit(
                    fillBar,
                    width - 16, height - 106,
                    0, 0,
                    14, 103,
                    14, 103
            );
            guiGraphics.disableScissor();

            if (sprintPowerPercentage >= 0.9) {
                guiGraphics.blit(
                        readyBar,
                        width - 16, height - 106,
                        0, 0,
                        14, 103,
                        14, 103
                );
            }

        }
    }

    private static void footprintOverlay(RenderGuiOverlayEvent.Post event, Minecraft mcinstance) {
        if (!lookingAtParticle.isEmpty()) {
            GuiGraphics guiGraphics = event.getGuiGraphics();
            int width = mcinstance.getWindow().getGuiScaledWidth();
            int height = mcinstance.getWindow().getGuiScaledHeight();
            int centerX = width / 2;
            int centerY = height / 2;

            Component text = Component.translatable("generic.footprint_smells_like", lookingAtParticle).withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY);

            float scale = 1f;

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(centerX, height - 70, 0);
            guiGraphics.pose().scale(scale, scale, scale);
            guiGraphics.drawCenteredString(Minecraft.getInstance().font, text, 0, 0, 0);
            guiGraphics.pose().popPose();

            if (newParticleTime <= 0) lookingAtParticle = "";
        }
    }

    private static void eagleUnlatchOverlay(RenderGuiOverlayEvent.Post event, Minecraft mcinstance) {
        if (isBeingLatched && setFreeCounter < 199) {
            mcinstance.player.displayClientMessage(Component.literal(""), true);
            GuiGraphics guiGraphics = event.getGuiGraphics();
            int width = mcinstance.getWindow().getGuiScaledWidth();
            int height = mcinstance.getWindow().getGuiScaledHeight();
            int centerX = width / 2;
            int centerY = height / 2 + 10;

            ResourceLocation emptyBar = ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID,
                    "textures/hud/escape_bar_empty.png");
            ResourceLocation fillBar = ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID,
                    "textures/hud/escape_bar_fill.png");

            int toFill = centerX - 100 + setFreeCounter;

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            guiGraphics.blit(emptyBar,centerX-105, centerY + 20, 0,0,210, 24, 210, 24);
            RenderSystem.disableBlend();


            guiGraphics.enableScissor(centerX - 100, centerY + 23, toFill, centerY + 43);
            guiGraphics.blit(fillBar,centerX-100, centerY + 23, 0,0,200, 18,200, 18);
            guiGraphics.disableScissor();

            guiGraphics.drawCenteredString(Minecraft.getInstance().font,Component.translatable("generic.unlatch_tip"), centerX, centerY + 28, 0xFFFFFF );

        }
    }


    @SubscribeEvent
    public static void renderWind(RenderLevelStageEvent event) {

        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_WEATHER) return;

        if (LeapClientState.getSprintingCounter() < 200) return;

        Camera cam = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 pos = cam.getPosition();

        renderWindEffect(Minecraft.getInstance().gameRenderer.lightTexture(),
                event.getPartialTick(), pos.x, pos.y, pos.z);
    }

    private static final ResourceLocation WIND = ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/environment/wind.png");

    private static final float[] windSizeX = new float[1024];
    private static final float[] windSizeZ = new float[1024];

    static {
        for (int i = 0; i < 32; ++i) {
            for (int j = 0; j < 32; ++j) {
                float f = j - 16;
                float f1 = i - 16;
                float f2 = Mth.sqrt(f * f + f1 * f1);

                windSizeX[i << 5 | j] = -f1 / f2;
                windSizeZ[i << 5 | j] =  f  / f2;
            }
        }
    }

    private static double smoothSpeed = 0;

    private static void renderWindEffect(LightTexture pLightTexture, float pPartialTick, double pCamX, double pCamY, double pCamZ) {
        Minecraft minecraft = Minecraft.getInstance();
        int ticks = Minecraft.getInstance().player.tickCount;

        float f = ((LeapClientState.getSprintingCounter()-200) / 100f);

        if (LeapClientState.getSprintCounterThreshold() < 5 && minecraft.player.onGround()) {
            f = (float) LeapClientState.getSprintCounterThreshold() /8;
        }

        WCEClient.playLocalSound(ModSounds.STEALTH_WOOSH.get(), SoundSource.AMBIENT, f*0.10f, f*minecraft.player.getRandom().nextFloat());

        if (!(f <= 0.0f)) {
            pLightTexture.turnOnLightLayer();
            Level level = minecraft.level;
            int i = Mth.floor(pCamX);
            int j = Mth.floor(pCamY);
            int k = Mth.floor(pCamZ);
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder bufferbuilder = tesselator.getBuilder();
            RenderSystem.disableCull();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            int l = 2;

            RenderSystem.depthMask(Minecraft.useShaderTransparency());
            int i1 = -1;
            float f1 = (float)ticks + pPartialTick;
            RenderSystem.setShader(GameRenderer::getParticleShader);
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

            double targetSpeed = minecraft.player.getDeltaMovement().length() * 10.0;
            smoothSpeed = Mth.lerp(pPartialTick, smoothSpeed, targetSpeed);

            for(int j1 = k - l; j1 <= k + l; ++j1) {
                for(int k1 = i - l; k1 <= i + l; ++k1) {
                    int l1 = (j1 - k + 16) * 32 + k1 - i + 16;
                    double d0 = (double)windSizeX[l1] * 0.5D;
                    double d1 = (double)windSizeZ[l1] * 0.5D;
                    blockpos$mutableblockpos.set(k1, pCamY, j1);

                    int j2 = j - l;
                    int k2 = j + l;
                    int l2 = j;

                        if (j2 != k2) {
                            RandomSource randomsource = RandomSource.create((long)(k1 * k1 * 3121 + k1 * 45238971 ^ j1 * j1 * 418711 + j1 * 13761));
                            blockpos$mutableblockpos.set(k1, j2, j1);

                            {
                                if (i1 != 1) {
                                    if (i1 >= 0) {
                                        tesselator.end();
                                    }

                                    i1 = 1;
                                    RenderSystem.setShaderTexture(0, WIND);
                                    bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
                                }

                                float f5 = 0.0F;

                                float f6 = 0;
                                float f7 = (float)(randomsource.nextDouble() + (double)(f1) * (minecraft.player.getDeltaMovement().length() > 0.15 ? -0.01D : -0.0005D));
                                double d3 = (double)k1 + 0.5D - pCamX;
                                double d5 = (double)j1 + 0.5D - pCamZ;
                                float f8 = (float)Math.sqrt(d3 * d3 + d5 * d5) / (float)l;
                                float f9 = ((1.0F - f8 * f8) * 0.3F + 0.5F) * f;
                                blockpos$mutableblockpos.set(k1, l2, j1);
                                int k3 = getLightColor(level, blockpos$mutableblockpos);
                                int l3 = k3 >> 16 & '\uffff';
                                int i4 = k3 & '\uffff';
                                int j4 = (l3 * 3 + 240) / 4;
                                int k4 = (i4 * 3 + 240) / 4;
                                bufferbuilder.vertex((double)k1 - pCamX - d0 + 0.5D, (double)k2 - pCamY, (double)j1 - pCamZ - d1 + 0.5D).uv(0.0F + f6, (float)j2 * 0.25F + f5 + f7).color(1.0F, 1.0F, 1.0F, f9).uv2(k4, j4).endVertex();
                                bufferbuilder.vertex((double)k1 - pCamX + d0 + 0.5D, (double)k2 - pCamY, (double)j1 - pCamZ + d1 + 0.5D).uv(1.0F + f6, (float)j2 * 0.25F + f5 + f7).color(1.0F, 1.0F, 1.0F, f9).uv2(k4, j4).endVertex();
                                bufferbuilder.vertex((double)k1 - pCamX + d0 + 0.5D, (double)j2 - pCamY, (double)j1 - pCamZ + d1 + 0.5D).uv(1.0F + f6, (float)k2 * 0.25F + f5 + f7).color(1.0F, 1.0F, 1.0F, f9).uv2(k4, j4).endVertex();
                                bufferbuilder.vertex((double)k1 - pCamX - d0 + 0.5D, (double)j2 - pCamY, (double)j1 - pCamZ - d1 + 0.5D).uv(0.0F + f6, (float)k2 * 0.25F + f5 + f7).color(1.0F, 1.0F, 1.0F, f9).uv2(k4, j4).endVertex();
                            }
                        }

                }
            }

            if (i1 >= 0) {
                tesselator.end();
            }

            RenderSystem.enableCull();
            RenderSystem.disableBlend();
            pLightTexture.turnOffLightLayer();
        }
    }
}
