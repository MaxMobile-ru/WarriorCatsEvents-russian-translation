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
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.attachments.CapabilityManager;
import net.snowteb.warriorcats_events.attachments.ModAttachments;
import net.snowteb.warriorcats_events.attachments.PlayerSkill;
import net.snowteb.warriorcats_events.zconfig.WCEServerConfig;

import static net.snowteb.warriorcats_events.attachments.PlayerSkill.DMG_SKILL_UUID;

public class CtSMoreDMGPacket implements CustomPacketPayload {

    public CtSMoreDMGPacket() {

    }

    public CtSMoreDMGPacket(FriendlyByteBuf buf) {

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

            int currentLevel = player.getData(ModAttachments.PLAYER_SKILL).getDMGLevel();

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
                        bonus,
                        AttributeModifier.Operation.ADD_VALUE
                ));

                CapabilityManager.attachmentProvider(player, ModAttachments.PLAYER_SKILL, data -> {
                    data.setDMGLevel(currentLevel + 1);
                });

                player.getPersistentData().putInt("skill_dmg_level", currentLevel + 1);

                player.sendSystemMessage(Component.translatable("skills.claws_level_increased", (currentLevel + 1)));

                if (currentLevel + 1 == PlayerSkill.maxDMGLevel) {
                    MinecraftServer server = player.getServer();
                    if (server != null) {

                        AdvancementHolder adv = server.getAdvancements()
                                .get(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID,"skill_damage_advancement"));

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

    public static final Type<CtSMoreDMGPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "more_dmg"));

    public static final StreamCodec<FriendlyByteBuf, CtSMoreDMGPacket> CODEC =
            StreamCodec.of(
                    (buf, pkt) -> pkt.toBytes(buf),
                    buf -> new CtSMoreDMGPacket(buf)
            );


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
