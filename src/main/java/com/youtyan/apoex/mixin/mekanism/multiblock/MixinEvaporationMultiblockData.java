package com.youtyan.apoex.mixin.mekanism.multiblock;

import com.youtyan.apoex.IApoExMultiblock;
import com.youtyan.apoex.recipe.ApoExOutputHandler;
import com.youtyan.apoex.recipe.ApoExSkippingInputHandler;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.outputs.IOutputHandler;
import mekanism.common.content.evaporation.EvaporationMultiblockData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EvaporationMultiblockData.class, remap = false)
public abstract class MixinEvaporationMultiblockData implements IApoExMultiblock {

    @Unique
    private float apoex_evaporation_consumptionAccumulator = 100F;

    @Override
    public float getConsumptionAccumulator() {
        return this.apoex_evaporation_consumptionAccumulator;
    }

    @Override
    public void setConsumptionAccumulator(float value) {
        this.apoex_evaporation_consumptionAccumulator = value;
    }

    @Inject(method = "getTemperature", at = @At("RETURN"), cancellable = true)
    private void apoex_getTemperature(CallbackInfoReturnable<Double> cir) {
        float mult = this.getHeatEfficiency();
        if (mult > 0) {
            cir.setReturnValue(cir.getReturnValue() * (1.0 + mult));
        }
    }

    @Inject(method = "getMaxFluid", at = @At("RETURN"), cancellable = true)
    private void apoex_getMaxFluid(CallbackInfoReturnable<Integer> cir) {
        float mult = this.getFuelCapacityMultiplier();
        if (mult > 0) {
            long newCapacity = (long) (cir.getReturnValue() * (1.0F + mult));
            cir.setReturnValue((int) Math.min(Integer.MAX_VALUE, newCapacity));
        }
    }

    @ModifyArg(
        method = "createNewCachedRecipe",
        at = @At(value = "INVOKE", target = "Lmekanism/api/recipes/cache/OneInputCachedRecipe;fluidToFluid(Lmekanism/api/recipes/FluidToFluidRecipe;Ljava/util/function/BooleanSupplier;Lmekanism/api/recipes/inputs/IInputHandler;Lmekanism/api/recipes/outputs/IOutputHandler;)Lmekanism/api/recipes/cache/OneInputCachedRecipe;"),
        index = 2
    )
    private IInputHandler<?> apoex_wrapInputHandler(IInputHandler<?> original) {
        return new ApoExSkippingInputHandler(original, this);
    }

    @ModifyArg(
        method = "createNewCachedRecipe",
        at = @At(value = "INVOKE", target = "Lmekanism/api/recipes/cache/OneInputCachedRecipe;fluidToFluid(Lmekanism/api/recipes/FluidToFluidRecipe;Ljava/util/function/BooleanSupplier;Lmekanism/api/recipes/inputs/IInputHandler;Lmekanism/api/recipes/outputs/IOutputHandler;)Lmekanism/api/recipes/cache/OneInputCachedRecipe;"),
        index = 3
    )
    private IOutputHandler<?> apoex_wrapOutputHandler(IOutputHandler<?> original) {
        return new ApoExOutputHandler<>(original, this);
    }
}