package net.snowteb.warriorcats_events.entity.custom;

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
import net.snowteb.warriorcats_events.event.ModEvents2;
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

import static net.snowteb.warriorcats_events.entity.custom.WCatEntity.Rank.*;

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

    private int lovingParticlesTicks = 0;

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

    private static final EntityDataAccessor<String> CHEST_FUR =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> BELLY_FUR =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> LEGS_FUR =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> HEAD_FUR =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> CHEEK_FUR =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> BACK_FUR =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> BOBTAIL =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> TAIL_FUR =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);

    private static final EntityDataAccessor<String> CHIMERA_GENE =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> CHIMERA_VARIANT =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.INT);


    private static final EntityDataAccessor<String> BASE =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> ORANGE_BASE =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> WHITE_RATIO =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> ALBINO =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> DILUTE =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> AGOUTI =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> TABBY_STRIPES =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> EYES_ANOMALY =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> SILVER =
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


    private static final EntityDataAccessor<String> BASE_CHIMERA =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> ORANGE_BASE_CHIMERA =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> WHITE_RATIO_CHIMERA =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> ALBINO_CHIMERA =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> DILUTE_CHIMERA =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> AGOUTI_CHIMERA =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> TABBY_STRIPES_CHIMERA =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> SILVER_CHIMERA =
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

    public static final EntityDataAccessor<Integer> IDLE_POSE =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.INT);


    private static final EntityDataAccessor<Boolean> GENETICAL_SKIN =
            SynchedEntityData.defineId(WCatEntity.class, EntityDataSerializers.BOOLEAN);

    private WCGenetics storedFatherGenetics;
    public String textureKey;
    private final String[] textureLayersPaths = new String[27];

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

    private final Map<String, List<String>> dialoguePool = new HashMap<>();

    private void loadDialogueMap() {

        dialoguePool.put("CALM.GIVE_ITEM.SUCCESS", Arrays.asList(
                "Oh! Thank you, <morph.name>!",
                "It looks good! I appreciate it!",
                "It's just what I needed, thank you, <<morph.name>!",
                "Aww! This is exactly what I wanted!",
                "I will definitely enjoy this.",
                "You are so kind! Thank you!",
                "That's very kind of you, <morph.name>.",
                "You brought this for me? That’s kind of you, <morph.name>.",
                "This came at a good time. Thank you.",
                "I’m glad you shared this with me. Thanks.",
                "I didn’t expect this, but I appreciate it a lot."
        ));
        dialoguePool.put("CALM.GIVE_ITEM.FAIL", Arrays.asList(
                "Oh, I'm good, thank you anyway.",
                "Err.. Thanks, but maybe for later?",
                "I don't really need that.",
                "Don't worry, you can keep it.",
                "Maybe give this to someone else.",
                "Thanks, but it's unnecessary.",
                "I'll pass for now, thank you!",
                "No, thank you. I don’t really need it.",
                "I’m fine without it, but thank you.",
                "Maybe save it for someone who needs it more.",
                "Another time, perhaps."
        ));

        dialoguePool.put("CALM.SHOW_AFFECTION.SUCCESS", Arrays.asList(
                "Oh! That's comforting. Thank you, <morph.name>.",
                "I appreciate your kindness.",
                "I appreciate you too!",
                "That feels nice!",
                "Thank you, <morph.name>. That makes me feel better",
                "You’re very considerate, <morph.name>."
        ));
        dialoguePool.put("CALM.SHOW_AFFECTION.FAIL", Arrays.asList(
                "Not right now, please.",
                "I'm not really in the mood for that...",
                "Please give me some space.",
                "Er.. Okay?",
                "Please don't-",
                "I appreciate my personal space more for now.",
                "I appreciate it, but I’m not in the mood.",
                "Another time, maybe.",
                "I’d prefer some distance for now."
        ));

        dialoguePool.put("CALM.TALK.SUCCESS", Arrays.asList(
                "It's a good day, isn't it, <morph.name>?",
                "The wind feels peaceful today.",
                "I've been thinking… Things are finally settling down.",
                "I enjoy moments like this. Quiet. Simple.",
                "You are always so serene… it's comforting.",
                "I remember when I was a kit... Oh, no I don't remember-",
                "Did you see that huge mouse today, <morph.name>?",
                "I smell rain, don't you, <morph.name>?",
                "I once caught a pigeon the size of a cat!",
                "You know... Sometimes I feel like the trees are whispering... Are they, <morph.name>?",
                "This place feels quiet today, doesn’t it, <morph.name>?"
        ));
        dialoguePool.put("CALM.TALK.FAIL", Arrays.asList(
                "I'm... not really in the mood to talk right now.",
                "Let's leave it for later.",
                "I’d rather stay quiet for now.",
                "I need some space to think.",
                "Not now, please.",
                "Sorry, not right now.",
                "Sorry, what did you say?",
                "Let’s talk another time.",
                "Sorry… I’m focusing on something.",
                "Maybe later. I’m not in a talking mood."
        ));


        dialoguePool.put("GRUMPY.GIVE_ITEM.SUCCESS", Arrays.asList(
                "Fine… I guess this is alright.",
                "Hmph. Thanks… I suppose.",
                "It'll do.",
                "Yeah… thanks, <morph.name>.",
                "I might use this, but don't get used to it.",
                "Well, something good at last.",
                "As long as it's not crow-food...",
                "Huh… better than nothing.",
                "I’ve seen worse. Thanks, <morph.name>.",
                "Okay… I’ll admit it’s decent."
        ));
        dialoguePool.put("GRUMPY.GIVE_ITEM.FAIL", Arrays.asList(
                "I don't want that.",
                "Don't you have anything better to do?",
                "Why would I want this?",
                "Take it back.",
                "Nah.",
                "It smells like crow-food...",
                "Uh, I'll pass.",
                "Someone else might eat this, but not me.",
                "Give it to someone else.",
                "Do I have to?",
                "No thanks."
        ));

        dialoguePool.put("GRUMPY.SHOW_AFFECTION.SUCCESS", Arrays.asList(
                "Tch… fine. Just for a second.",
                "Don't make a big deal out of it.",
                "…Thanks.",
                "Alright. That's enough.",
                "Whatever. I don't hate it.",
                "I... Okay?",
                "Alright… if it makes you feel better.",
                "Just this once.",
                "Okay… but only for a moment.",
                "…Thanks, I guess.",
                "Hmph… fine."
        ));
        dialoguePool.put("GRUMPY.SHOW_AFFECTION.FAIL", Arrays.asList(
                "Paws off.",
                "No touchies.",
                "Don't, thank you.",
                "Back away.",
                "I said no.",
                "I'm not that type of cat.",
                "I'd rather eat crow-food.",
                "Get off, mouse-brain.",
                "Not happening.",
                "Find someone else for that."
        ));

        dialoguePool.put("GRUMPY.TALK.SUCCESS", Arrays.asList(
                "I smell trouble today, don't you, <morph.name>?",
                "See that cat over there? They are mouse-brained.",
                "If anything causes trouble, they'll have to answer to my claws.",
                "Tch… everything feels off around here.",
                "If something goes wrong, I’ll be ready.",
                "Once, I heard rustling in the grass behind me. I just stood still. Turned out to be a mouse. Still didn’t like that it got so close without me knowing.",
                "This place smelled better yesterday...",
                "I don’t enjoy company… but you’re tolerable, <morph.name>.",
                "I don’t trust the quiet… Do you, <morph.name>?",
                "I don’t like how the territory feels lately.",
                "Hey, <morph.name>, how is prey running?",
                "I once clawed a cat's ears off for getting too close. Beware.",
                "Nothing stays peaceful for long."
        ));
        dialoguePool.put("GRUMPY.TALK.FAIL", Arrays.asList(
                "Hmph. Don’t bother me.",
                "I’m not here to chat.",
                "Chatting catches no prey, you should know it.",
                "Save it.",
                "Not interested.",
                "Go talk to someone else.",
                "Someone else might want to hear all your meowing.",
                "I don’t feel like talking.",
                "Go bother someone else.",
                "I’ve got better things to do.",
                "Leave the chatter for someone else."
        ));


        dialoguePool.put("CAUTIOUS.GIVE_ITEM.SUCCESS", Arrays.asList(
                "Thank you, <morph.name>. I appreciate it.",
                "Smells nice, thank you!.",
                "Seems fine, thanks.",
                "Alright… I’ll take it.",
                "Thanks, <morph.name>.",
                "I appreciate the gesture, <morph.name>.",
                "I’ll take it. Thanks, <morph.name>.",
                "Thanks. I wasn’t expecting this, but I’m grateful.",
                "Did you catch this yourself? Thanks, <morph.name>.",
                "If you're sure... I'll take it."
        ));
        dialoguePool.put("CAUTIOUS.GIVE_ITEM.FAIL", Arrays.asList(
                "I'd rather not take that for now.",
                "Err... No, thanks, <morph.name>.",
                "No… sorry.",
                "No, you can have it, <morph.name>.",
                "Better not.",
                "Maybe another time.",
                "I appreciate the thought, but I’ll refuse this time.",
                "Better if someone else takes it instead.",
                "Oh! No, you can keep it. Thanks anyway.",
                "I think I’ll pass for now."
        ));

        dialoguePool.put("CAUTIOUS.SHOW_AFFECTION.SUCCESS", Arrays.asList(
                "…Alright. That’s okay.",
                "I’m okay with that.",
                "That’s alright.",
                "That feels… alright.",
                "Well... I think I trust you, <morph.name>.",
                "Thank you. I’m comfortable.",
                "I’m okay with this… I think.",
                "Thank you… That was reassuring.",
                "That's sweet of you, <morph.name>."
        ));
        dialoguePool.put("CAUTIOUS.SHOW_AFFECTION.FAIL", Arrays.asList(
                "Too close.",
                "Wait… no.",
                "Please step back.",
                "Not now, sorry.",
                "Err, not for now.",
                "Sorry… I don’t feel comfortable with that.",
                "I’d rather keep some distance for the moment."
        ));

        dialoguePool.put("CAUTIOUS.TALK.SUCCESS", Arrays.asList(
                "Do you… hear that, <morph.name>?",
                "I’ve been keeping my guard up, In case a fox shows up.",
                "I hear things in the night... Don't you, <morph.name>?",
                "I’m watching the area… just in case.",
                "I heard the bushes rustling the other day, didn't you, <morph.name>?",
                "I hope this place is safe enough for us.",
                "Do you like this place too, <morph.name>?",
                "No threats yet... but I’ll stay aware.",
                "Once, I followed scent trail but stopped halfway. The earth dipped suddenly into a hollow. If I had rushed forward, I would’ve slipped... That's why you must always be careful.",
                "I trust careful paws more than lucky ones. Don't you, <morph.name>?",
                "Quiet day so far.",
                "I sometimes mark the places where a loner passes. It sounds strange, I know… but one day it will be of use."
        ));
        dialoguePool.put("CAUTIOUS.TALK.FAIL", Arrays.asList(
                "Not now.",
                "Sorry, I'm up to something.",
                "I need to stay alert, we can talk later.",
                "Let’s talk later… maybe.",
                "I'm a little busy, let's leave it for later.",
                "I’ve been keeping watch… Things feel quiet, but I’m not fully convinced.",
                "Calm days make me think something might be approaching.",
                "Let’s talk later. I’m paying attention to something.",
                "We can leave it for later."
        ));


        dialoguePool.put("INDEPENDENT.GIVE_ITEM.SUCCESS", Arrays.asList(
                "Thanks. I could've gotten it myself… but fine.",
                "I’ll take it this time.",
                "Seems fine. Appreciated.",
                "Thanks, I'll handle the rest on my own.",
                "Thanks, <morph.name>.",
                "Only this once...",
                "Well... Maybe this time. Thanks, <morph.name>.",
                "I’ll accept this time."
        ));
        dialoguePool.put("INDEPENDENT.GIVE_ITEM.FAIL", Arrays.asList(
                "I can hunt by myself.",
                "Keep it. I can manage myself.",
                "Unnecessary.",
                "No. I’ll obtain my own prey.",
                "I prefer independence, thanks anyway.",
                "Keep it. I’ll catch my own.",
                "I’m fine without it.",
                "Maybe someone else might need it."
        ));

        dialoguePool.put("INDEPENDENT.SHOW_AFFECTION.SUCCESS", Arrays.asList(
                "…Alright. Just a moment.",
                "You are so sweet, thanks, <morph.name>.",
                "Fine. Briefly.",
                "I can allow this… for now.",
                "Well, it's not that bad.",
                "I appreciate you too, <morph.name>."
        ));
        dialoguePool.put("INDEPENDENT.SHOW_AFFECTION.FAIL", Arrays.asList(
                "Maybe another time.",
                "That kind of closeness isn’t for me.",
                "Personal space, please.",
                "Not right now.",
                "I'm good, thanks."
        ));

        dialoguePool.put("INDEPENDENT.TALK.SUCCESS", Arrays.asList(
                "Greetings, <morph.name>, how is your day?",
                "A good cat can always survive on their own. Don't you think, <morph.name>?",
                "I survived on my own for a long time in the past. Have you, <morph.name>?",
                "Relying on someone else would weaken any cat. Good thing you are different, <morph.name>.",
                "I always prefer to catch my own prey. Don't you, <morph.name>?",
                "The wild always taught me to rely on my own paws.",
                "I remember when I was a kit, I had to learn to hunt on my own.",
                "How is prey running, <morph.name>?",
                "Long time ago I spent a leaf-fall season living near a fallen log. Prey was scarce, but I learned every sound the forest made. Hunger teaches you to listen… and to trust your instincts.",
                "I almost ate deathberries when I was a kit, did you know? I immediately spit them out when they tasted quite... Weird.",
                "I don’t avoid others. I just… return to myself every now and then. The world feels clearer when it’s only you and your paws for a little..."
        ));
        dialoguePool.put("INDEPENDENT.TALK.FAIL", Arrays.asList(
                "I don’t need conversation.",
                "I prefer silence for now.",
                "Not now, I'm quite busy.",
                "I’ll pass for now.",
                "I’m fine on my own for now.",
                "Not in the mood to chat."
        ));


        dialoguePool.put("FRIENDLY.GIVE_ITEM.SUCCESS", Arrays.asList(
                "Wow! Thanks, <morph.name>!",
                "You’re always so thoughtful! Thank you!",
                "It looks good! I appreciate it, <morph.name>!",
                "It's just what I wanted, thank you!",
                "Aww! This is exactly what I wanted!",
                "You're amazing, thank you, <morph.name>!",
                "I love it!",
                "This is great! Thanks, <morph.name>!",
                "You always bring good prey, thanks, <morph.name>."
        ));
        dialoguePool.put("FRIENDLY.GIVE_ITEM.FAIL", Arrays.asList(
                "Err… maybe not the best this time.",
                "That’s kind, but I'm good, thank you, <morph.name>!",
                "I'm good. Thanks anyway!",
                "Maybe next time, okay?",
                "Not right now, but thanks, <morph.name>!",
                "I appreciate it, but I’m fine for now."
        ));

        dialoguePool.put("FRIENDLY.SHOW_AFFECTION.SUCCESS", Arrays.asList(
                "That feels nice!",
                "Thank you! You're so sweet, <morph.name>!",
                "I appreciate your company!",
                "This feels good!",
                "It makes me happy. Thank you, <morph.name>!",
                "I really appreciate the care.",
                "You’re good company, <morph.name>."
        ));
        dialoguePool.put("FRIENDLY.SHOW_AFFECTION.FAIL", Arrays.asList(
                "A little too much!",
                "Err… slow down!",
                "Sorry, not right now.",
                "Oh! Too close!",
                "Not right now, sorry!",
                "Not at the moment, sorry.",
                "Maybe later, okay?"
        ));

        dialoguePool.put("FRIENDLY.TALK.SUCCESS", Arrays.asList(
                "Hey! Nice to see you again, <morph.name>!",
                "I was hoping you'd come by!",
                "Hi! how has your day been, <morph.name>?",
                "I was just thinking about you, <morph.name>! How are things going?",
                "Am I crazy or did I smell a fox last night... Anyway, how are you, <morph.name>?",
                "Hey! How is it going, <morph.name>?",
                "I would really love a walk. Wouldn't you?",
                "I’ve had a pretty calm day so far. How about you?",
                "Hi, <morph.name>! Have you seen anything interesting today?",
                "I was just thinking about exploring nearby later."
        ));
        dialoguePool.put("FRIENDLY.TALK.FAIL", Arrays.asList(
                "Sorry, I’m a bit tired to talk.",
                "Maybe later, okay?",
                "We can talk later, sorry, <morph.name>!",
                "I don’t really feel like talking now.",
                "Could we talk another time?",
                "Sorry… I’m a bit worn out."
        ));


        dialoguePool.put("SHY.GIVE_ITEM.SUCCESS", Arrays.asList(
                "Wow… Thank you, <morph.name>.",
                "I… appreciate it.",
                "You didn’t have to…",
                "Thanks… really, <morph.name>.",
                "This means a lot to me. Thank you, <morph.name>.",
                "For me...? Thanks, <morph.name>!",
                "You’re very kind… thank you, <morph.name>.",
                "I didn’t expect this… thank you.",
                "This feels special… thanks.",
                "I’m really thankful, <morph.name>."
        ));
        dialoguePool.put("SHY.GIVE_ITEM.FAIL", Arrays.asList(
                "S-sorry… I can't take it.",
                "I can't take that right now.",
                "I-I'm good, thanks, <morph.name>.",
                "I'm not hungry. Sorry, <morph.name>.",
                "Maybe not now…",
                "I don’t think I should take that."
        ));

        dialoguePool.put("SHY.SHOW_AFFECTION.SUCCESS", Arrays.asList(
                "…Okay.",
                "That’s… nice.",
                "Thank you… <morph.name>.",
                "I'm… alright with this.",
                "Oh! You're so sweet, <morph.name>!",
                "It helps… more than I expected."
        ));
        dialoguePool.put("SHY.SHOW_AFFECTION.FAIL", Arrays.asList(
                "N-not right now…",
                "S-sorry, I'm not that affective.",
                "Not now, maybe another time?",
                "Sorry, not now.",
                "Not right now… Sorry.",
                "Maybe not right now…",
                "I'm not really in the mood, sorry."
        ));

        dialoguePool.put("SHY.TALK.SUCCESS", Arrays.asList(
                "E-err… Hi, <morph.name>!",
                "I used to be always alone, talking to the trees, to the moon... Until you came,  <morph.name>, and you made me feel I truly belonged...",
                "I’m glad you’re here, <morph.name>!",
                "Back in the days, I barely spoke to anyone. One evening, another cat sat beside me without saying a word. We just watched the sky. That's something I still remember...",
                "I don’t talk much… but I like listening. How has your day been, <morph.name>?",
                "I once practiced speaking... alone, among some bushes. I repeated greetings to myself until I felt brave enough to use them. I think I've improved, heh...",
                "It's nice being near you. How are you, <morph.name>?",
                "H-hi, <morph.name>! Have you caught any prey today?",
                "Are we going on an adventure? I heard there is so much out there!",
                "Hi <morph.name>! I-I was just watching the clouds.",
                "H-hey! Um… how has your day been?",
                "Sometimes silence feels comfortable... I think I even learned to talk to myself!"
        ));
        dialoguePool.put("SHY.TALK.FAIL", Arrays.asList(
                "I… um… excuse me I got to go-",
                "S-sorry, I can't talk right now",
                "*quietly stares into your soul*",
                "I’d rather stay in the corner for now.",
                "Maybe later, okay?"
        ));


        dialoguePool.put("AMBITIOUS.GIVE_ITEM.SUCCESS", Arrays.asList(
                "Yes! Thank you, <morph.name>!",
                "Good. This will do.",
                "Smells good. Thank you, <morph.name>.",
                "Just what I needed. Thank you, <morph.name>.",
                "I will return the favour."
        ));
        dialoguePool.put("AMBITIOUS.GIVE_ITEM.FAIL", Arrays.asList(
                "Thanks, but I don't need it right now.",
                "Maybe another time.",
                "I might take it later.",
                "Not now, but thanks.",
                "I can catch my own, thanks.",
                "I don’t need that at the moment."
        ));

        dialoguePool.put("AMBITIOUS.SHOW_AFFECTION.SUCCESS", Arrays.asList(
                "Alright… just for a moment.",
                "I appreciate your support.",
                "Oh, thank you, <morph.name>.",
                "This makes me feel better.",
                "I'm grateful.",
                "Fine… I’ll allow it.",
                "Thank you, <morph.name>."
        ));
        dialoguePool.put("AMBITIOUS.SHOW_AFFECTION.FAIL", Arrays.asList(
                "Aren't you too close?",
                "No distractions.",
                "A little space, please.",
                "Later. I’m a little busy.",
                "This isn't the time."
        ));

        dialoguePool.put("AMBITIOUS.TALK.SUCCESS", Arrays.asList(
                "I’m going to be the best warrior… you’ll see.",
                "When I was younger... I tried to impress everyone by running ahead of everyone. I slipped and almost ate all the mud from the forest, and I learned something important... Strength is about patience.",
                "The clan needs cats who dream bigger. Like you, <morph.name>.",
                "Someday, I’ll lead something greater.",
                "There’s always more to learn. And there is always a better you to be. Never stop chasing your dreams, <morph.name>.",
                "Those Badgers will never stand a chance!.",
                "I had always wanted to be someone others can rely on. You can always count on me, <morph.name>.",
                "If I see those dogs, I will teach them a lesson!.",
                "How is your day, <morph.name>? Have you thought about expanding our territory?",
                "I don’t dream of power, you know? I dream of being useful when it matters most, of being there for everyone when they need it the most, just as they would be for me."
        ));
        dialoguePool.put("AMBITIOUS.TALK.FAIL", Arrays.asList(
                "I don’t have time for distractions.",
                "I need to focus.",
                "Talking will catch no prey.",
                "Not now. I’m thinking.",
                "We can talk when it's something important."
        ));


        dialoguePool.put("HUMBLE.GIVE_ITEM.SUCCESS", Arrays.asList(
                "Thank you, <morph.name>. I truly appreciate it.",
                "You are very kind.",
                "This means a lot to me.",
                "Thank you from the heart.",
                "Thanks, <morph.name>. I appreciate it lots. ",
                "I’m grateful.",
                "If you're sharing it with me... thanks."
        ));
        dialoguePool.put("HUMBLE.GIVE_ITEM.FAIL", Arrays.asList(
                "I don't want to waste it.",
                "Someone else might need it more.",
                "Please, keep it.",
                "Maybe another time.",
                "Thank you… but not now.",
                "You should keep it. You might need it more than I do.",
                "I'm fine without it, but thank you.",
                "Save it for someone who needs it more."
        ));

        dialoguePool.put("HUMBLE.SHOW_AFFECTION.SUCCESS", Arrays.asList(
                "Thank you… truly.",
                "That brings me peace.",
                "You’re very kind, <morph.name>.",
                "I'm thankful for you.",
                "That warms my heart.",
                "Thank you. I'm glad you're here."
        ));
        dialoguePool.put("HUMBLE.SHOW_AFFECTION.FAIL", Arrays.asList(
                "Maybe another time.",
                "Not now, sorry.",
                "Save that for someone better.",
                "No need to. Thanks anyway, <morph.name>!",
                "I shouldn't accept this.",
                "I appreciate it, but I'm fine."
        ));

        dialoguePool.put("HUMBLE.TALK.SUCCESS", Arrays.asList(
                "I try to do my best… every day. Have I been a good warrior, <morph.name>?",
                "I’m grateful for what I have. A home and friends to rely on.",
                "I don’t need much… just peace and a good friend like you, <morph.name>!",
                "Little moments like this mean a lot.",
                "As long as I’m useful, I’m happy to be here.",
                "I’m grateful to share my days with you and the clan, <morph.name>.",
                "Thank you for thinking of me, <morph.name>.",
                "I'm glad to be part of your clan.",
                "I like simple days. A clean den, a calm clan, and knowing I'm trying my best to be a honorable cat.",
                "When I was younger, I tried so hard to impress others... Now I just try to be useful, to be my best.",
                "I still remember my first successful hunt. It was small scrawny bird… barely enough for one. But I carried it like i just caught a fox."
        ));
        dialoguePool.put("HUMBLE.TALK.FAIL", Arrays.asList(
                "We can talk when I get some time.",
                "Tell me about it another time.",
                "We can leave it for later, okay?",
                "Sorry, not now. I need to think quietly.",
                "Maybe later… if that’s alright.",
                "I don't think I have much to talk right now."
        ));


        dialoguePool.put("RECKLESS.GIVE_ITEM.SUCCESS", Arrays.asList(
                "Nice! Thank you, <morph.name>.",
                "Did you catch this yourself?! Thank you, <morph.name>!",
                "Looks amazing! Thanks, <morph.name>.",
                "Yes! Just what I wanted!",
                "Throw it to me!",
                "Alright. Works for me."
        ));
        dialoguePool.put("RECKLESS.GIVE_ITEM.FAIL", Arrays.asList(
                "Oh come on! I could have caught a better one!",
                "I can help you hunt next time if that's all you could find.",
                "Do you need me to eat this?",
                "Give me something exciting.",
                "I'll pass, thank you anyway.",
                "Nah, I'll manage without it."
        ));

        dialoguePool.put("RECKLESS.SHOW_AFFECTION.SUCCESS", Arrays.asList(
                "Oh! Alright!",
                "Come here!",
                "I appreciate you too, <morph.name>.",
                "Aren't you a sweet smelly furball, <morph.name>?",
                "That's so sweet of you, <morph.name>!"
        ));
        dialoguePool.put("RECKLESS.SHOW_AFFECTION.FAIL", Arrays.asList(
                "Hey, too close.",
                "Don't hold me back!",
                "Do you know about personal space?",
                "You smell like mouse-bile.",
                "We can leave that for later."
        ));

        dialoguePool.put("RECKLESS.TALK.SUCCESS", Arrays.asList(
                "Shall we go hunt?",
                "If a badger shows up, I’m jumping first!",
                "Have you heard the dogs too? I promise I'll teach them a lesson!",
                "Standing still is boring. Could we go hunt?",
                "And trust me, if anything goes wrong... I'll deal with it!",
                "When I was a kit, they always warned me about bees. I never believed them, until one day I decided to bite one... I tell this story to every kit I see now.",
                "Once I chased a squirrel across the river. Next thing I knew was that my fur was already soaked and cold. I’d probably do it again...",
                "One time a fox snapped at me and I snapped back before thinking. My paws moved faster than I could think. That happens a lot to me.",
                "I once jumped over a fallen tree without knowing what was on the other side. Until I found myself stuck in brambles...",
                "When I was young, I climbed a tree higher than I should have. The view was beautiful. Getting down wasn’t so much."
        ));
        dialoguePool.put("RECKLESS.TALK.FAIL", Arrays.asList(
                "Talking? Boring!",
                "I’d rather be fighting or hunting.",
                "Nope. I’m busy being alive.",
                "Save the chatter for latter.",
                "I’m out. Peace.",
                "Sorry, I have better things to do."
        ));


        dialoguePool.put("CALM.GIFT", Arrays.asList(
                "Hey, <morph.name>, I thought you might like this.",
                "<morph.name>! This is for you!",
                "For you, I hope you like it.",
                "Here, this reminded me of you.",
                "Hey, <morph.name>! I got something for you."
        ));
        dialoguePool.put("GRUMPY.GIFT", Arrays.asList(
                "Just don't make a fuss over it.",
                "I thought you might like this.",
                "I don't expect a \"Thank you\".",
                "I hope you appreciate it.",
                "Here, enjoy, you're welcome."
        ));
        dialoguePool.put("CAUTIOUS.GIFT", Arrays.asList(
                "Hey, <morph.name>, this is for you.",
                "I got something for you.",
                "I wasn’t sure if you’d like it, but… here.",
                "Here… I think it might be useful.",
                "If you don’t like it, I can take it back."
        ));
        dialoguePool.put("INDEPENDENT.GIFT", Arrays.asList(
                "I got this myself. Hope you like it.",
                "<morph.name>, This is for you.",
                "I hope you like it.",
                "Here, enjoy.",
                "Sharing is caring, right?"
        ));
        dialoguePool.put("FRIENDLY.GIFT", Arrays.asList(
                "<morph.name>! I got something for you!",
                "Hey, this is for you.",
                "You are a nice friend, you deserve this.",
                "Hey, <morph.name>, I thought you might like this.",
                "I was thinking of giving you this for a while."
        ));
        dialoguePool.put("SHY.GIFT", Arrays.asList(
                "H-hey <morph.name>,  I thought you might like this.",
                "This is for you! Thank you for always being so nice.",
                "I hope this shows how much I appreciate you.",
                "For you!",
                "Um… I hope it’s okay."
        ));
        dialoguePool.put("AMBITIOUS.GIFT", Arrays.asList(
                "Now it's yours, I hope you appreciate it.",
                "Take this, you might need it more than me.",
                "Use it well.",
                "I expect you to make good use of it.",
                "Consider it an investment in you."
        ));
        dialoguePool.put("HUMBLE.GIFT", Arrays.asList(
                "It may not be much, but I tried my best.",
                "This is for you, I hope you like it!",
                "I hope this shows how much I appreciate your friendship.",
                "I told you I would return the favour!",
                "A little gift for a great friend like you."
        ));
        dialoguePool.put("RECKLESS.GIFT", Arrays.asList(
                "Hey, <morph.name>! See if this fits your likes!",
                "You looked a little lost in your thoughts. Maybe this will make you feel better!",
                "Hey, clumsy furball! Catch this!",
                "A little gift for a little furball.",
                "Hey, <morph.name>! Think fast!"
        ));


        dialoguePool.put("TALK.MATE.COMMON.SUCCESS", Arrays.asList(
                "Do you remember the day we met? I keep thinking about how peaceful it felt... just us.",
                "You always know how to make me feel better... I'm glad you're here.",
                "I trust you, <morph.name>. More than anyone else. I'm happy to be with you, I wanted you to know that.",
                "I saw something funny while I was out hunting the other day. I wished you were there so I could tell you about it.",
                "I didn't say it at the time... But when you came back safely that day, I felt so relieved.",
                "<morph.name>! I was waiting for you, any good news?",
                "You can always rely on me, <morph.name>. I'll always be by your side, no matter what.",
                "<morph.name>! How are you? Any good news?",
                "I’ve been thinking about our future lately, <morph.name>. Where do you think we’ll be seasons from now?",
                "Did you know that I love you, <morph.name>? Do not ever forget it.",
                "You have done so much for me, for us... One day I will return the favour.",
                "Hey, <morph.name>! Tell me about your day. I love listening to you.",
                "And remember, if something is ever bothering you, you can tell me. I will always want to carry it with you."
        ));
        dialoguePool.put("TALK.MATE.UNUSUAL.SUCCESS", Arrays.asList(
                "There was a time when I thought I’d never let anyone this close. Then you showed up... and everything changed.",
                "To me, you will always be my beloved dumb furball.",
                "Remember that day I let that squirrel escape? I still think you should have been faster than me, lazy furball!",
                "Hey, <morph.name>! I've missed you, where have you been?",
                "You can always rely on me, <morph.name>. I'll always be by your side, no matter what.",
                "Remember how we didn't get along at first? Who could have guessed...",
                "Sometimes I'm sorry I was so reluctant to be your friend at first, and then I remember you were always an annoying furball, but a really lovely one...",
                "<morph.name>! How are you? Have you eaten? Anything I could help you with?",
                "You stink a little, but I still love you, <morph.name>. I will always do.",
                "I have bad days sometimes... But you will always be who I care the most.",
                "Thank you for staying with me, <morph.name>, even when I’m so complicated.",
                "I still don't understand how you could choose such an annoying cat like me! But... I'm glad you did.",
                "I always love when we go out together. We should do it more often!"
        ));
        dialoguePool.put("TALK.MATE.SHY.SUCCESS", Arrays.asList(
                "<morph.name>! I-I wanted to ask... Do you feel as happy as I feel when you're with me too?",
                "H-hey! I missed you, where have you been? Have you eaten yet?",
                "Sometimes I feel lonely without you... I hope I can always stay by your side.",
                "I like quiet moments… where we don't need to talk at all, but everything still feels... Right.",
                "<morph.name>! Y-you have a little feather on your fur!",
                "If it wasn't for you... I probably would not talk to anyone at all...",
                "And if you ever feel lost... Or lonely like I used to, remember I will always love you, <morph.name>.",
                "O-oh, hey! I knew I could smell something sweet in the air.",
                "Thank you, <morph.name>… for staying by my side, even when I’m awkward with my feelings.",
                "I like being with you… even if we don’t talk much. It feels nice.",
                "I feel braver when you're here… like nothing bad could reach us.",
                "I might not say it too often but... I love you, <morph.name>!",
                "The other day I saw a butterfly, it was beautiful and it looked just like you! I wanted to catch it and save it forever, but then I remembered I already have you.",
                "You can always rely on me, always remember that, you sweet furball."
        ));

    }

/*
    private static final Map<Personality, Set<Item>> DESIRED_ITEMS = Map.of(
            Personality.CALM, Set.of(
                    Items.POPPY,
                    Items.ROSE_BUSH,


                    ModItems.SQUIRREL_FOOD.get(),
                    ModItems.MOUSE_FOOD.get(),
                    ModItems.PIGEON_FOOD.get(),
                    Items.MUTTON,
                    Items.RABBIT,
                    Items.BEEF,
                    Items.PORKCHOP,
                    Items.COD,
                    Items.SALMON,
                    Items.CHICKEN
            ),

            Personality.GRUMPY, Set.of(
                    Items.BOOK,
                    Items.LIGHT_BLUE_DYE,

                    ModItems.SQUIRREL_FOOD.get(),
                    ModItems.MOUSE_FOOD.get(),
                    ModItems.PIGEON_FOOD.get(),
                    Items.MUTTON,
                    Items.RABBIT,
                    Items.BEEF,
                    Items.PORKCHOP,
                    Items.COD,
                    Items.SALMON,
                    Items.CHICKEN
            ),

            Personality.CAUTIOUS, Set.of(
                    Items.COOKED_SALMON,
                    Items.SWEET_BERRIES
            ),

            Personality.INDEPENDENT, Set.of(
                    Items.COOKED_COD,
                    Items.CAKE
            ),

            Personality.FRIENDLY, Set.of(
                    Items.COOKED_COD,
                    Items.CAKE,

                    ModItems.SQUIRREL_FOOD.get(),
                    ModItems.MOUSE_FOOD.get(),
                    ModItems.PIGEON_FOOD.get(),
                    Items.MUTTON,
                    Items.RABBIT,
                    Items.BEEF,
                    Items.PORKCHOP,
                    Items.COD,
                    Items.SALMON,
                    Items.CHICKEN
            ),

            Personality.SHY, Set.of(
                    Items.COOKED_COD,
                    Items.CAKE,

                    ModItems.SQUIRREL_FOOD.get(),
                    ModItems.MOUSE_FOOD.get(),
                    ModItems.PIGEON_FOOD.get(),
                    Items.MUTTON,
                    Items.RABBIT,
                    Items.BEEF,
                    Items.PORKCHOP,
                    Items.COD,
                    Items.SALMON,
                    Items.CHICKEN
            ),

            Personality.AMBITIOUS, Set.of(
                    Items.COOKED_COD,
                    Items.CAKE,

                    ModItems.SQUIRREL_FOOD.get(),
                    ModItems.MOUSE_FOOD.get(),
                    ModItems.PIGEON_FOOD.get(),
                    Items.MUTTON,
                    Items.RABBIT,
                    Items.BEEF,
                    Items.PORKCHOP,
                    Items.COD,
                    Items.SALMON,
                    Items.CHICKEN
            ),

            Personality.HUMBLE, Set.of(
                    Items.COOKED_COD,
                    Items.CAKE,

                    ModItems.SQUIRREL_FOOD.get(),
                    ModItems.MOUSE_FOOD.get(),
                    ModItems.PIGEON_FOOD.get(),
                    Items.MUTTON,
                    Items.RABBIT,
                    Items.BEEF,
                    Items.PORKCHOP,
                    Items.COD,
                    Items.SALMON,
                    Items.CHICKEN
            ),

            Personality.RECKLESS, Set.of(
                    Items.COOKED_COD,
                    Items.CAKE,

                    ModItems.SQUIRREL_FOOD.get(),
                    ModItems.MOUSE_FOOD.get(),
                    ModItems.PIGEON_FOOD.get(),
                    Items.MUTTON,
                    Items.RABBIT,
                    Items.BEEF,
                    Items.PORKCHOP,
                    Items.COD,
                    Items.SALMON,
                    Items.CHICKEN
            )
    );
    */


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

    public String getRandomMateDialogue(Personality personality, InteractionResult result) {

        loadDialogueMap();
        String firstKey = "";

        firstKey = switch (personality) {
            case NONE -> "MATE.COMMON.";
            case CALM -> "MATE.COMMON.";
            case GRUMPY -> "MATE.UNUSUAL.";
            case CAUTIOUS -> "MATE.UNUSUAL.";
            case INDEPENDENT -> "MATE.COMMON.";
            case FRIENDLY -> "MATE.COMMON.";
            case SHY -> "MATE.SHY.";
            case AMBITIOUS -> "MATE.COMMON.";
            case HUMBLE -> "MATE.COMMON.";
            case RECKLESS -> "MATE.COMMON.";
        };

        String key = "TALK." + firstKey + result.name();

        List<String> options = dialoguePool.getOrDefault(key, Arrays.asList("..."));

        return options.get(this.random.nextInt(options.size()));
    }


    public String getRandomDialogue(Personality personality, CatInteraction type, InteractionResult result) {

        loadDialogueMap();

        String key = personality.name() + "." + type.name() + "." + result.name();

        List<String> options = dialoguePool.getOrDefault(key, Arrays.asList("..."));

        return options.get(this.random.nextInt(options.size()));
    }

    public String getRandomGiftDialogue(Personality personality) {
        loadDialogueMap();
        String key = personality.name() + ".GIFT";
        List<String> options = dialoguePool.getOrDefault(key, Arrays.asList("..."));
        return options.get(this.random.nextInt(options.size()));
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

    public void sendInteractionMessage(UUID playerUUID, String result) {
        ServerPlayer player = this.level().getServer().getPlayerList().getPlayer(playerUUID);
        String morphName = player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                .map(WCEPlayerData::getMorphName).orElse(player.getName().toString());
        String resultCooked = result.replace("<morph.name>", morphName);

        if (player != null) {
            Component name = this.hasCustomName() ?
                    Component.literal("<").append(this.getCustomName().copy().withStyle(ChatFormatting.WHITE)).append("> ")
                    :
                    Component.literal("<???> ");
            if (this.getRank() != KIT && this.getRank() != APPRENTICE) {
                player.sendSystemMessage(Component.empty().append(name.copy()).append(Component.literal(resultCooked)));
            }
        }
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

    public boolean randomInteractionResultProcess(UUID playerUUID, CatInteraction interaction) {

        if (interaction == CatInteraction.TALK) {
            if (this.getMateUUID() != null) {
                if (this.getMateUUID().equals(playerUUID)) {
                    if (this.random.nextFloat() <= 0.85 + getMoodInteractionAddition()) {
                        String dialogue = this.getRandomMateDialogue(this.getPersonality(), InteractionResult.SUCCESS);
                        this.setFriendshipLevel(playerUUID, this.getFriendshipLevel(playerUUID) + 2);
                        this.sendInteractionMessage(playerUUID, dialogue);
                        this.randomImproveMood(playerUUID);
                        return true;
                    } else {
                        String dialogue = this.getRandomDialogue(Personality.CALM, CatInteraction.TALK, InteractionResult.FAIL);
                        this.sendInteractionMessage(playerUUID, dialogue);
                        return false;
                    }
                }
            }
            if (this.getPersonality() == Personality.CALM) {
                if (this.random.nextFloat() <= 0.7 + getMoodInteractionAddition() + ((double) this.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(Personality.CALM, CatInteraction.TALK, InteractionResult.SUCCESS);
                    this.setFriendshipLevel(playerUUID, this.getFriendshipLevel(playerUUID) + 2);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    this.randomImproveMood(playerUUID);
                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(Personality.CALM, CatInteraction.TALK, InteractionResult.FAIL);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    return false;
                }
            } else if (this.getPersonality() == Personality.GRUMPY) {
                if (this.random.nextFloat() <= 0.4 + getMoodInteractionAddition() + ((double) this.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(Personality.GRUMPY, CatInteraction.TALK, InteractionResult.SUCCESS);
                    this.setFriendshipLevel(playerUUID, this.getFriendshipLevel(playerUUID) + 5);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    this.randomImproveMood(playerUUID);
                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(Personality.GRUMPY, CatInteraction.TALK, InteractionResult.FAIL);
                    this.setFriendshipLevel(playerUUID, this.getFriendshipLevel(playerUUID) - 2);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            } else if (this.getPersonality() == Personality.CAUTIOUS) {
                if (this.random.nextFloat() <= 0.7 + getMoodInteractionAddition() + ((double) this.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(Personality.CAUTIOUS, CatInteraction.TALK, InteractionResult.SUCCESS);
                    this.setFriendshipLevel(playerUUID, this.getFriendshipLevel(playerUUID) + 2);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    this.randomImproveMood(playerUUID);

                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(Personality.CAUTIOUS, CatInteraction.TALK, InteractionResult.FAIL);
                    this.setFriendshipLevel(playerUUID, this.getFriendshipLevel(playerUUID) - 2);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            } else if (this.getPersonality() == Personality.INDEPENDENT) {
                if (this.random.nextFloat() <= 0.8 + getMoodInteractionAddition() + ((double) this.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(Personality.INDEPENDENT, CatInteraction.TALK, InteractionResult.SUCCESS);
                    this.setFriendshipLevel(playerUUID, this.getFriendshipLevel(playerUUID) + 2);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    this.randomImproveMood(playerUUID);

                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(Personality.INDEPENDENT, CatInteraction.TALK, InteractionResult.FAIL);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            } else if (this.getPersonality() == Personality.FRIENDLY) {
                if (this.random.nextFloat() <= 0.8 + getMoodInteractionAddition() + ((double) this.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(Personality.FRIENDLY, CatInteraction.TALK, InteractionResult.SUCCESS);
                    this.setFriendshipLevel(playerUUID, this.getFriendshipLevel(playerUUID) + 4);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    this.randomImproveMood(playerUUID);

                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(Personality.FRIENDLY, CatInteraction.TALK, InteractionResult.FAIL);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            } else if (this.getPersonality() == Personality.SHY) {
                if (this.random.nextFloat() <= 0.7 + getMoodInteractionAddition() + ((double) this.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(Personality.SHY, CatInteraction.TALK, InteractionResult.SUCCESS);
                    this.setFriendshipLevel(playerUUID, this.getFriendshipLevel(playerUUID) + 3);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    this.randomImproveMood(playerUUID);

                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(Personality.SHY, CatInteraction.TALK, InteractionResult.FAIL);
                    this.setFriendshipLevel(playerUUID, this.getFriendshipLevel(playerUUID) - 1);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            } else if (this.getPersonality() == Personality.AMBITIOUS) {
                if (this.random.nextFloat() <= 0.7 + getMoodInteractionAddition() + ((double) this.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(Personality.AMBITIOUS, CatInteraction.TALK, InteractionResult.SUCCESS);
                    this.setFriendshipLevel(playerUUID, this.getFriendshipLevel(playerUUID) + 2);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    this.randomImproveMood(playerUUID);

                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(Personality.AMBITIOUS, CatInteraction.TALK, InteractionResult.FAIL);
                    this.setFriendshipLevel(playerUUID, this.getFriendshipLevel(playerUUID) - 1);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            } else if (this.getPersonality() == Personality.HUMBLE) {
                if (this.random.nextFloat() <= 0.9 + getMoodInteractionAddition() + ((double) this.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(Personality.HUMBLE, CatInteraction.TALK, InteractionResult.SUCCESS);
                    this.setFriendshipLevel(playerUUID, this.getFriendshipLevel(playerUUID) + 2);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    this.randomImproveMood(playerUUID);

                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(Personality.HUMBLE, CatInteraction.TALK, InteractionResult.FAIL);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            } else if (this.getPersonality() == Personality.RECKLESS) {
                if (this.random.nextFloat() <= 0.7 + getMoodInteractionAddition() + ((double) this.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(Personality.RECKLESS, CatInteraction.TALK, InteractionResult.SUCCESS);
                    this.setFriendshipLevel(playerUUID, this.getFriendshipLevel(playerUUID) + 2);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    this.randomImproveMood(playerUUID);

                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(Personality.RECKLESS, CatInteraction.TALK, InteractionResult.FAIL);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            }

        } else if (interaction == CatInteraction.GIVE_ITEM) {

            if (this.getPersonality() == Personality.CALM) {
                if (this.random.nextFloat() <= 0.7 + getMoodInteractionAddition() + ((double) this.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(Personality.CALM, CatInteraction.GIVE_ITEM, InteractionResult.SUCCESS);
                    this.setFriendshipLevel(playerUUID, this.getFriendshipLevel(playerUUID) + 4);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    this.randomImproveMood(playerUUID);

                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(Personality.CALM, CatInteraction.GIVE_ITEM, InteractionResult.FAIL);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            } else if (this.getPersonality() == Personality.GRUMPY) {
                if (this.random.nextFloat() <= 0.45 + getMoodInteractionAddition() + ((double) this.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(Personality.GRUMPY, CatInteraction.GIVE_ITEM, InteractionResult.SUCCESS);
                    this.setFriendshipLevel(playerUUID, this.getFriendshipLevel(playerUUID) + 5);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    this.randomImproveMood(playerUUID);

                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(Personality.GRUMPY, CatInteraction.GIVE_ITEM, InteractionResult.FAIL);
                    this.setFriendshipLevel(playerUUID, this.getFriendshipLevel(playerUUID) - 2);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            } else if (this.getPersonality() == Personality.CAUTIOUS) {
                if (this.random.nextFloat() <= 0.40 + getMoodInteractionAddition() + ((double) this.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(Personality.CAUTIOUS, CatInteraction.GIVE_ITEM, InteractionResult.SUCCESS);
                    this.setFriendshipLevel(playerUUID, this.getFriendshipLevel(playerUUID) + 4);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    this.randomImproveMood(playerUUID);

                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(Personality.CAUTIOUS, CatInteraction.GIVE_ITEM, InteractionResult.FAIL);
                    this.setFriendshipLevel(playerUUID, this.getFriendshipLevel(playerUUID) - 3);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            } else if (this.getPersonality() == Personality.INDEPENDENT) {
                if (this.random.nextFloat() <= 0.3 - getMoodInteractionAddition() + ((double) this.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(Personality.INDEPENDENT, CatInteraction.GIVE_ITEM, InteractionResult.SUCCESS);
                    this.setFriendshipLevel(playerUUID, this.getFriendshipLevel(playerUUID) + 2);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    this.randomImproveMood(playerUUID);

                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(Personality.INDEPENDENT, CatInteraction.GIVE_ITEM, InteractionResult.FAIL);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            } else if (this.getPersonality() == Personality.FRIENDLY) {
                if (this.random.nextFloat() <= 0.8 + getMoodInteractionAddition() + ((double) this.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(Personality.FRIENDLY, CatInteraction.GIVE_ITEM, InteractionResult.SUCCESS);
                    this.setFriendshipLevel(playerUUID, this.getFriendshipLevel(playerUUID) + 4);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    this.randomImproveMood(playerUUID);

                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(Personality.FRIENDLY, CatInteraction.GIVE_ITEM, InteractionResult.FAIL);
                    this.setFriendshipLevel(playerUUID, this.getFriendshipLevel(playerUUID) + 1);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            } else if (this.getPersonality() == Personality.SHY) {
                if (this.random.nextFloat() <= 0.7 + getMoodInteractionAddition() + ((double) this.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(Personality.SHY, CatInteraction.GIVE_ITEM, InteractionResult.SUCCESS);
                    this.setFriendshipLevel(playerUUID, this.getFriendshipLevel(playerUUID) + 5);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    this.randomImproveMood(playerUUID);

                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(Personality.SHY, CatInteraction.GIVE_ITEM, InteractionResult.FAIL);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            } else if (this.getPersonality() == Personality.AMBITIOUS) {
                if (this.random.nextFloat() <= 0.8 + getMoodInteractionAddition() + ((double) this.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(Personality.AMBITIOUS, CatInteraction.GIVE_ITEM, InteractionResult.SUCCESS);
                    this.setFriendshipLevel(playerUUID, this.getFriendshipLevel(playerUUID) + 2);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    this.randomImproveMood(playerUUID);
                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(Personality.AMBITIOUS, CatInteraction.GIVE_ITEM, InteractionResult.FAIL);
                    this.setFriendshipLevel(playerUUID, this.getFriendshipLevel(playerUUID) - 3);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            } else if (this.getPersonality() == Personality.HUMBLE) {
                if (this.random.nextFloat() <= 0.8 + getMoodInteractionAddition() + ((double) this.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(Personality.HUMBLE, CatInteraction.GIVE_ITEM, InteractionResult.SUCCESS);
                    this.setFriendshipLevel(playerUUID, this.getFriendshipLevel(playerUUID) + 5);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    this.randomImproveMood(playerUUID);

                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(Personality.HUMBLE, CatInteraction.GIVE_ITEM, InteractionResult.FAIL);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            } else if (this.getPersonality() == Personality.RECKLESS) {
                if (this.random.nextFloat() <= 0.5 + getMoodInteractionAddition() + ((double) this.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(Personality.RECKLESS, CatInteraction.GIVE_ITEM, InteractionResult.SUCCESS);
                    this.setFriendshipLevel(playerUUID, this.getFriendshipLevel(playerUUID) + 3);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    this.randomImproveMood(playerUUID);

                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(Personality.RECKLESS, CatInteraction.GIVE_ITEM, InteractionResult.FAIL);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            }

        } else if (interaction == CatInteraction.SHOW_AFFECTION) {


            if (this.getPersonality() == Personality.CALM) {
                if (this.random.nextFloat() <= 0.7 + getMoodInteractionAddition() + ((double) this.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(Personality.CALM, CatInteraction.SHOW_AFFECTION, InteractionResult.SUCCESS);
                    this.setFriendshipLevel(playerUUID, this.getFriendshipLevel(playerUUID) + 3);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    this.randomImproveMood(playerUUID);

                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(Personality.CALM, CatInteraction.SHOW_AFFECTION, InteractionResult.FAIL);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            } else if (this.getPersonality() == Personality.GRUMPY) {
                if (this.random.nextFloat() <= 0.25 + getMoodInteractionAddition() + ((double) this.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(Personality.GRUMPY, CatInteraction.SHOW_AFFECTION, InteractionResult.SUCCESS);
                    this.setFriendshipLevel(playerUUID, this.getFriendshipLevel(playerUUID) + 7);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    this.randomImproveMood(playerUUID);

                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(Personality.GRUMPY, CatInteraction.SHOW_AFFECTION, InteractionResult.FAIL);
                    this.setFriendshipLevel(playerUUID, this.getFriendshipLevel(playerUUID) - 3);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            } else if (this.getPersonality() == Personality.CAUTIOUS) {
                if (this.random.nextFloat() <= 0.3 + getMoodInteractionAddition() + ((double) this.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(Personality.CAUTIOUS, CatInteraction.SHOW_AFFECTION, InteractionResult.SUCCESS);
                    this.setFriendshipLevel(playerUUID, this.getFriendshipLevel(playerUUID) + 4);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    this.randomImproveMood(playerUUID);

                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(Personality.CAUTIOUS, CatInteraction.SHOW_AFFECTION, InteractionResult.FAIL);
                    this.setFriendshipLevel(playerUUID, this.getFriendshipLevel(playerUUID) - 2);

                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            } else if (this.getPersonality() == Personality.INDEPENDENT) {
                if (this.random.nextFloat() <= 0.6 + getMoodInteractionAddition() + ((double) this.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(Personality.INDEPENDENT, CatInteraction.SHOW_AFFECTION, InteractionResult.SUCCESS);
                    this.setFriendshipLevel(playerUUID, this.getFriendshipLevel(playerUUID) + 2);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    this.randomImproveMood(playerUUID);

                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(Personality.INDEPENDENT, CatInteraction.SHOW_AFFECTION, InteractionResult.FAIL);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            } else if (this.getPersonality() == Personality.FRIENDLY) {
                if (this.random.nextFloat() <= 0.8 + getMoodInteractionAddition() + ((double) this.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(Personality.FRIENDLY, CatInteraction.SHOW_AFFECTION, InteractionResult.SUCCESS);
                    this.setFriendshipLevel(playerUUID, this.getFriendshipLevel(playerUUID) + 6);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    this.randomImproveMood(playerUUID);

                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(Personality.FRIENDLY, CatInteraction.SHOW_AFFECTION, InteractionResult.FAIL);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            } else if (this.getPersonality() == Personality.SHY) {
                if (this.random.nextFloat() <= 0.8 + getMoodInteractionAddition() + ((double) this.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(Personality.SHY, CatInteraction.SHOW_AFFECTION, InteractionResult.SUCCESS);
                    this.setFriendshipLevel(playerUUID, this.getFriendshipLevel(playerUUID) + 2);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    this.randomImproveMood(playerUUID);

                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(Personality.SHY, CatInteraction.SHOW_AFFECTION, InteractionResult.FAIL);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            } else if (this.getPersonality() == Personality.AMBITIOUS) {
                if (this.random.nextFloat() <= 0.6 + getMoodInteractionAddition() + ((double) this.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(Personality.AMBITIOUS, CatInteraction.SHOW_AFFECTION, InteractionResult.SUCCESS);
                    this.setFriendshipLevel(playerUUID, this.getFriendshipLevel(playerUUID) + 1);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    this.randomImproveMood(playerUUID);

                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(Personality.AMBITIOUS, CatInteraction.SHOW_AFFECTION, InteractionResult.FAIL);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            } else if (this.getPersonality() == Personality.HUMBLE) {
                if (this.random.nextFloat() <= 0.8 + getMoodInteractionAddition() + ((double) this.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(Personality.HUMBLE, CatInteraction.SHOW_AFFECTION, InteractionResult.SUCCESS);
                    this.setFriendshipLevel(playerUUID, this.getFriendshipLevel(playerUUID) + 3);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    this.randomImproveMood(playerUUID);

                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(Personality.HUMBLE, CatInteraction.SHOW_AFFECTION, InteractionResult.FAIL);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            } else if (this.getPersonality() == Personality.RECKLESS) {
                if (this.random.nextFloat() <= 0.6 + getMoodInteractionAddition() + ((double) this.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(Personality.RECKLESS, CatInteraction.SHOW_AFFECTION, InteractionResult.SUCCESS);
                    this.setFriendshipLevel(playerUUID, this.getFriendshipLevel(playerUUID) + 2);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    this.randomImproveMood(playerUUID);

                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(Personality.RECKLESS, CatInteraction.SHOW_AFFECTION, InteractionResult.FAIL);
                    this.setFriendshipLevel(playerUUID, this.getFriendshipLevel(playerUUID) - 1);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            }

        }

        return false;
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

//    private static final String[] PREFIX_1 = {
//            // 1, 10, 11
//            //MultiColor
//            "Leaf", "Marble", "Fleck", "Dapple", "Spotted",
//            "Tawny", "Mottle", "Speckle", "Brindle", "Splotch",
//            "Bumble", "Bright", "Sun", "Rainbow", "Pollen",
//            "Drift", "Freckle", "Blotch", "Petal", "Shimmer",
//            "Bloom"
//
//    };
//    private static final String[] PREFIX_2 = {
//            // 2, 3, 9
//            // Gray and cream
//            "Pearl", "Mist", "Ivory", "Silk", "Feather",
//            "Leaf", "Ash", "Fawn", "Soft", "Frost", "Snow",
//            "Cloud", "Storm", "Sparrow", "Rat", "Bengal",
//            "Willow", "Sand", "Dove", "Smoke", "Haze",
//            "Drizzle", "Silver"
//    };
//    private static final String[] PREFIX_3 = {
//            // 4, 8
//            // Tabbies
//            "Tiger", "Flame", "Ember", "Bracken", "Fire",
//            "Oak", "Rust", "Maple", "Amber", "Hare",
//            "Lion", "Dawn", "Dark", "Bumble", "Mole",
//            "Sun", "Blaze", "Chestnut", "Fox", "Cinder",
//            "Scorch", "Rowan", "Thorn", "Soot", "Red"
//    };
//    private static final String[] PREFIX_4 = {
//            // 5, 6, 7, 12
//            // Stone colors
//            "Stone", "Jay", "Pebble", "Dark", "Ash",
//            "Night", "Dust", "Mist", "Swift", "Shade",
//            "Holly", "Mole", "Storm", "Sparrow", "Twitch",
//            "Slate", "Raven", "Crow", "Dusk"
//
//
//    };
//
//
//    private static final String[] SUFIX = {
//            "claw", "fur", "feather", "pelt", "eye",
//            "heart", "tail", "wing", "whisker", "blaze",
//            "fang", "shade", "step", "fall", "song",
//            "stripe", "light", "leap", "foot", "spring",
//            "pit", "stream", "patch", "scar", "ear",
//            "frost"
//    };

    /**
     * Depending on the variant, pick a set of prefixes
     */
    private String[] getPrefixForVariant() {
//        return switch (variant) {
//            case 0 -> PREFIX_1;
//            case 1 -> PREFIX_2;
//            case 2 -> PREFIX_2;
//            case 3 -> PREFIX_3;
//            case 4 -> PREFIX_4;
//            case 5 -> PREFIX_4;
//            case 6 -> PREFIX_4;
//            case 7 -> PREFIX_3;
//            case 8 -> PREFIX_2;
//            case 9 -> PREFIX_1;
//            case 10 -> PREFIX_1;
//            case 11 -> PREFIX_4;
//            case 12 -> PREFIX_3; // chestnutpatch
//            case 13 -> PREFIX_2; // ratstar
//            case 14 -> PREFIX_4; // twitchstream
//            case 15 -> PREFIX_3; // blazepit
//            case 16 -> PREFIX_2; // bengalpelt
//            case 17 -> PREFIX_2; // sparrowstar
//            case 18 -> PREFIX_3; // foxeater
//            case 19 -> PREFIX_2; // willowsong
//            case 20 -> PREFIX_2; //13
//            case 21 -> PREFIX_2; //14
//            case 22 -> PREFIX_4; //15
//            case 23 -> PREFIX_3; //16
//            case 24 -> PREFIX_2; //17
//            case 25 -> PREFIX_4; //18
//            case 26 -> PREFIX_3; //19
//            case 27 -> PREFIX_2; //20
//            case 28 -> PREFIX_3; //21
//            case 29 -> PREFIX_1; //22
//
//            default -> PREFIX_1; // fallback
//        };
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
     * Otherwise return false.
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
                    || (target instanceof LizardTailEntity || (target instanceof LizardEntity lizard && !lizard.isTame() && this.isBaby()))
                    || target.getType().is(ModTags.EntityTypes.PREY_MOBS));
        });

        this.monsterTarget = new NearestAttackableTargetGoal<>(this, LivingEntity.class,
                10, false, false,
                target -> target instanceof Monster && !(target instanceof Creeper || target instanceof Piglin || target instanceof ZombifiedPiglin || target instanceof PiglinBrute)
                        && this.distanceTo(target) <= getThreatDetectionRange() && willAttackMonsters() && this.getRank() != KIT
                        && this.mode != CatMode.SIT && !this.returnHomeFlag && target.isAlive() && !target.isDeadOrDying());


//        this.mossBallTarget = new NearestAttackableTargetGoal<>(this, MossBallEntity.class,
//                10, false, false,
//                target -> target instanceof MossBallEntity mossBall && );


        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(0, new WCGoals.WCatReturnHomeGoal(this, 1.1D));
        this.goalSelector.addGoal(0, new WCGoals.WCatLeaderCallsGoal(this));

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


    /**
     * This is just too much, if you have any questions about the mobinteract just ask me, i aint writing all this :sob:
     */
    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);

        if (pPlayer.level().isClientSide) {
            LeapClientState.setCanceled();
        }

        if (this.isOwnedBy(pPlayer)) {
            if (pPlayer instanceof ServerPlayer sPlayer) {
                UUID currentUUID = sPlayer.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                        .map(WCEPlayerData::getCurrentClanUUID).orElse(ClanData.EMPTY_UUID);


                if (!this.getClanUUID().equals(currentUUID)) {
                    this.setClanUUID(currentUUID);
                    if (pPlayer instanceof ServerPlayer serverPlayer) {
                        ClanData data = ClanData.get(serverPlayer.serverLevel().getServer().overworld());
                        data.addClanCat(currentUUID, this);
                    }
                }
            }
        }

        if (DiseaseManager.healDiseaseMobInteract(itemstack, this)) {
            return InteractionResult.SUCCESS;
        }

        if (this.getRank() == MEDICINE && this.isTame() && this.getOwner() == pPlayer) {
            if (PlayerShape.getCurrentShape(pPlayer) instanceof Animal) {
                if (!pPlayer.getItemInHand(pHand).isEmpty()) {
                    if (pPlayer.getItemInHand(pHand).is(ModItems.DOCK_LEAVES.get())) {
                        medicineCatScentsBlock(pPlayer, ModBlocks.DOCK.get(), 40);
                        return InteractionResult.SUCCESS;
                    }
                    if (pPlayer.getItemInHand(pHand).is(ModItems.SORREL.get())) {
                        medicineCatScentsBlock(pPlayer, ModBlocks.SORRELPLANT.get(), 40);
                        return InteractionResult.SUCCESS;
                    }
                    if (pPlayer.getItemInHand(pHand).is(ModItems.BURNET.get())) {
                        medicineCatScentsBlock(pPlayer, ModBlocks.BURNETPLANT.get(), 40);
                        return InteractionResult.SUCCESS;
                    }
                    if (pPlayer.getItemInHand(pHand).is(ModItems.CHAMOMILE.get())) {
                        medicineCatScentsBlock(pPlayer, ModBlocks.CHAMOMILEPLANT.get(), 40);
                        return InteractionResult.SUCCESS;
                    }
                    if (pPlayer.getItemInHand(pHand).is(ModItems.DAISY.get())) {
                        medicineCatScentsBlock(pPlayer, ModBlocks.DAISYPLANT.get(), 40);
                        return InteractionResult.SUCCESS;
                    }
                    if (pPlayer.getItemInHand(pHand).is(ModItems.CATMINT.get())) {
                        medicineCatScentsBlock(pPlayer, ModBlocks.CATMINTPLANT.get(), 40);
                        return InteractionResult.SUCCESS;
                    }
                    if (pPlayer.getItemInHand(pHand).is(ModItems.YARROW.get())) {
                        medicineCatScentsBlock(pPlayer, ModBlocks.YARROWPLANT.get(), 40);
                        return InteractionResult.SUCCESS;
                    }
                    if (pPlayer.getItemInHand(pHand).is(ModItems.GLOW_SHROOM.get())) {
                        medicineCatScentsBlock(pPlayer, ModBlocks.GLOWSHROOM.get(), 40);
                        return InteractionResult.SUCCESS;
                    }
                    if (pPlayer.getItemInHand(pHand).is(ModItems.JUNIPER_BERRIES.get())) {
                        medicineCatScentsBlock(pPlayer, ModBlocks.JUNIPERPLANT.get(), 40);
                        return InteractionResult.SUCCESS;
                    }
                    if (pPlayer.getItemInHand(pHand).is(ModItems.COMFREY_LEAVES.get())
                    || pPlayer.getItemInHand(pHand).is(ModItems.COMFREY_ROOT.get())
                    ) {
                        medicineCatScentsBlock(pPlayer, ModBlocks.COMFREYPLANT.get(), 40);
                        return InteractionResult.SUCCESS;
                    }
                    if (pPlayer.getItemInHand(pHand).is(ModItems.FEVERFEW.get())) {
                        medicineCatScentsBlock(pPlayer, ModBlocks.FEVERFEWPLANT.get(), 40);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }

        if ((itemstack.is(ModItems.CLAWS.get()))) {

            if (!level().isClientSide && pPlayer instanceof ServerPlayer sPlayer) {

                if (PlayerShape.getCurrentShape(sPlayer) instanceof Animal) {
                    if (!this.isBaby() && pPlayer.isShiftKeyDown() && this.isTame() && (this.getOwner() == pPlayer)) {
                        Component catInvName = this.getCustomName();
                        NetworkHooks.openScreen(
                                sPlayer,
                                new SimpleMenuProvider(
                                        (id, inv, player) -> new WCatMenu(id, inv, this),
                                        Component.literal(catInvName.getString())
                                ),
                                buf -> buf.writeInt(this.getId())
                        );
                    }

                    if (this.isBaby() && this.getRank() == KIT) {
                        if (!level().isClientSide) {
                            this.startRiding(pPlayer, true);
                            this.isBeingCarried = true;
                        }
                        return InteractionResult.sidedSuccess(level().isClientSide);
                    }
                }
            }

            return InteractionResult.sidedSuccess(level().isClientSide);
        }

        if (!this.isTame() && itemstack.is(ModItems.FRESHKILL_AND_HERBS_BUNDLE.get())) {

            if (!this.level().isClientSide()) {

                if (!pPlayer.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }

                int tameRoll;

                if (this.getPersonality() == Personality.CAUTIOUS) {
                    tameRoll = this.random.nextInt(6);
//                    pPlayer.sendSystemMessage(Component.literal("Cautious"));
                } else if (this.getPersonality() == Personality.SHY) {
                    tameRoll = this.random.nextInt(4);
//                    pPlayer.sendSystemMessage(Component.literal("Shy"));
                } else if (this.getPersonality() == Personality.FRIENDLY) {
                    tameRoll = 0;
//                    pPlayer.sendSystemMessage(Component.literal("Friendly"));
                } else {
                    tameRoll = this.random.nextInt(2);
//                    pPlayer.sendSystemMessage(Component.literal("Other"));
                }

                if (tameRoll == 0) {
                    this.tame(pPlayer);
                    this.level().broadcastEntityEvent(this, (byte) 7);

                    if (!this.hasCustomName()) {
                        int variant = this.getVariant();
                        String[] prefixSet = getPrefixForVariant();

                        String genderS;
                        if (this.getGender() == 0) {
                            genderS = " ♂";
                        } else {
                            genderS = " ♀";
                        }

                        int i = this.random.nextInt(prefixSet.length);
                        int j = this.random.nextInt(SUFIXES.length);

                        String finalName;
                        if (this.isBaby()) {
                            finalName = prefixSet[i] + "paw" + genderS;
                            this.setRank(APPRENTICE);
                        } else {
                            finalName = prefixSet[i] + SUFIXES[j] + genderS;
                            this.setRank(WARRIOR);
                        }
                        //String finalName = prefixSet[i] + SUFIX[j] + genderS;

                        this.setCustomName(Component.literal(finalName));
                        this.setCustomNameVisible(true);
                        this.setPrefix(Component.literal(prefixSet[i]));

                        this.setNameColor(this.getRank());

                    }

                    this.setNameColor(this.getRank());


                    pPlayer.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA).ifPresent(cap -> {
                        String clanName = cap.getClanName(pPlayer.level());
                        if (this.getRank() != NONE) this.setClan(Component.literal(clanName));
                        this.setClanUUID(cap.getCurrentClanUUID());

                        if (this.level() instanceof ServerLevel sLevel) {
                            ClanData data = ClanData.get(sLevel);
                            ClanData.Clan clan = data.getClan(cap.getCurrentClanUUID());

                            if (clan != null) {
                                Component catJoinedClanLog = Component.empty()
                                        .append(this.hasCustomName() ? this.getCustomName().copy() : Component.literal("A Cat"))
                                        .append(" has joined ")
                                        .append(Component.literal(clan.name).withStyle(Style.EMPTY.withColor(clan.color)))
                                        .append("!");

                                this.registerClanLog(catJoinedClanLog);
                            }
                        }


                    });

                    this.rewardGeneticsAdvancements();
                    this.rewardMoonmoon();

                    mode = CatMode.FOLLOW;
                    sendModeMessage(pPlayer);

                } else {
                    this.level().broadcastEntityEvent(this, (byte) 6);
                    this.setCustomName(null);
                }
            }


            this.gameEvent(GameEvent.EAT);
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }

        if (this.isTame() && pPlayer.isShiftKeyDown() && pPlayer.getMainHandItem().isEmpty()) {

            if (!this.level().isClientSide() && pPlayer instanceof ServerPlayer sPlayer) {
                ClanData data = ClanData.get(sPlayer.serverLevel().getServer().overworld());
                UUID clanUUID = sPlayer.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                        .map(WCEPlayerData::getCurrentClanUUID).orElse(ClanData.EMPTY_UUID);
                ClanData.Clan clan = data.getClan(clanUUID);
                boolean clanExistsAndIsValid = (!clanUUID.equals(ClanData.EMPTY_UUID) && clan != null);
                boolean canAlsoCommand = false;

                if (clanExistsAndIsValid) canAlsoCommand
                        = data.canCommandWarriors(clan, sPlayer.getUUID()) && this.getClanUUID().equals(clanUUID) && !Objects.equals(this.getOwnerUUID(), pPlayer.getUUID());

                if (pPlayer.getUUID().equals(this.getOwnerUUID()) || canAlsoCommand) {

                    if (canAlsoCommand) {
                        this.setOwnerUUID(pPlayer.getUUID());

                        String morphName = pPlayer.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                                .map(WCEPlayerData::getMorphName).orElse(pPlayer.getName().getString());

                        Component message = Component.empty()
                                .append(Component.literal(morphName).withStyle(ChatFormatting.AQUA))
                                .append(Component.literal("(").withStyle(ChatFormatting.GRAY))
                                .append(Component.literal(pPlayer.getName().getString()).withStyle(ChatFormatting.GRAY))
                                .append(Component.literal(")").withStyle(ChatFormatting.GRAY))
                                .append(" has taken ")
                                .append(this.hasCustomName() ? this.getCustomName().copy() : Component.literal("a cat"));
                        this.registerClanLog(message);
                    }

                    switch (mode) {
                        case SIT:
                            mode = CatMode.FOLLOW;
                            this.setInSittingPose(false);
                            break;
                        case FOLLOW:
                            mode = CatMode.WANDER;
                            wanderCenter = this.blockPosition();
                            break;
                        case WANDER:
                            mode = CatMode.SIT;
                            this.setInSittingPose(true);
                            break;
                    }
                    sendModeMessage(pPlayer);
                }
                return InteractionResult.SUCCESS;
            }
        }

        if (this.isTame() && pPlayer.isShiftKeyDown() && pPlayer.getUUID().equals(this.getOwnerUUID())
                && itemstack.is(ModItems.WHISKERS.get()) && (this.getRank() != KIT)) {
            Rank current = this.getRank();

            switch (current) {
                case NONE:
                    this.setRank(APPRENTICE);
                    break;
                case APPRENTICE:
                    this.setRank(WARRIOR);
                    break;
                case WARRIOR:
                    this.setRank(MEDICINE);
                    break;
                case MEDICINE:
                    this.setRank(DEPUTY);
                    break;
                case DEPUTY:
                    this.setRank(NONE);
                    break;
            }
            updateClanCatData();

            this.setNameColor(this.getRank());


            String morphName = pPlayer.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                    .map(WCEPlayerData::getMorphName).orElse(pPlayer.getName().getString());

            Component message = Component.empty()
                    .append(Component.literal(morphName).withStyle(ChatFormatting.AQUA))
                    .append(Component.literal("(").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(pPlayer.getName().getString()).withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(")").withStyle(ChatFormatting.GRAY))
                    .append(" has changed ")
                    .append(this.hasCustomName() ? this.getCustomName().copy() : Component.literal("A cat"))
                    .append("'s rank to ")
                    .append(Component.literal(this.getRank().name()).withStyle(ChatFormatting.YELLOW));
            this.registerClanLog(message);
            this.updateClanCatData();

            sendRankMessage(pPlayer);
            return InteractionResult.SUCCESS;
        }


        if (this.isTame() && itemstack.is(ModItems.CATMINT.get()) &&
                this.getRank() != MEDICINE && !this.isExpectingKits() && !this.isBaby()) {

            if (!this.level().isClientSide()) {
                if (((ServerLevel) this.level()).getEntity(this.getMateUUID()) instanceof Player) {
                    return InteractionResult.PASS;
                }
            }

            if (!pPlayer.getAbilities().instabuild) itemstack.shrink(1);

            this.setInLove(pPlayer);


            return InteractionResult.SUCCESS;
        }

        if (this.isTame() && itemstack.is(ModItems.WARRIORNAMERANDOMIZER.get())
                && pPlayer.isShiftKeyDown()) {

            if (!this.level().isClientSide()) {

                if (this.getVariant() == 12 || this.getVariant() == 13 || this.getVariant() == 14
                        || this.getVariant() == 15 || this.getVariant() == 16 || this.getVariant() == 17
                        || this.getVariant() == 18 || this.getVariant() == 19) {

                    Component oldName = this.hasCustomName() ? this.getCustomName().copy() : Component.literal("A cat");

                    itemstack.hurtAndBreak(1, pPlayer, (p) ->
                            p.broadcastBreakEvent(pHand));
                    String genderV;
                    String finalName;
                    String prefixForVariant = "";
                    String suffixForVariant = "";

                    if (this.getVariant() == 12) {
                        prefixForVariant = "Chestnut";
                        suffixForVariant = "patch";
                    } else if (this.getVariant() == 13) {
                        prefixForVariant = "Rat";
                        suffixForVariant = "star";
                    } else if (this.getVariant() == 14) {
                        prefixForVariant = "Twitch";
                        suffixForVariant = "stream";
                    } else if (this.getVariant() == 15) {
                        prefixForVariant = "Blaze";
                        suffixForVariant = "pit";
                    } else if (this.getVariant() == 16) {
                        prefixForVariant = "Bengal";
                        suffixForVariant = "pelt";
                    } else if (this.getVariant() == 17) {
                        prefixForVariant = "Sparrow";
                        suffixForVariant = "star";
                    } else if (this.getVariant() == 18) {
                        prefixForVariant = "Fox";
                        suffixForVariant = "eater";
                    } else if (this.getVariant() == 19) {
                        prefixForVariant = "Willow";
                        suffixForVariant = "song";
                    }


                    if (this.getGender() == 0) {
                        genderV = " ♂";
                    } else {
                        genderV = " ♀";
                    }
                    if (this.isBaby()) {
                        finalName = prefixForVariant + "kit" + genderV;
                    } else if (this.getRank() == APPRENTICE) {
                        finalName = prefixForVariant + "paw" + genderV;
                    } else {
                        finalName = prefixForVariant + suffixForVariant + genderV;
                    }

                    this.setPrefix(Component.literal(prefixForVariant));


                    this.setCustomName(Component.literal(finalName));
                    this.setCustomNameVisible(true);

                    this.setNameColor(this.getRank());

                    String morphName = pPlayer.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                            .map(WCEPlayerData::getMorphName).orElse(pPlayer.getName().getString());

                    Component message = Component.empty()
                            .append(Component.literal(morphName).withStyle(ChatFormatting.AQUA))
                            .append(Component.literal("(").withStyle(ChatFormatting.GRAY))
                            .append(Component.literal(pPlayer.getName().getString()).withStyle(ChatFormatting.GRAY))
                            .append(Component.literal(")").withStyle(ChatFormatting.GRAY))
                            .append(" has changed ")
                            .append(oldName)
                            .append("'s name to ")
                            .append(this.hasCustomName() ? this.getCustomName().copy() : Component.literal("null"));
                    this.registerClanLog(message);
                    this.updateClanCatData();
                    this.updateNest();


                } else {
                    return InteractionResult.PASS;
                }

                this.updateMatesName();
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        if (this.isTame() && itemstack.is(ModItems.MYSTIC_FLOWERS_BOUQUET.get())) {

            if (!this.level().isClientSide) {
                if (pPlayer instanceof ServerPlayer sPlayer && PlayerShape.getCurrentShape(sPlayer) instanceof WCatEntity) {
                    String sPlayerMorphName = sPlayer.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                            .map(WCEPlayerData::getMorphName).orElse(sPlayer.getName().toString());
                    WCEPlayerData.Age playerMorphAge = sPlayer.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                            .map(WCEPlayerData::getMorphAge).orElse(WCEPlayerData.Age.ADULT);

                    if (playerMorphAge != WCEPlayerData.Age.ADULT) {
                        pPlayer.sendSystemMessage(Component.literal(sPlayerMorphName + " is not old enough for this.")
                                .withStyle(ChatFormatting.RED));
                        return InteractionResult.FAIL;
                    }
                    if (this.isBaby()) {
                        pPlayer.sendSystemMessage(Component.literal((this.hasCustomName() ? this.getCustomName().getString() : "This cat")
                                        + " is not an adult!")
                                .withStyle(ChatFormatting.RED));
                        pPlayer.hurt(this.damageSources().magic(), 10f);
                        return InteractionResult.FAIL;
                    }

                    if (this.isForbiddenFromMatingPlayer() && this.getForbiddenPlayer() == pPlayer.getUUID()) {
                        pPlayer.sendSystemMessage(Component.literal((this.hasCustomName() ? this.getCustomName().getString() : "This cat")
                                        + " is one of your descendants.")
                                .withStyle(ChatFormatting.GRAY));
                        return InteractionResult.FAIL;
                    }

                    if (sPlayer.getAbilities().instabuild) {
                        this.setFriendshipLevel(sPlayer.getUUID(), 100);
                    }

                    if (this.getFriendshipLevel(sPlayer.getUUID()) < 98) {
                        pPlayer.sendSystemMessage(Component.literal((this.hasCustomName() ? this.getCustomName().getString() : "This cat") + " doesn't like you that much!")
                                .withStyle(ChatFormatting.GRAY));
                        return InteractionResult.FAIL;
                    }
                    Entity currentMate = ((ServerLevel) this.level()).getEntity(this.getMateUUID());
                    if (currentMate instanceof Player) {
                        if (currentMate == pPlayer) {
                            pPlayer.sendSystemMessage(Component.literal((this.hasCustomName() ? this.getCustomName().getString() : "This cat")
                                            + " is already " + sPlayerMorphName + "'s mate!")
                                    .withStyle(ChatFormatting.YELLOW));
                        } else {
                            pPlayer.sendSystemMessage(Component.literal((this.hasCustomName() ? this.getCustomName().getString() : "This cat")
                                            + " can't take another mate!")
                                    .withStyle(ChatFormatting.YELLOW));
                        }
                        return InteractionResult.FAIL;
                    }

                    this.setMate(Component.literal(sPlayerMorphName));
                    this.setMateUUID(sPlayer.getUUID());

                    Component message = Component.literal((this.hasCustomName() ? this.getCustomName().getString() : "This cat") + " and "
                                    + sPlayerMorphName + " are now a beautiful couple!")
                            .withStyle(ChatFormatting.GREEN);

                    Component messageLog = Component.empty()
                            .append(Component.literal((this.hasCustomName() ? this.getCustomName().getString() : "A cat")))
                            .append(" and ")
                            .append(Component.literal(sPlayerMorphName).withStyle(ChatFormatting.AQUA))
                            .append(Component.literal("(").withStyle(ChatFormatting.GRAY))
                            .append(Component.literal(pPlayer.getName().getString()).withStyle(ChatFormatting.GRAY))
                            .append(Component.literal(")").withStyle(ChatFormatting.GRAY))
                            .append(" are now a beautiful couple!");
                    this.registerClanLog(messageLog);

                    pPlayer.sendSystemMessage(message);

                    WCGenetics mateGenetics = this.getGenetics();
                    if (!this.isOnGeneticalSkin()) mateGenetics = GeneticsForVariant.get(this.getVariant());

                    pPlayer.getItemInHand(InteractionHand.MAIN_HAND).shrink(1);
                    WCGenetics finalMateGenetics = mateGenetics;
                    sPlayer.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA).ifPresent(cap -> {
                        cap.setMateUUID(this.getUUID());
                        cap.setMateName(this.hasCustomName() ? this.getCustomName() : Component.literal("Undefined"));
                        cap.setMateGenetics(finalMateGenetics);
                        ModPackets.sendToPlayer(new S2CSyncClanDataPacket(cap), sPlayer);
                    });
                    this.level().playSound(null, this.blockPosition(), SoundEvents.CAT_PURREOW, SoundSource.NEUTRAL, 0.6F, 1.0F);
                    this.lovingParticlesTicks = 600;

                    this.setForbiddingFutureGensFromMatingPlayer(true);
                    this.setForbiddenPlayer(pPlayer.getUUID());
                    this.entityData.set(MOOD, Mood.HAPPY.ordinal());

                    return InteractionResult.SUCCESS;

                }
            }

        }

        if (itemstack.getItem() == ModItems.WARRIOR_NAMETAG.get() && this.isTame()) {

            if (itemstack.hasCustomHoverName()) {
                itemstack.shrink(1);

                Component oldName = this.hasCustomName() ? this.getCustomName().copy() : Component.literal("A cat");

                String fullName = itemstack.getHoverName().getString();

                String[] parts = fullName.split(" ");

                String genderV;
                if (this.getGender() == 0) {
                    genderV = " ♂";
                } else {
                    genderV = " ♀";
                }

                if (parts.length >= 2) {
                    String prefix = parts[0];
                    String sufix = parts[1];

                    this.setPrefix(Component.literal(prefix));

                    if (this.isBaby()) {
                        if (this.getRank() == APPRENTICE) {
                            this.setCustomName(Component.literal(prefix + "paw" + genderV));
                        } else {
                            this.setCustomName(Component.literal(prefix + "kit" + genderV));
                        }
                    } else {
                        this.setCustomName(Component.literal(prefix + sufix + genderV));
                    }

                } else {
                    String prefix = parts[0];

                    this.setPrefix(Component.literal(prefix));
                    this.setCustomName(Component.literal(prefix + genderV));

                }

                this.setNameColor(this.getRank());

                String morphName = pPlayer.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                        .map(WCEPlayerData::getMorphName).orElse(pPlayer.getName().getString());

                Component message = Component.empty()
                        .append(Component.literal(morphName).withStyle(ChatFormatting.AQUA))
                        .append(Component.literal("(").withStyle(ChatFormatting.GRAY))
                        .append(Component.literal(pPlayer.getName().getString()).withStyle(ChatFormatting.GRAY))
                        .append(Component.literal(")").withStyle(ChatFormatting.GRAY))
                        .append(" has changed ")
                        .append(oldName)
                        .append("'s name to ")
                        .append(this.hasCustomName() ? this.getCustomName().copy() : Component.literal("null"));
                this.registerClanLog(message);
                this.updateClanCatData();
                this.updateMatesName();
                this.updateNest();

            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }


        if (this.isTame() && itemstack.is(ModItems.WARRIORNAMERANDOMIZER.get())) {

            if (!this.level().isClientSide()) {
                itemstack.hurtAndBreak(1, pPlayer, (p) ->
                        p.broadcastBreakEvent(pHand));

                Component oldName = this.hasCustomName() ? this.getCustomName() : Component.literal("Unnamed cat");


                String[] prefixSet = getPrefixForVariant();

                String genderV;
                if (this.getGender() == 0) {
                    genderV = " ♂";
                } else {
                    genderV = " ♀";
                }

                int i = this.random.nextInt(prefixSet.length);
                int j = this.random.nextInt(SUFIXES.length);


                String finalName;

                if (this.isBaby()) {
                    finalName = prefixSet[i] + "kit" + genderV;
                } else if (this.getRank() == APPRENTICE) {
                    finalName = prefixSet[i] + "paw" + genderV;
                } else {
                    finalName = prefixSet[i] + SUFIXES[j] + genderV;
                }

                this.setPrefix(Component.literal(prefixSet[i]));


                this.setCustomName(Component.literal(finalName));
                this.setCustomNameVisible(true);

                this.setNameColor(this.getRank());

                String morphName = pPlayer.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                        .map(WCEPlayerData::getMorphName).orElse(pPlayer.getName().getString());

                Component message = Component.empty()
                        .append(Component.literal(morphName).withStyle(ChatFormatting.AQUA))
                        .append(Component.literal("(").withStyle(ChatFormatting.GRAY))
                        .append(Component.literal(pPlayer.getName().getString()).withStyle(ChatFormatting.GRAY))
                        .append(Component.literal(")").withStyle(ChatFormatting.GRAY))
                        .append(" has changed ")
                        .append(oldName)
                        .append("'s name to ")
                        .append(this.hasCustomName() ? this.getCustomName().copy() : Component.literal("null"));
                this.registerClanLog(message);
                this.updateClanCatData();
                this.updateMatesName();
                this.updateNest();
                this.rewardMoonmoon();

            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        if (itemstack.is(ModItems.WHISKERS.get())) {
            if (!(PlayerShape.getCurrentShape(pPlayer) instanceof Animal)) return InteractionResult.PASS;

            if (!level().isClientSide()) {
                itemstack.hurtAndBreak(1, pPlayer, (p) -> p.broadcastBreakEvent(pHand));

                if (pPlayer instanceof ServerPlayer sPlayer) {
                    ModPackets.sendToPlayer(new SyncDiseasesPacket(this.getId(), this.diseaseData()), sPlayer);
                }

                boolean isValidDeputy;
                if (this.level() instanceof  ServerLevel sLevel) {
                    ClanData data = ClanData.get(sLevel.getServer().overworld());

                    UUID clanUUID = pPlayer.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                            .map(WCEPlayerData::getCurrentClanUUID).orElse(ClanData.EMPTY_UUID);

                    ClanData.Clan clan = data.getClan(clanUUID);
                    if (clan != null) {
                        isValidDeputy = clan.members.get(pPlayer.getUUID()) == ClanData.ClanPlayerRank.DEPUTY
                                && data.canCommandWarriors(clan, pPlayer.getUUID())
                                && (this.getHealth() > this.getMaxHealth() / 2)
                                && !(this.onBorderPatrolFlag || this.onHuntingPatrolFlag || this.returnHomeFlag || this.tellingCatsToPatrol)
                                && this.getClanUUID().equals(clanUUID)
                                && !this.getClanUUID().equals(ClanData.EMPTY_UUID)
                                && (this.getRank() == WARRIOR || this.getRank() == APPRENTICE)
                                && this.getOwnerUUID() != null
                                && this.hasHomePosition();
                    } else {
                        isValidDeputy = false;
                    }
                } else {
                    isValidDeputy = false;
                }

                if (this.isTame() && this.getOwner() == pPlayer) {
                    if (this.getPersonality() == Personality.NONE || this.getPersonality() == null) {
                        this.assignRandomPersonality(this.random);
                    }
                    pPlayer.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA).ifPresent(cap -> {
                        String clanName = cap.getClanName(pPlayer.level());
                        if (this.getClan().equals(Component.literal("None"))
                                || !this.getClan().equals(Component.literal(cap.getClanName(pPlayer.level())))) {
                            if (this.getRank() != NONE) this.setClan(Component.literal(clanName));
                        }

                    });

                    if (this.level() instanceof  ServerLevel sLevel) {
                        Entity ent = sLevel.getEntity(this.getMateUUID());
                        if (ent != null) {
                            if (ent instanceof Player playerMate) {
                                String morphName = playerMate.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                                        .map(WCEPlayerData::getMorphName).orElse("None");

                                if (!this.getMate().equals(Component.literal(morphName))) {
                                    this.setMate(Component.literal(morphName));
                                }

                            } else {
                                if (!this.getMate().equals(ent.getCustomName())) {
                                    this.setMate(ent.getCustomName());
                                }
                            }
                        }
                    }

                    if (this.getMood() == null) {
                        this.setRandomMood(this.random);
                    }

                    Component clanText = this.getClan();
                    this.getPersonality();

                    boolean expectingKits = this.isExpectingKits();
                    String name = this.hasCustomName() ? this.getCustomName().getString() : "Unknown";
                    float kittingTime = ((getKittingTime()) - this.getKittingTicks()) / (20f * 60f);
                    String KitTime;
                    String genderText = this.isMale() ? "Tom-cat" : "She-cat";
                    String expectingText = expectingKits ? "Yes" : "No";
                    Component catMate = this.getMate();
                    String rankText = switch (this.getRank()) {
                        case NONE -> "Loner";
                        case KIT -> "Kit";
                        case APPRENTICE -> "Apprentice";
                        case WARRIOR -> "Warrior";
                        case MEDICINE -> "Medicine Cat";
                        case DEPUTY -> "Deputy";
                    };

                    String personalityText = switch (this.getPersonality()) {
                        case NONE -> "None";
                        case CALM -> "Calm";
                        case GRUMPY -> "Grumpy";
                        case CAUTIOUS -> "Cautious";
                        case INDEPENDENT -> "Independent";
                        case FRIENDLY -> "Friendly";
                        case SHY -> "Shy";
                        case AMBITIOUS -> "Ambitious";
                        case HUMBLE -> "Humble";
                        case RECKLESS -> "Reckless";
                    };

                    String ageText;
                    float moons;
                    if (!level().isClientSide()) {
                        float moonsCalc = (float) ((this.getAge() + (20 * 60 * getKitGrowthTimeMinutes())) / (100.0 * getKitGrowthTimeMinutes()));
                        this.entityData.set(AGE_SYNC, moonsCalc);
                    }


                    if (this.getAge() < 0) {
                        moons = this.entityData.get(AGE_SYNC);

                        ageText = String.format("%.2f moons", moons);

                    } else {
                        ageText = "Fully grown";
                    }

                    if (this.getKittingTicks() > 20) {
                        KitTime = String.format("%.2f min", kittingTime);
                    } else {
                        KitTime = "Not expecting";
                    }

                    this.lastMode = this.mode;
                    this.mode = CatMode.SIT;
                    this.setInSittingPose(true);
                    this.lookAtLeaderFlag = true;

                    if (!pPlayer.level().isClientSide && pPlayer instanceof ServerPlayer sPlayer) {
                        ModEvents2.schedule(1, () -> {
                            ModPackets.INSTANCE.send(
                                    PacketDistributor.PLAYER.with(() -> sPlayer),
                                    new OpenCatDataScreenPacket(this.getId(), isValidDeputy)
                            );
                        });
                    }

                } else {
                    if (!pPlayer.level().isClientSide && pPlayer instanceof ServerPlayer sPlayer) {
                        ModPackets.INSTANCE.send(
                                PacketDistributor.PLAYER.with(() -> sPlayer),
                                new OpenCatDataScreenPacket(this.getId(), isValidDeputy)
                        );
                    }
                }
            }

            return InteractionResult.SUCCESS;
        }

        if (this.isBaby() && this.getRank() == KIT) {
            if (itemstack.is(ModItems.DEATHBERRIES.get())) {
                if (!this.level().isClientSide()) {
                    ServerLevel level = ((ServerLevel) this.level());
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
                                .append(this.hasCustomName() ? this.getCustomName().copy() : Component.literal("a kit"))
                                .append("!");
                        this.registerClanLog(message);
                    }

                    this.addEffect(new MobEffectInstance(ModEffects.DEATHBERRIES.get(), 3600, 0));
                    this.level().playSound(null, this.blockPosition(), SoundEvents.CAT_EAT, SoundSource.AMBIENT, 0.8f, 1f);
                    BlockParticleOption particle = new BlockParticleOption(
                            ParticleTypes.BLOCK,
                            Blocks.REDSTONE_BLOCK.defaultBlockState()
                    );
                    level.sendParticles(
                            particle,
                            this.getX(),
                            this.getY(),
                            this.getZ(),
                            10,
                            0.2f, 0.2f, 0.2f, 0.1
                    );
                }

                this.gameEvent(GameEvent.EAT);
                return InteractionResult.sidedSuccess(this.level().isClientSide());

            }
            if (itemstack.is(ModItems.YARROW.get()) && (this.hasEffect(ModEffects.DEATHBERRIES.get()) || this.hasEffect(MobEffects.POISON))) {
                if (!this.level().isClientSide()) {
                    ServerLevel level = ((ServerLevel) this.level());

                    if (this.hasEffect(ModEffects.DEATHBERRIES.get())) {
                        this.removeEffect(ModEffects.DEATHBERRIES.get());
                    }
                    if (this.hasEffect(MobEffects.POISON)) {
                        this.removeEffect(MobEffects.POISON);
                    }

                    this.level().playSound(null, this.blockPosition(), SoundEvents.CAT_EAT, SoundSource.AMBIENT, 0.8f, 1f);
                    level.sendParticles(
                            ParticleTypes.HAPPY_VILLAGER,
                            this.getX(),
                            this.getY(),
                            this.getZ(),
                            10,
                            0.2, 0.2, 0.2, 0.1
                    );
                    this.gameEvent(GameEvent.EAT);
                    return InteractionResult.sidedSuccess(this.level().isClientSide());
                }
            }
        }


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
                    Component.literal("")
                            .append(name).withStyle(ChatFormatting.GRAY)
                            .append(" can't scent that fast!").withStyle(ChatFormatting.GRAY)
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
                    Component.literal("")
                            .append(name).withStyle(ChatFormatting.GRAY)
                            .append(" can't scent anything nearby.").withStyle(ChatFormatting.GRAY)
            );
            catSniffTickCooldown = 400;
            return;
        }

        int distance = (int) Math.sqrt(closestDistSqr[0]);

        String distanceText = String.valueOf(distance);

        player.sendSystemMessage(
                Component.literal("")
                        .append(name)
                        .append(Component.literal(" is scenting ")).withStyle(ChatFormatting.WHITE)
                        .append(targetBlock.getName().withStyle(ChatFormatting.AQUA))
                        .append(Component.literal(" at about ")).withStyle(ChatFormatting.WHITE)
                        .append(Component.literal(distanceText).withStyle(ChatFormatting.GREEN))
                        .append(Component.literal(" blocks."))
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
            female.setStoredFatherGenetics(male);

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

        Component message = Component.empty()
                .append(mateName.copy())
                .append(" & ")
                .append(thisName.copy())
                .append(" are now mates!");

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

        this.saveGeneticsNBT(tag);
        if (this.storedFatherGenetics != null) {
            CompoundTag fatherTag = new CompoundTag();
            saveFatherGeneticsToNBT(fatherTag, this.storedFatherGenetics);
            tag.put("StoredFatherGenetics", fatherTag);
        }

        if (this.getPlayerBoundUuid() != null){
            tag.putUUID("PlayerBoundUUID", this.getPlayerBoundUuid());
        }
        tag.putInt("AnimIndex", this.entityData.get(ANIM_INDEX));
        tag.putBoolean("ShowMorphName", this.entityData.get(SHOW_MORPH_NAME));


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


        this.loadGeneticsNBT(tag);

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
                    fatherTag.getInt("Rufousing"),
                    fatherTag.getInt("BlueRufousing"),
                    fatherTag.getInt("Noise"),
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
                if (animIndex == -2) {
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

//        if (this.getPlayerBoundUuid().equals(ClanData.EMPTY_UUID)){
//            if (this.hasEffect(ModEffects.NUMB_EFFECT.get())) {
//                tAnimationState.getController().setAnimation(RawAnimation.begin()
//                        .then("animation.wcat.fall_death", Animation.LoopType.PLAY_ONCE)
//                        .then("animation.wcat.death_idle", Animation.LoopType.LOOP));
//                return PlayState.CONTINUE;
//            }
//        }

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
                        then("animation.wcat.sprint" + this.entityData.get(IDLE_POSE), Animation.LoopType.LOOP));

                tAnimationState.getController().setAnimationSpeed(0.185 * Math.exp(9.91 * speed));

            } else {
                if (this.isBrokenPaw()) {
                    tAnimationState.getController().setAnimation(RawAnimation.begin().
                            then("animation.wcat.walk_limp", Animation.LoopType.LOOP));
                } else {
                    tAnimationState.getController().setAnimation(RawAnimation.begin().
                            then("animation.wcat.walk" + this.entityData.get(IDLE_POSE), Animation.LoopType.LOOP));
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
        this.entityData.set(IDLE_POSE, Math.min(i, WCGenetics.Values.MAX_IDLE_POSES -1));
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
        String name = this.getName().getString();
        switch (mode) {
            case SIT:
                player.displayClientMessage(Component.literal(name + " is staying."), true);
                break;
            case FOLLOW:
                player.displayClientMessage(Component.literal(name + " is following you."), true);
                break;
            case WANDER:
                player.displayClientMessage(Component.literal(name + " is wandering."), true);
                break;
        }
    }

    public void sendModeMessage(Player player, CatMode cMode) {
        String name = this.getName().getString();
        switch (cMode) {
            case SIT:
                player.displayClientMessage(Component.literal(name + " will stay."), true);
                break;
            case FOLLOW:
                player.displayClientMessage(Component.literal(name + " will follow you."), true);
                break;
            case WANDER:
                player.displayClientMessage(Component.literal(name + " is wandering."), true);
                break;
        }
    }

    /**
     * Sends a different message depending on the cat's rank
     */
    private void sendRankMessage(Player player) {
        Rank r = this.getRank();
        String name = this.getName().getString();

        switch (r) {
            case NONE -> player.displayClientMessage(Component.literal(name + " is now a loner."), true);
            case KIT -> player.displayClientMessage(Component.literal(name + " is now a kit."), true);
            case APPRENTICE -> player.displayClientMessage(Component.literal(name + " is now an apprentice."), true);
            case WARRIOR -> player.displayClientMessage(Component.literal(name + " is now a warrior."), true);
            case MEDICINE -> player.displayClientMessage(Component.literal(name + " is now a medicine cat."), true);
            case DEPUTY -> player.displayClientMessage(Component.literal(name + " is now a deputy."), true);
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

        Component message = Component.empty()
                .append((this.hasCustomName() ? this.getCustomName().copy() : Component.literal("A she-cat")))
                .append(Component.literal(" has brought " + litterSize + " kits to the clan!").withStyle(ChatFormatting.WHITE));

        this.registerClanLog(message);


        for (int i = 0; i < litterSize; i++) {
            this.resetLove();
            WCatEntity kit = ModEntities.WCAT.get().create(server);

            if (kit != null) {

                if (this.storedFatherGenetics != null) {

                    WCGenetics motherGenetics = this.getGenetics();
                    if (WCGenetics.Chimerism.isChimera(this.entityData.get(CHIMERA_GENE))) {
                        if (this.getRandom().nextBoolean()) {
                            motherGenetics = this.getChimeraGenetics();
                        }
                    }

                    WCGenetics fatherGenetics = this.storedFatherGenetics;

                    kit.setOnGeneticalSkin(this.isOnGeneticalSkin());

                    kit.inheritGeneticsFromParents(motherGenetics, fatherGenetics);

                } else {
                    kit.initializeGenetics();
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

                    Component messageKit = Component.literal(kitName).withStyle(ChatFormatting.GREEN)
                            .append(Component.literal(" has been born!").withStyle(ChatFormatting.WHITE));

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

            Component message = Component.empty()
                    .append(prefix)
                    .append(Component.literal("paw has become a warrior. "))
                    .append(prefix)
                    .append(Component.literal("paw will now be known as "))
                    .append(Component.literal(newName).withStyle(ChatFormatting.GOLD))
                    .append(Component.literal("!")
                    );

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







    public boolean isOnGeneticalSkin() {
        return this.entityData.get(GENETICAL_SKIN);
    }

    public void setOnGeneticalSkin(boolean value) {
        this.entityData.set(GENETICAL_SKIN, value);
    }


    public WCGenetics getGenetics() {
        WCGenetics genetics = new WCGenetics();

        genetics.chestFur = (this.entityData.get(CHEST_FUR));
        genetics.bellyFur = (this.entityData.get(BELLY_FUR));
        genetics.legsFur = (this.entityData.get(LEGS_FUR));
        genetics.headFur = (this.entityData.get(HEAD_FUR));
        genetics.cheekFur = (this.entityData.get(CHEEK_FUR));
        genetics.backFur = (this.entityData.get(BACK_FUR));
        genetics.bobtail = (this.entityData.get(BOBTAIL));
        genetics.tailFur = (this.entityData.get(TAIL_FUR));

        genetics.base = (this.entityData.get(BASE));
        genetics.orangeBase = (this.entityData.get(ORANGE_BASE));
        genetics.whiteRatio = (this.entityData.get(WHITE_RATIO));
        genetics.albino = (this.entityData.get(ALBINO));
        genetics.dilute = (this.entityData.get(DILUTE));
        genetics.agouti = (this.entityData.get(AGOUTI));
        genetics.tabbyStripes = (this.entityData.get(TABBY_STRIPES));
        genetics.eyesAnomaly = (this.entityData.get(EYES_ANOMALY));
        genetics.rufousing = (this.entityData.get(RUFOUSING_VARIANT));
        genetics.blueRufousing = (this.entityData.get(BLUE_RUFOUSING_VARIANT));
        genetics.noise = (this.entityData.get(NOISE));
        genetics.chimeraGene = this.entityData.get(CHIMERA_GENE);
        genetics.silver = this.entityData.get(SILVER);

        return genetics;
    }

    public WCGenetics.GeneticalVariants getGenVariants() {
        WCGenetics.GeneticalVariants variants = new WCGenetics.GeneticalVariants(
                this.entityData.get(EYE_COLOR_LEFT),
                this.entityData.get(EYE_COLOR_RIGHT),
                this.entityData.get(RUFOUSING_VARIANT),
                this.entityData.get(BLUE_RUFOUSING_VARIANT),
                this.entityData.get(ORANGE_BASE_VARIANT),
                this.entityData.get(WHITE_RATIO_VARIANT),
                this.entityData.get(TABBY_STRIPES_VARIANT),
                this.entityData.get(ALBINO_VARIANT),
                this.entityData.get(EYE_COLOR_VARIANT_LEFT),
                this.entityData.get(EYE_COLOR_VARIANT_RIGHT),
                this.entityData.get(NOISE),
                this.entityData.get(SIZE),
                this.entityData.get(SILVER_VARIANT),
                this.entityData.get(SCARS)
        );
        return variants;
    }

    public WCGenetics.GeneticalChimeraVariants getChimeraGenVariants() {
        WCGenetics.GeneticalChimeraVariants variants = new WCGenetics.GeneticalChimeraVariants(
                this.entityData.get(CHIMERA_VARIANT),
                this.entityData.get(RUFOUSING_VARIANT_CHIMERA),
                this.entityData.get(BLUE_RUFOUSING_VARIANT_CHIMERA),
                this.entityData.get(ORANGE_BASE_VARIANT_CHIMERA),
                this.entityData.get(WHITE_RATIO_VARIANT_CHIMERA),
                this.entityData.get(TABBY_STRIPES_VARIANT_CHIMERA),
                this.entityData.get(ALBINO_VARIANT_CHIMERA),
                this.entityData.get(NOISE),
                this.entityData.get(CHIMERA_GENE),
                this.entityData.get(SILVER_VARIANT_CHIMERA)
        );
        return variants;
    }


    public WCGenetics getChimeraGenetics() {
        WCGenetics genetics = new WCGenetics();

        genetics.chestFur = (this.entityData.get(CHEST_FUR));
        genetics.bellyFur = (this.entityData.get(BELLY_FUR));
        genetics.legsFur = (this.entityData.get(LEGS_FUR));
        genetics.headFur = (this.entityData.get(HEAD_FUR));
        genetics.cheekFur = (this.entityData.get(CHEEK_FUR));
        genetics.backFur = (this.entityData.get(BACK_FUR));
        genetics.bobtail = (this.entityData.get(BOBTAIL));
        genetics.tailFur = (this.entityData.get(TAIL_FUR));

        genetics.base = (this.entityData.get(BASE_CHIMERA));
        genetics.orangeBase = (this.entityData.get(ORANGE_BASE_CHIMERA));
        genetics.whiteRatio = (this.entityData.get(WHITE_RATIO_CHIMERA));
        genetics.albino = (this.entityData.get(ALBINO_CHIMERA));
        genetics.dilute = (this.entityData.get(DILUTE_CHIMERA));
        genetics.agouti = (this.entityData.get(AGOUTI_CHIMERA));
        genetics.tabbyStripes = (this.entityData.get(TABBY_STRIPES_CHIMERA));
        genetics.eyesAnomaly = (this.entityData.get(EYES_ANOMALY));
        genetics.rufousing = (this.entityData.get(RUFOUSING_VARIANT_CHIMERA));
        genetics.blueRufousing = (this.entityData.get(BLUE_RUFOUSING_VARIANT_CHIMERA));
        genetics.noise = (this.entityData.get(NOISE_CHIMERA));
        genetics.chimeraGene = this.entityData.get(CHIMERA_GENE);
        genetics.silver = this.entityData.get(SILVER_CHIMERA);

        return genetics;
    }

    public void setGenetics(WCGenetics genetics) {
        this.entityData.set(CHEST_FUR, genetics.chestFur);
        this.entityData.set(BELLY_FUR, genetics.bellyFur);
        this.entityData.set(LEGS_FUR, genetics.legsFur);
        this.entityData.set(HEAD_FUR, genetics.headFur);
        this.entityData.set(CHEEK_FUR, genetics.cheekFur);
        this.entityData.set(BACK_FUR, genetics.backFur);
        this.entityData.set(BOBTAIL, genetics.bobtail);
        this.entityData.set(TAIL_FUR, genetics.tailFur);

        this.entityData.set(BASE, genetics.base);
        this.entityData.set(ORANGE_BASE, genetics.orangeBase);
        this.entityData.set(WHITE_RATIO, genetics.whiteRatio);
        this.entityData.set(ALBINO, genetics.albino);
        this.entityData.set(DILUTE, genetics.dilute);
        this.entityData.set(AGOUTI, genetics.agouti);
        this.entityData.set(TABBY_STRIPES, genetics.tabbyStripes);
        this.entityData.set(EYES_ANOMALY, genetics.eyesAnomaly);

        this.entityData.set(RUFOUSING_VARIANT, genetics.rufousing);
        this.entityData.set(BLUE_RUFOUSING_VARIANT, genetics.blueRufousing);
        this.entityData.set(NOISE, genetics.noise);
        this.entityData.set(CHIMERA_GENE, genetics.chimeraGene);
        this.entityData.set(SILVER, genetics.silver);
    }

    public void initializeGenetics() {
        this.entityData.set(CHEST_FUR, WCGenetics.FurGene.generateAlelo(random) + "-" + WCGenetics.FurGene.generateAlelo(random));
        this.entityData.set(BELLY_FUR, WCGenetics.FurGene.generateAlelo(random) + "-" + WCGenetics.FurGene.generateAlelo(random));
        this.entityData.set(LEGS_FUR, WCGenetics.FurGene.generateAlelo(random) + "-" + WCGenetics.FurGene.generateAlelo(random));
        this.entityData.set(HEAD_FUR, WCGenetics.FurGene.generateAlelo(random) + "-" + WCGenetics.FurGene.generateAlelo(random));
        this.entityData.set(CHEEK_FUR, WCGenetics.FurGene.generateAlelo(random) + "-" + WCGenetics.FurGene.generateAlelo(random));
        this.entityData.set(BACK_FUR, WCGenetics.FurGene.generateAlelo(random) + "-" + WCGenetics.FurGene.generateAlelo(random));
        this.entityData.set(BOBTAIL, WCGenetics.Bobtail.init(random));
        this.entityData.set(TAIL_FUR, WCGenetics.FurGene.generateAlelo(random) + "-" + WCGenetics.FurGene.generateAlelo(random));

        this.setOnGeneticalSkin(true);

        this.entityData.set(BASE, WCGenetics.Base.generateAlelo(this.random) + "-" + WCGenetics.Base.generateAlelo(this.random));

        this.entityData.set(ORANGE_BASE, WCGenetics.OrangeBase.generateAlelo(this.random) + "-" + WCGenetics.OrangeBase.generateAlelo(this.random));
        this.entityData.set(ORANGE_BASE_VARIANT, this.random.nextInt(WCGenetics.Values.MAX_ORANGE_VARIANTS));

        this.entityData.set(WHITE_RATIO, WCGenetics.WhiteRatio.generateAlelo(this.random) + "-" + WCGenetics.WhiteRatio.generateAlelo(this.random));
        this.entityData.set(WHITE_RATIO_VARIANT, this.random.nextInt(WCGenetics.Values.MAX_WHITE_VARIANTS));

        this.entityData.set(ALBINO, WCGenetics.Albino.generateAlelo(this.random) + "-" + WCGenetics.Albino.generateAlelo(this.random));
        this.entityData.set(ALBINO_VARIANT, this.random.nextInt(WCGenetics.Values.MAX_ALBINO_VARIANTS));

        this.entityData.set(DILUTE, WCGenetics.Dilute.generateAlelo(this.random) + "-" + WCGenetics.Dilute.generateAlelo(this.random));

        this.entityData.set(AGOUTI, WCGenetics.Agouti.generateAlelo(this.random) + "-" + WCGenetics.Agouti.generateAlelo(this.random));

        this.entityData.set(TABBY_STRIPES, WCGenetics.TabbyStripeTypes.generateAlelo(this.random) + "-" + WCGenetics.TabbyStripeTypes.generateAlelo(this.random));
        this.entityData.set(TABBY_STRIPES_VARIANT, this.random.nextInt(WCGenetics.Values.MAX_TABBY_VARIANTS));

        this.entityData.set(EYES_ANOMALY, WCGenetics.EyesAnomaly.generateAlelo(this.random) + "-" + WCGenetics.EyesAnomaly.generateAlelo(this.random));

        this.entityData.set(SILVER, WCGenetics.Silver.generateAlelo(this.random) + "-" + WCGenetics.Silver.generateAlelo(this.random));
        this.entityData.set(SILVER_VARIANT, this.random.nextInt(WCGenetics.Values.MAX_SILVER_VARIANTS));

        String leftEyeColor = WCGenetics.EyeColor.generateAlelo(this.random, this.entityData.get(WHITE_RATIO), this.entityData.get(ALBINO));
        int eyeLeftVariant = this.random.nextInt(WCGenetics.Values.MAX_EYE_VARIANTS);

        this.entityData.set(EYE_COLOR_LEFT, leftEyeColor);
        this.entityData.set(EYE_COLOR_VARIANT_LEFT, eyeLeftVariant);

        if (WCGenetics.EyesAnomaly.isHeteroChromic(this.entityData.get(EYES_ANOMALY))) {
            this.entityData.set(EYE_COLOR_RIGHT, WCGenetics.EyeColor.generateAlelo(this.random, this.entityData.get(WHITE_RATIO), this.entityData.get(ALBINO)));
            this.entityData.set(EYE_COLOR_VARIANT_RIGHT, this.random.nextInt(WCGenetics.Values.MAX_EYE_VARIANTS));
        } else {
            this.entityData.set(EYE_COLOR_RIGHT, leftEyeColor);
            this.entityData.set(EYE_COLOR_VARIANT_RIGHT, eyeLeftVariant);
        }

        this.entityData.set(NOISE, this.random.nextInt(WCGenetics.Values.MAX_NOISE_VARIANTS));

        if (WCGenetics.Base.isBlack(this.entityData.get(BASE))) {
            this.entityData.set(RUFOUSING_VARIANT, this.random.nextInt(3));
        } else {
            this.entityData.set(RUFOUSING_VARIANT, this.random.nextInt(WCGenetics.Values.MAX_RUFOUSING_VARIANTS));
        }

        if (WCGenetics.Dilute.isDilute(this.entityData.get(DILUTE))) {
            this.entityData.set(BLUE_RUFOUSING_VARIANT, this.random.nextInt(3));
        } else {
            this.entityData.set(BLUE_RUFOUSING_VARIANT, this.random.nextInt(WCGenetics.Values.MAX_BLUE_RUFOUSING_VARIANTS));
        }

        this.entityData.set(CHIMERA_GENE, WCGenetics.Chimerism.generateAlelo(this.random) + "-" + WCGenetics.Chimerism.generateAlelo(this.random));
        if (WCGenetics.Chimerism.isChimera(this.entityData.get(CHIMERA_GENE))) {
            this.initializeChimeraGenetics();
        }

    }

    public void initializeChimeraGenetics() {
        this.entityData.set(BASE_CHIMERA, WCGenetics.Base.generateAlelo(this.random) + "-" + WCGenetics.Base.generateAlelo(this.random));

        this.entityData.set(CHIMERA_VARIANT, this.random.nextInt(WCGenetics.Values.MAX_CHIMERISM_VARIANTS));

        this.entityData.set(ORANGE_BASE_CHIMERA, WCGenetics.OrangeBase.generateAlelo(this.random) + "-" + WCGenetics.OrangeBase.generateAlelo(this.random));
        this.entityData.set(ORANGE_BASE_VARIANT_CHIMERA, this.random.nextInt(WCGenetics.Values.MAX_ORANGE_VARIANTS));

        this.entityData.set(WHITE_RATIO_CHIMERA, WCGenetics.WhiteRatio.generateAlelo(this.random) + "-" + WCGenetics.WhiteRatio.generateAlelo(this.random));
        this.entityData.set(WHITE_RATIO_VARIANT_CHIMERA, this.random.nextInt(WCGenetics.Values.MAX_WHITE_VARIANTS));

        this.entityData.set(ALBINO_CHIMERA, WCGenetics.Albino.generateAlelo(this.random) + "-" + WCGenetics.Albino.generateAlelo(this.random));
        this.entityData.set(ALBINO_VARIANT_CHIMERA, this.random.nextInt(WCGenetics.Values.MAX_ALBINO_VARIANTS));

        this.entityData.set(DILUTE_CHIMERA, WCGenetics.Dilute.generateAlelo(this.random) + "-" + WCGenetics.Dilute.generateAlelo(this.random));

        this.entityData.set(AGOUTI_CHIMERA, WCGenetics.Agouti.generateAlelo(this.random) + "-" + WCGenetics.Agouti.generateAlelo(this.random));

        this.entityData.set(TABBY_STRIPES_CHIMERA, WCGenetics.TabbyStripeTypes.generateAlelo(this.random) + "-" + WCGenetics.TabbyStripeTypes.generateAlelo(this.random));
        this.entityData.set(TABBY_STRIPES_VARIANT_CHIMERA, this.random.nextInt(WCGenetics.Values.MAX_TABBY_VARIANTS));

        this.entityData.set(SILVER_CHIMERA, WCGenetics.Silver.generateAlelo(this.random) + "-" + WCGenetics.Silver.generateAlelo(this.random));
        this.entityData.set(SILVER_VARIANT_CHIMERA, this.random.nextInt(WCGenetics.Values.MAX_SILVER_VARIANTS));

        this.entityData.set(EYES_ANOMALY, "h-h");
        String leftEyeColor = WCGenetics.EyeColor.generateAlelo(this.random, this.entityData.get(WHITE_RATIO), this.entityData.get(ALBINO));
        int eyeLeftVariant = this.random.nextInt(WCGenetics.Values.MAX_EYE_VARIANTS);
        this.entityData.set(EYE_COLOR_LEFT, leftEyeColor);
        this.entityData.set(EYE_COLOR_VARIANT_LEFT, eyeLeftVariant);

        if (WCGenetics.EyesAnomaly.isHeteroChromic(this.entityData.get(EYES_ANOMALY))) {
            this.entityData.set(EYE_COLOR_RIGHT, WCGenetics.EyeColor.generateAlelo(this.random, this.entityData.get(WHITE_RATIO_CHIMERA), this.entityData.get(ALBINO_CHIMERA)));
            this.entityData.set(EYE_COLOR_VARIANT_RIGHT, this.random.nextInt(WCGenetics.Values.MAX_EYE_VARIANTS));
        } else {
            this.entityData.set(EYE_COLOR_RIGHT, leftEyeColor);
            this.entityData.set(EYE_COLOR_VARIANT_RIGHT, eyeLeftVariant);
        }

        this.entityData.set(NOISE_CHIMERA, this.random.nextInt(WCGenetics.Values.MAX_NOISE_VARIANTS));

        if (WCGenetics.Base.isBlack(this.entityData.get(BASE_CHIMERA))) {
            this.entityData.set(RUFOUSING_VARIANT_CHIMERA, this.random.nextInt(3));
        } else {
            this.entityData.set(RUFOUSING_VARIANT_CHIMERA, this.random.nextInt(WCGenetics.Values.MAX_BLUE_RUFOUSING_VARIANTS));
        }

        if (WCGenetics.Dilute.isDilute(this.entityData.get(DILUTE_CHIMERA))) {
            this.entityData.set(BLUE_RUFOUSING_VARIANT_CHIMERA, this.random.nextInt(3));
        } else {
            this.entityData.set(BLUE_RUFOUSING_VARIANT_CHIMERA, this.random.nextInt(WCGenetics.Values.MAX_BLUE_RUFOUSING_VARIANTS));
        }

    }

    public void saveGeneticsNBT(CompoundTag tag) {

        CompoundTag geneticsTag = new CompoundTag();

        geneticsTag.putBoolean("Genetical", this.isOnGeneticalSkin());

        geneticsTag.putString("ChestFur", this.entityData.get(CHEST_FUR));
        geneticsTag.putString("BellyFur", this.entityData.get(BELLY_FUR));
        geneticsTag.putString("LegsFur", this.entityData.get(LEGS_FUR));
        geneticsTag.putString("HeadFur", this.entityData.get(HEAD_FUR));
        geneticsTag.putString("CheekFur", this.entityData.get(CHEEK_FUR));
        geneticsTag.putString("BackFur", this.entityData.get(BACK_FUR));
        geneticsTag.putString("TailFur", this.entityData.get(TAIL_FUR));
        geneticsTag.putString("Bobtail", this.entityData.get(BOBTAIL));

        geneticsTag.putString("Base", this.entityData.get(BASE));
        geneticsTag.putString("OrangeBase", this.entityData.get(ORANGE_BASE));
        geneticsTag.putString("WhiteRatio", this.entityData.get(WHITE_RATIO));
        geneticsTag.putString("Albino", this.entityData.get(ALBINO));
        geneticsTag.putString("Dilute", this.entityData.get(DILUTE));
        geneticsTag.putString("Agouti", this.entityData.get(AGOUTI));
        geneticsTag.putString("TabbyStripes", this.entityData.get(TABBY_STRIPES));
        geneticsTag.putString("EyeColorLeft", this.entityData.get(EYE_COLOR_LEFT));
        geneticsTag.putString("EyeColorRight", this.entityData.get(EYE_COLOR_RIGHT));
        geneticsTag.putString("EyesAnomaly", this.entityData.get(EYES_ANOMALY));
        geneticsTag.putString("Silver", this.entityData.get(SILVER));

        geneticsTag.putInt("Rufousing", this.entityData.get(RUFOUSING_VARIANT));
        geneticsTag.putInt("BlueRufousing", this.entityData.get(BLUE_RUFOUSING_VARIANT));
        geneticsTag.putInt("OrangeBaseVariant", this.entityData.get(ORANGE_BASE_VARIANT));
        geneticsTag.putInt("WhiteRatioVariant", this.entityData.get(WHITE_RATIO_VARIANT));
        geneticsTag.putInt("AlbinoVariant", this.entityData.get(ALBINO_VARIANT));
        geneticsTag.putInt("SilverVariant", this.entityData.get(SILVER_VARIANT));

        geneticsTag.putInt("TabbyStripesVariant", this.entityData.get(TABBY_STRIPES_VARIANT));
        geneticsTag.putInt("EyeColorVariantLeft", this.entityData.get(EYE_COLOR_VARIANT_LEFT));
        geneticsTag.putInt("EyeColorVariantRight", this.entityData.get(EYE_COLOR_VARIANT_RIGHT));
        geneticsTag.putInt("Noise", this.entityData.get(NOISE));
        geneticsTag.putFloat("Size", this.entityData.get(SIZE));
        geneticsTag.putFloat("Scars", this.entityData.get(SCARS));

        geneticsTag.putInt("IdlePose", this.entityData.get(IDLE_POSE));


        geneticsTag.putString("BaseChimera", this.entityData.get(BASE_CHIMERA));
        geneticsTag.putString("OrangeBaseChimera", this.entityData.get(ORANGE_BASE_CHIMERA));
        geneticsTag.putString("WhiteRatioChimera", this.entityData.get(WHITE_RATIO_CHIMERA));
        geneticsTag.putString("AlbinoChimera", this.entityData.get(ALBINO_CHIMERA));
        geneticsTag.putString("DiluteChimera", this.entityData.get(DILUTE_CHIMERA));
        geneticsTag.putString("AgoutiChimera", this.entityData.get(AGOUTI_CHIMERA));
        geneticsTag.putString("TabbyStripesChimera", this.entityData.get(TABBY_STRIPES_CHIMERA));

        geneticsTag.putInt("RufousingChimera", this.entityData.get(RUFOUSING_VARIANT_CHIMERA));
        geneticsTag.putInt("BlueRufousingChimera", this.entityData.get(BLUE_RUFOUSING_VARIANT_CHIMERA));
        geneticsTag.putInt("OrangeBaseVariantChimera", this.entityData.get(ORANGE_BASE_VARIANT_CHIMERA));
        geneticsTag.putInt("WhiteRatioVariantChimera", this.entityData.get(WHITE_RATIO_VARIANT_CHIMERA));
        geneticsTag.putInt("AlbinoVariantChimera", this.entityData.get(ALBINO_VARIANT_CHIMERA));

        geneticsTag.putInt("TabbyStripesVariantChimera", this.entityData.get(TABBY_STRIPES_VARIANT_CHIMERA));
        geneticsTag.putInt("NoiseChimera", this.entityData.get(NOISE_CHIMERA));

        geneticsTag.putString("Chimera", this.entityData.get(CHIMERA_GENE));
        geneticsTag.putInt("ChimeraVariant", this.entityData.get(CHIMERA_VARIANT));
        geneticsTag.putInt("SilverVariantChimera", this.entityData.get(SILVER_VARIANT_CHIMERA));
        geneticsTag.putString("SilverChimera", this.entityData.get(SILVER_CHIMERA));

        tag.put("Genetics", geneticsTag);
    }

    public void loadGeneticsNBT(CompoundTag tag) {

        if (tag.contains("Genetics")) {
            CompoundTag geneticsTag = tag.getCompound("Genetics");

            this.setOnGeneticalSkin(geneticsTag.getBoolean("Genetical"));

            WCGenetics genetics = new WCGenetics(
                    geneticsTag.getString("Bobtail"),
                    geneticsTag.getString("ChestFur"),
                    geneticsTag.getString("BellyFur"),
                    geneticsTag.getString("LegsFur"),
                    geneticsTag.getString("HeadFur"),
                    geneticsTag.getString("CheekFur"),
                    geneticsTag.getString("TailFur"),
                    geneticsTag.getString("BackFur"),

                    geneticsTag.getString("Base"),
                    geneticsTag.getString("OrangeBase"),
                    geneticsTag.getString("WhiteRatio"),
                    geneticsTag.getString("Albino"),
                    geneticsTag.getString("Dilute"),
                    geneticsTag.getString("Agouti"),
                    geneticsTag.getString("TabbyStripes"),
                    geneticsTag.getString("EyesAnomaly"),

                    geneticsTag.getInt("Rufousing"),
                    geneticsTag.getInt("BlueRufousing"),
                    geneticsTag.getInt("Noise"),
                    geneticsTag.getString("Chimera"),
                    geneticsTag.getString("Silver")
            );

            WCGenetics geneticsChimera = new WCGenetics(
                    geneticsTag.getString("Bobtail"),
                    geneticsTag.getString("ChestFur"),
                    geneticsTag.getString("BellyFur"),
                    geneticsTag.getString("LegsFur"),
                    geneticsTag.getString("HeadFur"),
                    geneticsTag.getString("CheekFur"),
                    geneticsTag.getString("TailFur"),
                    geneticsTag.getString("BackFur"),

                    geneticsTag.getString("BaseChimera"),
                    geneticsTag.getString("OrangeBaseChimera"),
                    geneticsTag.getString("WhiteRatioChimera"),
                    geneticsTag.getString("AlbinoChimera"),
                    geneticsTag.getString("DiluteChimera"),
                    geneticsTag.getString("AgoutiChimera"),
                    geneticsTag.getString("TabbyStripesChimera"),
                    geneticsTag.getString("EyesAnomalyChimera"),

                    geneticsTag.getInt("RufousingChimera"),
                    geneticsTag.getInt("BlueRufousingChimera"),
                    geneticsTag.getInt("NoiseChimera"),
                    geneticsTag.getString("Chimera"),
                    geneticsTag.getString("SilverChimera")
            );

            this.setGenetics(genetics);

            this.setChimeraGenetics(geneticsChimera);

            this.entityData.set(CHIMERA_VARIANT, geneticsTag.getInt("ChimeraVariant"));

            this.entityData.set(EYE_COLOR_LEFT, geneticsTag.getString("EyeColorLeft"));
            this.entityData.set(EYE_COLOR_RIGHT, geneticsTag.getString("EyeColorRight"));

            this.entityData.set(ORANGE_BASE_VARIANT, geneticsTag.getInt("OrangeBaseVariant"));
            this.entityData.set(WHITE_RATIO_VARIANT, geneticsTag.getInt("WhiteRatioVariant"));
            this.entityData.set(ALBINO_VARIANT, geneticsTag.getInt("AlbinoVariant"));
            this.entityData.set(TABBY_STRIPES_VARIANT, geneticsTag.getInt("TabbyStripesVariant"));
            this.entityData.set(EYE_COLOR_VARIANT_LEFT, geneticsTag.getInt("EyeColorVariantLeft"));
            this.entityData.set(EYE_COLOR_VARIANT_RIGHT, geneticsTag.getInt("EyeColorVariantRight"));
            this.entityData.set(SIZE, geneticsTag.getFloat("Size"));
            this.entityData.set(SILVER_VARIANT, geneticsTag.getInt("SilverVariant"));
            this.entityData.set(SCARS, geneticsTag.getInt("Scars"));

            this.entityData.set(IDLE_POSE, geneticsTag.getInt("IdlePose"));



            this.entityData.set(ORANGE_BASE_VARIANT_CHIMERA, geneticsTag.getInt("OrangeBaseVariantChimera"));
            this.entityData.set(WHITE_RATIO_VARIANT_CHIMERA, geneticsTag.getInt("WhiteRatioVariantChimera"));
            this.entityData.set(ALBINO_VARIANT_CHIMERA, geneticsTag.getInt("AlbinoVariantChimera"));
            this.entityData.set(TABBY_STRIPES_VARIANT_CHIMERA, geneticsTag.getInt("TabbyStripesVariantChimera"));
            this.entityData.set(SILVER_VARIANT_CHIMERA, geneticsTag.getInt("SilverVariantChimera"));

        }
    }

    public void setStoredFatherGenetics(WCatEntity father) {
        if (father.isOnGeneticalSkin()) {
            if (WCGenetics.Chimerism.isChimera(father.entityData.get(CHIMERA_GENE))) {
                if (father.getRandom().nextBoolean()) {
                    this.storedFatherGenetics = father.getChimeraGenetics();
                } else {
                    this.storedFatherGenetics = father.getGenetics();
                }
            } else {
                this.storedFatherGenetics = father.getGenetics();
            }
        } else {
            this.storedFatherGenetics = GeneticsForVariant.get(father.getVariant());
        }
    }

    private void saveFatherGeneticsToNBT(CompoundTag tag, WCGenetics genetics) {

        tag.putString("ChestFur", genetics.chestFur);
        tag.putString("BellyFur", genetics.bellyFur);
        tag.putString("LegsFur", genetics.legsFur);
        tag.putString("HeadFur", genetics.headFur);
        tag.putString("CheekFur", genetics.cheekFur);
        tag.putString("BackFur", genetics.backFur);
        tag.putString("TailFur", genetics.tailFur);
        tag.putString("Bobtail", genetics.bobtail);

        tag.putString("Base", genetics.base);
        tag.putString("Orange", genetics.orangeBase);
        tag.putString("WhiteRatio", genetics.whiteRatio);
        tag.putString("Albino", genetics.albino);
        tag.putString("Dilute", genetics.dilute);
        tag.putString("Agouti", genetics.agouti);
        tag.putString("TabbyStripes", genetics.tabbyStripes);
        tag.putString("EyesAnomaly", genetics.eyesAnomaly);
        tag.putInt("Rufousing", genetics.rufousing);
        tag.putInt("BlueRufousing", genetics.blueRufousing);
        tag.putInt("Noise", genetics.noise);
        tag.putString("Chimera", genetics.chimeraGene);
        tag.putString("Silver", genetics.silver);

    }

    public static String inheritGenetics(String motherGene, String fatherGene, RandomSource random) {
        String[] motherAlleles = motherGene.split("-");
        String[] fatherAlleles = fatherGene.split("-");

        if ((motherAlleles[0].equals("") || motherAlleles[1].equals("")) || (fatherAlleles[0].equals("") || fatherAlleles[1].equals(""))) return normalize("x-x");

        String motherAllele = motherAlleles[random.nextInt(2)];
        String fatherAllele = fatherAlleles[random.nextInt(2)];

        return normalize(motherAllele + "-" + fatherAllele);
    }

    private static String normalize(String gene) {

        String[] alleles = gene.split("-");

        String a = alleles[0];
        String b = alleles[1];

        if (b.equals(b.toUpperCase()) && a.equals(a.toLowerCase())) {
            return b + "-" + a;
        }

        return gene;
    }

    @OnlyIn(Dist.CLIENT)
    public String getCatTextureKey() {
        if (this.textureKey == null) this.defineTextureLayers();

        return this.textureKey;
    }

    @OnlyIn(Dist.CLIENT)
    private void defineTextureLayers() {
        String folderPath = "warriorcats_events:textures/entity/wcat/genetics/";

        if (!this.isOnGeneticalSkin()) return;

        String basePath = folderPath + "base/";
        if (WCGenetics.Base.isBlack(this.entityData.get(BASE))) {
            basePath += "black";
        } else if (WCGenetics.Base.isChocolate(this.entityData.get(BASE))) {
            basePath += "chocolate";
        } else if (WCGenetics.Base.isCinnamon(this.entityData.get(BASE))) {
            basePath += "cinnamon";
        } else {
            basePath = folderPath + "empty";
        }

//        String orangeBasePath = folderPath + "orange_base/";
//        if (WCGenetics.OrangeBase.isOrange(this.entityData.get(ORANGE_BASE), this.entityData.get(GENDER))) {
//
//            if (WCGenetics.TabbyStripeTypes.isMackerel(this.entityData.get(TABBY_STRIPES))) {
//                orangeBasePath += "orange_" + "mackerel_" + this.entityData.get(TABBY_STRIPES_VARIANT);// ORANGE_BASE -> 0-4
//            } else {
//                orangeBasePath += "orange_" + "classic_" + this.entityData.get(TABBY_STRIPES_VARIANT);// ORANGE_BASE -> 0-4
//            }
//
//        } else if (WCGenetics.OrangeBase.isTortoiseshell(this.entityData.get(ORANGE_BASE))) {
//
//            if (WCGenetics.TabbyStripeTypes.isMackerel(this.entityData.get(TABBY_STRIPES))) {
//                orangeBasePath += "tortie_" + this.entityData.get(ORANGE_BASE_VARIANT) + "_mackerel_" + this.entityData.get(TABBY_STRIPES_VARIANT);// ORANGE_BASE -> 0-4
//            } else {
//                orangeBasePath += "tortie_" + this.entityData.get(ORANGE_BASE_VARIANT) + "_classic_" + this.entityData.get(TABBY_STRIPES_VARIANT);// ORANGE_BASE -> 0-4
//            }
//
//        } else {
//            orangeBasePath = folderPath + "empty";
//        }

        String orangeBasePath = folderPath + "orange_base/";
        String stripesForOrangePath = folderPath + "agouti_marks/orange/";
        if (WCGenetics.OrangeBase.isOrange(this.entityData.get(ORANGE_BASE), this.entityData.get(GENDER))) {

            orangeBasePath += "orange";

            if (WCGenetics.TabbyStripeTypes.isMackerel(this.entityData.get(TABBY_STRIPES))) {
                stripesForOrangePath += "mackerel_orange_" + this.entityData.get(TABBY_STRIPES_VARIANT);// ORANGE_BASE -> 0-4
            } else {
                stripesForOrangePath += "classic_orange_" + this.entityData.get(TABBY_STRIPES_VARIANT);// ORANGE_BASE -> 0-4
            }

        } else if (WCGenetics.OrangeBase.isTortoiseshell(this.entityData.get(ORANGE_BASE))) {

            orangeBasePath += "tortie_" + this.entityData.get(ORANGE_BASE_VARIANT);// ORANGE_BASE -> 0-4

            if (WCGenetics.TabbyStripeTypes.isMackerel(this.entityData.get(TABBY_STRIPES))) {
                stripesForOrangePath += "mackerel_orange_" + this.entityData.get(TABBY_STRIPES_VARIANT);// ORANGE_BASE -> 0-4
            } else {
                stripesForOrangePath += "classic_orange_" + this.entityData.get(TABBY_STRIPES_VARIANT);// ORANGE_BASE -> 0-4
            }

        } else {
            orangeBasePath = folderPath + "empty";
            stripesForOrangePath = folderPath + "empty";
        }

        if (WCGenetics.Dilute.isDilute(this.entityData.get(DILUTE))) {
            if (WCGenetics.Base.isBlack(this.entityData.get(BASE))) {
                basePath = folderPath + "base/black_to_gray";
            } else if (WCGenetics.Base.isChocolate(this.entityData.get(BASE))) {
                basePath = folderPath + "base/chocolate_to_lilac";
            } else if (WCGenetics.Base.isCinnamon(this.entityData.get(BASE))) {
                basePath = folderPath + "base/cinnamon_to_fawn";
            }

            if (WCGenetics.OrangeBase.isOrange(this.entityData.get(ORANGE_BASE), this.entityData.get(GENDER))) {
                orangeBasePath = folderPath + "orange_base/orange_to_cream";

                if (WCGenetics.TabbyStripeTypes.isMackerel(this.entityData.get(TABBY_STRIPES))) {
                    stripesForOrangePath = folderPath + "agouti_marks/orange/mackerel_otc_" + this.entityData.get(TABBY_STRIPES_VARIANT);
                } else {
                    stripesForOrangePath = folderPath + "agouti_marks/orange/classic_otc_" + this.entityData.get(TABBY_STRIPES_VARIANT);
                }

            } else if (WCGenetics.OrangeBase.isTortoiseshell(this.entityData.get(ORANGE_BASE))) {
                orangeBasePath = folderPath + "orange_base/tortie_to_cream_" + this.entityData.get(ORANGE_BASE_VARIANT);

                if (WCGenetics.TabbyStripeTypes.isMackerel(this.entityData.get(TABBY_STRIPES))) {
                    stripesForOrangePath = folderPath + "agouti_marks/orange/mackerel_otc_" + this.entityData.get(TABBY_STRIPES_VARIANT);
                } else {
                    stripesForOrangePath = folderPath + "agouti_marks/orange/classic_otc_" + this.entityData.get(TABBY_STRIPES_VARIANT);
                }

            } else {
                orangeBasePath = folderPath + "empty";
                stripesForOrangePath = folderPath + "empty";
            }

        }

        String albinoPath = folderPath + "albino/";
        if (!WCGenetics.Albino.isNotAlbino(this.entityData.get(ALBINO))) {
            if (WCGenetics.Albino.isTrueAlbino(this.entityData.get(ALBINO))) {
                albinoPath += "full_albino_" + this.entityData.get(ALBINO_VARIANT);
            } else if (WCGenetics.Albino.isMink(this.entityData.get(ALBINO))) {
                albinoPath += "mink_" + this.entityData.get(ALBINO_VARIANT);// ALBINO -> 0-2
            } else if (WCGenetics.Albino.isSepia(this.entityData.get(ALBINO))) {
                albinoPath += "sepia_" + this.entityData.get(ALBINO_VARIANT);// ALBINO -> 0-2
            } else if (WCGenetics.Albino.isSiamese(this.entityData.get(ALBINO))) {
                albinoPath += "siamese_" + this.entityData.get(ALBINO_VARIANT);// ALBINO -> 0-2
            } else {
                albinoPath = folderPath + "empty";
            }
        } else {
            albinoPath = folderPath + "empty";
        }

        String whiteMarks = folderPath + "white_marks/";
        if (WCGenetics.WhiteRatio.isWhite(this.entityData.get(WHITE_RATIO))) {
            whiteMarks += "full_white";
        } else if (WCGenetics.WhiteRatio.isHighSpotted(this.entityData.get(WHITE_RATIO))) {
            whiteMarks += "high_spots_" + this.entityData.get(WHITE_RATIO_VARIANT);// WHITE_MARKS -> 0-3
        } else if (WCGenetics.WhiteRatio.isLowSpotted(this.entityData.get(WHITE_RATIO))) {
            whiteMarks += "low_spots_" + this.entityData.get(WHITE_RATIO_VARIANT);// WHITE_MARKS -> 0-3
        } else {
            whiteMarks = folderPath + "empty";
        }


        String agoutiMarks = folderPath + "agouti_marks/";
        if (WCGenetics.Agouti.isTabby(this.entityData.get(AGOUTI))) {
            String secondStripesKey = "";
            if (WCGenetics.Base.isBlack(this.entityData.get(BASE))) {
                if (!WCGenetics.Dilute.isDilute(this.entityData.get(DILUTE))) {
                    basePath = folderPath + "base/black_to_darkbrown";
                }
                secondStripesKey = "black_";
            } else if (WCGenetics.Base.isChocolate(this.entityData.get(BASE))) {
                secondStripesKey = "darkbrown_";
            } else if (WCGenetics.Base.isCinnamon(this.entityData.get(BASE))) {
                secondStripesKey = "mediumbrown_";
            }


            if (WCGenetics.TabbyStripeTypes.isMackerel(this.entityData.get(TABBY_STRIPES))) {
                if (WCGenetics.Dilute.isDilute(this.entityData.get(DILUTE))) {
                    agoutiMarks += "mackerel_dilute_" + secondStripesKey + this.entityData.get(TABBY_STRIPES_VARIANT); // TABBY_MARKS -> 0-4
                } else {
                    agoutiMarks += "mackerel_" + secondStripesKey + this.entityData.get(TABBY_STRIPES_VARIANT); // TABBY_MARKS -> 0-4
                }
            } else if (WCGenetics.TabbyStripeTypes.isClassic(this.entityData.get(TABBY_STRIPES))) {
                if (WCGenetics.Dilute.isDilute(this.entityData.get(DILUTE))) {
                    agoutiMarks += "classic_dilute_" + secondStripesKey + this.entityData.get(TABBY_STRIPES_VARIANT); // TABBY_MARKS -> 0-4
                } else {
                    agoutiMarks += "classic_" + secondStripesKey + this.entityData.get(TABBY_STRIPES_VARIANT); // TABBY_MARKS -> 0-4
                }
            } else {
                agoutiMarks = folderPath + "empty";
            }
        } else {
            agoutiMarks = folderPath + "empty";
        }

        String eyeColorLeft = folderPath + "eyes_color/left/";
        switch (WCGenetics.EyeColor.getEyeColor(this.entityData.get(EYE_COLOR_LEFT))) {
            case BLUE -> eyeColorLeft += "blue_" + this.entityData.get(EYE_COLOR_VARIANT_LEFT);// EYES -> 0.4
            case GREEN -> eyeColorLeft += "green_" + this.entityData.get(EYE_COLOR_VARIANT_LEFT);// EYES -> 0.4
            case YELLOW -> eyeColorLeft += "yellow_" + this.entityData.get(EYE_COLOR_VARIANT_LEFT);// EYES -> 0.4
            case RED -> eyeColorLeft += "red_" + this.entityData.get(EYE_COLOR_VARIANT_LEFT);// EYES -> 0.4
            case BLIND -> eyeColorLeft += "blind";
        }

        String eyeColorRight = folderPath + "eyes_color/right/";
        switch (WCGenetics.EyeColor.getEyeColor(this.entityData.get(EYE_COLOR_RIGHT))) {
            case BLUE -> eyeColorRight += "blue_" + this.entityData.get(EYE_COLOR_VARIANT_RIGHT);// EYES -> 0.4
            case GREEN -> eyeColorRight += "green_" + this.entityData.get(EYE_COLOR_VARIANT_RIGHT);// EYES -> 0.4
            case YELLOW -> eyeColorRight += "yellow_" + this.entityData.get(EYE_COLOR_VARIANT_RIGHT);// EYES -> 0.4
            case RED -> eyeColorRight += "red_" + this.entityData.get(EYE_COLOR_VARIANT_RIGHT);// EYES -> 0.4
            case BLIND -> eyeColorRight += "blind";
        }

        String scarsPath = folderPath + "scars/scars_" + this.entityData.get(SCARS);

        String noisePath = folderPath + "details/noise_" + this.entityData.get(NOISE);
        if (WCGenetics.Base.isBlack(this.entityData.get(BASE)) && WCGenetics.Dilute.isDilute(this.entityData.get(DILUTE))){
            noisePath = folderPath + "details/noise_black_" + this.entityData.get(NOISE);
        }
        String skinDetails = folderPath + "details/skin";

        int rufousingRatio = this.entityData.get(RUFOUSING_VARIANT);
        int rufousingIntKey = rufousingRatio * 5;
        if (WCGenetics.Albino.isTrueAlbino(this.entityData.get(ALBINO))) rufousingIntKey = 0;
        String rufousing = folderPath + "details/rufousing_" + rufousingIntKey;

        int bluerufousingRatio = this.entityData.get(BLUE_RUFOUSING_VARIANT);
        int bluerufousingIntKey = bluerufousingRatio * 5;
        if (WCGenetics.Albino.isTrueAlbino(this.entityData.get(ALBINO))) bluerufousingIntKey = 0;
        String bluerufousing = folderPath + "details/blue_rufousing_" + bluerufousingIntKey;


        String silver = folderPath + "silver/";
        String silver2 = folderPath + "silver/";
        if (WCGenetics.Silver.isSilver(this.entityData.get(SILVER), this.entityData.get(AGOUTI), this.entityData.get(ORANGE_BASE), this.getGender())) {
            silver2 = folderPath + "empty";
            if (WCGenetics.TabbyStripeTypes.isClassic(this.entityData.get(TABBY_STRIPES))) {
                silver += "classic_";
            } else {
                silver += "mackerel_";
            }
            silver += this.entityData.get(TABBY_STRIPES_VARIANT) + "_silver_" + this.entityData.get(SILVER_VARIANT);
        } else if (WCGenetics.Silver.isSmokeTortie(this.entityData.get(SILVER), this.entityData.get(AGOUTI), this.entityData.get(ORANGE_BASE))) {
            if (this.entityData.get(SILVER_VARIANT) == 2) {
                if (WCGenetics.TabbyStripeTypes.isClassic(this.entityData.get(TABBY_STRIPES))) {
                    silver += "classic_";
                } else {
                    silver += "mackerel_";
                }
                silver += this.entityData.get(TABBY_STRIPES_VARIANT) + "_smoke_" + this.entityData.get(SILVER_VARIANT);
            } else {
                silver += "smoke_" +  this.entityData.get(SILVER_VARIANT);
            }

            if (WCGenetics.TabbyStripeTypes.isClassic(this.entityData.get(TABBY_STRIPES))) {
                silver2 += "classic_";
            } else {
                silver2 += "mackerel_";
            }
            silver2 += this.entityData.get(TABBY_STRIPES_VARIANT) + "_silver_" + this.entityData.get(SILVER_VARIANT);

        } else if (WCGenetics.Silver.isSmoke(this.entityData.get(SILVER), this.entityData.get(AGOUTI))) {
            if (this.entityData.get(SILVER_VARIANT) == 2) {
                if (WCGenetics.TabbyStripeTypes.isClassic(this.entityData.get(TABBY_STRIPES))) {
                    silver += "classic_";
                } else {
                    silver += "mackerel_";
                }
                silver += this.entityData.get(TABBY_STRIPES_VARIANT) + "_smoke_" + this.entityData.get(SILVER_VARIANT);
            } else {
                silver += "smoke_" +  this.entityData.get(SILVER_VARIANT);
            }

            silver2 = folderPath + "empty";

        } else {
            silver = folderPath + "empty";
            silver2 = folderPath + "empty";
        }


        String armorOverlay = folderPath + "details/armor";

        String[] chimeraArray = defineTextureLayersChimera();

        String extraChimeraKey = "";
        if (WCGenetics.Chimerism.isChimera(this.entityData.get(CHIMERA_GENE))) {
            extraChimeraKey = WCGenetics.encodeGene(this.entityData.get(BASE_CHIMERA)) + this.entityData.get(CHIMERA_VARIANT) + "_" +
                    WCGenetics.encodeGene(this.entityData.get(ORANGE_BASE_CHIMERA)) + this.entityData.get(ORANGE_BASE_VARIANT_CHIMERA) + "_" +
                    WCGenetics.encodeGene(this.entityData.get(AGOUTI_CHIMERA)) + "_" +
                    WCGenetics.encodeGene(this.entityData.get(TABBY_STRIPES_CHIMERA)) + this.entityData.get(TABBY_STRIPES_VARIANT_CHIMERA) + "_" +
                    WCGenetics.encodeGene(this.entityData.get(SILVER_CHIMERA)) + this.entityData.get(SILVER_VARIANT_CHIMERA) + "_" +
                    WCGenetics.encodeGene(this.entityData.get(WHITE_RATIO_CHIMERA)) + this.entityData.get(WHITE_RATIO_VARIANT_CHIMERA) + "_" +
                    WCGenetics.encodeGene(this.entityData.get(DILUTE_CHIMERA)) + "_" +
                    WCGenetics.encodeGene(this.entityData.get(ALBINO_CHIMERA)) + this.entityData.get(ALBINO_VARIANT_CHIMERA) + "_" +
                    this.entityData.get(BLUE_RUFOUSING_VARIANT_CHIMERA) + "ruf" + this.entityData.get(RUFOUSING_VARIANT_CHIMERA) + "_" +
                    "noise_" + this.entityData.get(NOISE_CHIMERA);
        }


        /**
         *  // BASE -> 0-4
         *  // ORANGE_BASE -> 0-4
         *  // WHITE_MARKS -> 0-3
         *  // ALBINO -> 0-2
         *  // DILUTE -> 0-4
         *  // TABBY_MARKS -> 0-4
         *  // EYES -> 0.4
         *  // NOISE -> 0-2
         */

        this.textureLayersPaths[0] = basePath + ".png";
        this.textureLayersPaths[1] = agoutiMarks + ".png";
        this.textureLayersPaths[2] = orangeBasePath + ".png";
        this.textureLayersPaths[3] = stripesForOrangePath + ".png";
        this.textureLayersPaths[4] = rufousing + ".png";
        this.textureLayersPaths[5] = bluerufousing + ".png";
        this.textureLayersPaths[6] = silver + ".png";
        this.textureLayersPaths[7] = silver2 + ".png";
        this.textureLayersPaths[8] = whiteMarks + ".png";
        this.textureLayersPaths[9] = albinoPath + ".png";
        this.textureLayersPaths[10] = noisePath + ".png";

        this.textureLayersPaths[11] = chimeraArray[0];
        this.textureLayersPaths[12] = chimeraArray[1];
        this.textureLayersPaths[13] = chimeraArray[2];
        this.textureLayersPaths[14] = chimeraArray[3];
        this.textureLayersPaths[15] = chimeraArray[4];
        this.textureLayersPaths[16] = chimeraArray[5];
        this.textureLayersPaths[17] = chimeraArray[6];
        this.textureLayersPaths[18] = chimeraArray[7];
        //
        this.textureLayersPaths[19] = chimeraArray[7];
        this.textureLayersPaths[20] = chimeraArray[8];
        this.textureLayersPaths[21] = chimeraArray[9];

        this.textureLayersPaths[22] = skinDetails + ".png";
        this.textureLayersPaths[23] = eyeColorLeft + ".png";
        this.textureLayersPaths[24] = eyeColorRight + ".png";
        this.textureLayersPaths[25] = scarsPath + ".png";
        this.textureLayersPaths[26] = armorOverlay + ".png";

        this.textureKey =
                "wcat/" + WCGenetics.encodeGene(this.entityData.get(BASE)) + "_" +
                        WCGenetics.encodeGene(this.entityData.get(ORANGE_BASE)) + this.entityData.get(ORANGE_BASE_VARIANT) + "_" +
                        WCGenetics.encodeGene(this.entityData.get(AGOUTI)) + "_" +
                        WCGenetics.encodeGene(this.entityData.get(TABBY_STRIPES)) + this.entityData.get(TABBY_STRIPES_VARIANT) + "_" +
                        WCGenetics.encodeGene(this.entityData.get(WHITE_RATIO)) + this.entityData.get(WHITE_RATIO_VARIANT) + "_" +
                        WCGenetics.encodeGene(this.entityData.get(SILVER)) + this.entityData.get(SILVER_VARIANT) + "_" +
                        WCGenetics.encodeGene(this.entityData.get(DILUTE)) + "_" +
                        WCGenetics.encodeGene(this.entityData.get(ALBINO)) + this.entityData.get(ALBINO_VARIANT) + "_" +
                        WCGenetics.encodeGene(this.entityData.get(EYES_ANOMALY)) + "_" +
                        "eyel" + this.entityData.get(EYE_COLOR_LEFT)  + this.entityData.get(EYE_COLOR_VARIANT_LEFT) + "_" +
                        "eyer" + this.entityData.get(EYE_COLOR_RIGHT) + this.entityData.get(EYE_COLOR_VARIANT_RIGHT) + "_" +
                        "bruf" + this.entityData.get(BLUE_RUFOUSING_VARIANT) + "ruf" + this.entityData.get(RUFOUSING_VARIANT) + "_" +
                        "noise_" + this.entityData.get(NOISE) + "_" +
                        "scars_" + this.entityData.get(SCARS) + "_" +
                        extraChimeraKey;
    }


    @OnlyIn(Dist.CLIENT)
    private String[] defineTextureLayersChimera() {
        String folderPath = "warriorcats_events:textures/entity/wcat/genetics/genetics_chimera/";

        String[] chimeraArray = new String[11];

        chimeraArray[0] = folderPath + "empty.png";
        chimeraArray[1] = folderPath + "empty.png";
        chimeraArray[2] = folderPath + "empty.png";
        chimeraArray[3] = folderPath + "empty.png";
        chimeraArray[4] = folderPath + "empty.png";
        chimeraArray[5] = folderPath + "empty.png";
        chimeraArray[6] = folderPath + "empty.png";
        chimeraArray[7] = folderPath + "empty.png";
        chimeraArray[8] = folderPath + "empty.png";
        chimeraArray[9] = folderPath + "empty.png";
        chimeraArray[10] = folderPath + "empty.png";

        if (!this.isOnGeneticalSkin()) return chimeraArray;
        if (!WCGenetics.Chimerism.isChimera(this.entityData.get(CHIMERA_GENE))) return chimeraArray;

        String basePath = folderPath + "base/";
        if (WCGenetics.Base.isBlack(this.entityData.get(BASE_CHIMERA))) {
            basePath += "black_" + this.entityData.get(CHIMERA_VARIANT);
        } else if (WCGenetics.Base.isChocolate(this.entityData.get(BASE_CHIMERA))) {
            basePath += "chocolate_" + this.entityData.get(CHIMERA_VARIANT);
        } else if (WCGenetics.Base.isCinnamon(this.entityData.get(BASE_CHIMERA))) {
            basePath += "cinnamon_" + this.entityData.get(CHIMERA_VARIANT);
        } else {
            basePath = folderPath + "empty";
        }

        String orangeBasePath = folderPath + "orange_base/";
        String stripesForOrangePath = folderPath + "agouti_marks/orange/";
        if (WCGenetics.OrangeBase.isOrange(this.entityData.get(ORANGE_BASE_CHIMERA), this.entityData.get(GENDER))) {

            orangeBasePath += "orange";

            if (WCGenetics.TabbyStripeTypes.isMackerel(this.entityData.get(TABBY_STRIPES_CHIMERA))) {
                stripesForOrangePath += "mackerel_orange_" + this.entityData.get(TABBY_STRIPES_VARIANT_CHIMERA);// ORANGE_BASE -> 0-4
            } else {
                stripesForOrangePath += "classic_orange_" + this.entityData.get(TABBY_STRIPES_VARIANT_CHIMERA);// ORANGE_BASE -> 0-4
            }

        } else if (WCGenetics.OrangeBase.isTortoiseshell(this.entityData.get(ORANGE_BASE_CHIMERA))) {

            orangeBasePath += "tortie_" + this.entityData.get(ORANGE_BASE_VARIANT_CHIMERA);// ORANGE_BASE -> 0-4

            if (WCGenetics.TabbyStripeTypes.isMackerel(this.entityData.get(TABBY_STRIPES_CHIMERA))) {
                stripesForOrangePath += "mackerel_orange_" + this.entityData.get(TABBY_STRIPES_VARIANT_CHIMERA);// ORANGE_BASE -> 0-4
            } else {
                stripesForOrangePath += "classic_orange_" + this.entityData.get(TABBY_STRIPES_VARIANT_CHIMERA);// ORANGE_BASE -> 0-4
            }

        } else {
            orangeBasePath = folderPath + "empty";
            stripesForOrangePath = folderPath + "empty";
        }


        if (WCGenetics.Dilute.isDilute(this.entityData.get(DILUTE_CHIMERA))) {
            if (WCGenetics.Base.isBlack(this.entityData.get(BASE_CHIMERA))) {
                basePath = folderPath + "base/black_to_gray_" + this.entityData.get(CHIMERA_VARIANT);
            } else if (WCGenetics.Base.isChocolate(this.entityData.get(BASE_CHIMERA))) {
                basePath = folderPath + "base/chocolate_to_lilac_" + this.entityData.get(CHIMERA_VARIANT);
            } else if (WCGenetics.Base.isCinnamon(this.entityData.get(BASE_CHIMERA))) {
                basePath = folderPath + "base/cinnamon_to_fawn_" + this.entityData.get(CHIMERA_VARIANT);
            }

            if (WCGenetics.OrangeBase.isOrange(this.entityData.get(ORANGE_BASE_CHIMERA), this.entityData.get(GENDER))) {
                orangeBasePath = folderPath + "orange_base/orange_to_cream";

                if (WCGenetics.TabbyStripeTypes.isMackerel(this.entityData.get(TABBY_STRIPES_CHIMERA))) {
                    stripesForOrangePath = folderPath + "agouti_marks/orange/mackerel_otc_" + this.entityData.get(TABBY_STRIPES_VARIANT_CHIMERA);
                } else {
                    stripesForOrangePath = folderPath + "agouti_marks/orange/classic_otc_" + this.entityData.get(TABBY_STRIPES_VARIANT_CHIMERA);
                }

            } else if (WCGenetics.OrangeBase.isTortoiseshell(this.entityData.get(ORANGE_BASE_CHIMERA))) {
                orangeBasePath = folderPath + "orange_base/tortie_to_cream_" + this.entityData.get(ORANGE_BASE_VARIANT_CHIMERA);

                if (WCGenetics.TabbyStripeTypes.isMackerel(this.entityData.get(TABBY_STRIPES_CHIMERA))) {
                    stripesForOrangePath = folderPath + "agouti_marks/orange/mackerel_otc_" + this.entityData.get(TABBY_STRIPES_VARIANT_CHIMERA);
                } else {
                    stripesForOrangePath = folderPath + "agouti_marks/orange/classic_otc_" + this.entityData.get(TABBY_STRIPES_VARIANT_CHIMERA);
                }

            } else {
                orangeBasePath = folderPath + "empty";
                stripesForOrangePath = folderPath + "empty";
            }

        }

        String albinoPath = folderPath + "albino/";
        if (!WCGenetics.Albino.isNotAlbino(this.entityData.get(ALBINO_CHIMERA))) {
            if (WCGenetics.Albino.isTrueAlbino(this.entityData.get(ALBINO_CHIMERA))) {
                albinoPath += "full_albino_" + this.entityData.get(ALBINO_VARIANT_CHIMERA);
            } else if (WCGenetics.Albino.isMink(this.entityData.get(ALBINO_CHIMERA))) {
                albinoPath += "mink_" + this.entityData.get(ALBINO_VARIANT_CHIMERA);// ALBINO -> 0-2
            } else if (WCGenetics.Albino.isSepia(this.entityData.get(ALBINO_CHIMERA))) {
                albinoPath += "sepia_" + this.entityData.get(ALBINO_VARIANT_CHIMERA);// ALBINO -> 0-2
            } else if (WCGenetics.Albino.isSiamese(this.entityData.get(ALBINO_CHIMERA))) {
                albinoPath += "siamese_" + this.entityData.get(ALBINO_VARIANT_CHIMERA);// ALBINO -> 0-2
            } else {
                albinoPath = folderPath + "empty";
            }
        } else {
            albinoPath = folderPath + "empty";
        }

        String whiteMarks = folderPath + "white_marks/";
        if (WCGenetics.WhiteRatio.isWhite(this.entityData.get(WHITE_RATIO_CHIMERA))) {
            whiteMarks += "full_white";
        } else if (WCGenetics.WhiteRatio.isHighSpotted(this.entityData.get(WHITE_RATIO_CHIMERA))) {
            whiteMarks += "high_spots_" + this.entityData.get(WHITE_RATIO_VARIANT_CHIMERA);// WHITE_MARKS -> 0-3
        } else if (WCGenetics.WhiteRatio.isLowSpotted(this.entityData.get(WHITE_RATIO_CHIMERA))) {
            whiteMarks += "low_spots_" + this.entityData.get(WHITE_RATIO_VARIANT_CHIMERA);// WHITE_MARKS -> 0-3
        } else {
            whiteMarks = folderPath + "empty";
        }


        String agoutiMarks = folderPath + "agouti_marks/";
        if (WCGenetics.Agouti.isTabby(this.entityData.get(AGOUTI_CHIMERA))) {
            String secondStripesKey = "";
            if (WCGenetics.Base.isBlack(this.entityData.get(BASE_CHIMERA))) {
                if (!WCGenetics.Dilute.isDilute(this.entityData.get(DILUTE_CHIMERA))) {
                    basePath = folderPath + "base/black_to_darkbrown_" + this.entityData.get(CHIMERA_VARIANT);
                }
                secondStripesKey = "black_";
            } else if (WCGenetics.Base.isChocolate(this.entityData.get(BASE_CHIMERA))) {
                secondStripesKey = "darkbrown_";
            } else if (WCGenetics.Base.isCinnamon(this.entityData.get(BASE_CHIMERA))) {
                secondStripesKey = "mediumbrown_";
            }


            if (WCGenetics.TabbyStripeTypes.isMackerel(this.entityData.get(TABBY_STRIPES_CHIMERA))) {
                if (WCGenetics.Dilute.isDilute(this.entityData.get(DILUTE_CHIMERA))) {
                    agoutiMarks += "mackerel_dilute_" + secondStripesKey + this.entityData.get(TABBY_STRIPES_VARIANT_CHIMERA); // TABBY_MARKS -> 0-4
                } else {
                    agoutiMarks += "mackerel_" + secondStripesKey + this.entityData.get(TABBY_STRIPES_VARIANT_CHIMERA); // TABBY_MARKS -> 0-4
                }
            } else if (WCGenetics.TabbyStripeTypes.isClassic(this.entityData.get(TABBY_STRIPES_CHIMERA))) {
                if (WCGenetics.Dilute.isDilute(this.entityData.get(DILUTE_CHIMERA))) {
                    agoutiMarks += "classic_dilute_" + secondStripesKey + this.entityData.get(TABBY_STRIPES_VARIANT_CHIMERA); // TABBY_MARKS -> 0-4
                } else {
                    agoutiMarks += "classic_" + secondStripesKey + this.entityData.get(TABBY_STRIPES_VARIANT_CHIMERA); // TABBY_MARKS -> 0-4
                }
            } else {
                agoutiMarks = folderPath + "empty";
            }
        } else {
            agoutiMarks = folderPath + "empty";
        }


        String noisePath = folderPath + "details/noise_" + this.entityData.get(NOISE_CHIMERA);
        if (WCGenetics.Base.isBlack(this.entityData.get(BASE_CHIMERA)) && WCGenetics.Dilute.isDilute(this.entityData.get(DILUTE_CHIMERA))){
            noisePath = folderPath + "details/noise_black_" + this.entityData.get(NOISE_CHIMERA);
        }

        int rufousingRatio = this.entityData.get(RUFOUSING_VARIANT_CHIMERA);
        int rufousingIntKey = rufousingRatio * 5;
        if (WCGenetics.Albino.isTrueAlbino(this.entityData.get(ALBINO_CHIMERA))) rufousingIntKey = 0;
        String rufousing = folderPath + "details/rufousing_" + rufousingIntKey;

        int bluerufousingRatio = this.entityData.get(BLUE_RUFOUSING_VARIANT_CHIMERA);
        int bluerufousingIntKey = bluerufousingRatio * 5;
        if (WCGenetics.Albino.isTrueAlbino(this.entityData.get(ALBINO_CHIMERA))) bluerufousingIntKey = 0;
        String bluerufousing = folderPath + "details/blue_rufousing_" + bluerufousingIntKey;

        String silver = folderPath + "silver/";
        String silver2 = folderPath + "silver/";
        if (WCGenetics.Silver.isSilver(this.entityData.get(SILVER_CHIMERA), this.entityData.get(AGOUTI_CHIMERA), this.entityData.get(ORANGE_BASE_CHIMERA), this.getGender())) {
            silver2 = folderPath + "empty";
            if (WCGenetics.TabbyStripeTypes.isClassic(this.entityData.get(TABBY_STRIPES_CHIMERA))) {
                silver += "classic_";
            } else {
                silver += "mackerel_";
            }
            silver += this.entityData.get(TABBY_STRIPES_VARIANT_CHIMERA) + "_silver_" + this.entityData.get(SILVER_VARIANT_CHIMERA);
        } else if (WCGenetics.Silver.isSmokeTortie(this.entityData.get(SILVER_CHIMERA), this.entityData.get(AGOUTI_CHIMERA), this.entityData.get(ORANGE_BASE_CHIMERA))) {
            if (this.entityData.get(SILVER_VARIANT_CHIMERA) == 2) {
                if (WCGenetics.TabbyStripeTypes.isClassic(this.entityData.get(TABBY_STRIPES_CHIMERA))) {
                    silver += "classic_";
                } else {
                    silver += "mackerel_";
                }
                silver += this.entityData.get(TABBY_STRIPES_VARIANT_CHIMERA) + "_smoke_" + this.entityData.get(SILVER_VARIANT_CHIMERA);
            } else {
                silver += "smoke_" +  this.entityData.get(SILVER_VARIANT_CHIMERA);
            }

            if (WCGenetics.TabbyStripeTypes.isClassic(this.entityData.get(TABBY_STRIPES_CHIMERA))) {
                silver2 += "classic_";
            } else {
                silver2 += "mackerel_";
            }
            silver2 += this.entityData.get(TABBY_STRIPES_VARIANT_CHIMERA) + "_silver_" + this.entityData.get(SILVER_VARIANT_CHIMERA);

        } else if (WCGenetics.Silver.isSmoke(this.entityData.get(SILVER_CHIMERA), this.entityData.get(AGOUTI_CHIMERA))) {
            if (this.entityData.get(SILVER_VARIANT_CHIMERA) == 2) {
                if (WCGenetics.TabbyStripeTypes.isClassic(this.entityData.get(TABBY_STRIPES_CHIMERA))) {
                    silver += "classic_";
                } else {
                    silver += "mackerel_";
                }
                silver += this.entityData.get(TABBY_STRIPES_VARIANT_CHIMERA) + "_smoke_" + this.entityData.get(SILVER_VARIANT_CHIMERA);
            } else {
                silver += "smoke_" +  this.entityData.get(SILVER_VARIANT_CHIMERA);
            }

            silver2 = folderPath + "empty";

        } else {
            silver = folderPath + "empty";
            silver2 = folderPath + "empty";
        }
        /**
         *  // BASE -> 0-4
         *  // ORANGE_BASE -> 0-4
         *  // WHITE_MARKS -> 0-3
         *  // ALBINO -> 0-2
         *  // DILUTE -> 0-4
         *  // TABBY_MARKS -> 0-4
         *  // EYES -> 0.4
         *  // NOISE -> 0-2
         */

        chimeraArray[0] = basePath + ".png";
        chimeraArray[1] = agoutiMarks + ".png";
        chimeraArray[2] = orangeBasePath + ".png";
        chimeraArray[3] = stripesForOrangePath + ".png";
        chimeraArray[4] = rufousing + ".png";
        chimeraArray[5] = bluerufousing + ".png";
        chimeraArray[6] = silver + ".png";
        chimeraArray[7] = silver2 + ".png";
        chimeraArray[8] = whiteMarks + ".png";
        chimeraArray[9] = albinoPath + ".png";
        chimeraArray[10] = noisePath + ".png";


        return chimeraArray;
    }



    @OnlyIn(Dist.CLIENT)
    public String[] getTextureLayersPaths() {
        if (this.textureKey == null) this.defineTextureLayers();

        return this.textureLayersPaths;
    }

    public void inheritGeneticsFromParents(WCGenetics mother, WCGenetics father) {
        WCGenetics childGenes = new WCGenetics();

        childGenes.chestFur = inheritGenetics(mother.chestFur, father.chestFur, random);
        childGenes.bellyFur = inheritGenetics(mother.bellyFur, father.bellyFur, random);
        childGenes.legsFur = inheritGenetics(mother.legsFur, father.legsFur, random);
        childGenes.headFur = inheritGenetics(mother.headFur, father.headFur, random);
        childGenes.cheekFur = inheritGenetics(mother.cheekFur, father.cheekFur, random);
        childGenes.backFur = inheritGenetics(mother.backFur, father.backFur, random);
        childGenes.tailFur = inheritGenetics(mother.tailFur, father.tailFur, random);
        childGenes.bobtail = inheritGenetics(mother.bobtail, father.bobtail, random);

        childGenes.base = inheritGenetics(mother.base, father.base, random);
        childGenes.orangeBase = inheritGenetics(mother.orangeBase, father.orangeBase, random);
        childGenes.whiteRatio = inheritGenetics(mother.whiteRatio, father.whiteRatio, random);
        childGenes.albino = inheritGenetics(mother.albino, father.albino, random);
        childGenes.dilute = inheritGenetics(mother.dilute, father.dilute, random);
        childGenes.agouti = inheritGenetics(mother.agouti, father.agouti, random);
        childGenes.tabbyStripes = inheritGenetics(mother.tabbyStripes, father.tabbyStripes, random);
        childGenes.eyesAnomaly = inheritGenetics(mother.eyesAnomaly, father.eyesAnomaly, random);
        childGenes.chimeraGene = inheritGenetics(mother.chimeraGene, father.chimeraGene, random);
        childGenes.silver = inheritGenetics(mother.silver, father.silver, random);


        this.setGenetics(childGenes);


        if (WCGenetics.Chimerism.isChimera(childGenes.chimeraGene)) {
            WCGenetics chimeraChildGenes = new WCGenetics();
            WCGenetics.GeneticalChimeraVariants chimeraChildVariants = new WCGenetics.GeneticalChimeraVariants();


            WCGenetics toInheritFrom;
            if (this.random.nextBoolean()) {
                toInheritFrom = father;
            } else {
                toInheritFrom = mother;
            }

            chimeraChildGenes.chimeraGene = inheritGenetics(father.chimeraGene, mother.chimeraGene, random);

            chimeraChildGenes.base = inheritGenetics(toInheritFrom.base, toInheritFrom.base, random);
            chimeraChildGenes.orangeBase = inheritGenetics(toInheritFrom.orangeBase, toInheritFrom.orangeBase, random);
            chimeraChildGenes.whiteRatio = inheritGenetics(toInheritFrom.whiteRatio, toInheritFrom.whiteRatio, random);
            chimeraChildGenes.albino = inheritGenetics(toInheritFrom.albino, toInheritFrom.albino, random);
            chimeraChildGenes.dilute = inheritGenetics(toInheritFrom.dilute, toInheritFrom.dilute, random);
            chimeraChildGenes.silver = inheritGenetics(toInheritFrom.silver, toInheritFrom.silver, random);
            chimeraChildGenes.agouti = inheritGenetics(toInheritFrom.agouti, toInheritFrom.agouti, random);
            chimeraChildGenes.tabbyStripes = inheritGenetics(toInheritFrom.tabbyStripes, toInheritFrom.tabbyStripes, random);
            chimeraChildGenes.rufousing = (father.rufousing + mother.rufousing)/2;
            chimeraChildGenes.blueRufousing = (father.blueRufousing + mother.blueRufousing)/2;
            chimeraChildGenes.noise = (father.noise + mother.noise)/2;

            this.setChimeraGenetics(chimeraChildGenes);

            chimeraChildVariants.chimeraVariant = this.random.nextInt(WCGenetics.Values.MAX_CHIMERISM_VARIANTS);
            chimeraChildVariants.rufousingVariant = this.random.nextInt(WCGenetics.Values.MAX_RUFOUSING_VARIANTS);
            chimeraChildVariants.blueRufousingVariant = this.random.nextInt(WCGenetics.Values.MAX_BLUE_RUFOUSING_VARIANTS);
            chimeraChildVariants.orangeVar = this.random.nextInt(WCGenetics.Values.MAX_ORANGE_VARIANTS);
            chimeraChildVariants.whiteVar = this.random.nextInt(WCGenetics.Values.MAX_WHITE_VARIANTS);
            chimeraChildVariants.tabbyVar = this.random.nextInt(WCGenetics.Values.MAX_TABBY_VARIANTS);
            chimeraChildVariants.albinoVar = this.random.nextInt(WCGenetics.Values.MAX_ALBINO_VARIANTS);
            chimeraChildVariants.noise = this.random.nextInt(WCGenetics.Values.MAX_NOISE_VARIANTS);
            chimeraChildVariants.silverVar = this.random.nextInt(WCGenetics.Values.MAX_SILVER_VARIANTS);

            this.setGeneticalVariantsChimera(chimeraChildVariants);

        }

        String leftEyeColor = WCGenetics.EyeColor.generateAlelo(this.random, this.entityData.get(WHITE_RATIO), this.entityData.get(ALBINO));
        int eyeLeftVariant = this.random.nextInt(WCGenetics.Values.MAX_EYE_VARIANTS);

        this.entityData.set(EYE_COLOR_LEFT, leftEyeColor);
        this.entityData.set(EYE_COLOR_VARIANT_LEFT, eyeLeftVariant);

        if (WCGenetics.EyesAnomaly.isHeteroChromic(this.entityData.get(EYES_ANOMALY))) {
            this.entityData.set(EYE_COLOR_RIGHT, WCGenetics.EyeColor.generateAlelo(this.random, this.entityData.get(WHITE_RATIO), this.entityData.get(ALBINO)));
            this.entityData.set(EYE_COLOR_VARIANT_RIGHT, this.random.nextInt(WCGenetics.Values.MAX_EYE_VARIANTS));
        } else {
            this.entityData.set(EYE_COLOR_RIGHT, leftEyeColor);
            this.entityData.set(EYE_COLOR_VARIANT_RIGHT, eyeLeftVariant);
        }

        this.entityData.set(ORANGE_BASE_VARIANT, this.random.nextInt(WCGenetics.Values.MAX_ORANGE_VARIANTS));
        this.entityData.set(WHITE_RATIO_VARIANT, this.random.nextInt(WCGenetics.Values.MAX_WHITE_VARIANTS));
        this.entityData.set(ALBINO_VARIANT, this.random.nextInt(WCGenetics.Values.MAX_ALBINO_VARIANTS));
        this.entityData.set(TABBY_STRIPES_VARIANT, this.random.nextInt(WCGenetics.Values.MAX_TABBY_VARIANTS));
        this.entityData.set(SILVER_VARIANT, this.random.nextInt(WCGenetics.Values.MAX_SILVER_VARIANTS));

        int rufousingVar = (mother.rufousing + father.rufousing)/2;
        int blueRufousingVar = (mother.blueRufousing + father.blueRufousing)/2;

        if (WCGenetics.Base.isBlack(this.entityData.get(BASE))) {
            this.entityData.set(RUFOUSING_VARIANT, this.random.nextInt(3));
        } else {
            this.entityData.set(RUFOUSING_VARIANT, rufousingVar);
        }

        if (WCGenetics.Dilute.isDilute(this.entityData.get(DILUTE))) {
            this.entityData.set(BLUE_RUFOUSING_VARIANT, this.random.nextInt(3));
        } else {
            this.entityData.set(BLUE_RUFOUSING_VARIANT, blueRufousingVar);
        }

    }

    public void setGeneticalVariants(String eyesVariantLeft, String eyesVariantRight, int rufVar, int blueRufVar,
                                     int orangeVar, int whiteVar, int tabbyVar,
                                     int albinoVar, int leftEyeVar, int rightEyeVar, int noise,
                                     float size, int silverVar, int scars) {

        this.entityData.set(EYE_COLOR_LEFT, eyesVariantLeft);
        this.entityData.set(EYE_COLOR_RIGHT, eyesVariantRight);
        this.entityData.set(RUFOUSING_VARIANT, Math.min(rufVar, WCGenetics.Values.MAX_RUFOUSING_VARIANTS-1));
        this.entityData.set(BLUE_RUFOUSING_VARIANT, Math.min(blueRufVar, WCGenetics.Values.MAX_BLUE_RUFOUSING_VARIANTS-1));
        this.entityData.set(ORANGE_BASE_VARIANT, Math.min(orangeVar, WCGenetics.Values.MAX_ORANGE_VARIANTS-1));
        this.entityData.set(WHITE_RATIO_VARIANT, Math.min(WCGenetics.Values.MAX_WHITE_VARIANTS-1, whiteVar));
        this.entityData.set(TABBY_STRIPES_VARIANT, Math.min(tabbyVar, WCGenetics.Values.MAX_TABBY_VARIANTS-1));
        this.entityData.set(ALBINO_VARIANT, Math.min(albinoVar, WCGenetics.Values.MAX_ALBINO_VARIANTS-1));
        this.entityData.set(EYE_COLOR_VARIANT_LEFT, Math.min(leftEyeVar, WCGenetics.Values.MAX_EYE_VARIANTS-1));
        this.entityData.set(EYE_COLOR_VARIANT_RIGHT, Math.min(rightEyeVar, WCGenetics.Values.MAX_EYE_VARIANTS-1));
        this.entityData.set(NOISE, Math.min(noise, WCGenetics.Values.MAX_NOISE_VARIANTS-1));
        this.entityData.set(SILVER_VARIANT, Math.min(silverVar, WCGenetics.Values.MAX_SILVER_VARIANTS-1));
        this.entityData.set(SIZE, size);
        this.entityData.set(SCARS, Math.min(scars, WCGenetics.Values.MAX_SCAR_VARIANTS-1));

    }

    public void setGeneticalVariants(WCGenetics.GeneticalVariants variants) {

        this.entityData.set(EYE_COLOR_LEFT, variants.eyeColorLeft);
        this.entityData.set(EYE_COLOR_RIGHT, variants.eyeColorRight);
        this.entityData.set(RUFOUSING_VARIANT, Math.min(variants.rufousingVariant, WCGenetics.Values.MAX_RUFOUSING_VARIANTS-1));
        this.entityData.set(BLUE_RUFOUSING_VARIANT, Math.min(variants.blueRufousingVariant, WCGenetics.Values.MAX_BLUE_RUFOUSING_VARIANTS-1));
        this.entityData.set(ORANGE_BASE_VARIANT, Math.min(variants.orangeVar, WCGenetics.Values.MAX_ORANGE_VARIANTS-1));
        this.entityData.set(WHITE_RATIO_VARIANT, Math.min(WCGenetics.Values.MAX_WHITE_VARIANTS-1, variants.whiteVar));
        this.entityData.set(TABBY_STRIPES_VARIANT, Math.min(variants.tabbyVar, WCGenetics.Values.MAX_TABBY_VARIANTS-1));
        this.entityData.set(ALBINO_VARIANT, Math.min(variants.albinoVar, WCGenetics.Values.MAX_ALBINO_VARIANTS-1));
        this.entityData.set(EYE_COLOR_VARIANT_LEFT, Math.min(variants.leftEyeVar, WCGenetics.Values.MAX_EYE_VARIANTS-1));
        this.entityData.set(EYE_COLOR_VARIANT_RIGHT, Math.min(variants.rightEyeVar, WCGenetics.Values.MAX_EYE_VARIANTS-1));
        this.entityData.set(NOISE, Math.min(variants.noise, WCGenetics.Values.MAX_NOISE_VARIANTS-1));
        this.entityData.set(SILVER_VARIANT, Math.min(variants.silverVar, WCGenetics.Values.MAX_SILVER_VARIANTS-1));
        this.entityData.set(SIZE, variants.size);
        this.entityData.set(SCARS, Math.min(variants.scars, WCGenetics.Values.MAX_SCAR_VARIANTS-1));

    }

    public void setGeneticalVariantsChimera(int chimeraVariant, int rufVar, int blueRufVar,
                                            int orangeVar, int whiteVar, int tabbyVar,
                                            int albinoVar, int noise, int silverVar) {

        this.entityData.set(CHIMERA_VARIANT, Math.min(chimeraVariant, WCGenetics.Values.MAX_CHIMERISM_VARIANTS-1));

        this.entityData.set(RUFOUSING_VARIANT_CHIMERA, Math.min(rufVar, WCGenetics.Values.MAX_RUFOUSING_VARIANTS-1));
        this.entityData.set(BLUE_RUFOUSING_VARIANT_CHIMERA, Math.min(blueRufVar, WCGenetics.Values.MAX_BLUE_RUFOUSING_VARIANTS-1));
        this.entityData.set(ORANGE_BASE_VARIANT_CHIMERA, Math.min(orangeVar, WCGenetics.Values.MAX_ORANGE_VARIANTS-1));
        this.entityData.set(WHITE_RATIO_VARIANT_CHIMERA, Math.min(WCGenetics.Values.MAX_WHITE_VARIANTS-1, whiteVar));
        this.entityData.set(TABBY_STRIPES_VARIANT_CHIMERA, Math.min(tabbyVar, WCGenetics.Values.MAX_TABBY_VARIANTS-1));
        this.entityData.set(ALBINO_VARIANT_CHIMERA, Math.min(albinoVar, WCGenetics.Values.MAX_ALBINO_VARIANTS-1));
        this.entityData.set(NOISE_CHIMERA, Math.min(noise, WCGenetics.Values.MAX_NOISE_VARIANTS-1));
        this.entityData.set(SILVER_VARIANT_CHIMERA, Math.min(silverVar, WCGenetics.Values.MAX_SILVER_VARIANTS-1));

    }

    public void setGeneticalVariantsChimera(WCGenetics.GeneticalChimeraVariants variants) {

        this.entityData.set(CHIMERA_VARIANT, Math.min(variants.chimeraVariant, WCGenetics.Values.MAX_CHIMERISM_VARIANTS-1));

        this.entityData.set(RUFOUSING_VARIANT_CHIMERA, Math.min(variants.rufousingVariant, WCGenetics.Values.MAX_RUFOUSING_VARIANTS-1));
        this.entityData.set(BLUE_RUFOUSING_VARIANT_CHIMERA, Math.min(variants.blueRufousingVariant, WCGenetics.Values.MAX_BLUE_RUFOUSING_VARIANTS-1));
        this.entityData.set(ORANGE_BASE_VARIANT_CHIMERA, Math.min(variants.orangeVar, WCGenetics.Values.MAX_ORANGE_VARIANTS-1));
        this.entityData.set(WHITE_RATIO_VARIANT_CHIMERA, Math.min(WCGenetics.Values.MAX_WHITE_VARIANTS-1, variants.whiteVar));
        this.entityData.set(TABBY_STRIPES_VARIANT_CHIMERA, Math.min(variants.tabbyVar, WCGenetics.Values.MAX_TABBY_VARIANTS-1));
        this.entityData.set(ALBINO_VARIANT_CHIMERA, Math.min(variants.albinoVar, WCGenetics.Values.MAX_ALBINO_VARIANTS-1));
        this.entityData.set(NOISE_CHIMERA, Math.min(variants.noise, WCGenetics.Values.MAX_NOISE_VARIANTS-1));
        this.entityData.set(SILVER_VARIANT_CHIMERA, Math.min(variants.silverVar, WCGenetics.Values.MAX_SILVER_VARIANTS-1));

    }

    public void setChimeraGenetics(WCGenetics genetics) {

        this.entityData.set(CHIMERA_GENE, genetics.chimeraGene);

        this.entityData.set(BASE_CHIMERA, genetics.base);
        this.entityData.set(ORANGE_BASE_CHIMERA, genetics.orangeBase);
        this.entityData.set(WHITE_RATIO_CHIMERA, genetics.whiteRatio);
        this.entityData.set(ALBINO_CHIMERA, genetics.albino);
        this.entityData.set(DILUTE_CHIMERA, genetics.dilute);
        this.entityData.set(AGOUTI_CHIMERA, genetics.agouti);
        this.entityData.set(TABBY_STRIPES_CHIMERA, genetics.tabbyStripes);

        this.entityData.set(RUFOUSING_VARIANT_CHIMERA, genetics.rufousing);
        this.entityData.set(BLUE_RUFOUSING_VARIANT_CHIMERA, genetics.blueRufousing);
        this.entityData.set(NOISE_CHIMERA, genetics.noise);
        this.entityData.set(SILVER_CHIMERA, genetics.silver);
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


    public void setNonGeneticalValues(WCGenetics genetics, float size) {
        this.entityData.set(CHEST_FUR, genetics.chestFur);
        this.entityData.set(BELLY_FUR, genetics.bellyFur);
        this.entityData.set(LEGS_FUR, genetics.legsFur);
        this.entityData.set(HEAD_FUR, genetics.headFur);
        this.entityData.set(CHEEK_FUR, genetics.cheekFur);
        this.entityData.set(BACK_FUR, genetics.backFur);
        this.entityData.set(BOBTAIL, genetics.bobtail);
        this.entityData.set(TAIL_FUR, genetics.tailFur);

        this.entityData.set(SIZE, size);
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

        if (vehicle instanceof Player player) {
            if (PlayerShape.getCurrentShape(player) instanceof WCatEntity cat) {
                sizeOffset = cat.getSize()*0.07f;
                if (cat.getSize() > 1.1f) sizeOffset += 0.05;
                sizeOffsetDistance = sizeOffset;
                if (cat.getSize() <= 0.81) {
                    sizeOffset = -sizeOffset;
                    if (cat.getSize() <= 0.61) {
                        sizeOffset = sizeOffset - 0.07f;
                        sizeOffsetDistance += 0.10f;
                    }
                    sizeOffsetDistance = -sizeOffsetDistance*0.4f;
                }

            }
        }

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

        Component message = Component.empty()
                .append(prefix)
                .append(Component.literal("kit has become an apprentice. "))
                .append(prefix)
                .append(Component.literal("kit will now be known as "))
                .append(Component.literal(newName).withStyle(ChatFormatting.GOLD))
                .append(Component.literal("!")
                );

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
        this.initializeGenetics();
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


    public class WCGoals {

        public static class WCatFollowOwnerGoal extends Goal {

            private final TamableAnimal cat;
            private LivingEntity owner;
            private final double speed;
            private final float stopDistance;
            private final float startDistance;
            private final double angleOffset;


            public WCatFollowOwnerGoal(TamableAnimal cat, double speed, float stopDistance, float startDistance) {
                this.cat = cat;
                this.speed = speed;
                this.angleOffset = cat.getId() * 0.8;


                if (cat instanceof WCatEntity wCatEntity && (wCatEntity.getPersonality() == Personality.INDEPENDENT || wCatEntity.getPersonality() == Personality.SHY)) {
                    if (wCatEntity.getPersonality() == Personality.SHY) {
                        this.stopDistance = stopDistance * 5;
                        this.startDistance = startDistance * 5;
                    } else {
                        this.stopDistance = stopDistance * 3;
                        this.startDistance = startDistance * 3;
                    }
                } else {
                    this.stopDistance = stopDistance;
                    this.startDistance = startDistance;
                }

                this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
            }

            @Override
            public boolean canUse() {

                if (!(cat instanceof WCatEntity)) return false;
                WCatEntity wcat = (WCatEntity) cat;


                if (!cat.isTame()) return false;
                if (wcat.mode != CatMode.FOLLOW) return false;
                if (cat.isOrderedToSit()) return false;

                if (cat.getTarget() != null && cat.getTarget().isAlive()) return false;

                LivingEntity ownerEntity = cat.getOwner();
                if (ownerEntity == null) return false;
                if (cat.distanceTo(ownerEntity) < startDistance) return false;

                this.owner = ownerEntity;
                return true;
            }

            @Override
            public boolean canContinueToUse() {
                if (!(cat instanceof WCatEntity)) return false;
                WCatEntity wcat = (WCatEntity) cat;

                if (wcat.mode != CatMode.FOLLOW) return false;
                if (cat.isOrderedToSit()) return false;

                if (cat.getTarget() != null && cat.getTarget().isAlive()) return false;

                if (owner == null || !owner.isAlive()) return false;

                return cat.distanceTo(owner) > stopDistance;
            }

            @Override
            public void tick() {
                if (owner == null) return;

                double dist = cat.distanceTo(owner);

                cat.getLookControl().setLookAt(owner, 10.0F, cat.getMaxHeadXRot());
                applySeparation();

                if (dist > 25 && (cat.getOwner() != null && cat.getOwner().onGround())) {
                    cat.teleportTo(owner.getX(), owner.getY(), owner.getZ());
                    cat.getNavigation().stop();
//                return;
                }

                if (dist <= stopDistance) {
                    cat.getNavigation().stop();
                    return;
                }

                double dx = cat.getX() - owner.getX();
                double dz = cat.getZ() - owner.getZ();
                double len = Math.sqrt(dx * dx + dz * dz);

                if (len < 0.001) len = 0.001;

                double ratio = stopDistance / len;

                double targetX = owner.getX() + dx * ratio;
                double targetZ = owner.getZ() + dz * ratio;
                double targetY = owner.getY();

                double offsetX = Math.cos(angleOffset) * 0.6;
                double offsetZ = Math.sin(angleOffset) * 0.6;

                if (!hasGroundAhead(targetX, targetZ) || pathHasVoidAhead(owner) ||
                        (cat.distanceTo(owner) < 6.5D && isACatTooClose())) {
                    cat.getNavigation().stop();
                    return;
                }

                cat.getNavigation().moveTo(targetX + offsetX, targetY, targetZ + offsetZ, speed);
            }

            private boolean isACatTooClose() {
                AABB box = this.cat.getBoundingBox().inflate(0.6);
                List<WCatEntity> entities = cat.level().getEntitiesOfClass(
                        WCatEntity.class,
                        box,
                        kitty -> kitty.isAlive() && kitty != this.cat && kitty.mode == CatMode.FOLLOW
                );
                return !entities.isEmpty();
            }


            private boolean pathHasVoidAhead(Entity owner) {
                Vec3 catPos = cat.position();
                Vec3 playerPos = owner.position();

                Vec3 dir = playerPos.subtract(catPos);
                double dist = dir.length();
                dir = dir.normalize();

                Level level = cat.level();

                for (double d = 0; d < Math.min(dist, 6); d += 0.5) {
                    Vec3 sample = catPos.add(dir.scale(d));
                    BlockPos pos = BlockPos.containing(sample.x, cat.getY(), sample.z);

                    boolean hasGround = false;
                    for (int i = 1; i <= 4; i++) {
                        if (!level.isEmptyBlock(pos.below(i))) {
                            hasGround = true;
                            break;
                        }
                    }

                    if (!hasGround) {
                        return true;
                    }
                }

                return false;
            }


            private boolean hasGroundAhead(double targetX, double targetZ) {
                Level level = cat.level();

                BlockPos pos = BlockPos.containing(targetX, cat.getY(), targetZ);

                for (int i = 0; i < 4; i++) {
                    BlockPos check = pos.below(i + 1);
                    if (!level.isEmptyBlock(check)) {
                        return true;
                    }
                }
                return false;
            }


            private void applySeparation() {
                AABB box = cat.getBoundingBox().inflate(0.3);
                List<WCatEntity> others = cat.level().getEntitiesOfClass(
                        WCatEntity.class,
                        box,
                        e -> e != cat
                );

                if (others.isEmpty()) return;

                double pushX = 0;
                double pushZ = 0;

                for (WCatEntity other : others) {
                    double dx = cat.getX() - other.getX();
                    double dz = cat.getZ() - other.getZ();
                    double dist = Math.max(Math.sqrt(dx * dx + dz * dz), 0.001);

                    pushX += dx / dist;
                    pushZ += dz / dist;
                }

                cat.setDeltaMovement(
                        cat.getDeltaMovement().add(pushX * 0.05, 0, pushZ * 0.05)
                );
            }

        }

        public static class WCatCasualBlockSeekGoal extends Goal {

            private final WCatEntity cat;
            private final double speed;
            private final int baseRadius;
            private final double chance;
            private int cooldown = 0;

            private BlockPos targetPos = null;
            private Predicate<BlockState> targetPredicate;

            public WCatCasualBlockSeekGoal(WCatEntity cat, double speed, int baseRadius, double chance) {
                this.cat = cat;
                this.speed = speed;
                this.baseRadius = baseRadius;
                this.chance = chance;
                this.setFlags(EnumSet.of(Flag.MOVE));
            }

            @Override
            public boolean canUse() {
                if (cat.returnHomeFlag) return false;

                if (cooldown > 0) {
                    cooldown--;
                    return false;
                }

                if (cat.isResting() || cat.isChilling()) return false;

                if (cat.isOrderedToSit()) return false;

                if (this.targetPos != null) {
                    if (targetPredicate != null &&
                            targetPredicate.test(cat.level().getBlockState(this.targetPos))) {
                        return false;
                    }
                    this.targetPos = null;
                }


                if (cat.mode != CatMode.WANDER) return false;

                if (cat.isExpectingKits()) {
                    if (cat.getRandom().nextDouble() >= this.chance * 2) return false;
                } else {
                    if (cat.getRandom().nextDouble() >= this.chance) return false;
                }

                this.targetPredicate = defineTargetPredicate();

                this.targetPos = findTargetBlock();
                return this.targetPos != null;
            }

            @Override
            public boolean canContinueToUse() {
                return targetPos != null &&
                        !cat.getNavigation().isDone() &&
                        !cat.isOrderedToSit();
            }

            @Override
            public void start() {

                if (cat.wanderCenter != null &&
                        cat.blockPosition().distSqr(cat.wanderCenter) > cat.getWanderRadius() * cat.getWanderRadius()) {
                    cat.getNavigation().moveTo(cat.wanderCenter.getX(), cat.wanderCenter.getY(), cat.wanderCenter.getZ(), speed);
                    return;
                }

                if (targetPos != null) {
                    cat.getNavigation().moveTo(
                            targetPos.getX() + 0.5,
                            targetPos.getY(),
                            targetPos.getZ() + 0.5,
                            speed
                    );
                }
            }

            @Override
            public void stop() {
                targetPos = null;
                cat.getNavigation().stop();
                this.cooldown = 400 + cat.getRandom().nextInt(4) * 80;
            }

            private Predicate<BlockState> defineTargetPredicate() {

                return switch (cat.getRank()) {
                    case MEDICINE -> state -> state.is(ModBlocks.STONECLEFT.get());
                    default -> state -> state.getBlock() instanceof NestBlock;
                };
            }

            private BlockPos findTargetBlock() {
                Level level = cat.level();
                BlockPos origin = cat.blockPosition();

                List<BlockPos> found = new ArrayList<>();

                int radius = this.baseRadius;

                for (int x = -radius; x <= radius; x++) {
                    for (int y = -2; y <= 2; y++) {
                        for (int z = -radius; z <= radius; z++) {
                            BlockPos pos = origin.offset(x, y, z);
                            if (targetPredicate.test(level.getBlockState(pos))) {
                                found.add(pos);
                            }
                        }
                    }
                }

                if (found.isEmpty()) return null;

                return found.get(cat.getRandom().nextInt(found.size()));
            }
        }

        public static class WCatBoundedWanderGoal extends WaterAvoidingRandomStrollGoal {

            private final WCatEntity cat;
            private int cooldown = 0;
            private boolean shouldSearchForLavender = false;

            public WCatBoundedWanderGoal(WCatEntity cat, double speed) {
                super(cat, speed);
                this.cat = cat;
                this.setInterval(40);
            }


            @Override
            public boolean canUse() {
                if (this.cat.returnHomeFlag) return false;

                if (cooldown > 0) {
                    cooldown--;
                    return false;
                }

                if (cat.isResting() || cat.isChilling()) return false;

                WCatEntity wcat = cat;

                if (wcat.mode != CatMode.WANDER) return false;
                if (cat.isOrderedToSit()) return false;


                if (wcat.wanderCenter == null) return false;

                Vec3 pos = this.getRandomPointInRadius(wcat);

                if (pos == null) return false;

                this.wantedX = pos.x;
                this.wantedY = pos.y;
                this.wantedZ = pos.z;

                return true;
            }

            @Override
            public void stop() {
                super.stop();

                cooldown = cat.getRandom().nextInt(5) * 20 + 140;

                if (!(cat.onBorderPatrolFlag || cat.onHuntingPatrolFlag)) {
                    if (cat.getRandom().nextFloat() < 0.013 || cat.distanceToSqr(cat.getHomePosition().getCenter()) < 1.5 * 1.5) {
                        int counterBonus = 0;
                        if (cat.distanceToSqr(cat.getHomePosition().getCenter()) < 1.5 * 1.5) {
                            Vec3 pos = cat.getHomePosition().getCenter();
                            cat.teleportTo(pos.x, pos.y, pos.z);

                            if (cat.level().isNight()) counterBonus = (2 + cat.getRandom().nextInt(5)) * 800;
                        }
                        cat.setResting(true, ((1 + cat.getRandom().nextInt(5)) * 800) + counterBonus);
                    } else if (shouldSearchForLavender) {

                        BlockPos catPos = cat.blockPosition();
                        boolean foundLavender = false;

                        for (int dx = -1; dx <= 1; dx++) {
                            for (int dz = -1; dz <= 1; dz++) {
                                BlockPos checkPos = catPos.offset(dx, 0, dz);
                                if (cat.level().getBlockState(checkPos).getBlock() instanceof LavenderPetalsBlock) {
                                    foundLavender = true;
                                    break;
                                }
                            }
                            if (foundLavender) break;
                        }

                        if (foundLavender) {
                            cat.setChilling(true, (1 + cat.getRandom().nextInt(5)) * 800);
                            shouldSearchForLavender = false;
                        }
                    }
                }
            }

            private Vec3 getRandomPointInRadius(WCatEntity wcat) {
                int attempts = 7;

                float bedChanceBonus = switch (cat.getMood()) {
                    case SAD -> 0.03f;
                    case STRESSED -> -0.05f;
                    default -> 0;
                };

                if (cat.level().isNight()) bedChanceBonus += 0.02f;

                if (cat.isBaby() && cat.getRank() == KIT) bedChanceBonus += 0.05f;

                float personalityBedChanceBonus = switch (cat.getPersonality()) {
                    case INDEPENDENT ->  0.002f;
                    case GRUMPY -> -0.01f;
                    case RECKLESS -> -0.005f;
                    case CAUTIOUS -> -0.004f;
                    default -> 0;
                };

                if (this.cat.getRandom().nextFloat() < Math.max(0.008f, 0.013 + bedChanceBonus + personalityBedChanceBonus)) {
                    BlockPos homePos = wcat.getHomePosition();
                    if (homePos != null && !homePos.equals(BlockPos.ZERO)) {
                        Vec3 targetPos = Vec3.atCenterOf(homePos);
                        if (wcat.distanceToSqr(targetPos) < 20 * 20) {
                            return targetPos;
                        }
                    }
                }

                float hangoutChanceBonus = switch (cat.getMood()) {
                    case SAD -> -0.04f;
                    case STRESSED -> -0.02f;
                    case HAPPY -> 0.05f;
                    default -> 0;
                };
                float personalityHangoutChanceBonus = switch (cat.getPersonality()) {
                    case INDEPENDENT ->  -0.003f;
                    case SHY -> -0.004f;
                    case GRUMPY -> -0.01f;
                    case RECKLESS -> 0.01f;
                    case CAUTIOUS -> -0.004f;
                    case FRIENDLY -> 0.01f;
                    default -> 0;
                };

                shouldSearchForLavender = false;
                if (this.cat.getRandom().nextFloat() < Math.max(0.03f, 0.05 + hangoutChanceBonus + personalityHangoutChanceBonus)) {
                    shouldSearchForLavender = true;
                    {
                        int radius = cat.getWanderRadius();

                        BlockPos origin = cat.blockPosition();
                        BlockPos nearest = null;
                        double nearestDist = Double.MAX_VALUE;

                        for (int x = -radius; x <= radius; x++) {
                            for (int z = -radius; z <= radius; z++) {

                                BlockPos pos = origin.offset(x, 0, z);
                                if (!cat.level().isLoaded(pos)) continue;
                                BlockState state = cat.level().getBlockState(pos);

                                if (state.getBlock() instanceof LavenderPetalsBlock) {

                                    double dist = origin.distSqr(pos);

                                    if (dist < nearestDist) {
                                        nearestDist = dist;
                                        nearest = pos;
                                    }
                                }
                            }
                        }

                        if (nearest != null) {
                            return nearest.getCenter();
                        }
                    }
                }

                for (int i = 0; i < attempts; i++) {

                    double angle = cat.getRandom().nextDouble() * (Math.PI * 2);
                    double radious;
                    if (cat.isTame()) {
                        radious = cat.getRandom().nextDouble() * cat.getWanderRadius();
                    } else {
                        radious = cat.getRandom().nextDouble() * 32;
                    }

                    double x = wcat.wanderCenter.getX() + 0.5 + Math.cos(angle) * radious;
                    double z = wcat.wanderCenter.getZ() + 0.5 + Math.sin(angle) * radious;
                    double y = wcat.getY();

                    BlockPos groundPos = BlockPos.containing(x, y - 1, z);

                    if (!cat.level().isLoaded(groundPos)) continue;

                    if (cat.level().getBlockState(groundPos).isSolid()) {
                        return new Vec3(x, y, z);
                    }
                }

                return null;
            }

            @Override
            protected Vec3 getPosition() {
                return new Vec3(this.wantedX, this.wantedY, this.wantedZ);
            }


            @Override
            public boolean canContinueToUse() {
                if (!(cat instanceof WCatEntity)) return false;

                if (cat.mode != CatMode.WANDER) return false;
                if (cat.isOrderedToSit()) return false;

                return !cat.getNavigation().isDone();
            }
        }

        public static class WCatGiveRandomItemGoal extends Goal {
            private final WCatEntity cat;
            private final double speedModifier;
            private static final TargetingConditions PLAYER_TARGET
                    = TargetingConditions.forNonCombat().range(32f).ignoreLineOfSight();

            @Nullable
            private Player player;

            WCatGiveRandomItemGoal(WCatEntity cat) {
                this.cat = cat;
                this.speedModifier = 1f;
                this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
            }

            public boolean canUse() {
                if (cat.getMood() == Mood.SAD || cat.getMood() == Mood.STRESSED) return false;

                if (cat.onBorderPatrolFlag || cat.onHuntingPatrolFlag) return false;

                if (this.cat.getRank() != WARRIOR) return false;

                if (cat.isResting() || cat.isChilling()) return false;

                if (this.cat.mode != CatMode.WANDER) return false;

                if (cat.getTarget() != null && cat.getTarget().isAlive()) return false;

                if (this.cat.getRandom().nextFloat() > ((float) 1 / (20 * 60 * 15))) return false;

                this.player = this.cat.level().getNearestPlayer(PLAYER_TARGET, this.cat);

                if (this.player == null) return false;

                if (this.cat.getFriendshipLevel(this.player.getUUID()) < 65) return false;

                return this.cat.getTarget() != this.player;

            }

            public boolean canContinueToUse() {
                return this.player != null && this.cat.distanceToSqr(this.player) < 32 * 32;
            }

            public void start() {
                this.cat.getNavigation().moveTo(this.player, this.speedModifier);
            }

            public void stop() {
                this.player = null;
                this.cat.getNavigation().stop();
            }

            public void tick() {
                this.cat.getLookControl().setLookAt(this.player, (float) (this.cat.getMaxHeadYRot() + 20), (float) this.cat.getMaxHeadXRot());
                if (this.cat.distanceToSqr(this.player) < 6.25D) {
                    this.cat.getNavigation().stop();
                    performGiveItemInteraction();
                    String message = cat.getRandomGiftDialogue(this.cat.getPersonality());
                    this.cat.sendInteractionMessage(this.player.getUUID(), message);
                    stop();
                } else {
                    this.cat.getNavigation().moveTo(this.player, this.speedModifier);
                }

            }

            private void performGiveItemInteraction() {
                ItemStack item = new ItemStack(getPersonalityItemPool(), 2 + this.cat.getRandom().nextInt(3));
                ItemEntity itemEntity = new ItemEntity(this.player.level(), this.cat.getX(), this.cat.getY() + 0.5, this.cat.getZ(), item);
                itemEntity.getPersistentData().putBoolean("gift_by_cat", true);

                Vec3 look = this.cat.getLookAngle();
                double impulse = 0.35;
                itemEntity.setDeltaMovement(look.x * impulse, 0.2, look.z * impulse);

                itemEntity.setDefaultPickUpDelay();

                this.player.level().addFreshEntity(itemEntity);
            }

            private Item getPersonalityItemPool() {
                Item item = Items.COD;
                int randomPool = this.cat.getRandom().nextInt(4);

                if (randomPool == 0) {
                    switch (this.cat.getPersonality()) {
                        case NONE -> item = Items.COD;
                        case AMBITIOUS -> item = Items.DIAMOND;
                        case CALM -> item = Items.COD;
                        case FRIENDLY -> item = ModItems.ANIMAL_TOOTH.get();
                        case CAUTIOUS -> item = ModItems.DOCK.get();
                        case RECKLESS -> item = ModItems.ANIMAL_TEETH.get();
                        case GRUMPY -> item = ModItems.ANIMAL_TOOTH.get();
                        case HUMBLE -> item = Items.GLOW_BERRIES;
                        case SHY -> item = Items.SALMON;
                        case INDEPENDENT -> item = Items.CHICKEN;
                    }
                } else if (randomPool == 1) {
                    switch (this.cat.getPersonality()) {
                        case NONE -> item = Items.COD;
                        case AMBITIOUS -> item = Items.EMERALD;
                        case CALM -> item = Items.SALMON;
                        case FRIENDLY -> item = Items.RABBIT;
                        case CAUTIOUS -> item = ModItems.CATMINT.get();
                        case RECKLESS -> item = Items.BEEF;
                        case GRUMPY -> item = ModItems.MOUSE_FOOD.get();
                        case HUMBLE -> item = ModItems.SQUIRREL_FOOD.get();
                        case SHY -> item = Items.TROPICAL_FISH;
                        case INDEPENDENT -> item = ModItems.MOUSE_FOOD.get();
                    }
                } else if (randomPool == 2) {
                    switch (this.cat.getPersonality()) {
                        case NONE -> item = Items.COD;
                        case AMBITIOUS -> item = ModItems.PIGEON_FOOD.get();
                        case CALM -> item = ModItems.ANIMAL_TEETH.get();
                        case FRIENDLY -> item = ModItems.SQUIRREL_FOOD.get();
                        case CAUTIOUS -> item = ModItems.GLOW_SHROOM.get();
                        case RECKLESS -> item = Items.DIAMOND;
                        case GRUMPY -> item = ModItems.DOCK_LEAVES.get();
                        case HUMBLE -> item = ModItems.ANIMAL_TOOTH.get();
                        case SHY -> item = ModItems.LEAF_MANE.get();
                        case INDEPENDENT -> item = Items.DIAMOND;
                    }
                } else if (randomPool == 3) {
                    switch (this.cat.getPersonality()) {
                        case NONE -> item = Items.COD;
                        case AMBITIOUS -> item = ModItems.ANIMAL_TEETH.get();
                        case CALM -> item = ModItems.DOCK_LEAVES.get();
                        case FRIENDLY -> item = Items.IRON_INGOT;
                        case CAUTIOUS -> item = ModItems.LEAF_MANE.get();
                        case RECKLESS -> item = Items.EMERALD;
                        case GRUMPY -> item = Items.GLOW_BERRIES;
                        case HUMBLE -> item = Items.PORKCHOP;
                        case SHY -> item = ModItems.FLOWER_CROWN.get();
                        case INDEPENDENT -> item = ModItems.ANIMAL_TEETH.get();
                    }
                }

                return item;
            }

        }

        public static class WCatRunWithPlayerGoal extends Goal {
            private final WCatEntity cat;
            private final double speedModifier;
            private static final TargetingConditions PLAYER_TARGET
                    = TargetingConditions.forNonCombat().range(10.0D).ignoreLineOfSight();

            @Nullable
            private Player player;

            WCatRunWithPlayerGoal(WCatEntity cat, double pSpeedModifier) {
                this.cat = cat;
                this.speedModifier = pSpeedModifier;
                this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
            }

            public boolean canUse() {
                if (this.cat.getRank() != WARRIOR) return false;

                if (this.cat.mode != CatMode.FOLLOW) return false;

                if (cat.getTarget() != null && cat.getTarget().isAlive()) return false;

                this.player = this.cat.level().getNearestPlayer(PLAYER_TARGET, this.cat);

                if (this.player == null) return false;

                if (this.cat.getFriendshipLevel(this.player.getUUID()) < 80) return false;

                return this.player.isSprinting() && this.cat.getTarget() != this.player;

            }

            public boolean canContinueToUse() {
                return this.player != null && this.player.isSprinting() && this.cat.distanceToSqr(this.player) < 256.0D;
            }

            public void start() {
                this.player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 0, false, false, false), this.cat);
                this.cat.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 1, false, false, false), this.cat);
            }

            public void stop() {
                this.player = null;
                this.cat.getNavigation().stop();
            }

            public void tick() {
                this.cat.getLookControl().setLookAt(this.player, (float) (this.cat.getMaxHeadYRot() + 20), (float) this.cat.getMaxHeadXRot());
                if (this.cat.distanceToSqr(this.player) < 6.25D) {
                    this.cat.getNavigation().stop();
                } else {
                    this.cat.getNavigation().moveTo(this.player, this.speedModifier);
                }

                if (this.player.isSprinting() && this.player.level().random.nextInt(6) == 0) {
                    this.player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 0, false, false, false), this.cat);
                    this.cat.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 1, false, false, false), this.cat);
                }

            }
        }

        public static class WCatLeaderCallsGoal extends Goal {
            private final WCatEntity cat;
            private final double speed;
            private BlockPos ownerPosition;
            private static final int BASE_OBEY_TICKS = 140;
            private int obeyingLeaderCallForTicks = BASE_OBEY_TICKS;

            public WCatLeaderCallsGoal(WCatEntity cat) {
                this.cat = cat;
                this.speed = 1.2f;
                this.setFlags(EnumSet.of(Flag.MOVE));
            }

            @Override
            public boolean canUse() {
                if (!(cat.leaderCallingToSitFlag || cat.leaderCallingToFollowFlag)) return false;

                if (cat.getOwner() == null) return false;

                this.ownerPosition = cat.getOwner().blockPosition();

                if (cat.distanceToSqr(ownerPosition.getX(), ownerPosition.getY(), ownerPosition.getZ()) > 48 * 48)
                    return false;

                return true;
            }

            @Override
            public boolean canContinueToUse() {
                if (ownerPosition == null) return false;

                if (cat.distanceToSqr(ownerPosition.getX(), ownerPosition.getY(), ownerPosition.getZ()) > 48 * 48) {
                    return false;
                }

                return cat.distanceToSqr(ownerPosition.getX(), ownerPosition.getY(), ownerPosition.getZ()) > 5 * 5;
            }

            @Override
            public void start() {
                this.cat.returnHomeFlag = false;
                this.cat.mode = CatMode.WANDER;
                this.cat.setResting(false, 0);
                this.cat.setChilling(false, 0);
                this.cat.onBorderPatrolFlag = false;
                this.cat.onHuntingPatrolFlag = false;
//            this.cat.getNavigation().moveTo(ownerPosition.getX(), ownerPosition.getY(), ownerPosition.getZ(), speed);
            }

            @Override
            public void stop() {
                if (cat.leaderCallingToSitFlag) {
                    this.obeyingLeaderCallForTicks = BASE_OBEY_TICKS;
                    cat.leaderCallingToFollowFlag = false;
                    cat.leaderCallingToSitFlag = false;
                    cat.mode = CatMode.SIT;
                    cat.setOrderedToSit(true);
                    cat.lookAtLeaderFlag = true;
                    this.ownerPosition = null;
                }
                if (cat.leaderCallingToFollowFlag) {
                    this.obeyingLeaderCallForTicks = BASE_OBEY_TICKS;
                    cat.leaderCallingToFollowFlag = false;
                    cat.leaderCallingToSitFlag = false;
                    cat.mode = CatMode.FOLLOW;
                    cat.setOrderedToSit(false);
                    this.ownerPosition = null;
                }

            }

            @Override
            public void tick() {

                if (ownerPosition == null) return;

                double dist = cat.distanceToSqr(
                        ownerPosition.getX(),
                        ownerPosition.getY(),
                        ownerPosition.getZ()
                );

//            if (this.cat.obeyingLeaderCallForTicks > 200 && this.startCountingObeyTicks) {
//            }

                if (dist < 5.0 || this.obeyingLeaderCallForTicks <= 0) {
                    stop();
                } else {
                    this.obeyingLeaderCallForTicks--;
                    cat.getNavigation().moveTo(
                            ownerPosition.getX(),
                            ownerPosition.getY(),
                            ownerPosition.getZ(),
                            speed
                    );
                }
            }

        }

        public static class WCatReturnHomeGoal extends Goal {
            private final WCatEntity cat;
            private final double speed;
            private BlockPos homeTarget;
            private int stuckTicks = 0;
            private Vec3 lastPos = Vec3.ZERO;

            private int tickCounterUntilHorizontalImpulse;
            private boolean countUntilHorizontalImpulse;


            public WCatReturnHomeGoal(WCatEntity cat, double speed) {
                this.cat = cat;
                this.speed = speed;
                this.setFlags(EnumSet.of(Flag.MOVE));
            }

            @Override
            public boolean canUse() {
                if (!cat.returnHomeFlag) return false;

                this.homeTarget = cat.getHomePosition();

                if (this.homeTarget == null || this.homeTarget.equals(BlockPos.ZERO)) return false;

                if (cat.distanceToSqr(homeTarget.getX(), homeTarget.getY(), homeTarget.getZ()) > 160000) return false;

                return true;
            }

            @Override
            public boolean canContinueToUse() {
                if (homeTarget == null) return false;

                if (cat.distanceToSqr(homeTarget.getX(), homeTarget.getY(), homeTarget.getZ()) > 160000) {
                    return false;
                }

                return cat.mode == CatMode.WANDER && cat.distanceToSqr(homeTarget.getX(), homeTarget.getY(), homeTarget.getZ()) > 2 * 2;
            }

            @Override
            public void start() {
                this.cat.getNavigation().moveTo(homeTarget.getX(), homeTarget.getY(), homeTarget.getZ(), speed);
            }

            @Override
            public void stop() {
                cat.wanderCenter = cat.blockPosition();
                cat.mode = CatMode.WANDER;
                cat.returnHomeFlag = false;
                this.homeTarget = null;
                cat.getNavigation().stop();
                if (cat.returningFromPatrol) {
                    cat.returningFromPatrol = false;
                    Component log = Component.empty()
                            .append(cat.hasCustomName() ? cat.getCustomName().copy() : Component.literal("A cat"))
                            .append(" has returned from their patrol!");

                    ClanData data = ClanData.get(cat.getServer().overworld());
                    ClanData.Clan clan = data.getClan(cat.getClanUUID());
                    if (clan != null) {
                        data.registerLog(cat.getServer().overworld(), clan.clanUUID, log);
                    }
                }
            }

            @Override
            public void tick() {

                if (homeTarget == null) return;

                double dist = cat.distanceToSqr(
                        homeTarget.getX(),
                        homeTarget.getY(),
                        homeTarget.getZ()
                );

                if (dist < 2.0) {
                    cat.getNavigation().moveTo(
                            homeTarget.getX(),
                            homeTarget.getY(),
                            homeTarget.getZ(),
                            speed
                    );
                    cat.returnHomeFlag = false;
                    return;
                }

                if (cat.getNavigation().isDone()) {
                    cat.getNavigation().moveTo(
                            homeTarget.getX(),
                            homeTarget.getY(),
                            homeTarget.getZ(),
                            speed
                    );
                }

                Vec3 current = cat.position();

                if (current.distanceToSqr(lastPos) < 0.01) {
                    stuckTicks++;
                } else {
                    stuckTicks = 0;
                    lastPos = current;
                }

                if (this.countUntilHorizontalImpulse) {
                    this.tickCounterUntilHorizontalImpulse++;
                    if (this.tickCounterUntilHorizontalImpulse >= 7) {
                        Vec3 lookAngleImpulse = cat.getLookAngle().normalize().scale(0.8);
                        Vec3 impulse = new Vec3(lookAngleImpulse.x, 0.2, lookAngleImpulse.z);
                        cat.setDeltaMovement(cat.getDeltaMovement().add(impulse));
                        cat.hasImpulse = true;
                        this.countUntilHorizontalImpulse = false;
                        this.tickCounterUntilHorizontalImpulse = 0;
                        this.cat.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 50, 0, false, false));
                    }
                }


                if (stuckTicks >= 40) {
                    if (cat.horizontalCollision || cat.verticalCollision) {
                        Vec3 lookAngleImpulse = cat.getLookAngle().normalize().scale(2);

                        Vec3 impulse = new Vec3(lookAngleImpulse.x, 0.8, lookAngleImpulse.z);

                        cat.setDeltaMovement(cat.getDeltaMovement().add(impulse));

                        cat.hasImpulse = true;
                        this.countUntilHorizontalImpulse = true;
                    }

                    stuckTicks = 0;
                }

            }

        }

        public static class WCatLookAtPlayerGoal extends LookAtPlayerGoal {
            private final WCatEntity cat;

            public WCatLookAtPlayerGoal(WCatEntity cat, Class<? extends LivingEntity> pLookAtType, float pLookDistance) {
                super(cat, pLookAtType, pLookDistance);
                this.cat = cat;
            }

            @Override
            public boolean canUse() {
                if (this.cat.isAnImage()) return false;

                if (this.cat.isChilling() && this.cat.getRandom().nextBoolean()) return false;

                if (cat.isResting()) return false;

                return super.canUse();
            }

            @Override
            public void tick() {
                if (cat.isAnImage()) return;
                super.tick();
            }

        }

        public static class WCatMoveToMateGoal extends Goal {
            private final WCatEntity cat;
            private LivingEntity targetMate;
            private boolean isCloseToMate = false;
            private static final int BASE_DURATION = 100;
            private int interactionTickCount = BASE_DURATION;

            public WCatMoveToMateGoal(WCatEntity cat) {
                this.cat = cat;
                this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
            }

            @Override
            public boolean canUse() {
                if (cat.returnHomeFlag) return false;

                if (cat.isResting() || cat.isChilling()) return false;

                if (cat.mode != CatMode.WANDER) return false;

                if (cat.getMateUUID() == null) return false;
                if (cat.getMateUUID().equals(emptyUUID)) return false;

                if (cat.getMood() == Mood.SAD || cat.getMood() == Mood.STRESSED) return false;

                if (cat.getRandom().nextFloat() > 0.0001667f) return false;

                return findMate();
            }

            @Override
            public boolean canContinueToUse() {
                return targetMate != null && cat.mode == CatMode.WANDER;
            }

            @Override
            public void tick() {

                if (cat.distanceToSqr(targetMate) > 1f && !this.isCloseToMate) {
                    cat.getNavigation().moveTo(targetMate.getX(), targetMate.getY(), targetMate.getZ(), 1f);
                } else if (!this.isCloseToMate) {
                    this.isCloseToMate = true;
                    this.interactionTickCount = BASE_DURATION;
                    targetMate.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0, false, false));
                }

                if (this.isCloseToMate && this.interactionTickCount > 0) {
                    this.interactionTickCount--;
                    ServerLevel sLevel = ((ServerLevel) cat.level());
                    cat.getLookControl().setLookAt(targetMate, (float) (cat.getMaxHeadYRot() + 20), (float) cat.getMaxHeadXRot());

                    float chance = cat.getRandom().nextFloat();
                    if (chance <= 0.05f) {
                        sLevel.sendParticles(ParticleTypes.HEART, cat.getX(), cat.getY(), cat.getZ(), 2, 0.5f, 0.5f, 0.5f, 0.1f);
                        if (chance < 0.03) {
                            sLevel.playSound(null, cat.blockPosition(), SoundEvents.CAT_PURR, SoundSource.NEUTRAL, 0.4F, 1.0F);
                        }
                    }

                    if (cat.distanceToSqr(targetMate) > 1f) {
                        cat.getNavigation().moveTo(targetMate.getX(), targetMate.getY(), targetMate.getZ(), 1f);
                    }
                }
                if (this.interactionTickCount <= 0) {
                    stop();
                }
            }

            @Override
            public void stop() {
                if (targetMate != null && this.interactionTickCount <= 0) {
                    if (!cat.level().isClientSide()) {
                        ServerLevel sLevel = ((ServerLevel) cat.level());

                        sLevel.sendParticles(ParticleTypes.HEART, cat.getX(), cat.getY(), cat.getZ(), 2, 0.5f, 0.5f, 0.5f, 0.1f);
                        cat.playSound(SoundEvents.CAT_PURR);
                        this.targetMate = null;
                        this.interactionTickCount = BASE_DURATION;
                        this.isCloseToMate = false;
                    }
                }
                super.stop();
            }

            private boolean findMate() {
                AABB box = cat.getBoundingBox().inflate(28);

                List<LivingEntity> cats = cat.level().getEntitiesOfClass(
                        LivingEntity.class,
                        box
                );

                for (LivingEntity potentialMate : cats) {
                    if (potentialMate == cat) continue;
                    if (!(potentialMate instanceof WCatEntity || potentialMate instanceof Player)) continue;

                    UUID potentialMateUUID = potentialMate.getUUID();
                    if (!potentialMateUUID.equals(cat.getMateUUID())) continue;

                    this.targetMate = potentialMate;
                    return true;

                }

                return false;
            }

        }

        public static class WCatRandomLookAroundGoal extends RandomLookAroundGoal {
            private final WCatEntity cat;

            public WCatRandomLookAroundGoal(WCatEntity pMob) {
                super(pMob);
                this.cat = pMob;
            }

            @Override
            public boolean canUse() {
                if (this.cat.lookAtLeaderFlag && this.cat.isLookingAtLeader) return false;
                if (this.cat.isAnImage()) return false;

                if (cat.isResting()) return false;

                return super.canUse();
            }

            @Override
            public void tick() {
                if (cat.isAnImage()) return;
                super.tick();
            }

        }

        public static class WCatSeekShelterGoal extends Goal {
            private final WCatEntity cat;
            private double wantedX;
            private double wantedY;
            private double wantedZ;
            private final double speedModifier;
            private final Level level;

            public WCatSeekShelterGoal(WCatEntity cat, double pSpeedModifier) {
                this.cat = cat;
                this.speedModifier = pSpeedModifier;
                this.level = cat.level();
                this.setFlags(EnumSet.of(Flag.MOVE));
            }

            @Override
            public boolean canUse() {
                if (cat.returnHomeFlag) return false;

                if (cat.getTarget() != null && cat.getTarget().isAlive()) return false;

                if (cat.mode != CatMode.WANDER || cat.getTarget() != null) return false;
                BlockPos pos = cat.blockPosition();

                boolean isExposed = this.level.canSeeSky(pos);
                boolean isRaining = this.level.isThundering() || this.level.isRainingAt(pos);

                if (isRaining && isExposed) return setWantedPos();

                return false;
            }

            public boolean canContinueToUse() {
                return !this.cat.getNavigation().isDone();
            }

            public void start() {
                this.cat.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
                this.cat.setResting(false, 0);
                this.cat.setChilling(false, 0);
            }

            protected boolean setWantedPos() {
                BlockPos pos = this.findShelter();
                if (pos == null) {
                    return false;
                } else {
                    this.wantedX = pos.getX();
                    this.wantedY = pos.getY();
                    this.wantedZ = pos.getZ();
                    return true;
                }
            }

            @Nullable
            private BlockPos findShelter() {
                BlockPos origin = cat.blockPosition();

                for (int i = 0; i < 12; i++) {
                    BlockPos pos = origin.offset(
                            cat.getRandom().nextInt(14) - 7,
                            cat.getRandom().nextInt(6) - 2,
                            cat.getRandom().nextInt(14) - 7
                    );

                    if (!cat.level().getBlockState(pos.below()).entityCanStandOn(cat.level(), pos.below(), cat))
                        continue;

                    if (!cat.level().isEmptyBlock(pos) || !cat.level().isEmptyBlock(pos.above()))
                        continue;

                    if (cat.level().canSeeSky(pos))
                        continue;

                    return pos;
                }

                return null;
            }

        }

        public static class WCatMedicineHealsCats extends Goal {

            private final WCatEntity cat;
            private LivingEntity target;
            private int cooldown = 400;
            private int keepTicks = 0;
            private final int BASE_COOLDOWN = 600;
            private int healCooldown = 0;

            public WCatMedicineHealsCats(WCatEntity cat) {
                this.cat = cat;
                this.setFlags(EnumSet.of(Flag.MOVE));
            }

            @Override
            public boolean canUse() {
                if (cat.returnHomeFlag) return false;

                if (cat.getRank() != MEDICINE) return false;

                if (cat.isResting()) return false;

                if (target != null && (!target.isAlive() || cat.distanceTo(target) > 25D)) {
                    target = null;
                }

                if (cat.isOrderedToSit()) return false;

                if (target != null) return false;

                if (cooldown > 0) {
                    cooldown--;
                    return false;
                }

                target = findNearestInjuredClanmate();

                if (target != null) {
                    cooldown = ((BASE_COOLDOWN + cat.getRandom().nextInt(5) * 20));
                    if (!cat.hasPoultice()) {
                        if (!cat.tryMakePoultice()) {
                            cooldown = ((BASE_COOLDOWN + cat.getRandom().nextInt(5) * 20));
                            ;
                            return false;
                        }
                    }
                    return true;
                }

                cooldown = 600;
                return false;
            }


            @Override
            public boolean canContinueToUse() {
                if (target != null && cat.distanceTo(target) > 20D) return false;
                return target != null
                        && target.isAlive()
                        && !cat.isOrderedToSit()
                        && cat.getRank() == MEDICINE
                        && cat.hasPoultice();
            }

            @Override
            public void start() {
                if (target != null) {
                    if (cat.isChilling()) {
                        cat.setChilling(false, 0);
                    }
                    cat.getNavigation().moveTo(target, 1.1D);
                }
            }

            @Override
            public void stop() {
                target = null;
                keepTicks = 0;
                cat.getNavigation().stop();
            }

            @Override
            public void tick() {
                if (healCooldown > 0) healCooldown--;

                if (target == null || !target.isAlive()) {
                    stop();
                    return;
                }

                /**
                 * If it is not moving, then move to the target.
                 */
                if (!cat.getNavigation().isInProgress()) {
                    cat.getNavigation().moveTo(target, 1.1D);
                }

                /**
                 * If it still is not moving, start counting.
                 */
                if (cat.getNavigation().isInProgress()) {
                    keepTicks = 0;
                } else {
                    keepTicks++;
                }

                /**
                 * In case it gets stuck without being able to pick up the item or move, then stop.
                 */
                if (keepTicks > 60) {
                    stop();
                    return;
                }

                /**
                 * Withing certain distance of the target, try to insert it into the inventory.
                 * Then remove 1 from the stack on the ground.
                 */
                WCatEntity medicine = this.cat;
                LivingEntity injured = this.target;
                if (medicine.distanceTo(injured) < 1.52D && healCooldown <= 0) {
                    if (medicine.tryHealClanmante(injured)) {


                        healCooldown = 20;

                        if (target.getHealth() >= target.getMaxHealth() - 4) {
                            stop();
                            target = null;
                        }

                        injured.level().playSound(
                                null, injured.getX(), injured.getY(), injured.getZ(),
                                SoundEvents.SLIME_JUMP, SoundSource.NEUTRAL,
                                0.5F, 1.5F + cat.getRandom().nextFloat() * 0.2F
                        );
                        injured.level().playSound(
                                null, injured.getX(), injured.getY(), injured.getZ(),
                                SoundEvents.CAT_EAT, SoundSource.NEUTRAL,
                                0.6F, 0.9F + cat.getRandom().nextFloat() * 0.2F
                        );
                        ((ServerLevel) injured.level()).sendParticles(
                                ParticleTypes.HAPPY_VILLAGER,
                                injured.getX(), injured.getY() + injured.getBbHeight() * 0.6,
                                injured.getZ(), 30, 0.3, 0.3, 0.3, 1
                        );

                    }
                } else {

                    if (medicine.distanceTo(injured) > 30D) {
                        this.stop();
                    }
                    /**
                     * If the distance to the item is not enough and this is not moving, then move around the item.
                     */
                    if (target != null && !cat.getNavigation().isInProgress()) {
                        List<BlockPos> positions = List.of(
                                target.blockPosition().offset(2, 1, 2),
                                target.blockPosition().offset(2, 1, -2),
                                target.blockPosition().offset(-2, 1, 2),
                                target.blockPosition().offset(-2, 1, -2)
                        );

                        for (BlockPos pos : positions) {
                            if (cat.getNavigation().moveTo(pos.getX(), pos.getY(), pos.getZ(), 1.1D)) {
                                break;
                            }
                        }
                    }

                }
            }

            private LivingEntity findNearestInjuredClanmate() {
                AABB box = cat.getBoundingBox().inflate(28);

                List<LivingEntity> cats = cat.level().getEntitiesOfClass(
                        LivingEntity.class,
                        box
                );

                double closestDist = Double.MAX_VALUE;
                LivingEntity closest = null;

                for (LivingEntity catToHeal : cats) {
                    if (catToHeal.getHealth() >= catToHeal.getMaxHealth() - 4) continue;
                    if (catToHeal == cat) continue;
                    if (!(catToHeal instanceof WCatEntity || catToHeal instanceof Player)) continue;
                    if (catToHeal instanceof WCatEntity kitty) {
                        if (kitty.getOwner() != cat.getOwner()) continue;
                    }
                    if (catToHeal instanceof Player playerKitty) {
                        if (cat.getFriendshipLevel(playerKitty.getUUID()) < 10) continue;
                    }

                    double dist = cat.distanceToSqr(catToHeal);
                    if (dist < closestDist) {
                        closestDist = dist;
                        closest = catToHeal;
                    }
                }

                return closest;
            }

        }

        public static class WCatPickupItemGoal extends Goal {

            private final WCatEntity cat;
            private ItemEntity target;
            private int cooldown = 0;
            private int keepTicks = 0;
            private static final int BASE_COOLDOWN = 45;
            private float growingMinimumDistance = 0;

            public WCatPickupItemGoal(WCatEntity cat) {
                this.cat = cat;
                this.setFlags(EnumSet.of(Flag.MOVE));
            }

            @Override
            public boolean canUse() {
                if (cat.returnHomeFlag) return false;
                if (cat.onBorderPatrolFlag) return false;

                if (cat.isResting()) return false;

                if (cat.getTarget() != null && cat.getTarget().isAlive()) return false;

                if (target != null && (!target.isAlive() || cat.distanceTo(target) > 20D)) {
                    target = null;
                }

                if (cat.isOrderedToSit()) return false;

                if (target != null) return false;

                if (cooldown > 0) {
                    cooldown--;
                    return false;
                }

                target = findNearestItem();

                if (target != null) {
                    return true;
                }

                cooldown = 10;
                return false;
            }


            @Override
            public boolean canContinueToUse() {
                if (target != null && cat.distanceTo(target) > 20D) return false;

                if (!isValidTarget(target)) {
                    stop();
                    return false;
                }

                if (cat.getTarget() != null && cat.getTarget().isAlive()) return false;

                return target != null
                        && target.isAlive()
                        && !cat.isOrderedToSit();
            }

            @Override
            public void start() {
                this.growingMinimumDistance = 0;
                if (target != null) {
                    if (cat.isChilling()) {
                        cat.setChilling(false, 0);
                    }
                    cat.getNavigation().moveTo(target, 1.1D);
                }
            }

            @Override
            public void stop() {
                target = null;
                keepTicks = 0;
                cat.getNavigation().stop();
            }

            @Override
            public void tick() {
                if (target == null || !target.isAlive()) {
                    stop();
                    return;
                }

                if (!isValidTarget(target)) {
                    stop();
                    return;
                }

                /**
                 * If it is not moving, then move to the target.
                 */
                if (cat.getNavigation().isDone()) {
                    cat.getNavigation().moveTo(target, 1.1D);
                }

                /**
                 * If it still is not moving, start counting.
                 */
                if (!cat.getNavigation().isDone()) {
                    keepTicks = 0;
                } else {
                    keepTicks++;
                }

                /**
                 * In case it gets stuck without being able to pick up the item or move, then stop.
                 */
                if (keepTicks > 60) {
                    stop();
                    return;
                }

                /**
                 * Withing certain distance of the target, try to insert it into the inventory.
                 * Then remove 1 from the stack on the ground.
                 */
                ItemStack groundItems = target.getItem();
                if (cat.distanceTo(target) < 1.42D + this.growingMinimumDistance) {
                    if (cat.tryInsert(groundItems)) {
                        groundItems.shrink(1);

                        if (groundItems.isEmpty()) {
                            target.discard();
                        }


                        cat.level().playSound(
                                null, cat.getX(), cat.getY(), cat.getZ(),
                                SoundEvents.ITEM_PICKUP, SoundSource.NEUTRAL,
                                0.5F, 1.4F + cat.getRandom().nextFloat() * 0.2F
                        );
                        cat.level().playSound(
                                null, cat.getX(), cat.getY(), cat.getZ(),
                                SoundEvents.CAT_EAT, SoundSource.NEUTRAL,
                                0.6F, 0.9F + cat.getRandom().nextFloat() * 0.2F
                        );

                    }
                    stop();
                } else {

                    if (cat.distanceTo(target) > 20D) {
                        this.stop();
                    }
                    /**
                     * If the distance to the item is not enough and this is not moving, then move around the item.
                     */
                    if (target != null && cat.getNavigation().isDone()) {
                        List<BlockPos> positions = List.of(
                                target.blockPosition().offset(2, 1, 2),
                                target.blockPosition().offset(2, 1, -2),
                                target.blockPosition().offset(-2, 1, 2),
                                target.blockPosition().offset(-2, 1, -2)
                        );

                        this.growingMinimumDistance += 0.05F;

                        for (BlockPos pos : positions) {
                            if (cat.getNavigation().moveTo(pos.getX(), pos.getY(), pos.getZ(), 1.1D)) {
                                break;
                            }
                        }
                    }
                }
            }

            private boolean isValidTarget(ItemEntity item) {
                return item != null
                        && item.isAlive()
                        && !item.getItem().isEmpty()
                        && cat.canAccept(item.getItem());
            }

            /**
             * In certain are, make a list of droped items.
             * Then for every item in the list, verify if the cat can accept it.
             * If the distance to the item is less than the one from the last item, then set that item as the closest.
             */
            private ItemEntity findNearestItem() {
                AABB box = cat.getBoundingBox().inflate(12);

                List<ItemEntity> items = cat.level().getEntitiesOfClass(
                        ItemEntity.class,
                        box
                );

                double closestDist = Double.MAX_VALUE;
                ItemEntity closest = null;

                for (ItemEntity item : items) {
                    ItemStack stack = item.getItem();

                    if (!cat.canAccept(stack)) continue;
                    if (item.getPersistentData().getBoolean("gift_by_cat")) continue;


                    double dist = cat.distanceToSqr(item);
                    if (dist < closestDist) {
                        closestDist = dist;
                        closest = item;
                    }
                }
                cooldown = (int) ((BASE_COOLDOWN + cat.getRandom().nextInt(10)) * cat.itemPickupChanceMultiplier());

                return closest;
            }

        }

        public static class WCatDepositFreshkill extends Goal {
            private final WCatEntity cat;
            private final double speed;
            private BlockPos freshKillPos;
            private int checkCooldown = 0;
            private float distanceMultiplier = 1f;

            public WCatDepositFreshkill(WCatEntity cat) {
                this.cat = cat;
                this.speed = 1f;
                this.setFlags(EnumSet.of(Flag.MOVE));
            }

            @Override
            public boolean canUse() {
                if (!cat.isTame()) return false;

                if (cat.returnHomeFlag) return false;

                if (checkCooldown > 0) {
                    checkCooldown--;
                    return false;
                }

                if (cat.isResting())  return false;

                if (cat.mode != CatMode.WANDER) return false;

                if (cat.getCatInventory().isEmpty()) return false;

                for (int i = 0; i < cat.getCatInventory().getContainerSize(); i++) {
                    if (cat.getCatInventory().getItem(i).is(ModTags.Items.PREY)) {
                        return true;
                    }
                }

                checkCooldown = 100 + (6 + cat.getRandom().nextInt(4))*20;

                return false;
            }

            @Override
            public boolean canContinueToUse() {

                return cat.mode == CatMode.WANDER
                        && !cat.getCatInventory().isEmpty();
            }

            @Override
            public void start() {
                if (cat.isChilling()) {
                    cat.setChilling(false, 0);
                }
                distanceMultiplier = 1f;
            }

            @Override
            public void stop() {
                this.freshKillPos = null;
                distanceMultiplier = 1f;
                cat.getNavigation().stop();
            }

            @Override
            public void tick() {

                if (freshKillPos == null) {

                    checkCooldown = 100 + (6 + cat.getRandom().nextInt(4))*20;

                    freshKillPos = findTargetBlock();
                    if (freshKillPos == null) return;
                    distanceMultiplier = 1f;

                    cat.getNavigation().moveTo(freshKillPos.getX(), freshKillPos.getY(), freshKillPos.getZ(), speed);
                }

                if (cat.tickCount % 3 == 0) {
                    if (this.freshKillPos != null) {
                        if (cat.distanceToSqr(freshKillPos.getCenter()) < 4*distanceMultiplier) {
                            BlockEntity blockEntity = cat.level().getBlockEntity(freshKillPos);
                            if (blockEntity instanceof FreshkillPileBlockEntity freshkillPileBlockEntity) {

                                for (int i = 0; i < cat.getCatInventory().getContainerSize(); i++) {
                                    ItemStack catStack = cat.getCatInventory().getItem(i);
                                    if (catStack.is(ModTags.Items.PREY)) {

                                        boolean couldInsert = freshkillPileBlockEntity.putItem(catStack.copyWithCount(1));

                                        if (couldInsert) {
                                            cat.setItemSynced(i, cat.getCatInventory().getItem(i).copyWithCount(cat.getCatInventory().getItem(i).getCount() - 1));

                                            cat.level().playSound(
                                                    null, cat.getX(), cat.getY(), cat.getZ(),
                                                    SoundEvents.ITEM_PICKUP, SoundSource.NEUTRAL,
                                                    0.5F, 0.9F + cat.getRandom().nextFloat() * 0.2F
                                            );
                                        } else {
                                            freshKillPos = null;
                                        }

                                    }
                                }

                            } else {
                                freshKillPos = null;
                            }
                        } else {
                            if (cat.getNavigation().isDone()) {
                                cat.getNavigation().moveTo(freshKillPos.getX(), freshKillPos.getY(), freshKillPos.getZ(), speed);
                                distanceMultiplier = Math.min(distanceMultiplier + 0.05f, 4f);                        }
                        }
                    }
                }
            }

            private BlockPos findTargetBlock() {
                Level level = cat.level();
                BlockPos origin = cat.blockPosition();

                int radius = 18;

                for (int x = -radius; x <= radius; x++) {
                    for (int y = -2; y <= 2; y++) {
                        for (int z = -radius; z <= radius; z++) {
                            BlockPos pos = origin.offset(x, y, z);
                            if (!level.isLoaded(pos)) continue;
                            if (level.getBlockState(pos).getBlock() == ModBlocks.FRESHKILL_PILE.get()) {
                                BlockEntity blockEntity = level.getBlockEntity(pos);
                                if (blockEntity instanceof FreshkillPileBlockEntity freshkillPileBlockEntity) {
                                    for (int i = 0; i < freshkillPileBlockEntity.getItemHandler().getSlots(); i++) {
                                        if (freshkillPileBlockEntity.getItemHandler().getStackInSlot(i).isEmpty()) {
                                            return pos;
                                        } else {
                                            for (int j = 0; j < cat.getCatInventory().getContainerSize(); j++) {
                                                if (ItemStack.isSameItemSameTags(freshkillPileBlockEntity.getItemHandler().getStackInSlot(i),
                                                        cat.getCatInventory().getItem(j)) &&
                                                        freshkillPileBlockEntity.getItemHandler().getStackInSlot(i).getCount()
                                                                < freshkillPileBlockEntity.getItemHandler().getStackInSlot(i).getItem().getMaxStackSize()) {
                                                    return pos;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                return null;
            }

        }

        public static class WCatDeputySendsPatrols extends Goal {
            private final WCatEntity cat;
            private List<WCatEntity> catsInRange = new ArrayList<>();
            private int patrolType;
            private Map<Integer, BlockPos> territoryToPatrol = new HashMap<>();
            private int currentIndex = 0;
            private int stuckTicks = 0;

            public WCatDeputySendsPatrols(WCatEntity cat) {
                this.cat = cat;
            }

            @Override
            public boolean canUse() {
                if (cat.getRank() != DEPUTY) return false;

                if (cat.isResting() || cat.returnHomeFlag) return false;

                if (cat.getClanUUID().equals(ClanData.EMPTY_UUID)) return false;

                if (cat.catsToTell.isEmpty()) return false;

                if (!cat.tellingCatsToPatrol) return false;

                return true;
            }

            @Override
            public void stop() {
                this.catsInRange = new ArrayList<>();
                cat.tellingCatsToPatrol = false;
                cat.catsToTell = new ArrayList<>();
                this.currentIndex = 0;
                super.stop();
            }

            @Override
            public void start() {
                this.catsInRange = cat.catsToTell;
                this.patrolType = cat.patrolType;

                this.territoryToPatrol = cat.areasToPatrol;
            }

            @Override
            public void tick() {
                if (territoryToPatrol == null || territoryToPatrol.isEmpty()) {
                    stop();
                    return;
                }
                if (catsInRange == null || catsInRange.isEmpty()) {
                    stop();
                    return;
                }

                if (currentIndex >= catsInRange.size()) {
                    stop();
                    return;
                }

                WCatEntity target = catsInRange.get(currentIndex);

                if (target == null) {
                    currentIndex++;
                    return;
                }

                if (catsInRange.contains(cat)) {
                    for (WCatEntity cat : catsInRange) {
                        if (cat != this.cat) {
                            if (this.patrolType == 0) {
                                cat.setOnBorderPatrol(this.territoryToPatrol);
                            } else {
                                cat.setOnHuntingPatrol(this.territoryToPatrol);
                            }
                        }
                    }
                    if (this.patrolType == 0) {
                        cat.setOnBorderPatrol(this.territoryToPatrol);
                    } else {
                        cat.setOnHuntingPatrol(this.territoryToPatrol);
                    }

                    cat.level().playSound(null, cat.blockPosition(), ModSounds.LEADER_CALL.get(), SoundSource.AMBIENT, 0.7F, 0.9f);

                    stop();
                    return;
                }

                if (cat.distanceTo(target) > 1.5) {
                    cat.getNavigation().moveTo(target, 1.0);
                    stuckTicks++;
                    if (stuckTicks >= 300) {
                        currentIndex++;
                        stuckTicks = 0;
                    }
                } else {
                    if (this.patrolType == 0) {
                        target.setOnBorderPatrol(this.territoryToPatrol);
                    } else {
                        target.setOnHuntingPatrol(this.territoryToPatrol);
                    }
                    currentIndex++;
                    stuckTicks = 0;
                }
            }
        }

        public static class WCatBorderPatrolGoal extends Goal {
            private final WCatEntity cat;
            private final double speed = 1f;
            private BlockPos goalPos = null;
            private int territoryNotReachable = 0;

            private int tickCounterUntilHorizontalImpulse = 0;
            private boolean countUntilHorizontalImpulse;

            public WCatBorderPatrolGoal(WCatEntity cat) {
                this.cat = cat;
                this.setFlags(EnumSet.of(Flag.MOVE));
            }

            @Override
            public boolean canUse() {
                if (cat.returnHomeFlag) return false;

                if (!cat.onBorderPatrolFlag) return false;

                if (cat.mode != CatMode.WANDER) return false;


                if (cat.onAreaToPatrolForTicks > 0) {
                    return false;
                }

                if (cat.getAreaToPatrolIndex(0) == null) return false;

                return true;
            }

            @Override
            public boolean canContinueToUse() {
                if (cat.returnHomeFlag) return false;

                if (cat.mode != CatMode.WANDER) return false;

                if (!cat.onBorderPatrolFlag) return false;

                if (cat.onAreaToPatrolForTicks > 0) {
                    return false;
                }

                if (cat.getAreaToPatrolIndex(0) == null) return false;

                return true;
            }

            @Override
            public void start() {

                this.goalPos = cat.getAreaToPatrolIndex(cat.patrolIndex);

                super.start();
            }

            @Override
            public void stop() {
                super.stop();
            }

            @Override
            public void tick() {

                if (this.goalPos == null) {
                    cat.finishPatrol();
                    return;
                }

                double distanceToGoal = cat.distanceToSqr(this.goalPos.getCenter());
                if (distanceToGoal > 2*2) {
                    cat.getNavigation().moveTo(this.goalPos.getX(), this.goalPos.getY() ,this.goalPos.getZ(), speed);
                    this.territoryNotReachable++;
                    if (this.territoryNotReachable >= 3000) {
                        cat.finishPatrol();
                    }

                    if (this.countUntilHorizontalImpulse) {
                        this.tickCounterUntilHorizontalImpulse++;
                        if (this.tickCounterUntilHorizontalImpulse >= 7) {
                            Vec3 lookAngleImpulse = cat.getLookAngle().normalize().scale(0.8);
                            Vec3 impulse = new Vec3(lookAngleImpulse.x, 0.2, lookAngleImpulse.z);
                            cat.setDeltaMovement(cat.getDeltaMovement().add(impulse));
                            cat.hasImpulse = true;
                            this.countUntilHorizontalImpulse = false;
                            this.tickCounterUntilHorizontalImpulse = 0;
                            this.cat.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 50, 0, false, false));
                        }
                    }

                    if (territoryNotReachable > 1500 && territoryNotReachable < 1502) {
                        if (cat.horizontalCollision || cat.verticalCollision) {
                            Vec3 lookAngleImpulse = cat.getLookAngle().normalize().scale(2);

                            Vec3 impulse = new Vec3(lookAngleImpulse.x, 0.8, lookAngleImpulse.z);

                            cat.setDeltaMovement(cat.getDeltaMovement().add(impulse));

                            cat.hasImpulse = true;
                            this.countUntilHorizontalImpulse = true;
                        }
                    }

                } else {
                    cat.onAreaToPatrolForTicks = 1200;
                    cat.patrolIndex++;
                    cat.wanderCenter = cat.blockPosition();

                    ChunkPos pos = cat.chunkPosition();
                    UUID clanUUID = cat.getClanUUID();

                    if (cat.level() instanceof ServerLevel sLevel) {
                        boolean done = false;

                        LevelChunk chunk = sLevel.getChunkAt(this.goalPos);
                        for (BlockEntity ent : chunk.getBlockEntities().values()) {
                            if (ent instanceof TreeStumpBlockEntity treeStumpBlock) {
                                if (treeStumpBlock.getTimeUntilRenewScent() <= 0) {
                                    if (clanUUID != ClanData.EMPTY_UUID) {
                                        ClanData data = ClanData.get(cat.getServer().overworld());
                                        ClanData.Clan clan = data.getClan(clanUUID);
                                        if (clan != null) {
                                            if (clan.claimedTerritory.containsKey(pos)) {
                                                data.reclaimChunk(clanUUID, pos, cat.getServer().overworld());
                                                done = true;
                                                Component name = cat.hasCustomName() ? cat.getCustomName() : Component.literal("A cat");
                                                Component log = Component.empty()
                                                        .append(name.copy())
                                                        .append(" has remarked territory for ")
                                                        .append(Component.literal(clan.name).withStyle(Style.EMPTY.withColor(clan.color)))
                                                        .append(" at: ")
                                                        .append(Component.literal(
                                                                String.format("X=%d, Z=%d", pos.x, pos.z)
                                                        ).withStyle(ChatFormatting.AQUA));
                                                data.registerLog(cat.getServer().overworld(), clanUUID, log);

                                                treeStumpBlock.resetTimer();
                                                sLevel.sendBlockUpdated(treeStumpBlock.getBlockPos(),
                                                        treeStumpBlock.getBlockState(), treeStumpBlock.getBlockState(), 2);

                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (!done) {
                            for (ChunkPos pos1 : ClanData.SQUARE_ADJACENT_CHUNKS) {

                                if (done) break;

                                ChunkPos newChunkPos = new ChunkPos(pos.x + pos1.x, pos.z + pos1.z);
                                LevelChunk currentChecking = sLevel.getChunk(newChunkPos.x, newChunkPos.z);

                                for (BlockEntity ent : currentChecking.getBlockEntities().values()) {
                                    if (ent instanceof TreeStumpBlockEntity treeStumpBlock) {
                                        if (treeStumpBlock.getTimeUntilRenewScent() <= 0) {
                                            if (clanUUID != ClanData.EMPTY_UUID) {
                                                ClanData data = ClanData.get(cat.getServer().overworld());
                                                ClanData.Clan clan = data.getClan(clanUUID);
                                                if (clan != null) {
                                                    if (clan.claimedTerritory.containsKey(pos)) {
                                                        data.reclaimChunk(clanUUID, pos, cat.getServer().overworld());
                                                        done = true;
                                                        Component name = cat.hasCustomName() ? cat.getCustomName() : Component.literal("A cat");
                                                        Component log = Component.empty()
                                                                .append(name.copy())
                                                                .append(" has remarked territory for ")
                                                                .append(Component.literal(clan.name).withStyle(Style.EMPTY.withColor(clan.color)))
                                                                .append(" at: ")
                                                                .append(Component.literal(
                                                                        String.format("X=%d, Z=%d", pos.x, pos.z)
                                                                ).withStyle(ChatFormatting.AQUA));
                                                        data.registerLog(cat.getServer().overworld(), clanUUID, log);

                                                        treeStumpBlock.resetTimer();
                                                        sLevel.sendBlockUpdated(treeStumpBlock.getBlockPos(),
                                                                treeStumpBlock.getBlockState(), treeStumpBlock.getBlockState(), 2);

                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    BlockPos next = cat.getAreaToPatrolIndex(cat.patrolIndex);
                    if (next != null) {
                        this.goalPos = next;
                    }

                }

            }
        }

        public static class WCatHuntingPatrolGoal extends Goal {
            private final WCatEntity cat;
            private final double speed = 1f;
            private BlockPos goalPos = null;
            private int territoryNotReachable = 0;

            private int tickCounterUntilHorizontalImpulse = 0;
            private boolean countUntilHorizontalImpulse;

            public WCatHuntingPatrolGoal(WCatEntity cat) {
                this.cat = cat;
                this.setFlags(EnumSet.of(Flag.MOVE));
            }

            @Override
            public boolean canUse() {
                if (cat.returnHomeFlag) return false;

                if (!cat.onHuntingPatrolFlag) return false;

                if (cat.mode != CatMode.WANDER) return false;


                if (cat.onAreaToPatrolForTicks > 0) {
                    return false;
                }

                if (cat.getAreaToPatrolIndex(0) == null) return false;

                return true;
            }

            @Override
            public boolean canContinueToUse() {
                if (cat.returnHomeFlag) return false;

                if (cat.mode != CatMode.WANDER) return false;

                if (!cat.onHuntingPatrolFlag) return false;

                if (cat.onAreaToPatrolForTicks > 0) {
                    return false;
                }

                if (cat.getAreaToPatrolIndex(0) == null) return false;

                return true;
            }

            @Override
            public void start() {

                this.goalPos = cat.getAreaToPatrolIndex(cat.patrolIndex);

                super.start();
            }

            @Override
            public void stop() {
                super.stop();
            }

            @Override
            public void tick() {

                if (this.goalPos == null) {
                    cat.finishPatrol();
                    return;
                }

                double distanceToGoal = cat.distanceToSqr(this.goalPos.getCenter());
                if (distanceToGoal > 4*4) {
                    cat.getNavigation().moveTo(this.goalPos.getX(), this.goalPos.getY() ,this.goalPos.getZ(), speed);
                    this.territoryNotReachable++;
                    if (this.territoryNotReachable >= 3000) {
                        cat.finishPatrol();
                    }

                    if (this.countUntilHorizontalImpulse) {
                        this.tickCounterUntilHorizontalImpulse++;
                        if (this.tickCounterUntilHorizontalImpulse >= 7) {
                            Vec3 lookAngleImpulse = cat.getLookAngle().normalize().scale(0.8);
                            Vec3 impulse = new Vec3(lookAngleImpulse.x, 0.2, lookAngleImpulse.z);
                            cat.setDeltaMovement(cat.getDeltaMovement().add(impulse));
                            cat.hasImpulse = true;
                            this.countUntilHorizontalImpulse = false;
                            this.tickCounterUntilHorizontalImpulse = 0;
                            this.cat.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 50, 0, false, false));
                        }
                    }


                    if (territoryNotReachable > 1500 && territoryNotReachable < 1502) {
                        if (cat.horizontalCollision || cat.verticalCollision) {
                            Vec3 lookAngleImpulse = cat.getLookAngle().normalize().scale(2);

                            Vec3 impulse = new Vec3(lookAngleImpulse.x, 0.8, lookAngleImpulse.z);

                            cat.setDeltaMovement(cat.getDeltaMovement().add(impulse));

                            cat.hasImpulse = true;
                            this.countUntilHorizontalImpulse = true;
                        }
                    }


                } else {
                    cat.onAreaToPatrolForTicks = 2400;
                    cat.patrolIndex++;
                    cat.wanderCenter = cat.blockPosition();

                    BlockPos next = cat.getAreaToPatrolIndex(cat.patrolIndex);
                    if (next != null) {
                        this.goalPos = next;
                    }

                }

            }
        }

        public static class WCatAttackMossBall extends Goal {

            private final WCatEntity cat;
            private final double range;
            private float increment = 0;
            private int cooldown = 0;


            private MossBallEntity mossBall;

            public WCatAttackMossBall(WCatEntity cat) {
                this.cat = cat;
                this.range = 15;
                this.setFlags(EnumSet.of(Flag.MOVE));
            }

            @Override
            public boolean canUse() {
                if (cooldown > 0) {
                    cooldown--;
                    return false;
                }

                if (cat.getRank() != KIT) {
                    return false;
                }

                if (cat.mode != CatMode.WANDER) {
                    return false;
                }

                if (cat.isResting() || cat.isChilling()) {
                    return false;
                }

                if (!(cat.getMood() == Mood.HAPPY || cat.getMood() == Mood.CALM)) {
                    return false;
                }

                if (cat.isPassenger()) return false;

                cooldown = 20;
                this.mossBall = findValidMossBall();

                return this.mossBall != null;
            }

            @Override
            public boolean canContinueToUse() {
                if (cat.getRank() != KIT) return false;

                if (cat.mode != CatMode.WANDER) return false;

                if (cat.isResting() || cat.isChilling()) return false;

                if (!(cat.getMood() == Mood.HAPPY || cat.getMood() == Mood.CALM)) {
                    return false;
                }

                if (cat.isPassenger()) return false;

                return this.mossBall != null && this.mossBall.isAlive() && cat.distanceTo(this.mossBall) < 20;
            }

            @Override
            public void tick() {
                if (mossBall == null) return;

                if (cat.distanceTo(mossBall) < 0.55 + increment) {

                    mossBall.kickBall(cat);

                    increment = 0;
                    cat.setAttacking(true);

                } else {
                    if (cat.tickCount % 2 == 0 && cat.distanceTo(this.mossBall) < 2){
                        Vec3 dir = this.mossBall.position()
                                .subtract(cat.position())
                                .normalize()
                                .scale(0.1);
                        cat.setDeltaMovement(cat.getDeltaMovement().add(dir));
                        cat.getLookControl().setLookAt(this.mossBall);
                    }

                    if (cat.isAttacking() && cat.tickCount % 20 == 0) cat.setAttacking(false);

                    cat.getNavigation().moveTo(this.mossBall, 1.2f);
                    increment += 0.03f;

                }



                super.tick();
            }

            @Override
            public void stop() {
                cat.setAttacking(false);
                this.mossBall = null;
            }

            @Override
            public void start() {
                increment = 0;
                super.start();
            }

            private MossBallEntity findValidMossBall() {
                List<MossBallEntity> balls = cat.level().getEntitiesOfClass(
                        MossBallEntity.class,
                        cat.getBoundingBox().inflate(range),
                        ball -> ball.isAlive()
                                && ball.getLife() >= 10
                                && ball.getWater() == 0
                );

                MossBallEntity best = null;
                double bestDist = Double.MAX_VALUE;

                for (MossBallEntity ball : balls) {

                    double dist = cat.distanceToSqr(ball);

                    if (cat.getNavigation().createPath(ball, 0) == null) continue;

                    if (dist < bestDist) {
                        bestDist = dist;
                        best = ball;
                    }
                }

                return best;
            }
        }
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

    private void finishPatrol() {
        this.returnHomeFlag = true;
        this.onBorderPatrolFlag = false;
        this.onHuntingPatrolFlag = false;
        this.returningFromPatrol = true;
        this.onAreaToPatrolForTicks = 0;
        this.setAreasToPatrol(new HashMap<>());
    }

    private int patrolIndex = 0;
    public boolean onBorderPatrolFlag = false;
    public boolean onHuntingPatrolFlag = false;
    public boolean returningFromPatrol = false;

    public boolean tellingCatsToPatrol = false;
    public List<WCatEntity> catsToTell = new ArrayList<>();
    public int patrolType = 0;

    private Map<Integer, BlockPos> areasToPatrol = new HashMap<>();

    private int onAreaToPatrolForTicks = 0;

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
