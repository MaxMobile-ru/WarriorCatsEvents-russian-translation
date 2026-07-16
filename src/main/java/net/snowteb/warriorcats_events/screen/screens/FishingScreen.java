package net.snowteb.warriorcats_events.screen.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.snowteb.warriorcats_events.zconfig.WCEClientConfig;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.c2s.others.CtSFishFailed;
import net.snowteb.warriorcats_events.network.packet.c2s.others.CtSFishSuccesful;
import org.lwjgl.glfw.GLFW;

public class FishingScreen extends Screen {
    private static final ResourceLocation TEXTUREBACKGROUND =
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/fishing/fishing_gui_1.png");
    private static final ResourceLocation TEXTUREWAVES =
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/fishing/fishing_gui_2.png");

    private static final ResourceLocation TEXTUREFISH_1 =
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/fishing/fish_1_gui.png");
    private static final ResourceLocation TEXTUREFISH_2 =
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/fishing/fish_2_gui.png");
    private static final ResourceLocation TEXTUREFISH_3 =
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/fishing/fish_3_gui.png");
    private static final ResourceLocation TEXTUREFISH_4 =
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/fishing/fish_4_gui.png");
    private static final ResourceLocation TEXTUREFISH_5 =
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/fishing/fish_5_gui.png");
    private static final ResourceLocation TEXTUREFISH_6 =
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/fishing/fish_6_gui.png");
    private static final ResourceLocation TEXTUREFISH_7 =
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/fishing/fish_7_gui.png");
    private static final ResourceLocation TEXTUREFISH_8 =
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/fishing/fish_8_gui.png");



    private static final ResourceLocation TEXTURECLAWOVERLAY =
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/fishing/fishing_gui_4.png");

    private ResourceLocation currentFishTexture;

    private int imageHeight;
    private int imageWidth;
    Player pPlayer = Minecraft.getInstance().player;
    private int randomTiming;
    private int randomYOffset;
    private int randomFish;


    private double offset = 0;
    private int offsetWaves = 0;
    private int offsetWaves2 = 0;
    boolean clicked = false;

    private final BlockPos clickedPos;

    public FishingScreen(BlockPos clickedPos) {
        super(Component.literal("Fishing"));
        this.clickedPos = clickedPos;
    }

    @Override
    protected void init() {
        this.imageWidth = 176;
        this.imageHeight = 48;

        int luckLevel = EnchantmentHelper.getItemEnchantmentLevel(
                Minecraft.getInstance().level.registryAccess()
                        .registryOrThrow(Registries.ENCHANTMENT)
                        .getHolderOrThrow(Enchantments.LUCK_OF_THE_SEA),
                Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND)
        );

        this.randomTiming = (20 * 12 + RandomSource.create().nextInt(8) * 20) - luckLevel*40;

        this.randomYOffset = -(RandomSource.create().nextInt(16) * 2);
        this.randomFish = RandomSource.create().nextInt(8) + 1;

        switch (randomFish) {
            case 1 -> currentFishTexture = TEXTUREFISH_1;
            case 2 -> currentFishTexture = TEXTUREFISH_2;
            case 3 -> currentFishTexture = TEXTUREFISH_3;
            case 4 -> currentFishTexture = TEXTUREFISH_4;
            case 5 -> currentFishTexture = TEXTUREFISH_5;
            case 6 -> currentFishTexture = TEXTUREFISH_6;
            case 7 -> currentFishTexture = TEXTUREFISH_7;
            case 8 -> currentFishTexture = TEXTUREFISH_8;
            default -> currentFishTexture = TEXTUREFISH_1;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        int guiX = ((width - imageWidth) / 2);
        int centerX = (this.width / 2);
        int guiY = ((height - imageHeight - 145 + WCEClientConfig.CLIENT.FISHING_SCREEN_Y_OFFSET.get()) / 2);

        int scissorX = guiX + 2;
        int scissorY = guiY + 2;
        int scissorX2 = guiX + 174;
        int scissorY2 = guiY + 46;

        RenderSystem.enableBlend();

        guiGraphics.setColor(0.6f,0.6f,0.9f,1f);
        guiGraphics.blit(TEXTUREBACKGROUND, guiX, guiY, 0, 0, imageWidth, imageHeight, 176, 48);
        guiGraphics.enableScissor(
                scissorX,
                scissorY,
                scissorX2,
                scissorY2
        );
        guiGraphics.setColor(1f,1f,1f,1f);

        guiGraphics.blit(TEXTUREWAVES, guiX + offsetWaves + 176, guiY, 0, 0, imageWidth, imageHeight, 176, 48);
        guiGraphics.blit(TEXTUREWAVES, guiX + offsetWaves2, guiY, 0, 0, imageWidth, imageHeight, 176, 48);
        guiGraphics.blit(currentFishTexture, guiX + (int) (offset + 176), guiY + this.randomYOffset, 0, 0, imageWidth, imageHeight, 176, 48);

        guiGraphics.blit(TEXTURECLAWOVERLAY, guiX, guiY, 0, 0, imageWidth, imageHeight, 176, 48);

        guiGraphics.disableScissor();

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(centerX, guiY + 51, 0);
        guiGraphics.pose().scale(0.83f,0.83f,0);
        guiGraphics.drawCenteredString(
                this.font,
                Component.translatable("screen.fishing.click_tip"),
                0,
                0,
                0x88FFFFFF
        );
        guiGraphics.pose().popPose();

        if (pPlayer.tickCount % 2 == 0) {
            this.offsetWaves--;
            this.offsetWaves2--;
            if (this.offsetWaves <= -352) {
                this.offsetWaves = 0;
            }
            if (this.offsetWaves2 <= -176) {
                this.offsetWaves2 = 176;
            }
        }

        if (this.randomTiming <= 0) {
            this.offset -= 1.25;
            if ((this.offset <= -196)) {
                this.offset = 0;
                Minecraft.getInstance().setScreen(null);
                if (pPlayer != null) {
                    pPlayer.displayClientMessage(
                            Component.translatable("screen.fishing.missed").withStyle(ChatFormatting.GRAY),
                            true
                    );
                }
            }
        }

        RenderSystem.disableBlend();

    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void tick() {
        if (this.randomTiming > 0) {
            this.randomTiming--;
        }

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        clicked = true;

        if (this.offset <= -90 && this.offset > -103) {

            Player player = Minecraft.getInstance().player;
            if (player != null) {
                player.displayClientMessage(
                        Component.translatable("screen.fishing.catch").withStyle(ChatFormatting.DARK_AQUA),
                        true
                );
                ModPackets.sendToServer(new CtSFishSuccesful(this.clickedPos));

            }

            Minecraft.getInstance().setScreen(null);
            return true;
        } else {
            Minecraft.getInstance().setScreen(null);
            if (pPlayer != null) {
                pPlayer.displayClientMessage(
                        Component.translatable("screen.fishing.missed").withStyle(ChatFormatting.GRAY),
                        true
                );
                ModPackets.sendToServer(new CtSFishFailed());
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == GLFW.GLFW_KEY_E) {
            this.onClose();
            return true;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }
}
