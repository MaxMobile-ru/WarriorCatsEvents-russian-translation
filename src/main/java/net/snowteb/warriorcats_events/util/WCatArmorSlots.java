package net.snowteb.warriorcats_events.util;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;

public class WCatArmorSlots extends Slot {
    private final EquipmentSlot equipmentSlot;
    private final WCatEntity entity;

    public WCatArmorSlots(WCatEntity entity, EquipmentSlot slot, int x, int y) {
        super(new SimpleContainer(1), 0, x, y);
        this.entity = entity;
        this.equipmentSlot = slot;
    }

    @Override
    public ItemStack getItem() {
        return entity.getItemBySlot(equipmentSlot);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem armor && armor.getEquipmentSlot() == equipmentSlot;
    }

    @Override
    public void set(ItemStack stack) {
        entity.setItemSlot(equipmentSlot, stack);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return true;
    }

    @Override
    public ItemStack remove(int amount) {
        ItemStack stack = getItem();
        set(ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void onTake(Player playerIn, ItemStack stack) {
        set(ItemStack.EMPTY);
    }


}

