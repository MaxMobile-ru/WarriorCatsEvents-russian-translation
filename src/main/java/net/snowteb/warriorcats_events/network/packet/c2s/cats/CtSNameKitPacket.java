package net.snowteb.warriorcats_events.network.packet.c2s.cats;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;

public class CtSNameKitPacket implements CustomPacketPayload {
    private final String kitPrefix;
    private final int kitID;

    public CtSNameKitPacket(String kitPrefix, int kitID) {
        this.kitPrefix = kitPrefix;
        this.kitID = kitID;
    }

    public CtSNameKitPacket(FriendlyByteBuf buf) {
        this.kitPrefix = buf.readUtf(64);
        this.kitID = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(this.kitPrefix);
        buf.writeInt(this.kitID);
    }

    public void handle(IPayloadContext ctx) {

        ctx.enqueueWork(() -> {
            Player player = ctx.player();

            if (player instanceof ServerPlayer sPlayer) {
                ServerLevel level = ((ServerLevel) sPlayer.level());

                WCatEntity kit = (WCatEntity) level.getEntity(this.kitID);

                if (kit != null) {

                    String finalName;

                    String genderS;
                    if (kit.getGender() == 0) {
                        genderS = " ♂";
                    } else {
                        genderS = " ♀";
                    }

                    finalName = this.kitPrefix + "kit" + genderS;
                    kit.setCustomName(Component.literal(finalName));
                    kit.setCustomNameVisible(true);

                    kit.setPrefix(Component.literal(kitPrefix));

                    kit.setNameColor(kit.getRank());


                    Component messageLog = Component.translatable("item.warriorcats_events.kit.kit_born",
                            Component.literal(finalName).withStyle(ChatFormatting.GREEN),
                            Component.literal("(").withStyle(ChatFormatting.GRAY)
                                    .append(Component.literal(sPlayer.getName().getString()).withStyle(ChatFormatting.GRAY))
                                    .append(Component.literal(")").withStyle(ChatFormatting.GRAY)));

                    kit.registerClanLog(messageLog);

                    sPlayer.sendSystemMessage(
                            Component.translatable("item.warriorcats_events.kit.kit_born",
                                    Component.literal(finalName).withStyle(ChatFormatting.GREEN),
                                    Component.empty())
                    );
                }


            }
        });
    }

    public static final Type<CtSNameKitPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "name_kit"));

    public static final StreamCodec<FriendlyByteBuf, CtSNameKitPacket> CODEC =
            StreamCodec.of(
                    (buf, pkt) -> pkt.toBytes(buf),
                    buf -> new CtSNameKitPacket(buf)
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

