package net.snowteb.warriorcats_events.clanplayerdata;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WCEPlayerDataProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static Capability<WCEPlayerData> PLAYER_CLAN_DATA = CapabilityManager.get(new CapabilityToken<WCEPlayerData>() {});


    private WCEPlayerData clanData  = null;
    private final LazyOptional<WCEPlayerData> optional = LazyOptional.of(this::createClanData);

    private WCEPlayerData createClanData() {
        if(this.clanData == null) {
            this.clanData = new WCEPlayerData();
        }

        return this.clanData;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == PLAYER_CLAN_DATA) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }


    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        createClanData().saveNBT(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createClanData().loadNBT(nbt);
    }

    public WCEPlayerData getOrCreateClanData() {
        WCEPlayerData t = createClanData();
        t.reset();
        return t;
    }


}
