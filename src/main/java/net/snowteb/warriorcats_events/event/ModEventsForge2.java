package net.snowteb.warriorcats_events.event;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.player.*;
import net.neoforged.neoforge.event.level.SleepFinishedTimeEvent;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.attachments.CapabilityManager;
import net.snowteb.warriorcats_events.attachments.ModAttachments;
import net.snowteb.warriorcats_events.block.ModBlocks;
import net.snowteb.warriorcats_events.block.custom.*;
import net.snowteb.warriorcats_events.block.entity.KittypetBowlBlockEntity;
import net.snowteb.warriorcats_events.clan.ClanData;
import net.snowteb.warriorcats_events.diseases.*;
import net.snowteb.warriorcats_events.entity.custom.EagleEntity;
import net.snowteb.warriorcats_events.entity.custom.MossBallEntity;
import net.snowteb.warriorcats_events.entity.custom.WCatAvoidGoal;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.item.ModFoodHerbs;
import net.snowteb.warriorcats_events.item.ModItems;
import net.snowteb.warriorcats_events.managers.Sequence;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.s2c.others.ThirstDataSyncStCPacket;
import net.snowteb.warriorcats_events.particles.WCEParticles;
import net.snowteb.warriorcats_events.util.ModTags;
import net.snowteb.warriorcats_events.zconfig.WCEPreyItemsConfig;
import net.snowteb.warriorcats_events.zconfig.WCEServerConfig;
import tocraft.walkers.api.PlayerShape;

import java.util.*;

@EventBusSubscriber(modid = WarriorCatsEvents.MODID)
public class ModEventsForge2 {

    private static final Map<UUID, List<ItemStack>> savedItems = new HashMap<>();

    @SubscribeEvent
    public static void playerRespawn(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;

        List<ItemStack> saved = savedItems.remove(event.getOriginal().getUUID());
        if (saved != null) {
            for (ItemStack item : saved) {
                event.getEntity().addItem(item);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        Iterator<ItemEntity> it = event.getDrops().iterator();
        while (it.hasNext()) {
            ItemEntity ie = it.next();


            if (ie.getItem().is(ModItems.CLAWS.get())
                    || ie.getItem().is(ModItems.WHISKERS.get())
                    || ie.getItem().is(ModItems.WARRIORS_GUIDE.get())) {
                savedItems.computeIfAbsent(player.getUUID(), k -> new ArrayList<>()).add(ie.getItem());
                it.remove();
            }
        }

    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.isCanceled()) return;
        Level level = event.getLevel();
        Player player = event.getEntity();
        InteractionHand hand = event.getHand();
        if (hand != InteractionHand.MAIN_HAND) return;
        ItemStack stack = event.getItemStack();

        BlockPos pos = event.getPos();
        BlockState blockState = level.getBlockState(pos);

        if (level.getBlockState(pos).is(Blocks.STONE)) {
            if (stack.is(ModItems.CLAWS.get())){
                if (!level.isClientSide()) {
                    if (level.getRandom().nextInt(4) == 0){
                        level.destroyBlock(pos, false);

                        Direction dir = event.getFace();
                        if (dir != null) {
                            BlockState state = ModBlocks.CARVED_STONE.get().defaultBlockState()
                                    .setValue(WCEStoneBlock.FACING, dir);
                            level.setBlockAndUpdate(pos, state);
                        }

                        ItemStack newStack = new ItemStack(ModItems.PEBBLES_ITEM.get(), 2);

                        ItemEntity itemEntity = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), newStack);
                        level.addFreshEntity(itemEntity);
                    } else {
                        Vec3 position = pos.getCenter();
                        ((ServerLevel) level).sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, blockState),
                                position.x, position.y, position.z, 30, 0.0, 0.0, 0.0, 0.5);
                    }
                }
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
            }
        }

        if (level.getBlockState(pos).is(Blocks.COBWEB)) {
            if (stack.is(Items.STICK)) {
                if (!level.isClientSide()) {
                    level.destroyBlock(pos, false);
                    stack.shrink(1);

                    ItemStack newStack = new ItemStack(ModItems.COBWEB_WITH_A_STICK.get());
                    if (!player.addItem(newStack)) {
                        player.drop(newStack, false);
                    }
                }
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
            }
        }

        if ((level.getBlockState(pos).getBlock() instanceof KittyPetBowl bowlBlock)) {
            if (player.getItemInHand(hand).is(Items.WATER_BUCKET) && !player.isShiftKeyDown()) {
                boolean handled = false;
                if (!level.isClientSide()) {
                    if (level.getBlockEntity(pos) instanceof KittypetBowlBlockEntity bowl) {

                        if (bowl.canRefillWater()) {
                            bowl.performFillWater((ServerLevel) level, bowlBlock.getCenterOfBowl(pos, blockState), false);

                            if (!player.getAbilities().instabuild) {
                                player.setItemInHand(event.getHand(), new ItemStack(Items.BUCKET));
                            }

                            bowlBlock.updateBlockStateFromEntity(bowl, blockState, level, pos);
                            handled = true;
                        }
                    }
                }

                if (handled) event.setCancellationResult(InteractionResult.SUCCESS);
                else  event.setCancellationResult(InteractionResult.CONSUME);
                event.setCanceled(true);
            }
        }

        if (!level.isClientSide()) {
            if (player.getItemInHand(hand).is(Items.BONE)) {
                if (blockState.isSolid()) {
                    BlockPos above = pos.above();
                    BlockState onTop = level.getBlockState(above);
                    if (onTop.isAir() || onTop.is(BlockTags.SMALL_FLOWERS)
                            || onTop.is(Blocks.SHORT_GRASS)
                            || onTop.is(Blocks.TALL_GRASS)
                    ) {
                        BlockState newBlockstate = ModBlocks.PREY_BONES.get().defaultBlockState()
                                .setValue(PreyBonesBlock.FACING, player.getDirection().getOpposite());

                        level.setBlockAndUpdate(above, newBlockstate);

                        if (!player.getAbilities().instabuild) player.getItemInHand(hand).shrink(1);

                        level.playSound(null, pos, SoundEvents.BONE_BLOCK_PLACE, SoundSource.BLOCKS,
                                0.8F, 1.4F);

                        event.setCanceled(true);
                        event.setCancellationResult(InteractionResult.SUCCESS);
                    }
                } else if (blockState.is(ModBlocks.PREY_BONES.get())) {
                    PreyBonesBlock.Bones bones = blockState.getValue(PreyBonesBlock.BONES);

                    if (PreyBonesBlock.canPlaceBones(bones)){
                        BlockState newBlockstate = ModBlocks.PREY_BONES.get().defaultBlockState()
                                .setValue(PreyBonesBlock.FACING, blockState.getValue(PreyBonesBlock.FACING));

                        if (bones == PreyBonesBlock.Bones.STAGE_1) {
                            newBlockstate = newBlockstate.setValue(PreyBonesBlock.BONES, PreyBonesBlock.Bones.STAGE_2);
                        } else if (bones == PreyBonesBlock.Bones.STAGE_2) {
                            newBlockstate = newBlockstate.setValue(PreyBonesBlock.BONES, PreyBonesBlock.Bones.STAGE_3);
                        }

                        level.setBlockAndUpdate(pos, newBlockstate);
                        if (!player.getAbilities().instabuild) player.getItemInHand(hand).shrink(1);


                        level.playSound(null, pos, SoundEvents.BONE_BLOCK_PLACE, SoundSource.BLOCKS,
                                0.8F, 1.4F);

                        event.setCanceled(true);
                        event.setCancellationResult(InteractionResult.SUCCESS);
                    }

                }
            }
        }


    }


    @SubscribeEvent
    public static void onCanContinueSleeping(CanContinueSleepingEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity instanceof ServerPlayer) {
            if (event.getProblem() == Player.BedSleepingProblem.NOT_POSSIBLE_NOW) event.setContinueSleeping(true);
        }
    }


    @SubscribeEvent
    public static void onSleepAttempt(CanPlayerSleepEvent event) {
        ServerPlayer player = event.getEntity();
        if (!player.level().isDay()) return;

        if (player.level().isDay()) {
            event.setProblem(null);
        }

        int sleepingCooldown = player.getData(ModAttachments.PLAYER_WCE_DATA).getSleepingCooldown();

        if (sleepingCooldown > 0) {
            player.displayClientMessage(
                    Component.translatable("generic.not_tired_enough")
                            .withStyle(ChatFormatting.GRAY),
                    true
            );
            event.setProblem(Player.BedSleepingProblem.OTHER_PROBLEM);
        }
    }


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onSleepFinished(SleepFinishedTimeEvent event) {
        long quarterDay = 24000L / 4;
        LevelAccessor level = event.getLevel();
        if (level instanceof ServerLevel sLevel) {
            if (sLevel.isDay()) {
                long dayTime = sLevel.getDayTime();
                event.setTimeAddition(dayTime + quarterDay);
                WarriorCatsEvents.LOGGER.info("Time advanced for " + quarterDay + " ticks");
            }

            for (ServerPlayer player : sLevel.players()) {
                if (player.isSleeping()) {
                    CapabilityManager.attachmentProvider(player, ModAttachments.PLAYER_WCE_DATA, cap -> {
                        cap.setSleepingCooldown(12000);
                    });
                }
            }
        }
    }


    @SubscribeEvent
    public static void onPlayerWakeUp(PlayerWakeUpEvent event) {
        Player player = event.getEntity();

        BlockPos sleepingPos = player.getSleepingPos().orElse(null);
        if (sleepingPos == null) return;

        Level level = player.level();
        BlockState state = level.getBlockState(sleepingPos);

        if ((state.getBlock() instanceof NestBlock)) {
            level.setBlock(
                    sleepingPos,
                    state.setValue(NestBlock.OCCUPIED, false),
                    3
            );

            if (level instanceof ServerLevel) {
                ModEventsForge1.schedule(1, () -> {
                    Vec3 exactPos = sleepingPos.getCenter().add(0, 0.1, 0);
                    player.teleportTo(exactPos.x, exactPos.y, exactPos.z);
                });

            }
        } else if (state.getBlock() instanceof MakeshiftBedBlock) {
            if (level instanceof ServerLevel sLevel) {

                Vec3 exactPos = sleepingPos.getCenter().add(0, -0.3, 0);

                sLevel.sendParticles(
                        new BlockParticleOption(ParticleTypes.BLOCK, state),
                        exactPos.x, exactPos.y, exactPos.z,
                        15, 0.5, 0.3, 0.5, 0.1
                );

                sLevel.playSound(null, player.blockPosition(), SoundEvents.CHERRY_LEAVES_BREAK,
                        SoundSource.BLOCKS, 1.0F, 1.0F);

            }

            level.setBlock(
                    sleepingPos,
                    Blocks.AIR.defaultBlockState(),
                    3
            );

        }
    }


    /**
     * This modifies some foods so that some will fill thirsts, and some other will fill more hunger.
     */
    @SubscribeEvent
    public static void onFoodEaten(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player player)) return;
        ItemStack stack = event.getItem();

        if (player.level().isClientSide()) return;

        if (player instanceof ServerPlayer sPlayer) DiseaseManager.healDisease(stack, sPlayer);

        CapabilityManager.attachmentProvider(player, ModAttachments.PLAYER_THIRST, thirst -> {

            if (stack.has(DataComponents.FOOD)) {
                if (stack.getFoodProperties(player) == ModFoodHerbs.SORREL) {
                    int randomThirst = 2 + player.getRandom().nextInt(3);
                    thirst.addThirst(randomThirst);
                }

                if (stack.getFoodProperties(player) == ModFoodHerbs.TRAVELING_HERBS) {
                    thirst.addThirst(4);
                }

                if (stack.getFoodProperties(player) == ModFoodHerbs.MOUSE_FOOD) {
                    int randomThirst = 1 + player.getRandom().nextInt(2);
                    thirst.addThirst(randomThirst);
                }

                if (stack.getFoodProperties(player) == ModFoodHerbs.SQUIRREL_FOOD) {
                    int randomThirst = 2 + player.getRandom().nextInt(2);
                    thirst.addThirst(randomThirst);
                }

                if (stack.getFoodProperties(player) == ModFoodHerbs.PIGEON_FOOD) {
                    int randomThirst = 2 + player.getRandom().nextInt(2);
                    thirst.addThirst(randomThirst);
                }

                if (WCEServerConfig.SERVER.VANILLA_MEAT_BONUS.get()) {
                    if (stack.is(Items.CHICKEN) || stack.is(Items.PORKCHOP)
                            || stack.is(Items.BEEF) || stack.is(Items.MUTTON)
                            || stack.is(Items.RABBIT) || stack.is(Items.SALMON)
                            || stack.is(Items.COD) || stack.is(Items.TROPICAL_FISH)
                            || stack.is(Items.SWEET_BERRIES)) {
                        int randomThirst = 1 + player.getRandom().nextInt(1);
                        thirst.addThirst(randomThirst);
                        if (!stack.is(Items.SWEET_BERRIES)) {
                            player.getFoodData().eat(3, 0.84f);
                        }
                    }
                }

                if (stack.is(ModTags.Items.ADDITIONAL_PREY) || WCEPreyItemsConfig.PREY_ITEMS.contains(stack.getItem())) {
                    int randomThirst = 1 + player.getRandom().nextInt(2);
                    thirst.addThirst(randomThirst);
                    player.getFoodData().eat(4, 0.84f);
                }
            }

            if (stack.is(Items.POTION)) {
                PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
                if (contents != null && contents.is(Potions.WATER)) {
                    int randomThirst = 2 + player.getRandom().nextInt(2);
                    thirst.addThirst(randomThirst);
                }
            }


            if (player instanceof ServerPlayer serverPlayer) {
                ModPackets.sendToPlayer(new ThirstDataSyncStCPacket(thirst.getThirst()), serverPlayer);
            }
        });

        if (stack.has(DataComponents.FOOD)) {

            if (stack.is(Items.CHICKEN) || stack.is(Items.PORKCHOP)
                    || stack.is(Items.BEEF) || stack.is(Items.MUTTON)
                    || stack.is(Items.RABBIT) || stack.is(Items.SALMON)
                    || stack.is(Items.COD) || stack.is(Items.TROPICAL_FISH)
                    || stack.is(ModItems.SQUIRREL_FOOD.get()) || stack.is(ModItems.PIGEON_FOOD.get())
                    || stack.is(ModItems.MOUSE_FOOD.get())
            ) {
                if (event.getEntity().level().random.nextFloat() < 0.19) {
                    Level level = event.getEntity().level();
                    LivingEntity entity = event.getEntity();
                    if (entity instanceof Player && level instanceof ServerLevel) {
                        ItemStack bonestack = new ItemStack(Items.BONE, 1 + level.getRandom().nextInt(3));
                        ItemEntity itemEnt = new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(), bonestack);
                        itemEnt.setDeltaMovement(entity.getLookAngle().scale(0.3));
                        itemEnt.setPickUpDelay(60);
                        level.addFreshEntity(itemEnt);
                        level.playSound(null, itemEnt.blockPosition(), SoundEvents.TURTLE_EGG_CRACK, SoundSource.PLAYERS, 0.8f, 1.0f);
                    }
                }
            }
        }

        ModFoodHerbs.herbsEffects(stack, player);
    }


    /**
     * Every time an entity falls:
     * Check if it is a player, if it is not:
     * Then make a list of all the players in the server, and for every player verify if their UUID is the same as the shape that fell.
     * If it is, then choose it as the owner of the shape.
     * Then check if the owner has Jump level greater than 2. If it does, reduce it's fall distance.
     * <p>
     * If the entity is a Wild cat, reduce its fall distance too.
     */
    @SubscribeEvent
    public static void onFall(LivingFallEvent event) {
        LivingEntity entity = event.getEntity();

        if (!(entity instanceof Player || entity instanceof WCatEntity)) return;
        if ((entity instanceof Player p && !(PlayerShape.getCurrentShape(p) instanceof WCatEntity))) return;

        if (entity instanceof WCatEntity cat) {
            if (cat.level().isClientSide()) return;

            if (cat.getPlayerBoundUuid().equals(ClanData.EMPTY_UUID)) {
                if (cat.returnHomeFlag) {
                    event.setDistance(Math.max(0f, event.getDistance() - 12f));
                } else {
                    if (cat.allowReducedFallDamage()) {
                        event.setDistance(Math.max(0f, event.getDistance() - 9f));
                    }
                }
            } else {
                Player player = cat.level().getPlayerByUUID(cat.getPlayerBoundUuid());
                if (player instanceof ServerPlayer sPlayer) {
                    if (sPlayer instanceof Diseaseable<?> diseaseable) {
                        if (diseaseable.allowReducedFallDamage()) {
                            CapabilityManager.attachmentProvider(player, ModAttachments.PLAYER_SKILL, cap -> {
                                if (cap.getJumpLevel() >= 2) {
                                    event.setDistance(Math.max(0f, event.getDistance() - 8f));
                                }
                            });
                        } else {
                            if (diseaseable.hasDisease(DiseaseTypes.BROKEN_PAW)) {
                                event.setDistance(event.getDistance() + 8f);
                            }
                        }
                    }
                }
            }

        }

    }

//    private static void sendServerMsg(LivingEntity entity, String message) {
//        if (entity.level() instanceof ServerLevel slevel) {
//            for (ServerPlayer sPlayer : slevel.players()) {
//                sPlayer.sendSystemMessage(Component.literal(message));
//            }
//        }
//    }

    /**
     * Every time a creeper spawns, add this goal to it.
     * This so that creepers run away from Wild Cats.
     */
    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;

        if (event.getEntity() instanceof Diseaseable<?> diseaseable) {
            ServerLevel sLevel = ((ServerLevel) event.getLevel());
            new Sequence(sLevel).wait(10).then(() -> diseaseable.onChange()).run();
        }

        if (event.getEntity() instanceof Creeper creeper) {

            boolean alreadyHasAvoid = creeper.goalSelector.getAvailableGoals().stream()
                    .anyMatch(g -> g.getGoal() instanceof WCatAvoidGoal);

            if (!alreadyHasAvoid) {
                creeper.goalSelector.addGoal(3, new WCatAvoidGoal(creeper, 12.0F, 1.3D, 1.2D));
            }

        }

        if (WCEServerConfig.SERVER.ENHANCED_ANIMALS.get()) {
            if (event.getEntity() instanceof Fox fox) {
                CompoundTag tag = fox.getPersistentData();

                fox.goalSelector.getAvailableGoals().removeIf(g
                        -> g.getGoal() instanceof NearestAttackableTargetGoal<?>);
                fox.goalSelector.getAvailableGoals().removeIf(g
                        -> g.getGoal() instanceof MeleeAttackGoal);
                fox.goalSelector.getAvailableGoals().removeIf(g
                        -> g.getGoal() instanceof AvoidEntityGoal<?>);


                fox.goalSelector.addGoal(2,
                        new NearestAttackableTargetGoal<>(fox, LivingEntity.class, 10, false, false,
                                (animal) -> (animal instanceof Player player && PlayerShape.getCurrentShape(player) instanceof WCatEntity) || animal instanceof WCatEntity
                                        || animal instanceof Chicken || animal instanceof Rabbit || animal instanceof AbstractSchoolingFish));
                fox.goalSelector.addGoal(2, new MeleeAttackGoal(fox, 1.2D, true));

                if (!tag.getBoolean("fox_enhanced")) {
                    tag.putBoolean("fox_enhanced", true);
                    fox.getAttribute(Attributes.MAX_HEALTH).setBaseValue(30.0D);
                    fox.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.35D);
                    fox.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(5.2D);
                    fox.getAttribute(Attributes.ARMOR).setBaseValue(12D);
                    fox.setHealth(fox.getMaxHealth());
                }

            }

            if (event.getEntity() instanceof Wolf wolf) {
                CompoundTag tag = wolf.getPersistentData();

                wolf.goalSelector.getAvailableGoals().removeIf(g
                        -> g.getGoal() instanceof NearestAttackableTargetGoal<?>);

                wolf.goalSelector.addGoal(4,
                        new NearestAttackableTargetGoal<>(wolf, LivingEntity.class, 10, false, false,
                                (animal) -> (animal instanceof Player player && PlayerShape.getCurrentShape(player) instanceof WCatEntity) || animal instanceof WCatEntity || animal instanceof AbstractSkeleton));

                if (!tag.getBoolean("wolf_enhanced")) {
                    tag.putBoolean("wolf_enhanced", true);
                    wolf.getAttribute(Attributes.MAX_HEALTH).setBaseValue(30.0D);
                    wolf.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.35D);
                    wolf.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(5.2D);
                    wolf.getAttribute(Attributes.ARMOR).setBaseValue(12D);
                    wolf.setHealth(wolf.getMaxHealth());
                }

            }
        }

        if (event.getEntity() instanceof WCatEntity wCat) {
            CompoundTag tag = wCat.getPersistentData();

            if (wCat.isTame()) {
                LivingEntity owner = wCat.getOwner();
                if (owner instanceof ServerPlayer serverPlayer) {
                    UUID clanUUID = serverPlayer.getData(ModAttachments.PLAYER_WCE_DATA).getCurrentClanUUID();

                    if (!Objects.equals(clanUUID, wCat.getClanUUID())) {
                        wCat.setClanUUID(clanUUID);
                        wCat.updateClanCatData();
                    }
                }
            }

            if (!tag.getBoolean("spawn_att_applied")) {
                tag.putBoolean("spawn_att_applied", true);

                if (wCat.isBaby()) {
                    wCat.applyBabyAttributes();
                } else {
                    wCat.applyAdultAttributes();
                }

                wCat.setHealth(wCat.getMaxHealth());
            }
        }

        if (event.getEntity() instanceof ServerPlayer player) {
            ServerLevel level = player.serverLevel();
            UUID mateUUID = player.getData(ModAttachments.PLAYER_WCE_DATA).getMateUUID();
            Entity ent = level.getEntity(mateUUID);
            if (ent != null) {
                if (ent instanceof ServerPlayer playerMate) {
                    String myMorphName = player.getData(ModAttachments.PLAYER_WCE_DATA).getMorphName();

                    String mateMorphName = playerMate.getData(ModAttachments.PLAYER_WCE_DATA).getMorphName();

                    CapabilityManager.attachmentProvider(playerMate, ModAttachments.PLAYER_WCE_DATA, cap -> {
                        if (!cap.getMateName().getString().equals(myMorphName)) {
                            cap.setMateName(Component.literal(myMorphName));
                        }
                    });

                    CapabilityManager.attachmentProvider(player, ModAttachments.PLAYER_WCE_DATA, cap -> {
                        if (!cap.getMateName().getString().equals(mateMorphName)) {
                            cap.setMateName(Component.literal(mateMorphName));
                        }
                    });

                }
            }
        }

    }

    /**
     * Every time a player wants to attack a wild cat, check certain conditions, and under that criteria decide whether to let the player attack it or not.
     */
    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        Player player = event.getEntity();
        Entity target = event.getTarget();

        if (target instanceof EagleEntity eagle) {
            if (player.getVehicle() == eagle) {
                event.setCanceled(true);
            }
        }

        if (target instanceof ServerPlayer serverPlayer) {

            UUID targetUUID = serverPlayer.getData(ModAttachments.PLAYER_WCE_DATA).getCurrentClanUUID();
            UUID thisUUID = player.getData(ModAttachments.PLAYER_WCE_DATA).getCurrentClanUUID();

            if (!targetUUID.equals(thisUUID) || (targetUUID.equals(ClanData.EMPTY_UUID) || thisUUID.equals(ClanData.EMPTY_UUID))) {
                return;
            }
            if (!player.isShiftKeyDown()) {
                event.setCanceled(true);
            }
        }

        if (!(target instanceof WCatEntity || target instanceof EagleEntity)) {
            return;
        }

        WCatEntity wcat = null;
        if (target instanceof WCatEntity) wcat = (WCatEntity) target;

        EagleEntity eagle = null;
        if (target instanceof EagleEntity) eagle = (EagleEntity) target;

        if (player.level().isClientSide()) {
            return;
        }

        if (wcat != null) {

            if (!wcat.isTame()) {
                return;
            }

            {
                AABB box = wcat.getBoundingBox().inflate(0.5);
                List<MossBallEntity> mossBalls = wcat.level().getEntitiesOfClass(MossBallEntity.class, box);
                if (!mossBalls.isEmpty()) {
                    event.setCanceled(true);
                }
            }

            if (player instanceof ServerPlayer serverPlayer) {
                if (!wcat.getClanUUID().equals(ClanData.EMPTY_UUID)) {
                    UUID clanUUID = serverPlayer.getData(ModAttachments.PLAYER_WCE_DATA).getCurrentClanUUID();

                    ClanData data = ClanData.get(serverPlayer.serverLevel().getServer().overworld());
                    ClanData.Clan clan = data.getClan(clanUUID);
                    if (clan != null) {
                        if (!wcat.getClanUUID().equals(clanUUID)) {
                            return;
                        }
                    }
                }
            }

            if (!player.isShiftKeyDown()) {
                event.setCanceled(true);
            }
        } else if (eagle != null) {
            if (!eagle.isTame()) {
                return;
            }

            LivingEntity owner = eagle.getOwner();
            if (owner == null) {
                return;
            }

            if (!owner.getUUID().equals(player.getUUID())) {
                return;
            }

            if (!player.isShiftKeyDown()) {
                event.setCanceled(true);
            }
        }
    }

}
