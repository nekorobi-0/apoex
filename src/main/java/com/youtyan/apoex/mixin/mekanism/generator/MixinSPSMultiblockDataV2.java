package com.youtyan.apoex.mixin.mekanism.generator;

import com.youtyan.apoex.IApoExMultiblock;
import mekanism.api.Action;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.common.content.sps.SPSMultiblockData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = SPSMultiblockData.class, remap = false)
public abstract class MixinSPSMultiblockDataV2 {

    @ModifyArg(method = "process", at = @At(value = "INVOKE", target = "Lmekanism/api/chemical/gas/IGasTank;shrinkStack(JLmekanism/api/Action;)J"), index = 0)
    private long apoex_shrinkStack(long amount) {
        if (this instanceof IApoExMultiblock multiblock) {
            float efficiency = multiblock.getFuelEfficiency();
            if (efficiency > 0 && efficiency < 1.0F) {
                float storedFraction = multiblock.getStoredFuelFraction();
                float consumeRate = 1.0F - efficiency;
                storedFraction += consumeRate;
                
                if (storedFraction >= 1.0F) {
                    storedFraction -= 1.0F;
                    multiblock.setStoredFuelFraction(storedFraction);
                    return amount;
                } else {
                    multiblock.setStoredFuelFraction(storedFraction);
                    return 0;
                }
            }
        }
        return amount;
    }
    
    @ModifyArg(method = "process", at = @At(value = "INVOKE", target = "Lmekanism/api/chemical/gas/IGasTank;insert(Lmekanism/api/chemical/ChemicalStack;Lmekanism/api/Action;Lmekanism/api/AutomationType;)Lmekanism/api/chemical/ChemicalStack;"), index = 0)
    private ChemicalStack apoex_insertOutput(ChemicalStack stack) {
        if (this instanceof IApoExMultiblock multiblock) {
            float mult = multiblock.getGenerationMultiplier();
            if (mult > 0) {
                stack.setAmount((long) (stack.getAmount() * (1.0F + mult)));
            }
        }
        return stack;
    }
}
