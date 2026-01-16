package com.youtyan.apoex.mixin.mekanism.generator;

import com.youtyan.apoex.IApoExGenerator;
import mekanism.api.math.FloatingLong;
import mekanism.generators.common.tile.TileEntityGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TileEntityGenerator.class, remap = false)
public class MixinTileEntityGeneratorV2 {

    @Inject(method = "getMaxOutput", at = @At("RETURN"), cancellable = true)
    private void apoex_getMaxOutput(CallbackInfoReturnable<FloatingLong> cir) {
        if (this instanceof IApoExGenerator gen) {
            float mult = gen.getGenerationMultiplier();
            if (mult > 0) {
                cir.setReturnValue(cir.getReturnValue().multiply(1.0F + mult));
            }
        }
    }

    @ModifyVariable(method = "updateMaxOutputRaw", at = @At("HEAD"), argsOnly = true)
    private FloatingLong apoex_updateMaxOutputRaw(FloatingLong maxOutput) {
        if (this instanceof IApoExGenerator gen) {
            float mult = gen.getGenerationMultiplier();
            if (mult > 0) {
                maxOutput = maxOutput.multiply(1.0F + mult);
            }
            
            float efficiency = gen.getFuelEfficiency();
            if (efficiency > 0 && efficiency < 1.0F) {
                float effMult = 1.0F / (1.0F - efficiency);
                maxOutput = maxOutput.multiply(effMult);
            }
        }
        return maxOutput;
    }
}
