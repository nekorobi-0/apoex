package com.youtyan.apoex.mixin.mekanism.multiblock;

import com.youtyan.apoex.IApoExMultiblock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.IntSupplier;
import java.util.function.LongSupplier;

@Mixin(targets = "mekanism.common.content.boiler.BoilerMultiblockData", remap = false)
public abstract class MixinBoilerMultiblockData {

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lmekanism/common/capabilities/chemical/multiblock/MultiblockChemicalTankBuilder;input(Lmekanism/common/lib/multiblock/MultiblockData;Ljava/util/function/LongSupplier;Ljava/util/function/Predicate;Lmekanism/api/IContentsListener;)Lmekanism/api/chemical/IChemicalTank;"), index = 1)
    private LongSupplier apoex_modifySuperheatedCoolantCapacity(LongSupplier original) {
        return () -> {
            long cap = original.getAsLong();
            if (this instanceof IApoExMultiblock multiblock) {
                float mult = multiblock.getFuelCapacityMultiplier();
                if (mult > 0) {
                    return (long) (cap * (1.0F + mult));
                }
            }
            return cap;
        };
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lmekanism/common/capabilities/fluid/VariableCapacityFluidTank;input(Lmekanism/common/lib/multiblock/MultiblockData;Ljava/util/function/IntSupplier;Ljava/util/function/Predicate;Lmekanism/api/IContentsListener;)Lmekanism/common/capabilities/fluid/VariableCapacityFluidTank;"), index = 1)
    private IntSupplier apoex_modifyWaterTankCapacity(IntSupplier original) {
        return () -> {
            int cap = original.getAsInt();
            if (this instanceof IApoExMultiblock multiblock) {
                float mult = multiblock.getFuelCapacityMultiplier();
                if (mult > 0) {
                    return (int) (cap * (1.0F + mult));
                }
            }
            return cap;
        };
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lmekanism/common/capabilities/chemical/multiblock/MultiblockChemicalTankBuilder;output(Lmekanism/common/lib/multiblock/MultiblockData;Ljava/util/function/LongSupplier;Ljava/util/function/Predicate;Lmekanism/api/IContentsListener;)Lmekanism/api/chemical/IChemicalTank;", ordinal = 0), index = 1)
    private LongSupplier apoex_modifySteamTankCapacity(LongSupplier original) {
        return () -> {
            long cap = original.getAsLong();
            if (this instanceof IApoExMultiblock multiblock) {
                float mult = multiblock.getFuelCapacityMultiplier();
                if (mult > 0) {
                    return (long) (cap * (1.0F + mult));
                }
            }
            return cap;
        };
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lmekanism/common/capabilities/chemical/multiblock/MultiblockChemicalTankBuilder;output(Lmekanism/common/lib/multiblock/MultiblockData;Ljava/util/function/LongSupplier;Ljava/util/function/Predicate;Lmekanism/api/IContentsListener;)Lmekanism/api/chemical/IChemicalTank;", ordinal = 1), index = 1)
    private LongSupplier apoex_modifyCooledCoolantCapacity(LongSupplier original) {
        return () -> {
            long cap = original.getAsLong();
            if (this instanceof IApoExMultiblock multiblock) {
                float mult = multiblock.getFuelCapacityMultiplier();
                if (mult > 0) {
                    return (long) (cap * (1.0F + mult));
                }
            }
            return cap;
        };
    }

    @ModifyVariable(method = "tick", at = @At(value = "STORE"), name = "amountToBoil")
    private int apoex_modifyAmountToBoil(int amountToBoil) {
        if (this instanceof IApoExMultiblock multiblock) {
            float genMult = multiblock.getGenerationMultiplier();
            if (genMult > 0) {
                return (int) (amountToBoil * (1.0F + genMult));
            }
        }
        return amountToBoil;
    }
}
