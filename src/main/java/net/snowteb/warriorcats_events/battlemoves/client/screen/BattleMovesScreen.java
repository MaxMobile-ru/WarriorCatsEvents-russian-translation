package net.snowteb.warriorcats_events.battlemoves.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.snowteb.warriorcats_events.screen.screens.AbstractWCEScreen;
import net.snowteb.warriorcats_events.skills.PlayerSkill;
import net.snowteb.warriorcats_events.skills.PlayerSkillProvider;

public class BattleMovesScreen extends AbstractWCEScreen {

    private PlayerSkill playerSkill;

    public BattleMovesScreen() {
        this.playerSkill = new PlayerSkill();
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer != null) {
            this.playerSkill = (PlayerSkill) localPlayer.getCapability(PlayerSkillProvider.SKILL_DATA).orElse(new PlayerSkill());
        }
    }

    @Override
    protected void init() {
        BattleMovesCarouselWidget carouselWidget = new BattleMovesCarouselWidget(this.width, this.height, 0, 0, this);

        this.addRenderableWidget(carouselWidget);

        super.init();
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public int getErrorXOffset() {
        return 0;
    }

    @Override
    public int getErrorYOffset() {
        return 0;
    }

    @Override
    public float getErrorScale() {
        return 1f;
    }

}
