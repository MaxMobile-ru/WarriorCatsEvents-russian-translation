package net.snowteb.warriorcats_events.battlemoves;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

public class PlayerBattleMovesStore {
    public static final int MAX_SLOTS = 3;

    private final Map<String, BattleMoveTypeAccessor> unlocked = new LinkedHashMap<>();
    private final String[] slots = new String[MAX_SLOTS];

    public boolean isUnlocked(String id) {
        return unlocked.containsKey(id);
    }

    public void unlockMove(String id) {
        unlocked.putIfAbsent(id, new BattleMoveTypeAccessor(id));
    }

    public Map<String, BattleMoveTypeAccessor> getUnlocked() {
        return unlocked;
    }

    public void setInSlot(String id, int slot) {
        if (!unlocked.containsKey(id)) return;
        slot = Mth.clamp(slot, 0, MAX_SLOTS - 1);
        slots[slot] = id;
    }

    @Nullable
    public BattleMoveTypeAccessor getSlot(int slot) {
        slot = Mth.clamp(slot, 0, MAX_SLOTS - 1);
        String id = slots[slot];
        return id == null ? null : unlocked.get(id);
    }

    public boolean isSlotEmpty(int slot) {
        slot = Mth.clamp(slot, 0, MAX_SLOTS - 1);
        return getSlot(slot) == null;
    }

    public void removeFromSlot(int slot) {
        slot = Mth.clamp(slot, 0, MAX_SLOTS - 1);
        slots[slot] = null;
    }

    public void serialize(CompoundTag tag) {
        ListTag movesTag = new ListTag();
        for (BattleMoveTypeAccessor accessor : unlocked.values()) {
            CompoundTag moveTag = new CompoundTag();
            accessor.serializeNBT(moveTag);
            movesTag.add(moveTag);
        }
        tag.put("UnlockedMoves", movesTag);

        ListTag slotsTag = new ListTag();
        for (String id : slots) {
            slotsTag.add(net.minecraft.nbt.StringTag.valueOf(id == null ? "" : id));
        }
        tag.put("Slots", slotsTag);
    }

    public static PlayerBattleMovesStore deserialize(CompoundTag tag) {
        PlayerBattleMovesStore store = new PlayerBattleMovesStore();
        if (!tag.contains("UnlockedMoves")) return store;

        ListTag movesTag = tag.getList("UnlockedMoves", Tag.TAG_COMPOUND);
        for (int i = 0; i < movesTag.size(); i++) {
            BattleMoveTypeAccessor accessor = BattleMoveTypeAccessor.deserializeNBT(movesTag.getCompound(i));
            if (BattleMoveRegistry.getByID(accessor.getIdentifier()) != null) {
                store.unlocked.put(accessor.getIdentifier(), accessor);
            }
        }

        ListTag slotsTag = tag.getList("Slots", Tag.TAG_STRING);
        for (int i = 0; i < Math.min(MAX_SLOTS, slotsTag.size()); i++) {
            String id = slotsTag.getString(i);
            if (!id.isEmpty() && store.unlocked.containsKey(id)) {
                store.slots[i] = id;
            }
        }

        return store;
    }

    public void storeTick() {
        for (int i = 0; i <= MAX_SLOTS; i++) {
            BattleMoveTypeAccessor accessor = getSlot(i);
            if (accessor == null) continue;
            accessor.accessorTick();
        }
    }
}
