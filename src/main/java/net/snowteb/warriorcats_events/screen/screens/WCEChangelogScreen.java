package net.snowteb.warriorcats_events.screen.screens;

import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.snowteb.warriorcats_events.WCEClient;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.screen.widgets.ChangelogScrollList;
import net.snowteb.warriorcats_events.screen.widgets.GradientToggleButton;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

import static net.snowteb.warriorcats_events.screen.screens.CreateClanScreen.BG_TEXTURE;

@OnlyIn(Dist.CLIENT)
public class WCEChangelogScreen extends Screen {

    private final Screen parent;

    private List<String> lines = new ArrayList<>();

    private GradientToggleButton backButton;

    private ChangelogScrollList changelogList;

    public WCEChangelogScreen(Screen parent) {
        super(Component.literal("Warrior Cats Events"));
        this.parent = parent;
    }

    private int pulsationCicle = 0;

    private boolean pulsationSwitch = false;

    private float pulsationIncrease = 0f;

    @Override
    protected void init() {
        int centerX = width / 2;
        int centerY = height / 2;

        lines.clear();
        defineChangelogLines();

        changelogList = new ChangelogScrollList(Minecraft.getInstance(), 270, 200,
                centerY-50, centerY+80, 50);
        changelogList.setLeftPos(centerX-136);
        changelogList.setLogs(lines);
        changelogList.setRenderTopAndBottom(false);



        backButton = new GradientToggleButton(
                centerX - 135, centerY + 90, 40, 17,
                Component.literal("Back"),
                btn -> {
                    onClose();
                },  ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                60, 20, 1f, 0xFFFFFFFF
        );

        this.addRenderableWidget(changelogList);
        this.addRenderableWidget(backButton);

    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {

        int centerX = width / 2;
        int centerY = height / 2;

        pGuiGraphics.blit(BG_TEXTURE, 0, 0, 0, 0, this.width, this.height, this.width, this.height);

        float scale = 0.60f;

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(centerX-(125*scale), centerY-(215*scale), 0);

        pGuiGraphics.pose().scale(scale, scale, scale);

        pGuiGraphics.blit(WCEClient.WCE_TITLE,
                0,
                0, 0, 0,
                250, 125,250,125);

        pGuiGraphics.pose().popPose();

        pGuiGraphics.renderOutline(centerX - 140, centerY -60, 280, 170, 0x11FFFFFF);

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(centerX-135, centerY-55, 0);

        float textScale = 0.50f;
        pGuiGraphics.pose().scale(textScale, textScale, textScale);

        int y = 0;

//        for (String line : lines) {
//            List<FormattedCharSequence> wrapped = this.font.split(FormattedText.of(line), 550);
//            for (FormattedCharSequence subLine : wrapped) {
//
//
//                pGuiGraphics.drawString(this.font,subLine, 0, y, 0xFFFFFFFF);
//                y += this.font.lineHeight;
//            }
//            y+=6;
//        }
        pGuiGraphics.pose().popPose();


        float versionScale = 1.4f;

        versionScale += pulsationIncrease;

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(centerX + 70, centerY-90, 0.1);

        pGuiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(-20f));

        pGuiGraphics.pose().scale(versionScale, versionScale, versionScale);

        pGuiGraphics.drawCenteredString(this.font, WarriorCatsEvents.MOD_VERSION, 0, 0, ChatFormatting.GOLD.getColor());

        pGuiGraphics.pose().popPose();


        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);


    }

    @Override
    public void tick() {
        if (pulsationCicle >= 0 && pulsationCicle <= 10) {
            pulsationCicle++;

            if (pulsationSwitch) {
                pulsationIncrease += 0.02f;
            } else {
                pulsationIncrease -= 0.02f;
            }

            if (pulsationCicle >= 10) {
                pulsationSwitch = !pulsationSwitch;
                pulsationCicle = 0;
            }
        }
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.onClose();
            return true;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    private void defineChangelogLines() {
        lines.add("$(##) Now available on Aternos!");
        lines.add("$(#) Nests, Moonstone, Lizards, and more by Bem te vi!");
        lines.add("Greetings! This update adds several new decorative and functional blocks, this thanks to the contribution by 'bem te vi' and his amazing talent with modeling. We all thank him for such beautiful pieces of art 💖  \n" +
                "\n" +
                "This is only the part 1 of the contributions made by 'bem te vi', there will be more to come in the future.\n" +
                "\n" +
                "This update, aside from decorative blocks, accessories, and fixes, also includes the new Lizard!\n" +
                "Lizards are very cute and useful creatures that can be tamed using Spider eyes. Kits usually hunt these down so better to be careful before taming.\n" +
                "Once you have tamed a Lizard, you can Shift+Right-click to carry them on your head.\n" +
                "Lizards will also occasionally dig up valuable items! From ores, to even valuable items such as Hearts of the sea.\n" +
                "Lizards can be bred and will lay eggs in dark places once they have a mate.\n" +
                "\n" +
                "Again, thanks to 'bem te vi' for contributing with basically all models and textures in this update.");
        lines.add("$(##) Changelog");

        lines.add("- Added: Cherry Blossom Nest, Driftwood Nest, Daisy Nest, Acacia Nest, Terracotta Nest, Bamboo Nest, Berry Nest, Coral Nest, Glowberry Nest, Muddy Nest, Kittypet Nest, Acorn Nest, Nautilus Nest, Sunflower Nest.");
        lines.add("- Added Glowrocks, found in caves, can be tinted of different colors. Many can be placed together.");
        lines.add("- Added Moonstone, a block intended to be a replacement for the Enchanting table. Surrounding it with Glowrocks will cause the same effect as surrounding an Enchanting table with Bookshelfs.");
        lines.add("- Added accessories: Blue Morpho Wing, Goliath Birdwing Wing, Monarch Wing, Tiger Swallowtail Wing. As for now (until future updates), these can be found in village chests. ");
        lines.add("- Added Acorn Lantern, Daisy Chain, Lavender Chain. ");
        lines.add("- Added Stickfire, as a replacement for furnaces.");
        lines.add("- Added Lizards! Lizards can be found around the world, and can be tamed using Spider eyes. Lizards can ocassionally dig valuable items for you. You can carry a lizard on your head by using Shift+Right-Click on a tamed lizard. ");
        lines.add("- Added drop to vanilla frogs. They will now drop a respective food item.");
        lines.add("- Added back the old config system for additional items (through .toml). This change wont be reverted, and will be the new average in the future.");
        lines.add("- Removed some limitations for Leaping.");
        lines.add("- Adjusted some diseases, fixed some values.");
        lines.add("- Fixed an issue that caused players to be kicked out of their clans, and reset some character data.");
        lines.add("- Fixed an issue with Warrior Nametags.");
        lines.add("- Reduced chance of breaking paws.");
        lines.add("- Reduced chance of sore pads.");
        lines.add("- Fishing will now spawn an actual fish instead of an item, looks very cute :3");
        lines.add("- The Leaping button can now be configured. Will stay as Left Click by default.");
        lines.add("- Added sounds specific for Pigeons.");
        lines.add("- Added subtitles to most sounds for accessibility settings.");
        lines.add("- Slightly reduced enemy spawns in territory.");
        lines.add("- Other fixes and adjustements to Wild Cats.");
        lines.add("- Minor adjustements to prey.");
        lines.add("- Removed naturally spawned too massive Wild Cats");
        lines.add("- Increased the time to charge leap from up to 8 secs, to 15 secs.");
        lines.add("- Increased the hitbox for leaping.");
        lines.add("- Added an op command to invite players to clans.");
        lines.add("- Added new public emote.");
        lines.add("- Added new exclusive emote for contributors");
        lines.add("- Other minor adjustements.");

        lines.add("$(/#) Thank you for reading 🐈");
    }

//    private void defineChangelogLines() {
//        lines.add("$(#) ");
//        lines.add("");
//        lines.add("$(##) Changelog");

//        lines.add("");
//        lines.add("");
//        lines.add("");
//        lines.add("");
//        lines.add("");
//        lines.add("");
//        lines.add("");
//        lines.add("");
//        lines.add("");
//        lines.add("");
//        lines.add("");
//        lines.add("");
//        lines.add("");
//        lines.add("");
//        lines.add("");
//        lines.add("");
//        lines.add("");
//
//        lines.add("$(/#) Thank you for reading 🐈");
//    }



}
