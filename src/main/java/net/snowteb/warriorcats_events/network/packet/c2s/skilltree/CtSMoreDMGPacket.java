package net.snowteb.warriorcats_events.network.packet.c2s.skilltree;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.Advancement;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.skills.ISkillData;
import net.snowteb.warriorcats_events.skills.PlayerSkill;
import net.snowteb.warriorcats_events.skills.PlayerSkillProvider;
import net.snowteb.warriorcats_events.zconfig.WCEServerConfig;

import java.util.function.Supplier;

import static net.snowteb.warriorcats_events.skills.PlayerSkill.DMG_SKILL_UUID;

public class CtSMoreDMGPacket {

    public CtSMoreDMGPacket() {

    }

    public CtSMoreDMGPacket(FriendlyByteBuf buf) {

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
                    .map(ISkillData::getDMGLevel)
                    .orElse(player.getPersistentData().getInt("skill_dmg_level"));

            int cost = PlayerSkill.getDefaultDMGCost() * (currentLevel + 1);
            int remaining = cost - player.totalExperience;



            if (player.totalExperience < cost && currentLevel < PlayerSkill.maxDMGLevel) {
                player.sendSystemMessage(Component.translatable("generic.need_more_xp", remaining).withStyle(ChatFormatting.RED));
                return;
            }

            if (currentLevel < PlayerSkill.maxDMGLevel) {
                player.giveExperiencePoints(-cost);
                var attribute = player.getAttribute(Attributes.ATTACK_DAMAGE);
                if (attribute == null) return;

                attribute.removeModifier(DMG_SKILL_UUID);
                double bonus = (PlayerSkill.damageMultiplier* WCEServerConfig.SERVER.SKILL_DMG_MULTIPLIER.get()) * (currentLevel + 1);
                attribute.addPermanentModifier(new AttributeModifier(
                        DMG_SKILL_UUID,
                        "skill_dmg_bonus",
                        bonus,
                        AttributeModifier.Operation.ADDITION
                ));

                player.getCapability(PlayerSkillProvider.SKILL_DATA).ifPresent(data -> {
                    data.setDMGLevel(currentLevel + 1);
                });

                player.getPersistentData().putInt("skill_dmg_level", currentLevel + 1);

                player.sendSystemMessage(Component.translatable("skills.claws_level_increased", (currentLevel + 1)));

                if (currentLevel + 1 == PlayerSkill.maxDMGLevel) {
                    MinecraftServer server = player.getServer();
                    if (server != null) {

                        Advancement adv = server.getAdvancements()
                                .getAdvancement(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID,"skill_damage_advancement"));

                        if (adv != null) {
                            player.getAdvancements().award(adv, "unlock_skill");
                        }
                    }
                }
            } else {
                player.sendSystemMessage(Component.translatable("skills.claws_level_maxed", (currentLevel)).withStyle(ChatFormatting.YELLOW));
            }

        });
        return true;
    }

}
