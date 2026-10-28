package net.snowteb.warriorcats_events.client;

import net.minecraft.client.Minecraft;
import net.snowteb.warriorcats_events.clanplayerdata.WCEPlayerData;
import net.snowteb.warriorcats_events.clanplayerdata.WCEPlayerDataProvider;

public class ClientClanData {

    public static void set(WCEPlayerData newData) {
        if (Minecraft.getInstance().player == null) return;
        Minecraft.getInstance().player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA).ifPresent(data -> {
            data.copyFrom(newData);
        });
    }

    public static WCEPlayerData get() {
        if (Minecraft.getInstance().player == null) return new WCEPlayerData();
        return Minecraft.getInstance().player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA).orElse(new WCEPlayerData());
    }
}
