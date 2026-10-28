package net.snowteb.warriorcats_events.battlemoves.types;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.snowteb.warriorcats_events.battlemoves.BattleMove;
import net.snowteb.warriorcats_events.battlemoves.client.ClientGetters;
import net.snowteb.warriorcats_events.managers.Sequence;
import net.snowteb.warriorcats_events.sound.ModSounds;

import java.util.UUID;

public class SlashMove extends BattleMove<SlashMove> {


    @Override
    public Sequence clientTrigger(UUID playerUUID, Level level) {
        return new Sequence(level)
                .then(lvl -> {
                    Player player = level.getPlayerByUUID(playerUUID);
                    if (player != null) {
                        ClientGetters.fovOffset(30, 0.05f);
                    }
                })
                .thenWait(10)
                .then(lvl -> {
                    Player player = level.getPlayerByUUID(playerUUID);
                    if (player != null) {
                        player.swing(InteractionHand.MAIN_HAND);

                        if (!player.onGround()){
                            player.setDeltaMovement(player.getDeltaMovement().add(0, -1, 0));
                        }
                        ClientGetters.fovOffset(0, 0.02f);
                    }
                });
    }

    @Override
    public Sequence serverTrigger(UUID playerUUID, ServerLevel level) {
        return new Sequence(level)
                .then(lvl -> {
                    Player player = level.getPlayerByUUID(playerUUID);
                    if (player != null) {
                        this.setShapeAnimation(((ServerPlayer) player));
                        level.playSound(null, player.blockPosition(), ModSounds.LONG_WOOSH.get(),
                                SoundSource.PLAYERS, 1.0f, 1.0f);

                        getEntitiesInCone(player, 8, 90, entity -> entity instanceof LivingEntity)
                                .forEach(e -> {
                                    ((LivingEntity) e).addEffect(new MobEffectInstance(MobEffects.LEVITATION, 10, 2));
                                });
                    }
                })
                .thenWait(10)
                .then(lvl -> {
                    Player player = level.getPlayerByUUID(playerUUID);
                    if (player != null) {
                        player.sweepAttack();
                        player.swing(InteractionHand.MAIN_HAND);

                        getEntitiesInCone(player, 8, 90, entity -> entity instanceof LivingEntity)
                                .forEach(e -> {
                                    e.hurt(player.damageSources().playerAttack(player), 3f);
                                    Vec3 look = player.getLookAngle().normalize();
                                    e.setDeltaMovement(look.scale(1f));
                                });

                        if (!player.onGround()){
                            level.playSound(null, player.blockPosition(), ModSounds.SHORT_WOOSH.get(),
                                    SoundSource.PLAYERS, 1.0f, 1.0f);
                            level.playSound(null, player.blockPosition(), SoundEvents.DRAGON_FIREBALL_EXPLODE,
                                    SoundSource.PLAYERS, 1.0f, 1.0f);
                        }
                    }
                })
                .thenWait(10)
                .then(lvl -> {
                    Player player = level.getPlayerByUUID(playerUUID);
                    if (player != null) {
                        this.clearShapeAnimation(((ServerPlayer) player));
                    }
                });
    }

    @Override
    protected boolean canTrigger(Player player) {
        return super.canTrigger(player);
    }

    @Override
    public int getCooldown(Player player) {
        return 200;
    }

    @Override
    public boolean tryTakeCost(Player player) {
        return true;
    }

    @Override
    public Component getCostFailedMessage(Player player) {
        return Component.empty();
    }

    @Override
    public String getAnimation(ServerPlayer player) {
        return "animation.wcat.attack";
    }

    @Override
    public boolean animationLoops(ServerPlayer player) {
        return false;
    }
}
