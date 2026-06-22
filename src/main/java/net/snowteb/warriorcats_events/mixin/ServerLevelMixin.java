package net.snowteb.warriorcats_events.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.snowteb.warriorcats_events.clan.ClanData;
import net.snowteb.warriorcats_events.entity.ModEntities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {

    @Inject(method = "tickChunk", at = @At("HEAD"))
    public void spawnPreyTick(LevelChunk pChunk, int pRandomTickSpeed, CallbackInfo ci) {

        if (pChunk.getLevel() instanceof ServerLevel sLevel) {

            ChunkPos pos = pChunk.getPos();
            RandomSource random = sLevel.getRandom();

            if ((sLevel.getGameTime() + pos.x + pos.z) % 7200 == 0 && random.nextFloat() < 0.15) {
                ClanData data = ClanData.get(sLevel.getServer().overworld());
                List<ClanData.Clan> clans = data.getAllClans();

                boolean isChunkTerritory = false;

                for (ClanData.Clan clan : clans) {
                    for (ChunkPos chunkPos : clan.claimedTerritory.keySet()) {
                        if (!chunkPos.equals(clan.coreTerritory)) {
                            if (chunkPos.equals(pos)) {
                                if (clan.claimedTerritory.get(chunkPos).time > (data.getMaxTerritoryTime() / 2.8)) {
                                    isChunkTerritory = true;
                                }
                            }
                        }
                    }
                }

                if (isChunkTerritory) {

                    int x = pos.getMinBlockX() + random.nextInt(16);
                    int z = pos.getMinBlockZ() + random.nextInt(16);


                    EntityType<? extends Mob> type;

                    float chanceOfType = random.nextFloat();

                    int maxCount = 3;

                    if (chanceOfType < 0.004) {
                        type = ModEntities.EAGLE.get();
                        maxCount = 1;
                    } else if (chanceOfType < 0.008) {
                        int roll = random.nextInt(4);
                        if (roll == 0) {
                            type = EntityType.FOX;
                        } else if (roll == 1) {
                            type = ModEntities.BADGER.get();
                        } else if (roll == 2) {
                            type = EntityType.WOLF;
                        } else {
                            type = ModEntities.BADGER.get();
                        }
                        maxCount = 2;
                    } else {
                        int entityToSpawn = random.nextInt(5);
                        type = switch (entityToSpawn) {
                            case 0 -> ModEntities.SQUIRREL.get();
                            case 1 -> ModEntities.MOUSE.get();
                            case 2 -> ModEntities.PIGEON.get();
                            case 3 -> EntityType.RABBIT;
                            default -> ModEntities.SQUIRREL.get();
                        };
                    }

                    AABB box = new AABB(
                            pos.getMinBlockX() - 12, sLevel.getMinBuildHeight(), pos.getMinBlockZ() - 12,
                            pos.getMaxBlockX() + 12, sLevel.getMaxBuildHeight(), pos.getMaxBlockZ() + 12
                    );

                    List<LivingEntity> entities = sLevel.getEntitiesOfClass(
                            LivingEntity.class,
                            box,
                            ent -> ent.getType() == type && ent.isAlive()
                    );

                    if (entities.size() < 2) {

                        int amount = type == ModEntities.EAGLE.get() ? 1 : 1 + random.nextInt(maxCount);

                        for (int i = 0; i < amount; i++) {

                            int offsetX = x + random.nextInt(8) - 4;
                            int offsetZ = z + random.nextInt(8) - 4;
                            int offsetY = sLevel.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, offsetX, offsetZ);

                            BlockPos finalPos = new BlockPos(offsetX, offsetY, offsetZ);

                            Mob newEntity = type.create(sLevel);

                            if (newEntity != null) {
                                newEntity.moveTo(offsetX + 0.5, offsetY, offsetZ + 0.5,
                                        random.nextFloat() * 360F, 0);

                                if (!NaturalSpawner.isSpawnPositionOk(SpawnPlacements.Type.ON_GROUND, sLevel, finalPos, newEntity.getType())) continue;

                                if (newEntity.checkSpawnRules(sLevel, MobSpawnType.MOB_SUMMONED) && newEntity.checkSpawnObstruction(sLevel)) {
                                    sLevel.addFreshEntity(newEntity);
                                    sLevel.sendParticles(
                                            ParticleTypes.CLOUD, offsetX + 0.5, offsetY + 0.2, offsetZ + 0.5,
                                            5, 0.2, 0.1, 0.2, 0.01
                                    );
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
