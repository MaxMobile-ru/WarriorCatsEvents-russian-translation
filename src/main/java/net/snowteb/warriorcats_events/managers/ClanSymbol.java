package net.snowteb.warriorcats_events.managers;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.snowteb.warriorcats_events.WarriorCatsEvents;

public final class ClanSymbol {

    public static final ResourceLocation SPRITE = ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/clan_symbols_sprite.png");
    public static final int SYMBOLS_AMOUNT = 147;
    public static final int SYMBOL_SIZE = 50;

    private ClanSymbol() {}

    public static int getSymbolCoordinate(int index) {
        return index * SYMBOL_SIZE;
    }

    public static final char BASE_CHAR = '\uE000';
    public static final ResourceLocation SYMBOL_FONT =
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "clan_symbols");

    public static String getChar(int index) {
        return String.valueOf((char) (BASE_CHAR + index));
    }

    public static MutableComponent symbolComponent(int index) {
        return Component.literal(getChar(index)).withStyle(Style.EMPTY.withFont(SYMBOL_FONT));
    }

}
