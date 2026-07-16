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
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.attachments.CapabilityManager;
import net.snowteb.warriorcats_events.attachments.ModAttachments;
import net.snowteb.warriorcats_events.attachments.PlayerSkill;
import net.snowteb.warriorcats_events.util.ModAttributes;
import net.snowteb.warriorcats_events.zconfig.WCEServerConfig;

import static net.snowteb.warriorcats_events.attachments.PlayerSkill.JUMP_SKILL_UUID;

public class CtSMoreJumpPacket implements CustomPacketPayload {

    public CtSMoreJumpPacket() {

    }

    public CtSMoreJumpPacket(FriendlyByteBuf buf) {

    }

    public void toBytes(FriendlyByteBuf buf) {

    }


    public boolean handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();

            if (!WCEServerConfig.SERVER.SKILL_TREE_SERVER.get()) {
                player.sendSystemMessage(Component.translatable("generic.skill_tree_disabled").withStyle(ChatFormatting.RED));
                return;
            }

            int currentLevel = player.getData(ModAttachments.PLAYER_SKILL).getJumpLevel();

            int currentSpeedLevel = player.getData(ModAttachments.PLAYER_SKILL).getSpeedLevel();

            int currentHPLevel = player.getData(ModAttachments.PLAYER_SKILL).getHPLevel();

            int cost = PlayerSkill.getDefaultJumpCost() * (currentLevel + 1);
            int remaining = cost - player.totalExperience;

            if (currentSpeedLevel == PlayerSkill.maxSpeedLevel && currentHPLevel == PlayerSkill.maxHPLevel) {

            if (player.totalExperience < cost && currentLevel < PlayerSkill.maxJumpLevel) {
                player.sendSystemMessage(Component.translatable("generic.need_more_xp", remaining).withStyle(ChatFormatting.RED));
                return;
            }
                if (currentLevel < PlayerSkill.maxJumpLevel) {
                    player.giveExperiencePoints(-cost);
                    var attribute = player.getAttribute(ModAttributes.PLAYER_JUMP);
                    if (attribute == null) return;

                    attribute.removeModifier(JUMP_SKILL_UUID);
                    double bonus = (PlayerSkill.jumpMultiplier * WCEServerConfig.SERVER.SKILL_JUMP_MULTIPLIER.get()) * (currentLevel + 1);
                    attribute.addPermanentModifier(new AttributeModifier(
                            JUMP_SKILL_UUID,
                            bonus,
                            AttributeModifier.Operation.ADD_VALUE
                    ));

                    CapabilityManager.attachmentProvider(player,ModAttachments.PLAYER_SKILL, data -> {
                        data.setJumpLevel(currentLevel + 1);
                    });

                    player.getPersistentData().putInt("skill_jump_level", currentLevel + 1);

                    player.sendSystemMessage(Component.translatable("skills.jump_level_increased", (currentLevel + 1)));

                    if (currentLevel + 1 == PlayerSkill.maxJumpLevel) {
                        MinecraftServer server = player.getServer();
                        if (server != null) {

                            AdvancementHolder adv = server.getAdvancements()
                                    .get(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID,"skill_jump_advancement"));

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

    public static final Type<CtSMoreJumpPacket> TYPE
            = new Type<>(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "more_jump"));

    public static final StreamCodec<FriendlyByteBuf, CtSMoreJumpPacket> CODEC =
            StreamCodec.of(
                    (buf, pkt) -> pkt.toBytes(buf),
                    buf -> new CtSMoreJumpPacket(buf)
            );


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
