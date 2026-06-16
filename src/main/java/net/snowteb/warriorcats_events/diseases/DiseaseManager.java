package net.snowteb.warriorcats_events.diseases;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;
import net.snowteb.warriorcats_events.compat.CompatibilitiesServer;
import net.snowteb.warriorcats_events.entity.custom.WCatEntity;
import net.snowteb.warriorcats_events.item.ModItems;
import net.snowteb.warriorcats_events.managers.ClimbDataAccessor;
import net.snowteb.warriorcats_events.zconfig.WCEServerConfig;

import java.util.*;

public class DiseaseManager {
    private static final Map<DiseaseType<?>, List<InteractiveCure>> CURES = new HashMap<>();

    public static boolean healDiseaseMobInteract(ItemStack stack, Diseaseable<?> diseaseable) {
        if (!(diseaseable.getEntity().level() instanceof ServerLevel serverLevel)) return false;

        List<DiseaseType<?>> diseasesFor = getDiseasesFor(stack.getItem(), diseaseable);
        if (diseasesFor.isEmpty()) return false;
        DiseaseType<?> diseaseToHeal = diseasesFor.get(0);
        if (diseaseToHeal == null) return false;
        if (!getCureInteraction(diseaseToHeal, stack.getItem()).defaultConsumable()) return false;

        Disease<?> instance = diseaseable.getDisease(diseaseToHeal);

        if (instance.canHeal() && instance.isCorrectCure(stack, diseaseable)) {
            instance.diseaseHealingSequence(serverLevel, diseaseable.getEntity()).run();
            stack.shrink(1);
            return true;
        }

        return false;
    }

    public static void healDisease(ItemStack stack, ServerPlayer player) {
        if (!(player instanceof Diseaseable<?> diseaseable)) return;
        List<DiseaseType<?>> diseasesFor = getDiseasesFor(stack.getItem(), diseaseable);
        if (diseasesFor.isEmpty()) return;
        DiseaseType<?> diseaseToHeal = diseasesFor.get(0);
        if (diseaseToHeal == null) return;
        if (!getCureInteraction(diseaseToHeal, stack.getItem()).defaultConsumable()) return;

        Disease<?> instance = diseaseable.getDisease(diseaseToHeal);

        ServerLevel sLevel = player.serverLevel();
        if (instance.canHeal() && instance.isCorrectCure(stack, diseaseable)) {
            instance.diseaseHealingSequence(sLevel, player).run();
        }

    }

    public static void tick(Diseaseable<?> diseaseable) {
        LivingEntity entity = diseaseable.getEntity();
        if (!(entity.level() instanceof ServerLevel sLevel)) return;
        if (entity.tickCount % 80 != 0) return;
        if (!WCEServerConfig.SERVER.DISEASES.get()) return;

        boolean gotSick = false;
        if (isInSnow(entity, sLevel)) {
            if (sLevel.getRandom().nextFloat() < 0.0003) {
                if (sLevel.getRandom().nextFloat() < 0.2 || diseaseable.hasDisease(DiseaseTypes.WHITECOUGH)) {
                    diseaseable.addDisease(DiseaseTypes.GREENCOUGH, true);
                } else {
                    diseaseable.addDisease(DiseaseTypes.WHITECOUGH, true);
                }
                gotSick = true;
            }


            if (sLevel.getRandom().nextFloat() < 0.0005) {
                diseaseable.addDisease(DiseaseTypes.CHILLS, true);
                gotSick = true;
            }
        }


        if (gotSick) {
            if (entity instanceof WCatEntity cat && cat.getOwner() instanceof Player player) {
                String name = "A clanmate";
                player.sendSystemMessage(Component.literal(name + " has gotten sick.")
                        .withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
            }
        }

        if (isSorePads(entity, sLevel) && sLevel.getRandom().nextFloat() < 0.005) {
            diseaseable.addDisease(DiseaseTypes.SORE_PADS, true);
        }

    }

    private static boolean isInSnow(LivingEntity entity, ServerLevel sLevel) {
        boolean freezing = entity.isInPowderSnow;
        BlockPos o = entity.blockPosition();

        boolean inSnowyBiome = sLevel.getBiome(o).is(Tags.Biomes.IS_SNOWY);
        boolean underSnowOrRain = sLevel.canSeeSky(o) && sLevel.isRainingAt(o);

        int snowBlocksCount = 0;
        for (int x = -3; x <= 3; x++) {
            for (int y = -1; y <= 0; y++) {
                for (int z = -3; z <= 3; z++) {
                    BlockState state = sLevel.getBlockState(new BlockPos(o.getX() + x, o.getY() + y, o.getZ() + z));
                    if (state.is(Blocks.SNOW) || state.is(Blocks.SNOW_BLOCK)
                            || state.is(Blocks.POWDER_SNOW) || state.is(BlockTags.ICE)) {
                        snowBlocksCount++;
                    }
                    if (snowBlocksCount >= 4) break;
                }
                if (snowBlocksCount == 4) break;
            }
            if (snowBlocksCount == 4) break;
        }
        boolean snowyBlocks = snowBlocksCount == 4;

        return freezing || ((inSnowyBiome || CompatibilitiesServer.isLeafBare(sLevel)) && (snowyBlocks || underSnowOrRain));
    }

    private static boolean isSorePads(LivingEntity entity, ServerLevel sLevel) {
        if (!(entity instanceof ClimbDataAccessor climber)) return false;

        boolean climbingExhausted;
        boolean sprintTooLong;

        climbingExhausted = climber.wce$getExhaustion() > 70;
        sprintTooLong = climber.wce$getSprintTime() > 800;

        boolean soringSurface = false;
        boolean overusedHardSurface = false;

        BlockState under = sLevel.getBlockState(entity.blockPosition().below());

        boolean burningMagmaBlock = under.is(Blocks.MAGMA_BLOCK) && !(entity.isSteppingCarefully() || hasFrostWalker(entity));

        if (burningMagmaBlock
                || under.is(Blocks.CACTUS)
                || under.is(Blocks.CAMPFIRE)
                || under.is(Blocks.SOUL_CAMPFIRE)
        ) soringSurface = true;

        if (under.is(BlockTags.BASE_STONE_OVERWORLD)
                || under.is(BlockTags.BASE_STONE_NETHER)
        ) overusedHardSurface = climber.wce$getSprintTime() > 600;


        return climbingExhausted || soringSurface || sprintTooLong || overusedHardSurface;
    }

    public static List<Item> getCuresFor(DiseaseType<?> type) {
        if (CURES.isEmpty()) initCures();
        List<InteractiveCure> cures = CURES.get(type);
        if (cures == null) return new ArrayList<>();
        List<Item> list = new ArrayList<>();
        for (InteractiveCure cure : cures) {
            if (cure.item() == null) continue;
            list.add(cure.item());
        }

        return list;
    }

    public static InteractiveCure getCureInteraction(DiseaseType<?> type, Item item) {
        for (InteractiveCure cure : CURES.get(type)) {
            if (cure.item() != item) continue;

            return cure;
        }
        return new InteractiveCure(Items.AIR, false);
    }

    private static List<DiseaseType<?>> getDiseasesFor(Item item, Diseaseable<?> diseaseable) {
        if (CURES.isEmpty()) initCures();

        List<DiseaseType<?>> list = new ArrayList<>();

        for (DiseaseType<?> disease : CURES.keySet()) {
            for (InteractiveCure cure : CURES.get(disease)) {
                if (!cure.defaultConsumable()) continue;
                if (cure.item().equals(item)) {
                    if (diseaseable.hasDisease(disease)) {
                        Disease<?> instance = diseaseable.getDisease(disease);
                        if (instance.canHeal() && instance.shouldShowUnhealed()) {
                            list.add(disease);
                        }
                    }
                }
            }

        }
        return list;
    }

    private static void initCures() {
        CURES.clear();

        CURES.put(DiseaseTypes.GREENCOUGH, List.of(
                new InteractiveCure(ModItems.CATMINT.get(), true)
        ));

        CURES.put(DiseaseTypes.WHITECOUGH, List.of(
                new InteractiveCure(ModItems.CATMINT.get(), true),
                new InteractiveCure(ModItems.JUNIPER_BERRIES.get(), true)
        ));

        CURES.put(DiseaseTypes.CHILLS, List.of(
                new InteractiveCure(ModItems.FEVERFEW.get(), true),
                new InteractiveCure(ModItems.JUNIPER_BERRIES.get(), true),
                new InteractiveCure(ModItems.POPPY_SEEDS.get(), true)
        ));

        CURES.put(DiseaseTypes.DEATHBERRIES_POISONING, List.of(
                new InteractiveCure(ModItems.YARROW.get(), true)
        ));

        CURES.put(DiseaseTypes.BROKEN_PAW, List.of(
                new InteractiveCure(ModItems.LEG_WRAP.get(), false)
        ));

        CURES.put(DiseaseTypes.FEVER, List.of(
                new InteractiveCure(ModItems.FEVERFEW.get(), true)
        ));

        CURES.put(DiseaseTypes.SORE_PADS, List.of(
                new InteractiveCure(ModItems.DOCK_POULTICE.get(), false),
                new InteractiveCure(ModItems.YARROW_POULTICE.get(), false)
        ));
    }

    public record InteractiveCure(Item item, boolean defaultConsumable){}

    public static void refreshData(ServerPlayer player) {
        if (player instanceof Diseaseable<?> diseaseable) {
            diseaseable.onChange();
        }
    }

    private static final Map<UUID, List<DiseaseInstance>> pendingToAdd = new HashMap<>();

    public record DiseaseInstance(Disease<?> disease, boolean organic){}

    public static void diseaseAdd(LivingEntity entity, Disease<?> disease,  boolean organic) {
        pendingToAdd.computeIfAbsent(entity.getUUID(), k -> new ArrayList<>());
        pendingToAdd.get(entity.getUUID()).add(new DiseaseInstance(disease, organic));
    }

    public static List<DiseaseInstance> pendingDiseases(LivingEntity entity) {
        pendingToAdd.computeIfAbsent(entity.getUUID(), k -> new ArrayList<>());
        return pendingToAdd.get(entity.getUUID());
    }

    public static void clearPendingDiseases(LivingEntity entity) {
        pendingToAdd.remove(entity.getUUID());
    }

    public static boolean hasFrostWalker(LivingEntity entity) {
        for (ItemStack stack : entity.getArmorSlots()) {
            if (stack.isEmpty()) continue;

            int level = stack.getEnchantmentLevel(
                    entity.registryAccess().holderOrThrow(Enchantments.FROST_WALKER)
            );

            if (level > 0) {
                return true;
            }
        }
        return false;
    }
}
