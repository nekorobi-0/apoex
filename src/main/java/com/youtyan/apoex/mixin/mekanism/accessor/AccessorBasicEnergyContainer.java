package com.youtyan.apoex.mixin.mekanism.accessor;

import mekanism.api.IContentsListener;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = BasicEnergyContainer.class, remap = false)
public interface AccessorBasicEnergyContainer {
    @Accessor("listener")
    IContentsListener getListener();
}
