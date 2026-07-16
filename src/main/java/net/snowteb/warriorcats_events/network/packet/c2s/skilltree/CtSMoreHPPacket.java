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

import static net.snowteb.warriorcats_events.attachments.PlayerSkill.HP_SKILL_UUID;

public class CtSMoreHPPacket implements CustomPacketPayload {

    public CtSMoreHPPacket() {

    }

    public CtSMoreHPPacket(FriendlyByteBuf buf) {

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

            int currentLevel = player.getData(ModAttachments.PLAYER_SKILL).getHPLevel();
            int cost = PlayerSkill.getDefaultHPCost() * (currentLevel + 1);
            int remaining = cost - player.totalExperience;


            if (player.totalExperience < cost && currentLevel < PlayerSkill.maxHPLevel) {
                player.sendSystemMessage(Component.translatable("generic.need_more_xp", remaining).withStyle(ChatFormatting.RED));
                return;
            }

            if (currentLevel < PlayerSkill.maxHPLevel) {
                player.giveExperiencePoints(-cost);
                var attribute = player.getAttribute(Attributes.MAX_HEALTH);
                if (attribute == null) return;

                attribute.removeModifier(HP_SKILL_UUID);
                double bonus = (PlayerSkill.hpMultiplier* WCEServerConfig.SERVER.SKILL_HP_MULTIPLIER.get()) * (currentLevel + 1);
                attribute.addPermanentModifier(new AttributeModifier(
                        HP_SKILL_UUID,
                        bonus,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                ));

                CapabilityManager.attachmentProvider(player, ModAttachments.PLAYER_SKILL, data -> {
                    data.setHPLevel(currentLevel + 1);
                });

                player.getPersistentData().putInt("skill_hp_level", currentLevel + 1);

                player.sendSystemMessage(Component.translatable("skills.hp_level_increased", (currentLevel + 1)));

                if (currentLevel + 1 == PlayerSkill.maxHPLevel) {
                    MinecraftServer server = player.getServer();
                    if (server != null) {

                        AdvancementHolder adv = server.getAdvancements()
                                .get(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID,"skill_hp_advancement"));

                        if (adv != null) {
                            player.getAdvancements().award(adv, "unlock_skill");
                        }
                    }
                }
            } else {
                player.sendSystemMessage(Component.translatable("skills.hp_level_maxed", (currentLevel)).withStyle(ChatFormatting.YELLOW));
            }

        });
        return true;
    }

    public static final Type<CtSMoreHPPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "more_hp"));

    public static final StreamCodec<FriendlyByteBuf, CtSMoreHPPacket> CODEC =
            StreamCodec.of(
                    (buf, pkt) -> pkt.toBytes(buf),
                    buf -> new CtSMoreHPPacket(buf)
            );


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
