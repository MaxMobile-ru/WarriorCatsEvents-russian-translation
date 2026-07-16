package net.snowteb.warriorcats_events.diseases.kinds;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.snowteb.warriorcats_events.diseases.Disease;
import net.snowteb.warriorcats_events.diseases.DiseaseTypes;
import net.snowteb.warriorcats_events.diseases.Diseaseable;

public class CoughDisease extends Disease<CoughDisease> {

    @Override
    public <T extends LivingEntity> void onAdd(Diseaseable<T> tDiseaseable, boolean organic) {
        if (this.getType() == DiseaseTypes.GREENCOUGH) {
            Disease<?> base = tDiseaseable.getDisease(DiseaseTypes.WHITECOUGH);
            if (base == null) return;
            Disease<?> whitecough = base.copy();
            if (tDiseaseable.removeDisease(DiseaseTypes.WHITECOUGH)) {
                tDiseaseable.getEntity().sendSystemMessage(
                        Component.translatable("managers.disease_turned_into",
                                Component.literal(DiseaseTypes.WHITECOUGH.getName().getString()).withStyle(ChatFormatting.GRAY),
                                Component.literal(DiseaseTypes.GREENCOUGH.getName().getString()).withStyle(ChatFormatting.DARK_GREEN))
                );
                Disease<?> greencough = tDiseaseable.getDisease(DiseaseTypes.GREENCOUGH);
                greencough.setLevel(whitecough.getLevel());
            }

            if (organic && tDiseaseable.getEntity().getRandom().nextFloat() < 0.2) {
                tDiseaseable.addDisease(DiseaseTypes.FEVER, true);
            }
        }
    }

    @Override
    public <T extends LivingEntity> void onRemove(Diseaseable<T> tDiseaseable) {

    }

    @Override
    public <T extends LivingEntity> boolean canAdd(Diseaseable<T> tDiseaseable) {
        if (this.getType() == DiseaseTypes.WHITECOUGH && tDiseaseable.hasDisease(DiseaseTypes.GREENCOUGH)) return false;

        return super.canAdd(tDiseaseable);
    }

    @Override
    protected void defaultTickEvent(Diseaseable<?> diseaseable, boolean change) {
        super.defaultTickEvent(diseaseable, change);
    }

    @Override
    public void onIncreaseLevel(Diseaseable<?> diseaseable) {
        if (this.getType().is(DiseaseTypes.WHITECOUGH)) {
            if (this.getLevel() >= this.getType().getMaxLevel()) {
                diseaseable.addDisease(DiseaseTypes.GREENCOUGH, true);
            }
        }
    }

}
