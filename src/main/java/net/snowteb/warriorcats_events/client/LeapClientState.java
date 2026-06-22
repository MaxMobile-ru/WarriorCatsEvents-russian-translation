package net.snowteb.warriorcats_events.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.snowteb.warriorcats_events.attachments.ModAttachments;
import net.snowteb.warriorcats_events.clan.ClanData;
import net.snowteb.warriorcats_events.entity.custom.WCatEntity;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.c2s.skilltree.CtSPerformLeapPacket;
import net.snowteb.warriorcats_events.attachments.ISkillData;
import net.snowteb.warriorcats_events.sound.ModSounds;
import net.snowteb.warriorcats_events.util.ModKeybinds;
import net.snowteb.warriorcats_events.zconfig.WCEClientConfig;
import tocraft.walkers.api.PlayerShape;

import javax.annotation.Nullable;
import java.util.UUID;

public class LeapClientState {
    private static int shiftKeyDownCounter = 0;
    private static int leapPowerCounter = 0;
    private static boolean attackWasDown = false;

    private static float sprintingCounter = 0;

    private static int sprintCounterThreshold = 8;

    @Nullable
    private static Entity lockedLookEntity = null;
    private static boolean wasLookKeyDown = false;
    private static boolean lockingTarget = false;

    private static final int MAX_SNEAK_COUNTER = 300;

    public static void tick(boolean shifting) {

        if (!WCEClientConfig.CLIENT.LEAP.get()) return;

        boolean hasTool = false;

        if (Minecraft.getInstance().player != null) {
            hasTool = (Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof PickaxeItem
                    || Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof AxeItem
                    || Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof ShovelItem);

            if (!(PlayerShape.getCurrentShape(Minecraft.getInstance().player) instanceof Animal)) return;
        }


        if (shifting && Minecraft.getInstance().player.onGround() && !hasTool) {
            sprintingCounter = 0;

            if (shiftKeyDownCounter > MAX_SNEAK_COUNTER + 2 || !Minecraft.getInstance().player.onGround()) return;

            boolean lookKeyDown = Minecraft.getInstance().options.keyUse.isDown();
            if (lookKeyDown && !wasLookKeyDown) {
                lockedLookEntity = getEntityPlayerIsLookingAtClient(Minecraft.getInstance().player, 15.0D);
            }

            if (lockedLookEntity != null) {
                if (!lockedLookEntity.isAlive()
                        || lockedLookEntity.distanceTo(Minecraft.getInstance().player) > 17.0F) {
                    lockedLookEntity = null;
                    lockingTarget = false;
                } else {
                    if (lockingTarget) {
                        forceLookAt(lockedLookEntity, Minecraft.getInstance().getFps());
                    }
                }
            }

            boolean attackDown = ModKeybinds.LEAP_KEY.isDown();

            shiftKeyDownCounter++;

            if (shiftKeyDownCounter >= 20 && !attackWasDown) {
                leapPowerCounter+=2;
                leapPowerCounter = Math.min(leapPowerCounter, 100);
                if (shiftKeyDownCounter <= MAX_SNEAK_COUNTER && Minecraft.getInstance().player.onGround()) {
                    Minecraft.getInstance().player.playSound(ModSounds.SHORT_WOOSH.get(), 0.3f, 0.7f);
                } else {
                    leapPowerCounter = 0;
                    lockingTarget = false;
                }

                if (leapPowerCounter > 0) {
                    if (shiftKeyDownCounter <= MAX_SNEAK_COUNTER && Minecraft.getInstance().player.onGround()
                            && attackDown && !attackWasDown) {
                        lockedLookEntity = null;
                        wasLookKeyDown = false;
                        lockingTarget = false;
                        ModPackets.sendToServer(new CtSPerformLeapPacket(leapPowerCounter));
                        leapPowerCounter = 0;
                        shiftKeyDownCounter = 0;
                    }
                }

            }
            wasLookKeyDown = lookKeyDown;
            attackWasDown = attackDown;

        } else {
            LocalPlayer localPlayer = Minecraft.getInstance().player;
            int runSkillLevel = 0;
            if (localPlayer != null) {
                runSkillLevel = localPlayer.getData(ModAttachments.PLAYER_SKILL).getSpeedLevel();
            }

            if (runSkillLevel >= 8 && !hasTool) {
                if (PlayerShape.getCurrentShape(localPlayer) instanceof WCatEntity && localPlayer.isSprinting()) {
                    sprintingCounter = Math.min(sprintingCounter, 300);

                    if (localPlayer.onGround() && localPlayer.getDeltaMovement().length() > 0.17) {
                        sprintingCounter++;
                        sprintCounterThreshold = 10;

                        if (sprintingCounter >= 200 && runSkillLevel > 9) {
                            setMaxUpStep(localPlayer, 1.2f);
                        } else {
                            setMaxUpStep(localPlayer,0.6f);
                        }

                        if (sprintingCounter >= 280) {
                            leapPowerCounter = 100;

                            if (ModKeybinds.LEAP_KEY.isDown()) {
                                sprintingCounter -= 40;
                                ModPackets.sendToServer(new CtSPerformLeapPacket(leapPowerCounter));
                            }
                        }

                    } else {
                        leapPowerCounter = 0;
                        if (sprintCounterThreshold > 0) sprintCounterThreshold--;
                        if (sprintCounterThreshold <= 0) {
                            if (sprintingCounter > 0) sprintingCounter -= 1.7f;
                        }
                        setMaxUpStep(localPlayer,0.6f);


                        lockedLookEntity = null;
                        wasLookKeyDown = false;
                        lockingTarget = false;
                        shiftKeyDownCounter = 0;
                    }
                } else {
                    lockedLookEntity = null;
                    wasLookKeyDown = false;
                    lockingTarget = false;
                    leapPowerCounter = 0;
                    shiftKeyDownCounter = 0;
                    if (sprintCounterThreshold > 0) sprintCounterThreshold--;
                    if (sprintCounterThreshold <= 0) {
                        if (sprintingCounter > 0) sprintingCounter -= 1.7f;
                    }

                    setMaxUpStep(localPlayer,0.6f);
                }
            } else {
                lockedLookEntity = null;
                wasLookKeyDown = false;
                lockingTarget = false;
                leapPowerCounter = 0;
                shiftKeyDownCounter = 0;
                if (sprintCounterThreshold > 0) sprintCounterThreshold--;
                if (sprintingCounter > 0) sprintingCounter -= 1.7f;

                setMaxUpStep(localPlayer,0.6f);
            }


        }



    }

    @Nullable
    public static Entity getEntityPlayerIsLookingAtClient(LocalPlayer player, double range) {
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();
        Vec3 reachVec = eyePos.add(lookVec.scale(range));

        AABB searchBox = player.getBoundingBox()
                .expandTowards(lookVec.scale(range))
                .inflate(2.0D);

        EntityHitResult result = ProjectileUtil.getEntityHitResult(
                player.level(),
                player,
                eyePos,
                reachVec,
                searchBox,
                e -> e.isAlive() && e != player
                        && !(e instanceof TamableAnimal cat && cat.isTame() && (cat.getOwner() == player || isClanedWith(cat, player)))
        );

        if (result != null) {
            if (!lockingTarget) {
                lockingTarget = true;
                player.displayClientMessage(Component.literal("Target Locked").withStyle(ChatFormatting.GRAY), true);
            } else {
                lockingTarget = false;
                player.displayClientMessage(Component.literal("Target unlocked").withStyle(ChatFormatting.DARK_GRAY), true);
            }
        } else {
            if (lockingTarget) {
                player.displayClientMessage(Component.literal("Target unlocked").withStyle(ChatFormatting.DARK_GRAY), true);
                lockingTarget = false;
            }
        }

        return result != null ? result.getEntity() : null;
    }


    public static void forceLookAt(Entity target, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || target == null) return;

        Vec3 eyes = player.getEyePosition(partialTick);
        Vec3 targetPos = target.getEyePosition(partialTick);

        Vec3 dir = targetPos.subtract(eyes);

        double dx = dir.x;
        double dy = dir.y;
        double dz = dir.z;

        double distXZ = Math.sqrt(dx * dx + dz * dz);

        float yaw = (float)(Math.toDegrees(Math.atan2(dz, dx)) - 90F);
        float pitch = (float)(-Math.toDegrees(Math.atan2(dy, distXZ)));


        player.setYRot(Mth.lerp(0.25F, player.getYRot(), yaw));
        player.setYBodyRot(Mth.lerp(0.25F, player.getYRot(), yaw));
        player.yRotO = yaw;
        player.xRotO = pitch;
    }




    public static int getLeapPowerCounter() {
        return leapPowerCounter;
    }

    public static float getSprintingCounter() {
        return sprintingCounter;
    }

    public static boolean isLeapActive() {
        return shiftKeyDownCounter < MAX_SNEAK_COUNTER + 4;
    }

    public static boolean isLockingTarget() {
        return lockingTarget;
    }

    public static boolean isClanedWith(TamableAnimal animal, LocalPlayer player) {
        if (animal instanceof WCatEntity cat && cat.isTame() && !cat.isOwnedBy(player)) {
            UUID clanUUID = ClientClanData.get().getCurrentClanUUID();
            if (!clanUUID.equals(ClanData.EMPTY_UUID)) {
                if (cat.getClanUUID().equals(clanUUID)) {
                    return true;
                }
            }
        }
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public static void setCanceled(){
        shiftKeyDownCounter = MAX_SNEAK_COUNTER + 100;
    }

    public static int getSprintCounterThreshold() {
        return sprintCounterThreshold;
    }


    @Nullable
    public static Entity getAnyEntityPlayerIsLookingAt(LocalPlayer player, double range) {
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();
        Vec3 reachVec = eyePos.add(lookVec.scale(range));

        AABB searchBox = player.getBoundingBox()
                .expandTowards(lookVec.scale(range))
                .inflate(2.0D);

        EntityHitResult result = ProjectileUtil.getEntityHitResult(
                player.level(),
                player,
                eyePos,
                reachVec,
                searchBox,
                e -> e.isAlive() && e != player
        );

        return result != null ? result.getEntity() : null;
    }

    private static void setMaxUpStep(LocalPlayer player, float maxStepUp) {
        AttributeInstance step = player.getAttribute(Attributes.STEP_HEIGHT);
        if (step != null) step.setBaseValue(maxStepUp);
    }
}
