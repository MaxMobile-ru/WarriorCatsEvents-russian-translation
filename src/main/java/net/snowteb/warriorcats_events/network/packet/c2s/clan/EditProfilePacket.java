package net.snowteb.warriorcats_events.network.packet.c2s.clan;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.clan.WCEPlayerDataProvider;
import net.snowteb.warriorcats_events.clan.WCEPlayerDataUtils;
import tocraft.walkers.api.PlayerShape;

import java.util.function.Supplier;

public class EditProfilePacket {

    private final String key;
    private final String newValue;


    public EditProfilePacket(String key, String newValue) {
        this.key = key;
        this.newValue = newValue;
    }

    public static EditProfilePacket decode(FriendlyByteBuf buf) {
        String key = buf.readUtf();
        String newValue = buf.readUtf();

        return new EditProfilePacket(key, newValue);
    }

    public static void encode(EditProfilePacket packet, FriendlyByteBuf buf) {

        buf.writeUtf(packet.key);
        buf.writeUtf(packet.newValue);
    }

    public static void handle(EditProfilePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA).ifPresent(cap -> {

                if (packet.key.equals("bio")) {
                    cap.setCharacterBio(packet.newValue);
                } else if (packet.key.equals("gender")) {
                    cap.setGenderText(packet.newValue);
                    if (!packet.newValue.isEmpty()) {
                        cap.setGenderData(2);
                    }
                } else {
                    player.sendSystemMessage(Component.literal("Invalid key provided."));
                }

                PlayerShape.updateShapes(player, WCEPlayerDataUtils.createShape(player, player.serverLevel()));
            });


            player.server.getCommands().performPrefixedCommand(
                    player.createCommandSourceStack(),
                    "wce info profile"
            );

        });

        ctx.get().setPacketHandled(true);
    }

}
