package net.snowteb.warriorcats_events.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.block.ModBlocks;
import net.snowteb.warriorcats_events.block.custom.PreyBonesBlock;
import net.snowteb.warriorcats_events.entity.ModEntities;
import net.snowteb.warriorcats_events.item.custom.*;
import net.snowteb.warriorcats_events.util.ItemWithToolTip;
import vazkii.patchouli.api.PatchouliAPI;

import javax.annotation.Nullable;
import java.util.List;


public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(BuiltInRegistries.ITEM, WarriorCatsEvents.MODID);

    public static final DeferredHolder<Item, Item> MOSS_BED_ITEM =
            ITEMS.register("moss_bed",
                    () -> new BlockItem(ModBlocks.MOSS_BED.get(), new Item.Properties()) {
                        @Override
                        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                            tooltipComponents.add(Component.translatable("block.warriorcats_events.moss_bed.tooltip").withStyle(ChatFormatting.GRAY));
                        }
                    });

    public static final DeferredHolder<Item, Item> HAY_BED_ITEM =
            ITEMS.register("hay_bed",
                    () -> new BlockItem(ModBlocks.HAY_BED.get(), new Item.Properties()) {

                        @Override
                        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                            tooltipComponents.add(Component.translatable("block.warriorcats_events.hay_bed.tooltip").withStyle(ChatFormatting.GRAY));
                        }
                    });

    public static final DeferredHolder<Item, Item> KELP_BED_ITEM =
            ITEMS.register("kelp_bed",
                    () -> new BlockItem(ModBlocks.KELP_BED.get(), new Item.Properties()) {
                        @Override
                        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                            tooltipComponents.add(Component.translatable("block.warriorcats_events.kelp_bed.tooltip").withStyle(ChatFormatting.GRAY));
                        }
                    });

    public static final DeferredHolder<Item, Item> STONE_BED_ITEM =
            ITEMS.register("stone_bed",
                    () -> new BlockItem(ModBlocks.STONE_BED.get(), new Item.Properties()) {
                        @Override
                        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                            tooltipComponents.add(Component.translatable("block.warriorcats_events.stone_bed.tooltip").withStyle(ChatFormatting.GRAY));
                        }
                    });

    public static final DeferredHolder<Item, Item> LAVENDER_BED_ITEM =
            ITEMS.register("lavender_bed",
                    () -> new BlockItem(ModBlocks.LAVENDER_BED.get(), new Item.Properties()) {
                        @Override
                        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                            tooltipComponents.add(Component.translatable("block.warriorcats_events.lavender_bed.tooltip").withStyle(ChatFormatting.GRAY));
                        }
                    });

    public static final DeferredHolder<Item, Item> CHERRY_BLOSSOM_BED_ITEM =
            ITEMS.register("cherry_blossom_bed",
                    () -> new BlockItem(ModBlocks.CHERRY_BLOSSOM_BED.get(), new Item.Properties()) {
                        @Override
                        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                            tooltipComponents.add(Component.translatable("block.warriorcats_events.cherry_blossom_bed.tooltip").withStyle(ChatFormatting.GRAY));
                        }
                    });

    public static final DeferredHolder<Item, Item> DRIFTWOOD_BED_ITEM =
            ITEMS.register("driftwood_bed",
                    () -> new BlockItem(ModBlocks.DRIFTWOOD_BED.get(), new Item.Properties()) {
                        @Override
                        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                            tooltipComponents.add(Component.translatable("block.warriorcats_events.driftwood_bed.tooltip").withStyle(ChatFormatting.GRAY));
                        }
                    });

    public static final DeferredHolder<Item, Item> DAISY_BED_ITEM =
            ITEMS.register("daisy_bed",
                    () -> new BlockItem(ModBlocks.DAISY_BED.get(), new Item.Properties()) {
                        @Override
                        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                            tooltipComponents.add(Component.translatable("block.warriorcats_events.daisy_bed.tooltip").withStyle(ChatFormatting.GRAY));
                        }
                    });

    public static final DeferredHolder<Item, Item> ACACIA_BED_ITEM =
            ITEMS.register("acacia_bed",
                    () -> new BlockItem(ModBlocks.ACACIA_BED.get(), new Item.Properties()) {
                        @Override
                        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                            tooltipComponents.add(Component.translatable("block.warriorcats_events.acacia_bed.tooltip").withStyle(ChatFormatting.GRAY));
                        }
                    });

    public static final DeferredHolder<Item, Item> TERRACOTTA_BED_ITEM =
            ITEMS.register("terracotta_bed",
                    () -> new BlockItem(ModBlocks.TERRACOTTA_BED.get(), new Item.Properties()) {
                        @Override
                        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                            tooltipComponents.add(Component.translatable("block.warriorcats_events.terracotta_bed.tooltip").withStyle(ChatFormatting.GRAY));
                        }
                    });

    public static final DeferredHolder<Item, Item> BAMBOO_BED_ITEM =
            ITEMS.register("bamboo_bed",
                    () -> new BlockItem(ModBlocks.BAMBOO_BED.get(), new Item.Properties()) {
                        @Override
                        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                            tooltipComponents.add(Component.translatable("block.warriorcats_events.bamboo_bed.tooltip").withStyle(ChatFormatting.GRAY));
                        }
                    });

    public static final DeferredHolder<Item, Item> BERRY_BED_ITEM =
            ITEMS.register("berry_bed",
                    () -> new BlockItem(ModBlocks.BERRY_BED.get(), new Item.Properties()) {
                        @Override
                        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                            tooltipComponents.add(Component.translatable("block.warriorcats_events.berry_bed.tooltip").withStyle(ChatFormatting.GRAY));
                        }
                    });

    public static final DeferredHolder<Item, Item> CORAL_BED_ITEM =
            ITEMS.register("coral_bed",
                    () -> new BlockItem(ModBlocks.CORAL_BED.get(), new Item.Properties()) {
                        @Override
                        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                            tooltipComponents.add(Component.translatable("block.warriorcats_events.coral_bed.tooltip").withStyle(ChatFormatting.GRAY));
                        }
                    });

    public static final DeferredHolder<Item, Item> GLOWBERRY_BED_ITEM =
            ITEMS.register("glowberry_bed",
                    () -> new BlockItem(ModBlocks.GLOWBERRY_BED.get(), new Item.Properties()) {
                        @Override
                        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                            tooltipComponents.add(Component.translatable("block.warriorcats_events.glowberry_bed.tooltip").withStyle(ChatFormatting.GRAY));
                        }
                    });

    public static final DeferredHolder<Item, Item> MUDDY_BED_ITEM =
            ITEMS.register("muddy_bed",
                    () -> new BlockItem(ModBlocks.MUDDY_BED.get(), new Item.Properties()) {
                        @Override
                        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                            tooltipComponents.add(Component.translatable("block.warriorcats_events.muddy_bed.tooltip").withStyle(ChatFormatting.GRAY));
                        }
                    });

    public static final DeferredHolder<Item, Item> KITTYPET_BED_ITEM =
            ITEMS.register("kittypet_bed",
                    () -> new BlockItem(ModBlocks.KITTYPET_BED.get(), new Item.Properties()) {
                        @Override
                        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                            tooltipComponents.add(Component.translatable("block.warriorcats_events.kittypet_bed.tooltip").withStyle(ChatFormatting.GRAY));
                        }
                    });

    public static final DeferredHolder<Item, Item> ACORN_BED_ITEM =
            ITEMS.register("acorn_bed",
                    () -> new BlockItem(ModBlocks.ACORN_BED.get(), new Item.Properties()) {
                        @Override
                        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                            tooltipComponents.add(Component.translatable("block.warriorcats_events.acorn_bed.tooltip").withStyle(ChatFormatting.GRAY));
                        }
                    });


    public static final DeferredHolder<Item, Item> NAUTILUS_BED_ITEM =
            ITEMS.register("nautilus_bed",
                    () -> new BlockItem(ModBlocks.NAUTILUS_BED.get(), new Item.Properties()) {
                        @Override
                        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                            tooltipComponents.add(Component.translatable("block.warriorcats_events.nautilus_bed.tooltip").withStyle(ChatFormatting.GRAY));
                        }
                    });

    public static final DeferredHolder<Item, Item> SUNFLOWER_BED_ITEM =
            ITEMS.register("sunflower_bed",
                    () -> new BlockItem(ModBlocks.SUNFLOWER_BED.get(), new Item.Properties()) {
                        @Override
                        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                            tooltipComponents.add(Component.translatable("block.warriorcats_events.sunflower_bed.tooltip").withStyle(ChatFormatting.GRAY));
                        }
                    });




    public static final DeferredHolder<Item, Item> LIZARD_EGG_ITEM =
            ITEMS.register("lizard_egg",
                    () -> new BlockItem(ModBlocks.LIZARD_EGG_BLOCK.get(), new Item.Properties()));






    public static final DeferredHolder<Item, Item> MAKESHIFT_BED_ITEM =
            ITEMS.register("makeshift_bed",
                    () -> new BlockItem(ModBlocks.MAKESHIFT_BED.get(), new Item.Properties()));

    public static final DeferredHolder<Item, Item> PREY_BONES_ITEM =
            ITEMS.register("prey_bones",
                    () -> new BlockItem(ModBlocks.PREY_BONES.get(), new Item.Properties()));

    public static final DeferredHolder<Item, Item> PEBBLES_ITEM =
            ITEMS.register("pebbles",
                    () -> new BlockItem(ModBlocks.PEBBLES.get(), new Item.Properties()));

    public static final DeferredHolder<Item, Item> CARVED_STONE_ITEM =
            ITEMS.register("carved_stone",
                    () -> new BlockItem(ModBlocks.CARVED_STONE.get(), new Item.Properties()));


    public static final DeferredHolder<Item, Item> MOSS_BALL =
            ITEMS.register("moss_ball",
                    () -> new MossBallItem(new Item.Properties()));


    public static final DeferredHolder<Item, Item> STONE_CRAFTING_TABLE =
            ITEMS.register("stone_crafting_table",
                    () -> new BlockItem(ModBlocks.STONE_CRAFTING_TABLE.get(), new Item.Properties()) {
                        @Override
                        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                            tooltipComponents.add(Component.translatable("block.warriorcats_events.stone_crafting_table.tooltip").withStyle(ChatFormatting.GRAY));

                        }
                    });

    public static final DeferredHolder<Item, Item> HERB_MIXING_ROCK =
            ITEMS.register("herb_rock",
                    () -> new BlockItem(ModBlocks.HERB_MIXING_ROCK.get(), new Item.Properties()) {
                        @Override
                        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                            tooltipComponents.add(Component.translatable("block.warriorcats_events.herb_rock.tooltip").withStyle(ChatFormatting.GRAY));
                        }
                    });



    public static final DeferredHolder<Item, Item> MOONSTONE_BLOCK_ITEM =
            ITEMS.register("moonstone",
                    () -> new BlockItem(ModBlocks.MOONSTONE_BLOCK.get(), new Item.Properties()) {
                        @Override
                        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                            tooltipComponents.add(Component.translatable("block.warriorcats_events.moonstone.tooltip").withStyle(ChatFormatting.GRAY));
                        }
                    });


    public static final DeferredHolder<Item, Item> GLOWROCKS_ITEM =
            ITEMS.register("glowrocks",
                    () -> new StandingAndWallBlockItem(ModBlocks.GLOWROCKS.get(), ModBlocks.WALL_GLOWROCKS.get(), new Item.Properties(), Direction.DOWN));


    public static final DeferredHolder<Item, Item> GREEN_GLOWROCKS_ITEM =
            ITEMS.register("green_glowrocks",
                    () -> new StandingAndWallBlockItem(ModBlocks.GREEN_GLOWROCKS.get(), ModBlocks.GREEN_WALL_GLOWROCKS.get(), new Item.Properties(), Direction.DOWN));

    public static final DeferredHolder<Item, Item> PINK_GLOWROCKS_ITEM =
            ITEMS.register("pink_glowrocks",
                    () -> new StandingAndWallBlockItem(ModBlocks.PINK_GLOWROCKS.get(), ModBlocks.PINK_WALL_GLOWROCKS.get(), new Item.Properties(), Direction.DOWN));

    public static final DeferredHolder<Item, Item> RED_GLOWROCKS_ITEM =
            ITEMS.register("red_glowrocks",
                    () -> new StandingAndWallBlockItem(ModBlocks.RED_GLOWROCKS.get(), ModBlocks.RED_WALL_GLOWROCKS.get(), new Item.Properties(), Direction.DOWN));

    public static final DeferredHolder<Item, Item> YELLOW_GLOWROCKS_ITEM =
            ITEMS.register("yellow_glowrocks",
                    () -> new StandingAndWallBlockItem(ModBlocks.YELLOW_GLOWROCKS.get(), ModBlocks.YELLOW_WALL_GLOWROCKS.get(), new Item.Properties(), Direction.DOWN));


    public static final DeferredHolder<Item, Item> ACORN_LANTERN_ITEM =
            ITEMS.register("acorn_lantern",
                    () -> new BlockItem(ModBlocks.ACORN_LANTERN.get(), new Item.Properties()));

    public static final DeferredHolder<Item, Item> DAISY_CHAIN_ITEM =
            ITEMS.register("daisy_chain",
                    () -> new BlockItem(ModBlocks.DAISY_CHAIN.get(), new Item.Properties()));

    public static final DeferredHolder<Item, Item> LAVENDER_CHAIN_ITEM =
            ITEMS.register("lavender_chain",
                    () -> new BlockItem(ModBlocks.LAVENDER_CHAIN.get(), new Item.Properties()));


    public static final DeferredHolder<Item, Item> STICKFIRE_ITEM =
            ITEMS.register("stickfire",
                    () -> new BlockItem(ModBlocks.STICKFIRE.get(), new Item.Properties()));


    public static final DeferredHolder<Item, Item> BLUE_MORPHO_WING = ITEMS.register("blue_morpho_wing",
            () -> new ButterflyWingArmorItem());
    public static final DeferredHolder<Item, Item> GOLIATH_BIRDWING_WING = ITEMS.register("goliath_birdwing_wing",
            () -> new ButterflyWingArmorItem());
    public static final DeferredHolder<Item, Item> MONARCH_WING = ITEMS.register("monarch_wing",
            () -> new ButterflyWingArmorItem());
    public static final DeferredHolder<Item, Item> TIGER_SWALLOWTAIL_WING = ITEMS.register("tiger_swallowtail_wing",
            () -> new ButterflyWingArmorItem());







    public static final DeferredHolder<Item, Item> MOUSE_SPAWN_EGG = ITEMS.register("mouse_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.MOUSE,0xAD8A6C, 0xE3AD7D,
                    new Item.Properties()));
    public static final DeferredHolder<Item, Item> SQUIRREL_SPAWN_EGG = ITEMS.register("squirrel_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SQUIRREL,0xF59527, 0xFCC68B,
                    new Item.Properties()));
    public static final DeferredHolder<Item, Item> WILDCAT_SPAWN_EGG = ITEMS.register("wildcat_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.WCAT,0xfcfbe6, 0xf5dfb8,
                    new Item.Properties()));
    public static final DeferredHolder<Item, Item> PIGEON_SPAWN_EGG = ITEMS.register("pigeon_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.PIGEON,0xa8acb3, 0xccc9bc,
                    new Item.Properties()));
    public static final DeferredHolder<Item, Item> BADGER_SPAWN_EGG = ITEMS.register("badger_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.BADGER,0x4a4946, 0xe3e2e1,
                    new Item.Properties()));
    public static final DeferredHolder<Item, Item> GOLDEN_EAGLE_SPAWN_EGG = ITEMS.register("golden_eagle_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.EAGLE,0x33281c, 0x795925,
                    new Item.Properties()));
    public static final DeferredHolder<Item, Item> LIZARD_SPAWN_EGG = ITEMS.register("lizard_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.LIZARD,0xE6A193, 0xFADEB9,
                    new Item.Properties()));



    public static final DeferredHolder<Item, Item> WARRIORS_GUIDE = ITEMS.register("warriors_guide",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
                    if (!level.isClientSide) {
                        PatchouliAPI.get().openBookGUI((ServerPlayer) player,
                                ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "warriors_guide"));
                    }
                    return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
                }

                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("item.warriorcats_events.warriors_guide.tooltip").withStyle(ChatFormatting.GRAY));
                }
            });



public static final DeferredHolder<Item, Item> FLOWER_CROWN = ITEMS.register("flower_crown",
        () -> new FlowerCrownItem());
    public static final DeferredHolder<Item, Item> LEAF_MANE = ITEMS.register("leaf_mane",
            () -> new LeafManeItem());
    public static final DeferredHolder<Item, Item> FLOWER_ARMOR = ITEMS.register("flower_armor",
            () -> new FlowerArmorItem());
    public static final DeferredHolder<Item, Item> TEETH_CLAWS = ITEMS.register("teeth_claws",
            () -> new TeethClawsItem());


    public static final DeferredHolder<Item, Item> BLUE_CAT_SOCKS = ITEMS.register("blue_cat_socks",
            () -> new CatSocksArmorItem());
    public static final DeferredHolder<Item, Item> ORANGE_CAT_SOCKS = ITEMS.register("orange_cat_socks",
            () -> new CatSocksArmorItem());
    public static final DeferredHolder<Item, Item> PINK_CAT_SOCKS = ITEMS.register("pink_cat_socks",
            () -> new CatSocksArmorItem());
    public static final DeferredHolder<Item, Item> WHITE_CAT_SOCKS = ITEMS.register("white_cat_socks",
            () -> new CatSocksArmorItem());
    public static final DeferredHolder<Item, Item> BLACK_CAT_SOCKS = ITEMS.register("black_cat_socks",
            () -> new CatSocksArmorItem());
    public static final DeferredHolder<Item, Item> GREEN_CAT_SOCKS = ITEMS.register("green_cat_socks",
            () -> new CatSocksArmorItem());

    public static final DeferredHolder<Item, Item> HEAD_LEAF = ITEMS.register("head_leaf",
            () -> new PlantHeadArmorItem());
    public static final DeferredHolder<Item, Item> HEAD_FLOWER = ITEMS.register("head_flower",
            () -> new PlantHeadArmorItem());
    public static final DeferredHolder<Item, Item> HEAD_DANDELION = ITEMS.register("head_dandelion",
            () -> new PlantHeadArmorItem());

    public static final DeferredHolder<Item, Item> TAIL_VINES = ITEMS.register("tail_vines",
            () -> new PlantTailArmorItem());
    public static final DeferredHolder<Item, Item> DRAPED_TAIL_VINES = ITEMS.register("draped_tail_vines",
            () -> new DrapedTailVinesArmorItem());


    public static final DeferredHolder<Item, Item> ANCIENT_STICK = ITEMS.register("ancient_stick",
        () -> new AncientStickItem(new Item.Properties().stacksTo(1).durability(550)));

    public static final DeferredHolder<Item, Item> KIT_ITEM = ITEMS.register("kit",
            () -> new KitItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> MYSTIC_FLOWERS_BOUQUET = ITEMS.register("rare_flowers_bouquet",
            () -> new FlowersBouquetItem(new Item.Properties().stacksTo(1),
                    "item.warriorcats_events.rare_flowers_bouquet.tooltip"));
    public static final DeferredHolder<Item, Item> STRANGE_SHINY_STONE = ITEMS.register("strange_shiny_stone",
            () -> new ItemWithToolTip(new Item.Properties().stacksTo(16),
                    "item.warriorcats_events.strange_shiny_stone.tooltip"));


    public static final DeferredHolder<Item, Item> SQUIRREL_SKULL = ITEMS.register("squirrel_skull",
            () -> new PreySkullItem(new Item.Properties().stacksTo(1), PreyBonesBlock.Bones.STAGE_SQUIRREL));
    public static final DeferredHolder<Item, Item> GOLDEN_EAGLE_SKULL = ITEMS.register("golden_eagle_skull",
            () -> new PreySkullItem(new Item.Properties().stacksTo(1), PreyBonesBlock.Bones.STAGE_EAGLE));
    public static final DeferredHolder<Item, Item> BADGER_SKULL = ITEMS.register("badger_skull",
            () -> new PreySkullItem(new Item.Properties().stacksTo(1), PreyBonesBlock.Bones.STAGE_BADGER));



    public static final DeferredHolder<Item, Item> ANIMAL_TOOTH = ITEMS.register("animal_tooth",
            () -> new Item(new Item.Properties().stacksTo(16)));
    public static final DeferredHolder<Item, Item> ANIMAL_TEETH = ITEMS.register("animal_teeth",
            () -> new Item(new Item.Properties().stacksTo(16)));

    public static final DeferredHolder<Item, Item> WARRIOR_NAMETAG = ITEMS.register("warrior_nametag",
            () -> new WarriorNametag(new Item.Properties().stacksTo(8)));


    public static final DeferredHolder<Item, Item> DOCK_LEAVES = ITEMS.register("dock_leaves",
            () -> new ItemWithToolTip(new Item.Properties(), "item.warriorcats_events.dock_leaves.tooltip"));
    public static final DeferredHolder<Item, Item> SORREL = ITEMS.register("sorrel_leaves",
            () -> new ItemWithToolTip(new Item.Properties().food(ModFoodHerbs.SORREL),
                    "item.warriorcats_events.sorrel_leaves.tooltip"));
    public static final DeferredHolder<Item, Item> BURNET = ITEMS.register("burnet_leaves",
            () -> new ItemWithToolTip(new Item.Properties().food(ModFoodHerbs.BURNET),
                    "item.warriorcats_events.burnet_leaves.tooltip"));
    public static final DeferredHolder<Item, Item> CHAMOMILE = ITEMS.register("chamomile",
            () -> new ItemWithToolTip(new Item.Properties().food(ModFoodHerbs.CHAMOMILE),
                    "item.warriorcats_events.chamomile.tooltip"));
    public static final DeferredHolder<Item, Item> DAISY = ITEMS.register("daisy_leaves",
            () -> new ItemWithToolTip(new Item.Properties().food(ModFoodHerbs.DAISY),
                    "item.warriorcats_events.daisy_leaves.tooltip"));
    public static final DeferredHolder<Item, Item> DEATHBERRIES = ITEMS.register("deathberries",
            () -> new ItemWithToolTip(new Item.Properties().food(ModFoodHerbs.DEATHBERRIES),
                    "item.warriorcats_events.deathberries.tooltip"));
    public static final DeferredHolder<Item, Item> CATMINT = ITEMS.register("catmint_leaves",
            () -> new ItemWithToolTip(new Item.Properties().food(ModFoodHerbs.CATMINT),
                    "item.warriorcats_events.catmint.tooltip"));
    public static final DeferredHolder<Item, Item> YARROW = ITEMS.register("yarrow_leaves",
            () -> new ItemWithToolTip(new Item.Properties().food(ModFoodHerbs.YARROW),
                    "item.warriorcats_events.yarrow.tooltip"));
    public static final DeferredHolder<Item, Item> FEVERFEW = ITEMS.register("feverfew_leaves",
            () -> new ItemWithToolTip(new Item.Properties().food(ModFoodHerbs.FEVERFEW),
                    "item.warriorcats_events.feverfew.tooltip"));
    public static final DeferredHolder<Item, Item> JUNIPER_BERRIES = ITEMS.register("juniper_berries",
            () -> new ItemWithToolTip(new Item.Properties().food(ModFoodHerbs.JUNIPER),
                    "item.warriorcats_events.juniper.tooltip"));
    public static final DeferredHolder<Item, Item> POPPY_SEEDS = ITEMS.register("poppy_seeds",
            () -> new ItemWithToolTip(new Item.Properties().food(ModFoodHerbs.POPPY_SEEDS),
                    "item.warriorcats_events.poppy_seeds.tooltip"));
    public static final DeferredHolder<Item, Item> COMFREY_ROOT = ITEMS.register("comfrey_root",
            () -> new ItemWithToolTip(new Item.Properties(),
                    "item.warriorcats_events.comfrey_root.tooltip"));
    public static final DeferredHolder<Item, Item> COMFREY_LEAVES = ITEMS.register("comfrey_leaves",
            () -> new ItemWithToolTip(new Item.Properties(),
                    "item.warriorcats_events.comfrey_leaves.tooltip"));


    public static final DeferredHolder<Item, Item> TRAVELING_HERBS = ITEMS.register("traveling_herbs",
            () -> new ItemWithToolTip(new Item.Properties().food(ModFoodHerbs.TRAVELING_HERBS),
            "item.warriorcats_events.traveling_herbs.tooltip"));




    public static final DeferredHolder<Item, Item> DOCK_POULTICE = ITEMS.register("dock_poultice",
            () -> new DockPoultice(new Item.Properties().stacksTo(16)));
    public static final DeferredHolder<Item, Item> YARROW_POULTICE = ITEMS.register("yarrow_poultice",
            () -> new YarrowPoultice(new Item.Properties().stacksTo(16)));
    public static final DeferredHolder<Item, Item> COMFREY_POULTICE = ITEMS.register("comfrey_poultice",
            () -> new ComfreyPoultice(new Item.Properties().stacksTo(16)));

    public static final DeferredHolder<Item, Item> COBWEB_WITH_A_STICK = ITEMS.register("cobweb_on_a_stick",
            () -> new ItemWithToolTip(new Item.Properties().stacksTo(1),
                    "item.warriorcats_events.cobweb_on_a_stick.tooltip"));


    public static final DeferredHolder<Item, Item> MOUSE_FOOD = ITEMS.register("mouse",
            () -> new Item(new Item.Properties().food(ModFoodHerbs.MOUSE_FOOD)));
    public static final DeferredHolder<Item, Item> SQUIRREL_FOOD = ITEMS.register("squirrel",
            () -> new Item(new Item.Properties().food(ModFoodHerbs.SQUIRREL_FOOD)));
    public static final DeferredHolder<Item, Item> PIGEON_FOOD = ITEMS.register("pigeon",
            () -> new Item(new Item.Properties().food(ModFoodHerbs.PIGEON_FOOD)));
    public static final DeferredHolder<Item, Item> EAGLE_MEAT_FOOD = ITEMS.register("golden_eagle_meat",
            () -> new Item(new Item.Properties().food(ModFoodHerbs.EAGLE_MEAT_FOOD)));
    public static final DeferredHolder<Item, Item> SHREDDED_MEAT = ITEMS.register("shredded_meat",
            () -> new Item(new Item.Properties().food(ModFoodHerbs.SHREDDED_MEAT)));
    public static final DeferredHolder<Item, Item> FROG = ITEMS.register("frog",
            () -> new Item(new Item.Properties().food(ModFoodHerbs.FROG)));

    public static final DeferredHolder<Item, Item> SUS_MOUSE_FOOD = ITEMS.register("suspicious_mouse",
            () -> new SuspiciousFoodItem(new Item.Properties().food(ModFoodHerbs.MOUSE_FOOD)));
    public static final DeferredHolder<Item, Item> SUS_SQUIRREL_FOOD = ITEMS.register("suspicious_squirrel",
            () -> new SuspiciousFoodItem(new Item.Properties().food(ModFoodHerbs.SQUIRREL_FOOD)));
    public static final DeferredHolder<Item, Item> SUS_PIGEON_FOOD = ITEMS.register("suspicious_pigeon",
            () -> new SuspiciousFoodItem(new Item.Properties().food(ModFoodHerbs.PIGEON_FOOD)));
    public static final DeferredHolder<Item, Item> SUS_EAGLE_MEAT_FOOD = ITEMS.register("suspicious_golden_eagle_meat",
            () -> new SuspiciousFoodItem(new Item.Properties().food(ModFoodHerbs.EAGLE_MEAT_FOOD)));
    public static final DeferredHolder<Item, Item> SUS_SHREDDED_MEAT = ITEMS.register("suspicious_shredded_meat",
            () -> new SuspiciousFoodItem(new Item.Properties().food(ModFoodHerbs.SHREDDED_MEAT)));
    public static final DeferredHolder<Item, Item> SUS_FROG = ITEMS.register("suspicious_frog",
            () -> new SuspiciousFoodItem(new Item.Properties().food(ModFoodHerbs.FROG)));

    public static final DeferredHolder<Item, Item> WHISKERS = ITEMS.register("whiskers",
            () -> new WhiskersItem(new Item.Properties().stacksTo(1).durability(400),
                    "item.warriorcats_events.whiskers.tooltip"));

    public static final DeferredHolder<Item, Item> CLAWS = ITEMS.register("claws",
            () -> new ClawsTooltip(new Item.Properties().stacksTo(1).durability(260),
                    "item.warriorcats_events.claws.tooltip"));

    public static final DeferredHolder<Item, Item> WARRIORNAMERANDOMIZER = ITEMS.register("warrior_name_randomizer",
            () -> new ItemWithToolTip(new Item.Properties().stacksTo(1).durability(32),
                    "item.warriorcats_events.warrior_name_randomizer.tooltip"));

    public static final DeferredHolder<Item, Item> FRESHKILL_AND_HERBS_BUNDLE = ITEMS.register("freshkill_and_herbs",
            () -> new ItemWithToolTip(new Item.Properties(),
                    "item.warriorcats_events.freshkill_and_herbs.tooltip"));

    public static final DeferredHolder<Item, Item> LEG_WRAP = ITEMS.register("leg_wrap",
            () -> new LegWrapItem("item.warriorcats_events.leg_wrap.tooltip"));


    public static final DeferredHolder<Item, Item> DOCK = ITEMS.register("dock",
            () -> new BlockItem(ModBlocks.DOCK.get(), new Item.Properties()));
    public static final DeferredHolder<Item, Item> SORRELPLANT = ITEMS.register("sorrel",
            () -> new BlockItem(ModBlocks.SORRELPLANT.get(), new Item.Properties()));
    public static final DeferredHolder<Item, Item> BURNETPLANT = ITEMS.register("burnet",
            () -> new BlockItem(ModBlocks.BURNETPLANT.get(), new Item.Properties()));
    public static final DeferredHolder<Item, Item> CHAMOMILEPLANT = ITEMS.register("chamomile_stems",
            () -> new BlockItem(ModBlocks.CHAMOMILEPLANT.get(), new Item.Properties()));
    public static final DeferredHolder<Item, Item> DAISYPLANT = ITEMS.register("daisy",
            () -> new BlockItem(ModBlocks.DAISYPLANT.get(), new Item.Properties()));
    public static final DeferredHolder<Item, Item> DEATHBERRIESBUSH = ITEMS.register("deathberries_bush",
            () -> new BlockItem(ModBlocks.DEATHBERRIESBUSH.get(), new Item.Properties()));
    public static final DeferredHolder<Item, Item> CATMINTPLANT = ITEMS.register("catmint",
            () -> new BlockItem(ModBlocks.CATMINTPLANT.get(), new Item.Properties()));
    public static final DeferredHolder<Item, Item> YARROWPLANT = ITEMS.register("yarrow",
            () -> new BlockItem(ModBlocks.YARROWPLANT.get(), new Item.Properties()));
    public static final DeferredHolder<Item, Item> FEVERFEWPLANT = ITEMS.register("feverfew",
            () -> new BlockItem(ModBlocks.FEVERFEWPLANT.get(), new Item.Properties()));
    public static final DeferredHolder<Item, Item> JUNIPERPLANT = ITEMS.register("juniper",
            () -> new BlockItem(ModBlocks.JUNIPERPLANT.get(), new Item.Properties()));
    public static final DeferredHolder<Item, Item> COMFREYPLANT = ITEMS.register("comfrey",
            () -> new BlockItem(ModBlocks.COMFREYPLANT.get(), new Item.Properties()));


    public static final DeferredHolder<Item, Item> GLOW_SHROOM = ITEMS.register("glowshroom",
            () -> new BlockItem(ModBlocks.GLOWSHROOM.get(), new Item.Properties().stacksTo(16)));


    public static final DeferredHolder<Item, Item> GREEN_PARROT_FEATHER = ITEMS.register("green_parrot_feather",
            () -> new Item(new Item.Properties().stacksTo(16)));
    public static final DeferredHolder<Item, Item> RED_PARROT_FEATHER = ITEMS.register("red_parrot_feather",
            () -> new Item(new Item.Properties().stacksTo(16)));
    public static final DeferredHolder<Item, Item> BLUE_PARROT_FEATHER = ITEMS.register("blue_parrot_feather",
            () -> new Item(new Item.Properties().stacksTo(16)));
    public static final DeferredHolder<Item, Item> LIGHTBLUE_PARROT_FEATHER = ITEMS.register("lightblue_parrot_feather",
            () -> new Item(new Item.Properties().stacksTo(16)));
    public static final DeferredHolder<Item, Item> GRAY_PARROT_FEATHER = ITEMS.register("gray_parrot_feather",
            () -> new Item(new Item.Properties().stacksTo(16)));

    public static final DeferredHolder<Item, Item> BLACK_VULTURE_FEATHER = ITEMS.register("black_vulture_feather",
            () -> new Item(new Item.Properties().stacksTo(16)));
    public static final DeferredHolder<Item, Item> PIGEON_FEATHER = ITEMS.register("pigeon_feather",
            () -> new Item(new Item.Properties().stacksTo(16)));
    public static final DeferredHolder<Item, Item> CROW_FEATHER = ITEMS.register("crow_feather",
            () -> new Item(new Item.Properties().stacksTo(16)));
    public static final DeferredHolder<Item, Item> GOLDFINCH_FEATHER = ITEMS.register("goldfinch_feather",
            () -> new Item(new Item.Properties().stacksTo(16)));
    public static final DeferredHolder<Item, Item> CARDINAL_FEATHER = ITEMS.register("cardinal_feather",
            () -> new Item(new Item.Properties().stacksTo(16)));

    public static final DeferredHolder<Item, Item> GREEN_PARROT_BODY_FEATHERS = ITEMS.register("green_parrot_body_feathers",
            () -> new FeathersArmorItem());
    public static final DeferredHolder<Item, Item> RED_PARROT_BODY_FEATHERS = ITEMS.register("red_parrot_body_feathers",
            () -> new FeathersArmorItem());
    public static final DeferredHolder<Item, Item> BLUE_PARROT_BODY_FEATHERS = ITEMS.register("blue_parrot_body_feathers",
            () -> new FeathersArmorItem());
    public static final DeferredHolder<Item, Item> LIGHTBLUE_PARROT_BODY_FEATHERS = ITEMS.register("lightblue_parrot_body_feathers",
            () -> new FeathersArmorItem());
    public static final DeferredHolder<Item, Item> GRAY_PARROT_BODY_FEATHERS = ITEMS.register("gray_parrot_body_feathers",
            () -> new FeathersArmorItem());
    public static final DeferredHolder<Item, Item> CHICKEN_BODY_FEATHERS = ITEMS.register("chicken_body_feathers",
            () -> new FeathersArmorItem());


    public static final DeferredHolder<Item, Item> VULTURE_BODY_FEATHERS = ITEMS.register("vulture_body_feathers",
            () -> new FeathersArmorItem());
    public static final DeferredHolder<Item, Item> PIGEON_BODY_FEATHERS = ITEMS.register("pigeon_body_feathers",
            () -> new FeathersArmorItem());
    public static final DeferredHolder<Item, Item> CROW_BODY_FEATHERS = ITEMS.register("crow_body_feathers",
            () -> new FeathersArmorItem());
    public static final DeferredHolder<Item, Item> GOLDFINCH_BODY_FEATHERS = ITEMS.register("goldfinch_body_feathers",
            () -> new FeathersArmorItem());
    public static final DeferredHolder<Item, Item> CARDINAL_BODY_FEATHERS = ITEMS.register("cardinal_body_feathers",
            () -> new FeathersArmorItem());


    public static final DeferredHolder<Item, Item> CAT_COLLAR = ITEMS.register("cat_collar",
            () -> new CollarArmorItem());

    public static final DeferredHolder<Item, Item> COLLAR_BELL = ITEMS.register("collar_bell",
            () -> new Item(new Item.Properties().stacksTo(16)));

    public static final DeferredHolder<Item, Item> HEAD_GLOWBERRY = ITEMS.register("head_glowberry",
            () -> new PlantHeadArmorItem());
    public static final DeferredHolder<Item, Item> HEAD_SWEETBERRY = ITEMS.register("head_sweetberry",
            () -> new PlantHeadArmorItem());

    public static final DeferredHolder<Item, Item> CAT_HAT = ITEMS.register("cat_hat",
            () -> new FlowerCrownItem());

    public static final DeferredHolder<Item, Item> CAT_PINK_BOW = ITEMS.register("head_pink_bow",
            () -> new FlowerCrownItem());
    public static final DeferredHolder<Item, Item> CAT_RED_BOW = ITEMS.register("head_red_bow",
            () -> new FlowerCrownItem());
    public static final DeferredHolder<Item, Item> CAT_BLACK_BOW = ITEMS.register("black_bow",
            () -> new FlowerArmorItem());

    public static final DeferredHolder<Item, Item> SKULL_MASK = ITEMS.register("skull_mask",
            () -> new BoneHelmetItem());


    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }

}