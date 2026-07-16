package net.snowteb.warriorcats_events.wcedatabase;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.snowteb.warriorcats_events.WarriorCatsEvents;

@Mod.EventBusSubscriber(modid = WarriorCatsEvents.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonInitializeDatabase {

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            DataManager.getContributors(WarriorCatsEvents.Collaborators.getList()::addAll);
        });
    }
}
