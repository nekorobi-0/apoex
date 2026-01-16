package com.youtyan.apoex.mixin.mekanism.generator;

import org.spongepowered.asm.mixin.Mixin;
import mekanism.generators.common.content.fusion.FusionReactorMultiblockData;

// このクラスは MixinFusionReactorMultiblockData に統合されたため無効化
@Mixin(value = FusionReactorMultiblockData.class, remap = false)
public abstract class MixinFusionReactorMultiblockDataV2 {
}