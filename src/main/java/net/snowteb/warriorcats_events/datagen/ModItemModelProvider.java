package net.snowteb.warriorcats_events.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.block.ModBlocks;
import net.snowteb.warriorcats_events.item.ModItems;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, WarriorCatsEvents.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {

        simpleItem(ModItems.GENERATIONS_MUSIC_DISC);
        simpleItem(ModItems.WHISKERS);
        simpleItem(ModItems.CLAWS);

        simpleItem(ModItems.MOUSE_FOOD);
        simpleItem(ModItems.SQUIRREL_FOOD);
        simpleItem(ModItems.PIGEON_FOOD);
        simpleItem(ModItems.EAGLE_MEAT_FOOD);
        simpleItem(ModItems.SHREDDED_MEAT);

        simpleItem(ModItems.SUS_MOUSE_FOOD);
        simpleItem(ModItems.SUS_SQUIRREL_FOOD);
        simpleItem(ModItems.SUS_PIGEON_FOOD);
        simpleItem(ModItems.SUS_EAGLE_MEAT_FOOD);
        simpleItem(ModItems.SUS_SHREDDED_MEAT);

        simpleItem(ModItems.WARRIORNAMERANDOMIZER);
        simpleItem(ModItems.FRESHKILL_AND_HERBS_BUNDLE);
        simpleItem(ModItems.TRAVELING_HERBS);
        simpleItem(ModItems.WARRIORS_GUIDE);
        simpleItem(ModItems.FLOWER_CROWN);
        simpleItem(ModItems.FLOWER_ARMOR);
        simpleItem(ModItems.LEAF_MANE);
        simpleItem(ModItems.TEETH_CLAWS);
        simpleItem(ModItems.ANIMAL_TOOTH);
        simpleItem(ModItems.ANIMAL_TEETH);
        simpleItem(ModItems.WARRIOR_NAMETAG);
        simpleItem(ModItems.KIT_ITEM);

        simpleItem(ModItems.BLUE_CAT_SOCKS);
        simpleItem(ModItems.ORANGE_CAT_SOCKS);
        simpleItem(ModItems.PINK_CAT_SOCKS);
        simpleItem(ModItems.WHITE_CAT_SOCKS);
        simpleItem(ModItems.BLACK_CAT_SOCKS);
        simpleItem(ModItems.GREEN_CAT_SOCKS);

        simpleItem(ModItems.HEAD_LEAF);
        simpleItem(ModItems.HEAD_FLOWER);
        simpleItem(ModItems.HEAD_DANDELION);
        simpleItem(ModItems.TAIL_VINES);
        simpleItem(ModItems.DRAPED_TAIL_VINES);
        simpleItem(ModItems.HEAD_GLOWBERRY);
        simpleItem(ModItems.HEAD_SWEETBERRY);
        simpleItem(ModItems.CAT_HAT);
        simpleItem(ModItems.CAT_PINK_BOW);
        simpleItem(ModItems.CAT_RED_BOW);
        simpleItem(ModItems.CAT_BLACK_BOW);
        simpleItem(ModItems.SKULL_MASK);

        simpleItem(ModItems.GREEN_PARROT_FEATHER);
        simpleItem(ModItems.BLUE_PARROT_FEATHER);
        simpleItem(ModItems.LIGHTBLUE_PARROT_FEATHER);
        simpleItem(ModItems.RED_PARROT_FEATHER);
        simpleItem(ModItems.GRAY_PARROT_FEATHER);

        simpleItem(ModItems.BLACK_VULTURE_FEATHER);
        simpleItem(ModItems.PIGEON_FEATHER);
        simpleItem(ModItems.CROW_FEATHER);
        simpleItem(ModItems.GOLDFINCH_FEATHER);
        simpleItem(ModItems.CARDINAL_FEATHER);

        simpleItem(ModItems.GREEN_PARROT_BODY_FEATHERS);
        simpleItem(ModItems.BLUE_PARROT_BODY_FEATHERS);
        simpleItem(ModItems.LIGHTBLUE_PARROT_BODY_FEATHERS);
        simpleItem(ModItems.RED_PARROT_BODY_FEATHERS);
        simpleItem(ModItems.GRAY_PARROT_BODY_FEATHERS);
        simpleItem(ModItems.CHICKEN_BODY_FEATHERS);

        simpleItem(ModItems.VULTURE_BODY_FEATHERS);
        simpleItem(ModItems.PIGEON_BODY_FEATHERS);
        simpleItem(ModItems.CROW_BODY_FEATHERS);
        simpleItem(ModItems.GOLDFINCH_BODY_FEATHERS);
        simpleItem(ModItems.CARDINAL_BODY_FEATHERS);

        simpleItem(ModItems.COLLAR_BELL);


        simpleItem(ModItems.SQUIRREL_SKULL);
        simpleItem(ModItems.BADGER_SKULL);
        simpleItem(ModItems.GOLDEN_EAGLE_SKULL);

        simpleItem(ModItems.PEBBLES_ITEM);



        simpleItem(ModItems.JUNIPERPLANT);


        simpleItem(ModItems.LEG_WRAP);
        simpleItem(ModItems.YARROW_POULTICE);
        simpleItem(ModItems.COMFREY_LEAVES);
        simpleItem(ModItems.COMFREY_ROOT);
        simpleItem(ModItems.COMFREY_POULTICE);
        simpleItem(ModItems.COBWEB_WITH_A_STICK);

        simpleItem(ModItems.ACORN_LANTERN_ITEM);
        simpleItem(ModItems.DAISY_CHAIN_ITEM);
        simpleItem(ModItems.LAVENDER_CHAIN_ITEM);

        simpleItem(ModItems.BLUE_MORPHO_WING);
        simpleItem(ModItems.GOLIATH_BIRDWING_WING);
        simpleItem(ModItems.TIGER_SWALLOWTAIL_WING);
        simpleItem(ModItems.MONARCH_WING);

        simpleItem(ModItems.LIZARD_EGG_ITEM);

//        simpleItem(ModItems.MOSS_BALL);


        simpleBlockItem(ModBlocks.LEAF_DOOR);
//        simpleBlockItem(ModBlocks.LEAF_TRAPDOOR);

//        withExistingParent(ModItems.STONE_CRAFTING_TABLE.getId().getPath(), modLoc("block/" + ModBlocks.STONE_CRAFTING_TABLE.getId().getPath()));

        withExistingParent(ModItems.MOUSE_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.SQUIRREL_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.WILDCAT_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.PIGEON_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.BADGER_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.GOLDEN_EAGLE_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.LIZARD_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));


        basicItem(ModItems.DOCK_LEAVES.get());
        basicItem(ModItems.SORREL.get());
        basicItem(ModItems.BURNET.get());
        basicItem(ModItems.CHAMOMILE.get());
        basicItem(ModItems.DAISY.get());
        basicItem(ModItems.DEATHBERRIES.get());
        basicItem(ModItems.CATMINT.get());
        basicItem(ModItems.YARROW.get());
        basicItem(ModItems.FEVERFEW.get());
        basicItem(ModItems.JUNIPER_BERRIES.get());
        basicItem(ModItems.POPPY_SEEDS.get());
        basicItem(ModItems.GLOW_SHROOM.get());
        basicItem(ModItems.MYSTIC_FLOWERS_BOUQUET.get());
        basicItem(ModItems.STRANGE_SHINY_STONE.get());


        {
            withExistingParent(
                    ModBlocks.DOCK.getId().getPath(),
                    ResourceLocation.withDefaultNamespace("item/generated")
            ).texture("layer0",
                    ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "block/dock_stage0")
            );
            withExistingParent(
                    ModBlocks.SORRELPLANT.getId().getPath(),
                    ResourceLocation.withDefaultNamespace("item/generated")
            ).texture("layer0",
                    ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "block/sorrel_stage0")
            );
            withExistingParent(
                    ModBlocks.BURNETPLANT.getId().getPath(),
                    ResourceLocation.withDefaultNamespace("item/generated")
            ).texture("layer0",
                    ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "block/burnet_stage0")
            );
            withExistingParent(
                    ModBlocks.CHAMOMILEPLANT.getId().getPath(),
                    ResourceLocation.withDefaultNamespace("item/generated")
            ).texture("layer0",
                    ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "block/chamomile_stage0")
            );
            withExistingParent(
                    ModBlocks.DAISYPLANT.getId().getPath(),
                    ResourceLocation.withDefaultNamespace("item/generated")
            ).texture("layer0",
                    ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "block/daisy_stage0")
            );
            withExistingParent(
                    ModBlocks.DEATHBERRIESBUSH.getId().getPath(),
                    ResourceLocation.withDefaultNamespace("item/generated")
            ).texture("layer0",
                    ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "block/deathberries_stage0")
            );
            withExistingParent(
                    ModBlocks.CATMINTPLANT.getId().getPath(),
                    ResourceLocation.withDefaultNamespace("item/generated")
            ).texture("layer0",
                    ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "block/catmint_stage0")
            );
            withExistingParent(
                    ModBlocks.YARROWPLANT.getId().getPath(),
                    ResourceLocation.withDefaultNamespace("item/generated")
            ).texture("layer0",
                    ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "block/yarrow_stage0")
            );
            withExistingParent(
                    ModBlocks.FEVERFEWPLANT.getId().getPath(),
                    ResourceLocation.withDefaultNamespace("item/generated")
            ).texture("layer0",
                    ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "block/feverfew_stage0")
            );
            withExistingParent(
                    ModBlocks.COMFREYPLANT.getId().getPath(),
                    ResourceLocation.withDefaultNamespace("item/generated")
            ).texture("layer0",
                    ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "block/comfrey_stage0")
            );

        }


    }



    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                ResourceLocation.withDefaultNamespace("item/generated")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "item/" + item.getId().getPath()));
    }
    private ItemModelBuilder simpleBlockItem(RegistryObject<Block> item) {
        return withExistingParent(item.getId().getPath(),
                ResourceLocation.withDefaultNamespace("item/generated")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "item/" + item.getId().getPath()));
    }
    private ItemModelBuilder saplingItem(RegistryObject<Block> item) {
        return withExistingParent(item.getId().getPath(),
                ResourceLocation.withDefaultNamespace("item/generated")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "block/" + item.getId().getPath()));
    }


}
