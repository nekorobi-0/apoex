package com.youtyan.apoex.mixin.astralmekanism.generator;

import astral_mekanism.block.blockentity.generator.BEGasBurningGenerator;
import com.youtyan.apoex.IApoExGenerator;
import mekanism.api.math.FloatingLong;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = BEGasBurningGenerator.class, remap = false)
public abstract class MixinBEGasBurningGenerator implements IApoExGenerator {

    @ModifyVariable(method = "burning", at = @At(value = "STORE"), name = "energyPer1mB")
    private FloatingLong apoex_modifyEnergyPer1mB(FloatingLong energyPer1mB) {
        float mult = this.getGenerationMultiplier();
        if (mult > 0) {
            return energyPer1mB.multiply(1.0F + mult);
        }
        return energyPer1mB;
    }

    @ModifyVariable(method = "burning", at = @At(value = "STORE"), name = "burnGasAmount")
    private long apoex_modifyBurnGasAmount(long burnGasAmount) {
        float efficiency = this.getFuelEfficiency();
        if (efficiency > 0 && efficiency < 1.0F) {
            float storedFraction = this.getStoredFuelFraction();
            float consumeRate = 1.0F - efficiency;
            storedFraction += consumeRate * burnGasAmount;

            long toConsume = (long) storedFraction;
            if (toConsume > 0) {
                this.setStoredFuelFraction(storedFraction - toConsume);
                return toConsume;
            } else {
                this.setStoredFuelFraction(storedFraction);
                return 0;
            }
        }
        return burnGasAmount;
    }

    @ModifyArg(method = "getInitialGasTanks", at = @At(value = "INVOKE", target = "Lmekanism/api/chemical/ChemicalTankBuilder;create(JLjava/util/function/Predicate;Lmekanism/api/IContentsListener;)Lmekanism/api/chemical/IChemicalTank;"), index = 0)
    private long apoex_modifyFuelTankCapacity(long capacity) {
        float mult = this.getFuelCapacityMultiplier();
        if (mult > 0) {
            return (long) (capacity * (1.0F + mult));
        }
        return capacity;
    }
}
