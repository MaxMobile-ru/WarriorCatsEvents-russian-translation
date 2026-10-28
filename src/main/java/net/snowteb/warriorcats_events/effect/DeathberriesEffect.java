package net.snowteb.warriorcats_events.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.snowteb.warriorcats_events.damagesources.WCEDamageSources;

public class DeathberriesEffect extends MobEffect {
    public DeathberriesEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);

        this.addAttributeModifier(Attributes.MOVEMENT_SPEED,
                "7f5d3d4b-1a2c-4e69-b7f1-9f8c5d9b6a3d",
                -0.4F, AttributeModifier.Operation.MULTIPLY_BASE);

    }

    /**
     * Hurt the entity every time this is called.
     * And if it's a player, then remove its saturation as well
     */
    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {

        entity.hurt(WCEDamageSources.deathberries(entity.level()), 1.0F + (amplifier*1.2f));

        if (entity instanceof Player player) {
            player.getFoodData().setSaturation(player.getFoodData().getSaturationLevel() - 3*amplifier);
        }
    }

    /**
     * Whatever the applyEffectTick does, it will do it every 40 ticks instead of every tickkkk.
     */
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 40 == 0;
    }

    @Override
    public double getAttributeModifierValue(int pAmplifier, AttributeModifier pModifier) {
        return pModifier.getAmount() - 0.1f*pAmplifier;
    }
}

