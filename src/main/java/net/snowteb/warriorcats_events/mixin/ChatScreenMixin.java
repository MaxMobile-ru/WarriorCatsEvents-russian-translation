package net.snowteb.warriorcats_events.mixin;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {

    @Shadow
    protected EditBox input;

    private String lastText = "";

    @Inject(method = "keyPressed", at = @At("TAIL"))
    public void handleChat(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {

        if (input != null) {
            String current = input.getValue();

            if (!current.equals(lastText)) {
                String text = replaceEmojis(current);

                if (!text.equals(current)) {
                    int cursor = input.getCursorPosition();

                    input.setValue(text);
                    input.setCursorPosition(Math.min(cursor, text.length()));
                }

                lastText = text;
            }
        }
    }

    private String replaceEmojis(String text) {
        text = text.replace(":sob:", "\uD83D\uDE2D");
        text = text.replace(":cry:", "\uD83D\uDE2D");
        text = text.replace(":sparkles:", "✨");
        text = text.replace(":sparkle:", "✨");
        text = text.replace(":cat:", "\uD83D\uDC08");
        text = text.replace(":cat2:", "\uD83D\uDC31");
        text = text.replace(":plead:", "\uD83E\uDD79");
        text = text.replace(":pleading:", "\uD83E\uDD79");
        text = text.replace(":wilted_rose:", "\uD83E\uDD40");
        text = text.replace(":heart:", "❤");
        text = text.replace(":fire:", "\uD83D\uDD25");
        text = text.replace(":flame:", "\uD83D\uDD25");
        text = text.replace(":smile:", "\uD83D\uDE3A");
        text = text.replace(":popcorn:", "\uD83C\uDF7F");
        text = text.replace(":eyes:", "\uD83D\uDC40");
        text = text.replace(":skull:", "\uD83D\uDC80");
        text = text.replace(":tada:", "\uD83C\uDF89");
        text = text.replace(":confetti:", "\uD83C\uDF89");
        text = text.replace(":knife:", "\uD83D\uDD2A");
        text = text.replace(":pensive:", "\uD83D\uDE14");
        text = text.replace(":salute:", "\uD83E\uDEE1");
        text = text.replace(":thumb_up:", "\uD83D\uDC4D");
        text = text.replace(":thumbs_up:", "\uD83D\uDC4D");
        text = text.replace(":thumbup:", "\uD83D\uDC4D");
        text = text.replace(":handshake:", "\uD83E\uDD1D");
        text = text.replace(":v:", "✌");

        return text;
    }

}
