package com.youtyan.apoex.mixin.mekanism.accessor;

import mekanism.common.tile.prefab.TileEntityProgressMachine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = TileEntityProgressMachine.class, remap = false)
public interface TileEntityProgressMachineAccessor {
    @Accessor("ticksRequired")
    void setTicksRequired(int ticksRequired);

    @Accessor("ticksRequired")
    int getTicksRequired();

    @Accessor("baseTicksRequired")
    int getBaseTicksRequired();

    @Accessor("baseTicksRequired")
    void setBaseTicksRequired(int baseTicksRequired);
}