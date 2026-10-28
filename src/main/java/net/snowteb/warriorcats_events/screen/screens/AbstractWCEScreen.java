package net.snowteb.warriorcats_events.screen.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.snowteb.warriorcats_events.screen.screens.createmorph.SubRenderable;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AbstractWCEScreen extends Screen {

    protected int displayErrorTime = 0;
    private Component displayErrorText = Component.empty();
    private boolean isAnError = false;

    @Nullable
    private EnqueuedOperation enqueuedOperation = null;

    protected AbstractWCEScreen() {
        super(Component.empty());
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderError(pGuiGraphics);
    }

    public void displayMessage(Component msg, boolean isAnError, int time) {
        displayErrorTime = time;
        displayErrorText = msg;
        this.isAnError = isAnError;
    }

    public void displayMessage(Component msg, boolean isAnError) {
        this.displayMessage(msg, isAnError, 140);
    }

    private void renderError(GuiGraphics pGuiGraphics) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        if (displayErrorTime > 0) {
            pGuiGraphics.pose().pushPose();

            float scale = getErrorScale();

            List<FormattedCharSequence> lines = font.split(displayErrorText, (int) (140 * (1/scale)));

            int textWidth = 0;

            for (FormattedCharSequence line : lines) {
                if (font.width(line) > textWidth) textWidth = font.width(line);
            }

            int fillOffset = 6;

            int fillx0 = (-textWidth / 2 - fillOffset);
            int fillx1 = (textWidth / 2 + fillOffset);
            int filly0 = (-fillOffset / 2) - (isAnError ? 12 : 0);
            int filly1 = (9 * lines.size() + fillOffset / 2);

            float xTrans = centerX + getErrorXOffset();
            float yTrans = centerY - (isAnError ? 0 : 10) + getErrorYOffset();

            pGuiGraphics.pose().translate(xTrans, yTrans, 5000);
            pGuiGraphics.pose().scale(scale, scale, scale);

            int color = isAnError ? 0xFFEB7C7C : 0xFFFFFFA0;

            float alpha = (float) displayErrorTime / 20;

            float finalAlpha = Mth.clamp(alpha, 0f, 1f);

            pGuiGraphics.setColor(1f,1f,1f, finalAlpha);
            {

                int shadowOffset = -4;

                pGuiGraphics.fill(fillx0 - 1 + shadowOffset, filly0 + shadowOffset,
                        fillx1 + 1 + shadowOffset, filly1 + shadowOffset + 1, 0x44000000);


                pGuiGraphics.fill(fillx0 - 1, filly0, fillx1 + 1, filly1, 0xFF29241D);
                pGuiGraphics.fill(fillx0, filly0 - 1, fillx1, filly1 + 1, 0xFF29241D);
                pGuiGraphics.fillGradient(fillx0, filly0, fillx1, filly1, 0xFFD4A068,  0xFFFFD4A8);
                pGuiGraphics.fill(fillx0 + 1, filly0 + 1, fillx1 - 1, filly1 - 1, 0xFF29241D);


                int y = 1;

                if (isAnError) {
                    pGuiGraphics.drawCenteredString(font, "⚠", 0, y - 11, 0xFFFFCB5C);
                }
                for (FormattedCharSequence line : lines) {
                    pGuiGraphics.drawCenteredString(font, line, 0,  y, color);
                    y += 9;
                }

            }
            pGuiGraphics.setColor(1f,1f,1f,1f);

            pGuiGraphics.pose().popPose();
        }
    }

    @Override
    public void tick() {
        if (this.enqueuedOperation != null) {
            enqueuedOperation.tick();
            if (this.enqueuedOperation.isFinished()) {
                this.enqueuedOperation = null;
            }
        }

        for (Renderable renderable : this.renderables) {
            if (renderable instanceof SubRenderable sub) {
                sub.tickkkk();
            }
        }

        if (displayErrorTime > 0) displayErrorTime--;
    }

    private static class EnqueuedOperation {
        private int tickTime;
        private Runnable runnable;

        public EnqueuedOperation(Runnable runnable) {
            this(20, runnable);
        }

        public EnqueuedOperation(int tickTime, Runnable runnable) {
            this.tickTime = tickTime;
            this.runnable = runnable;
        }

        public void tick() {
            if (this.tickTime > 0) {
                tickTime--;
                return;
            }

            if (this.runnable != null) {
                runnable.run();
                this.runnable = null;
            }
        }

        public boolean isFinished() {
            return this.tickTime <= 0 && this.runnable == null;
        }
    }

    public abstract int getErrorXOffset();
    public abstract int getErrorYOffset();
    public abstract float getErrorScale();

    public void enqueue(Runnable runnable) {
        this.displayMessage(Component.translatable("screen.clan.sending"), false);
        this.enqueuedOperation = new EnqueuedOperation(runnable);
    }
}
