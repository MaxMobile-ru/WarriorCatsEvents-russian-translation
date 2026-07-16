package net.snowteb.warriorcats_events.screen.screens;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.snowteb.warriorcats_events.flappycat.FlappyCatScreen;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.c2s.skilltree.ReqSkillDataPacket;
import net.snowteb.warriorcats_events.screen.screens.createmorph.CreateMorphScreen;
import net.snowteb.warriorcats_events.screen.widgets.ButtonScrollList;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

public class WCEOptionsScreen extends Screen {
    private ButtonScrollList list;
    private Button close;


    private float menuX;
    private final float targetX = 0;


    public WCEOptionsScreen() {
        super(Component.empty());
    }

    @Override
    protected void init() {
        list = new ButtonScrollList(minecraft, 120, height - 60, 20, this.height - 20, 25);

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        list.addButton(Component.translatable("screen.options.profile"), () -> {
            player.connection.sendCommand("wce info profile");
        }, Component.translatable("screen.options.profile.tip"));
        list.addButton(Component.translatable("screen.options.skill_tree"), () -> {
            Minecraft.getInstance().setScreen(new SkillScreen());
            ModPackets.sendToServer(new ReqSkillDataPacket());
        }, Component.translatable("screen.options.skill_tree.tip"));
        list.addButton(Component.translatable("screen.options.edit_morph"), () -> {
            Minecraft.getInstance().setScreen(new CreateMorphScreen(false));
        }, Component.translatable("screen.options.edit_morph.tip"));
        list.addButton(Component.translatable("screen.options.info_setup"), () -> {
            player.connection.sendCommand("wce info setup");
        }, Component.translatable("screen.options.info_setup.tip"));
        list.addButton(Component.translatable("screen.options.morph_pose"), () -> {
            player.connection.sendCommand("wce info morphPose");
        }, Component.translatable("screen.options.morph_pose.tip"));
        list.addButton(Component.translatable("screen.options.manage_clan"), () -> {
            player.connection.sendCommand("wce clan manage");
        }, Component.translatable("screen.options.manage_clan.tip"));
        list.addButton(Component.translatable("screen.options.territory_map"), () -> {
            player.connection.sendCommand("wce clan map");
        }, Component.translatable("screen.options.territory_map.tip"));
        list.addButton(Component.translatable("screen.options.my_clan"), () -> {
            player.connection.sendCommand("wce clan");
        }, Component.translatable("screen.options.my_clan.tip"));
        list.addButton(Component.translatable("screen.options.clan_list"), () -> {
            player.connection.sendCommand("wce clan list");
        }, Component.translatable("screen.options.clan_list.tip"));
        list.addButton(Component.translatable("screen.options.register_clan"), () -> {
            player.connection.sendCommand("wce clan register");
        }, Component.translatable("screen.options.register_clan.tip"));
        list.addButton(Component.translatable("screen.options.changelog"), () -> {
            Minecraft.getInstance().setScreen(new WCEChangelogScreen(this));
        }, Component.translatable("screen.options.changelog.tip"));
        list.addButton(Component.translatable("screen.options.config"), () -> {
            Minecraft.getInstance().setScreen(new WCEConfigScreen(this));
        }, Component.translatable("screen.options.config.tip"));
        list.addButton(Component.translatable("screen.options.flappycat"), () -> {
            Minecraft.getInstance().setScreen(new FlappyCatScreen(this));
        }, Component.translatable("screen.options.flappycat.tip"));


        list.setRenderSelection(false);
        list.setRenderTopAndBottom(true);
        list.setLeftPos(0);


        close = Button.builder(Component.translatable("screen.options.close"), button -> {
            this.onClose();
        }).bounds(this.width - 65, this.height - 25, 60, 20)
                .build();

        this.addRenderableWidget(close);
        this.addRenderableWidget(list);
        menuX = -500;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (menuX < targetX) {
            menuX += -(menuX) * 0.05f;
            if (menuX > targetX - 0.3) menuX = targetX;
        }

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(menuX, 0, 0);

        fillGradientHorizontal(pGuiGraphics,119,0, 125, this.height, 0, 0xbb000000, 0x00000000);

        pGuiGraphics.pose().pushPose();
        float scale = 1.1f;
        pGuiGraphics.pose().translate(10, 5, 0);
        pGuiGraphics.pose().scale(scale, scale, scale);
        pGuiGraphics.drawString(this.font, Component.translatable("key.warriorcats_events.options"), 0, 0, 0xFFaaaaaa);
        pGuiGraphics.pose().popPose();

        pGuiGraphics.enableScissor(0,0, (int) (120 + menuX),this.height);
        this.renderDirtBackground(pGuiGraphics);
        pGuiGraphics.fill(0,0,120,this.height, 0x33000000);
        pGuiGraphics.disableScissor();


        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        if (pMouseY > 20 && pMouseX < this.height - 20) {
            this.list.renderButtonTooltip(pGuiGraphics, pMouseX, pMouseY + 5);
        }

        pGuiGraphics.pose().popPose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public static void fillGradientHorizontal(GuiGraphics guiGraphics, int x1, int y1,
                                              int x2, int y2, int z,
                                              int colorLeft, int colorRight) {
        VertexConsumer consumer = guiGraphics.bufferSource()
                .getBuffer(RenderType.gui());

        Matrix4f matrix = guiGraphics.pose().last().pose();

        float a1 = (float) FastColor.ARGB32.alpha(colorLeft) / 255.0F;
        float r1 = (float) FastColor.ARGB32.red(colorLeft) / 255.0F;
        float g1 = (float) FastColor.ARGB32.green(colorLeft) / 255.0F;
        float b1 = (float) FastColor.ARGB32.blue(colorLeft) / 255.0F;

        float a2 = (float) FastColor.ARGB32.alpha(colorRight) / 255.0F;
        float r2 = (float) FastColor.ARGB32.red(colorRight) / 255.0F;
        float g2 = (float) FastColor.ARGB32.green(colorRight) / 255.0F;
        float b2 = (float) FastColor.ARGB32.blue(colorRight) / 255.0F;

        consumer.vertex(matrix, x1, y1, z).color(r1, g1, b1, a1).endVertex();
        consumer.vertex(matrix, x1, y2, z).color(r1, g1, b1, a1).endVertex();

        consumer.vertex(matrix, x2, y2, z).color(r2, g2, b2, a2).endVertex();
        consumer.vertex(matrix, x2, y1, z).color(r2, g2, b2, a2).endVertex();

        guiGraphics.flush();
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == GLFW.GLFW_KEY_E
                ||  pKeyCode == GLFW.GLFW_KEY_W
                || pKeyCode == GLFW.GLFW_KEY_A
                || pKeyCode == GLFW.GLFW_KEY_S
                || pKeyCode == GLFW.GLFW_KEY_D) {
            this.onClose();
            return true;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }
}
