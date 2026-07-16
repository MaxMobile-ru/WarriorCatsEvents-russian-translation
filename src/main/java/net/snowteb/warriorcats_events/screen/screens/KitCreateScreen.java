package net.snowteb.warriorcats_events.screen.screens;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.c2s.cats.CtSNameKitPacket;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class KitCreateScreen extends Screen {
    private int textCooldown = 0;

    private EditBox kitPrefixBox;

    //    private VariantScrollList variantScrollList;

    private final WCatEntity kitten;


    public KitCreateScreen(WCatEntity kitten) {
        super(Component.literal("Kit"));
        this.kitten = kitten;
    }

    private static final ResourceLocation BANNER =
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/clan_setup/banner.png");


    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {

        this.renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        int centerx = (this.width) / 2;
        int centery = (this.height) / 2;

        boolean prefixToolTip = pMouseX >= centerx - 220 && pMouseY >= centery - 40
                && pMouseX <= centerx - 130 && pMouseY <= centery - 20;

        if (prefixToolTip) {
            pGuiGraphics.renderTooltip(Minecraft.getInstance().font,
                    Component.translatable("screen.kitcreate.prefix_tip",
                            Component.literal("'Bengal'").withStyle(ChatFormatting.YELLOW)
                                    .append(Component.literal("kit").withStyle(ChatFormatting.GRAY))
                    ).withStyle(ChatFormatting.GRAY)
                    ,pMouseX, pMouseY);
        }

        String kitPrefix = kitPrefixBox.getValue().trim();

        String morphNameShow = "...";

        if (!kitPrefix.isEmpty()) {
            morphNameShow = kitPrefix + "kit";
        }



        pGuiGraphics.drawCenteredString(Minecraft.getInstance().font,
                morphNameShow,
                centerx, centery + 10, 0xFFFFFFFF);

        if (textCooldown > 0) {
            pGuiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("screen.kitcreate.fields_empty"),
                    centerx - 55, centery + 75, 0xFFFF0000);
        }


        for (Renderable renderable : this.renderables) {
            renderable.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        }



        kitten.setOnGround(true);
        kitten.setYRot(0);
        kitten.yHeadRot = 0;
        kitten.yBodyRot = 0;

        pGuiGraphics.pose().pushPose();

        pGuiGraphics.pose().translate(centerx, centery - 10, 0);

        float scale = 3.4f;

        pGuiGraphics.pose().scale(scale, scale, scale);

        Quaternionf rotation = new Quaternionf(0.0F, 0.0F, 0.0F, 0.0F);
        Quaternionf pose = new Quaternionf(0.8F, 0.0F, 0.3F, 0.0F);

        InventoryScreen.renderEntityInInventory(
                pGuiGraphics,
                0,
                0,
                48,
                new Vector3f(0,0,0),
                pose,
                rotation,
                kitten
        );

        pGuiGraphics.pose().popPose();


    }

    @Override
    public void tick() {

        if (textCooldown > 0) {
            textCooldown--;
        }

        super.tick();
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        kitPrefixBox = new EditBox(
                this.font,
                centerX-45, centerY + 28,
                90, 20,
                Component.translatable("screen.setup.prefix")
        );
        kitPrefixBox.setMaxLength(13);
        kitPrefixBox.setHint(Component.translatable("screen.setup.prefix_hint"));

        this.addRenderableWidget(kitPrefixBox);

        Button saveButton = Button.builder(
                Component.translatable("screen.kitcreate.done"),
                btn -> onSave()
        ).bounds(centerX - 40, centerY + 85, 80, 20).build();

        this.addRenderableWidget(saveButton);

        Button setRandomPrefix = Button.builder(
                Component.translatable("screen.setup.random_prefix"),
                btn -> {

                    int value = Minecraft.getInstance().player.getRandom().nextInt(WCatEntity.PREFIXES.length);
                    String prefix = WCatEntity.PREFIXES[value];
                    kitPrefixBox.setValue(prefix);
                }
        ).bounds(centerX - 40, centerY + 53, 80, 15).build();

        this.addRenderableWidget(setRandomPrefix);
    }

    private void onSave() {
        String kitPrefix = kitPrefixBox.getValue().trim();

        if (kitPrefix.isEmpty()) {
            textCooldown = 100;
            return;
        }


        String prefix = kitPrefixBox.getValue().trim();

        this.minecraft.setScreen(null);

        ModPackets.sendToServer(new CtSNameKitPacket(prefix, kitten.getId()));

    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            Minecraft.getInstance().setScreen(null);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

}