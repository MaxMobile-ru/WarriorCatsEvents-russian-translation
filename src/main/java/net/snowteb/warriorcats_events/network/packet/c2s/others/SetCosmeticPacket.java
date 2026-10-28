package net.snowteb.warriorcats_events.network.packet.c2s.others;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import tocraft.walkers.api.PlayerShape;

import java.util.function.Supplier;

public class SetCosmeticPacket {

    private final int index;

    public SetCosmeticPacket(int index) {

        this.index = index;
    }

    public static SetCosmeticPacket decode(FriendlyByteBuf buf) {
        int index = buf.readInt();

        return new SetCosmeticPacket(index);
    }

    public static void encode(SetCosmeticPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.index);
    }

    public static void handle(SetCosmeticPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            if (!WarriorCatsEvents.Collaborators.isContributor(player.getUUID())) return;

            LivingEntity shape = PlayerShape.getCurrentShape(player);
            if (shape instanceof WCatEntity catShape) {

                PlayerShape.updateShapes(player, null);

                if (packet.index == 0) {
                    catShape.embeddedAccessories().setSunGlasses(!catShape.embeddedAccessories().hasSunGlasses());
                } else if (packet.index == 1) {
                    catShape.embeddedAccessories().setMossCoat(!catShape.embeddedAccessories().hasMossCoat());
                }

                PlayerShape.updateShapes(player, catShape);

                player.teleportTo(player.getX(), player.getY() + 0.2, player.getZ());

            }



        });

        ctx.get().setPacketHandled(true);
    }

}
