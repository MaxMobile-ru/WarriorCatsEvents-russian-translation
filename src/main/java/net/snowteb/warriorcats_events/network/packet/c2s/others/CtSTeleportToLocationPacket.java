package net.snowteb.warriorcats_events.network.packet.c2s.others;

import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.item.ModItems;
import net.snowteb.warriorcats_events.zconfig.WCEServerConfig;

import java.util.Optional;

import static net.minecraft.world.level.Level.OVERWORLD;

public class CtSTeleportToLocationPacket implements CustomPacketPayload {

    private final int data;

    public CtSTeleportToLocationPacket(int data) {
        this.data = data;
    }

    public static CtSTeleportToLocationPacket decode(FriendlyByteBuf buf) {
        return new CtSTeleportToLocationPacket(buf.readInt());
    }


    public static void encode(CtSTeleportToLocationPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.data);
    }

    public static void handle(CtSTeleportToLocationPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) ctx.player();

            ServerLevel level = (ServerLevel) player.level();
            int type = packet.data;

            if (WCEServerConfig.SERVER.TELEPORT_WHEN_JOIN.get()) {
                if (type == 1){
                    locateBiomeAsync(level, player,
                            level.registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(Biomes.TAIGA)
                    );
                }
                if (type == 2){
                    locateBiomeAsync(level, player,
                            level.registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(Biomes.PLAINS)
                    );
                }
                if (type == 3){
                    locateBiomeAsync(level, player,
                            level.registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(Biomes.SAVANNA)
                    );
                }
                if (type == 4){
                    locateBiomeAsync(level, player,
                            level.registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(Biomes.FOREST)
                    );
                }
                if (type == 5) {
                    locateStructureAsync(
                            level,
                            getStructure(level, "minecraft:village_taiga"),
                            player
                    );
                }
                if (type == 6){
                    locateStructureAsync(
                            level,
                            getStructure(level, "minecraft:village_plains"),
                            player
                    );
                }
                if (type == 7){
                    locateStructureAsync(
                            level,
                            getStructure(level, "minecraft:village_savanna"),
                            player
                    );
                }
                if (type == 8){
                    locateStructureAsync(
                            level,
                            getStructure(level, "minecraft:jungle_pyramid"),
                            player
                    );
                }
            }

            CompoundTag persistent;
            if (player.getPersistentData().contains(Player.PERSISTED_NBT_TAG)) {
                persistent = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
            } else {
                persistent = new CompoundTag();
                player.getPersistentData().put(Player.PERSISTED_NBT_TAG, persistent);
            }

            if (!persistent.getBoolean("warriorcats_events.starting_items")) {
                player.getInventory().add(new ItemStack(ModItems.WARRIORS_GUIDE.get()));
                player.getInventory().add(new ItemStack(ModItems.CLAWS.get()));
                player.getInventory().add(new ItemStack(ModItems.WHISKERS.get()));
                player.getInventory().add(new ItemStack(Items.COD));
                player.sendSystemMessage(Component.literal("You have received your own [Claws] and [A Warrior's Guide]!").withStyle(ChatFormatting.YELLOW));
                player.sendSystemMessage(Component.literal("Get support and stay tuned for future updates: ").append(
                        Component.literal("[Discord]")
                                .withStyle(style -> style
                                        .withColor(0x579dff)
                                        .withUnderlined(true)
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/SkYvZr9DBb"))
                                )
                ));

                player.sendSystemMessage(Component.literal("If you ever feel lost, you can always check the ").append(
                        Component.literal("[Wiki]")
                                .withStyle(style -> style
                                        .withColor(0xfc8e4a)
                                        .withUnderlined(true)
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/Snowo69/WarriorCatsEvents/wiki"))
                                )
                )
                                .append(Component.literal("!").withStyle(ChatFormatting.WHITE))
                );

                player.sendSystemMessage(Component.literal(""));
                player.sendSystemMessage(Component.literal("Your journey begins now...")
                        .withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));


                persistent.putBoolean("warriorcats_events.starting_items", true);
                if (player.isInvulnerable()) player.setInvulnerable(false);
            }

        });
    }

    private static void summonParticles(ServerLevel level, ServerPlayer player) {

        MinecraftServer server = level.getServer();
        int delay = 140;

        server.tell(new TickTask(
                server.getTickCount() + delay,
                () -> {
                    level.getChunkAt(player.blockPosition());
                    level.playSound(null, player.blockPosition() ,SoundEvents.CAT_STRAY_AMBIENT, SoundSource.MASTER);

                    level.sendParticles(
                            ParticleTypes.CLOUD,
                            player.getX(),
                            player.getY() - 6,
                            player.getZ(),
                            1200,
                            0.1, 1.5, 0.1,
                            0.2
                    );
                    level.sendParticles(
                            ParticleTypes.CAMPFIRE_SIGNAL_SMOKE,
                            player.getX(),
                            player.getY() - 6,
                            player.getZ(),
                            1000,
                            0.3, 1.5, 0.3,
                            0.2
                    );
                }
        ));
    }

    public static Holder<Structure> getStructure(ServerLevel level, String id) {
        return level.registryAccess()
                .registryOrThrow(Registries.STRUCTURE)
                .getHolderOrThrow(
                        ResourceKey.create(
                                Registries.STRUCTURE, ResourceLocation.parse(id)
                        )
                );
    }

    public static void teleportPlayer(ServerPlayer player, BlockPos pos) {

        ServerLevel level = player.serverLevel();

        level.getChunkAt(pos);

        pos = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, pos);

        player.setRespawnPosition(OVERWORLD, pos, player.getYRot(), true, false);

        ChunkPos chunkPos = new ChunkPos(pos);

        level.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, chunkPos, 1, player.getId());

        player.connection.teleport(pos.getX() + 0.5, pos.getY() + 30, pos.getZ() + 0.5,
                player.getYRot(), player.getXRot());

        player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 200));

    }


    public static void locateStructureAsync(ServerLevel level, Holder<Structure> structure, ServerPlayer player) {

        BlockPos origin = player.blockPosition();

        WarriorCatsEvents.singleThreadExecutor.execute(() -> {
            Optional<BlockPos> optional = findStructure(level, origin, structure, 100);
            level.getServer().execute(() -> optional.ifPresentOrElse(
                    pos -> {
                        teleportPlayer(player, pos);
                        summonParticles(level, player);
                    },
                    () -> player.sendSystemMessage(Component.literal("Couldn't find a nearby location.").withStyle(ChatFormatting.RED))
            ));
        });

    }
    public static void locateBiomeAsync(ServerLevel level, ServerPlayer player, Holder<Biome> biome) {

        BlockPos origin = player.blockPosition();

        player.sendSystemMessage(Component.literal("Teleporting...").withStyle(ChatFormatting.DARK_GRAY));

        WarriorCatsEvents.singleThreadExecutor.execute(() -> {
            Optional<BlockPos> optional = findBiome(level, origin, biome, 4000);
            level.getServer().execute(() -> optional.ifPresentOrElse(
                    pos -> {

                        teleportPlayer(player, pos);
                        summonParticles(level, player);
                    },
                    () -> player.sendSystemMessage(Component.literal("Couldn't find a nearby location.").withStyle(ChatFormatting.RED))
            ));
        });

    }

    public static Optional<BlockPos> findStructure(ServerLevel level, BlockPos origin, Holder<Structure> structure, int radius) {

        HolderSet<Structure> set = HolderSet.direct(structure);

        Pair<BlockPos, Holder<Structure>> result = level.getChunkSource().getGenerator()
                        .findNearestMapStructure(level, set, origin, radius, false);

        if (result == null) return Optional.empty();

        return Optional.of(result.getFirst());
    }
    public static Optional<BlockPos> findBiome(ServerLevel level, BlockPos origin, Holder<Biome> biome, int radius) {

        Pair<BlockPos, Holder<Biome>> result =
                level.getChunkSource().getGenerator().getBiomeSource()
                        .findClosestBiome3d(origin, radius,
                                64, 32,
                                holder -> holder.equals(biome),
                                level.getChunkSource().randomState().sampler(),
                                level
                        );

        if (result == null) return Optional.empty();

        return Optional.of(result.getFirst());
    }

    public static final Type<CtSTeleportToLocationPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "teleport_to_location"));

    public static final StreamCodec<FriendlyByteBuf, CtSTeleportToLocationPacket> CODEC =
            StreamCodec.of(
                    (buf, pkt) -> encode(pkt, buf),
                    buf -> decode(buf)
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}



