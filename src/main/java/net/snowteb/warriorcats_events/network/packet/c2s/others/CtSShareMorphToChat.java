package net.snowteb.warriorcats_events.network.packet.c2s.others;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCGenetics;
import net.snowteb.warriorcats_events.managers.ServerPlayerMorphsCache;

import java.util.Map;

public class CtSShareMorphToChat implements CustomPacketPayload {

    private final String Key;

    private final WCGenetics genetics;
    private final WCGenetics.GeneticalVariants variants;
    private final WCGenetics chimeraGenetics;
    private final WCGenetics.GeneticalChimeraVariants chimeraVariants;
    private final boolean onGeneticalSkin;
    private final int presetVariant;

    public CtSShareMorphToChat(String key, WCGenetics genetics, WCGenetics.GeneticalVariants variants,
                               WCGenetics chimeraGens, WCGenetics.GeneticalChimeraVariants chimeraVariants, boolean onGeneticalSkin, int presetVariant) {
        this.Key = key;
        this.genetics = genetics;
        this.variants = variants;
        this.chimeraGenetics = chimeraGens;
        this.chimeraVariants = chimeraVariants;
        this.onGeneticalSkin = onGeneticalSkin;
        this.presetVariant = presetVariant;
    }

    public static CtSShareMorphToChat decode(FriendlyByteBuf buf) {

        String key = buf.readUtf();

        WCGenetics genetics = WCGenetics.decode(buf);
        WCGenetics.GeneticalVariants variants = WCGenetics.GeneticalVariants.decode(buf);

        WCGenetics chimeraGens = WCGenetics.decode(buf);
        WCGenetics.GeneticalChimeraVariants chimeraVariants = WCGenetics.GeneticalChimeraVariants.decode(buf);
        boolean onGeneticalSkin = buf.readBoolean();
        int presetVariant = buf.readInt();

        return new CtSShareMorphToChat( key ,genetics, variants, chimeraGens, chimeraVariants,onGeneticalSkin ,presetVariant);
    }

    public static void encode(CtSShareMorphToChat packet, FriendlyByteBuf buf) {

        buf.writeUtf(packet.Key);

        packet.genetics.encode(buf);
        packet.variants.encode(buf);
        packet.chimeraGenetics.encode(buf);
        packet.chimeraVariants.encode(buf);
        buf.writeBoolean(packet.onGeneticalSkin);
        buf.writeInt(packet.presetVariant);

    }

    public static void handle(CtSShareMorphToChat packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) ctx.player();

            ServerPlayerMorphsCache.ServerMorphData data =
                    new ServerPlayerMorphsCache.ServerMorphData(packet.genetics, packet.chimeraGenetics, packet.variants,
                            packet.chimeraVariants, packet.onGeneticalSkin, packet.presetVariant, 12000);

            int extraKey = 0;
            String key = packet.Key;
            for (Map.Entry<String, ServerPlayerMorphsCache.ServerMorphData> entry : ServerPlayerMorphsCache.getMorphsCache().entrySet()) {
                if (entry.getKey().toLowerCase().contains(packet.Key.toLowerCase())) {
                    extraKey++;
                    key = packet.Key + "_" + extraKey;
                }
            }

            ServerPlayerMorphsCache.add(key, data);

            if (player.getServer() != null) {
                MinecraftServer server = player.getServer();

                String finalKey = key;
                Component text = Component.literal(finalKey)
                        .withStyle(style -> style.withClickEvent(
                                new ClickEvent(ClickEvent.Action.OPEN_URL, "/wce:morph:" + finalKey)
                        ).withHoverEvent(
                                new HoverEvent(
                                        HoverEvent.Action.SHOW_TEXT,
                                        Component.literal("Display Morph")
                                                .withStyle(ChatFormatting.WHITE)
                                )
                        ));

                server.getPlayerList().broadcastSystemMessage(
                        Component.empty()
                                .append(Component.literal("[WCE] ").withStyle(ChatFormatting.WHITE))
                                .append(player.getDisplayName().copy().withStyle(ChatFormatting.AQUA))
                                .append(Component.literal(" has shared a morph: ").withStyle(ChatFormatting.WHITE))
                                .append(Component.literal("[").withStyle(ChatFormatting.LIGHT_PURPLE))
                                .append(text.copy().withStyle(ChatFormatting.LIGHT_PURPLE))
                                .append(Component.literal("]").withStyle(ChatFormatting.LIGHT_PURPLE)), false
                );
            }
        });

    }


    public static final Type<CtSShareMorphToChat> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "share_morph_to_chat"));

    public static final StreamCodec<FriendlyByteBuf, CtSShareMorphToChat> CODEC =
            StreamCodec.of(
                    (buf, pkt) -> encode(pkt, buf),
                    buf -> decode(buf)
            );


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
