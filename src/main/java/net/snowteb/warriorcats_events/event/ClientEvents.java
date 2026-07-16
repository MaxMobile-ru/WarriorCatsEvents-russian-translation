package net.snowteb.warriorcats_events.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.FoliageColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;
import net.snowteb.warriorcats_events.WCEClient;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.block.ModBlocks;
import net.snowteb.warriorcats_events.block.entity.*;
import net.snowteb.warriorcats_events.client.ClientStoredMorphs;
import net.snowteb.warriorcats_events.client.ThirstHUD;
import net.snowteb.warriorcats_events.effect.FeverEffectOverlay;
import net.snowteb.warriorcats_events.entity.ModEntities;
import net.snowteb.warriorcats_events.entity.client.*;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.item.ModItems;
import net.snowteb.warriorcats_events.item.custom.CollarArmorItem;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.s2c.cats.OpenCatDataScreenPacket;
import net.snowteb.warriorcats_events.network.packet.s2c.clan.OpenClanSetupScreenPacket;
import net.snowteb.warriorcats_events.network.packet.s2c.others.StCFishingScreenPacket;
import net.snowteb.warriorcats_events.particles.*;
import net.snowteb.warriorcats_events.screen.screens.CatDataScreen;
import net.snowteb.warriorcats_events.screen.screens.ClanSetupScreen;
import net.snowteb.warriorcats_events.screen.menus.ModMenuTypes;
import net.snowteb.warriorcats_events.screen.screens.*;
import net.snowteb.warriorcats_events.skills.StealthClientState;
import net.snowteb.warriorcats_events.sound.ModSounds;
import net.snowteb.warriorcats_events.util.ModKeybinds;

import java.util.Collections;

@Mod.EventBusSubscriber(modid = WarriorCatsEvents.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEvents {

    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.FRESH_KILL_PILE.get(), FreshkillPileRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.MOSS_BED.get(), MossBedRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.TREE_STUMP.get(), TreeStumpEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.HERB_MIXING_ROCK.get(), MixingHerbsRockRenderer::new);
    }

    @SubscribeEvent
    public static void onKeyRegister(RegisterKeyMappingsEvent event) {
        event.register(ModKeybinds.HISSING_KEY);
        event.register(ModKeybinds.WATERDRINK_KEY);
        event.register(ModKeybinds.CLIMB_KEY);
        event.register(WCEClient.EMOTES_HUD_MENU_KEY);
        event.register(ModKeybinds.OPTIONS_KEY);
        event.register(ModKeybinds.LEAP_KEY);
    }

    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
        event.registerBelowAll("thirst", ThirstHUD.HUD_THIRST);
        event.registerBelowAll("stealth", StealthClientState.HUD_STEALTH);
        event.registerBelowAll("fever", FeverEffectOverlay.HUD_FEVER);
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
    public static void clientSetup(FMLClientSetupEvent event) {
        MenuScreens.register(ModMenuTypes.STONECLEFT_MENU.get(), StoneCleftScreen::new);
        MenuScreens.register(ModMenuTypes.FRESHKILL_PILE_MENU.get(), FreshkillPileScreen::new);
        MenuScreens.register(ModMenuTypes.WCAT_INVENTORY.get(), WCatScreen::new);
        MenuScreens.register(ModMenuTypes.HERB_MIXING.get(), HerbMixingRockScreen::new);

        UpdateCheck.checkForUpdates();

        ClientStoredMorphs.load();

        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                        (mc, parent) -> {
                            WCEClient.playLocalSound(ModSounds.MENU_OPEN.get(), SoundSource.NEUTRAL, 1.0f, 1.3f);
                            return new WCEConfigScreen(parent);
                        }
                )
        );

        {
            for (RegistryObject<Item> collar : Collections.singletonList(ModItems.CAT_COLLAR)) {

                ItemProperties.register(collar.get(),
                        ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "has_bell"),
                        (stack, level, entity, seed) -> stack.getTag() != null && stack.getTag().getBoolean("HasBell") ? 1f : 0f);

                ItemProperties.register(collar.get(),
                        ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "has_spikes"),
                        (stack, level, entity, seed) -> stack.getTag() != null && stack.getTag().getBoolean("HasSpikes") ? 1f : 0f);

                ItemProperties.register(collar.get(),
                        ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "has_glow"),
                        (stack, level, entity, seed) -> stack.getTag() != null && stack.getTag().getBoolean("HasGlow") ? 1f : 0f);
            }

            ItemProperties.register(ModItems.MOSS_BALL.get(),
                    ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "honeylevel"),
                    (stack, level, entity, seed) -> {
                        return stack.hasTag() ? stack.getTag().getInt("honeylevel") : 0.0F;
                    });

            ItemProperties.register(ModItems.MOSS_BALL.get(),
                    ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "waterlevel"),
                    (stack, level, entity, seed) -> {
                        return stack.hasTag() ? stack.getTag().getInt("WaterLevel") : 0.0F;
                    });
        }


        ModPackets.INSTANCE.registerMessage(
                13,
                StCFishingScreenPacket.class,
                (pkt, buf) -> pkt.toBytes(buf),
                StCFishingScreenPacket::new,
                (pkt, ctxSupplier) -> {
                    ctxSupplier.get().enqueueWork(() -> {
                        Minecraft.getInstance().setScreen(new FishingScreen(pkt.clickedPos));
                    });
                    ctxSupplier.get().setPacketHandled(true);
                }
        );

        ModPackets.INSTANCE.registerMessage(
                20,
                OpenClanSetupScreenPacket.class,
                (pkt, buf) -> pkt.toBytes(buf),
                OpenClanSetupScreenPacket::new,
                (pkt, ctxSupplier) -> {
                    ctxSupplier.get().enqueueWork(() -> {
                        Minecraft.getInstance().setScreen(new ClanSetupScreen());
                    });
                    ctxSupplier.get().setPacketHandled(true);
                }
        );

        ModPackets.INSTANCE.registerMessage(
                32,
                OpenCatDataScreenPacket.class,
                (pkt, buf) -> pkt.toBytes(buf),
                OpenCatDataScreenPacket::new,
                (pkt, ctxSupplier) -> {
                    ctxSupplier.get().enqueueWork(() -> {
                        Minecraft mc = Minecraft.getInstance();
                        if (mc.level == null) return;

                        var entity = mc.level.getEntity(pkt.catId);
                        if (entity instanceof WCatEntity cat) {
                            mc.setScreen(new CatDataScreen(cat.getDisplayName(), cat, pkt.isDeputy));
                        }
                    });

                    ctxSupplier.get().setPacketHandled(true);
                }
        );


    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> {
            if (tintIndex != 0) return 0xFFFFFF;

            if (stack.getItem() instanceof CollarArmorItem item) {
                return item.getColor(stack);
            }
            return DyeableLeatherItem.DEFAULT_LEATHER_COLOR;
        }, ModItems.CAT_COLLAR.get());
    }

    @SubscribeEvent
    public static void onResourcesReload(TextureStitchEvent event) {
        WCModel.TEXTURES = WCModel.getTextures();
    }


}

