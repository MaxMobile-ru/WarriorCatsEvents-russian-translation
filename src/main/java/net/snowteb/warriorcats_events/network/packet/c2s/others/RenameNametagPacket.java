package net.snowteb.warriorcats_events.network.packet.c2s.others;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.clan.WCEPlayerData;
import net.snowteb.warriorcats_events.clan.WCEPlayerDataProvider;
import net.snowteb.warriorcats_events.entity.custom.EagleEntity;
import net.snowteb.warriorcats_events.item.ModItems;
import net.snowteb.warriorcats_events.sound.ModSounds;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class RenameNametagPacket {

    private final String newName;
    private final InteractionHand hand;

    public RenameNametagPacket(String newName, InteractionHand hand) {
        this.newName = newName;
        this.hand = hand;
    }

    public static void encode(RenameNametagPacket msg, FriendlyByteBuf buf) {
        buf.writeEnum(msg.hand);
        buf.writeUtf(msg.newName);
    }

    public static RenameNametagPacket decode(FriendlyByteBuf buf) {
        InteractionHand hand = buf.readEnum(InteractionHand.class);
        String newName = buf.readUtf(384);

        return new RenameNametagPacket(newName, hand);
    }

    public static void handle(RenameNametagPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {

            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            ItemStack stack = player.getItemInHand(msg.hand);
            if (stack.is(ModItems.WARRIOR_NAMETAG.get())) {

                if (!msg.newName.isEmpty()){
                    stack.setHoverName(Component.literal(msg.newName));
                } else {
                    stack.resetHoverName();
                }
            }

        });

        ctx.get().setPacketHandled(true);
    }

}

