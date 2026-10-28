package net.snowteb.warriorcats_events.mixin;

import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.snowteb.warriorcats_events.clanplayerdata.WCEPlayerData;
import net.snowteb.warriorcats_events.clanplayerdata.WCEPlayerDataProvider;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.managers.MorphNameUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tocraft.walkers.api.PlayerShape;

@Mixin(value = ChatType.class, priority = 9999)
public class ChatTypeMorphNameMixin {

    @Inject(method = "bind(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/network/chat/ChatType$Bound;"
    , at = @At("HEAD"), cancellable = true)
    private static void redirectChatMessage(ResourceKey<ChatType> pChatTypeKey, Entity pEntity, CallbackInfoReturnable<ChatType.Bound> cir) {

        if (pEntity instanceof ServerPlayer player) {
            boolean shouldRewriteName = player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                    .map(WCEPlayerData::isMorphNameInChat).orElse(false);
            if (shouldRewriteName && PlayerShape.getCurrentShape(player) instanceof WCatEntity) {

                Component newName = MorphNameUtils.getChatMorphName(player);

                cir.setReturnValue(net.minecraft.network.chat.ChatType.bind(pChatTypeKey, pEntity.level().registryAccess(), newName));
                cir.cancel();
            }
        }
    }

}
