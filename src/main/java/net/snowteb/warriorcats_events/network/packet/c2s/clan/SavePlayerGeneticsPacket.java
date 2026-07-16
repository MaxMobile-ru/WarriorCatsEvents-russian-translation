package net.snowteb.warriorcats_events.network.packet.c2s.clan;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.attachments.CapabilityManager;
import net.snowteb.warriorcats_events.attachments.ModAttachments;
import net.snowteb.warriorcats_events.attachments.WCEPlayerDataUtils;
import net.snowteb.warriorcats_events.diseases.Diseaseable;
import net.snowteb.warriorcats_events.entity.ModEntities;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCGenetics;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.s2c.clan.S2CSyncClanDataPacket;
import tocraft.walkers.api.PlayerShape;

public class SavePlayerGeneticsPacket implements CustomPacketPayload {

    private final WCGenetics.PackedGeneticData data;

    public SavePlayerGeneticsPacket(WCGenetics.PackedGeneticData data) {
        this.data = data;
    }

    public static SavePlayerGeneticsPacket decode(FriendlyByteBuf buf) {

        CompoundTag tag = buf.readNbt();
        WCGenetics.PackedGeneticData data1 = null;
        if (tag != null) {
            data1 = WCGenetics.loadModuleNBT(tag);
        }

        return new SavePlayerGeneticsPacket(data1);

    }

    public static void encode(SavePlayerGeneticsPacket packet, FriendlyByteBuf buf) {

        if (packet.data != null) {
            CompoundTag tag = new CompoundTag();
            WCGenetics.saveModuleNBT(tag, packet.data);
            buf.writeNbt(tag);
        }
    }

    public static void handle(SavePlayerGeneticsPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) ctx.player();

            PlayerShape.updateShapes(player, null);

            CapabilityManager.attachmentProvider(player, ModAttachments.PLAYER_WCE_DATA, cap -> {
                cap.setFirstLoginHandled(true);

                cap.setPlayerGenetics(packet.data.genetics);
                cap.setPlayerGeneticalVariants(packet.data.variants);
                cap.setOnGeneticalSkin(packet.data.onGeneticalSkin);
                cap.setVariantData(packet.data.morphSkin);
                cap.setPlayerChimeraGenetics(packet.data.chimerasGenetics);
                cap.setPlayerChimeraVariants(packet.data.chimeraVariants);

                ModPackets.sendToPlayer(new S2CSyncClanDataPacket(cap), player);
            });

            WCatEntity shape = WCEPlayerDataUtils.createShape(player, player.level());

            if (!PlayerShape.updateShapes(player, shape)) {
                player.sendSystemMessage(Component.literal("Couldn't update your morph"));
            }

            if (player instanceof Diseaseable<?> diseaseable) diseaseable.onChange();


        });

    }

    public static final Type<SavePlayerGeneticsPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "save_player_genetics"));

    public static final StreamCodec<FriendlyByteBuf, SavePlayerGeneticsPacket> CODEC =
            StreamCodec.of(
                    (buf, pkt) -> encode(pkt, buf),
                    buf -> decode(buf)
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
