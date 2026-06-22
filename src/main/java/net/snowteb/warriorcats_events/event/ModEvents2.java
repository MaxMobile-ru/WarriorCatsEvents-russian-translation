package net.snowteb.warriorcats_events.event;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.minecraftforge.server.command.ConfigCommand;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.clan.ClanData;
import net.snowteb.warriorcats_events.clan.WCEPlayerData;
import net.snowteb.warriorcats_events.clan.WCEPlayerDataProvider;
import net.snowteb.warriorcats_events.commands.*;
import net.snowteb.warriorcats_events.diseases.kinds.BrokenPaw;
import net.snowteb.warriorcats_events.diseases.DiseaseTypes;
import net.snowteb.warriorcats_events.diseases.Diseaseable;
import net.snowteb.warriorcats_events.entity.custom.EagleEntity;
import net.snowteb.warriorcats_events.entity.custom.WCGenetics;
import net.snowteb.warriorcats_events.entity.custom.WCatEntity;
import net.snowteb.warriorcats_events.item.ModItems;
import net.snowteb.warriorcats_events.managers.*;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.s2c.clan.OpenClanSetupScreenPacket;
import net.snowteb.warriorcats_events.network.packet.s2c.clan.S2CSyncClanDataPacket;
import net.snowteb.warriorcats_events.network.packet.s2c.skilltree.SyncSkillDataPacket;
import net.snowteb.warriorcats_events.network.packet.s2c.others.ThirstDataSyncStCPacket;
import net.snowteb.warriorcats_events.particles.WCEParticles;
import net.snowteb.warriorcats_events.skills.PlayerSkill;
import net.snowteb.warriorcats_events.skills.PlayerSkillProvider;
import net.snowteb.warriorcats_events.stealth.PlayerStealth;
import net.snowteb.warriorcats_events.stealth.PlayerStealthProvider;
import net.snowteb.warriorcats_events.thirst.PlayerThirst;
import net.snowteb.warriorcats_events.thirst.PlayerThirstProvider;
import net.snowteb.warriorcats_events.util.*;
import net.snowteb.warriorcats_events.zconfig.WCEServerConfig;
import tocraft.walkers.api.PlayerShape;

import java.util.*;

@Mod.EventBusSubscriber(modid = WarriorCatsEvents.MODID)
public class ModEvents2 {

    private static final List<Task> tasks = new ArrayList<>();

    public static void schedule(int ticksDelay, Runnable action) {
        tasks.add(new Task(ticksDelay, action));
    }

    public static int sleepTick = 0;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        if (!ClanData.CHUNK_TASKS.isEmpty()) {
            for (int i = 0; i < 5; i++) {
                Runnable task = ClanData.CHUNK_TASKS.poll();
                if (task == null) {
                    break;
                }
                task.run();
            }
        }


        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                ClanInviteManager.tick(player);
                CarryPlayerRequestManager.tick(player);
                PlayerMateRequestManager.tick(player);
                PlayerKittingRequestManager.tick(player);
            }
        }

        Iterator<Task> it = tasks.iterator();
        while (it.hasNext()) {
            Task task = it.next();
            task.ticks--;

            if (task.ticks <= 0) {
                task.action.run();
                it.remove();
            }
        }

        ClanData data = ClanData.get(event.getServer().overworld());
        boolean shouldSync = data.territoryTick(event.getServer().overworld());
        if (shouldSync) {
            data.syncTerritoriesToClients(event.getServer().overworld());
            data.setDirty();
        }

        if (event.getServer().overworld().getGameTime() % 600 == 0) {
            data.setDirty();
        }

        ServerPlayerMorphsCache.tick();
    }

    private static boolean isDiamond(ItemStack stack) {
        return stack.getItem() == Items.DIAMOND;
    }
    private static boolean isEmerald(ItemStack stack) {
        return stack.getItem() == Items.EMERALD;
    }


    @SubscribeEvent
    public static void worldTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.level.isClientSide) return;

        if (event.level instanceof ServerLevel serverLevel) {
            SequenceManager.tick(serverLevel);
        }

        boolean isNightTime = event.level.isNight();

        if (isNightTime) {

            if (event.level.getGameTime() % 200 == 0) {
                List<ItemEntity> items = event.level.getEntitiesOfClass(
                        ItemEntity.class,
                        new AABB(
                                -30000000, event.level.getMinBuildHeight(), -30000000,
                                30000000, event.level.getMaxBuildHeight(), 30000000
                        ),
                        e -> !e.isRemoved() && !e.getItem().isEmpty()
                );


                for (ItemEntity item1 : items) {
                    ItemStack potentialDiamond = item1.getItem();

                    if (!isDiamond(potentialDiamond)) continue;

                    for (ItemEntity item2 : items) {
                        if (item1 == item2) continue;

                        ItemStack potentialEmerald = item2.getItem();

                        if (!isEmerald(potentialEmerald)) continue;

                        double dist = item1.distanceTo(item2);

                        if (dist >= 1.0) continue;

                        if (potentialDiamond.getCount() <= 0 || potentialEmerald.getCount() <= 0) continue;

                        potentialDiamond.shrink(1);
                        potentialEmerald.shrink(1);

                        ItemStack ritualStack = new ItemStack(ModItems.STRANGE_SHINY_STONE.get());
                        ItemEntity ritualItem = new ItemEntity(event.level, item2.getX(), item2.getY(), item2.getZ(), ritualStack);

                        event.level.addFreshEntity(ritualItem);

                        ((ServerLevel) event.level).sendParticles(ParticleTypes.GLOW_SQUID_INK,
                                ritualItem.getX(), ritualItem.getY(), ritualItem.getZ(),
                                15, 0.2f, 0.2f, 0.2f, 0.2f);

                        event.level.playSound(null, ritualItem.blockPosition(),
                                SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.AMBIENT,
                                1.0f, 1.3f);

                        break;
                    }
                }
            }
        }
    }

    private static class Task {
        int ticks;
        final Runnable action;

        Task(int ticks, Runnable action) {
            this.ticks = ticks;
            this.action = action;
        }
    }


    @SubscribeEvent
    public static void onCommandsRegister(RegisterCommandsEvent event) {
        InfoSetupCommand.register(event.getDispatcher());
        GetClanDataCommand.register(event.getDispatcher());
        ResetClanDataCommand.register(event.getDispatcher());
        ClanListCommand.register(event.getDispatcher());
        RegisterClanCommand.register(event.getDispatcher());
        ClanInviteAcceptCommand.register(event.getDispatcher());
        ClanInviteDenyCommand.register(event.getDispatcher());
        InviteToClanCommand.register(event.getDispatcher());
        DismantleClanCommand.register(event.getDispatcher());
        KickClanMemberCommand.register(event.getDispatcher());
        ChangeRankClanMemberCommand.register(event.getDispatcher());
        SetNewLeaderCommand.register(event.getDispatcher());
        LeaveClanCommand.register(event.getDispatcher());
        ManageClanCommand.register(event.getDispatcher());
        ChangeMemberPermissionCommand.register(event.getDispatcher());
        OpenMorphCreateCommand.register(event.getDispatcher());
        OpClanCommands.register(event.getDispatcher());
        CarryRequestAcceptCommand.register(event.getDispatcher());
        CarryRequestDenyCommand.register(event.getDispatcher());
        ToggleChatMorphName.register(event.getDispatcher());
        MateRequestCommands.register(event.getDispatcher());
        KitRequestCommands.register(event.getDispatcher());
        OpenSummonCatCommand.register(event.getDispatcher());
        SetPoseCommand.register(event.getDispatcher());
        InfoProfileCommand.register(event.getDispatcher());
        ResyncShapesCommand.register(event.getDispatcher());
        WCEGameruleCommand.register(event.getDispatcher());
        DiseaseCommands.register(event.getDispatcher());
        ChangelogCommand.register(event.getDispatcher());


        ConfigCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if(event.getObject() instanceof Player) {

            if(!event.getObject().getCapability(PlayerThirstProvider.PLAYER_THIRST).isPresent()) {
                PlayerThirstProvider provider = new PlayerThirstProvider();
                provider.getOrCreateThirst();
                event.addCapability(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "properties"), provider);

                event.addCapability(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "skill_data"),
                        new PlayerSkillProvider());

                event.addCapability(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "stealth_mode"),
                        new PlayerStealthProvider());

            }

            if (!event.getObject().getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA).isPresent()) {
                WCEPlayerDataProvider provider = new WCEPlayerDataProvider();
                provider.getOrCreateClanData();
                event.addCapability(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "clan_data"), provider);
            }

        }
    }

    @SubscribeEvent
    public static void onPlayerDead(PlayerEvent.Clone event) {

        /**
         * When the player dies, reset the thirst back to max and sync it.
         */
        event.getEntity().getCapability(PlayerThirstProvider.PLAYER_THIRST).ifPresent(newStore -> {
            if (event.isWasDeath()) {
                newStore.reset();
            } else {
                event.getOriginal().getCapability(PlayerThirstProvider.PLAYER_THIRST).ifPresent(oldStore -> {
                    newStore.copyFrom(oldStore);
                });
            }

            if (event.getEntity() instanceof ServerPlayer player) {
                ModPackets.sendToPlayer(new ThirstDataSyncStCPacket(newStore.getThirst()), player);
            }
        });


        {
            event.getOriginal().reviveCaps();

            event.getEntity().getCapability(PlayerStealthProvider.STEALTH_MODE).ifPresent(newStore -> {
                event.getOriginal().getCapability(PlayerStealthProvider.STEALTH_MODE).ifPresent(oldStore -> {
                    newStore.copyFrom(oldStore);

                    if (event.getEntity() instanceof ServerPlayer player) {
                        newStore.sync(player);
                    }
                });
            });

            event.getEntity().getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA).ifPresent(newStore -> {
                event.getOriginal().getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA).ifPresent(oldStore -> {
                    newStore.copyFrom(oldStore);

                    if (event.getEntity() instanceof ServerPlayer player) {
                        ModPackets.sendToPlayer(new S2CSyncClanDataPacket(newStore), player);
                    }
                });
            });

            event.getEntity().getCapability(PlayerSkillProvider.SKILL_DATA).ifPresent(newStore -> {
                event.getOriginal().getCapability(PlayerSkillProvider.SKILL_DATA).ifPresent(oldStore -> {
                    newStore.copyFrom(oldStore);

                    if (event.getEntity() instanceof ServerPlayer player) {

                        PlayerSkill.reviveAttributes(player, newStore);

                        ModPackets.sendToPlayer(
                                new SyncSkillDataPacket(newStore.getSpeedLevel(), newStore.getHPLevel(),
                                        newStore.getDMGLevel(), newStore.getJumpLevel(), newStore.getArmorLevel()
                                , newStore.isClimbUnlocked()),
                                player
                        );
                    }


                });


            });

            event.getOriginal().invalidateCaps();
        }


    }


    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(PlayerThirst.class);
        event.register(PlayerSkill.class);
        event.register(PlayerStealth.class);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.END) {

            ServerPlayer player = (ServerPlayer) event.player;

            if (player.getFirstPassenger() != null) {
                if (player.getFirstPassenger() instanceof Player rider) {
                    if (player.isShiftKeyDown()) rider.stopRiding();
                }
            }

            if (player.isSleeping()) {
                if (PlayerShape.getCurrentShape(player) instanceof WCatEntity) {
                    if (player.level() instanceof ServerLevel sLevel && player.tickCount % 10 == 0) {

                        Vec3 position = player.blockPosition().getCenter();

                        sLevel.sendParticles(
                                WCEParticles.SLEEP.get(),
                                position.x, position.y + 0.3, position.z,
                                1, 0, 0, 0,0.005);
                    }
                }
            }

            int endTick = event.player.getPersistentData().getInt("wcat_animation_playing");
            if (endTick != 0) {
                if (player.server.getTickCount() >= endTick) {

                    LivingEntity shape = PlayerShape.getCurrentShape(player);
                    if (shape instanceof WCatEntity cat) {
                        cat.setAnimIndex(-1);
                        PlayerShape.updateShapes(player, cat);
                    }

                    player.getPersistentData().remove("wcat_animation_playing");
                }
            }

            int jumpEndTick = event.player.getPersistentData().getInt("wcat_jump");
            if (jumpEndTick != 0) {
                if (player.server.getTickCount() >= jumpEndTick) {

                    LivingEntity shape = PlayerShape.getCurrentShape(player);
                    if (shape instanceof WCatEntity && player.onGround()) {

                        Vec3 currentDeltaMov = player.getDeltaMovement();
                        player.setDeltaMovement(currentDeltaMov.x, currentDeltaMov.y + 0.7f, currentDeltaMov.z);
                        player.hurtMarked = true;

                        player.serverLevel().sendParticles(ParticleTypes.GLOW_SQUID_INK,
                                player.getX(), player.getY(),  player.getZ(),
                                3, 0.3f, 0.3f, 0.3f,0.2f);
                    }

                    player.getPersistentData().remove("wcat_jump");
                }
            }



            event.player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA).ifPresent(WCEPlayerData::tick);

            event.player.getCapability(PlayerThirstProvider.PLAYER_THIRST).ifPresent(thirst -> {
                if (WCEServerConfig.SERVER.THIRST.get() && PlayerShape.getCurrentShape(player) instanceof Animal) {
                    /**
                    * Every tick, a 0.000357 chance of decreasing thirst.
                    * Which means, on average every 2-3 minutes
                    * Then sync it.
                    */
                    if (thirst.getThirst() > 0 && event.player.getRandom().nextFloat() < 0.000359 && !(event.player.isCreative() || event.player.isSpectator())) {
                        int oldThirst = thirst.getThirst();
                        thirst.subThirst(1);
                        if (oldThirst != thirst.getThirst()) {
                            ModPackets.sendToPlayer(new ThirstDataSyncStCPacket(thirst.getThirst()), ((ServerPlayer) event.player));
                        }
                    }

                    /**
                     * If the thirst reaches zero, then hurt the player every 80 ticks, and set saturation to zero.
                     */
                    if (thirst.getThirst() <= 0) {
                        thirst.tick();

                        if (thirst.getThirstDamageTimer() >= 60) {
                            event.player.getFoodData().setSaturation(0);
                            event.player.hurt(event.player.damageSources().starve(), 3.0f);
                            event.player.displayClientMessage(Component.literal("You are dehydrated!").withStyle(ChatFormatting.RED), true);

                            thirst.resetThirstDamageTimer();
                        }

                    } else {
                        thirst.resetThirstDamageTimer();
                    }
                }
            });



            event.player.getCapability(PlayerStealthProvider.STEALTH_MODE).ifPresent(cap -> {

                if (cap.isStealthOn() && event.player.tickCount % 2 == 0) {
                    event.player.setInvisible(true);
                }

                /**
                 * If Stealth is active, and its unlocked, and its on:
                 * Then send a particle every 3 ticks.
                 */
                    if (cap.isStealthOn() && cap.isUnlocked() && cap.isOn()) {
                        ServerLevel level = (ServerLevel) event.player.level();

                        if (event.player.tickCount % 3 == 0) {
                            if (event.player.onGround()) {
                                level.sendParticles(
                                        ParticleTypes.CAMPFIRE_COSY_SMOKE,
                                        event.player.getX(),
                                        event.player.getY() + 0.1,
                                        event.player.getZ(),
                                        2, 0.1, 0, 0.1, 0.002
                                );
                            }

                        }

                    }


            });

            event.player.getCapability(PlayerSkillProvider.SKILL_DATA).ifPresent(cap -> {
                if (!cap.isLeaping()) return;
                if (player instanceof ClimbDataAccessor data && data.wce$isClimbing()) cap.setLeaping(false);

                if (event.player.tickCount % 2 == 0) {
                    ((ServerLevel) event.player.level()).sendParticles(ParticleTypes.INSTANT_EFFECT,
                            event.player.getX(), event.player.getY(), event.player.getZ(),
                            1,
                            0,0,0, 0);
                }

                int damagePowerMultiplier = cap.getLeapPower()/100;

                AABB hitbox = event.player.getBoundingBox().inflate(0.8D);

                List<LivingEntity> targets = event.player.level().getEntitiesOfClass(
                        LivingEntity.class,
                        hitbox,
                        e -> e != event.player && e.isAlive()
                );

                if (!targets.isEmpty()) {
                    LivingEntity target = targets.stream()
                            .min(Comparator.comparingDouble(e ->
                                    e.distanceToSqr(event.player)))
                            .orElse(null);

                    WCEPlayerData.Age morphAge = event.player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                            .map(WCEPlayerData::getMorphAge).orElse(WCEPlayerData.Age.ADULT);

                    float finalMultiplier = switch (morphAge) {
                        case KIT -> 0.33f;
                        case APPRENTICE -> 0.67f;
                        case ADULT -> 1.0f;
                    };

                    float damage = ((float) (5.0F + ((float) cap.getDMGLevel() /1.25)*damagePowerMultiplier))*finalMultiplier;
                    if (event.player.hasLineOfSight(target)) {
                        boolean isAClanmate = false;

                        if (target instanceof Player playerTarget) {
                            UUID targetClan = playerTarget.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                                    .map(WCEPlayerData::getCurrentClanUUID).orElse(ClanData.EMPTY_UUID);
                            UUID thisClan = playerTarget.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                                    .map(WCEPlayerData::getCurrentClanUUID).orElse(ClanData.EMPTY_UUID);

                            if (targetClan.equals(thisClan)) {
                                isAClanmate = true;
                            }
                        }

                        if (!((target instanceof TamableAnimal cat && cat.isTame() && cat.getOwner() == event.player) || (target instanceof EagleEntity eagle && eagle.getOwner() == event.player) || isAClanmate)) {
                            BlockParticleOption particle = new BlockParticleOption(ParticleTypes.BLOCK,
                                    Blocks.REDSTONE_BLOCK.defaultBlockState());

                            if (event.player.getRandom().nextFloat() < 0.2 && damagePowerMultiplier > 0.7) {
                                target.hurt(event.player.damageSources().playerAttack(event.player), damage * 1.5f);
                                ((ServerLevel) event.player.level()).sendParticles(ParticleTypes.CRIT,
                                        target.getX(), target.getY(), target.getZ(),
                                        30,
                                        0.2f,0.2f,0.2f, 0.2f);
                                ((ServerLevel) event.player.level()).sendParticles(particle,
                                        target.getX(), target.getY(), target.getZ(),
                                        30,
                                        0.1,0.2,0.1, 0.1);
                                event.player.level().playSound(null, event.player.blockPosition(), SoundEvents.CAT_HISS, SoundSource.PLAYERS, 0.7f, 1f);
                                event.player.displayClientMessage(Component.literal("Critical hit!").withStyle(ChatFormatting.GRAY), true);
                            } else {
                                target.hurt(event.player.damageSources().playerAttack(event.player), damage);
                            }

                            if (target.isAlive()) event.player.setLastHurtMob(target);

                            event.player.level().playSound(null, event.player.blockPosition(),SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS);

                            ((ServerLevel) event.player.level()).sendParticles(particle,
                                    target.getX(), target.getY(), target.getZ(),
                                    10,
                                    0.1,0.2,0.1, 0.1);
                            ((ServerLevel) event.player.level()).sendParticles(ParticleTypes.CRIT,
                                    target.getX(), target.getY(), target.getZ(),
                                    10,
                                    0.2f,0.2f,0.2f, 0.2f);
                        }
                    }


                    cap.setLeaping(false);
                    cap.setLeapPower(0);
                    event.player.setDeltaMovement(Vec3.ZERO);
                }

                if (event.player.onGround()) {
                    cap.setLeaping(false);
                    cap.setLeapPower(0);
                }
            });


        }

/*
        if (event.player.tickCount % 20 == 0) {
            if (event.player.level().isClientSide()) {
                double speed = event.player.getDeltaMovement().length();
                event.player.sendSystemMessage(Component.literal("Speed Client: " + speed));
            }
        */


    }


    /**
     * When the player joins, sync its thirst data and its stealth data.
     */
    @SubscribeEvent
    public static void onPlayerJoinWorld(EntityJoinLevelEvent event) {
        if(!event.getLevel().isClientSide()) {
            if(event.getEntity() instanceof ServerPlayer player) {

                {
                    boolean skillTree = WCEServerConfig.SERVER.SKILL_TREE_SERVER.get();
                    player.getCapability(PlayerSkillProvider.SKILL_DATA).ifPresent(skillData -> {
                        if (!skillTree) PlayerSkill.removeAttributes(player);
                        else PlayerSkill.reviveAttributes(player, skillData);
                    });
                }

                if (player.level() instanceof ServerLevel serverLevel) {
                    ClanData data = ClanData.get(serverLevel.getServer().overworld());
                    data.syncTerritoriesToAClient(player);
                }

                player.getCapability(PlayerSkillProvider.SKILL_DATA).ifPresent(cap -> {
                   ModPackets.sendToPlayer(new SyncSkillDataPacket(cap.getSpeedLevel(), cap.getHPLevel(),
                           cap.getDMGLevel(), cap.getJumpLevel(), cap.getArmorLevel(), cap.isClimbUnlocked()), player);
                });

                player.getCapability(PlayerThirstProvider.PLAYER_THIRST).ifPresent(thirst -> {
                    ModPackets.sendToPlayer(new ThirstDataSyncStCPacket(thirst.getThirst()), player);
                });

                player.getCapability(PlayerStealthProvider.STEALTH_MODE).ifPresent(cap -> {
                    cap.sync(player);
                });

                player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA).ifPresent(cap -> {

                    UUID clanUUID = cap.getCurrentClanUUID();


                    if (clanUUID == null || clanUUID.equals(ClanData.EMPTY_UUID)) {
                        cap.setCurrentClanUUID(ClanData.EMPTY_UUID);
                        return;
                    }

                    ClanData data;
                    try {
                        data = ClanData.get(player.serverLevel().getServer().overworld());
                    } catch (Exception e) {
                        cap.setCurrentClanUUID(ClanData.EMPTY_UUID);
                        WarriorCatsEvents.LOGGER.debug("Could not get ClanData from overworld, resetting clan for: {}", player.getName().getString());
                        return;
                    }

                    if (data.clans == null || !data.clans.containsKey(clanUUID)) {
                        cap.setCurrentClanUUID(ClanData.EMPTY_UUID);
                        player.sendSystemMessage(Component.literal("You have been removed from your clan.").withStyle(ChatFormatting.YELLOW));
                        WarriorCatsEvents.LOGGER.debug("Could not find a clan with UUID \"{}\", resetting clan for: {}", clanUUID, player.getName().getString());
                    }

                    ModPackets.sendToPlayer(new S2CSyncClanDataPacket(cap), player);
                });


            }
        }
    }

    /**
     * Every time and entity jumps, if it is a player, then modify its jump depending on the custom Attribute.
     */
    @SubscribeEvent
    public static void onJump(LivingEvent.LivingJumpEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        boolean canJumpBoost = true;

        if (player instanceof Diseaseable<?> diseaseable) {
            if (diseaseable.hasDisease(DiseaseTypes.BROKEN_PAW)) {
                if (diseaseable.getDisease(DiseaseTypes.BROKEN_PAW) instanceof BrokenPaw brokenPaw) {
                    brokenPaw.hurt(player);
                    canJumpBoost = false;
                    player.setDeltaMovement(player.getDeltaMovement().add(0, -0.04, 0));
                }
            }
        }


        if (player.isSprinting() && canJumpBoost) {
            AttributeInstance att = player.getAttribute(ModAttributes.PLAYER_JUMP.get());
            if (att == null) return;
            double extra = att.getValue();
            if (extra > 0) {
                player.setDeltaMovement(
                        player.getDeltaMovement().x,
                        player.getDeltaMovement().y + extra,
                        player.getDeltaMovement().z
                );
            }
        }

    }


    /**
     * When the player joins for the first time. Give it items and send a message.
     */
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();

        CompoundTag data = player.getPersistentData();
        CompoundTag persistent;



        if (player instanceof ServerPlayer sPlayer) {
            ClanData clanData = ClanData.get(sPlayer.serverLevel().getServer().overworld());

            UUID clanUUID = sPlayer.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                    .map(WCEPlayerData::getCurrentClanUUID).orElse(ClanData.EMPTY_UUID);
            ClanData.Clan clan =   clanData.getClan(clanUUID);

            if (clan != null) {
                if (!clan.members.containsKey(sPlayer.getUUID())) {
                    sPlayer.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA).ifPresent(cap -> {
                        cap.setCurrentClanUUID(ClanData.EMPTY_UUID);
                        cap.setClanName("None");
                        sPlayer.sendSystemMessage(Component.literal("You have been removed from your clan.").withStyle(ChatFormatting.YELLOW));
                    });
                }

            } else {
                sPlayer.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA).ifPresent(cap -> {
                    cap.setCurrentClanUUID(ClanData.EMPTY_UUID);
                    cap.setClanName("None");
                });
            }

            player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA).ifPresent(cap -> {
                ModPackets.sendToPlayer(new S2CSyncClanDataPacket(cap), sPlayer);

                clanData.playerMorphNames.put(player.getUUID(), cap.getMorphName());
                WCGenetics.PackedGeneticData morphData =
                        new WCGenetics.PackedGeneticData(cap.getPlayerGenetics(),
                                cap.getPlayerGeneticalVariants(),
                                cap.getPlayerChimeraGenetics(),
                                cap.getPlayerChimeraVariants(),
                                cap.isOnGeneticalSkin(), cap.getVariantData());

                clanData.playerMorphData.put(player.getUUID(), morphData);

                clanData.setDirty();
            });

        }

        if (data.contains(Player.PERSISTED_NBT_TAG)) {
            persistent = data.getCompound(Player.PERSISTED_NBT_TAG);
        } else {
            persistent = new CompoundTag();
            data.put(Player.PERSISTED_NBT_TAG, persistent);
        }

        if (persistent.getBoolean("warriorcats_events.starting_items")) {
            if (player.isInvulnerable()) player.setInvulnerable(false);
            return;
        }

        if (player instanceof ServerPlayer sPlayer) {
            sPlayer.setInvulnerable(true);

            player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA).ifPresent(cap -> {
                    ModPackets.sendToPlayer(new S2CSyncClanDataPacket(cap), sPlayer);
                    ModPackets.sendToPlayer(new OpenClanSetupScreenPacket(), sPlayer);
            });
        }

    }

}
