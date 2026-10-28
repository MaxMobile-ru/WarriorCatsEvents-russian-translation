//package net.snowteb.warriorcats_events.client;
//
//import javax.annotation.Nullable;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//public class ClientClanCache {
//
//    private static List<ClanInfo> clans = new ArrayList<>();
//
//    public static void setClans(List<ClanInfo> list) {
//        clans = list;
//    }
//
//    public static List<ClanInfo> getClans() {
//        return clans;
//    }
//
//    @Nullable
//    public static ClanInfo getClan(UUID uuid) {
//        return clans.stream()
//                .filter(c -> c.uuid.equals(uuid))
//                .findFirst()
//                .orElse(null);
//    }
//
//    public static  void addClan(ClanInfo clanInfo) {
//        clans.add(clanInfo);
//    }
//}
//
