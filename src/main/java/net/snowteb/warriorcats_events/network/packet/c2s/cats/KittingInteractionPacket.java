package net.snowteb.warriorcats_events.network.packet.c2s.cats;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.clan.WCEPlayerData;
import net.snowteb.warriorcats_events.clan.WCEPlayerDataProvider;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.item.ModItems;
import net.snowteb.warriorcats_events.zconfig.WCEServerConfig;

import java.util.function.Supplier;

public class KittingInteractionPacket {

    private final int entityId;

    public KittingInteractionPacket(int entityId) {
        this.entityId = entityId;
    }

    public static void encode(KittingInteractionPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.entityId);
    }

    public static KittingInteractionPacket decode(FriendlyByteBuf buf) {
        return new KittingInteractionPacket(
                buf.readInt()
        );
    }

    public static void handle(KittingInteractionPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {

            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;


            ServerLevel level = player.serverLevel();
            Entity entity = level.getEntity(msg.entityId);

//            int sPlayerGender = player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
//                    .map(WCEPlayerData::getGenderData).orElse(-1);

            String morphName = player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                    .map(WCEPlayerData::getMorphName).orElse("your character");

            if (!(entity instanceof WCatEntity cat)) return;

            int kittingCooldown = cat.getKittingInteractCooldown();

            int kittingCD = (int) ((WCEServerConfig.SERVER.KIT_GROWTH_MINUTES.get() * 20 * 60) * 0.75f);

            if (cat.getFriendshipLevel(player.getUUID()) < 98) {
                return;
            }

//            if (sPlayerGender == 0 || sPlayerGender == 1) {
//                if (cat.getGender() == sPlayerGender) {
//                    Component name = cat.hasCustomName() ? cat.getCustomName() : Component.literal("This cat");
//                    player.sendSystemMessage(Component.empty()
//                            .append(name.copy().withStyle(ChatFormatting.AQUA))
//                            .append(Component.literal(" and ").withStyle(ChatFormatting.YELLOW))
//                            .append(Component.literal(morphName).withStyle(ChatFormatting.AQUA))
//                            .append(Component.literal(" can't have kits!").withStyle(ChatFormatting.YELLOW))
//                    );
//                    return;
//                }
//            }

            if (kittingCooldown > 0) {
                Component name = cat.hasCustomName() ? cat.getCustomName() : Component.translatable("generic.this_cat");
                player.sendSystemMessage(Component.translatable("generic.cat_had_kits_recently",
                                name)
                        .withStyle(ChatFormatting.GRAY));
                return;
            }

            ItemStack kitStack = new ItemStack(ModItems.KIT_ITEM.get(), 1 + cat.getRandom().nextInt(3));
            ItemEntity kitItemEntity = new ItemEntity(level, cat.getX(), cat.getY() + 0.5, cat.getZ(), kitStack);
            float yaw = cat.yBodyRot;
            double rad = Math.toRadians(yaw);

            Vec3 look = new Vec3(
                    -Math.sin(rad),
                    0,
                    Math.cos(rad)
            );
            double impulse = 0.15;
            kitItemEntity.setDeltaMovement(look.x * impulse, 0.2, look.z * impulse);
            kitItemEntity.setDefaultPickUpDelay();
            level.addFreshEntity(kitItemEntity);

            cat.setKittingInteractCooldown(kittingCD);

            level.playSound(null, cat.blockPosition(), SoundEvents.CAT_PURREOW,
                    SoundSource.AMBIENT, 0.7F, 1.0F);
            level.sendParticles(ParticleTypes.HEART, cat.getX(), cat.getY(), cat.getZ(),
                    3, 0.2f,0.2f,0.2f,0.2f);
            player.sendSystemMessage(
                    Component.translatable("generic.kits_brought_to_world",
                            Component.literal(morphName).withStyle(ChatFormatting.GOLD),
                            cat.hasCustomName()
                                    ? cat.getCustomName().copy().withStyle(ChatFormatting.GOLD)
                                    : Component.translatable("generic.this_cat").withStyle(ChatFormatting.WHITE)
                            )
            );

        });

        ctx.get().setPacketHandled(true);
    }

}

