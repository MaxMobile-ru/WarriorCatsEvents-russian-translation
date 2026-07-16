package net.snowteb.warriorcats_events.network.packet.c2s.cats;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.entity.ModEntities;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCGenetics;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;

import java.util.function.Supplier;

public class SummonCustomCatPacket {

    private final WCGenetics genetics;
    private final WCGenetics.GeneticalVariants variants;

    private final WCGenetics chimeraGenetics;
    private final WCGenetics.GeneticalChimeraVariants chimeraVariants;

    private final boolean onGeneticalSkin;

    public SummonCustomCatPacket(boolean geneticalSkin, WCGenetics genetics, WCGenetics.GeneticalVariants variants,
                                 WCGenetics chimeraGens, WCGenetics.GeneticalChimeraVariants chimeraVariants) {
        this.genetics = genetics;
        this.variants = variants;
        this.chimeraGenetics = chimeraGens;
        this.chimeraVariants = chimeraVariants;
        this.onGeneticalSkin = geneticalSkin;
    }

    public static SummonCustomCatPacket decode(FriendlyByteBuf buf) {

        boolean geneticalSkin = buf.readBoolean();

        WCGenetics genetics = WCGenetics.decode(buf);
        WCGenetics.GeneticalVariants variants = WCGenetics.GeneticalVariants.decode(buf);

        WCGenetics chimeraGens = WCGenetics.decode(buf);
        WCGenetics.GeneticalChimeraVariants chimeraVariants = WCGenetics.GeneticalChimeraVariants.decode(buf);

        return new SummonCustomCatPacket(geneticalSkin, genetics, variants, chimeraGens, chimeraVariants);
    }

    public static void encode(SummonCustomCatPacket packet, FriendlyByteBuf buf) {

        buf.writeBoolean(packet.onGeneticalSkin);

        packet.genetics.encode(buf);
        packet.variants.encode(buf);
        packet.chimeraGenetics.encode(buf);
        packet.chimeraVariants.encode(buf);

    }

    public static void handle(SummonCustomCatPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            ServerLevel level = player.serverLevel();

            WCatEntity cat = createCat(level,
                    packet.genetics, packet.variants, packet.chimeraGenetics,
                    packet.chimeraVariants, packet.onGeneticalSkin, player.blockPosition());

            if (cat == null) return;

            cat.setPos(player.position());

            level.addFreshEntity(cat);

            cat.wanderCenter = player.blockPosition();

        });

        ctx.get().setPacketHandled(true);
    }

    private static WCatEntity createCat(ServerLevel level,
                                        WCGenetics genetics, WCGenetics.GeneticalVariants variants,
                                        WCGenetics chimeraGenetics, WCGenetics.GeneticalChimeraVariants variantsChimera,
                                        boolean onGeneticalSkin, BlockPos pos) {

        WCatEntity cat = ModEntities.WCAT.get().create(level);

        if (cat == null) return null;

        cat.finalizeSpawn(level, level.getCurrentDifficultyAt(pos),
                MobSpawnType.MOB_SUMMONED, null, null);

        cat.setOnGeneticalSkin(onGeneticalSkin);

        if (onGeneticalSkin) {
            cat.getGeneticsModule().setGenetics(genetics);

            cat.getGeneticsModule().setGeneticalVariants(variants);
            cat.getGeneticsModule().setChimeraGenetics(chimeraGenetics);

            cat.getGeneticsModule().setGeneticalVariantsChimera(variantsChimera);

        } else {
            cat.getGeneticsModule().setNonGeneticalValues(genetics, variants.size);
        }

        return cat;
    }
}
