package net.snowteb.warriorcats_events.network.packet.c2s.others;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.effect.ModEffects;
import net.snowteb.warriorcats_events.managers.Sequence;

import java.util.function.Supplier;

public class CtSFishSuccesful {

    private final BlockPos pos;
    public CtSFishSuccesful(BlockPos pos) {
        this.pos = pos;
    }

    public static void encode(CtSFishSuccesful msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
    }

    public static CtSFishSuccesful decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        return new CtSFishSuccesful(pos);
    }

    public static void handle(CtSFishSuccesful msg, Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            InteractionHand pHand = InteractionHand.MAIN_HAND;
            ItemStack itemstack = player.getItemInHand(pHand);
            itemstack.hurtAndBreak(3, player, (p) -> p.broadcastBreakEvent(pHand));

            ServerLevel level = player.serverLevel();
            RandomSource random = level.getRandom();

            int itemNumber = random.nextInt(10) + 1;

            EntityType<?> toSpawn;

            switch (itemNumber) {
                case 1 -> toSpawn = EntityType.COD;
                case 2 -> toSpawn = EntityType.SALMON;
                case 3 -> toSpawn = EntityType.COD;
                case 4 -> toSpawn = EntityType.SALMON;
                case 5 -> toSpawn = EntityType.COD;
                case 6 -> toSpawn = EntityType.SALMON;
                case 7 -> toSpawn = EntityType.COD;
                case 8 -> toSpawn = EntityType.SALMON;
                case 9 -> toSpawn = EntityType.PUFFERFISH;
                case 10 -> toSpawn = EntityType.TROPICAL_FISH;
                default -> toSpawn = EntityType.COD;
            }

            AbstractFish fish = (AbstractFish) toSpawn.create(level);
            if (fish != null) {
                fish.setPos(msg.pos.getCenter());
                level.addFreshEntity(fish);
                fish.setHealth(1);
                Vec3 direction = player.position()
                        .subtract(Vec3.atCenterOf(msg.pos)).add(0,2,0)
                        .normalize();
                fish.setDeltaMovement(direction.scale(0.8));
                new Sequence(level).thenWait(20).then(
                        (lvl) -> fish.addEffect(new MobEffectInstance(ModEffects.NUMB_EFFECT.get(),
                                1000, 0, false, false))
                ).run();
            }

            ExperienceOrb.award(level, player.position(), random.nextInt(4) + 2);

            level.playSound(
                    null,
                    player.blockPosition(),
                    SoundEvents.AMBIENT_UNDERWATER_EXIT,
                    SoundSource.PLAYERS,
                    0.8F,
                    1.0F
            );
            level.playSound(
                    null,
                    player.blockPosition(),
                    SoundEvents.COD_DEATH,
                    SoundSource.PLAYERS,
                    0.8F,
                    1.0F
            );

            level.sendParticles(
                    ParticleTypes.SPLASH,
                    player.getX(),
                    player.getY() + 0.5,
                    player.getZ(),
                    30,
                    0.4, 0.2, 0.4,
                    0.02
            );

        });
        ctx.get().setPacketHandled(true);
    }

}
