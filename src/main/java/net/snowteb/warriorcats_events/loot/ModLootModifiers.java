package net.snowteb.warriorcats_events.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.snowteb.warriorcats_events.WarriorCatsEvents;

public class ModLootModifiers {

    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS,
                    WarriorCatsEvents.MODID);

    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>,
            MapCodec<? extends IGlobalLootModifier>> ADD_ITEM =
            LOOT_MODIFIERS.register(
                    "add_item",
                    () -> AddItemModifier.CODEC
            );

    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>,
            MapCodec<? extends IGlobalLootModifier>> ADD_ITEM_RARE =
            LOOT_MODIFIERS.register(
                    "add_item_rare",
                    () -> AddItemModifierRare.CODEC
            );

    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>,
            MapCodec<? extends IGlobalLootModifier>> ADD_ITEM_POOL =
            LOOT_MODIFIERS.register(
                    "add_item_pool",
                    () -> AddItemPoolModifier.CODEC
            );

    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>,
            MapCodec<? extends IGlobalLootModifier>> ADD_ITEM_TO_DROP =
            LOOT_MODIFIERS.register(
                    "add_drop",
                    () -> AddItemToDrop.CODEC
            );

    public static void register(IEventBus bus) {
        LOOT_MODIFIERS.register(bus);
    }
}
