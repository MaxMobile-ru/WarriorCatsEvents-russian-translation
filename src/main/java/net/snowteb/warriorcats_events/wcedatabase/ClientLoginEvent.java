package net.snowteb.warriorcats_events.wcedatabase;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.flappycat.FlappyCatClientData;


@EventBusSubscriber(modid = WarriorCatsEvents.MODID, value = Dist.CLIENT)
public class ClientLoginEvent {

    @SubscribeEvent
    public static void onClientLogin(final ClientPlayerNetworkEvent.LoggingIn event) {
        DataManager.getScores(FlappyCatClientData::setScores);
    }

}
