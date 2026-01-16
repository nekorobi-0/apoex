package com.youtyan.apoex.mixin.mekanism.generator;

import com.youtyan.apoex.IApoExGenerator;
import mekanism.generators.common.tile.turbine.TileEntityTurbineCasing;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = TileEntityTurbineCasing.class, remap = false)
public abstract class MixinTileEntityTurbineCasing implements IApoExGenerator {
    // Methods are inherited from MixinTileEntityMekanism which is applied to TileEntityMekanism (parent of TileEntityTurbineCasing)
}
