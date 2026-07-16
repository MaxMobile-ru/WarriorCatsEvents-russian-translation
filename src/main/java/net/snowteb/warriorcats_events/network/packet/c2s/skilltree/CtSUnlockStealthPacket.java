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
import net.snowteb.warriorcats_events.attachments.PlayerSkill;
import net.snowteb.warriorcats_events.zconfig.WCEServerConfig;

public class CtSUnlockStealthPacket implements CustomPacketPayload {

    public CtSUnlockStealthPacket() {}

    public static void encode(CtSUnlockStealthPacket msg, FriendlyByteBuf buf) {}

    public static CtSUnlockStealthPacket decode(FriendlyByteBuf buf) {
        return new CtSUnlockStealthPacket();
    }

    public static void handle(CtSUnlockStealthPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) ctx.player();

            if (!WCEServerConfig.SERVER.SKILL_TREE_SERVER.get()) {
                player.sendSystemMessage(Component.translatable("generic.skill_tree_disabled").withStyle(ChatFormatting.RED));
                return;
            }

            int cost = PlayerSkill.getDefaultStealthCost();
            int remaining = cost - player.totalExperience;

            int currentArmorLevel = player.getData(ModAttachments.PLAYER_SKILL).getArmorLevel();
            int currentJumpLevel = player.getData(ModAttachments.PLAYER_SKILL).getJumpLevel();

            if (currentJumpLevel == PlayerSkill.maxJumpLevel && currentArmorLevel == PlayerSkill.maxArmorLevel) {
                CapabilityManager.attachmentProvider(player, ModAttachments.PLAYER_STEALTH, cap -> {
                    if (cap.isUnlocked()) {
                        player.sendSystemMessage(Component.translatable("skills.stealth_already_unlocked")
                                .withStyle(ChatFormatting.YELLOW));
                        return;
                    }

                    if (player.totalExperience < cost) {
                        player.sendSystemMessage(Component.translatable("generic.need_more_xp", remaining)
                                .withStyle(ChatFormatting.RED));
                        return;
                    }

                    player.giveExperiencePoints(-cost);

                    cap.setUnlocked(true);
                    cap.sync(player);

                    MinecraftServer server = player.getServer();
                    if (server != null) {

                        AdvancementHolder adv = server.getAdvancements()
                                .get(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID,"skill_stealth_advancement"));

                        if (adv != null) {
                            player.getAdvancements().award(adv, "unlock_skill");
                        }
                    }


                    player.sendSystemMessage(Component.translatable("skills.stealth_unlocked")
                            .withStyle(ChatFormatting.GREEN));

                });
            }

        });

    }

    public static final Type<CtSUnlockStealthPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "unlock_stealth"));

    public static final StreamCodec<FriendlyByteBuf, CtSUnlockStealthPacket> CODEC =
            StreamCodec.of(
                    (buf, pkt) ->encode(pkt, buf),
                    buf -> decode(buf)
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
