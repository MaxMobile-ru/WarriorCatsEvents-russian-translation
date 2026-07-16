package net.snowteb.warriorcats_events.client;

import net.minecraft.network.chat.Component;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCGenetics;

import java.util.*;

public class ClanInfo {

    public UUID uuid;
    public String name;
    public int color;
    public String leaderName;
    public String clanSentence;
    public int symbolIndex;
    public int memberCount;
    public List<String> memberMorphNames;

    public Map<UUID, Member> playersInClan = new HashMap<>();
    public List<ClientClanCat> clanCats = new ArrayList<>();

    public List<ClientLogEntry> clanLogs = new ArrayList<>();
    public boolean canManage = false;



    public ClanInfo(UUID uuid, String name, int color, String leader, String clanSentence, boolean canManage,
                    int memberCount, List<String> memberMorphNames, List<ClientClanCat> clanCatsNPCSres,
                    List<ClientLogEntry> clanLogs, int symbolIndex) {
        this.uuid = uuid;
        this.name = name;
        this.color = color;
        this.leaderName = leader;
        this.memberCount = memberCount;
        this.memberMorphNames = memberMorphNames;
        this.clanCats = clanCatsNPCSres;
        this.clanLogs = clanLogs;
        this.canManage = canManage;
        this.clanSentence = clanSentence;
        this.symbolIndex = symbolIndex;
    }

    public ClanInfo(UUID uuid, String name, int color, String leader, String clanSentence, int memberCount, Map<UUID, Member> playersInClan, int symbolIndex) {
        this.uuid = uuid;
        this.name = name;
        this.color = color;
        this.leaderName = leader;
        this.memberCount = memberCount;
        this.memberMorphNames = new ArrayList<>();
        this.playersInClan = playersInClan;
        this.clanSentence = clanSentence;
        this.symbolIndex = symbolIndex;
    }

    public Member getMember(UUID memberUUID) {
        return playersInClan.get(memberUUID);
    }

    public ClientClanCat getClanCat(UUID clanCatUUID) {
        for (ClientClanCat cat : clanCats) {
            if (cat.uuid.equals(clanCatUUID)) {
                return cat;
            }
        }
        return null;
    }


    public static class Member {

        private final UUID playerUUID;
        private final String morphName;
        private final String rank;
        private final String permissions;
        private final String age;
        private final boolean isOnline;

        private final WCGenetics genetics;
        private final WCGenetics.GeneticalVariants variants;
        private final WCGenetics chimeraGenetics;
        private final WCGenetics.GeneticalChimeraVariants chimeraVariants;
        private final boolean onGeneticalSkin;
        private final int morphVariant;


        public Member(UUID playerUUID, String morphName, String rank, String permissions, String age, boolean isOnline,
                      WCGenetics genetics, WCGenetics.GeneticalVariants variants,
                      WCGenetics chimeraGenetics, WCGenetics.GeneticalChimeraVariants chimeraVariants,
                      boolean onGeneticalSkin, int morphVariant) {
            this.playerUUID = playerUUID;
            this.morphName = morphName;
            this.rank = rank;
            this.permissions = permissions;
            this.age = age;
            this.isOnline = isOnline;

            this.genetics = genetics;
            this.variants = variants;
            this.chimeraGenetics = chimeraGenetics;
            this.chimeraVariants = chimeraVariants;
            this.onGeneticalSkin = onGeneticalSkin;
            this.morphVariant = morphVariant;
        }

        public UUID getPlayerUUID() { return playerUUID; }
        public String getPlayerMorphName() { return morphName; }
        public String getRank() { return rank; }
        public String getPerms() { return permissions; }
        public String getPlayerMorphAge() { return age; }
        public boolean isPlayerOnline() { return isOnline; }
        public WCGenetics getGenetics() { return genetics; }
        public WCGenetics.GeneticalVariants getVariants() { return variants; }
        public WCGenetics getChimeraGenetics() { return chimeraGenetics; }
        public WCGenetics.GeneticalChimeraVariants getChimeraVariants() { return chimeraVariants; }
        public boolean isOnGeneticalSkin() { return onGeneticalSkin; }
        public int getMorphVariant() { return morphVariant; }
    }


    public static class ClientClanCat {

        public UUID uuid;
        public String name;
        public String gender;
        public String rank;
        public String age;
        public String parents;
        public int variant;

        public boolean onGeneticalSkin = false;
        public WCGenetics genetics = new WCGenetics();
        public WCGenetics chimeraGenetics = new WCGenetics();
        public WCGenetics.GeneticalVariants variants = new WCGenetics.GeneticalVariants();
        public WCGenetics.GeneticalChimeraVariants chimeraVariants = new WCGenetics.GeneticalChimeraVariants();


        public ClientClanCat(UUID uuid, String name, String gender, String rank,
                             String age, int variant, String parents, boolean onGeneticalSkin,
                             WCGenetics genetics, WCGenetics chimeraGenetics,
                             WCGenetics.GeneticalVariants variants, WCGenetics.GeneticalChimeraVariants chimeraVariants) {
            this.uuid = uuid;
            this.name = name;
            this.gender = gender;
            this.variant = variant;
            this.parents = parents;
            this.rank = rank;
            this.age = age;

            this.onGeneticalSkin = onGeneticalSkin;
            this.genetics = genetics;
            this.chimeraGenetics = chimeraGenetics;
            this.variants = variants;
            this.chimeraVariants = chimeraVariants;
        }
    }


    public static class ClientLogEntry {
        public long gameTimeID;
        public Component message;

        public ClientLogEntry(long gameTimeID, Component message) {
            this.gameTimeID = gameTimeID;
            this.message = message;
        }
    }

}
