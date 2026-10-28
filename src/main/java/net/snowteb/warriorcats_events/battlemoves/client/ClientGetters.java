package net.snowteb.warriorcats_events.battlemoves.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.snowteb.warriorcats_events.WarriorCatsEvents;

public class ClientGetters {

    private static float delta = 0.1f;

    public static int getFov() {
        return Minecraft.getInstance().options.fov().get();
    }

    public static void fovOffset(int fov, float delta) {
        BattleMoveFovHandler.setTargetOffset(fov);
        ClientGetters.delta = delta;
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = WarriorCatsEvents.MODID)
    public static class BattleMoveFovHandler {

        private static float currentOffset = 0f;
        private static float targetOffset = 0f;

        public static void setTargetOffset(float offset) {
            targetOffset = offset;
        }

        @SubscribeEvent
        public static void onComputeFov(ViewportEvent.ComputeFov event) {
            if (!event.usedConfiguredFov()) return;

            currentOffset = Mth.lerp(delta, currentOffset, targetOffset);
            event.setFOV(event.getFOV() + currentOffset);
        }
    }
}
