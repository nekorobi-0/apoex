package com.youtyan.apoex.mixin.mekanism.generator;

import com.youtyan.apoex.IApoExMultiblock;
import mekanism.common.content.evaporation.EvaporationMultiblockData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EvaporationMultiblockData.class, remap = false)
public abstract class MixinEvaporationMultiblockDataV2 {

    @Inject(method = "getTemperature", at = @At("RETURN"), cancellable = true)
    private void apoex_getTemperature(CallbackInfoReturnable<Double> cir) {
        if (this instanceof IApoExMultiblock multiblock) {
            float mult = multiblock.getHeatEfficiency();
            if (mult > 0) {
                cir.setReturnValue(cir.getReturnValue() * (1.0 + mult));
            }
        }
    }
}
