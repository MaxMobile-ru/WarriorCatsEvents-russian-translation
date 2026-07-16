package net.snowteb.warriorcats_events.network.packet.c2s.clan;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.clan.WCEPlayerDataProvider;
import net.snowteb.warriorcats_events.clan.WCEPlayerDataUtils;
import net.snowteb.warriorcats_events.diseases.Diseaseable;
import net.snowteb.warriorcats_events.entity.ModEntities;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCGenetics;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.s2c.clan.S2CSyncClanDataPacket;
import tocraft.walkers.api.PlayerShape;

import java.util.function.Supplier;

public class SavePlayerGeneticsPacket {

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

    public static void handle(SavePlayerGeneticsPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            PlayerShape.updateShapes(player, null);

            player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA).ifPresent(cap -> {
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

        ctx.get().setPacketHandled(true);
    }


}
