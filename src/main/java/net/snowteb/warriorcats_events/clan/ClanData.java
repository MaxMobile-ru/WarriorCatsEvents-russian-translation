package net.snowteb.warriorcats_events.clan;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.saveddata.SavedData;
import net.snowteb.warriorcats_events.block.entity.TreeStumpBlockEntity;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCGenetics;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.s2c.clan.S2CSyncClanDataPacket;
import net.snowteb.warriorcats_events.network.packet.s2c.clan.SyncTerritoryToClients;
import net.snowteb.warriorcats_events.zconfig.WCEServerConfig;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ClanData extends SavedData {
    public Map<UUID, Clan> clans = new HashMap<>();
    public Map<UUID, String> playerMorphNames = new HashMap<>();
    public Map<UUID, WCGenetics.PackedGeneticData> playerMorphData = new HashMap<>();



    public static final UUID EMPTY_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    private static final int DATA_VERSION = 1;

    public enum ClanPlayerRank {
        LEADER,
        DEPUTY,
        MEDICINE,
        MEDICINEAPP,
        WARRIOR,
        ELDER,
        QUEEN,
        APPRENTICE,
        KIT
    }

    public enum ClanPermissions {
        OWNER,
        ADMIN,
        MEMBER,
        GUEST
    }


    public static class ClanLogEntry {
        public long gameTimeID;
        public Component message;

        public ClanLogEntry(long gameTimeID, Component message) {
            this.gameTimeID = gameTimeID;
            this.message = message;
        }

        public CompoundTag save() {
            CompoundTag tag = new CompoundTag();
            tag.putLong("Time", gameTimeID);
            tag.putString("Message", Component.Serializer.toJson(message));
            return tag;
        }

        public static ClanLogEntry load(CompoundTag tag) {
            return new ClanLogEntry(
                    tag.getLong("Time"),
                    Component.Serializer.fromJson(tag.getString("Message"))
            );
        }
    }


    public static class ClanCat {
        public UUID catUUID;
        public String catGender;
        public Component catName;
        public int catVariant;
        public Component catAge;
        public String catRank;
        public Component catParents;

        public boolean onGeneticalSkin = false;
        public WCGenetics genetics = new WCGenetics();
        public WCGenetics chimeraGenetics = new WCGenetics();
        public WCGenetics.GeneticalVariants variants = new WCGenetics.GeneticalVariants();
        public WCGenetics.GeneticalChimeraVariants chimeraVariants = new WCGenetics.GeneticalChimeraVariants();


        public static ClanCat buildCat(WCatEntity cat) {
            ClanCat clanCat = new ClanCat();

            clanCat.catUUID = cat.getUUID();
            clanCat.catGender = cat.getGender() == 0 ? Component.translatable("generic.wcat.tomcat").getString()
                    : Component.translatable("generic.wcat.shecat").getString();
            clanCat.catName = cat.hasCustomName() ? cat.getCustomName() : Component.translatable("generic.wcat.unnamedcat");
            clanCat.catVariant = cat.getVariant();
            clanCat.catAge = cat.getAge() < 0
                    ? Component.translatable("generic.wcat.age_moons",
                    String.format("%.2f", cat.getAgeInMoons()))
                    : Component.translatable("generic.wcat.fully_grown");

            clanCat.catRank = cat.getRank().name();


            clanCat.onGeneticalSkin = cat.isOnGeneticalSkin();
            clanCat.genetics = cat.getGeneticsModule().getGenetics();
            clanCat.chimeraGenetics = cat.getGeneticsModule().getChimeraGenetics();
            clanCat.variants = cat.getGeneticsModule().getGenVariants();
            clanCat.chimeraVariants = cat.getGeneticsModule().getChimeraGenVariants();

            Component parentsText;
            Component catMother = cat.getMother();
            Component catFather = cat.getFather();

            if (catMother.getString().equals("None") && catFather.getString().equals("None")) {
                parentsText = Component.translatable("generic.wcat.parents_unknown");
            } else {
                if (catMother.getString().equals("None") && !catFather.getString().equals("None")) {
                    parentsText = Component.translatable("generic.wcat.parents_1",
                            catFather.copy().withStyle(ChatFormatting.AQUA));

                } else if (!catMother.getString().equals("None") && catFather.getString().equals("None")) {
                    parentsText = Component.translatable("generic.wcat.parents_1",
                            catMother.copy().withStyle(ChatFormatting.AQUA));
                } else {
                    parentsText = Component.translatable("generic.wcat.parents_2",
                            catMother.copy().withStyle(ChatFormatting.AQUA),
                            catFather.copy().withStyle(ChatFormatting.AQUA)
                            );
                }
            }

            clanCat.catParents = parentsText;

            return clanCat;
        }

        public CompoundTag save() {
            CompoundTag tag = new CompoundTag();

            tag.putUUID("UUID", this.catUUID);
            tag.putString("Gender", this.catGender);
            tag.putString("Name", Component.Serializer.toJson(this.catName));
            tag.putInt("Variant", this.catVariant);
            tag.putString("Age", Component.Serializer.toJson(this.catAge));
            tag.putString("Rank", this.catRank);
            tag.putString("Parents", Component.Serializer.toJson(this.catParents));

            {
                CompoundTag geneticsTag = new CompoundTag();

                geneticsTag.putBoolean("Genetical", onGeneticalSkin);

                geneticsTag.putString("ChestFur", genetics.chestFur);
                geneticsTag.putString("BellyFur", genetics.bellyFur);
                geneticsTag.putString("LegsFur", genetics.legsFur);
                geneticsTag.putString("HeadFur", genetics.headFur);
                geneticsTag.putString("CheekFur", genetics.cheekFur);
                geneticsTag.putString("BackFur", genetics.backFur);
                geneticsTag.putString("TailFur", genetics.tailFur);
                geneticsTag.putString("Bobtail", genetics.bobtail);

                geneticsTag.putString("Base", genetics.base);
                geneticsTag.putString("OrangeBase", genetics.orangeBase);
                geneticsTag.putString("WhiteRatio", genetics.whiteRatio);
                geneticsTag.putString("Albino", genetics.albino);
                geneticsTag.putString("Dilute", genetics.dilute);
                geneticsTag.putString("Agouti", genetics.agouti);
                geneticsTag.putString("TabbyStripes", genetics.tabbyStripes);
                geneticsTag.putString("EyeColorLeft", variants.eyeColorLeft);
                geneticsTag.putString("EyeColorRight", variants.eyeColorRight);
                geneticsTag.putString("EyesAnomaly", genetics.eyesAnomaly);
                geneticsTag.putString("Silver", genetics.silver);

                geneticsTag.putInt("Rufousing", variants.rufousingVariant);
                geneticsTag.putInt("BlueRufousing", variants.blueRufousingVariant);
                geneticsTag.putInt("OrangeBaseVariant", variants.orangeVar);
                geneticsTag.putInt("WhiteRatioVariant", variants.whiteVar);
                geneticsTag.putInt("AlbinoVariant", variants.albinoVar);
                geneticsTag.putInt("SilverVariant", variants.silverVar);

                geneticsTag.putInt("TabbyStripesVariant", variants.tabbyVar);
                geneticsTag.putInt("EyeColorVariantLeft", variants.leftEyeVar);
                geneticsTag.putInt("EyeColorVariantRight", variants.rightEyeVar);
                geneticsTag.putInt("Noise", variants.noise);
                geneticsTag.putFloat("Size", variants.size);
                geneticsTag.putFloat("Scars", variants.scars);
                geneticsTag.putInt("SkinColor", variants.skin_color);

                geneticsTag.putString("BaseChimera", chimeraGenetics.base);
                geneticsTag.putString("OrangeBaseChimera", chimeraGenetics.orangeBase);
                geneticsTag.putString("WhiteRatioChimera", chimeraGenetics.whiteRatio);
                geneticsTag.putString("AlbinoChimera", chimeraGenetics.albino);
                geneticsTag.putString("DiluteChimera", chimeraGenetics.dilute);
                geneticsTag.putString("AgoutiChimera", chimeraGenetics.agouti);
                geneticsTag.putString("TabbyStripesChimera", chimeraGenetics.tabbyStripes);

                geneticsTag.putInt("RufousingChimera", chimeraVariants.rufousingVariant);
                geneticsTag.putInt("BlueRufousingChimera", chimeraVariants.blueRufousingVariant);
                geneticsTag.putInt("OrangeBaseVariantChimera", chimeraVariants.orangeVar);
                geneticsTag.putInt("WhiteRatioVariantChimera", chimeraVariants.whiteVar);
                geneticsTag.putInt("AlbinoVariantChimera", chimeraVariants.albinoVar);

                geneticsTag.putInt("TabbyStripesVariantChimera", chimeraVariants.tabbyVar);
                geneticsTag.putInt("NoiseChimera", chimeraVariants.noise);

                geneticsTag.putString("Chimera", chimeraGenetics.chimeraGene);
                geneticsTag.putInt("ChimeraVariant", chimeraVariants.chimeraVariant);
                geneticsTag.putInt("SilverVariantChimera", chimeraVariants.silverVar);
                geneticsTag.putString("SilverChimera", chimeraGenetics.silver);

                tag.put("Genetics", geneticsTag);
            }

            return tag;
        }

        public static ClanCat load(CompoundTag tag) {
            ClanCat cat = new ClanCat();

            cat.catUUID = tag.getUUID("UUID");
            cat.catGender = tag.getString("Gender");
            cat.catName = Component.Serializer.fromJson(tag.getString("Name"));
            cat.catVariant = tag.getInt("Variant");
            cat.catAge = Component.Serializer.fromJson(tag.getString("Age"));
            cat.catRank = tag.getString("Rank");
            cat.catParents = Component.Serializer.fromJson(tag.getString("Parents"));

            {
                if (tag.contains("Genetics")) {
                    CompoundTag geneticsTag = tag.getCompound("Genetics");

                    cat.onGeneticalSkin = geneticsTag.getBoolean("Genetical");

                    WCGenetics genetics = new WCGenetics(
                            geneticsTag.getString("Bobtail"),
                            geneticsTag.getString("ChestFur"),
                            geneticsTag.getString("BellyFur"),
                            geneticsTag.getString("LegsFur"),
                            geneticsTag.getString("HeadFur"),
                            geneticsTag.getString("CheekFur"),
                            geneticsTag.getString("TailFur"),
                            geneticsTag.getString("BackFur"),

                            geneticsTag.getString("Base"),
                            geneticsTag.getString("OrangeBase"),
                            geneticsTag.getString("WhiteRatio"),
                            geneticsTag.getString("Albino"),
                            geneticsTag.getString("Dilute"),
                            geneticsTag.getString("Agouti"),
                            geneticsTag.getString("TabbyStripes"),
                            geneticsTag.getString("EyesAnomaly"),

                            geneticsTag.getString("Chimera"),
                            geneticsTag.getString("Silver")
                    );

                    WCGenetics geneticsChimera = new WCGenetics(
                            geneticsTag.getString("Bobtail"),
                            geneticsTag.getString("ChestFur"),
                            geneticsTag.getString("BellyFur"),
                            geneticsTag.getString("LegsFur"),
                            geneticsTag.getString("HeadFur"),
                            geneticsTag.getString("CheekFur"),
                            geneticsTag.getString("TailFur"),
                            geneticsTag.getString("BackFur"),

                            geneticsTag.getString("BaseChimera"),
                            geneticsTag.getString("OrangeBaseChimera"),
                            geneticsTag.getString("WhiteRatioChimera"),
                            geneticsTag.getString("AlbinoChimera"),
                            geneticsTag.getString("DiluteChimera"),
                            geneticsTag.getString("AgoutiChimera"),
                            geneticsTag.getString("TabbyStripesChimera"),
                            geneticsTag.getString("EyesAnomalyChimera"),

                            geneticsTag.getString("Chimera"),
                            geneticsTag.getString("SilverChimera")
                    );

                    cat.genetics = genetics;

                    cat.chimeraGenetics = geneticsChimera;

                    cat.chimeraVariants.chimeraVariant = geneticsTag.getInt("ChimeraVariant");

                    cat.variants.eyeColorLeft = geneticsTag.getString("EyeColorLeft");
                    cat.variants.eyeColorRight = geneticsTag.getString("EyeColorRight");

                    cat.variants.orangeVar = geneticsTag.getInt("OrangeBaseVariant");
                    cat.variants.whiteVar = geneticsTag.getInt("WhiteRatioVariant");
                    cat.variants.albinoVar = geneticsTag.getInt("AlbinoVariant");
                    cat.variants.tabbyVar = geneticsTag.getInt("TabbyStripesVariant");
                    cat.variants.leftEyeVar = geneticsTag.getInt("EyeColorVariantLeft");
                    cat.variants.rightEyeVar = geneticsTag.getInt("EyeColorVariantRight");

                    cat.variants.size = geneticsTag.getFloat("Size");
                    cat.variants.silverVar = geneticsTag.getInt("SilverVariant");
                    cat.variants.scars = geneticsTag.getInt("Scars");
                    cat.variants.skin_color = geneticsTag.getInt("SkinColor");


                    cat.chimeraVariants.orangeVar = geneticsTag.getInt("OrangeBaseVariantChimera");
                    cat.chimeraVariants.whiteVar = geneticsTag.getInt("WhiteRatioVariantChimera");
                    cat.chimeraVariants.albinoVar = geneticsTag.getInt("AlbinoVariantChimera");
                    cat.chimeraVariants.tabbyVar = geneticsTag.getInt("TabbyStripesVariantChimera");
                    cat.chimeraVariants.silverVar = geneticsTag.getInt("SilverVariantChimera");

                }
            }

            return cat;
        }

    }

    public static class Clan {
        public UUID clanUUID;
        public String name;
        public String clanBioSentence;
        public int clanSymbolIndex;
        public int color;
        UUID leader;
        public String leaderName;
        public String normalizedName;
        public Map<UUID, ClanPlayerRank> members = new HashMap<>();
        public Map<UUID, ClanPermissions> memberPerms = new HashMap<>();
        public Map<UUID, ClanCat> clanCats = new HashMap<>();
        public List<ClanLogEntry> logs = new ArrayList<>();

        public Map<ChunkPos, TerritoryChunk> claimedTerritory = new HashMap<>();
        public ChunkPos coreTerritory;
    }










    public static boolean isInEnemyTerritory(ServerPlayer player, ChunkPos pos) {
        ClanData data = ClanData.get(player.serverLevel());

        UUID playerClanUUID = player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                .map(WCEPlayerData::getCurrentClanUUID).orElse(ClanData.EMPTY_UUID);

        for (ClanData.Clan clan : data.clans.values()) {
            if (clan.claimedTerritory.containsKey(pos)) {
                if (!clan.clanUUID.equals(playerClanUUID)) {
                    return true;
                } else {
                    if (!data.canCommandWarriors(clan, player.getUUID())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static class TerritoryChunk{
        public ChunkPos chunkPos;
        public String name;
        public int time;
        public int timeToReclaim;

        public TerritoryChunk(ChunkPos chunkPos, String name, int time, int timeToReclaim) {
            this.chunkPos = chunkPos;
            this.name = name;
            this.time = time;
            this.timeToReclaim = timeToReclaim;
        }
    }

    public record ClanTerritories(List<TerritoryChunk> claimedTerritory, String clanName, int color, ChunkPos core) {}

    public void syncTerritoriesToClients(ServerLevel level) {

        Map<UUID, ClanTerritories> list = new HashMap<>();
        for (Clan clan : clans.values()) {

            List<TerritoryChunk> chunksClaimed = clan.claimedTerritory.values().stream().toList();

            ClanTerritories clanTerritory = new ClanTerritories(chunksClaimed, clan.name, clan.color, clan.coreTerritory);

            list.put(clan.clanUUID, clanTerritory);
        }

        SyncTerritoryToClients packet = new SyncTerritoryToClients(list);

        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
            ModPackets.sendToPlayer(packet, player);
        }
    }

    public void syncTerritoriesToAClient(ServerPlayer serverPlayer) {

        Map<UUID, ClanTerritories> list = new HashMap<>();
        for (Clan clan : clans.values()) {

            List<TerritoryChunk> chunksClaimed = clan.claimedTerritory.values().stream().toList();

            ClanTerritories clanTerritory = new ClanTerritories(chunksClaimed, clan.name, clan.color, clan.coreTerritory);

            list.put(clan.clanUUID, clanTerritory);
        }

        SyncTerritoryToClients packet = new SyncTerritoryToClients(list);

        ModPackets.sendToPlayer(packet, serverPlayer);

    }

    public boolean isChunkClaimedByOtherClan(ChunkPos chunkPos, UUID clanUUID) {
        for (Clan clan : clans.values()) {
            if (!clan.clanUUID.equals(clanUUID) && clan.claimedTerritory.containsKey(chunkPos)) {
                return true;
            }
        }
        return false;
    }

    public boolean claimChunk(UUID clanUUID, ChunkPos pos, String name) {
        Clan clan = getClan(clanUUID);
        if (clan == null) return false;

        if (clan.claimedTerritory.containsKey(pos)) return false;

        if (isChunkClaimedByOtherClan(pos, clanUUID)) return false;

        TerritoryChunk claimedChunk = new TerritoryChunk(pos, name,
                (WCEServerConfig.SERVER.MAX_TERRITORY_TIME.get()*60*20)/2,
                ((WCEServerConfig.SERVER.MAX_TERRITORY_TIME.get()*20*60)/8));

        clan.claimedTerritory.put(pos, claimedChunk);

        for (ChunkPos chunk : SQUARE_ADJACENT_CHUNKS) {
            ChunkPos checking = new ChunkPos(pos.x + chunk.x, pos.z + chunk.z);
            if (!isChunkClaimedByOtherClan(checking, clanUUID)) {
                if (!clan.claimedTerritory.containsKey(checking)) {
                    TerritoryChunk currentClaiming = new TerritoryChunk(checking, "",
                            (WCEServerConfig.SERVER.MAX_TERRITORY_TIME.get()*60*20)/2,
                            ((WCEServerConfig.SERVER.MAX_TERRITORY_TIME.get()*20*60)/8));

                    clan.claimedTerritory.put(checking, currentClaiming);
                }
            }
        }

        setDirty();
        return true;
    }

    public boolean reclaimChunk(UUID clanUUID, ChunkPos pos, ServerLevel level) {
        Clan clan = getClan(clanUUID);
        if (clan == null) return false;

        TerritoryChunk claimedChunk = clan.claimedTerritory.get(pos);
        if (claimedChunk == null) return false;

        int time = Mth.clamp(claimedChunk.time + (getMaxTerritoryTime()/3), 0, getMaxTerritoryTime());

        TerritoryChunk newClaimedChunk = new TerritoryChunk(pos, claimedChunk.name, time, (WCEServerConfig.SERVER.MAX_TERRITORY_TIME.get()*20*60)/8);

        clan.claimedTerritory.put(pos, newClaimedChunk);

        for (ChunkPos chunk : SQUARE_ADJACENT_CHUNKS) {
            ChunkPos checking = new ChunkPos(pos.x + chunk.x, pos.z + chunk.z);
            if (clan.claimedTerritory.containsKey(checking)) {
                TerritoryChunk currentChecking = clan.claimedTerritory.get(checking);

                int t = Mth.clamp(currentChecking.time + (getMaxTerritoryTime()/3), 0, getMaxTerritoryTime());

                TerritoryChunk newCurrentClaimedChunk = new TerritoryChunk(checking, currentChecking.name, t, (WCEServerConfig.SERVER.MAX_TERRITORY_TIME.get()*20*60)/8);

                clan.claimedTerritory.put(checking, newCurrentClaimedChunk);
            }
        }

        syncTerritoriesToClients(level.getServer().overworld());

        setDirty();
        return true;
    }


    public int getMaxTerritoryTime() {
        return WCEServerConfig.SERVER.MAX_TERRITORY_TIME.get()*60*20;
    }

    public boolean unclaimChunk(UUID clanUUID, ChunkPos pos, ServerLevel level) {
        Clan clan = getClan(clanUUID);
        if (clan == null) return false;

        if (!clan.claimedTerritory.containsKey(pos)) return false;

        removeMarkersFromChunk(level.getServer().overworld(), pos);

        clan.claimedTerritory.remove(pos);

        removeTerritoryNotConnected(clan, level);

        setDirty();
        return true;
    }

    public static CompoundTag saveTerritory(Map<ChunkPos, TerritoryChunk> map) {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();

        for (TerritoryChunk territory : map.values()) {
            CompoundTag entry = new CompoundTag();

            ChunkPos pos = territory.chunkPos;

            entry.putInt("x", pos.x);
            entry.putInt("z", pos.z);
            entry.putString("name", territory.name);
            entry.putInt("time", territory.time);
            entry.putInt("timeToReclaim", territory.timeToReclaim);

            list.add(entry);
        }

        tag.put("claimedTerritory", list);
        return tag;
    }

    public static Map<ChunkPos, TerritoryChunk> loadTerritory(CompoundTag tag) {
        Map<ChunkPos, TerritoryChunk> map = new HashMap<>();

        if (!tag.contains("claimedTerritory", Tag.TAG_LIST)) {
            return map;
        }

        ListTag list = tag.getList("claimedTerritory", Tag.TAG_COMPOUND);

        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);

            int x = entry.getInt("x");
            int z = entry.getInt("z");
            String name = entry.getString("name");
            int time = entry.getInt("time");
            int timeToReclaim = entry.getInt("timeToReclaim");

            ChunkPos pos = new ChunkPos(x, z);
            TerritoryChunk territory = new TerritoryChunk(pos, name, time, timeToReclaim);

            map.put(pos, territory);
        }

        return map;
    }

    public boolean territoryTick(ServerLevel serverLevel) {
        Iterator<Clan> clanIterator = clans.values().iterator();
        boolean result = false;

        while (clanIterator.hasNext()) {
            Clan clan = clanIterator.next();
            boolean shouldCheckForConnection = false;
            Iterator<TerritoryChunk> chunkIterator = clan.claimedTerritory.values().iterator();

            while (chunkIterator.hasNext()) {
                TerritoryChunk chunk = chunkIterator.next();

                boolean corePresent = false;

                if (chunk.chunkPos.equals(clan.coreTerritory) || isAdjacentToCore(chunk.chunkPos, clan.coreTerritory)) {
                    corePresent = true;
                }

                if (!corePresent) chunk.time--;
                if (chunk.timeToReclaim > 0) chunk.timeToReclaim--;

                if (chunk.time <= 0) {
                    removeMarkersFromChunk(serverLevel, chunk.chunkPos);

                    chunkIterator.remove();

                    result = true;
                    shouldCheckForConnection = true;
                }

            }

            if (shouldCheckForConnection) {
                removeTerritoryNotConnected(clan, serverLevel);
            }

        }

        return result;
    }

    public static final ChunkPos[] SQUARE_ADJACENT_CHUNKS = new ChunkPos[]{
            new ChunkPos(-1, 1), new ChunkPos(0, 1), new ChunkPos(1, 1),
            new ChunkPos(-1, 0),                              new ChunkPos(1, 0),
            new ChunkPos(-1, -1), new ChunkPos(0, -1), new ChunkPos(1, -1)
    };

    public boolean isAdjacentToCore(ChunkPos pos, ChunkPos core) {
        if (core == null) return false;
        if (pos == null) return false;

        for (ChunkPos chunk : SQUARE_ADJACENT_CHUNKS) {
            ChunkPos checking = new ChunkPos(pos.x + chunk.x, pos.z + chunk.z);
            if (checking.equals(core)) {
                return true;
            }
        }
        return false;
    }

    public void removeTerritoryNotConnected(Clan clan, ServerLevel sLevel) {

        ChunkPos[] axisSet = new ChunkPos[]{
                new ChunkPos(1, 0), new ChunkPos(-1, 0),
                new ChunkPos(0, 1), new ChunkPos(0, -1)
        };

        Set<ChunkPos> valid = new HashSet<>();
        Queue<ChunkPos> toCheck = new ArrayDeque<>();

        ChunkPos core = clan.coreTerritory;

        if (core != null && clan.claimedTerritory.containsKey(core)) {
            valid.add(core);
            toCheck.add(core);

            while (!toCheck.isEmpty()) {
                ChunkPos currentChunk = toCheck.poll();

                for (ChunkPos axis : axisSet) {
                    ChunkPos adjacent = new ChunkPos(currentChunk.x + axis.x, currentChunk.z + axis.z);

                    if (clan.claimedTerritory.containsKey(adjacent) && !valid.contains(adjacent)) {
                        valid.add(adjacent);
                        toCheck.add(adjacent);
                    }
                }
            }

            Iterator<ChunkPos> iterator = clan.claimedTerritory.keySet().iterator();
            while (iterator.hasNext()) {
                ChunkPos chunk = iterator.next();
                if (!valid.contains(chunk)) {
                    removeMarkersFromChunk(sLevel, chunk);

                    iterator.remove();
                }
            }

        } else {
            Iterator<ChunkPos> iterator = clan.claimedTerritory.keySet().iterator();
            while (iterator.hasNext()) {
                ChunkPos chunk = iterator.next();
                removeMarkersFromChunk(sLevel, chunk);
                iterator.remove();
            }
        }

    }

    public void removeMarkersFromChunk(ServerLevel sLevel, ChunkPos chunk) {
        if (chunk == null) return;

        Map<BlockPos, BlockEntity> map = sLevel.getChunk(chunk.x, chunk.z).getBlockEntities();

        List<BlockEntity> blockEntities = new ArrayList<>(map.values());
        for (BlockEntity blockEntity : blockEntities) {
            if (blockEntity instanceof TreeStumpBlockEntity stump) {
                sLevel.setBlock(stump.getBlockPos(), Blocks.AIR.defaultBlockState(), 3);
            }
        }
    }














    public Clan createClan(String name, int color, UUID leader, String leaderName, String clanBioSentence, int clanSymbolIndex) {
        Clan clan = new Clan();
        clan.clanUUID = UUID.randomUUID();
        clan.name = name;
        clan.clanBioSentence = clanBioSentence;
        clan.clanSymbolIndex = clanSymbolIndex;
        clan.color = color;
        clan.leader = leader;
        clan.leaderName = leaderName;

        createNormalizedName(clan, name);

        clan.members.put(leader, ClanPlayerRank.LEADER);
        clan.memberPerms.put(leader, ClanPermissions.OWNER);

        clans.put(clan.clanUUID, clan);

        setDirty();
        return clan;
    }

    public static final Queue<Runnable> CHUNK_TASKS = new ConcurrentLinkedQueue<>();

    public boolean renameClan(Clan clan, String newName, ServerLevel sLevel) {
        if (clan == null) return false;

        clan.name = newName;
        createNormalizedName(clan, newName);

        sLevel.getAllEntities().forEach(entity -> {
            if (entity instanceof WCatEntity cat) {
                if (cat.isTame()) {
                    if (cat.getClanUUID().equals(clan.clanUUID)) {
                        if (cat.getRank() != WCatEntity.Rank.NONE) cat.setClan(Component.literal(newName));
                    }
                }
            }
        });

        List<ServerPlayer> players = sLevel.getServer().getPlayerList().getPlayers();
        for (ServerPlayer player : players) {
            if (player != null) {
                UUID clanUUID = player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                        .map(WCEPlayerData::getCurrentClanUUID).orElse(EMPTY_UUID);

                if (clanUUID.equals(clan.clanUUID)) {
                    player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA).ifPresent(cap -> {
                        cap.setClanName(newName);
                    });
                }
            }
        }

        for (ChunkPos chunkPos : clan.claimedTerritory.keySet()) {
            CHUNK_TASKS.add(() -> {
                LevelChunk chunk = sLevel.getChunk(chunkPos.x, chunkPos.z);
                for (BlockEntity entity : chunk.getBlockEntities().values()) {
                    if (entity instanceof TreeStumpBlockEntity stump) {
                        stump.setOwnerClanName(clan.name);
                        sLevel.sendBlockUpdated(stump.getBlockPos(), stump.getBlockState(),
                                stump.getBlockState(), 3);
                    }
                }
            });
        }

        setDirty();
        syncTerritoriesToClients(sLevel);
        return true;
    }

    private void createNormalizedName(Clan clan, String name) {
        String baseName = normalizeName(name);
        String currentName = baseName;
        int i = 1;
        boolean exists;
        do {
            exists = false;
            for (Clan presentClan : clans.values()) {
                if (presentClan.normalizedName.equals(currentName)) {
                    exists = true;
                    currentName = baseName + i;
                    i++;
                    break;
                }
            }
        } while (exists);
        clan.normalizedName = currentName;
    }

    public boolean addClanCat(UUID clanUUID, WCatEntity cat) {
        Clan clan = getClan(clanUUID);
        if (clan == null) return false;
        if (cat == null) return false;

        ClanCat clanCat = ClanCat.buildCat(cat);

        clan.clanCats.put(cat.getUUID(), clanCat);
        setDirty();
        return true;
    }

    public boolean addClanCat(Clan clan, WCatEntity cat) {
        if (clan == null) return false;
        if (cat == null) return false;

        ClanCat clanCat = ClanCat.buildCat(cat);

        clan.clanCats.put(cat.getUUID(), clanCat);
        setDirty();
        return true;
    }

    public boolean removeClanCatFromAnyClan(WCatEntity cat) {
        if (cat == null) return false;
        Clan clan = getClan(cat.getClanUUID());
        if (clan == null) return false;

        for (Clan clan2 : clans.values()) {
            clan2.clanCats.remove(cat.getUUID());
        }

        setDirty();
        return true;
    }

    public boolean removeClanCatFromClan(UUID clanUUID, WCatEntity cat) {
        if (clanUUID == null) return false;
        Clan clan = getClan(clanUUID);
        if (clan == null) return false;

//        Component catLeftClanLog = Component.empty()
//                .append(cat.hasCustomName() ? cat.getCustomName().copy() : Component.literal("A Cat"))
//                .append(" has left ")
//                .append(Component.literal(clan.name).withStyle(Style.EMPTY.withColor(clan.color)));

        Component catLeftClanLog1 = Component.translatable("clan.cat_left_clan",
                cat.hasCustomName() ? cat.getCustomName().copy() : Component.translatable("generic.catname"),
                Component.literal(clan.name).withStyle(Style.EMPTY.withColor(clan.color)));

        if (cat.level() instanceof ServerLevel serverLevel) {
            this.registerLog(serverLevel, clan.clanUUID, catLeftClanLog1);
        }

        clan.clanCats.remove(cat.getUUID());
        setDirty();
        return true;
    }

    public boolean changeMemberPermissions(ServerPlayer player, UUID clanUUID, ClanPermissions permissions) {
        Clan clan = getClan(clanUUID);
        if (clan == null) return false;
        if (player == null) return false;

        UUID playerUUID = player.getUUID();
        if (!clan.memberPerms.containsKey(playerUUID)) return false;

        clan.memberPerms.put(playerUUID, permissions);

//        Component playerJoinedLog = Component.empty()
//                .append(Component.literal(player.getName().getString()).withStyle(ChatFormatting.YELLOW))
//                .append(" has been made ")
//                .append(Component.literal(permissions.name()).withStyle(ChatFormatting.RED));

        Component playerJoinedLog1 = Component.translatable("clan.member_perms",
                Component.literal(player.getName().getString()).withStyle(ChatFormatting.YELLOW),
                Component.literal(permissions.name()).withStyle(ChatFormatting.RED));

        this.registerLog(player.serverLevel().getServer().overworld(), clan.clanUUID, playerJoinedLog1);

        Component message = Component.translatable("clan.player_set_perms",
                Component.literal(permissions.name()).withStyle(ChatFormatting.RED),
                Component.literal(clan.name).withStyle(Style.EMPTY.withColor(clan.color))
                );

        player.sendSystemMessage(message);

        setDirty();

        return true;
    }

    public boolean changeMemberPermissions(UUID playerUUID, UUID clanUUID, ClanPermissions permissions) {
        Clan clan = getClan(clanUUID);
        if (clan == null) return false;
        if (!clan.memberPerms.containsKey(playerUUID)) return false;

        clan.memberPerms.put(playerUUID, permissions);

        setDirty();

        return true;
    }

    public boolean addMember(ServerPlayer player, UUID clanUUID, ClanPlayerRank rank) {
        Clan clan = getClan(clanUUID);
        if (clan == null) return false;
        if (player == null) return false;

        UUID playerUUID = player.getUUID();
        if (clan.members.containsKey(playerUUID)) return false;

        clan.members.put(playerUUID, rank);
        clan.memberPerms.put(playerUUID, ClanPermissions.GUEST);


        player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA).ifPresent(cap -> {
            cap.setClanName(clan.name);
            cap.setCurrentClanUUID(clan.clanUUID);

            for (Entity entity : player.serverLevel().getEntities().getAll()) {
                if (entity instanceof WCatEntity cat) {
                    if (cat.isOwnedBy(player)) {
                        cat.setClanUUID(clan.clanUUID);
                        addClanCat(clan, cat);
                    }
                }
            }

            ModPackets.sendToPlayer(new S2CSyncClanDataPacket(cap), player);

            Component playerJoinedLog =  Component.translatable("clan.player_joined_log",
                    logFormattedPlayerName(player),
                    Component.literal(clan.name).withStyle(Style.EMPTY.withColor(clan.color))
            );

            this.registerLog(player.serverLevel().getServer().overworld(), clan.clanUUID, playerJoinedLog);
        });
        Component message = Component.translatable("clan.client_clan_joined",
                Component.literal(clan.name).withStyle(Style.EMPTY.withColor(clan.color)));

        player.sendSystemMessage(message);



        setDirty();


        return true;
    }

    public boolean removeMember(ServerPlayer player, UUID clanUUID) {
        Clan clan = getClan(clanUUID);
        if (clan == null) return false;
        if (player == null) return false;

        UUID playerUUID = player.getUUID();
        if (!clan.members.containsKey(playerUUID)) return false;

        clan.members.remove(playerUUID);
        clan.memberPerms.remove(playerUUID);

        player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA).ifPresent(cap -> {
            cap.setClanName("None");
            cap.setCurrentClanUUID(EMPTY_UUID);

            for (Entity entity : player.serverLevel().getEntities().getAll()) {
                if (entity instanceof WCatEntity cat) {
                    if (cat.isOwnedBy(player)) {
                        cat.setClanUUID(EMPTY_UUID);
                        clan.clanCats.remove(cat.getUUID());
                    }
                }
            }

            ModPackets.sendToPlayer(new S2CSyncClanDataPacket(cap), player);

            Component playerLeftLog =  Component.translatable("clan.cat_left_clan",
                    logFormattedPlayerName(player),
                    Component.literal(clan.name).withStyle(Style.EMPTY.withColor(clan.color))
            );

            this.registerLog(player.serverLevel().getServer().overworld(), clan.clanUUID, playerLeftLog);
        });
        player.sendSystemMessage(Component.translatable("clan.client_clan_left").withStyle(ChatFormatting.YELLOW));

        if (clan.members.isEmpty()) {
            deleteClan(player.serverLevel().getServer().overworld(), clanUUID);
        }

        setDirty();



        return true;
    }

    public boolean removeMember(UUID targetUUID, UUID clanUUID) {
        Clan clan = getClan(clanUUID);
        if (clan == null) return false;

        if (!clan.members.containsKey(targetUUID)) return false;

        clan.members.remove(targetUUID);
        clan.memberPerms.remove(targetUUID);

        setDirty();
        return true;
    }

    public boolean changeMemberRank(ServerPlayer player, UUID clanUUID, ClanPlayerRank newRank) {
        Clan clan = getClan(clanUUID);
        if (clan == null) return false;
        if (player == null) return false;

        UUID playerUUID = player.getUUID();
        if (!clan.members.containsKey(playerUUID)) return false;

        Component playerRankChangeLog = Component.translatable("clan.player_changerank_log",
                logFormattedPlayerName(player),
                Component.literal(newRank.name()).withStyle(ChatFormatting.GOLD)
                );

        this.registerLog(player.serverLevel().getServer().overworld(), clan.clanUUID, playerRankChangeLog);

        clan.members.put(playerUUID, newRank);
        setDirty();
        return true;
    }

    public boolean changeMemberRank(UUID targetUUID, UUID clanUUID, ClanPlayerRank newRank) {
        Clan clan = getClan(clanUUID);
        if (clan == null) return false;

        if (!clan.members.containsKey(targetUUID)) return false;

        clan.members.put(targetUUID, newRank);
        setDirty();
        return true;
    }

    public boolean deleteClan(ServerLevel level, UUID clanUUID) {

        Clan clan = getClan(clanUUID);
        if (clan == null) return false;

        clan.members.clear();
        clan.memberPerms.clear();

        for (ChunkPos chunkPos : clan.claimedTerritory.keySet()) {
            removeMarkersFromChunk(level.getServer().overworld(), chunkPos);
        }

        clans.remove(clanUUID);

        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
            player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA).ifPresent(
                    cap -> {
                        if (clanUUID.equals(cap.getCurrentClanUUID())) {
                            cap.setCurrentClanUUID(EMPTY_UUID);
                            cap.setClanName("None");
                            player.sendSystemMessage(Component.translatable("clan.client_clan_left").withStyle(ChatFormatting.YELLOW));
                            ModPackets.sendToPlayer(new S2CSyncClanDataPacket(cap), player);
                        }
                    }
            );
        }

        for (Entity entity : level.getEntities().getAll()) {
            if (entity instanceof WCatEntity cat) {
                if (clanUUID.equals(cat.getClanUUID())) {
                    cat.setClanUUID(EMPTY_UUID);
                }
            }
        }

        syncTerritoriesToClients(level.getServer().overworld());

        setDirty();
        return true;
    }


    public Clan getClan(UUID clanUUID) {
        return clans.get(clanUUID);
    }

    public void registerLog(ServerLevel level, UUID clanUUID, Component message) {
        Clan clan = getClan(clanUUID);
        if (clan == null) return;

        long time = level.getGameTime();

        clan.logs.add(new ClanLogEntry(time, message));

        if (clan.logs.size() > 300) {
            clan.logs.remove(0);
        }

        setDirty();
    }


    public static String normalizeName(String name) {
        return name
                .toLowerCase(Locale.ROOT)
                .trim()
                .replace(" ", "_");
    }

    @Nullable
    public Clan getClanByName(String input) {
        for (Clan clan : clans.values()) {
            if (clan.normalizedName.equals(normalizeName(input))) {
                return clan;
            }
        }
        return null;
    }

    public boolean canManage(Clan clan, UUID player) {
        ClanPlayerRank rank = clan.members.get(player);
        ClanPermissions perms = clan.memberPerms.get(player);
        if (rank == null) return false;
        if (perms == null) return false;

        boolean can = rank == ClanPlayerRank.LEADER
                || perms == ClanPermissions.OWNER
                || perms == ClanPermissions.ADMIN;

        return can;
    }

    public boolean canManagePlayer(UUID clanUUID, UUID managerUUID, UUID targetUUID) {
        ClanData.Clan clan = getClan(clanUUID);
        if (clan == null) return false;
        ClanPermissions managerPerms = clan.memberPerms.get(managerUUID);
        ClanPermissions targetPerms = clan.memberPerms.get(targetUUID);
        if (!clan.memberPerms.containsKey(managerUUID)) return false;
        if (!clan.memberPerms.containsKey(targetUUID)) return false;


        if (targetPerms == ClanPermissions.OWNER) return false;
        if (managerPerms == ClanPermissions.OWNER) return true;
        if (managerPerms == ClanPermissions.ADMIN && targetPerms == ClanPermissions.ADMIN) return false;

        return managerPerms == ClanPermissions.ADMIN;
    }

    public boolean canInvite(Clan clan, UUID player) {
        ClanPlayerRank rank = clan.members.get(player);
        ClanPermissions perms = clan.memberPerms.get(player);
        if (rank == null) return false;
        if (perms == null) return false;

        boolean can = rank == ClanPlayerRank.LEADER
                || perms == ClanPermissions.OWNER
                || perms == ClanPermissions.ADMIN;

        return can;
    }

    public boolean canCommandWarriors(Clan clan, UUID player) {
        ClanPermissions perms = clan.memberPerms.get(player);
        if (perms == null) return false;

        boolean can = (perms != ClanPermissions.GUEST);

        return can;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {

        tag.putInt("Version", DATA_VERSION);
        ListTag clanList = new ListTag();

        for (Clan clan : clans.values()) {

            CompoundTag clanTag = new CompoundTag();
            clanTag.putUUID("ClanUUID", clan.clanUUID);
            clanTag.putString("Name", clan.name);
            clanTag.putInt("Color", clan.color);

            clanTag.putUUID("Leader", clan.leader);
            clanTag.putString("NameKey", clan.normalizedName);
            clanTag.putString("LeaderName", clan.leaderName);
            clanTag.putString("ClanBio", clan.clanBioSentence);
            clanTag.putInt("SymbolIndex", clan.clanSymbolIndex);


            ListTag members = new ListTag();
            for (var entry : clan.members.entrySet()) {
                CompoundTag m = new CompoundTag();
                m.putUUID("UUID", entry.getKey());
                m.putString("Rank", entry.getValue().name());
                members.add(m);
            }

            clanTag.put("Members", members);

            ListTag catList = new ListTag();
            for (ClanCat cat : clan.clanCats.values()) {
                catList.add(cat.save());
            }
            clanTag.put("ClanCats", catList);


            ListTag logList = new ListTag();
            for (ClanLogEntry log : clan.logs) {
                logList.add(log.save());
            }
            clanTag.put("Logs", logList);


            ListTag permsList = new ListTag();
            for (var entry : clan.memberPerms.entrySet()) {
                CompoundTag p = new CompoundTag();
                p.putUUID("UUID", entry.getKey());
                p.putString("Perm", entry.getValue().name());
                permsList.add(p);
            }
            clanTag.put("Permissions", permsList);

            clanList.add(clanTag);

            CompoundTag territoryTag = new CompoundTag();
            territoryTag = saveTerritory(clan.claimedTerritory);
            clanTag.put("Territory", territoryTag);

            if (clan.coreTerritory != null) {
                CompoundTag coreTag = new CompoundTag();
                coreTag.putInt("x", clan.coreTerritory.x);
                coreTag.putInt("z", clan.coreTerritory.z);

                clanTag.put("CoreTerritory", coreTag);
            }

        }
        tag.put("Clans", clanList);




        ListTag morphList = new ListTag();
        for (var entry : playerMorphNames.entrySet()) {
            CompoundTag morphTag = new CompoundTag();
            morphTag.putUUID("UUID", entry.getKey());
            morphTag.putString("MorphName", entry.getValue());
            morphList.add(morphTag);
        }
        tag.put("PlayerMorphNames", morphList);

        ListTag morphDataList = new ListTag();
        for (var entry : playerMorphData.entrySet()) {
            CompoundTag morphTag = new CompoundTag();
            morphTag.putUUID("UUID", entry.getKey());

            CompoundTag morphData = new CompoundTag();
            WCGenetics.writeGeneticsNBT(morphData, entry.getValue().genetics, entry.getValue().variants,
                    entry.getValue().chimerasGenetics, entry.getValue().chimeraVariants,
                    entry.getValue().onGeneticalSkin, entry.getValue().morphSkin);

            morphTag.put("MorphData", morphData);

            morphDataList.add(morphTag);
        }
        tag.put("PlayerMorphData", morphDataList);

        return tag;
    }

    public static ClanData load(CompoundTag tag) {
        ClanData data = new ClanData();

        int version = tag.contains("Version") ? tag.getInt("Version") : 0;

        if (!tag.contains("Clans", Tag.TAG_LIST)) {
            return data;
        }

        ListTag clanList = tag.getList("Clans", Tag.TAG_COMPOUND);
        for (Tag t : clanList) {
            CompoundTag clanTag = (CompoundTag) t;

            Clan clan = new Clan();
            clan.clanUUID = clanTag.getUUID("ClanUUID");
            clan.name = clanTag.getString("Name");
            clan.color = clanTag.getInt("Color");
            clan.leader = clanTag.getUUID("Leader");
            clan.leaderName = clanTag.getString("LeaderName");
            clan.clanBioSentence = clanTag.getString("ClanBio");
            clan.clanSymbolIndex = clanTag.getInt("SymbolIndex");

            clan.normalizedName = clanTag.getString("NameKey");

            clan.members = new HashMap<>();
            ListTag members = clanTag.getList("Members", Tag.TAG_COMPOUND);
            for (Tag m : members) {
                CompoundTag mt = (CompoundTag) m;
                clan.members.put(
                        mt.getUUID("UUID"),
                        ClanPlayerRank.valueOf(mt.getString("Rank"))
                );
            }

            ListTag catList = clanTag.getList("ClanCats", Tag.TAG_COMPOUND);
            for (int i = 0; i < catList.size(); i++) {
                CompoundTag catTag = catList.getCompound(i);
                ClanCat cat = ClanCat.load(catTag);

                clan.clanCats.put(cat.catUUID, cat);
            }


            if (clanTag.contains("Logs", Tag.TAG_LIST)) {
                ListTag logList = clanTag.getList("Logs", Tag.TAG_COMPOUND);
                for (int i = 0; i < logList.size(); i++) {
                    CompoundTag logTag = logList.getCompound(i);
                    clan.logs.add(ClanLogEntry.load(logTag));
                }
            }

            clan.memberPerms = new HashMap<>();

            if (clanTag.contains("Permissions", Tag.TAG_LIST)) {
                ListTag permsList = clanTag.getList("Permissions", Tag.TAG_COMPOUND);
                for (Tag p : permsList) {
                    CompoundTag pt = (CompoundTag) p;
                    clan.memberPerms.put(
                            pt.getUUID("UUID"),
                            ClanPermissions.valueOf(pt.getString("Perm"))
                    );
                }
            }

            if (clanTag.contains("Territory", Tag.TAG_COMPOUND)) {
                clan.claimedTerritory = loadTerritory(clanTag.getCompound("Territory"));
            }

            if (clanTag.contains("CoreTerritory", Tag.TAG_COMPOUND)) {
                CompoundTag coreTag = clanTag.getCompound("CoreTerritory");

                int x = coreTag.getInt("x");
                int z = coreTag.getInt("z");

                clan.coreTerritory = new ChunkPos(x, z);
            }


            data.clans.put(clan.clanUUID, clan);
        }

        if (tag.contains("PlayerMorphNames", Tag.TAG_LIST)) {
            ListTag morphList = tag.getList("PlayerMorphNames", Tag.TAG_COMPOUND);
            for (Tag t : morphList) {
                CompoundTag morphTag = (CompoundTag) t;
                data.playerMorphNames.put(
                        morphTag.getUUID("UUID"),
                        morphTag.getString("MorphName")
                );
            }
        }

        if (tag.contains("PlayerMorphData", Tag.TAG_LIST)) {
            ListTag morphList = tag.getList("PlayerMorphData", Tag.TAG_COMPOUND);
            for (Tag t : morphList) {
                CompoundTag morphTag = (CompoundTag) t;

                WCGenetics.PackedGeneticData morphData;
                if (morphTag.contains("MorphData")) {
                    morphData = WCGenetics.loadGeneticsNBT(morphTag.getCompound("MorphData"));
                } else {
                    morphData = WCGenetics.PackedGeneticData.empty();
                }

                data.playerMorphData.put(
                        morphTag.getUUID("UUID"),
                        morphData
                );
            }
        }

        return data;
    }

    public static ClanData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                ClanData::load,
                ClanData::new,
                "wce_clans"
        );
    }

    public List<Clan> getAllClans() {
        return new ArrayList<>(clans.values());
    }

    @Nullable
    public Clan getClanByCat(UUID catUUID) {
        for (Clan clan : clans.values()) {
            if (clan.clanCats.containsKey(catUUID)) {
                return clan;
            }
        }
        return null;
    }

    public void checkForEmptyClans(ServerLevel serverLevel) {

        Iterator<Clan> clanIterator = clans.values().iterator();

        while(clanIterator.hasNext()){
            Clan clan = clanIterator.next();

            Iterator<UUID> iterator = clan.members.keySet().iterator();
            while (iterator.hasNext()) {
                UUID uuid = iterator.next();
                ServerPlayer player = serverLevel.getServer().getPlayerList().getPlayer(uuid);
                if (player != null) {
                    UUID playerClanUUID = player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                            .map(WCEPlayerData::getCurrentClanUUID)
                            .orElse(ClanData.EMPTY_UUID);

                    if (!playerClanUUID.equals(clan.clanUUID)) {
                        iterator.remove();
                    }
                }
            }

            if(clan.members.isEmpty()){
                clanIterator.remove();
            }
        }
    }


    public static Component logFormattedPlayerName(Player player) {
        String morphName = player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                .map(WCEPlayerData::getMorphName).orElse("");

        return Component.empty()
                .append(Component.literal(morphName).withStyle(ChatFormatting.AQUA))
                .append(Component.literal("(").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(player.getName().getString()).withStyle(ChatFormatting.GRAY))
                .append(Component.literal(")").withStyle(ChatFormatting.GRAY));
    }

}

