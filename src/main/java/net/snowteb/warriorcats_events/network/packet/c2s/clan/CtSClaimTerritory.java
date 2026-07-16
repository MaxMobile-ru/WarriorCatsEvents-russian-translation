package net.snowteb.warriorcats_events.network.packet.c2s.clan;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.attachments.ModAttachments;
import net.snowteb.warriorcats_events.block.ModBlocks;
import net.snowteb.warriorcats_events.block.custom.TreeStumpBlock;
import net.snowteb.warriorcats_events.block.entity.TreeStumpBlockEntity;
import net.snowteb.warriorcats_events.clan.ClanData;
import net.snowteb.warriorcats_events.zconfig.WCEServerConfig;

import java.util.UUID;

public class CtSClaimTerritory implements CustomPacketPayload {
    private final String name;

    public CtSClaimTerritory(String name) {
        this.name = name;
    }

    public static CtSClaimTerritory decode(FriendlyByteBuf buf) {
        String name = buf.readUtf();
        return new CtSClaimTerritory(name);
    }

    public static void encode(CtSClaimTerritory packet, FriendlyByteBuf buf) {
        buf.writeUtf(packet.name);
    }

    public static void handle(CtSClaimTerritory packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) ctx.player();

            ServerLevel overworldLevel = player.getServer().overworld();

            UUID uuid = player.getData(ModAttachments.PLAYER_WCE_DATA).getCurrentClanUUID();

            String morphName = player.getData(ModAttachments.PLAYER_WCE_DATA).getMorphName();

            if (player.level().dimension() != Level.OVERWORLD) {
                player.sendSystemMessage(Component.translatable("territory.not_available_in_dimensions").withStyle(ChatFormatting.RED));
                return;
            }

            if (uuid.equals(ClanData.EMPTY_UUID)) {
                player.sendSystemMessage(Component.translatable("clan.player_not_clan").withStyle(ChatFormatting.RED));
                return;
            }

            ClanData data = ClanData.get(overworldLevel);
            ClanData.Clan clan = data.getClan(uuid);
            if (clan == null) {
                player.sendSystemMessage(Component.literal("Invalid clan provided.").withStyle(ChatFormatting.RED));
                return;
            }

            ChunkPos currentPosition = player.chunkPosition();

            if (!data.canManage(clan, player.getUUID())) {
                player.sendSystemMessage(Component.translatable("clan.no_permissions").withStyle(ChatFormatting.RED));
                return;
            }

            if (clan.claimedTerritory.containsKey(currentPosition)) {
                player.sendSystemMessage(Component.translatable("clan.territory_already_claimed").withStyle(ChatFormatting.RED));
                return;
            }

            if (!(
                    overworldLevel.getBlockState(player.blockPosition()).isAir()
                    || overworldLevel.getBlockState(player.blockPosition()).is(BlockTags.FLOWERS)
                    || overworldLevel.getBlockState(player.blockPosition()).is(Blocks.SHORT_GRASS)
                    || overworldLevel.getBlockState(player.blockPosition()).is(Blocks.TALL_GRASS)
            )
                    || !overworldLevel.getBlockState(player.blockPosition().below()).isSolid()) {
                player.sendSystemMessage(Component.translatable("territory.invalid_position").withStyle(ChatFormatting.RED));
                return;
            }

            if (clan.claimedTerritory.size() >= WCEServerConfig.SERVER.MAX_TERRITORY_SIZE.get()) {
                player.sendSystemMessage(Component.translatable("territory.max_territory").withStyle(ChatFormatting.RED));
                return;
            }

            boolean handled = false;

            if (clan.claimedTerritory.isEmpty()) {

                if (!packet.takeXPfromPlayer(player, 450)) return;

                Component log = Component.translatable("territory.core_territory_set_log",
                        ClanData.logFormattedPlayerName(player),
                        Component.literal(packet.name).withStyle(ChatFormatting.LIGHT_PURPLE),
                        Component.literal(
                                String.format("X=%d, Z=%d", currentPosition.x, currentPosition.z)
                        ).withStyle(ChatFormatting.AQUA));


                boolean success = data.claimChunk(clan.clanUUID, currentPosition, packet.name);
                if (success) {
                    data.registerLog(overworldLevel, clan.clanUUID, log);
                    clan.coreTerritory = currentPosition;
                    player.sendSystemMessage(Component.translatable("territory.core_territory_claimed"));
                    handled = true;
                } else {
                    player.sendSystemMessage(Component.translatable("territory.territory_already_claimed").withStyle(ChatFormatting.RED));
                    player.giveExperiencePoints(450);
                }

            } else {

                if (!packet.takeXPfromPlayer(player, 850)) return;

                boolean isTerritoryConnected = false;

                ChunkPos[] neighbors = new ChunkPos[] {
                        new ChunkPos(currentPosition.x + 1, currentPosition.z),
                        new ChunkPos(currentPosition.x - 1, currentPosition.z),
                        new ChunkPos(currentPosition.x, currentPosition.z + 1),
                        new ChunkPos(currentPosition.x, currentPosition.z - 1)
                };

                for (ChunkPos pos : neighbors) {
                    if (clan.claimedTerritory.containsKey(pos)) {
                        isTerritoryConnected = true;
                        break;
                    }
                }

                if (isTerritoryConnected) {

                    Component log = Component.translatable("territory.new_territory_claimed",
                            ClanData.logFormattedPlayerName(player),
                            Component.literal(packet.name).withStyle(ChatFormatting.LIGHT_PURPLE),
                            Component.literal(
                                    String.format("X=%d, Z=%d", currentPosition.x, currentPosition.z)
                            ).withStyle(ChatFormatting.AQUA));


                    boolean result = data.claimChunk(clan.clanUUID, currentPosition, packet.name);
                    if (result) {
                        data.registerLog(overworldLevel, clan.clanUUID, log);
                        player.sendSystemMessage(Component.translatable("territory.new_territory_succesfully_claimed"));
                        handled = true;
                    } else {
                        player.sendSystemMessage(Component.translatable("territory.territory_already_claimed").withStyle(ChatFormatting.RED));
                        player.giveExperiencePoints(850);
                    }

                } else {
                    player.sendSystemMessage(Component.translatable("territory.territory_not_connected").withStyle(ChatFormatting.RED));
                    player.giveExperiencePoints(850);
                }
            }

            if (handled) {
                data.syncTerritoriesToClients(overworldLevel);

                data.removeMarkersFromChunk(overworldLevel,  currentPosition);

                Direction facing = player.getDirection().getOpposite();

                TreeStumpBlock.StumpVariant[] variants = TreeStumpBlock.StumpVariant.values();

                TreeStumpBlock.StumpVariant randomVariant = variants[overworldLevel.getRandom().nextInt(variants.length)];

                Holder<Biome> biome = overworldLevel.getBiome(player.blockPosition());
                if (biome.is(BiomeTags.IS_FOREST)) {
                    if (overworldLevel.getRandom().nextBoolean()) {
                        randomVariant = TreeStumpBlock.StumpVariant.OAK;
                    } else {
                        randomVariant = TreeStumpBlock.StumpVariant.BIRCH;
                    }
                } else if (biome.is(BiomeTags.IS_TAIGA)) {
                    randomVariant = TreeStumpBlock.StumpVariant.SPRUCE;
                } else if (biome.is(Biomes.CHERRY_GROVE)) {
                    randomVariant = TreeStumpBlock.StumpVariant.CHERRY;
                } else if (biome.is(Biomes.JUNGLE)) {
                    randomVariant = TreeStumpBlock.StumpVariant.JUNGLE;
                } else if (biome.is(Biomes.SAVANNA)) {
                    randomVariant = TreeStumpBlock.StumpVariant.ACACIA;
                } else if (biome.is(Biomes.DARK_FOREST)) {
                    randomVariant = TreeStumpBlock.StumpVariant.DARK_OAK;
                }

                BlockState blockState = ModBlocks.TREE_STUMP.get().defaultBlockState()
                        .setValue(HorizontalDirectionalBlock.FACING, facing)
                        .setValue(TreeStumpBlock.VARIANT, randomVariant);

                overworldLevel.setBlock(player.blockPosition(),  blockState, 3);


                BlockState placedBlock = overworldLevel.getBlockState(player.blockPosition());

                if (placedBlock.getBlock() instanceof TreeStumpBlock) {
                    BlockEntity entity = overworldLevel.getBlockEntity(player.blockPosition());
                    if (entity instanceof TreeStumpBlockEntity tree) {
                        tree.setOwnerClanUUID(clan.clanUUID);
                        tree.setTerritoryPos(currentPosition);
                        tree.setTimeUntilRenewScent((WCEServerConfig.SERVER.MAX_TERRITORY_TIME.get()*20*60)/8);
                        tree.setOwnerClanColor(clan.color);
                        tree.setOwnerClanName(clan.name);
                        tree.setTerritoryName(packet.name);
                    }
                }
            }

        });
    }

    private boolean takeXPfromPlayer(ServerPlayer player, int cost) {

        if (player.getAbilities().instabuild) return true;
        int remaining = cost - player.totalExperience;

        if (player.totalExperience < cost) {
            player.sendSystemMessage(Component.translatable("generic.need_more_xp", remaining)
                    .withStyle(ChatFormatting.RED));
            return false;
        }

        player.giveExperiencePoints(-cost);
        return true;
    }

    public static final Type<CtSClaimTerritory> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "claim_territory"));

    public static final StreamCodec<FriendlyByteBuf, CtSClaimTerritory> CODEC =
            StreamCodec.of(
                    (buf, pkt) -> encode(pkt, buf),
                    buf -> decode(buf)
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
