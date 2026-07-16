package net.snowteb.warriorcats_events.diseases;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.managers.Sequence;
import org.jetbrains.annotations.ApiStatus;
import tocraft.walkers.api.PlayerShape;

import java.util.ArrayList;
import java.util.List;

public class Disease<T extends Disease<T>> {

    private DiseaseType<T> type;

    private int level;
    private int ageSecs;
    private int healedLevel;
    private int healingCooldown;
    private boolean forRemove;

    public Disease() {
        this.level = 1;
        this.ageSecs = 0;
        this.healedLevel = 0;
        this.healingCooldown = 0;
    }

    public final T copy() {
        T copy = type.create();
        copy.loadNBT(this.getData());
        return copy;
    }

    @SuppressWarnings("unchecked")
    protected final T self() {
        return (T) this;
    }

    protected final void tick(LivingEntity entity) {
        if (entity.isDeadOrDying()) return;
        if (entity instanceof Player player) {
            if (!(PlayerShape.getCurrentShape(player) instanceof WCatEntity)) return;
        }
        if (!(entity instanceof Diseaseable<?> diseaseable)) return;
        type.getTickAction().accept(diseaseable, self());

    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = Mth.clamp(level, 0, type.getMaxLevel());
    }

    public final DiseaseType<T> getType() {
        return type;
    }

    protected void defaultTickEvent(Diseaseable<?> diseaseable, boolean shouldUpdate) {
        LivingEntity entity = diseaseable.getEntity();
        if (entity.level().isClientSide()) return;
        if (entity.tickCount % 20 != 0) return;

        boolean changed = shouldUpdate;

        ageTick(diseaseable);

        if (getHealedLevel() >= getType().getMaxHealLevel()) diseaseable.remove(getType());

        if (levelTick(diseaseable)) changed = true;

        if (getHealingCooldown() > 0) {
            healingCooldown--;
            if (getHealingCooldown() == 0) changed = true;
        }

        if (type.isContagious()) {
            contagion(entity);
        }


        if (changed) {
            diseaseable.onChange();
        }
    }

    /**
     *
     * @return Should return true only when the level has changed.
     */
    protected boolean levelTick(Diseaseable<?> diseaseable) {
        boolean changed = false;

        if (!type.isInfinite()) {
            if (getLevel() < type.getMaxLevel()) {
                if (ageSecs % increaseLevelEvery() == 0) {
                    setLevel(getLevel() + 1);
                    changed = true;
                }
            }
        } else {
            if (ageSecs % increaseLevelEvery() == 0) {
                setLevel(getLevel() + 1);
                changed = true;
            }
        }

        if (changed) onIncreaseLevel(diseaseable);


        return changed;
    }

    protected int increaseLevelEvery() {
        return type.getMaxTimeSeconds() / type.getMaxLevel() + 1;
    }

    public void onIncreaseLevel(Diseaseable<?> diseaseable) {
    }

    private void ageTick(Diseaseable<?> diseaseable) {
        if (!getType().isInfinite()) {
            if (getAge() < getType().getMaxTimeSeconds()) ageSecs++;
            else diseaseable.remove(getType());
        } else {
            ageSecs++;
        }
    }

    protected void contagion(LivingEntity entity) {
        if (entity.getRandom().nextFloat() < contagionChance()) {
            contagionDisease(entity, 0.75D);
        }
    }

    protected void contagionDisease(LivingEntity entity, double area) {
        List<LivingEntity> entitiesNearby = entity.level().getEntitiesOfClass(LivingEntity.class,
                entity.getBoundingBox().inflate(area), target -> {
                    return !target.equals(entity)
                            && target instanceof Diseaseable<?> dis
                            && !dis.hasDisease(type)
                            ;
                });
        for (LivingEntity livingEntity : entitiesNearby) {
            if (livingEntity instanceof Diseaseable<?> dis2) {
                dis2.addDisease(type, true);
            }
        }
    }

    public float contagionChance() {
        return getRelativeValue(0.005f, 0.05f);
    }

    public boolean heal(LivingEntity entity) {
        if (!canHeal()) return false;

        if (entity.level() instanceof ServerLevel sLevel) {
            sLevel.sendParticles(
                    ParticleTypes.HAPPY_VILLAGER,
                    entity.getX(), entity.getY(), entity.getZ(),
                    5, 0.2, 0.2, 0.2, 0.02
            );
        }

        healedLevel++;
        setLevel(getLevel()-1);
        healingCooldown = type.getMaxTimeSeconds() / (type.getMaxLevel() * 2);
        if (entity instanceof Diseaseable<?> diseaseable) {
            diseaseable.onChange();
        }

        return true;
    }

    public Sequence diseaseHealingSequence(ServerLevel sLevel, LivingEntity entity) {
        return new Sequence(sLevel)
                .thenEveryFor(20, 300, () -> {
                    sLevel.sendParticles(
                            ParticleTypes.HAPPY_VILLAGER,
                            entity.getX(), entity.getY(), entity.getZ(),
                            5, 0.2, 0.2, 0.2, 0.02
                    );
                })
                .then(() -> {
                    if (heal(entity)) {
                        if (this.getType().getMaxHealLevel() > 1){
                            entity.sendSystemMessage(
                                    Component.translatable("managers.disease_improved",
                                            getType().getName().getString(),
                                            getHealLevel() + "/" + getType().getMaxHealLevel())
                            );
                        }

                        if (isHealed()) {
                            entity.sendSystemMessage(
                                    Component.translatable("managers.disease_healed",
                                            getType().getName().getString()
                                            ).withStyle(ChatFormatting.GREEN)
                            );
                        }
                    }
                });
    }

    public boolean canHeal() {
        return getHealingCooldown() <= 0;
    }

    public boolean shouldShowUnhealed() {
        return canHeal();
    }

    public int getHealingCooldown() {
        return healingCooldown;
    }

    public int getAge() {
        return ageSecs;
    }

    public void setAge(int age) {
        this.ageSecs = age;
    }

    public int getHealedLevel() {
        return healedLevel;
    }

    public void setHealedLevel(int healedLevel) {
        this.healedLevel = healedLevel;
    }

    public void setHealingCooldown(int healingCooldown) {
        this.healingCooldown = healingCooldown;
    }

    /**
     *
     * @param min the minimum value you will obtain.
     * @param max the max value you will get when the level is maxed.
     * @return A float based on the current level of the disease.
     */
    public float getRelativeValue(float min, float max) {
        if (type.getMaxLevel() <= 0) return min;

        return (float) (
                min * (Math.pow(
                        (max / min), (double) (getLevel() - 1) / (type.getMaxLevel() - 1)
                )));
    }

    public boolean isHealed() {
        return healedLevel >= type.getMaxHealLevel();
    }

    @ApiStatus.Internal
    public final void setForRemove() {
        forRemove = true;
    }

    @ApiStatus.Internal
    public final boolean shouldRemove() {
        return forRemove;
    }


    public int getHealLevel() {
        return healedLevel;
    }

    @ApiStatus.Internal
    protected final void setType(DiseaseType<T> tDiseaseType) {
        this.type = tDiseaseType;
    }

    /**
     * The methods for saving the Disease Data to NBT.
     * <p>
     * Don't forget to call the super() before adding your own fields.
     */
    public void loadNBT(CompoundTag diseaseTag) {
        ageSecs = (diseaseTag.getInt("DiseaseTime"));
        level = (diseaseTag.getInt("DiseaseLevel"));
        healedLevel = (diseaseTag.getInt("HealedLevel"));
        healingCooldown = (diseaseTag.getInt("HealedCD"));
        forRemove = (diseaseTag.getBoolean("remove"));
    }

    public void saveNBT(CompoundTag diseaseTag) {
        diseaseTag.putString("DiseaseID", getType().getID());
        diseaseTag.putInt("DiseaseTime", ageSecs);
        diseaseTag.putInt("DiseaseLevel", level);
        diseaseTag.putInt("HealedLevel", healedLevel);
        diseaseTag.putInt("HealedCD", healingCooldown);
        diseaseTag.putBoolean("remove", forRemove);
    }

    /**
     *
     * @return A CompoundTag containing the NBT of the disease.
     */
    public final CompoundTag getData() {
        CompoundTag tag = new CompoundTag();
        saveNBT(tag);
        return tag;
    }


    /**
     * @param tDiseaseable The instance of Diseasable
     * @return Whether the entity/player should be allowed to leap when they try to. A message can and should be included inside this method.
     */
    public boolean allowLeap(Diseaseable<?> tDiseaseable) {
        return true;
    }


    /**
     * @param tDiseaseable The instance of Diseasable
     * @return Whether the entity/player should be allowed to climb when they try to. A message can and should be included inside this method.
     */
    public boolean allowClimb(Diseaseable<?> tDiseaseable) {
        return true;
    }

    /**
     *
     * Called right after a disease has been added to the Diseasable
     */
    public <J extends LivingEntity> void onAdd(Diseaseable<J> tDiseaseable, boolean organic) {

    }

    /**
     *
     * Called right before a disease is removed from the Diseasable
     */
    public <J extends LivingEntity> void onRemove(Diseaseable<J> tDiseaseable) {

    }

    /**
     *
     * Called before a disease is added.
     */
    public <J extends LivingEntity> boolean canAdd(Diseaseable<J> tDiseaseable) {
        return true;
    }

    /**
     * This method is to be overridden to include your own DiseaseManager or the list of cures for your disease.
     */
    public List<ItemStack> getCures() {
        List<ItemStack> list = new ArrayList<>();
        for (Item item : DiseaseManager.getCuresFor(getType())) {
         list.add(new ItemStack(item));
        }

        return list;
    }

    public boolean isCorrectCure(ItemStack stack, Diseaseable<?> tDiseaseable) {
        for (ItemStack sample : getCures()) {
            if (stack.is(sample.getItem())) {
                return true;
            }
        }
        return false;
    }

    public <J extends LivingEntity> boolean allowReducedFallDamage(Diseaseable<J> tDiseaseable) {
        return true;
    }
}
