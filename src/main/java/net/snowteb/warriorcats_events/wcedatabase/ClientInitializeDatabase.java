package net.snowteb.warriorcats_events.wcedatabase;


import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.flappycat.FlappyCatClientData;


@EventBusSubscriber(modid = WarriorCatsEvents.MODID, value = Dist.CLIENT)
public class ClientInitializeDatabase {

    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
           DataManager.getScores(FlappyCatClientData::setScores);
        });
    }

}
