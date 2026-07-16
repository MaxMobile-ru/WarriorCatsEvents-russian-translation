package net.snowteb.warriorcats_events.entity.custom.wcat;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.network.NetworkHooks;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.block.ModBlocks;
import net.snowteb.warriorcats_events.clan.ClanData;
import net.snowteb.warriorcats_events.clan.WCEPlayerData;
import net.snowteb.warriorcats_events.clan.WCEPlayerDataProvider;
import net.snowteb.warriorcats_events.client.LeapClientState;
import net.snowteb.warriorcats_events.diseases.DiseaseManager;
import net.snowteb.warriorcats_events.effect.ModEffects;
import net.snowteb.warriorcats_events.event.ModEventsForge1;
import net.snowteb.warriorcats_events.item.ModItems;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.s2c.cats.OpenCatDataScreenPacket;
import net.snowteb.warriorcats_events.network.packet.s2c.clan.S2CSyncClanDataPacket;
import net.snowteb.warriorcats_events.network.packet.s2c.others.SyncDiseasesPacket;
import net.snowteb.warriorcats_events.screen.menus.WCatMenu;
import net.snowteb.warriorcats_events.util.GeneticsForVariant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tocraft.walkers.api.PlayerShape;

import java.util.Objects;
import java.util.UUID;

import static net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity.AGE_SYNC;
import static net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity.Rank.*;

public class MobInteractModule {
    private final WCatEntity cat;
    
    public MobInteractModule(WCatEntity cat) {
        this.cat = cat;
    }

    @Nullable
    public InteractionResult interact(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);

        if (pPlayer.level().isClientSide) {
            LeapClientState.setCanceled();
        }

        boolean clanMemberManaging = false;
        if (pPlayer.level() instanceof ServerLevel serverLevel){
            ClanData data = ClanData.get(serverLevel.getServer().overworld());
            ClanData.Clan clan = data.getClan(cat.getClanUUID());
            if (clan != null) {
                if (data.canCommandWarriors(clan, pPlayer.getUUID()) || pPlayer.hasPermissions(3))
                    clanMemberManaging = true;
            }
        }

        syncClanData(pPlayer);

        if (DiseaseManager.healDiseaseMobInteract(itemstack, cat)) {
            return InteractionResult.SUCCESS;
        }

        if (cat.getRank() == MEDICINE && cat.isTame() && cat.getOwner() == pPlayer) {
            InteractionResult result = medicineCatScentHerbs(pPlayer, pHand);
            if (result != null) return result;
        }

        if ((itemstack.is(ModItems.CLAWS.get()))) {
            return clawsInteraction(pPlayer);
        }

        if (!cat.isTame() && itemstack.is(ModItems.FRESHKILL_AND_HERBS_BUNDLE.get())) {
            return wildCatTaming(pPlayer, itemstack);
        }

        if (cat.isTame() && pPlayer.isShiftKeyDown() && pPlayer.getMainHandItem().isEmpty()) {
            InteractionResult result = catSetMode(pPlayer);
            if (result != null) return result;
        }

        if (cat.isTame() && pPlayer.isShiftKeyDown() && pPlayer.getUUID().equals(cat.getOwnerUUID())
                && itemstack.is(ModItems.WHISKERS.get()) && (cat.getRank() != KIT)) {

            return changeRank(pPlayer);
        }


        if ((cat.isOwnedBy(pPlayer) || clanMemberManaging) && itemstack.is(ModItems.CATMINT.get()) &&
                cat.getRank() != MEDICINE && !cat.isExpectingKits() && !cat.isBaby()) {

            return breeding(pPlayer, itemstack);
        }

        if ((cat.isOwnedBy(pPlayer) || clanMemberManaging) && itemstack.is(ModItems.WARRIORNAMERANDOMIZER.get())) {
            return warriorNameRandomize(pPlayer, pHand, itemstack);
        }

        if (cat.isTame() && itemstack.is(ModItems.MYSTIC_FLOWERS_BOUQUET.get())) {
            InteractionResult result = marryingCats(pPlayer);
            if (result != null) return result;
        }

        if (itemstack.getItem() == ModItems.WARRIOR_NAMETAG.get() && (cat.isOwnedBy(pPlayer) || clanMemberManaging)) {
            return useWarriorNametag(pPlayer, itemstack);
        }

        if (itemstack.is(ModItems.WHISKERS.get())) {
            return openWCatMenu(pPlayer, pHand, itemstack);
        }

        if (cat.isBaby() && cat.getRank() == KIT) {
            InteractionResult result = giveDeathberriesOrYarrow(pPlayer, itemstack);
            if (result != null) return result;
        }

        return null;
    }

    private @Nullable InteractionResult giveDeathberriesOrYarrow(Player pPlayer, ItemStack itemstack) {
        if (itemstack.is(ModItems.DEATHBERRIES.get())) {
            if (!cat.level().isClientSide()) {
                ServerLevel level = ((ServerLevel) cat.level());
                if (pPlayer instanceof ServerPlayer serverPlayer) {

                    MinecraftServer server = serverPlayer.getServer();
                    if (server != null) {

                        Advancement adv = server.getAdvancements()
                                .getAdvancement(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID,"fed_kit_deathberries"));

                        if (adv != null) {
                            serverPlayer.getAdvancements().award(adv, "fed_kit_deathberries");
                        }
                    }

                    String morphName = pPlayer.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                            .map(WCEPlayerData::getMorphName).orElse(pPlayer.getName().getString());


                    Component message = Component.empty()
                            .append(Component.literal(morphName).withStyle(ChatFormatting.AQUA))
                            .append(Component.literal("(").withStyle(ChatFormatting.GRAY))
                            .append(Component.literal(pPlayer.getName().getString()).withStyle(ChatFormatting.GRAY))
                            .append(Component.literal(")").withStyle(ChatFormatting.GRAY))
                            .append(" has given Deathberries to ")
                            .append(cat.hasCustomName() ? cat.getCustomName().copy() : Component.literal("a kit"))
                            .append("!");
                    cat.registerClanLog(message);
                }

                cat.addEffect(new MobEffectInstance(ModEffects.DEATHBERRIES.get(), 3600, 0));
                cat.level().playSound(null, cat.blockPosition(), SoundEvents.CAT_EAT, SoundSource.AMBIENT, 0.8f, 1f);
                BlockParticleOption particle = new BlockParticleOption(
                        ParticleTypes.BLOCK,
                        Blocks.REDSTONE_BLOCK.defaultBlockState()
                );
                level.sendParticles(
                        particle,
                        cat.getX(),
                        cat.getY(),
                        cat.getZ(),
                        10,
                        0.2f, 0.2f, 0.2f, 0.1
                );
            }

            cat.gameEvent(GameEvent.EAT);
            return InteractionResult.sidedSuccess(cat.level().isClientSide());

        }
        if (itemstack.is(ModItems.YARROW.get()) && (cat.hasEffect(ModEffects.DEATHBERRIES.get()) || cat.hasEffect(MobEffects.POISON))) {
            if (!cat.level().isClientSide()) {
                ServerLevel level = ((ServerLevel) cat.level());

                if (cat.hasEffect(ModEffects.DEATHBERRIES.get())) {
                    cat.removeEffect(ModEffects.DEATHBERRIES.get());
                }
                if (cat.hasEffect(MobEffects.POISON)) {
                    cat.removeEffect(MobEffects.POISON);
                }

                cat.level().playSound(null, cat.blockPosition(), SoundEvents.CAT_EAT, SoundSource.AMBIENT, 0.8f, 1f);
                level.sendParticles(
                        ParticleTypes.HAPPY_VILLAGER,
                        cat.getX(),
                        cat.getY(),
                        cat.getZ(),
                        10,
                        0.2, 0.2, 0.2, 0.1
                );
                cat.gameEvent(GameEvent.EAT);
                return InteractionResult.sidedSuccess(cat.level().isClientSide());
            }
        }
        return null;
    }

    private @NotNull InteractionResult openWCatMenu(Player pPlayer, InteractionHand pHand, ItemStack itemstack) {
        if (!(PlayerShape.getCurrentShape(pPlayer) instanceof Animal)) return InteractionResult.PASS;

        if (!cat.level().isClientSide()) {
            itemstack.hurtAndBreak(1, pPlayer, (p) -> p.broadcastBreakEvent(pHand));

            ModPackets.sendToPlayer(new SyncDiseasesPacket(cat.getId(), cat.diseaseData()), (ServerPlayer) pPlayer);

            cat.lastMode = cat.mode;

            boolean isValidDeputy;
            if (cat.level() instanceof  ServerLevel sLevel) {
                ClanData data = ClanData.get(sLevel.getServer().overworld());

                UUID clanUUID = pPlayer.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                        .map(WCEPlayerData::getCurrentClanUUID).orElse(ClanData.EMPTY_UUID);

                ClanData.Clan clan = data.getClan(clanUUID);
                if (clan != null) {
                    isValidDeputy = clan.members.get(pPlayer.getUUID()) == ClanData.ClanPlayerRank.DEPUTY
                            && data.canCommandWarriors(clan, pPlayer.getUUID())
                            && (cat.getHealth() > cat.getMaxHealth() / 2)
                            && !(cat.onBorderPatrolFlag || cat.onHuntingPatrolFlag || cat.returnHomeFlag || cat.tellingCatsToPatrol)
                            && cat.getClanUUID().equals(clanUUID)
                            && !cat.getClanUUID().equals(ClanData.EMPTY_UUID)
                            && (cat.getRank() == WARRIOR || cat.getRank() == APPRENTICE)
                            && cat.getOwnerUUID() != null
                            && cat.hasHomePosition();
                } else {
                    isValidDeputy = false;
                }
            } else {
                isValidDeputy = false;
            }

            if (cat.isTame() && cat.getOwner() == pPlayer) {
                if (cat.getPersonality() == WCatEntity.Personality.NONE || cat.getPersonality() == null) {
                    cat.assignRandomPersonality(cat.getRandom());
                }
                pPlayer.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA).ifPresent(cap -> {
                    String clanName = cap.getClanName(pPlayer.level());
                    if (cat.getClan().equals(Component.literal("None"))
                            || !cat.getClan().equals(Component.literal(cap.getClanName(pPlayer.level())))) {
                        if (cat.getRank() != NONE) cat.setClan(Component.literal(clanName));
                    }

                });

                if (cat.level() instanceof  ServerLevel sLevel) {
                    Entity ent = sLevel.getEntity(cat.getMateUUID());
                    if (ent != null) {
                        if (ent instanceof Player playerMate) {
                            String morphName = playerMate.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                                    .map(WCEPlayerData::getMorphName).orElse("None");

                            if (!cat.getMate().equals(Component.literal(morphName))) {
                                cat.setMate(Component.literal(morphName));
                            }

                        } else {
                            if (!cat.getMate().equals(ent.getCustomName())) {
                                cat.setMate(ent.getCustomName());
                            }
                        }
                    }
                }

                if (cat.getMood() == null) {
                    cat.setRandomMood(cat.getRandom());
                }

                float moonsCalc = (float) ((cat.getAge() + (20 * 60 * cat.getKitGrowthTimeMinutes())) / (100.0 * cat.getKitGrowthTimeMinutes()));
                cat.getEntityData().set(AGE_SYNC, Mth.clamp(moonsCalc, 0, 12));


                cat.mode = WCatEntity.CatMode.SIT;
                cat.setInSittingPose(true);
                cat.lookAtLeaderFlag = true;

                if (!pPlayer.level().isClientSide && pPlayer instanceof ServerPlayer sPlayer) {
                    ModEventsForge1.schedule(1, () -> {
                        ModPackets.sendToPlayer(new OpenCatDataScreenPacket(cat.getId(), isValidDeputy), sPlayer);
                    });
                }

            } else {
                if (!pPlayer.level().isClientSide && pPlayer instanceof ServerPlayer sPlayer) {
                    ModPackets.sendToPlayer(new OpenCatDataScreenPacket(cat.getId(), isValidDeputy), sPlayer);
                }
            }
        }

        return InteractionResult.SUCCESS;
    }

    private @NotNull InteractionResult warriorNameRandomize(Player pPlayer, InteractionHand pHand, ItemStack itemstack) {
        if (!cat.level().isClientSide()) {
            itemstack.hurtAndBreak(1, pPlayer, (p) ->
                    p.broadcastBreakEvent(pHand));

            Component oldName = cat.hasCustomName() ? cat.getCustomName() : Component.literal("Unnamed cat");


            String[] prefixSet = cat.getPrefixForVariant();

            String genderV;
            if (cat.getGender() == 0) {
                genderV = " ♂";
            } else {
                genderV = " ♀";
            }

            int i = cat.getRandom().nextInt(prefixSet.length);
            int j = cat.getRandom().nextInt(WCatEntity.SUFIXES.length);


            String finalName;

            if (cat.isBaby()) {
                finalName = prefixSet[i] + "kit" + genderV;
            } else if (cat.getRank() == APPRENTICE) {
                finalName = prefixSet[i] + "paw" + genderV;
            } else {
                finalName = prefixSet[i] + WCatEntity.SUFIXES[j] + genderV;
            }

            cat.setPrefix(Component.literal(prefixSet[i]));


            cat.setCustomName(Component.literal(finalName));
            cat.setCustomNameVisible(true);

            cat.setNameColor(cat.getRank());

            String morphName = pPlayer.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                    .map(WCEPlayerData::getMorphName).orElse(pPlayer.getName().getString());

            Component message = Component.translatable("wcat.change_name_log",
                    ClanData.logFormattedPlayerName(pPlayer),
                    oldName,
                    cat.hasCustomName() ? cat.getCustomName().copy() : Component.literal("null"));

            cat.registerClanLog(message);
            cat.updateClanCatData();
            cat.updateMatesName();
            cat.updateNest();
            cat.rewardMoonmoon();

        }
        return InteractionResult.sidedSuccess(cat.level().isClientSide);
    }

    private @NotNull InteractionResult useWarriorNametag(Player pPlayer, ItemStack itemstack) {
        if (itemstack.hasCustomHoverName()) {

            Component oldName = cat.hasCustomName() ? cat.getCustomName().copy() : Component.literal("A cat");

            String fullName = itemstack.getHoverName().getString();

            String[] parts = fullName.split(" ");

            String genderV;
            if (cat.getGender() == 0) {
                genderV = " ♂";
            } else {
                genderV = " ♀";
            }

            if (parts.length >= 2) {
                String prefix = parts[0];
                String sufix = parts[1];

                cat.setPrefix(Component.literal(prefix));

                if (cat.isBaby()) {
                    if (cat.getRank() == APPRENTICE) {
                        cat.setCustomName(Component.literal(prefix + "paw" + genderV));
                    } else {
                        cat.setCustomName(Component.literal(prefix + "kit" + genderV));
                    }
                } else {
                    cat.setCustomName(Component.literal(prefix + sufix + genderV));
                }

            } else {
                String prefix = parts[0];

                cat.setPrefix(Component.literal(prefix));
                cat.setCustomName(Component.literal(prefix + genderV));

            }

            cat.setNameColor(cat.getRank());

            String morphName = pPlayer.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                    .map(WCEPlayerData::getMorphName).orElse(pPlayer.getName().getString());

            Component message = Component.translatable("wcat.change_name_log",
                    ClanData.logFormattedPlayerName(pPlayer),
                    oldName,
                    cat.hasCustomName() ? cat.getCustomName().copy() : Component.literal("null"));

            cat.registerClanLog(message);
            cat.updateClanCatData();
            cat.updateMatesName();
            cat.updateNest();

            itemstack.shrink(1);
        }

        return InteractionResult.sidedSuccess(cat.level().isClientSide);
    }

    private @Nullable InteractionResult marryingCats(Player pPlayer) {
        if (!cat.level().isClientSide) {
            if (pPlayer instanceof ServerPlayer sPlayer && PlayerShape.getCurrentShape(sPlayer) instanceof WCatEntity) {
                String sPlayerMorphName = sPlayer.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                        .map(WCEPlayerData::getMorphName).orElse(sPlayer.getName().toString());
                WCEPlayerData.Age playerMorphAge = sPlayer.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                        .map(WCEPlayerData::getMorphAge).orElse(WCEPlayerData.Age.ADULT);

                if (playerMorphAge != WCEPlayerData.Age.ADULT) {
                    pPlayer.sendSystemMessage(Component.translatable( "generic.cat_not_old_enough",sPlayerMorphName)
                            .withStyle(ChatFormatting.RED));
                    return InteractionResult.FAIL;
                }
                Component catNameArgument = (cat.hasCustomName() ? cat.getCustomName() : Component.translatable("generic.this_cat"));

                if (cat.isBaby()) {
                    pPlayer.sendSystemMessage(Component.translatable("wcat.is_not_adult",
                            catNameArgument).withStyle(ChatFormatting.RED));
                    pPlayer.hurt(cat.damageSources().magic(), 10f);
                    return InteractionResult.FAIL;
                }

                if (cat.isForbiddenFromMatingPlayer() && cat.getForbiddenPlayer().equals(pPlayer.getUUID())) {
                    pPlayer.sendSystemMessage(Component.translatable("wcat.is_descendant",
                            catNameArgument).withStyle(ChatFormatting.GRAY));
                    return InteractionResult.FAIL;
                }

                if (sPlayer.getAbilities().instabuild) {
                    cat.setFriendshipLevel(sPlayer.getUUID(), 100);
                }

                if (cat.getFriendshipLevel(sPlayer.getUUID()) < 98) {
                    pPlayer.sendSystemMessage(Component.translatable("wcat.doesnt_like_you",
                            catNameArgument).withStyle(ChatFormatting.GRAY));
                    return InteractionResult.FAIL;
                }

                Entity currentMate = cat.getMateEntity();

                if (currentMate instanceof LivingEntity) {
                    if (currentMate == pPlayer) {
                        pPlayer.sendSystemMessage(Component.translatable("wcat.is_already_mate",
                                        catNameArgument,
                                        sPlayerMorphName).withStyle(ChatFormatting.YELLOW));
                    } else {
                        pPlayer.sendSystemMessage(Component.translatable("wcat.already_has_mate", catNameArgument)
                                .withStyle(ChatFormatting.YELLOW));
                    }
                    return InteractionResult.FAIL;
                }

                cat.setMate(Component.literal(sPlayerMorphName));
                cat.setMateUUID(sPlayer.getUUID());

                Component message = Component.translatable("managers.new_couple",
                                sPlayerMorphName,
                                catNameArgument).withStyle(ChatFormatting.GREEN);

                Component messageLog = Component.translatable("managers.new_couple",
                                cat.hasCustomName() ? cat.getCustomName() : Component.translatable("generic.catname"),
                                ClanData.logFormattedPlayerName(pPlayer));

                cat.registerClanLog(messageLog);

                pPlayer.sendSystemMessage(message);

                WCGenetics mateGenetics = cat.getGeneticsModule().getGenetics();
                if (!cat.isOnGeneticalSkin()) mateGenetics = GeneticsForVariant.get(cat.getVariant());

                pPlayer.getItemInHand(InteractionHand.MAIN_HAND).shrink(1);
                WCGenetics finalMateGenetics = mateGenetics;
                sPlayer.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA).ifPresent(cap -> {
                    cap.setMateUUID(cat.getUUID());
                    cap.setMateName(cat.hasCustomName() ? cat.getCustomName() : Component.literal("Undefined"));
                    cap.setMateGenetics(finalMateGenetics);
                    ModPackets.sendToPlayer(new S2CSyncClanDataPacket(cap), sPlayer);
                });
                cat.level().playSound(null, cat.blockPosition(), SoundEvents.CAT_PURREOW, SoundSource.NEUTRAL, 0.6F, 1.0F);
                cat.lovingParticlesTicks = 600;

                cat.setForbiddingFutureGensFromMatingPlayer(true);
                cat.setForbiddenPlayer(pPlayer.getUUID());
                cat.setSpecificMood(WCatEntity.Mood.HAPPY);

                return InteractionResult.SUCCESS;

            }
        }
        return null;
    }

    private @NotNull InteractionResult breeding(Player pPlayer, ItemStack itemstack) {
        if (!cat.level().isClientSide()) {
            if (((ServerLevel) cat.level()).getEntity(cat.getMateUUID()) instanceof Player) {
                return InteractionResult.PASS;
            }
        }

        if (!cat.getList().isEmpty()) {
            Component msg = Component.translatable("wcat.cant_bring_kits_sick",
                            cat.hasCustomName() ? cat.getCustomName() : Component.translatable("generic.this_cat"));

            pPlayer.displayClientMessage(msg, true);
            return InteractionResult.FAIL;
        }

        if (!pPlayer.getAbilities().instabuild) itemstack.shrink(1);

        cat.setInLove(pPlayer);

        return InteractionResult.SUCCESS;
    }

    private @NotNull InteractionResult changeRank(Player pPlayer) {
        WCatEntity.Rank current = cat.getRank();

        if (cat.getAgeInMoons() < 6) {
            cat.setRank(KIT);
        } else {
            switch (current) {
                case NONE:
                    cat.setRank(APPRENTICE);
                    break;
                case APPRENTICE:
                    cat.setRank(WARRIOR);
                    break;
                case WARRIOR:
                    cat.setRank(MEDICINE);
                    break;
                case MEDICINE:
                    cat.setRank(DEPUTY);
                    break;
                case DEPUTY:
                    cat.setRank(NONE);
                    break;
            }
        }

        cat.setNameColor(cat.getRank());


        Component message = Component.translatable("wcat.change_rank_log",
                ClanData.logFormattedPlayerName(pPlayer),
                cat.hasCustomName() ? cat.getCustomName() : Component.translatable("generic.catname"),
                Component.literal(cat.getRank().name()).withStyle(ChatFormatting.YELLOW));

        cat.registerClanLog(message);
        cat.updateClanCatData();

        cat.sendRankMessage(pPlayer);
        return InteractionResult.SUCCESS;
    }

    private @Nullable InteractionResult catSetMode(Player pPlayer) {
        if (!cat.level().isClientSide() && pPlayer instanceof ServerPlayer sPlayer) {
            ClanData data = ClanData.get(sPlayer.serverLevel().getServer().overworld());
            UUID clanUUID = sPlayer.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                    .map(WCEPlayerData::getCurrentClanUUID).orElse(ClanData.EMPTY_UUID);
            ClanData.Clan clan = data.getClan(clanUUID);
            boolean clanExistsAndIsValid = (!clanUUID.equals(ClanData.EMPTY_UUID) && clan != null);
            boolean canAlsoCommand = false;

            if (clanExistsAndIsValid) canAlsoCommand
                    = data.canCommandWarriors(clan, sPlayer.getUUID()) && cat.getClanUUID().equals(clanUUID) && !Objects.equals(cat.getOwnerUUID(), pPlayer.getUUID());

            if (pPlayer.getUUID().equals(cat.getOwnerUUID()) || canAlsoCommand) {

                if (canAlsoCommand) {
                    cat.setOwnerUUID(pPlayer.getUUID());


                    Component message = Component.translatable("wcat.cat_taken_log",
                                    ClanData.logFormattedPlayerName(pPlayer),
                                    cat.hasCustomName() ? cat.getCustomName().copy() : Component.translatable("generic.catname"));

                    cat.registerClanLog(message);
                }

                switch (cat.mode) {
                    case SIT:
                        cat.mode = WCatEntity.CatMode.FOLLOW;
                        cat.setInSittingPose(false);
                        break;
                    case FOLLOW:
                        cat.mode = WCatEntity.CatMode.WANDER;
                        cat.wanderCenter = cat.blockPosition();
                        break;
                    case WANDER:
                        cat.mode = WCatEntity.CatMode.SIT;
                        cat.setInSittingPose(true);
                        break;
                }
                cat.sendModeMessage(pPlayer);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
    }

    private @NotNull InteractionResult wildCatTaming(Player pPlayer, ItemStack itemstack) {
        if (!cat.level().isClientSide()) {

            if (!pPlayer.getAbilities().instabuild) {
                itemstack.shrink(1);
            }

            int tameRoll;

            if (cat.getPersonality() == WCatEntity.Personality.CAUTIOUS) {
                tameRoll = cat.getRandom().nextInt(6);
            } else if (cat.getPersonality() == WCatEntity.Personality.SHY) {
                tameRoll = cat.getRandom().nextInt(4);
            } else if (cat.getPersonality() == WCatEntity.Personality.FRIENDLY) {
                tameRoll = 0;
            } else {
                tameRoll = cat.getRandom().nextInt(2);
            }

            if (tameRoll == 0) {
                cat.tame(pPlayer);
                cat.level().broadcastEntityEvent(cat, (byte) 7);

                if (!cat.hasCustomName()) {
                    int variant = cat.getVariant();
                    String[] prefixSet = cat.getPrefixForVariant();

                    String genderS;
                    if (cat.getGender() == 0) {
                        genderS = " ♂";
                    } else {
                        genderS = " ♀";
                    }

                    int i = cat.getRandom().nextInt(prefixSet.length);
                    int j = cat.getRandom().nextInt(WCatEntity.SUFIXES.length);

                    String finalName;
                    if (cat.isBaby()) {
                        finalName = prefixSet[i] + "paw" + genderS;
                        cat.setRank(APPRENTICE);
                    } else {
                        finalName = prefixSet[i] + WCatEntity.SUFIXES[j] + genderS;
                        cat.setRank(WARRIOR);
                    }
                    //String finalName = prefixSet[i] + SUFIX[j] + genderS;

                    cat.setCustomName(Component.literal(finalName));
                    cat.setCustomNameVisible(true);
                    cat.setPrefix(Component.literal(prefixSet[i]));

                    cat.setNameColor(cat.getRank());

                }

                cat.setNameColor(cat.getRank());


                pPlayer.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA).ifPresent(cap -> {
                    String clanName = cap.getClanName(pPlayer.level());
                    if (cat.getRank() != NONE) cat.setClan(Component.literal(clanName));
                    cat.setClanUUID(cap.getCurrentClanUUID());

                    if (cat.level() instanceof ServerLevel sLevel) {
                        ClanData data = ClanData.get(sLevel);
                        ClanData.Clan clan = data.getClan(cap.getCurrentClanUUID());

                        if (clan != null) {
                            Component catJoinedClanLog = Component.translatable("clan.player_joined_log",
                                            cat.hasCustomName() ? cat.getCustomName() : Component.translatable("generic.catname"),
                                            Component.literal(clan.name).withStyle(Style.EMPTY.withColor(clan.color)));

                            cat.registerClanLog(catJoinedClanLog);
                        }
                    }


                });

                cat.rewardGeneticsAdvancements();
                cat.rewardMoonmoon();

                cat.mode = WCatEntity.CatMode.FOLLOW;
                cat.sendModeMessage(pPlayer);

            } else {
                cat.level().broadcastEntityEvent(cat, (byte) 6);
                cat.setCustomName(null);
            }
        }


        cat.gameEvent(GameEvent.EAT);
        return InteractionResult.sidedSuccess(cat.level().isClientSide());
    }

    private @NotNull InteractionResult clawsInteraction(Player pPlayer) {
        if (!cat.level().isClientSide && pPlayer instanceof ServerPlayer sPlayer) {

            if (PlayerShape.getCurrentShape(sPlayer) instanceof WCatEntity) {
                if (!cat.isBaby() && pPlayer.isShiftKeyDown() && cat.isTame() && (cat.getOwner() == pPlayer)) {
                    Component catInvName = cat.getCustomName();
                    NetworkHooks.openScreen(
                            sPlayer,
                            new SimpleMenuProvider(
                                    (id, inv, player) -> new WCatMenu(id, inv, cat),
                                    Component.literal(catInvName.getString())
                            ),
                            buf -> buf.writeInt(cat.getId())
                    );
                }

                boolean canCarry = PlayerShape.getCurrentShape(pPlayer) instanceof WCatEntity catShape && !catShape.isBaby();

                if (cat.isBaby() && cat.getRank() == KIT && canCarry) {
                    if (!cat.level().isClientSide) {
                        cat.startRiding(pPlayer, true);
                        cat.isBeingCarried = true;
                    }
                    return InteractionResult.sidedSuccess(cat.level().isClientSide);
                }
            }
        }

        return InteractionResult.sidedSuccess(cat.level().isClientSide);
    }

    private @Nullable InteractionResult medicineCatScentHerbs(Player pPlayer, InteractionHand pHand) {
        if (PlayerShape.getCurrentShape(pPlayer) instanceof Animal) {
            if (!pPlayer.getItemInHand(pHand).isEmpty()) {
                if (pPlayer.getItemInHand(pHand).is(ModItems.DOCK_LEAVES.get())) {
                    cat.medicineCatScentsBlock(pPlayer, ModBlocks.DOCK.get(), 40);
                    return InteractionResult.SUCCESS;
                }
                if (pPlayer.getItemInHand(pHand).is(ModItems.SORREL.get())) {
                    cat.medicineCatScentsBlock(pPlayer, ModBlocks.SORRELPLANT.get(), 40);
                    return InteractionResult.SUCCESS;
                }
                if (pPlayer.getItemInHand(pHand).is(ModItems.BURNET.get())) {
                    cat.medicineCatScentsBlock(pPlayer, ModBlocks.BURNETPLANT.get(), 40);
                    return InteractionResult.SUCCESS;
                }
                if (pPlayer.getItemInHand(pHand).is(ModItems.CHAMOMILE.get())) {
                    cat.medicineCatScentsBlock(pPlayer, ModBlocks.CHAMOMILEPLANT.get(), 40);
                    return InteractionResult.SUCCESS;
                }
                if (pPlayer.getItemInHand(pHand).is(ModItems.DAISY.get())) {
                    cat.medicineCatScentsBlock(pPlayer, ModBlocks.DAISYPLANT.get(), 40);
                    return InteractionResult.SUCCESS;
                }
                if (pPlayer.getItemInHand(pHand).is(ModItems.CATMINT.get())) {
                    cat.medicineCatScentsBlock(pPlayer, ModBlocks.CATMINTPLANT.get(), 40);
                    return InteractionResult.SUCCESS;
                }
                if (pPlayer.getItemInHand(pHand).is(ModItems.YARROW.get())) {
                    cat.medicineCatScentsBlock(pPlayer, ModBlocks.YARROWPLANT.get(), 40);
                    return InteractionResult.SUCCESS;
                }
                if (pPlayer.getItemInHand(pHand).is(ModItems.GLOW_SHROOM.get())) {
                    cat.medicineCatScentsBlock(pPlayer, ModBlocks.GLOWSHROOM.get(), 40);
                    return InteractionResult.SUCCESS;
                }
                if (pPlayer.getItemInHand(pHand).is(ModItems.JUNIPER_BERRIES.get())) {
                    cat.medicineCatScentsBlock(pPlayer, ModBlocks.JUNIPERPLANT.get(), 40);
                    return InteractionResult.SUCCESS;
                }
                if (pPlayer.getItemInHand(pHand).is(ModItems.COMFREY_LEAVES.get())
                        || pPlayer.getItemInHand(pHand).is(ModItems.COMFREY_ROOT.get())
                ) {
                    cat.medicineCatScentsBlock(pPlayer, ModBlocks.COMFREYPLANT.get(), 40);
                    return InteractionResult.SUCCESS;
                }
                if (pPlayer.getItemInHand(pHand).is(ModItems.FEVERFEW.get())) {
                    cat.medicineCatScentsBlock(pPlayer, ModBlocks.FEVERFEWPLANT.get(), 40);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return null;
    }

    private void syncClanData(Player pPlayer) {
        if (cat.isOwnedBy(pPlayer)) {
            if (pPlayer instanceof ServerPlayer sPlayer) {
                UUID currentUUID = sPlayer.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                        .map(WCEPlayerData::getCurrentClanUUID).orElse(ClanData.EMPTY_UUID);


                if (!cat.getClanUUID().equals(currentUUID)) {
                    cat.setClanUUID(currentUUID);
                    if (pPlayer instanceof ServerPlayer serverPlayer) {
                        ClanData data = ClanData.get(serverPlayer.serverLevel().getServer().overworld());
                        data.addClanCat(currentUUID, cat);
                    }
                }
            }
        }
    }
}
