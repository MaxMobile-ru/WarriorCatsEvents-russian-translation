package net.snowteb.warriorcats_events.battlemoves.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.snowteb.warriorcats_events.battlemoves.BattleMoveRegistry;
import net.snowteb.warriorcats_events.battlemoves.BattleMoveType;
import net.snowteb.warriorcats_events.battlemoves.PlayerBattleMovesStore;
import net.snowteb.warriorcats_events.screen.screens.AbstractWCEScreen;
import net.snowteb.warriorcats_events.screen.screens.createmorph.FancySimpleButton;
import net.snowteb.warriorcats_events.screen.screens.createmorph.SubRenderable;
import net.snowteb.warriorcats_events.skills.PlayerSkill;
import net.snowteb.warriorcats_events.skills.PlayerSkillProvider;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

public class BattleMovesCarouselWidget implements Renderable, SubRenderable, GuiEventListener, NarratableEntry {

    private int x0;
    private int y0;
    private final int width;
    private final int height;

    private final int originalY0;
    private final int originalX0;

    private final List<BattleMoveWidget> children = new ArrayList<>();

    private int index = 0;
    private @Nullable GuiEventListener focused;
    private boolean active;

    private int spacing;

    private int visibleStart = 0;
    private int visibleEnd = 0;

    List<BattleMovesSlotWidget> slotWidgets = new ArrayList<>();

    @Nullable
    private BattleMoveWidget toAssign;

    public BattleMovesCarouselWidget(int width, int height, int x0, int y0, AbstractWCEScreen screen) {
        this.width = width;
        this.x0 = x0;
        this.y0 = y0;
        this.height = height;
        this.originalY0 = y0;
        this.originalX0 = x0;
        this.active = true;
        this.init(screen);
    }

    public void init(AbstractWCEScreen screen) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        this.children.clear();

        PlayerSkill skillData = (PlayerSkill) player.getCapability(PlayerSkillProvider.SKILL_DATA).orElse(new PlayerSkill());

        List<BattleMoveType<?>> unlocked = skillData.getBattleMovesStore().getUnlocked().keySet()
                .stream().map(BattleMoveRegistry::getByID).filter(Objects::nonNull)
                .collect(Collectors.toList());


        List<BattleMoveType<?>> typesAvailable = new ArrayList<>(BattleMoveRegistry.getList().values());

        int centerX = width / 2;
        int centerY = height / 2;

        int widgetWidth = 120;
        int widgetHeight = 160;
        int widgetX = centerX - (widgetWidth / 2);
        int widgetY = centerY - 20 - (widgetHeight / 2);
        spacing = (int) (widgetWidth*1.1);

        for (int i = 0; i < typesAvailable.size(); i++) {

            BattleMoveType<?> type = typesAvailable.get(i);

            BattleMoveWidget widget = new BattleMoveWidget(widgetX, widgetY, widgetWidth, widgetHeight, type, unlocked.contains(type), this);
            widget.setIndex(i);

            if (unlocked.contains(type)) {
                FancySimpleButton assignButton = new FancySimpleButton(
                        widgetWidth / 2, 15,
                        widgetWidth/2 - (widgetWidth/2)/2, widgetHeight,
                        Component.translatable("screen.battlemoves.assign"),
                        b -> {
                            setMoveToAssign(widget);
                        },
                        0.8f, true
                );

                widget.addWidget(assignButton);
            } else {
                FancySimpleButton unlockButton = new FancySimpleButton(
                        widgetWidth / 2, 15,
                        widgetWidth/2 - (widgetWidth/2)/2, widgetHeight,
                        Component.translatable("screen.battlemoves.unlock"),
                        b -> {

                        },
                        0.8f, true
                );

                widget.addWidget(unlockButton);
            }

            this.children.add(widget);

            widgetX += spacing;
        }

        int slotWidth = 30;
        int slotHeight = 30;
        int slotXPos = centerX -((PlayerBattleMovesStore.MAX_SLOTS*slotWidth + PlayerBattleMovesStore.MAX_SLOTS*3)/2);
        int slotYPos = height - slotHeight - 5;
        for (int i = 0; i < PlayerBattleMovesStore.MAX_SLOTS; i++) {
            BattleMovesSlotWidget slotWidget = new BattleMovesSlotWidget(
                    slotXPos, slotYPos,
                    slotWidth, slotHeight,
                    this, skillData.getBattleMovesStore().getSlot(i)
            );
            slotWidget.setIndex(i);
            slotXPos += slotWidth + 3;

            this.slotWidgets.add(slotWidget);
        }

    }

    public void setMoveToAssign(BattleMoveWidget widget) {
        boolean isEmpty = widget == null;

        for (BattleMoveWidget battleMoveWidget : this.children) {
            battleMoveWidget.setPulsating(false);
        }

        if (widget != null) {
            if (this.children.contains(widget)) {
                widget.setPulsating(true);
            }
        }

        this.toAssign = widget;

        for (BattleMovesSlotWidget slotWidget : this.slotWidgets) {
            slotWidget.setPulsating(!isEmpty);
        }
    }

    @Nullable
    public BattleMoveType<?> getToAssign() {
        if (this.toAssign == null) return null;
        return this.toAssign.battleMoveType;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {

        int offset = -index * spacing;

        visibleStart = Math.max(index - 3, 0);
        visibleEnd = Math.min(index + 4, this.children.size());


        for (int i = visibleStart; i < visibleEnd; i++) {
            BattleMoveWidget widget = this.children.get(i);
            widget.setXOffset(offset);
            if (i != index) {
                widget.setScaleOffset(-0.2f);
                widget.setFocused(false);
            } else {
                widget.setScaleOffset(0);
                widget.setFocused(true);
            }
            widget.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        }

        for (Renderable renderable : this.slotWidgets) {
            renderable.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        }

    }

    private List<Renderable> getVisibleWidgets() {
        if (children.isEmpty()) return List.of();
        return children.subList(visibleStart, visibleEnd).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void setFocused(boolean pFocused) {

    }

    @Override
    public boolean isFocused() {
        return false;
    }

    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
    }

    @Override
    public void adjustYPos(int yOffset) {
        this.y0 = getOriginalYPos0() + yOffset;
    }

    @Override
    public int getOriginalYPos0() {
        return originalY0;
    }

    @Override
    public int getOriginalYPos1() {
        return originalY0 + height;
    }

    @Override
    public void setClickableIn(BiPredicate<Double, Double> predicate) {

    }

    @Override
    public @Nullable BiPredicate<Double, Double> isClickableIn() {
        return null;
    }

    @Override
    public boolean isMouseOver(double pMouseX, double pMouseY) {
        for (Renderable renderable : this.getVisibleWidgets()) {
            if (renderable instanceof GuiEventListener listener) {
                if (listener.isMouseOver(pMouseX, pMouseY)) return true;
            }
        }
        return pMouseX >= x0 && pMouseX < x0 + width
                && pMouseY >= y0 && pMouseY < y0 + height;
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (!this.active) return false;

        for (Renderable renderable : this.slotWidgets) {
            if  (renderable instanceof GuiEventListener listener) {
                if (listener.mouseClicked(pMouseX, pMouseY, pButton)) {
                    return true;
                }
            }
        }

        for (Renderable renderable :  this.getVisibleWidgets()) {
            if (renderable instanceof GuiEventListener listener) {
                if (listener.mouseClicked(pMouseX, pMouseY, pButton)) {
                    listener.setFocused(true);
                    this.setFocusedWidget(listener);
                    return true;
                }
            }
        }

        if (!GuiEventListener.super.mouseClicked(pMouseX, pMouseY, pButton)) {
            for (Renderable renderable :  this.getVisibleWidgets()) {
                if (renderable instanceof GuiEventListener listener) {
                    listener.setFocused(false);
                    this.setFocusedWidget(null);
                }
            }

            setMoveToAssign(null);

            boolean isMouseOver = pMouseX > x0 - 1 && pMouseX < x0 + width
                    && pMouseY > y0 - 1 && pMouseY < y0 + height;
            return isMouseOver;
        }

        return false;
    }

    public void setActive(boolean pActive) {
        this.active = pActive;
        for (Renderable renderable :  this.getWidgets()) {
            if (renderable instanceof AbstractWidget widget) {
                widget.active = pActive;
            }
        }
    }

    public boolean isCarouselActive() {
        return this.active;
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        for (Renderable renderable :  this.getVisibleWidgets()) {
            if (renderable instanceof GuiEventListener listener) {
                if (listener.isMouseOver(pMouseX, pMouseY)) {
                    if (listener.mouseScrolled(pMouseX, pMouseY, pDelta)) return true;
                }
            }
        }

        setIndex(Mth.clamp(getIndex() + (pDelta > 0 ? -1 : 1), 0, Math.max(0, children.size() - 1)));

        return true;
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        for (Renderable renderable :  this.getVisibleWidgets()) {
            if (renderable instanceof GuiEventListener listener) {
                if (listener.mouseReleased(pMouseX, pMouseY, pButton)) return true;
            }
        }
        return GuiEventListener.super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    public void mouseMoved(double pMouseX, double pMouseY) {
        for (Renderable renderable :  this.getVisibleWidgets()) {
            if (renderable instanceof GuiEventListener listener) {
                listener.mouseMoved(pMouseX, pMouseY);
            }
        }
        GuiEventListener.super.mouseMoved(pMouseX, pMouseY);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (this.getFocusedWidget() != null) {
            return (this.getFocusedWidget().keyPressed(pKeyCode, pScanCode, pModifiers));
        }
        return GuiEventListener.super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        for (Renderable renderable :  this.getVisibleWidgets()) {
            if (renderable instanceof GuiEventListener listener) {
                if (listener.keyReleased(pKeyCode, pScanCode, pModifiers)) return true;
            }
        }
        return GuiEventListener.super.keyReleased(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean charTyped(char pCodePoint, int pModifiers) {
        if (this.getFocusedWidget() != null) {
            return (this.getFocusedWidget().charTyped(pCodePoint, pModifiers));
        }
        return GuiEventListener.super.charTyped(pCodePoint, pModifiers);
    }

    public void setFocusedWidget(GuiEventListener listener) {
        if (this.focused != null && this.focused != listener) {
            this.focused.setFocused(false);
        }
        if (listener != null) {
            listener.setFocused(true);
        }
        this.focused = listener;
    }

    @Nullable
    public GuiEventListener getFocusedWidget() {
        return this.focused;
    }

    public List<Renderable> getWidgets() {
        return this.children.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public void tickkkk() {
        for (Renderable renderable : children) {
            if (renderable instanceof SubRenderable sub) sub.tickkkk();
        }
    }

    @Override
    public void adjustXPos(int xOffset) {
        this.x0 = originalX0 + xOffset;
        for (Renderable renderable : children) {
            if (renderable instanceof SubRenderable sub) sub.adjustXPos(xOffset);
        }
    }

    @Override
    public int getOriginalXPos0() {
        return this.originalX0;
    }

    @Override
    public int getOriginalXPos1() {
        return this.originalX0 + width;
    }

}
