package net.snowteb.warriorcats_events.diseases.kinds;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.snowteb.warriorcats_events.diseases.Disease;
import net.snowteb.warriorcats_events.diseases.DiseaseTypes;
import net.snowteb.warriorcats_events.diseases.Diseaseable;
import net.snowteb.warriorcats_events.managers.Sequence;

public class Chills extends Disease<Chills> {

    public int maxFreeze = 30;
    private boolean shouldHeal = false;

    @Override
    public <T extends LivingEntity> void onAdd(Diseaseable<T> tDiseaseable, boolean organic) {

        if (tDiseaseable.getEntity() instanceof ServerPlayer player) {
            player.displayClientMessage(Component.translatable("managers.disease_got_chills").withStyle(ChatFormatting.AQUA),
                    false);
        }


        if (organic && tDiseaseable.getEntity().getRandom().nextFloat() < 0.2) {
            tDiseaseable.addDisease(DiseaseTypes.FEVER, true);
        }
    }

    @Override
    public void saveNBT(CompoundTag diseaseTag) {
        super.saveNBT(diseaseTag);
        diseaseTag.putInt("maxFreeze", maxFreeze);
        diseaseTag.putBoolean("shouldHeal", shouldHeal);
    }

    @Override
    public void loadNBT(CompoundTag diseaseTag) {
        super.loadNBT(diseaseTag);
        maxFreeze = diseaseTag.getInt("maxFreeze");
        shouldHeal = diseaseTag.getBoolean("shouldHeal");
    }

    @Override
    protected void defaultTickEvent(Diseaseable<?> diseaseable, boolean shouldUpdate) {
        super.defaultTickEvent(diseaseable, shouldUpdate);
        this.healingTick(diseaseable);
    }

    public void healingTick(Diseaseable<?> diseaseable) {
        LivingEntity ent = diseaseable.getEntity();
        if (!ent.level().isClientSide()){
            if (maxFreeze > 10) ent.setTicksFrozen(141);
        }

        if (shouldHeal && ent.tickCount % 10 == 0) {
            maxFreeze--;
            if (maxFreeze <= 0) {
                this.shouldHeal = false;
                if (heal(ent)) {
                    if (isHealed() && ent instanceof ServerPlayer player) {
                        player.sendSystemMessage(
                                Component.translatable("managers.disease_no_longer_has", getType().getName().getString())
                                        .withStyle(ChatFormatting.GREEN)
                        );
                    }
                }
            }
        }


    }

    @Override
    public Sequence diseaseHealingSequence(ServerLevel sLevel, LivingEntity entity) {
        return new Sequence(sLevel)
                .thenEveryFor(20, 120, () -> {
                    sLevel.sendParticles(
                            ParticleTypes.HAPPY_VILLAGER,
                            entity.getX(), entity.getY(), entity.getZ(),
                            2, 0.2, 0.2, 0.2, 0.02
                    );
                })
                .then(() -> {
                    this.shouldHeal = true;
                    if (entity instanceof ServerPlayer player) {
                        player.displayClientMessage(
                                Component.translatable("managers.disease_chills_feel_calmer")
                                        .withStyle(ChatFormatting.ITALIC)
                                        .withStyle(ChatFormatting.GRAY)
                                , true);
                    }
                    if (entity instanceof Diseaseable<?> diseaseable) {
                        diseaseable.onChange();
                    }
                });
    }

    @Override
    public boolean canHeal() {
        return super.canHeal() && !this.shouldHeal;
    }
}
