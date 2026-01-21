package com.youtyan.apoex.mixin.mekanismgenerators.generator;

import com.youtyan.apoex.IApoExGenerator;
import mekanism.api.math.FloatingLong;
import mekanism.generators.common.tile.TileEntityGenerator;
import mekanism.generators.common.tile.TileEntityHeatGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TileEntityHeatGenerator.class, remap = false)
public abstract class MixinTileEntityHeatGenerator extends TileEntityGenerator {

    public MixinTileEntityHeatGenerator() {
        super(null, null, null, null);
    }

    @ModifyArg(method = "onUpdateServer", at = @At(value = "INVOKE", target = "Lmekanism/common/capabilities/fluid/BasicFluidTank;extract(ILmekanism/api/Action;Lmekanism/api/AutomationType;)Lnet/minecraftforge/fluids/FluidStack;"), index = 0)
    private int apoex_extract(int amount) {
        if (this instanceof IApoExGenerator gen) {
            float efficiency = gen.getFuelEfficiency();
            if (efficiency > 0 && efficiency < 1.0F) {
                float storedFraction = gen.getStoredFuelFraction();
                float consumeRate = 1.0F - efficiency;
                storedFraction += consumeRate;
                
                if (storedFraction >= 1.0F) {
                    storedFraction -= 1.0F;
                    gen.setStoredFuelFraction(storedFraction);
                    return amount;
                } else {
                    gen.setStoredFuelFraction(storedFraction);
                    return 0;
                }
            }
        }
        return amount;
    }

    @Inject(method = "getProductionRate", at = @At("RETURN"), cancellable = true)
    private void apoex_getProductionRate(CallbackInfoReturnable<FloatingLong> cir) {
        if (this instanceof IApoExGenerator gen) {
            float mult = gen.getGenerationMultiplier();
            if (mult > 0) {
                cir.setReturnValue(cir.getReturnValue().multiply(1.0F + mult));
            }
        }
    }
}
