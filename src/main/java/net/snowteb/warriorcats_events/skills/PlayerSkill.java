package net.snowteb.warriorcats_events.skills;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.snowteb.warriorcats_events.util.ModAttributes;
import net.snowteb.warriorcats_events.zconfig.WCEServerConfig;

import java.util.UUID;

public class PlayerSkill implements ISkillData {

    public static final double speedMultiplier = 0.025;
    public static final double hpMultiplier = 0.1;
    public static final double damageMultiplier = 0.09;
    public static final double jumpMultiplier = 0.093;
    public static final double armorMultiplier = 2.5;

    public static int getDefaultSpeedCost() {
        return (int) (30 * WCEServerConfig.SERVER.SKILL_COST_MULTIPLIER.get());
    }

    public static int getDefaultHPCost() {
        return (int) (160 * WCEServerConfig.SERVER.SKILL_COST_MULTIPLIER.get());
    }

    public static int getDefaultDMGCost() {
        return (int) (40 * WCEServerConfig.SERVER.SKILL_COST_MULTIPLIER.get());
    }

    public static int getDefaultJumpCost() {
        return (int) (300 * WCEServerConfig.SERVER.SKILL_COST_MULTIPLIER.get());
    }

    public static int getDefaultArmorCost() {
        return (int) (300 * WCEServerConfig.SERVER.SKILL_COST_MULTIPLIER.get());
    }

    public static int getDefaultStealthCost() {
        return (int) (2921 * WCEServerConfig.SERVER.SKILL_COST_MULTIPLIER.get());
    }

    public static int getDefaultClimbCost() {
        return (int) (1800 * WCEServerConfig.SERVER.SKILL_COST_MULTIPLIER.get());
    }


    public static int maxSpeedLevel = 10;
    public static int maxHPLevel = 5;
    public static int maxDMGLevel = 10;
    public static int maxJumpLevel = 3;
    public static int maxArmorLevel = 3;

    private int speedLevel = 0;
    private int HPLevel = 0;
    private int DMGLevel = 0;
    private int jumpLevel = 0;
    private int armorLevel = 0;

    private boolean climbUnlocked = false;

    private boolean isLeaping = false;
    private int leapPower = 0;

    public static final UUID SPEED_SKILL_UUID =
            UUID.fromString("a3d2c21a-9e65-4e8b-92b0-d9d239a5f6ac");
    public static final UUID HP_SKILL_UUID =
            UUID.fromString("a3d2c21a-9e66-4e8b-92b0-d9d239a5f6ac");
    public static final UUID DMG_SKILL_UUID =
            UUID.fromString("a3d2c21a-9e67-4e8b-92b0-d9d239a5f6ac");
    public static final UUID JUMP_SKILL_UUID =
            UUID.fromString("a3d2c21a-9e68-4e8b-92b0-d9d239a5f6ac");
    public static final UUID ARMOR_SKILL_UUID =
            UUID.fromString("a3d2c21a-9e69-4e8b-92b0-d9d239a5f6ac");


    @Override
    public int getSpeedLevel() {
        return speedLevel;
    }

    @Override
    public void setSpeedLevel(int level) {
        this.speedLevel = level;
    }

    @Override
    public boolean isLeaping() {
        return isLeaping;
    }

    @Override
    public void setLeaping(boolean isLeaping) {
        this.isLeaping = isLeaping;
    }

    @Override
    public int getLeapPower() {
        return leapPower;
    }

    @Override
    public void setLeapPower(int leapPower) {
        this.leapPower = leapPower;
    }

    @Override
    public int getHPLevel() {
        return HPLevel;
    }

    @Override
    public void setHPLevel(int level) {
        this.HPLevel = level;
    }

    @Override
    public int getDMGLevel() {
        return DMGLevel;
    }

    @Override
    public void setDMGLevel(int level) {
        this.DMGLevel = level;
    }

    @Override
    public int getJumpLevel() {
        return jumpLevel;
    }

    @Override
    public void setJumpLevel(int level) {
        this.jumpLevel = level;
    }

    @Override
    public int getArmorLevel() {
        return armorLevel;
    }

    @Override
    public void setArmorLevel(int level) {
        this.armorLevel = level;
    }

    @Override
    public boolean isClimbUnlocked() {
        return climbUnlocked;
    }

    @Override
    public void setClimbUnlocked(boolean climbUnlocked) {
        this.climbUnlocked = climbUnlocked;
    }


    @Override
    public void copyFrom(ISkillData other) {
        this.speedLevel = other.getSpeedLevel();
        this.HPLevel = other.getHPLevel();
        this.DMGLevel = other.getDMGLevel();
        this.jumpLevel = other.getJumpLevel();
        this.armorLevel = other.getArmorLevel();
        this.climbUnlocked = other.isClimbUnlocked();
    }


    public static void reviveAttributes(ServerPlayer player, ISkillData newStore) {
        var speedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null) {
            speedAttr.removeModifier(PlayerSkill.SPEED_SKILL_UUID);
            double bonus = (speedMultiplier * WCEServerConfig.SERVER.SKILL_SPEED_MULTIPLIER.get()) * newStore.getSpeedLevel();
            if (bonus > 0) {
                speedAttr.addPermanentModifier(
                        new AttributeModifier(
                                PlayerSkill.SPEED_SKILL_UUID,
                                "skill_speed_bonus",
                                bonus,
                                AttributeModifier.Operation.MULTIPLY_TOTAL
                        )
                );
            }
        }
        var hpAttr = player.getAttribute(Attributes.MAX_HEALTH);
        if (hpAttr != null) {
            hpAttr.removeModifier(PlayerSkill.HP_SKILL_UUID);

            double bonus = (hpMultiplier * WCEServerConfig.SERVER.SKILL_HP_MULTIPLIER.get()) * newStore.getHPLevel();
            if (bonus > 0) {
                hpAttr.addPermanentModifier(
                        new AttributeModifier(
                                PlayerSkill.HP_SKILL_UUID,
                                "skill_hp_bonus",
                                bonus,
                                AttributeModifier.Operation.MULTIPLY_TOTAL
                        )
                );
            }
        }
        var dmgAttr = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (dmgAttr != null) {
            dmgAttr.removeModifier(PlayerSkill.DMG_SKILL_UUID);
            double bonus = (damageMultiplier * WCEServerConfig.SERVER.SKILL_DMG_MULTIPLIER.get()) * newStore.getDMGLevel();
            if (bonus > 0) {
                dmgAttr.addPermanentModifier(
                        new AttributeModifier(
                                PlayerSkill.DMG_SKILL_UUID,
                                "skill_dmg_bonus",
                                bonus,
                                AttributeModifier.Operation.ADDITION
                        )
                );
            }
        }
        var jumpAttr = player.getAttribute(ModAttributes.PLAYER_JUMP.get());
        if (jumpAttr != null) {
            jumpAttr.removeModifier(PlayerSkill.JUMP_SKILL_UUID);
            double bonus = (jumpMultiplier * WCEServerConfig.SERVER.SKILL_JUMP_MULTIPLIER.get()) * newStore.getJumpLevel();
            if (bonus > 0) {
                jumpAttr.addPermanentModifier(
                        new AttributeModifier(
                                PlayerSkill.JUMP_SKILL_UUID,
                                "skill_jump_bonus",
                                bonus,
                                AttributeModifier.Operation.ADDITION
                        )
                );
            }
        }
        var armorAttr = player.getAttribute(Attributes.ARMOR);
        if (armorAttr != null) {
            armorAttr.removeModifier(PlayerSkill.ARMOR_SKILL_UUID);
            double bonus = (armorMultiplier * WCEServerConfig.SERVER.SKILL_ARMOR_MULTIPLIER.get()) * newStore.getArmorLevel();
            if (bonus > 0) {
                armorAttr.addPermanentModifier(
                        new AttributeModifier(
                                PlayerSkill.ARMOR_SKILL_UUID,
                                "skill_armor_bonus",
                                bonus,
                                AttributeModifier.Operation.ADDITION
                        )
                );
            }
        }
    }

    public static void removeAttributes(ServerPlayer player) {
        var speedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null) {
            speedAttr.removeModifier(PlayerSkill.SPEED_SKILL_UUID);
        }
        var hpAttr = player.getAttribute(Attributes.MAX_HEALTH);
        if (hpAttr != null) {
            hpAttr.removeModifier(PlayerSkill.HP_SKILL_UUID);
        }
        var dmgAttr = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (dmgAttr != null) {
            dmgAttr.removeModifier(PlayerSkill.DMG_SKILL_UUID);
        }
        var jumpAttr = player.getAttribute(ModAttributes.PLAYER_JUMP.get());
        if (jumpAttr != null) {
            jumpAttr.removeModifier(PlayerSkill.JUMP_SKILL_UUID);
        }
        var armorAttr = player.getAttribute(Attributes.ARMOR);
        if (armorAttr != null) {
            armorAttr.removeModifier(PlayerSkill.ARMOR_SKILL_UUID);
        }
    }

}

