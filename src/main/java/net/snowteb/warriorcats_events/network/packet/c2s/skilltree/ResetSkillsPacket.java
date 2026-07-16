package net.snowteb.warriorcats_events.network.packet.c2s.skilltree;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.skills.ISkillData;
import net.snowteb.warriorcats_events.skills.PlayerSkill;
import net.snowteb.warriorcats_events.skills.PlayerSkillProvider;
import net.snowteb.warriorcats_events.stealth.PlayerStealthProvider;
import net.snowteb.warriorcats_events.util.ModAttributes;

import java.util.UUID;
import java.util.function.Supplier;

public class ResetSkillsPacket {
    public ResetSkillsPacket() {}
    public ResetSkillsPacket(FriendlyByteBuf buf) {}
    public void toBytes(FriendlyByteBuf buf) {}

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;

            int speedLevel = player.getCapability(PlayerSkillProvider.SKILL_DATA)
                    .map(ISkillData::getSpeedLevel)
                    .orElse(player.getPersistentData().getInt("skill_speed_level"));
            int hpLevel = player.getCapability(PlayerSkillProvider.SKILL_DATA)
                    .map(ISkillData::getHPLevel)
                    .orElse(player.getPersistentData().getInt("skill_hp_level"));
            int dmgLevel = player.getCapability(PlayerSkillProvider.SKILL_DATA)
                    .map(ISkillData::getDMGLevel)
                    .orElse(player.getPersistentData().getInt("skill_dmg_level"));
            int jumpLevel = player.getCapability(PlayerSkillProvider.SKILL_DATA)
                    .map(ISkillData::getJumpLevel)
                    .orElse(player.getPersistentData().getInt("skill_jump_level"));
            int armorLevel = player.getCapability(PlayerSkillProvider.SKILL_DATA)
                    .map(ISkillData::getArmorLevel)
                    .orElse(player.getPersistentData().getInt("skill_armor_level"));

            int climbLevel = (player.getCapability(PlayerSkillProvider.SKILL_DATA)
                    .map(ISkillData::isClimbUnlocked)
                    .orElse(false)) ? 1 : 0;

            player.giveExperiencePoints((int) ((PlayerSkill.getDefaultSpeedCost()* speedLevel)* 0.66));
            player.giveExperiencePoints((int) ((PlayerSkill.getDefaultHPCost()* hpLevel)* 0.66));
            player.giveExperiencePoints((int) ((PlayerSkill.getDefaultDMGCost()* dmgLevel)* 0.66));
            player.giveExperiencePoints((int) ((PlayerSkill.getDefaultJumpCost()* jumpLevel)* 0.66));
            player.giveExperiencePoints((int) ((PlayerSkill.getDefaultArmorCost()* armorLevel)* 0.66));
            player.giveExperiencePoints((int) ((PlayerSkill.getDefaultClimbCost()* climbLevel)* 0.66));

            player.getCapability(PlayerSkillProvider.SKILL_DATA).ifPresent(data -> data.setSpeedLevel(0));
            player.getCapability(PlayerSkillProvider.SKILL_DATA).ifPresent(data -> data.setHPLevel(0));
            player.getCapability(PlayerSkillProvider.SKILL_DATA).ifPresent(data -> data.setDMGLevel(0));
            player.getCapability(PlayerSkillProvider.SKILL_DATA).ifPresent(data -> data.setJumpLevel(0));
            player.getCapability(PlayerSkillProvider.SKILL_DATA).ifPresent(data -> data.setArmorLevel(0));
            player.getCapability(PlayerSkillProvider.SKILL_DATA).ifPresent(data -> data.setClimbUnlocked(false));

            player.getPersistentData().putInt("skill_speed_level", 0);
            player.getPersistentData().putInt("skill_hp_level", 0);
            player.getPersistentData().putInt("skill_dmg_level", 0);
            player.getPersistentData().putInt("skill_jump_level", 0);
            player.getPersistentData().putInt("skill_armor_level", 0);

            resetAttribute(Attributes.MAX_HEALTH, PlayerSkill.HP_SKILL_UUID, player);
            resetAttribute(Attributes.MOVEMENT_SPEED, PlayerSkill.SPEED_SKILL_UUID, player);
            resetAttribute(Attributes.ATTACK_DAMAGE, PlayerSkill.DMG_SKILL_UUID, player);
            resetAttribute(ModAttributes.PLAYER_JUMP.get(), PlayerSkill.JUMP_SKILL_UUID, player);
            resetAttribute(Attributes.ARMOR, PlayerSkill.ARMOR_SKILL_UUID, player);

            player.getCapability(PlayerStealthProvider.STEALTH_MODE).ifPresent(cap -> {
                cap.setUnlocked(false);
                cap.sync(player);
            });


        });
        return true;
    }



    private void resetAttribute(Attribute attribute, UUID skillData, ServerPlayer player) {
        var Attr = player.getAttribute(attribute);
        if (Attr != null) Attr.removeModifier(skillData);
    }
}
