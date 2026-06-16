package net.snowteb.warriorcats_events.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.snowteb.warriorcats_events.clan.ClanData;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class LizardEggBlockEntity extends BlockEntity {
    private UUID ownerUUID = null;

    public LizardEggBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.LIZARD_EGG.get(), pos, blockState);
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        if (ownerUUID != null) pTag.putUUID("Owner", ownerUUID);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        if (pTag.contains("Owner")) this.ownerUUID =  pTag.getUUID("Owner");
    }

    public void setOwnerUUID(UUID uuid) {
        this.ownerUUID = uuid;
    }

    @Nullable
    public UUID getOwnerUUID() {
        return this.ownerUUID;
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }


}
