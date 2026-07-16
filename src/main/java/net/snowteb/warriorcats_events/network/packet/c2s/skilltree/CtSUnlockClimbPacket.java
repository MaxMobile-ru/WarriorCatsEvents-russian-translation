package net.snowteb.warriorcats_events.network.packet.c2s.skilltree;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.Advancement;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.s2c.skilltree.SyncSkillDataPacket;
import net.snowteb.warriorcats_events.skills.ISkillData;
import net.snowteb.warriorcats_events.skills.PlayerSkill;
import net.snowteb.warriorcats_events.skills.PlayerSkillProvider;
import net.snowteb.warriorcats_events.stealth.PlayerStealthProvider;
import net.snowteb.warriorcats_events.zconfig.WCEServerConfig;

import java.util.function.Supplier;

public class CtSUnlockClimbPacket {

    public CtSUnlockClimbPacket() {}

    public static void encode(CtSUnlockClimbPacket msg, FriendlyByteBuf buf) {}

    public static CtSUnlockClimbPacket decode(FriendlyByteBuf buf) {
        return new CtSUnlockClimbPacket();
    }

    public static void handle(CtSUnlockClimbPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();

            if (player == null) return;

            if (!WCEServerConfig.SERVER.SKILL_TREE_SERVER.get()) {
                player.sendSystemMessage(Component.translatable("generic.skill_tree_disabled").withStyle(ChatFormatting.RED));
                return;
            }

            int cost = PlayerSkill.getDefaultClimbCost();
            int remaining = cost - player.totalExperience;

            int currentSpeedLevel = player.getCapability(PlayerSkillProvider.SKILL_DATA)
                    .map(ISkillData::getSpeedLevel)
                    .orElse(player.getPersistentData().getInt("skill_speed_level"));
            int currentJumpLevel = player.getCapability(PlayerSkillProvider.SKILL_DATA)
                    .map(ISkillData::getJumpLevel)
                    .orElse(player.getPersistentData().getInt("skill_jump_level"));

            if (currentJumpLevel == PlayerSkill.maxJumpLevel && currentSpeedLevel == PlayerSkill.maxSpeedLevel) {
                player.getCapability(PlayerSkillProvider.SKILL_DATA).ifPresent(cap -> {

                    if (cap.isClimbUnlocked()) {
                        player.sendSystemMessage(Component.translatable("skills.climb_already_unlocked")
                                .withStyle(ChatFormatting.YELLOW));
                        return;
                    }

                    if (player.totalExperience < cost) {
                        player.sendSystemMessage(Component.translatable("generic.need_more_xp", remaining)
                                .withStyle(ChatFormatting.RED));
                        return;
                    }

                    player.giveExperiencePoints(-cost);

                    cap.setClimbUnlocked(true);
                    ModPackets.sendToPlayer(new SyncSkillDataPacket(cap.getSpeedLevel(), cap.getHPLevel(),
                            cap.getDMGLevel(), cap.getJumpLevel(), cap.getArmorLevel(), cap.isClimbUnlocked()), player);

                    MinecraftServer server = player.getServer();
                    if (server != null) {

                        Advancement adv = server.getAdvancements()
                                .getAdvancement(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID,"skill_climb_advancement"));

                        if (adv != null) {
                            player.getAdvancements().award(adv, "unlock_skill");
                        }
                    }


                    player.sendSystemMessage(Component.translatable("skills.climb_unlocked")
                            .withStyle(ChatFormatting.GREEN));


                });
            }

        });
        ctx.get().setPacketHandled(true);
    }

}
