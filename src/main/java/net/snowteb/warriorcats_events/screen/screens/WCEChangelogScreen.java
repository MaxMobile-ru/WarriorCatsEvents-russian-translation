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

import static net.snowteb.warriorcats_events.screen.screens.clan.FancyCreateClanScreen.BG_TEXTURE;

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
        lines.add("$(#) 1.13.0 | Clans Rework and more");
        lines.add("Greetings, it has been a long time since the last update released. Life has been busy and it will be even more busy the following month, but hope to be able to still be here.\n" +
                "\n" +
                "This update isn't as big as expected. It was decided to release what's important now and take more time to finish some big additions that are planned. But it is something, hope it is appreciated for now.\n" +
                "\n" +
                "This update offers a rework for how clans are viewed and managed. As well as some new fields and options for clans, and other adjustments in the backend.\n" +
                "\n" +
                "This update also features the new Russian translation, provided by Max_Mobile. We thank them for this contribution, and hope all the Russian players will have a better experience now.");
        lines.add("$(##) Changelog");

        lines.add("- Reworked clan list, clan menu, and the clan manage menu.");
        lines.add("- Removed clan manage menu and integrated inside the clan menu options.");
        lines.add("- Added different logs category and more info displayed in clan menus.");
        lines.add("- Added new fields and options for clans.");
        lines.add("- Added join requests to clans.");
        lines.add("- Fixed issue that makes cats and players move their paw while on first person.\n");
        lines.add("- Added clan chat, use /wce clan toggleClanChat");
        lines.add("- Added Russian translation.");
        lines.add("- Adjusted targetting rules for wolves.");
        lines.add("- Fixed issues with changing names of NPCs");
        lines.add("- Added command /wce reloadContributors");
        lines.add("- Adjustements to the command for setting new leader.");
        lines.add("- Adjustements to how clans are synced to the client.");
        lines.add("- Fixed other issues of clan territories.");
        lines.add("- Fixed crashes caused by KittypetBowl");
        lines.add("- Other minor fixes and adjustements.");


        lines.add("$(##) Discord Boosters");
        lines.add("Boost our Discord server to be a contributor and have exclusive emotes, bigger cat sizes, and other features!");

        lines.add("$(##) Our Minecraft server!");
        lines.add("Join our Minecraft server today! More information in the Discord server.");


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
