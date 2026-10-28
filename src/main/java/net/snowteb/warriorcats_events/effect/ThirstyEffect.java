package net.snowteb.warriorcats_events.effect;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.s2c.others.ThirstDataSyncStCPacket;
import net.snowteb.warriorcats_events.thirst.PlayerThirstProvider;

public class ThirstyEffect extends MobEffect {
    public ThirstyEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);


    }

    /**
     * Hurt the entity every time this is called.
     * And if it's a player, then remove its saturation as well
     */
    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {

        if (entity instanceof ServerPlayer player) {
            player.getCapability(PlayerThirstProvider.PLAYER_THIRST).ifPresent(thirstCap -> {
                if (thirstCap.getThirst() > 0){
                    thirstCap.subThirst(1);
                    ModPackets.sendToPlayer(new ThirstDataSyncStCPacket(thirstCap.getThirst()), player);
                }
            });
        }
    }

    /**
     * Whatever the applyEfectTick does, it will do it every 40 ticks instead of every tickkkk.
     */
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }


}

