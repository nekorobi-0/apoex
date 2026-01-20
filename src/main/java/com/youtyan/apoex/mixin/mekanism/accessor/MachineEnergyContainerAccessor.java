package com.youtyan.apoex.mixin.mekanism.accessor;

import mekanism.api.math.FloatingLong;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = MachineEnergyContainer.class, remap = false)
public interface MachineEnergyContainerAccessor {
    @Accessor("currentMaxEnergy")
    FloatingLong getCurrentMaxEnergy();

    @Accessor("currentMaxEnergy")
    void setCurrentMaxEnergy(FloatingLong maxEnergy);

    @Accessor("currentEnergyPerTick")
    FloatingLong getCurrentEnergyPerTick();

    @Accessor("currentEnergyPerTick")
    void setCurrentEnergyPerTick(FloatingLong energyPerTick);
}
