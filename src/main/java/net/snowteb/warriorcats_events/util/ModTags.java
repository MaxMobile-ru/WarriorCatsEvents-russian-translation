package net.snowteb.warriorcats_events.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.snowteb.warriorcats_events.WarriorCatsEvents;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> HIT_THROUGH_BLOCKS = tag("hit_through_blocks");

        private static TagKey<Block> tag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, name));
        }
    }

    public static class EntityTypes {
        public static final TagKey<EntityType<?>> PREY_MOBS = tag("additional_prey");
        public static final TagKey<EntityType<?>> PREDATOR_MOBS = tag("predator_mobs");

        private static TagKey<EntityType<?>> tag(String name) {
            return TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, name));
        }
    }

    public static class DamageTypes {
        public static final TagKey<DamageType> WCAT_PANIC = tag("wcat_panic");

        private static TagKey<DamageType> tag(String name) {
            return TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, name));
        }
    }

    public static class Items {
        public static final TagKey<Item> PREY = tag("prey");
        public static final TagKey<Item> HERBS = tag("herbs");
        public static final TagKey<Item> FEATHERS = tag("feathers");
        public static final TagKey<Item> WCE_FEATHERS = tag("wce_feathers");
        public static final TagKey<Item> HERB_CRAFTING = tag("herb_crafting");
        public static final TagKey<Item> ADDITIONAL_PREY = tag("additional_prey");
        public static final TagKey<Item> SKULLS = tag("skulls");
        public static final TagKey<Item> FILL_BOWL = tag("fill_bowl");
        public static final TagKey<Item> LEAF_WRAPPINGS = tag("leaf_wrappings");
        public static final TagKey<Item> SUSPICIOUS_FOOD = tag("suspicious_food");

        private static TagKey<Item> tag(String name) {
               return ItemTags.create(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, name));
        }
    }
}
