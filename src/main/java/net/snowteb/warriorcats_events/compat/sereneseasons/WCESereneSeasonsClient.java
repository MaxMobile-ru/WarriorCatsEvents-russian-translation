package net.snowteb.warriorcats_events.compat.sereneseasons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.level.Level;
import net.snowteb.warriorcats_events.client.ClientTerritoryEvents;
import sereneseasons.api.season.ISeasonState;
import sereneseasons.api.season.SeasonHelper;

public class WCESereneSeasonsClient {

    public static void seasonOverlay(GuiGraphics pGuiGraphics, Level level) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null) return;

        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();

        ISeasonState state = SeasonHelper.getSeasonState(level);
        Component text = switch (state.getSubSeason()) {
            case EARLY_SPRING -> Component.translatable("wce.ss.early_newleaf").withStyle(Style.EMPTY.withColor(0xff50d773));
            case MID_SPRING -> Component.translatable("wce.ss.mid_newleaf").withStyle(Style.EMPTY.withColor(0xff49d956));
            case LATE_SPRING -> Component.translatable("wce.ss.late_newleaf").withStyle(Style.EMPTY.withColor(0xff2ddc3a));
            case EARLY_SUMMER -> Component.translatable("wce.ss.early_greenleaf").withStyle(Style.EMPTY.withColor(0xff34bf30));
            case MID_SUMMER -> Component.translatable("wce.ss.mid_greenleaf").withStyle(Style.EMPTY.withColor(0xff56bc13));
            case LATE_SUMMER -> Component.translatable("wce.ss.late_greenleaf").withStyle(Style.EMPTY.withColor(0xff69b81a));
            case EARLY_AUTUMN -> Component.translatable("wce.ss.early_leaffall").withStyle(Style.EMPTY.withColor(0xff94ad2b));
            case MID_AUTUMN -> Component.translatable("wce.ss.mid_leaffall").withStyle(Style.EMPTY.withColor(0xffb79f29));
            case LATE_AUTUMN -> Component.translatable("wce.ss.late_leaffall").withStyle(Style.EMPTY.withColor(0xFFce9249));
            case EARLY_WINTER -> Component.translatable("wce.ss.early_leafbare").withStyle(Style.EMPTY.withColor(0xffa19cb3));
            case MID_WINTER -> Component.translatable("wce.ss.mid_leafbare").withStyle(Style.EMPTY.withColor(0xff879aeb));
            case LATE_WINTER -> Component.translatable("wce.ss.late_leafbare").withStyle(Style.EMPTY.withColor(0xff5dadc1));
        };

        {
            int lineLenght = mc.font.width(text.getString()) + 5;

            float scale = 0.9f;

            int xPosition = (int) (mc.getWindow().getGuiScaledWidth() - (lineLenght*scale) - 10);

            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate(xPosition, 5, 0);
            pGuiGraphics.pose().scale(scale, scale, scale);
            pGuiGraphics.drawString(mc.font, text, 0, 0, 0xFFFFFFFF);

            int color = text.getStyle().getColor() != null ? text.getStyle().getColor().getValue() : 0xFFFFFF;
            pGuiGraphics.fill(-2, 10, mc.font.width(text.getString()) + 5, 12, color);
            pGuiGraphics.pose().popPose();
        }
    }

}
