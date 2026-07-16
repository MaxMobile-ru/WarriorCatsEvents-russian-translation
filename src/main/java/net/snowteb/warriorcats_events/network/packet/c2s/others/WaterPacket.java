package net.snowteb.warriorcats_events.network.packet.c2s.others;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
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
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.attachments.CapabilityManager;
import net.snowteb.warriorcats_events.attachments.ModAttachments;
import net.snowteb.warriorcats_events.entity.custom.MossBallEntity;
import net.snowteb.warriorcats_events.item.custom.MossBallItem;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.s2c.others.ThirstDataSyncStCPacket;
import tocraft.walkers.api.PlayerShape;

public class WaterPacket implements CustomPacketPayload {

    public WaterPacket() {

    }

    public WaterPacket(FriendlyByteBuf buf) {

    }

    public void toBytes(FriendlyByteBuf buf) {

    }

    public boolean handle(IPayloadContext context) {

        context.enqueueWork(() -> {

            ServerPlayer player = (ServerPlayer) context.player();
            ServerLevel level = player.serverLevel().getLevel();

            if (isLookingAtWater(player, level)) {

                level.playSound(null, player.blockPosition(), SoundEvents.DROWNED_SWIM, SoundSource.PLAYERS, 0.2f, 1.5f);


                CapabilityManager.attachmentProvider(player, ModAttachments.PLAYER_THIRST, thirst -> {
                    thirst.addThirst(1);
                    ModPackets.sendToPlayer(new ThirstDataSyncStCPacket(thirst.getThirst()), player);
                    level.sendParticles(ParticleTypes.SPLASH, player.getX(), player.getY() + 0.4, player.getZ(), 10, 0.3, 0.2, 0.3, 0.01);
                });


            } else if (isLookingAtCauldron(player, level)) {

                drinkFromCauldron(player, level);

            } else if (isLookingAtMossBall(player, level)) {

                drinkFromMossBall(player, level);

            } else {

                player.displayClientMessage(Component.translatable("generic.not_water").withStyle(ChatFormatting.RED), true);

                CapabilityManager.attachmentProvider(player, ModAttachments.PLAYER_THIRST, thirst -> {
                    ModPackets.sendToPlayer(new ThirstDataSyncStCPacket(thirst.getThirst()), player);
                });

            }

        });

        return true;
    }

    private boolean isLookingAtWater(ServerPlayer player, ServerLevel level) {
        double reach = 2.0D;

        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();
        Vec3 endPos = eyePos.add(lookVec.scale(reach));

        ClipContext context = new ClipContext(
                eyePos,
                endPos,
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.ANY,
                player
        );

        BlockHitResult hitResult = level.clip(context);
        if (hitResult.getType() != HitResult.Type.BLOCK) return false;

        BlockPos pos = hitResult.getBlockPos();
        BlockState state = level.getBlockState(pos);

        return state.getFluidState().is(FluidTags.WATER);
    }

    private boolean isLookingAtCauldron(ServerPlayer player, ServerLevel level) {
        double reach = 2.0D;

        if (!(PlayerShape.getCurrentShape(player) instanceof Animal)) return false;

        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();
        Vec3 endPos = eyePos.add(lookVec.scale(reach));

        ClipContext context = new ClipContext(
                eyePos,
                endPos,
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.ANY,
                player
        );

        BlockHitResult hitResult = level.clip(context);
        if (hitResult.getType() != HitResult.Type.BLOCK) return false;

        BlockPos pos = hitResult.getBlockPos();
        BlockState state = level.getBlockState(pos);

        return state.getBlock() instanceof AbstractCauldronBlock;
    }

    private void drinkFromCauldron(ServerPlayer player, ServerLevel level) {
        double reach = 2.0D;

        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();
        Vec3 endPos = eyePos.add(lookVec.scale(reach));

        ClipContext context = new ClipContext(
                eyePos,
                endPos,
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.ANY,
                player
        );

        BlockHitResult hitResult = level.clip(context);
        if (hitResult.getType() != HitResult.Type.BLOCK) return;

        BlockPos pos = hitResult.getBlockPos();
        BlockState state = level.getBlockState(pos);

        if (state.getBlock() instanceof LayeredCauldronBlock) {

            if (!level.isClientSide) {

                float thirstLevel = player.getData(ModAttachments.PLAYER_THIRST).getThirst();

                if (thirstLevel >= 20f) return;


                CapabilityManager.attachmentProvider(player, ModAttachments.PLAYER_THIRST, thirst -> {
                    thirst.addThirst(1);
                    ModPackets.sendToPlayer(new ThirstDataSyncStCPacket(thirst.getThirst()), player);
                    level.sendParticles(ParticleTypes.SPLASH, player.getX(), player.getY() + 0.4, player.getZ(), 10, 0.3, 0.2, 0.3, 0.01);
                });

                level.playSound(null, player.blockPosition(), SoundEvents.DROWNED_SWIM, SoundSource.PLAYERS, 0.2f, 1.5f);

                if (level.getRandom().nextInt(8) == 0) {
                    LayeredCauldronBlock.lowerFillLevel(state, level, pos);
                    level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
                }

            }


        }
    }


    private boolean isLookingAtMossBall(ServerPlayer player, ServerLevel level) {
        double reach = 2.0D;

        if (!(PlayerShape.getCurrentShape(player) instanceof Animal)) return false;

        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();
        Vec3 endPos = eyePos.add(lookVec.scale(reach));

        AABB box = player.getBoundingBox().expandTowards(lookVec.scale(reach)).inflate(1.0D);

        EntityHitResult hitResult = ProjectileUtil.getEntityHitResult(
                level, player, eyePos, endPos, box,
                entity -> entity instanceof MossBallEntity
        );

        return hitResult != null && hitResult.getEntity() instanceof MossBallEntity;
    }

    private void drinkFromMossBall(ServerPlayer player, ServerLevel level) {
        double reach = 2.0D;

        if (!(PlayerShape.getCurrentShape(player) instanceof Animal)) return;

        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();
        Vec3 endPos = eyePos.add(lookVec.scale(reach));

        AABB box = player.getBoundingBox().expandTowards(lookVec.scale(reach)).inflate(1.0D);

        EntityHitResult hitResult = ProjectileUtil.getEntityHitResult(
                level, player, eyePos, endPos, box,
                entity -> entity instanceof MossBallEntity
        );

        if (hitResult != null && hitResult.getEntity() instanceof MossBallEntity mossBall) {
            if (!level.isClientSide) {

                if (player.getFoodData().needsFood()) {

                    if (mossBall.getHoney() > 0) {
                        int newLevel = mossBall.getHoney() - 1;
                        mossBall.setHoney(newLevel);

                        level.sendParticles(ParticleTypes.FALLING_HONEY, player.getX(), player.getY() + 0.4, player.getZ(), 10, 0.1, 0.1, 0.1, 0.01);
                        level.playSound(null, player.blockPosition(), SoundEvents.HONEY_DRINK, SoundSource.PLAYERS, 0.2f, 1.5f);

                        player.getFoodData().eat(2, 0.3f);

                        ItemStack stack = mossBall.getItem().copy();
                        MossBallItem.setHoneyLevel(stack, newLevel);

                        mossBall.setStack(stack);

                        return;
                    }
                }

                float thirstLevel = player.getData(ModAttachments.PLAYER_THIRST).getThirst();

                if (thirstLevel >= 20f) return;

                if (mossBall.getWater() <= 0) {
                    player.displayClientMessage(Component.translatable("generic.doesnt_have_water").withStyle(ChatFormatting.GRAY), true);
                    return;
                }

                CapabilityManager.attachmentProvider(player, ModAttachments.PLAYER_THIRST, thirst -> {
                    thirst.addThirst(1);
                    ModPackets.sendToPlayer(new ThirstDataSyncStCPacket(thirst.getThirst()), player);
                    level.sendParticles(ParticleTypes.SPLASH, player.getX(), player.getY() + 0.4, player.getZ(), 10, 0.3, 0.2, 0.3, 0.01);


                    level.playSound(null, player.blockPosition(), SoundEvents.DROWNED_SWIM, SoundSource.PLAYERS, 0.2f, 1.5f);

                    int newWaterLevel = mossBall.getWater() - 1;


                    mossBall.setWater(newWaterLevel);

                    ItemStack stack = mossBall.getItem().copy();
                    MossBallItem.setWaterLevel(stack, newWaterLevel);
                    mossBall.setStack(stack);
                });


            }
        }
    }

    public static final Type<WaterPacket> TYPE
            = new Type<>(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "water_packet"));

    public static final StreamCodec<FriendlyByteBuf, WaterPacket> CODEC =
            StreamCodec.of(
                    (buf, pkt) -> pkt.toBytes(buf),
                    buf -> new WaterPacket(buf)
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
