package net.snowteb.warriorcats_events;

import com.mojang.logging.LogUtils;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.common.MinecraftForge;
import net.snowteb.warriorcats_events.block.ModBlocks;
import net.snowteb.warriorcats_events.block.entity.ModBlockEntities;
import net.snowteb.warriorcats_events.diseases.DiseaseRegistry;
import net.snowteb.warriorcats_events.effect.ModEffects;
import net.snowteb.warriorcats_events.entity.ModEntities;
import net.snowteb.warriorcats_events.integration.WCatIntegration;
import net.snowteb.warriorcats_events.item.ModItems;
import net.snowteb.warriorcats_events.loot.ModLootModifiers;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.particles.WCEParticles;
import net.snowteb.warriorcats_events.recipes.WCERecipes;
import net.snowteb.warriorcats_events.screen.menus.ModMenuTypes;
import net.snowteb.warriorcats_events.sound.ModSounds;
import net.snowteb.warriorcats_events.util.ModAttributes;
import net.snowteb.warriorcats_events.worldgen.ModFeatures;
import net.snowteb.warriorcats_events.zconfig.WCEClientConfig;
import net.snowteb.warriorcats_events.zconfig.WCEPreyItemsConfig;
import net.snowteb.warriorcats_events.zconfig.WCEServerConfig;
import org.slf4j.Logger;
import software.bernie.geckolib.GeckoLib;
import tocraft.walkers.integrations.Integrations;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Mod(WarriorCatsEvents.MODID)
public class WarriorCatsEvents {
    public static final String MODID = "warriorcats_events";
    public static final String MOD_VERSION = "1.10.0";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();


    public static class Collaborators {
        private static final Set<UUID> CONTRIBUTORS = Set.of(
                UUID.fromString("76754682-2435-4a2e-b2b5-c3ee99782812"),

                UUID.fromString("380df991-f603-344c-a090-369bad2a924a"),
                UUID.fromString("bd10cd3b-b641-4db7-839b-691339fcbfaf"),
                UUID.fromString("02c910cd-3367-4ce8-80d3-04e803bf580a"),
                UUID.fromString("cead1fe2-b208-4886-a17c-f486ba511d63"),
                UUID.fromString("c10ad235-4fa9-4d3a-9343-0e59c424b1d3"),

                UUID.fromString("714870da-15d4-47f1-8a53-05015326a09d"),
                UUID.fromString("9289ae40-9cae-419b-b4c1-3109eca4b15d"),
                UUID.fromString("cf7dda00-f2fe-4cb5-99f7-251cbebc7e0c"),
                UUID.fromString("011869e2-c199-433f-96ae-b6311cd478f2"),

                UUID.fromString("bc526ba0-c886-4241-8df0-85702f2250e5")
        );

        public static boolean isContributor(UUID uuid) {
            return CONTRIBUTORS.contains(uuid);
        }

        public static boolean isOwner(UUID uuid) {
            return uuid.equals(UUID.fromString("76754682-2435-4a2e-b2b5-c3ee99782812"));
        }
    }


    public WarriorCatsEvents() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        GeckoLib.initialize();

        ModLoadingContext.get().registerConfig(
                ModConfig.Type.SERVER, WCEServerConfig.SPEC,
                MODID + "-server.toml");

        ModLoadingContext.get().registerConfig(
                ModConfig.Type.CLIENT, WCEClientConfig.SPEC,
                MODID + "-client.toml");

        ModLoadingContext.get().registerConfig(
                ModConfig.Type.COMMON, WCEPreyItemsConfig.SPEC,
                MODID + "-prey_items.toml");

        ModSounds.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModEntities.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModEffects.register(modEventBus);
        ModAttributes.ATTRIBUTES.register(modEventBus);
        ModLootModifiers.register(modEventBus);
        WCECreativeTab.CREATIVE_TABS.register(modEventBus);
        WCEParticles.register(modEventBus);
        WCERecipes.register(modEventBus);
        DiseaseRegistry.init();
        ModFeatures.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);

        MinecraftForge.EVENT_BUS.register(this);

    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModPackets.register();

            Integrations.register(WarriorCatsEvents.MODID, WCatIntegration::new);

        });


    }



    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {

            event.accept(ModItems.SORREL);
            event.accept(ModItems.BURNET);
            event.accept(ModItems.DAISY);
            event.accept(ModItems.CHAMOMILE);
            event.accept(ModItems.YARROW);
            event.accept(ModItems.CATMINT);
            event.accept(ModItems.DEATHBERRIES);

            event.accept(ModItems.MOUSE_FOOD);
            event.accept(ModItems.SQUIRREL_FOOD);
            event.accept(ModItems.PIGEON_FOOD);
            event.accept(ModItems.SHREDDED_MEAT);
            event.accept(ModItems.EAGLE_MEAT_FOOD);

            event.accept(ModItems.SUS_MOUSE_FOOD);
            event.accept(ModItems.SUS_SQUIRREL_FOOD);
            event.accept(ModItems.SUS_PIGEON_FOOD);
            event.accept(ModItems.SUS_SHREDDED_MEAT);
            event.accept(ModItems.SUS_EAGLE_MEAT_FOOD);

            event.accept(ModItems.TRAVELING_HERBS);


        }

        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.GENERATIONS_MUSIC_DISC);
            event.accept(ModBlocks.STONECLEFT);
            event.accept(ModItems.WHISKERS);
            event.accept(ModItems.CLAWS);
            event.accept(ModItems.WARRIORNAMERANDOMIZER);
            event.accept(ModItems.FRESHKILL_AND_HERBS_BUNDLE);
            event.accept(ModItems.DOCK_POULTICE);
            event.accept(ModItems.WARRIOR_NAMETAG);
            event.accept(ModItems.ANCIENT_STICK);
            event.accept(ModItems.MYSTIC_FLOWERS_BOUQUET);

        }

        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.DOCK_LEAVES);
            event.accept(ModItems.SORREL);
            event.accept(ModItems.BURNET);
            event.accept(ModItems.DAISY);
            event.accept(ModItems.CHAMOMILE);
            event.accept(ModItems.YARROW);
            event.accept(ModItems.CATMINT);
            event.accept(ModItems.ANIMAL_TOOTH);
            event.accept(ModItems.ANIMAL_TEETH);
            event.accept(ModItems.STRANGE_SHINY_STONE);

        }


        if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {

            event.accept(ModBlocks.DOCK);
            event.accept(ModBlocks.SORRELPLANT);
            event.accept(ModBlocks.BURNETPLANT);
            event.accept(ModBlocks.CHAMOMILEPLANT);
            event.accept(ModBlocks.DAISYPLANT);
            event.accept(ModBlocks.DEATHBERRIESBUSH);
            event.accept(ModBlocks.CATMINTPLANT);
            event.accept(ModBlocks.YARROWPLANT);
            event.accept(ModBlocks.GLOWSHROOM);

            event.accept(ModBlocks.LEAF_DOOR);
            event.accept(ModBlocks.LEAF_TRAPDOOR);
            event.accept(ModBlocks.MOSS_BED);
            event.accept(ModBlocks.HAY_BED);
            event.accept(ModBlocks.KELP_BED);
            event.accept(ModBlocks.STONE_BED);
            event.accept(ModBlocks.LAVENDER_BED);
            event.accept(ModBlocks.CHERRY_BLOSSOM_BED);
            event.accept(ModBlocks.DRIFTWOOD_BED);
            event.accept(ModBlocks.DAISY_BED);
            event.accept(ModBlocks.ACACIA_BED);
            event.accept(ModBlocks.TERRACOTTA_BED);
            event.accept(ModBlocks.BAMBOO_BED);
            event.accept(ModBlocks.BERRY_BED);
            event.accept(ModBlocks.CORAL_BED);
            event.accept(ModBlocks.GLOWBERRY_BED);
            event.accept(ModBlocks.MUDDY_BED);
            event.accept(ModBlocks.KITTYPET_BED);
            event.accept(ModBlocks.ACORN_BED);
            event.accept(ModBlocks.NAUTILUS_BED);
            event.accept(ModBlocks.SUNFLOWER_BED);

        }


        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(ModItems.MOUSE_SPAWN_EGG);
            event.accept(ModItems.SQUIRREL_SPAWN_EGG);
            event.accept(ModItems.WILDCAT_SPAWN_EGG);
            event.accept(ModItems.PIGEON_SPAWN_EGG);
            event.accept(ModItems.BADGER_SPAWN_EGG);
            event.accept(ModItems.KIT_ITEM);
        }

        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(ModItems.FLOWER_CROWN);
            event.accept(ModItems.FLOWER_ARMOR);
            event.accept(ModItems.LEAF_MANE);
            event.accept(ModItems.TEETH_CLAWS);
        }

    }

}
