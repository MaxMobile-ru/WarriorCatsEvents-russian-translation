package net.snowteb.warriorcats_events.network.packet.c2s.cats;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.clan.WCEPlayerData;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCGenetics;
import net.snowteb.warriorcats_events.util.GeneticsForVariant;
import net.snowteb.warriorcats_events.clan.ClanData;
import net.snowteb.warriorcats_events.clan.WCEPlayerDataProvider;
import net.snowteb.warriorcats_events.entity.ModEntities;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.item.ModItems;
import net.snowteb.warriorcats_events.zconfig.WCEServerConfig;

import java.util.UUID;
import java.util.function.Supplier;

import static net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity.Rank.KIT;

public class CtSCreateAndSpawnKitPacket {
    private final String kitPrefix;
    private final int kitVariant;

    public CtSCreateAndSpawnKitPacket(String kitPrefix, int kitVariant) {
        this.kitPrefix = kitPrefix;
        this.kitVariant = kitVariant;
    }

    public CtSCreateAndSpawnKitPacket(FriendlyByteBuf buf) {
        this.kitPrefix = buf.readUtf(64);
        this.kitVariant = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(this.kitPrefix);
        buf.writeInt(this.kitVariant);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> {
            Player player = ctx.getSender();
            String prefix = this.kitPrefix;
            int variant = this.kitVariant;

            if (player instanceof ServerPlayer sPlayer) {
                BlockPos clickedPos = sPlayer.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                        .map(WCEPlayerData::getTempClickedPosData).orElse(sPlayer.blockPosition());

                String clan = sPlayer.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                        .map(cap -> cap.getClanName(sPlayer.serverLevel())).orElse("undefined");


                ServerLevel level = ((ServerLevel) sPlayer.level());

                WCatEntity kit = ModEntities.WCAT.get().create(level);


                if (kit != null) {

                    WCGenetics playerGens = player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                                    .map(WCEPlayerData::getPlayerGenetics).orElse(new WCGenetics());

                    WCGenetics mateGens = player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                                    .map(WCEPlayerData::getMateGenetics).orElse(new WCGenetics());

                    boolean onGenSkin = player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                            .map(WCEPlayerData::isOnGeneticalSkin).orElse(false);

                    if (!onGenSkin) {
                        int variantData = player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                                .map(WCEPlayerData::getVariantData).orElse(0);
                        playerGens = GeneticsForVariant.get(variantData);
                    }


                    Vec3 pos = clickedPos.getCenter();
                    kit.setPos(pos.x, pos.y, pos.z);

                    kit.finalizeSpawn(level, level.getCurrentDifficultyAt(clickedPos),
                            MobSpawnType.MOB_SUMMONED, null, null);

                    int minutes = WCEServerConfig.SERVER.KIT_GROWTH_MINUTES.get();
                    int growingTicks = minutes * 20 * 60;
                    kit.setAge(-growingTicks);

                    kit.setOnGeneticalSkin(true);
                    kit.getGeneticsModule().inheritGeneticsFromParents(playerGens, mateGens);

                    level.addFreshEntity(kit);

                    kit.setTame(true);
                    kit.tame(player);
                    kit.setOwnerUUID(sPlayer.getUUID());
                    kit.setRank(KIT);
                    kit.kitBorn = true;
                    String finalName = "";
                    kit.setVariant(variant);

                    kit.mode = WCatEntity.CatMode.FOLLOW;

                    kit.setGender(sPlayer.getRandom().nextInt(2));

                    if (!kit.hasCustomName()) {
                        String genderS;
                        if (kit.getGender() == 0) {
                            genderS = " ♂";
                        } else {
                            genderS = " ♀";
                        }

                        finalName = prefix + "kit" + genderS;
                        kit.setCustomName(Component.literal(finalName));
                        kit.setCustomNameVisible(true);

                        kit.setPrefix(Component.literal(kitPrefix));

                        sPlayer.sendSystemMessage(Component.literal(finalName).withStyle(ChatFormatting.GREEN)
                                .append(Component.literal(" has been born!").withStyle(ChatFormatting.WHITE))
                        );
                    }

                    kit.wanderCenter = sPlayer.blockPosition();
                    kit.applyBabyAttributes();
                    kit.setHealth(kit.getMaxHealth());
                    kit.setNameColor(KIT);

                    kit.assignRandomPersonality(kit.getRandom());
                    kit.setSpecificMood(WCatEntity.Mood.CALM);


                    kit.setHomePosition(clickedPos);
                    kit.setClan(Component.literal(clan));
                    kit.setClanUUID(sPlayer.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                            .map(WCEPlayerData::getCurrentClanUUID).orElse(ClanData.EMPTY_UUID));

                    UUID otherParentUUID = sPlayer.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                            .map(WCEPlayerData::getMateUUID).orElse(null);

                    int morphGender = sPlayer.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                            .map(WCEPlayerData::getGenderData)
                            .orElse(0);

                    String morphName = sPlayer.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                            .map(WCEPlayerData::getMorphName)
                            .orElse("Unknown");

                    Entity otherParent = null;

                    if (otherParentUUID != null) {
                        otherParent = level.getEntity(otherParentUUID);
                    }

                    if (otherParent instanceof WCatEntity) {

                        if (morphGender == 0) {
                            kit.setFatherUUID(sPlayer.getUUID());
                            kit.setMotherUUID(otherParent.getUUID());
                            kit.setMother(otherParent.hasCustomName() ? otherParent.getCustomName() : Component.literal("Unknown"));
                            kit.setFather(Component.literal(morphName));
                        } else {
                            kit.setFatherUUID(otherParent.getUUID());
                            kit.setMotherUUID(sPlayer.getUUID());
                            kit.setMother(Component.literal(morphName));
                            kit.setFather(otherParent.hasCustomName() ? otherParent.getCustomName() : Component.literal("Unknown"));
                        }
                    } else {
                        kit.setMotherUUID(sPlayer.getUUID());
                        kit.setMother(Component.literal(morphName));
                    }

                    kit.setForbiddenPlayer(sPlayer.getUUID());
                    kit.setForbiddenFromMatingPlayer(true);
                    kit.setForbiddingFutureGensFromMatingPlayer(true);
                    kit.setFriendshipLevel(sPlayer.getUUID(), 50);

                    if (player.getItemInHand(InteractionHand.MAIN_HAND).is(ModItems.KIT_ITEM.get()) && !player.getAbilities().instabuild) player.getItemInHand(InteractionHand.MAIN_HAND).shrink(1);
                    level.sendParticles(ParticleTypes.HAPPY_VILLAGER, clickedPos.getX(), clickedPos.getY() + 0.5, clickedPos.getZ(), 15,0.3f,0.3f, 0.3f,0.2f);

                    Component messageLog = Component.literal(finalName).withStyle(ChatFormatting.GREEN)
                            .append(Component.literal(" has been born! ").withStyle(ChatFormatting.WHITE))
                            .append(Component.literal("(").withStyle(ChatFormatting.GRAY))
                            .append(Component.literal(player.getName().getString()).withStyle(ChatFormatting.GRAY))
                            .append(Component.literal(")").withStyle(ChatFormatting.GRAY));
                    kit.registerClanLog(messageLog);


                        MinecraftServer mcServer = player.getServer();
                        if (mcServer != null) {
                            Advancement adv = mcServer.getAdvancements()
                                    .getAdvancement(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID,"kit_generated"));
                            if (adv != null) {
                                sPlayer.getAdvancements().award(adv, "kit_generated");
                            }
                        }


                }


            }
        });

        ctx.setPacketHandled(true);
    }
}

