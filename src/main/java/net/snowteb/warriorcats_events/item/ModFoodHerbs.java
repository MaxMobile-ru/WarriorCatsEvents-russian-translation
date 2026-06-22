package net.snowteb.warriorcats_events.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.snowteb.warriorcats_events.attachments.CapabilityManager;
import net.snowteb.warriorcats_events.attachments.ModAttachments;
import net.snowteb.warriorcats_events.diseases.DiseaseTypes;
import net.snowteb.warriorcats_events.diseases.Diseaseable;
import net.snowteb.warriorcats_events.effect.ModEffects;
import net.snowteb.warriorcats_events.util.ModTags;

public class ModFoodHerbs {

    // Traveling herb. Can also run up appetite and quench thirst.
    public static final FoodProperties SORREL = new FoodProperties.Builder().alwaysEdible().fast()
            .nutrition(1).saturationModifier(1f)
            .effect(() -> new MobEffectInstance(MobEffects.CONFUSION,140,0),1f)
            .build();

    // A traveling herb. The leaves are swallowed. Used to give strength. Good for expecting queens.
    public static final FoodProperties BURNET = new FoodProperties.Builder().alwaysEdible().fast()
            .nutrition(1).saturationModifier(2f)
            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST,1200,0),0.3f)
            .effect(() -> new MobEffectInstance(MobEffects.CONFUSION,140,0),1f)
            .build();

    // Strengthens the heart and soothes the mind. Also given to traveling cats for strength.
    public static final FoodProperties CHAMOMILE = new FoodProperties.Builder().alwaysEdible().fast()
            .nutrition(1).saturationModifier(2f)
            .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED,1200,0),0.3f)
            .effect(() -> new MobEffectInstance(MobEffects.CONFUSION,140,0),1f)
            .build();

    // Chewed into a paste. Eases the pain of aching joints, such as back pain. It is also a traveling herb.
    public static final FoodProperties DAISY = new FoodProperties.Builder().alwaysEdible().fast()
            .nutrition(1).saturationModifier(2f)
            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,1200,0),0.3f)
            .effect(() -> new MobEffectInstance(MobEffects.CONFUSION,140,0),1f)
            .build();

    // Traveling herbs bundle
    public static final FoodProperties TRAVELING_HERBS = new FoodProperties.Builder().alwaysEdible().fast()
            .nutrition(1).saturationModifier(14f)
            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,3600,0),1f)
            .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED,3600,0),1f)
            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST,3600,0),1f)
            .effect(() -> new MobEffectInstance(MobEffects.CONFUSION,160,0),1f)
            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION,160,0),1f)
            .build();

    // Deathberries
    public static final FoodProperties DEATHBERRIES = new FoodProperties.Builder().alwaysEdible().fast()
            .nutrition(1).saturationModifier(2f)
            .effect(() -> new MobEffectInstance(MobEffects.CONFUSION,7200,0),1f)
            .build();

    // Catmint
    public static final FoodProperties CATMINT = new FoodProperties.Builder().alwaysEdible().fast()
            .nutrition(1).saturationModifier(2f)
            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION,1200,0),0.15f)
            .effect(() -> new MobEffectInstance(MobEffects.SLOW_FALLING,120,0),0.15f)
            .effect(() -> new MobEffectInstance(MobEffects.LEVITATION,20,0),0.05f)
            .build();

    // blah blah blep
    public static final FoodProperties YARROW = new FoodProperties.Builder().alwaysEdible().fast()
            .nutrition(1).saturationModifier(1f)
            .effect(() -> new MobEffectInstance(MobEffects.CONFUSION,600,0),1f)
            .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,600,1),1f)
            .build();

    public static final FoodProperties FEVERFEW = new FoodProperties.Builder().alwaysEdible().fast()
            .nutrition(1).saturationModifier(2f)
            .effect(() -> new MobEffectInstance(MobEffects.CONFUSION,140,0),1f)
            .build();

    public static final FoodProperties JUNIPER = new FoodProperties.Builder().alwaysEdible().fast()
            .nutrition(2).saturationModifier(2f)
            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST,1200,0),0.3f)
            .effect(() -> new MobEffectInstance(MobEffects.CONFUSION,140,0),1f)
            .build();

    public static final FoodProperties POPPY_SEEDS = new FoodProperties.Builder().alwaysEdible().fast()
            .nutrition(0).saturationModifier(0f)
            .effect(() -> new MobEffectInstance(ModEffects.TIRED,1200,0),1f)
            .build();

    public static void herbsEffects(ItemStack stack, Player player) {

        if (stack.getItem() == ModItems.YARROW.get()) {

            if (player.hasEffect(MobEffects.POISON)) {
                player.removeEffect(MobEffects.POISON);
            }
            if (player.hasEffect(ModEffects.DEATHBERRIES)) {
                player.removeEffect(ModEffects.DEATHBERRIES);
            }

        }



        if (stack.is(ModItems.DEATHBERRIES.get()) || stack.is(ModTags.Items.SUSPICIOUS_FOOD)) {
            if (player instanceof Diseaseable<?> diseaseable) {
                diseaseable.addDisease(DiseaseTypes.DEATHBERRIES_POISONING, true);
            }
        }

        if (stack.is(ModItems.FEVERFEW.get())) {
            if (player.isOnFire()) player.setRemainingFireTicks(0);
        }

        if (stack.is(ModItems.POPPY_SEEDS.get())) {
            CapabilityManager.attachmentProvider(player, ModAttachments.PLAYER_WCE_DATA, cap -> {
                cap.setSleepingCooldown(0);
            });
        }
    }


    public static final FoodProperties MOUSE_FOOD = new FoodProperties.Builder()
            .nutrition(7).saturationModifier(1.0f).build();
    public static final FoodProperties SQUIRREL_FOOD = new FoodProperties.Builder()
            .nutrition(9).saturationModifier(1.1f).build();
    public static final FoodProperties PIGEON_FOOD = new FoodProperties.Builder()
            .nutrition(10).saturationModifier(1.3f).build();

    public static final FoodProperties EAGLE_MEAT_FOOD = new FoodProperties.Builder()
            .nutrition(5).saturationModifier(0.8f).build();

    public static final FoodProperties SHREDDED_MEAT = new FoodProperties.Builder()
            .nutrition(4).saturationModifier(0.7f).build();

    public static final FoodProperties FROG = new FoodProperties.Builder()
            .nutrition(8).saturationModifier(0.9f).build();
}
