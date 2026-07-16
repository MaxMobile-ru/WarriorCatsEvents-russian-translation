package net.snowteb.warriorcats_events.managers;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerMateRequestManager {

    private static final Map<UUID, MateRequest> requests = new HashMap<>();

    public static void request(ServerPlayer target, ServerPlayer requester) {
        requests.put(target.getUUID(), new MateRequest(requester.getUUID()));
    }

    public static MateRequest getRequest(ServerPlayer player) {
        return requests.get(player.getUUID());
    }

    public static void clear(ServerPlayer player) {
        requests.remove(player.getUUID());
    }

    public static void tick(ServerPlayer player) {
        MateRequest request = requests.get(player.getUUID());
        if (request == null) return;

        if (request.tick()) {
            requests.remove(player.getUUID());
            player.sendSystemMessage(Component.translatable("managers.request_expired").withStyle(ChatFormatting.GRAY));
        }
    }


    public static class MateRequest {
        public final UUID requester;
        private int ticksLeft;

        public static final int TIME = 80 * 20;

        public MateRequest(UUID requester) {
            this.requester = requester;
            this.ticksLeft = TIME;
        }

        public boolean tick() {
            ticksLeft--;
            return ticksLeft <= 0;
        }

    }

}

