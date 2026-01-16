package com.youtyan.apoex.mixin.mekanism.generator;

import org.spongepowered.asm.mixin.Mixin;
import mekanism.generators.common.content.turbine.TurbineMultiblockData;

// このクラスは MixinTurbineMultiblockData に統合されたため無効化
@Mixin(value = TurbineMultiblockData.class, remap = false)
public abstract class MixinTurbineMultiblockDataV2 {
}