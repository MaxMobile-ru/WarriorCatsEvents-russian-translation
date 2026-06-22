package net.snowteb.warriorcats_events.event;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.event.sound.PlaySoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.snowteb.warriorcats_events.WCEClient;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.attachments.CapabilityManager;
import net.snowteb.warriorcats_events.attachments.ModAttachments;
import net.snowteb.warriorcats_events.block.ModBlocks;
import net.snowteb.warriorcats_events.block.entity.*;
import net.snowteb.warriorcats_events.client.ClientStoredMorphs;
import net.snowteb.warriorcats_events.client.EntityChatBubbleManager;
import net.snowteb.warriorcats_events.client.LeapClientState;
import net.snowteb.warriorcats_events.client.ThirstHUD;
import net.snowteb.warriorcats_events.datacomponents.ModDataComponents;
import net.snowteb.warriorcats_events.effect.FeverEffectOverlay;
import net.snowteb.warriorcats_events.entity.ModEntities;
import net.snowteb.warriorcats_events.entity.client.*;
import net.snowteb.warriorcats_events.entity.custom.EagleEntity;
import net.snowteb.warriorcats_events.entity.custom.WCatEntity;
import net.snowteb.warriorcats_events.item.ModItems;
import net.snowteb.warriorcats_events.managers.ClimbDataAccessor;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.c2s.clan.EmoteMorphPacket;
import net.snowteb.warriorcats_events.network.packet.c2s.others.*;
import net.snowteb.warriorcats_events.particles.*;
import net.snowteb.warriorcats_events.screen.menus.ModMenuTypes;
import net.snowteb.warriorcats_events.screen.screens.*;
import net.snowteb.warriorcats_events.attachments.StealthClientState;
import net.snowteb.warriorcats_events.sound.ModSounds;
import net.snowteb.warriorcats_events.util.ModKeybinds;
import net.snowteb.warriorcats_events.zconfig.WCEServerConfig;
import org.lwjgl.glfw.GLFW;
import tocraft.walkers.api.PlayerShape;

import java.util.Arrays;

import static net.snowteb.warriorcats_events.WCEClient.*;

public class ClientEvents {
    private static boolean hissPressed;
    private static boolean waterPressed;
    private static int hissCooldown = 0;
    private static int waterCooldown = 0;
    public int shiftKeyDownCount = 0;

    @EventBusSubscriber(modid = WarriorCatsEvents.MODID, value = Dist.CLIENT)
    public static class ClientForgeEvents {

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

                emoteOffset -= (int) event.getScrollDeltaY();

                if (WarriorCatsEvents.Collaborators.isContributor(Minecraft.getInstance().player.getUUID())) {
                    emoteOffset = Mth.clamp(emoteOffset, -3, EmoteIndexData.MAX_EMOTES);
                } else {
                    emoteOffset = Mth.clamp(emoteOffset, -1, EmoteIndexData.MAX_EMOTES);
                }

                WCEClient.playLocalSound(ModSounds.MENU_CLICK.get(), SoundSource.NEUTRAL, 0.1f, 1.3f);

                event.setCanceled(true);
            } else if (isRenderingSoundMenu && mc.screen == null) {
                soundOffset -= (int) event.getScrollDeltaY();

                soundOffset = Mth.clamp(soundOffset, 0, MAX_SOUNDS);


                WCEClient.playLocalSound(ModSounds.MENU_CLICK.get(), SoundSource.NEUTRAL, 0.1f, 1.3f);

                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            Minecraft mc = Minecraft.getInstance();
            LocalPlayer player = mc.player;
            if (mc.player == null) return;
            if (mc.level == null) return;
            /**
             * If the skill is unlocked and on, and the player is shifting, send the info to the StealthClientState.
             */

            if (newParticleTime > 0) newParticleTime--;

            EntityChatBubbleManager.tick();

            CapabilityManager.attachmentProvider(player, ModAttachments.PLAYER_STEALTH, cap -> {
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
            if (hissCooldown > 0) hissCooldown--;
            if (waterCooldown > 0) waterCooldown--;

//            if (ModKeybinds.HISSING_KEY.isDown() && hissCooldown == 0) {
//                if (!hissPressed) {
//                    ModPackets.sendToServer(new CtSPlayCatSoundPacket());
//                    hissPressed = true;
//                    hissCooldown = 30;
//                }
//            } else {
//                hissPressed = false;
//            }

            if (WCEServerConfig.SERVER.THIRST.get() && (ModKeybinds.WATERDRINK_KEY.isDown() && waterCooldown == 0)) {
                if (!waterPressed) {
                    ModPackets.sendToServer(new WaterPacket());
//                    ModPackets.sendToServer(new CtSStartSleep());
                    waterPressed = true;
                    waterCooldown = 7;
                }
            } else {
                waterPressed = false;
            }
//            if (ModKeybinds.SKILLMENU_KEY.isDown()) {
//                Minecraft.getInstance().setScreen(new TerritoryMapScreen(null));
//            }


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
                    boolean climbUnlocked = player.getData(ModAttachments.PLAYER_SKILL).isClimbUnlocked();
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

        /**
         * Part of the Update checker
         */
        @SubscribeEvent
        public static void onClientLogin(ClientPlayerNetworkEvent.LoggingIn event) {
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


    }

    @EventBusSubscriber(modid = WarriorCatsEvents.MODID, value = Dist.CLIENT)
    public static class ClientModBusEvents {


        /*
        @SubscribeEvent
        public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(ModModelLayers.VANILLAWCAT_LAYER, VanillaWCatModel::createBodyLayer);
        }
        */
        @SubscribeEvent
        public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(ModBlockEntities.FRESH_KILL_PILE.get(), FreshkillPileRenderer::new);
            event.registerBlockEntityRenderer(ModBlockEntities.MOSS_BED.get(), MossBedRenderer::new);
            event.registerBlockEntityRenderer(ModBlockEntities.STONE_TABLE.get(), StoneTableRenderer::new);
            event.registerBlockEntityRenderer(ModBlockEntities.TREE_STUMP.get(), TreeStumpEntityRenderer::new);
        }

        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event) {
            event.register(ModKeybinds.HISSING_KEY);
            event.register(ModKeybinds.WATERDRINK_KEY);
            event.register(ModKeybinds.CLIMB_KEY);
            event.register(WCEClient.EMOTES_HUD_MENU_KEY);
            event.register(ModKeybinds.OPTIONS_KEY);
            event.register(ModKeybinds.LEAP_KEY);
//            event.register(ModKeybinds.SKILLMENU_KEY);
        }

        @SubscribeEvent
        public static void registerGuiOverlays(RegisterGuiLayersEvent event) {
            event.registerBelowAll(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "thirst"), ThirstHUD.HUD_THIRST);
            event.registerBelowAll(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "stealth"), StealthClientState.HUD_STEALTH);
            event.registerBelowAll(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "fever"), FeverEffectOverlay.HUD_FEVER);
        }

        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(ModEntities.MOUSE.get(), MouseRenderer::new);
            event.registerEntityRenderer(ModEntities.SQUIRREL.get(), SquirrelRenderer::new);
            event.registerEntityRenderer(ModEntities.WCAT.get(), WCRenderer::new);
            event.registerEntityRenderer(ModEntities.PIGEON.get(), PigeonRenderer::new);
            event.registerEntityRenderer(ModEntities.BADGER.get(), BadgerRenderer::new);
            event.registerEntityRenderer(ModEntities.EAGLE.get(), EagleRenderer::new);
            event.registerEntityRenderer(ModEntities.LIZARD.get(), LizardRenderer::new);
            event.registerEntityRenderer(ModEntities.LIZARD_TAIL.get(), LizardTailRenderer::new);

            event.registerEntityRenderer(ModEntities.MOSS_BALL.get(), MossBallRenderer::new);

        }

        @SubscribeEvent
        public static void registerParticles(RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(WCEParticles.SLEEP.get(), ParticleSleep.Factory::new);
            event.registerSpriteSet(WCEParticles.LAVENDER.get(), ParticleLavender.Factory::new);
            event.registerSpriteSet(WCEParticles.HERBS.get(), ParticleHerbs.Factory::new);
            event.registerSpriteSet(WCEParticles.HERBS_FALL.get(), ParticleHerbsFall.Factory::new);
            event.registerSpriteSet(WCEParticles.FOOTPRINT.get(), ParticleFootprint.Factory::new);
            event.registerSpriteSet(WCEParticles.GREENCOUGH.get(), ParticleSickness.Provider::new);
            event.registerSpriteSet(WCEParticles.WHITECOUGH.get(), ParticleSickness.Provider::new);
        }

        /**
         * This allows the leaf door to change colors depending on the biome.
         */
        @SubscribeEvent
        public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
            event.register(
                    (state, world, pos, tintIndex) -> {
                        return world != null && pos != null
                                ? BiomeColors.getAverageFoliageColor(world, pos)
                                : FoliageColor.getDefaultColor();
                    },
                    ModBlocks.LEAF_DOOR.get(), ModBlocks.LEAF_TRAPDOOR.get(), ModBlocks.MAKESHIFT_BED.get()
            );
        }

        @SubscribeEvent
        public static void registerMenuScreens(RegisterMenuScreensEvent event) {
            event.register(ModMenuTypes.STONECLEFT_MENU.get(), StoneCleftScreen::new);
            event.register(ModMenuTypes.FRESHKILL_PILE_MENU.get(), FreshkillPileScreen::new);
            event.register(ModMenuTypes.WCAT_INVENTORY.get(), WCatScreen::new);
        }



        @SubscribeEvent
        public static void clientSetup(FMLClientSetupEvent event) {

            UpdateCheck.checkForUpdates();

            ClientStoredMorphs.load();

            {
                for (DeferredHolder<Item, Item> collar : Arrays.asList(
                        ModItems.BLACK_CAT_COLLAR,
                        ModItems.BROWN_CAT_COLLAR,
                        ModItems.WHITE_CAT_COLLAR,
                        ModItems.PINK_CAT_COLLAR,
                        ModItems.ORANGE_CAT_COLLAR,
                        ModItems.RED_CAT_COLLAR,
                        ModItems.BLUE_CAT_COLLAR,
                        ModItems.PURPLE_CAT_COLLAR)) {

                    ItemProperties.register(
                            collar.get(),
                            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "has_bell"),
                            (stack, level, entity, seed) ->
                                    stack.getOrDefault(ModDataComponents.HAS_BELL.get(), false) ? 1f : 0f
                    );

                    ItemProperties.register(
                            collar.get(),
                            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "has_spikes"),
                            (stack, level, entity, seed) ->
                                    stack.getOrDefault(ModDataComponents.HAS_SPIKES.get(), false) ? 1f : 0f
                    );

                    ItemProperties.register(
                            collar.get(),
                            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "has_glow"),
                            (stack, level, entity, seed) ->
                                    stack.getOrDefault(ModDataComponents.HAS_GLOW.get(), false) ? 1f : 0f
                    );
                }

                ItemProperties.register(
                        ModItems.MOSS_BALL.get(),
                        ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "honeylevel"),
                        (stack, level, entity, seed) ->
                                stack.getOrDefault(ModDataComponents.HONEY_LEVEL.get(), 0)
                );

                ItemProperties.register(
                        ModItems.MOSS_BALL.get(),
                        ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "waterlevel"),
                        (stack, level, entity, seed) ->
                                stack.getOrDefault(ModDataComponents.WATER_LEVEL.get(), 0)
                );
            }



        }

    }

}

