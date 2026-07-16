package net.snowteb.warriorcats_events.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.snowteb.warriorcats_events.attachments.ModAttachments;
import net.snowteb.warriorcats_events.clan.ClanData;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.util.CuteTextUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tocraft.walkers.api.PlayerShape;

import java.util.UUID;

@Mixin(value = ChatType.class, priority = 9999)
public class ChatTypeMorphNameMixin {

    @Inject(method = "bind(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/network/chat/ChatType$Bound;"
    , at = @At("HEAD"), cancellable = true)
    private static void redirectChatMessage(ResourceKey<ChatType> pChatTypeKey, Entity pEntity, CallbackInfoReturnable<ChatType.Bound> cir) {

        if (pEntity instanceof ServerPlayer player) {
            boolean shouldRewriteName = player.getData(ModAttachments.PLAYER_WCE_DATA).isMorphNameInChat();
            if (shouldRewriteName && PlayerShape.getCurrentShape(player) instanceof WCatEntity){
                String morphName = player.getData(ModAttachments.PLAYER_WCE_DATA).getMorphName();

                UUID clanUUID = player.getData(ModAttachments.PLAYER_WCE_DATA).getCurrentClanUUID();

                String age = String.valueOf(player.getData(ModAttachments.PLAYER_WCE_DATA).getMorphAge());

                boolean useFancyFont = player.getData(ModAttachments.PLAYER_WCE_DATA).isUsingFancyFont();

                ClanData data = ClanData.get(player.getServer().overworld());
                ClanData.Clan clan = data.getClan(clanUUID);
                int color = 0xFFFFFF;
                String clanName = "No clan";
                String rank = "No rank";


                if (clan != null) {
                    color = clan.color;
                    clanName = clan.name;
                    rank = String.valueOf(clan.members.get(player.getUUID()));
                }

                Component finalMorphName1 = Component.empty()
                        .append(Component.literal("").withStyle(Style.EMPTY.withColor(color)))
                        .append(Component.literal(morphName).withStyle(Style.EMPTY.withColor(color)))
                        .append(Component.literal(" ").withStyle(Style.EMPTY.withColor(color)));

                Component hoverText = Component.empty()
                        .append(Component.literal(morphName).withStyle(ChatFormatting.GOLD))
                        .append(Component.literal("\n"))
                        .append(Component.literal("From: "))
                        .append(Component.literal(clanName).withStyle(Style.EMPTY.withColor(color)))
                        .append(Component.literal("\n"))
                        .append(Component.literal("Rank: "))
                        .append(Component.literal(rank))
                        .append(Component.literal("\n"))
                        .append(Component.literal("Age: "))
                        .append(Component.literal(age));

                Component finalMorphName2 = finalMorphName1.copy().withStyle(style ->
                        style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText))
                );


                int grayColor = 0x888888;

                Component newName = Component.empty()
                        .append(finalMorphName2.copy())
                        .append(Component.literal("").withStyle(Style.EMPTY.withColor(grayColor)))
                        .append(Component.literal("| ").withStyle(ChatFormatting.WHITE))
                        .append(CuteTextUtils.reformatToSmallText(pEntity, useFancyFont).copy().withStyle(Style.EMPTY.withColor(grayColor)))
                        .append(Component.literal("").withStyle(Style.EMPTY.withColor(grayColor)));

                cir.setReturnValue(net.minecraft.network.chat.ChatType.bind(pChatTypeKey, pEntity.level().registryAccess(), newName));
                cir.cancel();
            }
        }
    }

}
