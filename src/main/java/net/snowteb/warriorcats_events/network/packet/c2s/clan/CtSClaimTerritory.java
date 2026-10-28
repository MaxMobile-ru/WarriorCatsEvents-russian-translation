package net.snowteb.warriorcats_events.network.packet.c2s.clan;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
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
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.block.ModBlocks;
import net.snowteb.warriorcats_events.block.custom.TreeStumpBlock;
import net.snowteb.warriorcats_events.block.entity.TreeStumpBlockEntity;
import net.snowteb.warriorcats_events.clan.ClanData;
import net.snowteb.warriorcats_events.clan.ClanObjects;
import net.snowteb.warriorcats_events.clanplayerdata.WCEPlayerData;
import net.snowteb.warriorcats_events.clanplayerdata.WCEPlayerDataProvider;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.s2c.clan.DisplayClanMenuMessage;
import net.snowteb.warriorcats_events.zconfig.WCEServerConfig;

import java.util.UUID;
import java.util.function.Supplier;

public class CtSClaimTerritory {
    private final String name;

    public CtSClaimTerritory(String name) {
        this.name = name;
    }

    public static CtSClaimTerritory decode(FriendlyByteBuf buf) {
        String name = buf.readUtf(30);
        return new CtSClaimTerritory(name);
    }

    public static void encode(CtSClaimTerritory packet, FriendlyByteBuf buf) {
        buf.writeUtf(packet.name);
    }

    public static void handle(CtSClaimTerritory packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            ServerLevel overworldLevel = player.getServer().overworld();

            UUID uuid = player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                    .map(WCEPlayerData::getCurrentClanUUID).orElse(ClanData.EMPTY_UUID);


            if (player.level().dimension() != Level.OVERWORLD) {
                ModPackets.sendToPlayer(new DisplayClanMenuMessage(Component.translatable("territory.not_available_in_dimensions"),true), player);
                return;
            }

            if (uuid.equals(ClanData.EMPTY_UUID)) {
                ModPackets.sendToPlayer(new DisplayClanMenuMessage(Component.translatable("clan.player_not_clan"),true), player);
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
                ModPackets.sendToPlayer(new DisplayClanMenuMessage(Component.translatable("clan.no_permissions"),true), player);
                return;
            }

            if (clan.claimedTerritory.containsKey(currentPosition)) {
                ModPackets.sendToPlayer(new DisplayClanMenuMessage(Component.translatable("clan.territory_already_claimed"),true), player);
                return;
            }

            if (!(
                    overworldLevel.getBlockState(player.blockPosition()).isAir()
                    || overworldLevel.getBlockState(player.blockPosition()).is(BlockTags.FLOWERS)
                    || overworldLevel.getBlockState(player.blockPosition()).is(Blocks.GRASS)
            )
                    || !overworldLevel.getBlockState(player.blockPosition().below()).isSolid()) {
                ModPackets.sendToPlayer(new DisplayClanMenuMessage(Component.translatable("territory.invalid_position"),true), player);
                return;
            }

            if (clan.claimedTerritory.size() >= WCEServerConfig.SERVER.MAX_TERRITORY_SIZE.get()) {
                ModPackets.sendToPlayer(new DisplayClanMenuMessage(Component.translatable("territory.max_territory"),true), player);
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
                    data.registerLog(overworldLevel, clan.clanUUID, log, ClanObjects.ClanLogEntry.Type.OTHERS);
                    clan.coreTerritory = currentPosition;
                    ModPackets.sendToPlayer(new DisplayClanMenuMessage(Component.translatable("territory.core_territory_claimed"),false), player);
                    handled = true;
                } else {
                    ModPackets.sendToPlayer(new DisplayClanMenuMessage(Component.translatable("territory.territory_already_claimed"),true), player);
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
                        data.registerLog(overworldLevel, clan.clanUUID, log, ClanObjects.ClanLogEntry.Type.OTHERS);
                        ModPackets.sendToPlayer(new DisplayClanMenuMessage(Component.translatable("territory.new_territory_succesfully_claimed"),false), player);
                        handled = true;
                    } else {
                        ModPackets.sendToPlayer(new DisplayClanMenuMessage(Component.translatable("territory.territory_already_claimed"),true), player);
                        player.giveExperiencePoints(850);
                    }

                } else {
                    ModPackets.sendToPlayer(new DisplayClanMenuMessage(Component.translatable("territory.territory_not_connected"),true), player);
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
                        tree.setTimeUntilRenewScent((int) ((WCEServerConfig.SERVER.MAX_TERRITORY_TIME.get()*20*60)*WCEServerConfig.SERVER.TREE_STUMP_MULTIPLIER.get()));
                        tree.setOwnerClanColor(clan.color);
                        tree.setOwnerClanName(clan.name);
                        tree.setTerritoryName(packet.name);
                    }
                }
            }

        });

        ctx.get().setPacketHandled(true);
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
}
