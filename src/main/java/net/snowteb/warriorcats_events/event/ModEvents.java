package net.snowteb.warriorcats_events.event;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.entity.ModEntities;
import net.snowteb.warriorcats_events.entity.custom.*;
import net.snowteb.warriorcats_events.util.ModAttributes;
import net.snowteb.warriorcats_events.zconfig.WCEPreyItemsConfig;

@EventBusSubscriber(modid = WarriorCatsEvents.MODID)
public class ModEvents {

    /**
     * Valid spawns registry
     */
    @SubscribeEvent
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(
                ModEntities.WCAT.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (entityType, level, spawnReason, pos, random) ->
                        level.getBlockState(pos.below()).isSolid()
                                && level.getFluidState(pos).isEmpty()
                                && level.getFluidState(pos.below()).isEmpty(),
                RegisterSpawnPlacementsEvent.Operation.REPLACE
        );

        event.register(
                ModEntities.SQUIRREL.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (entityType, level, spawnReason, pos, random) ->
                        level.getBlockState(pos.below()).isSolid()
                                && level.getFluidState(pos).isEmpty()
                                && level.getFluidState(pos.below()).isEmpty(),
                RegisterSpawnPlacementsEvent.Operation.REPLACE
        );
        event.register(
                ModEntities.MOUSE.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (entityType, level, spawnReason, pos, random) ->
                        level.getBlockState(pos.below()).isSolid()
                                && level.getFluidState(pos).isEmpty()
                                && level.getFluidState(pos.below()).isEmpty(),
                RegisterSpawnPlacementsEvent.Operation.REPLACE
        );
        event.register(
                ModEntities.PIGEON.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (entityType, level, spawnReason, pos, random) ->
                        level.getBlockState(pos.below()).isSolid()
                                && level.getFluidState(pos).isEmpty()
                                && level.getFluidState(pos.below()).isEmpty(),
                RegisterSpawnPlacementsEvent.Operation.REPLACE

        );
        event.register(
                ModEntities.BADGER.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (entityType, level, spawnReason, pos, random) ->
                        level.getBlockState(pos.below()).isSolid()
                                && level.getFluidState(pos).isEmpty()
                                && level.getFluidState(pos.below()).isEmpty(),
                RegisterSpawnPlacementsEvent.Operation.REPLACE

        );

        event.register(
                ModEntities.EAGLE.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (entityType, level, spawnReason, pos, random) ->
                        level.getFluidState(pos).isEmpty()
                                && level.getFluidState(pos.below()).isEmpty(),
                RegisterSpawnPlacementsEvent.Operation.REPLACE

        );

        event.register(
                ModEntities.LIZARD.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (entityType, level, spawnReason, pos, random) ->
                        level.getFluidState(pos).isEmpty() && level.getFluidState(pos.below()).isEmpty() && !level.getBlockState(pos).is(Blocks.SNOW),
                RegisterSpawnPlacementsEvent.Operation.REPLACE
        );

    }

    @SubscribeEvent
    public static void entityAttributeEvent(EntityAttributeCreationEvent event) {
        event.put(ModEntities.MOUSE.get(), MouseEntity.setAttributes().build());
        event.put(ModEntities.SQUIRREL.get(), SquirrelEntity.setAttributes().build());
        event.put(ModEntities.WCAT.get(), WCatEntity.setAttributes().build());
        event.put(ModEntities.PIGEON.get(), PigeonEntity.setAttributes().build());
        event.put(ModEntities.BADGER.get(), BadgerEntity.setAttributes().build());
        event.put(ModEntities.EAGLE.get(), EagleEntity.setAttributes().build());
        event.put(ModEntities.LIZARD.get(), LizardEntity.setAttributes().build());
        event.put(ModEntities.LIZARD_TAIL.get(), LizardTailEntity.setAttributes().build());

    }

    @SubscribeEvent
    public static void onEntityAttributeModification(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, ModAttributes.PLAYER_JUMP);
    }


    @SubscribeEvent
    public static void onConfigLoad(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == WCEPreyItemsConfig.SPEC) {
            WCEPreyItemsConfig.getItemListFromString();
        }
    }

    @SubscribeEvent
    public static void onConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == WCEPreyItemsConfig.SPEC) {
            WCEPreyItemsConfig.getItemListFromString();
        }
    }


}