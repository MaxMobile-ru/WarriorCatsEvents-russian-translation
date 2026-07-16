package net.snowteb.warriorcats_events.event;

import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.FoliageColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.snowteb.warriorcats_events.WCEClient;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.block.ModBlocks;
import net.snowteb.warriorcats_events.block.entity.*;
import net.snowteb.warriorcats_events.client.ClientStoredMorphs;
import net.snowteb.warriorcats_events.client.ThirstHUD;
import net.snowteb.warriorcats_events.datacomponents.ModDataComponents;
import net.snowteb.warriorcats_events.effect.FeverEffectOverlay;
import net.snowteb.warriorcats_events.entity.ModEntities;
import net.snowteb.warriorcats_events.entity.client.*;
import net.snowteb.warriorcats_events.item.ModItems;
import net.snowteb.warriorcats_events.item.custom.CollarArmorItem;
import net.snowteb.warriorcats_events.particles.*;
import net.snowteb.warriorcats_events.screen.menus.ModMenuTypes;
import net.snowteb.warriorcats_events.screen.screens.*;
import net.snowteb.warriorcats_events.attachments.StealthClientState;
import net.snowteb.warriorcats_events.util.ModKeybinds;

import java.util.Arrays;
import java.util.List;

@EventBusSubscriber(modid = WarriorCatsEvents.MODID, value = Dist.CLIENT)
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
        event.register(ModMenuTypes.HERB_MIXING.get(), HerbMixingRockScreen::new);
    }


    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {

        UpdateCheck.checkForUpdates();

        ClientStoredMorphs.load();

        {
            for (DeferredHolder<Item, Item> collar : List.of(ModItems.CAT_COLLAR)) {

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

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> {
            if (tintIndex != 0) return 0xFFFFFFFF;

            DyedItemColor dyedColor = stack.get(DataComponents.DYED_COLOR);
            int rgb = dyedColor != null ? dyedColor.rgb() : DyedItemColor.LEATHER_COLOR;

            return 0xFF000000 | rgb;
        }, ModItems.CAT_COLLAR.get());
    }


    @SubscribeEvent
    public static void onResourcesReload(TextureAtlasStitchedEvent event) {
        WCModel.TEXTURES = WCModel.getTextures();
    }

}

