package net.snowteb.warriorcats_events.network.packet.c2s.cats;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;

import java.util.function.Supplier;

public class CtSNameKitPacket {
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

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> {
            Player player = ctx.getSender();
            String prefix = this.kitPrefix;

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

                    finalName = prefix + "kit" + genderS;
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

        ctx.setPacketHandled(true);
    }
}

