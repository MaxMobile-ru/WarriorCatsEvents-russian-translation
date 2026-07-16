package net.snowteb.warriorcats_events.network.packet.s2c.others;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.client.ClientPacketHandles;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCGenetics;

public class StCFinallySaveMorph implements CustomPacketPayload {

    private final String Key;

    private final WCGenetics genetics;
    private final WCGenetics.GeneticalVariants variants;
    private final WCGenetics chimeraGenetics;
    private final WCGenetics.GeneticalChimeraVariants chimeraVariants;
    private final boolean onGeneticalSkin;
    private final int presetVariant;


    public StCFinallySaveMorph(String key, WCGenetics genetics, WCGenetics.GeneticalVariants variants,
                               WCGenetics chimeraGens, WCGenetics.GeneticalChimeraVariants chimeraVariants,
                               boolean onGeneticalSkin, int presetVariant) {
        this.Key = key;
        this.genetics = genetics;
        this.variants = variants;
        this.chimeraGenetics = chimeraGens;
        this.chimeraVariants = chimeraVariants;
        this.onGeneticalSkin = onGeneticalSkin;
        this.presetVariant = presetVariant;
    }

    public static StCFinallySaveMorph decode(FriendlyByteBuf buf) {

        String key = buf.readUtf();

        WCGenetics genetics = WCGenetics.decode(buf);
        WCGenetics.GeneticalVariants variants = WCGenetics.GeneticalVariants.decode(buf);

        WCGenetics chimeraGens = WCGenetics.decode(buf);
        WCGenetics.GeneticalChimeraVariants chimeraVariants = WCGenetics.GeneticalChimeraVariants.decode(buf);
        boolean onGeneticalSkin = buf.readBoolean();
        int presetVariant = buf.readInt();

        return new StCFinallySaveMorph( key ,genetics, variants, chimeraGens, chimeraVariants, onGeneticalSkin, presetVariant);
    }

    public static void encode(StCFinallySaveMorph packet, FriendlyByteBuf buf) {

        buf.writeUtf(packet.Key);

        packet.genetics.encode(buf);
        packet.variants.encode(buf);
        packet.chimeraGenetics.encode(buf);
        packet.chimeraVariants.encode(buf);
        buf.writeBoolean(packet.onGeneticalSkin);
        buf.writeInt(packet.presetVariant);

    }

    public static void handle(StCFinallySaveMorph packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientPacketHandles.openSaveMorphScreen( packet.Key ,packet.genetics, packet.variants,
                    packet.chimeraGenetics, packet.chimeraVariants, packet.onGeneticalSkin, packet.presetVariant );
        });
    }

    public static final Type<StCFinallySaveMorph> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "finally_save_morph"));

    public static final StreamCodec<FriendlyByteBuf, StCFinallySaveMorph> CODEC =
            StreamCodec.of(
                    (buf, pkt) -> encode(pkt, buf),
                    buf -> decode(buf)
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
