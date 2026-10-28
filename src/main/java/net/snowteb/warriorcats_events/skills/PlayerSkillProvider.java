package net.snowteb.warriorcats_events.skills;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerSkillProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public static Capability<ISkillData> SKILL_DATA = CapabilityManager.get(new CapabilityToken<>() {});

    private PlayerSkill backend = null;
    private final LazyOptional<PlayerSkill> optional = LazyOptional.of(this::createSkills);

    private PlayerSkill createSkills() {
        if (backend == null) backend = new PlayerSkill();
        return backend;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == SKILL_DATA ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        PlayerSkill s = createSkills();
        return s.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        PlayerSkill s = createSkills();
        s.deserializeNBT(nbt);
    }

}
