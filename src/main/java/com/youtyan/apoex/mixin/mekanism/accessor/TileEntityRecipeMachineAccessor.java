package com.youtyan.apoex.mixin.mekanism.accessor;

import mekanism.common.recipe.lookup.monitor.RecipeCacheLookupMonitor;
import mekanism.common.tile.prefab.TileEntityRecipeMachine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = TileEntityRecipeMachine.class, remap = false)
public interface TileEntityRecipeMachineAccessor {
    @Accessor("recipeCacheLookupMonitor")
    RecipeCacheLookupMonitor<?> getRecipeCacheLookupMonitor();
}
