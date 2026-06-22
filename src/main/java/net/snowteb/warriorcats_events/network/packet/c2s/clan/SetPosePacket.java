package net.snowteb.warriorcats_events.network.packet.c2s.clan;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.clan.WCEPlayerData;
import net.snowteb.warriorcats_events.clan.WCEPlayerDataProvider;
import net.snowteb.warriorcats_events.diseases.DiseaseTypes;
import net.snowteb.warriorcats_events.diseases.Diseaseable;
import net.snowteb.warriorcats_events.diseases.kinds.BrokenPaw;
import net.snowteb.warriorcats_events.entity.custom.WCatEntity;
import tocraft.walkers.api.PlayerShape;

import java.util.function.Supplier;

public class SetPosePacket {

    private final int pose;

    public SetPosePacket(int pose) {

        this.pose = pose;
    }

    public static SetPosePacket decode(FriendlyByteBuf buf) {
        int pose = buf.readInt();

        return new SetPosePacket(pose);
    }

    public static void encode(SetPosePacket packet, FriendlyByteBuf buf) {

        buf.writeInt(packet.pose);
    }

    public static void handle(SetPosePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA).ifPresent(data -> {
                LivingEntity shape = PlayerShape.getCurrentShape(player);
                if (shape instanceof WCatEntity catShape) {

                    PlayerShape.updateShapes(player, null);

                    if (packet.pose == 4) {
                        boolean alreadyBrokenPaw = catShape.isBrokenPaw();
                        boolean brokenPawByDisease = false;
                        if (player instanceof Diseaseable<?> diseaseable) {
                            if (diseaseable.getDisease(DiseaseTypes.BROKEN_PAW) instanceof BrokenPaw brokenPaw) {
                                if (!brokenPaw.isBoneWrapped()) brokenPawByDisease = true;
                            }
                        }
                        boolean brokenLeg = !alreadyBrokenPaw || brokenPawByDisease;
                        catShape.setBrokenPaw(brokenLeg);
                    } else {
                        catShape.setIdlePose(packet.pose);
                        data.setIdlePose(packet.pose);
                    }

                    PlayerShape.updateShapes(player, catShape);

                    player.teleportTo(player.getX(), player.getY() + 0.2, player.getZ());

                }
            });


        });

        ctx.get().setPacketHandled(true);
    }

}
