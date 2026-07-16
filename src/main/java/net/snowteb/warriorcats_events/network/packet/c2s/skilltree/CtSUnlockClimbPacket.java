package net.snowteb.warriorcats_events.network.packet.c2s.skilltree;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.attachments.CapabilityManager;
import net.snowteb.warriorcats_events.attachments.ModAttachments;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.s2c.skilltree.SyncSkillDataPacket;
import net.snowteb.warriorcats_events.attachments.PlayerSkill;
import net.snowteb.warriorcats_events.zconfig.WCEServerConfig;

public class CtSUnlockClimbPacket implements CustomPacketPayload {

    public CtSUnlockClimbPacket() {}

    public static void encode(CtSUnlockClimbPacket msg, FriendlyByteBuf buf) {}

    public static CtSUnlockClimbPacket decode(FriendlyByteBuf buf) {
        return new CtSUnlockClimbPacket();
    }

    public static void handle(CtSUnlockClimbPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) ctx.player();

            if (!WCEServerConfig.SERVER.SKILL_TREE_SERVER.get()) {
                player.sendSystemMessage(Component.translatable("generic.skill_tree_disabled").withStyle(ChatFormatting.RED));
                return;
            }

            int cost = PlayerSkill.getDefaultClimbCost();
            int remaining = cost - player.totalExperience;

            int currentSpeedLevel = player.getData(ModAttachments.PLAYER_SKILL).getSpeedLevel();
            int currentJumpLevel = player.getData(ModAttachments.PLAYER_SKILL).getJumpLevel();

            if (currentJumpLevel == PlayerSkill.maxJumpLevel && currentSpeedLevel == PlayerSkill.maxSpeedLevel) {
                CapabilityManager.attachmentProvider(player, ModAttachments.PLAYER_SKILL, cap -> {
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

                        AdvancementHolder adv = server.getAdvancements()
                                .get(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID,"skill_climb_advancement"));

                        if (adv != null) {
                            player.getAdvancements().award(adv, "unlock_skill");
                        }
                    }


                    player.sendSystemMessage(Component.translatable("skills.climb_unlocked")
                            .withStyle(ChatFormatting.GREEN));


                });
            }

        });
    }


    public static final Type<CtSUnlockClimbPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "unlock_climb"));

    public static final StreamCodec<FriendlyByteBuf, CtSUnlockClimbPacket> CODEC =
            StreamCodec.of(
                    (buf, pkt) -> encode(pkt, buf),
                    buf -> decode(buf)
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
