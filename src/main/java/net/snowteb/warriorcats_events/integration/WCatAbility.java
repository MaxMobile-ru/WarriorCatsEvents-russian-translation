package net.snowteb.warriorcats_events.integration;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.snowteb.warriorcats_events.effect.ModEffects;
import net.snowteb.warriorcats_events.item.ModItems;
import net.snowteb.warriorcats_events.sound.ModSounds;
import tocraft.walkers.Walkers;
import tocraft.walkers.ability.GenericShapeAbility;

public class WCatAbility<T extends LivingEntity> extends GenericShapeAbility<T>{

    public static final MapCodec<WCatAbility<?>> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.stable(new WCatAbility<>()));
    public static final ResourceLocation ID = Walkers.id("wcat_night_vision");


    @Override
    public MapCodec<? extends GenericShapeAbility<?>> codec() {
        return CODEC;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void onUse(Player player, LivingEntity shape, Level world) {

        if (world.isClientSide) {
            return;
        }

        if (world.isNight()) {
            world.playSound(null,player.blockPosition(), SoundEvents.CAT_PURREOW, SoundSource.PLAYERS, 0.7f,1f);
            world.playSound(null,player.blockPosition(), ModSounds.LONG_WOOSH.get(), SoundSource.PLAYERS, 0.9f,1f);
            ((ServerLevel) player.level()).sendParticles(ParticleTypes.CLOUD, player.getX(),player.getY(),player.getZ(),5,0.2,0.2,0.2,0.4);
        } else {
            world.playSound(null,player.blockPosition(), SoundEvents.CAT_PURREOW, SoundSource.PLAYERS, 0.7f,1f);
            world.playSound(null,player.blockPosition(), ModSounds.LONG_WOOSH.get(), SoundSource.PLAYERS, 0.9f,1f);
        }
        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 300, 0, true, false));

        player.addEffect(new MobEffectInstance(ModEffects.SHARP_EYE.get(), 1100, 0, true, false));
        player.addEffect(new MobEffectInstance(ModEffects.SHARP_SCENT.get(), 500, 0, true, false));

    }

    @Override
    public Item getIcon() {
        return ModItems.WHISKERS.get();
    }

    @Override
    public int getDefaultCooldown() {
        return 1200;
    }
}
