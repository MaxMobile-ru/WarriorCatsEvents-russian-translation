package net.snowteb.warriorcats_events;


import com.mojang.logging.LogUtils;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.snowteb.warriorcats_events.attachments.ModAttachments;
import net.snowteb.warriorcats_events.block.ModBlocks;
import net.snowteb.warriorcats_events.block.entity.ModBlockEntities;
import net.snowteb.warriorcats_events.datacomponents.ModDataComponents;
import net.snowteb.warriorcats_events.diseases.DiseaseRegistry;
import net.snowteb.warriorcats_events.effect.ModEffects;
import net.snowteb.warriorcats_events.entity.ModEntities;
import net.snowteb.warriorcats_events.integration.WCatIntegration;
import net.snowteb.warriorcats_events.item.ModItems;
import net.snowteb.warriorcats_events.loot.ModLootModifiers;
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


    public WarriorCatsEvents(IEventBus modEventBus, ModContainer modContainer) {

        modContainer.registerConfig(ModConfig.Type.SERVER, WCEServerConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.CLIENT, WCEClientConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.COMMON, WCEPreyItemsConfig.SPEC, MODID + "-prey_items.toml");

        ModSounds.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModEntities.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModEffects.register(modEventBus);
        ModAttributes.ATTRIBUTES.register(modEventBus);
        ModAttachments.register(modEventBus);
        ModLootModifiers.register(modEventBus);
        WCECreativeTab.CREATIVE_TABS.register(modEventBus);
        WCEParticles.register(modEventBus);
        WCERecipes.register(modEventBus);
        ModDataComponents.register(modEventBus);
        DiseaseRegistry.init();
        ModFeatures.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);

    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            Integrations.register(WarriorCatsEvents.MODID, WCatIntegration::new);
        });

    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {

            event.accept(ModItems.SORREL.get());
            event.accept(ModItems.BURNET.get());
            event.accept(ModItems.DAISY.get());
            event.accept(ModItems.CHAMOMILE.get());
            event.accept(ModItems.YARROW.get());
            event.accept(ModItems.CATMINT.get());
            event.accept(ModItems.DEATHBERRIES.get());

            event.accept(ModItems.MOUSE_FOOD.get());
            event.accept(ModItems.SQUIRREL_FOOD.get());
            event.accept(ModItems.PIGEON_FOOD.get());
            event.accept(ModItems.SHREDDED_MEAT.get());
            event.accept(ModItems.EAGLE_MEAT_FOOD.get());

            event.accept(ModItems.SUS_MOUSE_FOOD.get());
            event.accept(ModItems.SUS_SQUIRREL_FOOD.get());
            event.accept(ModItems.SUS_PIGEON_FOOD.get());
            event.accept(ModItems.SUS_SHREDDED_MEAT.get());
            event.accept(ModItems.SUS_EAGLE_MEAT_FOOD.get());

            event.accept(ModItems.TRAVELING_HERBS.get());


        }

        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModBlocks.STONECLEFT.get());
            event.accept(ModItems.WHISKERS.get());
            event.accept(ModItems.CLAWS.get());
            event.accept(ModItems.WARRIORNAMERANDOMIZER.get());
            event.accept(ModItems.FRESHKILL_AND_HERBS_BUNDLE.get());
            event.accept(ModItems.DOCK_POULTICE.get());
            event.accept(ModItems.WARRIOR_NAMETAG.get());
            event.accept(ModItems.ANCIENT_STICK.get());
            event.accept(ModItems.MYSTIC_FLOWERS_BOUQUET.get());

        }

        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.DOCK_LEAVES.get());
            event.accept(ModItems.SORREL.get());
            event.accept(ModItems.BURNET.get());
            event.accept(ModItems.DAISY.get());
            event.accept(ModItems.CHAMOMILE.get());
            event.accept(ModItems.YARROW.get());
            event.accept(ModItems.CATMINT.get());
            event.accept(ModItems.ANIMAL_TOOTH.get());
            event.accept(ModItems.ANIMAL_TEETH.get());
            event.accept(ModItems.STRANGE_SHINY_STONE.get());

        }


        if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {

            event.accept(ModBlocks.DOCK.get());
            event.accept(ModBlocks.SORRELPLANT.get());
            event.accept(ModBlocks.BURNETPLANT.get());
            event.accept(ModBlocks.CHAMOMILEPLANT.get());
            event.accept(ModBlocks.DAISYPLANT.get());
            event.accept(ModBlocks.DEATHBERRIESBUSH.get());
            event.accept(ModBlocks.CATMINTPLANT.get());
            event.accept(ModBlocks.YARROWPLANT.get());
            event.accept(ModBlocks.GLOWSHROOM.get());

            event.accept(ModBlocks.LEAF_DOOR.get());
            event.accept(ModBlocks.LEAF_TRAPDOOR.get());
            event.accept(ModBlocks.MOSS_BED.get());
            event.accept(ModBlocks.HAY_BED.get());
            event.accept(ModBlocks.KELP_BED.get());
            event.accept(ModBlocks.STONE_BED.get());
            event.accept(ModBlocks.LAVENDER_BED.get());

        }


        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(ModItems.MOUSE_SPAWN_EGG.get());
            event.accept(ModItems.SQUIRREL_SPAWN_EGG.get());
            event.accept(ModItems.WILDCAT_SPAWN_EGG.get());
            event.accept(ModItems.PIGEON_SPAWN_EGG.get());
            event.accept(ModItems.BADGER_SPAWN_EGG.get());
            event.accept(ModItems.KIT_ITEM.get());
        }

        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(ModItems.FLOWER_CROWN.get());
            event.accept(ModItems.FLOWER_ARMOR.get());
            event.accept(ModItems.LEAF_MANE.get());
            event.accept(ModItems.TEETH_CLAWS.get());
        }

    }

}
