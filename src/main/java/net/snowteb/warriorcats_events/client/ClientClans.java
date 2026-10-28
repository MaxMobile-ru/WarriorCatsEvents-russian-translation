package net.snowteb.warriorcats_events.client;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.snowteb.warriorcats_events.clan.ClanData;
import net.snowteb.warriorcats_events.clan.ClanObjects;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ClientClans {

    private static Map<UUID, Entry> clans = new HashMap<>();

    private static boolean loading = false;

    public static void startBatch() {
        clans = new HashMap<>();
        loading = true;
    }

    public static boolean isLoading() {
        return loading;
    }

    public static void appendClans(List<Entry> batch, boolean isLast) {
        Map<UUID, Entry> toPut = new HashMap<>();
        batch.forEach((entry) -> {
            toPut.put(entry.clan.clanUUID, entry);
        });
        clans.putAll(toPut);

        if (isLast) loading = false;
    }

    public static void setClans(Map<UUID, Entry> list) {
        clans = list;
    }

    public static Map<UUID, Entry> getClans() {
        return clans;
    }

    public static void addClan(Entry clanInfo) {
        clans.put(clanInfo.clan.clanUUID, clanInfo);
    }

    @Nullable
    public static Entry getClan(UUID uuid) {
        return clans.get(uuid);
    }

    public static class Entry {
        private final ClanData.Clan clan;
        private final Map<UUID, ClanObjects.Player> players = new HashMap<>();
        private final boolean canManage;

        public Entry(ClanData.Clan clan, boolean canManage, Map<UUID, ClanObjects.Player> players) {
            this.clan = clan;
            this.canManage = canManage;
            this.players.putAll(players);
        }

        public ClanData.Clan getClan() {
            return clan;
        }

        public boolean canManage() {
            return canManage;
        }

        public ClanData.ClanPermissions getPermissions(UUID uuid) {
            return clan.members.getOrDefault(uuid, ClanData.Clan.Member.empty()).getPermissions();
        }

        public ClanData.ClanPlayerRank getRank(UUID uuid) {
            return clan.members.getOrDefault(uuid, ClanData.Clan.Member.empty()).getRank();
        }

        public Map<UUID, ClanObjects.Player> getPlayers() {
            return players;
        }

        public Map<UUID, ClanData.Clan.Member> getMembers() {
            return clan.members;
        }

        @Nullable
        public ClanObjects.Player getPlayer(UUID uuid) {
            return players.get(uuid);
        }

        @Nullable
        public ClanData.Clan.Member getMember(UUID uuid) {
            return clan.members.get(uuid);
        }

        public void writeToBuf(FriendlyByteBuf buf) {
            CompoundTag tag = new CompoundTag();

            clan.serializeClan(tag);

            buf.writeNbt(tag);
            buf.writeBoolean(canManage);

            buf.writeInt(players.size());
            for (Map.Entry<UUID, ClanObjects.Player> entry : players.entrySet()) {
                CompoundTag playerTag = new CompoundTag();
                buf.writeUUID(entry.getKey());
                entry.getValue().serialize(playerTag);
                buf.writeNbt(playerTag);
            }

        }

        public static Entry readFromBuf(FriendlyByteBuf buf) {
            CompoundTag tag = buf.readNbt();
            ClanData.Clan clan = new ClanData.Clan();
            if (tag != null) {
                ClanData.Clan.deserializeClan(tag, clan);
            }

            boolean canManage = buf.readBoolean();


            int memberSize = buf.readInt();
            Map<UUID, ClanObjects.Player> members = new HashMap<>();

            for (int i = 0; i < memberSize; i++) {
                UUID uuid = buf.readUUID();
                CompoundTag playerTag = buf.readNbt();
                ClanObjects.Player player = new ClanObjects.Player();
                if (playerTag != null) {
                    player = ClanObjects.Player.deserialize(playerTag);
                }

                members.put(uuid, player);
            }

            return new Entry(clan, canManage, members);
        }
    }




}
