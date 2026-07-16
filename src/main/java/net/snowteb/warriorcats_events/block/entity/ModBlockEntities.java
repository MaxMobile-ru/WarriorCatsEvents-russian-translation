package net.snowteb.warriorcats_events.block.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.block.ModBlocks;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, WarriorCatsEvents.MODID);


    public static final Supplier<BlockEntityType<StoneCleftBlockEntity>> STONECLEFT_BLOCK =
            BLOCK_ENTITIES.register("stone_cleft_block", () ->
                    BlockEntityType.Builder.of(StoneCleftBlockEntity::new,
                            ModBlocks.STONECLEFT.get()).build(null));

    public static final Supplier<BlockEntityType<FreshkillPileBlockEntity>> FRESH_KILL_PILE =
            BLOCK_ENTITIES.register("fresh_kill_pile_block", () ->
                    BlockEntityType.Builder.of(FreshkillPileBlockEntity::new,
                            ModBlocks.FRESHKILL_PILE.get()).build(null));

    public static final Supplier<BlockEntityType<NestBlockEntity>> MOSS_BED =
            BLOCK_ENTITIES.register("moss_bed", () ->
                    BlockEntityType.Builder.of(NestBlockEntity::new,
                            ModBlocks.MOSS_BED.get(),
                            ModBlocks.HAY_BED.get(),
                            ModBlocks.KELP_BED.get(),
                            ModBlocks.STONE_BED.get(),
                            ModBlocks.LAVENDER_BED.get(),
                            ModBlocks.DRIFTWOOD_BED.get(),
                            ModBlocks.DAISY_BED.get(),
                            ModBlocks.ACACIA_BED.get(),
                            ModBlocks.TERRACOTTA_BED.get(),
                            ModBlocks.BAMBOO_BED.get(),
                            ModBlocks.BERRY_BED.get(),
                            ModBlocks.CORAL_BED.get(),
                            ModBlocks.GLOWBERRY_BED.get(),
                            ModBlocks.MUDDY_BED.get(),
                            ModBlocks.KITTYPET_BED.get(),
                            ModBlocks.ACORN_BED.get(),
                            ModBlocks.NAUTILUS_BED.get(),
                            ModBlocks.SUNFLOWER_BED.get(),
                            ModBlocks.CHERRY_BLOSSOM_BED.get()
                            )
                            .build(null)
            );

    public static final Supplier<BlockEntityType<MakeshiftBedBlockEntity>> MAKESHIFT_BED =
            BLOCK_ENTITIES.register("makeshift_bed", () ->
                    BlockEntityType.Builder.of(MakeshiftBedBlockEntity::new,
                                    ModBlocks.MAKESHIFT_BED.get())
                            .build(null)
            );


    public static final Supplier<BlockEntityType<TreeStumpBlockEntity>> TREE_STUMP =
            BLOCK_ENTITIES.register("tree_stump", () ->
                    BlockEntityType.Builder.of(TreeStumpBlockEntity::new,
                            ModBlocks.TREE_STUMP.get()).build(null));


    public static final Supplier<BlockEntityType<KittypetBowlBlockEntity>> KITTYPET_BOWL =
            BLOCK_ENTITIES.register("kittypet_bowl", () ->
                    BlockEntityType.Builder.of(KittypetBowlBlockEntity::new,
                            ModBlocks.WHITE_KITTYPET_BOWL.get(),
                            ModBlocks.ORANGE_KITTYPET_BOWL.get(),
                            ModBlocks.MAGENTA_KITTYPET_BOWL.get(),
                            ModBlocks.BLUE_KITTYPET_BOWL.get(),
                            ModBlocks.YELLOW_KITTYPET_BOWL.get(),
                            ModBlocks.LIME_KITTYPET_BOWL.get(),
                            ModBlocks.PINK_KITTYPET_BOWL.get(),
                            ModBlocks.BLACK_KITTYPET_BOWL.get(),
                            ModBlocks.RED_KITTYPET_BOWL.get()
                    ).build(null));

    public static final Supplier<BlockEntityType<MoonstoneBlockEntity>> MOONSTONE =
            BLOCK_ENTITIES.register("moonstone", () ->
                    BlockEntityType.Builder.of(MoonstoneBlockEntity::new,
                            ModBlocks.MOONSTONE_BLOCK.get()).build(null)
            );

    public static final Supplier<BlockEntityType<StickfireBlockEntity>> STICKFIRE =
            BLOCK_ENTITIES.register("stickfire", () ->
                    BlockEntityType.Builder.of(StickfireBlockEntity::new,
                            ModBlocks.STICKFIRE.get()).build(null)
            );

    public static final Supplier<BlockEntityType<LizardEggBlockEntity>> LIZARD_EGG =
            BLOCK_ENTITIES.register("lizard_egg", () ->
                    BlockEntityType.Builder.of(LizardEggBlockEntity::new,
                            ModBlocks.LIZARD_EGG_BLOCK.get()).build(null)
            );

    public static final Supplier<BlockEntityType<HerbMixingRockBlockEntity>> HERB_MIXING_ROCK =
            BLOCK_ENTITIES.register("herb_mixing_rock", () ->
                    BlockEntityType.Builder.of(HerbMixingRockBlockEntity::new,
                            ModBlocks.HERB_MIXING_ROCK.get()).build(null)
            );


    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
