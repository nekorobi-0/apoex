package com.youtyan.apoex.mixin.astralmekanism.generator;

import astral_mekanism.block.blockentity.generator.BEHeatGenerator;
import com.youtyan.apoex.IApoExGenerator;
import mekanism.api.math.FloatingLong;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BEHeatGenerator.class, remap = false)
public abstract class MixinBEHeatGenerator implements IApoExGenerator {

    @ModifyVariable(method = "simulate", at = @At(value = "STORE"), name = "energyFromHeat")
    private FloatingLong apoex_modifyEnergyFromHeat(FloatingLong energyFromHeat) {
        float mult = this.getGenerationMultiplier();
        if (mult > 0) {
            return energyFromHeat.multiply(1.0F + mult);
        }
        return energyFromHeat;
    }

    @Inject(method = "getBoost", at = @At("RETURN"), cancellable = true)
    private void apoex_getBoost(CallbackInfoReturnable<FloatingLong> cir) {
        float mult = this.getHeatEfficiency();
        if (mult > 0) {
            cir.setReturnValue(cir.getReturnValue().multiply(1.0F + mult));
        }
    }
}
