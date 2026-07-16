package net.snowteb.warriorcats_events.network.packet.c2s.others;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.attachments.ModAttachments;
import net.snowteb.warriorcats_events.attachments.WCEPlayerData;
import net.snowteb.warriorcats_events.entity.custom.EagleEntity;
import net.snowteb.warriorcats_events.item.ModItems;
import net.snowteb.warriorcats_events.sound.ModSounds;

import java.util.ArrayList;
import java.util.List;

public class RenameNametagPacket implements CustomPacketPayload {

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

    public static void handle(RenameNametagPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {

            ServerPlayer player = (ServerPlayer) ctx.player();

            ItemStack stack = player.getItemInHand(msg.hand);
            if (stack.is(ModItems.WARRIOR_NAMETAG.get())) {

                if (!msg.newName.isEmpty()){
                    stack.set(DataComponents.CUSTOM_NAME, Component.literal(msg.newName));
                } else {
                    stack.set(DataComponents.CUSTOM_NAME, null);
                }
            }


        });
    }

    public static final Type<RenameNametagPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "rename_nametag"));

    public static final StreamCodec<FriendlyByteBuf, RenameNametagPacket> CODEC =
            StreamCodec.of(
                    (buf, pkt) -> encode(pkt, buf),
                    buf -> decode(buf)
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

