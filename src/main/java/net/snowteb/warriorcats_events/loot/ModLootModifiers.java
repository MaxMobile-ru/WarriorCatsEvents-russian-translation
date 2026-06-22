package net.snowteb.warriorcats_events.loot;

import com.mojang.serialization.Codec;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.snowteb.warriorcats_events.WarriorCatsEvents;

public class ModLootModifiers {

    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, WarriorCatsEvents.MODID);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> ADD_ITEM =
            LOOT_MODIFIERS_SERIALIZERS.register("add_item", AddItemModifier.CODEC);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> ADD_ITEM_RARE =
            LOOT_MODIFIERS_SERIALIZERS.register("add_item_rare", AddItemModifierRare.CODEC);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> ADD_ITEM_POOL =
            LOOT_MODIFIERS_SERIALIZERS.register("add_item_pool", AddItemPoolModifier.CODEC);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> ADD_ITEM_TO_DROP =
            LOOT_MODIFIERS_SERIALIZERS.register("add_drop", AddItemToDrop.CODEC);


    public static void register(IEventBus bus) {
        LOOT_MODIFIERS_SERIALIZERS.register(bus);
    }
}
