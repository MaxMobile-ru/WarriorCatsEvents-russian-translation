package net.snowteb.warriorcats_events.screen.screens;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.entity.custom.EagleEntity;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.c2s.cats.CommandCatsPacket;
import net.snowteb.warriorcats_events.network.packet.c2s.others.CallEaglesPacket;
import net.snowteb.warriorcats_events.screen.widgets.MultipleSelectionScrollList;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AncientStickScreen extends Screen {
    private final List<WCatEntity> catsInRange;
    private final List<EagleEntity> eaglesInRange;
    public List<Component> catListNames = new ArrayList<>();

    private Button setAllToFollow;
    private Button setAllToStay;
    private Button dismissAll;

    private Button setSpecificToFollow;
    private Button setSpecificToStay;
    private Button dismissSpecific;
    private Button callEagles;

    private MultipleSelectionScrollList catsScrollListAll;
    private MultipleSelectionScrollList catsScrollListSelected;

    private int showTimeNoCatsSelected = 0;
    private static final int SHOW_TIME = 80;

    private static final ResourceLocation SHOWALLCATS =
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/showallcats.png");


    public AncientStickScreen(List<WCatEntity> cats, List<EagleEntity> eagles) {
        super(Component.literal("AncientStick"));
        this.catsInRange = cats;
        this.eaglesInRange = eagles;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        pGuiGraphics.fillGradient(0, 0, this.width, this.height,0xC0101010,0x00101010);


        int centerx = (this.width) / 2;
        int centery = (this.height) / 2;

        boolean shouldShowAllCats = (pMouseX < centerx + 50 && pMouseX > centerx - 50
                                    && pMouseY < 28 && pMouseY > 0);

        pGuiGraphics.blit(SHOWALLCATS,
                centerx - 50,-10,
                0,0,
                100,40,
                100,40
        );
        pGuiGraphics.drawCenteredString(Minecraft.getInstance().font,
                Component.translatable("screen.ancientstick.available_cats"),
                centerx, 5, 0xFFFFFF);

        int lessWidth1 = 0;
        int lessWidth2 = 0;

        if (catsScrollListAll.children().size() < 4) {
            lessWidth1 = 6;
        }
        if (catsScrollListSelected.children().size() < 4) {
            lessWidth2 = 6;
        }

        pGuiGraphics.renderOutline(9,29, 118 - lessWidth1, 82, 0xFFFFFFFF);

        pGuiGraphics.renderOutline(9,139, 118 - lessWidth2, 82, 0xFFF2AE11);

        pGuiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("screen.ancientstick.clanmates_in_range"),
                10, 15, 0xFFFFFF);
        pGuiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("screen.ancientstick.clanmates_selected"),
                10, 125, ChatFormatting.GOLD.getColor());

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        if (showTimeNoCatsSelected > 0) {
//            pGuiGraphics.drawCenteredString(Minecraft.getInstance().font,
//                    "No specific cats selected",
//                    centerx, centery + 60, 0xFFFF0000);

            Component text = Component.translatable("screen.ancientstick.no_cats_selected").withStyle(ChatFormatting.RED);

            pGuiGraphics.renderTooltip(Minecraft.getInstance().font,
                    text, (int) (centerx - Minecraft.getInstance().font.width(text)/1.8), centery + 60);
        }

        if (shouldShowAllCats) pGuiGraphics.renderTooltip(Minecraft.getInstance().font, catListNames,
                Optional.empty(), pMouseX, pMouseY + 20);

    }

    @Override
    protected void init() {
        int centerX = (this.width) / 2;
        int centerY = (this.height) / 2;

        int listWidth = 110;
        int listHeight = 40;
        int top1 = 10 + 20;
        int bottom1 = 90 + 20;

        int top2 = 10 + 110 + 20;
        int bottom2 = 90 + 110 + 20;

        this.catsScrollListAll = new MultipleSelectionScrollList(
                Minecraft.getInstance(),
                listWidth,
                listHeight,
                top1,
                bottom1,
                20,
                false
        );

        this.catsScrollListSelected = new MultipleSelectionScrollList(
                Minecraft.getInstance(),
                listWidth,
                listHeight,
                top2,
                bottom2,
                20,
                false
        );

        for (WCatEntity cat : catsInRange) {
            catListNames.add(cat.hasCustomName() ? cat.getCustomName() : Component.literal("Unknown Cat"));
            catsScrollListAll.addOption(cat.hasCustomName() ? cat.getCustomName().getString() : "Unknown Cat", cat.getId());
        }

        this.catsScrollListSelected.setRenderTopAndBottom(false);
        this.catsScrollListSelected.setRenderBackground(true);
        this.catsScrollListSelected.setOtherList(catsScrollListAll);
        this.catsScrollListSelected.setLeftPos(((10)));


        this.catsScrollListAll.setRenderTopAndBottom(false);
        this.catsScrollListAll.setRenderBackground(true);
        this.catsScrollListAll.setOtherList(catsScrollListSelected);
        this.catsScrollListAll.setLeftPos(((10)));

        List<Integer> idsInRange = new ArrayList<>();
        for (WCatEntity cat : catsInRange) {
            idsInRange.add(cat.getId());
        }

        setAllToFollow = Button.builder(
                Component.translatable("screen.ancientstick.follow"),
                btn -> {
                    this.onClose();
                    ModPackets.sendToServer(new CommandCatsPacket(idsInRange, catsScrollListSelected.getSelectedIds(), "all", "follow"));
                }
        ).bounds(this.width - 110, 10, 100, 20).build();

        setAllToStay = Button.builder(
                Component.translatable("screen.ancientstick.stay"),
                btn -> {
                    this.onClose();
                    ModPackets.sendToServer(new CommandCatsPacket(idsInRange, catsScrollListSelected.getSelectedIds(), "all", "stay"));
                }
        ).bounds(this.width - 110, 35, 100, 20).build();

        dismissAll = Button.builder(
                Component.translatable("screen.ancientstick.dismiss"),
                btn -> {
                    this.onClose();
                    ModPackets.sendToServer(new CommandCatsPacket(idsInRange, catsScrollListSelected.getSelectedIds(), "all", "wander"));
                }
        ).bounds(this.width - 110, 60, 100, 20).build();




        setSpecificToFollow = Button.builder(
                Component.translatable("screen.ancientstick.follow_selected"),
                btn -> {
                    if (!catsScrollListSelected.getSelectedIds().isEmpty()) {
                        this.onClose();
                        ModPackets.sendToServer(new CommandCatsPacket(idsInRange,
                                catsScrollListSelected.getSelectedIds(),
                                "specific", "follow"));
                    } else {
                        showTimeNoCatsSelected = SHOW_TIME;
                    }
                }
        ).bounds(this.width - 140, 10 + 120, 130, 20).build();

        setSpecificToStay = Button.builder(
                Component.translatable("screen.ancientstick.stay_selected"),
                btn -> {
                    if (!catsScrollListSelected.getSelectedIds().isEmpty()) {
                        this.onClose();
                        ModPackets.sendToServer(new CommandCatsPacket(idsInRange,
                                catsScrollListSelected.getSelectedIds(),
                                "specific", "stay"));
                    } else {
                        showTimeNoCatsSelected = SHOW_TIME;
                    }
                }
        ).bounds(this.width - 140, 35 + 120, 130, 20).build();

        dismissSpecific = Button.builder(
                Component.translatable("screen.ancientstick.dismiss_selected"),
                btn -> {
                    if (!catsScrollListSelected.getSelectedIds().isEmpty()) {
                        this.onClose();
                        ModPackets.sendToServer(new CommandCatsPacket(idsInRange,
                                catsScrollListSelected.getSelectedIds(),
                                "specific", "wander"));
                    } else {
                        showTimeNoCatsSelected = SHOW_TIME;
                    }
                }
        ).bounds(this.width - 140, 60 + 120, 130, 20).build();


        Component eaglesText;
        if (eaglesInRange.size() > 1) {
            eaglesText = Component.translatable("screen.ancientstick.call_eagles");
        } else {
            eaglesText = Component.translatable("screen.ancientstick.call_eagle");
        }

        callEagles = Button.builder(
                eaglesText,
                btn -> {
                    if (!eaglesInRange.isEmpty()) {
                        this.onClose();
                        List<Integer> list = eaglesInRange.stream().map(EagleEntity::getId).toList();
                        ModPackets.sendToServer(new CallEaglesPacket(list));
                    }
                }
        ).bounds(this.width - 140, 85 + 120, 130, 20).build();


        this.addRenderableWidget(setAllToFollow);
        this.addRenderableWidget(setAllToStay);
        this.addRenderableWidget(dismissAll);

        this.addRenderableWidget(setSpecificToFollow);
        this.addRenderableWidget(setSpecificToStay);
        this.addRenderableWidget(dismissSpecific);
        if (!eaglesInRange.isEmpty()) this.addRenderableWidget(callEagles);

        this.addRenderableWidget(catsScrollListAll);
        this.addRenderableWidget(catsScrollListSelected);

    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == GLFW.GLFW_KEY_E) {
            this.onClose();
            return true;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public void tick() {
        if (showTimeNoCatsSelected > 0) {
            showTimeNoCatsSelected--;
        }
    }
}
