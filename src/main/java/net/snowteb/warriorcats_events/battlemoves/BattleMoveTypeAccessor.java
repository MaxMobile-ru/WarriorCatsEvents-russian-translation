package net.snowteb.warriorcats_events.battlemoves;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BattleMoveTypeAccessor {
    private final String identifier;
    private int cooldown = 0;

    public BattleMoveTypeAccessor (String identifier) {
        this.identifier = identifier;
    }

    public void serializeNBT(CompoundTag tag) {
        tag.putString("identifier", identifier);
        tag.putInt("cooldown", cooldown);
    }

    public static BattleMoveTypeAccessor deserializeNBT(CompoundTag tag) {
        String identifier = tag.getString("identifier");
        BattleMoveTypeAccessor accessor = new BattleMoveTypeAccessor(identifier);

        accessor.cooldown = tag.getInt("cooldown");
        return accessor;
    }

    @Nullable
    public BattleMoveType<?> access() {
        return BattleMoveRegistry.getByID(identifier);
    }

    public boolean run(@Nonnull Player p) {
        if (cooldown > 0) return false;
        BattleMoveType<?> type = access();
        if (type == null) return false;

        type.getMove().onTrigger(p);
        this.setCooldown(type, p);
        return true;
    }

    public void accessorTick() {
        if (this.cooldown <= 0) return;
        this.cooldown--;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setCooldown(@Nonnull BattleMoveType<?> type, Player player) {
        this.cooldown = type.getMove().getCooldown(player);
    }

    public int getCooldown() {
        return cooldown;
    }

}
