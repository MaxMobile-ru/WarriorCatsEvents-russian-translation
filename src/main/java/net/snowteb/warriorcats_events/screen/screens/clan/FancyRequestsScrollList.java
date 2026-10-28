package net.snowteb.warriorcats_events.screen.screens.clan;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FormattedCharSequence;
import net.snowteb.warriorcats_events.WCEClient;
import net.snowteb.warriorcats_events.clan.ClanObjects;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.c2s.clan.ManageJoinRequestPacket;
import net.snowteb.warriorcats_events.screen.screens.createmorph.FancySimpleButton;
import net.snowteb.warriorcats_events.screen.screens.createmorph.SubRenderable;
import net.snowteb.warriorcats_events.sound.ModSounds;

import java.util.List;
import java.util.function.BiPredicate;

public class FancyRequestsScrollList extends AbstractSelectionList<FancyRequestsScrollList.RequestEntry>  implements SubRenderable {

    final int originalYPos0;
    final int originalXPos0;
    final int originalYPos1;
    final int originalXPos1;
    private BiPredicate<Double, Double> isClickableIn;

    public FancyRequestsScrollList(Minecraft mc, int width, int height, int top, int bottom, int itemHeight) {
        super(mc, width, height, top, bottom, itemHeight);
        originalYPos0 = top;
        originalYPos1 = bottom;
        originalXPos0 = 0;
        originalXPos1 = width;
    }

    public void setRequests(List<ClanObjects.JoinRequest> list) {
        for (ClanObjects.JoinRequest joinRequest : list) {
            this.addEntry(new RequestEntry(joinRequest));
        }
    }

    @Override
    protected void renderSelection(GuiGraphics pGuiGraphics, int pTop, int pWidth, int pHeight, int pOuterColor, int pInnerColor) {
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        pGuiGraphics.fill(this.x0, this.y0,
                this.x0 + width, this.y1,
                0xFF292929);

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        pGuiGraphics.setColor(0.25F, 0.25F, 0.25F, 1.0F);
        pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

        int color = 0x990D0C08;

        pGuiGraphics.fillGradient(RenderType.guiOverlay(), this.x0, this.y0, this.x1, this.y0 + 6, color, 0, 0);
        pGuiGraphics.fillGradient(RenderType.guiOverlay(), this.x0, this.y1 - 6, this.x1, this.y1, 0, color, 0);

        pGuiGraphics.renderOutline(this.x0, this.y0 - 1,
                this.width, this.y1 - this.y0 + 2,
                0xFF59533B);

        if (this.children().isEmpty()) {
            pGuiGraphics.drawCenteredString(
                    Minecraft.getInstance().font,
                    Component.translatable("screen.clan.empty_requests")
                            .withStyle(ChatFormatting.DARK_GRAY).withStyle(ChatFormatting.ITALIC),
                    this.getLeft() + this.getWidth()/2,
                    this.getTop() + this.getHeight()/2,
                    0xFFFFFFFF
                    );
        }
    }

    @Override
    protected int getScrollbarPosition() {
        return this.getRight();
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (this.isClickableIn() != null) {
            if (!this.isClickableIn().test(pMouseX, pMouseY)) {
                return false;
            }
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        if (this.isClickableIn() != null) {
            if (!this.isClickableIn().test(pMouseX, pMouseY)) {
                return false;
            }
        }
        return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }

    @Override
    public boolean isMouseOver(double pMouseX, double pMouseY) {
        if (this.isClickableIn() != null) {
            if (!this.isClickableIn().test(pMouseX, pMouseY)) {
                return false;
            }
        }
        return super.isMouseOver(pMouseX, pMouseY);
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
    }

    @Override
    public int getRowWidth() {
        return this.width;
    }

    @Override
    public void adjustYPos(int yOffset) {
        this.y0 = getOriginalYPos0() + yOffset;
        this.y1 = getOriginalYPos1() + yOffset;
    }

    @Override
    public int getOriginalYPos0() {
        return originalYPos0;
    }

    @Override
    public int getOriginalYPos1() {
        return originalYPos1;
    }

    @Override
    public void setClickableIn(BiPredicate<Double, Double> predicate) {
        isClickableIn = predicate;
    }

    @Override
    public void adjustXPos(int xOffset) {
        this.setLeftPos(this.originalXPos0 + xOffset);
    }

    @Override
    public int getOriginalXPos0() {
        return originalXPos0;
    }

    @Override
    public int getOriginalXPos1() {
        return originalXPos1;
    }

    @Override
    public BiPredicate<Double, Double> isClickableIn() {
        return isClickableIn;
    }

    public class RequestEntry extends Entry<RequestEntry> {

        private final ClanObjects.JoinRequest request;
        private final FancySimpleButton acceptButton;
        private final FancySimpleButton denyButton;

        public RequestEntry(ClanObjects.JoinRequest request) {
           this.request = request;

            int buttonWidth = 40;
            int buttonHeight = 12;

            FancyRequestsScrollList thisList = FancyRequestsScrollList.this;

            acceptButton = new FancySimpleButton(
                    buttonWidth, buttonHeight,
                    0,0,
                    Component.empty()
                            .append("✔ ")
                            .append(Component.translatable("mco.invites.button.accept")),
                    but -> {
                        ModPackets.sendToServer(new ManageJoinRequestPacket(this.request, "accept"));

                        thisList.removeEntry(this);
                        thisList.setScrollAmount(Math.min(thisList.getScrollAmount(), thisList.getMaxScroll()));
                    },
                    0.8f, true
            );

            denyButton = new FancySimpleButton(
                    buttonWidth, buttonHeight,
                    0,0,
                    Component.empty()
                            .append("❌ ")
                            .append(Component.translatable("mco.invites.button.reject")),
                    but -> {
                        ModPackets.sendToServer(new ManageJoinRequestPacket(this.request, "deny"));

                        thisList.removeEntry(this);
                        thisList.setScrollAmount(Math.min(thisList.getScrollAmount(), thisList.getMaxScroll()));
                    },
                    0.8f, true
            );


        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            FancyRequestsScrollList.this.setSelected(this);
            WCEClient.playLocalSound(ModSounds.MENU_CLICK.get(), SoundSource.NEUTRAL, 0.1f,1.3f);

            if (acceptButton.isHovered()) {
                if (acceptButton.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
            }

            if (denyButton.isHovered()) {
                if (denyButton.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
            }

            return true;
        }

        @Override
        public void render(GuiGraphics pGuiGraphics, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pHovering, float pPartialTick) {
            boolean selected = FancyRequestsScrollList.this.getSelected() == this;


            int bgColor = selected ? 0x33FFFFFF : (pHovering ? 0x22FFFFFF : 0x11FFFFFF);

            int left = pLeft + 2;
            int top = pTop;
            int right = pLeft + pWidth - 5;
            int bottom = pTop + pHeight;

            pGuiGraphics.fill(left, top, right, bottom, bgColor);

            pGuiGraphics.renderOutline(left, top, right - left, bottom - top, bgColor);


            int color = selected ? 0xFFFFDE57 : (pHovering ? 0xFFFFF8DE : 0xFFF2DCAE);

            {
                int textStartX = left + 5;
                int textMaxX = right - 95;
                int maxWidth = textMaxX - textStartX;

                pGuiGraphics.pose().pushPose();
                pGuiGraphics.pose().translate(textStartX, top + 4,0);
                float textScale = 0.7f;
                pGuiGraphics.pose().scale(textScale, textScale, textScale);

                List<FormattedCharSequence> lines =
                        Minecraft.getInstance().font.split(this.request.getGUIMessage(), (int) (maxWidth/textScale));

                int subY = 0;
                for (FormattedCharSequence line : lines) {
                    pGuiGraphics.drawString(
                            Minecraft.getInstance().font,
                            line,
                            0,
                            subY,
                            color
                    );
                    subY += 9;
                }

                subY+= 9;

                String reqMsg = this.request.getRequesterMessage().isEmpty() ? "..." : this.request.getRequesterMessage();
                Component requesterMessage = Component.literal("\"" + reqMsg + "\"")
                        .withStyle(ChatFormatting.ITALIC);

                List<FormattedCharSequence> msgLines
                        = Minecraft.getInstance().font.split(requesterMessage, (int) (maxWidth/textScale));

                for  (FormattedCharSequence line : msgLines) {
                    pGuiGraphics.drawString(
                            Minecraft.getInstance().font,
                            line,
                            0,
                            subY,
                            0xFFFFFFFF
                    );
                    subY += 9;
                }

                pGuiGraphics.pose().popPose();
            }

            acceptButton.setX(right - 45);
            acceptButton.setY(top + 5);
            acceptButton.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

            denyButton.setX(right - 90);
            denyButton.setY(top + 5);
            denyButton.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);


        }
    }

}


