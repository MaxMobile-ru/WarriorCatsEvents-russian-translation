package net.snowteb.warriorcats_events.managers;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerKittingRequestManager {

    private static final Map<UUID, KitRequest> requests = new HashMap<>();

    public static void request(ServerPlayer target, ServerPlayer requester) {
        requests.put(target.getUUID(), new KitRequest(requester.getUUID()));
    }

    public static KitRequest getRequest(ServerPlayer player) {
        return requests.get(player.getUUID());
    }

    public static void clear(ServerPlayer player) {
        requests.remove(player.getUUID());
    }

    public static void tick(ServerPlayer player) {
        KitRequest request = requests.get(player.getUUID());
        if (request == null) return;

        if (request.tick()) {
            requests.remove(player.getUUID());
            player.sendSystemMessage(Component.translatable("managers.request_expired").withStyle(ChatFormatting.GRAY));
        }
    }


    public static class KitRequest {
        public final UUID requester;
        private int ticksLeft;

        public static final int TIME = 80 * 20;

        public KitRequest(UUID requester) {
            this.requester = requester;
            this.ticksLeft = TIME;
        }

        public boolean tick() {
            ticksLeft--;
            return ticksLeft <= 0;
        }

    }

    public static Component getMessage() {
    return Component.empty()
            .append(
                    Component.translatable("commands.button.accept")
                            .withStyle(style -> style
                                    .withColor(ChatFormatting.GREEN)
                                    .withItalic(true)
                                    .withUnderlined(true)
                                    .withClickEvent(
                                            new ClickEvent(
                                                    ClickEvent.Action.RUN_COMMAND,
                                                    "/wce mate kits accept"
                                            )
                                    )
                                    .withHoverEvent(
                                            new HoverEvent(
                                                    HoverEvent.Action.SHOW_TEXT,
                                                    Component.literal("Accept")
                                                            .withStyle(ChatFormatting.GREEN)
                                            )
                                    )
                            )
            )

            .append("       ")

            .append(
                    Component.translatable("commands.button.deny")
                            .withStyle(style -> style
                                    .withColor(ChatFormatting.RED)
                                    .withItalic(true)
                                    .withUnderlined(true)
                                    .withClickEvent(
                                            new ClickEvent(
                                                    ClickEvent.Action.RUN_COMMAND,
                                                    "/wce mate kits decline"
                                            )
                                    )
                                    .withHoverEvent(
                                            new HoverEvent(
                                                    HoverEvent.Action.SHOW_TEXT,
                                                    Component.literal("Decline")
                                                            .withStyle(ChatFormatting.RED)
                                            )
                                    )
                            )
            );
    }
}

