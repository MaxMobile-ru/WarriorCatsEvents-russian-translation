package net.snowteb.warriorcats_events.client;

import net.snowteb.warriorcats_events.clan.WCEPlayerData;

public class ClientClanData {
    private static WCEPlayerData data = new WCEPlayerData();

    public static void set(WCEPlayerData newData) {
        data = newData;
    }

    public static WCEPlayerData get() {
        return data;
    }
}
