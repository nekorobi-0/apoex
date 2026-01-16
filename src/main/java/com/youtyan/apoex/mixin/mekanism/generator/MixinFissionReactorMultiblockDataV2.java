package com.youtyan.apoex.mixin.mekanism.generator;

import org.spongepowered.asm.mixin.Mixin;
import mekanism.generators.common.content.fission.FissionReactorMultiblockData;

// このクラスは MixinFissionReactorMultiblockData に統合されたため無効化
@Mixin(value = FissionReactorMultiblockData.class, remap = false)
public abstract class MixinFissionReactorMultiblockDataV2 {
}