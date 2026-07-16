package net.snowteb.warriorcats_events.entity.custom.wcat;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.block.ModBlocks;
import net.snowteb.warriorcats_events.block.custom.LavenderPetalsBlock;
import net.snowteb.warriorcats_events.block.custom.NestBlock;
import net.snowteb.warriorcats_events.block.entity.FreshkillPileBlockEntity;
import net.snowteb.warriorcats_events.block.entity.NestBlockEntity;
import net.snowteb.warriorcats_events.block.entity.TreeStumpBlockEntity;
import net.snowteb.warriorcats_events.clan.ClanData;
import net.snowteb.warriorcats_events.clan.WCEPlayerData;
import net.snowteb.warriorcats_events.clan.WCEPlayerDataProvider;
import net.snowteb.warriorcats_events.client.LeapClientState;
import net.snowteb.warriorcats_events.damagesources.WCEDamageTypes;
import net.snowteb.warriorcats_events.diseases.*;
import net.snowteb.warriorcats_events.diseases.kinds.BrokenPaw;
import net.snowteb.warriorcats_events.effect.ModEffects;
import net.snowteb.warriorcats_events.entity.ModEntities;
import net.snowteb.warriorcats_events.entity.client.WCModel;
import net.snowteb.warriorcats_events.entity.custom.*;
import net.snowteb.warriorcats_events.event.ModEventsForge1;
import net.snowteb.warriorcats_events.item.ModItems;
import net.snowteb.warriorcats_events.managers.ClimbDataAccessor;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.s2c.cats.OpenCatDataScreenPacket;
import net.snowteb.warriorcats_events.network.packet.s2c.clan.S2CSyncClanDataPacket;
import net.snowteb.warriorcats_events.network.packet.s2c.others.SyncDiseasesPacket;
import net.snowteb.warriorcats_events.particles.WCEParticles;
import net.snowteb.warriorcats_events.screen.menus.WCatMenu;
import net.snowteb.warriorcats_events.sound.ModSounds;
import net.snowteb.warriorcats_events.util.GeneticsForVariant;
import net.snowteb.warriorcats_events.util.ModTags;
import net.snowteb.warriorcats_events.zconfig.WCEServerConfig;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import tocraft.walkers.api.PlayerShape;

import java.util.*;
import java.util.function.Predicate;

import static net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity.Rank.*;

/**
 * Welcome to by far the most complicated shi to understand.
 */

public class WCatEntity extends TamableAnimal implements GeoEntity, Diseaseable<WCatEntity> {

    public enum CatMode {
        SIT,
        FOLLOW,
        WANDER
    }


    public enum Rank {
        NONE,
        KIT,
        APPRENTICE,
        WARRIOR,
        MEDICINE,
        DEPUTY
    }

    private static final EntityDataAccessor<Integer> VARIANT =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.INT);


    private static final EntityDataAccessor<Float> SCALE =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> KITTING_TICKS =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.INT);

    public static final UUID emptyUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    private GeneticsModule geneticsModule = new GeneticsModule(this);
    private DialoguesModule dialogueModule = new DialoguesModule(this);
    private MobInteractModule interactionModule = new MobInteractModule(this);

    private Goal preyTarget;
    private Goal monsterTarget;
    public boolean kitBorn = false;
    boolean animPlayed;
    boolean playerAnimPlayed;
    public CatMode mode = CatMode.WANDER;
    public CatMode lastMode = CatMode.WANDER;
    public BlockPos wanderCenter = null;
    int maxVariants = WCModel.TEXTURES.length;
    private boolean wasBaby = this.isBaby();
    int catSniffTickCooldown = 0;

    private int grumpyAtOwnerTick = 0;
    private int scentTick = 0;
    private int soundTick = 0;
    private Vec3 scentDirection = null;
    private Vec3 scentStartPos = null;
    private double scentDistance = 0;
    private double scentMaxDistance = 5;
    private double scentStep = 0.2;
    private boolean moodLoaded = false;

    public boolean returnHomeFlag = false;
    public boolean leaderCallingToFollowFlag = false;
    public boolean leaderCallingToSitFlag = false;
    public boolean lookAtLeaderFlag = false;
    public boolean isLookingAtLeader = false;
    public boolean isBeingCarried = false;

    private UUID motherUUID = null;
    private UUID fatherUUID = null;
    private UUID mateUUID = null;
    private int generation = 0;

    private boolean forbidFutureGenerationsFromMatingPlayer = false;
    private boolean forbiddenFromMatingPlayer = false;
    private UUID forbiddenPlayer = null;

    int lovingParticlesTicks = 0;

    public Component getMother() {
        return this.entityData.get(MOTHER).orElse(Component.literal("None"));
    }

    public void setMother(@Nullable Component name) {
        this.entityData.set(MOTHER, Optional.ofNullable(name));
    }

    public Component getFather() {
        return this.entityData.get(FATHER).orElse(Component.literal("None"));
    }

    public void setFather(@Nullable Component name) {
        this.entityData.set(FATHER, Optional.ofNullable(name));
    }

    public UUID getMotherUUID() {
        return motherUUID;
    }

    public void setMotherUUID(UUID uuid) {
        this.motherUUID = uuid;
    }


    public UUID getFatherUUID() {
        return fatherUUID;
    }


    public void setFatherUUID(UUID uuid) {
        this.fatherUUID = uuid;
    }

    public UUID getMateUUID() {
        return mateUUID;
    }

    public void setMateUUID(UUID uuid) {
        this.mateUUID = uuid;
    }

    public int getGeneration() {
        return generation;
    }

    public void setGeneration(int generation) {
        this.generation = generation;
    }


    // RULES
    public void setForbiddingFutureGensFromMatingPlayer(boolean value) {
        this.forbidFutureGenerationsFromMatingPlayer = value;
    }

    public boolean isForbiddingFutureGensFromMatingPlayer() {
        return this.forbidFutureGenerationsFromMatingPlayer;
    }

    public void setForbiddenPlayer(UUID uuid) {
        this.forbiddenPlayer = uuid;
    }

    public UUID getForbiddenPlayer() {
        return this.forbiddenPlayer;
    }

    public void setForbiddenFromMatingPlayer(boolean value) {
        this.forbiddenFromMatingPlayer = value;
    }

    public boolean isForbiddenFromMatingPlayer() {
        return forbiddenFromMatingPlayer;
    }


    private BlockPos homePosition = BlockPos.ZERO;

    private final SimpleContainer inventory = new SimpleContainer(3);

    public static final EntityDataAccessor<ItemStack> HEAD_ARMOR =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.ITEM_STACK);
    public static final EntityDataAccessor<ItemStack> CHEST_ARMOR =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.ITEM_STACK);
    public static final EntityDataAccessor<ItemStack> LEGS_ARMOR =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.ITEM_STACK);
    public static final EntityDataAccessor<ItemStack> FEET_ARMOR =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.ITEM_STACK);


    public float getAgeInMoons() {
        return this.entityData.get(AGE_SYNC);
    }

    public void setAgeInMoons(float i) {
        this.entityData.set(AGE_SYNC, i);
    }

    //    @Nullable
//    private Vec3 leaderCallTarget;


    private static final EntityDataAccessor<Boolean> ATTACKING =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.BOOLEAN);
    public int attackAnimationTimeout = 0;

    private static final EntityDataAccessor<Integer> GENDER =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> EXPECTING_KITS =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<Component>> MATE =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.OPTIONAL_COMPONENT);
    private static final EntityDataAccessor<Optional<Component>> PREFIX =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.OPTIONAL_COMPONENT);
    private static final EntityDataAccessor<Integer> RANK =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Float> AGE_SYNC =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> APP_SCALE =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Optional<Component>> CLAN =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.OPTIONAL_COMPONENT);
    private static final EntityDataAccessor<Integer> PERSONALITY =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> MOOD =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> INTERACTION_COOLDOWN =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> KITTING_COOLDOWN =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Optional<Component>> FATHER =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.OPTIONAL_COMPONENT);
    private static final EntityDataAccessor<Optional<Component>> MOTHER =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.OPTIONAL_COMPONENT);

    public static final EntityDataAccessor<Optional<UUID>> CLAN_UUID =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.OPTIONAL_UUID);


    // GENETICS

    public static final EntityDataAccessor<String> CHEST_FUR =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> BELLY_FUR =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> LEGS_FUR =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> HEAD_FUR =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> CHEEK_FUR =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> BACK_FUR =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> BOBTAIL =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> TAIL_FUR =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);

    public static final EntityDataAccessor<String> CHIMERA_GENE =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> CHIMERA_VARIANT =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.INT);


    public static final EntityDataAccessor<String> BASE =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> ORANGE_BASE =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> WHITE_RATIO =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> ALBINO =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> DILUTE =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> AGOUTI =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> TABBY_STRIPES =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> EYES_ANOMALY =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> SILVER =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);

    public static final EntityDataAccessor<String> EYE_COLOR_LEFT =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> EYE_COLOR_RIGHT =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> RUFOUSING_VARIANT =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> BLUE_RUFOUSING_VARIANT =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> ORANGE_BASE_VARIANT =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> WHITE_RATIO_VARIANT =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> ALBINO_VARIANT =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> TABBY_STRIPES_VARIANT =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> EYE_COLOR_VARIANT_LEFT =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> EYE_COLOR_VARIANT_RIGHT =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> NOISE =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SILVER_VARIANT =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.INT);


    public static final EntityDataAccessor<String> BASE_CHIMERA =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> ORANGE_BASE_CHIMERA =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> WHITE_RATIO_CHIMERA =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> ALBINO_CHIMERA =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> DILUTE_CHIMERA =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> AGOUTI_CHIMERA =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> TABBY_STRIPES_CHIMERA =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> SILVER_CHIMERA =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);

    public static final EntityDataAccessor<Integer> RUFOUSING_VARIANT_CHIMERA =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> BLUE_RUFOUSING_VARIANT_CHIMERA =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> ORANGE_BASE_VARIANT_CHIMERA =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> WHITE_RATIO_VARIANT_CHIMERA =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> ALBINO_VARIANT_CHIMERA =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> TABBY_STRIPES_VARIANT_CHIMERA =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> NOISE_CHIMERA =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SILVER_VARIANT_CHIMERA =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.INT);


    public static final EntityDataAccessor<Float> SIZE =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.FLOAT);

    public static final EntityDataAccessor<Integer> SCARS =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.INT);

    public static final EntityDataAccessor<Integer> SKIN_COLOR =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.INT);

    public static final EntityDataAccessor<Integer> IDLE_POSE =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.INT);


    private static final EntityDataAccessor<Boolean> GENETICAL_SKIN =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.BOOLEAN);

    WCGenetics storedFatherGenetics;
    public String textureKey;
    public final String[] textureLayersPaths = new String[26];

    // GENETICS


    private static final EntityDataAccessor<Optional<UUID>> PLAYER_BOUND_UUID =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    private static final EntityDataAccessor<Integer> ANIM_INDEX =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Boolean> SHOW_MORPH_NAME =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.BOOLEAN);


    private static final EntityDataAccessor<Boolean> IS_RESTING =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Integer> SITTING_INDEX =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> CHILLING_INDEX =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Boolean> IS_WAKING_UP =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> BROKEN_PAW =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> WRAPED_PAW =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.BOOLEAN);

    private int restingForTicks = 0;
    private int chillingForTicks = 0;

    private int sittingForTicks = 0;


    private final List<Disease<?>> diseaseList = new ArrayList<>();
    @Override
    public List<Disease<?>> getList() {
        return diseaseList;
    }

    @Override
    public void loadDiseasesNBT(CompoundTag tag) {
        Diseaseable.super.loadDiseasesNBT(tag);
        if (tag.contains("brokenPaw")) {
            this.entityData.set(BROKEN_PAW, tag.getBoolean("brokenPaw"));
        }
        if (tag.contains("wrapedPaw")) {
            this.entityData.set(WRAPED_PAW, tag.getBoolean("wrapedPaw"));
        }
    }

    @Override
    public void writeDiseasesNBT(CompoundTag tag) {
        Diseaseable.super.writeDiseasesNBT(tag);
        tag.putBoolean("brokenPaw", this.entityData.get(BROKEN_PAW));
        tag.putBoolean("wrapedPaw", this.entityData.get(WRAPED_PAW));
    }

    @Override
    public void onChange() {
        Diseaseable.super.onChange();
        if (!this.level().isClientSide()) {
            this.setBrokenPaw(this.hasDisease(DiseaseTypes.BROKEN_PAW));
            boolean wrappedPaw = this.getDisease(DiseaseTypes.BROKEN_PAW) instanceof BrokenPaw bp
                    && bp.isBoneWrapped();
            this.setWrappedPaw(wrappedPaw);
        }
    }

    public void setBrokenPaw(boolean value) {
        this.entityData.set(BROKEN_PAW, value);
    }

    public boolean isBrokenPaw() {
        return this.entityData.get(BROKEN_PAW)
                && !this.entityData.get(WRAPED_PAW)
                ;
    }

    public void setWrappedPaw(boolean value) {
        this.entityData.set(WRAPED_PAW, value);
    }

    public boolean isWrappedPaw() {
        return this.entityData.get(WRAPED_PAW) && this.entityData.get(BROKEN_PAW);
    }


    public boolean isRestingFromSitting() {
        return this.entityData.get(SITTING_INDEX) == 3;
    }

    public void setResting(boolean resting, int ticks) {
        this.entityData.set(IS_RESTING, resting);
        if (!resting && this.isWakingUp()) {
            this.entityData.set(IS_WAKING_UP, false);
        }
        if (!resting && this.isRestingFromSitting()) {
            this.entityData.set(SITTING_INDEX, 0);
        }

        this.restingForTicks = ticks;
    }

    public boolean isWakingUp() {
        return this.entityData.get(IS_WAKING_UP);
    }
    public void setWakingUp(boolean wakingUp) {
        this.entityData.set(IS_WAKING_UP, wakingUp);
    }

    public boolean isResting() {
        return this.entityData.get(IS_RESTING) || this.isRestingFromSitting();
    }

    public void setChilling(boolean resting, int ticks) {
        if (resting) {
            this.entityData.set(CHILLING_INDEX, 1 + this.getRandom().nextInt(3));
        } else {
            this.entityData.set(CHILLING_INDEX, 0);
        }
        this.chillingForTicks = ticks;
    }

    public boolean isChilling() {
        return this.entityData.get(CHILLING_INDEX) != 0;
    }

    private boolean isImage;

    public void setAnImage(boolean isAnImage) {
        this.isImage = isAnImage;
    }

    public boolean isAnImage() {
        return this.isImage;
    }

    private boolean isFlyingImage;

    public void setAFlyingImage(boolean b) {
        this.isFlyingImage = b;
    }

    public boolean isAFlyingImage() {
        return this.isFlyingImage;
    }

    public enum CatInteraction {
        GIVE_ITEM,
        TALK,
        SHOW_AFFECTION,
    }

    public enum Mood {
        HAPPY,
        CALM,
        STRESSED,
        SAD
    }

    final Map<String, List<String>> dialoguePool = new HashMap<>();


    public enum Personality {

        NONE, // 0

        // 1
        /**
         * Interacts normally. Normal cat, doesn't change anything.
         */
        CALM,

        // 2
        /**
         * Attacks monsters
         * <p>
         * Attacks the player for a little if it gets attacked, hisses sometimes
         * <p>
         * Responds aggresively sometimes, higher chance of interaction failed
         */
        GRUMPY,

        //3
        /**
         * Extremely low taming probability
         * Lower rate of picking up items
         */
        CAUTIOUS,

        //4
        /**
         * Attacks monsters <p>
         * Double wander radius, might not ask or lower chance of asking for food and herbs. <P>
         * Double stop and start distance in follow
         */
        INDEPENDENT,

        //5
        /**
         * 100% chance of taming
         * Purrs sometimes
         */
        FRIENDLY,

        //6
        /**
         * Low taming probability
         * Triple follow and stop distance
         * Mrrows sometimes
         */
        SHY,

        //7
        /**
         * Attacks monsters <p>
         * Higher rate of picking it up. Might end up eating it or consuming it.
         */
        AMBITIOUS,

        //8
        /**
         * ItemPickup goal, lower rate of picking up items
         */
        HUMBLE,

        //9
        /**
         * Attacks monsters 20 blocks<p>
         * Slightly higher rate of picking up items
         */
        RECKLESS,
    }

    public DialoguesModule getDialogueModule() {
        return this.dialogueModule;
    }


    public static final EntityDataAccessor<Integer> FRIENDSHIP_SYNC =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.INT);


    private final Map<UUID, Integer> friendshipMap = new HashMap<>();

    public void syncFriendshipToPlayer(ServerPlayer player) {
        int value = getFriendshipLevel(player.getUUID());
        this.entityData.set(FRIENDSHIP_SYNC, value);
    }


    public int getFriendshipLevel(UUID playerUUID) {
        return friendshipMap.getOrDefault(playerUUID, 0);
    }

    public void setFriendshipLevel(UUID playerUUID, int value) {
        value = Math.max(0, Math.min(100, value));
        friendshipMap.put(playerUUID, value);
        if (!level().isClientSide) {
            Player player = level().getPlayerByUUID(playerUUID);
            if (player instanceof ServerPlayer sp) {
                syncFriendshipToPlayer(sp);
            }
        }
    }

    public float getMoodInteractionAddition() {
        Mood currentMood = this.getMood();
        switch (currentMood) {
            case HAPPY -> {
                return 0.2f;
            }
            case CALM -> {
                return 0f;
            }
            case STRESSED -> {
                return -0.3f;
            }
            case SAD -> {
                return -0.2f;
            }
        }
        return 0;
    }



    public int getKittingInteractCooldown() {
        return this.entityData.get(KITTING_COOLDOWN);
    }

    public void setKittingInteractCooldown(int value) {
        this.entityData.set(KITTING_COOLDOWN, value);
    }

    public int getInteractionCooldown() {
        return this.entityData.get(INTERACTION_COOLDOWN);
    }

    public void setInteractionCooldown(int value) {
        this.entityData.set(INTERACTION_COOLDOWN, value);
    }

    public void randomImproveMood(UUID playerUUID) {
        if (this.random.nextFloat() <= (0.2 + (double) getFriendshipLevel(playerUUID) / 300)) {
            int randomMood = this.random.nextInt(1);
            Mood[] values = Mood.values();
            if (randomMood == 0) {
                this.entityData.set(MOOD, values[0].ordinal());
            } else {
                this.entityData.set(MOOD, values[1].ordinal());
            }
        }
    }



    public Mood getMood() {
        int value = this.entityData.get(MOOD);
        if (value < 0 || value >= Mood.values().length) {
            return Mood.CALM;
        }
        return Mood.values()[value];
    }

    public void setRandomMood(RandomSource random) {
        Mood[] values = Mood.values();
        int index = random.nextInt(values.length);
        this.entityData.set(MOOD, values[index].ordinal());
    }

    public void setSpecificMood(Mood mood) {
        this.entityData.set(MOOD, mood.ordinal());
    }


    public void assignRandomPersonality(RandomSource random) {
        Personality[] values = Personality.values();
        int index = 1 + random.nextInt(values.length - 1);
        setPersonality(values[index]);
    }


    public Personality getPersonality() {
        int value = this.entityData.get(PERSONALITY);
        if (value < 0 || value >= Personality.values().length) {
            return Personality.NONE;
        }
        return Personality.values()[value];
    }

    public void setPersonality(Personality personality) {
        this.entityData.set(PERSONALITY, personality.ordinal());
    }

    public Component getClan() {
        return this.entityData.get(CLAN).orElse(Component.literal("None"));
    }

    public void setClan(Component clanName) {
        this.entityData.set(CLAN, Optional.of(clanName));
    }

    public UUID getClanUUID() {
        return this.entityData.get(CLAN_UUID).orElse(ClanData.EMPTY_UUID);
    }

    public void setClanUUID(UUID uuid) {
        UUID oldClan = this.getClanUUID();

        this.entityData.set(CLAN_UUID, Optional.ofNullable(uuid));

        if (oldClan.equals(uuid)) return;

        if (!this.level().isClientSide && this.level() instanceof ServerLevel sLevel) {
            ClanData data = ClanData.get(sLevel);

            if (!oldClan.equals(ClanData.EMPTY_UUID)) {
                data.removeClanCatFromClan(oldClan, this);
            }

            if (!uuid.equals(ClanData.EMPTY_UUID)) {
                data.addClanCat(uuid, this);
            }
        }
    }


    public static final String[] PREFIXES = {
            "Adder", "Alder", "Allium", "Almond", "Amber", "Amethyst", "Andesite",
            "Ant", "Apple", "Armadillo", "Ash", "Ashen", "Ashy", "Aspen", "Aster",
            "Axolotl", "Azure", "Badger", "Bark", "Barley", "Bat", "Bear", "Beige",
            "Bengal", "Bent", "Berry", "Big", "Birch", "Bird", "Black", "Blaze",
            "Blazing", "Blizzard", "Bloom", "Blooming", "Blossom", "Blotch",
            "Blotched", "Blotchy", "Blue", "Bluet", "Blueberry", "Boa", "Bold",
            "Bone", "Bracken", "Branch", "Bramble", "Bright", "Brindle", "Broken",
            "Bubble", "Bug", "Bumble", "Burn", "Burnet", "Burning", "Burnt", "Butterfly",
            "Cactus", "Camel", "Carp", "Chamomile", "Cherry", "Chestnut", "Chervil",
            "Chirp", "Chirping", "Chisel", "Cinder", "Cinnamon", "Clay", "Cloud", "Clouded",
            "Coal", "Cobble", "Cocoa", "Cod", "Cold", "Cornflower", "Cranberry",
            "Crane", "Cream", "Crow", "Crystal", "Current", "Daisy", "Damp", "Dandelion",
            "Dapple", "Dappled", "Dark", "Dawn", "Deep", "Deer", "Destiny", "Dew",
            "Diamond", "Diorite", "Dock", "Dog", "Dolphin", "Donkey", "Dove", "Dragonfly",
            "Drift", "Drip", "Drizzle", "Duck", "Dull", "Dusk", "Dust", "Dusty", "Eagle",
            "Echo", "Eel", "Egg", "Elk", "Ember", "Emerald", "Evening", "Faded", "Falcon",
            "Fallen", "Fallow", "Fang", "Fawn", "Feather", "Fennel", "Fierce", "Fig", "Fin",
            "Finch", "Fir", "Fire", "Fish", "Flame", "Flare", "Fleck", "Flecked", "Fleet",
            "Float", "Flow", "Flower", "Flowering", "Flurry", "Flutter", "Flying", "Fog",
            "Fox", "Freckle", "Frog", "Frost", "Frosted", "Frosty", "Fuzzy", "Gecko",
            "Gentle", "Glade", "Glow", "Glowing", "Goat", "Gold", "Golden", "Goldenrod",
            "Goose", "Granite", "Green", "Grey", "Guppy", "Hail", "Hallow", "Hallowed",
            "Happy", "Hare", "Hawk", "Hay", "Haze", "Hazel", "Hazy", "Heavy", "Heather",
            "Heron", "Hollow", "Holly", "Hop", "Hope", "Hornet", "Horse", "Hound", "Howl",
            "Howling", "Hunch", "Hurricane", "Ice", "Icicle", "Icy", "Indigo", "Iron",
            "Ivory", "Ivy", "Jackdaw", "Jay", "Joy", "Jump", "Jumping", "Jungle", "Juniper",
            "Kestrel", "Kink", "Kite", "Kiwi", "Lake", "Lantern", "Large", "Lark",
            "Lavender", "Leaf", "Leopard", "Light", "Lightning", "Lilac", "Lily",
            "Lion", "Little", "Lizard", "Llama", "Long", "Loud", "Magpie", "Mallow",
            "Maple", "Marble", "Marbled", "Marigold", "Marsh", "Meadow", "Mellow",
            "Melon", "Milk", "Minnow", "Mint", "Minty", "Mist", "Misted", "Misty",
            "Mold", "Mole", "Moon", "Morning", "Moss", "Mossy", "Moth", "Mouse",
            "Mule", "Mushroom", "Nectar", "Needle", "Nettle", "Newt", "Night", "Noble",
            "Nut", "Nutmeg", "Oak", "Oat", "Ocean", "Ocelot", "Odd", "Olive", "Otter",
            "Orange", "Orchid", "Owl", "Pale", "Panda", "Parrot", "Parsley", "Patch",
            "Patched", "Peanut", "Pear", "Pearl", "Pecan", "Pebble", "Peony", "Perch",
            "Petal", "Pigeon", "Pike", "Pine", "Pink", "Pistachio", "Plum", "Plump",
            "Pollen", "Pond", "Pool", "Pop", "Poppy", "Pounce", "Prickle", "Proud",
            "Puddle", "Pumpkin", "Python", "Quail", "Quick", "Quiet", "Rabbit", "Raccoon",
            "Radiant", "Rain", "Rainbow", "Raspberry", "Rat", "Rattle", "Rattlesnake",
            "Raven", "Ravenous", "Red", "Reed", "River", "Robin", "Rook", "Rooster", "Root",
            "Rose", "Rosemary", "Rowan", "Running", "Rust", "Sage", "Salmon", "Sand", "Scarlet",
            "Scorch", "Scorpion", "Shade", "Shaded", "Shadow", "Shark", "Sheep", "Shell",
            "Shimmer", "Shimmering", "Shivering", "Short", "Shrew", "Shy", "Silk", "Silver",
            "Skunk", "Sky", "Slate", "Sleet", "Sloe", "Small", "Smoke", "Smolder", "Smoldering",
            "Sniff", "Snow", "Snowdrop", "Soft", "Soot", "Sorrel", "Speckled", "Spark", "Sparkle",
            "Sparkling", "Sparrow", "Speck", "Speckle", "Speckled", "Spider", "Splinter",
            "Spot", "Spotted", "Splash", "Splotch", "Splotched", "Spruce", "Squid", "Squirrel",
            "Starling", "Steady", "Stone", "Stomp", "Storm", "Storming", "Stormy", "Strawberry",
            "Stream", "Sun", "Sunflower", "Sunny", "Sunset", "Swallow", "Swan", "Sweet", "Swift",
            "Sycamore", "Tadpole", "Tall", "Tan", "Tawny", "Thicket", "Thistle", "Thorn", "Thorny",
            "Thrush", "Thunder", "Thyme", "Tide", "Tiger", "Tiny", "Toad", "Torch", "Trout", "Trudge",
            "Tsunami", "Tulip", "Turtle", "Twig", "Twitch", "Typhoon", "Umber", "Valley", "Vine",
            "Violet", "Viper", "Void", "Vole", "Walnut", "Warm", "Wasp", "Wave", "Waving", "Web",
            "Wheat", "Whirl", "Whisper", "Whispering", "White", "Whorl", "Wild", "Willow", "Wilted",
            "Wind", "Winding", "Wish", "Wisp", "Wolf", "Wood", "Woods", "Wren", "Yarrow", "Yellow",
            "Yew", "Zap", "Zip"
    };

    public static final String[] SUFIXES = {
            "ash", "bark", "beak", "beam", "bee", "belly", "berry", "bird",
            "bite", "blaze", "bloom", "blossom", "bound", "bracken", "branch",
            "breeze", "briar", "bright", "brush", "brook", "bush", "burn",
            "burr", "burrow", "bush", "call", "charm", "chill", "claw",
            "cleft", "cliff", "cloud", "crash", "crawl", "crouch", "creek",
            "creep", "cry", "curl", "dapple", "dappled", "dapples", "dash",
            "dawn", "dew", "dig", "dot", "dream", "drop", "dusk", "dust",
            "echo", "ear", "eater", "eye", "eyes", "face", "fall", "fang",
            "fate", "fawn", "feather", "feet", "fern", "field", "fig", "fin",
            "fire", "fish", "flake", "flame", "flap", "flight", "flow", "flutter",
            "foot", "footed", "forest", "fox", "frond", "frost", "fur", "gaze",
            "ghost", "glaze", "gleam", "glimmer", "glow", "gorse", "grass", "hawk",
            "haze", "heart", "horn", "hunt", "ice", "ivy", "jaw", "jump", "keep",
            "keeper", "leaf", "leap", "leg", "light", "mask", "minnow", "mint",
            "mist", "moon", "morning", "mound", "mountain", "mouse", "muzzle",
            "needle", "nose", "orb", "orbs", "pad", "patch", "patches", "path",
            "peak", "pelt", "petal", "pit", "plant", "pond", "pool", "poppy",
            "pounce", "pride", "puddle", "puff", "quail", "race", "rain", "reed",
            "ripple", "river", "rock", "rose", "runner", "rustle", "scar", "scratch",
            "seed", "shade", "shadow", "shell", "shimmer", "shine", "shiver",
            "shore", "sight", "skip", "sky", "slash", "slice", "slide", "slip",
            "smudge", "snap", "sneeze", "snout", "snow", "soar", "song", "soul",
            "spark", "speck", "speckle", "speckles", "spirit", "splash", "splotch",
            "splotches", "spot", "spots", "spring", "spout", "squeak", "stalk",
            "stem", "step", "sting", "stomp", "stone", "storm", "stream", "strike",
            "stripe", "surge", "sweep", "swoop", "tail", "talon", "teeth", "thistle",
            "thorn", "throat", "thump", "thunder", "tide", "toe", "tooth", "trail",
            "trickle", "tuft", "twist", "vine", "wander", "watcher", "water", "wave",
            "whisker", "whisper", "whistle", "willow", "wind", "winds", "wing", "wish"
    };

    /**
     * Depending on the variant, pick a set of prefixes
     */
    String[] getPrefixForVariant() {
        return PREFIXES;
    }


    public WCatEntity(EntityType<? extends TamableAnimal> type, Level world) {
        super(type, world);
        if (!this.level().isClientSide()) {
            this.setGender(this.random.nextInt(2));
        }
    }

    public int getWanderRadius() {
        if (this.getPersonality() == Personality.INDEPENDENT) {
            return WCEServerConfig.SERVER.WILDCAT_WANDER_RADIUS.get() * 2;
        }
        return WCEServerConfig.SERVER.WILDCAT_WANDER_RADIUS.get();
    }

    public int getKittingTime() {
        return 20 * 60 * WCEServerConfig.SERVER.KITTING_MINUTES.get();
    }

    public int getKitGrowthTimeMinutes() {
        return WCEServerConfig.SERVER.KIT_GROWTH_MINUTES.get();
    }


    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    /**
     * Under certain conditions, proceed to follow the owner.
     */

    /**
     * Under certain chance and conditions, find a target block in certain range. This depends on the cat's rank.
     * When it starts, move to the block.
     * When it stops, set a cooldown so it doesn't constantly move from block to block.
     */

    /**
     * Under Certain conditions, the cat will pick a position withing the radius and will move to it.
     * When it stops, set a cooldown so it doesn't constantly wander around.
     */


    public BlockPos getHomePosition() {
        return homePosition;
    }

    public boolean hasHomePosition() {
        return homePosition != null && !homePosition.equals(BlockPos.ZERO);
    }

    public void setHomePosition(BlockPos pos) {
        this.homePosition = (pos == null) ? BlockPos.ZERO : pos;
    }

    public boolean canAccept(ItemStack stack) {

        if (this.getRank() == MEDICINE) {
            if (!stack.is(ModTags.Items.HERBS)) return false;
        } else if (this.isBaby() && this.getRank() == KIT) {
            if (!(stack.is(Items.STICK) || stack.is(Items.MOSS_BLOCK) || stack.is(Items.SLIME_BALL))) return false;
        } else {
            if (!stack.is(ModTags.Items.PREY)) return false;
        }

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack slot = inventory.getItem(i);

            if (!slot.isEmpty() && ItemStack.isSameItemSameTags(slot, stack) && slot.getCount() < 32) {
                return true;
            }

            if (slot.isEmpty()) {
                return true;
            }
        }

        return false;
    }

    public boolean tryMakePoultice() {
        int emptySlotIndex = -1;
        int dockLeavesSlotIndex = -1;
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack slot = inventory.getItem(i);
            if (slot.is(ModItems.DOCK_LEAVES.get())) {
                dockLeavesSlotIndex = i;
            }
            if (slot.isEmpty()) {
                emptySlotIndex = i;
            }
        }

        if (emptySlotIndex == -1 || dockLeavesSlotIndex == -1) return false;

        ItemStack ingredient = inventory.getItem(dockLeavesSlotIndex);

        ingredient.shrink(1);

        if (ingredient.isEmpty()) {
            this.setItemSynced(dockLeavesSlotIndex, ItemStack.EMPTY);
        }

        this.setItemSynced(emptySlotIndex, new ItemStack(ModItems.DOCK_POULTICE.get(), 2));

        this.level().playSound(null, this.blockPosition(),
                SoundEvents.SLIME_JUMP, SoundSource.NEUTRAL, 0.6F, 1.4F);

        return true;
    }

    public boolean hasPoultice() {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack slot = inventory.getItem(i);
            if (slot.is(ModItems.DOCK_POULTICE.get())) {

                return true;
            }
        }
        return false;
    }

    public boolean tryHealClanmante(LivingEntity catToHeal) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack slot = inventory.getItem(i);
            if (slot.is(ModItems.DOCK_POULTICE.get())) {
                inventory.getItem(i).shrink(1);
                catToHeal.setHealth(catToHeal.getHealth() + 6F);
                return true;
            }
        }
        return false;
    }


    /**
     * For every slot in the inventory:
     * If the slot is empty, then copy the item from the dropped item and return true.
     * If the item in the slot is the same as the item on the ground, then increment its ammount by 1 and return true.
     * Otherwise, return false.
     */
    public boolean tryInsert(ItemStack stack) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack slot = inventory.getItem(i);
            if (slot.isEmpty()) {
                this.setItemSynced(i, stack.copyWithCount(1));
                return true;
            }
            if (ItemStack.isSameItemSameTags(slot, stack) && slot.getCount() < 32) {
                slot.grow(1);
                return true;
            }
        }
        return false;
    }

    /**
     * Called when the entity dies.
     */
    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {

        if (this.level().isClientSide) return;
        /**
         * If it has an owner, then send the owner a message with the coordinates where the cat died.
         */


        if (this.getMateUUID() != null) {
            Entity mate = ((ServerLevel) this.level()).getEntity(this.getMateUUID());
            if (mate instanceof WCatEntity catMate) {
                catMate.entityData.set(MOOD, Mood.SAD.ordinal());
                catMate.setMateUUID(emptyUUID);
                catMate.setMate(Component.empty().append(this.getCustomName().copy()).append(Component.literal(" (deceased)").withStyle(ChatFormatting.GRAY)));
            }
        }

        /**
         * For every slot in the cats inventory, get the item that's in the slot.
         * If the slot is not empty, then spawn a dropped item at the position and set the slot in the inventory empty.
         */
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);

            if (!stack.isEmpty()) {
                Containers.dropItemStack(this.level(), this.getX(), this.getY(), this.getZ(), stack.copy());
                this.setItemSynced(i, ItemStack.EMPTY);
            }
        }

        dropArmor(EquipmentSlot.HEAD);
        dropArmor(EquipmentSlot.CHEST);
        dropArmor(EquipmentSlot.LEGS);
        dropArmor(EquipmentSlot.FEET);

        super.dropCustomDeathLoot(source, looting, recentlyHit);

    }

    private double getThreatDetectionRange() {
        double range = 8D;

        switch (getPersonality()) {
            case RECKLESS -> range = 14D;
            case GRUMPY, INDEPENDENT, AMBITIOUS -> range = 8D;
        }

        return range;
    }

    private boolean willAttackMonsters() {
        boolean value = false;
        switch (getPersonality()) {
            case RECKLESS -> value = true;
            case GRUMPY -> value = true;
            case INDEPENDENT -> value = true;
            case AMBITIOUS -> value = true;
        }
        return value;
    }

    public double itemPickupChanceMultiplier() {
        double value = 1;
        switch (getPersonality()) {
            case RECKLESS -> value = 0.7;
            case AMBITIOUS -> value = 0.3;
            case CAUTIOUS -> value = 1.6;
            case HUMBLE -> value = 1.3;
        }
        return value;
    }


    @Override
    protected void registerGoals() {
        this.preyTarget = new NearestAttackableTargetGoal<>(this, Animal.class, 10, false, false, (target) -> {
            return mode == CatMode.WANDER && !this.returnHomeFlag && !this.onBorderPatrolFlag
                    && (target instanceof MouseEntity
                    || target instanceof PigeonEntity
                    || target instanceof SquirrelEntity
                    || target instanceof Rabbit
                    || (target instanceof LizardTailEntity || (target instanceof LizardEntity lizard && !lizard.isTame() && this.isBaby()))
                    || target.getType().is(ModTags.EntityTypes.PREY_MOBS));
        });

        this.monsterTarget = new NearestAttackableTargetGoal<>(this, LivingEntity.class,
                10, false, false,
                target -> target instanceof Monster && !(target instanceof Creeper || target instanceof Piglin || target instanceof ZombifiedPiglin || target instanceof PiglinBrute)
                        && this.distanceTo(target) <= getThreatDetectionRange() && willAttackMonsters() && this.getRank() != KIT
                        && this.mode != CatMode.SIT && !this.returnHomeFlag && target.isAlive() && !target.isDeadOrDying());

        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(0, new WCGoals.WCatReturnHomeGoal(this, 1.1D));
        this.goalSelector.addGoal(0, new WCGoals.WCatLeaderCallsGoal(this));

        this.targetSelector.addGoal(1, new WCGoals.WCatPanic(this, 1.2D));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.goalSelector.addGoal(3, new WCGoals.WCatSeekShelterGoal(this, 1.2D));
        this.goalSelector.addGoal(3, new WCGoals.WCatMedicineHealsCats(this));
        this.goalSelector.addGoal(3, new WCGoals.WCatAttackMossBall(this));
        this.goalSelector.addGoal(4, new WCGoals.WCatPickupItemGoal(this));

        this.goalSelector.addGoal(5, new WCGoals.WCatDeputySendsPatrols(this));
        this.goalSelector.addGoal(5, new WCGoals.WCatBorderPatrolGoal(this));
        this.goalSelector.addGoal(5, new WCGoals.WCatHuntingPatrolGoal(this));

        this.goalSelector.addGoal(6, new WCGoals.WCatDepositFreshkill(this));
        this.goalSelector.addGoal(7, new BreedGoal(this, 0.8D));
        this.goalSelector.addGoal(8, new WCGoals.WCatRunWithPlayerGoal(this, 1f));
        this.goalSelector.addGoal(8, new WCGoals.WCatFollowOwnerGoal(this, 1.2D, 1.2F, 7.0F));
        this.targetSelector.addGoal(9, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(10, this.monsterTarget);
        this.targetSelector.addGoal(11, this.preyTarget);
        this.goalSelector.addGoal(12, new WCAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(13, new WCGoals.WCatMoveToMateGoal(this));
        if (!this.isAnImage()) this.goalSelector.addGoal(14, new WCGoals.WCatBoundedWanderGoal(this, 0.8D));
        this.goalSelector.addGoal(15, new WCGoals.WCatGiveRandomItemGoal(this));
        if (!this.isAnImage()) this.goalSelector.addGoal(15, new WCGoals.WCatRandomLookAroundGoal(this));
        if (!this.isAnImage()) this.goalSelector.addGoal(15, new WCGoals.WCatLookAtPlayerGoal(this, Player.class, 8.0F));
        if (!this.isAnImage()) this.goalSelector.addGoal(16, new WCGoals.WCatCasualBlockSeekGoal(this, 0.8D, 15, 0.07D));

    }


    public static AttributeSupplier.Builder setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.33D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(Attributes.ARMOR, 1.0D);
    }

    private void dropArmor(EquipmentSlot slot) {
        ItemStack stack = getItemBySlot(slot);
        if (!stack.isEmpty()) {
            spawnAtLocation(stack.copy());
            setItemSlot(slot, ItemStack.EMPTY);
        }
    }

    @Override
    protected void hurtArmor(DamageSource source, float amount) {
        if (amount <= 0) return;

        damageArmor(EquipmentSlot.HEAD, amount);
        damageArmor(EquipmentSlot.CHEST, amount);
        damageArmor(EquipmentSlot.LEGS, amount);
        damageArmor(EquipmentSlot.FEET, amount);
    }

    private void damageArmor(EquipmentSlot slot, float damage) {
        ItemStack stack = getItemBySlot(slot);
        if (stack.isEmpty()) return;

        int durabilityLoss = Math.max(1, Math.round(damage));

        stack.hurtAndBreak(
                durabilityLoss,
                this,
                e -> setItemSlot(slot, ItemStack.EMPTY)
        );
    }

    public MobInteractModule getInteractionModule() {
        return interactionModule;
    }

    /**
     * This is just too much, if you have any questions about the mobinteract just ask me, i aint writing all this :sob:
     */
    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        InteractionResult result = this.getInteractionModule().interact(pPlayer, pHand);
        if (result != null) return result;

        return super.mobInteract(pPlayer, pHand);
    }


    /**
     * If the cooldown is not ready, send message and return.
     * <p>
     * Scan all the blocks in certain radius. If it finds the desired block, check if the last block or this one was nearest.
     * After that, if the block was null (couldn't find a block), send a message, set cooldown, and return.
     * Otherwise, send a message and set cooldown.
     */
    public void medicineCatScentsBlock(Player player, Block targetBlock, int radius) {
        if (this.level().isClientSide) return;

        Component name = this.getName();

        if (catSniffTickCooldown > 0) {
            player.sendSystemMessage(
                    Component.translatable("wcat.cant_scent", name).withStyle(ChatFormatting.GRAY)
            );
            return;
        }

        BlockPos catPos = this.blockPosition();
        final BlockPos[] closest = {null};
        final double[] closestDistSqr = {Double.MAX_VALUE};

        BlockPos.betweenClosedStream(
                catPos.offset(-radius, -radius, -radius),
                catPos.offset(radius, radius, radius)
        ).forEach(pos -> {
            if (this.level().getBlockState(pos).is(targetBlock)) {
                double dist = pos.distSqr(catPos);
                if (dist < closestDistSqr[0]) {
                    closestDistSqr[0] = dist;
                    closest[0] = pos.immutable();
                }
            }
        });

        if (closest[0] == null) {
            player.sendSystemMessage(
                        Component.translatable("wcat.no_herbs_nearby", name).withStyle(ChatFormatting.GRAY)
            );
            catSniffTickCooldown = 400;
            return;
        }

        int distance = (int) Math.sqrt(closestDistSqr[0]);

        String distanceText = String.valueOf(distance);

        player.sendSystemMessage(
                Component.translatable("wcat.medicine_found_herbs",
                                name,
                                targetBlock.getName().withStyle(ChatFormatting.AQUA),
                                Component.literal(distanceText).withStyle(ChatFormatting.GREEN))
        );

        catSniffTickCooldown = 400;

        Vec3 catVec = this.position().add(0, 0.6, 0);
        Vec3 targetVec = Vec3.atCenterOf(closest[0]);

        Vec3 direction = targetVec.subtract(catVec).normalize();

        double maxDistance = 10.0;

        double step = 0.2;

        for (double d = 0; d < maxDistance; d += step) {
            Vec3 particlePos = catVec.add(direction.scale(d));

            double pOffset = 0.1 + (1 / 3) * d + (0.05 * (Math.exp(0.5 * d) - 1));
            ((ServerLevel) this.level()).sendParticles(
                    ParticleTypes.END_ROD,
                    particlePos.x,
                    particlePos.y,
                    particlePos.z,
                    1,
                    pOffset, pOffset, pOffset,
                    0.0
            );
        }

    }


    /**
     * Called when two entities are set in love.
     * <p>
     * If the other parent is a Wild cat, and this cat is tamed, and the other one is tamed:
     * Then if this cats gender is 1, and if the other cats gender is 0, then set this cat to expect kits and send the advancement.
     * Then reset the love counter, this so the she cat doesn't spawn isInfinite kits for no reason.
     * <p>
     * This method is called in both cats, so its not necessary to make two logics.
     */
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {

        if (!(otherParent instanceof WCatEntity partner)) return null;
        if (!this.isTame() || !partner.isTame()) return null;

        WCatEntity female = null;
        WCatEntity male = null;

        if (this.getGender() == 1 && partner.getGender() == 0) {
            female = this;
            male = partner;


        } else if (this.getGender() == 0 && partner.getGender() == 1) {
            female = partner;
            male = this;
        }

        if (female != null) {
            female.setExpectingKits(true);
            female.getGeneticsModule().setStoredFatherGenetics(male);

            Entity owner = female.getOwner();
            if (owner instanceof ServerPlayer serverPlayer) {
                MinecraftServer server = serverPlayer.getServer();
                if (server != null) {
                    Advancement adv = server.getAdvancements()
                            .getAdvancement(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID,"bred_wildcat"));
                    if (adv != null) {
                        serverPlayer.getAdvancements().award(adv, "bred_wildcat");
                    }
                }
            }
        }

        this.resetLove();
        partner.resetLove();
        this.entityData.set(MOOD, Mood.HAPPY.ordinal());
        partner.entityData.set(MOOD, Mood.HAPPY.ordinal());

        if (this.getGender() == partner.getGender()) {
            Entity owner = this.getOwner();
            if (owner instanceof ServerPlayer serverPlayer) {
                MinecraftServer server = serverPlayer.getServer();
                if (server != null) {
                    Advancement adv = server.getAdvancements()
                            .getAdvancement(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID,"homo_bred"));
                    if (adv != null) {
                        serverPlayer.getAdvancements().award(adv, "homo_bred");
                    }
                }
            }
        }

        Component mateName = otherParent.getCustomName();
        Component thisName = this.getCustomName();

        this.setMate(mateName);
        partner.setMate(thisName);
        this.setMateUUID(otherParent.getUUID());
        partner.setMateUUID(this.getUUID());

        if (mateName == null) mateName = Component.empty();
        if (thisName == null) thisName = Component.empty();

        Component message = Component.translatable("wcat.mates_log",
                        mateName.copy(),
                        thisName.copy());

        this.registerClanLog(message);

        if (Objects.equals(partner.getMate(), Component.literal("None"))) {
            partner.setMate(this.getCustomName());
        }

        ExperienceOrb.award(level, this.position(), random.nextInt(3) + 2);


        return null;
    }


    /**
     * Called every tick.
     */
    @Override
    public void aiStep() {
        super.aiStep();

        /**
         * If this cat is expecting kits, then set a counter until the kits are born.
         */
        if (!this.level().isClientSide && this.isExpectingKits()) {
            this.setKittingTicks(this.getKittingTicks() + 1);

            if (this.getKittingTicks() >= getKittingTime()) {
                this.setExpectingKits(false);
                this.setKittingTicks(0);

                Kitting();

            }
        }

        /**
         * If the kit grew from baby to adult, then perform onGrewUp, change its rank and attributes.
         */
        if (!this.level().isClientSide()) {
            if (this.getInteractionCooldown() > 0) {
                this.setInteractionCooldown(this.getInteractionCooldown() - 1);
            }
            if (this.getKittingInteractCooldown() > 0) {
                this.setKittingInteractCooldown(this.getKittingInteractCooldown() - 1);
            }

            if (this.wasBaby && !this.isBaby()) {
                this.setRank(WARRIOR);
                this.onGrewUp();
                this.applyAdultAttributes();
                this.setHealth(this.getMaxHealth());
            }
            this.wasBaby = this.isBaby();
        }

        /**
         * If the cats mode is sit:
         * if it isn't ordered to sit, order it to sit, then stop all navigations.
         * Otherwise (If the cats mode is not sit):
         * If it is ordered to sit, then set it to false.
         */
        if (!this.level().isClientSide()){
            if (mode == CatMode.SIT) {
                if (!this.isOrderedToSit()) this.setOrderedToSit(true);
                this.setTarget(null);
                this.getNavigation().stop();
                this.sittingForTicks++;

                int newState = 0;

                if (this.sittingForTicks > 7200 + this.getId()*10) {
                    newState = 3;
                } else if (this.sittingForTicks > 3600 + this.getId()*10) {
                    newState = 2;
                } else if (this.sittingForTicks > 600+ this.getId()*10) {
                    newState = 1;
                }

                if (this.entityData.get(SITTING_INDEX) != newState) {
                    this.entityData.set(SITTING_INDEX, newState);
                }

                if (!this.isResting() && newState == 3) {
                    this.setResting(true, 36000 + 200*this.getId());
                }

            } else {
                if (this.isOrderedToSit()) this.setOrderedToSit(false);
                this.sittingForTicks = 0;
                if (this.entityData.get(SITTING_INDEX) != 0) {
                    this.entityData.set(SITTING_INDEX, 0);
                }
            }
        }

        if (!this.level().isClientSide()) {
            if (this.isResting()) {
                if (this.restingForTicks > 0) {
                    this.restingForTicks--;

                    if (this.level() instanceof ServerLevel sLevel && this.tickCount % 10 == 0) {
                        sLevel.sendParticles(
                                WCEParticles.SLEEP.get(),
                                this.getX(), this.getY() + 0.5, this.getZ(),
                                1, 0, 0, 0,0.005);
                    }

                    if (this.restingForTicks < 80) {
                        if (!this.isWakingUp()) {
                            this.setWakingUp(true);
                        }
                    }

                } else {
                    this.setResting(false, 0);
                    this.entityData.set(SITTING_INDEX, 0);
                }
            }

            if (this.isChilling()) {
                if (this.chillingForTicks > 0) {
                    this.chillingForTicks--;

                } else {
                    this.setChilling(false, 0);
                }
            }

            if (this.mode != CatMode.WANDER) {
                if (this.isResting()) {
                    this.setResting(false, 0);
                }
                if (this.isChilling()) {
                    this.setChilling(false, 0);
                }
            }
        }

        /**
         * If a cat is following and its distance to the owner is bigger than 25, then find a valid position under certain conditions, and teleport to it.
         */
        if (this.tickCount % 20 != 0) return;
        if (mode == CatMode.FOLLOW && this.isTame()) {
            LivingEntity owner = this.getOwner();

            if (owner != null) {
                double dist = this.distanceTo(owner);

                if (dist > 25 && this.getOwner().onGround()) {
                    BlockPos ownerPos = owner.blockPosition();

                    for (int dx = -2; dx <= 2; dx++) {
                        for (int dz = -2; dz <= 2; dz++) {
                            BlockPos tpPos = ownerPos.offset(dx, 0, dz);
                            BlockPos below = tpPos.below();
                            BlockPos above = tpPos.above();

                            BlockState floor = level().getBlockState(below);
                            BlockState blockAt = level().getBlockState(tpPos);
                            BlockState blockAbove = level().getBlockState(above);

                            AABB targetBox = this.getBoundingBox().move(tpPos.getX() + 0.5 - this.getX(), tpPos.getY() - this.getY(), tpPos.getZ() + 0.5 - this.getZ());

                            boolean solidFloor = floor.isSolid();
                            boolean spaceAir = blockAt.isAir() && blockAbove.isAir();
                            boolean noFluid = blockAt.getFluidState().isEmpty() && blockAbove.getFluidState().isEmpty();
                            boolean notLeaves = !blockAt.is(BlockTags.LEAVES);
                            boolean noCollision = level().noCollision(this, targetBox);

                            if (solidFloor && spaceAir && noFluid && notLeaves && noCollision) {
                                this.teleportTo(tpPos.getX() + 0.5, tpPos.getY(), tpPos.getZ() + 0.5);
                                this.getNavigation().stop();
                                return;
                            }
                        }
                    }
                }
            }
        }

    }

    public void applyBabyAttributes() {
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(15.0);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(2.0);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25);

    }

    public void applyAppAttributes() {
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(27.0);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(3.0);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.3);

        this.setHealth(this.getMaxHealth());
    }

    public void applyAdultAttributes() {
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(40.0);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(4.0);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.33);

    }

    /**
     * It can't attack other animals tamed by their own owner
     */
    @Override
    public boolean canAttack(LivingEntity target) {
        if (this.mode == CatMode.SIT) return false;
        if (this.level().getDifficulty() == Difficulty.PEACEFUL) return false;

        if (target instanceof Player player) {
            if (player instanceof ServerPlayer sPlayer) {
                UUID clanID = sPlayer.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                        .map(WCEPlayerData::getCurrentClanUUID).orElse(ClanData.EMPTY_UUID);

                if (this.getClanUUID().equals(clanID)) {
                    return false;
                }
            }
        }
        if (target instanceof TamableAnimal tam && tam.isTame()) {
            if (tam instanceof WCatEntity cat) {
                if (cat.getClanUUID().equals(this.getClanUUID())) return false;
            }

            LivingEntity myOwner = this.getOwner();
            UUID thisOwnerUUID = myOwner != null ? myOwner.getUUID() : null;
            UUID targetOwner = tam.getOwnerUUID();

            if (targetOwner != null && thisOwnerUUID != null && targetOwner.equals(thisOwnerUUID)) {
                return false;
            }
        }

        if (target instanceof EagleEntity tam && tam.isTame()) {

            LivingEntity myOwner = this.getOwner();
            UUID thisOwnerUUID = myOwner != null ? myOwner.getUUID() : null;
            UUID targetOwner = tam.getOwnerUUID();

            if (targetOwner != null && targetOwner.equals(thisOwnerUUID)) {
                return false;
            }
        }

        return super.canAttack(target);
    }

    /**
     * It is allied to any other animals tamed by their own owner
     */
    @Override
    public boolean isAlliedTo(Entity other) {
        if (other instanceof Player player) {
            if (player instanceof ServerPlayer sPlayer) {
                UUID clanID = sPlayer.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                        .map(WCEPlayerData::getCurrentClanUUID).orElse(ClanData.EMPTY_UUID);

                if (this.getClanUUID().equals(clanID)) {
                    return true;
                }
            }
        }
        if (other instanceof TamableAnimal tam && tam.isTame()) {
            if (tam instanceof WCatEntity cat) {
                if (cat.getClanUUID().equals(this.getClanUUID())) return true;
            }

            LivingEntity myOwner = this.getOwner();
            UUID thisOwnerUUID = myOwner != null ? myOwner.getUUID() : null;
            UUID targetOwner = tam.getOwnerUUID();

            if (targetOwner != null && thisOwnerUUID != null && targetOwner.equals(thisOwnerUUID)) {
                return true;
            }
        }

        if (other instanceof EagleEntity tam && tam.isTame()) {

            LivingEntity myOwner = this.getOwner();
            UUID thisOwnerUUID = myOwner != null ? myOwner.getUUID() : null;
            UUID targetOwner = tam.getOwnerUUID();

            if (targetOwner != null && targetOwner.equals(thisOwnerUUID)) {
                return true;
            }
        }
        return super.isAlliedTo(other);
    }


    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        ListTag list = new ListTag();

        if (friendshipMap != null){
            for (var entry : friendshipMap.entrySet()) {
                UUID player = entry.getKey();
                Integer level = entry.getValue();

                if (player != null && level != null) {
                    CompoundTag t = new CompoundTag();
                    t.putUUID("Player", player);
                    t.putInt("Level", level);
                    list.add(t);
                }
            }
        }

        tag.put("Friendships", list);


        if (homePosition != null) {
            tag.put("HomePos", NbtUtils.writeBlockPos(homePosition));
        }

        if (!this.entityData.get(HEAD_ARMOR).isEmpty()) {
            tag.put("HeadArmor", this.entityData.get(HEAD_ARMOR).save(new CompoundTag()));
        }
        if (!this.entityData.get(CHEST_ARMOR).isEmpty()) {
            tag.put("ChestArmor", this.entityData.get(CHEST_ARMOR).save(new CompoundTag()));
        }
        if (!this.entityData.get(LEGS_ARMOR).isEmpty()) {
            tag.put("LegsArmor", this.entityData.get(LEGS_ARMOR).save(new CompoundTag()));
        }
        if (!this.entityData.get(FEET_ARMOR).isEmpty()) {
            tag.put("FeetArmor", this.entityData.get(FEET_ARMOR).save(new CompoundTag()));
        }

        var personality = this.getPersonality();
        tag.putInt("Personality", personality == null ? 0 : personality.ordinal());

        if (this.getClan() != null) {
            tag.putString("Clan", Component.Serializer.toJson(this.getClan()));
        }
        tag.putInt("InteractionCooldown", this.getInteractionCooldown());
        tag.putInt("KittingInteractCooldown", this.getKittingInteractCooldown());


        if (this.mode == null) {
            this.mode = CatMode.WANDER;
        }
        tag.putInt("WCatMode", this.mode.ordinal());
        tag.putInt("Variant", this.getVariant());
        tag.putInt("KittingTicks", this.getKittingTicks());
        if (this.getRank() != null){
            tag.putInt("Rank", this.getRank().ordinal());
        }
        tag.putBoolean("kitBorn", kitBorn);
        tag.putBoolean("AppScale", this.isAppScale());
        if (inventory != null){
            tag.put("Inventory", inventory.createTag());
        }

        tag.putBoolean("ReturningHome", this.returnHomeFlag);

        tag.putBoolean("IsAnImage", this.isAnImage());

        tag.putInt("SittingForTicks", this.sittingForTicks);


        if (this.getMate() != null) {
            tag.putString("Mate", Component.Serializer.toJson(this.getMate()));
        }
        if (this.getPrefix() != null) {
            tag.putString("Prefix", Component.Serializer.toJson(this.getPrefix()));
        }

        if (this.getMother() != null) {
            tag.putString("Mother", Component.Serializer.toJson(this.getMother()));
        }
        if (this.getFather() != null) {
            tag.putString("Father", Component.Serializer.toJson(this.getFather()));
        }

        tag.putBoolean("ExpectingKits", this.isExpectingKits());
        tag.putInt("Gender", this.getGender());


        if (wanderCenter != null) {
            tag.putInt("WanderX", wanderCenter.getX());
            tag.putInt("WanderY", wanderCenter.getY());
            tag.putInt("WanderZ", wanderCenter.getZ());
            tag.putBoolean("HasWanderCenter", true);
        } else {
            tag.putBoolean("HasWanderCenter", false);
        }

        CompoundTag family = new CompoundTag();
        if (motherUUID != null)
            family.putUUID("Mother", motherUUID);
        if (fatherUUID != null)
            family.putUUID("Father", fatherUUID);
        if (mateUUID != null)
            family.putUUID("Mate", mateUUID);
        family.putInt("Generation", generation);
        tag.put("Family", family);


        tag.putBoolean("ForbidFG", this.isForbiddingFutureGensFromMatingPlayer());
        tag.putBoolean("Forbidden", this.isForbiddenFromMatingPlayer());
        if (this.getForbiddenPlayer() != null) {
            tag.putUUID("ForbiddenP", this.getForbiddenPlayer());
        }

        if (this.getClanUUID() != null){
            tag.putUUID("ClanUUID", this.getClanUUID());
        }

        this.getGeneticsModule().saveGeneticsNBT(tag);
        if (this.storedFatherGenetics != null) {
            CompoundTag fatherTag = new CompoundTag();
            this.getGeneticsModule().saveFatherGeneticsToNBT(fatherTag, this.storedFatherGenetics);
            tag.put("StoredFatherGenetics", fatherTag);
        }

        if (this.getPlayerBoundUuid() != null){
            tag.putUUID("PlayerBoundUUID", this.getPlayerBoundUuid());
        }
        tag.putInt("AnimIndex", this.entityData.get(ANIM_INDEX));
        tag.putBoolean("ShowMorphName", this.entityData.get(SHOW_MORPH_NAME));

        tag.putFloat("AgeMoons", this.entityData.get(AGE_SYNC));


        CompoundTag patrolData = this.savePatrolDataNBT();
        tag.put("PatrolData", patrolData);

        this.writeDiseasesNBT(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);


        if (tag.contains("Family")) {
            CompoundTag family = tag.getCompound("Family");
            if (family.hasUUID("Mother"))
                motherUUID = family.getUUID("Mother");
            if (family.hasUUID("Father"))
                fatherUUID = family.getUUID("Father");
            if (family.hasUUID("Mate"))
                mateUUID = family.getUUID("Mate");
            generation = family.getInt("Generation");
        }


        friendshipMap.clear();

        if (tag.contains("HomePos")) {
            this.homePosition = NbtUtils.readBlockPos(tag.getCompound("HomePos"));
        } else {
            this.homePosition = BlockPos.ZERO;
        }

        ListTag list = tag.getList("Friendships", Tag.TAG_COMPOUND);

        for (int i = 0; i < list.size(); i++) {
            CompoundTag t = list.getCompound(i);
            UUID playerId = t.getUUID("Player");
            int level = t.getInt("Level");
            friendshipMap.put(playerId, level);
        }

        if (tag.contains("HeadArmor")) {
            this.entityData.set(HEAD_ARMOR, ItemStack.of(tag.getCompound("HeadArmor")));
        }
        if (tag.contains("ChestArmor")) {
            this.entityData.set(CHEST_ARMOR, ItemStack.of(tag.getCompound("ChestArmor")));
        }
        if (tag.contains("LegsArmor")) {
            this.entityData.set(LEGS_ARMOR, ItemStack.of(tag.getCompound("LegsArmor")));
        }
        if (tag.contains("FeetArmor")) {
            this.entityData.set(FEET_ARMOR, ItemStack.of(tag.getCompound("FeetArmor")));
        }

        if (tag.contains("Rank")) {
            int value = tag.getInt("Rank");
            this.setRank(values()[value]);
        }

        if (tag.contains("Personality")) {
            int value = tag.getInt("Personality");
            this.setPersonality(Personality.values()[value]);
        }

        inventory.fromTag(tag.getList("Inventory", Tag.TAG_COMPOUND));


        if (tag.contains("kitBorn")) {
            kitBorn = tag.getBoolean("kitBorn");
        }
        if (tag.contains("AppScale")) {
            this.setAppScale(tag.getBoolean("AppScale"));
        }
        if (tag.contains("KittingTicks")) {
            this.setKittingTicks(tag.getInt("KittingTicks"));
        }

        if (tag.contains("InteractionCooldown")) {
            this.setInteractionCooldown(tag.getInt("InteractionCooldown"));
        }

        if (tag.contains("KittingInteractCooldown")) {
            this.setKittingInteractCooldown(tag.getInt("KittingInteractCooldown"));
        }


        if (tag.contains("Gender")) {
            this.setGender(tag.getInt("Gender"));
        }

        if (tag.contains("Mate")) {
            Component mate = Component.Serializer.fromJson(tag.getString("Mate"));
            this.setMate(mate);
        }

        if (tag.contains("Mother")) {
            Component name = Component.Serializer.fromJson(tag.getString("Mother"));
            this.setMother(name);
        }

        if (tag.contains("Father")) {
            Component name = Component.Serializer.fromJson(tag.getString("Father"));
            this.setFather(name);
        }

        if (tag.contains("Clan")) {
            Component clan = Component.Serializer.fromJson(tag.getString("Clan"));
            this.setClan(clan);
        }

        if (tag.contains("Prefix")) {
            Component prefix = Component.Serializer.fromJson(tag.getString("Prefix"));
            this.setPrefix(prefix);
        }

        if (tag.contains("ExpectingKits")) {
            this.setExpectingKits(tag.getBoolean("ExpectingKits"));
        }

        if (tag.contains("WCatMode")) {
            int modeIndex = tag.getInt("WCatMode");
            this.mode = CatMode.values()[modeIndex] != null ? CatMode.values()[modeIndex] : CatMode.WANDER;
        }


        if (tag.contains("ReturningHome")) {
            this.returnHomeFlag = tag.getBoolean("ReturningHome");
        }


        if (tag.contains("SittingForTicks")) {
            this.sittingForTicks = tag.getInt("SittingForTicks");
        }

        if (tag.contains("Variant")) {
            this.setVariant(tag.getInt("Variant"));
        }

        if (tag.contains("ForbidFG")) {
            this.setForbiddingFutureGensFromMatingPlayer(tag.getBoolean("ForbidFG"));
        }
        if (tag.contains("Forbidden")) {
            this.setForbiddenFromMatingPlayer(tag.getBoolean("Forbidden"));
        }
        if (tag.hasUUID("ForbiddenP")) {
            this.setForbiddenPlayer(tag.getUUID("ForbiddenP"));
        } else {
            this.setForbiddenPlayer(null);
        }

        if (tag.contains("IsAnImage")) {
            this.setAnImage(tag.getBoolean("IsAnImage"));
        }


        if (tag.getBoolean("HasWanderCenter")) {
            int x = tag.getInt("WanderX");
            int y = tag.getInt("WanderY");
            int z = tag.getInt("WanderZ");
            this.wanderCenter = new BlockPos(x, y, z);
        } else {
            this.wanderCenter = null;
        }

        if (tag.contains("ClanUUID")) {
            this.setClanUUID(tag.getUUID("ClanUUID"));
        } else {
            this.setClanUUID(ClanData.EMPTY_UUID);
        }


        this.getGeneticsModule().loadGeneticsNBT(tag);

        if (tag.contains("StoredFatherGenetics")) {

            CompoundTag fatherTag = tag.getCompound("StoredFatherGenetics");

            this.storedFatherGenetics = new WCGenetics(
                    fatherTag.getString("Bobtail"),
                    fatherTag.getString("ChestFur"),
                    fatherTag.getString("BellyFur"),
                    fatherTag.getString("LegsFur"),
                    fatherTag.getString("HeadFur"),
                    fatherTag.getString("CheekFur"),
                    fatherTag.getString("TailFur"),
                    fatherTag.getString("BackFur"),

                    fatherTag.getString("Base"),
                    fatherTag.getString("Orange"),
                    fatherTag.getString("WhiteRatio"),
                    fatherTag.getString("Albino"),
                    fatherTag.getString("Dilute"),
                    fatherTag.getString("Agouti"),
                    fatherTag.getString("TabbyStripes"),
                    fatherTag.getString("EyesAnomaly"),
                    fatherTag.getString("Chimera"),
                    fatherTag.getString("Silver")
            );
        }

        if (tag.contains("PlayerBoundUUID")) {
            this.setPlayerBoundUuid(tag.getUUID("PlayerBoundUUID"));
        }

        if (tag.contains("AnimIndex")) {
            this.setAnimIndex(tag.getInt("AnimIndex"));
        }

        if (tag.contains("ShowMorphName")) {
            this.setShowMorphName(tag.getBoolean("ShowMorphName"));
        }

        if (tag.contains("AgeMoons")) {
            this.entityData.set(AGE_SYNC, tag.getFloat("AgeMoons"));
        }

        this.loadPatrolDataNBT(tag);

        this.loadDiseasesNBT(tag);

    }

    /**
     * This is in charge of animations.
     * Again, any questions about this just ask me personally
     */
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>
                (this, "controller", 0, this::predicate));
        controllers.add(new AnimationController<>
                (this, "attackController", 0, this::attackPredicate));
        controllers.add(new AnimationController<>
                (this, "playerController", 0, this::playerPredicate));
        controllers.add(new AnimationController<>
                (this, "blinkController", 0, this::blinkPredicate));

    }


    private <T extends GeoAnimatable> PlayState playerPredicate(AnimationState<T> state) {

        if (this.getPlayerBoundUuid().equals(ClanData.EMPTY_UUID)) {
            return PlayState.CONTINUE;
        }

        int animIndex = this.entityData.get(ANIM_INDEX);

        if (animIndex != -1) {

            state.getController().setTransitionLength(0);

            if (!playerAnimPlayed) {
                if (animIndex == -3) {
                    state.getController().setAnimation(RawAnimation.begin()
                            .then("animation.wcat.start_stand_premium", Animation.LoopType.PLAY_ONCE)
                            .then("animation.wcat.stand_idle_premium", Animation.LoopType.LOOP));
                    playerAnimPlayed = true;
                } else if (animIndex == -2) {
                    state.getController().setAnimation(RawAnimation.begin()
                            .then("animation.wcat.silly", Animation.LoopType.PLAY_ONCE));
                    playerAnimPlayed = true;
                } else if (animIndex == 1) {
                    state.getController().setAnimation(RawAnimation.begin()
                            .then("animation.wcat.groom", Animation.LoopType.PLAY_ONCE));
                    playerAnimPlayed = true;
                } else if (animIndex == 2) {
                    state.getController().setAnimation(RawAnimation.begin()
                            .then("animation.wcat.stretch", Animation.LoopType.PLAY_ONCE));
                    playerAnimPlayed = true;
                } else if (animIndex == 3) {
                    state.getController().setAnimation(RawAnimation.begin()
                            .then("animation.wcat.scratch", Animation.LoopType.PLAY_ONCE));
                    playerAnimPlayed = true;
                } else if (animIndex == 4) {
                    state.getController().setAnimation(RawAnimation.begin()
                            .then("animation.wcat.attack", Animation.LoopType.PLAY_ONCE));
                    playerAnimPlayed = true;
                } else if (animIndex == 5) {
                    state.getController().setAnimation(RawAnimation.begin()
                            .then("animation.wcat.standstand", Animation.LoopType.PLAY_ONCE)
                            .then("animation.wcat.standidle", Animation.LoopType.LOOP));
                    playerAnimPlayed = true;
                } else if (animIndex == 6) {
                    state.getController().setAnimation(RawAnimation.begin()
                            .then("animation.wcat.sitlay", Animation.LoopType.PLAY_ONCE)
                            .then("animation.wcat.layidle", Animation.LoopType.LOOP));
                    playerAnimPlayed = true;
                } else if (animIndex == 7) {
                    state.getController().setAnimation(RawAnimation.begin()
                            .then("animation.wcat.start_sit", Animation.LoopType.PLAY_ONCE)
                            .then("animation.wcat.sit_idle", Animation.LoopType.LOOP));
                    playerAnimPlayed = true;
                } else if (animIndex == 8) {
                    state.getController().setAnimation(RawAnimation.begin()
                            .then("animation.wcat.start_loaf", Animation.LoopType.PLAY_ONCE)
                            .then("animation.wcat.loaf_idle", Animation.LoopType.LOOP));
                    playerAnimPlayed = true;
                } else if (animIndex == 9) {
                    state.getController().setAnimation(RawAnimation.begin()
                            .then("animation.wcat.laysleep", Animation.LoopType.PLAY_ONCE)
                            .then("animation.wcat.sleep_idle", Animation.LoopType.LOOP));
                    playerAnimPlayed = true;
                } else if (animIndex == 10) {
                    state.getController().setAnimation(RawAnimation.begin()
                            .then("animation.wcat.fall_death", Animation.LoopType.PLAY_ONCE)
                            .then("animation.wcat.death_idle", Animation.LoopType.LOOP));
                    playerAnimPlayed = true;
                } else if (animIndex == 11) {
                    state.getController().setAnimation(RawAnimation.begin()
                            .then("animation.wcat.roll", Animation.LoopType.PLAY_ONCE));
                    playerAnimPlayed = true;
                } else if (animIndex == 12) {
                    state.getController().setAnimation(RawAnimation.begin()
                            .then("animation.wcat.scared", Animation.LoopType.PLAY_ONCE)
                            .then("animation.wcat.scared_idle", Animation.LoopType.LOOP));
                    playerAnimPlayed = true;
                } else if (animIndex == 13) {
                    state.getController().setAnimation(RawAnimation.begin()
                            .then("animation.wcat.start_drop", Animation.LoopType.PLAY_ONCE)
                            .then("animation.wcat.drop_idle", Animation.LoopType.LOOP));
                    playerAnimPlayed = true;
                }

            }

            if (playerAnimPlayed && (state.getController().hasAnimationFinished()|| state.isMoving()) ) {
                state.getController().setTransitionLength(3);

                state.getController().setAnimation(RawAnimation.begin()
                        .then("animation.wcat.empty", Animation.LoopType.PLAY_ONCE));
                playerAnimPlayed = false;
                this.setAnimIndex(animIndex);

                return PlayState.CONTINUE;
            }
        } else if (playerAnimPlayed && (state.getController().hasAnimationFinished() || state.isMoving())) {
            state.getController().setTransitionLength(3);

            state.getController().setAnimation(RawAnimation.begin()
                    .then("animation.wcat.empty", Animation.LoopType.PLAY_ONCE));
            playerAnimPlayed = false;
        }


        return PlayState.CONTINUE;
    }

    public void setAnimIndex (int value) {
        this.entityData.set(ANIM_INDEX, value);
    }


    private <T extends GeoAnimatable> PlayState attackPredicate(AnimationState<T> state) {
        var controller = state.getController();
        if (this.isAttacking() && attackAnimationTimeout <= 0) {
            attackAnimationTimeout = 100;
            controller.setAnimation(RawAnimation.begin()
                    .then("animation.wcat.attack", Animation.LoopType.PLAY_ONCE));
            controller.forceAnimationReset();
            return PlayState.CONTINUE;
        } else {
            --this.attackAnimationTimeout;
        }

        if (!this.isAttacking()) {
            if (this.swinging && this.attackAnimationTimeout <= 0) {
                controller.setAnimation(RawAnimation.begin()
                        .then("animation.wcat.swing", Animation.LoopType.LOOP));
            } else {
                controller.stop();
                return PlayState.STOP;
            }
            this.swinging = false;
        }
        return PlayState.CONTINUE;
    }

    private int nextIdleAnimTick = 0;

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {
        if (this.isAFlyingImage()) {
            tAnimationState.getController().setAnimation(RawAnimation.begin().
                    then("animation.wcat.falling", Animation.LoopType.LOOP));
            tAnimationState.getController().setAnimationSpeed(1f);
            return PlayState.CONTINUE;
        }

        if (this.isAnImage()) return PlayState.STOP;

        LivingEntity cat = (LivingEntity) tAnimationState.getAnimatable();
        double speed = cat.getDeltaMovement().length();
        float animSpeed = (float) (speed * 6.0f);
        animSpeed = Mth.clamp(animSpeed * animSpeed, 0.2f, 1.5f);

        tAnimationState.getController().setTransitionLength(3);

        if (!this.getPlayerBoundUuid().equals(ClanData.EMPTY_UUID)) {
            Player player = this.level().getPlayerByUUID(this.getPlayerBoundUuid());
            if (player != null) {
                if (player instanceof ClimbDataAccessor data) {
                    if (data.wce$isClimbing() || player.onClimbable()){
                        if (this.getDeltaMovement().y > 0){
                            tAnimationState.getController().setAnimation(RawAnimation.begin()
                                    .then("animation.wcat.climb", Animation.LoopType.LOOP));
                            tAnimationState.getController().setAnimationSpeed(1f);
                        } else {
                            tAnimationState.getController().setAnimation(RawAnimation.begin()
                                    .then("animation.wcat.climb_idle", Animation.LoopType.LOOP));
                            tAnimationState.getController().setAnimationSpeed(1f);
                        }
                        return PlayState.CONTINUE;

                    }
                }
            }
        }

        if (this.isResting() || this.entityData.get(SITTING_INDEX) == 3) {
            if (this.isWakingUp()) {
                tAnimationState.getController().setAnimation(RawAnimation.begin()
                        .then("animation.wcat.stretch", Animation.LoopType.PLAY_ONCE));
                tAnimationState.getController().setAnimationSpeed(0.8f);
            } else {
                tAnimationState.getController().setAnimation(RawAnimation.begin()
                        .then("animation.wcat.laysleep", Animation.LoopType.PLAY_ONCE)
                        .then("animation.wcat.sleep_idle", Animation.LoopType.LOOP));
                tAnimationState.getController().setAnimationSpeed(0.8f);
            }

            return PlayState.CONTINUE;
        }

        if (this.entityData.get(SITTING_INDEX) != 0) {
            if (this.entityData.get(SITTING_INDEX) == 1) {
                tAnimationState.getController().setAnimation(RawAnimation.begin()
                        .then("animation.wcat.start_sit", Animation.LoopType.PLAY_ONCE)
                        .then("animation.wcat.sit_idle", Animation.LoopType.LOOP));
                tAnimationState.getController().setAnimationSpeed(1f);

                return PlayState.CONTINUE;
            } else if (this.entityData.get(SITTING_INDEX) == 2) {
                if (this.getId() % 2 == 0) {
                    tAnimationState.getController().setAnimation(RawAnimation.begin()
                            .then("animation.wcat.sitlay", Animation.LoopType.PLAY_ONCE)
                            .then("animation.wcat.layidle", Animation.LoopType.LOOP));
                    tAnimationState.getController().setAnimationSpeed(1f);

                } else {
                    tAnimationState.getController().setAnimation(RawAnimation.begin()
                            .then("animation.wcat.start_loaf", Animation.LoopType.PLAY_ONCE)
                            .then("animation.wcat.loaf_idle", Animation.LoopType.LOOP));
                    tAnimationState.getController().setAnimationSpeed(1f);

                }

                return PlayState.CONTINUE;
            }
        }

        if (this.isChilling()) {
            if (this.entityData.get(CHILLING_INDEX) == 1) {
                tAnimationState.getController().setAnimation(RawAnimation.begin()
                        .then("animation.wcat.start_sit", Animation.LoopType.PLAY_ONCE)
                        .then("animation.wcat.sit_idle", Animation.LoopType.LOOP));
                tAnimationState.getController().setAnimationSpeed(1f);

                return PlayState.CONTINUE;
            } else if (this.entityData.get(CHILLING_INDEX) == 2) {
                tAnimationState.getController().setAnimation(RawAnimation.begin()
                        .then("animation.wcat.sitlay", Animation.LoopType.PLAY_ONCE)
                        .then("animation.wcat.layidle", Animation.LoopType.LOOP));
                tAnimationState.getController().setAnimationSpeed(1f);

                return PlayState.CONTINUE;
            } else if (this.entityData.get(CHILLING_INDEX) == 3){
                tAnimationState.getController().setAnimation(RawAnimation.begin()
                        .then("animation.wcat.start_loaf", Animation.LoopType.PLAY_ONCE)
                        .then("animation.wcat.loaf_idle", Animation.LoopType.LOOP));
                tAnimationState.getController().setAnimationSpeed(1f);

                return PlayState.CONTINUE;
            }
        }



        if (this.isInWater()) {
            if (this.isSwimming()) {
                tAnimationState.getController().setAnimation(RawAnimation.begin().
                        then("animation.wcat.swim", Animation.LoopType.LOOP));
                tAnimationState.getController().setAnimationSpeed(1f);
            } else {
                tAnimationState.getController().setAnimation(RawAnimation.begin().
                        then("animation.wcat.inwater", Animation.LoopType.LOOP));
                tAnimationState.getController().setAnimationSpeed(1.4f);
            }
            return PlayState.CONTINUE;
        }

        if (!this.onGround() && !this.isInWater()) {
            tAnimationState.getController().setAnimation(RawAnimation.begin().
                    then("animation.wcat.falling", Animation.LoopType.LOOP));
            animPlayed = false;
            tAnimationState.getController().setAnimationSpeed(1.f);
            return PlayState.CONTINUE;
        }

        if ((tAnimationState.isMoving()) && !this.isCrouching()) {
            tAnimationState.getController().setTransitionLength(0);

            if ((speed > 0.2039) && !this.isInWater()) {
                tAnimationState.getController().setAnimation(RawAnimation.begin().
                        then("animation.wcat.sprint" + this.getIdlePose(), Animation.LoopType.LOOP));

                tAnimationState.getController().setAnimationSpeed(0.185 * Math.exp(9.91 * speed));

            } else {
                if (this.isBrokenPaw()) {
                    tAnimationState.getController().setAnimation(RawAnimation.begin().
                            then("animation.wcat.walk_limp", Animation.LoopType.LOOP));
                } else {
                    tAnimationState.getController().setAnimation(RawAnimation.begin().
                            then("animation.wcat.walk" + this.getIdlePose(), Animation.LoopType.LOOP));
                }
                tAnimationState.getController().setAnimationSpeed(animSpeed);

            }

            animPlayed = false;
            return PlayState.CONTINUE;
        }


        if (this.tickCount >= nextIdleAnimTick
                && !this.isBeingCarried
                && !this.isAnImage() && this.getPlayerBoundUuid().equals(ClanData.EMPTY_UUID)
                && !this.isResting()) {

            int rand = this.random.nextInt(4);

            if (rand == 0 && !animPlayed) {

                tAnimationState.getController().setAnimation(RawAnimation.begin()
                        .then("animation.wcat.groom", Animation.LoopType.PLAY_ONCE));
                animPlayed = true;

            } else if (rand == 1 && !animPlayed) {

                tAnimationState.getController().setAnimation(RawAnimation.begin()
                        .then("animation.wcat.scratch", Animation.LoopType.PLAY_ONCE));
                animPlayed = true;

            } else if (rand == 2 && !animPlayed) {

                tAnimationState.getController().setAnimation(RawAnimation.begin()
                        .then("animation.wcat.stretch", Animation.LoopType.PLAY_ONCE));
                animPlayed = true;
            } else if (rand == 3 && !animPlayed) {

                tAnimationState.getController().setAnimation(RawAnimation.begin()
                        .then("animation.wcat.roll", Animation.LoopType.PLAY_ONCE));
                animPlayed = true;
            }
            tAnimationState.getController().setAnimationSpeed(1f);
            nextIdleAnimTick = this.tickCount + 600 + this.random.nextInt(800);

            return PlayState.CONTINUE;

        }
        if (animPlayed && tAnimationState.getController().hasAnimationFinished()) {

            if (this.isBrokenPaw()) {
                tAnimationState.getController().setAnimation(RawAnimation.begin().
                        then("animation.wcat.idle_limp", Animation.LoopType.LOOP));
            } else {
                tAnimationState.getController().setAnimation(RawAnimation.begin()
                        .then("animation.wcat.idle" + this.entityData.get(IDLE_POSE), Animation.LoopType.LOOP));
            }

            tAnimationState.getController().setAnimationSpeed(1f);
            animPlayed = false;

            return PlayState.CONTINUE;
        }

        if (this.isCrouching()) {
            tAnimationState.getController().setTransitionLength(0);

            if ((tAnimationState.isMoving()) && this.isCrouching()) {
                tAnimationState.getController().setAnimation(RawAnimation.begin().
                        then("animation.wcat.crouchingwalk", Animation.LoopType.LOOP));
                tAnimationState.getController().setAnimationSpeed(1f);
            } else {
                tAnimationState.getController().setAnimation(RawAnimation.begin().
                        then("animation.wcat.crouchingidle", Animation.LoopType.LOOP));
                tAnimationState.getController().setAnimationSpeed(1f);
            }


            animPlayed = false;
        } else if (!animPlayed) {

            if (this.isBrokenPaw()) {
                tAnimationState.getController().setAnimation(RawAnimation.begin().
                        then("animation.wcat.idle_limp", Animation.LoopType.LOOP));
            } else {
                tAnimationState.getController().setAnimation(RawAnimation.begin().
                        then("animation.wcat.idle" + this.entityData.get(IDLE_POSE), Animation.LoopType.LOOP));
            }
            tAnimationState.getController().setAnimationSpeed(1f);
        }

        return PlayState.CONTINUE;

    }

    private <T extends GeoAnimatable> PlayState blinkPredicate(AnimationState<T> state) {

        var controller = state.getController();

        if (this.isAnImage()) return PlayState.CONTINUE;

        if (!this.getPlayerBoundUuid().equals(ClanData.EMPTY_UUID)) return PlayState.CONTINUE;

        if ((this.tickCount + this.getId()*2) % 100 == 0 && this.getRandom().nextFloat() < 0.80
                && (!this.isResting() && this.entityData.get(SITTING_INDEX) != 3)) {
            controller.setAnimation(RawAnimation.begin()
                            .then("animation.wcat.blink", Animation.LoopType.PLAY_ONCE));

            controller.forceAnimationReset();
        }

        return PlayState.CONTINUE;
    }


    public void setIdlePose(int i) {
        this.entityData.set(IDLE_POSE, Math.min(i, WCGenetics.Constants.MAX_IDLE_POSES -1));
    }

    public int getIdlePose() {
        return this.entityData.get(IDLE_POSE);
    }


    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }


    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return List.of(
                getItemBySlot(EquipmentSlot.HEAD),
                getItemBySlot(EquipmentSlot.CHEST),
                getItemBySlot(EquipmentSlot.LEGS),
                getItemBySlot(EquipmentSlot.FEET)
        );
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> entityData.get(HEAD_ARMOR);
            case CHEST -> entityData.get(CHEST_ARMOR);
            case LEGS -> entityData.get(LEGS_ARMOR);
            case FEET -> entityData.get(FEET_ARMOR);
            default -> super.getItemBySlot(slot);
        };
    }


    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) {
        super.setItemSlot(slot, stack);
        switch (slot) {
            case HEAD -> entityData.set(HEAD_ARMOR, stack.copy());
            case CHEST -> entityData.set(CHEST_ARMOR, stack.copy());
            case LEGS -> entityData.set(LEGS_ARMOR, stack.copy());
            case FEET -> entityData.set(FEET_ARMOR, stack.copy());
        }
    }


    public void setNameColor(Rank rank) {
        if (!WCEServerConfig.SERVER.COLORED_NAMES.get()) {
            TextColor none = TextColor.fromRgb(0xFFFFFF);
            Component actualName = this.getCustomName();
            if (actualName != null) {
                this.setCustomName(
                        Component.literal(actualName.getString())
                                .withStyle(style -> style.withColor(none))
                );

                this.setCustomNameVisible(true);
            }

            return;
        }


        TextColor none = TextColor.fromRgb(0xFFFFFF);
        TextColor kit = TextColor.fromRgb(0x42fcb5);
        TextColor apprentice = TextColor.fromRgb(0xeefc90);
        TextColor warrior = TextColor.fromRgb(0xffac3b);
        TextColor medicine = TextColor.fromRgb(0x56bdfc);
        TextColor deputy = TextColor.fromRgb(0xfc6951);

        TextColor colorToUse;
        switch (rank) {
            case KIT -> colorToUse = kit;
            case APPRENTICE -> colorToUse = apprentice;
            case WARRIOR -> colorToUse = warrior;
            case MEDICINE -> colorToUse = medicine;
            case DEPUTY -> colorToUse = deputy;
            default -> colorToUse = none;
        }

        Component actualName = this.getCustomName();
        if (actualName != null) {
            this.setCustomName(
                    Component.literal(actualName.getString())
                            .withStyle(style -> style.withColor(colorToUse))
            );

            this.setCustomNameVisible(true);
        }

    }

    /**
     * Sends a different message depending on the cat's mode.
     */
    public void sendModeMessage(Player player) {
        this.sendModeMessage(player, mode);
    }

    public void sendModeMessage(Player player, CatMode cMode) {
        String name = this.getName().getString();
        switch (cMode) {
            case SIT:
                player.displayClientMessage(Component.translatable("wcat.stay_message", name), true);
                break;
            case FOLLOW:
                player.displayClientMessage(Component.translatable("wcat.follow_message", name), true);
                break;
            case WANDER:
                player.displayClientMessage(Component.translatable("wcat.wander_message", name), true);
                break;
        }
    }

    /**
     * Sends a different message depending on the cat's rank
     */
    public void sendRankMessage(Player player) {
        Rank r = this.getRank();
        String name = this.getName().getString();

        switch (r) {
            case NONE -> player.displayClientMessage(Component.translatable("wcat.rank_loner",name), true);
            case KIT -> player.displayClientMessage(Component.translatable("wcat.rank_kit",name), true);
            case APPRENTICE -> player.displayClientMessage(Component.translatable("wcat.rank_app",name), true);
            case WARRIOR -> player.displayClientMessage(Component.translatable("wcat.rank_warrior",name), true);
            case MEDICINE -> player.displayClientMessage(Component.translatable("wcat.rank_medicine",name), true);
            case DEPUTY -> player.displayClientMessage(Component.translatable("wcat.rank_deputy",name), true);
        }
    }

    /**
     * This is called when the she-cat is kitting.
     * First, set a random number which will be the ammount of kits, then for every kit that will be born:
     * Reset love so it doesn't spawn kits infinitely.
     * Create a Wild Cat instance.
     * Set its position, age, set it tamed, set its rank, etc etc.
     * Then set its name.
     * If the owner is a player, send a message announcing that the kit was born.
     * Then set its variant, wander center, attributes
     * And finally: Spawn the kit
     * Tadaaaaa
     */
    private void Kitting() {
        String lastPrefix = "";
        if (!(this.level() instanceof ServerLevel server)) return;
        this.setExpectingKits(false);
        LivingEntity owner = this.getOwner();

        int litterSize = 1 + this.random.nextInt(3);

        Component message = Component.translatable("wcat.has_brought_kits",
                        (this.hasCustomName() ? this.getCustomName().copy() : Component.translatable("generic.a_she_cat")),
                        litterSize);

        this.registerClanLog(message);


        for (int i = 0; i < litterSize; i++) {
            this.resetLove();
            WCatEntity kit = ModEntities.WCAT.get().create(server);

            if (kit != null) {

                if (this.storedFatherGenetics != null) {

                    WCGenetics motherGenetics = this.getGeneticsModule().getGenetics();
                    if (WCGenetics.Chimerism.isChimera(this.entityData.get(CHIMERA_GENE))) {
                        if (this.getRandom().nextBoolean()) {
                            motherGenetics = this.getGeneticsModule().getChimeraGenetics();
                        }
                    }

                    WCGenetics fatherGenetics = this.storedFatherGenetics;

                    kit.setOnGeneticalSkin(this.isOnGeneticalSkin());

                    kit.getGeneticsModule().inheritGeneticsFromParents(motherGenetics, fatherGenetics);

                } else {
                    kit.getGeneticsModule().initializeGenetics();
                }


                kit.setPos(this.getX(), this.getY(), this.getZ());
                int minutes = WCEServerConfig.SERVER.KIT_GROWTH_MINUTES.get();
                int growingTicks = minutes * 20 * 60;
                kit.setAge(-growingTicks);
                kit.setTame(true);
                kit.setRank(KIT);
                kit.kitBorn = true;
                String finalName = "";
                int randomVariant = this.random.nextInt(maxVariants);
                kit.setVariant(randomVariant);

                if (!kit.hasCustomName()) {
                    int variant = kit.getVariant();
                    String[] prefixSet = getPrefixForVariant();


                    String genderS;
                    if (kit.getGender() == 0) {
                        genderS = " ♂";
                    } else {
                        genderS = " ♀";
                    }

                    int k = kit.random.nextInt(prefixSet.length);

                    /**
                     * This is so that kits born in the same litter dont have the same prefix when they are born.
                     * Since all this can happen in the same tick, then they all might share the same name, that's why this exists.
                     */
                    if (prefixSet[k].equals(lastPrefix)) {
                        k = (int) (kit.random.nextInt(prefixSet.length) / 1.5F);
                    }

                    lastPrefix = prefixSet[k];

                    finalName = prefixSet[k] + "kit" + genderS;
                    kit.setCustomName(Component.literal(finalName));
                    kit.setCustomNameVisible(true);

                    kit.setPrefix(Component.literal(prefixSet[k]));

                }


                if (owner instanceof Player player) {
                    String kitName = finalName;

                    kit.setOwnerUUID(player.getUUID());

                    Component messageKit = Component.translatable("wcat.has_been_born",
                            Component.literal(kitName).withStyle(ChatFormatting.GREEN));

                    owner.sendSystemMessage(messageKit);
                    this.registerClanLog(messageKit);

                    kit.rewardGeneticsAdvancements();

                }

                kit.wanderCenter = this.blockPosition();
                kit.applyBabyAttributes();
                kit.setHealth(kit.getMaxHealth());
                kit.setNameColor(KIT);

                kit.assignRandomPersonality(kit.getRandom());

                kit.setHomePosition(this.getHomePosition());
                kit.setClan(this.getClan());
                kit.setClanUUID(this.getClanUUID());

                LivingEntity father = this.getMateEntity();

                if (father != null) {
                    kit.setFatherUUID(father.getUUID());
                }
                kit.setMotherUUID(this.getUUID());
                if (father instanceof WCatEntity catFather) {
                    int gen = Math.max(this.getGeneration(), father != null ? catFather.getGeneration() : 0) + 1;
                    kit.setGeneration(gen);
                }

                kit.setMother(this.getCustomName());
                kit.setFather(this.getMate());

                kit.entityData.set(MOOD, Mood.CALM.ordinal());

                if (this.isForbiddingFutureGensFromMatingPlayer()) {
                    kit.setForbiddenPlayer(this.getForbiddenPlayer());
                    kit.setForbiddenFromMatingPlayer(true);
                    kit.setForbiddingFutureGensFromMatingPlayer(true);
                }


                server.addFreshEntity(kit);
            }
        }

        this.storedFatherGenetics = null;
    }

    public @Nullable LivingEntity getMateEntity() {

        UUID mateUUID = this.getMateUUID();
        if (mateUUID == null) return null;

        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return null;
        }
        Entity entity = serverLevel.getEntity(mateUUID);
        if (entity instanceof WCatEntity || entity instanceof Player) {
            return ((LivingEntity) entity);
        }

        return null;
    }


    /**
     * Called when the age is zero (when the cat grows from baby to adult)
     */
    private void onGrewUp() {
        Component prefix = this.getPrefix();
        if (prefix != null && this.isTame()) {
            String genderV = this.getGender() == 0 ? " ♂" : " ♀";
            int i = this.random.nextInt(SUFIXES.length);

            String newName = prefix.getString() + SUFIXES[i] + genderV;

            Component message = Component.translatable("wcat.new_name_announcement",
                            prefix.copy().append("paw"),
                            Component.literal(newName).withStyle(ChatFormatting.GOLD));

            Entity owner = this.getOwner();
            if (owner instanceof Player) {
                owner.sendSystemMessage(message);
            }

            this.updateClanCatData();
            this.registerClanLog(message);


            this.updateNest();

            this.setCustomName(Component.literal(newName));
            this.setCustomNameVisible(true);
            this.setNameColor(this.getRank());
            this.setAppScale(false);
            this.level().broadcastEntityEvent(this, (byte) 6);
            this.level().playSound(null, this.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.AMBIENT, 0.8f, 1.2f);

            this.rewardMoonmoon();
        }
    }

    /**
     * Different voice pitch depending on a cat's age and rank
     */
    @Override
    public float getVoicePitch() {
        return this.isBaby() ?
                (this.getRank() == APPRENTICE ?
                        (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.3F
                        :
                        (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.5F)
                :
                (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F;
    }


    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(HEAD_ARMOR, ItemStack.EMPTY);
        this.entityData.define(CHEST_ARMOR, ItemStack.EMPTY);
        this.entityData.define(LEGS_ARMOR, ItemStack.EMPTY);
        this.entityData.define(FEET_ARMOR, ItemStack.EMPTY);

        this.entityData.define(VARIANT, 0);
        this.entityData.define(SCALE, 1.0f);
        this.entityData.define(GENDER, 0);
        this.entityData.define(EXPECTING_KITS, false);
        this.entityData.define(KITTING_TICKS, 0);
        this.entityData.define(MATE, Optional.empty());
        this.entityData.define(PREFIX, Optional.empty());
        this.entityData.define(RANK, 0);
        this.entityData.define(AGE_SYNC, 0.0f);
        this.entityData.define(APP_SCALE, false);
        this.entityData.define(ATTACKING, false);

        this.entityData.define(PERSONALITY, 0);
        this.entityData.define(CLAN, Optional.empty());
        this.entityData.define(FRIENDSHIP_SYNC, 0);
        this.entityData.define(MOOD, Mood.CALM.ordinal());
        this.entityData.define(INTERACTION_COOLDOWN, 0);
        this.entityData.define(KITTING_COOLDOWN, 0);

        this.entityData.define(MOTHER, Optional.empty());
        this.entityData.define(FATHER, Optional.empty());

        this.entityData.define(CLAN_UUID, Optional.empty());
        this.entityData.define(SIZE, 0.0f);
        this.entityData.define(SCARS, 0);
        this.entityData.define(SKIN_COLOR, 9);
        this.entityData.define(IDLE_POSE, 0);


        // GENETICS

        this.entityData.define(CHEST_FUR, "s-s");
        this.entityData.define(BELLY_FUR, "s-s");
        this.entityData.define(LEGS_FUR, "s-s");
        this.entityData.define(HEAD_FUR, "s-s");
        this.entityData.define(CHEEK_FUR, "s-s");
        this.entityData.define(BACK_FUR, "s-s");
        this.entityData.define(BOBTAIL, "B-B");
        this.entityData.define(TAIL_FUR, "s-s");

        this.entityData.define(BASE, "B-b");
        this.entityData.define(ORANGE_BASE, "o-o");
        this.entityData.define(WHITE_RATIO, "w-w");
        this.entityData.define(ALBINO, "C-cs");
        this.entityData.define(DILUTE, "d-d");
        this.entityData.define(AGOUTI, "a-a");
        this.entityData.define(TABBY_STRIPES, "mc-mc");
        this.entityData.define(EYE_COLOR_LEFT, "green");
        this.entityData.define(EYE_COLOR_RIGHT, "green");
        this.entityData.define(EYES_ANOMALY, "H-h");
        this.entityData.define(SILVER, "i-i");

        this.entityData.define(RUFOUSING_VARIANT, 0);
        this.entityData.define(BLUE_RUFOUSING_VARIANT, 0);
        this.entityData.define(ORANGE_BASE_VARIANT, 0);
        this.entityData.define(WHITE_RATIO_VARIANT, 0);
        this.entityData.define(ALBINO_VARIANT, 0);
        this.entityData.define(TABBY_STRIPES_VARIANT, 0);
        this.entityData.define(EYE_COLOR_VARIANT_LEFT, 0);
        this.entityData.define(EYE_COLOR_VARIANT_RIGHT, 0);
        this.entityData.define(NOISE, 0);
        this.entityData.define(SILVER_VARIANT, 0);

        //
        this.entityData.define(CHIMERA_GENE, "C-C");
        this.entityData.define(CHIMERA_VARIANT, 0);

        this.entityData.define(BASE_CHIMERA, "B-b");
        this.entityData.define(ORANGE_BASE_CHIMERA, "o-o");
        this.entityData.define(WHITE_RATIO_CHIMERA, "w-w");
        this.entityData.define(ALBINO_CHIMERA, "C-cs");
        this.entityData.define(DILUTE_CHIMERA, "d-d");
        this.entityData.define(AGOUTI_CHIMERA, "a-a");
        this.entityData.define(TABBY_STRIPES_CHIMERA, "mc-mc");
        this.entityData.define(SILVER_CHIMERA, "i-i");

        this.entityData.define(RUFOUSING_VARIANT_CHIMERA, 0);
        this.entityData.define(BLUE_RUFOUSING_VARIANT_CHIMERA, 0);
        this.entityData.define(ORANGE_BASE_VARIANT_CHIMERA, 0);
        this.entityData.define(WHITE_RATIO_VARIANT_CHIMERA, 0);
        this.entityData.define(ALBINO_VARIANT_CHIMERA, 0);
        this.entityData.define(TABBY_STRIPES_VARIANT_CHIMERA, 0);
        this.entityData.define(NOISE_CHIMERA, 0);
        this.entityData.define(SILVER_VARIANT_CHIMERA, 0);
        //

        this.entityData.define(GENETICAL_SKIN, false);

        // GENETICS

        this.entityData.define(PLAYER_BOUND_UUID, Optional.of(ClanData.EMPTY_UUID));
        this.entityData.define(ANIM_INDEX, -1);
        this.entityData.define(SHOW_MORPH_NAME, true);

        this.entityData.define(IS_RESTING, false);
        this.entityData.define(SITTING_INDEX, 0);
        this.entityData.define(CHILLING_INDEX, 0);
        this.entityData.define(IS_WAKING_UP, false);

        this.entityData.define(BROKEN_PAW, false);
        this.entityData.define(WRAPED_PAW, false);


    }

    public void setShowMorphName(boolean value) {
        this.entityData.set(SHOW_MORPH_NAME, value);
    }

    public boolean shouldShowMorphName() {
        return this.entityData.get(SHOW_MORPH_NAME);
    }

    public void setPlayerBoundUuid(UUID uuid) {
        this.entityData.set(PLAYER_BOUND_UUID, Optional.ofNullable(uuid));
    }

    public UUID getPlayerBoundUuid() {
        return this.entityData.get(PLAYER_BOUND_UUID).orElse(ClanData.EMPTY_UUID);
    }





    public GeneticsModule getGeneticsModule() {
        return this.geneticsModule;
    }





    public boolean isOnGeneticalSkin() {
        return this.entityData.get(GENETICAL_SKIN);
    }

    public void setOnGeneticalSkin(boolean value) {
        this.entityData.set(GENETICAL_SKIN, value);
    }



    public void setSize(float value) {
        this.entityData.set(SIZE, value);
    }

    public float getSize() {
        return this.entityData.get(SIZE);
    }

    public void rewardGeneticsAdvancements() {
        if (this.getOwner() instanceof ServerPlayer serverPlayer) {
            MinecraftServer server = serverPlayer.getServer();
            if (server != null) {

                if (WCGenetics.Chimerism.isChimera(this.entityData.get(CHIMERA_GENE))) {
                    Advancement adv = server.getAdvancements()
                            .getAdvancement(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID,"chimera_obtained"));
                    if (adv != null) {
                        serverPlayer.getAdvancements().award(adv, "chimera_obtained");
                    }
                }

                if (WCGenetics.EyesAnomaly.isHeteroChromic(this.entityData.get(EYES_ANOMALY))) {
                    Advancement adv = server.getAdvancements()
                            .getAdvancement(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID,"heterochromic_obtained"));
                    if (adv != null) {
                        serverPlayer.getAdvancements().award(adv, "heterochromic_obtained");
                    }
                }

                if (WCGenetics.Albino.isTrueAlbino(this.entityData.get(ALBINO)) || WCGenetics.Albino.isTrueAlbino(this.entityData.get(ALBINO_CHIMERA))) {
                    Advancement adv = server.getAdvancements()
                            .getAdvancement(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID,"albino_obtained"));
                    if (adv != null) {
                        serverPlayer.getAdvancements().award(adv, "albino_obtained");
                    }
                }


            }
        }
    }

    public void rewardMoonmoon() {
        if (this.hasCustomName() && this.getCustomName().getString().toLowerCase().contains("moonmoon")) {
            if (this.getOwner() instanceof ServerPlayer serverPlayer) {
                MinecraftServer server = serverPlayer.getServer();
                if (server != null) {
                    Advancement adv = server.getAdvancements()
                            .getAdvancement(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID,"moonmoon"));
                    if (adv != null) {
                        serverPlayer.getAdvancements().award(adv, "moonmoon");
                    }
                }
            }
        }
    }





    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        if (!this.level().isClientSide && !moodLoaded) {
            this.setRandomMood(this.level().getRandom());
            moodLoaded = true;
        }
        if (!this.level().isClientSide) {
            this.setRank(this.getRank());
            this.setSpecificMood(this.getMood());
            this.setPersonality(this.getPersonality());
        }
    }


    /**
     * Indicator to allow the cat to perform the attack animation
     */
    public void setAttacking(boolean attacking) {
        this.entityData.set(ATTACKING, attacking);
    }

    /**
     * Indicator to allow the cat to perform attack animation
     */
    public boolean isAttacking() {
        return this.entityData.get(ATTACKING);
    }


    public int getVariant() {
        return this.entityData.get(VARIANT);
    }

    /**
     * Sets the variant and stores it in NBT.
     * Then change the functional size of the cat depending on the variant.
     * This is the collision box size, not the visual size.
     */
    public void setVariant(int variant) {
        this.entityData.set(VARIANT, variant);

        float scale = 1f;

        this.entityData.set(SCALE, scale);
    }


    @Override
    public int getExperienceReward() {
        return 25 + 5 * this.random.nextInt(3);
    }

    @Override
    public void rideTick() {

        super.rideTick();

        Entity vehicle = this.getVehicle();
        if (vehicle == null) return;

        float yawDeg;
        float pitchDeg;

        if (vehicle instanceof LivingEntity) {
            yawDeg = ((LivingEntity) vehicle).yBodyRot;
            pitchDeg = vehicle.getXRot();
        } else {
            yawDeg = vehicle.getYRot();
            pitchDeg = vehicle.getXRot();
        }

        double yaw = Math.toRadians(-yawDeg);

        double dirX = Math.sin(yaw);
        double dirZ = Math.cos(yaw);


        float sizeOffset = 0.0f;
        float sizeOffsetDistance = 0.0f;

        double distance = 0.66;

        double offsetY = 0.15;

        offsetY += sizeOffset;

        distance += sizeOffsetDistance*2.5;


        double pitch = Math.toRadians(pitchDeg);

        double verticalOffset = Math.sin(-pitch) * 0.4;

        double offsetX = dirX * distance + (verticalOffset / 5);
        double offsetZ = dirZ * distance + (verticalOffset / 5);

        this.setPos(
                vehicle.getX() + offsetX,
                vehicle.getY() + offsetY + verticalOffset,
                vehicle.getZ() + offsetZ
        );

        float sideYaw = yawDeg + 200F;

        this.setYRot(sideYaw);
        this.setYHeadRot(sideYaw);

    }


    @Override
    public boolean canRiderInteract() {
        return true;
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
    }


    private boolean apprenticeAge = false;


    public Vec3 clientMovement = Vec3.ZERO;
    private Vec3 lastClientPos = Vec3.ZERO;

    @Override
    public void tick() {
        super.tick();

        this.diseaseTick();

        if (!this.level().isClientSide()) {

            this.patrolTick();

            if (this.isPassenger()) {
                Entity vehicle = this.getVehicle();
                if (vehicle instanceof Player player) {
                    if (player.isShiftKeyDown() || !(PlayerShape.getCurrentShape(player) instanceof WCatEntity)) {
                        this.stopRiding();
                        this.isBeingCarried = false;
                    }
                }
            }

            if (lovingParticlesTicks > 0) {
                lovingParticlesTicks--;
                float chance = this.random.nextFloat();
                if (chance <= 0.05f) {
                    Entity mate = ((ServerLevel) this.level()).getEntity(this.getMateUUID());
                    if (mate != null) {
                        this.getNavigation().moveTo(mate, 1f);
                        this.getLookControl().setLookAt(mate, (float) (this.getMaxHeadYRot() + 20), (float) this.getMaxHeadXRot());
                    }
                    ((ServerLevel) this.level()).sendParticles(ParticleTypes.HEART, this.getX(), this.getY(), this.getZ(), 2, 0.5f, 0.5f, 0.5f, 0.1f);
                    if (chance < 0.03) {
                        this.level().playSound(null, this.blockPosition(), SoundEvents.CAT_PURR, SoundSource.NEUTRAL, 0.4F, 1.0F);
                    }
                }
            }

            if (this.getMood() == Mood.SAD) {
                Vec3 currentMovement = this.getDeltaMovement();
                this.setDeltaMovement(currentMovement.x * 0.8, currentMovement.y, currentMovement.z * 0.8);
            }

            if (this.mode == CatMode.SIT && this.lookAtLeaderFlag && this.entityData.get(SITTING_INDEX) != 3) {
                LivingEntity owner = this.getOwner();
                if (owner != null) {
                    if (this.distanceToSqr(owner) <= 100 && !owner.isSpectator()) {
                        this.getLookControl().setLookAt(owner, (float) (this.getMaxHeadYRot() + 20), (float) this.getMaxHeadXRot());
                        this.isLookingAtLeader = true;
                    } else {
                        this.isLookingAtLeader = false;
                    }
                }
            } else if (this.mode != CatMode.SIT) {
                this.lookAtLeaderFlag = false;
                this.isLookingAtLeader = false;
            }

            if (this.getPersonality() == Personality.AMBITIOUS) {
                if (this.random.nextFloat() <= ((float) 1 / 18000)) this.eatPreyInInventory();
            }

            if (this.level().isThundering()) {
                if (this.random.nextFloat() <= 0.000009) {
                    if (this.level().canSeeSky(this.blockPosition())) {
                        this.setHealth(this.getMaxHealth() / 10);
                        LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(this.level());
                        if (lightning != null) {
                            lightning.moveTo(this.getX(), this.getY(), this.getZ());
                            this.level().addFreshEntity(lightning);
                        }
                    }
                }
            }

            if (this.tickCount % 9600 == 0) {
                this.setRandomMood(this.random);
            }

            if (this.tickCount % 20 == 0) {
                if (this.getTarget() != null) {
                    if (!this.getTarget().isAlive()) {
                        this.setTarget(null);
                    }
                }

                float moonsCalc = (float) ((this.getAge() + (20 * 60 * getKitGrowthTimeMinutes())) / (100.0 * getKitGrowthTimeMinutes()));
                this.entityData.set(AGE_SYNC, moonsCalc);
            }


            if (this.tickCount % 400 == 0 && this.level() instanceof ServerLevel serverLevel) {

                UUID fatherUUID = this.getFatherUUID();
                if (fatherUUID != null && !fatherUUID.equals(emptyUUID)) {
                    Entity father = serverLevel.getEntity(fatherUUID);
                    if (father instanceof Player player) {
                        String morphName = player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                                .map(WCEPlayerData::getMorphName)
                                .orElse(player.getName().getString());

                        if (!morphName.equals(this.getFather().getString())) {
                            this.setFather(Component.literal(morphName));
                        }
                    }
                }

                UUID motherUUID = this.getMotherUUID();
                if (motherUUID != null && !motherUUID.equals(emptyUUID)) {
                    Entity mother = serverLevel.getEntity(motherUUID);
                    if (mother instanceof Player player) {
                        String morphName = player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                                .map(WCEPlayerData::getMorphName)
                                .orElse(player.getName().getString());

                        if (!morphName.equals(this.getMother().getString())) {
                            this.setMother(Component.literal(morphName));
                        }
                    }
                }

                this.updateClanCatData();

            }


            if (soundTick > 0) {
                soundTick--;
            }

            if (grumpyAtOwnerTick > 0) {
                grumpyAtOwnerTick--;

                if (grumpyAtOwnerTick == 0) {

                    if (this.getTarget() != null && this.getTarget().equals(this.getOwner())) {
                        this.setTarget(null);
                    }
                }
            }


            if (catSniffTickCooldown > 0) catSniffTickCooldown--;

            if (scentDirection != null && scentDistance < scentMaxDistance) {
                scentTick++;

                if (scentTick % 2 == 0) {
                    Vec3 particlePos = scentStartPos.add(scentDirection.scale(scentDistance));

                    ((ServerLevel) this.level()).sendParticles(
                            ParticleTypes.HAPPY_VILLAGER,
                            particlePos.x, particlePos.y, particlePos.z,
                            1,
                            particlePos.x * scentDistance,
                            particlePos.y * scentDistance,
                            particlePos.z * scentDistance,
                            0.0
                    );


                    scentDistance += scentStep;
                }
            }

            if (scentDistance > scentMaxDistance) {
                scentDirection = null;
            }

            if (!apprenticeAge && this.getAge() >= -((getKitGrowthTimeMinutes() * 60 * 20) / 2)
                    && this.kitBorn && this.isTame()) {
                kitToAppGrow();
            }
        }
        if (this.level().isClientSide) {
            Vec3 pos = this.position();
            clientMovement = pos.subtract(lastClientPos);
            lastClientPos = pos;

        }

    }

    private void kitToAppGrow() {
        apprenticeAge = true;

        String genderS;
        if (this.getGender() == 0) {
            genderS = " ♂";
        } else {
            genderS = " ♀";
        }

        String prefix = this.getPrefix().getString();
        String newName = prefix + "paw" + genderS;
        this.setCustomName(Component.literal(newName));
        this.setCustomNameVisible(true);
        this.setAppScale(true);

        Component message = Component.translatable("wcat.new_name_announcement_kit",
                prefix + "kit",
                Component.literal(newName).withStyle(ChatFormatting.GOLD));

        Entity owner = this.getOwner();
        if (owner instanceof Player) {
            owner.sendSystemMessage(message);
        }

        this.updateClanCatData();
        this.registerClanLog(message);


        this.updateNest();

        this.setRank(APPRENTICE);
        this.level().broadcastEntityEvent(this, (byte) 6);
        this.level().playSound(null, this.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.AMBIENT, 0.8f, 1.6f);
        this.kitBorn = false;
        this.applyAppAttributes();
        this.setNameColor(this.getRank());
    }

    public boolean isExpectingKits() {
        return this.entityData.get(EXPECTING_KITS);
    }

    public void setExpectingKits(boolean value) {
        this.entityData.set(EXPECTING_KITS, value);
    }

    public int getGender() {
        return this.entityData.get(GENDER);
    }

    public void setGender(int value) {
        this.entityData.set(GENDER, value);
    }

    public int getKittingTicks() {
        return this.entityData.get(KITTING_TICKS);
    }

    public void setKittingTicks(int value) {
        this.entityData.set(KITTING_TICKS, value);
    }

    public Component getMate() {
        return this.entityData.get(MATE).orElse(Component.literal("None"));
    }

    public void setMate(@Nullable Component name) {
        this.entityData.set(MATE, Optional.ofNullable(name));
    }

    public Component getPrefix() {
        return this.entityData.get(PREFIX).orElse(Component.literal("None"));
    }

    public void setPrefix(@Nullable Component prefix) {
        this.entityData.set(PREFIX, Optional.ofNullable(prefix));
    }

    public Rank getRank() {
        int value = this.entityData.get(RANK);
        if (value < 0 || value >= values().length) {
            return NONE;
        }
        return values()[value];
    }

    public void setRank(Rank rank) {
        this.entityData.set(RANK, rank.ordinal());

        this.setNameColor(rank);
    }

    public boolean isMale() {
        return this.getGender() == 0;
    }

    public boolean isAppScale() {
        return this.entityData.get(APP_SCALE);
    }

    public void setAppScale(boolean value) {
        this.entityData.set(APP_SCALE, value);
    }

    /**
     * When a Wild Cat spawns, set a random gender and a random variant.
     */
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        SpawnGroupData data = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);


        if (!this.level().isClientSide()) {
            this.setGender(this.random.nextInt(2));
        }
        int randomVariant = this.random.nextInt(maxVariants);
        this.setVariant(randomVariant);
        this.wanderCenter = this.blockPosition();
        this.assignRandomPersonality(this.random);
        this.getGeneticsModule().initializeGenetics();
        if (this.getAge() < 0 && this.getAge() > -25000) {
            int minutes = WCEServerConfig.SERVER.KIT_GROWTH_MINUTES.get();
            int growingTicks = minutes * 20 * 60;
            this.setAge((growingTicks / 2) + 100);
            this.setAppScale(true);
        }

        return data;
    }

    /**
     * If the cat is crouching, change its collision box.
     */
    @Override
    public EntityDimensions getDimensions(Pose pose) {
        float scale = this.entityData.get(SCALE);

        return switch (pose) {
            case CROUCHING -> super.getDimensions(Pose.STANDING).scale(scale * 0.8f);
            default -> super.getDimensions(pose).scale(scale);
        };
    }


    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        if (SCALE.equals(pKey)) {
            this.refreshDimensions();
        }
        super.onSyncedDataUpdated(pKey);
    }


    @Override
    public boolean isFood(ItemStack stack) {
        if (this.isBaby()) return stack.is(ModTags.Items.PREY) || stack.is(ModTags.Items.ADDITIONAL_PREY);
        if (stack.is(ModItems.CATMINT.get())) {
            return !this.isExpectingKits() && this.isTame();
        }
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean result = super.hurt(source, amount);

        if (!level().isClientSide && result) {
            if (this.isResting()) {
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY(),this.getZ(),
                            30,0.4f,0.4f,0.4f,0.2f);
                    if (source.getEntity() instanceof Player player) {
                        if (this.getFriendshipLevel(player.getUUID()) > 0) {
                            this.setFriendshipLevel(player.getUUID(), this.getFriendshipLevel(player.getUUID()) - 10);
                        }
                    }
                }
                this.setResting(false, 0);
            }

            if (this.isChilling()) {
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY(),this.getZ(),
                            30,0.4f,0.4f,0.4f,0.2f);
                    if (source.getEntity() instanceof Player player) {
                        if (this.getFriendshipLevel(player.getUUID()) > 0) {
                            this.setFriendshipLevel(player.getUUID(), this.getFriendshipLevel(player.getUUID()) - 5);
                        }
                    }
                }
                this.setChilling(false, 0);
            }

            Entity enemy = source.getEntity();

            if (enemy instanceof LivingEntity livingEnemy) {

                if (livingEnemy.equals(this.getOwner()) && this.getPersonality() == Personality.GRUMPY) {
                    this.setTarget(livingEnemy);
                    this.grumpyAtOwnerTick = 37;
                }
                this.alertNearbyAllies(livingEnemy);
            }
        }

        return result;
    }


    private void alertNearbyAllies(LivingEntity enemy) {
        double radius = 16.0D;

        if (!this.isTame() && this.getOwner() == null) {

            List<WCatEntity> allies = level().getEntitiesOfClass(
                    WCatEntity.class,
                    this.getBoundingBox().inflate(radius),
                    cat ->
                            cat != this &&
                                    !cat.isTame() &&
                                    cat.getOwner() == null &&
                                    !cat.isDeadOrDying()
            );

            if (enemy.isAlive()) {
                for (WCatEntity ally : allies) {
                    ally.setTarget(enemy);
                    ally.setLastHurtByMob(enemy);
                }
            }

            return;

        }

        if (this.isTame() && this.getOwner() != null) {
            if (enemy == this.getOwner()) return;

            if (enemy instanceof ServerPlayer player) {
                UUID clanUUID = player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                        .map(WCEPlayerData::getCurrentClanUUID).orElse(ClanData.EMPTY_UUID);
                if (!clanUUID.equals(ClanData.EMPTY_UUID)) {
                    if (clanUUID.equals(this.getClanUUID())) return;
                }
            }

            List<WCatEntity> allies = level().getEntitiesOfClass(
                    WCatEntity.class,
                    this.getBoundingBox().inflate(radius),
                    cat ->
                            cat != this &&
                                    cat.isTame() &&
                                    cat.getOwner() != null &&
                                    cat.getOwner().getUUID().equals(this.getOwner().getUUID()) &&
                                    !cat.isDeadOrDying()
            );

            if (enemy.isAlive()) {
                for (WCatEntity ally : allies) {
                    ally.setTarget(enemy);
                    ally.setLastHurtByMob(enemy);
                }
            }

        }

    }


    @Override
    protected SoundEvent getAmbientSound() {

        if (this.isResting() || this.entityData.get(SITTING_INDEX) == 3) return SoundEvents.CAT_PURR;

        if (this.random.nextFloat() < 0.05f) {
            if (this.getPersonality() == Personality.GRUMPY) {
                return SoundEvents.CAT_HISS;
            } else if (this.getPersonality() == Personality.SHY) {
                return SoundEvents.CAT_STRAY_AMBIENT;
            } else if (this.getPersonality() == Personality.FRIENDLY) {
                return SoundEvents.CAT_PURREOW;
            }
        }
        return SoundEvents.CAT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        if (source.is(WCEDamageTypes.GREENCOUGH) || source.is(WCEDamageTypes.WHITECOUGH)) {
            return ModSounds.WILDCAT_COUGH.get();
        }
        return SoundEvents.CAT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.WILDCAT_SCREAM.get();
    }

    /**
     * When the pose changes, refresh dimensions. This is what allows you to not glitch into walls and stuff if you are a Wild Cat
     */
    @Override
    public void setPose(Pose pose) {
        super.setPose(pose);
        this.refreshDimensions();
    }

    @Override
    public float getScale() {
        return 1f;
    }

    public void eatPreyInInventory() {
        int slotIndexToConsume = 0;
        boolean itemFound = false;
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack slot = inventory.getItem(i);

            if (slot.is(ModTags.Items.PREY)) {
                slotIndexToConsume = i;
                itemFound = true;
            }
        }

        if (itemFound) {
            inventory.getItem(slotIndexToConsume).shrink(1);
            ServerLevel sLevel = ((ServerLevel) this.level());
            sLevel.playSound(null, this.blockPosition(), SoundEvents.CAT_EAT, SoundSource.NEUTRAL, 0.6F, 1.0F);
            sLevel.playSound(null, this.blockPosition(), SoundEvents.PLAYER_BURP, SoundSource.NEUTRAL, 0.6F, 1.0F);
            sLevel.playSound(null, this.blockPosition(), SoundEvents.GENERIC_EAT, SoundSource.NEUTRAL, 0.6F, 1.0F);
            sLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.BEEF)), this.getX(), this.getY(), this.getZ(), 30, 0.4f, 0.4f, 0.4f, 0.1f);
        }

    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        if (source.is(DamageTypes.SWEET_BERRY_BUSH)) return true;
        if (source.is(DamageTypes.IN_WALL) && this.isBaby() && this.isBeingCarried) {
            return true;
        }
        return super.isInvulnerableTo(source);
    }

    public void registerClanLog(Component message) {

        if (this.level() instanceof ServerLevel sLevel) {
            ClanData data = ClanData.get(sLevel);
            ClanData.Clan clan = data.getClan(this.getClanUUID());

            if (clan != null) {
                data.registerLog(sLevel, clan.clanUUID, message);
                data.setDirty();
            }
        }

    }

    public void updateClanCatData() {
        if (this.level() instanceof ServerLevel sLevel) {
            ClanData data = ClanData.get(sLevel);
            ClanData.Clan clan = data.getClan(this.getClanUUID());

            if (clan != null) {
                if (this.getRank() != NONE) {
                    data.addClanCat(clan, this);
                } else {
                    data.removeClanCatFromClan(clan.clanUUID, this);
                    this.setClan(Component.empty());
                }
            }
        }
    }

    public void updateNest() {
        if (this.hasHomePosition()) {
            BlockState state = this.level().getBlockState(this.getHomePosition());
            if (state.getBlock() instanceof NestBlock) {
                BlockEntity blockEntity = this.level().getBlockEntity(this.getHomePosition());

                if (blockEntity instanceof NestBlockEntity mossBed) {
                    if (mossBed.isOwnedBy(this.getUUID())) {
                        if (this.hasCustomName()) {
                            if (!mossBed.getCatName().equals(this.getCustomName().getString())) {
                                mossBed.setCatName(this.getCustomName().getString());
                                this.level().sendBlockUpdated(this.getHomePosition(), state, state, 3);
                            }
                        }
                    }
                }
            }
        }
    }


    @Override
    public void setYHeadRot(float rotation) {
        if (isAnImage()) return;
        super.setYHeadRot(rotation);
    }

    @Override
    public void setYBodyRot(float rotation) {
        if (isAnImage()) return;
        super.setYBodyRot(rotation);
    }

    @Override
    public void setXRot(float rotation) {
        if (isAnImage()) return;
        super.setXRot(rotation);
    }

    public void setItemSynced(int pIndex, ItemStack pStack) {
        this.inventory.setItem(pIndex, pStack);
        this.updateMainHandFromInventory();
        this.updateOffHandFromInventory();
    }

    public void updateMainHandFromInventory() {
        ItemStack toEquip = ItemStack.EMPTY;

        for (int i = 0; i < 3; i++) {
            ItemStack stack = this.inventory.getItem(i);
            if (!stack.isEmpty()) {
                toEquip = stack.copyWithCount(1);
                break;
            }
        }

        this.setItemSlot(EquipmentSlot.MAINHAND, toEquip);
    }

    public void updateOffHandFromInventory() {
        ItemStack toEquip = ItemStack.EMPTY;
        int found = 0;
        for (int i = 0; i < 3; i++) {
            ItemStack stack = this.inventory.getItem(i);

            if (!stack.isEmpty()) {
                found++;

                if (found == 2) {
                    toEquip = stack.copyWithCount(1);
                    break;
                }
            }
        }

        this.setItemSlot(EquipmentSlot.OFFHAND, toEquip);
    }


    public SimpleContainer getCatInventory() {
        return this.inventory;
    }

    @Override
    public int getAmbientSoundInterval() {
        if (this.getRandom().nextFloat() < 0.50f) return 180;
        else return 220;
    }

    @Override
    public void setTarget(@Nullable LivingEntity pTarget) {
        if (this.isOrderedToSit() || this.mode == CatMode.SIT) return;
        if (this.getRank() == MEDICINE) return;
        super.setTarget(pTarget);
    }

    public void updateMatesName() {
        if (this.level() instanceof ServerLevel sLevel) {
            Entity mate = sLevel.getEntity(this.getMateUUID());
            if (mate != null) {
                if (mate instanceof WCatEntity cat) {
                    cat.setMate(this.hasCustomName() ? this.getCustomName() : Component.literal("Unnamed cat"));
                }
            }
        }
    }

    @Override
    public void die(DamageSource pCause) {

        if (this.isTame()) {
            if (this.getOwner() instanceof ServerPlayer owner) {
                owner.sendSystemMessage(Component.literal(
                        String.format("At: X=%.0f, Y=%.0f, Z=%.0f",
                                this.getX(), this.getY(), this.getZ())
                ).withStyle(ChatFormatting.GRAY));
            }
        }

        Component message = Component.empty()
                .append(pCause.getLocalizedDeathMessage(this).copy())
                .append(". At: ")
                .append(Component.literal(String.format("X=%.0f, Y=%.0f, Z=%.0f",
                        this.getX(), this.getY(), this.getZ())));
        this.registerClanLog(message);

        if (this.level() instanceof ServerLevel sLevel) {
            if (!this.getClanUUID().equals(ClanData.EMPTY_UUID)) {
                ClanData data = ClanData.get(sLevel);
                data.removeClanCatFromAnyClan(this);
            }

            BlockPos homepos = this.getHomePosition();
            if (homepos != null) {
                if (sLevel.getBlockState(homepos).getBlock() instanceof NestBlock) {
                    BlockEntity bEntity = sLevel.getBlockEntity(homepos);
                    if (bEntity instanceof NestBlockEntity mbEntity) {
                        mbEntity.resetAssigned();
                        mbEntity.setChanged();
                        if (!sLevel.isClientSide()) {
                            sLevel.sendBlockUpdated(homepos, sLevel.getBlockState(homepos),
                                    sLevel.getBlockState(homepos), 3);
                        }
                    }
                }
            }
        }

        super.die(pCause);
    }

    @Override
    public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
        return false;
    }

    @Override
    public boolean isPersistenceRequired() {
        return true;
    }

    @Override
    public boolean isPushable() {
        if (this.isResting() || this.entityData.get(SITTING_INDEX) == 3 || this.isChilling()) return false;
        return super.isPushable();
    }

    @Override
    public LivingEntity getTarget() {
        if (this.mode == CatMode.SIT){
            this.setTarget(null);
            this.setAggressive(false);
            this.setLastHurtByPlayer(null);
            this.setLastHurtByMob(null);
            return null;
        }
        if (this.isResting()) {
            return null;
        }
        return super.getTarget();
    }



    private void patrolTick() {

        if (this.mode != CatMode.WANDER) {
            if (this.tellingCatsToPatrol) tellingCatsToPatrol = false;
        }

        if (!(this.onBorderPatrolFlag || this.onHuntingPatrolFlag)) return;

        if (this.mode != CatMode.WANDER) {
            this.finishPatrol();
        }

        if (this.onAreaToPatrolForTicks > 0) {
            this.onAreaToPatrolForTicks--;
            if (this.onAreaToPatrolForTicks <= 0) {
                BlockPos next = this.getAreaToPatrolIndex(this.patrolIndex);
                if (next == null) {
                    this.finishPatrol();
                }
            }
        }
    }

    void finishPatrol() {
        this.returnHomeFlag = true;
        this.onBorderPatrolFlag = false;
        this.onHuntingPatrolFlag = false;
        this.returningFromPatrol = true;
        this.onAreaToPatrolForTicks = 0;
        this.setAreasToPatrol(new HashMap<>());
    }

    int patrolIndex = 0;
    public boolean onBorderPatrolFlag = false;
    public boolean onHuntingPatrolFlag = false;
    public boolean returningFromPatrol = false;

    public boolean tellingCatsToPatrol = false;
    public List<WCatEntity> catsToTell = new ArrayList<>();
    public int patrolType = 0;

    private Map<Integer, BlockPos> areasToPatrol = new HashMap<>();

    int onAreaToPatrolForTicks = 0;

    public void setAreasToPatrol(Map<Integer, BlockPos> map) {
        this.areasToPatrol = map;
    }

    public Map<Integer, BlockPos> getAreasToPatrol() {
        return this.areasToPatrol;
    }

    @Nullable
    public BlockPos getAreaToPatrolIndex(int index) {
        return this.areasToPatrol.get(index);
    }

    public void setOnBorderPatrol(Map<Integer, BlockPos> map) {
        this.onBorderPatrolFlag = true;
        this.onHuntingPatrolFlag = false;
        this.returningFromPatrol = false;
        this.mode = CatMode.WANDER;
        this.setInSittingPose(false);
        this.setOrderedToSit(false);

        if (this.isResting()) this.setResting(false, 0);
        if (this.isChilling()) this.setChilling(false, 0);

        this.patrolIndex = 0;

        this.setAreasToPatrol(map);
        this.onAreaToPatrolForTicks = 0;
    }


    public void setOnHuntingPatrol(Map<Integer, BlockPos> map) {
        this.onBorderPatrolFlag = false;
        this.onHuntingPatrolFlag = true;
        this.returningFromPatrol = false;
        this.mode = CatMode.WANDER;
        this.setInSittingPose(false);
        this.setOrderedToSit(false);

        if (this.isResting()) this.setResting(false, 0);
        if (this.isChilling()) this.setChilling(false, 0);

        this.patrolIndex = 0;

        this.setAreasToPatrol(map);
        this.onAreaToPatrolForTicks = 0;
    }

    public void setDeputyToSetPatrols(Map<Integer, BlockPos> map, List<WCatEntity> cats, int patrolType) {
        this.mode = CatMode.WANDER;
        this.setInSittingPose(false);
        this.setOrderedToSit(false);

        this.catsToTell = cats;
        this.setAreasToPatrol(map);
        this.tellingCatsToPatrol = true;
        this.patrolType = patrolType;
    }

    public CompoundTag savePatrolDataNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();

        for (Map.Entry<Integer, BlockPos> entry : this.areasToPatrol.entrySet()) {
            CompoundTag t = new CompoundTag();
            t.putInt("Index", entry.getKey());

            BlockPos pos = entry.getValue();
            t.putInt("X", pos.getX());
            t.putInt("Y", pos.getY());
            t.putInt("Z", pos.getZ());

            list.add(t);
        }

        tag.put("Areas", list);

        tag.putBoolean("OnBorderPatrol", this.onBorderPatrolFlag);
        tag.putBoolean("OnHuntingPatrol", this.onHuntingPatrolFlag);
        tag.putBoolean("ReturningPatrol", this.returningFromPatrol);

        tag.putInt("PatrolIndex", this.patrolIndex);
        tag.putInt("PatrolType", this.patrolType);
        tag.putInt("PatrolWait", this.onAreaToPatrolForTicks);

        return tag;
    }

    public void loadPatrolDataNBT(CompoundTag superTag) {
        if (superTag.contains("PatrolData")) {
            CompoundTag tag = superTag.getCompound("PatrolData");
            this.onBorderPatrolFlag = tag.getBoolean("OnBorderPatrol");
            this.onHuntingPatrolFlag = tag.getBoolean("OnHuntingPatrol");
            this.returningFromPatrol = tag.getBoolean("ReturningPatrol");

            this.patrolIndex = tag.getInt("PatrolIndex");
            this.patrolType = tag.getInt("PatrolType");
            this.onAreaToPatrolForTicks = tag.getInt("PatrolWait");

            if (tag.contains("Areas")) {
                Map<Integer, BlockPos> map = new HashMap<>();

                ListTag list = tag.getList("Areas", Tag.TAG_COMPOUND);

                for (int i = 0; i < list.size(); i++) {
                    CompoundTag tag2 = list.getCompound(i);

                    int index = tag2.getInt("Index");
                    BlockPos pos = new BlockPos(
                            tag2.getInt("X"),
                            tag2.getInt("Y"),
                            tag2.getInt("Z")
                    );

                    map.put(index, pos);
                }

                this.setAreasToPatrol(map);
            } else {
                this.areasToPatrol = new HashMap<>();
            }
        }

    }

}
