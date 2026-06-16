package net.snowteb.warriorcats_events.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.block.ModBlocks;
import net.snowteb.warriorcats_events.compat.CompatibilityTags;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagGenerator extends BlockTagsProvider {
    public ModBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, WarriorCatsEvents.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {

        this.tag(BlockTags.DOORS).add(ModBlocks.LEAF_DOOR.get());
        this.tag(BlockTags.TRAPDOORS).add(ModBlocks.LEAF_TRAPDOOR.get());

        this.tag(BlockTags.FLOWERS).add(ModBlocks.LAVENDER.get());
        this.tag(BlockTags.TALL_FLOWERS).add(ModBlocks.LAVENDER.get());
        this.tag(BlockTags.SMALL_FLOWERS).add(ModBlocks.LAVENDER_PETALS.get());


        this.tag(BlockTags.BEDS).add(
                ModBlocks.MOSS_BED.get(),
                ModBlocks.HAY_BED.get(),
                ModBlocks.KELP_BED.get(),
                ModBlocks.STONE_BED.get(),
                ModBlocks.CHERRY_BLOSSOM_BED.get(),
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
                ModBlocks.LAVENDER_BED.get()
        );

        this.tag(CompatibilityTags.SERENE_SEASONS_SPRING_CROPS_BLOCK).add(ModBlocks.DOCK.get(), ModBlocks.SORRELPLANT.get(), ModBlocks.BURNETPLANT.get(), ModBlocks.DAISYPLANT.get());
        this.tag(CompatibilityTags.SERENE_SEASONS_SUMMER_CROPS_BLOCK).add(ModBlocks.CHAMOMILEPLANT.get(), ModBlocks.CATMINTPLANT.get(), ModBlocks.YARROWPLANT.get(), ModBlocks.FEVERFEWPLANT.get(), ModBlocks.COMFREYPLANT.get());
        this.tag(CompatibilityTags.SERENE_SEASONS_AUTUMN_CROPS_BLOCK).add(ModBlocks.DEATHBERRIESBUSH.get(), ModBlocks.COMFREYPLANT.get());
        this.tag(CompatibilityTags.SERENE_SEASONS_WINTER_CROPS_BLOCK).add(ModBlocks.JUNIPERPLANT.get());

    }
}
