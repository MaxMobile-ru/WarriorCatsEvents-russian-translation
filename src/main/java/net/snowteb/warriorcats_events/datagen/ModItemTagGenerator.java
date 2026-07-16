package net.snowteb.warriorcats_events.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.compat.CompatibilityTags;
import net.snowteb.warriorcats_events.item.ModItems;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagGenerator extends ItemTagsProvider {
    public ModItemTagGenerator(PackOutput p_275343_, CompletableFuture<HolderLookup.Provider> p_275729_,
                               CompletableFuture<TagLookup<Block>> p_275322_, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_275343_, p_275729_, p_275322_, WarriorCatsEvents.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {

        this.tag(CompatibilityTags.SERENE_SEASONS_SPRING_CROPS).add(ModItems.DOCK_LEAVES.get(), ModItems.SORREL.get(), ModItems.BURNET.get(), ModItems.DAISY.get());
        this.tag(CompatibilityTags.SERENE_SEASONS_SUMMER_CROPS).add(ModItems.CHAMOMILE.get(), ModItems.CATMINT.get(), ModItems.YARROW.get(), ModItems.FEVERFEW.get(), ModItems.COMFREY_LEAVES.get(), ModItems.COMFREY_ROOT.get());
        this.tag(CompatibilityTags.SERENE_SEASONS_AUTUMN_CROPS).add(ModItems.DEATHBERRIES.get(), ModItems.COMFREY_LEAVES.get(), ModItems.COMFREY_ROOT.get());
        this.tag(CompatibilityTags.SERENE_SEASONS_WINTER_CROPS).add(ModItems.JUNIPER_BERRIES.get());

        this.tag(ItemTags.DYEABLE).add(ModItems.CAT_COLLAR.get());
    }
}
