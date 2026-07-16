package net.snowteb.warriorcats_events.wcedatabase;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.snowteb.warriorcats_events.WarriorCatsEvents;

@EventBusSubscriber(modid = WarriorCatsEvents.MODID)
public class CommonInitializeDatabase {

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            DataManager.getContributors(WarriorCatsEvents.Collaborators.getList()::addAll);
        });
    }
}
