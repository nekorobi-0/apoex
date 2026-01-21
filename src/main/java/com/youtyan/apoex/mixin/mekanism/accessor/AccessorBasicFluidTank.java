package com.youtyan.apoex.mixin.mekanism.accessor;

import mekanism.api.IContentsListener;
import mekanism.common.capabilities.fluid.BasicFluidTank;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = BasicFluidTank.class, remap = false)
public interface AccessorBasicFluidTank {
    @Accessor("listener")
    IContentsListener getListener();
}
