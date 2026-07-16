package net.snowteb.warriorcats_events.event;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.entity.ModEntities;
import net.snowteb.warriorcats_events.entity.custom.*;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.util.ModAttributes;
import net.snowteb.warriorcats_events.zconfig.WCEPreyItemsConfig;

@Mod.EventBusSubscriber(modid = WarriorCatsEvents.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBusEvents {

    /**
     * Valid spawns registry
     */
    @SubscribeEvent
    public static void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            SpawnPlacements.register(
                    ModEntities.WCAT.get(),
                    SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    (entityType, level, spawnReason, pos, random) ->
                            level.getBlockState(pos.below()).isSolid()
                                    && !level.getBlockState(pos).is(BlockTags.LEAVES)
                                    && !level.getBlockState(pos.above()).is(BlockTags.LEAVES)
                                    && level.getFluidState(pos).isEmpty()
                                    && level.getFluidState(pos.below()).isEmpty()

            );
            SpawnPlacements.register(
                    ModEntities.SQUIRREL.get(),
                    SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    (entityType, level, spawnReason, pos, random) ->
                            level.getBlockState(pos.below()).isSolid()
                                    && !level.getBlockState(pos).is(BlockTags.LEAVES)
                                    && !level.getBlockState(pos.above()).is(BlockTags.LEAVES)
                                    && level.getFluidState(pos).isEmpty()
                                    && level.getFluidState(pos.below()).isEmpty()

            );
            SpawnPlacements.register(
                    ModEntities.MOUSE.get(),
                    SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    (entityType, level, spawnReason, pos, random) ->
                            level.getBlockState(pos.below()).isSolid()
                                    && !level.getBlockState(pos).is(BlockTags.LEAVES)
                                    && !level.getBlockState(pos.above()).is(BlockTags.LEAVES)
                                    && level.getFluidState(pos).isEmpty()
                                    && level.getFluidState(pos.below()).isEmpty()

            );
            SpawnPlacements.register(
                    ModEntities.PIGEON.get(),
                    SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    (entityType, level, spawnReason, pos, random) ->
                            level.getBlockState(pos.below()).isSolid()
                                    && !level.getBlockState(pos).is(BlockTags.LEAVES)
                                    && !level.getBlockState(pos.above()).is(BlockTags.LEAVES)
                                    && level.getFluidState(pos).isEmpty()
                                    && level.getFluidState(pos.below()).isEmpty()

            );
            SpawnPlacements.register(
                    ModEntities.BADGER.get(),
                    SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    (entityType, level, spawnReason, pos, random) ->
                            level.getBlockState(pos.below()).isSolid()
                                    && !level.getBlockState(pos).is(BlockTags.LEAVES)
                                    && !level.getBlockState(pos.above()).is(BlockTags.LEAVES)
                                    && level.getFluidState(pos).isEmpty()
                                    && level.getFluidState(pos.below()).isEmpty()

            );

            SpawnPlacements.register(
                    ModEntities.EAGLE.get(),
                    SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    (entityType, level, spawnReason, pos, random) ->
                            level.getFluidState(pos).isEmpty() && level.getFluidState(pos.below()).isEmpty()

            );

            SpawnPlacements.register(
                    ModEntities.LIZARD.get(),
                    SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    (entityType, level, spawnReason, pos, random) ->
                            level.getFluidState(pos).isEmpty() && level.getFluidState(pos.below()).isEmpty() && !level.getBlockState(pos).is(Blocks.SNOW)
            );

        });


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

    /**
     * This adds the custom attribute to the player.
     */
    @SubscribeEvent
    public static void onEntityAttributeModification(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, ModAttributes.PLAYER_JUMP.get());
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