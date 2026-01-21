package com.youtyan.apoex.mixin.mekanism.accessor;

import mekanism.api.IContentsListener;
import mekanism.api.chemical.BasicChemicalTank;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = BasicChemicalTank.class, remap = false)
public interface AccessorBasicChemicalTank {
    @Accessor("listener")
    IContentsListener getListener();
}
