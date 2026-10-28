package net.snowteb.warriorcats_events.clan;

import net.minecraft.ChatFormatting;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCGenetics;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import org.apache.logging.log4j.core.util.UuidUtil;

import java.util.*;

public class ClanObjects {

    public record ClanLogEntry(long gameTimeID, Component message, Type type) {
        public enum Type {
            DEATHS,
            CUSTOM,
            PLAYERS,
            OTHERS
        }

        public CompoundTag save() {
            CompoundTag tag = new CompoundTag();
            tag.putLong("Time", gameTimeID);
            tag.putString("Message", Component.Serializer.toJson(message));
            tag.putInt("Type", type.ordinal());
            return tag;
        }

        public static ClanLogEntry load(CompoundTag tag) {

            long time = 0;
            Component message = Component.empty();
            Type type = Type.OTHERS;

            if (tag.contains("Time")) {
                time = tag.getLong("Time");
            }
            if (tag.contains("Message")) {
                message = Component.Serializer.fromJson(tag.getString("Message"));
            }
            if (tag.contains("Type")) {
                type = Type.values()[tag.getInt("Type")];
            }

            return new ClanLogEntry(time, message, type);
        }
    }

    public static class Player {
        private UUID playerUUID;
        private String displayName;
        private String morphName;
        private WCGenetics.PackedGeneticData genetics;

        public Player(String displayName, String morphName, WCGenetics.PackedGeneticData genetics, UUID playerUUID) {
            this.displayName = displayName;
            this.morphName = morphName;
            this.genetics = genetics;
            this.playerUUID = playerUUID;
        }

        public Player() {
            this.displayName = "<???>";
            this.morphName = "<???>";
            this.genetics = WCGenetics.PackedGeneticData.empty();
            this.playerUUID = ClanData.EMPTY_UUID;
        }

        public UUID getUUID() {
            return playerUUID;
        }

        public void setUUID(UUID playerUUID) {
            this.playerUUID = playerUUID;
        }


        public String getUsername() {
            return displayName != null ? displayName : "???";
        }

        public String getMorphName() {
            return morphName != null ? morphName : "<Unknown>";
        }

        public WCGenetics.PackedGeneticData getGenetics() {
            if (genetics == null) {
                genetics = new WCGenetics.PackedGeneticData(new WCGenetics(), new WCGenetics.GeneticalVariants(),
                        new WCGenetics(), new WCGenetics.GeneticalChimeraVariants(), true, 0);
            }

            return genetics;
        }

        public void setGenetics(WCGenetics.PackedGeneticData genetics) {
            this.genetics = genetics;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public void setMorphName(String morphName) {
            this.morphName = morphName;
        }

        public void serialize(CompoundTag tag) {
            if (displayName != null) {
                tag.putString("Username", displayName);
            }
            if (morphName != null) {
                tag.putString("MorphName", morphName);
            }
            if (genetics != null) {
                WCGenetics.saveModuleNBT(tag, genetics);
            }
            if (playerUUID != null) {
                tag.putUUID("UUID", playerUUID);
            }
        }

        public static Player deserialize(CompoundTag tag) {
            String name = "???";
            if (tag.contains("Username")) {
                name = tag.getString("Username");
            }

            String morphName = "<Unknown>";
            if (tag.contains("MorphName")) {
                morphName = tag.getString("MorphName");
            }

            UUID playerUUID = ClanData.EMPTY_UUID;
            if (tag.contains("UUID")) {
                playerUUID = tag.getUUID("UUID");
            }

            WCGenetics.PackedGeneticData genetics = WCGenetics.loadModuleNBT(tag);

            return new Player(name, morphName, genetics, playerUUID);
        }
    }

    public static class JoinRequest {
        private final UUID applicantUUID;
        private final UUID clanUUID;
        private final Component senderName;
        private final Component clanName;
        private final String requesterMessage;


        public JoinRequest(ServerPlayer player, ClanData.Clan clan, String requesterMessage) {
            this(player.getUUID(),
                    clan.clanUUID,
                    ClanData.logFormattedPlayerName(player),
                    Component.literal(clan.name).withStyle(Style.EMPTY.withColor(clan.color)),
                    requesterMessage
            );
        }

        public JoinRequest(UUID applicantUUID, UUID clanUUID, Component senderName, Component clanName, String requesterMessage) {
            this.applicantUUID = applicantUUID;
            this.clanUUID = clanUUID;
            this.senderName = senderName;
            this.clanName = clanName;
            this.requesterMessage = requesterMessage;
        }

        public Component getGUIMessage() {
            return Component.translatable(
                    "clan.join_request.guimessage",
                    senderName,
                    clanName
            );
        }

        public String getRequesterMessage() {
            return requesterMessage;
        }

        public UUID getApplicantUUID() {
            return applicantUUID;
        }

        public UUID getClanUUID() {
            return clanUUID;
        }

        public void accept(ServerLevel level, ServerPlayer executer) {
            ServerLevel overworld = level.getServer().overworld();
            ClanData data = ClanData.get(overworld);
            ClanData.Clan clan = data.getClan(clanUUID);

            if (clan != null) {
                boolean has = clan.joinRequests.removeIf(joinRequest -> joinRequest.getApplicantUUID().equals(this.applicantUUID));

                if (has) {
                    Component log = Component.translatable("clan.join_request.log_accepted",
                            ClanData.logFormattedPlayerName(executer),
                            this.senderName
                    );

                    ServerPlayer player = level.getServer().getPlayerList().getPlayer(this.applicantUUID);
                    if (player != null) {
                        boolean joined = data.addMember(player, clanUUID, ClanData.ClanPlayerRank.WARRIOR);
                        if (!joined) {
                            player.sendSystemMessage(Component.translatable("commands.clan.cant_join_clan").withStyle(ChatFormatting.RED));
                        } else {
                            for (ClanData.Clan cl : data.getAllClans()) {
                                if (!cl.clanUUID.equals(this.clanUUID)) {
                                    cl.joinRequests.removeIf(jr -> jr.getApplicantUUID().equals(this.applicantUUID));
                                }
                            }
                            data.registerLog(overworld, clanUUID, log, ClanLogEntry.Type.PLAYERS);
                        }
                    } else {
                        JoinRequest existing = data.pendingJoins.putIfAbsent(applicantUUID, this);
                        if (existing != null && !existing.clanUUID.equals(this.clanUUID)) {
                            executer.sendSystemMessage(Component.translatable("clan.join_request.already_pending")
                                    .withStyle(ChatFormatting.YELLOW));
                        } else {
                            for (ClanData.Clan cl : data.getAllClans()) {
                                if (!cl.clanUUID.equals(this.clanUUID)) {
                                    cl.joinRequests.removeIf(jr -> jr.getApplicantUUID().equals(this.applicantUUID));
                                }
                            }
                            data.registerLog(overworld, clanUUID, log, ClanLogEntry.Type.PLAYERS);
                        }
                    }
                }
            }
        }

        public void decline(ServerLevel level, ServerPlayer executer) {
            ServerLevel overworld = level.getServer().overworld();
            ClanData data = ClanData.get(overworld);
            ClanData.Clan clan = data.getClan(clanUUID);

            if (clan != null) {
                boolean has = clan.joinRequests.removeIf(joinRequest -> joinRequest.getApplicantUUID().equals(this.applicantUUID));

                if (has) {
                    ServerPlayer player = level.getServer().getPlayerList().getPlayer(this.applicantUUID);
                    if (player != null) {
                        player.sendSystemMessage(Component.translatable("clan.join_request.declined",
                                        Component.literal(clan.name).withStyle(Style.EMPTY.withColor(clan.color))
                                )
                                .withStyle(ChatFormatting.YELLOW));
                    }

                    Component log = Component.translatable("clan.join_request.log_declined",
                            ClanData.logFormattedPlayerName(executer),
                            this.senderName
                    );

                    data.registerLog(overworld, clanUUID, log, ClanLogEntry.Type.PLAYERS);
                }
            }
        }

        public static void onPlayerJoinLevel(ServerLevel level, ServerPlayer player) {
            ServerLevel overworld = level.getServer().overworld();
            ClanData data = ClanData.get(overworld);

            JoinRequest join = data.pendingJoins.remove(player.getUUID());
            if (join != null) {
                ClanData.Clan clan = data.getClan(join.clanUUID);
                if (clan != null) {
                    boolean joined = data.addMember(player, clan.clanUUID, ClanData.ClanPlayerRank.WARRIOR);

                    if (!joined) {
                        player.sendSystemMessage(Component.translatable("commands.clan.cant_join_clan").withStyle(ChatFormatting.RED));
                    } else {
                        for (ClanData.Clan cl : data.getAllClans()) {
                            cl.joinRequests.removeIf(joinRequest -> joinRequest.getApplicantUUID().equals(join.applicantUUID));
                        }
                    }
                }
            }
        }

        public static void serializeMap(Map<UUID, JoinRequest> map, CompoundTag tag) {
            ListTag list = new ListTag();
            for (Map.Entry<UUID, JoinRequest> entry : map.entrySet()) {
                CompoundTag entryTag = new CompoundTag();

                entryTag.putUUID("UUID", entry.getKey());
                entryTag.put("JoinRequest", entry.getValue().serializeSingleRequest());

                list.add(entryTag);
            }

            tag.put("PendingJoins", list);
        }

        public static Map<UUID, JoinRequest> deserializeMap(CompoundTag tag) {
            Map<UUID, JoinRequest> map = new HashMap<>();

            if (!tag.contains("PendingJoins", Tag.TAG_LIST)) {
                return map;
            }

            ListTag listTag = tag.getList("PendingJoins", Tag.TAG_COMPOUND);

            for (int i = 0; i < listTag.size(); i++) {
                CompoundTag entryTag = listTag.getCompound(i);

                JoinRequest request = deserializeSingleRequest(entryTag.getCompound("JoinRequest"));
                UUID playerUUID = entryTag.getUUID("UUID");

                map.put(playerUUID, request);
            }

            return map;
        }



        public static void serializeRequests(List<JoinRequest> requests, CompoundTag tag) {
            ListTag list = new ListTag();
            for (JoinRequest req : requests) {
                list.add(req.serializeSingleRequest());
            }

            tag.put("JoinRequests", list);
        }

        public static List<JoinRequest> deserializeRequests(CompoundTag tag) {
            List<JoinRequest> list = new ArrayList<>();

            if (!tag.contains("JoinRequests", Tag.TAG_LIST)) {
                return list;
            }

            ListTag listTag = tag.getList("JoinRequests", Tag.TAG_COMPOUND);

            for (int i = 0; i < listTag.size(); i++) {
                JoinRequest request = deserializeSingleRequest(listTag.getCompound(i));

                list.add(request);
            }

            return list;
        }


        public CompoundTag serializeSingleRequest() {
            CompoundTag entry = new CompoundTag();

            entry.putUUID("PlayerUUID", this.applicantUUID);
            entry.putUUID("ClanUUID", this.clanUUID);
            entry.putString("SenderName", Component.Serializer.toJson(this.senderName));
            entry.putString("ClanName", Component.Serializer.toJson(this.clanName));
            entry.putString("Message", this.requesterMessage);

            return entry;
        }

        public static JoinRequest deserializeSingleRequest(CompoundTag tag) {
            UUID requester = tag.getUUID("PlayerUUID");
            UUID clanUUID = tag.getUUID("ClanUUID");
            Component senderName = Component.Serializer.fromJson(tag.getString("SenderName"));
            Component clanName = Component.Serializer.fromJson(tag.getString("ClanName"));
            String message = tag.getString("Message");

            return new JoinRequest(requester, clanUUID, senderName, clanName, message);
        }

    }

}
