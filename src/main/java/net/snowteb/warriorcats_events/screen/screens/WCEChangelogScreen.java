package net.snowteb.warriorcats_events.screen.screens;

import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
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
        changelogList.setX(centerX-136);
        changelogList.setLogs(lines);
//        changelogList.setRenderTopAndBottom(false);



        backButton = new GradientToggleButton(
                centerX - 135, centerY + 90, 40, 17,
                Component.translatable("screen.changelog.back"),
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
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

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

        pGuiGraphics.pose().popPose();


        float versionScale = 1.4f;

        versionScale += pulsationIncrease;

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(centerX + 70, centerY-90, 0.1);

        pGuiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(-20f));

        pGuiGraphics.pose().scale(versionScale, versionScale, versionScale);

        pGuiGraphics.drawCenteredString(this.font, WarriorCatsEvents.MOD_VERSION, 0, 0, ChatFormatting.GOLD.getColor());

        pGuiGraphics.pose().popPose();




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
        lines.add("$(#) 1.11.0 | Reworks, more languages, and more!");
        lines.add("Greetingss, this update will be mainly focused on internal reworks, and some very nice changes.\n" +
                "\n" +
                "This includes a complete rework of the Create Morph menu. Now it will be not only more pretty, but also a lot more friendly with our 'squared-screen' friends.\n" +
                "\n" +
                "Not only this, but also some huge news...\n" +
                "\n" +
                "WCE is now available in other languages!!!!\n" +
                "Thanks to the amazing work of tangyunyun, who contacted me to voluntarily help with this, WCE is now available in Chinese!!! \n" +
                "希望你喜欢\n" +
                "\n" +
                "Additionally, WCE is now also available in Spanish. This is a translation made of my own, since I myself speak spanish. Espero aprecien el arduo trabajo c:\n" +
                "\n" +
                "Also, thanks to 'Dazzy / Phen' and 'Bog' and their amazing contribution of a piece of code... WCE now also supports custom cat textures! You can now make a resourcepack with the ID \"warriorcats_events\" and add any textures you want. Here is a template to make your own resource packs. You can force resourcepacks in Multiplayer servers.\n" +
                "\n" +
                "https://www.mediafire.com/file/sd9qmmnoy2apld2/example_pack_1.20.1.zip/file\n" +
                "\n" +
                "There is also a lot more changes, adjustements, and additions in this update, so...");
        lines.add("$(##) Changelog");

        lines.add("- Fixed kits showing as fleas when the server config is changed.");
        lines.add("- Removed all Hard-coded Strings and replaced by translation keys.");
        lines.add("- Replaced genetics serializing for Wild Cats. From 1.16.0, Wild Cats that are not migrated from older versions to 1.11.X-1.15.X, will lose all their genetic traits.");
        lines.add("- Reworked and improved Create Morph menu.");
        lines.add("- Improved rendering for carrying kits.");
        lines.add("- Fixed the infinite XP glitch in the Skill Tree.");
        lines.add("- Added server config for max claimable territory.");
        lines.add("- All cat collars have been removed. Cat collars are now replaced by a single dyable cat collar, that allows endless color combinations.");
        lines.add("- Stored morphs now support preset cats.");
        lines.add("- Replaced player morph serializing. You might experience minor one-time issues.");
        lines.add("- Added Chinese and Spanish translations.");
        lines.add("- Added prefix randomization to the \"Kit\" item.");
        lines.add("- Added Carved stone! You can now Right-click stone to carve it, and obtain pebbles, which can be crafted into cobblestone.");
        lines.add("- Reworked herb mixing system. Now it wont be managed through the Crafting Rock.");
        lines.add("- Added Herb Rock, a dedicated block for mixing herbs.");
        lines.add("- Added skin shades! You can change the skin shade of your character in the Details section.");
        lines.add("- Updated Warrior's Guide.");
        lines.add("- Warrior Nametags are now renamable. Shift+Right-click to rename a Warrior Nametag.");
        lines.add("- Claws, Whiskers, and Warriors Guide will no longer drop on death.");
        lines.add("- Improved cats AI. Now they will run and avoid damage sources such as fire, cactus, and others.");
        lines.add("- Cats are now inmune to Sweet Berry bushes.");
        lines.add("- Added an option to kick/remove NPCs from clans in the clan menu.");
        lines.add("- Added Flappy Cat! Play Flappy Cat from the last option in the WCE Options menu.");
        lines.add("- Other minor adjustements.");
        lines.add("- Other minor reworks.");
        lines.add("- Removed Herobrinepaw.");

        lines.add("$(##) Discord Boosters");
        lines.add("Boost our Discord server to be a contributor and have exclusive emotes, bigger cat sizes, and other features!");


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
