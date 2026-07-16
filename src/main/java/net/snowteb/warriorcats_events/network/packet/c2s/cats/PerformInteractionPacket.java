package net.snowteb.warriorcats_events.network.packet.c2s.cats;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.item.ModItems;
import net.snowteb.warriorcats_events.util.ModTags;

public class PerformInteractionPacket implements CustomPacketPayload {

    private final int entityId;
    private final WCatEntity.CatInteraction interaction;

    public PerformInteractionPacket(int entityId, WCatEntity.CatInteraction interaction) {
        this.entityId = entityId;
        this.interaction = interaction;
    }

    public static void encode(PerformInteractionPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.entityId);
        buf.writeEnum(msg.interaction);
    }

    public static PerformInteractionPacket decode(FriendlyByteBuf buf) {
        return new PerformInteractionPacket(
                buf.readInt(),
                buf.readEnum(WCatEntity.CatInteraction.class)
        );
    }

    public static void handle(PerformInteractionPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {

            ServerPlayer player = (ServerPlayer) ctx.player();

            ServerLevel level = player.serverLevel();
            Entity entity = level.getEntity(msg.entityId);

            if (!(entity instanceof WCatEntity cat)) return;

            if (cat.getInteractionCooldown() > 0) {
                Component name = cat.hasCustomName() ? cat.getCustomName() : Component.literal("This cat");
                player.sendSystemMessage(Component.empty().append(name.copy().withStyle(ChatFormatting.GRAY)).append(Component.literal(" already interacted!").withStyle(ChatFormatting.GRAY)));
                return;
            }

            switch (msg.interaction) {

                case SHOW_AFFECTION -> interactShowAffection(cat, player, msg);

                case GIVE_ITEM -> interactGivePrey(cat, player, msg);

                case TALK -> interactTalk(cat, player, msg);
            }

        });

    }

    private static void interactShowAffection(WCatEntity cat, ServerPlayer player, PerformInteractionPacket msg) {
        boolean result = cat.getDialogueModule().randomInteractionResultProcess(player.getUUID(), WCatEntity.CatInteraction.SHOW_AFFECTION);

        if (result) {
            player.serverLevel().playSound(null, cat.blockPosition(), SoundEvents.CAT_PURREOW, SoundSource.NEUTRAL, 0.6F, 1.0F);
            player.serverLevel().playSound(null, cat.blockPosition(), SoundEvents.CAT_PURR, SoundSource.NEUTRAL, 1F, 1.0F);
            player.serverLevel().sendParticles(ParticleTypes.HEART, cat.getX(), cat.getY(),cat.getZ(),10,0.4f,0.4f,0.4f,0.1f);
        } else {
            player.serverLevel().sendParticles(ParticleTypes.SMOKE, cat.getX(), cat.getY(),cat.getZ(),30,0.4f,0.4f,0.4f,0.2f);
        }
        cat.setInteractionCooldown(6000);
    }

    private static void interactGivePrey(WCatEntity cat, ServerPlayer player, PerformInteractionPacket msg) {
        ItemStack preyStack = findPrey(player);
        if (preyStack != null) {
            boolean result = cat.getDialogueModule().randomInteractionResultProcess(player.getUUID(), WCatEntity.CatInteraction.GIVE_ITEM);
            if (result) {
                preyStack.shrink(1);
                player.serverLevel().playSound(null, cat.blockPosition(), SoundEvents.CAT_EAT, SoundSource.NEUTRAL, 0.6F, 1.0F);
                player.serverLevel().playSound(null, cat.blockPosition(), SoundEvents.PLAYER_BURP, SoundSource.NEUTRAL, 0.6F, 1.0F);
                player.serverLevel().playSound(null, cat.blockPosition(), SoundEvents.GENERIC_EAT, SoundSource.NEUTRAL, 0.6F, 1.0F);
                player.serverLevel().sendParticles(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.BEEF)), cat.getX(), cat.getY(),cat.getZ(),30,0.4f,0.4f,0.4f,0.1f);
                cat.addEffect(new MobEffectInstance(MobEffects.REGENERATION ,160 ,1));
            } else {
                player.serverLevel().sendParticles(ParticleTypes.SMOKE, cat.getX(), cat.getY(),cat.getZ(),30,0.4f,0.4f,0.4f,0.2f);
            }
            cat.setInteractionCooldown(6000);

        } else {
            player.sendSystemMessage(Component.translatable("generic.nothing_to_offer").withStyle(ChatFormatting.GRAY));
        }
    }

    private static void interactTalk(WCatEntity cat, ServerPlayer player, PerformInteractionPacket msg) {
        boolean result = cat.getDialogueModule().randomInteractionResultProcess(player.getUUID(), WCatEntity.CatInteraction.TALK);
        if (result) {
            player.serverLevel().sendParticles(ParticleTypes.HAPPY_VILLAGER, cat.getX(), cat.getY(),cat.getZ(),30,0.4f,0.4f,0.4f,0.1f);
        } else {
            player.serverLevel().sendParticles(ParticleTypes.SMOKE, cat.getX(), cat.getY(),cat.getZ(),30,0.4f,0.4f,0.4f,0.2f);
        }
        cat.setInteractionCooldown(6000);
    }

    private static ItemStack findPrey(ServerPlayer player) {
        for (ItemStack stack : player.getInventory().items) {
            if (stack.isEmpty()) continue;
            Item item = stack.getItem();
            if (item == ModItems.SQUIRREL_FOOD.get() ||
                    item == ModItems.MOUSE_FOOD.get() ||
                    item == ModItems.PIGEON_FOOD.get() ||
                    stack.is(ModTags.Items.PREY)) {
                return stack;
            }
        }

        return null;
    }

    public static final Type<PerformInteractionPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "perform_interaction"));

    public static final StreamCodec<FriendlyByteBuf, PerformInteractionPacket> CODEC =
            StreamCodec.of(
                    (buf, pkt) -> encode(pkt, buf),
                    buf -> decode(buf)
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

