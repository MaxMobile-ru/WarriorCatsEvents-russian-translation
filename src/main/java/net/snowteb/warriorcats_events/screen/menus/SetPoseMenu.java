package net.snowteb.warriorcats_events.screen.menus;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.c2s.clan.SetPosePacket;

public class SetPoseMenu extends Screen {

    private Button setPose0;
    private Button setPose1;
    private Button setPose2;
    private Button setPose3;
    private Button setPose4Limp;
    private Button close;

    public SetPoseMenu() {
        super(Component.empty());
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;


        int y = 20;
        int constant = 30;

        setPose0 = Button.builder(
                Component.literal("No pose"),
                btn -> {
                    setPose(0);
                }
        ).bounds(10, y, 80, 18).build();

        y += constant;

        setPose1 = Button.builder(
                Component.literal("Gracious"),
                btn -> {
                    setPose(1);
                }
        ).bounds(10, y, 80, 18).build();

        y += constant;

        setPose2 = Button.builder(
                Component.literal("Dominant"),
                btn -> {
                    setPose(2);
                }
        ).bounds(10, y, 80, 18).build();

        y += constant;

        setPose3 = Button.builder(
                Component.literal("Refined"),
                btn -> {
                    setPose(3);
                }
        ).bounds(10, y, 80, 18).build();

        y += constant;

        setPose4Limp = Button.builder(
                Component.literal("Limping"),
                btn -> {
                    setPose(4);
                }
        ).bounds(10, y, 80, 18).build();


        close = Button.builder(
                Component.literal("Close"),
                btn -> {
                    onClose();
                }
        ).bounds(this.width - 90, this.height - 38, 80, 18).build();

        this.addRenderableWidget(setPose0);
        this.addRenderableWidget(setPose1);
        this.addRenderableWidget(setPose2);
        this.addRenderableWidget(setPose3);
        this.addRenderableWidget(setPose4Limp);
        this.addRenderableWidget(close);

        super.init();
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        Minecraft.getInstance().setScreen(null);
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    private void setPose(int i) {
        ModPackets.sendToServer(new SetPosePacket(i));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
