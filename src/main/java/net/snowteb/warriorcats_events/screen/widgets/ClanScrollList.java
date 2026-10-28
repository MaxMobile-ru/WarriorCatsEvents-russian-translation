package net.snowteb.warriorcats_events.screen.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.sounds.SoundSource;
import net.snowteb.warriorcats_events.WCEClient;
import net.snowteb.warriorcats_events.client.ClientClans;
import net.snowteb.warriorcats_events.managers.ClanSymbol;
import net.snowteb.warriorcats_events.screen.screens.clan.FancySpecificClanScreen;
import net.snowteb.warriorcats_events.sound.ModSounds;

import java.util.Comparator;
import java.util.List;

public class ClanScrollList extends AbstractSelectionList<ClanScrollList.ClanEntry> {

    public ClanScrollList(Minecraft mc, int width, int height, int top, int bottom, int itemHeight) {
        super(mc, width, height, top, bottom, itemHeight);
    }

    @Override
    protected int getScrollbarPosition() {
        return this.getLeft() + this.getRowWidth() + 13;
    }

    @Override
    public int getRowWidth() {
        return this.width - 20;
    }

    @Override
    public void updateNarration(NarrationElementOutput narration) {
    }

    public void addClans(List<ClientClans.Entry> clans) {
        List<ClientClans.Entry> sortedList = clans.stream()
                .sorted(Comparator.comparing(entry -> entry.getClan().name, String.CASE_INSENSITIVE_ORDER))
                .toList();

        for (ClientClans.Entry entry : sortedList) {
            this.addEntry(new ClanEntry(entry));
        }
    }

    public class ClanEntry extends AbstractSelectionList.Entry<ClanEntry> {

        private final ClientClans.Entry clan;

        public ClanEntry(ClientClans.Entry clan) {
            this.clan = clan;
        }

        public ClientClans.Entry getClan() {
            return clan;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0) {
                ClanScrollList.this.setSelected(this);
                WCEClient.playLocalSound(ModSounds.MENU_OPEN.get(), SoundSource.NEUTRAL, 0.7f,1.0f);
                Minecraft.getInstance().setScreen(new FancySpecificClanScreen(clan));
                return true;
            }
            return false;
        }


        @Override
        public void render(GuiGraphics pGuiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {

            width -= 10;

            boolean selected = ClanScrollList.this.getSelected() == this;

            int bgColor = selected ? 0x66FFFFFF
                    :
                    hovering ? 0x22FFFFFF
                            :
                            0x22000000;

            pGuiGraphics.fill(left, top, left + width, top + height, bgColor);
            if (hovering) {
                pGuiGraphics.renderOutline(left, top, width, height, (clan.getClan().color & 0x00FFFFFF) | 0x55 << 24);
            }

            int yStart = top + 4;

            pGuiGraphics.drawString(
                    Minecraft.getInstance().font,
                    clan.getClan().name,
                    left + 6,
                    yStart,
                    clan.getClan().color
            );

            {
                pGuiGraphics.pose().pushPose();
                pGuiGraphics.pose().translate(left + 6, yStart + 10, 0);
                float scale = 0.8f;
                pGuiGraphics.pose().scale(scale, scale, scale);

                pGuiGraphics.drawString(
                        Minecraft.getInstance().font,
                        "\uD83D\uDC51 : " + clan.getClan().leaderName,
                        0,
                        0,
                        0xFFcccccc
                );

                pGuiGraphics.pose().popPose();
            }


            {
                pGuiGraphics.pose().pushPose();
                pGuiGraphics.pose().translate(left + width - 70, yStart + 10, 0);
                float scale = 0.8f;
                pGuiGraphics.pose().scale(scale, scale, scale);

                pGuiGraphics.drawString(
                        Minecraft.getInstance().font,
                        " \uD83D\uDC08 : " + (clan.getMembers().size() + clan.getClan().clanCats.size()),
                        0,
                        0,
                        0xFFcccccc
                );

                pGuiGraphics.pose().popPose();
            }

            int color = clan.getClan().color;

            float a = ((color >> 24) & 0xFF) / 255f;
            float r = ((color >> 16) & 0xFF) / 255f;
            float g = ((color >> 8) & 0xFF) / 255f;
            float b = (color & 0xFF) / 255f;

            pGuiGraphics.setColor(r, g, b, a);


            double symbolScale = 2.5;
            double symbolSize = ClanSymbol.SYMBOL_SIZE/symbolScale;

            int symbolY = (int) ((top + ((double) height /2)) - (symbolSize)/2);
            int symbolX = (int) (width - symbolSize/4);

            pGuiGraphics.blit(
                    ClanSymbol.SPRITE,
                    symbolX, symbolY,
                    (float) ((float) ClanSymbol.getSymbolCoordinate(clan.getClan().clanSymbolIndex) / symbolScale), (float) 0,
                    (int) (symbolSize), (int) (symbolSize),
                    (int) (symbolSize * ClanSymbol.SYMBOLS_AMOUNT),
                    (int) (symbolSize)
            );

            pGuiGraphics.setColor(1f, 1f, 1f, 1f);

        }
    }
}
