package net.snowteb.warriorcats_events.datagen.loot;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.snowteb.warriorcats_events.block.ModBlocks;
import net.snowteb.warriorcats_events.block.custom.GlowrocksBlock;
import net.snowteb.warriorcats_events.block.custom.LavenderPetalsBlock;
import net.snowteb.warriorcats_events.block.custom.PreyBonesBlock;
import net.snowteb.warriorcats_events.block.custom.WallGlowrocksBlock;
import net.snowteb.warriorcats_events.item.ModItems;

import java.util.Set;

public class ModBlockLootTables extends BlockLootSubProvider {


    public ModBlockLootTables(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {

        this.dropSelf(ModBlocks.STONECLEFT.get());
        this.dropSelf(ModBlocks.GLOWSHROOM.get());

        this.dropSelf(ModBlocks.STONE_CRAFTING_TABLE.get());

        this.dropSelf(ModBlocks.LEAF_TRAPDOOR.get());

        this.dropSelf(ModBlocks.PEBBLES.get());

        this.dropSelf(ModBlocks.WHITE_KITTYPET_BOWL.get());
        this.dropSelf(ModBlocks.ORANGE_KITTYPET_BOWL.get());
        this.dropSelf(ModBlocks.MAGENTA_KITTYPET_BOWL.get());
        this.dropSelf(ModBlocks.BLUE_KITTYPET_BOWL.get());
        this.dropSelf(ModBlocks.YELLOW_KITTYPET_BOWL.get());
        this.dropSelf(ModBlocks.LIME_KITTYPET_BOWL.get());
        this.dropSelf(ModBlocks.PINK_KITTYPET_BOWL.get());
        this.dropSelf(ModBlocks.BLACK_KITTYPET_BOWL.get());
        this.dropSelf(ModBlocks.RED_KITTYPET_BOWL.get());

        this.dropSelf(ModBlocks.HERB_MIXING_ROCK.get());

        HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);

        //DOCK
        {
            this.add(ModBlocks.DOCK.get(), block -> this.applyExplosionDecay(
                    block, LootTable.lootTable().withPool(LootPool.lootPool().when(
                                            LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.DOCK.get())
                                                    .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SweetBerryBushBlock.AGE, 3))
                                    ).add(LootItem.lootTableItem(ModItems.DOCK_LEAVES.get()))
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 3.0F)))
                                    .apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))
                    ).withPool(LootPool.lootPool().when(
                                            LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.DOCK.get())
                                                    .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SweetBerryBushBlock.AGE, 2))
                                    ).add(LootItem.lootTableItem(ModItems.DOCK_LEAVES.get()))
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                                    .apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))
                    )));
        }
        //SORRELPLANT
        {
            this.add(ModBlocks.SORRELPLANT.get(), block -> this.applyExplosionDecay(
                    block, LootTable.lootTable().withPool(LootPool.lootPool().when(
                                            LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.SORRELPLANT.get())
                                                    .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SweetBerryBushBlock.AGE, 3))
                                    ).add(LootItem.lootTableItem(ModItems.SORREL.get()))
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 3.0F)))
                                    .apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))
                    ).withPool(LootPool.lootPool().when(
                                            LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.SORRELPLANT.get())
                                                    .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SweetBerryBushBlock.AGE, 2))
                                    ).add(LootItem.lootTableItem(ModItems.SORREL.get()))
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                                    .apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))
                    )));
        }
        //DAISYPLANT
        {
            this.add(ModBlocks.DAISYPLANT.get(), block -> this.applyExplosionDecay(
                    block, LootTable.lootTable().withPool(LootPool.lootPool().when(
                                            LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.DAISYPLANT.get())
                                                    .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SweetBerryBushBlock.AGE, 3))
                                    ).add(LootItem.lootTableItem(ModItems.DAISY.get()))
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 3.0F)))
                                    .apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))
                    ).withPool(LootPool.lootPool().when(
                                            LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.DAISYPLANT.get())
                                                    .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SweetBerryBushBlock.AGE, 2))
                                    ).add(LootItem.lootTableItem(ModItems.DAISY.get()))
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                                    .apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))
                    )));
        }
        //BURNETPLANT
        {
            this.add(ModBlocks.BURNETPLANT.get(), block -> this.applyExplosionDecay(
                    block, LootTable.lootTable().withPool(LootPool.lootPool().when(
                                            LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.BURNETPLANT.get())
                                                    .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SweetBerryBushBlock.AGE, 3))
                                    ).add(LootItem.lootTableItem(ModItems.BURNET.get()))
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 3.0F)))
                                    .apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))
                    ).withPool(LootPool.lootPool().when(
                                            LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.BURNETPLANT.get())
                                                    .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SweetBerryBushBlock.AGE, 2))
                                    ).add(LootItem.lootTableItem(ModItems.BURNET.get()))
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                                    .apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))
                    )));

        }
        //CHAMOMILEPLANT
        {
            this.add(ModBlocks.CHAMOMILEPLANT.get(), block -> this.applyExplosionDecay(
                    block, LootTable.lootTable().withPool(LootPool.lootPool().when(
                                            LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.CHAMOMILEPLANT.get())
                                                    .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SweetBerryBushBlock.AGE, 3))
                                    ).add(LootItem.lootTableItem(ModItems.CHAMOMILE.get()))
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 3.0F)))
                                    .apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))
                    ).withPool(LootPool.lootPool().when(
                                            LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.CHAMOMILEPLANT.get())
                                                    .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SweetBerryBushBlock.AGE, 2))
                                    ).add(LootItem.lootTableItem(ModItems.CHAMOMILE.get()))
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                                    .apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))
                    )));
        }
        //DEATHBERRIESBUSH
        {
            this.add(ModBlocks.DEATHBERRIESBUSH.get(), block -> this.applyExplosionDecay(
                    block, LootTable.lootTable().withPool(LootPool.lootPool().when(
                                            LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.DEATHBERRIESBUSH.get())
                                                    .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SweetBerryBushBlock.AGE, 3))
                                    ).add(LootItem.lootTableItem(ModItems.DEATHBERRIES.get()))
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 3.0F)))
                                    .apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))
                    ).withPool(LootPool.lootPool().when(
                                            LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.DEATHBERRIESBUSH.get())
                                                    .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SweetBerryBushBlock.AGE, 2))
                                    ).add(LootItem.lootTableItem(ModItems.DEATHBERRIES.get()))
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 1.0F)))
                                    .apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))
                    )));
        }
        //CATMINT
        {
            this.add(ModBlocks.CATMINTPLANT.get(), block -> this.applyExplosionDecay(
                    block, LootTable.lootTable().withPool(LootPool.lootPool().when(
                                            LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.CATMINTPLANT.get())
                                                    .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SweetBerryBushBlock.AGE, 3))
                                    ).add(LootItem.lootTableItem(ModItems.CATMINT.get()))
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 3.0F)))
                                    .apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))
                    ).withPool(LootPool.lootPool().when(
                                            LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.CATMINTPLANT.get())
                                                    .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SweetBerryBushBlock.AGE, 2))
                                    ).add(LootItem.lootTableItem(ModItems.CATMINT.get()))
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                                    .apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))
                    )));
        }
        //YARROW
        {
            this.add(ModBlocks.YARROWPLANT.get(), block -> this.applyExplosionDecay(
                    block, LootTable.lootTable().withPool(LootPool.lootPool().when(
                                            LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.YARROWPLANT.get())
                                                    .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SweetBerryBushBlock.AGE, 3))
                                    ).add(LootItem.lootTableItem(ModItems.YARROW.get()))
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 3.0F)))
                                    .apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))
                    ).withPool(LootPool.lootPool().when(
                                            LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.YARROWPLANT.get())
                                                    .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SweetBerryBushBlock.AGE, 2))
                                    ).add(LootItem.lootTableItem(ModItems.YARROW.get()))
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                                    .apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))
                    )));
        }
        //FEVERFEWPLANT
        {
            this.add(ModBlocks.FEVERFEWPLANT.get(), block -> this.applyExplosionDecay(
                    block, LootTable.lootTable().withPool(LootPool.lootPool().when(
                                            LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.FEVERFEWPLANT.get())
                                                    .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SweetBerryBushBlock.AGE, 3))
                                    ).add(LootItem.lootTableItem(ModItems.FEVERFEW.get()))
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 3.0F)))
                                    .apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))
                    ).withPool(LootPool.lootPool().when(
                                            LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.FEVERFEWPLANT.get())
                                                    .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SweetBerryBushBlock.AGE, 2))
                                    ).add(LootItem.lootTableItem(ModItems.FEVERFEW.get()))
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                                    .apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))
                    )));
        }
        //JUNIPER
        {
            this.add(ModBlocks.JUNIPERPLANT.get(), block -> this.applyExplosionDecay(
                    block, LootTable.lootTable().withPool(LootPool.lootPool().when(
                                            LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.JUNIPERPLANT.get())
                                                    .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SweetBerryBushBlock.AGE, 3))
                                    ).add(LootItem.lootTableItem(ModItems.JUNIPER_BERRIES.get()))
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0F, 6.0F)))
                                    .apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))
                    ).withPool(LootPool.lootPool().when(
                                            LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.JUNIPERPLANT.get())
                                                    .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SweetBerryBushBlock.AGE, 2))
                                    ).add(LootItem.lootTableItem(ModItems.JUNIPER_BERRIES.get()))
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                                    .apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))
                    )));
        }
        //COMFREY
        {
            this.add(ModBlocks.COMFREYPLANT.get(), block -> this.applyExplosionDecay(block,
                    LootTable.lootTable()
                            .withPool(
                                    LootPool.lootPool()
                                            .when(
                                                    LootItemBlockStatePropertyCondition
                                                            .hasBlockStateProperties(ModBlocks.COMFREYPLANT.get())
                                                            .setProperties(
                                                                    StatePropertiesPredicate.Builder.properties()
                                                                            .hasProperty(SweetBerryBushBlock.AGE, 3)
                                                            )
                                            )
                                            .add(
                                                    AlternativesEntry.alternatives(
                                                            LootItem.lootTableItem(ModItems.COMFREY_ROOT.get())
                                                                    .when(LootItemRandomChanceCondition.randomChance(0.25f))
                                                                    .apply(SetItemCountFunction.setCount(
                                                                            UniformGenerator.between(1.0F, 2.0F)
                                                                    )),
                                                            LootItem.lootTableItem(ModItems.COMFREY_LEAVES.get())
                                                                    .apply(SetItemCountFunction.setCount(
                                                                            UniformGenerator.between(2.0F, 4.0F)
                                                                    ))
                                                    )
                                            )
                            )
                            .withPool(
                                    LootPool.lootPool()
                                            .when(
                                                    LootItemBlockStatePropertyCondition
                                                            .hasBlockStateProperties(ModBlocks.COMFREYPLANT.get())
                                                            .setProperties(
                                                                    StatePropertiesPredicate.Builder.properties()
                                                                            .hasProperty(SweetBerryBushBlock.AGE, 2)
                                                            )
                                            )
                                            .add(
                                                    LootItem.lootTableItem(ModItems.COMFREY_LEAVES.get())
                                                            .apply(SetItemCountFunction.setCount(
                                                                    UniformGenerator.between(1.0F, 2.0F)
                                                            ))
                                            )
                            )
            ));
        }


        this.add(ModBlocks.MOSS_BED.get(),
                block -> createSingleItemTableWithSilkTouch(
                        block,
                        Items.FEATHER,
                        UniformGenerator.between(1.0F, 3.0F)
                ).withPool(
                        LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(Items.MOSS_BLOCK)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F))))
                )
        );

        this.add(ModBlocks.FRESHKILL_PILE.get(),
                block -> createSingleItemTableWithSilkTouch(
                        block,
                        Items.COBBLESTONE,
                        UniformGenerator.between(2.0F, 4.0F)
                )

        );

        this.add(ModBlocks.HAY_BED.get(),
                block -> createSingleItemTableWithSilkTouch(
                        block,
                        Items.WHEAT,
                        UniformGenerator.between(5.0F, 11.0F)
                )

        );

        this.add(ModBlocks.KELP_BED.get(),
                block -> createSingleItemTableWithSilkTouch(
                        block,
                        Items.KELP,
                        UniformGenerator.between(1.0F, 3.0F)
                )
        );

        this.add(ModBlocks.STONE_BED.get(),
                block -> createSingleItemTableWithSilkTouch(
                        block,
                        Items.COBBLESTONE,
                        UniformGenerator.between(1.0F, 3.0F)
                )
        );

        this.add(ModBlocks.LAVENDER_BED.get(),
                block -> createSingleItemTableWithSilkTouch(
                        block,
                        ModBlocks.LAVENDER_PETALS.get(),
                        UniformGenerator.between(4.0F, 14.0F)
                )
        );

        this.add(ModBlocks.CHERRY_BLOSSOM_BED.get(),
                block -> createSingleItemTableWithSilkTouch(
                        block,
                        Items.CHERRY_LEAVES,
                        UniformGenerator.between(2.0F, 4.0F)
                )
        );

        this.add(ModBlocks.DRIFTWOOD_BED.get(),
                block -> createSingleItemTableWithSilkTouch(
                        block,
                        Items.STICK,
                        UniformGenerator.between(3.0F, 5.0F)
                )
        );

        this.add(ModBlocks.DAISY_BED.get(),
                block -> createSingleItemTableWithSilkTouch(
                        block,
                        ModItems.DAISY.get(),
                        UniformGenerator.between(3.0F, 6.0F)
                )
        );

        this.add(ModBlocks.ACACIA_BED.get(),
                block -> createSingleItemTableWithSilkTouch(
                        block,
                        ModItems.CHAMOMILE.get(),
                        UniformGenerator.between(2.0F, 4.0F)
                )
        );

        this.add(ModBlocks.TERRACOTTA_BED.get(),
                block -> createSingleItemTableWithSilkTouch(
                        block,
                        Items.TERRACOTTA,
                        UniformGenerator.between(3.0F, 5.0F)
                )
        );

        this.add(ModBlocks.BAMBOO_BED.get(),
                block -> createSingleItemTableWithSilkTouch(
                        block,
                        Items.BAMBOO,
                        UniformGenerator.between(2.0F, 4.0F)
                )
        );

        this.add(ModBlocks.BERRY_BED.get(),
                block -> createSingleItemTableWithSilkTouch(
                        block,
                        Items.SWEET_BERRIES,
                        UniformGenerator.between(2.0F, 4.0F)
                )
        );

        this.add(ModBlocks.CORAL_BED.get(),
                block -> createSingleItemTableWithSilkTouch(
                        block,
                        Items.HORN_CORAL,
                        UniformGenerator.between(2.0F, 4.0F)
                )
        );

        this.add(ModBlocks.GLOWBERRY_BED.get(),
                block -> createSingleItemTableWithSilkTouch(
                        block,
                        Items.GLOW_BERRIES,
                        UniformGenerator.between(1.0F, 3.0F)
                )
        );

        this.add(ModBlocks.MUDDY_BED.get(),
                block -> createSingleItemTableWithSilkTouch(
                        block,
                        Items.MUD,
                        UniformGenerator.between(1.0F, 3.0F)
                )
        );

        this.dropSelf(ModBlocks.KITTYPET_BED.get());

        this.add(ModBlocks.ACORN_BED.get(),
                block -> createSingleItemTableWithSilkTouch(
                        block,
                        Items.SPRUCE_LEAVES,
                        UniformGenerator.between(1.0F, 3.0F)
                )
        );

        this.add(ModBlocks.NAUTILUS_BED.get(),
                block -> LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .add(LootItem.lootTableItem(Items.HEART_OF_THE_SEA)
                                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F))))
                        )
                        .withPool(LootPool.lootPool()
                                .add(LootItem.lootTableItem(Items.NAUTILUS_SHELL)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0F, 4.0F))))
                        )
        );

        this.add(ModBlocks.SUNFLOWER_BED.get(),
                block -> createSingleItemTableWithSilkTouch(
                        block,
                        Items.SUNFLOWER,
                        UniformGenerator.between(1.0F, 2.0F)
                )
        );

        this.dropWhenSilkTouch(ModBlocks.LIZARD_EGG_BLOCK.get());



        this.add(ModBlocks.STICKFIRE.get(),
                block -> createSingleItemTableWithSilkTouch(
                        block,
                        Items.CHARCOAL,
                        UniformGenerator.between(1.0F, 3.0F)
                )
        );



        this.add(ModBlocks.GLOWROCKS.get(), createGlowrocksTable(ModBlocks.GLOWROCKS.get(), ModItems.GLOWROCKS_ITEM.get()));
        this.add(ModBlocks.WALL_GLOWROCKS.get(), createWallGlowrocksTable(ModBlocks.WALL_GLOWROCKS.get(), ModItems.GLOWROCKS_ITEM.get()));

        this.add(ModBlocks.GREEN_GLOWROCKS.get(), createGlowrocksTable(ModBlocks.GREEN_GLOWROCKS.get(), ModItems.GREEN_GLOWROCKS_ITEM.get()));
        this.add(ModBlocks.GREEN_WALL_GLOWROCKS.get(), createWallGlowrocksTable(ModBlocks.GREEN_WALL_GLOWROCKS.get(), ModItems.GREEN_GLOWROCKS_ITEM.get()));

        this.add(ModBlocks.PINK_GLOWROCKS.get(), createGlowrocksTable(ModBlocks.PINK_GLOWROCKS.get(), ModItems.PINK_GLOWROCKS_ITEM.get()));
        this.add(ModBlocks.PINK_WALL_GLOWROCKS.get(), createWallGlowrocksTable(ModBlocks.PINK_WALL_GLOWROCKS.get(), ModItems.PINK_GLOWROCKS_ITEM.get()));

        this.add(ModBlocks.RED_GLOWROCKS.get(), createGlowrocksTable(ModBlocks.RED_GLOWROCKS.get(), ModItems.RED_GLOWROCKS_ITEM.get()));
        this.add(ModBlocks.RED_WALL_GLOWROCKS.get(), createWallGlowrocksTable(ModBlocks.RED_WALL_GLOWROCKS.get(), ModItems.RED_GLOWROCKS_ITEM.get()));

        this.add(ModBlocks.YELLOW_GLOWROCKS.get(), createGlowrocksTable(ModBlocks.YELLOW_GLOWROCKS.get(), ModItems.YELLOW_GLOWROCKS_ITEM.get()));
        this.add(ModBlocks.YELLOW_WALL_GLOWROCKS.get(), createWallGlowrocksTable(ModBlocks.YELLOW_WALL_GLOWROCKS.get(), ModItems.YELLOW_GLOWROCKS_ITEM.get()));


        this.dropSelf(ModBlocks.ACORN_LANTERN.get());

        this.dropSelf(ModBlocks.DAISY_CHAIN.get());
        this.dropSelf(ModBlocks.LAVENDER_CHAIN.get());

        this.dropSelf(ModBlocks.MOONSTONE_BLOCK.get());




        this.add(ModBlocks.MAKESHIFT_BED.get(), noDrop());
        this.add(ModBlocks.TREE_STUMP.get(), noDrop());

        this.add(ModBlocks.LEAF_DOOR.get(),
                block -> createDoorTable(ModBlocks.LEAF_DOOR.get()));


        this.add(ModBlocks.LAVENDER_PETALS.get(),
                block -> LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1))
                                        .add(LootItem.lootTableItem(ModBlocks.LAVENDER_PETALS.get())
                                                .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                                        .setProperties(StatePropertiesPredicate.Builder.properties()
                                                                .hasProperty(LavenderPetalsBlock.AMOUNT, 1)))
                                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                                        .add(LootItem.lootTableItem(ModBlocks.LAVENDER_PETALS.get())
                                                .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                                        .setProperties(StatePropertiesPredicate.Builder.properties()
                                                                .hasProperty(LavenderPetalsBlock.AMOUNT, 2)))
                                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(2))))
                                        .add(LootItem.lootTableItem(ModBlocks.LAVENDER_PETALS.get())
                                                .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                                        .setProperties(StatePropertiesPredicate.Builder.properties()
                                                                .hasProperty(LavenderPetalsBlock.AMOUNT, 3)))
                                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(3))))
                                        .add(LootItem.lootTableItem(ModBlocks.LAVENDER_PETALS.get())
                                                .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                                        .setProperties(StatePropertiesPredicate.Builder.properties()
                                                                .hasProperty(LavenderPetalsBlock.AMOUNT, 4)))
                                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(4))))
                        )
        );

        this.add(ModBlocks.LAVENDER.get(),
                block -> LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1))
                                        .add(
                                                LootItem.lootTableItem(block)
                                                        .when(
                                                                LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                                                        .setProperties(
                                                                                StatePropertiesPredicate.Builder.properties()
                                                                                        .hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER)
                                                                        )
                                                        )
                                        )
                        )
        );


        this.add(ModBlocks.PREY_BONES.get(), block ->
                LootTable.lootTable()

                        .withPool(
                                LootPool.lootPool().add(
                                        AlternativesEntry.alternatives(

                                                LootItem.lootTableItem(Items.BONE)
                                                        .when(LootItemBlockStatePropertyCondition
                                                                .hasBlockStateProperties(block)
                                                                .setProperties(StatePropertiesPredicate.Builder.properties()
                                                                        .hasProperty(PreyBonesBlock.BONES, PreyBonesBlock.Bones.STAGE_1)
                                                                )
                                                        ).apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F))),

                                                LootItem.lootTableItem(Items.BONE)
                                                        .when(LootItemBlockStatePropertyCondition
                                                                .hasBlockStateProperties(block)
                                                                .setProperties(StatePropertiesPredicate.Builder.properties()
                                                                        .hasProperty(PreyBonesBlock.BONES, PreyBonesBlock.Bones.STAGE_2)
                                                                )
                                                        ).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F))),

                                                LootItem.lootTableItem(Items.BONE)
                                                        .when(LootItemBlockStatePropertyCondition
                                                                .hasBlockStateProperties(block)
                                                                .setProperties(StatePropertiesPredicate.Builder.properties()
                                                                        .hasProperty(PreyBonesBlock.BONES, PreyBonesBlock.Bones.STAGE_3)
                                                                )
                                                        ).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 3.0F))),

                                                LootItem.lootTableItem(Items.BONE)
                                                        .when(LootItemBlockStatePropertyCondition
                                                                .hasBlockStateProperties(block)
                                                                .setProperties(StatePropertiesPredicate.Builder.properties()
                                                                        .hasProperty(PreyBonesBlock.BONES, PreyBonesBlock.Bones.STAGE_BADGER)
                                                                )
                                                        ).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 3.0F))),

                                                LootItem.lootTableItem(Items.BONE)
                                                        .when(LootItemBlockStatePropertyCondition
                                                                .hasBlockStateProperties(block)
                                                                .setProperties(StatePropertiesPredicate.Builder.properties()
                                                                        .hasProperty(PreyBonesBlock.BONES, PreyBonesBlock.Bones.STAGE_SQUIRREL)
                                                                )
                                                        ).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 3.0F))),

                                                LootItem.lootTableItem(Items.BONE)
                                                        .when(LootItemBlockStatePropertyCondition
                                                                .hasBlockStateProperties(block)
                                                                .setProperties(StatePropertiesPredicate.Builder.properties()
                                                                        .hasProperty(PreyBonesBlock.BONES, PreyBonesBlock.Bones.STAGE_EAGLE)
                                                                )
                                                        ).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 3.0F))),

                                                LootItem.lootTableItem(Items.BONE)
                                        )
                                )
                        )
                        .withPool(
                                LootPool.lootPool().add(
                                        LootItem.lootTableItem(ModItems.BADGER_SKULL.get())
                                                .when(LootItemBlockStatePropertyCondition
                                                        .hasBlockStateProperties(block)
                                                        .setProperties(StatePropertiesPredicate.Builder.properties()
                                                                .hasProperty(PreyBonesBlock.BONES, PreyBonesBlock.Bones.STAGE_BADGER)
                                                        )
                                                )
                                )
                        )
                        .withPool(
                                LootPool.lootPool().add(
                                        LootItem.lootTableItem(ModItems.SQUIRREL_SKULL.get())
                                                .when(LootItemBlockStatePropertyCondition
                                                        .hasBlockStateProperties(block)
                                                        .setProperties(StatePropertiesPredicate.Builder.properties()
                                                                .hasProperty(PreyBonesBlock.BONES, PreyBonesBlock.Bones.STAGE_SQUIRREL)
                                                        )
                                                )
                                )
                        )
                        .withPool(
                                LootPool.lootPool().add(
                                        LootItem.lootTableItem(ModItems.GOLDEN_EAGLE_SKULL.get())
                                                .when(LootItemBlockStatePropertyCondition
                                                        .hasBlockStateProperties(block)
                                                        .setProperties(StatePropertiesPredicate.Builder.properties()
                                                                .hasProperty(PreyBonesBlock.BONES, PreyBonesBlock.Bones.STAGE_EAGLE)
                                                        )
                                                )
                                )
                        )
        );




    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }

    private LootTable.Builder createGlowrocksTable(Block sourceBlock, Item item) {
        LootPool.Builder pool = LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .when(ExplosionCondition.survivesExplosion());

        int minAmount = 1;
        int maxAmount = 6;

        for (int i = minAmount; i <= maxAmount; i++) {
            pool.add(LootItem.lootTableItem(item)
                    .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(sourceBlock)
                            .setProperties(StatePropertiesPredicate.Builder.properties()
                                    .hasProperty(GlowrocksBlock.AMOUNT_G, i)))
                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(i)))
            );
        }

        return LootTable.lootTable().withPool(pool);
    }

    private LootTable.Builder createWallGlowrocksTable(Block sourceBlock, Item item) {
        LootPool.Builder pool = LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .when(ExplosionCondition.survivesExplosion());

        int minAmount = 1;
        int maxAmount = 4;

        for (int i = minAmount; i <= maxAmount; i++) {
            pool.add(LootItem.lootTableItem(item)
                    .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(sourceBlock)
                            .setProperties(StatePropertiesPredicate.Builder.properties()
                                    .hasProperty(WallGlowrocksBlock.AMOUNT_WG, i)))
                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(i)))
            );
        }

        return LootTable.lootTable().withPool(pool);
    }
}
