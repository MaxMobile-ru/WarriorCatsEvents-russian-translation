package net.snowteb.warriorcats_events.block;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.block.custom.*;
import net.snowteb.warriorcats_events.util.ModBlockSetTypes;
import net.snowteb.warriorcats_events.item.ModItems;
import net.snowteb.warriorcats_events.util.ModSoundTypes;

import java.util.List;
import java.util.function.Supplier;

import static net.snowteb.warriorcats_events.block.custom.GlowrocksBlock.lightLevel;

public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(WarriorCatsEvents.MODID);

    public static final DeferredHolder<Block, Block> MOSS_BED = BLOCKS.register("moss_bed",
                    () -> new NestBlock(
                            BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).sound(SoundType.MOSS).strength(0.2F).noOcclusion()
                    ));
    public static final DeferredHolder<Block, Block> HAY_BED = BLOCKS.register("hay_bed",
            () -> new NestBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).sound(SoundType.MOSS).strength(0.2F).noOcclusion()
            ));
    public static final DeferredHolder<Block, Block> KELP_BED = BLOCKS.register("kelp_bed",
            () -> new NestBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).sound(SoundType.MOSS).strength(0.2F).noOcclusion()
            ));

    public static final DeferredHolder<Block, Block> STONE_BED = BLOCKS.register("stone_bed",
            () -> new NestBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).sound(SoundType.STONE).strength(0.4F).noOcclusion()
            ));

    public static final DeferredHolder<Block, Block> LAVENDER_BED = BLOCKS.register("lavender_bed",
            () -> new NestBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).sound(SoundType.MOSS).strength(0.2F).noOcclusion()
            ));

    public static final DeferredHolder<Block, Block> CHERRY_BLOSSOM_BED = BLOCKS.register("cherry_blossom_bed",
            () -> new NestBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).sound(SoundType.MOSS).strength(0.2F).noOcclusion()
            ));

    public static final DeferredHolder<Block, Block> DRIFTWOOD_BED = BLOCKS.register("driftwood_bed",
            () -> new NestBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).sound(SoundType.BAMBOO_WOOD).strength(0.2F).noOcclusion()
            ));

    public static final DeferredHolder<Block, Block> DAISY_BED = BLOCKS.register("daisy_bed",
            () -> new NestBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).sound(SoundType.MOSS).strength(0.2F).noOcclusion()
            ));

    public static final DeferredHolder<Block, Block> ACACIA_BED = BLOCKS.register("acacia_bed",
            () -> new NestBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).sound(SoundType.MOSS).strength(0.2F).noOcclusion()
            ));

    public static final DeferredHolder<Block, Block> TERRACOTTA_BED = BLOCKS.register("terracotta_bed",
            () -> new NestBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).sound(SoundType.STONE).strength(0.2F).noOcclusion()
            ));

    public static final DeferredHolder<Block, Block> BAMBOO_BED = BLOCKS.register("bamboo_bed",
            () -> new NestBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).sound(SoundType.BAMBOO).strength(0.2F).noOcclusion()
            ));

    public static final DeferredHolder<Block, Block> BERRY_BED = BLOCKS.register("berry_bed",
            () -> new NestBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).sound(SoundType.MOSS).strength(0.2F).noOcclusion()
            ));

    public static final DeferredHolder<Block, Block> CORAL_BED = BLOCKS.register("coral_bed",
            () -> new NestBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).sound(SoundType.CORAL_BLOCK).strength(0.2F).noOcclusion()
            ));

    public static final DeferredHolder<Block, Block> GLOWBERRY_BED = BLOCKS.register("glowberry_bed",
            () -> new NestBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).sound(SoundType.MOSS).strength(0.2F).noOcclusion()
                            .lightLevel(state -> 4)
            ));

    public static final DeferredHolder<Block, Block> MUDDY_BED = BLOCKS.register("muddy_bed",
            () -> new NestBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).sound(SoundType.MUD).strength(0.2F).noOcclusion()
            ));

    public static final DeferredHolder<Block, Block> KITTYPET_BED = BLOCKS.register("kittypet_bed",
            () -> new KittypetNestBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).sound(SoundType.BAMBOO_WOOD).strength(0.4F).noOcclusion()
            ));

    public static final DeferredHolder<Block, Block> ACORN_BED = BLOCKS.register("acorn_bed",
            () -> new NestBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).sound(SoundType.MOSS).strength(0.2F).noOcclusion()
            ));

    public static final DeferredHolder<Block, Block> NAUTILUS_BED = BLOCKS.register("nautilus_bed",
            () -> new NautilusNestBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).sound(SoundType.CORAL_BLOCK).strength(0.4F).noOcclusion()
            ));

    public static final DeferredHolder<Block, Block> SUNFLOWER_BED = BLOCKS.register("sunflower_bed",
            () -> new NestBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).sound(SoundType.MOSS).strength(0.2F).noOcclusion()
            ));



    public static final DeferredHolder<Block, Block> LIZARD_EGG_BLOCK = BLOCKS.register("lizard_egg",
            () -> new LizardEggBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TURTLE_EGG)));




    public static final DeferredHolder<Block, Block> MOONSTONE_BLOCK = BLOCKS.register("moonstone",
            () -> new MoonstoneBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .lightLevel(state -> 10).strength(0.5F).noOcclusion()
            ));


    public static final DeferredHolder<Block, Block> GLOWROCKS = BLOCKS.register("glowrocks",
            () -> new GlowrocksBlock(BlockBehaviour.Properties.of().noCollission().strength(0.5f)
                    .lightLevel(state -> lightLevel(6, state, GlowrocksBlock.AMOUNT_G, 14)).sound(SoundType.STONE)
                    .pushReaction(PushReaction.DESTROY), ParticleTypes.END_ROD));

    public static final DeferredHolder<Block, Block> WALL_GLOWROCKS = BLOCKS.register("wall_glowrocks",
            () -> new WallGlowrocksBlock(BlockBehaviour.Properties.of().noCollission().strength(0.5f)
                    .lightLevel((state) -> lightLevel(4, state, WallGlowrocksBlock.AMOUNT_WG, 14)).sound(SoundType.STONE)
                    .pushReaction(PushReaction.DESTROY), ParticleTypes.END_ROD));


    public static final DeferredHolder<Block, Block> GREEN_GLOWROCKS = BLOCKS.register("green_glowrocks",
            () -> new GlowrocksBlock(BlockBehaviour.Properties.of().strength(0.5f)
                    .lightLevel((state) -> lightLevel(6, state, GlowrocksBlock.AMOUNT_G, 13)).sound(SoundType.STONE)
                    .pushReaction(PushReaction.DESTROY), ParticleTypes.END_ROD));

    public static final DeferredHolder<Block, Block> GREEN_WALL_GLOWROCKS = BLOCKS.register("green_wall_glowrocks",
            () -> new WallGlowrocksBlock(BlockBehaviour.Properties.of().strength(0.5f)
                    .lightLevel((state) -> lightLevel(4, state, WallGlowrocksBlock.AMOUNT_WG, 13)).sound(SoundType.STONE)
                    .pushReaction(PushReaction.DESTROY), ParticleTypes.END_ROD));


    public static final DeferredHolder<Block, Block> PINK_GLOWROCKS = BLOCKS.register("pink_glowrocks",
            () -> new GlowrocksBlock(BlockBehaviour.Properties.of().strength(0.5f)
                    .lightLevel((state) -> lightLevel(6, state, GlowrocksBlock.AMOUNT_G, 13)).sound(SoundType.STONE)
                    .pushReaction(PushReaction.DESTROY), ParticleTypes.END_ROD));

    public static final DeferredHolder<Block, Block> PINK_WALL_GLOWROCKS = BLOCKS.register("pink_wall_glowrocks",
            () -> new WallGlowrocksBlock(BlockBehaviour.Properties.of().strength(0.5f)
                    .lightLevel((state) -> lightLevel(4, state, WallGlowrocksBlock.AMOUNT_WG, 13)).sound(SoundType.STONE)
                    .pushReaction(PushReaction.DESTROY), ParticleTypes.END_ROD));


    public static final DeferredHolder<Block, Block> RED_GLOWROCKS = BLOCKS.register("red_glowrocks",
            () -> new GlowrocksBlock(BlockBehaviour.Properties.of().strength(0.5f)
                    .lightLevel((state) -> lightLevel(6, state, GlowrocksBlock.AMOUNT_G, 10)).sound(SoundType.STONE)
                    .pushReaction(PushReaction.DESTROY), ParticleTypes.END_ROD));

    public static final DeferredHolder<Block, Block> RED_WALL_GLOWROCKS = BLOCKS.register("red_wall_glowrocks",
            () -> new WallGlowrocksBlock(BlockBehaviour.Properties.of().strength(0.5f)
                    .lightLevel((state) -> lightLevel(4, state, WallGlowrocksBlock.AMOUNT_WG, 10)).sound(SoundType.STONE)
                    .pushReaction(PushReaction.DESTROY), ParticleTypes.END_ROD));


    public static final DeferredHolder<Block, Block> YELLOW_GLOWROCKS = BLOCKS.register("yellow_glowrocks",
            () -> new GlowrocksBlock(BlockBehaviour.Properties.of().strength(0.5f)
                    .lightLevel((state) -> lightLevel(6, state, GlowrocksBlock.AMOUNT_G, 12)).sound(SoundType.STONE)
                    .pushReaction(PushReaction.DESTROY), ParticleTypes.END_ROD));

    public static final DeferredHolder<Block, Block> YELLOW_WALL_GLOWROCKS = BLOCKS.register("yellow_wall_glowrocks",
            () -> new WallGlowrocksBlock(BlockBehaviour.Properties.of().strength(0.5f)
                    .lightLevel((state) -> lightLevel(4, state, WallGlowrocksBlock.AMOUNT_WG, 12)).sound(SoundType.STONE)
                    .pushReaction(PushReaction.DESTROY), ParticleTypes.END_ROD));
    
    

    public static final DeferredHolder<Block, Block> ACORN_LANTERN = BLOCKS.register("acorn_lantern",
            () -> new AcornLanternBlock(BlockBehaviour.Properties.of().forceSolidOn().strength(1.0F).sound(SoundType.LANTERN)
                    .lightLevel((state) -> 15).noOcclusion().pushReaction(PushReaction.DESTROY)));


    public static final DeferredHolder<Block, Block> DAISY_CHAIN = BLOCKS.register("daisy_chain",
            () -> new WallDecorationBlock(BlockBehaviour.Properties.of().noCollission().instabreak()
                    .sound(SoundType.CHERRY_LEAVES).pushReaction(PushReaction.DESTROY)));

    public static final DeferredHolder<Block, Block> LAVENDER_CHAIN = BLOCKS.register("lavender_chain",
            () -> new WallDecorationBlock(BlockBehaviour.Properties.of().noCollission().instabreak()
                    .sound(SoundType.CHERRY_LEAVES).pushReaction(PushReaction.DESTROY)));


    public static final DeferredHolder<Block, Block> STICKFIRE = BLOCKS.register("stickfire",
            () -> new StickfireBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAMPFIRE).noOcclusion()));





    public static final DeferredHolder<Block, Block> MAKESHIFT_BED = BLOCKS.register("makeshift_bed",
            () -> new MakeshiftBedBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).sound(SoundType.MOSS).strength(0.0F).noOcclusion()
            ));

    public static final DeferredHolder<Block, Block> PREY_BONES = BLOCKS.register("prey_bones",
            () -> new PreyBonesBlock(
                    BlockBehaviour.Properties.of().sound(ModSoundTypes.PREY_BONES).strength(0.0F).noOcclusion()
            ));

    public static final DeferredHolder<Block, Block> PEBBLES = BLOCKS.register("pebbles",
            () -> new PebblesBlock(
                    BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(0.25F).noOcclusion()
            ));


    public static final DeferredHolder<Block, Block> STONE_CRAFTING_TABLE = BLOCKS.register("stone_crafting_table",
            ()  -> new StoneCraftingTable(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).strength(0.2F).noOcclusion()));

    public static final DeferredHolder<Block, Block> DOCK = BLOCKS.register("dock",
            () -> new GenericBushBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.SWEET_BERRY_BUSH).randomTicks(),
                    List.of(new GenericBushBlock.DropItem(ModItems.DOCK_LEAVES, 1f)),
                    1,
                    3,
                    SoundEvents.AZALEA_LEAVES_FALL
            ));
    public static final DeferredHolder<Block, Block> SORRELPLANT = BLOCKS.register("sorrel",
            () -> new GenericBushBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.SWEET_BERRY_BUSH).randomTicks(),
                    List.of(new GenericBushBlock.DropItem(ModItems.SORREL, 1f)),
                    1,
                    3,
                    SoundEvents.AZALEA_LEAVES_FALL
            ));
    public static final DeferredHolder<Block, Block> BURNETPLANT = BLOCKS.register("burnet",
            () -> new GenericBushBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.SWEET_BERRY_BUSH).randomTicks(),
                    List.of(new GenericBushBlock.DropItem(ModItems.BURNET, 1f)),
                    1,
                    3,
                    SoundEvents.AZALEA_LEAVES_FALL
            ));
    public static final DeferredHolder<Block, Block> CHAMOMILEPLANT = BLOCKS.register("chamomile_stems",
            () -> new GenericBushBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.SWEET_BERRY_BUSH).randomTicks(),
                    List.of(new GenericBushBlock.DropItem(ModItems.CHAMOMILE, 1f)),
                    1,
                    3,
                    SoundEvents.AZALEA_LEAVES_FALL
            ));
    public static final DeferredHolder<Block, Block> DAISYPLANT = BLOCKS.register("daisy",
            () -> new GenericBushBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.SWEET_BERRY_BUSH).randomTicks(),
                    List.of(new GenericBushBlock.DropItem(ModItems.DAISY, 1f)),
                    1,
                    3,
                    SoundEvents.AZALEA_LEAVES_FALL
            ));
    public static final DeferredHolder<Block, Block> CATMINTPLANT = BLOCKS.register("catmint",
            () -> new GenericBushBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.SWEET_BERRY_BUSH).randomTicks(),
                    List.of(new GenericBushBlock.DropItem(ModItems.CATMINT, 1f)),
                    1,
                    3,
                    SoundEvents.AZALEA_LEAVES_FALL
            ));
    public static final DeferredHolder<Block, Block> DEATHBERRIESBUSH = BLOCKS.register("deathberries_bush",
            () -> new GenericBushBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.SWEET_BERRY_BUSH).randomTicks(),
                    List.of(new GenericBushBlock.DropItem(ModItems.DEATHBERRIES, 1f)),
                    1,
                    2,
                    SoundEvents.AZALEA_LEAVES_FALL
            ));
    public static final DeferredHolder<Block, Block> YARROWPLANT = BLOCKS.register("yarrow",
            () -> new GenericBushBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.SWEET_BERRY_BUSH).randomTicks(),
                    List.of(new GenericBushBlock.DropItem(ModItems.YARROW, 1f)),
                    1,
                    2,
                    SoundEvents.AZALEA_LEAVES_FALL
            ));

    public static final DeferredHolder<Block, Block> FEVERFEWPLANT = BLOCKS.register("feverfew",
            () -> new GenericBushBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.SWEET_BERRY_BUSH).randomTicks(),
                    List.of(new GenericBushBlock.DropItem(ModItems.FEVERFEW, 1f)),
                    1,
                    2,
                    SoundEvents.AZALEA_LEAVES_FALL
            ));

    public static final DeferredHolder<Block, Block> JUNIPERPLANT = BLOCKS.register("juniper",
            () -> new BigBushBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.SWEET_BERRY_BUSH).randomTicks(),
                    ModItems.JUNIPER_BERRIES,
                    1,
                    2,
                    SoundEvents.AZALEA_LEAVES_FALL
            ));

    public static final DeferredHolder<Block, Block> COMFREYPLANT = BLOCKS.register("comfrey",
            () -> new GenericBushBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.SWEET_BERRY_BUSH).randomTicks(),
                    List.of(new GenericBushBlock.DropItem(ModItems.COMFREY_LEAVES, 1f),
                            new GenericBushBlock.DropItem(ModItems.COMFREY_ROOT, 0.25f)),
                    1,
                    2,
                    SoundEvents.AZALEA_LEAVES_FALL
            ));



    public static final DeferredHolder<Block, Block> LEAF_DOOR = registerBlock("leaf_door",
            () -> new LeafDoorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_DOOR).sound(SoundType.CHERRY_LEAVES).noOcclusion(),
                    ModBlockSetTypes.LEAF));

    public static final DeferredHolder<Block, Block> TREE_STUMP = registerBlock("tree_stump",
            () -> new TreeStumpBlock(BlockBehaviour.Properties.of().noOcclusion().sound(SoundType.WOOD)));

    public static final DeferredHolder<Block, Block> LEAF_TRAPDOOR = registerBlock("leaf_trapdoor",
            () -> new LeafTrapdoorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_DOOR).sound(SoundType.CHERRY_LEAVES).noOcclusion(),
                    ModBlockSetTypes.LEAF));

    public static final DeferredHolder<Block, Block> GLOWSHROOM = BLOCKS.register("glowshroom", () -> new GlowshroomBlock(BlockBehaviour.Properties.of()));


    public static final DeferredHolder<Block, Block> STONECLEFT = registerBlock("stonecleft",
            () -> new StoneCleftBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion().requiresCorrectToolForDrops()));

    public static final DeferredHolder<Block, Block> FRESHKILL_PILE = registerBlock("fresh_kill_pile",
            () -> new FreshkillPileBlock(BlockBehaviour.Properties.of()));

    public static  final DeferredBlock<DoublePlantBlock> LAVENDER = registerBlock("lavender",
            () -> new DoublePlantBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.ROSE_BUSH)));

    public static  final DeferredBlock<LavenderPetalsBlock> LAVENDER_PETALS = registerBlock("lavender_petals",
            () -> new LavenderPetalsBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.PINK_PETALS)));




    // KITTYPET BOWLS

    public static final DeferredBlock<KittyPetBowl> WHITE_KITTYPET_BOWL = registerBlock("white_kittypet_bowl",
            () -> new KittyPetBowl(BlockBehaviour.Properties.of().strength(0.5f)));

    public static final DeferredBlock<KittyPetBowl> ORANGE_KITTYPET_BOWL = registerBlock("orange_kittypet_bowl",
            () -> new KittyPetBowl(BlockBehaviour.Properties.of().strength(0.5f)));

    public static final DeferredBlock<KittyPetBowl> MAGENTA_KITTYPET_BOWL = registerBlock("magenta_kittypet_bowl",
            () -> new KittyPetBowl(BlockBehaviour.Properties.of().strength(0.5f)));

    public static final DeferredBlock<KittyPetBowl> BLUE_KITTYPET_BOWL = registerBlock("blue_kittypet_bowl",
            () -> new KittyPetBowl(BlockBehaviour.Properties.of().strength(0.5f)));

    public static final DeferredBlock<KittyPetBowl> YELLOW_KITTYPET_BOWL = registerBlock("yellow_kittypet_bowl",
            () -> new KittyPetBowl(BlockBehaviour.Properties.of().strength(0.5f)));

    public static final DeferredBlock<KittyPetBowl> LIME_KITTYPET_BOWL = registerBlock("lime_kittypet_bowl",
            () -> new KittyPetBowl(BlockBehaviour.Properties.of().strength(0.5f)));

    public static final DeferredBlock<KittyPetBowl> PINK_KITTYPET_BOWL = registerBlock("pink_kittypet_bowl",
            () -> new KittyPetBowl(BlockBehaviour.Properties.of().strength(0.5f)));

    public static final DeferredBlock<KittyPetBowl> BLACK_KITTYPET_BOWL = registerBlock("black_kittypet_bowl",
            () -> new KittyPetBowl(BlockBehaviour.Properties.of().strength(0.5f)));

    public static final DeferredBlock<KittyPetBowl> RED_KITTYPET_BOWL = registerBlock("red_kittypet_bowl",
            () -> new KittyPetBowl(BlockBehaviour.Properties.of().strength(0.5f)));

    //


    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}
