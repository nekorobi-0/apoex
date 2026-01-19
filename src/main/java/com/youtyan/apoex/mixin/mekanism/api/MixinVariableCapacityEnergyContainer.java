package com.youtyan.apoex.mixin.mekanism.api;

import com.youtyan.apoex.IApoExMultiblock;
import com.youtyan.apoex.mixin.mekanism.accessor.AccessorBasicEnergyContainer;
import mekanism.api.math.FloatingLong;
import mekanism.common.capabilities.energy.VariableCapacityEnergyContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = VariableCapacityEnergyContainer.class, remap = false)
public class MixinVariableCapacityEnergyContainer {

    @Inject(method = "getMaxEnergy", at = @At("RETURN"), cancellable = true, remap = false)
    private void apoex_getMaxEnergy(CallbackInfoReturnable<FloatingLong> cir) {
        AccessorBasicEnergyContainer accessor = (AccessorBasicEnergyContainer) this;
        if (accessor.getListener() instanceof IApoExMultiblock multiblock) {
            float mult = multiblock.getEnergyCapacityMultiplier();
            if (mult > 0) {
                cir.setReturnValue(cir.getReturnValue().multiply(1.0F + mult));
            }
        }
    }
}
