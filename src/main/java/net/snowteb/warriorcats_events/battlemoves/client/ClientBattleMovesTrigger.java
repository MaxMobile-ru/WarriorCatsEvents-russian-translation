package net.snowteb.warriorcats_events.battlemoves.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.battlemoves.BattleMoveTypeAccessor;
import net.snowteb.warriorcats_events.battlemoves.network.CtSLaunchMovePacket;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.skills.PlayerSkill;
import net.snowteb.warriorcats_events.skills.PlayerSkillProvider;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = WarriorCatsEvents.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientBattleMovesTrigger {

    @SubscribeEvent
    public static void keyInput(InputEvent.Key event) {

//        if (event.getKey() == GLFW.GLFW_KEY_F && event.getAction() == GLFW.GLFW_PRESS) {
//            launchMove(1);
//        }

    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        if (Minecraft.getInstance().isPaused()) return;

        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer != null) {
            PlayerSkill data = (PlayerSkill) localPlayer.getCapability(PlayerSkillProvider.SKILL_DATA).orElse(new PlayerSkill());
            data.skillTick();
        }

    }

    private static void launchMove(int index) {
        ModPackets.sendToServer(new CtSLaunchMovePacket(index));
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer != null) {
            PlayerSkill data = (PlayerSkill) localPlayer.getCapability(PlayerSkillProvider.SKILL_DATA).orElse(new PlayerSkill());
            BattleMoveTypeAccessor accessor = data.getBattleMovesStore().getSlot(index);
            if (accessor == null) return;
            accessor.run(localPlayer);
        }
    }

}
