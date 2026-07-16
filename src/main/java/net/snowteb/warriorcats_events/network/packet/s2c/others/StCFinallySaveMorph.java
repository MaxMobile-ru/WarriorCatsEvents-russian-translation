package net.snowteb.warriorcats_events.network.packet.s2c.others;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.client.ClientPacketHandles;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCGenetics;

import java.util.function.Supplier;

public class StCFinallySaveMorph {

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

        return new StCFinallySaveMorph( key ,genetics, variants, chimeraGens, chimeraVariants,onGeneticalSkin , presetVariant);
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

    public void handle(Supplier<NetworkEvent.Context> packet) {
        NetworkEvent.Context ctx = packet.get();
        ctx.enqueueWork(() -> {

            ClientPacketHandles.openSaveMorphScreen( Key ,genetics, variants, chimeraGenetics, chimeraVariants, onGeneticalSkin , presetVariant);

        });

        ctx.setPacketHandled(true);
    }

}
