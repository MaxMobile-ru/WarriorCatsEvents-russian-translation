package net.snowteb.warriorcats_events.network.packet.c2s.clan;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import tocraft.walkers.api.PlayerShape;

public class EmoteMorphPacket implements CustomPacketPayload {

    private final int animIndex;

    public EmoteMorphPacket(int animIndex) {

        this.animIndex = animIndex;
    }

    public static EmoteMorphPacket decode(FriendlyByteBuf buf) {
        int animIndex = buf.readInt();

        return new EmoteMorphPacket(animIndex);
    }

    public static void encode(EmoteMorphPacket packet, FriendlyByteBuf buf) {

        buf.writeInt(packet.animIndex);
    }

    public static void handle(EmoteMorphPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) ctx.player();

            if (packet.animIndex < -1 && !WarriorCatsEvents.Collaborators.isContributor(player.getUUID())) return;

            LivingEntity shape = PlayerShape.getCurrentShape(player);
            if (shape instanceof WCatEntity catShape) {

                catShape.setAnimIndex(packet.animIndex);

                PlayerShape.updateShapes(player, catShape);

                player.getPersistentData().putInt("wcat_animation_playing", player.server.getTickCount() + 10);

                if (packet.animIndex == -2) {
                    player.getPersistentData().putInt("wcat_jump", player.server.getTickCount() + 25);
                }
            }

        });
    }

    public static final Type<EmoteMorphPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "emote_morph"));

    public static final StreamCodec<FriendlyByteBuf, EmoteMorphPacket> CODEC =
            StreamCodec.of(
                    (buf, pkt) -> encode(pkt, buf),
                    buf -> decode(buf)
            );


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
