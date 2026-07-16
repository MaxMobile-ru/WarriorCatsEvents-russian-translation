package net.snowteb.warriorcats_events.screen.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.c2s.others.RenameNametagPacket;

public class RenameTagMenu extends Screen {

    private final ItemStack item;
    private String name;
    private final InteractionHand hand;

    public RenameTagMenu(ItemStack item, InteractionHand pUsedHand) {
        super(Component.empty());
        this.item = item;
        this.hand = pUsedHand;
    }

    @Override
    protected void init() {
        int centerX = width / 2;
        int centerY = height / 2;

        EditBox text = new EditBox(this.font, centerX - 60, centerY + 10, 120, 20, Component.empty());
        text.setCanLoseFocus(false);
        text.setHint(Component.translatable("screen.setup.name_hint"));
        text.setTextColor(-1);
        text.setTextColorUneditable(-1);
        text.setMaxLength(50);
        text.setResponder(s -> this.name = s);


        Button save = Button.builder(Component.translatable("screen.button.done"), b -> {
            ModPackets.sendToServer(new RenameNametagPacket(text.getValue(), hand));
            this.onClose();
        }).bounds(centerX -25, centerY + 40, 50, 20).build();

        this.addRenderableWidget(text);
        this.addRenderableWidget(save);
        this.setInitialFocus(text);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics);

        int centerX = width / 2;
        int centerY = height / 2;

        String raw = Component.translatable("item.warriorcats_events.warrior_nametag.tooltip").getString();

        String[] lines = raw.split("\\\\n");

        int y = 0;
        for (String line : lines) {
            pGuiGraphics.drawCenteredString(this.font, line,
                    centerX, centerY - 25 + y, 0xFF888888);
            y += 9;
        }

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(centerX, centerY - 35, 0);
        float scale = 4f;
        pGuiGraphics.pose().scale(scale, scale, scale);

        pGuiGraphics.renderItem(item, -6,-15);

        pGuiGraphics.pose().popPose();

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }
}
