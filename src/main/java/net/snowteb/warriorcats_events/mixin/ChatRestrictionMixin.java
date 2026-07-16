package net.snowteb.warriorcats_events.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.ChatVisiblity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class ChatRestrictionMixin {

    @Final
    @Shadow
    private boolean allowsChat;

    @Inject(method = "getChatStatus", at = @At("HEAD"), cancellable = true)
    public void modifyChatStatus(CallbackInfoReturnable<Minecraft.ChatStatus> cir) {
        Minecraft mc = (Minecraft)(Object)this;

        if (mc.options.chatVisibility().get() == ChatVisiblity.HIDDEN) {
            cir.setReturnValue(Minecraft.ChatStatus.DISABLED_BY_OPTIONS);
        } else if (!allowsChat) {
            cir.setReturnValue(Minecraft.ChatStatus.DISABLED_BY_LAUNCHER);
        } else {
            cir.setReturnValue(Minecraft.ChatStatus.ENABLED);
        }
    }

}
