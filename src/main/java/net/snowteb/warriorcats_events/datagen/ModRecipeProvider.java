package net.snowteb.warriorcats_events.datagen;

import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.block.ModBlocks;
import net.snowteb.warriorcats_events.item.ModItems;
import net.snowteb.warriorcats_events.recipes.WCERecipes;
import net.snowteb.warriorcats_events.util.ModTags;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.util.IConsumer;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput output) {
        super(output);
    }



    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.STONECLEFT.get())
                .pattern("ABA")
                .pattern("B B")
                .pattern("ABA")
                .define('A', ItemTags.SAPLINGS)
                .define('B', Items.COBBLESTONE)
                .unlockedBy("has_item", has(Items.COBBLESTONE))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MYSTIC_FLOWERS_BOUQUET.get())
                .pattern("CAC")
                .pattern(" BA")
                .pattern("B C")
                .define('A', ModItems.STRANGE_SHINY_STONE.get())
                .define('B', ModItems.ANCIENT_STICK.get())
                .define('C', ItemTags.FLOWERS)
                .unlockedBy("has_item", has(ItemTags.FLOWERS))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.FLOWER_CROWN.get())
                .pattern("A A")
                .pattern("BCB")
                .pattern("   ")
                .define('A', ModTags.Items.HERBS)
                .define('B', ItemTags.FLOWERS)
                .define('C', Items.VINE)
                .unlockedBy("has_item", has(ModTags.Items.HERBS))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.LEAF_MANE.get())
                .pattern("AAA")
                .pattern("ACA")
                .pattern("AAA")
                .define('A', ItemTags.LEAVES)
                .define('C', Items.VINE)
                .unlockedBy("has_item", has(ItemTags.LEAVES))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.WARRIOR_NAMETAG.get())
                .pattern("  A")
                .pattern("CB ")
                .pattern("BC ")
                .define('A', Items.STRING)
                .define('B', Items.SUGAR_CANE)
                .define('C', ModTags.Items.HERBS)
                .unlockedBy("has_item", has(Items.SUGAR_CANE))
                .save(pWriter);


        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.ANIMAL_TEETH.get(), 1)
                .requires(ModItems.ANIMAL_TOOTH.get())
                .requires(ModItems.ANIMAL_TOOTH.get())
                .requires(ModItems.ANIMAL_TOOTH.get())
                .requires(ModItems.ANIMAL_TOOTH.get())
                .requires(Items.VINE)
                .unlockedBy("has_item", has(ModItems.ANIMAL_TOOTH.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.TEETH_CLAWS.get())
                .pattern("   ")
                .pattern("A A")
                .pattern("A A")
                .define('A', ModItems.ANIMAL_TEETH.get())
                .unlockedBy("has_item", has(ModItems.ANIMAL_TEETH.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.FLOWER_ARMOR.get())
                .pattern("ACA")
                .pattern("BCB")
                .pattern("ACA")
                .define('B', ModTags.Items.HERBS)
                .define('A', ItemTags.FLOWERS)
                .define('C', Items.VINE)
                .unlockedBy("has_item", has(ModTags.Items.HERBS))
                .save(pWriter);

        //
//        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.TRAVELING_HERBS.get(), 1)
//                .requires(ModItems.SORREL.get())
//                .requires(ModItems.BURNET.get())
//                .requires(ModItems.DAISY.get())
//                .requires(ModItems.CHAMOMILE.get())
//                .requires(ModItems.DOCK_LEAVES.get())
//                .unlockedBy(getHasName(ModItems.DOCK_LEAVES.get()), has(ModItems.DOCK_LEAVES.get()))
//                .save(pWriter);
        //

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.WARRIORNAMERANDOMIZER.get(), 1)
                .requires(ModTags.Items.HERBS)
                .requires(ModTags.Items.HERBS)
                .requires(ModTags.Items.HERBS)
                .requires(Items.SUGAR_CANE)
                .requires(ItemTags.SAPLINGS)
                .unlockedBy("has_item", has(Items.SUGAR_CANE))
                .save(pWriter);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.WHISKERS.get(), 1)
                .requires(ItemTags.SAPLINGS)
                .requires(Items.WHEAT_SEEDS)
                .requires(ModTags.Items.HERBS)
                .requires(Items.LEATHER)
                .unlockedBy("has_item", has(ModTags.Items.HERBS))
                .save(pWriter);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.CLAWS.get(), 1)
                .requires(ItemTags.LOGS)
                .requires(Items.LEATHER)
                .unlockedBy("has_item", has(Items.LEATHER))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ANCIENT_STICK.get())
                .pattern("CDB")
                .pattern(" AE")
                .pattern("A C")
                .define('A', Items.STICK)
                .define('B', ModItems.ANIMAL_TOOTH.get())
                .define('C', ModTags.Items.HERBS)
                .define('D', ModItems.STRANGE_SHINY_STONE.get())
                .define('E', Items.EMERALD)
                .unlockedBy("has_item", has(Items.STICK))
                .save(pWriter);

        //
//        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.FRESHKILL_AND_HERBS_BUNDLE.get(), 2)
//                .requires(ModTags.Items.HERBS)
//                .requires(ModTags.Items.PREY)
//                .requires(ModTags.Items.HERBS)
//                .requires(ModTags.Items.PREY)
//                .requires(ModItems.DOCK_LEAVES.get())
//                .unlockedBy("has_item", has(ModTags.Items.PREY))
//                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.DOCK_POULTICE.get(), 1)
                .requires(ModItems.DOCK_LEAVES.get())
                .unlockedBy("has_item", has(ModItems.DOCK_LEAVES.get()))
                .save(pWriter);
        //

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.LEAF_DOOR.get(), 2)
                .pattern("AA")
                .pattern("AA")
                .pattern("AA")
                .define('A', ItemTags.LEAVES)
                .unlockedBy("has_leaves", has(ItemTags.LEAVES))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.LEAF_TRAPDOOR.get(), 4)
                .pattern("AAA")
                .pattern("AAA")
                .define('A', ItemTags.LEAVES)
                .unlockedBy("has_leaves", has(ItemTags.LEAVES))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.MOSS_BED.get(), 2)
                .pattern("   ")
                .pattern("BDB")
                .pattern("CBC")
                .define('B', ItemTags.LEAVES)
                .define('C', ModTags.Items.FEATHERS)
                .define('D', Items.MOSS_BLOCK)
                .unlockedBy("has_item", has(Items.MOSS_BLOCK))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.HAY_BED.get(), 2)
                .pattern("   ")
                .pattern("BDB")
                .pattern("CBC")
                .define('B', Items.WHEAT)
                .define('C', ModTags.Items.FEATHERS)
                .define('D', Blocks.HAY_BLOCK)
                .unlockedBy("has_item", has(Items.HAY_BLOCK))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.KELP_BED.get(), 2)
                .pattern("   ")
                .pattern("BDB")
                .pattern("CBC")
                .define('B', Items.KELP)
                .define('C', Items.SEAGRASS)
                .define('D', Blocks.GRASS)
                .unlockedBy("has_item", has(Items.KELP))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.STONE_BED.get(), 2)
                .pattern("   ")
                .pattern("BDB")
                .pattern("DBD")
                .define('B', Items.STONE)
                .define('D', ModTags.Items.FEATHERS)
                .unlockedBy("has_item", has(Items.STONE))
                .save(pWriter);


        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.LAVENDER_BED.get(), 2)
                .pattern("   ")
                .pattern("BCB")
                .pattern("DBD")
                .define('B', ModBlocks.LAVENDER.get())
                .define('D', ModBlocks.LAVENDER_PETALS.get())
                .define('C', Items.MOSS_BLOCK)
                .unlockedBy("has_item", has(ModBlocks.LAVENDER.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.CHERRY_BLOSSOM_BED.get(), 2)
                .pattern("   ")
                .pattern("BCB")
                .pattern("DBD")
                .define('B', Items.CHERRY_LEAVES)
                .define('D', Items.CHERRY_SAPLING)
                .define('C', Items.MOSS_BLOCK)
                .unlockedBy("has_item", has(Items.CHERRY_LEAVES))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.DRIFTWOOD_BED.get(), 2)
                .pattern("   ")
                .pattern("BCB")
                .pattern("DBD")
                .define('B', Items.STICK)
                .define('D', Items.MANGROVE_ROOTS)
                .define('C', Items.SAND)
                .unlockedBy("has_item", has(Items.MANGROVE_ROOTS))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.DAISY_BED.get(), 2)
                .pattern("   ")
                .pattern("BCB")
                .pattern("DBD")
                .define('B', ModItems.DAISY.get())
                .define('D', Items.OXEYE_DAISY)
                .define('C', ModBlocks.DAISYPLANT.get())
                .unlockedBy("has_item", has(ModItems.DAISY.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.ACACIA_BED.get(), 2)
                .pattern("   ")
                .pattern("BCB")
                .pattern("DBD")
                .define('B', Items.ACACIA_LEAVES)
                .define('D', ModItems.CHAMOMILE.get())
                .define('C', ModBlocks.CHAMOMILEPLANT.get())
                .unlockedBy("has_item", has(ModItems.CHAMOMILE.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.TERRACOTTA_BED.get(), 2)
                .pattern("   ")
                .pattern("BCB")
                .pattern("DBD")
                .define('B', ItemTags.TERRACOTTA)
                .define('D', Items.STICK)
                .define('C', ItemTags.LEAVES)
                .unlockedBy("has_item", has(ItemTags.TERRACOTTA))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BAMBOO_BED.get(), 2)
                .pattern("   ")
                .pattern("BCB")
                .pattern("DBD")
                .define('B', Items.BAMBOO)
                .define('D', ItemTags.LEAVES)
                .define('C', Items.BAMBOO)
                .unlockedBy("has_item", has(ItemTags.TERRACOTTA))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BERRY_BED.get(), 2)
                .pattern("   ")
                .pattern("BCB")
                .pattern("DBD")
                .define('B', Items.SWEET_BERRIES)
                .define('D', ItemTags.LEAVES)
                .define('C', ItemTags.LEAVES)
                .unlockedBy("has_item", has(Items.SWEET_BERRIES))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.CORAL_BED.get(), 2)
                .pattern("   ")
                .pattern("B D")
                .pattern("ECE")
                .define('B', Items.BRAIN_CORAL)
                .define('C', Items.TUBE_CORAL)
                .define('D', Items.FIRE_CORAL)
                .define('E', Items.SAND)
                .unlockedBy("has_item", has(Items.BRAIN_CORAL))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.GLOWBERRY_BED.get(), 2)
                .pattern("   ")
                .pattern("BCB")
                .pattern("DBD")
                .define('B', Items.GLOW_BERRIES)
                .define('D', ItemTags.LEAVES)
                .define('C', ItemTags.LEAVES)
                .unlockedBy("has_item", has(Items.GLOW_BERRIES))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.MUDDY_BED.get(), 2)
                .pattern("   ")
                .pattern("BCB")
                .pattern("DBD")
                .define('B', Items.MUD)
                .define('D', ItemTags.LEAVES)
                .define('C', ItemTags.LEAVES)
                .unlockedBy("has_item", has(Items.MUD))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.KITTYPET_BED.get(), 1)
                .pattern("   ")
                .pattern("BCB")
                .pattern("BBB")
                .define('B', Items.STICK)
                .define('C', Items.BAMBOO_PLANKS)
                .unlockedBy("has_item", has(Items.BAMBOO_PLANKS))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.ACORN_BED.get(), 2)
                .pattern("   ")
                .pattern("DCD")
                .pattern("BBB")
                .define('B', ItemTags.LEAVES)
                .define('C', ItemTags.LOGS_THAT_BURN)
                .define('D', Items.SPRUCE_LEAVES)
                .unlockedBy("has_item", has(Items.SPRUCE_LEAVES))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.NAUTILUS_BED.get(), 1)
                .pattern("B B")
                .pattern("BCB")
                .pattern("BDB")
                .define('B', Items.NAUTILUS_SHELL)
                .define('C', Items.KELP)
                .define('D', Items.HEART_OF_THE_SEA)
                .unlockedBy("has_item", has(Items.SPRUCE_LEAVES))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SUNFLOWER_BED.get(), 2)
                .pattern("   ")
                .pattern("D D")
                .pattern("BBB")
                .define('B', Items.SUNFLOWER)
                .define('D', ItemTags.LEAVES)
                .unlockedBy("has_item", has(Items.SUNFLOWER))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.MOONSTONE_BLOCK.get(), 1)
                .pattern("   ")
                .pattern("ACA")
                .pattern("BCB")
                .define('A', ModItems.STRANGE_SHINY_STONE.get())
                .define('B', Items.DEEPSLATE)
                .define('C', ModBlocks.GLOWROCKS.get())
                .unlockedBy("has_item", has(ModItems.STRANGE_SHINY_STONE.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.GREEN_GLOWROCKS_ITEM.get(), 4)
                .requires(ModItems.GLOWROCKS_ITEM.get())
                .requires(ModItems.GLOWROCKS_ITEM.get())
                .requires(ModItems.GLOWROCKS_ITEM.get())
                .requires(ModItems.GLOWROCKS_ITEM.get())
                .requires(Items.LIME_DYE)
                .unlockedBy("has_item", has(ModItems.GLOWROCKS_ITEM.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.PINK_GLOWROCKS_ITEM.get(), 4)
                .requires(ModItems.GLOWROCKS_ITEM.get())
                .requires(ModItems.GLOWROCKS_ITEM.get())
                .requires(ModItems.GLOWROCKS_ITEM.get())
                .requires(ModItems.GLOWROCKS_ITEM.get())
                .requires(Items.PINK_DYE)
                .unlockedBy("has_item", has(ModItems.GLOWROCKS_ITEM.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.RED_GLOWROCKS_ITEM.get(), 4)
                .requires(ModItems.GLOWROCKS_ITEM.get())
                .requires(ModItems.GLOWROCKS_ITEM.get())
                .requires(ModItems.GLOWROCKS_ITEM.get())
                .requires(ModItems.GLOWROCKS_ITEM.get())
                .requires(Items.RED_DYE)
                .unlockedBy("has_item", has(ModItems.GLOWROCKS_ITEM.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.YELLOW_GLOWROCKS_ITEM.get(), 4)
                .requires(ModItems.GLOWROCKS_ITEM.get())
                .requires(ModItems.GLOWROCKS_ITEM.get())
                .requires(ModItems.GLOWROCKS_ITEM.get())
                .requires(ModItems.GLOWROCKS_ITEM.get())
                .requires(Items.YELLOW_DYE)
                .unlockedBy("has_item", has(ModItems.GLOWROCKS_ITEM.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModBlocks.ACORN_LANTERN.get(), 1)
                .requires(Items.LANTERN)
                .requires(Items.MOSS_CARPET)
                .unlockedBy("has_item", has(Items.LANTERN))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.DAISY_CHAIN.get(), 3)
                .pattern("B B")
                .pattern("AAA")
                .define('A', ModItems.DAISY.get())
                .define('B', Items.STRING)
                .unlockedBy("has_item", has(ModItems.DAISY.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.LAVENDER_CHAIN.get(), 3)
                .pattern("B B")
                .pattern("AAA")
                .define('A', ModBlocks.LAVENDER.get())
                .define('B', Items.STRING)
                .unlockedBy("has_item", has(ModBlocks.LAVENDER.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.STICKFIRE.get(), 1)
                .pattern("CCC")
                .pattern("BCB")
                .pattern("AAA")
                .define('A', ItemTags.COALS)
                .define('B', Items.COBBLESTONE)
                .define('C', Items.STICK)
                .unlockedBy("has_item", has(ItemTags.COALS))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.FRESHKILL_PILE.get(), 1)
                .pattern("BAB")
                .pattern("ADA")
                .pattern("BAB")
                .define('A', Items.STICK)
                .define('B', Blocks.COBBLESTONE)
                .define('D', ModItems.DOCK_LEAVES.get())
                .unlockedBy("has_item", has(Items.COBBLESTONE))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.STONE_CRAFTING_TABLE.get(), 1)
                .pattern("A  ")
                .pattern("B  ")
                .pattern("   ")
                .define('B', Blocks.CRAFTING_TABLE)
                .define('A', ModTags.Items.HERBS)
                .unlockedBy("has_item", has(Items.CRAFTING_TABLE))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.WARRIORS_GUIDE.get(), 1)
                .pattern(" A ")
                .pattern("CBC")
                .pattern(" A ")
                .define('A', ModTags.Items.HERBS)
                .define('B', Items.LEATHER)
                .define('C', ItemTags.LEAVES)
                .unlockedBy("has_item", has(Items.LEATHER))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BLUE_CAT_SOCKS.get(), 1)
                .pattern("A A")
                .define('A', Items.BLUE_WOOL)
                .unlockedBy("has_item", has(Items.BLUE_WOOL))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BLUE_CAT_SOCKS.get(), 1)
                .requires(ModItems.WHITE_CAT_SOCKS.get())
                .requires(Items.BLUE_DYE)
                .unlockedBy("has_item", has(ModItems.WHITE_CAT_SOCKS.get()))
                .save(pWriter, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "blue_cat_socks_from_dye"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ORANGE_CAT_SOCKS.get(), 1)
                .pattern("A A")
                .define('A', Items.ORANGE_WOOL)
                .unlockedBy("has_item", has(Items.ORANGE_WOOL))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.ORANGE_CAT_SOCKS.get(), 1)
                .requires(ModItems.WHITE_CAT_SOCKS.get())
                .requires(Items.ORANGE_DYE)
                .unlockedBy("has_item", has(ModItems.WHITE_CAT_SOCKS.get()))
                .save(pWriter, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "orange_cat_socks_from_dye"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PINK_CAT_SOCKS.get(), 1)
                .pattern("A A")
                .define('A', Items.PINK_WOOL)
                .unlockedBy("has_item", has(Items.PINK_WOOL))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.PINK_CAT_SOCKS.get(), 1)
                .requires(ModItems.WHITE_CAT_SOCKS.get())
                .requires(Items.PINK_DYE)
                .unlockedBy("has_item", has(ModItems.WHITE_CAT_SOCKS.get()))
                .save(pWriter, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "pink_cat_socks_from_dye"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.WHITE_CAT_SOCKS.get(), 1)
                .pattern("A A")
                .define('A', Items.WHITE_WOOL)
                .unlockedBy("has_item", has(Items.WHITE_WOOL))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BLACK_CAT_SOCKS.get(), 1)
                .pattern("A A")
                .define('A', Items.BLACK_WOOL)
                .unlockedBy("has_item", has(Items.BLACK_WOOL))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BLACK_CAT_SOCKS.get(), 1)
                .requires(ModItems.WHITE_CAT_SOCKS.get())
                .requires(Items.BLACK_DYE)
                .unlockedBy("has_item", has(ModItems.WHITE_CAT_SOCKS.get()))
                .save(pWriter, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "black_cat_socks_from_dye"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.GREEN_CAT_SOCKS.get(), 1)
                .pattern("A A")
                .define('A', Items.LIME_WOOL)
                .unlockedBy("has_item", has(Items.LIME_WOOL))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.GREEN_CAT_SOCKS.get(), 1)
                .requires(ModItems.WHITE_CAT_SOCKS.get())
                .requires(Items.LIME_DYE)
                .unlockedBy("has_item", has(ModItems.WHITE_CAT_SOCKS.get()))
                .save(pWriter, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "green_cat_socks_from_dye"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.HEAD_LEAF.get(), 2)
                .requires(ItemTags.LEAVES)
                .unlockedBy("has_item", has(ItemTags.LEAVES))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.HEAD_FLOWER.get(), 1)
                .requires(Items.POPPY)
                .requires(ModItems.DOCK_POULTICE.get())
                .unlockedBy("has_item", has(Items.POPPY))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.HEAD_DANDELION.get(), 1)
                .requires(Items.DANDELION)
                .requires(ModItems.DOCK_POULTICE.get())
                .unlockedBy("has_item", has(Items.DANDELION))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.TAIL_VINES.get(), 1)
                .pattern("A A")
                .pattern("BCB")
                .pattern("A A")
                .define('A', Items.STRING)
                .define('B', Items.VINE)
                .define('C', ModItems.DOCK_LEAVES.get())
                .unlockedBy("has_item", has(Items.VINE))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.DRAPED_TAIL_VINES.get(), 1)
                .pattern("A A")
                .pattern("BCB")
                .pattern("A A")
                .define('A', ItemTags.SMALL_FLOWERS)
                .define('B', Items.GLOW_LICHEN)
                .define('C', ModItems.TAIL_VINES.get())
                .unlockedBy("has_item", has(ModItems.TAIL_VINES.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.MOSS_BLOCK, 1)
                .pattern("AA")
                .pattern("AA")
                .define('A', Items.MOSS_CARPET)
                .unlockedBy("has_item", has(Items.MOSS_CARPET))
                .save(pWriter, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "moss_block_from_carpet"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BLUE_PARROT_BODY_FEATHERS.get(), 1)
                .pattern("AAA")
                .pattern("AAA")
                .pattern("AAA")
                .define('A', ModItems.BLUE_PARROT_FEATHER.get())
                .unlockedBy("has_item", has(ModItems.BLUE_PARROT_FEATHER.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.RED_PARROT_BODY_FEATHERS.get(), 1)
                .pattern("AAA")
                .pattern("AAA")
                .pattern("AAA")
                .define('A', ModItems.RED_PARROT_FEATHER.get())
                .unlockedBy("has_item", has(ModItems.RED_PARROT_FEATHER.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.GREEN_PARROT_BODY_FEATHERS.get(), 1)
                .pattern("AAA")
                .pattern("AAA")
                .pattern("AAA")
                .define('A', ModItems.GREEN_PARROT_FEATHER.get())
                .unlockedBy("has_item", has(ModItems.GREEN_PARROT_FEATHER.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.LIGHTBLUE_PARROT_BODY_FEATHERS.get(), 1)
                .pattern("AAA")
                .pattern("AAA")
                .pattern("AAA")
                .define('A', ModItems.LIGHTBLUE_PARROT_FEATHER.get())
                .unlockedBy("has_item", has(ModItems.LIGHTBLUE_PARROT_FEATHER.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.GRAY_PARROT_BODY_FEATHERS.get(), 1)
                .pattern("AAA")
                .pattern("AAA")
                .pattern("AAA")
                .define('A', ModItems.GRAY_PARROT_FEATHER.get())
                .unlockedBy("has_item", has(ModItems.GRAY_PARROT_FEATHER.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CHICKEN_BODY_FEATHERS.get(), 1)
                .pattern("AAA")
                .pattern("AAA")
                .pattern("AAA")
                .define('A', Items.FEATHER)
                .unlockedBy("has_item", has(Items.FEATHER))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.VULTURE_BODY_FEATHERS.get(), 1)
                .pattern("AAA")
                .pattern("AAA")
                .pattern("AAA")
                .define('A', ModItems.BLACK_VULTURE_FEATHER.get())
                .unlockedBy("has_item", has(ModItems.BLACK_VULTURE_FEATHER.get()))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PIGEON_BODY_FEATHERS.get(), 1)
                .pattern("AAA")
                .pattern("AAA")
                .pattern("AAA")
                .define('A', ModItems.PIGEON_FEATHER.get())
                .unlockedBy("has_item", has(ModItems.PIGEON_FEATHER.get()))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CROW_BODY_FEATHERS.get(), 1)
                .pattern("AAA")
                .pattern("AAA")
                .pattern("AAA")
                .define('A', ModItems.CROW_FEATHER.get())
                .unlockedBy("has_item", has(ModItems.CROW_FEATHER.get()))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.GOLDFINCH_BODY_FEATHERS.get(), 1)
                .pattern("AAA")
                .pattern("AAA")
                .pattern("AAA")
                .define('A', ModItems.GOLDFINCH_FEATHER.get())
                .unlockedBy("has_item", has(ModItems.GOLDFINCH_FEATHER.get()))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CARDINAL_BODY_FEATHERS.get(), 1)
                .pattern("AAA")
                .pattern("AAA")
                .pattern("AAA")
                .define('A', ModItems.CARDINAL_FEATHER.get())
                .unlockedBy("has_item", has(ModItems.CARDINAL_FEATHER.get()))
                .save(pWriter);


        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BLACK_VULTURE_FEATHER.get(), 2)
                .requires(ModTags.Items.FEATHERS)
                .requires(ModTags.Items.FEATHERS)
                .requires(ModTags.Items.FEATHERS)
                .requires(Items.BLACK_DYE)
                .unlockedBy("has_item", has(ModTags.Items.FEATHERS))
                .save(pWriter);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.CROW_FEATHER.get(), 1)
                .requires(ModTags.Items.FEATHERS)
                .requires(Items.BLACK_DYE)
                .unlockedBy("has_item", has(ModTags.Items.FEATHERS))
                .save(pWriter);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.GOLDFINCH_FEATHER.get(), 3)
                .requires(ModTags.Items.FEATHERS)
                .requires(ModTags.Items.FEATHERS)
                .requires(ModTags.Items.FEATHERS)
                .requires(Items.BLACK_DYE)
                .requires(Items.YELLOW_DYE)
                .requires(Items.YELLOW_DYE)
                .unlockedBy("has_item", has(ModTags.Items.FEATHERS))
                .save(pWriter);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.CARDINAL_FEATHER.get(), 1)
                .requires(ModTags.Items.FEATHERS)
                .requires(Items.RED_DYE)
                .unlockedBy("has_item", has(ModTags.Items.FEATHERS))
                .save(pWriter);


        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.GENERATIONS_MUSIC_DISC.get(), 1)
                .pattern("ABA")
                .pattern("BCB")
                .pattern("ABA")
                .define('A', ModTags.Items.HERBS)
                .define('B', Items.DIAMOND_BLOCK)
                .define('C', ItemTags.MUSIC_DISCS)
                .unlockedBy("has_item", has(ItemTags.MUSIC_DISCS))
                .save(pWriter);


        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.NAME_TAG, 1)
                .requires(ModItems.WARRIOR_NAMETAG.get())
                .unlockedBy("has_item", has(ModItems.WARRIOR_NAMETAG.get()))
                .save(pWriter, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "nametag_from_warriortag"));


        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModBlocks.LAVENDER_PETALS.get(), 3)
                .requires(ModBlocks.LAVENDER.get())
                .unlockedBy("has_item", has(ModBlocks.LAVENDER.get()))
                .save(pWriter);


        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BLACK_CAT_COLLAR.get(), 1)
                .pattern(" B ")
                .pattern("AAA")
                .define('A', Items.LEATHER)
                .define('B', Items.BLACK_DYE)
                .unlockedBy("has_item", has(Items.LEATHER))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BROWN_CAT_COLLAR.get(), 1)
                .pattern("AAA")
                .define('A', Items.LEATHER)
                .unlockedBy("has_item", has(ItemTags.MUSIC_DISCS))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.WHITE_CAT_COLLAR.get(), 1)
                .pattern(" B ")
                .pattern("AAA")
                .define('A', Items.LEATHER)
                .define('B', Items.WHITE_DYE)
                .unlockedBy("has_item", has(Items.LEATHER))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PINK_CAT_COLLAR.get(), 1)
                .pattern(" B ")
                .pattern("AAA")
                .define('A', Items.LEATHER)
                .define('B', Items.PINK_DYE)
                .unlockedBy("has_item", has(Items.LEATHER))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ORANGE_CAT_COLLAR.get(), 1)
                .pattern(" B ")
                .pattern("AAA")
                .define('A', Items.LEATHER)
                .define('B', Items.ORANGE_DYE)
                .unlockedBy("has_item", has(Items.LEATHER))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.RED_CAT_COLLAR.get(), 1)
                .pattern(" B ")
                .pattern("AAA")
                .define('A', Items.LEATHER)
                .define('B', Items.RED_DYE)
                .unlockedBy("has_item", has(Items.LEATHER))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BLUE_CAT_COLLAR.get(), 1)
                .pattern(" B ")
                .pattern("AAA")
                .define('A', Items.LEATHER)
                .define('B', Items.BLUE_DYE)
                .unlockedBy("has_item", has(Items.LEATHER))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PURPLE_CAT_COLLAR.get(), 1)
                .pattern(" B ")
                .pattern("AAA")
                .define('A', Items.LEATHER)
                .define('B', Items.PURPLE_DYE)
                .unlockedBy("has_item", has(Items.LEATHER))
                .save(pWriter);


        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BLACK_CAT_COLLAR.get(), 1)
                .requires(ModItems.BROWN_CAT_COLLAR.get())
                .requires(Items.BLACK_DYE)
                .unlockedBy("has_item", has(ModItems.BROWN_CAT_COLLAR.get()))
                .save(pWriter, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "black_collar_recolored"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.WHITE_CAT_COLLAR.get(), 1)
                .requires(ModItems.BROWN_CAT_COLLAR.get())
                .requires(Items.WHITE_DYE)
                .unlockedBy("has_item", has(ModItems.BROWN_CAT_COLLAR.get()))
                .save(pWriter, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "white_collar_recolored"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.PINK_CAT_COLLAR.get(), 1)
                .requires(ModItems.BROWN_CAT_COLLAR.get())
                .requires(Items.PINK_DYE)
                .unlockedBy("has_item", has(ModItems.BROWN_CAT_COLLAR.get()))
                .save(pWriter, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "pink_collar_recolored"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.ORANGE_CAT_COLLAR.get(), 1)
                .requires(ModItems.BROWN_CAT_COLLAR.get())
                .requires(Items.ORANGE_DYE)
                .unlockedBy("has_item", has(ModItems.BROWN_CAT_COLLAR.get()))
                .save(pWriter, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "orange_collar_recolored"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.RED_CAT_COLLAR.get(), 1)
                .requires(ModItems.BROWN_CAT_COLLAR.get())
                .requires(Items.RED_DYE)
                .unlockedBy("has_item", has(ModItems.BROWN_CAT_COLLAR.get()))
                .save(pWriter, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "red_collar_recolored"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BLUE_CAT_COLLAR.get(), 1)
                .requires(ModItems.BROWN_CAT_COLLAR.get())
                .requires(Items.BLUE_DYE)
                .unlockedBy("has_item", has(ModItems.BROWN_CAT_COLLAR.get()))
                .save(pWriter, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "blue_collar_recolored"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.PURPLE_CAT_COLLAR.get(), 1)
                .requires(ModItems.BROWN_CAT_COLLAR.get())
                .requires(Items.PURPLE_DYE)
                .unlockedBy("has_item", has(ModItems.BROWN_CAT_COLLAR.get()))
                .save(pWriter, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "purple_collar_recolored"));


        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.COLLAR_BELL.get(), 1)
                .requires(Items.GOLD_INGOT)
                .requires(Items.GOLD_NUGGET)
                .unlockedBy("has_item", has(Items.GOLD_INGOT))
                .save(pWriter);



        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.HEAD_GLOWBERRY.get(), 1)
                .requires(Items.GLOW_BERRIES)
                .requires(ModItems.DOCK_POULTICE.get())
                .unlockedBy("has_item", has(Items.GLOW_BERRIES))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.HEAD_SWEETBERRY.get(), 1)
                .requires(Items.SWEET_BERRIES)
                .requires(ModItems.DOCK_POULTICE.get())
                .unlockedBy("has_item", has(Items.SWEET_BERRIES))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.FEATHER, 2)
                .requires(ModTags.Items.WCE_FEATHERS)
                .requires(ModTags.Items.WCE_FEATHERS)
                .requires(ModTags.Items.WCE_FEATHERS)
                .unlockedBy("has_item", has(ModTags.Items.FEATHERS))
                .save(pWriter, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "feather_from_wce_feather"));


        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SKULL_MASK.get())
                .pattern(" B ")
                .pattern("BAB")
                .pattern(" B ")
                .define('A', ModTags.Items.SKULLS)
                .define('B', Items.BONE)
                .unlockedBy("has_item", has(Items.BONE))
                .save(pWriter);



        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.WHITE_KITTYPET_BOWL.get(), 1)
                .pattern("   ")
                .pattern("BAB")
                .pattern("BBB")
                .define('A', Items.IRON_INGOT)
                .define('B', Items.WHITE_TERRACOTTA)
                .unlockedBy("has_item", has(Items.IRON_INGOT))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.ORANGE_KITTYPET_BOWL.get(), 1)
                .pattern("   ")
                .pattern("BAB")
                .pattern("BBB")
                .define('A', Items.IRON_INGOT)
                .define('B', Items.ORANGE_TERRACOTTA)
                .unlockedBy("has_item", has(Items.IRON_INGOT))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.MAGENTA_KITTYPET_BOWL.get(), 1)
                .pattern("   ")
                .pattern("BAB")
                .pattern("BBB")
                .define('A', Items.IRON_INGOT)
                .define('B', Items.MAGENTA_TERRACOTTA)
                .unlockedBy("has_item", has(Items.IRON_INGOT))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLUE_KITTYPET_BOWL.get(), 1)
                .pattern("   ")
                .pattern("BAB")
                .pattern("BBB")
                .define('A', Items.IRON_INGOT)
                .define('B', Items.BLUE_TERRACOTTA)
                .unlockedBy("has_item", has(Items.IRON_INGOT))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.YELLOW_KITTYPET_BOWL.get(), 1)
                .pattern("   ")
                .pattern("BAB")
                .pattern("BBB")
                .define('A', Items.IRON_INGOT)
                .define('B', Items.YELLOW_TERRACOTTA)
                .unlockedBy("has_item", has(Items.IRON_INGOT))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.LIME_KITTYPET_BOWL.get(), 1)
                .pattern("   ")
                .pattern("BAB")
                .pattern("BBB")
                .define('A', Items.IRON_INGOT)
                .define('B', Items.LIME_TERRACOTTA)
                .unlockedBy("has_item", has(Items.IRON_INGOT))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.PINK_KITTYPET_BOWL.get(), 1)
                .pattern("   ")
                .pattern("BAB")
                .pattern("BBB")
                .define('A', Items.IRON_INGOT)
                .define('B', Items.PINK_TERRACOTTA)
                .unlockedBy("has_item", has(Items.IRON_INGOT))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLACK_KITTYPET_BOWL.get(), 1)
                .pattern("   ")
                .pattern("BAB")
                .pattern("BBB")
                .define('A', Items.IRON_INGOT)
                .define('B', Items.BLACK_TERRACOTTA)
                .unlockedBy("has_item", has(Items.IRON_INGOT))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.RED_KITTYPET_BOWL.get(), 1)
                .pattern("   ")
                .pattern("BAB")
                .pattern("BBB")
                .define('A', Items.IRON_INGOT)
                .define('B', Items.RED_TERRACOTTA)
                .unlockedBy("has_item", has(Items.IRON_INGOT))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.POPPY_SEEDS.get(), 1)
                .requires(Items.POPPY)
                .unlockedBy("has_item", has(Items.POPPY))
                .save(pWriter);



//
//        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.MOSS_BALL.get(), 2)
//                .requires(Items.MOSS_BLOCK)
//                .unlockedBy("has_item", has(Items.MOSS_BLOCK))
//                .save(pWriter);

    }


}
