package net.snowteb.warriorcats_events.network.packet.c2s.skilltree;

import net.minecraft.core.Holder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.attachments.CapabilityManager;
import net.snowteb.warriorcats_events.attachments.ModAttachments;
import net.snowteb.warriorcats_events.attachments.PlayerSkill;
import net.snowteb.warriorcats_events.util.ModAttributes;

public class ResetSkillsPacket implements CustomPacketPayload {

    public ResetSkillsPacket() {}

    public ResetSkillsPacket(FriendlyByteBuf buf) {}

    public void toBytes(FriendlyByteBuf buf) {}

    public boolean handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();

            int speedLevel = player.getData(ModAttachments.PLAYER_SKILL).getSpeedLevel();
            int hpLevel = player.getData(ModAttachments.PLAYER_SKILL).getHPLevel();
            int dmgLevel = player.getData(ModAttachments.PLAYER_SKILL).getDMGLevel();
            int jumpLevel = player.getData(ModAttachments.PLAYER_SKILL).getJumpLevel();
            int armorLevel = player.getData(ModAttachments.PLAYER_SKILL).getArmorLevel();

            int climbLevel = (player.getData(ModAttachments.PLAYER_SKILL).isClimbUnlocked()) ? 1 : 0;

            player.giveExperiencePoints((int) ((PlayerSkill.getDefaultSpeedCost()* speedLevel)* 0.66));
            player.giveExperiencePoints((int) ((PlayerSkill.getDefaultHPCost()* hpLevel)* 0.66));
            player.giveExperiencePoints((int) ((PlayerSkill.getDefaultDMGCost()* dmgLevel)* 0.66));
            player.giveExperiencePoints((int) ((PlayerSkill.getDefaultJumpCost()* jumpLevel)* 0.66));
            player.giveExperiencePoints((int) ((PlayerSkill.getDefaultArmorCost()* armorLevel)* 0.66));
            player.giveExperiencePoints((int) ((PlayerSkill.getDefaultClimbCost()* climbLevel)* 0.66));

            CapabilityManager.attachmentProvider(player, ModAttachments.PLAYER_SKILL, data -> {
                data.setSpeedLevel(0);
                data.setHPLevel(0);
                data.setDMGLevel(0);
                data.setJumpLevel(0);
                data.setArmorLevel(0);
                data.setClimbUnlocked(false);
            });

            player.getPersistentData().putInt("skill_speed_level", 0);
            player.getPersistentData().putInt("skill_hp_level", 0);
            player.getPersistentData().putInt("skill_dmg_level", 0);
            player.getPersistentData().putInt("skill_jump_level", 0);
            player.getPersistentData().putInt("skill_armor_level", 0);

            resetAttribute(Attributes.MAX_HEALTH, PlayerSkill.HP_SKILL_UUID, player);
            resetAttribute(Attributes.MOVEMENT_SPEED, PlayerSkill.SPEED_SKILL_UUID, player);
            resetAttribute(Attributes.ATTACK_DAMAGE, PlayerSkill.DMG_SKILL_UUID, player);
            resetAttribute(ModAttributes.PLAYER_JUMP, PlayerSkill.JUMP_SKILL_UUID, player);
            resetAttribute(Attributes.ARMOR, PlayerSkill.ARMOR_SKILL_UUID, player);

            CapabilityManager.attachmentProvider(player, ModAttachments.PLAYER_STEALTH, cap -> {
                cap.setUnlocked(false);
                cap.sync(player);
            });


        });
        return true;
    }


    private void resetAttribute(Holder<Attribute> attribute, ResourceLocation skillData, ServerPlayer player) {
        var Attr = player.getAttribute(attribute);
        if (Attr != null) Attr.removeModifier(skillData);
    }

    public static final Type<ResetSkillsPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "reset_skills"));

    public static final StreamCodec<FriendlyByteBuf, ResetSkillsPacket> CODEC =
            StreamCodec.of(
                    (buf, pkt) -> pkt.toBytes(buf),
                    buf -> new ResetSkillsPacket(buf)
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
