package net.snowteb.warriorcats_events.network.packet.c2s.clan;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.attachments.CapabilityManager;
import net.snowteb.warriorcats_events.attachments.ModAttachments;
import net.snowteb.warriorcats_events.diseases.DiseaseTypes;
import net.snowteb.warriorcats_events.diseases.Diseaseable;
import net.snowteb.warriorcats_events.diseases.kinds.BrokenPaw;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import tocraft.walkers.api.PlayerShape;

public class SetPosePacket implements CustomPacketPayload {

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

    public static void handle(SetPosePacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) ctx.player();

            CapabilityManager.attachmentProvider(player, ModAttachments.PLAYER_WCE_DATA, cap -> {
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
                        cap.setIdlePose(packet.pose);
                    }

                    PlayerShape.updateShapes(player, catShape);

                    player.teleportTo(player.getX(), player.getY() + 0.2, player.getZ());

                }
            });


        });
    }

    public static final Type<SetPosePacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "set_pose"));

    public static final StreamCodec<FriendlyByteBuf, SetPosePacket> CODEC =
            StreamCodec.of(
                    (buf, pkt) -> encode(pkt, buf),
                    buf -> decode(buf)
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
