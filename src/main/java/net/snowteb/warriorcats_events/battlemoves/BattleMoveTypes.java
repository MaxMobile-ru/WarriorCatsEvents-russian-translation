package net.snowteb.warriorcats_events.battlemoves;

import net.minecraft.resources.ResourceLocation;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.battlemoves.types.SlashMove;

@WCEBattleMoves
public class BattleMoveTypes {

    public static final BattleMoveType<SlashMove> SLASH = BattleMoveRegistry.register(new BattleMoveType<>(
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "slash"),
            BattleMoveCategory.ATTACK, new SlashMove()
    ));

    public static final BattleMoveType<SlashMove> PUNCH = BattleMoveRegistry.register(new BattleMoveType<>(
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "punch"),
            BattleMoveCategory.ATTACK, new SlashMove()
    ));

    public static final BattleMoveType<SlashMove> YELL = BattleMoveRegistry.register(new BattleMoveType<>(
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "yell"),
            BattleMoveCategory.TACTICAL, new SlashMove()
    ));

    public static final BattleMoveType<SlashMove> WOAH = BattleMoveRegistry.register(new BattleMoveType<>(
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "woah"),
            BattleMoveCategory.TACTICAL, new SlashMove()
    ));

    public static final BattleMoveType<SlashMove> DASH = BattleMoveRegistry.register(new BattleMoveType<>(
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "dash"),
            BattleMoveCategory.TACTICAL, new SlashMove()
    ));

    public static final BattleMoveType<SlashMove> POUNCE = BattleMoveRegistry.register(new BattleMoveType<>(
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "pounce"),
            BattleMoveCategory.TACTICAL, new SlashMove()
    ));

    public static final BattleMoveType<SlashMove> RAWR = BattleMoveRegistry.register(new BattleMoveType<>(
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "rawr"),
            BattleMoveCategory.DEFENSE, new SlashMove()
    ));

}
