package net.snowteb.warriorcats_events.network.packet.c2s.skilltree;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.Advancement;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.skills.ISkillData;
import net.snowteb.warriorcats_events.skills.PlayerSkill;
import net.snowteb.warriorcats_events.skills.PlayerSkillProvider;
import net.snowteb.warriorcats_events.util.ModAttributes;
import net.snowteb.warriorcats_events.zconfig.WCEServerConfig;

import java.util.function.Supplier;

import static net.snowteb.warriorcats_events.skills.PlayerSkill.JUMP_SKILL_UUID;

public class CtSMoreJumpPacket {

    public CtSMoreJumpPacket() {

    }

    public CtSMoreJumpPacket(FriendlyByteBuf buf) {

    }

    public void toBytes(FriendlyByteBuf buf) {

    }


    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;

            if (!WCEServerConfig.SERVER.SKILL_TREE_SERVER.get()) {
                player.sendSystemMessage(Component.translatable("generic.skill_tree_disabled").withStyle(ChatFormatting.RED));
                return;
            }

            int currentLevel = player.getCapability(PlayerSkillProvider.SKILL_DATA)
                    .map(ISkillData::getJumpLevel)
                    .orElse(player.getPersistentData().getInt("skill_jump_level"));

            int currentSpeedLevel = player.getCapability(PlayerSkillProvider.SKILL_DATA)
                    .map(ISkillData::getSpeedLevel)
                    .orElse(player.getPersistentData().getInt("skill_speed_level"));
            int currentHPLevel = player.getCapability(PlayerSkillProvider.SKILL_DATA)
                    .map(ISkillData::getHPLevel)
                    .orElse(player.getPersistentData().getInt("skill_hp_level"));

            int cost = PlayerSkill.getDefaultJumpCost() * (currentLevel + 1);
            int remaining = cost - player.totalExperience;

            if (currentSpeedLevel == PlayerSkill.maxSpeedLevel && currentHPLevel == PlayerSkill.maxHPLevel) {

            if (player.totalExperience < cost && currentLevel < PlayerSkill.maxJumpLevel) {
                player.sendSystemMessage(Component.translatable("generic.need_more_xp", remaining).withStyle(ChatFormatting.RED));
                return;
            }
                if (currentLevel < PlayerSkill.maxJumpLevel) {
                    player.giveExperiencePoints(-cost);
                    var attribute = player.getAttribute(ModAttributes.PLAYER_JUMP.get());
                    if (attribute == null) return;

                    attribute.removeModifier(JUMP_SKILL_UUID);
                    double bonus = (PlayerSkill.jumpMultiplier * WCEServerConfig.SERVER.SKILL_JUMP_MULTIPLIER.get()) * (currentLevel + 1);
                    attribute.addPermanentModifier(new AttributeModifier(
                            JUMP_SKILL_UUID,
                            "skill_jump_bonus",
                            bonus,
                            AttributeModifier.Operation.ADDITION
                    ));

                    player.getCapability(PlayerSkillProvider.SKILL_DATA).ifPresent(data -> {
                        data.setJumpLevel(currentLevel + 1);
                    });

                    player.getPersistentData().putInt("skill_jump_level", currentLevel + 1);

                    player.sendSystemMessage(Component.translatable("skills.jump_level_increased", (currentLevel + 1)));

                    if (currentLevel + 1 == PlayerSkill.maxJumpLevel) {
                        MinecraftServer server = player.getServer();
                        if (server != null) {

                            Advancement adv = server.getAdvancements()
                                    .getAdvancement(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID,"skill_jump_advancement"));

                            if (adv != null) {
                                player.getAdvancements().award(adv, "unlock_skill");
                            }
                        }
                    }
                }
                else {
                    player.sendSystemMessage(Component.translatable("skills.jump_level_maxed", (currentLevel)).withStyle(ChatFormatting.YELLOW));
                }
            } else {
                player.sendSystemMessage(Component.translatable("skills.requierements_not_present").withStyle(ChatFormatting.RED));
            }

        });
        return true;
    }

}
