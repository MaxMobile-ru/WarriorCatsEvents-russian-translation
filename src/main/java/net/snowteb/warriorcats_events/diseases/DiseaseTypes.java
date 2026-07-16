package net.snowteb.warriorcats_events.diseases;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.damagesources.WCEDamageSources;
import net.snowteb.warriorcats_events.diseases.kinds.*;
import net.snowteb.warriorcats_events.effect.ModEffects;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.particles.WCEParticles;
import tocraft.walkers.api.PlayerShape;

@WCEDiseaseTypes
public class DiseaseTypes {

    public static final DiseaseType<CoughDisease> GREENCOUGH =
            DiseaseRegistry.register(
                    new DiseaseType<>(
                            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "greencough"),
                            3000, 5, 5,
                            (diseaseable, disease) -> {
                                LivingEntity entity = diseaseable.getEntity();

                                if (entity.level() instanceof ServerLevel sLevel) {
                                    if (entity.tickCount % 20 == 0) {

                                        sLevel.sendParticles(
                                                WCEParticles.GREENCOUGH.get(),
                                                entity.getX(), entity.getY() + 0.5, entity.getZ(),
                                                2 + disease.getLevel(), 0.1, 0.1, 0.1, 0.01
                                        );

                                        entity.addEffect(new MobEffectInstance(ModEffects.WEAK_PAWS, 100,
                                                Math.max(0, disease.getLevel() - 1), false, false));
                                        if (disease.getLevel() >= 3) {
                                            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100,
                                                    disease.getLevel() - 1, false, false));
                                        }

                                        if (entity.getRandom().nextFloat() < disease.getRelativeValue(0.02f, 0.1f)) {
                                            if (entity instanceof Player) {
                                                entity.hurt(WCEDamageSources.greencough(entity.level()), disease.getRelativeValue(2.0f, 12f));
                                            } else {
                                                entity.hurt(WCEDamageSources.greencough(entity.level()), disease.getRelativeValue(0.5f, 5f));
                                            }
                                        }

                                        if (entity.getRandom().nextFloat() < 0.004 && disease.getHealedLevel() < 2) {
                                            diseaseable.addDisease(DiseaseTypes.FEVER, true);
                                        }

                                    }
                                }

                            }, CoughDisease::new
                    ).infinite().contagious()
            );

    public static final DiseaseType<CoughDisease> WHITECOUGH =
            DiseaseRegistry.register(
                    new DiseaseType<>(
                            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "whitecough"),
                            1500, 3, 3,
                            (diseaseable, disease) -> {
                                LivingEntity entity = diseaseable.getEntity();

                                if (entity.level() instanceof ServerLevel sLevel) {
                                    if (entity.tickCount % 20 == 0) {

                                        sLevel.sendParticles(
                                                WCEParticles.WHITECOUGH.get(),
                                                entity.getX(), entity.getY() + 0.5, entity.getZ(),
                                                2, 0.2, 0.5, 0.2, 0.02
                                        );

                                        if (disease.getLevel() >= 2) {
                                            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100,
                                                    disease.getLevel() - 1, false, false));
                                        }

                                        if (entity.getRandom().nextFloat() < disease.getRelativeValue(0.01f, 0.04f)) {
                                            if (entity.getHealth() > 8) {
                                                if (entity instanceof Player) {
                                                    entity.hurt(WCEDamageSources.whitecough(entity.level()), disease.getRelativeValue(1.0f, 4f));
                                                } else {
                                                    entity.hurt(WCEDamageSources.whitecough(entity.level()), disease.getRelativeValue(0.2f, 2f));
                                                }
                                            }
                                        }

                                        if (entity.getRandom().nextFloat() < 0.004 && disease.getHealedLevel() < 2) {
                                            diseaseable.addDisease(DiseaseTypes.FEVER, true);
                                        }
                                    }
                                }

                            }, CoughDisease::new
                    ).contagious()
            );

    public static final DiseaseType<BrokenPaw> BROKEN_PAW =
            DiseaseRegistry.register(
                    new DiseaseType<>(
                            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "broken_leg"),
                            3600, 1, 5,
                            (diseaseable, disease) -> {
                                LivingEntity entity = diseaseable.getEntity();

                                if (entity.level() instanceof ServerLevel sLevel) {

                                    boolean isMoving = disease.isEntityMoving(entity.position());

                                    if (entity.tickCount % 20 == 0) {


                                        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 31,
                                                disease.getPainLevel(), false, false));
                                        entity.addEffect(new MobEffectInstance(ModEffects.WEAK_PAWS, 31,
                                                Mth.clamp(disease.getPainLevel(), 0, 2), false, false));

                                        if (disease.getPainLevel() > 3) {
                                            entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 31,
                                                    0, false, false));
                                        }

                                        float extraChance = 0f;
                                        if (entity.isSprinting()) extraChance = 0.5f;
                                        if (entity.isInWater()) extraChance = -0.05f;
                                        if (entity.isShiftKeyDown()) extraChance = -0.03f;
                                        extraChance -= disease.getHealedLevel()*0.1f;

                                        if (entity.getRandom().nextFloat() < (0.1 + extraChance) && isMoving) {
                                            disease.hurt(entity);
                                        }

                                    }

                                    if (entity.tickCount % 100 == 0 && !isMoving) {
                                        if (disease.getPainLevel() > 0) disease.setPainLevel(disease.getPainLevel()-1);
                                    }

                                    disease.setLastPos(entity.position());

                                }

                            }, BrokenPaw::new
                    ).infinite()
            );

    public static final DiseaseType<Chills> CHILLS =
            DiseaseRegistry.register(
                    new DiseaseType<>(
                            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "chills"),
                            600, 1, 1,
                            (diseaseable, disease) -> {
                                LivingEntity entity = diseaseable.getEntity();

                                if (entity.level() instanceof ServerLevel) {

                                    if (entity.tickCount % 20 == 0) {

                                        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 31,
                                                disease.getLevel(), false, false));

                                        if (entity.getRandom().nextFloat() < 0.05) {
                                            if (entity.getHealth() > entity.getMaxHealth()/2) entity.hurt(entity.damageSources().freeze(), 1f);
                                        }

                                    }

                                }

                            }, Chills::new
                    )
            );


    public static final DiseaseType<DeathberriesPoisoning> DEATHBERRIES_POISONING =
            DiseaseRegistry.register(
                    new DiseaseType<>(
                            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "deathberries_poisoning"),
                            350, 8, 1,
                            (diseaseable, disease) -> {
                                LivingEntity entity = diseaseable.getEntity();

                                if (entity.level() instanceof ServerLevel) {

                                    if (entity.tickCount % 20 == 0) {

                                        entity.addEffect(new MobEffectInstance(ModEffects.DEATHBERRIES, 41,
                                                disease.getLevel() - 1, false, false));

                                        entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 160,
                                                0, false, false));

                                        if (disease.getLevel() > 4) {
                                            entity.addEffect(new MobEffectInstance(ModEffects.NUMB_EFFECT,
                                                    41, 0, false, false));
                                            entity.addEffect(new MobEffectInstance(MobEffects.WITHER,
                                                    41, 0, false, false));

                                            if (entity instanceof ServerPlayer player){
                                                if (PlayerShape.getCurrentShape(player) instanceof WCatEntity cat) {
                                                    cat.setAnimIndex(10);
                                                    player.getPersistentData().putInt("wcat_animation_playing", player.server.getTickCount() + 10);
                                                    PlayerShape.updateShapes(player, cat);
                                                }
                                            }

                                        }
                                    }

                                }

                            }, DeathberriesPoisoning::new
                    ).infinite()
            );

    public static final DiseaseType<SimpleDisease> FEVER =
            DiseaseRegistry.register(
                    new DiseaseType<>(
                            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "fever"),
                            1200, 1, 2,
                            (diseaseable, disease) -> {
                                LivingEntity entity = diseaseable.getEntity();

                                if (entity.level() instanceof ServerLevel) {

                                    if (entity.tickCount % 20 == 0) {

                                        entity.addEffect(new MobEffectInstance(ModEffects.FEVER, 80,
                                                0, false, false));

                                        entity.addEffect(new MobEffectInstance(ModEffects.WEAK_PAWS, 160,
                                                0, false, false));

                                        if (entity.getRandom().nextFloat() < 0.01) {
                                            entity.addEffect(new MobEffectInstance(ModEffects.NUMB_EFFECT,
                                                    60,0, false, false));
                                        }
                                    }

                                }

                            }, SimpleDisease::new
                    )
            );

    public static final DiseaseType<SorePads> SORE_PADS =
            DiseaseRegistry.register(
                    new DiseaseType<>(
                            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "sore_pads"),
                            1500, 3, 2,
                            (diseaseable, disease) -> {
                                LivingEntity entity = diseaseable.getEntity();

                                if (entity.level() instanceof ServerLevel) {

                                    boolean isMoving = disease.isEntityMoving(entity.position());

                                    if (entity.tickCount % 20 == 0) {

                                        if (disease.getLevel() > 1) {
                                            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,
                                                    60,Math.max(disease.getLevel() - 2, 0), false, false));
                                        }

                                        if (isMoving && entity.getRandom().nextFloat() < disease.getRelativeValue(0.004f, 0.009f)) {
                                            diseaseable.tryHurt(entity.damageSources().fall(), disease.getLevel(), 4f);
                                        }
                                    }

                                    disease.setLastPos(entity.position());
                                }

                            }, SorePads::new
                    )
            );


}
