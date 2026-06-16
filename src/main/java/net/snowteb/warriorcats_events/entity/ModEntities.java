package net.snowteb.warriorcats_events.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.entity.custom.*;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, WarriorCatsEvents.MODID);


    public static final Supplier<EntityType<MouseEntity>> MOUSE =
            ENTITY_TYPES.register("mouse",
                    () -> EntityType.Builder.of(MouseEntity::new, MobCategory.CREATURE)
                            .sized(0.7f,0.7f)
                            .build(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "mouse").toString()));
    public static final Supplier<EntityType<SquirrelEntity>> SQUIRREL =
            ENTITY_TYPES.register("squirrel",
                    () -> EntityType.Builder.of(SquirrelEntity::new, MobCategory.CREATURE)
                            .sized(0.7f,0.7f)
                            .build(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "squirrel").toString()));
    public static final Supplier<EntityType<WCatEntity>> WCAT =
            ENTITY_TYPES.register("warrior_cat",
                    () -> EntityType.Builder.of(WCatEntity::new, MobCategory.CREATURE)
                            .sized(0.69f,0.69f)
                            .build(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "warrior_cat").toString()));

    public static final Supplier<EntityType<PigeonEntity>> PIGEON =
            ENTITY_TYPES.register("pigeon",
                    () -> EntityType.Builder.of(PigeonEntity::new, MobCategory.CREATURE)
                            .sized(0.9f,0.9f)
                            .build(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "pigeon").toString()));

    public static final Supplier<EntityType<BadgerEntity>> BADGER =
            ENTITY_TYPES.register("badger",
                    () -> EntityType.Builder.of(BadgerEntity::new, MobCategory.CREATURE)
                            .sized(1.1f,1.1f)
                            .build(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "badger").toString()));

    public static final Supplier<EntityType<EagleEntity>> EAGLE =
            ENTITY_TYPES.register("golden_eagle",
                    () -> EntityType.Builder.of(EagleEntity::new, MobCategory.CREATURE)
                            .sized(1f,1f)
                            .build(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "golden_eagle").toString()));

    public static final Supplier<EntityType<LizardEntity>> LIZARD =
            ENTITY_TYPES.register("lizard",
                    () -> EntityType.Builder.of(LizardEntity::new, MobCategory.CREATURE)
                            .sized(0.30f,0.30f)
                            .build(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "lizard").toString()));

    public static final Supplier<EntityType<LizardTailEntity>> LIZARD_TAIL =
            ENTITY_TYPES.register("lizard_tail",
                    () -> EntityType.Builder.of(LizardTailEntity::new, MobCategory.CREATURE)
                            .sized(0.30f,0.30f)
                            .build(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "lizard_tail").toString()));


    public static final Supplier<EntityType<MossBallEntity>> MOSS_BALL =
            ENTITY_TYPES.register("moss_ball",
                    () -> EntityType.Builder.<MossBallEntity>of(MossBallEntity::new, MobCategory.MISC)
                            .sized(0.35f, 0.35f)
                            .build(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "moss_ball").toString()));



    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
