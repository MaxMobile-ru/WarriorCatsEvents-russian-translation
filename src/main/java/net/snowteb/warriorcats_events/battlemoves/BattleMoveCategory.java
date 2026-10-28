package net.snowteb.warriorcats_events.battlemoves;

import net.minecraft.network.chat.Component;
import net.snowteb.warriorcats_events.battlemoves.types.SlashMove;

public class BattleMoveCategory {

    public static final BattleMoveCategory NONE = new BattleMoveCategory(Component.translatable("battlemoves.category.none"), 0xFFFFFFFF);
    public static final BattleMoveCategory ATTACK = new BattleMoveCategory(Component.translatable("battlemoves.category.attack"), 0xFFFF0000);
    public static final BattleMoveCategory DEFENSE = new BattleMoveCategory(Component.translatable("battlemoves.category.defense"), 0xFF0000FF);
    public static final BattleMoveCategory TACTICAL = new BattleMoveCategory(Component.translatable("battlemoves.category.tactical"), 0xFF00FF00);

    private final Component name;
    private final int color;

    public BattleMoveCategory(Component name, int color) {
        this.name = name;
        this.color = color;
    }

    public Component getName() {
        return name;
    }

    public int getColor() {
        return color;
    }
}
