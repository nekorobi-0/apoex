package com.youtyan.apoex.mixin.mekanism.api;

import com.youtyan.apoex.IApoExMultiblock;
import com.youtyan.apoex.mixin.mekanism.accessor.AccessorBasicFluidTank;
import mekanism.common.capabilities.fluid.VariableCapacityFluidTank;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = VariableCapacityFluidTank.class, remap = false)
public class MixinVariableCapacityFluidTank {

    @Inject(method = "getCapacity", at = @At("RETURN"), cancellable = true, remap = false)
    private void apoex_getCapacity(CallbackInfoReturnable<Integer> cir) {
        AccessorBasicFluidTank accessor = (AccessorBasicFluidTank) this;
        if (accessor.getListener() instanceof IApoExMultiblock multiblock) {
            float mult = multiblock.getFuelCapacityMultiplier();
            if (mult > 0) {
                long newCapacity = (long) (cir.getReturnValue() * (1.0F + mult));
                cir.setReturnValue((int) Math.min(Integer.MAX_VALUE, newCapacity));
            }
        }
    }
}
